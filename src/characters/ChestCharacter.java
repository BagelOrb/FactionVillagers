package characters;

import factions.FactionUtils;
import generics.Tuple;
import happiness.Happiness;
import happiness.HappinessIfBlockPresent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import main.Debug;
import main.FactionVillagers;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.util.DataKey;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import utils.BlockChecker;
import utils.WalkingGroundFinder;
import buildings.BuildingWithStorage;
import characters.Navigator.GoAndDo;
import city.City;

import com.massivecraft.massivecore.util.Txt;

public abstract class ChestCharacter extends Character {

	private static final int getReadyToWorkDelay = plugin.getConfig().getInt("character.getReadyToWorkDelay");
	private static final int doHomeMoveThroughDelay = plugin.getConfig().getInt("character.doHomeMoveThroughDelay");
	private static final int doStorageDelay = plugin.getConfig().getInt("character.doStorageDelay");
	private int jobWaitingTime = plugin.getConfig().getInt(getConfigYmlPath()+".jobWaitingTime");

	
	private static final double minSpeed = plugin.getConfig().getDouble("happiness.minSpeed");
	private static final double maxSpeed = plugin.getConfig().getDouble("happiness.maxSpeed");
	
	private float speedModifier = .5f;
	public int getJobWaitingTime() {
		return Math.max(1, (int) (jobWaitingTime		/ speedModifier));
	}

	public int getDostoragedelay() {
		return Math.max(1, (int) (doStorageDelay		/ speedModifier));
	}

	public int getDohomemovethroughdelay() {
		return Math.max(1, (int) (doHomeMoveThroughDelay/ speedModifier));
	}

	public int getGetreadytoworkdelay() {
		return Math.max(1, (int) (getReadyToWorkDelay	/ speedModifier));
	}
	
	private double happinessFromHappyMeal = 0;

	@Override
	public double getHappiness() {
		return super.getHappiness() + happinessFromHappyMeal;
	}
	
	
	public int sleepTime = plugin.getConfig().getInt("character.sleepTime");
	

	public boolean recheck() {
		boolean valid = super.recheck();
		if (!valid) 
			return false;

		
		double happiness = Happiness.getHappinessPercentage(getHappiness());
		speedModifier =  (float) (minSpeed+happiness*(maxSpeed-minSpeed))*2;
		
		getNPC().getNavigator().getLocalParameters().speedModifier(speedModifier/2f);
		
		return valid;
	}

	
	public final ItemStack[] itemsNeededToEat = plugin.getConfig().getList(getConfigYmlPath()+".itemsNeededToEat").toArray(new ItemStack[0]);
	
	final List<Production> productions;
//	private final ItemStack[] itemsNeededToConsume; // = plugin.getConfig().getList(getConfigYmlPath()+".itemsNeededToConsume").toArray(new ItemStack[0]);
//	private final ItemStack[] itemsProduced; // = plugin.getConfig().getList(getConfigYmlPath()+".itemsProduced").toArray(new ItemStack[0]);


//	public Location spawnLocation; // used because in onSpawn() we need to know the location, but npc.getEntity() returns null!!!

	
	public Inventory inventory; 
	public BuildingWithStorage storageRoom;
	public Location storageLocation;

	/**
	 * number of days he didn't eat
	 */
	private int numberOfTimesNotEaten = 0;
	
	/**
	 * @return the numberOfTimesNotEaten
	 */
	@Override
	public int getNumberOfTimesNotEaten() {
		return numberOfTimesNotEaten;
	}

	/**
	 * @param numberOfTimesNotEaten the numberOfTimesNotEaten to set
	 */
	public void setNumberOfTimesNotEaten(int numberOfTimesNotEaten) {
		this.numberOfTimesNotEaten = numberOfTimesNotEaten;
	}






	/**
	 * the n-th time he didn't eat he becomes unemployed
	 */
	private int numberOfTimesNotEatenUntilBecomingUnemployed = 3;

	
	InternalState internalState = InternalState.NORMAL; 

	boolean canFindStorageRoom = false;
	
	protected Production selectedProduction;

	
	Navigator homeToJobNavigator;
	Navigator jobToHomeNavigator;
	Navigator homeToStorageNavigator;
	Navigator storageToHomeNavigator;

	
	@SuppressWarnings("unchecked")
	public ChestCharacter(String name) {
		super(name);
		
		List<Production> prods = (List<Production>) plugin.getConfig().getList(getConfigYmlPath()+".productions");
		if (prods == null)
			Debug.warn("Couldn't get productions from config! "+ getConfigYmlPath()+".productions");
		productions = prods;
	
		

	}
	
	public void construct(BuildingWithStorage building, Location homeLocation) {
		super.construct(building, homeLocation);
		
		/*
		FileConfiguration conf = FactionVillagers.getCurrentPlugin().getConfig();
//		conf.set(getConfigYmlPath()+".production", new Tuple<ItemStack[], ItemStack[]>(production.getItemsNeededToConsume(), itemsProduced));
		Production prod = new Production(production.getItemsNeededToConsume(), production.getItemsProduced());
		List<Production> prods = new LinkedList<Production>();
		prods.add(prod);
		conf.set(getConfigYmlPath()+".production", prods);
		conf.getConfigurationSection(getConfigYmlPath()).set("production.getItemsNeededToConsume()", null);
		conf.getConfigurationSection(getConfigYmlPath()).set("itemsProduced", null);
		*/
	}
	
	/*
	// when saving
	private void moveItemsConsumedToInventory() {
//		LinkedList<ItemStack> itemsLeft = new LinkedList<ItemStack>(Arrays.asList(getItemsNeededToConsume()));
		ItemStack[] itemsLeft = production.getItemsNeededToConsume();
		for (ItemStack notYetConsumed : itemsNeededToCompleteConsumption)
		{
			for (ItemStack total : itemsLeft)
				if (total.getType() == notYetConsumed.getType())
				{
					total.setAmount(total.getAmount() - notYetConsumed.getAmount());
					continue;
				}
		}
		InventoryTraitUtils.addItem(inventory, itemsLeft);
	}
	*/
	//Save settings for this NPC. These values will be added to the citizens saves.yml under this NPC.
	public void save(DataKey key) {
		super.save(key);
		
//		moveItemsConsumedToInventory();
		
	}

	//Here you should load up any values you have previously saved. 
	//This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
	//This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
	//This is called BEFORE onSpawn so do not try to access npc.getBukkitEntity(). It will be null.
	public void load(DataKey key) {
		
		super.load(key);
//		construct(homeBuilding);
		
	}
	

	//Run code when your trait is attached to a NPC. 
	//This is called BEFORE onSpawn so do not try to access npc.getBukkitEntity(). It will be null.
	@Override
	public void onAttach() {
		super.onAttach();
		
		//This will send a empty key to the Load method, forcing it to load the config.yml defaults.
		//Load will get called again with a real key if this NPC has previously been saved
//		load(new net.citizensnpcs.api.util.MemoryDataKey());
	}

	//Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
	//This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
		super.onSpawn();
		setStoragePreferred();
		inventory = getNPC().getTrait(Inventory.class);
		
		npc.getNavigator().getLocalParameters().avoidWater(true);
		npc.getNavigator().getLocalParameters().useNewPathfinder(false);
		
		
		new BukkitRunnable() {
			

			@Override
			public void run() {
				BlockChecker pathChecker = WalkingGroundFinder.pathChecker(homeBuilding.city.getFaction());
				Location homeLoc = getHomeLocation();
				Location locNow = getNPC().getEntity().getLocation();
				Navigator toHomeNavigator = Navigator.getNavigator(locNow, homeLoc , 4000, pathChecker );
				
				ChestCharacter.this.currentAction = toHomeNavigator.toAction(ChestCharacter.this, getFirstAction(), teleportToAndDo(getHomeLocation(), getFirstAction()));
				
				goTo(currentAction.getLocation());
			}
		}.runTaskLater(FactionVillagers.getCurrentPlugin(), random.nextInt(180)+20);

	}
	
	public Action getFirstAction() {
		return getHomeToStorageForFoodNavigatorAction();		
	}

	// Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
//		mainActionsCycle.cancel();
	}

	//run code when the NPC is removed. Use this to tear down any repeating tasks.
	@Override
	public void onRemove() {
	}

	
    // An example event handler. All traits will be registered automatically as Bukkit Listeners.
	@EventHandler
	public void onDeath(NPCDeathEvent event){
		
		
	}
	
	
	
	
	
	public void start() {
		if (this.getNPC().isSpawned())
			decideNextAction();
	}
	





	Action doGetFood = new Action(){


		@Override
		public Location getLocation() {
			return storageLocation;
		}

		@Override
		public Action doAction() {
			setStorageFound();
			
			boolean hasEaten = tryToEatDirectlyFromStorage(storageRoom);
			
			if(!hasEaten)
			{
				if(ChestCharacter.this instanceof Unemployed)
				{
					Unemployed unemployed = (Unemployed) ChestCharacter.this;
					unemployed.destroy();
					unemployed.getNPC().destroy();
					FactionUtils.sendMessage(homeBuilding.city.getFaction(), Txt.parse("<bad>An unemployed villager couldn't find enough food and left your city!"));
				}
				else
				{
					setNumberOfTimesNotEaten(getNumberOfTimesNotEaten()+1);
					if (getNumberOfTimesNotEaten() >= numberOfTimesNotEatenUntilBecomingUnemployed)
					{
						homeBuilding.destroy();
					FactionUtils.sendMessage(homeBuilding.city.getFaction(), Txt.parse("<bad>"+getNPC().getName()+" couldn't find enough food and became unemployed!"));
					}
					else 
					{
						FactionUtils.sendMessage(homeBuilding.city.getFaction(), Txt.parse("<gold>"+getNPC().getName()+" couldn't eat!"));
					}
				}
			}
			else
			{
				if( ! (ChestCharacter.this instanceof Unemployed))
				{
					happinessFromHappyMeal = tryToEatHappyMeal(storageRoom);
				}
				doStorage();
			}
			
			return getStorageToHomeNavigatorAction();
		}

		@Override
		public long getWaitingTime() {
			return getDostoragedelay();
		}

		@Override
		public Action cantNavigate() {

			teleport(getHomeLocation());
			return newHomeToStorageNavigatorAction.getAction();
		}

		@Override
		public ActionType getActionType() {
			return ActionType.STORAGE;
		}
	};
	Action doStorage = new Action(){
		@Override
		public Location getLocation() {
			return storageLocation;
		}

		@Override
		public Action doAction() {
			doStorage();
			return getStorageToHomeNavigatorAction();
		}

		@Override
		public long getWaitingTime() {
			return getDostoragedelay();
		}

		@Override
		public Action cantNavigate() {

			teleport(getHomeLocation());
			return newHomeToStorageNavigatorAction.getAction();
		}

		@Override
		public ActionType getActionType() {
			return ActionType.STORAGE;
		}
	};
	Action doGetReadyToWork = new Action() {
		@Override
		public Location getLocation() {
			return getHomeLocation();
		}
		
		@Override
		public Action doAction() {
			//storeItems((BuildingWithStorage) homeBuilding); Don't store items at home
			return goDoJobIfPossible();
		}
		@Override
		public long getWaitingTime() {
			return getGetreadytoworkdelay();
		}
		@Override
		public Action cantNavigate() {

			teleport(getHomeLocation());
			return doGetReadyToWork;
		}
		@Override
		public ActionType getActionType() {
			return ActionType.HOME;
		}
	};
	Action doHomeMoveThrough = new Action() {
		@Override
		public Location getLocation() {
			return getHomeLocation();
		}
		
		@Override
		public Action doAction() {
			return getHomeToStorageNavigatorAction();
		}
		@Override
		public long getWaitingTime() {
			return getDohomemovethroughdelay();
		}
		@Override
		public Action cantNavigate() {
			teleport(getHomeLocation());
			return doHomeMoveThrough;
		}
		@Override
		public ActionType getActionType() {
			return ActionType.MOVE_THROUGH;
		}
	};

	
	private Action getHomeToJobNavigatorAction(final Location jobLoc) {
		if (homeToJobNavigator == null)
		{
			Debug.warn("homeToJobNavigator == null!! how can this be?!");
			return getJobAction(jobLoc);
		}
		showWayPoints(homeToJobNavigator);
		return homeToJobNavigator.toAction(
				ChestCharacter.this, 
				getJobAction(jobLoc), 
				doGetReadyToWork);
	}
	private void showWayPoints(Navigator navigator) {
		if (navigator == null)
			return;
		LinkedList<GoAndDo> wayPoints = navigator.wayPoints;
		ArrayList<Block> wayPointBlocks = new ArrayList<Block>(wayPoints.size());
		for (int i = 0 ; i< wayPoints.size(); i++)
		{
			Location loc = wayPoints.get(i).loc; // .add(0, -1, 0);
			wayPointBlocks.add(loc.getWorld().getBlockAt(loc));
		}
		if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
			ShowBlockChange.showAs(wayPointBlocks, Material.DEAD_BUSH, playersToShowSearchSpace, 1000);
	}

	/**
	 * @return the jobToHomeNavigatorAction
	 */
	public Action getJobToHomeNavigatorAction() {
		jobToHomeNavigator = homeToJobNavigator.reversed(); 
		showWayPoints(jobToHomeNavigator);
		return jobToHomeNavigator.toAction(this, doHomeMoveThrough, doHomeMoveThrough);
	}
	/**
	 * @return the homeToStorageNavigatorAction
	 */
	public Action getHomeToStorageNavigatorAction() {
		if (homeToStorageNavigator == null) {
			setNewHomeToStorageNavigator();
		}
		showWayPoints(homeToStorageNavigator);
		return homeToStorageNavigator.toAction(ChestCharacter.this, doStorage, newHomeToStorageNavigatorAction);
	}
	/**
	 * @return the homeToStorageForFoodNavigatorAction
	 */
	public Action getHomeToStorageForFoodNavigatorAction() {
		homeBuilding.recheckAndShowMessage(null, true, true);
		if (homeToStorageNavigator == null) {
			setNewHomeToStorageNavigator();
		}
		showWayPoints(homeToStorageNavigator);
		return homeToStorageNavigator.toAction(ChestCharacter.this, doGetFood, newHomeToStorageNavigatorAction);
	}
	/**
	 * @return the storageToHomeNavigatorAction
	 */
	public Action getStorageToHomeNavigatorAction() {
		if (homeToStorageNavigator == null) { 
			setNewHomeToStorageNavigator();
		}
		if (storageToHomeNavigator == null)
		{
			Debug.warn("getStorageToHomeNavigatorAction: storageToHomeNavigator begin null should have already been handled in setNewHomeToStorageNavigator!");
			Debug.out("HAVE YOU AVER SEEN THIS MESSAGE??"); // delete these things if never seen
		}
			storageToHomeNavigator = homeToStorageNavigator.reversed();
		showWayPoints(storageToHomeNavigator);
		return storageToHomeNavigator.toAction(ChestCharacter.this, doGetReadyToWork, doGetReadyToWork);
	}
	/**
	 * construct a new homeToStorageNavigator only on execution!
	 * @return a new homeToStorageNavigatorAction from a new navigator
	 */
	
	public ActionContainer newHomeToStorageNavigatorAction = new ActionContainer() {
		@Override
		public Action getAction() {
			setNewHomeToStorageNavigator();
			return getHomeToStorageNavigatorAction();
		}
	};
	
//	/**
//	 * construct a new homeToStorageNavigator only on execution!
//	 * @return a new homeToStorageNavigatorAction from a new navigator
//	 */
//	public ActionContainer newStorageToHomeNavigatorAction = new ActionContainer() {
//		@Override
//		public Action getAction() {
//			setStorage();
//			setNewHomeToStorageNavigator();
//			return getStorageToHomeNavigatorAction();
//		}
//	};
	
	
	
	
//	static final BlockChecker directionPrioritizer(final Location location) { 
//		return new BlockChecker(){
//
//		@Override
//		public boolean isValid(Block block) {
//			
//			return BlockUtils.isPath(block) || ;
//		}};
//	}
	private void setNewHomeToStorageNavigator() {
		setStoragePreferred();
		
//		if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
//			WalkingGroundFinder.closestBlockOnWalkableGround(
//					getHomeLocation(), 4000, BlockChecker.checkForExactLocation(storageLocation.clone().add(0, -1, 0)), playersToShowSearchSpace, false, WalkingGroundFinder.pathChecker(homeBuilding.city.getFaction()));
		
		WalkingGroundFinder.WalkingGroundFinderResult result = WalkingGroundFinder.closestBlockOnWalkableGround(
				getHomeLocation(), 4000, BlockChecker.checkForExactLocation(storageLocation.clone().add(0, -1, 0)), playersToShowSearchSpace, WalkingGroundFinder.pathChecker(homeBuilding.city.getFaction()));
		if (result == null)
		{
			canFindStorageRoom = false;
			FactionUtils.sendMessage(homeBuilding.city.getFaction(), Txt.parse("<bad>"+getNPC().getName()+" couldn't find the storage room!"));
			
			storageRoom = (BuildingWithStorage) homeBuilding;
			
			homeToStorageNavigator = new Navigator(); // is simply home to home ... navigator is empty.. no waypoints...
			storageToHomeNavigator = homeToStorageNavigator.reversed();
			storageRoom = (BuildingWithStorage) homeBuilding;
			storageLocation = getHomeLocation();
		}
		else
		{
			canFindStorageRoom = true;
			homeToStorageNavigator = new Navigator(result.wayPoints);
			storageToHomeNavigator = homeToStorageNavigator.reversed();
		}
	}
	
	enum InternalState {
		NORMAL, WANT_TO_STORE, WANT_ITEMS_TO_CONSUME // , WAITING_FOR_ACTIVE
	}
//	enum WalkingState {
//		HOME2JOB, JOB2HOME, HOME2STORAGE, STORAGE2HOME
//	}
	
	private void doStorage() {
		setStorageFound();
		if(!storeItems(storageRoom))
		{
			//Storage Room is full
			internalState = InternalState.WANT_TO_STORE;
		}
		else
		{
			if (internalState == InternalState.WANT_TO_STORE)
				internalState = InternalState.NORMAL;
			
			if(getConsumption(storageRoom))
			{
				if (internalState == InternalState.WANT_ITEMS_TO_CONSUME)
					internalState = InternalState.NORMAL;
			}
			else
			{
				internalState = InternalState.WANT_ITEMS_TO_CONSUME;
			}
		}
	}

	Action goDoJobIfPossible() {
		if(homeBuilding.startingBlock.getWorld().getTime() > sleepTime && homeBuilding.startingBlock.getWorld().getTime() < 23000)
		{
			return sleepAtHome;
		}
		//otherwise
		
		if (internalState == InternalState.WANT_TO_STORE )
			return getHomeToStorageNavigatorAction();
		if (internalState == InternalState.WANT_ITEMS_TO_CONSUME)
		{
			boolean couldConsume = getConsumption((BuildingWithStorage) homeBuilding);
			if (couldConsume)
			{
				internalState = InternalState.NORMAL;
				// continue with rest of method
			}
			else
			{
				return getHomeToStorageNavigatorAction();
			}
		}
		
		if (!homeBuilding.isActive())
		{
			homeBuilding.recheckAndShowMessage(null, true, true);
			
			return waitAtHomeForJob;
		}
		else
		{			
			final Location jobLoc = findJob();
			if (jobLoc == null)
			{
				if(Debug.debug && Debug.showSearchSpaceDebug)
					plugin.getServer().getLogger().info(npc.getName() + " from " + homeBuilding.city.getFaction().getName() + " can't find a job!");
				return waitAtHomeForJob; 
			}
			// otherwise: 
			return getHomeToJobNavigatorAction(jobLoc) ;
			
		}
		
	}
	


	private Action getJobAction(final Location jobLoc) {
		return new Action(){ // do job
			
			@Override
			public Location getLocation() {
				return jobLoc;
			}
			
			@Override
			public Action doAction() {
				return doJob();
			}
			@Override
			public long getWaitingTime() {
				return getJobWaitingTime();
			}

			@Override
			public Action cantNavigate() {
				teleport(getHomeLocation());
				cancelCurrentJob();
				return goDoJobIfPossible();
			}
			@Override
			public ActionType getActionType() {
				return ActionType.JOB;
			}
		}; 
	}
	
	
	protected abstract void cancelCurrentJob();
	
	Action waitAtHomeForJob = new Action(){

		@Override
		public Location getLocation() {
			return getHomeLocation();
		}

		@Override
		public Action doAction() {
			return goDoJobIfPossible();
		}

		@Override
		public long getWaitingTime() {
			return cantDoActionWaitingTime;
		}

		@Override
		public Action cantNavigate() {
			Debug.warn("Can't navigate to home from home?!?!!? w00t!");
			return waitAtHomeForJob;
		}
		@Override
		public ActionType getActionType() {
			return ActionType.WAIT;
		}
	};
	
	Action sleepAtHome = new Action(){

		@Override
		public Location getLocation() {
			return getHomeLocation();
		}

		@Override
		public Action doAction() {
			//Check if it's day
			if(homeBuilding.startingBlock.getWorld().getTime() < sleepTime || homeBuilding.startingBlock.getWorld().getTime() > 23000)
				return getHomeToStorageForFoodNavigatorAction();
			else
				return sleepAtHome;
		}

		@Override
		public long getWaitingTime() {
			return 1000; //1 MC hour
		}

		@Override
		public Action cantNavigate() {
			Debug.warn("Can't navigate to home from home?!?!!? w00t!");
			return sleepAtHome;
		}
		@Override
		public ActionType getActionType() {
			return ActionType.WAIT;
		}
	};

	
	/**
	 * sets the homeToJobNavigator and returns the location of the job
	 * @return location of job
	 */
	abstract Location findJob();
	abstract Action doJob();

//	public void waitTicks(long ticks) {
//		final Boxed<Boolean> done = new Boxed<Boolean>(false);
//		Bukkit.getScheduler().runTaskLater(FactionVillagers.getCurrentPlugin(), new Runnable(){
//			@Override
//			public void run() {
//				done.t = true;
//			}
//		}, ticks);
//		while (!done.t)
//		{
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	
	
	

	
	
	
	
	
	
	/**
	 *  set storageRoom and storageLocation depending on whether we could find the preferred storage
	 */
	private void setStorageFound() {
		
		if (canFindStorageRoom)
			setStoragePreferred();
		else
		{
			storageRoom = (BuildingWithStorage) homeBuilding;
			storageLocation = homeBuilding.homeLocationRequirement.getHomeLocation();// might be different from this.homeLocation! > Unemployed
		}
	}
	
	/**
	 * set storageRoom and storageLocation irrespective of whether we can navigate toward it
	 */
	private void setStoragePreferred() {
		City city = homeBuilding.city;
		if(city.storageRoom != null && city.storageRoom.isActive() )
		{
			storageRoom = city.storageRoom;
			storageLocation = storageRoom.homeLocationRequirement.getHomeLocation();	
			return;
		}
		// otherwise:
			
		storageRoom = (BuildingWithStorage) homeBuilding;
		storageLocation = homeBuilding.homeLocationRequirement.getHomeLocation();// might be different from this.homeLocation! > Unemployed
		
	}
	
	
	public boolean storeItems(BuildingWithStorage building)
	{
		HashMap<Integer, ItemStack> leftOvers = building.addItem(InventoryTraitUtils.getItems(inventory));
		InventoryTraitUtils.clear(inventory);
		InventoryTraitUtils.addItem(inventory, leftOvers.values().toArray(new ItemStack[0]));
		return leftOvers.size() == 0;
	}
	
	
	
	public boolean getConsumption(BuildingWithStorage fromBuilding) {

		for (Production production : productions)
		{
			boolean hasConsumption = true;

			for (ItemStack toBeConsumed : production.getItemsNeededToConsume())
			{
				if (InventoryTraitUtils.containsAtLeast(inventory, toBeConsumed.getType(), toBeConsumed.getAmount()))
					continue;
				boolean hasThis = fromBuilding.containsAtLeast(toBeConsumed, toBeConsumed.getAmount());
				hasConsumption = hasConsumption && hasThis;
			}
			if (hasConsumption)
			{
				for (ItemStack itemNeeded : production.getItemsNeededToConsume())
				{
					if (InventoryTraitUtils.containsAtLeast(inventory, itemNeeded.getType(), itemNeeded.getAmount()))
						continue;
					Tuple<List<ItemStack>, List<ItemStack>> gottenNunremoved = fromBuilding.getItem(itemNeeded);
					if (!gottenNunremoved.snd.isEmpty())
					{
						Debug.err("couldn't remove items which were verified to be in the BuildingWithStorage! unremoved:");
						for (ItemStack is : gottenNunremoved.snd)
							Debug.out(is.getAmount()+" "+is.getType());
					}
					InventoryTraitUtils.addItem(inventory, gottenNunremoved.fst.toArray(new ItemStack[0]));
				}
				selectedProduction = production;
				return true;
			}
		}
		return productions.isEmpty();
	}
	
	public boolean consume() {
		boolean hasConsumption = true;
//		Debug.out("selectedProduction: "+selectedProduction.toString());
//		Debug.out("getItemsNeededToConsume: "+selectedProduction.getItemsNeededToConsume().toString());
		
		if(selectedProduction != null)
		{
			for (ItemStack toBeConsumed : selectedProduction.getItemsNeededToConsume())
				hasConsumption = hasConsumption && InventoryTraitUtils.containsAtLeast(inventory, toBeConsumed.getType(), toBeConsumed.getAmount());
			
			if (hasConsumption)
			{
				for (ItemStack consumed : selectedProduction.getItemsNeededToConsume()) // register consumption
					homeBuilding.city.statistics.consume(homeBuilding, consumed.getType(), consumed.getAmount());
				Tuple<List<ItemStack>, List<ItemStack>> couldntRemove = InventoryTraitUtils.getItem(inventory, selectedProduction.getItemsNeededToConsume());
				if (!couldntRemove.snd.isEmpty()) // TODO: remove this
					Debug.warn("couldnt remove consumption from own inv eventhough he checked!");
			}
		}
		
		return hasConsumption;
	}
	
	private boolean tryToEatDirectlyFromStorage(BuildingWithStorage fromBuilding) {
		
		// items needed depending on how many days we haven't eaten
		ItemStack[] itemsNeededToEatNow = new ItemStack[itemsNeededToEat.length];
		for (int i = 0; i< itemsNeededToEat.length; i++)
		{
			itemsNeededToEatNow[i] = itemsNeededToEat[i].clone();
			itemsNeededToEatNow[i].setAmount(itemsNeededToEat[i].getAmount()*(1+getNumberOfTimesNotEaten()));
		}
		
		
		boolean hasFood = true;
		for (ItemStack toBeEaten : itemsNeededToEatNow)
		{
			if (InventoryTraitUtils.containsAtLeast(inventory, toBeEaten.getType(), toBeEaten.getAmount()))
				continue;
			boolean hasThis = fromBuilding.containsAtLeast(toBeEaten, toBeEaten.getAmount());
			hasFood = hasFood && hasThis;
		}
		if (hasFood)
		{
			setNumberOfTimesNotEaten(0);
			for (ItemStack itemNeeded : itemsNeededToEatNow)
			{
				if (InventoryTraitUtils.containsAtLeast(inventory, itemNeeded.getType(), itemNeeded.getAmount()))
					continue;
//				Tuple<List<ItemStack>, List<ItemStack>> gottenNunremoved = 
				fromBuilding.getItem(itemNeeded);
			}
			
			return true;
		}
		
		return false;
	}
	private double tryToEatHappyMeal(BuildingWithStorage fromBuilding) {
		double happiness = 0;
		for (HappinessIfBlockPresent hr : tempHappinessFromResouce)
		{
			if (fromBuilding.containsAtLeast(new ItemStack(hr.material, 1), 1))
			{
				fromBuilding.getItem(new ItemStack(hr.material, 1));
				happiness += hr.happiness;
			}
		}
		
		return happiness;
	}

	void produce() {
		if(selectedProduction != null)
		{
			for (ItemStack produced : selectedProduction.getItemsProduced())
			{
				InventoryTraitUtils.add(inventory, produced);
				// register production:
				homeBuilding.city.statistics.produce(homeBuilding, produced.getType(), produced.getAmount());
			}
		}
	}
	
	
	public void showSearchSpaceToUser(final Player player) {
		playersToShowSearchSpace.add(player);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				playersToShowSearchSpace.remove(player);
			}
		}.runTaskLater(plugin, 1000);
	}

	@Override
	public void destroy() {
		cancelCurrentJob();
	}

	public abstract CharacterType getCharacterType();



}
