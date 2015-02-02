package commands;


import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.util.Txt;

public class CmdMCityPROTOTYPE extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityPROTOTYPE()
	{
		// Aliases
		this.addAliases("s", "sub");

		// Args
		this.addOptionalArg("integerArg", "1");
		
		this.setDesc("do a subcommand");
		this.setHelp("This command is used to try out commands");
		
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
			int defaultInt = 1;
			int index = 0;
			Integer integerArg = this.arg(index, ARInteger.get(), defaultInt);
			if (integerArg == null) return;
	
			sendMessage("performing SubCommand!");
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
