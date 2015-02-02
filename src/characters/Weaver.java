package characters;

import generics.Tuple;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Weaver extends HomeWorker {
		
	public Weaver() {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	public CharacterType getCharacterType() { return CharacterType.WEAVER; }
	
	public static final String traitName = "weaver";
	
	@Override
	String getTraitName() {
		return traitName;
	}
	
	@Override
	Action doJob() {
		
		if(selectedProduction != null)
		{
			boolean hasConsumption = true;
			
			for (ItemStack toBeConsumed : selectedProduction.getItemsNeededToConsume())
				hasConsumption = hasConsumption && InventoryTraitUtils.containsAtLeast(inventory, toBeConsumed.getType(), toBeConsumed.getAmount());
			
			
			if (hasConsumption)
			{
				
				List<ItemStack> woolsConsumed = new LinkedList<ItemStack>();
				
				for (ItemStack consumed : selectedProduction.getItemsNeededToConsume()) // register consumption
					homeBuilding.city.statistics.consume(homeBuilding, consumed.getType(), consumed.getAmount());
				Tuple<List<ItemStack>, List<ItemStack>> getResult = InventoryTraitUtils.getItem(inventory, selectedProduction.getItemsNeededToConsume());
				for (ItemStack consumed : getResult.fst)
				{
					if (consumed.getType() == Material.WOOL)
					{
						for (int i = 0; i<consumed.getAmount(); i++)
						{
							ItemStack consumedStackOfOne = consumed.clone();
							consumedStackOfOne.setAmount(1);
							woolsConsumed.add(consumedStackOfOne);
						}
					}
				}
			
				
			
			
				for (ItemStack produced : selectedProduction.getItemsProduced())
				{
					if (produced.getType() == Material.CARPET)
					{
						for (int i = 0; i< produced.getAmount(); i++)
						{
							ItemStack carpet = new ItemStack(Material.CARPET, 1);
							carpet.setDurability(woolsConsumed.get(i % woolsConsumed.size()).getDurability());
							InventoryTraitUtils.add(inventory, carpet);
						}
					} else 
					{
						InventoryTraitUtils.add(inventory, produced);
					}
					// register production:
					homeBuilding.city.statistics.produce(homeBuilding, produced.getType(), produced.getAmount());
					
				}
				
			}
		}
		
		return getJobToHomeNavigatorAction(); 
	}


}
