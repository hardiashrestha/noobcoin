public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║     💰  N O O B C O I N  💰      ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("\n  Booting blockchain...\n");

        Blockchain chain = new Blockchain();

        // Seed wallets
        Wallet.createWallet("Shrestha",   10000);
        Wallet.createWallet("Bob",      7500);
        Wallet.createWallet("Kushaal",  7000);

        // Use PORT env var for Render, fallback to 8080 locally
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null) ? Integer.parseInt(portEnv) : 8080;

        NoobCoinServer server = new NoobCoinServer(chain);
        server.start(port);
    }
}
