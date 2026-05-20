package Commands;

import Collection.LabWork;
import tools.CollectionManager;

import java.util.stream.Collectors;

public class Show extends Command{
    {setName("show");
        setInfo("выводит все элементы в коллекции");}
    public Show(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg) {
        String answer = getCollectionManager().getLabCollection().stream()
                .map(LabWork::toString)
                .collect(Collectors.joining("\n"));
        return answer;
    }
}
