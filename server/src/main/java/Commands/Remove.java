package Commands;

import Collection.LabWork;
import Commands.Command;
import tools.CollectionManager;

import java.util.Iterator;
import java.util.Optional;

public class Remove extends Command<Integer> {
    //TODO CHANGE FOR DB
    {setName("remove");
        setInfo("удаляет элемент по id");}
    public Remove(CollectionManager cm) {
        super(cm);
    }

    public String execute(Integer args) {
        int key;
        key = args;
        CollectionManager cm = getCollectionManager();
        Optional<LabWork> target = cm.getLabCollection().stream()
                .filter(lab -> lab.getId() == key)
                .findFirst();

        if (target.isPresent()) {
            cm.getLabCollection().remove(target.get());
            return "Элемент с id " + key + " удален";
        } else {
            throw new IllegalArgumentException("Элемент с таким id не найден");
        }
    }
}