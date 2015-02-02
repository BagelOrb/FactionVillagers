package commands;

import main.MCity;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.entity.EntityType;

import characters.SpawnSteve;

import com.massivecraft.massivecore.util.Txt;



public class CmdMCitySpawnSteve extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCitySpawnSteve()
	{
		// Aliases
		this.addAliases("spawnSteve", "spawn steve");

		// Args
		this.addOptionalArg("optionalArg", "");
		
		this.setDesc("Make a new spawn steve");
		this.setHelp("This is very op");
		
		// Requirements
//		this.addRequirements(ReqFactionsEnabled.get());
//		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}


	@Override
	public void perform()
	{
		if(player.isOp())
		{
			sendMessage("setting spawn steve!");
			
			if (MCity.getCurrentPlugin().spawnSteve != null)
			{
				MCity.getCurrentPlugin().spawnSteve.destroy();
			}
			
			NPC newSteve = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Welcome");
			MCity.getCurrentPlugin().spawnSteve = newSteve;
			newSteve.addTrait(SpawnSteve.class);
			newSteve.spawn(player.getLocation());
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
