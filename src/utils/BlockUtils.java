package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import characters.MineUtils;

public class BlockUtils {

	public static final BlockFace[] cubeFaces = new BlockFace[]{
		BlockFace.NORTH, 
		BlockFace.EAST, 
		BlockFace.SOUTH, 
		BlockFace.WEST, 
		BlockFace.UP, 
		BlockFace.DOWN
	};
	public static final BlockFace[] gewesten8 = new BlockFace[]{
        BlockFace.NORTH,
        BlockFace.NORTH_EAST,
        BlockFace.EAST,
        BlockFace.SOUTH_EAST,
        BlockFace.SOUTH,
        BlockFace.SOUTH_WEST,
        BlockFace.WEST,
        BlockFace.NORTH_WEST
	};
	public static final BlockFace[] gewesten4 = new BlockFace[]{
		BlockFace.NORTH,
		BlockFace.EAST,
		BlockFace.SOUTH,
		BlockFace.WEST,
	};
	
	public static ArrayList<Block> getBlocksNext4(Block from) {
		ArrayList<Block> ret = new ArrayList<Block>(4); 
		for (BlockFace dir : gewesten4)
		{
			ret.add(from.getRelative(dir));
		}
		return ret;
	}
	public static BlockFace getNextDirClockwise4(BlockFace dir) {
		switch(dir)
		{
		case NORTH: return BlockFace.EAST;
		case EAST: return BlockFace.SOUTH;
		case SOUTH: return BlockFace.WEST;
		case WEST: return BlockFace.NORTH;
		default:
			return dir;
		}
	}
	public static BlockFace getNextDirAntiClockwise4(BlockFace dir) {
		switch(dir)
		{
		case NORTH: return BlockFace.WEST;
		case EAST: return BlockFace.NORTH;
		case SOUTH: return BlockFace.EAST;
		case WEST: return BlockFace.SOUTH;
		default:
			return dir;
		}
	}
	public static byte toTorchData(BlockFace dir) {
		switch (dir) {
		case NORTH:
			return 3;
		case EAST:
			return 2;
		case SOUTH:
			return 4;
		case WEST:
			return 1;
		default:
			return 5;
		}
	}
	public static boolean isMiningStop(Material type) {
		if (type.isSolid() && !MineUtils.isMinableType(type))
			return true;
		List<Material> miningStopTypes = Arrays.asList(new Material[]{Material.COBBLE_WALL,  Material.BEDROCK, Material.OBSIDIAN}); 
		return miningStopTypes.contains(type);
	}
	
	
	public static boolean isLogType(Material m) { 
		List<Material> logTypes = Arrays.asList(Material.LOG, Material.LOG_2);
		return logTypes.contains(m); }
	public static boolean isLeaveType(Material m) { 
		final List<Material> leaveTypes = Arrays.asList(Material.LEAVES, Material.LEAVES_2);
		return leaveTypes.contains(m); }
	public static boolean isGateType(Material m) { 
		final List<Material> gates = Arrays.asList(new Material[]{Material.FENCE_GATE,  Material.WOODEN_DOOR,  Material.SPRUCE_DOOR, Material.ACACIA_DOOR, Material.JUNGLE_DOOR, Material.DARK_OAK_DOOR, Material.BIRCH_DOOR, Material.TRAP_DOOR});
		return gates.contains(m); }
	public static boolean isFenceType(Material m) { 
		final List<Material> fenceTypes = Arrays.asList(new Material[]{Material.FENCE,  Material.COBBLE_WALL, Material.NETHER_FENCE}); // not Material.FENCE_GATE !!! you can walk thugh gates...
		return fenceTypes.contains(m); }
	public static boolean isRailType(Material m) { 
		final List<Material> railTypes = Arrays.asList(new Material[]{Material.RAILS,  Material.ACTIVATOR_RAIL, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.CARPET});
		return railTypes.contains(m); }

	
	public static ItemStack logToWood(ItemStack log) {
		ItemStack ret = log.clone();
		if (log.getType() == Material.LOG) {
			ret.setType(Material.WOOD);
			ret.setDurability(log.getDurability());
		} else if (log.getType() == Material.LOG_2) {
			ret.setType(Material.WOOD);
			ret.setDurability((short) (log.getDurability()+4));
		}
		return ret;
	}
	public static ItemStack logToWood(Material mat, byte data) {
		ItemStack ret = new ItemStack(mat, 1, data);
		if (mat == Material.LOG) {
			ret.setType(Material.WOOD);
			ret.setDurability(data);
		} else if (mat == Material.LOG_2) {
			ret.setType(Material.WOOD);
			ret.setDurability((short) (data+  4));
		}
		return ret;
	}
	public static ItemStack woodToLog(ItemStack wood) {
		ItemStack ret = wood.clone();
		if (ret.getType() != Material.WOOD)
			return ret;
		
		if (wood.getDurability() < 4) 
		{
			ret.setType(Material.LOG);
			ret.setDurability(wood.getDurability());
		} else 
		{
			ret.setType(Material.LOG_2);
			ret.setDurability((short) (wood.getDurability()-4));
		}
		return ret;
	}
	
	public static Openable getOpenableState(Block gate) {
		BlockState state = gate.getState();
		if (state == null) return null;
		MaterialData matData = state.getData();
		if (matData == null) return null;
		
		if (matData instanceof Openable )
			return (Openable) matData;
		else return null;
	}

	private static final Random rnd = new Random();
	public static final HashSet<Block> getAllConnectedBlocksOfSameType(Block startingBlock, int maxBlocksChecked)
	{
		HashSet<Block> connectedBlocks = new HashSet<Block>();
		Stack<Block> todoBlocks = new Stack<Block>();
		
		todoBlocks.add(startingBlock);
		int nChecked = 0;
		while (!todoBlocks.isEmpty() && nChecked < maxBlocksChecked) 
		{
			Block currentBlock = todoBlocks.remove(rnd.nextInt(todoBlocks.size()));
			if (connectedBlocks.contains(currentBlock)) continue;
			connectedBlocks.add(currentBlock);
			nChecked++;
			
			LinkedList<Block> toBeAdded = new LinkedList<Block>();
			for (BlockFace face : BlockUtils.cubeFaces)
			{
				Block connectedBlock = currentBlock.getRelative(face);
				
				if (startingBlock.getType().equals(connectedBlock.getType())) 
				{
					if(!connectedBlocks.contains(connectedBlock))
						toBeAdded.add(connectedBlock);
				}
			}
			Collections.shuffle(toBeAdded);
			todoBlocks.addAll(toBeAdded);
		}
		
		return connectedBlocks;
	}
	public static Block getHighestBlock(HashSet<Block> blocks) 
	{
		Block highestBlock = null;
		int highestY = 0;
		for(Block block : blocks)
		{
			if(block.getY() > highestY)
			{
				highestY = block.getY();
				highestBlock = block;
			}
		}
		return highestBlock;
	}
	public static Block getLowestBlock(HashSet<Block> blocks) 
	{
		Block lowestBlock = null;
		int lowestY = 128;
		for(Block block : blocks)
		{
			if(block.getY() < lowestY)
			{
				lowestY = block.getY();
				lowestBlock = block;
			}
		}
		return lowestBlock;
	}
	public static Block getConsistentLowestBlock(HashSet<Block> blocks) 
	{
		Block lowestBlock = null;
		int lowestY = 128;
		int lowestX = Integer.MAX_VALUE;
		int lowestZ = Integer.MAX_VALUE;
		for(Block block : blocks)
		{
			if(block.getY() < lowestY ||
					block.getY() == lowestY && block.getX() < lowestX ||
					block.getY() == lowestY && block.getX() == lowestX && block.getZ() < lowestZ)
			{
				lowestX = block.getX();
				lowestY = block.getY();
				lowestZ = block.getZ();
				lowestBlock = block;
			} 
		}
		return lowestBlock;
	}
	static public Block closestBlock(Location origin, int maxRadius, BlockChecker blockChecker)
	{
	    int x = origin.getBlockX();
	    int y = origin.getBlockY();
	    int z = origin.getBlockZ();
	    World world = origin.getWorld();
	    Block block = null;
	    
	    for(int radius = 0; radius < maxRadius; radius++)
	    {
		    for(int cy = -radius; cy < radius; cy++)
		    {
			    for(int cx = -radius; cx < radius; cx++)
			    {
			    	block = world.getBlockAt(x + cx, y + cy, z - radius);
			    	if(blockChecker.isValid(block))
			    	{
			    		return block;
			    	}
			    	
			    	block = world.getBlockAt(x + cx, y + cy, z + radius);
			    	if(blockChecker.isValid(block))
			    	{
			    		return block;
			    	}	
			    }
		    }
		    
		    for(int cy = -radius; cy < radius; cy++)
		    {
			    for(int cz = -radius; cz < radius; cz++)
			    {
			    	block = world.getBlockAt(x - radius, y + cy, z + cz);
			    	if(blockChecker.isValid(block))
			    	{
			    		return block;
			    	}
			    	
			    	block = world.getBlockAt(x + radius, y + cy, z + cz);
			    	if(blockChecker.isValid(block))
			    	{
			    		return block;
			    	}	
			    }
		    }
		    
		    for(int cx = -radius; cx < radius; cx++)
		    {
			    for(int cz = -radius; cz < radius; cz++)
			    {
			    	block = world.getBlockAt(x - cx, y - radius, z + cz);
			    	if(blockChecker.isValid(block))
			    	{
			    		return block;
			    	}
			    	
			    	block = world.getBlockAt(x + cx, y + radius, z + cz);
			    	if(blockChecker.isValid(block))
			    	{
			    		return block;
			    	}	
			    }
		    }
	    }
	
	    return null;
	}
	@SuppressWarnings("deprecation")
	public static void setGateOpen(Block gate, boolean open) {
		BlockState state = gate.getState();
		MaterialData matData = state.getData();
		if (!(matData instanceof Openable)) return;
		Openable gateData = (Openable) matData;
		if (open == false && gate.getBlockPower()>0)
			return;
		gateData.setOpen(open);
		state.setData(matData);
//		Debug.out("");
		gate.setData(state.getRawData());
	}
	
	/**
	 * @param gate
	 * @return whether the block is a gate and open
	 */
	public static boolean isOpenGate(Block gate) {
		BlockState state = gate.getState();
		MaterialData matData = state.getData();
		if (!(matData instanceof Openable)) return false;
		Openable gateData = (Openable) matData;
		return gateData.isOpen();
//		return state.getRawData() == 0;
	}
	
	public static boolean isDoorType(Material m) {
		final List<Material> doorTypes = Arrays.asList(new Material[]{Material.WOODEN_DOOR,  Material.SPRUCE_DOOR, Material.ACACIA_DOOR, Material.JUNGLE_DOOR, Material.DARK_OAK_DOOR, Material.BIRCH_DOOR});
		return doorTypes.contains(m); }



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
