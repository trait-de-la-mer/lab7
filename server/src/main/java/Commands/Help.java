package Commands;

import tools.CollectionManager;
import tools.CommandManager;
import tools.User;

import java.util.stream.Collectors;

public class Help extends Command{
    {setName("help");
        setInfo("Выводит все команды и их выполнение");}

    public Help(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object args, User user) {
        String answer = CommandManager.getCommands().entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue().getInfo())
                .collect(Collectors.joining("\n"));
        return answer;
    }
}
