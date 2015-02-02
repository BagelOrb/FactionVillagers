package characters;

import happiness.Happiness;

import java.util.LinkedList;
import java.util.List;

import main.Debug;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import utils.MatUtils;
import buildings.Building;
import buildings.BuildingWithStorage;
import buildings.BuildingWithTradeVillager;
import buildings.TownHall;

import com.massivecraft.massivecore.util.Txt;

import factions.FactionUtils;

public class NpcUtils {

	/**
	 * @param npc
	 * @return null if it has no character attached
	 */
	public static Character getCharacter(NPC npc) {
		for (CharacterType type : CharacterType.values())
		{
			if (npc.hasTrait(type.charClass))
				return npc.getTrait(type.charClass);
		}
		if (npc.hasTrait(Unemployed.class))
			return npc.getTrait(Unemployed.class);
		
		if (npc.hasTrait(SpawnSteve.class))
			return null; // is no Character!
		
		Debug.out("couldn't get Character class! of NPC with traits: "+npc.getTraits());
		return null;
	}

	public static void recheckAllNPCs() {
		LinkedList<NPC> allNPCs = new LinkedList<NPC>();
		for (NPC npc : CitizensAPI.getNPCRegistry())
			allNPCs.add(npc);
		
		for (NPC npc : allNPCs)
		{
			Character charr = getCharacter(npc);
			if (npc.hasTrait(SpawnSteve.class))
				return;
			if (charr == null)
			{
				Debug.out("Removed NPC without building: "+(charr==null? npc.getName() : charr.getName()));
				npc.destroy();
			}
			else
				recheckCitizenAndBuilding(null, charr);
		}
		
	}

	public static boolean recheckTraderAndBuilding(Player player, Villager villager, List<Building> homeBuildings) {
	//		Debug.out("charTrait != null");
			if (homeBuildings.size() != 1)
			{
				Debug.out("Villager not belonging to any one building! Destroyed!");
				villager.remove();
				for (Building building : homeBuildings)
				{
					if (building == null) continue;
					building.recheckAndShowMessage(player, true, true);
				}
				return false;
			}
			
			Building homeBuilding = homeBuildings.get(0);
			if (homeBuilding == null)
			{
				Debug.warn("NPC without home building! Destroying...");
				villager.remove();
				return false;
			}
			
			if (!(homeBuilding instanceof BuildingWithTradeVillager))
			{
				Debug.warn("Trade villager without BuildingWithTradeVillager building! Destroying...");
				villager.remove();
				return false;
			}
			
			BuildingWithTradeVillager tradeBuilding = (BuildingWithTradeVillager) homeBuilding;
			
			return NpcUtils.recheckTraderAndBuilding(player, villager, tradeBuilding);
		}

	public static boolean recheckTraderAndBuilding(Player player, Villager villager, BuildingWithTradeVillager tradeBuilding) {
		if (!villager.equals(tradeBuilding.villager))
		{
			if (tradeBuilding.villager == null)
			{
				tradeBuilding.villager = villager;
				return true;
			}
			else
			{
				Debug.out("Trade villager is not the villager of his building!");
				villager.remove();
				tradeBuilding.prettyPrintBuildingErrorState(tradeBuilding.recheck(), true);
				return false;
			}
		}
		
		if (tradeBuilding != null && FactionUtils.getFaction(player) != tradeBuilding.city.getFaction())
			return false;
		else 
		{
			boolean buildingWasActive = tradeBuilding.isActive();
			List<String> errors = tradeBuilding.recheck();
			if (errors.size() > 0 || !buildingWasActive)
				player.sendMessage(tradeBuilding.prettyPrintBuildingErrorState(errors, buildingWasActive));
	
			if (errors.size()>0)
				VillagerUtils.setTrades(villager); // no trades!
			else
				VillagerUtils.setTrades(villager, tradeBuilding.getTrades());
			return true;
		}
	}

	public static String getNPCinfo(Player player, ChestCharacter charTrait) {

		
		String msg = NpcUtils.getBasicInfoString(charTrait.getCharacterType(), charTrait.homeBuilding);
		
		//Status
		msg += "<gold>Status: "+ (charTrait.homeBuilding.isActive() ? "<good>Active" : "<bad>Inactive") +"\n";
		
		msg += "<gold>Happiness: "+ Happiness.happinessToString(charTrait, "<gold>") + "\n";
		
		if (charTrait instanceof Character)
		{
			ChestCharacter chestChar = ((ChestCharacter) charTrait);
			
			//Daily consumption
			msg += "<gold>Daily consumption: ";
			if(chestChar.itemsNeededToEat.length > 0)
			{
				LinkedList<String> itemsNeededToEatStrings = new LinkedList<String>();
				for(ItemStack itemNeededToEat : chestChar.itemsNeededToEat)
				{
					itemsNeededToEatStrings.add("<bad>"+itemNeededToEat.getAmount()+" "+MatUtils.prettyPrint(itemNeededToEat.getType()));
				}
				msg += StringUtils.join(itemsNeededToEatStrings, "<gold>, ") + "<gold>.\n";
			}
			else
			{
				msg += "<good>Nothing!\n";
			}
			
			//Productions
			if (chestChar.productions.size() > 0)
			{
				if(chestChar.productions.get(0).getItemsNeededToConsume().length > 0 || chestChar.productions.get(0).getItemsProduced().length > 0)
				{
					msg += "<gold>Productions:\n";
					LinkedList<String> productionsStrings = new LinkedList<String>();
					for (Production production : chestChar.productions)
					{
						LinkedList<String> consumedStrings = new LinkedList<String>();
						LinkedList<String> producedStrings = new LinkedList<String>();
						
						if(production.itemsProduced.isEmpty())
						{
							for(ItemStack item : production.itemsNeededToConsume)
							{
								consumedStrings.add(item.getAmount()+" "+MatUtils.prettyPrint(item.getType()));
							}
							productionsStrings.add("uses <bad>"+StringUtils.join(consumedStrings, "<info>, <bad>"));
						}
						else if(production.itemsNeededToConsume.isEmpty())
						{
							for(ItemStack item : production.itemsProduced)
							{
								producedStrings.add(MatUtils.prettyPrint(item.getType()));
							}
							productionsStrings.add("produces <good>"+ StringUtils.join(producedStrings, "<info>, <good>"));
						}
						else
						{
							for(ItemStack item : production.itemsNeededToConsume)
							{
								consumedStrings.add(item.getAmount()+" "+MatUtils.prettyPrint(item.getType()));
							}
							for(ItemStack item : production.itemsProduced)
							{
								producedStrings.add(item.getAmount()+" "+MatUtils.prettyPrint(item.getType()));
							}
							productionsStrings.add("uses <bad>"+StringUtils.join(consumedStrings, "<info>, <bad>") +"<info> to produce <good>"+ StringUtils.join(producedStrings, "<info>, <good>"));
						}
					}
					
					String allProductionsString = StringUtils.join(productionsStrings, "<info> <underline>or<reset>\n<info>") + "<info>.";
					
					//Make the first character upper case 
					allProductionsString = java.lang.Character.toUpperCase(allProductionsString.charAt(0)) + allProductionsString.substring(1);
					
					msg += "<info>" + allProductionsString;
				}
			}
		}
		
		return Txt.parse(msg);
	}
	
	
	public static String getGoldenDelimiterString(String stringInBetween) 
	{
		int maxCharactersOnALine = 52;
		String stripeChar = "_";
		
		String stringInBetweenClean = "";
		for(String toReplace : Txt.parseReplacements.keySet())
		{
			stringInBetweenClean = Txt.parse(StringUtils.replace(stringInBetween, toReplace, "<empty>"));
		}
		
		int stripeLength = ((maxCharactersOnALine - stringInBetweenClean.length()) / 2) - 3;
		
		String stripe = "";
		for(int i = 0; i < stripeLength; i++)
		{
			stripe += stripeChar;
		}
		
		return Txt.parse("<reset><gold>"+stripe+".[ "+stringInBetween+" <reset><gold>]."+stripe+"\n<reset>");
	}

	public static String getBasicInfoString(CharacterType characterType,
			BuildingWithStorage homeBuilding) {
		
		String msg = getGoldenDelimiterString("<green>Villager <good>"+characterType.prettyPrint()+(homeBuilding.isUnique() ? "" : " "+homeBuilding.buildingId));
		
		msg += "<gold>Description: <silver>"+characterType.description +"\n";
		return msg;
	}

	/**
	 * @param player
	 * @param charTrait
	 * @return whether the npc is destroyed
	 */
	public static boolean recheckCitizenAndBuilding(Player player, Character charTrait) {
	//		Debug.out("charTrait != null");
			if (charTrait.homeBuilding != null && player != null &&
					FactionUtils.getFaction(player) != charTrait.homeBuilding.city.getFaction())
				return false;
			
			
			
			boolean doesntBelongToHomeBuilding = false;
			if (charTrait instanceof Unemployed)
			{
				if (! ((TownHall) charTrait.homeBuilding).getPool().contains(charTrait))
				{
					Debug.out("Unemployed without homebuilding! \r\n pool= \r\n"+((TownHall) charTrait.homeBuilding).getPool());
					doesntBelongToHomeBuilding = true;
				}
			}
			else if (charTrait instanceof ChestCharacter && !charTrait.getNPC().equals(charTrait.homeBuilding.getNPC())  )
				doesntBelongToHomeBuilding = true;
			
			
			if (charTrait.homeBuilding == null || charTrait.homeBuilding.isDestroyed() || doesntBelongToHomeBuilding )
			{
				Debug.out(charTrait.getClass());
				Debug.out(3, "NPC without homeBuilding! Destroying...");
				charTrait.getNPC().destroy();
				return true;
			}
			else 
			{
				boolean buildingWasActive = charTrait.homeBuilding.isActive();
				List<String> errors = charTrait.homeBuilding.recheck();
				if (errors.size() > 0 || !buildingWasActive)
					if (player != null)
						player.sendMessage(charTrait.homeBuilding.prettyPrintBuildingErrorState(errors, buildingWasActive));
			}
			charTrait.recheck();
			return false;
		}


	
	
}
