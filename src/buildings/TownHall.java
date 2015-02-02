package buildings;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import main.Debug;
import main.FactionVillagers;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;

import buildings.BuildingRequirement.HasEnoughMatsWithFreeSpaces;
import characters.Character;
import characters.CharacterType;
import characters.NpcUtils;
import characters.Unemployed;
import city.City;

import com.massivecraft.massivecore.util.Txt;

import factions.FactionUtils;

public class TownHall extends BuildingWithTradeVillager {

	static final String configYmlPath = configYmlPath_building+".townHall";
	private static final int numberOfStartingUnemployeds = FactionVillagers.getCurrentPlugin().getConfig().getInt(configYmlPath+".numberOfStartingUnemployeds");
	private static final boolean showDebugTrades = FactionVillagers.getCurrentPlugin().getConfig().getBoolean(configYmlPath+".showDebugTrades");
//	private static final int numberOfBedBlocks = FactionVillagers.getCurrentPlugin().getConfig().getInt(configYmlPath+".numberOfBedBlocks");
	
	@Override
	public int getNumberOfVillagersNeeded() {
		return 0;
	}
	
	public HasEnoughMatsWithFreeSpaces bedsRequirement = new HasEnoughMatsWithFreeSpaces(numberOfStartingUnemployeds, Material.BED_BLOCK);
	
	
	
	private void reAssignUnemployedsToBeds() {
		LinkedList<Entry<Block, Location>> poolPlaces = getSortedPoolPlaces();
		List<Unemployed> pool = getPool();
		for (int u = 0; u< pool.size(); u++)
		{
			if (u < poolPlaces.size())
				pool.get(u).setHomeLocation(poolPlaces.get(u).getValue());
			else
			{
				FactionUtils.sendMessage(city.getFaction(), Txt.parse("<bad>Unemployed villager left because he had no place to sleep!"));
				Unemployed unemployed = pool.get(u);
				unemployed.destroy();
				unemployed.getNPC().destroy();
			}
			
		}
		
	}
	
	
	
	
	static final Comparator<Map.Entry<Block, Location>> blockLocComparator = new Comparator<Map.Entry<Block, Location>>(){
		@Override
		public int compare(Map.Entry<Block, Location> o1, Map.Entry<Block, Location> o2) {
			int x = Integer.compare(o1.getKey().getX(), o2.getKey().getX());
			if (x!=0) 
				return x;
			int y = Integer.compare(o1.getKey().getY(), o2.getKey().getY());
			if (y!=0) 
				return y;
			int z = Integer.compare(o1.getKey().getZ(), o2.getKey().getZ());
			return z;
		}};
		
	public LinkedList<Entry<Block, Location>> getSortedPoolPlaces() {
		LinkedList<Map.Entry<Block, Location>> sortedPoolPlaces = 
				new LinkedList<Map.Entry<Block, Location>>(bedsRequirement.blocksWithFreeSpace.entrySet());
		Collections.sort(sortedPoolPlaces, blockLocComparator);
		return sortedPoolPlaces;
	}
	public Map.Entry<Block, Location> getFreePoolPlace() {
		if (pool.size() >= bedsRequirement.blocksWithFreeSpace.size())
			return null;
		
		return getSortedPoolPlaces().get(pool.size());
		
	}
	
	public List<Integer> pool = new LinkedList<Integer>();


	
	
	public List<Unemployed> getPool() {
		List<Unemployed> ret = new LinkedList<Unemployed>();
		for (int npcId : pool)
			try {
				NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
				if (npc==null)
					Debug.warn("Couldn't get unemployed villager by id!");
				else
					ret.add((Unemployed) NpcUtils.getCharacter(npc));
			} catch (Exception e) {
				Debug.out("Couldn't get unemployed villager by id!");
				e.printStackTrace();
			}
		return ret;
	}
	
	@Override
	Material getTraderJobBlockRequirement() {
		return Material.WORKBENCH;
	}
	
	public TownHall(City city, Block startingBlock) {
		super(city, startingBlock);
		//requirements.add(BuildingRequirement.hasMaterial(Material.BOOKSHELF)); Bookshelf way too expensive for starting up
		requirements.add(bedsRequirement);
		requirements.add(BuildingRequirement.canOnlyBeBuiltOnce);
	}
	
	
	@Override 
	public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
//		startingBlock.setType(Material.BOOKSHELF);
//		MetaDataUtils.setBelongingTo(startingBlock, this);
		city.townHall = this;

		

//		int numberOfUnemployedsToSpawn = numberOfStartingUnemployeds;
//		for (Building building : city.getAllBuildings())
//			if (building.getBuildingType() != BuildingType.TOWN_HALL)
//				numberOfUnemployedsToSpawn -= building.getNumberOfVillagersNeeded();
//		for (int i = 0; i<numberOfUnemployedsToSpawn && i< bedsRequirement.blocksWithFreeSpace.size() - pool.size(); i++) // start with 2 unemployed dudes
//		{
//			Entry<Block, Location> freePoolPlace = getFreePoolPlace();
//			createUnemployed(freePoolPlace, freePoolPlace.getValue());
//		}
		
	}
	@Override
	public void destroy() {
		super.destroy();
		city.townHall = null;
		MetaDataUtils.removeBelongingTo(startingBlock, this);
		for (Unemployed character : getPool())
		{
			character.destroy();
			character.getNPC().destroy();
		}
		
		//Drop a mayor paper...
		startingBlock.getWorld().dropItemNaturally(startingBlock.getLocation(), Trade.getHirePaperFor(CharacterType.MAYOR));
	}
	
	@Override
	public boolean isUnique()
	{
		return true;
	}
	
	@Override
	public String getBuildingMessage() //Return an empty string
	{
		return "<i>Put bread in the Town Hall chest to attract new villagers to your city!";
	}
	
	@Override
	public Collection<Trade> getTrades() {
		return Trade.getHireRecipes(showDebugTrades);
	}


//	@Override
//	void createNPC() { 
//	
//	}

	@Override
	public CharacterType getTraderCharacterType() {
		return CharacterType.MAYOR;
	}
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.NONE;
	}

	@Override
	public BuildingType getBuildingType() {
		return BuildingType.TOWN_HALL;
	}

	@Override
	String getConfigYmlPath() {
		return configYmlPath;
	}

	@Override
	public void setBelongingBlocks() {
		
	}

	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = super.toJsonObjectBuilder();
		
		JsonArrayBuilder poolArray = Json.createArrayBuilder();
		for (Unemployed q : getPool())
		{
			poolArray.add(q.getNPC().getId());
		}
		ret.add("unemployedPool", poolArray);
		return ret;

	}
	
	@Override
	public void loadJsonObjectFields(JsonObject o) {
        super.loadJsonObjectFields(o);
		
		JsonArray poolArray = o.getJsonArray("unemployedPool");
		if (poolArray == null)
		{
			Debug.warn("Can't find TownHall villager pool field!!");
			return;
		}
		for (int i = 0; i< poolArray.size(); i++)
		{
			int npcId = poolArray.getInt(i);
			pool.add( npcId);
		}
	}


	@Override
	public Building fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		loadJsonObjectFields(o);
		return this;
	}

	
	
	@Override public List<String> recheck() {
		List<String> errors = super.recheck();

		reAssignUnemployedsToBeds();
		
		for (int i = 0; i< pool.size(); i++)
		{
			npcId = pool.get(i);
			NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
			if (npc != null)
			{
				Character character = NpcUtils.getCharacter(npc);
				if (character instanceof Unemployed)
				{
					Unemployed unemployed = (Unemployed) character;
					if (!(unemployed==null || !unemployed.getNPC().isSpawned() || unemployed.getNPC().getEntity().isDead()))
					{
						continue;
					}
				}
			} // otherwise:
			pool.remove((Integer) npcId);
			
			Entry<Block, Location> freePoolPlace = getFreePoolPlace();
			if (freePoolPlace != null )
				createUnemployed(freePoolPlace, freePoolPlace.getValue());
		}
		
		return errors;
	}

	

	/**
	 * @return whether a new unemployed was created
	 */
	public boolean createUnemployed() {
		Entry<Block, Location> freePoolPlace = getFreePoolPlace();
		if (freePoolPlace != null )
			return createUnemployed(freePoolPlace, freePoolPlace.getValue());
		return false;
	}

	/**
	 * @param spawnLoc 
	 * @return whether a new unemployed was created
	 */
	public boolean createUnemployed(Location spawnLoc) {
		Entry<Block, Location> freePoolPlace = getFreePoolPlace();
		if (freePoolPlace != null )
			return createUnemployed(freePoolPlace, spawnLoc);
		return false;
	}
	public boolean createUnemployed(Entry<Block, Location> entry, Location spawnLoc) {
		Entry<Block, Location> freePoolingPlace = getFreePoolPlace();
		if (freePoolingPlace == null)
			return false;
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, CharacterType.UNEMPLOYED.prettyPrint());
		
		
		Unemployed trait = new Unemployed();
		trait.construct(this, freePoolingPlace.getValue());
		npc.addTrait(trait);
		Inventory invTrait = new Inventory();
		npc.addTrait(invTrait);

		npc.spawn(spawnLoc);
		
//		npc.teleport(homeLocation, TeleportCause.PLUGIN);
		pool.add(npc.getId());
		return true;
	}


}
