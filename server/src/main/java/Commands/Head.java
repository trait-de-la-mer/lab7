package Commands;

import Collection.LabWork;
import tools.CollectionManager;

public class Head extends Command{
    {
        setName("head");
        setInfo("Выводит первый эл-т в коллекции");
    }

    public Head(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg) {
        LabWork firstElement = getCollectionManager().getElemnt(0);
        return firstElement.toString();
    }
}
