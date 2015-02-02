package characters;

import java.util.HashSet;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import utils.BlockChecker;
import utils.WalkingGroundFinder;
import characters.Navigator.GoAndDo;

public abstract class AbstractFarmer extends ChestCharacter {

	//TODO config.yml shite:
	private final int numberOfCollectsPerBatch = plugin.getConfig().getInt("character."+getTraitName()+".numberOfCollectsPerBatch");
//	private static final int amountBreadConsumed = 1;
	private static final int jobRange = 1000;

	Block selectedBlock = null;
	static HashSet<Block> selectedBlocks = new HashSet<Block>();
	protected int collectRemaining;


	public AbstractFarmer(String traitName) {
		super(traitName);
	}
	

//	@Override
//	public ItemStack[] getItemsNeededToConsume() {
//		return new ItemStack[]{new ItemStack(Material.BREAD, amountBreadConsumed)}; 
//	}

	abstract BlockChecker getJobBlockChecker();
	
	@Override
	public Location findJob() {
		return findClosestWheatBlock(true, jobRange);
	}

	
	private Location findClosestWheatBlock(boolean fromHome, int maxBlocksChecked) {
		Location fromLocation = (fromHome)? getHomeLocation() : this.getNPC().getEntity().getLocation();
//		if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
//			WalkingGroundFinder.closestBlockOnWalkableGround(fromLocation, maxBlocksChecked, getJobBlockChecker(), playersToShowSearchSpace, false, WalkingGroundFinder.walkEverywhere);
		
		WalkingGroundFinder.WalkingGroundFinderResult jobFindResult = WalkingGroundFinder.closestBlockOnWalkableGround(
				fromLocation, maxBlocksChecked, getJobBlockChecker(), playersToShowSearchSpace, WalkingGroundFinder.walkEverywhere); //Get closest tree within the first 1000 checked blocks
		if(jobFindResult != null)
		{
			Block jobBlockGround = jobFindResult.block;
			selectedBlock = jobBlockGround.getRelative(BlockFace.UP, 1);
			Location nextLocation = selectedBlock.getLocation().add(.5, 0, .5);
			selectedBlocks.add(selectedBlock);
			jobFindResult.wayPoints.add(GoAndDo.newNoAction(nextLocation));
			
			if (fromHome)
			{
				this.homeToJobNavigator = new Navigator(jobFindResult.wayPoints);

			}
			else
			{
				homeToJobNavigator.wayPoints.add(GoAndDo.newNoAction(nextLocation)); // these are only used on the way back!
			}
			
			return selectedBlock.getLocation().add(.5, 0, .5);
		}
		else
		{
			if(Debug.showCouldntFindJobDebug)
				Debug.out(npc.getName() + " from " + homeBuilding.city.getFaction().getName() + " couldn't find fully grown crops!");
			return null;
		}
	}

	@Override
	Action doJob() {
		consume();
		collectRemaining = numberOfCollectsPerBatch;
		return collect(true);
	}

	abstract void harvest();

	
	Action collect(boolean firstArrival) {
		harvest();
		final Location wheatLoc;
		

		if (collectRemaining == 0 || 
				(wheatLoc = findClosestWheatBlock(false, jobRange)) == null) 
		{ // when we have found another crop, we are not yet done!
			return getJobToHomeNavigatorAction();
		}
		else 
		{
			return new Action(){ // collect wheat action

				@Override
				public Location getLocation() {
					return wheatLoc;
				}

				@Override
				public Action doAction() {
					return collect(false);
				}

				@Override
				public long getWaitingTime() {
					return getJobWaitingTime();
				}

				@Override
				public Action cantNavigate() {
					teleport(getHomeLocation());
					return doHomeMoveThrough;
				}
				@Override
				public ActionType getActionType() {
					return ActionType.JOB;
				}
				}; 
		}
	}
	@Override
	protected void cancelCurrentJob() {
		selectedBlocks.remove(selectedBlock);
		selectedBlock = null;
	}



}
