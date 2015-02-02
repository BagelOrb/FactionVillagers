package commands;

import main.FactionVillagers;

import org.bukkit.ChatColor;

import com.massivecraft.massivecore.util.Txt;

public class CmdFactionVillagersReload extends FVCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionVillagersReload()
	{
		// Aliases
		this.addAliases("rel", "reload");

		this.setDesc("reload the config of the plugin");
		this.setHelp("This command reloads the config");

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
			FactionVillagers.getCurrentPlugin().reloadMyConfig();
			sendMessage(ChatColor.GREEN + "Reloaded!");
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
