import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    public List<Block> chain = new ArrayList<>();
    public int difficulty = 3; // 3 leading zeros = fast but still shows PoW

    public Blockchain() {
        // Genesis block
        Block genesis = new Block("0");
        genesis.mineBlock(difficulty);
        chain.add(genesis);
        System.out.println("  ✔ Genesis block created.\n");
    }

    public void addBlock(Block newBlock) {
        newBlock.previousHash = getLatestBlock().hash;
        System.out.println("  ⛏ Mining block...");
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.hash.equals(current.calculateHash())) return false;
            if (!current.previousHash.equals(previous.hash)) return false;
        }
        return true;
    }

    public void printChain() {
        System.out.println("\n══════════════ NOOBCOIN BLOCKCHAIN ══════════════");
        for (int i = 0; i < chain.size(); i++) {
            Block b = chain.get(i);
            System.out.println("  Block #" + i);
            System.out.println("  Hash       : " + b.hash.substring(0, 20) + "...");
            System.out.println("  Prev Hash  : " + b.previousHash.substring(0, Math.min(20, b.previousHash.length())) + "...");
            System.out.println("  Txns       : " + (b.transactions.isEmpty() ? "None" : b.transactions));
            System.out.println("  ──────────────────────────────────────────────");
        }
        System.out.println("  Chain Valid? → " + (isChainValid() ? "✔ YES" : "✘ NO — TAMPERED!"));
        System.out.println("══════════════════════════════════════════════════\n");
    }
}
