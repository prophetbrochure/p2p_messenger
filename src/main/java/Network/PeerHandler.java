package Network;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.OutputStreamWriter;

public class PeerHandler {
    private final Peer peer;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Map<String, Command> commands = new HashMap<>();
    private Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    PeerHandler(Socket socket, Peer peer) throws IOException  {
        this.socket = socket;
        this.peer = peer;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        commands.put("/history", new HistoryCommand(peer));
        commands.put("/exit", new ExitCommand(peer, socket, writer));
        commands.put("/back", new BackComman());
    }

    public Socket getSocket() {
        return socket;
    }
    
    public Peer getPeer() {
        return peer;
    }
    
    public void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public void read(String message) {
        if (Server.chatOpened) {
            System.out.println("[" + peer.getUsername() + " | " + socket.getInetAddress() + "]: " + message);
        }
        peer.getHistory().add("[" + peer.getUsername() + " | " + socket.getInetAddress() + "]: " + message);
    }
    

    public void handShake(String Username) {
        System.out.println("ПРОВЕРКА: " + peer.getUsername());
        //TODO Отправление ключей шифрование тут
        try {
            send("|UsErNaMe|" + Username);
        } catch (IOException e) {
            System.out.println("Ошибка при передачи Username который: " + Username);
            e.printStackTrace();
        }
        String input;
        try {
            input = reader.readLine();
            if (input.contains("|UsErNaMe|")) {
                input = input.replace("|UsErNaMe|", "");
                peer.setUsername(input);
                System.out.println("Username собеседника: " + peer.getUsername());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении Username");
            e.printStackTrace();
        }
    }

    public void run() {
        new Thread(new Runnable() {
            public void run() {

                String message = "";
                // while (true) {
                    try {
                        while (true) {
                            message = reader.readLine();
                            if (message == null) {
                                System.out.println("NULL HAVE NULL");
                                break;
                            }
                            if (message.equals("/exit")) {
                                System.out.println("Собеседник отключился.");
                                reader.close();
                                socket.close(); // ЭКСПЕРИМЕНТАЛЬНАЯ КОМАНДА
                                break;
                            }
                            read(message);
                        }
                    } catch (IOException e1) {
                        System.out.println("Собеседник отключился. с ошибкой");
                        // break;
                    }
                // }
            }
        }).start();
    }
        
    public void runWriter() {
        System.out.println("\n------------- Начало чата -------------");
        System.out.println("Введите '/history' чтобы вывести историю чата.");
        System.out.println("Введите '/back' чтобы выйти из чата.");
        System.out.println("Введите '/exit' чтобы отключиться.");
        System.out.println("АЙПИ СОБЕСЕДНИКА " + socket.getInetAddress() + " и порт СЕБЕСЕДНИКА " + socket.getPort());
        String message = "";
        
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
                }
            } else {
                command.execute();
            }
        }
        System.err.println("ВЫход из ввода run2");
    }
}
