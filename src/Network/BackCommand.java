package Network;

public class BackCommand implements Command {

    @Override
    public void execute() {
        Server.chatOpened = false;
    }
}
