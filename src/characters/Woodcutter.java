package characters;

import java.util.HashSet;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;

import utils.BlockChecker;
import utils.BlockUtils;
import utils.WalkingGroundFinder;

public class Woodcutter extends ChestCharacter {

	public static HashSet<Block> selectedWoodBlocks = new HashSet<Block>(); // all selected trees by all woodcutters!
	
	public static final String traitName = "woodcutter";
	
	protected static final long waitingTimePerTreeBlock = plugin.getConfig().getLong("character."+traitName+".waitingTimePerTreeBlock");
	
	public Location treeCutLocation;
	
	public HashSet<Block> selectedTree = new HashSet<Block>();

	public Woodcutter() {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	
	public CharacterType getCharacterType() { return CharacterType.WOODCUTTER; }
	
	@Override String getTraitName() {
		return traitName;
	}
	
	private static class TreeTopChecker extends BlockChecker {
		public boolean isValid(Block block) {
			block = block.getRelative(BlockFace.UP, 2);
			if(BlockUtils.isLogType(block.getType()))
			{
				HashSet<Block> blocks = utils.BlockUtils.getAllConnectedBlocksOfSameType(block, 1000);
				Block highestBlock = utils.BlockUtils.getHighestBlock(blocks);
				Block idBlock = utils.BlockUtils.getConsistentLowestBlock(blocks);
				if(BlockUtils.isLeaveType(highestBlock.getRelative(BlockFace.UP).getType()))
				{
					//MaterialData blockData = idBlock.getState().getData();
					//if(blockData instanceof Tree)
					//{
						if(!selectedWoodBlocks.contains(idBlock))
						{
							return true;
						}
					//}
				}
			}
			return false;
		}
	}

	@Override
	Location findJob() {
//		if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
//			WalkingGroundFinder.closestBlockOnWalkableGround(getHomeLocation(), 4000, new TreeTopChecker(), playersToShowSearchSpace, false, WalkingGroundFinder.walkEverywhere);

		WalkingGroundFinder.WalkingGroundFinderResult treeFindResult = WalkingGroundFinder.closestBlockOnWalkableGround(
				getHomeLocation(), 4000, new TreeTopChecker(), playersToShowSearchSpace, WalkingGroundFinder.walkEverywhere); //Get closest tree within the first 4000 checked blocks
		if(treeFindResult != null)
		{
			Block treeBlockGround = treeFindResult.block; 
			Block treeBlock = treeBlockGround.getRelative(BlockFace.UP, 2);
			selectedTree = utils.BlockUtils.getAllConnectedBlocksOfSameType(treeBlock, 1000);
			selectedWoodBlocks.addAll(selectedTree);
			Location nextLocation = treeBlockGround.getRelative(treeFindResult.fromDirection).getLocation().add(.5, 1, .5);
			this.homeToJobNavigator =  new Navigator(treeFindResult.wayPoints); 
			this.treeCutLocation = nextLocation;
			return nextLocation;
		}
		else
		{
			if(Debug.showCouldntFindJobDebug)
				Debug.out(npc.getName() + " from " + homeBuilding.city.getFaction().getName() + " couldn't find a tree!");
			return null;
		}
	}	
	
	@Override
	Action doJob()
	{
		boolean couldConsume = consume();
		if (!couldConsume)
		{
			cancelCurrentJob();
			return getJobToHomeNavigatorAction();
		}
		
		if(selectedProduction != null)
		{
			for (ItemStack produced : selectedProduction.getItemsProduced())
			{
				if (produced.getType() != Material.LOG)
				{
					homeBuilding.city.statistics.produce(homeBuilding, produced.getType(), produced.getAmount());
					InventoryTraitUtils.add(inventory, produced);
				}
			}
		}
		
		return doTreeBlock(selectedTree);
	}
	
	Action doTreeBlock(HashSet<Block> treeBlocks)
	{
		if(treeBlocks.size() > 0)
		{
			final Block treeBlock = BlockUtils.getConsistentLowestBlock(treeBlocks);
			npc.faceLocation(treeBlock.getLocation());
			treeBlocks.remove(treeBlock);
			final HashSet<Block> newTreeBlocks = treeBlocks;

			return new Action()
			{
				@Override
				public Location getLocation() {
					return treeCutLocation;
				}
	
				@SuppressWarnings("deprecation")
				@Override
				public Action doAction() {
					
					for(ItemStack drop : treeBlock.getDrops())
					{
						InventoryTraitUtils.add(inventory, drop);
						homeBuilding.city.statistics.produce(homeBuilding, drop.getType(), drop.getAmount());
					}
					
					if(blockIsOnDirt(treeBlock))
					{
						MaterialData data = treeBlock.getState().getData();
						
						if(data instanceof Tree)
						{
							TreeSpecies species = ((Tree) data).getSpecies();
							treeBlock.setTypeIdAndData(Material.SAPLING.getId(), species.getData(), true);
						}
						else if(treeBlock.getType() == Material.LOG_2)// Acacia & Dark oak
						{
							treeBlock.setTypeIdAndData(Material.SAPLING.getId(), (byte) (treeBlock.getData() + 4), true);
						}
					}
					else
					{
						treeBlock.setType(Material.AIR);
					}
					
					selectedWoodBlocks.remove(treeBlock);
					
					return doTreeBlock(newTreeBlocks);
				}
	
				@Override
				public long getWaitingTime() {
					return waitingTimePerTreeBlock;
				}
	
				@Override
				public Action cantNavigate() {
					cancelCurrentJob();
					return getJobToHomeNavigatorAction();
				}
	
				@Override
				public ActionType getActionType() {
					return ActionType.JOB;
				}
			};
		}
		else
		{
			selectedWoodBlocks.removeAll(selectedTree);
			selectedTree.clear();
			return getJobToHomeNavigatorAction();
		}
	}
	
	
	protected boolean blockIsOnDirt(Block treeBlock) {
		return (treeBlock.getRelative(BlockFace.DOWN).getType() == Material.DIRT || treeBlock.getRelative(BlockFace.DOWN).getType() == Material.GRASS);
	}

	@Override
	protected void cancelCurrentJob() {
		selectedWoodBlocks.removeAll(selectedTree);
		//selectedTree = null;
		selectedTree.clear();
	}
}
