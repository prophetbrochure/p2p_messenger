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
import javafx.application.Platform;


public class PeerHandler {
    private final Peer peer;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader; // Твой поток ввода называется reader
    private MessageListener uiListener;

    public PeerHandler(Socket socket, Peer peer) throws IOException {
        this.socket = socket;
        this.peer = peer;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    public void setUiListener(MessageListener listener) {
        this.uiListener = listener;
    }

    public Peer getPeer() { return peer; }
    public Socket getSocket() { return socket; }

    public void send(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeConnection();
        }
    }

    public void read(String message) {
        // Сохраняем в историю ПИРА (с пометкой, что это входящее)
        peer.getHistory().add("Other:" + message);

        // Оповещаем окно. Теперь окно само проверит, открыт ли этот пир
        if (Server.getChatWindow() != null) {
            Server.getChatWindow().onIncomingMessage(peer, message);
        }
    }

    public void handShake(String myUsername) {
        try {
            send("|UsErNaMe|" + myUsername);
            String response = reader.readLine();
            if (response != null && response.contains("|UsErNaMe|")) {
                peer.setUsername(response.replace("|UsErNaMe|", ""));
            }
        } catch (IOException e) {
            closeConnection();
        }
    }

    public void run() {
        Thread listenThread = new Thread(() -> {
            try {
                String input;
                while ((input = reader.readLine()) != null) {
                    if (!input.contains("|UsErNaMe|")) {
                        read(input);
                    }
                }
            } catch (IOException e) {
                System.out.println("DEBUG: Поток чтения прерван (IOException)");
            } finally {
                System.out.println("DEBUG: Собеседник отключился, очищаем ресурсы");
                closeConnection();
            }
        });
        listenThread.setDaemon(true);
        listenThread.start();
    }


    // В PeerHandler.java
    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // ВАЖНО: Не удаляем из списка, а помечаем как оффлайн
            this.getPeer().setOnline(false);

            System.out.println("DEBUG: Пользователь " + getPeer().getUsername() + " ушел в оффлайн.");

            Platform.runLater(() -> {
                if (Server.getChatWindow() != null) {
                    Server.getChatWindow().updateUI();
                }
            });
        }
    }
}