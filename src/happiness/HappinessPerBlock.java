package happiness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import main.Debug;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class HappinessPerBlock implements HappinessEnhancement , ConfigurationSerializable  {

	private Material material;
	private double happiness;

	public HappinessPerBlock(final Material material, final double happiness) {
		this.material = material;
		this.happiness = happiness;
	}
	
	@Override
	public double enhancement(HashSet<Block> borderBlocks) {
		int amountBlocks = 0;
		for (Block block : borderBlocks)
			if (block.getType() == material)
				amountBlocks++;
		return amountBlocks * happiness;
	}

	/*  see MCity ConfigurationSerialization
	 * (non-Javadoc)
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("material", material.toString());
		ret.put("happiness", happiness);
		return ret;
	}
	
	/**
	 * needed for serialization!  see MCity ConfigurationSerialization
	 * @param map mapping keys in config.yml to their objects
	 * @return a new object of this type
	 */
	public static HappinessPerBlock deserialize(Map<String, Object> map) {
		try {
			Material mat = Material.valueOf((String) map.get("material"));
			Double hap = (Double) map.get("happiness");
			HappinessPerBlock ret = new HappinessPerBlock(mat, hap);
			return ret;
		} catch (Exception e) {
			Debug.out("ERROR");
			return null;
		}
	}
}