package characters;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import utils.BlockChecker;

public class SugarCaneFarmer extends AbstractFarmer {

	public static final String traitName = "sugarCaneFarmer";
	@Override
	String getTraitName() {
		return traitName;
	}


	public SugarCaneFarmer() {
		super(traitName);
	}
	public CharacterType getCharacterType() { return CharacterType.SUGAR_CANE_FARMER; }
	

	SugarCaneChecker sugarCaneChecker = new SugarCaneChecker();
	@Override
	BlockChecker getJobBlockChecker() {
		return sugarCaneChecker;
	}
	public class SugarCaneChecker extends BlockChecker {
		
		@Override
		public boolean isValid(Block block) {
			Block sugarCaneBlock = block.getRelative(BlockFace.UP);
			return sugarCaneBlock.getType() == Material.SUGAR_CANE_BLOCK 
					&& sugarCaneBlock.getRelative(BlockFace.UP).getType() == Material.SUGAR_CANE_BLOCK
					&& !selectedBlocks.contains(sugarCaneBlock);
		}
		
	}

	

	void harvest() {
		
		short nSugarCaneBlocks = 0;
		LinkedList<Block> sugarCaneBlocks = new LinkedList<Block>(); 
		for (	Block sugarCaneBlock = selectedBlock.getRelative(BlockFace.UP); 
				sugarCaneBlock.getType() == Material.SUGAR_CANE_BLOCK; 
				sugarCaneBlock = sugarCaneBlock.getRelative(BlockFace.UP))
		{
			nSugarCaneBlocks++;
			sugarCaneBlocks.addFirst(sugarCaneBlock);
		}
		for (Block sugarCaneBlock : sugarCaneBlocks)
			sugarCaneBlock.setType(Material.AIR); // in reversed order!
		
		collectRemaining -= sugarCaneBlocks.size();
		
		selectedBlocks.remove(selectedBlock);
		selectedBlock = null;
		
		if(selectedProduction != null)
		{
			for (ItemStack produced : selectedProduction.getItemsProduced())
			{
				if (produced.getType() == Material.SUGAR_CANE)
				{
					InventoryTraitUtils.add(inventory, new ItemStack(Material.SUGAR_CANE, nSugarCaneBlocks));
					homeBuilding.city.statistics.produce(homeBuilding, Material.SUGAR_CANE, nSugarCaneBlocks);
				} else 
				{
					InventoryTraitUtils.add(inventory, produced);
					// register production:
					homeBuilding.city.statistics.produce(homeBuilding, produced.getType(), produced.getAmount());
				}
			}
		}
	}




}
