package commands;

import org.bukkit.inventory.ItemStack;

import buildings.Trade;
import characters.CharacterType;

import com.massivecraft.massivecore.util.Txt;

public class CmdFactionVillagersMayor extends FVCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionVillagersMayor()
	{
		// Aliases
		this.addAliases("m", "mayor", "paper");

		this.setDesc("get a paper to hire a Mayor");
		this.setHelp("This command gives you a paper to hire a Mayor");

		// Args
//		this.addOptionalArg("page", "1");

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
			ItemStack hirePaper = Trade.getHirePaperFor(CharacterType.MAYOR);
			player.getInventory().addItem(hirePaper);
			sendMessage(Txt.parse("<good>Mayor paper given!"));
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
