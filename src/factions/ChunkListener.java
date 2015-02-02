package factions;

import main.FactionVillagers;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.massivecraft.factions.entity.Faction;

public class ChunkListener implements Listener {
	
	public ChunkListener(FactionVillagers factionVillagers) {
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent e) 
	{
		//Debug.out("ChunkUnloadEvent called!");
		if(FactionVillagers.keepFactionChunksLoaded)
		{
			Chunk chunk = e.getChunk();
			Faction faction = FactionUtils.getFactionAt(chunk);
			if(!faction.isNone())
			{
				//Debug.out("Chunk [ "+chunk.getX()+", "+chunk.getZ()+" ] prevented from unloading! Faction: "+faction.getName());
				e.setCancelled(true);
			}
		}
	}
}
