package happiness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import main.Debug;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class HappinessIfBlockPresent implements HappinessEnhancement , ConfigurationSerializable {
	
	public Material material;
	public double happiness;
	
	public HappinessIfBlockPresent(final Material material, final double happiness) {
		this.material = material;
		this.happiness = happiness;
	}
	
	@Override
	public double enhancement(HashSet<Block> borderBlocks) {
		for (Block block : borderBlocks)
			if (block.getType() == material)
				return happiness;
		return 0;
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
	 * needed for serialization! see MCity ConfigurationSerialization
	 * @param map mapping keys in config.yml to their objects
	 * @return a new object of this type
	 */
	public static HappinessIfBlockPresent deserialize(Map<String, Object> map) {
		try {
			Material mat = Material.valueOf((String) map.get("material"));
			Double hap = (Double) map.get("happiness");
			HappinessIfBlockPresent ret = new HappinessIfBlockPresent(mat, hap);
			return ret;
		} catch (Exception e) {
			Debug.out("ERROR");
			return null;
		}
	}
}