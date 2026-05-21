package Commands;

import tools.CollectionManager;
import tools.CommandManager;
import tools.User;

import java.util.stream.Collectors;

public class History extends Command{
    {
        setName("history");
        setInfo("выводит последние 5 команд (без их аргументов)");
    }

    public History(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg, User user) {
        String answer = CommandManager.getHistory().stream().collect(Collectors.joining("\n"));
        return answer;
    }
}
