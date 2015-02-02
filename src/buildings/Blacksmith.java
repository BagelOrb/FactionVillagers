package buildings;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;

import javax.json.JsonObject;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import characters.CharacterType;
import city.City;

public class Blacksmith extends BuildingWithTradeVillager {

	static final String configYmlPath = configYmlPath_building+".blacksmith";
	public Blacksmith(City city, Block startingBlock) {
		super(city, startingBlock);
	}

	@Override
	Material getTraderJobBlockRequirement() {
		return Material.ANVIL;
	}
	
	@Override
	EntityType getNPCEntityType() {
		return EntityType.VILLAGER;
	}
	
	@Override public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
	}

	@Override
	public Collection<Trade> getTrades() {
		Collection<Trade> recipes = new LinkedList<Trade>();
		
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_SWORD, 1), new ItemStack(Material.DIAMOND, 1), new ItemStack(Material.STICK, 1)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_PICKAXE, 1), new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.STICK, 1)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_AXE, 1), new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.STICK, 1)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_SPADE, 1), new ItemStack(Material.DIAMOND, 1), new ItemStack(Material.STICK, 1)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_HOE, 1), new ItemStack(Material.DIAMOND, 1), new ItemStack(Material.STICK, 1)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_HELMET, 1), new ItemStack(Material.DIAMOND, 4)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), new ItemStack(Material.DIAMOND, 7)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_LEGGINGS, 1), new ItemStack(Material.DIAMOND, 6)));
		recipes.add(new Trade(new ItemStack(Material.DIAMOND_BOOTS, 1), new ItemStack(Material.DIAMOND, 3)));
			
		return recipes;
	}

	@Override
	public CharacterType getTraderCharacterType() {
		return CharacterType.BLACKSMITH;
	}
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.BLACKSMITH_HELPER;
	}

	@Override
	public BuildingType getBuildingType() {
		return BuildingType.BLACKSMITH;
	}

	@Override
	String getConfigYmlPath() {
		return configYmlPath;
	}

	@Override
	public void setBelongingBlocks() {
		
	}

	@Override
	public Building fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
        this.loadJsonObjectFields(o);
		return this;
	}

}
