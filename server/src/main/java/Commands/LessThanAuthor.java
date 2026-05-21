package Commands;

import Collection.LabWork;
import Collection.Person;
import tools.CollectionManager;
import tools.User;

import java.util.stream.Collectors;

public class LessThanAuthor extends Command<Person>{
    {setName("lessThanAuthor");
        setInfo("вывести элементы, значение поля author которых меньше заданного");}
    public LessThanAuthor(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Person person, User user) {
        String answer = getCollectionManager().getLabCollection().stream()
                .filter(lab -> lab.getAuthor().compareTo(person) < 0)
                .map(LabWork::toString)
                .collect(Collectors.joining("\n"));
        return answer;
    }
}
