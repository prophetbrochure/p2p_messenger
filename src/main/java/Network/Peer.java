package Network;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.List;

public class Peer {
    private final InetAddress ip;
    private final int port;
    private List<String> history = new ArrayList<>();
    private boolean isActive;

    public Peer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.isActive = true;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    List<String> getHistory() {
        return history;
    }

    public boolean isActive() {
        return isActive;
    }

    // TODO в новой реализации, где пир либо актив либо нет, название connected
    // не корректно новерное
    public void setActive(boolean connected) {
        isActive = connected;
    }
}
