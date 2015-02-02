package commands;

import org.bukkit.inventory.ItemStack;

import com.massivecraft.massivecore.util.Txt;



public class CmdFactionVillagersFullStack extends FVCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionVillagersFullStack()
	{
		// Aliases
		this.addAliases("f", "fullstack");

		// Args
		this.addOptionalArg("optionalArg", "");
		
		this.setDesc("make item in hand a stack of 64");
		this.setHelp("for OPs only");
		
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
			ItemStack item = player.getItemInHand();
			item.setAmount(64);
			player.setItemInHand(item);
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
