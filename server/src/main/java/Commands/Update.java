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
        Optional<LabWork> target = getCollectionManager().getLabCollection().stream()
                .filter(lab -> Objects.equals(lab.getId(), needId))
                .findFirst();
        if (target.isPresent()) {
            labWork.setId(needId);
            getCollectionManager().getLabCollection().remove(target.get());
            getCollectionManager().getLabCollection().add(labWork);
            return "Обновлена лаба по id " + needId;
        }
        return "Такого id нет";
    }
}
