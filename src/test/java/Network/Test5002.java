package Network;

import java.io.IOException;

public class Test5002 {
    Server server;
    public static void main(String[] args) {
        // Отдающая сторона.
        try {
            Server server = new Server(5002);
            
            server.start("User5002");

            String ip = "127.0.0.1";
            int port = 5000;

            server.connect(ip, port, "User5002");
            
        } catch (IOException e) {
            System.err.println("ошибка порт занят");
        }
    }
}
