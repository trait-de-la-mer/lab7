package Commands;

import tools.CSVParser;
import tools.CollectionManager;

public class Save extends Command{
    {setName("save");}
    public Save(CollectionManager cm) {
        super(cm);
    }

    @Override
    public String execute(Object arg) {
        CSVParser jParser = new CSVParser(getCollectionManager());
        jParser.convertToCSV(getCollectionManager().getLabCollection());
        return "saving";
    }
}
