package commands;


import main.FactionVillagers;

import org.bukkit.entity.Player;

import city.City;

import com.massivecraft.massivecore.cmd.MassiveCommand;

public abstract class FVCommand extends MassiveCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public Player player;
	public City playerCity;

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void fixSenderVars()
	{
		this.playerCity = null;			

		if (sender instanceof Player)
		{
			player = (Player) sender;
			this.playerCity = FactionVillagers.getCity(player);
		}
		
		// Check disabled
//		if (UConf.isDisabled(sender)) return;

	}


}
