package factions;

import java.util.Map;

import main.FactionVillagers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import buildings.Building;
import buildings.Trade;
import characters.CharacterType;
import city.City;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

public class FactionListener implements Listener {

	public FactionListener(FactionVillagers factionVillagers) {
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	public void onFactionCreate(EventFactionsCreate e) {
		Player player = e.getMSender().getPlayer();
		City city = new City(player.getWorld(), e.getFactionId());
		city.create();
		//player.getInventory().addItem(city.getStatisticsBook()); TODO: Give statistic book
		player.getInventory().addItem(Trade.getHirePaperFor(CharacterType.MAYOR));
		player.sendMessage(Txt.parse("<bad>IMPORTANT: <good>Mayor paper added to your inventory!\n<i>Build a Town Hall for your Mayor on your faction terrain to gain access to more villagers!"));
	}
	
	@EventHandler
	public void onFactionDisband(EventFactionsDisband e) {
		String facID = e.getFaction().getId();
		City city = FactionUtils.factionIDToCity.get(facID);
		city.destroy();
		FactionUtils.factionIDToCity.remove(facID);
	}
	
	@EventHandler
	public void onChunkChange(EventFactionsChunksChange e) {
		Map<PS, EventFactionsChunkChangeType> chunkChangeMap = e.getChunkType();
		for (Map.Entry<PS, EventFactionsChunkChangeType> chunkChange : chunkChangeMap.entrySet())
		{
			switch (chunkChange.getValue())
			{			
				case BUY: break;
				case CONQUER: loseChunk(chunkChange.getKey()); break;
				case PILLAGE: loseChunk(chunkChange.getKey()); break;
				case SELL: loseChunk(chunkChange.getKey()); break;
				case NONE: break;
				default: break;
			}
		}
	}

	@EventHandler
	public void onFactionRename(EventFactionsNameChange e) {
//		e.setCancelled(true);
//		e.getSender().sendMessage(Txt.parse("<bad>Faction renaming not supported yet!"));
	}
	
	private void loseChunk(PS chunk) {
		Faction fac = BoardColl.get().getFactionAt(chunk);
		if(!fac.getName().equalsIgnoreCase("SafeZone") && !fac.getName().equalsIgnoreCase("WarZone"))
		{
			for (Building building :  FactionUtils.factionIDToCity.get(fac.getId()).getAllBuildings())
			{
				if (building.startingBlock.getChunk().equals(chunk.asBukkitChunk()))
				{
					FactionUtils.sendMessage(fac, Txt.parse("<bad>"+building.prettyPrintFullyQualifiedBuildingPath()+" has been destroyed!"));
					building.destroy();
				}
			}
		}
	}
}
