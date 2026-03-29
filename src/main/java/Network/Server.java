package Network;

import java.io.IOException;

import java.net.Socket;
import java.net.InetAddress;
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

    public void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии Сервера");
            e.printStackTrace();
        }
    }

    public void start() {
        // Создается отдельный поток,
        // который ждёт и обрабатывает подключения
        new Thread(new Runnable() {
            public void run() {
                System.out.println("Ожидание подключения...");
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Входящее подключение: " + socket.getInetAddress());

                    if (!peersList.isEmpty()) {
                        for (int i = 0; i < peersList.size(); i++) {
                            PeerHandler peerHandler = peersList.get(i);
                            Peer peer = peerHandler.getPeer();

                            if (peer.getIp().equals(socket.getInetAddress()) & peer.getPort() == socket.getPort()) {
                                // System.out.println("ВХОДЯЩЕЕ: Такой пользователь уже был подключен. Подключам снова ");
                                peerHandler.getSocket().close();
                                peerHandler = new PeerHandler(socket, peer);
                                peersList.set(i, peerHandler);
                                peerHandler.run();
                                break;
                            }
                        }
                    } else {
                        Peer peer = new Peer(socket.getInetAddress(), socket.getPort());
                        PeerHandler peerHandler = new PeerHandler(socket, peer);
                        peerHandler.run();
                        peersList.add(peerHandler);
                    }
                } catch (IOException e) {} // Если при первом запуске, сразу выключить сервер
            }
        }).start();
    }

    public void connect(String ip, int port) {
        System.out.println("Попытка подключиться к: " + ip);
        try {

            if (!peersList.isEmpty()) {
                for (int i = 0; i < peersList.size(); i++) {
                    PeerHandler peerHandler = peersList.get(i);
                    Peer peer = peerHandler.getPeer();
                    InetAddress inetIp = InetAddress.getByName(ip);
                    
                    if (peer.getIp().equals(inetIp) && peer.getPort() == port) {
                        Socket socket = new Socket(ip, port, inetIp, peerHandler.getSocket().getLocalPort());
                        peerHandler.getSocket().close();
                        peerHandler = new PeerHandler(socket, peer);
                        peersList.set(i, peerHandler);
                        peerHandler.run();
                        peerHandler.runWriter();
                    }
                }
            } else {
                Socket socket = new Socket(ip, port);
                Peer peer = new Peer(socket.getInetAddress(), socket.getPort());
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
                        reConnectionAttempts = 3;
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
        }
    }
}
