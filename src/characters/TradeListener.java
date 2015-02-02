package characters;

import main.MCity;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import buildings.MetaDataUtils;


public class TradeListener implements Listener {

	public TradeListener(MCity mCity) {
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent event) {

		
//		Debug.out(event);
//		Debug.out(event.getView().getBottomInventory());
//		Debug.out(event.getView().getItem(0));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(EntityTargetEvent event) {
		if (event.getEntity() instanceof Villager)
			event.setCancelled(true);
		if (event.getTarget() instanceof Villager)
		{
			if (!MetaDataUtils.getBuildings(event.getTarget()).isEmpty())
				event.setCancelled(true);
		}
	}
}
