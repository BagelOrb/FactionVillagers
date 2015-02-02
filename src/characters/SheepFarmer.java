package characters;

import java.util.HashSet;
import java.util.List;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

import utils.BlockChecker;
import utils.EntityChecker;
import utils.EntityUtils;
import utils.WalkingGroundFinder;
import characters.Navigator.GoAndDo;

public class SheepFarmer extends ChestCharacter{

    public static final String traitName = "sheepFarmer";
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
	
	private Sheep selectedSheep = null;
	private static HashSet<Sheep> allSelectedSheep = new HashSet<Sheep>();
	private int collectRemaining;

	public SheepFarmer() {
		super(traitName);
	}
	public CharacterType getCharacterType() { return CharacterType.SHEEP_HERDER; }

	public class SheepChecker extends EntityChecker {
		@Override
		public boolean isValid(Entity entity) {
			if(entity.getType() == EntityType.SHEEP && entity instanceof Sheep)
			{
				if(((Sheep) entity).isAdult() && !((Sheep) entity).isSheared()) // Is adult and has wool
					return true;
			}
			return false;
		}
	}
	
	
	
	public class SheepBlockChecker extends BlockChecker {
		
		List<Sheep> nearbySheep;
		
		public SheepBlockChecker(List<Sheep> nearbySheep)
		{
			this.nearbySheep = nearbySheep;
		}
		
		@Override
		public boolean isValid(Block groundBlock) {
			Block blockAboveGround = groundBlock.getRelative(BlockFace.UP);
			for(Sheep sheep : nearbySheep)
			{
				if(blockAboveGround.getLocation().distanceSquared(sheep.getLocation()) <= 1)
				{
					selectedSheep = sheep;
					return true;
				}
			}
			return false;
		}
		
	}
	
	@Override
	public Location findJob() {
		return findClosestSheep(jobRadius, jobRange, true);
	}
	
	private Location findClosestSheep(int radius, int maxBlocksChecked, boolean fromHome) {
		
		List<Sheep> nearbySheep = EntityUtils.getCloseEntities(npc.getEntity(), new SheepChecker(), jobRadius, Sheep.class);

		Location fromLocation = this.getNPC().getEntity().getLocation();
//		if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
//			WalkingGroundFinder.closestBlockOnWalkableGround(fromLocation, maxBlocksChecked, new SheepBlockChecker(nearbySheep), playersToShowSearchSpace, false, WalkingGroundFinder.walkEverywhere);
		
		if(!nearbySheep.isEmpty())
		{			
			WalkingGroundFinder.WalkingGroundFinderResult sheepFindResult = WalkingGroundFinder.closestBlockOnWalkableGround(fromLocation, maxBlocksChecked, new SheepBlockChecker(nearbySheep), playersToShowSearchSpace, WalkingGroundFinder.walkEverywhere);
			if(sheepFindResult != null)
			{
				Location nextLocation = selectedSheep.getLocation();
				allSelectedSheep.add(selectedSheep);
				sheepFindResult.wayPoints.add(GoAndDo.newNoAction(nextLocation));
				
				if (fromHome)
				{
					homeToJobNavigator = new Navigator(sheepFindResult.wayPoints);
				}
				else
				{
					homeToJobNavigator.wayPoints.add(GoAndDo.newNoAction(nextLocation)); // these are only used on the way back!
				}
				
				return selectedSheep.getLocation();
			}
		}

		if(Debug.showCouldntFindJobDebug)
			Debug.out(npc.getName() + " from " + homeBuilding.city.getFaction().getName() + " couldn't find an adult sheep with wool!");
		return null;
	}

	@Override
	Action doJob() {
		consume();
		collectRemaining = numberOfCollectsPerBatch;
		return collectSheep(true);
	}

	
	Action collectSheep(boolean firstArrival) {
		if(selectedSheep.isValid() && npc.getEntity().getLocation().distance(selectedSheep.getLocation()) <= closeEnoughRange)
		{
			//Selected sheep is alive and close enough
			shaveSheep();
			//produce();
			collectRemaining--;
		}
		
		cancelCurrentJob();
		
		final Location sheepLocation;
		
		if (collectRemaining == 0 || 
				(sheepLocation = findClosestSheep(subJobRadius, subJobRange, false)) == null) 
		{
			//No more sheep to do
			this.jobToHomeNavigator = homeToJobNavigator.reversed();
			return getJobToHomeNavigatorAction();
		}
		else 
		{
			return new Action(){ // collect sheep

				@Override
				public Location getLocation() {
					return sheepLocation;
				}

				@Override
				public Action doAction() {
					return collectSheep(false);
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

	@SuppressWarnings("deprecation")
	private void shaveSheep() {
		ItemStack item = new ItemStack(Material.WOOL, 3, selectedSheep.getColor().getData());
		InventoryTraitUtils.add(inventory, item);
		homeBuilding.city.statistics.produce(homeBuilding, item.getType(), item.getAmount());
		selectedSheep.setSheared(true);
		
		if(selectedProduction != null)
		{
			for (ItemStack produced : selectedProduction.getItemsProduced())
			{
				if (produced.getType() != Material.WOOL)
				{
					InventoryTraitUtils.add(inventory, produced);
					homeBuilding.city.statistics.produce(homeBuilding, produced.getType(), produced.getAmount());
				}
			}
		}
	}

	@Override
	protected void cancelCurrentJob() {
		allSelectedSheep.remove(selectedSheep);
		selectedSheep = null;
	}


}
