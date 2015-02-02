package utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class BlockChecker {
	public abstract boolean isValid(Block block);
	
	public static BlockChecker checkFor(final Material mat) {
		return new BlockChecker(){

			@Override
			public boolean isValid(Block block) {
				return block.getType()==mat;
			}
			@Override
			public String toString() {
				return MatUtils.prettyPrint(mat);
			}};
	}
	public static BlockChecker checkForExactLocation(final Block checkedFor) {
		return new BlockChecker(){
			
			@Override
			public boolean isValid(Block block) {
				return block.getLocation().equals(checkedFor.getLocation());
			}
			@Override
			public String toString() {
				return "("+checkedFor.getX()+", "+checkedFor.getY()+", "+checkedFor.getZ()+")";
			}};
	}
	public static BlockChecker checkForExactLocation(final Location checkedFor) {
		return new BlockChecker(){
			
			@Override
			public boolean isValid(Block block) {
				return block.getWorld().getBlockAt(checkedFor).equals(block);
			}
			@Override
			public String toString() {
				return "("+checkedFor.getX()+", "+checkedFor.getY()+", "+checkedFor.getZ()+")";
			}};
	}
}
