package buildings;

import generics.Tuple;
import io.JsonBlock;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import utils.ChestUtils;
import utils.PlayerUtils;
import characters.Character;
import characters.InventoryTraitUtils;
import city.City;

public abstract class ChestBuilding extends BuildingWithStorage {

	
	public Chest mainChest;
	
	
	@Override public String getDebugInfo() {
		String ret = super.getDebugInfo() +
						"mainChest="+this.mainChest.getLocation()+"\n";
		return ret;
	}
	
	public ChestBuilding(City city, Block b) {
		super(city, b);
		requirements.add(BuildingRequirement.hasFreeChestSpace);
	}

	
	public void create(City city, PlayerInteractEvent event) {
		super.create(city, event);
		
		startingBlock.setType(Material.CHEST);
		mainChest = ((Chest) startingBlock.getState());
		ChestUtils.setDirection(startingBlock, PlayerUtils.getCardinalDirection4(event.getPlayer()).getOppositeFace());
		MetaDataUtils.setBelongingTo(startingBlock, this);
	}
	@Override
	public void setBelongingBlocks() {
		
	}
	
	@Override
	public void addTraits(NPC npc) {
		Inventory invTrait = new Inventory();
		Class<? extends Character> charClass = this.getCharacterType().charClass;
		
		try {
			Character trait = charClass.getConstructor().newInstance();
			trait.construct(this, homeLocationRequirement.getHomeLocation());
			npc.addTrait(trait);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		npc.addTrait(invTrait);
	}



	@Override public void destroy() {
		super.destroy();
		MetaDataUtils.removeBelongingTo(startingBlock, this);
		}
	
	
	@Override
	public boolean containsAtLeast(ItemStack item, int amount) {
//		if (item.getType() == Material.LOG)
//		{
//			ItemStack item2 = item.clone();
//			item2.setType(Material.LOG_2);
//			return mainChest.getBlockInventory().containsAtLeast(item, amount) || mainChest.getBlockInventory().containsAtLeast(item2, amount);
//		}
//		return mainChest.getBlockInventory().containsAtLeast(item, amount);
		return InventoryTraitUtils.containsAtLeast(mainChest.getBlockInventory().getContents(), item.getType(), item.getAmount());
	}
	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
		LinkedList<ItemStack> items2 = new LinkedList<ItemStack>();
		for (ItemStack it : items)
			items2.add(it);
		return mainChest.getBlockInventory().addItem(items2.toArray(new ItemStack[items.length]));
	}
	@Deprecated @Override 
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
		LinkedList<ItemStack> items2 = new LinkedList<ItemStack>();
		for (ItemStack it : items)
			items2.add(it);
		return mainChest.getBlockInventory().removeItem(items2.toArray(new ItemStack[items.length]));
	}
	@Override
	public Tuple<List<ItemStack>, List<ItemStack>> getItem(ItemStack... items) {
		return InventoryTraitUtils.getItem(mainChest.getBlockInventory().getContents(), items);
	}


	@Override 
	public List<String> recheck() {
		List<String> errors = super.recheck();
		
		if (mainChest == null 
				&& startingBlock != null 
				&& startingBlock.getState() instanceof Chest) 
		{
			mainChest = ((Chest) startingBlock.getState());
		}
		if (mainChest ==null || !mainChest.getBlock().equals(startingBlock))
		{
			destroy();
			return Arrays.asList(new String[]{" doesn't have its starting block! Destroyed building!"});
		}
		return (errors);
	}
	
	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder ret = super.toJsonObjectBuilder();
		ret.add("mainChest", JsonBlock.toJsonObjectBuilder(mainChest.getBlock()));
		return ret;
	}


	public void loadJsonObjectFields(JsonObject o) {
        super.loadJsonObjectFields(o);
		BlockState maybeChest = JsonBlock.fromJsonObject(o.getJsonObject("mainChest")).getState();
		if (maybeChest instanceof Chest)
			mainChest = (Chest)  maybeChest;
	}
}
