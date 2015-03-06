package characters;

import happiness.HappinessEnhancement;
import happiness.HappinessIfBlockPresent;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import main.Debug;
import main.FactionVillagers;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import utils.BlockUtils;
import buildings.Building;
import buildings.BuildingWithStorage;
import characters.Navigator.GoAndDo;
import city.City;

public abstract class Character extends Trait {

	public static enum ActionType {
		STORAGE,
		HOME,
		JOB,
		MOVE_THROUGH,
		CLOSE_GATE,
		OPEN_GATE, WAIT, OTHER
	}

	static abstract class Action {
			/**
			 * @return the location where to perform the action
			 */
			public abstract Location getLocation();
	//		/**
	//		 * called before execution. override this method when you want stuff to be done not on creation, but when executing!
	//		 */
	//		public void init() { }
			/**
			 * perform everything which you have to do at this place
			 * @return the next action to perform
			 */
			public abstract Action doAction();
			
			/**
			 * @return the time he needs to wait after the job
			 */
			public abstract long getWaitingTime();
			
			public abstract Action cantNavigate(); 
			public abstract ActionType getActionType();
		}

	public static abstract class ActionContainer {
		public abstract Action getAction();
	
		public static ActionContainer neww(final Action action) {
			return new ActionContainer() {
				@Override
				public Action getAction() {
					return action;
				}
			};
		}
	}

	protected static final FactionVillagers plugin = FactionVillagers.getCurrentPlugin();

	public static final int numberOfNavigationTriesBeforeStuck = plugin.getConfig().getInt("character.numberOfNavigationTriesBeforeStuck");
	static final long cantDoActionWaitingTime = plugin.getConfig().getInt("character.cantDoActionWaitingTime");
	protected static final Random random = new Random();
	public static final int closeEnoughRange = plugin.getConfig().getInt("character.closeEnoughRange");
	public BuildingWithStorage homeBuilding;

	abstract String getTraitName();

	protected String getConfigYmlPath() {
		return "character."+getTraitName();
	}

	private Location homeLocation;
	public Location getHomeLocation() {
		return homeLocation.clone();
	}

	public void setHomeLocation(Location homeLocation) {
		this.homeLocation = homeLocation;
	}
	
	/**
	 * @return number of days he didnt eat for ChestCharacters, zero otherwise
	 */
	public int getNumberOfTimesNotEaten() {
		return 0;
	}
	
	public List<HappinessEnhancement> happinessEnhancements;
	@SuppressWarnings("unchecked")
	public static List<HappinessIfBlockPresent> tempHappinessFromResouce = (List<HappinessIfBlockPresent>) FactionVillagers.getCurrentPlugin().getConfig().getList("happiness.tempHappinessFromResouce");
	private double noFoodUnhappinessModifier = plugin.getConfig().getDouble("happiness.noFoodUnhappinessModifier");

	@SuppressWarnings("unchecked")
	protected Character(String name) {
		super(name);
		
		try {
			happinessEnhancements = (List<HappinessEnhancement>) FactionVillagers.getCurrentPlugin().getConfig().getList(getConfigYmlPath()+".happinessEnhancements");
			happinessEnhancements.addAll((List<HappinessEnhancement>) FactionVillagers.getCurrentPlugin().getConfig().getList("happiness.commonHappinessEnhancements"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public double getHappiness() {
		double homeHappiness = getBuildingHappinessEnhancement();
		double foodUnhappiness = -1*getNumberOfTimesNotEaten() * noFoodUnhappinessModifier;
		double cityHappinessModifier = homeBuilding.city.getNpcHappinessEnhancement();
		return homeHappiness + foodUnhappiness + cityHappinessModifier;
	}
	
	
	
	
	
	private double getBuildingHappinessEnhancement() {
		double blockHappiness = 0;
		for (HappinessEnhancement happinessEnhancement : happinessEnhancements)
			blockHappiness += happinessEnhancement.enhancement(homeBuilding.airSpaceChecker.borderBlocks);
		return blockHappiness;
	}


	

	public abstract void destroy();

	public void construct(BuildingWithStorage building, Location homeLocation) {
		homeBuilding = building;

		setHomeLocation(homeLocation);
//		if (building instanceof ChestBuilding)
//		{
//			ChestBuilding chestBuilding = (ChestBuilding) building; 
////			Block blockInFrontOfChest = building.startingBlock.getRelative(ChestUtils.getDirection(chestBuilding.mainChest.getBlock()));
//			setHomeLocation(chestBuilding.homeLocationRequirement.getHomeLocation());
//		}
//		else
//			setHomeLocation(building.homeLocationRequirement.getHomeLocation());

		
	
	}
	@Override
	public void onSpawn() {
		npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

		Entity ent = npc.getEntity();
		if(ent instanceof Villager)
		{
			npc.getNavigator().getLocalParameters().speedModifier((float) 0.5);
			Villager villager = (Villager) ent;
			villager.setProfession(homeBuilding.getCharacterType().profession);
		}
	}

	/**
	 * don't call homeBuilding.recheck() from this method! (cause it already calles this method)
	 * @return whether this NPC is valid (not to be destroyed)
	 */
	public boolean recheck() {
		boolean valid = checkSettings();
		
		if (!valid) 
		{
			if (homeBuilding != null)
				homeBuilding.npcId = -1;
			getNPC().destroy();
		}
		return valid;
	}

	private boolean checkSettings() {
		if (homeBuilding == null) return false;
		
		return true;
	}
	
	public void save(DataKey key) {
		key.setString("homeBuildingId", homeBuilding.getFullyQualifiedId());
		key.setInt("homeX", getHomeLocation().getBlockX());
		key.setInt("homeY", getHomeLocation().getBlockY());
		key.setInt("homeZ", getHomeLocation().getBlockZ());
	}
	
	public void load(DataKey key) {
		String homeBuildingId = key.getString("homeBuildingId");
		homeBuilding = (BuildingWithStorage) Building.parseBuildingId(homeBuildingId);
		if (homeBuilding==null) 
		{
			Debug.out("all cities, all buildings: ");
			for (City city : FactionVillagers.allCities)
			{
				Debug.out("City "+city.getFaction().getName());
				for (Building building : city.getAllBuildings())
					Debug.out("Building "+building.getFullyQualifiedId());
			}
			Debug.warn("Couldn't parse homeBuilding! : "+homeBuildingId+". Destroying in 1 tick...");
//			getNPC().destroy();
			final Character thisTrait = this;
			new BukkitRunnable(){
				@Override
				public void run() {
				thisTrait.getNPC().destroy();
					
				}}.runTaskLater(FactionVillagers.getCurrentPlugin(), 1);
			return;
		}
		
		Location homeLocation;
		try{ 
			homeLocation = new Location(homeBuilding.startingBlock.getWorld(), key.getInt("homeX"), key.getInt("homeY"), key.getInt("homeZ"));
		}catch (Exception e) {
			homeLocation = homeBuilding.homeLocationRequirement.getHomeLocation();
		}
		
		
		construct(homeBuilding, homeLocation);
	}
	
	
	Location nextLocation;
	protected Action currentAction;
	int numberOfNavigationTries = 0;
	protected HashSet<Player> playersToShowSearchSpace = new HashSet<Player>();
	
	protected void teleport(Location location) {
//		Debug.out(5, "Teleporting "+getNPC().getName()+" to "+location);
		getNPC().teleport(location, TeleportCause.PLUGIN);
	}
	
	public void goTo(Location loc) {
		if (getNPC().isSpawned())
		{
			nextLocation = loc;
			getNPC().getNavigator().setTarget(loc);
		}
//		else
//			Debug.out("WARNING: Can't set next target location of despawned NPC!");
	}

	public void goTo(final Location loc, long delay) {
		if (getNPC().isSpawned())
			new BukkitRunnable(){

			@Override
			public void run() {
				goTo(loc);
			}}.runTaskLater(FactionVillagers.getCurrentPlugin(), delay);
//		else
//			Debug.out("WARNING: Can't set next target location of despawned NPC!");
	}

	@EventHandler
	public void onNavComplete(NavigationCompleteEvent event) {
		if(event.getNPC() != this.getNPC())
			return;
		
		decideNextAction();
	}

	@EventHandler
	public void onNavCancel(NavigationCancelEvent event) {
		if(event.getNPC() != this.getNPC())
			return;
	
		
		decideNextAction();
	}

	public Action teleportToAndDo(final Location tpLoc, final Action next) {
		return new Action() {
			
			@Override
			public long getWaitingTime() {
				return 0;
			}
			
			@Override
			public Location getLocation() {
				return tpLoc;
			}
			
			@Override
			public ActionType getActionType() {
				return ActionType.MOVE_THROUGH;
			}
			
			@Override
			public Action doAction() {
				teleport(tpLoc);
				return next;
			}
			
			@Override
			public Action cantNavigate() {
				return next;
			}
		};
	}
	
	public void decideNextAction(long delay) {
			new BukkitRunnable(){

			@Override
			public void run() {
				decideNextAction();
			}}.runTaskLater(FactionVillagers.getCurrentPlugin(), delay);
	}

	public void decideNextAction() {
		Location currentLocation = npc.getEntity().getLocation();
		
		if(!currentLocation.getChunk().isLoaded() || !nextLocation.getChunk().isLoaded())
		{
			Debug.out(this.getName()+"'s location or next location is in an unloaded chunk!");
			decideNextAction(10);
		}
		else
		{
			if(nextLocation.distanceSquared(currentLocation) > closeEnoughRange * closeEnoughRange) //Can't reach
			{
				if (numberOfNavigationTries < numberOfNavigationTriesBeforeStuck)
				{
					if (currentAction.getActionType() == ActionType.CLOSE_GATE)
					{
	//					Location inBetween = this.getNPC().getEntity().getLocation().add(nextLocation).multiply(.5);
	//					Block blockInBetween = inBetween.getWorld().getBlockAt(inBetween);
						Block blockInBetween = nextLocation.getWorld().getBlockAt(currentLocation);
						if (BlockUtils.isGateType(blockInBetween.getType()))
						{
							BlockUtils.setGateOpen(blockInBetween, true);
						}
					}
					
					Debug.out(this.getName()+" couldn't get to next loc "+nextLocation+"! He tries again...");
					if (Debug.showSearchSpaceDebug  && !playersToShowSearchSpace.isEmpty())
						ShowBlockChange.showAs(nextLocation, Material.YELLOW_FLOWER, playersToShowSearchSpace, currentAction.getWaitingTime()*2);
					
					numberOfNavigationTries++;
	//				currentAction = calculateNavigationTo(nextLocation); // TODO: is it bugging?
					nextLocation = currentAction.getLocation();
					goTo(nextLocation, cantDoActionWaitingTime); //currentAction.getWaitingTime());
					
				}
				else
				{
					numberOfNavigationTries = 0;
					if (Debug.showCouldntFindJobDebug)
						Debug.out(npc.getName() + " from " + homeBuilding.city.getFaction().getName() + " is too far away from his next location doing "+currentAction.getActionType()+"! \r\n location = "+nextLocation);
					
					currentAction = currentAction.cantNavigate();
					nextLocation = currentAction.getLocation();
					goTo(nextLocation, cantDoActionWaitingTime); //currentAction.getWaitingTime());
					if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty()) 
						ShowBlockChange.showAs(nextLocation.getWorld().getBlockAt(nextLocation), Material.SPONGE, playersToShowSearchSpace, currentAction.getWaitingTime()*2);
				}
			}
			else // destination reached!
			{
				numberOfNavigationTries = 0;
	//			Debug.out(npc.getName()+" does "+currentAction.getActionType());
				long waitingTime = currentAction.getWaitingTime();
				currentAction = currentAction.doAction();
				nextLocation = currentAction.getLocation();
				goTo(nextLocation, waitingTime);
				if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
					ShowBlockChange.showAs(nextLocation.getWorld().getBlockAt(nextLocation), Material.RED_ROSE, playersToShowSearchSpace, 100);
			}
		}
	}

	public void rerouteVia(Location loc) {
		delayCurrentAction(GoAndDo.newNoAction(loc));
	}

	public void delayCurrentAction(final GoAndDo goDoFirst) {
		final Action nextAction = currentAction; 
		currentAction = new Action() {
			@Override
			public long getWaitingTime() {
				return 20;
			}
			@Override
			public Location getLocation() {
				return goDoFirst.loc;
			}
			@Override
			public ActionType getActionType() {
				return ActionType.OTHER;
			}
			@Override
			public Action doAction() {
				goDoFirst.doAction(Character.this);
				return nextAction;
			}
			@Override
			public Action cantNavigate() {
				return nextAction;
			}
		};
		nextLocation = currentAction.getLocation();
		goTo(goDoFirst.getLocation());
	}




}
