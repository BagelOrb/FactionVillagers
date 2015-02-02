package buildings;

import generics.Tuple;

import java.util.HashMap;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import main.Debug;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import characters.Character;
import characters.CharacterType;
import characters.ChestCharacter;
import characters.NpcUtils;
import characters.Unemployed;
import city.City;

public abstract class BuildingWithStorage extends Building {

	
//	public ChestCharacter npc;
	public int npcId;
	
	@Override
	public int getNumberOfVillagersNeeded() {
		return 1;
	}

	
	public BuildingWithStorage(City city, Block startingBlock) {
		super(city, startingBlock);
	}
	
	/**
	 * see http://jd.bukkit.org/beta/apidocs/org/bukkit/inventory/Inventory.html#containsAtLeast(org.bukkit.inventory.ItemStack, int)
	 */
	public abstract boolean containsAtLeast(ItemStack item, int amountNeeded);
	/**
	 * see http://jd.bukkit.org/beta/apidocs/org/bukkit/inventory/Inventory.html#containsAtLeast(org.bukkit.inventory.ItemStack, int)
	 */
	public abstract HashMap<Integer,ItemStack> addItem(ItemStack... items);
	/**
	 * see http://jd.bukkit.org/beta/apidocs/org/bukkit/inventory/Inventory.html#containsAtLeast(org.bukkit.inventory.ItemStack, int)
	 * use getItem instead!
	 */
	@Deprecated 
	public abstract HashMap<Integer,ItemStack> removeItem(ItemStack... items);
	/**
	 * @param items
	 * @return a tuple of what has been (re)moved and what hasn't
	 */
	public abstract Tuple<List<ItemStack>, List<ItemStack>> getItem(ItemStack... items);
	
	@Override public void destroy() {
		super.destroy();
		if (getCharacterType() == CharacterType.NONE)
			return;
		
		ChestCharacter trait = getChestCharacter();
		if (trait == null)
		{
			Debug.warn("Buildings NPC doesn't have a ChestCharacter Trait!!");
			return;
		}
		
		//otherwise
		Location spawnLoc = trait.getNPC().getEntity().getLocation();
		trait.destroy();
		trait.getNPC().destroy();
		
			
		if (city.townHall != null)
		{
			TownHall townHall = BuildingWithStorage.this.city.townHall;
			if (townHall!=null)
			{
				townHall.createUnemployed(spawnLoc);
			}
			
			
		}

	}
	
	
	@Override public List<String> recheck() {
		if (getNPC() == null)
			createNPC(homeLocationRequirement.getHomeLocation());
		
		if (getCharacterType() != CharacterType.NONE)
		{
			NPC npc = getNPC();
			Character charTrait = null;
			charTrait = NpcUtils.getCharacter(npc);
			if (charTrait == null)
			{
				createNPC(homeLocationRequirement.getHomeLocation());
				npc = getNPC();
				charTrait = NpcUtils.getCharacter(npc);
			}
	//		NpcUtils.recheckCitizen(player, charTrait);
			if (charTrait.homeBuilding == null)
			{
				Debug.out("NPC without home building! Destroying...");
				charTrait.homeBuilding = this;
			}
			charTrait.recheck();
		}

		List<String> errors = super.recheck();
		return errors;
	}
	
	public ChestCharacter getChestCharacter() {
		NPC npc = getNPC();
		if (npc==null)
			return null;
		
		Character character = NpcUtils.getCharacter(npc);
		if (character == null)
			return null;
		if (character instanceof ChestCharacter)
			return (ChestCharacter) character;
		else
			return null;
	}
	public NPC getNPC() { 
		if (npcId==-1 || getCharacterType() == CharacterType.NONE) return null;
		try {
			return CitizensAPI.getNPCRegistry().getById(npcId);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);

		if (!(this instanceof TownHall))
		{
			Unemployed unemployed =   city.townHall.getPool().remove(0) ; // villagerPoolSizeRequirement.chosen.get(0);
			Location npcStartingLocation = unemployed.getNPC().getEntity().getLocation();
			
			unemployed.destroy();
			unemployed.getNPC().destroy();
			createNPC(npcStartingLocation);
			
		}
		else
		{
			createNPC(homeLocationRequirement.getHomeLocation());
		}

	}
	
	void createNPC(Location spawnLocation) {
			
			if (getCharacterType() != CharacterType.NONE)
			{
//				Debug.out("villagerPoolSizeRequirement="+villagerPoolSizeRequirement);
//				Debug.out(villagerPoolSizeRequirement.chosen);
//				Debug.out(villagerPoolSizeRequirement.chosen.size());
				
				
				NPC npc = CitizensAPI.getNPCRegistry().createNPC(getNPCEntityType(), getCharacterNamePretty());
				
				npcId = npc.getId();
				
				addTraits(npc);
				
				npc.spawn(spawnLocation);
				
			}	
		}
	abstract void addTraits(NPC npc);
	
	EntityType getNPCEntityType() {
		return EntityType.VILLAGER;
	}

	
	
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = super.toJsonObjectBuilder();
		if (npcId==-1) 
		{
			Debug.warn("SAVE: No NPC assigned to "+this.toString()+"!!");
		}
		ret.add("npcId", npcId);
		return ret;

	}
	
	public void loadJsonObjectFields(JsonObject o) {
        super.loadJsonObjectFields(o);
		npcId = o.getInt("npcId", -1);
		if (npcId == -1)
			Debug.warn("NPC not found! for "+getFullyQualifiedId());
//		npc = (ChestCharacter) NpcUtils.getCharacter(getNPC());
	}








}
