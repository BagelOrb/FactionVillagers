package buildings;

import java.lang.reflect.InvocationTargetException;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;

import characters.CharacterType;
import city.City;

public class WheatFarm extends ChestBuilding {

	static final String configYmlPath = configYmlPath_building+".wheatFarm";
	@Override
	String getConfigYmlPath() {
		return configYmlPath;
	}	
	@Override
	public void addTraits(NPC npc) {
		super.addTraits(npc);
	}
	
	@Override
	EntityType getNPCEntityType() {
		return EntityType.VILLAGER;
	}

	
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.WHEAT_FARMER;
	}
	
	@Override public String getDebugInfo() {
		String ret = super.getDebugInfo();
//						"woodcutterNPC id="+((woodcutterNPC==null)? "null" : this.woodcutterNPC.getId())+"\n";
		return ret;
	}

	
	public WheatFarm(City city, Block b) {
		super(city, b);
//		requirements.add(BuildingRequirement.hasMaterial(Material.BED_BLOCK));
	}


	@Override public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
	}
	
	@Override public void destroy() {
		super.destroy();

	}
	
	@Override
	public void setBelongingBlocks() {
		super.setBelongingBlocks();
	}
	@Override
	public BuildingType getBuildingType() {
		return BuildingType.WHEAT_FARM;
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = super.toJsonObjectBuilder();

		return ret;
	}
	
	@Override
	public Building fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
        this.loadJsonObjectFields(o);
		return this;
	}





	
	
	
}
