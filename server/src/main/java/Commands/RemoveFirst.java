package Commands;

import tools.CollectionManager;

public class RemoveFirst extends Command{
    //TODO CHANGE FOR DB
    {setName("remove_first");
        setInfo("удаляет первый элемент в коллекции");}
    public RemoveFirst(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg) {
        getCollectionManager().removeElement(0);
        return "Удален первый элемент коллекции";
    }
}
