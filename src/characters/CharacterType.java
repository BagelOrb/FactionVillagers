package characters;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Villager.Profession;

public enum CharacterType {
	WHEAT_FARMER		(WheatFarmer.class, 	Profession.FARMER, 		"Collects and replants nearby wheat."),
	WOODCUTTER			(Woodcutter.class, 		Profession.FARMER, 		"Collects wood by chopping down nearby trees."),
	BAKER				(Baker.class, 			Profession.LIBRARIAN, 	"Bakes bread from wheat."),
	STORAGE_KEEPER		(StorageKeeper.class,	Profession.PRIEST, 		"Manages the city storage room."),
	COW_FARMER			(CowFarmer.class, 		Profession.FARMER, 		"Collects leather and raw beef from nearby cows."),
	CHICKEN_FARMER		(ChickenFarmer.class, 	Profession.FARMER, 		"Collects raw chicken and feathers from nearby chickens."),
	SHEEP_HERDER		(SheepFarmer.class, 	Profession.FARMER, 		"Collects wool from nearby sheep."),
	SAWMILL_WORKER		(SawmillWorker.class, 	Profession.BLACKSMITH, 	"Makes planks from wood."),
	WEAVER				(Weaver.class, 			Profession.BUTCHER, 	"Weaves carpet from wool."),
	MINER				(Miner.class, 			Profession.BLACKSMITH, 	"Mines tunnels at the city mine."),
	MINE_WARDEN			(MineWarden.class, 		Profession.PRIEST, 		"Manages the city mine."),
	CONTRACTOR_HELPER	(ContractorHelper.class,Profession.BUTCHER, 	"Helps the Contractor."),
	BLACKSMITH_HELPER	(BlacksmithHelper.class,Profession.BUTCHER, 	"Collects items for the Blacksmith."),
	LIBRARIAN_HELPER	(LibrarianHelper.class,	Profession.BUTCHER,		"Collects items for the Librarian."),
	SUGAR_CANE_FARMER	(SugarCaneFarmer.class, Profession.FARMER, 		"Collects and replants nearby sugar canes."),
	BOOK_BINDER			(BookBinder.class,		Profession.PRIEST,		"Makes books from leather and sugar canes."),
	SMELTER				(Smelter.class,			Profession.BLACKSMITH,  "Smelts ores into ingots."),
	BLACKSMITH 			(null, 					Profession.BLACKSMITH,	"Sells diamond armor and tools."), 
	LIBRARIAN 			(null, 					Profession.PRIEST, 		"Sells enchanted books."), 
	NONE				(null,					Profession.BUTCHER,		"YOU'RE NOT SUPPOSED TO SEE THIS"),
	CONTRACTOR 			(null, 					Profession.PRIEST, 		"Sells papers to hire new villagers."), 
	MAYOR				(null,					Profession.PRIEST,		"Sells papers to hire new villagers."),
	UNEMPLOYED			(Unemployed.class,		Profession.FARMER,		"Can be assigned a job using a paper from the Mayor.");
	
	public final Class<? extends Character> charClass;
	public final Profession profession;
	public String description;
	
	CharacterType(Class<? extends Character> clazz, Profession prof, String desc) {
		charClass = clazz;
		profession = prof;
		description = desc;
	}
	
	
	public String prettyPrint() {
		String str = this.name();
		str = str.replace('_', ' ').toLowerCase();
		str = WordUtils.capitalize(str);
		return str;
	}
	
}
