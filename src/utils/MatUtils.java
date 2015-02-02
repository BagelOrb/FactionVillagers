package utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.MCity;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import characters.CharacterType;
import characters.ChestCharacter;
import characters.Production;

public class MatUtils {

	public static String prettyPrint(Material mat) {
		if (mat==null)
			return "Empty";
		
		switch (mat) {
		case WORKBENCH:
			return "Crafting Table";
		case LOG:
			return "Wood";
		case LOG_2:
			return "Wood";
		case WOOD:
			return "Wood Planks";
		default:
			String str = mat.name();
			str = str.replace('_', ' ').toLowerCase();
			str = WordUtils.capitalize(str);
			return str;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(prettyPrint(Material.BAKED_POTATO));
	}
	
	public static enum MatType {
//		ORE(new SetMat(null, this));
//		
//		private Mat mat;
//
//		MatType(Mat mat) {
//			this.mat = mat;
//		}
	}
	

	@SuppressWarnings("unchecked")
	public static Set<Material> getMaterialsUsedByVillagers() {
		try {
			
			
			FileConfiguration config = MCity.getCurrentPlugin().getConfig();
			
			HashSet<Material> mats = new HashSet<Material>();
			for (CharacterType ch : CharacterType.values())
			{
				if (ch.charClass != null && ChestCharacter.class.isAssignableFrom(ch.charClass))
				{				
					String traitName;
						traitName = (String) ch.charClass.getField("traitName").get(null);
					List<ItemStack> itemsNeededToEat = (List<ItemStack>) config.getList("character."+traitName+".itemsNeededToEat");
					for (ItemStack q : itemsNeededToEat)
						mats.add(q.getType());
					List<Production> prods = (List<Production>) config.getList("character."+traitName+".productions");
					for (Production q : prods)
					{
						for (ItemStack w : q.itemsNeededToConsume)
							mats.add(w.getType());
						for (ItemStack w : q.itemsProduced)
							mats.add(w.getType());
					}
				}
			}
			
			return mats;
			
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
}
