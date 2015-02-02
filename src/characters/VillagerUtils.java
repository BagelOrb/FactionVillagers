package characters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.nbt.NBTContainerEntity;
import me.dpohvar.powernbt.nbt.NBTTagCompound;
import me.dpohvar.powernbt.nbt.NBTTagInt;
import me.dpohvar.powernbt.nbt.NBTTagList;
import me.dpohvar.powernbt.nbt.NBTTagString;
import me.dpohvar.powernbt.utils.NBTQuery;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import buildings.Trade;

public class VillagerUtils {



	@SuppressWarnings("deprecation")
	public static void setInvulnerable(Villager villager, boolean invulnerable) {
		NBTContainerEntity con = new NBTContainerEntity(villager);
		
		NBTQuery namePath = NBTQuery.fromString("Invulnerable");
		try {
			con.setTag(namePath, new NBTTagInt(invulnerable? 1 : 0));
		} catch (NBTTagNotFound | NBTTagUnexpectedType e) {
			e.printStackTrace();
		}		
		
	}

	@SuppressWarnings("deprecation")
	public static void setName(Villager villager, String name) {
		NBTContainerEntity con = new NBTContainerEntity(villager);
		
		try {
			con.setTag(NBTQuery.fromString("CustomName"), new NBTTagString(name));
			//con.setTag(NBTQuery.fromString("CustomNameVisible"), new NBTTagInt(1)); //Uncomment this to always show the villagers name instead of only show name when u look at villager...
		} catch (NBTTagNotFound | NBTTagUnexpectedType e) {
			e.printStackTrace();
		}		
		
	}
	
	public static void setTrades(Villager villager, Trade... trades) {
		setTrades(villager, Arrays.asList(trades));
	}
	@SuppressWarnings("deprecation")
	public static void setTrades(Villager villager, Collection<Trade> trades) {
		NBTContainerEntity con = new NBTContainerEntity(villager);
		
		NBTQuery recipesPath = NBTQuery.fromString("Offers.Recipes");
		try {
			NBTTagList recipes = new NBTTagList();
			for (Trade trade : trades)
				recipes.add(newTrade(trade));
			con.setTag(recipesPath, recipes);
		} catch (NBTTagNotFound | NBTTagUnexpectedType e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("deprecation")
	public static void addTrade(Villager villager, Trade trade) {
		NBTContainerEntity con = new NBTContainerEntity(villager);
		
		NBTQuery recipesPath = NBTQuery.fromString("Offers.Recipes");
		try {
			NBTTagList recipes = (NBTTagList) con.getTag(recipesPath);
			if (recipes == null)
				recipes = new NBTTagList();
			recipes.add(newTrade(trade));
			con.setTag(recipesPath, recipes);
		} catch (NBTTagNotFound | NBTTagUnexpectedType e) {
			e.printStackTrace();
		}
	}

	private static NBTTagCompound newTrade(Trade trade) {
		if (trade.buyB == null) 
			return newTrade(trade.sell, trade.buy);
		else
			return newTrade(trade.sell, trade.buy, trade.buyB);
	}
	private static NBTTagCompound newTrade(ItemStack sell, ItemStack buy, ItemStack buyB) {
		NBTTagCompound trade = newTrade(sell, buy);
		trade.put("buyB", getItemStackNBT(buyB));
		return trade;
	}
	private static NBTTagCompound newTrade(ItemStack sell, ItemStack buy) {
		NBTTagCompound trade = new NBTTagCompound();
//		trade.put("maxUses", new NBTTagInt(999));
		trade.put("sell", getItemStackNBT(sell));
		trade.put("buy", getItemStackNBT(buy));
		trade.put("uses", new NBTTagInt(0));
		return trade;
	}

	@SuppressWarnings("deprecation")
	private static NBTTagCompound getItemStackNBT(ItemStack item) {
		NBTTagCompound ret = new NBTTagCompound();
		ret.put("id", new NBTTagInt(item.getType().getId()));
		ret.put("Damage", new NBTTagInt(0));
		ret.put("Count", new NBTTagInt(item.getAmount()));
		String name = item.getItemMeta().getDisplayName();
		Boolean hasLore = item.getItemMeta().hasLore();
		
		NBTTagCompound tag = new NBTTagCompound();

		if (name != null || hasLore)
		{
			NBTTagCompound display = new NBTTagCompound();
			
			if (name != null)
			{
				display.put("Name", new NBTTagString(name));
			}
		
			if(hasLore)
			{
				NBTTagList loreList = new NBTTagList();
				for(String lore : item.getItemMeta().getLore())
				{
					loreList.add(new NBTTagString(lore));
				}
				if(!loreList.isEmpty())
					display.put("Lore", loreList);
			}
		
			tag.put("display", display);
		}	
		
		addNormalEnchantments(tag, item);
		addBookEnchantments(tag, item);

		addBookTitleAuthor(tag,item);
			
		ret.put("tag", tag);
		return ret;
	}

	private static void addBookTitleAuthor(NBTTagCompound tag, ItemStack item) {
		if (item.getType() != Material.WRITTEN_BOOK)
			return;
		BookMeta book = (BookMeta) item.getItemMeta();
		tag.put("title", book.getTitle());
		tag.put("author", book.getAuthor());
	}

	private static void addBookEnchantments(NBTTagCompound tag, ItemStack item) {
		if (item.getType() != Material.ENCHANTED_BOOK)
			return;
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		addEnchantments(tag, meta.getStoredEnchants().entrySet(), "StoredEnchantments");
	}

	@SuppressWarnings("deprecation")
	private static void addEnchantments(NBTTagCompound tag, Set<Entry<Enchantment, Integer>> enchantmentList, String tagName) {
		NBTTagList enchantments = new NBTTagList();
		boolean hasEnchantments = false;
		for (Entry<Enchantment, Integer> ench : enchantmentList)
		{
			NBTTagCompound enchNBT = new NBTTagCompound();
			if (ench.getValue()==0)
				continue;
			hasEnchantments = true;
			enchNBT.put("id", ench.getKey().getId());
			enchNBT.put("lvl", ench.getValue());
			enchantments.add(enchNBT);
		}
		if (hasEnchantments)
			tag.put(tagName, enchantments);
	}

	private static void addNormalEnchantments(NBTTagCompound tag, ItemStack item) {
		addEnchantments(tag, item.getEnchantments().entrySet(), "ench");
	}

	
}
