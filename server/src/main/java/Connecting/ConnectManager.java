package Connecting;

import Commands.Command;
import tools.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectManager {
    private static final Logger log = LogManager.getLogger(ConnectManager.class);
    private final CommandManager commandManager;
    private final ConnectionAcceptor connectionAcceptor;
    private final BaseConnect baseConnect;
    private volatile boolean running = true;
    private final ExecutorService acceptPool = Executors.newCachedThreadPool();
    private final ExecutorService clientPool = Executors.newFixedThreadPool(8);
    private User user;

    public ConnectManager(BaseConnect baseConnect, CommandManager commandManager) {
        this.commandManager = commandManager;
        this.connectionAcceptor = new ConnectionAcceptor();
        this.baseConnect = baseConnect;
    }

    public void handle(int port) {
        try {
            connectionAcceptor.start(port);
            acceptPool.execute(() -> {
                while (running) {
                    try {
                        Socket client = connectionAcceptor.acceptClient();
                        clientPool.execute(() -> handleClient(client));
                    } catch (IOException e) {
                        log.error("Ошибка accept: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            log.error(e.getMessage());
            stop();
        }
    }
    private void handleClient(Socket sock) {
        RequestReader requestReader = new RequestReader();
        ResponseSender responseSender = new ResponseSender();
        try {
            requestReader.init(sock);
            responseSender.init(sock);
            UserManager userManager = new UserManager(baseConnect);
            while (running && !sock.isClosed()) {
                Requester<?> req = requestReader.readRequest();
                if (req == null) break;
                Command com = commandManager.getCommands().get(req.getCommand());
                user = req.getUser();
                if ("register".equals(req.getCommand())) {
                    userManager.addUser(user);
                    responseSender.sendResponse("зарегистрирован");
                    continue;
                }
                if (!userManager.check(user)) {
                    responseSender.sendResponse("логин или пароль неверный");
                    continue;
                }
                if (com != null) {
                    String result = commandManager.executC(com, req.getArgs(), user);
                    responseSender.sendResponse(result);
                } else {
                    responseSender.sendResponse("неизвестная команда");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Клиент отключился: " + e.getMessage());
        } finally {
            close(sock, requestReader, responseSender);
        }
    }
    private void close(Socket sock, RequestReader r, ResponseSender s) {
        try {
            if (r != null) r.close();
            if (s != null) s.close();
            if (sock != null && !sock.isClosed()) {
                sock.close();
                log.warn("Сокет клиента закрыт");
            }
        } catch (IOException ignored) {}
    }
    public void stop() {
        running = false;
        try {
            connectionAcceptor.stop();
        } catch (IOException e) {
            log.error("Ошибка остановки: " + e.getMessage());
        }
    }
}