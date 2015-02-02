package commands;


import characters.NpcUtils;

import com.massivecraft.massivecore.util.Txt;

public class CmdMCityProduction extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityProduction()
	{
		// Aliases
		this.addAliases("o", "p", "production", "productions");

		// Args
//		this.addOptionalArg("optionalArg", "");
		
		this.setDesc("show production rates");
		this.setHelp("This command gives you info on the netto production of goods.");
		
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
//		int defaultInt = 1;
//		int index = 0;
//		Integer integerArg = this.arg(index, ARInteger.get(), defaultInt);
//		if (integerArg == null) 
//			return;
		
		
		if (this.arg(0) != null)
			sendMessage(Txt.parse(this.arg(0)));
		
		if(playerCity != null)
		{
			
			playerCity.statistics.computeStatistics();
			
			String msg = NpcUtils.getGoldenDelimiterString("<good>"+playerCity.getFaction().getName()+" <green>Production Overview");
			
			msg += playerCity.statistics.toStringNetto("<white>");
			msg += "\n \n";
			msg += playerCity.statistics.toStringNettoLastDay("<white>");
			
			sendMessage(Txt.parse(msg));
		}
		else sendMessage(Txt.parse("<bad>You must be in a faction to do that!"));
	}

}
