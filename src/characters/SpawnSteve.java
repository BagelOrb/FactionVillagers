package characters;


import main.FactionVillagers;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.util.NMS;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.massivecraft.massivecore.util.Txt;

public class SpawnSteve extends Trait {

	public SpawnSteve() {
		super("spawnSteve");
	}
	
	protected static final FactionVillagers plugin = FactionVillagers.getCurrentPlugin();
	
	@Override
	public void onSpawn() {
		npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
		NMS.addOrRemoveFromPlayerList(getNPC().getEntity(), false);
		npc.data().setPersistent("removefromplayerlist", false);

		if(npc instanceof Player)
		{
			final Player playerNpc = (Player) npc.getEntity();
			playerNpc.setCanPickupItems(false);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) 
	{
		if(npc.getEntity() != null)
		{
			final Player player = e.getPlayer();
			if(player.getWorld() == npc.getEntity().getWorld())
			{
				if(player.getLocation().distanceSquared(npc.getEntity().getLocation()) < 32)
				{
					npc.faceLocation(player.getLocation());
				}
			}
		}
	}

	public static void onClick(NPCLeftClickEvent e) {
		NPC npc = e.getNPC();
//		SpawnSteve steve = npc.getTrait(SpawnSteve.class);
		
		if (FactionVillagers.getCurrentPlugin().spawnSteve == null)
			FactionVillagers.getCurrentPlugin().spawnSteve = npc;
			
			
		if (!FactionVillagers.getCurrentPlugin().spawnSteve.equals(npc))
			npc.destroy();
		else 
		{
			npc.getDefaultSpeechController().speak(new SpeechContext(npc, Txt.parse("<reset>Welcome to Faction Villagers! Be sure to check our sebsite:<i> http://factionvillagers.tk <reset>and our dynmap:<i> http://factionvillagers.tk/map <reset>"), e.getClicker()));
		}
	}


}
