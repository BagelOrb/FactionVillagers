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

public class Weavery extends HomeWorkBuilding {

	@Override
	public void addTraits(NPC npc) {
		super.addTraits(npc);
	}
	@Override
	EntityType getNPCEntityType() {
		return EntityType.VILLAGER;
	}

//	@Override
//	public String getTypeName() {
//		return "Bakery";
//	}
	static final String configYmlPath = configYmlPath_building+".weavery";
	@Override
	String getConfigYmlPath() {
		return configYmlPath;
	}	
	
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.WEAVER;
	}
	
	@Override
	Material getJobBlockMaterial() {
		return Material.WORKBENCH;
	}

	
	public Weavery(City city, Block b) {
		super(city, b);
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
		return BuildingType.WEAVERY;
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
