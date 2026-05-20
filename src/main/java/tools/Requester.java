package tools;

import java.io.Serializable;

public class Requester<T> implements Serializable {
    /**
     * класс для отправки на сервер, где есть название команды, аргумент и класс аргумента
     */
    private static final long serialVersionUID = 1L;
    private String command;
    private T args;
    private Class<T> objectClass;

    public void setObjectClass(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    public String getCommand() {
        return command;
    }

    public T getArgs() {
        return args;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setArgs(T args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "Requester{" +
                "command='" + command + '\'' +
                ", args=" + args +
                '}';
    }
}
