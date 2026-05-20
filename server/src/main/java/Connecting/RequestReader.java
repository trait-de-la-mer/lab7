package Connecting;

import Commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.Requester;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RequestReader {
    private ObjectInputStream in;
    private static final Logger log = LogManager.getLogger(RequestReader.class);


    public void init(Socket socket) throws IOException {
        in = new ObjectInputStream(socket.getInputStream());
    }

    public Requester<?> readRequest() throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof Requester<?> req) {
            log.info("Получено: " + req);
            return req;
        }
        throw new ClassCastException("неизвестный тип: " + obj.getClass());
    }

    public void close() {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
    }
}
