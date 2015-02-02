package utils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

	public static void restackAllNonFullStacks(Inventory inv) {
		ItemStack[] allContents = inv.getContents();
		inv.clear();
		for (ItemStack itemStack : allContents)
			inv.addItem(itemStack);
	}
}
