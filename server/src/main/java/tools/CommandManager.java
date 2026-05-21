package tools;

import Commands.Command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class CommandManager {
    private static final HashMap<String, Command<?>> commands= new HashMap<>();
    private static final LinkedList<String> history = new LinkedList<>();

    public CommandManager(Command... needComands) {
        for (Command com : needComands){
            commands.put(com.getName().toLowerCase(Locale.ENGLISH), com);
        }
    }

    public <T> String executC(Command cmd, T args, User user){
        try {
            String text;
            try {
                text = cmd.execute(args, user);
            } catch (IllegalArgumentException ex){
                text = ex.getMessage();
            } catch (Exception e) {
                text = "хз что случилось " + e.getMessage();
            }
            if (history.size() == 5) {
                history.removeLast();
            }
            history.addFirst(cmd.getName());
            return text;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "непредвединная ошибка";
        }
    }
    public static HashMap<String, Command<?>> getCommands() {
        return commands;
    }

    public static LinkedList<String> getHistory() {
        return history;
    }
}

