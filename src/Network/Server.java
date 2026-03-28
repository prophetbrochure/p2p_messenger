package Network;

import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;

import java.util.List;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private int reConnectionAttempts = 3;
    public static List<PeerHandler> peersList = new ArrayList<>();
    public static boolean chatOpened = false;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() {
        // Создается отдельный поток,
        // который ждёт и обрабатывает подключения
        new Thread(new Runnable() {
            public void run() {
                System.out.println("Ожидание подключения...");
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("Входящее подключение: " + socket.getLocalAddress());

                        Peer peer = new Peer(socket.getLocalAddress(), socket.getLocalPort());
                        PeerHandler peerHandler = new PeerHandler(socket, peer);
                        peerHandler.run();
                        peersList.add(peerHandler);
                    } catch (IOException e) {
                        System.out.println("\n\nEROR\n\nНеразу не ловил эту ошибку ещё");
                    }
                }
            }
        }).start();
    }

    public void connect(String ip, int port) {
        System.out.println("Попытка подключиться к: " + ip);
        try {

            Socket socket = new Socket(ip, port);
            Peer peer = new Peer(socket.getLocalAddress(), socket.getLocalPort());
            PeerHandler peerHandler = new PeerHandler(socket, peer);
            peerHandler.run();
            peersList.add(peerHandler);
            peerHandler.run2();

        } catch (IOException e) {
            // Попытка переподключения
            try {
                if (reConnectionAttempts-- > 0) {
                    System.out.println("Неудалось.\n");
                    Thread.sleep(3000);
                    connect(ip, port);
                } else {
                    System.out.println("Введён неверный Адрес, или пользователь не в сети.");
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
