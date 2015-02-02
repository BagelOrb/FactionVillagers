package characters;

import main.Debug;
import main.MCity;
import net.citizensnpcs.api.util.DataKey;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import utils.WalkingGroundFinder;
import buildings.Building;
import buildings.BuildingWithTradeVillager;
import city.City;

public class TradeVillagerWalker extends Character {


//	private BuildingWithTradeVillager homeBuilding;
	private Location homeLocation;
	private BukkitRunnable createTradeVillager;
	
	public TradeVillagerWalker() {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	
	public static final String traitName = "tradeVillager";
	String getTraitName() {
		return traitName;
	}
//	protected String getConfigYmlPath() {
//		return "character."+getTraitName();
//	}



	public void construct(BuildingWithTradeVillager buildingWithTradeVillager, Location toLocation, BukkitRunnable createTradeVillager) {
		super.construct(buildingWithTradeVillager, toLocation);
		this.createTradeVillager = createTradeVillager;
	}
	
	
	
	//Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
	//This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
		super.onSpawn();
		npc.getNavigator().getLocalParameters().avoidWater(true);
		npc.getNavigator().getLocalParameters().useNewPathfinder(false);
		
		
		Navigator toHomeNavigator = Navigator.getNavigator(npc.getEntity().getLocation(), getHomeLocation(), 4000, WalkingGroundFinder.pathChecker(homeBuilding.city.getFaction()));
		this.currentAction = toHomeNavigator.toAction(this, createTradeVillager(), teleportToAndDo(getHomeLocation(), createTradeVillager()));
		
		
		goTo(currentAction.getLocation(), random.nextInt(200));

	}

//	public void recheck() {
//		boolean valid = checkSettings();
//		
//		if (!valid) 
//		{
//			if (homeBuilding != null)
//				homeBuilding.npcId = -1;
//			getNPC().destroy();
//		}
//	}
//
//	private boolean checkSettings() {
//		if (homeBuilding == null) return false;
//		
//		return true;
//	}
	
	private Action createTradeVillager() {
		return new Action() {
			
			@Override
			public long getWaitingTime() {
				return 0;
			}
			
			@Override
			public Location getLocation() {
				return getHomeLocation();
			}
			
			@Override
			public ActionType getActionType() {
				return ActionType.MOVE_THROUGH;
			}
			
			@Override
			public Action doAction() {
				getNPC().destroy();
				createTradeVillager.run();
				return noAction;
			}
			
			@Override
			public Action cantNavigate() {
				createTradeVillager.run();
				getNPC().destroy();
				return noAction;
			}
		};
	}

	private Action noAction = new Action() {
		@Override public long getWaitingTime() { return 100; }
		@Override public Location getLocation() {return homeLocation; }
		@Override public ActionType getActionType() { return ActionType.WAIT; }
		@Override public Action doAction() { return noAction;}
		@Override public Action cantNavigate() { return noAction; }
	};


	public void save(DataKey key) {
		key.setString("homeBuildingId", homeBuilding.getFullyQualifiedId());
		key.setInt("homeX", homeLocation.getBlockX());
		key.setInt("homeY", homeLocation.getBlockY());
		key.setInt("homeZ", homeLocation.getBlockZ());
	}
	
	public void load(DataKey key) {
		String homeBuildingId = key.getString("homeBuildingId");
		homeBuilding = (BuildingWithTradeVillager) Building.parseBuildingId(homeBuildingId);
		if (homeBuilding==null) 
		{
			Debug.out("all cities, all buildings: ");
			for (City city : MCity.allCities)
			{
				Debug.out("City "+city.getFaction().getName());
				for (Building building : city.getAllBuildings())
					Debug.out("Building "+building.getFullyQualifiedId());
			}
			Debug.warn("Couldn't parse homeBuilding! : "+homeBuildingId+". Destroying in 1 tick...");
//			getNPC().destroy();
			final TradeVillagerWalker thisTrait = this;
			new BukkitRunnable(){
				@Override
				public void run() {
				thisTrait.getNPC().destroy();
					
				}}.runTaskLater(MCity.getCurrentPlugin(), 1);
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
	
	@Override
	public void destroy() {
	}




	

}
