package commands;

import java.util.Arrays;

import com.massivecraft.massivecore.cmd.HelpCommand;


public class CmdMCity extends MCCommand {

	public CmdMCityReload cmdMCityReload = new CmdMCityReload();
	public CmdMCityShow cmdMCityShow = new CmdMCityShow();
	public CmdMCityNpc cmdMCityNpc = new CmdMCityNpc();
	public CmdMCityRecheck cmdMCityRecheck = new CmdMCityRecheck();
	public CmdMCityDebug cmdMCityDebug = new CmdMCityDebug();
	public CmdMCityProduction cmdMCityProduction = new CmdMCityProduction();
	public CmdMCityMayor cmdMCityMayor = new CmdMCityMayor();
	public CmdMCityUnemployed cmdMCityUnemployed = new CmdMCityUnemployed();
	public CmdMCityUberTools cmdMCityUberTools = new CmdMCityUberTools();
	public CmdMCitySpawnSteve cmdMCitySpawnSteve = new CmdMCitySpawnSteve();
	public CmdMCityFullStack CmdMCityFullStack = new CmdMCityFullStack();
	
	
	public CmdMCity()
	{
		this.addSubCommand(HelpCommand.get());
		this.addSubCommand(this.cmdMCityNpc);
		this.addSubCommand(this.cmdMCityShow);
		this.addSubCommand(this.cmdMCityReload);
		this.addSubCommand(this.cmdMCityRecheck);
		this.addSubCommand(this.cmdMCityDebug);
		this.addSubCommand(this.cmdMCityProduction);
		this.addSubCommand(this.cmdMCityMayor);
		this.addSubCommand(this.cmdMCityUnemployed);
		this.addSubCommand(this.cmdMCityUberTools);
		this.addSubCommand(this.cmdMCitySpawnSteve);
		this.addSubCommand(this.CmdMCityFullStack);
		
		this.setDesc(" gives access to factionvillagers commands");
		this.setHelp("This command is used for factionvillagers commands");
		
		this.addAliases(Arrays.asList(new String[]{"fv", "factionvillagers"}));
	}
	
}
