package Network;

// ТЕСТОВАЯ ВЕРСИЯ, СО СВОИМИ импровизированными
// ВХОДНЫМИ ДАННЫМИ

import java.net.InetAddress;
import java.util.List;

public class Peer {
    private InetAddress ip;
    private int port;
    private List<String> history;

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
}
