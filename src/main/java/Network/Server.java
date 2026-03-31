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

    public void start(String Username) {
        // Создается отдельный поток,
        // который ждёт и обрабатывает подключения
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("Входящее подключение: " + socket.getInetAddress());

                        Peer peer = new Peer(socket.getInetAddress(), socket.getPort());
                        PeerHandler peerHandler = new PeerHandler(socket, peer);
                        peerHandler.handShake(Username);
    
                        boolean isUserInPeersList = false;
                        for (int i = 0; i < peersList.size(); i++) {
                            PeerHandler tempPeerHandler = peersList.get(i);
                            Peer tempPeer = tempPeerHandler.getPeer();
    
                            if (tempPeer.getIp().equals(socket.getInetAddress()) && tempPeer.getUsername().equals(peerHandler.getPeer().getUsername())) {
                                isUserInPeersList = true;
                                // System.out.println("ВХОДЯЩЕЕ: Такой пользователь уже был подключен. Подключаем снова");
                                tempPeerHandler.getSocket().close(); // ? надо ? закрывать
                                peerHandler.getPeer().setHistory(tempPeerHandler.getPeer().getHistory()); // TODO поправить эту огромную строку тут, и в функции ниже
                                peersList.set(i, peerHandler);
                                peerHandler.run();
                                break;
                            }
                        }
                        if (!isUserInPeersList) {
                            // System.out.println("ВХОДЯЩЕЕ: Новый пользователь. Подключаем");
                            peersList.add(peerHandler);
                            peerHandler.run();
                        }
                    } catch (IOException e) {} // Если при первом запуске, сразу выключить сервер
                }
            }
        }).start();
    }

    public void connect(String ip, int port, String Username) {
        System.out.println("Попытка подключиться к: " + ip);
        try {

            Socket socket = new Socket(ip, port);
            Peer peer = new Peer(socket.getInetAddress(), socket.getPort());
            PeerHandler peerHandler = new PeerHandler(socket, peer);
            peerHandler.handShake(Username);

            boolean isUserInPeersList = false;
            for (int i = 0; i < peersList.size(); i++) {
                PeerHandler tempPeerHandler = peersList.get(i);
                Peer tempPeer = tempPeerHandler.getPeer();
                InetAddress inetIp = InetAddress.getByName(ip);
                
                if (tempPeer.getIp().equals(inetIp) && tempPeer.getUsername().equals(peerHandler.getPeer().getUsername())) {
                    // System.out.println("ОТПРАВКА: Такой пользователь уже был подключен. Подключаем снова ");
                    isUserInPeersList = true;
                    tempPeerHandler.getSocket().close(); // ? надо ? закрывать
                    peerHandler.getPeer().setHistory(tempPeerHandler.getPeer().getHistory());
                    peersList.set(i, peerHandler);
                    peerHandler.run();
                    peerHandler.runWriter();
                }
            }
            if (!isUserInPeersList) {
                // System.out.println("ОТПРАВКА: Новый пользователь. Подключаем");
                peersList.add(peerHandler);
                peerHandler.run();
                peerHandler.runWriter();
            }
        } catch (IOException e) {
            // Попытка переподключения
            try {
                if (reConnectionAttempts-- > 0) {
                    System.out.println("Не удалось.\n");
                    Thread.sleep(3000);
                    connect(ip, port, Username);
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
