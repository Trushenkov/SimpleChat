package ru.tds.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Класс,в котором создается и устанавливается соединение с сервером.
 *
 * @author Трушенков Дмитрий
 */
public class TCPConnection {

    private final Socket socket;

    private final Thread thread;

    private final TCPConnectionListener eventListener;

    private final BufferedReader in;

    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String ipAddress, int port) throws IOException {
        this(eventListener, new Socket(ipAddress, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {

        this.socket = socket;
        this.eventListener = eventListener;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted()) {
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });

        thread.start();

    }

    /**
     * Метод для отправки сообщения.
     *
     * @param value сообщение, которое нужно отправить
     */
    public synchronized void sendString(String value) {
        try {
            out.write(value + "\n");
            out.flush();
        }
        catch (IOException e) {
            eventListener.onException(this, e);
            disconnect();
        }
    }

    /**
     * Метод для отсоединения пользователя от сервера.
     */
    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

}
