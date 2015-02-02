package buildings;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Material;
import org.bukkit.block.Block;

import utils.BlockChecker;
import buildings.BuildingRequirement.HasBlock;
import city.City;

public abstract class HomeWorkBuilding extends ChestBuilding {

	@Override
	public void addTraits(NPC npc) {
		super.addTraits(npc);
	}

	
	@Override public String getDebugInfo() {
		String ret = super.getDebugInfo();
		return ret;
	}

	public HasBlock jobBlockRequirement = new HasBlock(BlockChecker.checkFor(getJobBlockMaterial()));
	
	public HomeWorkBuilding(City city, Block b) {
		super(city, b);
		requirements.add(jobBlockRequirement);
	}

	abstract Material getJobBlockMaterial();
	

}
