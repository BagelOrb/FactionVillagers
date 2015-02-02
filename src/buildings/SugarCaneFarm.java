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

public class SugarCaneFarm extends ChestBuilding {

	static final String configYmlPath = configYmlPath_building+".sugarCaneFarm";
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
		return CharacterType.SUGAR_CANE_FARMER;
	}
	
	
	public SugarCaneFarm(City city, Block b) {
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
		return BuildingType.SUGAR_CANE_FARM;
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
