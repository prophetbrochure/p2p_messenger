package Network;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedWriter;

public class ExitCommand implements Command {
    private final Socket socket;
    private final Peer peer;
    private BufferedWriter writer;

    public ExitCommand(Peer peer, Socket socket, BufferedWriter writer) {
        this.socket = socket;
        this.peer = peer;
        this.writer = writer;
    }

    @Override
    public void execute() {
        System.out.println("Отключение собеседника.");

        try {
            // Закрытие потока и сокета вызовет исключение в потоке чтения PeerHandler,
            // что в свою очередь запустит цепочку closeConnection() и обновит UI.
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
