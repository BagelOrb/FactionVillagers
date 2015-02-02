package commands;

import main.MCity;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.ChatColor;

import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.util.Txt;

/**
 * This class is no longer used and is crappy code which only works foor woodcutter
 * @author TK
 *
 */
@Deprecated
public class CmdMCityNpcGo extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityNpcGo()
	{
		// Aliases
		this.addAliases("g", "go");

//		// Args
//		this.addOptionalArg("npc-id", "-1");

		this.setDesc("set a npc to work");
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
			//This block of code will allow your users to specify 
			// /mcc [command] OR /mcc # [command]
			//The first will run the command on the selected NPC, the second on the NPC with npcID #.
			Integer npcid = this.arg(0, ARInteger.get(), 1);
			if (npcid == null) return;
			
			
	
			
			
			//if you don't need to handle commands for specific NPCs, you can stop here and remove all below.
	
	
	
			//Now lets find the NPC this should run on.
			NPC npc;
			if (npcid == -1){
				//sender didn't specify an id, use his selected NPC.
				npc =	((Citizens)	MCity.getCurrentPlugin().getServer().getPluginManager().getPlugin("Citizens")).getNPCSelector().getSelected(sender);
				if(npc != null ){
					// Gets NPC Selected for this sender
					npcid = npc.getId();
				}
				else{
					//no NPC selected.
					sender.sendMessage(ChatColor.RED + "You must have a NPC selected to use this command");
					return;
				}			
			}
	
			try {
				npc = CitizensAPI.getNPCRegistry().getById(npcid);
			} catch (IllegalArgumentException e) {
				sendMessage(Txt.parse("<b>Invalid id"));
				return;
			}
					
			if (npc == null) {
				//speicifed number doesn't exist.
				sender.sendMessage(ChatColor.RED + "NPC with id " + npcid + " not found");
				return;
			}
	
	
			//	If you need access to the instance of Woocutter on the npc, get it like this
	//		Woodcutter trait =null;
	//		if (!npc.hasTrait(Woodcutter.class)) {
	//			sender.sendMessage(ChatColor.RED + "That command must be performed on a npc with trait: " + Woodcutter.class.toString() );
	//			return;
	//		}
	//		else trait = npc.getTrait(Woodcutter.class);
			//
	
	//		if (args2.[0].equalsIgnoreCase("go")) {
	//		if (args.get(1).equalsIgnoreCase("go")) {
				//Do something to the NPC or trait
	//		trait.start();
				return;
	//		}
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}

	}

}
