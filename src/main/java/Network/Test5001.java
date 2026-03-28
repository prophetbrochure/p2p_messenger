package Network;

import java.io.IOException;

public class Test5001 {
    Server server;
    public static void main(String[] args) {
        // Принимающая сторона.
        try {
            Server server = new Server(5001);
            
            server.start();

        } catch (IOException e) {
            System.err.println("ошибка порт занят");
        }
    }
}
