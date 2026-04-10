package p2pmessenger.network;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Test5002 {
    Server server;
    public static void main(String[] args) {
        // Отдающая сторона.
        try {
            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            Server server = new Server(5002, scanner);
            
            server.start("User5002");

            String ip = "127.0.0.1";
            int port = 5000;

            server.connect(ip, port, "User5002", true);
            
        } catch (IOException e) {
            System.err.println("ошибка порт занят");
        }
    }
}
