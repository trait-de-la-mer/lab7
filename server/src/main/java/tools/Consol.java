package tools;

import java.util.Scanner;

import Commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.CollectionManager;

public class Consol {
    private static final Logger log = LogManager.getLogger(Consol.class);
    private final CommandManager commandManager;
    private volatile boolean running = true; // синхронизирован с основным циклом

    public Consol(CommandManager cm) {
        this.commandManager = cm;
    }

    public void start() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            log.info("Серверная консоль активна. Доступные команды: save, exit");
            while (running) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    switch (input) {
                        case "save" -> {
                            log.info("Сохранено");
                            Command com = commandManager.getCommands().get("save");
                            com.execute(null, null);
                        }
                        case "exit" -> {
                            //TODO obrabotat exit
                        }
                        default -> System.out.println(" Неизвестная серверная команда. Используйте: save, exit");
                    }
                }
            }
            scanner.close();
        }, "Server-Console-Thread").start();
    }

    public void stop() {
        running = false;
    }
}