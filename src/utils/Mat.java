package utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * @author TK
 * An abstraction of Material..
 * One Mat can be multiple Materials (ores!)
 * One Material can be multiple Mats (dyes!)
 */
public interface Mat {

	
	
	/**
	 * @return any material with which this Mat is associated
	 */
	public abstract Material getRepresentativeMaterial();
	
	public abstract boolean is(Block block); 
	public abstract boolean is(ItemStack itemstack); 
	
	
	/**
	 * set the block to the representative material / state
	 * @param block
	 */
	public abstract void setRepresentative(Block block);
	/**
	 * set the item stack to the representative material / state
	 * @param block
	 */
	public abstract void setRepresentative(ItemStack itemStack);
	
	/**
	 * transfer the specific material  of the item stack to the block 
	 * @param itemStack
	 * @param block
	 * @return whether we could transfer the state
	 */
	public abstract boolean transfer(ItemStack itemStack, Block block);
	/**
	 * transfer the specific material of the block to the item stack 
	 * @param itemStack
	 * @param block
	 * @return whether we could transfer the state
	 */
	public abstract boolean transfer(Block block, ItemStack itemStack);
	
	/**
	 * transfer the specific material state of the item stack to the block 
	 * @param itemStack
	 * @param block
	 * @return whether we could transfer the state
	 */
	public abstract boolean transferState(ItemStack itemStack, Block block);
	/**
	 * transfer the specific material state of the block to the item stack 
	 * @param itemStack
	 * @param block
	 * @return whether we could transfer the state
	 */
	public abstract boolean transferState(Block block, ItemStack itemStack);
	
}
