package buildings;

import generics.Tuple;
import io.JsonBlock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import utils.ChestUtils;
import utils.PlayerUtils;
import characters.Character;
import characters.CharacterType;
import characters.InventoryTraitUtils;
import characters.StorageKeeper;
import city.City;

public class StorageRoom extends BuildingWithStorage {

	static final String configYmlPath = configYmlPath_building+".storageRoom";
	@Override
	String getConfigYmlPath() {
		return configYmlPath;
	}	
	
	@Override
	public void addTraits(NPC npc) {
//		super.addTraits(npc);
		Character trait = new StorageKeeper();
		trait.construct(this, homeLocationRequirement.getHomeLocation());
		npc.addTrait(trait);
		return;
	}
	@Override
	EntityType getNPCEntityType() {
		return EntityType.VILLAGER;
	}
	
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.STORAGE_KEEPER;
	}
	
	public HashSet<Chest> chests = new HashSet<Chest>();
	
	@Override public String getDebugInfo() {
		String ret = super.getDebugInfo() +
						"# chests="+this.chests.size()+"\n";
		return ret;
	}
	public StorageRoom(City city, Block b) {
		super(city, b);
//		requirements.add(BuildingRequirement.minAirSpaceSize(12));
//		requirements.add(BuildingRequirement.maxAirSpaceSize(1000));
		requirements.add(BuildingRequirement.canOnlyBeBuiltOnce);
		requirements.add(BuildingRequirement.hasEnoughMaterial(Material.CHEST, 4));
	}

	public static void main(String[] args) {
		
	}
	
	@Override
	public boolean isUnique()
	{
		return true;
	}
	
	@Override
	public String getBuildingMessage() //Return an empty string
	{
		return "<i>From now on you should put food in the Storage Room chests instead of the Town Hall chest so all your villagers can find it!";
	}

	@Override
	public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
		city.storageRoom = this;
		
		//Location loc = startingBlock.getLocation();
		//startingBlock = city.world.getBlockAt(loc);
		startingBlock.setType(Material.WORKBENCH);
		ChestUtils.setDirection(startingBlock, PlayerUtils.getCardinalDirection4(event.getPlayer()).getOppositeFace());
		MetaDataUtils.setBelongingTo(startingBlock, this);
		
		setBelongingBlocks();
		
		//Give statistics book
		event.getPlayer().getInventory().addItem(city.getStatisticsBook());
	}
	@Override
	public void setBelongingBlocks() {
		chests = new HashSet<Chest>();
		for (Block borderBlock : airSpaceChecker.borderBlocks)
			if (borderBlock.getType() == Material.CHEST)
			{
//				MetaDataUtils.setBelongingTo(borderBlock, this);
				chests.add((Chest) borderBlock.getState());
			}
	}
	@Override
	public void destroy() {
		super.destroy();
		city.storageRoom = null;
		MetaDataUtils.removeBelongingTo(startingBlock, this);
		
//		for (Chest chest : chests)
//			MetaDataUtils.removeBelongingTo(chest.getBlock());
	}

	@Override
	public boolean containsAtLeast(ItemStack item, int amountNeeded) {
		int amountFound = 0;
		for (Chest chest : chests)
		{
			if (chest.getBlock().getType() == Material.CHEST)
			{
				{
					Collection<? extends ItemStack> allItemStacksOfType = chest.getBlockInventory().all(item.getType()).values();
					for (ItemStack stack : allItemStacksOfType )
						amountFound += stack.getAmount();
				}
				if (item.getType() == Material.LOG)
				{
					Collection<? extends ItemStack> allItemStacksOfType = chest.getBlockInventory().all(Material.LOG_2).values();
					for (ItemStack stack : allItemStacksOfType )
						amountFound += stack.getAmount();
				}
				if (amountFound >= amountNeeded)
					return true;
			}
		}
		// otherwise
		return false;
	}
	
	public HashMap<Material, Integer> getContents() {
		HashMap<Material, Integer> contents = new HashMap<Material, Integer>();
		for (ItemStack is : getContentsRaw())
		{
			Material mat = null;
			int amount = 1;
			if (is != null)
			{
				mat = is.getType();
				amount = is.getAmount();
			}
			
			Integer amountAccountedFor = contents.get(mat);
			contents.put(mat, ((amountAccountedFor==null)? 0 : amountAccountedFor) + amount);
						
		}
		return contents;
	}
	public List<ItemStack> getContentsRaw() {
		ArrayList<ItemStack> contents = new ArrayList<ItemStack>(40);
		for (Chest chest : chests)
		{
			if (chest.getBlock().getType() == Material.CHEST)
			{
				ItemStack[] contentsHere = chest.getBlockInventory().getContents();
				contents.addAll(Arrays.asList(contentsHere));
			}
		}
		return contents;
	}
	
	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
		HashMap<Integer, ItemStack> itemsToBeProcessed = new HashMap<Integer, ItemStack>(); 
		for (int i = 0; i<items.length; i++)
			itemsToBeProcessed.put(i, items[i].clone());
		for (Chest chest : chests)
		{
			if (chest.getBlock().getType() == Material.CHEST)
				itemsToBeProcessed = chest.getBlockInventory().addItem(itemsToBeProcessed.values().toArray(new ItemStack[0])); 
			
		}
		return itemsToBeProcessed;
	}
	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
		if (items==null) return new HashMap<Integer, ItemStack>();
		HashMap<Integer, ItemStack> itemsToBeProcessed = new HashMap<Integer, ItemStack>(); 
		for (int i = 0; i<items.length; i++)
			itemsToBeProcessed.put(i, items[i].clone());
		for (Chest chest : chests)
		{
			if (chest.getBlock().getType() == Material.CHEST)
				itemsToBeProcessed = chest.getBlockInventory().removeItem(itemsToBeProcessed.values().toArray(new ItemStack[0])); 
			
		}
		return itemsToBeProcessed;
	}
	
	@Override
	public Tuple<List<ItemStack>, List<ItemStack>> getItem(ItemStack... items) {
		if (items==null) return new Tuple<List<ItemStack>, List<ItemStack>>(new LinkedList<ItemStack>(), new LinkedList<ItemStack>());
		List<ItemStack> itemsToBeProcessed = new LinkedList<ItemStack>(); 
		List<ItemStack> itemsGotten = new LinkedList<ItemStack>(); 
		for (int i = 0; i<items.length; i++)
			itemsToBeProcessed.add(items[i].clone());
		
		for (Chest chest : chests)
		{
			if (chest.getBlock().getType() == Material.CHEST)
			{
				Tuple<List<ItemStack>, List<ItemStack>> moved = InventoryTraitUtils.getItem(chest.getBlockInventory().getContents(), itemsToBeProcessed.toArray(new ItemStack[0]));
				itemsToBeProcessed = moved.snd;
				itemsGotten.addAll(moved.fst);
			}
		}
		return new Tuple<List<ItemStack>, List<ItemStack>>(itemsGotten, itemsToBeProcessed);
	}
	

	
	@Override
	public BuildingType getBuildingType() {
		return BuildingType.STORAGE_ROOM;
	}
	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = super.toJsonObjectBuilder();
		
		JsonArrayBuilder chestsJson = Json.createArrayBuilder();
		for (Chest chest : chests)
			chestsJson.add(JsonBlock.toJsonObjectBuilder(chest.getBlock()));
		
		ret.add("chests", chestsJson );
		return ret;
	}
	@Override
	public Building fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
        this.loadJsonObjectFields(o);
		JsonArray chestsJson = o.getJsonArray("chests");
		for (int i =0; i<chestsJson.size(); i++)
		{
			BlockState chestMaybe = JsonBlock.fromJsonObject(chestsJson.getJsonObject(i)).getState();
			if (chestMaybe instanceof Chest)
				chests.add((Chest) chestMaybe);
		}
		return this;
	}


}
