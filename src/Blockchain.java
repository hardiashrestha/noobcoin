import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    public List<Block> chain = new ArrayList<>();
    public int difficulty = 3;

    public Blockchain() {
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
            Block current  = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.hash.equals(current.calculateHash())) return false;
            if (!current.previousHash.equals(previous.hash))   return false;
        }
        return true;
    }
}
