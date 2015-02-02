package characters;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Crops;

import utils.BlockChecker;

public class WheatFarmer extends AbstractFarmer {

	public static final String traitName = "wheatFarmer";
	@Override
	String getTraitName() {
		return traitName;
	}


	public WheatFarmer() {
		super(traitName);
	}
	public CharacterType getCharacterType() { return CharacterType.WHEAT_FARMER; }
	

	WheatChecker wheatChecker = new WheatChecker();
	public class WheatChecker extends BlockChecker {
		
		@Override
		public boolean isValid(Block block) {
			Block wheatBlock = block.getRelative(BlockFace.UP);
			return wheatBlock.getType() == Material.CROPS 
					&& !selectedBlocks.contains(wheatBlock)
					&& ((Crops) wheatBlock.getState().getData()).getState() == CropState.RIPE;
		}
		
	}

	

	@SuppressWarnings("deprecation")
	void harvest() {
//		((Crops) selectedWheat.getState().getData()).setState(CropState.GERMINATED);
//		selectedWheat.getState().update(true);
		selectedBlock.setData((byte) 1);
		produce();
		selectedBlocks.remove(selectedBlock);
		selectedBlock = null;
		
		collectRemaining--;
	}
	@Override
	BlockChecker getJobBlockChecker() {
		return wheatChecker;
	}



}
