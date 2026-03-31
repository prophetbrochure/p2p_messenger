package Network;

public class HistoryCommand implements Command {
    private final Peer peer;

    public HistoryCommand(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void execute() {
        int i = 1;
        System.out.println("\n--------------- History ---------------");
        for (Message line : peer.getHistory()) {
            System.out.println(i++ + ") " + line.printMessage());
        }
    }
}
