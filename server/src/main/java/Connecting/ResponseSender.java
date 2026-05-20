package Connecting;

import tools.Requester;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Модуль отправки ответов: сериализует и отправляет Requester<String> клиенту
 */
public class ResponseSender {
    private ObjectOutputStream out;

    public void init(Socket socket) throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void sendResponse(String text) throws IOException {
        Requester<String> response = new Requester<>();
        response.setArgs(text);
        out.writeObject(response);
        out.flush();
    }

    public void close() {
        try { if (out != null) out.close(); } catch (IOException ignored) {}
    }
}