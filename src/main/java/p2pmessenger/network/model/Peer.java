package p2pmessenger.network.model;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.List;

public class Peer {
    private final InetAddress ip;
    private final int port;
    private final List<Message> history = new ArrayList<Message>();
    private String username;

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

    public List<Message> getHistory() {
        return history;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
