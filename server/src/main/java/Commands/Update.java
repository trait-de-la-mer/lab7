package Commands;

import Collection.LabWork;
import tools.CollectionManager;
import tools.UpdateArgs;

import java.util.Objects;
import java.util.Optional;

public class Update extends Command<UpdateArgs>{
    //TODO CHANGE FOR DB
    {
        setName("update");
        setInfo("обновляет лабу по айди");
    }

    public Update(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(UpdateArgs updateArgs) {
        Long needId = updateArgs.getId();
        LabWork labWork = updateArgs.getLabWork();
        boolean added = getCollectionManager().update(needId, labWork);
        if (added) {
            return "Обновлена лаба по id " + needId;
        }
        return "Такого id нет";
    }
}
