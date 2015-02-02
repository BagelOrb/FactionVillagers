package interaction;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;

import main.Debug;
import main.FactionVillagers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import utils.BlockUtils;
import buildings.Building;
import buildings.BuildingType;
import city.City;

import com.massivecraft.massivecore.util.Txt;

import factions.FactionUtils;

public class InteractListener implements Listener
{
	
	
	public InteractListener(FactionVillagers plugin)
	{
		
	}
	

	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClickBlock(PlayerInteractEvent event)
	{	
		if (event.isCancelled()) 
			return;
		
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))	
			return;
		
		Player player = event.getPlayer();

		ItemStack itemInHand = player.getItemInHand();
		
		switch (itemInHand.getType()) {
		case INK_SACK: // bone meal
			if(player.isOp())
			{
				if (itemInHand.getDurability() != 15) // (not) bone meal
					return;
				if (event.getClickedBlock().getType() != Material.CROPS)
					return;
				HashSet<Block> wheatBlocks = BlockUtils.getAllConnectedBlocksOfSameType(event.getClickedBlock(), 1000);
				for (Block wheat : wheatBlocks)
				{
					wheat.setData((byte)7);
	//				wheat.getState().setRawData((byte) 7);
				}
			}
			
			break;
		case BOOK:
			Debug.out("book in hand!");
			ItemMeta meta = itemInHand.getItemMeta();
			if (meta instanceof BookMeta) return;
			BookMeta book = (BookMeta) meta;
			if (!book.hasTitle()) return;
			StatisticsBookListener.updateBookStatistics(book, player);
			break;
		case PAPER: 
//			if (!FactionUtils.playerIsOnHisFacTerrain(player, event.getClickedBlock())) 
//				{
//					player.sendMessage("You must be on your own faction terrain for that!");
//					return;
//				}
			handleBuildingCreation(event, itemInHand);
			break;
		case EMERALD:
//			ShopkeepersPlugin shopkeepersPlugin = ((ShopkeepersPlugin) Bukkit.getServer().getPluginManager().getPlugin("Shopkeepers")); 
//			Shopkeeper contractor = shopkeepersPlugin.createNewAdminShopkeeper(event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), ShopObjectType.VILLAGER);
//			
//			ItemStack paper = new ItemStack(Material.PAPER, 64);
//			meta = paper.getItemMeta();
//			meta.setDisplayName("Uber paper 4 da wins");
//			paper.setItemMeta(meta);
//			contractor.getRecipes().add(new ItemStack[]{new ItemStack(Material.EMERALD, 2), new ItemStack(Material.EMERALD_BLOCK, 2), paper});
//			contractor.setName("Contractor NAME");
//			contractor.getShopObject().setItem(new ItemStack(Material.PAPER, 1));
//			contractor.getShopObject().cycleType();
//			contractor.getShopObject().cycleType();
			break;


		default:
			break;
		}
	}


	private void handleBuildingCreation(PlayerInteractEvent event, ItemStack itemInHand) {
		
		Block placedBlock = event.getClickedBlock();
		Block startingBlock = placedBlock.getRelative(event.getBlockFace());
		Player player = event.getPlayer();
		
		City city = FactionVillagers.getCity(player);
		if (city == null)
		{
            player.sendMessage(Txt.parse("<bad>You must be in a faction to hire a villager!"));
			return;
		}

			
		String itemName = itemInHand.getItemMeta().getDisplayName();
		if (itemName==null) itemName = "";
		
		
		Building newBuilding = null;
		for (BuildingType buildingType : BuildingType.values())
		{
			if (itemName.equalsIgnoreCase("Hire "+buildingType.characterType.prettyPrint()))
				try {
					newBuilding = buildingType.buildingClass.getConstructor(City.class, Block.class).newInstance(FactionVillagers.getCity(player), startingBlock);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
		}
		if (newBuilding == null)
		{
//			player.sendMessage("In order to register a building, use a form! E.g. \"Storage Room Permission Form\" or \"Farm Permission Form\"");
			return; // and don't execute the code below
		}
		
		

		List<String> errors = newBuilding.checkValidityOfBuildingPlace(true);
		if (errors.size()>0) 
		{
			player.sendMessage(Txt.parse("<bad>Cannot hire "+newBuilding.getBuildingType().characterType.prettyPrint()+"! \n\n - "+StringUtils.join(errors, "\n - ")));
			return;
		}
		
		newBuilding.create(city, event);
		
		String charName = newBuilding.getBuildingType().characterType.prettyPrint()
				+((newBuilding.isUnique())? "": " "+newBuilding.buildingId);
		FactionUtils.sendMessage(FactionUtils.getFaction(player), Txt.parse("<good>"+player.getName()+ " hired "+charName +"!"));
		
		String buildingMessage = newBuilding.getBuildingMessage();
		if(!buildingMessage.isEmpty())
			FactionUtils.sendMessage(FactionUtils.getFaction(player), Txt.parse(buildingMessage));
		
		InteractionUtils.decreasePlayersItemInHandByOne(player);
	}



}
