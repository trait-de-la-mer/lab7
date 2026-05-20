package Collection;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Person implements Comparable<Person>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private double weight; //Значение поля должно быть больше 0
    private Color eyeColor; //Поле не может быть null

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("имя не может быть null");
        this.name = name;
    }

    public void setWeight(double weight) {
        if (weight <= 0) throw new IllegalArgumentException("ты явно весишь больше");
        this.weight = weight;
    }

    public void setEyeColor(Color eyeColor) {
        if (eyeColor == null) throw new IllegalArgumentException("цвет не может быть null");
        this.eyeColor = eyeColor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Double.compare(weight, person.weight) == 0 && Objects.equals(name, person.name) && eyeColor == person.eyeColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight, eyeColor);
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public Color getEyeColor() {
        return eyeColor;
    }
    @Override
    public String toString() {
        return "name = " + name + ";" + "weight = " + weight + ";" + eyeColor;
    }

    @Override
    public int compareTo(Person person) {
        int weightCompare = Double.compare(this.weight, person.weight);
        if (weightCompare != 0) {
            return weightCompare;
        }
        return this.name.compareTo(person.name);
    }
}