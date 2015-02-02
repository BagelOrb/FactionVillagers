package io;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import main.Debug;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class JsonBlock {

	public static JsonObjectBuilder toJsonObjectBuilder(Block block) {
		JsonObjectBuilder ret =Json.createObjectBuilder()
				.add("x", block.getX())
				.add("y", block.getY())
				.add("z", block.getZ())
				.add("world", block.getWorld().getName());
		return ret;
	}

	public static Block fromJsonObject(JsonObject o) {
		try {
			int x = o.getInt("x");
			int y = o.getInt("y");
			int z = o.getInt("z");
			World world = Bukkit.getWorld(o.getString("world"));
			return world.getBlockAt(x, y, z);
		} catch (Exception e) {
			Debug.warn("Couldn't parse Json Block!");
			return null;
		}
	}

}
