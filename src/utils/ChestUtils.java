package utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class ChestUtils {

	@SuppressWarnings("deprecation")
	public static BlockFace getDirection(Block chest) {
		switch (chest.getData()) {
		case 2: return BlockFace.NORTH;
		case 3: return BlockFace.SOUTH;
		case 4: return BlockFace.WEST;
		case 5: return BlockFace.EAST;
		default: return BlockFace.NORTH;
		}
	}
	@SuppressWarnings("deprecation")
	public static void setDirection(Block chest, BlockFace direction) {
		switch (direction) {
		case NORTH: chest.setData((byte)2); return;
		case SOUTH: chest.setData((byte)3); return;
		case WEST: chest.setData((byte)4); return;
		case EAST: chest.setData((byte)5); return;
		default: chest.setData((byte)2); return;
		}
	}
	
	
}
