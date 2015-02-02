package buildings;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.json.JsonObject;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import characters.CharacterType;
import city.City;

public class Library extends BuildingWithTradeVillager {

	static final String configYmlPath = configYmlPath_building+".library";
	public Library(City city, Block startingBlock) {
		super(city, startingBlock);
		requirements.add(BuildingRequirement.hasEnoughMaterial(Material.BOOKSHELF, 4));
	}

	@Override
	Material getTraderJobBlockRequirement() {
		return Material.BOOKSHELF;
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
		
//		recipes.add(new Trade(new ItemStack(Material.DIAMOND, 1), new ItemStack(Material.ENCHANTED_BOOK, 1)));
		
		recipes.add(new Trade(this.city.getStatisticsBook(), new ItemStack(Material.BOOK, 1)));
		
		recipes.add(new Trade(new ItemStack(Material.BOOK_AND_QUILL, 1), new ItemStack(Material.BOOK, 1), new ItemStack(Material.FEATHER, 1)));
		
		ItemStack enchantedBook = getEnchantedBookWith(Enchantment.PROTECTION_ENVIRONMENTAL, 2, "Environmental Protection", "Blocks tons of damage.", "From "+this.city.getFaction().getName());
		recipes.add(new Trade(enchantedBook, new ItemStack(Material.BOOK, 1), new ItemStack(Material.DIAMOND, 1)));
		
		enchantedBook = getEnchantedBookWith(Enchantment.DAMAGE_ALL, 2, "Sword Sharpener", "Adds tons of damage.", "From "+this.city.getFaction().getName());
		recipes.add(new Trade(enchantedBook, new ItemStack(Material.BOOK, 1), new ItemStack(Material.DIAMOND, 1)));
		
		enchantedBook = getEnchantedBookWith(Enchantment.LOOT_BONUS_MOBS, 3, "Loot Bonus", "Better drops", "From "+this.city.getFaction().getName());
		recipes.add(new Trade(enchantedBook, new ItemStack(Material.BOOK, 1), new ItemStack(Material.DIAMOND, 5)));
		
		enchantedBook = getEnchantedBookWith(Enchantment.SILK_TOUCH, 1, "Silk Touch", "Very silky.", "From "+this.city.getFaction().getName());
		recipes.add(new Trade(enchantedBook, new ItemStack(Material.BOOK, 1), new ItemStack(Material.DIAMOND, 5)));
		
		enchantedBook = getEnchantedBookWith(Enchantment.LOOT_BONUS_BLOCKS, 3, "Fortune", "More diamonds!", "From "+this.city.getFaction().getName());
		recipes.add(new Trade(enchantedBook, new ItemStack(Material.BOOK, 1), new ItemStack(Material.DIAMOND, 20)));
		
		enchantedBook = getEnchantedBookWith(Enchantment.PROTECTION_ENVIRONMENTAL, 4, "JB's Blessing", "Respect it.", "From "+this.city.getFaction().getName());
		addStoredEnchantment(enchantedBook, Enchantment.DURABILITY, 3);
		addStoredEnchantment(enchantedBook, Enchantment.THORNS, 2);
		
		recipes.add(new Trade(enchantedBook, new ItemStack(Material.BOOK, 1), new ItemStack(Material.DIAMOND, 10)));
		
		return recipes;
	}
	
	private ItemStack getEnchantedBookWith(Enchantment enchant, int level, String displayName, String lore1, String lore2)
	{
		ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK, 1);
		addStoredEnchantment(enchantedBook, enchant, level);
		setDisplayName(enchantedBook, displayName);
		List<String> allLore = new ArrayList<String>();
		allLore.add(lore1);
		allLore.add(lore2);
		setLore(enchantedBook, allLore);
		
		return enchantedBook;
	}
	
	private void addStoredEnchantment(ItemStack enchantedBook, Enchantment enchant, int level)
	{
		if(enchantedBook.getItemMeta() instanceof EnchantmentStorageMeta)
		{
			EnchantmentStorageMeta enchMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
			enchMeta.addStoredEnchant(enchant, level, true);
			enchantedBook.setItemMeta(enchMeta);
		}
	}
	
	private void setDisplayName(ItemStack item, String name)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}
	
	private void setLore(ItemStack item, List<String> lore)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	@Override
	public CharacterType getTraderCharacterType() {
		return CharacterType.LIBRARIAN;
	}
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.LIBRARIAN_HELPER;
	}

	@Override
	public BuildingType getBuildingType() {
		return BuildingType.LIBRARY;
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
