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

                String message = "";
                while (true) {
                    try (InputStream input = socket.getInputStream();
                    InputStreamReader iSReader = new InputStreamReader(input);
                    BufferedReader reader = new BufferedReader(iSReader);) {
                        
                        if (peer.getUsername() == null) {
                            String Username = "";
                            message = reader.readLine();
                            if (message.contains("|UsErNaMe|")) {
                                message = message.replace("|UsErNaMe|", "");
                                Username = message;
                                peer.setUsername(Username);
                                // System.out.println("Username собеседника: " + peer.getUsername());
                            }
                        } else {
                            reader.readLine();
                        }
                        while (!message.equals("/exit")) {
                            message = reader.readLine();
                            if (message == null) {
                                System.out.println("NULL HAVE NULL");
                                break;
                            }
                            if (Server.chatOpened) {
                                System.out.println("[" + peer.getUsername() + " | " + socket.getInetAddress() + "]: " + message);
                            }
                            peer.getHistory().add("[" + peer.getUsername() + " | " + socket.getInetAddress() + "]: " + message);
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
        
    public void runWriter(String Username) {
        
        System.out.println("\n------------- Начало чата -------------");
        System.out.println("Введите '/history' чтобы вывести историю чата.");
        System.out.println("Введите '/back' чтобы выйти из чата.");
        System.out.println("Введите '/exit' чтобы отключиться.");
        System.out.println("АЙПИ СОБЕСЕДНИКА " + socket.getInetAddress() + " и порт СЕБЕСЕДНИКА " + socket.getPort());
        String message = "";
        
        Server.chatOpened = true;
        if (peer.getUsername() == null) {
            try {
                send("|UsErNaMe|" + Username);
            } catch (IOException e) {
                System.out.println("Пользователь не в сети.");
            }
        }
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
                }
            } else {
                command.execute();
            }
        }
        System.err.println("ВЫход из ввода run2");
    }
}
