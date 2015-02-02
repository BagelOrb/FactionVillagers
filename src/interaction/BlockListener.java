package interaction;

import java.util.List;

import main.FactionVillagers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import buildings.Building;
import buildings.MetaDataUtils;

import com.massivecraft.massivecore.util.Txt;

public class BlockListener implements Listener
{

	public BlockListener(FactionVillagers plugin)
	{
		
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event)
	{		
//		Block block = event.getBlock();
//		if (block.getType().hasGravity())
//		{
//			Block above = block.getRelative(BlockFace.UP);
//			if (MetaDataUtils.isFake(above))
//			{
//				MetaDataUtils.setFake(above, false);
////				above.getWorld().dropItem(arg0, arg1) // TODO ?
//				above.setType(Material.AIR);
//				event.setCancelled(true);
//			}
//				
//		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{		

	}
	
	
	@EventHandler
	public void onBlockPunch(BlockDamageEvent event)
	{
		if (event.isCancelled()) return;
		
		Player player = event.getPlayer();
		Block blockToBeBroken = event.getBlock();
		
		switch(event.getBlock().getType()) {
		case WALL_SIGN:
		case SIGN_POST:
			punchSign(player, event, blockToBeBroken);
			
			break;
		default:
			break;
		}
	}
	
	private void punchSign(Player player, BlockDamageEvent event, Block blockToBeBroken) {
		if (MetaDataUtils.belongsToBuilding(blockToBeBroken))
		{
			List<Building> buildings = MetaDataUtils.getBuildings(blockToBeBroken);
			for (Building building : buildings)
			{
				if (building.getSign().getBlock().equals(event.getBlock()))
				{
					event.setCancelled(true);
				}
				building.recheckAndShowMessage(player, true, true);
			}
			
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{		
		if (event.isCancelled()) return;
		
		Player player = event.getPlayer();
		Block blockToBeBroken = event.getBlock();
		
//		if (MetaDataUtils.isFake(blockToBeBroken))
//		{
//			MetaDataUtils.setFake(blockToBeBroken, false);
////			blockToBeBroken.getWorld().dropItem(arg0, arg1) // TODO ?
//			blockToBeBroken.setType(Material.AIR);
//			event.setCancelled(true);
//		}
//		Block blockAbove = blockToBeBroken.getRelative(BlockFace.UP);
//		if (MetaDataUtils.isFake(blockAbove))
//		{			
//			MetaDataUtils.setFake(blockAbove, false);
////			blockToBeBroken.getWorld().dropItem(arg0, arg1) // TODO ?
//			blockAbove.setType(Material.AIR);
//		}
		
		if (FactionVillagers.getCity(player)==null) return;
		
		// TODO : check if Factions plugin checks whether it should stop the BlockBreakEvent before this function is called!
		{
			List<Building> buildings = MetaDataUtils.getBuildings(blockToBeBroken);
			for (Building building : buildings)
				if (building.startingBlock.equals(blockToBeBroken))
				{
	//					if (!FactionUtils.playerIsOnHisFacTerrain(player, event.getBlock())) 
	//					{
	//						player.sendMessage("You must be on your own faction terrain for that!");
	//						event.setCancelled(true);
	//						return;
	//					}
					blockToBeBroken.setType(Material.AIR);
					//TODO: Give paper back?
					player.sendMessage(Txt.parse("<bad>Firing "+
							building.getBuildingType().characterType.prettyPrint() +((building.isUnique())? "" : " "+building.buildingId) 
							+"!"));
					building.destroy();
					return;
				}
		}
		
		switch(event.getBlock().getType()) {
		case WALL_SIGN:
		case SIGN_POST:
			if (MetaDataUtils.belongsToBuilding(blockToBeBroken))
			{
				List<Building> buildings = MetaDataUtils.getBuildings(blockToBeBroken);
				for (Building building : buildings)
				{
					if (building.getSign().getBlock().equals(event.getBlock()))
					{
						event.setCancelled(true);
					}
					building.recheckAndShowMessage(player, true, true);
				}
				
			}
			
			break;
		case ENDER_CHEST:
		case CHEST: 
		case BOOKSHELF:
		default:
			if (MetaDataUtils.belongsToBuilding(blockToBeBroken))
			{
				//TODO: check dit: moet gewoon beide weg tog?
				//blockToBeBroken.breakNaturally();
				//blockToBeBroken.setType(Material.AIR);
				List<Building> buildings = MetaDataUtils.getBuildings(blockToBeBroken);
				for (Building building : buildings)
				{
					building.recheckAndShowMessage(player, false, true);
				}

			}
			break;
		}
		
		
	}
}
