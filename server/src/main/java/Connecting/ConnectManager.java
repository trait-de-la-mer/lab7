package Connecting;

import Commands.Command;
import tools.*;

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
    private User user;
    private BaseConnect baseConnect;
    public ConnectManager(BaseConnect baseConnect, CommandManager commandManager) {
        this.commandManager = commandManager;
        this.connectionAcceptor = new ConnectionAcceptor();
        this.requestReader = new RequestReader();
        this.responseSender = new ResponseSender();
        this.baseConnect = baseConnect;
    }

    public void handle(int port){
        try {
            connectionAcceptor.start(port);
            while (running){
                try {
                    sock = connectionAcceptor.acceptClient();
                    requestReader.init(sock);
                    responseSender.init(sock);
                    UserManager userManager = new UserManager(baseConnect);
                    while (running){
                        Requester<?> req = requestReader.readRequest();
                        Command com = commandManager.getCommands().get(req.getCommand());
                        user = req.getUser();
                        if (req.getCommand().equals("register")){
                            userManager.addUser(user);
                            responseSender.sendResponse("зарегестрирован");
                            continue;
                        } else {
                            if (!userManager.check(user)){
                                responseSender.sendResponse("логин или пароль неверный");
                                continue;
                            }

                        }
                        if (com != null) {
                            String text = commandManager.executC(com, req.getArgs(), user);
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
        com.execute(null, user);
        try {
            connectionAcceptor.stop();
        } catch (IOException e) {
            log.error("Ошибка при остановке сервера: " + e.getMessage());
        }
    }

}
