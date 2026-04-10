package p2pmessenger.network.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String sender;
    private String text;
    private String sentTime;

    public Message(String username, String text) {
        sender = username;
        this.text = text;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        sentTime = now.format(formatter);
    }

    public String messageToString() {
        return "[" + sentTime + " | " + sender + "] " + text;
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
