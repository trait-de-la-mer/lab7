package Collection;

import java.io.Serial;
import java.io.Serializable;

public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private long x;
    private Integer y; //Поле не может быть null

    public void setX(long x) {
        this.x = x;
    }

    public void setY(Integer y) {
        if (y > 593) throw new IllegalArgumentException("y не может быть > 593");
        if (y == null) throw new IllegalArgumentException("y не может быть null");
        this.y = y;
    }

    @Override
    public String toString() {
        return x + ";" + y;
    }

    public long getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }
}