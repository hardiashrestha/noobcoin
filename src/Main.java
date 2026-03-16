public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("\n  Booting NoobCoin...");

        Blockchain chain = new Blockchain();

        // Seed wallets so new visitors aren't empty
        Wallet.createWallet("Alice", 1000);
        Wallet.createWallet("Bob", 500);
        Wallet.createWallet("Charlie", 750);

        NoobCoinServer server = new NoobCoinServer(chain);
        server.start(8080);
    }
}
