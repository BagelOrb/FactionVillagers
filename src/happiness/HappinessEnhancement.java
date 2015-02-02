package happiness;

import java.util.HashSet;

import org.bukkit.block.Block;

public interface HappinessEnhancement {

	public double enhancement(HashSet<Block> borderBlocks);
}
