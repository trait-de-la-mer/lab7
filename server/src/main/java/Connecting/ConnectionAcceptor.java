package Connecting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionAcceptor {
    private static final Logger log = LogManager.getLogger(ConnectionAcceptor.class);
    private volatile ServerSocket serv;
        public void start(int port) throws IOException {
            serv = new ServerSocket(port);
            log.info("Сервер запущен на порту " + port);
        }

        public Socket acceptClient() throws IOException {
            log.info("Ожидание клиента...");
            Socket client = serv.accept();
            log.info("Подключен: " + client.getInetAddress());
            return client;
        }

        public synchronized void stop() throws IOException {
            if (serv != null && !serv.isClosed()) {
                serv.close();
                log.info("Сокет закрыт");
            }
        }

        public synchronized boolean isRunning() {
            return serv != null && !serv.isClosed();
        }
}
