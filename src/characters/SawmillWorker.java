package characters;

import generics.Tuple;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import utils.BlockUtils;

public class SawmillWorker extends HomeWorker {
		
	public SawmillWorker() {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	public CharacterType getCharacterType() { return CharacterType.SAWMILL_WORKER; }
	
	public static final String traitName = "sawmillWorker";
	
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
				
				List<ItemStack> logsConsumed = new LinkedList<ItemStack>();
				
				for (ItemStack consumed : selectedProduction.getItemsNeededToConsume()) // register consumption
					homeBuilding.city.statistics.consume(homeBuilding, consumed.getType(), consumed.getAmount());
				Tuple<List<ItemStack>, List<ItemStack>> getResult = InventoryTraitUtils.getItem(inventory, selectedProduction.getItemsNeededToConsume());
				for (ItemStack consumed : getResult.fst)
				{
					if (consumed.getType() == Material.LOG || consumed.getType() == Material.LOG_2)
					{
						for (int i = 0; i<consumed.getAmount(); i++)
						{
							ItemStack consumedStackOfOne = consumed.clone();
							consumedStackOfOne.setAmount(1);
							logsConsumed.add(consumedStackOfOne);
						}
					}
				}
			
				
			
			
				for (ItemStack produced : selectedProduction.getItemsProduced())
				{
					if (produced.getType() == Material.WOOD)
					{
						for (int i = 0; i< produced.getAmount(); i++)
						{
							ItemStack wood = BlockUtils.logToWood(logsConsumed.get(i % logsConsumed.size()));
	//						ItemStack wood = new ItemStack(Material.WOOD, 1);
	//						wood.setDurability(logsConsumed.get(i % logsConsumed.size()).getDurability());
							InventoryTraitUtils.add(inventory, wood);
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
