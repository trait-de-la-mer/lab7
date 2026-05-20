package Collection;

import java.io.Serializable;

public enum Difficulty implements Serializable {
    EASY,
    NORMAL,
    IMPOSSIBLE;

    public static Difficulty fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Вы не ввели тип сложности!");
        }
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимый тип сложности");
        }
    }
    private static final long serialVersionUID = 1L;
}