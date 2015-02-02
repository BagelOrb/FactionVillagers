package interaction;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class SpawnListener implements Listener {

	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == SpawnReason.VILLAGE_DEFENSE)
			e.setCancelled(true); // for iron golems
		
	}
}
