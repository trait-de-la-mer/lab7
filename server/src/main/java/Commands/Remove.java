package Commands;

import Collection.LabWork;
import Commands.Command;
import tools.CollectionManager;
import tools.User;

import java.util.Iterator;
import java.util.Optional;

public class Remove extends Command<Integer> {
    //TODO CHANGE FOR DB
    {setName("remove");
        setInfo("удаляет элемент по id");}
    public Remove(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Integer args, User user) {
        int key = args;
        CollectionManager cm = getCollectionManager();
        boolean remove = cm.remove(key, user);
        if (remove) {
            return "Элемент с id " + key + " удален";
        } else {
            throw new IllegalArgumentException("Элемент с таким id не найден");
        }
    }
}