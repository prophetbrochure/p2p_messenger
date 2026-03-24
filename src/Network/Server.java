package Network;

import java.io.IOException;

// ТЕСТОВАЯ ВЕРСИЯ, СО СВОИМИ импровизированными
// ВХОДНЫМИ ДАННЫМИ

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<Peer> peersList;

    Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() {
        // Создается
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("Подключен: " + socket.getLocalAddress());
                        peersList.add(new Peer(socket.getLocalAddress(), socket.getLocalPort()));
                        new Thread(new PeerHandler(socket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // TODO здесь попытка Подключения к другим.
    }

}
