package start;

import Commands.*;
import tools.BaseConnect;
import tools.CSVParser;
import tools.CollectionManager;
import tools.CommandManager;
import Connecting.ConnectManager;
import tools.Consol;

import java.io.IOException;

public class Resever {
    public static void main(String[] args) throws IOException {
        int port = 6789;
        CollectionManager collectionManager = new CollectionManager();
        BaseConnect baseConnect = new BaseConnect();
        CSVParser csvParser = new CSVParser(collectionManager);
        collectionManager.setLabCollection(baseConnect.loadLabs());
        CommandManager commandManager = new CommandManager(
                new Add(collectionManager),
                new Remove(collectionManager),
                new Clear(collectionManager),
                new CountLessMin(collectionManager),
                new Head(collectionManager),
                new History(collectionManager),
                new Help(collectionManager),
                new Info(collectionManager),
                new Show(collectionManager),
                new Update(collectionManager),
                new RemoveFirst(collectionManager),
                new PrintUniqAthors(collectionManager),
                new LessThanAuthor(collectionManager),
                new Save(collectionManager)
        );
        Consol consol = new Consol(commandManager);
        consol.start();
        ConnectManager cm = new ConnectManager(commandManager);
        cm.handle(port);
        consol.stop();
    }
}