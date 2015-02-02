package commands;


import interaction.UberToolsListener;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.massivecraft.massivecore.util.Txt;

public class CmdMCityUberTools extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityUberTools()
	{
		// Aliases
		this.addAliases("u", "ubertools");

		// Args
//		this.addOptionalArg("optionalArg", "");
		
		this.setDesc("get all uber tools");
		this.setHelp("This command is used to get all Op tools");
		
		// Requirements
//		this.addRequirements(ReqFactionsEnabled.get());
//		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform()
	{
		if(player.isOp())
		{
	//		int defaultInt = 1;
	//		int index = 0;
	//		Integer integerArg = this.arg(index, ARInteger.get(), defaultInt);
	//		if (integerArg == null) 
	//			return;
	
			sendMessage("Giving you all uber tools");
			
			LinkedList<ItemStack> tools = new LinkedList<ItemStack>();
			for (String name : UberToolsListener.tools)
			{
				ItemStack tool = new ItemStack(Material.BLAZE_ROD);
				ItemMeta meta = tool.getItemMeta();
				meta.setDisplayName(name);
				tool.setItemMeta(meta);
				tools.add(tool);
			}
			HashMap<Integer, ItemStack> leftOvers = this.player.getInventory().addItem(tools.toArray(new ItemStack[0]));
			for (ItemStack leftover : leftOvers.values())
				player.getWorld().dropItem(player.getLocation().add(0, 1, 0), leftover);
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
		
	}

}
