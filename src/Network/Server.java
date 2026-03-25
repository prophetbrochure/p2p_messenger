// ТЕСТОВАЯ ВЕРСИЯ
package Network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private List<Peer> peersList = new ArrayList<>();
    int reConnectionAttempts = 3;

    Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        System.out.println("Сервер создан на порту: " + port);
    }

    public void start() {
        // Создается отдельный поток,
        // который ждёт и обрабатывает подключения
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("Входящее подключение: " + socket.getLocalAddress());
                        peersList.add(new Peer(socket.getLocalAddress(), socket.getLocalPort()));
                        new Thread(new PeerHandler(socket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void connect(String ip, int port) {
        try {
            System.out.println("Попытка подключиться к: " + ip);
            Socket socket = new Socket(ip, port);

            OutputStream output = socket.getOutputStream();
            OutputStreamWriter oSWriter = new OutputStreamWriter(output);
            BufferedWriter writer = new BufferedWriter(oSWriter);

            System.out.println("Введите /exit чтобы отключиться");
            Scanner scanner = new Scanner(System.in);

            String message = "";
            try {
                while (!message.equals("/exit")) {
                    message = scanner.nextLine();
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                }
                System.out.println("Чат закрывается.");

                scanner.close();
                writer.close();
                oSWriter.close();
                output.close();
                socket.close();

            } catch (IOException e1) {
                scanner.close();
                writer.close();
                oSWriter.close();
                output.close();
                socket.close();
                System.out.println("Собеседник покинул чат.");
            }
        } catch (IOException e) {
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
