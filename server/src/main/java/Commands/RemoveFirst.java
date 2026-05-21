package Commands;

import tools.CollectionManager;
import tools.User;

public class RemoveFirst extends Command{
    //TODO CHANGE FOR DB
    {setName("remove_first");
        setInfo("удаляет первый элемент в коллекции");}
    public RemoveFirst(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg, User user) {
        getCollectionManager().remove(0, user);
        return "Удален первый элемент коллекции";
    }
}
