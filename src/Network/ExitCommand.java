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
        System.out.println("Чат закрывается.");
        peer.setActive(false);
        try {
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
