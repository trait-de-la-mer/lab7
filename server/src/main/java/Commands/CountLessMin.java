package Commands;

import Collection.LabWork;
import tools.CollectionManager;
import tools.User;

import java.util.stream.Collectors;

public class CountLessMin extends Command<Double>{
    {
        setName("countLessMin");
        setInfo("вывести количество элементов, значение поля minimalPoint которых меньше заданного");
    }

    public CountLessMin(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Double arg, User user) {
        String answer = getCollectionManager().getLabCollection().stream()
                .filter(lab -> lab.getMinimalPoint() < arg)
                .map(LabWork::toString)
                .collect(Collectors.joining("\n"));
        return answer;
    }
}
