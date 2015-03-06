package commands;

import java.util.Arrays;

import com.massivecraft.massivecore.cmd.HelpCommand;


public class CmdFactionVillagers extends FVCommand {

	public CmdFactionVillagersReload cmdFactionVillagersReload = new CmdFactionVillagersReload();
	public CmdFactionVillagersShow cmdFactionVillagersShow = new CmdFactionVillagersShow();
	public CmdFactionVillagersNpc cmdFactionVillagersNpc = new CmdFactionVillagersNpc();
	public CmdFactionVillagersRecheck cmdFactionVillagersRecheck = new CmdFactionVillagersRecheck();
	public CmdFactionVillagersDebug cmdFactionVillagersDebug = new CmdFactionVillagersDebug();
	public CmdFactionVillagersProduction cmdFactionVillagersProduction = new CmdFactionVillagersProduction();
	public CmdFactionVillagersMayor cmdFactionVillagersMayor = new CmdFactionVillagersMayor();
	public CmdFactionVillagersUnemployed cmdFactionVillagersUnemployed = new CmdFactionVillagersUnemployed();
	public CmdFactionVillagersUberTools cmdFactionVillagersUberTools = new CmdFactionVillagersUberTools();
	public CmdFactionVillagersSpawnSteve cmdFactionVillagersSpawnSteve = new CmdFactionVillagersSpawnSteve();
	public CmdFactionVillagersFullStack CmdFactionVillagersFullStack = new CmdFactionVillagersFullStack();
	//public CmdFactionVillagersIndustriousVillagers CmdFactionVillagersIndustriousVillagers = new CmdFactionVillagersIndustriousVillagers();
	
	
	public CmdFactionVillagers()
	{
		this.addSubCommand(HelpCommand.get());
		this.addSubCommand(this.cmdFactionVillagersNpc);
		this.addSubCommand(this.cmdFactionVillagersShow);
		this.addSubCommand(this.cmdFactionVillagersReload);
		this.addSubCommand(this.cmdFactionVillagersRecheck);
		this.addSubCommand(this.cmdFactionVillagersDebug);
		this.addSubCommand(this.cmdFactionVillagersProduction);
		this.addSubCommand(this.cmdFactionVillagersMayor);
		this.addSubCommand(this.cmdFactionVillagersUnemployed);
		this.addSubCommand(this.cmdFactionVillagersUberTools);
		this.addSubCommand(this.cmdFactionVillagersSpawnSteve);
		this.addSubCommand(this.CmdFactionVillagersFullStack);
		//this.addSubCommand(this.CmdFactionVillagersIndustriousVillagers);
		
		this.setDesc(" gives access to factionvillagers commands");
		this.setHelp("This command is used for factionvillagers commands");
		
		this.addAliases(Arrays.asList(new String[]{"fv", "factionvillagers"}));
	}
	
}
