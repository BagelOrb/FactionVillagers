package main;

import factions.FactionUtils;
import io.JsonUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import net.citizensnpcs.api.CitizensAPI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scheduler.BukkitRunnable;

import characters.NpcUtils;
import city.City;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.util.Txt;

public class IO {

	public static File baseSaveLocation = new File("plugins\\FactionVillagers\\saves\\");
	private static String pluginJsonFileString = "plugin.json";
	private static File pluginJsonFile = new File(baseSaveLocation+"\\"+pluginJsonFileString );

	
	/*
	 *  P L U G I N   I / O
	 */
	
	public static void savePlugin() {
		
		try {
			
	        Debug.out(Txt.parse("<i>Saving Plugin..."));
	        baseSaveLocation.mkdir();
			JsonUtils.writeToFile(FactionVillagers.getCurrentPlugin().toJsonObjectBuilder(), pluginJsonFile );
	        Debug.out(Txt.parse("<good>Saving complete!"));
		} catch (IOException e) {
	        Debug.out(Txt.parse("<bad>Saving Problem!!!!!!"));
			e.printStackTrace();
		}
	}	
	
	

	public static JsonObjectBuilder toJsonObjectBuilder(FactionVillagers factionVillagers) {
		JsonObjectBuilder ret = Json.createObjectBuilder();
		if (factionVillagers.spawnSteve != null)
			ret.add("steveNpcId", factionVillagers.spawnSteve.getId());
		return ret;
	}



	public static void loadPlugin(FactionVillagers factionVillagers) {
	    Debug.out(Txt.parse("<i>Loading Plugin..."));
	    
	    baseSaveLocation.mkdir();
	    try {
			JsonReader reader = Json.createReader(new FileInputStream(pluginJsonFile));
			JsonObject o = reader.readObject();
			pluginFromJsonObject(factionVillagers, o);
			Debug.out(Txt.parse("<good>Loading complete!"));
		} catch (FileNotFoundException e) {
			Debug.out(Txt.parse("<good>Loading plugin problem!"));
			e.printStackTrace();
		}
	    
	}


	public static void pluginFromJsonObject(final FactionVillagers factionVillagers, JsonObject o) {
		final int npcId = o.getInt("steveNpcId", -1);
		if (npcId == -1)
			Debug.warn("Spawn Steve NPC not found!");
		else
		{
			new BukkitRunnable() {
				
				@Override
				public void run() {
//					try {
						factionVillagers.spawnSteve = CitizensAPI.getNPCRegistry().getById(npcId);
//					} catch (IllegalArgumentException e) {
//						e.printStackTrace();
//					}
					
				}
			}.runTaskLater(factionVillagers, 20);
		}
	}
	
	
	
	/*
	 * C I T Y    I / O
	 */
	public static void saveCities() {
	
		try {
			
	        Debug.out(Txt.parse("<i>Saving Cities..."));
	        baseSaveLocation.mkdir();
			for (City city : FactionVillagers.allCities)
				JsonUtils.writeToFile(city.toJsonObjectBuilder(), new File(baseSaveLocation+"\\"+city.factionID+".json"));
	        Debug.out(Txt.parse("<good>Saving complete!"));
	        Debug.out(Txt.parse("<i>Creating Backup of City files..."));
	        IO.backupCurrentSaves();
		} catch (IOException e) {
	        Debug.out(Txt.parse("<bad>Saving Problem!!!!!!"));
			e.printStackTrace();
		}
	}

	public static void loadCities() {
	    Debug.out(Txt.parse("<i>Loading Cities and Buildings..."));
	    
	    Collection<? extends Faction> factions = FactionUtils.getAllFactions();
	    List<String> facIDs = new ArrayList<String>(factions.size());
	    for (Faction fac : factions)
	    	facIDs.add(fac.getId());
	    
	    baseSaveLocation.mkdir();
		try {
			for (File file : baseSaveLocation.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".json");
				}
			}))
			{
				if (file.equals(pluginJsonFile))
					continue;
				if (!facIDs.contains(file.getName().substring(0, file.getName().length()-5)))
				{
					Debug.warn(Txt.parse("<bad>Found city saves for unexisting faction!"));
					file.renameTo(new File(file+"Unexisting"));
					continue;
				}
				City city = JsonUtils.readFromFile(file, City.class);
				FactionVillagers.allCities.add(city);
				
				Debug.out(Txt.parse("<good>Loaded City "+city.getFaction().getName()));
			}
		} catch (IOException | IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Debug.out(Txt.parse("<good>Loading complete!"));
	    
	    
	}

	public static void backupCurrentSaves() throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
		Date date = new Date();
		File backupFolder = new File(baseSaveLocation+"\\backup\\backup "+dateFormat.format(date)+"\\");
		backupFolder.mkdirs();
		for (File jsonFile : baseSaveLocation.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		}))
			FileUtils.copyFile(jsonFile, new File(backupFolder+"\\"+jsonFile.getName()));
	}

	public static void checkAllFactions() {
	    Debug.out(Txt.parse("<i>Verifying cities..."));
		ArrayList<Faction> factionList = new ArrayList<Faction>(FactionUtils.getAllFactions());
		boolean allOK = true;
		for (Faction fac : factionList)
		{
			if (fac.isNormal() && !fac.getName().equals("WarZone") && !fac.getName().equals("SafeZone")) 
			{
				City city = FactionUtils.factionIDToCity.get(fac.getId());
				if (city == null)
				{
					allOK = false;
	                Debug.warn(Txt.parse("<bad>Found faction without city!! creating city "+fac.getName()+"..."));
					city = new City(FactionVillagers.defaultWorld, fac.getId());
					city.create();
	                Debug.out(Txt.parse("<i>\t(Recovering buildings not implemented...)"));
				}
				List<String> errors = city.recheck();
				if (!errors.isEmpty())
					Debug.out("City "+city.getFaction().getName()+ " contained errors: \n"+StringUtils.join(errors, ",\n"));
				

			}
		}
		if (allOK) Debug.out(Txt.parse("<good>Cities verified."));
		
		
		NpcUtils.recheckAllNPCs();
	}







}
