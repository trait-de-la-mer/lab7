package app;

import Collection.LabWork;
import Collection.Person;
import tools.Command;
import tools.Consoll;
import tools.MiddleManager;
import tools.UpdateArgs;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    /**
     * задаем порты, команды доступные на клиенте и подключаемся
     */
    public static  HashMap<String, Command> commands = new HashMap<>();
    private static int port = 6789;
    public static void main(String[] args) {
        commands.put("add", new Command(1, LabWork.class));
        commands.put("show", new Command(0));
        commands.put("help", new Command(0));
        commands.put("remove", new Command(1, int.class));
        commands.put("clear", new Command(0));
        commands.put("countlessmin", new Command(1, double.class));
        commands.put("head", new Command(0));
        commands.put("info", new Command(0));
        commands.put("history", new Command(0));
        commands.put("update", new Command(1, UpdateArgs.class));
        commands.put("uniqauthor", new Command(0));
        commands.put("remove_first", new Command(0));
        commands.put("lessthanauthor", new Command(1, Person.class));
        MiddleManager mm = null;
        try{
            mm = new MiddleManager(port);
        } catch(IOException e) {
            Consoll.printSmt("нет подключения, попробуйте позже");
            System.exit(0);
        } catch (Exception e) {
            System.exit(-9999999);
        }
        Consoll consoll = new Consoll(mm);
            consoll.startConsole();
    }
}