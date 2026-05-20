package Connecting;

import Commands.Command;
import Commands.Save;
import tools.CommandManager;
import tools.Requester;

import java.io.IOException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;  // ← Добавить
import org.apache.logging.log4j.Logger;      // ← Добавить

public class ConnectManager {
    private static final Logger log = LogManager.getLogger(ConnectManager.class);
    private CommandManager commandManager;
    private ConnectionAcceptor connectionAcceptor;
    private ResponseSender responseSender;
    private RequestReader requestReader;
    private Socket sock;
    private boolean running = true;
    public ConnectManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.connectionAcceptor = new ConnectionAcceptor();
        this.requestReader = new RequestReader();
        this.responseSender = new ResponseSender();
    }

    public void handle(int port){
        try {
            connectionAcceptor.start(port);
            while (running){
                try {
                    sock = connectionAcceptor.acceptClient();
                    requestReader.init(sock);
                    responseSender.init(sock);

                    while (running){
                        Requester<?> req = requestReader.readRequest();
                        Command com = commandManager.getCommands().get(req.getCommand());
                        if (com != null) {
                            String text = commandManager.executC(com, req.getArgs());
                            responseSender.sendResponse(text);
                            //stop();
                        } else {
                            log.error("команды нет в мапе");
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    log.error("Клиент отключился или ошибка " + e.getMessage());
                } finally {
                    closeClientResources();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.warn("Возможно порт занят");
            stop();
        }
    }

    private void closeClientResources() {
        requestReader.close();
        responseSender.close();
        try {
            if (sock != null && !sock.isClosed()) {
                sock.close();
                log.warn("Сокет клиента закрыт");
            }
        } catch (IOException ignored) {}
    }

    public void stop(){
        running = false;
        closeClientResources();
        Command com = commandManager.getCommands().get("save");
        com.execute(null);
        try {
            connectionAcceptor.stop();
        } catch (IOException e) {
            log.error("Ошибка при остановке сервера: " + e.getMessage());
        }
    }

}
