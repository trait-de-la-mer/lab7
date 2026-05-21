package Commands;

import Collection.LabWork;
import Collection.Person;
import tools.CollectionManager;
import tools.User;

import java.util.stream.Collectors;

public class PrintUniqAthors extends Command{
    {setName("uniqAuthor");
        setInfo("Выводи уникальных авторов");}

    public PrintUniqAthors(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg, User user) {
        String answer = getCollectionManager().getLabCollection().stream()
                .map(LabWork::getAuthor)
                .distinct()
                .map(Person::toString).collect(Collectors.joining("\n"));
        return answer;
    }
}
