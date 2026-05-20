package Collection;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class LabWork implements Comparable<LabWork>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private double minimalPoint; //Значение поля должно быть больше 0
    private Difficulty difficulty; //Поле не может быть null
    private Person author; //Поле не может быть null

    public double getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(double minimalPoint) {
        if (minimalPoint < 0) throw new IllegalArgumentException("можем кнш и так проставить, но ты уверен?");
        this.minimalPoint = minimalPoint;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) throw new IllegalArgumentException("тип не может быть null");
        this.difficulty = difficulty;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Person getAuthor() {
        return author;
    }


    public void setCreationDate(LocalDate creationDate) {
        if (creationDate == null) throw new IllegalArgumentException("время создания не может быть null");
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }


    public void setId(Long id) {
        if (id <= 0) throw new IllegalArgumentException("Id должно быть > 0");
        this.id = id;
    }


    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("имя не может быть null");
        if (name.isEmpty()) throw new IllegalArgumentException("имя не может быть пустым");
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("координаты не могут быть null");
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "LabWork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minimalPoint +
                ", difficulty=" + difficulty +
                ", author: " + author +
                '}';
    }
    @Override
    public int compareTo(LabWork o) {
        return (int)(this.minimalPoint - o.minimalPoint);
    }
}