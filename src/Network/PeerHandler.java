package Network;

// ТЕСТОВАЯ ВЕРСИЯ, СО СВОИМИ импровизированными
// ВХОДНЫМИ ДАННЫМИ

import java.net.Socket;

public class PeerHandler implements Runnable {
    private Socket socket;

    PeerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // TODO ожидание сообщения от собеседника
    }
}
