package buildings;

import factions.FactionUtils;
import io.JsonAble;
import io.JsonBlock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import main.Debug;
import main.Debug.Debuggable;
import main.MCity;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import utils.ClosedAirSpaceChecker;
import utils.PlayerUtils;
import utils.StringUtilsTK;
import buildings.BuildingRequirement.ConnectedToTownHallViaPath;
import buildings.BuildingRequirement.HomeLocationRequirement;
import buildings.BuildingRequirement.VillagerPoolSizeRequirement;
import characters.CharacterType;
import city.City;

import com.massivecraft.massivecore.util.Txt;

public abstract class Building implements JsonAble<Building> , Debuggable{
	static final String configYmlPath_building = "building";
	static final FileConfiguration configYml = MCity.getCurrentPlugin().getConfig();
	static final String activeString = "Active";
	static final String joblessString = "UNEMPLOYED";
	static final String inactiveString = "INACTIVE";
	
	public long lastFoodTime;
	public long timeUntilDead = 1440000; // 24000 = day = 20 real minutes		 720000 = one month = 10 real hours

	boolean isDestroyed = false;
	public boolean isDestroyed() {
		return isDestroyed;
	}

	
	public City city;
	
	VillagerPoolSizeRequirement villagerPoolSizeRequirement;
	
	public int buildingId;

	
	public Block startingBlock;
	
	public abstract BuildingType getBuildingType();
	
	public List<BuildingRequirement> requirements = new ArrayList<BuildingRequirement>();// TODO make this static?
	public HomeLocationRequirement homeLocationRequirement = new HomeLocationRequirement();
	public ConnectedToTownHallViaPath connectedToTownHallViaPathRequirement = new ConnectedToTownHallViaPath();
	
	
	public int airSpaceSize;
	public ClosedAirSpaceChecker airSpaceChecker;
	
	
	public boolean isActive() {
		return hasRequirements;
	}

	private boolean hasRequirements = true;
	
	
	/**
	 * @return the hasRequirements
	 */
	public boolean isHasRequirements() {
		return hasRequirements;
	}

	/**
	 * @param hasRequirements the hasRequirements to set
	 */
	public void setHasRequirements(boolean hasRequirements) {
		this.hasRequirements = hasRequirements;
		updateSign();
	}

	public void updateSign() {
		Sign sign = getSign();
		if(sign != null)
		{
			String buildingString = this.toString();
			if (buildingString.length() > 16)
			{
				LinkedList<String> lines = StringUtilsTK.toLinesWithoutWordBreak(buildingString, 15);
				for (int l = 0; l<lines.size() && l<3; l++)
					sign.setLine(l, lines.get(l));
			} else
				sign.setLine(1, buildingString );
			

			if(hasRequirements)
				sign.setLine(3, activeString);
			else
				sign.setLine(3, inactiveString);
			
			sign.update();
		}
	}
	
	@Override public String getDebugInfo() {
		String ret = 
//						"npcId="+npcId+"\n"+
						"startingBlock="+this.startingBlock.getLocation()+"\n"+
						"hasRequirements="+this.hasRequirements+"\n"+
						"airSpaceSize="+this.airSpaceSize+"\n"+
						"buildingId="+this.buildingId+"\n"+
						"city="+this.city.getFaction().getName()+"\n"+
						"hasRequirements="+this.hasRequirements+"\n";
		return ret;
	}

	public Building(City city, Block startingBlock) {
		this.city = city;
		this.startingBlock = startingBlock;
		requirements.add(BuildingRequirement.hasFreeStartingBlock);
		requirements.add(BuildingRequirement.hasAirOrSignAboveStartingBlock);
		requirements.add(BuildingRequirement.isOnlyBuildingInAirSpace); // default for all buildings!
		requirements.add(BuildingRequirement.isOnFactionLand(city.getFaction())); // default for all buildings!
		
		requirements.add(BuildingRequirement.minAirSpaceSize(configYml.getInt(getConfigYmlPath()+".minAirSpace")));
		requirements.add(BuildingRequirement.maxAirSpaceSize(configYml.getInt(getConfigYmlPath()+".maxAirSpace")));

		requirements.add(BuildingRequirement.hasDoor()); // default for all buildings!
		requirements.add(homeLocationRequirement);
		requirements.add(connectedToTownHallViaPathRequirement);
		
		if (!(this instanceof TownHall))
		{
			villagerPoolSizeRequirement = new VillagerPoolSizeRequirement(city.getTownHallPool(), getNumberOfVillagersNeeded());
			requirements.add(villagerPoolSizeRequirement);
		}
		if (!(this instanceof TownHall || this instanceof StorageRoom))
		{
			requirements.add(BuildingRequirement.cityHasStorageRoom);
		}
		for (Object mat : configYml.getList(getConfigYmlPath()+".materialrequirements"))
		{
			requirements.add(BuildingRequirement.hasMaterial(Material.valueOf((String) mat)));
		}
		

	}

	public abstract int getNumberOfVillagersNeeded();

	abstract String getConfigYmlPath();

	public List<String> checkValidityOfBuildingPlace(boolean beforeBuilt) {
		airSpaceChecker = new ClosedAirSpaceChecker(startingBlock.getWorld());
		airSpaceSize = airSpaceChecker.sizeAirSpace(startingBlock);
		
		
		List<String> errors = new LinkedList<String>();
//		if (!checkBorderBlocks(airSpaceChecker.borderBlocks))
//			errors.add("border blocks are not OK, man!");
		for (BuildingRequirement req : requirements)
			if (!req.isMetBy(this, beforeBuilt))
				errors.add(getBuildingType().prettyPrint()+req.description+"!");
		return errors;
	}

//	abstract boolean checkBorderBlocks(HashSet<Block> borderBlocks);
	
	public void create(City city, PlayerInteractEvent event){
		lastFoodTime = event.getPlayer().getWorld().getFullTime();

		LinkedList<Integer> allIds = new LinkedList<Integer>(); 
		for (Building otherBuilding : city.buildings.get(getBuildingType()))
			allIds.add(otherBuilding.buildingId);
		int id = 0;
		boolean unique = false;
		while (!unique)
		{
			id++;
			unique = !allIds.contains(id);
		}
		buildingId = id;

		
		city.buildings.get(getBuildingType()).add(this);
		
		makeSignIfPossible(PlayerUtils.getCardinalDirection4(event.getPlayer()));
		
		for (Block block : airSpaceChecker.borderBlocks)
			MetaDataUtils.setBelongingTo(block, this);
		
		
	}
	
	public void makeSignIfPossible(BlockFace preferredDirection)
	{
		Block blockAboveStartingBlock = startingBlock.getRelative(BlockFace.UP);
		if(blockAboveStartingBlock.getType() == Material.AIR)
		{
			BlockFace directionToShow = null;
			
			if(blockAboveStartingBlock.getRelative(preferredDirection).getType().isSolid())
				directionToShow = preferredDirection;
			
			org.bukkit.material.Sign matSign;

			if(directionToShow != null) //There is a solid block around the sign block
			{
				blockAboveStartingBlock.setType(Material.WALL_SIGN);
				matSign = new org.bukkit.material.Sign(Material.WALL_SIGN);
			}
			else //There is only air around the sign block
			{
				blockAboveStartingBlock.setType(Material.SIGN_POST);
				matSign = new org.bukkit.material.Sign(Material.SIGN_POST);
			}
			
			matSign.setFacingDirection(preferredDirection.getOppositeFace());
			Sign sign = (Sign) blockAboveStartingBlock.getState();
			sign.setData(matSign);
			sign.update();
			
			MetaDataUtils.setBelongingTo(blockAboveStartingBlock, this);
			
		}
		
		Sign sign = this.getSign();
		if(sign != null)
		{
			sign.setLine(1, this.toString());
			sign.setLine(3, activeString);
			sign.update();
		}
	}
	
	public Sign getSign()
	{
		Block blockAboveStartingBlock = startingBlock.getRelative(BlockFace.UP);
		if(blockAboveStartingBlock.getType() == Material.WALL_SIGN || blockAboveStartingBlock.getType() == Material.SIGN_POST)
			return (Sign) blockAboveStartingBlock.getState();
		else
			return null;
	}

	public abstract void setBelongingBlocks();
	
	public void destroy() {
		isDestroyed = true;
		if (this instanceof StorageRoom)
			city.storageRoom = null;

		for (Block block : airSpaceChecker.borderBlocks)
			MetaDataUtils.removeBelongingTo(block, this);

		city.buildings.get(getBuildingType()).remove(this);
		// TODO ?! (something needs doing? zandzak! lobtard!)
		
		Sign sign = getSign();
		if(sign != null)
			sign.getBlock().setType(Material.AIR);
		
	}


	/**
	 * @param playerToShowErrors or null ==> show the message to the whole faction
	 * @param showStayInvalid
	 * @param showBecomeValid
	 * @return whether the building is active now
	 */
	public boolean recheckAndShowMessage(Player playerToShowErrors, boolean showStayInvalid, boolean showBecomeValid) {
		boolean buildingwWasActive = isActive();
		List<String> errors = recheck();
		
		
		boolean b = buildingwWasActive; // was active Before
		boolean a = errors.size() == 0; // is active  After
		boolean showMsg = false;
		if (b && !a)
		{
			FactionUtils.sendMessage(city.getFaction(), prettyPrintBuildingErrorState(errors, buildingwWasActive));
			return false;
		}
		
		
		if (!b && !a)
			showMsg = showStayInvalid;
		if (!b && a)
			showMsg = showBecomeValid;

		if (showMsg)
		{
			if (playerToShowErrors == null)
				FactionUtils.sendMessage(city.getFaction(), prettyPrintBuildingErrorState(errors, buildingwWasActive));
			else 
				playerToShowErrors.sendMessage(prettyPrintBuildingErrorState(errors, buildingwWasActive));
			return false;
		}
		else 
			return true;
	}
	
	public List<String> recheck() {
		for (Block block : airSpaceChecker.borderBlocks)
			MetaDataUtils.removeBelongingTo(block, this);
		List<String> errors = checkValidityOfBuildingPlace(false);
		setHasRequirements(errors.size()==0); 
		setBelongingBlocks();
		
		if (startingBlock.getType() == Material.AIR)
		{
			destroy();
			return Arrays.asList(new String[]{" doesn't have its starting block! Destroyed building"});
		}
		else
		{
			for (Block block : airSpaceChecker.borderBlocks)
				MetaDataUtils.setBelongingTo(block, this);
		}
		return (errors);
	}
	public String prettyPrintBuildingErrorState(List<String> errors, boolean buildingWasActive) {
		if (errors.size() > 0)
			return Txt.parse("<bad>"
					+this.toString()+
					((buildingWasActive)? " became " : " is ")
					+"inactive! \n - "+StringUtils.join(errors, "\n - "));
		else 
			return Txt.parse("<good>"
					+this.toString()+
					((buildingWasActive)? " is " : " became ")
					+"active!");
	}
	
	public static JsonAble<Building> newBuilding(BuildingType buildingType, City city2) {
		try {
			return buildingType.buildingClass.getConstructor(City.class, Block.class).newInstance(city2, null);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException 
				| NoSuchMethodException | SecurityException e) {
			Debug.out("ERROR: "+buildingType+" doesn't have a constructor with arguments (city, startingBlock)??");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}
		return null;
	}
	
	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = Json.createObjectBuilder();
		ret.add("hasRequirements" , hasRequirements);
		ret.add("buildingId" , buildingId);
		ret.add("startingBlock", JsonBlock.toJsonObjectBuilder(startingBlock) );
		ret.add("lastFoodTime", lastFoodTime);

		return ret;
	}

	public void loadJsonObjectFields(JsonObject o) {
		hasRequirements = o.getBoolean("hasRequirements");
		buildingId = o.getInt("buildingId");
		startingBlock = JsonBlock.fromJsonObject(o.getJsonObject("startingBlock"));
		lastFoodTime = o.getInt("lastFoodTime", (int) MCity.defaultWorld.getFullTime());

		checkValidityOfBuildingPlace(false);
	}
	public String getFullyQualifiedId() {
		return city.getFaction().getId()+"."+getBuildingType().toString().toLowerCase()+"."+buildingId;
	}
	
	public String prettyPrintFullyQualifiedBuildingPath() {
		return city.getFaction().getName()+"'s "+toString();
	}

	public String toString() {
		return getBuildingType().prettyPrint()
				+((isUnique())? "" :" "+buildingId);
	}
	
	public abstract CharacterType getCharacterType();
	
	public String getCharacterNamePretty() 
	{
		return getCharacterType().prettyPrint() + ((this.isUnique()) ? "" : " "+this.buildingId);
	}
	
	public boolean isUnique()
	{
		return false;
	}
	
	public String toStringColored() {
		return Txt.parse( 
				(isActive()? "<good>" : "<bad>") + toString() + "<reset>");
	}
	/**
	 * !! may return null !!
	 * @param bId
	 * @return null if the block has wrong meta-data
	 */
	public static Building parseBuildingId(String bId) {
		String[] ids = bId.split("\\.");
		if (ids.length==3)
		{
			String facID = ids[0];
			BuildingType type = BuildingType.valueOf(ids[1].toUpperCase());
			int id = Integer.parseInt(ids[2]);
			
			City city = FactionUtils.factionIDToCity.get(facID);
			if (city!=null)
				for (Building building : city.buildings.get(type))
					if (building.buildingId == id)
						return building;
		}
		return null;
	}

	public String getBuildingMessage() //Return an empty string
	{
		return "";
	}




}
