package Network;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.List;

public class Peer {
    private final InetAddress ip;
    private final int port;
    private List<String> history = new ArrayList<>();
    private String Username;
    private boolean isActive;

    public Peer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.isOnline = true;
    }

    public List<String> getHistory() {
        return history;
    }

    public InetAddress getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    public String getUsername() {
        return Username;
    }
    public boolean isActive() {
        return isActive;
    }
    public void addMessageToHistory(String message) {
        this.history.add(message);
    }
    private boolean isOnline = true;

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }

    // TODO в новой реализации, где пир либо актив либо нет, название connected
    // не корректно новерное
    public void setActive(boolean connected) {
        isActive = connected;
    }
}
