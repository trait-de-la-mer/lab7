package Commands;

import tools.CollectionManager;
import tools.User;

public class Info extends Command{
    {setName("info");
        setInfo("выводит информацию о коллекцииз (тип, дата инициализации, кол-во элементов)");};
    public Info(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg, User user) {
        String answer = "";
        answer += "тип: " + getCollectionManager().getCollectionType() + "\n";
        answer += "дата инициализации: " + getCollectionManager().getCreationDate()  + "\n";
        answer += "кол-во элементов: " + getCollectionManager().getCollectionSyze()  + "\n";
        return answer;
    }
}
