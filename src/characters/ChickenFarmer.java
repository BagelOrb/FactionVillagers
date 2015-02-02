package characters;

import java.util.HashSet;
import java.util.List;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import utils.BlockChecker;
import utils.EntityChecker;
import utils.EntityUtils;
import utils.WalkingGroundFinder;
import characters.Navigator.GoAndDo;

public class ChickenFarmer extends ChestCharacter {

	public static final String traitName = "chickenFarmer";
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
	
	private Chicken selected = null;
	private static HashSet<Chicken> allSelecteds = new HashSet<Chicken>();
	private int collectRemaining;


	public ChickenFarmer() {
		super(traitName);
	}
	public CharacterType getCharacterType() { return CharacterType.CHICKEN_FARMER; }
	

	public static class ChickenChecker extends EntityChecker {
		@Override
		public boolean isValid(Entity entity) {
			if(entity.getType() == EntityType.CHICKEN && entity instanceof Chicken)
			{
				if(((Chicken) entity).isAdult() && !allSelecteds.contains(entity))
					return true;
			}
			return false;
		}
	}
	
	public class ChickenBlockChecker extends BlockChecker {
		
		List<Chicken> nearbyChickens;
		
		public ChickenBlockChecker(List<Chicken> nearbyChickens)
		{
			this.nearbyChickens = nearbyChickens;
		}
		
		@Override
		public boolean isValid(Block groundBlock) {
			Block blockAboveGround = groundBlock.getRelative(BlockFace.UP);
			for(Chicken chicken : nearbyChickens)
			{
				if(blockAboveGround.getLocation().distanceSquared(chicken.getLocation()) <= 1)
				{
					selected = chicken;
					return true;
				}
			}
			return false;
		}
		
	}
	
	@Override
	public Location findJob() {
		Location loc = findClosestChicken(jobRadius, jobRange, true);
		if (loc == null)
			Debug.out("couldn't find any chicken!");
		return loc;
	}
	
	private Location findClosestChicken(int radius, int maxBlocksChecked, boolean fromHome) {
		
		List<Chicken> nearbyChickens = EntityUtils.getCloseEntities(npc.getEntity(), new ChickenChecker(), jobRadius, Chicken.class);
		Debug.out(nearbyChickens.size()+" nearby chickens");
		
		
		Location fromLocation = this.getNPC().getEntity().getLocation();
//		if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
//			WalkingGroundFinder.closestBlockOnWalkableGround(fromLocation, maxBlocksChecked, new ChickenBlockChecker(nearbyChickens), playersToShowSearchSpace, false, WalkingGroundFinder.walkEverywhere);
		
		if(!nearbyChickens.isEmpty())
		{			
			WalkingGroundFinder.WalkingGroundFinderResult chickenFindResult = WalkingGroundFinder.closestBlockOnWalkableGround(fromLocation, maxBlocksChecked, new ChickenBlockChecker(nearbyChickens), playersToShowSearchSpace, WalkingGroundFinder.walkEverywhere);
			if(chickenFindResult != null)
			{
				Location nextLocation = selected.getLocation();
				allSelecteds.add(selected);
				chickenFindResult.wayPoints.add(GoAndDo.newNoAction(nextLocation));
				
				if (fromHome)
				{
					homeToJobNavigator = new Navigator(chickenFindResult.wayPoints);
				}
				else
				{
					homeToJobNavigator.wayPoints.add(GoAndDo.newNoAction(nextLocation)); // these are only used on the way back!
				}
				
				return selected.getLocation();
			}
		}

		if(Debug.showCouldntFindJobDebug)
			Debug.out(npc.getName() + " from " + homeBuilding.city.getFaction().getName() + " couldn't find an adult chicken!");
		return null;
	}

	@Override
	Action doJob() {
		consume();
		collectRemaining = numberOfCollectsPerBatch;
		return collectChicken(true);
	}

	
	Action collectChicken(boolean firstArrival) {
		if(selected.isValid() && npc.getEntity().getLocation().distance(selected.getLocation()) <= closeEnoughRange)
		{
			//Selected chicken is alive and close enough
			selected.setBaby();
			produce();
			collectRemaining--;
		}
		
		cancelCurrentJob();
		
		final Location chickenLocation;
		
		if (collectRemaining == 0 || 
				(chickenLocation = findClosestChicken(subJobRadius, subJobRange, false)) == null) 
		{
			//No more chickens to do
			this.jobToHomeNavigator = homeToJobNavigator.reversed();
			return getJobToHomeNavigatorAction();
		}
		else 
		{
			return new Action(){ // collect chickens

				@Override
				public Location getLocation() {
					return chickenLocation;
				}

				@Override
				public Action doAction() {
					return collectChicken(false);
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
		allSelecteds.remove(selected);
		selected = null;
	}

}
