package Connecting;

import Commands.Command;
import tools.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectManager {
    private static final Logger log = LogManager.getLogger(ConnectManager.class);
    private final CommandManager commandManager;
    private final ConnectionAcceptor connectionAcceptor;
    private final BaseConnect baseConnect;
    private volatile boolean running = true;
    private final ExecutorService acceptPool = Executors.newCachedThreadPool();
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    private final ExecutorService processPool = Executors.newFixedThreadPool(8);
    private final ExecutorService sendPool = Executors.newFixedThreadPool(8);

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
        UserManager userManager = new UserManager(baseConnect);
        try {
            requestReader.init(sock);
            responseSender.init(sock);
            while (running && !sock.isClosed()) {
                Requester<?> req;
                try {
                    req = requestReader.readRequest();
                } catch (IOException | ClassNotFoundException e) {
                    log.debug("Клиент отключился: " + e.getMessage());
                    break;
                }

                if (req == null) break;
                String commandName = req.getCommand();
                Object args = req.getArgs();
                User user = req.getUser();
                try {
                    Future<String> future = processPool.submit(() -> {
                        if ("register".equals(commandName)) {
                            userManager.addUser(user);
                            return "зарегистрирован";
                        }
                        if (!userManager.check(user)) {
                            return "логин или пароль неверный";
                        }
                        Command com = commandManager.getCommands().get(commandName);
                        if (com != null) {
                            return commandManager.executC(com, args, user);
                        }
                        return "неизвестная команда";
                    });

                    String result = future.get();
                    sendPool.submit(() -> {
                        try {
                            responseSender.sendResponse(result);
                        } catch (IOException e) {
                            log.error("Ошибка отправки: " + e.getMessage());
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Прервано: " + e.getMessage());
                    break;
                } catch (ExecutionException e) {
                    log.error("Ошибка выполнения: " + e.getCause().getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Инициализация клиента: " + e.getMessage());
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