package Network;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.List;

public class Peer {
    private InetAddress ip;
    private int port;
    private List<String> history = new ArrayList<>();

    Peer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    InetAddress getIp() {
        return this.ip;
    }

    int getPort() {
        return this.port;
    }

    List<String> getHistory() {
        return this.history;
    }
}
