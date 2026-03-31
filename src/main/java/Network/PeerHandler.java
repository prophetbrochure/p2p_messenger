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
        // this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.writer = new DataOutputStream(socket.getOutputStream());
        // this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.reader = new DataInputStream(socket.getInputStream());
        commands.put("/history", new HistoryCommand(peer));
        commands.put("/exit", new ExitCommand(socket, writer));
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
            e.printStackTrace();
        }
    }

    public void send(String text) throws IOException {
        Message message = new Message(peer.getUsername(), text);

        peer.getHistory().add(message);
        
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
            e.printStackTrace();
        }
        return readed_message;
    }

    public String read() {

        byte[] readed_message = readByte();

        // пришёл массив из байт

        EncryptedMessage encryptedMessage4 = EncryptedMessage.fromBytes(readed_message);

        byte[] Byte_FromCryptMessage = cipher_sex.decrypt(encryptedMessage4);

        Byte_FromCryptMessage.toString(); // принтануть, это и есть сообщение

        String text = new String(Byte_FromCryptMessage, StandardCharsets.UTF_8); // В UTF=8
        return text;
    }

    public void handShake(String Username) {

        DH dh = new DH(Constants.generator, Constants.module);
        byte[] keyBytes = dh.getPublicKey().toByteArray();
        writeBytes(keyBytes); // write

        byte[] readed_message = readByte();
        BigInteger readedBigInteger = new BigInteger(readed_message);
        dh.generateSharedSecret(readedBigInteger); // read

        // -- Обменялись ключами.

        HKDF hkdf = new HKDF(dh.getSharedSecret());
        Key encKey = hkdf.getKey("encryption", 16);
        Key macKey = hkdf.getKey("MAC", 16);
        cipher_sex = new AES128_CTR_MAC(encKey, macKey);

        try {
            send("|UsErNaMe|" + Username);
        } catch (IOException e) {
            System.out.println("Ошибка при передачи Username который: " + Username);
            e.printStackTrace();
        }
        String input;
        // try {
        input = read();
        if (input.contains("|UsErNaMe|")) {
            input = input.replace("|UsErNaMe|", "");
            peer.setUsername(input);
            System.out.println("Username собеседника: " + peer.getUsername());
        }
        // } catch (IOException e) {
        //     System.out.println("Ошибка при чтении Username");
        //     e.printStackTrace();
        // }
    }

    public void run() {
        new Thread(new Runnable() {
            public void run() {

                String text = "";
                // while (true) { // Непомню зачем он тут
                try {
                    while (true) {
                        text = read();
                        if (text == null) {
                            System.out.println("NULL HAVE NULL");
                            break;
                        }
                        if (text.equals("/exit")) {
                            System.out.println("Собеседник отключился.");
                            reader.close();
                            socket.close(); // ЭКСПЕРИМЕНТАЛЬНАЯ КОМАНДА
                            break;
                        }
                        Message message = new Message(peer.getUsername(), text);
                        if (Server.chatOpened) {
                            message.printMessage();
                        }
                        peer.getHistory().add(message);
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
        System.out.println("АЙПИ СОБЕСЕДНИКА " + socket.getInetAddress() + " и имя СЕБЕСЕДНИКА " + peer.getUsername());
        String text = "";

        Server.chatOpened = true;
        while (Server.chatOpened) {
            text = scanner.nextLine();
            Command command = commands.get(text);
            if (command == null) {
                Message message = null;
                try {
                    send(text);
                } catch (IOException e) {
                    System.err.println("Ошибка при отправке сообщения. Сохранено в истории.");
                    peer.getHistory().add(message);
                }
            } else {
                command.execute();
            }
        }
    }
}
