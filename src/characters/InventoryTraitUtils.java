package characters;

import generics.Tuple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.citizensnpcs.api.trait.trait.Inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryTraitUtils {

	public static boolean add(Inventory inventory, ItemStack gainedItems) {
		ItemStack[] contents = inventory.getContents();
		return add(contents, gainedItems);
	}
	public static boolean add(ItemStack[] contents, ItemStack gainedItems) {
		for (int i = 0; i<contents.length; i++)
		{

			if (contents[i] != null && contents[i].isSimilar(gainedItems))
			{
				contents[i].setAmount(contents[i].getAmount() + gainedItems.getAmount());
				return true;
			}
		}
		for (int i = 0; i<contents.length; i++)
		{
			if (contents[i] == null)
			{
				contents[i] = gainedItems.clone();
				return true;
			}
		}
		// otherwise:
		return false;
	}
	@Deprecated
	public static ItemStack remove(Inventory inventory, ItemStack toBeRemoved) {
		ItemStack[] contents = inventory.getContents();
		return remove(contents, toBeRemoved);
	}
	@Deprecated
	public static ItemStack remove(ItemStack[] contents, ItemStack toBeRemoved) {
		int amountToBeRemoved = toBeRemoved.getAmount();
		for (int i = 0; i<contents.length; i++)
		{
			if (contents[i] == null)
			{
				continue;
			}
			else if (contents[i].getType() == toBeRemoved.getType()) // TODO: check durability?
			{
				int amounthere = contents[i].getAmount();
				if (amounthere > amountToBeRemoved) {
					contents[i].setAmount(contents[i].getAmount() - toBeRemoved.getAmount());
					return null;
				}
				else if (amounthere == amountToBeRemoved) {
					contents[i] = null;
					return null;
				} else {
					amountToBeRemoved -= amounthere;
					contents[i] = null;
				}
			}
		}
		// otherwise:
		ItemStack leftOver = toBeRemoved.clone();
		leftOver.setAmount(amountToBeRemoved);
		return leftOver;
	}
	public static Tuple<List<ItemStack>,List<ItemStack>> get(Inventory inventory, Material mat, int amountToBeRemoved) {
		ItemStack[] contents = inventory.getContents();
		return get(contents, mat, amountToBeRemoved);		
	}
	public static Tuple<List<ItemStack>, List<ItemStack>> get(ItemStack[] contents, Material mat, int amountToBeRemoved) {
		List<ItemStack> actuallyRemoved = new LinkedList<ItemStack>();
		for (int i = 0; i<contents.length; i++)
		{
			if (contents[i] == null)
			{
				continue;
			}
			else if (contents[i].getType() == mat 
					|| (mat== Material.LOG && contents[i].getType() == Material.LOG_2)) // TODO: check durability?
			{
				int amounthere = contents[i].getAmount();
				if (amounthere > amountToBeRemoved) {
					ItemStack ret = contents[i].clone();
					ret.setAmount(amountToBeRemoved);
					actuallyRemoved.add(ret);
					contents[i].setAmount(contents[i].getAmount() - amountToBeRemoved);
					return new Tuple<List<ItemStack>, List<ItemStack>>(actuallyRemoved, new LinkedList<ItemStack>());
				}
				else if (amounthere == amountToBeRemoved) {
					ItemStack ret = contents[i].clone();
					ret.setAmount(amountToBeRemoved);
					actuallyRemoved.add(ret);
					contents[i] = null;
					return new Tuple<List<ItemStack>, List<ItemStack>>(actuallyRemoved, new LinkedList<ItemStack>());
				} else {
					ItemStack ret = contents[i].clone();
					ret.setAmount(amountToBeRemoved);
					actuallyRemoved.add(ret);
					amountToBeRemoved -= amounthere;
					contents[i] = null;
				}
			}
		}
		// otherwise:
		LinkedList<ItemStack> unremoved = new LinkedList<ItemStack>();
		unremoved.add(new ItemStack(mat, amountToBeRemoved));
		return new Tuple<List<ItemStack>, List<ItemStack>>(actuallyRemoved, unremoved);
	}

	public static void clear(Inventory inventory) {
		Arrays.fill(inventory.getContents(), null);
	}

	public static ItemStack[] getItems(Inventory inventory) {
		LinkedList<ItemStack> nonNullItemStacks = new LinkedList<ItemStack>();
		for (ItemStack is : inventory.getContents())
			if (is != null)
				nonNullItemStacks.add(is);
		
//		ItemStack[] ret = (ItemStack[]) ArrayUtils.removeElement(inventory.getContents(), null);
//		Debug.out("getItems > return="+Arrays.toString(ret));
		return nonNullItemStacks.toArray(new ItemStack[0]);
	}

	public static void main(String[] args) {
		String[] strs = new String[]{"fsa",null};
		
		LinkedList<String> q = new LinkedList<String>();
		q.addAll(Arrays.asList(strs));
	}
//	public static
	
	public static boolean containsAtLeast(Inventory inventory, Material matNeeded, int amountNeeded) {
		return containsAtLeast(inventory.getContents(), matNeeded, amountNeeded);
	}
	public static boolean containsAtLeast(ItemStack[] contents, Material matNeeded, int amountNeeded) {
		int amountFound = 0;
		for (ItemStack isInInv : contents) {
			if (isInInv == null) 
				continue;
			if (isInInv.getType() == matNeeded || (matNeeded== Material.LOG && isInInv.getType() == Material.LOG_2))
				amountFound += isInInv.getAmount();
			if (amountFound >= amountNeeded)
				return true;
		}
		return false;
	}
	public static HashMap<Integer,ItemStack> addItem(Inventory inventory, ItemStack... items) {
		return addItem(inventory.getContents(), items);
	}
	public static HashMap<Integer,ItemStack> addItem(ItemStack[] contents, ItemStack... items) {
		HashMap<Integer,ItemStack> unaddedItems = new HashMap<Integer,ItemStack>();
		for (int i = 0; i<items.length; i++)
			if (!add(contents, items[i]))
				unaddedItems.put((Integer) i, items[i]);
		return unaddedItems;
	}
	@Deprecated
	public static HashMap<Integer,ItemStack> removeItem(Inventory inventory, ItemStack... items) {
		HashMap<Integer,ItemStack> unremovedItems = new HashMap<Integer,ItemStack>();
		for (int i = 0; i<items.length; i++){
			ItemStack leftOverHere = remove(inventory, items[i]);
			if (leftOverHere != null)
				unremovedItems.put((Integer) i, leftOverHere);
		}
		return unremovedItems;
		
	}
	/**
	 * @param inventory
	 * @param items
	 * @return <the items removed from the inventory or null when there wasn't enough, the items he couldnt remove>
	 */
	public static Tuple<List<ItemStack>, List<ItemStack>> getItem(Inventory inventory, ItemStack... items) {
		return getItem(inventory.getContents(), items);
	}
	public static Tuple<List<ItemStack>, List<ItemStack>> getItem(ItemStack[] contents, ItemStack... items) {
		List<ItemStack> gottenItems = new LinkedList<ItemStack>();
		List<ItemStack> notGottenItems = new LinkedList<ItemStack>();
		for (int i = 0; i<items.length; i++){
			Tuple<List<ItemStack>, List<ItemStack>> gotten = get(contents, items[i].getType(), items[i].getAmount());
			gottenItems.addAll(gotten.fst);
			notGottenItems.addAll(gotten.snd);
		}
		return new Tuple<List<ItemStack>, List<ItemStack>>(gottenItems, notGottenItems);
		
	}

}
