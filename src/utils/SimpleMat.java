package utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class SimpleMat implements Mat {

	final Material mat;
	
	public SimpleMat(Material mat2) {
		mat = mat2;
	}
	
	@Override
	public Material getRepresentativeMaterial() {
		return mat;
	}

	@Override
	public boolean is(Block block) {
		return block.getType() == mat;
	}

	@Override
	public boolean is(ItemStack itemstack) {
		return itemstack.getType() == mat;
	}

	@Override
	public void setRepresentative(Block block) {
		block.setType(mat);
	}

	@Override
	public void setRepresentative(ItemStack itemStack) {
		itemStack.setType(mat);
	}

	@Override
	public boolean transfer(ItemStack itemStack, Block block) {
		if (itemStack.getType() != mat)
			return false;
		block.setType(itemStack.getType());
		return true;
	}

	@Override
	public boolean transfer(Block block, ItemStack itemStack) {
		if (block.getType() != mat)
			return false;
		itemStack.setType(block.getType());
		return true;
	}

	@Override
	public boolean transferState(ItemStack itemStack, Block block) {
		return true;
	}

	@Override
	public boolean transferState(Block block, ItemStack itemStack) {
		return true;
	}

}
