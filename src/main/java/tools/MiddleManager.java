package tools;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;
import java.util.*;
import Collection.*;
import app.Main;
import org.w3c.dom.ls.LSOutput;

public class MiddleManager {
    /**
     * проверяет является ли введенное слово командой и проверят аргументы
     * подключается к серверу (при создании объекта менеджера)
     * отправляет данные к серверу типа реквестер
     */
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private static final LinkedList<String> history = new LinkedList<>();

    public MiddleManager(int port) throws IOException {
        socket = new Socket("localhost", port);
        Consoll.printSmt("Вроде подключились по порту: " + port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendCom(User user, String... nameCommand) { // проверяет на арг-ты и просит ввести сложные арг-ты и дает команду отпр
        if (nameCommand[0].equals("register")) {
            Requester<Objects> requester = new Requester<>();
            pullRequest(requester, nameCommand[0], null);
            sendObj(requester, user);
            return;
        }
        if (nameCommand != null && nameCommand.length != 0 && !Objects.equals(nameCommand[0], "")
                && Main.commands.containsKey(nameCommand[0].toLowerCase())) {
            int len = nameCommand.length;
            String name = nameCommand[0];
            Command cmd = Main.commands.get(name);
            int argCount = Main.commands.get(name).getArgCount();
            Class<?> type = Main.commands.get(name).getObjectClass();

            if (name.equals("update")) {
                if (len < 2) {
                    Consoll.printSmt("Нужно указать ID!");
                    return;
                }
                try {
                    Long id = Long.parseLong(nameCommand[1]);
                    LabWork labWork = cmd.makeLab();

                    UpdateArgs updateArgs = new UpdateArgs(id, labWork);
                    Requester<UpdateArgs> requester = new Requester<>();
                    pullRequest(requester, name, updateArgs);
                    requester.setObjectClass(UpdateArgs.class);
                    sendObj(requester, user);
                } catch (NumberFormatException e) {
                    Consoll.printSmt("ID должен быть числом!");
                }
            } else if (type == null) {
                if (len > 1) {Consoll.printSmt("Тут аргумент не очень нужны, но ладно");}
                Requester<Objects> requester = new Requester<>();
                pullRequest(requester, name, null);
                sendObj(requester, user);
            } else if (!type.isPrimitive()) {
                if (len > 1) {Consoll.printSmt("Тут аргумент не очень нужны, но ладно");}
                if (type.equals(LabWork.class)) {
                    LabWork labWork = Main.commands.get(nameCommand[0]).makeLab();
                    Requester<LabWork> requester = new Requester<>();
                    pullRequest(requester, nameCommand[0], labWork);
                    sendObj(requester, user);
                } else if (type.equals(Person.class)) {
                    Requester<Person> requester = new Requester<>();
                    Person person = Main.commands.get(nameCommand[0]).makePerson();
                    pullRequest(requester, nameCommand[0], person);
                    sendObj(requester, user);
                } else {
                    Consoll.printSmt("нужен неизвестный тип");
                }
            } else {
                if (len - 1 == argCount) {
                    if (type.equals(int.class)) {
                        String arg = nameCommand[1];
                        try {
                            Requester<Integer> requester = new Requester<>();
                            pullRequest(requester, nameCommand[0], Integer.parseInt(arg));
                            sendObj(requester, user);
                        } catch (NumberFormatException ex) {
                            System.out.println("Неверный тип аргумента");
                        }
                    }
                    else if (type.equals(double.class)){
                        String arg = nameCommand[1];
                        try {
                            Requester<Double> requester = new Requester<>();
                            pullRequest(requester, nameCommand[0], Double.parseDouble(arg));
                            sendObj(requester, user);
                        } catch (NumberFormatException ex) {
                            System.out.println("Неверный тип аргумента");
                        }
                    }
                } else {Consoll.printSmt("что-то не так с кол-ом аргументов");}
            }
        } else {
//            System.out.println(nameCommand != null);
//            System.out.println(nameCommand.length != 0);
//            System.out.println(!Objects.equals(nameCommand[0], ""));
//            System.out.println(Main.commands.containsKey(nameCommand[0].toLowerCase()));
            Consoll.printSmt("уверен что написал правильно?");}
    }

    public <T> void pullRequest(Requester<T> requester, String name, T obj){
        requester.setArgs(obj);
        requester.setCommand(name);
        if (obj != null) {
            requester.setObjectClass((Class<T>) obj.getClass());
        }
    }

    public <T> void sendObj(Requester<T> requester, User user) {
        requester.setUser(user);
        System.out.println(requester);
        try {
            out.writeObject(requester);
            out.flush();
            System.out.println("Отправлено");
            Object obj = in.readObject();
            Requester<String> answer = new Requester<>();
            if (obj instanceof Requester) {
                answer = (Requester<String>) obj;
            }
            Consoll.printSmt(answer.getArgs());
        } catch (EOFException|SocketException e){
            System.out.println("Сервер отлючился :(");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Я в душе не чаю как не может привестись к типу обджект, иди поплачь");
        }
    }

    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            Consoll.printSmt("Соединение закрыто");
        } catch (IOException e) {
            Consoll.printSmt("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}
