package buildings;

import java.lang.reflect.InvocationTargetException;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;

import characters.CharacterType;
import city.City;

public class Sawmill extends HomeWorkBuilding {

	@Override
	public void addTraits(NPC npc) {
		super.addTraits(npc);
	}
	@Override
	EntityType getNPCEntityType() {
		return EntityType.VILLAGER;
	}

	static final String configYmlPath = configYmlPath_building+".sawmill";
	@Override
	String getConfigYmlPath() {
		return configYmlPath;
	}	
	
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.SAWMILL_WORKER;
	}
	
	@Override public String getDebugInfo() {
		String ret = super.getDebugInfo();
		return ret;
	}

	@Override
	Material getJobBlockMaterial() {
		return Material.WORKBENCH;
	}
	public Sawmill(City city, Block b) {
		super(city, b);
	}

	@Override public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
	}
	
	@Override public void destroy() {
		super.destroy();
	}
	
	@Override
	public BuildingType getBuildingType() {
		return BuildingType.SAWMILL;
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
