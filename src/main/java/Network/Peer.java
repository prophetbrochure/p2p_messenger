package Network;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.List;

public class Peer {
    private final InetAddress ip;
    private final int port;
    private List<Message> history = new ArrayList<Message>();
    private String Username;

    public Peer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    List<Message> getHistory() {
        return history;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public void setHistory(List<Message> history) {
        this.history = history;
    }
}
