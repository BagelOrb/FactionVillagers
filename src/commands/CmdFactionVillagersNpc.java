package commands;


public class CmdFactionVillagersNpc extends FVCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

//	CmdFactionVillagersNpcGo go = new CmdFactionVillagersNpcGo();
	CmdFactionVillagersNpcCome come = new CmdFactionVillagersNpcCome();
	CmdFactionVillagersNpcFollow follow = new CmdFactionVillagersNpcFollow();
	
	public CmdFactionVillagersNpc()
	{
		// Aliases
		this.addAliases("n", "npc");

//		this.addSubCommand(go);
		this.addSubCommand(come);
		this.addSubCommand(follow);

//		// Args
//		this.addOptionalArg("npc-id", "-1");

		this.setDesc("control npcs");
		this.setHelp("This command is used to control citizens");

		// Requirements
//		this.addRequirements(ReqFactionsEnabled.get());
//		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //


}
