package Collection;

import java.io.Serializable;

public enum Color implements Serializable {
    GREEN,
    RED,
    BLACK,
    YELLOW,
    ORANGE;

    public static Color fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Вы не ввели тип цвета!");
        }
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимый тип цвета");
        }
    }
    private static final long serialVersionUID = 1L;
}
