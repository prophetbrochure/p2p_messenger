package p2pmessenger.network;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import p2pmessenger.crypto.AES128_CTR_MAC;
import p2pmessenger.crypto.Constants;
import p2pmessenger.crypto.DH;
import p2pmessenger.crypto.EncryptedMessage;
import p2pmessenger.crypto.HKDF;
import p2pmessenger.crypto.Key;
import p2pmessenger.network.command.BackCommand;
import p2pmessenger.network.command.Command;
import p2pmessenger.network.command.ExitCommand;
import p2pmessenger.network.command.HistoryCommand;
import p2pmessenger.network.command.VladCommand;
import p2pmessenger.network.model.Message;
import p2pmessenger.network.model.Peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class PeerHandler {
    private final Peer peer;
    private final Server server;
    private final Socket socket;
    private final DataOutputStream writer;
    private final DataInputStream reader;
    private final Map<String, Command> commands = new HashMap<>();
    private AES128_CTR_MAC cipher_sex;
    private String username; // НАШ ник
    private Scanner scanner;

    public PeerHandler(Socket socket, Peer peer, Server server) throws IOException {
        this.socket = socket;
        this.peer = peer;
        this.server = server;
        this.scanner = server.getScanner();
        this.writer = new DataOutputStream(socket.getOutputStream());
        this.reader = new DataInputStream(socket.getInputStream());
        commands.put("/vlad", new VladCommand(this, commands));
        commands.put("/history", new HistoryCommand(peer));
        commands.put("/exit", new ExitCommand(server, socket, peer));
        commands.put("/back", new BackCommand(server));
    }

    public void run() {
        if (cipher_sex == null) {
            System.out.println("Ключ не был передан. Выход");
        } else {
            new Thread(() -> {
                
                String text = "";
                while (true) {
                    text = read();
                    if (text == null || "/exit".equals(text)) {
                        System.out.println("Собеседник отключился.");
                        commands.get("/exit").execute();
                        break;
                    }
                    Message message = new Message(peer.getUsername(), text);
                    if (server.getChatOpened()) {
                        System.out.println(message.messageToString());
                    }
                    peer.getHistory().add(message);
                }
            }).start();
        }
    }

    public void runWriter() {
        if (cipher_sex == null) {
            System.out.println("Ключ не был передан. Выход");
        } else {
            System.out.println("\n------------- Начало чата -------------");
            System.out.println("Введите '/history' чтобы вывести историю чата.");
            System.out.println("Введите '/back' чтобы выйти из чата.");
            System.out.println("Введите '/exit' чтобы отключиться.");
            System.out.println("Айпи собеседника: " + socket.getInetAddress() + ", имя собеседника: " + peer.getUsername());
            String text = "";
            
            server.setChatOpened(true);
            while (server.getChatOpened()) {
                text = scanner.nextLine();
                Command command = commands.get(text);
                if (command == null) {
                    try {
                        send(text);
                    } catch (IOException e) {
                        System.err.println("Ошибка при отправке сообщения.");
                        commands.get("/exit").execute();
                    }
                } else {
                    command.execute();
                }
            }
        }
    }

    /**
     * @param username Наш Никнейм
     */
    public void handShake(String username) {
        this.username = username;

        DH dh = new DH(Constants.generator, Constants.module);
        byte[] keyBytes = dh.getPublicKey().toByteArray();
        writeBytes(keyBytes); // передаём ключ

        byte[] readed_message = readByte();
        if (readed_message == null) {
            System.out.println("Неудалось прочитать Ключ");
            return;
        }

        System.out.println("ПРОЧИТАЛИ КЛЮЧ ТОЛЬКО СЕЙЧАС");

        BigInteger readedBigInteger = new BigInteger(readed_message);
        dh.generateSharedSecret(readedBigInteger); // получаем ключ

        // -- Обменялись ключами.

        HKDF hkdf = new HKDF(dh.getSharedSecret());
        Key encKey = hkdf.getKey("encryption", 16);
        Key macKey = hkdf.getKey("MAC", 16);

        cipher_sex = new AES128_CTR_MAC(encKey, macKey);

        String text_to_encrypt = "|UsErNaMe|" + username;
        EncryptedMessage encryptedMessage = cipher_sex.encrypt(text_to_encrypt.getBytes());
        byte[] bytesUsername = encryptedMessage.toBytes();
        writeBytes(bytesUsername);

        String input;
        input = read();
        if (input.contains("|UsErNaMe|")) {
            input = input.replace("|UsErNaMe|", "");
            peer.setUsername(input);
        }
    }

    /**
     * Отправляет текст собеседнику.
     * 
     * @param text      Текст для отправки
     * @param isMessage true добавит сообщение в историю чата. false просто отправит
     *                  сообщение пользователю
     */
    public void send(String text) throws IOException {
        Message message = new Message(username, text);

        peer.getHistory().add(message);

        String text_to_encrypt = new String(message.getText());

        EncryptedMessage encryptedMessage = cipher_sex.encrypt(text_to_encrypt.getBytes());

        byte[] message_to_send = encryptedMessage.toBytes(); // THIS IS CRYPTED MESSAGE have to send

        writeBytes(message_to_send);
    }

    public void writeBytes(byte[] bytes) {
        try {
            writer.writeInt(bytes.length);
            writer.write(bytes);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Ошибка в отправки байтов");
            commands.get("/exit").execute();
        }
    }

    public String read() {

        byte[] readed_message = readByte();

        if (readed_message == null) {
            return null;
        }

        // пришёл массив из байт

        EncryptedMessage encryptedMessage4 = EncryptedMessage.fromBytes(readed_message);

        byte[] Byte_FromCryptMessage = cipher_sex.decrypt(encryptedMessage4);

        Byte_FromCryptMessage.toString(); // принтануть, это и есть сообщение

        String text = new String(Byte_FromCryptMessage, StandardCharsets.UTF_8); // В UTF=8
        return text;
    }

    public byte[] readByte() {
        int length = 0;
        byte[] readed_message = null;
        try {
            length = reader.readInt();
            readed_message = new byte[length];
            reader.readFully(readed_message);
        } catch (IOException e) {
            return null;
        }
        return readed_message;
    }

    public Socket getSocket() {
        return socket;
    }

    public Peer getPeer() {
        return peer;
    }
}
