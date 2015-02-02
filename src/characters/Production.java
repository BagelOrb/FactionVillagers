package characters;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

//@SerializableAs("Production") // alias in config.yml ? ==: Production
public class Production implements ConfigurationSerializable   {

	public List<ItemStack> itemsNeededToConsume;
	public List<ItemStack> itemsProduced;

//	@Deprecated 
	public Production(ItemStack[] itemsNeededToConsume2, ItemStack[] itemsProduced2) {
//		super(itemsNeededToConsume2, itemsProduced2);
		itemsNeededToConsume = Arrays.asList(itemsNeededToConsume2);
		itemsProduced = Arrays.asList(itemsProduced2);
//		itemsNeededToConsume = itemsNeededToConsume2;
//		itemsProduced = itemsProduced2;
	}
	public Production(ArrayList<ItemStack> l, ArrayList<ItemStack> r) {
		itemsNeededToConsume = l; itemsProduced = r;
	}
	//	private ItemStack[] itemsNeededToConsume; 
	/**
	 * @return the itemsNeededToConsume
	 */
	public ItemStack[] getItemsNeededToConsume() {
		return itemsNeededToConsume.toArray(new ItemStack[itemsNeededToConsume.size()]);
	}
	/**
	 * @param itemsNeededToConsume the itemsNeededToConsume to set
	 */
	public void setItemsNeededToConsume(ItemStack[] itemsNeededToConsume) {
		this.itemsNeededToConsume = Arrays.asList(itemsNeededToConsume);
	}

	
//	private ItemStack[] itemsProduced;
	/**
	 * @return the itemsProduced
	 */
	public ItemStack[] getItemsProduced() {
		return itemsProduced.toArray(new ItemStack[itemsProduced.size()]);
	}
	/**
	 * @param itemsProduced the itemsProduced to set
	 */
	public void setItemsProduced(ItemStack[] itemsProduced) {
		this.itemsProduced = Arrays.asList(itemsProduced);
	}
	
	public String toString() {
		return "Production: \r\n\t itemsNeededToConsume="+itemsNeededToConsume+"\r\n\titemsProduced="+itemsProduced;
	}



	
	/* (non-Javadoc)
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("itemsNeededToConsume", itemsNeededToConsume);
		ret.put("itemsProduced", itemsProduced);
		return ret;
	}
	
	/**
	 * needed for serialization!
	 * @param map mapping keys in config.yml to their objects
	 * @return a new object of this type
	 */
	@SuppressWarnings("unchecked")
	public static Production deserialize(Map<String, Object> map) {
		ArrayList<ItemStack> l = (ArrayList<ItemStack>) map.get("itemsNeededToConsume");
		ArrayList<ItemStack> r = (ArrayList<ItemStack>) map.get("itemsProduced");
		Production ret = new Production(l,r);
		return ret;
	}
}