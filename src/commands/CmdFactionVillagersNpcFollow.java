package commands;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;

import utils.EntityUtils;
import characters.CowFarmer.CowChecker;
import characters.NPCListener;

import com.massivecraft.massivecore.util.Txt;

public class CmdFactionVillagersNpcFollow extends FVCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionVillagersNpcFollow()
	{
		// Aliases
		this.addAliases("f", "follow");

//		// Args
//		this.addOptionalArg("npc-id", "-1");

		this.setDesc("let the npc follow you until it reaches you");
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
			npc.getNavigator().setTarget((Entity) EntityUtils.getCloseEntities(npc.getEntity(), new CowChecker(), 10, Cow.class).get(0), true);
	//		Block playerLocBlock = player.getWorld().getBlockAt(player.getLocation());
	//		Location toLoc = BlockUtils.getSolidUnder(playerLocBlock).getLocation().add(.5, 1, .5); 
	//		
	//		Character mcChar = NpcUtils.getCharacter(npc);
	//		if (mcChar != null || mcChar instanceof ChestCharacter3)
	//		{
	//			ChestCharacter3 cchar = (ChestCharacter3) mcChar;
	////			cchar.rerouteVia(player); 
	//		}
	//		else
	//			npc.getNavigator().setTarget(toLoc);
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
