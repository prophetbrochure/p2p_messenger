package Network;

// ТЕСТОВАЯ ВЕРСИЯ, СО СВОИМИ импровизированными
// ВХОДНЫМИ ДАННЫМИ

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PeerHandler implements Runnable {
    private Socket socket;

    PeerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Начало чата.");
            InputStream input = socket.getInputStream();
            InputStreamReader iSReader = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(iSReader);

            String line = "";
            try {
                while (!line.equals("/exit")) {
                    System.out.println("line: " + line);
                    line = reader.readLine();
                    System.out.println("[" + socket.getLocalAddress() + "]: " + line);
                }
                reader.close();
                iSReader.close();
                input.close();
                socket.close();
                System.out.println("Чат закрывается.");
            } catch (IOException e1) {
                reader.close();
                iSReader.close();
                input.close();
                socket.close();
                System.out.println("Собеседник покинул чат.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
