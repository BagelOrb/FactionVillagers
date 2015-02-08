package interaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import utils.BlockUtils;
import utils.StringUtilsTK;
import buildings.Building;
import buildings.MetaDataUtils;
import characters.ShowBlockChange;

public class UberToolsListener implements Listener
{

	public static final String[] tools = new String[]{"select", "setSelectionAsType", "xray", "destroyConnected", "recheckBuilding", "somethingDebug", "blockInfo", "fillMine"};
	
	static HashMap<Player, Block> selectedLeft = new HashMap<Player, Block>();
	static HashMap<Player, Block> selectedRight = new HashMap<Player, Block> ();
	
//	static HashMap<Player, Material> selectedType = new HashMap<Player, Block> ();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClickBlock(PlayerInteractEvent event)
	{
		
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))	
			return;
		
		Player player = event.getPlayer();
		if (!player.isOp()) 
			return;
		
		Block block = event.getClickedBlock();
		
		if (player.getItemInHand().getType() != Material.BLAZE_ROD)
			return;
		
		switch (player.getItemInHand().getItemMeta().getDisplayName()) {
		case "select":
			player.sendMessage("Selected second block.");
			selectedRight.put(player, block);
			break;
		case "setSelectionAsType":
			setSelectionAsType(block, selectedLeft.get(player), selectedRight.get(player), player);
			break;
		case "recheckBuilding":
			if (MetaDataUtils.belongsToBuilding(event.getClickedBlock()))
			{
				List<Building> buildings = MetaDataUtils.getBuildings(event.getClickedBlock());
				for (Building building : buildings)
				{
					player.sendMessage("Rechecking "+building.toString()+".");
					building.recheckAndShowMessage(player, true, true);
				}
			}
			break;
		case "somethingDebug":
			player.sendMessage("something debuggy");
			block.setData((byte) 2);
			break;
		case "blockInfo":
			boolean hasState = block.getState() != null;
			Chest chest = null;
			if (hasState && block.getState() instanceof Chest)
				chest = (Chest) block.getState();
			player.sendMessage(
					"info on block: \n"+
					"Location="+block.getLocation()+"\n"+
					"Type="+block.getType()+"\n"+
					"TypeId="+block.getTypeId()+"\n"+
					"Class="+block.getClass()+"\n"+
					"State="+block.getState()+"\n"+
					((block.getState()!= null)? 
					"Raw Data="+block.getState().getRawData()+"\n"+
						((chest != null)?
						"chest has 80 wheat :"+chest.getInventory().containsAtLeast(new ItemStack(Material.WHEAT,80), 80)
								: "")
					: "")+
//					""++"\n"+
//					""++"\n"+
//					""++"\n"+
					""
					);
			if (MetaDataUtils.belongsToBuilding(block))
			{
				player.sendMessage("belongsTo:"+block.getMetadata(MetaDataUtils.belongsToBuildingString).get(0).asString()+"\n");
				List<Building> buildings = MetaDataUtils.getBuildings(block);
				for (Building building : buildings)
					player.sendMessage("\n"+building.getFullyQualifiedId()+":\n" + StringUtilsTK.indent(building.getDebugInfo(), "-"));
			}
//			if (MetaDataUtils.isFake(block))
//			{
//				player.sendMessage("block is fake!!!");
//			}
			break;

		default:
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onLeftClickEvent(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (!player.isOp()) 
			return;
		
		Block block = event.getBlock();
		if (player.getItemInHand().getType() != Material.BLAZE_ROD)
			return;
		
		switch (player.getItemInHand().getItemMeta().getDisplayName()) {
		case "xray":
			HashSet<Block> blocks = BlockUtils.getAllConnectedBlocksOfSameType(block, 1000);
			ShowBlockChange.showAs(blocks, Material.AIR, Arrays.asList(new Player[]{player}), 1000);
			break;
		case "select":
			player.sendMessage("Selected first block.");
			selectedLeft.put(player, block);
			break;
		case "setSelectionAsType":
			setSelectionAsType(block, selectedLeft.get(player), selectedRight.get(player), player);
			break;
		case "destroyConnected":
			HashSet<Block> connectedBlocks = BlockUtils.getAllConnectedBlocksOfSameType(block, 1000);
			for (Block b : connectedBlocks)
				b.setType(Material.AIR);
			break;
		case "fillMine":
			HashSet<Block> carpets = new HashSet<Block>();
			Stack<Block> todo = new Stack<Block>();
			todo.add(block);
			while (!todo.isEmpty())
			{
				Block now = todo.pop();
				for (int x = -1; x <=1; x++)
					for (int z = -1; z <=1; z++)
						for (int y = -1; y <=2; y++)				
						{
							Block near = now.getRelative(x,y,z);
							if (near.getType() == Material.CARPET && !carpets.contains(near))
							{
								todo.add(near);
								carpets.add(near);
							}
							
						}
			}
			
			for (Block carpet : carpets)
				for (int x = -1; x <=1; x++)
					for (int z = -1; z <=1; z++)
						for (int y = -1; y <=2; y++)
							carpet.getRelative(x, y, z).setType(Material.STONE);
						
			break;
		default:
			return;
		}
		event.setCancelled(true);
	}

	
	@SuppressWarnings("deprecation")
	private void setSelectionAsType(Block block, Block l, Block r, Player player) {
		if (l==null || r==null)
		{
			player.sendMessage("Not two blocks selected!");
		}
		int xF, xT, yF,yT,zF,zT;
		xF = Math.min(l.getX(), r.getX());
		xT = Math.max(l.getX(), r.getX());
		yF = Math.min(l.getY(), r.getY());
		yT = Math.max(l.getY(), r.getY());
		zF = Math.min(l.getZ(), r.getZ());
		zT = Math.max(l.getZ(), r.getZ());
		for (int x = xF; x<=xT; x++)
			for (int y = yF; y<=yT; y++)
				for (int z = zF; z<=zT; z++){
					Block b = l.getWorld().getBlockAt(x,y,z);
					b.setType(block.getType());
					b.setData(block.getData());
				}
	}

	
}
