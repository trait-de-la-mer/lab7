package tools;

import Collection.Color;
import Collection.Coordinates;
import Collection.Difficulty;
import Collection.LabWork;
import Collection.Person;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class BaseConnect {
    Statement statement;
    Connection connection;
    public BaseConnect(String user, String password){
        String url = "jdbc:postgresql://localhost:5432/mydb";
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            System.out.println("успешно подключено к бд");
        } catch (SQLException e) {
            System.out.println("что-то не так с подключение к бд, проверь пользователя, пароль, доступность и т.д.");
            System.out.println(e.getMessage());
        }
    }

    public LinkedList<LabWork> loadLabs() {
        LinkedList<LabWork> labs = new LinkedList<>();
            String sql = """
                    SELECT
                        lw.id,
                        lw.name,
                        lw.creation_date,
                        lw.minimal_point,
                        lw.difficulty,

                        c.x,
                        c.y,

                        p.name AS person_name,
                        p.weight,
                        p.eye_color

                    FROM lab_work lw
                    JOIN coordinates c ON lw.coordinates_id = c.id
                    JOIN person p ON lw.author_id = p.id
                    ORDER BY lw.id
                    """;
            try {
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    LabWork lab = new LabWork();
                    lab.setId(rs.getLong("id"));
                    lab.setName(rs.getString("name"));
                    Coordinates coordinates = new Coordinates();
                    coordinates.setX(rs.getLong("x"));
                    coordinates.setY(rs.getInt("y"));
                    lab.setCoordinates(coordinates);
                    Person person = new Person();
                    person.setName(rs.getString("person_name"));
                    person.setWeight(rs.getDouble("weight"));
                    person.setEyeColor(Color.fromString(rs.getString("eye_color")));
                    lab.setAuthor(person);
                    LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                    lab.setCreationDate(creationDate);
                    lab.setMinimalPoint(rs.getDouble("minimal_point"));
                    lab.setDifficulty(Difficulty.fromString(rs.getString("difficulty")));
                    labs.add(lab);
                }
                String idSql = "SELECT last_value FROM lab_work_id_seq;";
                rs = statement.executeQuery(idSql);
                rs.next();
                CollectionManager.setLastId(rs.getLong("last_value"));

            } catch (SQLException e) {
                System.out.println("что-то не так с бд, не получилось прочитать");
                System.out.println(e.getMessage());
            }

        return labs;
    }

    public void addToDB(LabWork labWork, User user) throws SQLException {
        String userSql = "SELECT id FROM users WHERE login = ?";
        PreparedStatement userStatement = connection.prepareStatement(userSql);
        userStatement.setString(1, user.getLogin());
        ResultSet userRs = userStatement.executeQuery();
        userRs.next();
        int userId = userRs.getInt("id");
        String coordinatesSql = "INSERT INTO coordinates(x, y) VALUES (?, ?)";
        PreparedStatement coordinatesStatement = connection.prepareStatement(coordinatesSql,
                PreparedStatement.RETURN_GENERATED_KEYS);
        coordinatesStatement.setLong(1, labWork.getCoordinates().getX());
        coordinatesStatement.setFloat(2, labWork.getCoordinates().getY());
        coordinatesStatement.executeUpdate();
        ResultSet coordinatesKeys = coordinatesStatement.getGeneratedKeys();
        coordinatesKeys.next();
        int coordinatesId = coordinatesKeys.getInt(1);

        String personSql = "INSERT INTO person(name, weight, eye_color) VALUES (?, ?, ?)";
        PreparedStatement personStatement = connection.prepareStatement(personSql,
                        PreparedStatement.RETURN_GENERATED_KEYS);
        personStatement.setString(1, labWork.getAuthor().getName());
        personStatement.setDouble(2, labWork.getAuthor().getWeight());
        personStatement.setString(3, labWork.getAuthor().getEyeColor().toString());
        personStatement.executeUpdate();
        var personKeys = personStatement.getGeneratedKeys();
        personKeys.next();
        int personId = personKeys.getInt(1);

        String labSql = """
                INSERT INTO lab_work(name, coordinates_id, creation_date, minimal_point, difficulty, author_id, user_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        PreparedStatement labStatement = connection.prepareStatement(labSql);
        labStatement.setString(1, labWork.getName());
        labStatement.setInt(2, coordinatesId);
        labStatement.setDate(3, java.sql.Date.valueOf(labWork.getCreationDate()));
        labStatement.setDouble(4, labWork.getMinimalPoint());
        labStatement.setString(5, labWork.getDifficulty().toString());
        labStatement.setInt(6, personId);
        labStatement.setInt(7, userId);
        labStatement.executeUpdate();
        System.out.println("labwork add");
    }

    public void remove (long id, User user) throws SQLException {
        try {
            String findUsersql = "SELECT id FROM users WHERE password_hash = ? and login = ?";
            PreparedStatement selectUser = connection.prepareStatement(findUsersql);
            selectUser.setString(1, user.getPassword());
            selectUser.setString(2, user.getLogin());
            ResultSet idUser = selectUser.executeQuery();
            idUser.next();
            int userId = idUser.getInt("id");
            String selectSql = "SELECT coordinates_id, author_id FROM lab_work WHERE id = ? and user_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setLong(1, id);
            selectStatement.setInt(2, userId);
            ResultSet rs = selectStatement.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("LabWork с таким id не найден или нет прав");
            }
            int coordinatesId = rs.getInt("coordinates_id");
            int authorId = rs.getInt("author_id");

            String deleteLabSql = "DELETE FROM lab_work WHERE id = ?";
            PreparedStatement deleteLabStatement = connection.prepareStatement(deleteLabSql);
            deleteLabStatement.setLong(1, id);
            deleteLabStatement.executeUpdate();

            String deleteCoordinatesSql = "DELETE FROM coordinates WHERE id = ?";
            PreparedStatement deleteCoordinatesStatement = connection.prepareStatement(deleteCoordinatesSql);
            deleteCoordinatesStatement.setInt(1, coordinatesId);
            deleteCoordinatesStatement.executeUpdate();

            String deletePersonSql = "DELETE FROM person WHERE id = ?";
            PreparedStatement deletePersonStatement = connection.prepareStatement(deletePersonSql);
            deletePersonStatement.setInt(1, authorId);
            deletePersonStatement.executeUpdate();
            System.out.println("labwork из бд удалён");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear(User user) throws SQLException {
        try {
            String userSql = "SELECT id FROM users WHERE login = ?";
            PreparedStatement userStatement = connection.prepareStatement(userSql);
            userStatement.setString(1, user.getLogin());
            ResultSet userRs = userStatement.executeQuery();
            userRs.next();
            int userId = userRs.getInt("id");
            String countAllSql = "SELECT COUNT(*) FROM lab_work";
            PreparedStatement countAllStatement = connection.prepareStatement(countAllSql);
            ResultSet allRs = countAllStatement.executeQuery();
            allRs.next();
            int allCount = allRs.getInt(1);
            String countUserSql = "SELECT COUNT(*) FROM lab_work WHERE user_id = ?";
            PreparedStatement countUserStatement = connection.prepareStatement(countUserSql);
            countUserStatement.setInt(1, userId);
            ResultSet userCountRs = countUserStatement.executeQuery();
            userCountRs.next();
            int userCount = userCountRs.getInt(1);
            if (allCount != userCount) {
                throw new IllegalArgumentException("В коллекции не все эл-ты принадлежат заданному пользователю");
            }
            String sql = "TRUNCATE TABLE lab_work, coordinates, person RESTART IDENTITY CASCADE";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            System.out.println("коллекция очищена");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(long id, LabWork labWork, User user) throws SQLException {
        try {
            String findUsersql = "SELECT id FROM users WHERE password_hash = ? and login = ?";
            PreparedStatement selectUser = connection.prepareStatement(findUsersql);
            selectUser.setString(1, user.getPassword());
            selectUser.setString(2, user.getLogin());
            ResultSet idUser = selectUser.executeQuery();
            idUser.next();
            int userId = idUser.getInt("id");
            String selectSql = "SELECT coordinates_id, author_id FROM lab_work WHERE id = ? and user_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setLong(1, id);
            selectStatement.setLong(2, userId);
            ResultSet rs = selectStatement.executeQuery();
            System.out.println();
            if (!rs.next()) {
                throw new IllegalArgumentException("LabWork с таким id не найден или этот пользователь не может изменить эту лабу");
            }
            int coordinatesId = rs.getInt("coordinates_id");
            int authorId = rs.getInt("author_id");
            String updateCoordinatesSql = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
            PreparedStatement updateCoordinatesStatement = connection.prepareStatement(updateCoordinatesSql);
            updateCoordinatesStatement.setLong(1, labWork.getCoordinates().getX());
            updateCoordinatesStatement.setFloat(2, labWork.getCoordinates().getY());
            updateCoordinatesStatement.setInt(3, coordinatesId);
            updateCoordinatesStatement.executeUpdate();
            String updatePersonSql = "UPDATE person SET name = ?, weight = ?, eye_color = ? WHERE id = ?";
            PreparedStatement updatePersonStatement = connection.prepareStatement(updatePersonSql);
            updatePersonStatement.setString(1, labWork.getAuthor().getName());
            updatePersonStatement.setDouble(2, labWork.getAuthor().getWeight());
            updatePersonStatement.setString(3, labWork.getAuthor().getEyeColor().toString());
            updatePersonStatement.setInt(4, authorId);
            updatePersonStatement.executeUpdate();
            String updateLabSql = """
                UPDATE lab_work
                SET name = ?, creation_date = ?, minimal_point = ?, difficulty = ?
                WHERE id = ?
                """;
            PreparedStatement updateLabStatement = connection.prepareStatement(updateLabSql);
            updateLabStatement.setString(1, labWork.getName());
            updateLabStatement.setDate(2,java.sql.Date.valueOf(labWork.getCreationDate()));
            updateLabStatement.setDouble(3,labWork.getMinimalPoint());
            updateLabStatement.setString(4, labWork.getDifficulty().toString());
            updateLabStatement.setLong(5,id);
            updateLabStatement.executeUpdate();
            System.out.println("LabWork обновлён");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUser(User user) {
        try {
            String sql = "SELECT password_hash FROM users WHERE login = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getLogin());
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                return false;
            }
            String dbHash = rs.getString("password_hash");
            return dbHash.equals(user.getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addUser(User user) {
        try {
            String checkSql = "SELECT login FROM users WHERE login = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, user.getLogin());
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next()) {
                System.out.println("Пользователь уже существует");
                return false;
            }
            String insertSql = "INSERT INTO users(login, password_hash) VALUES (?, ?)";
            PreparedStatement insertStatement =connection.prepareStatement(insertSql);
            insertStatement.setString(1, user.getLogin());
            insertStatement.setString(2, user.getPassword());
            insertStatement.executeUpdate();
            System.out.println("Пользователь зарегистрирован");
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}