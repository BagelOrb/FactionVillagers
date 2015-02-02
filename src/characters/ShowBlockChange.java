package characters;

import java.util.Arrays;

import main.MCity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShowBlockChange {

	public static void showAs(Block block, Material as, final Iterable<Player> playersToShowSearchSpace, long ticks) {
		showAs(Arrays.asList(new Block[]{block}), as, playersToShowSearchSpace, ticks);
	}
	@SuppressWarnings("deprecation")
	public static void showAs(Iterable<Block> blocks, Material as, final Iterable<Player> playersToShowSearchSpace, long ticks) {
    	for (Block b : blocks)
    		for (Player player : playersToShowSearchSpace)
    			player.sendBlockChange(b.getLocation(), as, (byte) 0);
    	
    	final Iterable<Block> checked2 = blocks;
    	new BukkitRunnable() {
			
			@Override
			public void run() {
				for (Block b : checked2)
					for (Player player : playersToShowSearchSpace)
	        			player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
			}
		}.runTaskLater(MCity.getCurrentPlugin(), ticks);
	}
	public static void showAs(Location location, Material as,
			Iterable<Player> playersToShowSearchSpace, long ticks) {
		showAs(location.getWorld().getBlockAt(location), as, playersToShowSearchSpace, ticks);
	}
}
