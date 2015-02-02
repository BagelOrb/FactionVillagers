package main;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Debug {

	public static final boolean debug = true;

	public static void out(Object string) {
		String str;
		if (string instanceof Object[])
			str=Arrays.toString((Object[]) string);
		else str = string.toString();
		
		try {
			throw new Exception();
		} catch (Exception e) {
//			str = "Debug.out: "+e.getStackTrace()[1]+"\r\n\t\t\t:::"+str;
			str = e.getStackTrace()[1].getFileName()+":"+e.getStackTrace()[1].getLineNumber()+" :>>> \t"+str;
			
//			e.printStackTrace();
		}
		
		//Bukkit.getServer().getPluginManager().getPlugins()[0].getServer().getLogger().info(str);
		Bukkit.getServer().getConsoleSender().sendMessage(str);
		
	}
	public static void out(int stackTrace, Object string) {
		String str;
		if (string instanceof Object[])
			str=Arrays.toString((Object[]) string);
		else str = string.toString();
		
		try {
			throw new Exception();
		} catch (Exception e) {
//			str = "Debug.out: "+e.getStackTrace()[1]+"\r\n\t\t\t:::"+str;
			StackTraceElement[] trace = Arrays.copyOfRange(e.getStackTrace(), 1, stackTrace+1);
			str = StringUtils.join(trace, " >> ")+" :>>> \t"+str;
			
//			e.printStackTrace();
		}
		
		//Bukkit.getServer().getPluginManager().getPlugins()[0].getServer().getLogger().info(str);
		Bukkit.getServer().getConsoleSender().sendMessage(str);
	}
	public static void msg(Object string) {
		Collection<? extends Player> players  = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players)
			player.sendMessage(string.toString());
	}
	public static void err(Object string) {
		throw new RuntimeException("ERROR:"+string.toString());
	}
	public static void warn(Object string) {
		try {
			throw new Exception("WARNING: "+string.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public interface Debuggable {
		public String getDebugInfo();
	}
	
	


	public static boolean showSearchSpaceDebug = false;
	public static boolean showCouldntFindJobDebug = false;
}
