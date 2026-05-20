package Commands;

import tools.CollectionManager;

public class Clear extends Command{
    //TODO CHANGE FOR DB
    {setName("clear");
        setInfo("Очищает коллекцию");}

    public Clear(CollectionManager cm) {
        super(cm);
    }


    @Override
    public String execute(Object arg) {
        getCollectionManager().clearCollection();
        return "коллекция пуста";
    }
}