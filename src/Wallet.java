import java.util.*;

public class Wallet {
    private static Map<String, Float> ledger = new HashMap<>();
    // mockCoinLedger: walletName -> coinSymbol -> amount
    private static Map<String, Map<String, Double>> mockLedger = new HashMap<>();

    public static boolean createWallet(String name, float initialBalance) {
        if (ledger.containsKey(name)) return false;
        ledger.put(name, initialBalance);
        mockLedger.put(name, new HashMap<>());
        return true;
    }

    public static float getBalance(String name) {
        return ledger.getOrDefault(name, -1f);
    }

    public static double getMockBalance(String wallet, String coin) {
        if (!mockLedger.containsKey(wallet)) return 0;
        return mockLedger.get(wallet).getOrDefault(coin, 0.0);
    }

    public static boolean sendFunds(String sender, String receiver, float amount,
                                    Blockchain chain) {
        float senderBal   = getBalance(sender);
        float receiverBal = getBalance(receiver);
        if (senderBal < amount || senderBal == -1f || receiverBal == -1f) return false;

        ledger.put(sender,   senderBal - amount);
        ledger.put(receiver, receiverBal + amount);

        Transaction tx = new Transaction(sender, receiver, amount);
        Block block = new Block(chain.getLatestBlock().hash);
        block.addTransaction(tx);
        chain.addBlock(block);
        return true;
    }

    // Send NBC but receiver gets target coin equivalent
    public static boolean sendFundsAsCoin(String sender, String receiver, float nbcAmount,
                                          String targetCoin, Blockchain chain) {
        float senderBal   = getBalance(sender);
        float receiverBal = getBalance(receiver);
        if (senderBal < nbcAmount || senderBal == -1f || receiverBal == -1f) return false;

        double coinAmount = CoinConverter.convertFromNBC(nbcAmount, targetCoin.toUpperCase());
        if (coinAmount < 0) return false;

        // Deduct NBC from sender
        ledger.put(sender, senderBal - nbcAmount);

        // Credit mock coin to receiver
        Map<String, Double> receiverMock = mockLedger.getOrDefault(receiver, new HashMap<>());
        double existing = receiverMock.getOrDefault(targetCoin.toUpperCase(), 0.0);
        receiverMock.put(targetCoin.toUpperCase(), existing + coinAmount);
        mockLedger.put(receiver, receiverMock);

        Transaction tx = new Transaction(sender, receiver, nbcAmount);
        Block block = new Block(chain.getLatestBlock().hash);
        block.addTransaction(tx);
        chain.addBlock(block);
        return true;
    }

    // Convert user's NBC to mock coin (self conversion)
    public static boolean convertNBCtoMock(String wallet, float nbcAmount, String targetCoin, Blockchain chain) {
        return sendFundsAsCoin(wallet, wallet, nbcAmount, targetCoin, chain);
    }

    public static String allWalletsJson() {
        StringBuilder sb = new StringBuilder("[");
        for (Map.Entry<String, Float> e : ledger.entrySet()) {
            String name = e.getKey();
            sb.append("{\"name\":\"").append(name).append("\",")
              .append("\"balance\":").append(e.getValue()).append(",")
              .append("\"mockCoins\":{");
            Map<String, Double> coins = mockLedger.getOrDefault(name, new HashMap<>());
            boolean first = true;
            for (Map.Entry<String, Double> c : coins.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(c.getKey()).append("\":").append(c.getValue());
                first = false;
            }
            sb.append("}},");
        }
        if (sb.length() > 1 && sb.charAt(sb.length()-1) == ',') sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }
}
