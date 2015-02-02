package characters;

import java.util.HashMap;
import java.util.List;

import main.Debug;
import main.MCity;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import buildings.Building;
import buildings.BuildingWithTradeVillager;
import buildings.MetaDataUtils;

import com.massivecraft.massivecore.util.Txt;

import factions.FactionUtils;

public class NPCListener implements Listener {

	public NPCListener(MCity mCity) {
		// TODO Auto-generated constructor stub
	}

	public static HashMap<Player, NPC> selectedNPC = new HashMap<Player, NPC>();
	
//	@EventHandler
//	public void onEntityEvent(EntityInteractEvent e) {
//		Debug.out("EntityInteractEvent" );
//		Debug.out("EntityEvent entity type"+e.getEntityType());
//		Debug.out("EntityEvent event name"+e.getEventName());
//	}

	
	@EventHandler
	public void onTraderLeftClick(EntityDamageByEntityEvent e) {
		
		Entity interacted = e.getEntity();
		if (!(interacted instanceof Villager))
			return;
		Villager villager = (Villager) interacted;
		
		if (villager.hasMetadata("NPC"))
			return; // this code is for traders!
		
		e.setCancelled(true);

		
		
		Entity damager = e.getDamager();
		if (!(damager instanceof Player))
			return;
		
		Player player = (Player) damager;
		
		
		List<Building> homeBuildings = MetaDataUtils.getBuildings(villager);
//		if (homeBuildings.isEmpty())
//		{
//			Debug.out(string);
//			villager.remove();
////			VillagerUtils.setInvulnerable(villager, false);
////			VillagerUtils.setName(villager, "");
////			VillagerUtils.setTrades(villager, new Trade[0]);
//			return;
//		}
		
		boolean isCorrectVillager = NpcUtils.recheckTraderAndBuilding(player, villager, homeBuildings);
		
		if (isCorrectVillager && FactionUtils.getFaction(player).equals(homeBuildings.get(0).city.getFaction()))
		{
			BuildingWithTradeVillager homeBuilding = ((BuildingWithTradeVillager)homeBuildings.get(0));
			String msg = NpcUtils.getBasicInfoString(homeBuilding.getTraderCharacterType(), homeBuilding);
			player.sendMessage(Txt.parse("<i>"+msg));
		}
	}
	@EventHandler
	public void onTraderRightClick(PlayerInteractEntityEvent e) {
		
		Entity interacted = e.getRightClicked();
		if (!(interacted instanceof Villager))
			return;
		Villager villager = (Villager) interacted;

		if (villager.hasMetadata("NPC"))
			return; // this code is for traders!

			

		
		List<Building> homeBuildings = MetaDataUtils.getBuildings(villager);
//		if (homeBuildings.isEmpty())
//		{
//			villager.remove();
////			VillagerUtils.setInvulnerable(villager, false);
////			VillagerUtils.setName(villager, "");
////			VillagerUtils.setTrades(villager, new Trade[0]);
//			return;
//		}
		
		NpcUtils.recheckTraderAndBuilding(e.getPlayer(), villager, homeBuildings);
		
	}
	
	@EventHandler
	public void onCitizenClick(NPCLeftClickEvent e) {
		Player player = e.getClicker();
		NPC npc = e.getNPC();
		
		
		npc.faceLocation(player.getLocation());
//		Location vilagerLoc = ((Villager) npc.getEntity()).getLocation();
//		vilagerLoc.setDirection(vilagerLoc.clone().subtract(player.getLocation()).toVector());
//		((Villager) npc.getEntity()).set
		
//		Debug.out(Arrays.toString(npc.getNavigator().getLocalParameters().examiners()));
//		npc.getNavigator().getLocalParameters().clearExaminers();
//		npc.getNavigator().getLocalParameters().examiner(new CitizensNavigatorExaminer());

		
		if (npc.hasTrait(SpawnSteve.class))
		{
			SpawnSteve.onClick(e);
			return;
		}
		
		Character charTrait = NpcUtils.getCharacter(npc);
		
		
		/*
		selectedNPC.put(player, npc);
		if (charTrait == null)
			player.sendMessage("NPC selected");
		else
			player.sendMessage("NPC selected: "+charTrait.getName());
		
//		player.sendMessage(" is "+((npc.getNavigator().isPaused())? "" : "not ") + "paused.");
//		player.sendMessage(" is "+((npc.getNavigator().isNavigating())? "" : "not ") + "navigating.");
		Location loc = npc.getNavigator().getTargetAsLocation();
		if (loc != null)
		{
			player.sendMessage(" is going to "+loc.getX()+", "+loc.getY()+", "+loc.getZ());
			ShowBlockChange.showAs(loc, Material.BEACON, Arrays.asList(new Player[]{player}), 100);
		}
		*/
		if (charTrait == null || charTrait.homeBuilding == null) {
			Debug.out("NPC without trait! destroying..");
			npc.destroy();
			return;
		}
		else
		{
			NpcUtils.recheckCitizenAndBuilding(player, charTrait);
			
			
			
			if (charTrait instanceof Character)
			{
				if (player.isOp() && Debug.showSearchSpaceDebug)
					((ChestCharacter) charTrait).showSearchSpaceToUser(player);
			}
		}
		
		
		if(FactionUtils.getFaction(player).equals(charTrait.homeBuilding.city.getFaction()))
		{
			if (!(charTrait instanceof ChestCharacter))
				return;
			player.sendMessage(NpcUtils.getNPCinfo(player, (ChestCharacter) charTrait));
		}
//		player.openInventory(Bukkit.createInventory(player, InventoryType.MERCHANT));
		
		
	}

}
