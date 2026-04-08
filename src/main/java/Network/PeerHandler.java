package Network;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Crypto.AES128_CTR_MAC;
import Crypto.Constants;
import Crypto.DH;
import Crypto.EncryptedMessage;
import Crypto.HKDF;
import Crypto.Key;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class PeerHandler {
    private final Peer peer;
    private Socket socket;
    private DataOutputStream writer;
    private DataInputStream reader;
    private AES128_CTR_MAC cipher_sex;
    private Map<String, Command> commands = new HashMap<>();
    private Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    PeerHandler(Socket socket, Peer peer) throws IOException {
        this.socket = socket;
        this.peer = peer;
        this.writer = new DataOutputStream(socket.getOutputStream());
        this.reader = new DataInputStream(socket.getInputStream());
        commands.put("/history", new HistoryCommand(peer));
        commands.put("/exit", new ExitCommand(socket, peer));
        commands.put("/back", new BackComman());
    }

    public Socket getSocket() {
        return socket;
    }

    public Peer getPeer() {
        return peer;
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

    /** Отправляет текст собеседнику. 
     * @param text Текст для отправки
     * @param isMessage true добавит сообщение в историю чата. false просто отправит сообщение пользователю
     */
    public void send(String text, boolean isMessage) throws IOException {
        Message message = new Message(peer.getUsername(), text);

        if (isMessage) {
            peer.getHistory().add(message);
        }

        String text_to_encrypt = new String(message.getText());

        EncryptedMessage encryptedMessage = cipher_sex.encrypt(text_to_encrypt.getBytes());

        byte[] message_to_send = encryptedMessage.toBytes(); // THIS IS CRYPTED MESSAGE have to send

        writeBytes(message_to_send);
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

    /**
    * @param Username Наш Никнейм 
    */
    public void handShake(String Username) {

        DH dh = new DH(Constants.generator, Constants.module);
        byte[] keyBytes = dh.getPublicKey().toByteArray();
        writeBytes(keyBytes); // передаём ключ

        byte[] readed_message = readByte();
        BigInteger readedBigInteger = new BigInteger(readed_message);
        dh.generateSharedSecret(readedBigInteger); // получаем ключ

        // -- Обменялись ключами.

        HKDF hkdf = new HKDF(dh.getSharedSecret());
        Key encKey = hkdf.getKey("encryption", 16);
        Key macKey = hkdf.getKey("MAC", 16);

        cipher_sex = new AES128_CTR_MAC(encKey, macKey);

        try {
            send("|UsErNaMe|" + Username, false);
        } catch (IOException e) {
            System.out.println("Ошибка при передачи Username который: " + Username);
        }
        String input;
        input = read();
        if (input.contains("|UsErNaMe|")) {
            input = input.replace("|UsErNaMe|", "");
            peer.setUsername(input);
        }
    }

    public void run() {
        new Thread(new Runnable() {
            public void run() {

                String text = "";
                while (true) {
                    text = read();
                    if (text == null) {
                        System.out.println("Собеседник отключился.111");
                        commands.get("/exit").execute();
                        break;
                    }
                    if (text.equals("/exit")) {
                        System.out.println("Собеседник отключился.222");
                        commands.get("/exit").execute();
                        break;
                    }
                    Message message = new Message(peer.getUsername(), text);
                    if (Server.chatOpened) {
                        System.out.println(message.messageToString());
                    }
                    peer.getHistory().add(message);
                }
            }
        }).start();
    }

    public void runWriter() {
        System.out.println("\n------------- Начало чата -------------");
        System.out.println("Введите '/history' чтобы вывести историю чата.");
        System.out.println("Введите '/back' чтобы выйти из чата.");
        System.out.println("Введите '/exit' чтобы отключиться.");
        System.out.println("АЙПИ СОБЕСЕДНИКА " + socket.getInetAddress() + " и имя СЕБЕСЕДНИКА " + peer.getUsername());
        String text = "";

        Server.chatOpened = true;
        while (Server.chatOpened) {
            text = scanner.nextLine();
            Command command = commands.get(text);
            if (command == null) {
                Message message = null;
                try {
                    send(text, true);
                } catch (IOException e) {
                    System.err.println("Ошибка при отправке сообщения. Сохранено в истории и ВЫХОД.");
                    peer.getHistory().add(message);
                    commands.get("/exit").execute();
                }
            } else {
                command.execute();
            }
        }
    }
}
