package interaction;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import utils.BlockUtils;

public class LiquidFlowListener implements Listener {

	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event) {
		Block toBlock = event.getToBlock();
		
		if (BlockUtils.isRailType(toBlock.getType()))
			event.setCancelled(true);
//		if (MetaDataUtils.isFake(toBlock))
//		{
////			event.setCancelled(true);
//			MetaDataUtils.setFake(toBlock, false);
//			toBlock.setType(Material.AIR);
//		}
	}
}
