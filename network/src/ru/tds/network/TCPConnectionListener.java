package ru.tds.network;

import java.io.IOException;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);

    void onReceiveString(TCPConnection tcpConnection, String string);

    void onDisconnect(TCPConnection tcpConnection);

    void onException(TCPConnection tcpConnection, IOException exception);

}
