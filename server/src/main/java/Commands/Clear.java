package Commands;

import tools.CollectionManager;
import tools.User;

public class Clear extends Command{
    //TODO CHANGE FOR DB
    {setName("clear");
        setInfo("Очищает коллекцию");}

    public Clear(CollectionManager cm) {
        super(cm);
    }


    @Override
    public String execute(Object arg, User user) {
        getCollectionManager().clearCollection();
        return "коллекция пуста";
    }
}