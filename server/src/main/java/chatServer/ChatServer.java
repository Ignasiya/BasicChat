package chatServer;

import network.TCPConnection;
import network.TCPListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(55555)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendBroadcast("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendBroadcast(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendBroadcast("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendBroadcast(String value) {
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) connections.get(i).sendString(value);
    }
}
