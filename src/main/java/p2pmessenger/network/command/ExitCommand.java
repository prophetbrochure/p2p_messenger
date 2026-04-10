package p2pmessenger.network.command;

import java.io.IOException;
import java.net.Socket;

import p2pmessenger.network.Server;
import p2pmessenger.network.model.Peer;

public class ExitCommand implements Command {
    private final Server server;
    private final Socket socket;
    private Peer peer;

    public ExitCommand(Server server, Socket socket, Peer peer) {
        this.server = server;
        this.socket = socket;
        this.peer = peer;
    }

    @Override
    public void execute() {
        server.setChatOpened(false);

        for (int i = 0; i < server.getPeersList().size(); i++) {
            if (server.getPeersList().get(i).getPeer() == peer) {
                server.getPeersList().remove(i);
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
