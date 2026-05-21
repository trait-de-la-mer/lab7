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
    //TODO ДОБАВИТЬ, ИЗМЕНИТЬ ПО ИД, УДАЛИТЬ ВСЕ, ЗАКРЫТЬ СОЕДИНЕНИЕ
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
            } catch (SQLException e) {
                System.out.println("что-то не так с бд, не получилось прочитать");
                System.out.println(e.getMessage());
            }

        return labs;
    }

    public void addToDB(LabWork labWork) throws SQLException {
            String coordinatesSql =
                    "INSERT INTO coordinates(x, y) VALUES (?, ?)";

            PreparedStatement coordinatesStatement = connection.prepareStatement(coordinatesSql,
                    PreparedStatement.RETURN_GENERATED_KEYS);

            coordinatesStatement.setLong(1, labWork.getCoordinates().getX());
            coordinatesStatement.setFloat(2, labWork.getCoordinates().getY());
            coordinatesStatement.executeUpdate();
            ResultSet coordinatesKeys = coordinatesStatement.getGeneratedKeys();
            coordinatesKeys.next();

            int coordinatesId = coordinatesKeys.getInt(1);

            //person
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
            /*
             * 3. Добавляем lab_work
             */
            String labSql = """
                    INSERT INTO lab_work(name, coordinates_id, creation_date, minimal_point, difficulty, author_id)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
            PreparedStatement labStatement = connection.prepareStatement(labSql);
            labStatement.setString(1, labWork.getName());
            labStatement.setInt(2, coordinatesId);
            labStatement.setDate(3, java.sql.Date.valueOf(labWork.getCreationDate()));
            labStatement.setDouble(4, labWork.getMinimalPoint());
            labStatement.setString(5, labWork.getDifficulty().toString());
            labStatement.setInt(6, personId);
            labStatement.executeUpdate();
            System.out.println("labwork add");
    }
}