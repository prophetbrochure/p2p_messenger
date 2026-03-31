package Network;

import java.util.UUID;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String id;
    private String sender;
    private String text;
    private String sentTime;

    Message(String Username, String text) {
        id = UUID.randomUUID().toString();
        sender = Username;
        this.text = text;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        sentTime = now.format(formatter);
    }

    public String messageToString() {
        return "[" + sentTime + " | " + sender + "] " + text;
    }
    
    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getSentTime() {
        return sentTime;
    }
}
