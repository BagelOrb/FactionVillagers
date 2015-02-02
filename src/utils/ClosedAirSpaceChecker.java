package utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class ClosedAirSpaceChecker {

	private static final int MAX_SPACE = 8000;

	public ClosedAirSpaceChecker(World w) {
		world = w;
	}
	final World world;
	

	public HashSet<Block> borderBlocks;
	
	
	public int sizeAirSpace(Block startingBlock) {
		borderBlocks = new HashSet<Block>();
		HashSet<Block> air = new HashSet<Block>();
		Stack<Block> todo = new Stack<Block>();
		todo.add(startingBlock );
		
		while (!todo.isEmpty() ) 
		{
			Block airBlockNow = todo.pop();
			if (air.contains(airBlockNow)) continue;
			air.add(airBlockNow);
//			if (!airBlockNow.equals(beginBlock) && isBuildingIndicator(airBlockNow)) return -1;
			
			LinkedList<Block> toBeAdded = new LinkedList<Block>();
			for (BlockFace face : BlockUtils.cubeFaces)
			{
				Block airBlock = airBlockNow.getRelative(face);
				if (BlockUtils.isRailType(airBlockNow.getRelative(BlockFace.DOWN).getType()) 
						&& face != BlockFace.UP && face !=BlockFace.DOWN)
				{
					if (!isValidAir(airBlock))
					{
						borderBlocks.add(airBlock);
					}
					continue;
				}
				else if (isValidAir(airBlock)) {
					if (air.contains(airBlock))
						continue;
					else toBeAdded.add(airBlock);
				} else 
				{
					borderBlocks.add(airBlock);
				}
			}
			Collections.shuffle(toBeAdded);
			todo.addAll(toBeAdded);

			if (air.size()>=MAX_SPACE) return MAX_SPACE;
		}
		return air.size();
	}
	
//	private boolean isBuildingIndicator(Block now) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	

	private static boolean isValidAir(Block block) {
		if (BlockUtils.isRailType(block.getType()))
			return false;
//		if (BlockUtils.isRailType(block.getRelative(BlockFace.DOWN).getType()))
//			return false;
		
		switch (block.getType()) {
		case LADDER: 
			return false;
		default:
			return WalkingGroundUtils.isValidWalkingAirBlock(block) ;
		}
	}

	@Deprecated public int sizeWalkingSpace(Block startingBlock) {
		HashSet<Block> air = new HashSet<Block>();
		Stack<Block> todo = new Stack<Block>();
		todo.add(startingBlock );
		
		while (!todo.isEmpty() ) 
		{
			Block airBlockNow = todo.pop();
			air.add(airBlockNow);
//			if (!airBlockNow.equals(beginBlock) && isBuildingIndicator(airBlockNow)) return -1;
			for (BlockFace face : BlockUtils.gewesten4)
			{
				Block airBlock = airBlockNow.getRelative(face);
				if (isValidWalkingAir(airBlock)) {
					if (air.contains(airBlock))
						continue;
					else todo.add(airBlock);
				} 
			}
			
			if (air.size()>=MAX_SPACE) return MAX_SPACE;
		}
		return air.size();
	}

	private static boolean isValidWalkingAir(Block block) {
//		if (BlockUtils.leaveTypes.contains(block.getType()))
//			return true;
		return !block.getType().isSolid() && !block.getRelative(0, 1, 0).getType().isSolid();
	}
	
	

}
