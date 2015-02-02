package characters;

import main.MCity;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import utils.BlockUtils;

public class TreeListener implements Listener {

	public TreeListener(MCity mCity) {
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTreeGrow(StructureGrowEvent event) {
		if (event.getSpecies() == TreeType.BIG_TREE) 
			event.setCancelled(true);
		int minY = (int) event.getLocation().getY() +2;
//		Debug.out("TreeGrow concerned blocks:");
		for (BlockState block : event.getBlocks())
		{
//			Debug.out(":  :"+ block);
			if (block.getY() < minY && BlockUtils.isLeaveType(block.getType()))
				block.setType(Material.AIR);
		}
		new BukkitRunnable(){

			@Override
			public void run() {
				
			}}.runTaskLater(MCity.getCurrentPlugin(), 2);
		
	}
}
