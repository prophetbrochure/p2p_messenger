package Network;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.List;

public class Peer {
    private final InetAddress ip;
    private final int port;
    private List<String> history = new ArrayList<>();
    private boolean isConnected;

    Peer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.isConnected = true;
    }

    InetAddress getIp() {
        return ip;
    }

    int getPort() {
        return port;
    }

    List<String> getHistory() {
        return history;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
