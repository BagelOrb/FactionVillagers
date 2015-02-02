package interaction;

import generics.Tuple;
import happiness.Happiness;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import main.Debug;
import main.MCity;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import utils.MatUtils;
import utils.ServerUtils;
import utils.SetUtils;
import city.City;

import com.massivecraft.massivecore.util.Txt;

public class StatisticsBookListener implements Listener
{
	
	

	
//	public void onRightClickBlock(PlayerInteractEvent event)
//	{	
//	} see InteractListener!

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onBookRead(PlayerInteractEvent event) 
	{
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) )
			return;
		
		Player player = event.getPlayer();

		ItemStack itemInHand =  player.getItemInHand();
		
		if (itemInHand.getType() != Material.WRITTEN_BOOK) return;
		ItemMeta meta = itemInHand.getItemMeta();
		if (!(meta instanceof BookMeta)) return;
		BookMeta book = (BookMeta) meta;
//		if (!book.hasTitle()) return;
//		if (!book.getTitle().equalsIgnoreCase("Statistics")) return;
		
		if (!book.hasDisplayName())
			return;
		
		if (!book.getDisplayName().equalsIgnoreCase("Statistics")) 
			return;
		
		{
			try{
				String firstLine = book.getPage(1).split("\n")[0];
				String time = firstLine.substring(6);
				String[] hm = time.split(":");
				int hours = Integer.parseInt(hm[0]);
				int minutes = Integer.parseInt(hm[1]);
				int totMin = minutes + hours*60;
				Tuple<Long, Long> hmNow = ServerUtils.mcTimeToHoursMinutes(MCity.defaultWorld.getTime());
				long totMinNow = hmNow.fst*60+hmNow.snd;
				if (totMinNow - totMin > 10 || totMinNow - totMin < 0)
				{
					player.getOpenInventory().close();
					player.sendMessage("Please reopen the Statistics book.");
				}
			} catch (Exception e) {
				player.getOpenInventory().close();
				player.sendMessage("Please reopen the Statistics book.");
			}
		}
		itemInHand.setItemMeta(updateBookStatistics(book, player));
		}
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onBookSelect(PlayerItemHeldEvent event) 
	{	
		
		if (event.isCancelled()) 
			return;
		Player player = event.getPlayer();
		
		ItemStack itemInHand =  player.getInventory().getItem(event.getNewSlot());// player.getItemInHand();
		
		if (itemInHand == null) return;
		if (itemInHand.getType() != Material.WRITTEN_BOOK) return;
		
		ItemMeta meta = itemInHand.getItemMeta();
		if (!(meta instanceof BookMeta)) return;
		BookMeta book = (BookMeta) meta;
		
		if (! meta.hasDisplayName())
			return;
		
		if (meta.getDisplayName().equalsIgnoreCase("Statistics"))
		{
			if (MCity.getCity(player)==null)
			{
	            player.sendMessage(Txt.parse("<bad>You must be in a faction to do this!"));
				return;
			}
			
			itemInHand.setItemMeta(updateBookStatistics(book, player));
			return;
		}
		Debug.out("Remoive this message if you ever see it! otherwise remove code below!");
//		if (!book.hasTitle()) return;
//		if (!book.getTitle().equalsIgnoreCase("Statistics")) return;
		if (!book.getDisplayName().equalsIgnoreCase("Statistics")) return;
		
		if (MCity.getCity(player)==null)
		{
            player.sendMessage(Txt.parse("<bad>You must be in a faction to do this!"));
			return;
		}
		
		itemInHand.setItemMeta(updateBookStatistics(book, player));
	
	}

//	@EventHandler(priority = EventPriority.MONITOR)
//	public void onInvClose(InventoryCloseEvent e) {
//		Debug.out("InventoryCloseEvent");
//	}
	
	static BookMeta updateBookStatistics(BookMeta book, Player player) {
//		if (book.getTitle().equals("Statistics"))
//		{
//			Debug.out("displayBookStatistics "+qwe);
			
			City city = MCity.getCity(player);
			book.setAuthor(city.getFaction().getName());
			
			
			city.statistics.computeStatistics();
			
			
			LinkedList<String> pages = new LinkedList<String>();
			String page1 = "";
			page1 += "Time: "+ ServerUtils.formatMCtime(MCity.defaultWorld.getTime()) + "\n\n"; // don't change this! this is parsed when reading the book!
			page1 += "~                        ~\n\n";
			page1 += "<gold><bold>Statistics<reset><black>\nDetailed statistics on the city of "+ city.getFaction().getName() +"<reset><black>\n\n";
			page1 += "~                        ~\n\n";
			
			pages.addAll(breakPage(Txt.parse(page1)));
					
			//Building overview
			pages.addAll(breakPage(Txt.parse(city.getColoredBuildingsList("<black>"))));
			
			// Storage contents
			LinkedList<String> storageRoomPage = contentsOfStorage(city);
			if (storageRoomPage != null)
				pages.addAll(storageRoomPage);
			
			//Production today
			String totalNettoString = city.statistics.toStringNetto("<black>");
			
			pages.addAll(breakPage(totalNettoString));
			
			//Production yesterday
			String totalNettoLastDayString = city.statistics.toStringNettoLastDay("<black>");
			pages.addAll(breakPage(totalNettoLastDayString));
			
			//Happiness overview
			pages.addAll(Happiness.getStatisticsBookDescription(city));
			
			//LinkedList<String> stringsPerBuildingNetto = city.statistics.statistics.toStringNettoPerBuilding();
			//pages.addAll(stringsPerBuildingNetto);
			
			book.setPages(pages);
//		}
		return book;
		
	}
	public static final String delimiter = "<silver>-------------------\n";

	
	
	
	
	
	
	
	public static final Comparator<? super Entry<Material, Integer>> compareByKeyAlphabetically = new Comparator<Entry<Material, Integer>>(){
		@Override
		public int compare(Entry<Material, Integer> o1, Entry<Material, Integer> o2) {
			return o1.getKey().toString().compareTo(o2.getKey().toString());
		}};
		
	public static LinkedList<String> contentsOfStorage(City city) {
		if (city.storageRoom == null)
			return null;
		
		StringBuilder page = new StringBuilder();
		
		page.append("<gold>Storage overview\n"+delimiter);
		
		HashMap<Material, Integer> contents = city.storageRoom.getContents();
		
		{ // number of empty places
			Integer numberOfEmptyPlaces = contents.remove(null);
			if (numberOfEmptyPlaces == null)
				page.append("<bad>No empty slots in the storage room!\n");
			else if (numberOfEmptyPlaces < 5)
				page.append("<bad>Only "+numberOfEmptyPlaces+ " empty slots left in the storage room!\n");
			else
				page.append("<black>Empty slots: <good>"+numberOfEmptyPlaces+"\n");
			
			page.append("\n");
		}
		
		Set<Material> matsActuallyUsed = MatUtils.getMaterialsUsedByVillagers();
		
		int otherItems = 0;
		
		for (Entry<Material, Integer> entry : SetUtils.asSortedList(contents.entrySet(), compareByKeyAlphabetically))
		{
			if (! matsActuallyUsed.contains(entry.getKey()))
			{
				otherItems += entry.getValue();
				continue;
			}
			
			page.append("<black>"+MatUtils.prettyPrint(entry.getKey())+": "+(entry.getValue() > 9 ? "<gold>" : "<bad>")+entry.getValue()+"\n");
		}
		
		page.append("\n<black>Other: <gold>"+otherItems);
		
		return breakPage(Txt.parse(page.toString()));
		
	}
	
	
	
	
	
	

	final static int numberOfLinesPerPage = 13;
	public static LinkedList<String> breakPage(String in) {
		String[] lines = in.split("\\r?\\n");
		LinkedList<String> pages = new LinkedList<String>();
		String page = "";
		int nLines = 0;
		for (int p = 0; p<lines.length; p++)
		{
			String line = lines[p];
			if (page.length()+nLines + line.length()+1 >= 256)
			{
				pages.add(page);
				page = line+"\n";
				nLines = 1;
			} else
			{
				page += line+"\n";
				nLines++;
			}
		}
		pages.add(page);
		return pages;
	}
//	public static LinkedList<String> breakPage(String in) {
//		String[] lines = in.split("\\r?\\n");
//		LinkedList<String> pages = new LinkedList<String>();
//		for (int p = 0; p<lines.length; p+=numberOfLinesPerPage)
//		{
//			pages.add(StringUtils.join(ArrayUtils.subarray(lines, p, p+numberOfLinesPerPage), "\n"));
//		}
//		return pages;
//	}





}
