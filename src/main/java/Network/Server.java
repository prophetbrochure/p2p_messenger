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
    private static app.ChatWindow chatWindowInstance;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket();
        this.serverSocket.setReuseAddress(true);
        this.serverSocket.bind(new java.net.InetSocketAddress(port));
    }

    public void setChatWindow(app.ChatWindow window) {
        chatWindowInstance = window;
    }

    public static app.ChatWindow getChatWindow() {
        return chatWindowInstance;
    }

    public void closeServer() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии Сервера");
        }
    }

    public void start(String myUsername) {
        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Входящее подключение: " + socket.getInetAddress());

                    Peer newPeerData = new Peer(socket.getInetAddress(), socket.getPort());
                    PeerHandler newHandler = new PeerHandler(socket, newPeerData);
                    newHandler.handShake(myUsername);

                    processConnection(newHandler);

                } catch (IOException e) {
                    break;
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void connect(String ip, int port, String myUsername) {
        System.out.println("Попытка подключиться к: " + ip);
        try {
            Socket socket = new Socket(ip, port);
            Peer newPeerData = new Peer(socket.getInetAddress(), socket.getPort());
            PeerHandler newHandler = new PeerHandler(socket, newPeerData);
            newHandler.handShake(myUsername);

            processConnection(newHandler);

        } catch (IOException e) {
            handleReconnection(ip, port, myUsername);
        }
    }

    /**
     * Общая логика для входящих и исходящих соединений.
     * Проверяет, есть ли пользователь в списке, обновляет его или добавляет новый.
     */
    private void processConnection(PeerHandler newHandler) {
        boolean isAlreadyInList = false;
        InetAddress remoteIp = newHandler.getPeer().getIp();
        String remoteName = newHandler.getPeer().getUsername();

        for (int i = 0; i < peersList.size(); i++) {
            PeerHandler existingHandler = peersList.get(i);
            Peer existingPeer = existingHandler.getPeer();

            // Проверяем по IP и имени
            if (existingPeer.getIp().equals(remoteIp) && existingPeer.getUsername().equals(remoteName)) {
                isAlreadyInList = true;

                // Закрываем старый сокет, если он вдруг еще открыт
                try { existingHandler.getSocket().close(); } catch (Exception e) {}

                // Переносим старую историю в новый объект
                newHandler.getPeer().setHistory(existingPeer.getHistory());
                newHandler.getPeer().setOnline(true); // Ставим статус Online

                // Заменяем старый хендлер новым (с активным сокетом)
                peersList.set(i, newHandler);
                newHandler.run();
                break;
            }
        }

        if (!isAlreadyInList) {
            peersList.add(newHandler);
            newHandler.run();
        }

        if (chatWindowInstance != null) {
            chatWindowInstance.updateUI();
        }
    }

    private void handleReconnection(String ip, int port, String myUsername) {
        try {
            if (reConnectionAttempts-- > 0) {
                System.out.println("Не удалось. Повтор через 3 сек...");
                Thread.sleep(3000);
                connect(ip, port, myUsername);
            } else {
                System.out.println("Не удалось подключиться. Пользователь оффлайн.");
                reConnectionAttempts = 3;
                if (chatWindowInstance != null) chatWindowInstance.updateUI();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}