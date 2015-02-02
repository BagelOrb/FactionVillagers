package city;

import factions.FactionUtils;
import interaction.StatisticsBookListener;
import io.JsonAble;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import main.Debug;
import main.FactionVillagers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import utils.SetUtils;
import buildings.Building;
import buildings.BuildingType;
import buildings.BuildingWithStorage;
import buildings.Mine;
import buildings.StorageRoom;
import buildings.TownHall;
import characters.ChestCharacter;
import characters.InventoryTraitUtils;
import characters.Unemployed;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.util.Txt;

public class City implements JsonAble<City> {

	Random random = new Random();
	
	public String factionID;
	
	public Faction getFaction() {
		//TODO: Multiverse code?
		return FactionColl.get().get(factionID);
	}
	
	public HashMap<BuildingType, LinkedList<Building>> buildings = new HashMap<BuildingType, LinkedList<Building>>();
	public LinkedList<Building> getAllBuildings() {
		LinkedList<Building> ret = new LinkedList<Building>();
		for (LinkedList<Building> buildingsOfOneType : buildings.values())
			ret.addAll(buildingsOfOneType);
		return ret;		
	}
	
	public TownHall townHall;
	public StorageRoom storageRoom = null;
	public Mine mine = null;
	
	public CityStatistics statistics; // TODO: persist!

	private static final long timeBetweenImmigrantChecks = FactionVillagers.getCurrentPlugin().getConfig().getLong("immigration.timeBetweenImmigrantChecks");

	
	public ItemStack getStatisticsBook() {
		ItemStack statisticsBook = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta meta = (BookMeta) statisticsBook.getItemMeta();
		meta.setTitle("Statistics");
		meta.setDisplayName("Statistics");
		meta.setAuthor(this.getFaction().getName());
		meta.setLore(Arrays.asList("Shows your faction's statistics."));
		statisticsBook.setItemMeta(meta);
		return statisticsBook;
	}
	
	@Deprecated public City() { // only used in reflection to get an object of this type! no 
		checkImmigrants.runTaskTimer(FactionVillagers.getCurrentPlugin(), random.nextInt(500)+20, timeBetweenImmigrantChecks );
	}
	public City(World w, String facID) {
		factionID = facID;
		for (BuildingType type : BuildingType.values())
			buildings.put(type, new LinkedList<Building>());
		
		checkImmigrants.runTaskTimer(FactionVillagers.getCurrentPlugin(), random.nextInt(500)+20, timeBetweenImmigrantChecks );
	}
	
	private BukkitRunnable checkImmigrants = new BukkitRunnable() {

		@Override
		public void run() {
			
			Immigration.immigrantsCheck(City.this);
			
		}};

		
	public ItemStack[] getTotalConsumption() {
		ItemStack[] ret = new ItemStack[20];
		
		for (Building b : getAllBuildings())
		{
			if (b instanceof TownHall)  // handle unemployed pool only!
			{
				TownHall townhall = (TownHall) b;
				
				for (Unemployed unemployed : townhall.getPool())
				{
					HashMap<Integer, ItemStack> leftOvers = InventoryTraitUtils.addItem(ret, unemployed.itemsNeededToEat);
					if (!leftOvers.isEmpty())
						Debug.warn("Consuming more than 20 types in total?!");
				}
			} // no [else]!
			if (b instanceof BuildingWithStorage) // handle mayor as well!
			{
				BuildingWithStorage npcBuilding = (BuildingWithStorage) b;
				ChestCharacter chestChar = npcBuilding.getChestCharacter();
				if (chestChar != null)
				{
					ItemStack[] itemsNeededToEat = chestChar.itemsNeededToEat;
					HashMap<Integer, ItemStack> leftOvers = InventoryTraitUtils.addItem(ret, itemsNeededToEat);
					if (!leftOvers.isEmpty())
						Debug.warn("Consuming more than 20 types in total?!");
				}
			}
		}
		
		return ret;
	}
		
		
		
		
		
		
		
		
	public void create() {
		FactionVillagers.allCities.add(this);
		FactionUtils.factionIDToCity.put(factionID, this);
		statistics = new CityStatistics(this);
	}
	public void destroy() {
		for (Building b : getAllBuildings())
			b.destroy();
		FactionVillagers.allCities.remove(this);
		new File("saves\\"+factionID).delete();
	}

	public void messageAllCitizens(String msg) {
		// TODO: only message players of this city
		for (Player player :  getFaction().getOnlinePlayers())//Bukkit.getServer().getOnlinePlayers()) // faction.getOnlinePlayers()
			player.sendMessage(msg);
		
		
	}
	
	public String toString() {
		return factionID; // TODO
	}
	
	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonArrayBuilder buildingsArrayBuilder = Json.createArrayBuilder();
		for (Entry<BuildingType, LinkedList<Building>> entry : buildings.entrySet()) 
		{
			JsonArrayBuilder valueArrayBuilder = Json.createArrayBuilder();
			for (Building b : entry.getValue())
				valueArrayBuilder.add(b.toJsonObjectBuilder());
			buildingsArrayBuilder
					.add(Json.createObjectBuilder()
							.add("key", entry.getKey().name())
							.add("value", valueArrayBuilder) );
		}
		
		
		if (factionID == null) factionID = "error saving factionID: factionID=null!!!";
		JsonObjectBuilder ret = Json.createObjectBuilder()
				.add("factionID" , factionID)
				.add("buildings", buildingsArrayBuilder)
				.add("statistics", statistics.toJsonObjectBuilder());
		return ret;
	}

	@Override
	public City fromJsonObject(JsonObject o) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		
		String facID = o.getString("factionID");
		if (facID==null) Debug.err(">>> facID = null!!!");
		if (facID=="null") Debug.err(">>> facID = \"null\"!!!");
		
		factionID = facID;
		for (BuildingType type : BuildingType.values())
			buildings.put(type, new LinkedList<Building>());
		
		JsonArray ba = o.getJsonArray("buildings");
		for (JsonValue jsonBuildingValue : ba)
		{
			JsonObject kv = (JsonObject) jsonBuildingValue;
			BuildingType buildingType = BuildingType.valueOf(kv.getString("key"));
			LinkedList<Building> buildingList = this.buildings.get(buildingType);
			JsonArray jsonBuildingArray = kv.getJsonArray("value");
			for (JsonValue jsonBuildingVal : jsonBuildingArray)
			{
				try {
					JsonObject jsonBuilding = (JsonObject) jsonBuildingVal;
					Building building = Building.newBuilding(buildingType, this).fromJsonObject(jsonBuilding);
					buildingList.add(building);
				}catch (Exception e) {
					Debug.warn("Couldn't parse building!");
				}
			}
		}
		LinkedList<Building> storageRooms = this.buildings.get(BuildingType.STORAGE_ROOM);
		if (storageRooms.size()>0)
			this.storageRoom = (StorageRoom) storageRooms.getFirst();
		
		LinkedList<Building> townHalls = this.buildings.get(BuildingType.TOWN_HALL);
		if (townHalls.size()>0)
			this.townHall = (TownHall) townHalls.getFirst();

		LinkedList<Building> mines = this.buildings.get(BuildingType.MINE);
		if (mines.size()>0)
			this.mine = (Mine) mines.getFirst();
		

		FactionUtils.factionIDToCity.put(this.factionID, this);

		try {
			JsonObject statisticsJsonObject = o.getJsonObject("statistics");
			statistics = new CityStatistics(this).fromJsonObject(statisticsJsonObject);
		} catch (Exception e) {
			Debug.warn("Couldn't load city statistics from "+ this);
			statistics = new CityStatistics(this);
		}
		
		return this;
	}
	
	private static final Comparator<Entry<BuildingType, ?>> compareByKeyAlphabetically = new Comparator<Entry<BuildingType, ?>>(){
		@Override
		public int compare(Entry<BuildingType, ?> o1, Entry<BuildingType, ?> o2) {
			return o1.getKey().toString().compareTo(o2.getKey().toString());
		}};

	public String getColoredBuildingsList(String resetColor) {
		String ret = "";
		ret += "<gold>Building overview\n" + StatisticsBookListener.delimiter;
		
//		ret += "Buildings: \n";
		
		for (Entry<BuildingType, LinkedList<Building>> entry : SetUtils.asSortedList(buildings.entrySet(), compareByKeyAlphabetically))
			if (!entry.getValue().isEmpty())
			{
				ret += resetColor + entry.getKey().prettyPrint() +": ";
				LinkedList<Building> oneTypeBuildings = entry.getValue();
				List<String> buildingStrings = new ArrayList<String>(oneTypeBuildings.size());
				for (Building building : oneTypeBuildings)
					buildingStrings.add( Txt.parse( 
					((building.isActive())? "<good>" : "<bad>") + ((building.isUnique())? "V": building.buildingId) + resetColor));
				
				ret += StringUtils.join(buildingStrings,", ")+"\n";
			}
		
				
		return Txt.parse(ret);
	}
	public List<String> recheck() {
		List<String> errorsPerBuilding = new LinkedList<String>();
		for (Building building : getAllBuildings())
		{
			List<String> errors = building.recheck();
			if (errors.size()>0)
			{
				String errorsStr = building.toString()+" contained errors: \n"+StringUtils.join(errors, ",\n");
				errorsPerBuilding.add(errorsStr);
			}
		}
		return errorsPerBuilding;
	}

	public List<Unemployed> getTownHallPool() {
		if (townHall == null)
			return null;
		return townHall.getPool();
	}

	
	private static double overcrowdedBasePower = FactionVillagers.getCurrentPlugin().getConfig().getDouble("happiness.overcrowdedBasePower");
	private static double happinessFromEachUnemployed = FactionVillagers.getCurrentPlugin().getConfig().getDouble("happiness.happinessFromEachUnemployed");

	public double getNpcHappinessEnhancement() {
		int numberOfEmployeds = 0;
		for (Building b : getAllBuildings())
		{
			numberOfEmployeds += b.getNumberOfVillagersNeeded();
		}
		double crowdedness = Math.pow(overcrowdedBasePower, numberOfEmployeds) -1;
		
		double laziness = townHall.pool.size() * happinessFromEachUnemployed;
		return laziness-crowdedness;
	}
}
