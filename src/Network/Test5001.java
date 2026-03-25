package Network;

import java.io.IOException;

public class Test5001 {
    Server server;
    public static void main(String[] args) {
        // Принимающая сторона. покачто Может только читать написаное собеседником.
        try {
            Server server = new Server(5001);
            
            server.start();

            // String ip = "127.0.0.1";
            // int port = 5001;

            // server.connect(ip, port);
        } catch (IOException e) {
            // e.printStackTrace();
            System.err.println("ошибка порт занят");
        }
    }
}
