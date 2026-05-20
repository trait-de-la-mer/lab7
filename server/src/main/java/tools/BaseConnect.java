package tools;

import Collection.Color;
import Collection.Coordinates;
import Collection.Difficulty;
import Collection.LabWork;
import Collection.Person;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;

public class BaseConnect {

    public LinkedList<LabWork> loadLabs() {

        LinkedList<LabWork> labs = new LinkedList<>();

        String url = "jdbc:postgresql://localhost:5432/mydb";
        String user = "postgres";
        String password = "77097";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();

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
                person.setEyeColor(
                        Color.fromString(rs.getString("eye_color"))
                );

                lab.setAuthor(person);

                LocalDate creationDate =
                        rs.getDate("creation_date").toLocalDate();

                lab.setCreationDate(creationDate);

                lab.setMinimalPoint(rs.getDouble("minimal_point"));

                lab.setDifficulty(
                        Difficulty.fromString(rs.getString("difficulty"))
                );

                labs.add(lab);
            }

        } catch (Exception e) {
            System.out.println("что-то с подключение к БД");
        }

        return labs;
    }
}