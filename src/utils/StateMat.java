package utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import utils.MatUtils.MatType;

public class StateMat  implements Mat {

	final MatType matType;
	final Material mat;
	final byte durability;
	
	public StateMat(Material mat2, byte durability2, MatType matType2) { mat = mat2; durability = durability2; matType = matType2; }
	
	@Override
	public Material getRepresentativeMaterial() {
		return mat;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean is(Block block) {
		return block.getType() == mat && block.getData() == durability;
	}

	@Override
	public boolean is(ItemStack itemstack) {
		return itemstack.getType() == mat && itemstack.getDurability() == durability;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setRepresentative(Block block) {
		block.setType(mat);
		block.setData(durability);
	}

	@Override
	public void setRepresentative(ItemStack itemStack) {
		itemStack.setType(mat);
		itemStack.setDurability(durability);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean transfer(ItemStack itemStack, Block block) {
		if (itemStack.getType() != mat || itemStack.getDurability() != durability)
			return false;
		block.setType(mat);
		block.setData(durability);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean transfer(Block block, ItemStack itemStack) {
		if (block.getType() != mat || block.getData() != durability)
			return false;
		itemStack.setType(mat);
		itemStack.setDurability(durability);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean transferState(ItemStack itemStack, Block block) {
		if (itemStack.getType() != mat || itemStack.getDurability() != durability)
			return false;
		block.setData(durability);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean transferState(Block block, ItemStack itemStack) {
		if (block.getType() != mat || block.getData() != durability)
			return false;
		itemStack.setDurability(durability);
		return true;
	}

}
