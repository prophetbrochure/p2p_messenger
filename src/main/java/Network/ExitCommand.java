package Network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ExitCommand implements Command {
    private final Socket socket;
    private DataOutputStream writer;

    public ExitCommand(Socket socket, DataOutputStream writer) {
        this.socket = socket;
        this.writer = writer;
    }

    @Override
    public void execute() {
        System.out.println("Отключение собеседника.");
        Server.chatOpened = false;
        try {
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
