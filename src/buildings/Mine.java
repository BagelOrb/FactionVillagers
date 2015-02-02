package buildings;

import io.JsonBlock;

import java.lang.reflect.InvocationTargetException;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;

import utils.BlockChecker;
import buildings.BuildingRequirement.HasBlock;
import characters.CharacterType;
import city.City;

public class Mine extends ChestBuilding {

	@Override
	public void addTraits(NPC npc) {
		super.addTraits(npc);
	}
	@Override
	EntityType getNPCEntityType() {
		return EntityType.VILLAGER;
	}

	static final String configYmlPath = configYmlPath_building+".mine";
	@Override
	String getConfigYmlPath() {
		return configYmlPath;
	}	
	
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.MINE_WARDEN;
	}
	
	@Override public String getDebugInfo() {
		String ret = super.getDebugInfo();
		return ret;
	}

	public HasBlock poweredRailRequirement = new HasBlock(BlockChecker.checkFor(Material.CARPET));
	
	public Mine(City city, Block b) {
		super(city, b);
		requirements.add(BuildingRequirement.canOnlyBeBuiltOnce);
		requirements.add(poweredRailRequirement);
		//TODO: add requirement that the activator rail is connected to outside the building
	}
	
	@Override
	public boolean isUnique()
	{
		return true;
	}

	@Override public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
		city.mine = this;
	}
	
	@Override public void destroy() {
		super.destroy();
		city.mine = null;
	}
	
	@Override
	public void setBelongingBlocks() {
		super.setBelongingBlocks();
	}
	@Override
	public BuildingType getBuildingType() {
		return BuildingType.MINE;
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = super.toJsonObjectBuilder();
		ret.add("poweredRailBlock", JsonBlock.toJsonObjectBuilder(poweredRailRequirement.validBlock));
		return ret;
	}
	
	@Override
	public Building fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
        this.loadJsonObjectFields(o);
		JsonObject jsonPowRail = o.getJsonObject("poweredRailBlock");
		if (jsonPowRail != null)
		{
			poweredRailRequirement.validBlock = JsonBlock.fromJsonObject(jsonPowRail);
		}
		return this;
	}

}
