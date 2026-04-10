package p2pmessenger.network;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Test5001 {
    Server server;
    public static void main(String[] args) {
        // Принимающая сторона.
        try {
            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            Server server = new Server(5001, scanner);
            
            server.start("User5001");

        } catch (IOException e) {
            System.err.println("ошибка порт занят");
        }
    }
}
