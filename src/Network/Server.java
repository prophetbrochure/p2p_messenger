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

                        if (!peersList.isEmpty()) {
                            for (PeerHandler peerHandler : peersList) {
                                System.out.println("Просматриваем пирлист");
                                Peer peer = peerHandler.getPeer();
                                if (peer.getIp().equals(socket.getLocalAddress()) & peer.getPort() == socket.getLocalPort()) {
                                    System.out.println("ВХОДЯЩЕЕ: Такой пользователь уже был подключен. Подключам снова ");
                                    peerHandler.run();
                                    // peerHandler.runWriter();
                                }
                            }
                        } else {
                            Peer peer = new Peer(socket.getLocalAddress(), socket.getLocalPort());
                            PeerHandler peerHandler = new PeerHandler(socket, peer);
                            peerHandler.run();
                            peersList.add(peerHandler);
                        }
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

            if (!peersList.isEmpty()) {
                for (PeerHandler peerHandler : peersList) {
                    System.out.println("Просматриваем пирлист");
                    Peer peer = peerHandler.getPeer();
                    System.out.println("Новый " + ip + " и порт" + port + "\n\n");
                    System.out.println("Старый " + peer.getIp() + " и порт" + peer.getPort());

                    if (peer.getIp().equals(ip) & peer.getPort() == port) {
                        System.out.println("Такой пользователь уже был подключен. Подключам снова");
                        peerHandler.run();
                        // peerHandler.runWriter();
                    }
                }
            } else {
                Socket socket = new Socket(ip, port);
                Peer peer = new Peer(socket.getLocalAddress(), socket.getLocalPort());
                PeerHandler peerHandler = new PeerHandler(socket, peer);
                peerHandler.run();
                peersList.add(peerHandler);
                peerHandler.runWriter();
            }
                
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
