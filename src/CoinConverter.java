import java.util.HashMap;
import java.util.Map;

public class CoinConverter {
    // Hardcoded prices in USD (April 2026 approximate rates)
    // 1 NoobCoin = $1 USD (base unit)
    public static final Map<String, Double> COIN_PRICES = new HashMap<>();
    public static final Map<String, String> COIN_SYMBOLS = new HashMap<>();

    static {
        COIN_PRICES.put("BTC",  87000.0);
        COIN_PRICES.put("ETH",  1600.0);
        COIN_PRICES.put("SOL",  130.0);
        COIN_PRICES.put("BNB",  590.0);
        COIN_PRICES.put("XRP",  2.10);
        COIN_PRICES.put("DOGE", 0.16);
        COIN_PRICES.put("ADA",  0.35);
        COIN_PRICES.put("NBC",  1.0);  // NoobCoin itself

        COIN_SYMBOLS.put("BTC",  "Bitcoin");
        COIN_SYMBOLS.put("ETH",  "Ethereum");
        COIN_SYMBOLS.put("SOL",  "Solana");
        COIN_SYMBOLS.put("BNB",  "BNB");
        COIN_SYMBOLS.put("XRP",  "XRP");
        COIN_SYMBOLS.put("DOGE", "Dogecoin");
        COIN_SYMBOLS.put("ADA",  "Cardano");
        COIN_SYMBOLS.put("NBC",  "NoobCoin");
    }

    // Convert noobcoin amount to target coin amount
    public static double convertFromNBC(double nbcAmount, String targetCoin) {
        if (!COIN_PRICES.containsKey(targetCoin)) return -1;
        double usdValue = nbcAmount; // 1 NBC = $1
        return usdValue / COIN_PRICES.get(targetCoin);
    }

    // Convert any coin amount to NBC
    public static double convertToNBC(double amount, String fromCoin) {
        if (!COIN_PRICES.containsKey(fromCoin)) return -1;
        double usdValue = amount * COIN_PRICES.get(fromCoin);
        return usdValue; // 1 NBC = $1
    }

    public static boolean isValidCoin(String coin) {
        return COIN_PRICES.containsKey(coin.toUpperCase());
    }

    public static String getPricesJson() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Double> e : COIN_PRICES.entrySet()) {
            sb.append("\"").append(e.getKey()).append("\":{")
              .append("\"price\":").append(e.getValue()).append(",")
              .append("\"name\":\"").append(COIN_SYMBOLS.get(e.getKey())).append("\"")
              .append("},");
        }
        if (sb.charAt(sb.length()-1) == ',') sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }
}
