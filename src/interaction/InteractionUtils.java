package interaction;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InteractionUtils {

	public static void decreasePlayersItemInHandByOne(Player player) {
		ItemStack itemInHand = player.getItemInHand();

		if (itemInHand.getAmount()==1)
		{
			player.setItemInHand(null);
			player.updateInventory();
		}
		else
			itemInHand.setAmount(itemInHand.getAmount()-1);
	}
}
