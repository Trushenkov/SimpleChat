package ru.tds.client;

import ru.tds.network.TCPConnection;
import ru.tds.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Класс, в котором реализован графический интерфейс главного окна чата.
 *
 * @author Трушенков Дмитрий 15ИТ18
 */
public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "192.168.0.9";

    private static final int PORT = 8189;

    private static final int WIDTH = 400;

    private static final int HEIGHT = 600;

    private final JTextField fieldNickname = new JTextField("Гость");

    private final JTextArea log = new JTextArea();

    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientWindow());
    }

    private ClientWindow() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        add(fieldNickname, BorderLayout.NORTH);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);

        setVisible(true);
        try{
            connection = new TCPConnection(this,IP_ADDR,PORT);

        }catch (IOException e) {
            printMessage("Connection exception: " +e);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText()+": " +msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready... ");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String string) {
        printMessage(string);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close.");
    }

    @Override
    public void onException(TCPConnection tcpConnection, IOException exception) {
        printMessage("Connection exception :" + exception);
    }

    /**
     * Метод для печати сообщения в текстовой области.
     *
     * @param message сообщение
     */
    private synchronized void printMessage(String message){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}

