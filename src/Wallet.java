import java.util.*;

public class Wallet {
    public String name;
    public static Map<String, Float> ledger = new LinkedHashMap<>();

    public Wallet(String name, float initialBalance) {
        this.name = name;
        ledger.put(name, initialBalance);
    }

    // Register a new user wallet from the website
    public static boolean createWallet(String name, float initialBalance) {
        if (ledger.containsKey(name)) return false; // already exists
        ledger.put(name, initialBalance);
        return true;
    }

    public static float getBalance(String name) {
        return ledger.getOrDefault(name, -1f);
    }

    public static boolean sendFunds(String sender, String receiver, float amount,
                                     Blockchain chain) {
        float senderBal = getBalance(sender);
        float receiverBal = getBalance(receiver);
        if (senderBal < amount || senderBal == -1f || receiverBal == -1f) return false;

        ledger.put(sender, senderBal - amount);
        ledger.put(receiver, receiverBal + amount);

        Transaction tx = new Transaction(sender, receiver, amount);
        Block block = new Block(chain.getLatestBlock().hash);
        block.addTransaction(tx);
        chain.addBlock(block);
        return true;
    }

    public static String allWalletsJson() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry<String, Float> e : ledger.entrySet()) {
            if (!first) sb.append(",");
            sb.append(String.format("{\"name\":\"%s\",\"balance\":%.2f}", e.getKey(), e.getValue()));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
