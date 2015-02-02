package commands;

import characters.NpcUtils;
import com.massivecraft.massivecore.util.Txt;

public class CmdMCityShow extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityShow()
	{
		// Aliases
		this.addAliases("s", "show");

		this.setDesc("show info on your city");
		this.setHelp("This command gives info on your city and the buildings inside");

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
		if (playerCity!=null) {
			sendMessage(NpcUtils.getGoldenDelimiterString("<good>"+playerCity.getFaction().getName()+" <green>Building Overview") + playerCity.getColoredBuildingsList("<white>"));
		}
		else sendMessage(Txt.parse("<bad>You must be in a faction to do that!"));
	}

}
