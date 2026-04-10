package p2pmessenger.network.command;

import p2pmessenger.network.Server;

public class BackCommand implements Command {
    private Server server;

    public BackCommand(Server server) {
        this.server = server;
    }
    @Override
    public void execute() {
        server.setChatOpened(false);
    }
}
