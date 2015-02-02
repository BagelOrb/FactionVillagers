package main;

import factions.ChunkListener;
import factions.FactionListener;
import factions.FactionUtils;
import happiness.HappinessIfBlockPresent;
import happiness.HappinessPerBlock;
import interaction.AllListener;
import interaction.AnvilListener;
import interaction.BlockListener;
import interaction.InteractListener;
import interaction.LiquidFlowListener;
import interaction.SpawnListener;
import interaction.StatisticsBookListener;
import interaction.UberToolsListener;
import io.JsonAble;
import jarUtils.JarUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import characters.CharacterType;
import characters.NPCListener;
import characters.Production;
import characters.SpawnSteve;
import characters.TreeListener;
import city.City;

import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.util.Txt;
import commands.CmdFactionVillagers;


public class FactionVillagers extends MassivePlugin implements JsonAble<FactionVillagers> {
	

	static {
		// register classes for config.yml serialization
		ConfigurationSerialization.registerClass(Production.class);
		ConfigurationSerialization.registerClass(HappinessPerBlock.class);
		ConfigurationSerialization.registerClass(HappinessIfBlockPresent.class);
		
	}
	
	public static HashSet<City> allCities = new HashSet<City>();

	public static World defaultWorld;
	
//	@Deprecated public static City daCity = new City();
//	static 
//	{
//		daCity.create();
//	}
	public final BlockListener blockListener = new BlockListener(this); 
	public final InteractListener interactListener = new InteractListener(this); 
	public final AnvilListener anvilListener = new AnvilListener(this); 
	public final FactionListener factionListener = new FactionListener(this); 
	public final ChunkListener chunkListener = new ChunkListener(this); 
	public final NPCListener npcListener = new NPCListener(this); 
	public final TreeListener treeListener = new TreeListener(this); 
	public final characters.TradeListener TradeListener = new characters.TradeListener(this); 
	public final AllListener AllListener = new AllListener(this); 
	public final UberToolsListener UberToolsListener = new UberToolsListener(); 
	public final LiquidFlowListener LiquidFlowListener = new LiquidFlowListener(); 
	public final StatisticsBookListener StatisticsBookListener = new StatisticsBookListener(); 
	public final SpawnListener SpawnListener = new SpawnListener();

	public NPC spawnSteve; 

	public static boolean keepFactionChunksLoaded;
	
	public static FactionVillagers getCurrentPlugin() {
//		return currentPlugin; 
		return (FactionVillagers) Bukkit.getServer().getPluginManager().getPlugin("FactionVillagers"); 
	}


//	public static void setCurrentPlugin(FactionVillagers currentPlugin) {
//		FactionVillagers.currentPlugin = currentPlugin;
//	}


	public void onEnable() {
//		setCurrentPlugin(this);
		
		//load default settings.
		reloadMyConfig();
		
		
		
		
		
		
//		try {
//			FileConfiguration conf = this.getConfig();
//			
//			LinkedList<BuildingHappinessEnhancement> l = new LinkedList<BuildingHappinessEnhancement>();
//			l.add(new HappinessPerBlock(Material.ACACIA_STAIRS, 1.2));
//			conf.set("crap", l);
//			conf.set("q.w", 1.2);
//		
//			conf.save(new File("crap.yml"));
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		
//		System.exit(0);
		
		
		
		
		
		
		//Register your trait with Citizens.        
//		net.citizensnpcs.api.
		for (CharacterType charType : CharacterType.values())
			try {
				if (charType.charClass != null)
					CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(charType.charClass).withName((String) charType.charClass.getField("traitName").get(null)));
			} catch (IllegalArgumentException | IllegalAccessException
					| NoSuchFieldException | SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(SpawnSteve.class).withName("spawnSteve"));
//			
//		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Woodcutter.class).withName(Woodcutter.traitName));	
//		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Farmer.class).withName(Farmer.traitName));	
//		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(Baker.class).withName(Baker.traitName));
//		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(RoamTrait.class).withName(RoamTrait.traitName));	

		

		/*
		 * Jar inclusion
		 */
		{
		    try { // para Jar code jabbed from inet
		        final File[] libs = new File[] {
		                new File(FactionVillagers.getCurrentPlugin().getDataFolder(), "lib\\javax.json-1.0.3.jar")
		                , new File(FactionVillagers.getCurrentPlugin().getDataFolder(), "lib\\commons-io-2.4.jar") 
		                };
		        for (final File lib : libs) {
		            if (!lib.exists()) {
		                JarUtils.extractFromJar(lib.getName(),
		                        lib.getAbsolutePath());
		            }
		        }
		        for (final File lib : libs) {
		            if (!lib.exists()) {
		            	FactionVillagers.getCurrentPlugin().getLogger().warning(
		                        "There was a critical error loading My plugin! Could not find lib: "
		                                + lib.getName());
		                Bukkit.getServer().getPluginManager().disablePlugin(FactionVillagers.getCurrentPlugin());
		                return;
		            }
		            addClassPath(JarUtils.getJarUrl(lib));
		        }
		    } catch (final Exception e) {
		        e.printStackTrace();
		    }
		}
		
		defaultWorld = Bukkit.getServer().getWorld("world");
		
		//check if Citizens is present and enabled.
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(java.util.logging.Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}	
		
		IO.loadPlugin(this);
		IO.loadCities(); // only load! no recheck!		

		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.interactListener, this);
		pm.registerEvents(this.anvilListener, this);
		pm.registerEvents(this.factionListener, this);
		pm.registerEvents(this.chunkListener, this);
		pm.registerEvents(this.npcListener, this);
		pm.registerEvents(this.treeListener, this);
		pm.registerEvents(this.TradeListener, this);
		pm.registerEvents(this.UberToolsListener, this);
		pm.registerEvents(this.LiquidFlowListener, this);
		pm.registerEvents(this.StatisticsBookListener, this);
		pm.registerEvents(this.SpawnListener, this);
//		pm.registerEvents(this.AllListener, this);
		
		/*
		 * Faction stuff:
		 */
		keepFactionChunksLoaded = getCurrentPlugin().getConfig().getBoolean("keepFactionChunksLoaded");
		
		if(keepFactionChunksLoaded)
		{
			Debug.out(Txt.parse("<i>Loading all faction chunks..."));
			if(FactionUtils.loadAllFactionChunks())
				Debug.out(Txt.parse("<good>All faction chunks loaded!"));
			else
				Debug.out(Txt.parse("<bad>Not all faction chunks could be loaded!"));
		}
		
		/*
		 * JAR stuff:
		 */
		JarUtils.jarOnEnable();
        
		/*
		 * NPC stuff:
		 */
		
        
        //MCCharacters OnEnable
        
		outerCommand = new CmdFactionVillagers();
		outerCommand.register(getCurrentPlugin());
		
		

		
		new BukkitRunnable() {
			
			@Override
			public void run() {
			    IO.checkAllFactions();
			}
		}.runTaskLater(this, 10);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				for (City city : allCities)
					city.statistics.processDay();
			}
			
		}.runTaskTimer(this, 24000-defaultWorld.getTime(), 24000);
		// below this no code is executed!
		
		
	}

	


	/**
	 * for JAR inclusion
	 * @param url
	 * @throws IOException
	 */
	public static void addClassPath(final URL url) throws IOException { // para jar code jabbed from inet :P
	    final URLClassLoader sysloader = (URLClassLoader) ClassLoader
	            .getSystemClassLoader();
	    final Class<URLClassLoader> sysclass = URLClassLoader.class;
	    try {
	        final Method method = sysclass.getDeclaredMethod("addURL",
	                new Class[] { URL.class });
	        method.setAccessible(true);
	        method.invoke(sysloader, new Object[] { url });
	    } catch (final Throwable t) {
	        t.printStackTrace();
	        throw new IOException("Error adding " + url
	                + " to system classloader");
	    }
	}
	
	
	
	
	public void reloadMyConfig(){
		//This copies the config.yml included in your .jar to the folder for this plugin, only if it does not exist.
		FactionVillagers.getCurrentPlugin().saveDefaultConfig();
		//load this config.yml into memory
		FactionVillagers.getCurrentPlugin().reloadConfig();
	}
	
	public CmdFactionVillagers outerCommand;
	
	public void onDisable() { 
		IO.saveCities();
		IO.savePlugin();
	}

	/*
	 * for   onCommand(...) shit: see package commands
	 * 
	 * JBs command is now: \mc npc # go
	 * 
	 */
	
	public static City getCity(Player player) {
		return FactionUtils.factionIDToCity.get(FactionUtils.getFaction(player).getId());
	}


	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		return IO.toJsonObjectBuilder(this);
	}


	@Override
	public FactionVillagers fromJsonObject(JsonObject o) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		IO.pluginFromJsonObject(this, o); 
		return this;
	}
}
