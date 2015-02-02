package utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * A block prototype without coords!
 * 
 * @author TK
 *
 */
public class BlockMatState {

	public final Material mat;
	public final byte data;
	
	public BlockMatState (Material mat2, byte data2) {
		this.mat = mat2;
		this.data = data2;
	}
	public BlockMatState (Material mat2, int data2) {
		this.mat = mat2;
		this.data = (byte) data2;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isMetBy(Block block){
		if (block.getType() != mat) return false;
		if (block.getData() != data) return false;
		return true; // otherwise
	}
	
	@SuppressWarnings("deprecation")
	public void setBlock(Block block) {
		block.setType(mat);
		if (data != -1)
			block.setData(data);
	}
}
