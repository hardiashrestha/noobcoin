import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public String sender;
    public String receiver;
    public float amount;
    public static int count = 0;
    public int id;
    public String timestamp;

    public Transaction(String sender, String receiver, float amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.id = ++count;
        this.timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"sender\":\"%s\",\"receiver\":\"%s\",\"amount\":%.2f,\"time\":\"%s\"}",
            id, sender, receiver, amount, timestamp
        );
    }

    @Override
    public String toString() {
        return "[TX#" + id + " | " + sender + " → " + receiver + " : " + amount + " NBC]";
    }
}
