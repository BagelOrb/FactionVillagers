package utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import utils.MatUtils.MatType;

public class SetMat implements Mat {

	
	final MatType matType;
	final Material[] mats;
	
	public SetMat(Material[] mats2, MatType matType2) { mats = mats2; matType = matType2; }
	
	@Override
	public Material getRepresentativeMaterial() {
		return mats[0];
	}

	@Override
	public boolean is(Block block) {
		for (Material mat : mats)
			if (block.getType() == mat)
				return true;
		return false;
	}

	@Override
	public boolean is(ItemStack itemstack) {
		for (Material mat : mats)
			if (itemstack.getType() == mat)
				return true;
		return false;
	}

	@Override
	public void setRepresentative(Block block) {
		block.setType(mats[0]);
	}

	@Override
	public void setRepresentative(ItemStack itemStack) {
		itemStack.setType(mats[0]);
	}

	@Override
	public boolean transfer(ItemStack itemStack, Block block) {
		if (! Arrays.asList(mats).contains(itemStack.getType()))
			return false;
		block.setType(itemStack.getType());
		return true;
	}

	@Override
	public boolean transfer(Block block, ItemStack itemStack) {
		if (! Arrays.asList(mats).contains(block.getType()))
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
