package Network;

import java.net.Socket;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class PeerHandler implements Runnable {
    private final Socket socket;
    private final Peer peer;
    private Map<String, Command> commands = new HashMap<>();

    PeerHandler(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
        commands.put("/history", new HistoryCommand(peer));
        commands.put("/exit", new ExitCommand(peer, socket));
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            public void run() {
                System.out.println("\n------------- Начало чата -------------");
                String message = "";

                try (InputStream input = socket.getInputStream();
                        InputStreamReader iSReader = new InputStreamReader(input);
                        BufferedReader reader = new BufferedReader(iSReader);) {

                    while (!message.equals("/exit")) {
                        message = reader.readLine();
                        System.out.println("[" + socket.getLocalAddress() + "]: " + message);
                        peer.getHistory().add("[" + socket.getLocalAddress() + "]: " + message);
                    }
                    socket.close();
                    System.out.println("Собеседник покинул чат. Нажмите Enter...");

                } catch (IOException e1) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Собеседник покинул чат. Нажмите Enter...");
                }
            }
        }).start();

        System.out.println("Введите '/exit' чтобы отключиться.");
        System.out.println("Введите '/history' чтобы вывести историю чата.");
        String message = "";

        try (OutputStream output = socket.getOutputStream();
                OutputStreamWriter oSWriter = new OutputStreamWriter(output);
                BufferedWriter writer = new BufferedWriter(oSWriter);
                Scanner scanner = new Scanner(System.in)) {

            while (peer.isConnected()) {
                message = scanner.nextLine();
                Command command = commands.get(message);
                if (command == null) {
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                    peer.getHistory().add("[Me]: " + message);
                } else {
                    command.execute();
                }
            }
        } catch (IOException e1) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
