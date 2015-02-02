package commands;


import java.util.List;

import main.MCity;

import org.apache.commons.lang.StringUtils;

import characters.NpcUtils;
import city.City;

import com.massivecraft.massivecore.util.Txt;

public class CmdMCityRecheck extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityRecheck()
	{
		// Aliases
		this.addAliases("r", "recheck");

		this.setDesc(" rechecks the validity of all cities and all buildings");
		this.setHelp("This command is used to validate all cities and buildings");
		
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
			for (City city : MCity.allCities)
			{
				NpcUtils.recheckAllNPCs();
				List<String> errors = city.recheck();
				if (errors.size()>0)
					sendMessage("City "+city.getFaction().getName()+ " contained errors: \n"+StringUtils.join(errors, ",\n"));
			}
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}	
	}

}
