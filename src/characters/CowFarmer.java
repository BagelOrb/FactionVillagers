package characters;

import java.util.HashSet;
import java.util.List;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import utils.BlockChecker;
import utils.EntityChecker;
import utils.EntityUtils;
import utils.WalkingGroundFinder;
import characters.Navigator.GoAndDo;

public class CowFarmer extends ChestCharacter {

	public static final String traitName = "cowFarmer";
	@Override
	String getTraitName() {
		return traitName;
	}
	//TODO config.yml shite:
	private static final int numberOfCollectsPerBatch = plugin.getConfig().getInt("character."+traitName+".numberOfCollectsPerBatch");
	private static final int jobRadius = 32;
	private static final int subJobRadius = 16;
	private static final int jobRange = 1000;
	private static final int subJobRange = 500;
	
	private Cow selectedCow = null;
	private static HashSet<Cow> selectedCows = new HashSet<Cow>();
	private int collectRemaining;


	public CowFarmer() {
		super(traitName);
	}
	public CharacterType getCharacterType() { return CharacterType.COW_FARMER; }
	

	public static class CowChecker extends EntityChecker {
		@Override
		public boolean isValid(Entity entity) {
			if(entity.getType() == EntityType.COW && entity instanceof Cow)
			{
				if(((Cow) entity).isAdult() && !selectedCows.contains(entity))
					return true;
			}
			return false;
		}
	}
	
	public class CowBlockChecker extends BlockChecker {
		
		List<Cow> nearbyCows;
		
		public CowBlockChecker(List<Cow> nearbyCows)
		{
			this.nearbyCows = nearbyCows;
		}
		
		@Override
		public boolean isValid(Block groundBlock) {
			Block blockAboveGround = groundBlock.getRelative(BlockFace.UP);
			for(Cow cow : nearbyCows)
			{
				if(blockAboveGround.getLocation().distanceSquared(cow.getLocation()) <= 1)
				{
					selectedCow = cow;
					return true;
				}
			}
			return false;
		}
		
	}
	
	@Override
	public Location findJob() {
		return findClosestCow(jobRadius, jobRange, true);
	}
	
	private Location findClosestCow(int radius, int maxBlocksChecked, boolean fromHome) {
		
		List<Cow> nearbyCows = EntityUtils.getCloseEntities(npc.getEntity(), new CowChecker(), jobRadius, Cow.class);

		Location fromLocation = this.getNPC().getEntity().getLocation();
//		if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
//			WalkingGroundFinder.closestBlockOnWalkableGround(fromLocation, maxBlocksChecked, new CowBlockChecker(nearbyCows), playersToShowSearchSpace, false, WalkingGroundFinder.walkEverywhere);
		
		if(!nearbyCows.isEmpty())
		{			
			WalkingGroundFinder.WalkingGroundFinderResult cowFindResult = WalkingGroundFinder.closestBlockOnWalkableGround(fromLocation, maxBlocksChecked, new CowBlockChecker(nearbyCows), playersToShowSearchSpace, WalkingGroundFinder.walkEverywhere);
			if(cowFindResult != null)
			{
				Location nextLocation = selectedCow.getLocation();
				selectedCows.add(selectedCow);
				cowFindResult.wayPoints.add(GoAndDo.newNoAction(nextLocation));
				
				if (fromHome)
				{
					homeToJobNavigator = new Navigator(cowFindResult.wayPoints);
				}
				else
				{
					homeToJobNavigator.wayPoints.add(GoAndDo.newNoAction(nextLocation)); // these are only used on the way back!
				}
				
				return selectedCow.getLocation();
			}
		}

		if(Debug.showCouldntFindJobDebug)
			Debug.out(npc.getName() + " from " + homeBuilding.city.getFaction().getName() + " couldn't find an adult cow!");
		return null;
	}

	@Override
	Action doJob() {
		consume();
		collectRemaining = numberOfCollectsPerBatch;
		return collectCow(true);
	}

	
	Action collectCow(boolean firstArrival) {
		if(selectedCow.isValid() && npc.getEntity().getLocation().distance(selectedCow.getLocation()) <= closeEnoughRange)
		{
			//Selected cow is alive and close enough
			selectedCow.setBaby();
			produce();
			collectRemaining--;
		}
		
		cancelCurrentJob();
		
		final Location cowLocation;
		
		if (collectRemaining == 0 || 
				(cowLocation = findClosestCow(subJobRadius, subJobRange, false)) == null) 
		{
			//No more cows to do
			this.jobToHomeNavigator = homeToJobNavigator.reversed();
			return getJobToHomeNavigatorAction();
		}
		else 
		{
			return new Action(){ // collect cows

				@Override
				public Location getLocation() {
					return cowLocation;
				}

				@Override
				public Action doAction() {
					return collectCow(false);
				}

				@Override
				public long getWaitingTime() {
					return getJobWaitingTime();
				}

				@Override
				public Action cantNavigate() {
					cancelCurrentJob();
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
		selectedCows.remove(selectedCow);
		selectedCow = null;
	}

}
