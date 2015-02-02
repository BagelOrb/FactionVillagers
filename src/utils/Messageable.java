package utils;

import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;

import factions.FactionUtils;

public abstract class Messageable {

	public abstract void sendMessage(String msg);
	
	public static class PlayerMsg extends Messageable {

		public Player player;
		
		public PlayerMsg(Player player) {
			this.player = player;
		}
		
		@Override
		public void sendMessage(String msg) {
			player.sendMessage(msg);
		}
	}
	
	public static class FactionMsg extends Messageable {

		Faction fac;
		public FactionMsg(Faction fac) {
			this.fac = fac;
		}
		@Override
		public void sendMessage(String msg) {
			FactionUtils.sendMessage(fac, msg);
		}
		
	}
}
