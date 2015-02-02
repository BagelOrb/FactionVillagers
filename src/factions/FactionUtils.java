package factions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import main.FactionVillagers;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import city.City;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

public class FactionUtils {

	public static HashMap<String, City> factionIDToCity = new HashMap<String, City>();

//	public static final Faction daFaction = new Faction();
	
	public static Faction getFaction(Player player) {
		return MPlayer.get(player)
				.getFaction();
	}
	public static Faction getFactionAt(Location loc) {
		return BoardColl.get().getFactionAt(PS.valueOf(loc));
	}
//	public static Faction getFactionAt(Chunk chunk) {
//		// TODO
//		return daFaction;
//	}
	public static Faction getFactionAt(Block block) {
		return BoardColl.get().getFactionAt(PS.valueOf(block));
	}
	
	public static Faction getFactionAt(Chunk chunk) {
		return BoardColl.get().getFactionAt(PS.valueOf(chunk));
	}
	
	public static  boolean playerIsOnHisFacTerrain(Player player, Block block) {
		boolean noCity = FactionVillagers.getCity(player) == null;
		if (noCity) {
			player.sendMessage("noCity="+noCity);
			player.sendMessage("factionToCity keys :"+Arrays.toString(factionIDToCity.keySet().toArray()));
			
		}
		if (noCity) return false;
		
		boolean notOnOwnLand = !FactionVillagers.getCity(player).getFaction().equals(getFactionAt(block.getLocation()) );
		if (notOnOwnLand) player.sendMessage("notOnOwnLand="+notOnOwnLand);
		
		return !noCity && !notOnOwnLand;
	}
//	public static World getWorld(Faction fac) {
//		String universe = fac.getUniverse();
//        Debug.out("world=\""+universe+"\"");
//		return Bukkit.getServer().getWorld(universe); // TODO is this right?!
//	}
	public static Collection<? extends Faction> getAllFactions() {
		//TODO: Multiverse code?
		return FactionColl.get().getAll();
	}
	public static boolean isRealFaction(Faction faction) {
		if (faction.getName().equalsIgnoreCase("º2Wilderness") 
				|| faction.getName().equalsIgnoreCase("WarZone")
				|| faction.getName().equalsIgnoreCase("SafeZone"))
			return false;
		return false;
	}
	
	public static boolean canBuildOn(Faction own, Block block) {
		Faction fac = getFactionAt(block);
		if (fac == own) return true;
		if (fac.isNone()) return true;
		//if (fac.getName().equalsIgnoreCase("º2Wilderness")) return true;
		return true; // otherwise
	}
	public static void sendMessage(Faction fac, String msg) {
		// TODO  send message to all? or just the owner?
		for (Player player : fac.getOnlinePlayers())
		{
			player.sendMessage(msg);
		}
	}
	
	public static boolean loadAllFactionChunks()
	{
		Boolean allChunksAreLoaded = true;
		
		for (Faction faction : FactionColl.get().getAll())
		{
	    	for(PS ps : BoardColl.get().getChunks(faction))
	    	{
	    		allChunksAreLoaded = allChunksAreLoaded && ps.asBukkitChunk().load();
	    	}
		}
		
		return allChunksAreLoaded;
	}
}
