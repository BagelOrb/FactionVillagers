package buildings;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import main.Debug;
import main.MCity;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import buildings.BuildingRequirement.HasMatWithFreeSpace;
import characters.CharacterType;
import characters.TradeVillagerWalker;
import characters.Unemployed;
import characters.VillagerUtils;
import city.City;

import com.massivecraft.massivecore.util.Txt;

public abstract class BuildingWithTradeVillager extends ChestBuilding {

	private final HasMatWithFreeSpace traderBlockRequirement = new HasMatWithFreeSpace(getTraderJobBlockRequirement());
	
	abstract Material getTraderJobBlockRequirement();
	
	public Villager villager;

	public BuildingWithTradeVillager(City city, Block startingBlock) {
		super(city, startingBlock);
		requirements.add(traderBlockRequirement);
	}

	@Override
	public int getNumberOfVillagersNeeded() {
		return 2;
	}

//	@Override
//	String getConfigYmlPath() {
//		return null;
//	}
	
	


	@Override public void destroy() {
		super.destroy();
		if (villager != null && !villager.isDead())
			villager.remove();
	}
	
	@Override public List<String> recheck() {
		List<String> errors = super.recheck();

		Location traderSpawnLocation = traderBlockRequirement.getLocation();
		if (traderSpawnLocation == null) {
			traderSpawnLocation = homeLocationRequirement.getHomeLocation();
		}

		if (villager != null)
		{
			if(villager.isDead() || !villager.isValid())
			{
				Debug.out(Txt.parse("<bad>Trading villager is dead or invalid! <i>Creating one..."));
				villager.remove();
				createTradeVillager.run();
			}
		}
		else
		{
			Debug.out(Txt.parse("<bad>Trading villager is null. <i>Creating one..."));
			createTradeVillager.run();
		}
		
		
		
		villager.teleport(traderSpawnLocation);
		
		//Debug.out("Trading villager teleported to: "+traderSpawnLocation);
		//Debug.out("Villager location: " + villager.getLocation());
		
		if (errors.size()>0)
			VillagerUtils.setTrades(villager); // no trades!
		else
			VillagerUtils.setTrades(villager, getTrades());
		return errors;
	}
	
	@Override
	public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
		
		createTradeVillagerWalker();
	}
	
	private void createTradeVillagerWalker() {
		if (this instanceof TownHall)
			createTradeVillager.run();
		else
		{
			Unemployed unemployed = city.townHall.getPool().remove(0); // villagerPoolSizeRequirement.chosen.get(1);
			Location spawnLocation = unemployed.getNPC().getEntity().getLocation();
			
			unemployed.destroy();
			unemployed.getNPC().destroy();
			
			NPC npc = CitizensAPI.getNPCRegistry().createNPC(getNPCEntityType(), getTraderCharacterNamePretty());
			npc.spawn(spawnLocation);
			
			
			TradeVillagerWalker trait = new TradeVillagerWalker();
			trait.construct(this, traderBlockRequirement.getLocation(), createTradeVillager);
			npc.addTrait(trait);
		}
		
	}

	BukkitRunnable createTradeVillager = new BukkitRunnable() {
		
		@Override
		public void run() {
			Debug.out("creating trade villager...");
			Location traderSpawnLocation = traderBlockRequirement.getLocation();
			if (traderSpawnLocation == null)
				traderSpawnLocation = homeLocationRequirement.getHomeLocation();
			villager = (Villager) startingBlock.getWorld().spawnEntity(traderSpawnLocation, EntityType.VILLAGER);
			villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
			villager.setAdult();
			villager.setBreed(false);
			villager.setRemoveWhenFarAway(false);
			villager.setCanPickupItems(false);
			
			villager.setProfession(getTraderCharacterType().profession);
			VillagerUtils.setName(villager, getTraderCharacterNamePretty());
			if (!isActive())
				VillagerUtils.setTrades(villager); // no trades!
			else
				VillagerUtils.setTrades(villager, getTrades());
//		VillagerUtils.setInvulnerable(villager, true);
			
			MetaDataUtils.setBelongingTo(villager, BuildingWithTradeVillager.this);
			
		}
	};

	
	public abstract Collection<Trade> getTrades();
	
	public String getTraderCharacterNamePretty()
	{
		return getTraderCharacterType().prettyPrint() + ((this.isUnique()) ? "" : " "+this.buildingId);
	}
	
	public abstract CharacterType getTraderCharacterType();
//	abstract EntityType getNPCEntityType();

	
	
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = super.toJsonObjectBuilder();
		if (villager == null) 
		{
			Debug.warn("SAVE: No NPC assigned to "+this.toString()+"!!");
		}
		ret.add("villagerId", villager.getUniqueId().toString());
		return ret;

	}
	
	public void loadJsonObjectFields(JsonObject o) {
        super.loadJsonObjectFields(o);
		String uuidStr = o.getString("villagerId", null);
		if (uuidStr == null)
		{
			Debug.warn("NPC not found! for "+this.getFullyQualifiedId());
			return;
		}

		final UUID villagerId = UUID.fromString(uuidStr);
//		new BukkitRunnable() {
//			
//			@Override
//			public void run() {
				for(Entity e: MCity.getCurrentPlugin().getServer().getWorld("world").getEntities())
				{
					if(e.getUniqueId().equals(villagerId))
					{
						villager = (Villager) e;
						MetaDataUtils.setBelongingTo(villager, this);
						break;
					}
				}
				
//			}
//		}.runTaskLater(MCity.getCurrentPlugin(), 40);
	}

}
