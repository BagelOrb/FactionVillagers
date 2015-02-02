package utils;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class PlayerUtils {

	public static BlockFace getCardinalDirection8(Player player) {
        double rotation = (player.getLocation().getYaw() + 180) % 360;
        if (rotation < 0) rotation += 360;
         if (0 <= rotation && rotation < 22.5) {
            return BlockFace.NORTH;
        } else if (22.5 <= rotation && rotation < 67.5) {
            return BlockFace.NORTH_EAST;
        } else if (67.5 <= rotation && rotation < 112.5) {
            return BlockFace.EAST;
        } else if (112.5 <= rotation && rotation < 157.5) {
            return BlockFace.SOUTH_EAST;
        } else if (157.5 <= rotation && rotation < 202.5) {
            return BlockFace.SOUTH;
        } else if (202.5 <= rotation && rotation < 247.5) {
            return BlockFace.SOUTH_WEST;
        } else if (247.5 <= rotation && rotation < 292.5) {
            return BlockFace.WEST;
        } else if (292.5 <= rotation && rotation < 337.5) {
            return BlockFace.NORTH_WEST;
        } else if (337.5 <= rotation && rotation < 360.0) {
            return BlockFace.NORTH;
        } else {
            return null;
        }
    }
	
	public static BlockFace getCardinalDirection4(Player player) {
		double rotation = (player.getLocation().getYaw() + 180 ) % 360;
		
		BlockFace ret;
		if (rotation < 0) rotation += 360;
		if (0 <= rotation && rotation < 45) {
			ret = BlockFace.NORTH; 
		} else if (45 <= rotation && rotation < 135) {
			ret= BlockFace.EAST;
		} else if (135 <= rotation && rotation < 225) {
			ret= BlockFace.SOUTH;
		} else if (225 <= rotation && rotation < 315) {
			ret= BlockFace.WEST;
		} else if (315 <= rotation && rotation < 360) {
			ret =BlockFace.NORTH;
		} else {
			ret = null;
		}
		return ret;
	}
}
