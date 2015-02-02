package commands;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.block.Block;

import utils.WalkingGroundUtils;
import characters.Character;
import characters.NPCListener;
import characters.NpcUtils;

import com.massivecraft.massivecore.util.Txt;

public class CmdFactionVillagersNpcCome extends FVCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionVillagersNpcCome()
	{
		// Aliases
		this.addAliases("c", "come");

//		// Args
//		this.addOptionalArg("npc-id", "-1");

		this.setDesc("move an npc to where you are");
		this.setHelp("This command is used to command citizens");

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
			NPC npc = NPCListener.selectedNPC.get(this.player);
			if (npc == null)
			{
				sendMessage("No NPC selected!");
			}
			Block playerLocBlock = player.getWorld().getBlockAt(player.getLocation());
			Location toLoc = WalkingGroundUtils.getSolidUnder(playerLocBlock).getLocation().add(.5, 1, .5); 
			
			Character mcChar = NpcUtils.getCharacter(npc);
			if (mcChar != null || mcChar instanceof Character)
			{
				Character cchar = (Character) mcChar;
				cchar.rerouteVia(toLoc); 
			}
			else
				npc.getNavigator().setTarget(toLoc);
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
