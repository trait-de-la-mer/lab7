package Commands;

import Collection.LabWork;
import tools.CollectionManager;

public class Add extends Command<LabWork>{
    //TODO CHANGE FOR DB
    {setName("add");
        setInfo("удаляет элемент по id");}
    public Add(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(LabWork arg) {
        CollectionManager cm = getCollectionManager();
        arg.setId(cm.generateId());
        cm.addElement(arg);
        System.out.println(getCollectionManager().getLabCollection().toString());
        return "Успешно добавлено";
    }
}
