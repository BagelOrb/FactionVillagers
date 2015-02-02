package commands;

import com.massivecraft.massivecore.util.Txt;

public class CmdMCityUnemployed extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityUnemployed()
	{
		// Aliases
		this.addAliases("getu", "gu", "unemployed");

		this.setDesc("get a new unemployed villager to join your city");
		this.setHelp("This command gives you a new unemployed villager");

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
			if (playerCity !=null) 
			{
				if(playerCity.townHall != null)
				{
					if(playerCity.townHall.createUnemployed())
						sendMessage(Txt.parse("<good>Creating an unemplyed villager!"));
					else
						sendMessage(Txt.parse("<bad>There are not enough free beds in your Town Hall!"));
				}
				else
					sendMessage(Txt.parse("<bad>You must have a Town Hall in your city to do that!"));
			}
			else 
				sendMessage(Txt.parse("<bad>You must be in a city to do that!"));
		}
		else
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
	}

}
