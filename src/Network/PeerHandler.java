package Network;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStreamWriter;

public class PeerHandler {
    private final Peer peer;
    private Socket socket;
    private BufferedWriter writer;
    private Map<String, Command> commands = new HashMap<>();
    private Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    PeerHandler(Socket socket, Peer peer) throws IOException  {
        this.socket = socket;
        this.peer = peer;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        commands.put("/history", new HistoryCommand(peer));
        commands.put("/exit", new ExitCommand(peer, socket, writer));
        commands.put("/back", new BackCommand());
    }

    public void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public Peer getPeer() {
        return peer;
    }

    public void run() {
        new Thread(new Runnable() {
            public void run() {
                System.out.println("\n------------- Чтение -------------");
                String message = "";

                while (true) {

                    try (InputStream input = socket.getInputStream();
                    InputStreamReader iSReader = new InputStreamReader(input);
                    BufferedReader reader = new BufferedReader(iSReader);) {
                        
                        while (!message.equals("/exit")) {
                            message = reader.readLine();
                            if (message == null) {
                                System.out.println("NULL HAVE NULL");
                                break;
                            }
                            if (Server.chatOpened) {
                                System.out.println("[" + socket.getInetAddress() + " | " + socket.getPort() + "]: " + message);
                            }
                            peer.getHistory().add("[" + socket.getInetAddress() + " | " + socket.getPort() + "]: " + message);
                        }
                        System.out.println("Собеседник отключился.");
                        socket.close(); // ЭКСПЕРИМЕНТАЛЬНАЯ КОМАНДА
                        break;
                    } catch (IOException e1) {
                        System.out.println("Собеседник отключился. с ошибкой");
                        break;
                    }
                }
            }
        }).start();
    }
        
    public void runWriter() {
        
        System.out.println("\n------------- Начало чата -------------");
        System.out.println("Введите '/history' чтобы вывести историю чата.");
        System.out.println("Введите '/back' чтобы выйти из чата.");
        System.out.println("Введите '/exit' чтобы отключиться.");
        String message = "";
        System.out.println("АЙПИ СОБЕСЕДНИКА " + socket.getInetAddress() + " и порт СЕБЕСЕДНИКА " + socket.getPort());
        
        Server.chatOpened = true;
        while (Server.chatOpened) {
            message = scanner.nextLine();
            Command command = commands.get(message);
            if (command == null) {
                try {
                    send(message);
                    peer.getHistory().add("[Me]: " + message);
                } catch (IOException e) {
                    System.err.println("Ошибка при отправке сообщения. Сохранено в истории.");
                    peer.getHistory().add("[Me]: " + message);
                    // e.printStackTrace();
                }
            } else {
                command.execute();
            }
        }
        System.err.println("ВЫход из ввода run2");
    }
}
