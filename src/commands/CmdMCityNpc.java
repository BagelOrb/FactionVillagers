package commands;


public class CmdMCityNpc extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

//	CmdMCityNpcGo go = new CmdMCityNpcGo();
	CmdMCityNpcCome come = new CmdMCityNpcCome();
	CmdMCityNpcFollow follow = new CmdMCityNpcFollow();
	
	public CmdMCityNpc()
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
