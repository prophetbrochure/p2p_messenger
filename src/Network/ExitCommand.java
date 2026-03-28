package Network;

import java.io.IOException;
import java.net.Socket;

public class ExitCommand implements Command {
    private final Socket socket;
    private final Peer peer;

    public ExitCommand(Peer peer, Socket socket) {
        this.socket = socket;
        this.peer = peer;
    }

    @Override
    public void execute() {
        System.out.println("Чат закрывается.");
        peer.setConnected(false);
        try {
            socket.close();    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
