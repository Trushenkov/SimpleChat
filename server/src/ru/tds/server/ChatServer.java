package ru.tds.server;

import ru.tds.network.TCPConnection;
import ru.tds.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Класс, в котором описано поведение сервера чата.
 *
 * @author Трушенков Дмитрий 15ИТ18
 */
public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running");

        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPCOnnection exception :" + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected :" + tcpConnection);
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String string) {
        sendToAllConnections(string);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, IOException exception) {
        System.out.println("TCPConnection exception :" + exception);
    }

    /**
     * Метод для отправки сообщения всем пользователям.
     *
     * @param value сообщение для отправки
     */
    private void sendToAllConnections(String value) {
        System.out.println(value);
        for (TCPConnection connection : connections) {
            connection.sendString(value);
        }
    }
}
