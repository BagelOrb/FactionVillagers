package buildings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import main.FactionVillagers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import utils.MatUtils;
import characters.CharacterType;

import com.massivecraft.massivecore.util.Txt;

public class Trade {
	public final ItemStack sell; 
	public final ItemStack buy; 
	public final ItemStack buyB;
	public Trade(ItemStack sell, ItemStack buy, ItemStack buyB) {
		this.sell = sell; this.buy = buy; this.buyB = buyB;
	}
	public Trade(ItemStack sell, ItemStack buy) {
		this.sell = sell; this.buy = buy; this.buyB = null;
	}
	
	protected static final FactionVillagers plugin = FactionVillagers.getCurrentPlugin();
	
	public static Collection<Trade> getHireRecipes(boolean showDebugTrades) {
		Collection<Trade> recipes = new LinkedList<Trade>();
		
		
		//Debug trades
		if(showDebugTrades)
		{
			for (CharacterType guy :  CharacterType.values())
			{
				recipes.add(new Trade(getHirePaperFor(guy), new ItemStack(Material.COBBLESTONE, 1)));
			}
		}

		//Official Trades
		recipes.add(new Trade(getHirePaperFor(CharacterType.STORAGE_KEEPER),	new ItemStack(Material.CHEST, 1), new ItemStack(Material.GOLD_INGOT, 1)));	
		
		recipes.add(new Trade(getHirePaperFor(CharacterType.WHEAT_FARMER), 		new ItemStack(Material.IRON_HOE, 1), new ItemStack(Material.GOLD_INGOT, 1)));			
		recipes.add(new Trade(getHirePaperFor(CharacterType.BAKER), 			new ItemStack(Material.IRON_SPADE, 1), new ItemStack(Material.GOLD_INGOT, 2)));
		recipes.add(new Trade(getHirePaperFor(CharacterType.WEAVER), 			new ItemStack(Material.STICK, 1), new ItemStack(Material.GOLD_INGOT, 2)));
		
		recipes.add(new Trade(getHirePaperFor(CharacterType.WOODCUTTER), 		new ItemStack(Material.IRON_AXE, 1), new ItemStack(Material.GOLD_INGOT, 1)));
		recipes.add(new Trade(getHirePaperFor(CharacterType.SAWMILL_WORKER),	new ItemStack(Material.BOW, 1), new ItemStack(Material.GOLD_INGOT, 2)));	
					
		recipes.add(new Trade(getHirePaperFor(CharacterType.SHEEP_HERDER), 		new ItemStack(Material.SHEARS, 1), new ItemStack(Material.GOLD_INGOT, 2)));
		recipes.add(new Trade(getHirePaperFor(CharacterType.COW_FARMER), 		new ItemStack(Material.WHEAT, 1), new ItemStack(Material.GOLD_INGOT, 2)));
		recipes.add(new Trade(getHirePaperFor(CharacterType.CHICKEN_FARMER), 	new ItemStack(Material.SEEDS, 1), new ItemStack(Material.GOLD_INGOT, 2)));
		
		recipes.add(new Trade(getHirePaperFor(CharacterType.SUGAR_CANE_FARMER), new ItemStack(Material.IRON_SWORD, 1), new ItemStack(Material.GOLD_INGOT, 2)));	
		recipes.add(new Trade(getHirePaperFor(CharacterType.BOOK_BINDER), 		new ItemStack(Material.STRING, 1), new ItemStack(Material.GOLD_INGOT, 2)));				
		
		recipes.add(new Trade(getHirePaperFor(CharacterType.MINER), 			new ItemStack(Material.DIAMOND_PICKAXE, 1), new ItemStack(Material.GOLD_INGOT, 3)));				
		recipes.add(new Trade(getHirePaperFor(CharacterType.MINE_WARDEN), 		new ItemStack(Material.MINECART, 1), new ItemStack(Material.GOLD_INGOT, 5)));
		
		recipes.add(new Trade(getHirePaperFor(CharacterType.SMELTER), 			new ItemStack(Material.FLINT_AND_STEEL, 1), new ItemStack(Material.GOLD_INGOT, 2)));
		
		recipes.add(new Trade(getHirePaperFor(CharacterType.BLACKSMITH, CharacterType.BLACKSMITH_HELPER), 		new ItemStack(Material.COAL, 1), new ItemStack(Material.DIAMOND, 10))); 			 
		recipes.add(new Trade(getHirePaperFor(CharacterType.LIBRARIAN, CharacterType.LIBRARIAN_HELPER), 		new ItemStack(Material.BOOK, 1), new ItemStack(Material.DIAMOND, 10)));
		recipes.add(new Trade(getHirePaperFor(CharacterType.MAYOR), 			new ItemStack(Material.PAPER, 1))); 
		
		return recipes;
	}
	
	public static ItemStack getHirePaperFor(CharacterType guy) {
		
		return getHirePaperFor(guy, guy);
	}
	
	public static ItemStack getHirePaperFor(CharacterType guy, CharacterType guyToShow) {
		ItemStack paper = new ItemStack(Material.PAPER, 1);
		ItemMeta meta = paper.getItemMeta();
		meta.setDisplayName("Hire "+guy.prettyPrint());
		
		List<String> loreList = new ArrayList<String>();
		loreList.add(Txt.parse("<gold>"+guy.description));
		
		//Daily consumption
		if(guyToShow.charClass != null)
		{
			String traitName = null;
			try {
	
					traitName = (String) guyToShow.charClass.getField("traitName").get(null);
					
					ItemStack[] itemsNeededToEat = plugin.getConfig().getList("character."+traitName+".itemsNeededToEat").toArray(new ItemStack[0]);
					
					String dailyConsumption = "<gold>Daily consumption: ";
					if(itemsNeededToEat.length > 0)
					{
						LinkedList<String> itemsNeededToEatStrings = new LinkedList<String>();
						for(ItemStack itemNeededToEat : itemsNeededToEat)
						{
							itemsNeededToEatStrings.add("<bad>"+itemNeededToEat.getAmount()+" "+MatUtils.prettyPrint(itemNeededToEat.getType()));
						}
						dailyConsumption += StringUtils.join(itemsNeededToEatStrings, "<gold>, ") + "<gold>.";
					}
					else
					{
						dailyConsumption += "<good>Nothing!";
					}
					
					loreList.add(Txt.parse(dailyConsumption));
					
			} catch (IllegalArgumentException | IllegalAccessException
					| NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		loreList.add(Txt.parse("<good>Right-click on the floor"));
		loreList.add(Txt.parse("<good>of a room to hire this villager."));
		
		meta.setLore(loreList);
		
		
		paper.setItemMeta(meta);
		return paper;
	}

}