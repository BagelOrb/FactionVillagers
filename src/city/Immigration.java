package city;

import java.util.List;
import java.util.Random;

import main.MCity;

import org.bukkit.inventory.ItemStack;

import buildings.BuildingWithStorage;
import characters.InventoryTraitUtils;
import characters.Unemployed;

import com.massivecraft.massivecore.util.Txt;

import factions.FactionUtils;

public class Immigration {

	final static MCity plugin = MCity.getCurrentPlugin();
	
	private static final double lowerFoodMargin = plugin.getConfig().getDouble("immigration.lowerFoodMargin");
	private static final double upperFoodMargin = plugin.getConfig().getDouble("immigration.upperFoodMargin");

	private static final double chanceToGetNewUnemployed = plugin.getConfig().getDouble("immigration.chanceToGetNewUnemployed");
	
	public final static ItemStack[] itemsNeededToEatByUnemployed = plugin.getConfig().getList("character.unemployed.itemsNeededToEat").toArray(new ItemStack[0]);
	
	static Random random = new Random();
	
	public static void immigrantsCheck(City city) {
		if (city.townHall == null)
			return;

		
		boolean noStorage = false;
		BuildingWithStorage storage = city.storageRoom;
		if (storage == null)
		{
			noStorage = true;
			storage = city.townHall;
		}
		
		ItemStack[] totalConsumption = city.getTotalConsumption();
		
		//if(totalConsumption[0] == null) //TODO: Finish dit. Deze if helemaal weghalen tog? itemsNeededToEatByUnemployed mag altijd geadd worden aan totalConsumption..
		InventoryTraitUtils.addItem(totalConsumption, itemsNeededToEatByUnemployed);
		
//		for(ItemStack item : totalConsumption)
//		{
//			if(item != null)
//				Debug.out("ITEM: "+item.getAmount()+ " " + item.getType().toString());
//		}
		
		
		boolean withinLowerMarging = containsAtLeast(storage, totalConsumption, lowerFoodMargin);
		boolean withinUpperMarging = containsAtLeast(storage, totalConsumption, upperFoodMargin);
		
		if (!withinLowerMarging)
		{
			List<Unemployed> pool = city.townHall.getPool();
			if (pool.size()==0)
			{
				FactionUtils.sendMessage(city.getFaction(), Txt.parse("<bad>There are no villagers who want to migrate to your city! Put more food in your "+(noStorage ? "Town Hall chest" : "Storage Room")+" to attract villagers to your city!"));
				//Debug.out(Txt.parse("<i>No unemployed villagers to deport!"));
				return;
			}
			Unemployed unemployed = pool.get(0);
			unemployed.destroy();
			unemployed.getNPC().destroy();
			
			FactionUtils.sendMessage(city.getFaction(), Txt.parse("<bad>An unemployed villager left your city! Put more food in your "+(noStorage ? "Town Hall chest" : "Storage Room")+" to prevent villagers from leaving!"));
		}
		else if (withinUpperMarging)
		{
			if (random.nextDouble() < chanceToGetNewUnemployed)
			{
				boolean unemployedCreated = city.townHall.createUnemployed();
				if (unemployedCreated)
					FactionUtils.sendMessage(city.getFaction(), Txt.parse("<good>A new unemployed villager migrated to your city!"));
				else
					FactionUtils.sendMessage(city.getFaction(), Txt.parse("<info>Place more beds in the Town hall to house more unemployed villagers."));
					
			}
			else
			{
				FactionUtils.sendMessage(city.getFaction(), Txt.parse("<info>A new unemployed villager will migrate to your city soon."));
			}
		}
	}

	private static boolean containsAtLeast(BuildingWithStorage storage, ItemStack[] totalConsumption, double multiplier) {
		//boolean containsAll = true;
		for (ItemStack consumed : totalConsumption)
		{
			if (consumed == null)
				continue;
			if (!storage.containsAtLeast(consumed, (int) (consumed.getAmount()*multiplier)))
				return false;
		}
		return true;
	}




}
