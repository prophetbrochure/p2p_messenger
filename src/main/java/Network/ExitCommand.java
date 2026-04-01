package Network;

import java.io.IOException;
import java.net.Socket;

public class ExitCommand implements Command {
    private final Socket socket;
    private Peer peer;

    public ExitCommand(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
    }

    @Override
    public void execute() {
        Server.chatOpened = false;

        for (int i = 0; i < Server.peersList.size(); i++) {
            if (Server.peersList.get(i).getPeer() == peer) {
                Server.peersList.remove(i);
                break;
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
