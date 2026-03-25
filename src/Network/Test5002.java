package Network;

import java.io.IOException;

public class Test5002 {
    Server server;
    public static void main(String[] args) {
        // Отдающая сторона. покачто Может только писать собеседнику.
        try {
            Server server = new Server(5002);
            
            server.start();

            String ip = "127.0.0.1";
            int port = 5001;

            server.connect(ip, port);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
