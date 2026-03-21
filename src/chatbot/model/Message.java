package chatbot.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {

    public enum Sender { USER, BOT }

    private final String text;
    private final Sender sender;
    private final String timestamp;

    public Message(String text, Sender sender) {
        this.text = text;
        this.sender = sender;
        this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getText()      { return text; }
    public Sender getSender()    { return sender; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        String label = sender == Sender.USER ? "You" : "ORYN";
        return "[" + timestamp + "] " + label + ": " + text;
    }
}
