package characters;

import factions.FactionUtils;
import generics.Tuple;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import main.Debug;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import utils.BlockMatState;
import utils.BlockUtils;
import utils.WalkingGroundUtils;
import buildings.MetaDataUtils;
import characters.MineUtils.MineFindResult.Sort;
import characters.Miner.MineStopReason;
import characters.Navigator.GoAndDo;

import com.massivecraft.factions.entity.Faction;

public class MineUtils {
	static int maxNumberOfBlocksOnPathBetweenWaypPoints = 5;
	private static int pathBlocksBetweenLightings = 6;

    public static boolean isMinableType(Material mat) {
		List<Material> mats = Arrays.asList(new Material[]{ 
				Material.COBBLESTONE,  
				Material.STONE,
				Material.MOSSY_COBBLESTONE,
				Material.DIRT,
				Material.GRAVEL,
				Material.SAND,
				Material.SANDSTONE,
				
				Material.EMERALD_ORE,
				Material.REDSTONE_ORE,
				Material.COAL_ORE,
				Material.IRON_ORE,
				Material.GOLD_ORE,
				Material.DIAMOND_ORE,
				Material.LAPIS_ORE,
				
				Material.NETHERRACK,
				Material.QUARTZ_ORE,
				Material.SOUL_SAND,

				Material.CLAY,
				Material.SNOW,
				Material.SNOW_BLOCK,
				Material.ICE,
				
				Material.FENCE, // cause otherwise it will sometimes block itself
				Material.WOOD,
				
				Material.WATER, // cause we need to remove them!
				Material.STATIONARY_WATER,
				Material.LAVA,
				Material.STATIONARY_LAVA
//				Material.,
//				Material.,
		});
		if (mats.contains(mat))
			return true;
		else
			return false;
	}
    
    public static boolean isConsumedDuringMiningType(Material mat) {
		List<Material> mats = Arrays.asList(new Material[]{ 
				Material.COBBLESTONE,  
				Material.WOOD,
				Material.CARPET
		});
		if (mats.contains(mat))
			return true;
		else
			return false;
	}
	
    public static class HomeFindResult {
    	
    	public Block block;
    	public Block prevBlock;
    	int nBlocksOnPathSinceLastWayPoint;
    	public List<GoAndDo> wayPoints = new LinkedList<GoAndDo>();
    	public HomeFindResult(Block b) {
    		block = b; prevBlock = b; 
    		this.nBlocksOnPathSinceLastWayPoint = 0;
    	}
    	private HomeFindResult(Block b, Block prevBlock2, int nBlocksOnPathSinceLastWayPoint2) {
    		block = b; 
    		prevBlock = prevBlock2;
    		this.nBlocksOnPathSinceLastWayPoint = nBlocksOnPathSinceLastWayPoint2;
    	}
    	public HomeFindResult nextBlock(final Block blockNext) {
    		HomeFindResult ret = new HomeFindResult(blockNext, this.block, nBlocksOnPathSinceLastWayPoint+1);
    		ret.wayPoints = new LinkedList<GoAndDo>(this.wayPoints);
    		if (BlockUtils.isGateType(block.getRelative(BlockFace.UP).getType()))
    		{
    			ret.nBlocksOnPathSinceLastWayPoint = 0;
    			ret.wayPoints.add(new Navigator.GoAndOpenGate(prevBlock.getLocation().add(.5, 1, .5), block.getRelative(BlockFace.UP)));
    		}
    		else if  (BlockUtils.isGateType(prevBlock.getRelative(BlockFace.UP).getType()))
    		{
    			ret.nBlocksOnPathSinceLastWayPoint = 0;
    			ret.wayPoints.add(new Navigator.GoAndCloseGate(block.getLocation().add(.5, 1, .5), prevBlock.getRelative(BlockFace.UP)));
    		}
    		else if (nBlocksOnPathSinceLastWayPoint > MineUtils.maxNumberOfBlocksOnPathBetweenWaypPoints)
    		{
    			ret.nBlocksOnPathSinceLastWayPoint = 0;
    			ret.wayPoints.add(GoAndDo.newNoAction(blockNext.getLocation().add(.5, 0, .5)));
    		}
    		return ret;
    	}
    	
    }
    
    static HomeFindResult findMineBuilding(HashSet<Player> playersToShowSearchSpace, Block startBlock, Block endBlock) {
    	
    	HashSet<Block> checked = new HashSet<Block>();
    	LinkedList<HomeFindResult> todo = new LinkedList<HomeFindResult>(); 
    	todo.addLast(new HomeFindResult(startBlock));
    	
    	while (!todo.isEmpty() && checked.size() < Miner.maxRailsChecked*2 )
    	{
    		HomeFindResult blockResultNow = todo.poll();
    		Block blockNow = blockResultNow.block;
    		if (checked.contains(blockNow)) continue;
    		
    		if (blockNow.equals(endBlock))
    			return blockResultNow;
    		
    		checked.add(blockNow);
    		
    		for (BlockFace face : BlockUtils.gewesten4)
    		{
    			//				Debug.out("checking blocks next to");
    			Block directlyNext = blockNow.getRelative(face);
    			
    			Block next = MineUtils.getRailAt(blockNow, directlyNext);
    			if (next == null) continue;
    			//				Debug.out("found next rail");
    			if (checked.contains(next)) continue;
    			
    			HomeFindResult nextResult = blockResultNow.nextBlock(next);
    			todo.addLast(nextResult);
    		}
    	}
    	
//    	ShowBlockChange.showAs(checked, Material.POWERED_RAIL, playersToShowSearchSpace, 200);
    	if(Debug.showCouldntFindJobDebug)
    		Debug.out("couldn't find home! checked "+checked.size()+" blocks...");
    	return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	public static class MineFindResult {
		public Sort sort = Sort.NONE;
		enum Sort { CORNER, JUNCTION, END_POINT, LIGHTING, NONE };
		
		final double valueOfJob; 
		
		public Block block;
		public Block prevBlock;
		public BlockFace fromDirection;
		public BlockFace toDirection;
		int nBlocksOnPathSinceLastWayPoint;
		int nBlocksOnPathSinceLastLighting;
		int nBlocksOnPathSinceLastJunction;
		public List<GoAndDo> wayPoints = new LinkedList<GoAndDo>();
		public MineFindResult(Block b) {
			block = b; prevBlock = b; fromDirection = BlockFace.SELF;
			this.nBlocksOnPathSinceLastWayPoint = nBlocksOnPathSinceLastLighting = nBlocksOnPathSinceLastJunction 
					= 0;
			valueOfJob = 0;
		}
		private MineFindResult(Block b, Block prevBlock2, BlockFace from, int nBlocksOnPathSinceLastWayPoint2, int nBlocksOnPathSinceLastLighting2, int nBlocksOnPathSinceLastJunction2) {
			block = b; fromDirection = from;
			prevBlock = prevBlock2;
			this.nBlocksOnPathSinceLastWayPoint = nBlocksOnPathSinceLastWayPoint2;
			this.nBlocksOnPathSinceLastLighting = nBlocksOnPathSinceLastLighting2;
			this.nBlocksOnPathSinceLastJunction = nBlocksOnPathSinceLastJunction2;
			valueOfJob = nBlocksOnPathSinceLastJunction2 + 3./wayPoints.size(); // TODO: put into config?
		}
		public MineFindResult nextBlock(final Block blockNext, BlockFace fromDirectionNext) {
			MineFindResult ret = new MineFindResult(blockNext, this.block, fromDirectionNext, nBlocksOnPathSinceLastWayPoint+1, nBlocksOnPathSinceLastLighting+1, nBlocksOnPathSinceLastJunction+1);
			ret.wayPoints = new LinkedList<GoAndDo>(this.wayPoints);
			if (BlockUtils.isGateType(block.getRelative(BlockFace.UP).getType()))
			{
				ret.nBlocksOnPathSinceLastWayPoint = 0;
				ret.wayPoints.add(new Navigator.GoAndOpenGate(prevBlock.getLocation().add(.5, 1, .5), block.getRelative(BlockFace.UP)));
			}
			else if  (BlockUtils.isGateType(prevBlock.getRelative(BlockFace.UP).getType()))
			{
				ret.nBlocksOnPathSinceLastWayPoint = 0;
				ret.wayPoints.add(new Navigator.GoAndCloseGate(block.getLocation().add(.5, 1, .5), prevBlock.getRelative(BlockFace.UP)));
			}
			else if (nBlocksOnPathSinceLastWayPoint > MineUtils.maxNumberOfBlocksOnPathBetweenWaypPoints)
			{
				ret.nBlocksOnPathSinceLastWayPoint = 0;
				ret.wayPoints.add(GoAndDo.newNoAction(blockNext.getLocation().add(.5, 0, .5)));
			}
			
			if (getRailDirsNextTo(block).size() > 2)
			{
				ret.nBlocksOnPathSinceLastJunction = 0;
			}
			return ret;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	static List<MineFindResult> findMineJobs(HashSet<Player> playersToShowSearchSpace, Block startBlock, boolean findOnlyNearestJob, int maxRailsChecked) {
			LinkedList<MineFindResult> jobs = new LinkedList<MineFindResult>();
			
			HashSet<Block> checked = new HashSet<Block>();
			LinkedList<MineFindResult> todo = new LinkedList<MineFindResult>(); 
			todo.addLast(new MineFindResult(startBlock));
			
			while (!todo.isEmpty() && checked.size() < maxRailsChecked )
			{
				MineFindResult blockResultNow = todo.poll();
				Block blockNow = blockResultNow.block;
				if (checked.contains(blockNow)) continue;
				MineUtils.processNewBlock(blockResultNow, jobs);
				checked.add(blockNow);
				
				if (findOnlyNearestJob && jobs.size()>0)
					break;
	
				for (BlockFace face : BlockUtils.gewesten4)
				{
	//				Debug.out("checking blocks next to");
					Block directlyNext = blockNow.getRelative(face);
					
					Block next = MineUtils.getRailAt(blockNow, directlyNext);
					if (next == null) continue;
	//				Debug.out("found next rail");
					if (checked.contains(next)) continue;
					
					MineFindResult nextResult = blockResultNow.nextBlock(next, face.getOppositeFace());
					todo.addLast(nextResult);
				}
			}
	
	//		ShowBlockChange.showAs(checked, Material.POWERED_RAIL, playersToShowSearchSpace, 200);
			if (Debug.showSearchSpaceDebug && !playersToShowSearchSpace.isEmpty())
			{
				for (MineFindResult job : jobs)
				{
					switch (job.sort) {
					case CORNER:
						ShowBlockChange.showAs(job.block, Material.REDSTONE_WIRE, playersToShowSearchSpace, 500);
						break;
					case JUNCTION:
						ShowBlockChange.showAs(job.block, Material.getMaterial(40), playersToShowSearchSpace, 500);
						break;
					case END_POINT:
						ShowBlockChange.showAs(job.block, Material.getMaterial(39), playersToShowSearchSpace, 500);
						break;
					case LIGHTING:
						ShowBlockChange.showAs(job.block, Material.TORCH, playersToShowSearchSpace, 500);
						break;
					case NONE:
						ShowBlockChange.showAs(job.block, Material.SKULL, playersToShowSearchSpace, 2000);
						break;
					}
				}
			}
			if (jobs.isEmpty())
				Debug.out("Couldn't find any mining job!! (findOnlyNearestJob="+findOnlyNearestJob+")");
			return jobs;
		}

	static Block getRailAt(Block blockNow, Block directlyNext) {
		if (BlockUtils.isRailType(directlyNext.getType()) && 
				WalkingGroundUtils.isValidWalkingAirBlockOrGate(directlyNext.getRelative(BlockFace.UP)))
			return directlyNext;
		if (BlockUtils.isRailType(directlyNext.getRelative(BlockFace.DOWN).getType()) && 
				WalkingGroundUtils.isValidWalkingAirBlockOrGate(directlyNext) && 
				WalkingGroundUtils.isValidWalkingAirBlockOrGate(directlyNext.getRelative(BlockFace.UP)))
			return directlyNext.getRelative(BlockFace.DOWN);
		if (BlockUtils.isRailType(directlyNext.getRelative(BlockFace.UP).getType()) && 
				WalkingGroundUtils.isValidWalkingAirBlockOrGate(directlyNext.getRelative(BlockFace.UP, 2)) && 
				WalkingGroundUtils.isValidWalkingAirBlockOrGate(blockNow.getRelative(BlockFace.UP, 2)))
			return directlyNext.getRelative(BlockFace.UP);
		return null;
	}

	/**
	 * if block now is jobpoint:
	 * 1) add to jobs list;
	 * 2) set result.sort to the kind of jobpoint. 
	 * @param result finder result now
	 * @param jobs all jobs found
	 */
	static void processNewBlock(MineFindResult result, LinkedList<MineFindResult> jobs) {
		Block block = result.block;
		if (! BlockUtils.isRailType(block.getType()))
			return;
		
		BlockFace lookingDirection = result.fromDirection.getOppositeFace();
		
		if (MetaDataUtils.getBuildings(block).size() != 0)
			return; // block belongs to building! we can move through buildings, but not work there
		
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
	
		LinkedList<BlockFace> cobbleWallDirsNext = new LinkedList<BlockFace>();
		for (BlockFace face : BlockUtils.gewesten4)
		{
			if (BlockUtils.isMiningStop(block.getRelative(face).getType()) || BlockUtils.isMiningStop(block.getRelative(face).getRelative(BlockFace.UP).getType()))
				cobbleWallDirsNext.add(face);
		}
		
		if (railDirsNext.size() == 1)
		{ // end point!
			if (cobbleWallDirsNext.size() >= 3)
				return; // end point marked as closed!
			result.sort = Sort.END_POINT;
			jobs.addLast(result); // end point (not checked for obs, bedrock, fac, tunnel yet!)
			return;
		}
		
		boolean hasLighting = hasLighting(block, lookingDirection);
		if (hasLighting)
		{
			result.nBlocksOnPathSinceLastLighting = 0;
			return; // can't be a corner 
		}
	
		if (railDirsNext.size() == 2 && railDirsNext.get(0).getOppositeFace() == railDirsNext.get(1))
		{
			if (result.nBlocksOnPathSinceLastLighting > pathBlocksBetweenLightings 
					&& block.getLightLevel() < Miner.minLightLevel
					&& !hasLighting)
			{
				result.sort = Sort.LIGHTING;
				result.nBlocksOnPathSinceLastLighting = 0;
				jobs.addLast(result);
			}
			return; // straight road
		}
		if (railDirsNext.size() + cobbleWallDirsNext.size() >= 4)
		{
			return; // full junction, no jobs here!
		}
		
		LinkedList<BlockFace> railDirsNext8 = getRailDirsNext8(block);
		
		if (railDirsNext8.size() > railDirsNext.size())
			return; // junction with diagonal rail
		
		if (railDirsNext.size() == 2)
		{
			result.sort = Sort.CORNER;
			jobs.addLast(result);
		}
		else
		{
			result.sort = Sort.JUNCTION;
			jobs.addLast(result);
		}
	}

	/**
	 * @param block
	 * @param lookingDirection for 
	 * @return NONE also when it should have been a lighting job!
	 */
	public static Sort getJobSort(Block block) {
		if (! BlockUtils.isRailType(block.getType()))
			return Sort.NONE;
		
		
		if (MetaDataUtils.getBuildings(block).size() != 0)
			return Sort.NONE; // block belongs to building! we can move through buildings, but not work there
		
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
	
		LinkedList<BlockFace> cobbleWallDirsNext = new LinkedList<BlockFace>();
		for (BlockFace face : BlockUtils.gewesten4)
		{
			if (BlockUtils.isMiningStop(block.getRelative(face).getType()))
				cobbleWallDirsNext.add(face);
		}
		
		if (railDirsNext.size() == 1)
		{ // end point!
			if (cobbleWallDirsNext.size() >= 3)
				return Sort.NONE; // end point marked as closed!
			return Sort.END_POINT;
		}
		
		boolean hasLighting = hasLighting(block, BlockFace.NORTH) || hasLighting(block, BlockFace.EAST);
		if (hasLighting)
		{
			return Sort.NONE; // can't be a corner 
		}
	
		if (railDirsNext.size() == 2 && railDirsNext.get(0).getOppositeFace() == railDirsNext.get(1))
		{
			if (
//					(result.nBlocksOnPathSinceLastLighting > pathBlocksBetweenLightings // TODO: fully remove nBlocksOnPathSinceLastLighting 
//					|| 
					block.getLightLevel() < Miner.minLightLevel
//					)
					&& !hasLighting)
			{
				return Sort.LIGHTING;
			}
			return Sort.NONE; // straight road
		}
		
		if (railDirsNext.size() + cobbleWallDirsNext.size() >= 4)
		{
			return Sort.NONE; // full junction, no jobs here!
		}
		
		LinkedList<BlockFace> railDirsNext8 = getRailDirsNext8(block);
		
		if (railDirsNext8.size() > railDirsNext.size())
			return Sort.NONE; // junction with diagonal rail
		
		if (railDirsNext.size() == 2)
		{
			return Sort.CORNER;
		}
		else
		{
			return Sort.JUNCTION;
		}
	}
	

	private static LinkedList<BlockFace> getRailDirsNext8(Block block) {
		LinkedList<BlockFace> railDirsNext8 = new LinkedList<BlockFace>();
		for (BlockFace face : BlockUtils.gewesten8)
		{
			if (getRailAt(block, block.getRelative(face)) != null) // TODO: check up and down as well!
				railDirsNext8.add(face);
		}
		return railDirsNext8;
	}
	private static LinkedList<BlockFace> getRailDirsNextTo(Block block) {
		LinkedList<BlockFace> railDirsNext = new LinkedList<BlockFace>();
		for (BlockFace face : BlockUtils.gewesten4)
		{
			if (getRailAt(block, block.getRelative(face)) != null) // TODO: check up and down! (in that order)
				railDirsNext.add(face);
		}
		return railDirsNext;
	}
	
	public static boolean isEndpoint(Block block) {
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
		if (railDirsNext.size() == 1)
			return true;
		else
			return false;
		
	}
	public static boolean isJunction(Block block) {
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
		if (railDirsNext.size() > 2)
			return true;
		else
			return false;
	}
	public static boolean isJunctionCorrect(Block block) {
		if (! isJunction(block))
			return false;
		
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
		LinkedList<BlockFace> railDirsNext8 = getRailDirsNext8(block);
		if (railDirsNext8.size() > railDirsNext.size())
			return false;
		return true; // otherwise
	}
	public static boolean isCorner(Block block) {
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
		if (railDirsNext.size() == 2
				&& railDirsNext.get(0).getOppositeFace() != railDirsNext.get(1))
			return true;
		return false; // otherwise
	}
	public static boolean isStraightRoad(Block block) {
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
		if (railDirsNext.size() == 2 
				&& railDirsNext.get(0).getOppositeFace() == railDirsNext.get(1))
			return true;
		return false; // otherwise
	}
	
	
	
	public static boolean isValidRailPosition(Block block) 
	{
		if (BlockUtils.isMiningStop(block.getType()))
			return false;
		LinkedList<BlockFace> railDirsNext = getRailDirsNextTo(block);
		LinkedList<BlockFace> railDirsNext8 = getRailDirsNext8(block);
		if (railDirsNext8.size() >= 3 && railDirsNext.size() >= 2)
		{
			for (BlockFace railDir : railDirsNext)
			{
				Block next = block.getRelative(railDir);								// .
				Block nc = next.getRelative(BlockUtils.getNextDirClockwise4(railDir));	// ..
				Block ncc = nc.getRelative(railDir.getOppositeFace());					// .:
				if (hasRailAt(next) && hasRailAt(nc) && hasRailAt(ncc))
					return false;
			}
		}
		
		return true; // otherwise
	}
	
	
	public static boolean hasRailAt(Block position) 
	{ 
		if (BlockUtils.isRailType(position.getType()))
			return true;
		if (BlockUtils.isRailType(position.getRelative(BlockFace.UP).getType()))
			return true;
		if (BlockUtils.isRailType(position.getRelative(BlockFace.DOWN).getType()))
			return true;
		return false; // otherwise
	}
	
	
	
	
	/**
	 * checks whether a rail block has either an arc with a torch (or pilars with torches in case there is no ceiling)
	 * @param railBlock
	 * @param direction
	 * @return
	 */
	public static boolean hasLighting(Block railBlock, BlockFace direction) {
//		if (needsSupport) 
//			return hasArc(railBlock, direction);
//		else 
//			return hasPilars(railBlock, direction);
		return neededToMakeLighting(railBlock, direction).isEmpty();
	}
	public static LinkedList<Tuple<Block, BlockMatState>> neededToMakeLighting(Block railBlock, BlockFace direction) {
		Block left = railBlock.getRelative(BlockUtils.getNextDirAntiClockwise4(direction));
		Block right= railBlock.getRelative(BlockUtils.getNextDirClockwise4(direction));
		if (		!WalkingGroundUtils.isValidWalkingAirBlockOrGate(left.getRelative(BlockFace.UP, 3)) 
				|| 	!WalkingGroundUtils.isValidWalkingAirBlockOrGate(right.getRelative(BlockFace.UP, 3))
				|| 	!WalkingGroundUtils.isValidWalkingAirBlockOrGate(railBlock.getRelative(BlockFace.UP, 3))
				) 	
		{
			return neededToMakeArc(railBlock, direction);
		}
		else 
		{
			return neededToMakePilars(railBlock, direction);
		}
	}
	
	/*
	     
	 *     | .    .  |
	 *     |=||   ||=|
	 *     ~~~~|=|~~~~
	 
	public static boolean hasPilars(Block railBlock, BlockFace direction) {
		Block left = railBlock.getRelative(BlockUtils.getNextDirAntiClockwise4(direction));
		Block right= railBlock.getRelative(BlockUtils.getNextDirClockwise4(direction));
		
		if ( left.getType() != Material.FENCE) 								return false;
		if (right.getType() != Material.FENCE) 								return false;
		if ( left.getRelative(BlockFace.UP).getType() != Material.TORCH) 	return false;
		if (right.getRelative(BlockFace.UP).getType() != Material.TORCH) 	return false;
		return true; // otherwise
	}
	
	 *      _________
	 *     |[ ][ ][ ]|
	 *     |=||   ||=|
	 *     |=||   ||=|
	 *     ~~~~|=|~~~~
	 
	public static boolean hasArc(Block railBlock, BlockFace direction) {
		Block left = railBlock.getRelative(BlockUtils.getNextDirAntiClockwise4(direction));
		Block right= railBlock.getRelative(BlockUtils.getNextDirClockwise4(direction));
		
		if ( left.getType() != Material.FENCE) 									return false;
		if (right.getType() != Material.FENCE) 									return false;
		if ( left.getRelative(BlockFace.UP).getType() != Material.FENCE) 		return false;
		if (right.getRelative(BlockFace.UP).getType() != Material.FENCE) 		return false;
		if ( left.getRelative(BlockFace.UP, 2).getType() != Material.WOOD) 		return false;
		if (right.getRelative(BlockFace.UP, 2).getType() != Material.WOOD) 		return false;
		if (railBlock.getRelative(BlockFace.UP, 2).getType() != Material.WOOD)	return false;
		return isLitArc(railBlock, direction); // otherwise
	}
	
	 *      _________
	 *     |[ ][i][ ]|
	 *     |=||   ||=|
	 *     |=||   ||=|
	 *     ~~~~|=|~~~~
	 
	private static boolean isLitArc(Block railBlock, BlockFace direction) {
		Block up2 = railBlock.getRelative(BlockFace.UP, 2);
		if (up2.getRelative(direction).getType() == Material.TORCH) 					return true;
		if (up2.getRelative(direction.getOppositeFace()).getType() == Material.TORCH) 	return true;
		return false; // otherwise
	}
	*/
	
	
	/*     
	 *     | .    .  |
	 *     |=||   ||=|
	 *     ~~~~|=|~~~~
	 */
	public static LinkedList<Tuple<Block, BlockMatState>> neededToMakePilars(Block railBlock, BlockFace direction) {
		LinkedList<Tuple<Block, BlockMatState>> ret = new LinkedList<Tuple<Block, BlockMatState>>();
		Block left = railBlock.getRelative(BlockUtils.getNextDirAntiClockwise4(direction));
		Block right= railBlock.getRelative(BlockUtils.getNextDirClockwise4(direction));
		
		shouldBe(left, new BlockMatState(Material.FENCE, -1), ret);
		shouldBe(right, new BlockMatState(Material.FENCE, -1), ret);
		shouldBe( left.getRelative(BlockFace.UP), new BlockMatState(Material.TORCH, 5), ret);
		shouldBe(right.getRelative(BlockFace.UP), new BlockMatState(Material.TORCH, 5), ret);
		return ret;
	}
	
	private static void shouldBe(Block block, BlockMatState blockMatState, LinkedList<Tuple<Block, BlockMatState>> ret) {
		if (block.getType() != blockMatState.mat) ret.add(new Tuple<Block, BlockMatState>(block, blockMatState));
	}
	
	/*      _________
	 *     |[ ][ ][ ]|
	 *     |=||   ||=|
	 *     |=||   ||=|
	 *     ~~~~|=|~~~~
	 */
	public static LinkedList<Tuple<Block, BlockMatState>>  neededToMakeArc(Block railBlock, BlockFace direction) {
		LinkedList<Tuple<Block, BlockMatState>> ret = new LinkedList<Tuple<Block, BlockMatState>>();
		Block left = railBlock.getRelative(BlockUtils.getNextDirAntiClockwise4(direction));
		Block right= railBlock.getRelative(BlockUtils.getNextDirClockwise4(direction));
		
		shouldBe( left, new BlockMatState(Material.FENCE, -1), ret);
		shouldBe(right, new BlockMatState(Material.FENCE, -1), ret);
		shouldBe( left.getRelative(BlockFace.UP), new BlockMatState(Material.FENCE, -1), ret);
		shouldBe(right.getRelative(BlockFace.UP), new BlockMatState(Material.FENCE, -1), ret);
		shouldBe( left.getRelative(BlockFace.UP, 2), new BlockMatState(Material.WOOD, -1), ret);
		shouldBe(right.getRelative(BlockFace.UP, 2), new BlockMatState(Material.WOOD, -1), ret);
		shouldBe(railBlock.getRelative(BlockFace.UP, 2), new BlockMatState(Material.WOOD, -1), ret);
		
		Block up2 = railBlock.getRelative(BlockFace.UP, 2);
		if (!(up2.getRelative(direction).getType() == Material.TORCH
				|| up2.getRelative(direction.getOppositeFace()).getType() == Material.TORCH))
			ret.add(new Tuple<Block, BlockMatState>(up2.getRelative(direction.getOppositeFace()), new BlockMatState(Material.TORCH,   BlockUtils.toTorchData(direction))));
		
		return ret; 
	}
	
	
	
	
	
	
	
	/**
	 * assumes we can dig!
	 * @param toBecomeRail
	 * @param dir
	 * @param fac
	 * @return
	 */
	public static LinkedList<Tuple<Block, BlockMatState>> neededToMakeMine(Block toBecomeRail, BlockFace dir, Faction fac) {
		LinkedList<Tuple<Block, BlockMatState>> ret = new LinkedList<Tuple<Block, BlockMatState>> ();
		if (canMakeMine(toBecomeRail, dir, fac) != MineStopReason.NONE)
			return ret;
		
		// this step mining
		XZranges ranges0 = new XZranges(toBecomeRail, dir, 0);
		for (int y = toBecomeRail.getY(); y <= toBecomeRail.getY()+2; y++)
			for (int x = ranges0.xFrom; x <= ranges0.xTo; x++)
				for (int z = ranges0.zFrom; z <= ranges0.zTo; z++)
				{
					Block block = toBecomeRail.getWorld().getBlockAt(x, y, z);
					if (BlockUtils.isRailType(block.getType())
							|| BlockUtils.isRailType(block.getRelative(BlockFace.UP).getType()) )
						continue;
					if (! WalkingGroundUtils.isValidWalkingAirBlockOrGate(block))
						ret.add(new Tuple<Block, BlockMatState>(block, new BlockMatState(Material.AIR, -1)));
				}
		
		// mining one step forward
		XZranges ranges1 = new XZranges(toBecomeRail.getRelative(dir), dir, 0);
		for (int y = toBecomeRail.getY(); y <= toBecomeRail.getY()+2; y++)
			for (int x = ranges1.xFrom; x <= ranges1.xTo; x++)
				for (int z = ranges1.zFrom; z <= ranges1.zTo; z++)
				{
					Block block = toBecomeRail.getWorld().getBlockAt(x, y, z);
					if (BlockUtils.isRailType(block.getType())
							|| BlockUtils.isRailType(block.getRelative(BlockFace.UP).getType()) )
						continue;
					if (! WalkingGroundUtils.isValidWalkingAirBlockOrGate(block))
						if (isMinableType(block.getType()))
							ret.add(new Tuple<Block, BlockMatState>(block, new BlockMatState(Material.AIR, -1)));
				}
		
		// make the floor
		XZranges ranges01 = new XZranges(toBecomeRail, dir, 1);
		int y = toBecomeRail.getY()-1;
		for (int x = ranges01.xFrom; x <= ranges01.xTo; x++)
			for (int z = ranges01.zFrom; z <= ranges01.zTo; z++)
			{
				Block block = toBecomeRail.getWorld().getBlockAt(x, y, z);
				if (BlockUtils.isRailType(block.getType())
						|| BlockUtils.isRailType(block.getRelative(BlockFace.UP).getType()) )
					continue;
				if (! WalkingGroundUtils.isValidWalkingGroundBlock(block))
					ret.add(new Tuple<Block, BlockMatState>(block, new BlockMatState(Material.WOOD, -1)));
			}
		
		
		ret.add(new Tuple<Block, BlockMatState>(toBecomeRail, new BlockMatState(Material.CARPET, 15)));
		return ret; 
	}
	
	public static MineStopReason canMakeMine(Block toBecomeRail, BlockFace dir, Faction fac) {
		boolean hasStopBlock = false;
		// check the bare essentials
		XZranges ranges1 = new XZranges(toBecomeRail, dir, 0);
		for (int y = toBecomeRail.getY(); y <= toBecomeRail.getY()+2; y++)
			for (int x = ranges1.xFrom; x <= ranges1.xTo; x++)
				for (int z = ranges1.zFrom; z <= ranges1.zTo; z++)
				{
					Block block = toBecomeRail.getWorld().getBlockAt(x, y, z);
					if (!isMinableType(block.getType()))
					{
						if (BlockUtils.isRailType(block.getType()))
							continue;
						else if (WalkingGroundUtils.isValidWalkingAirBlockOrGate(block))
							continue;
						else 
						{
							if (block.getType() == Material.COBBLE_WALL)
								hasStopBlock = true;
							return MineStopReason.UNMINABLE_TYPE;
						}
					}
				}
		XZranges ranges = new XZranges(toBecomeRail, dir, 1);
		for (int y = toBecomeRail.getY(); y <= toBecomeRail.getY()+2; y++)
			for (int x = ranges.xFrom; x <= ranges.xTo; x++)
				for (int z = ranges.zFrom; z <= ranges.zTo; z++)
				{
					Block block = toBecomeRail.getWorld().getBlockAt(x, y, z);
					if (!FactionUtils.canBuildOn(fac, block))
					{
						return MineStopReason.OTHER_FACTION_TERRAIN;
					}
					if (block.getType() == Material.BEDROCK || block.getType() == Material.OBSIDIAN)
					{
						return MineStopReason.UNMINABLE_TYPE;
					}
					if (MetaDataUtils.getBuildings(block).size() > 0)
					{
						return MineStopReason.BUILDING;
					}
				}

		
		
		if (willConnectToOtherTunnelOnDifferentLevel(toBecomeRail, dir, ranges))
		{
			return MineStopReason.INTERRUPTS_OTHER_MINE;
		}
		
		if (hasStopBlock)
			return MineStopReason.HAS_COBBLE_WALL;
		
		return MineStopReason.NONE; // otherwise 
	}
	
	public static boolean willConnectToOtherTunnelOnDifferentLevel(Block toBecomeRail, BlockFace direction, XZranges ranges) {
		for (int y = toBecomeRail.getY()-3; y <= toBecomeRail.getY()+3; y++)
			for (int x = ranges.xFrom; x <= ranges.xTo; x++)
				for (int z = ranges.zFrom; z <= ranges.zTo; z++)
				{
					if (y == toBecomeRail.getY()-1)
						y = y+3; 					// skip height difference of 1!

					Block block = toBecomeRail.getWorld().getBlockAt(x, y, z);
					if (isExistingRailBlock(block))
						return true;
				}
		return false; // otherwise 
	}
	
	
	private static boolean isExistingRailBlock(Block block) {
		return BlockUtils.isRailType(block.getType()) 
				&& WalkingGroundUtils.isValidWalkingAirBlockOrGate(block.getRelative(BlockFace.UP));
	}

	public static boolean hasGround(Block toBecomeRail, BlockFace chosenDiggingDir) {
		int airBlocksBelow = 0;
		for (int x = toBecomeRail.getX()-1; x< toBecomeRail.getX()+1; x++)
			for (int z = toBecomeRail.getZ()-1; z< toBecomeRail.getZ()+1; z++)
			{
				Block block = toBecomeRail.getWorld().getBlockAt(x, toBecomeRail.getY()-2, z);
				if (! WalkingGroundUtils.isValidWalkingGroundBlock(block))
					airBlocksBelow++;
			}
		if (airBlocksBelow>=3)
			return false;
		else return true;
	}
	

	private static class XZranges {
		public int xFrom, xTo, zFrom,zTo;
		
		/**
		 * @param toBecomeRail from where to begin range (including this block)
		 * @param dir direction in which to check
		 * @param depth how far forward (0 is only at current depth, 1 is a total of 2 blocks in depth)
		 */
		public XZranges(Block toBecomeRail, BlockFace dir, int depth) {
			if (dir == BlockFace.NORTH || dir==BlockFace.SOUTH)
			{
				xFrom = toBecomeRail.getX() - 1;
				xTo = toBecomeRail.getX() + 1;
			} 
			else if (dir == BlockFace.EAST || dir==BlockFace.WEST)
			{
				zFrom = toBecomeRail.getZ() - 1;
				zTo = toBecomeRail.getZ() + 1;
			}
			switch(dir) {
			case NORTH:
				zFrom = toBecomeRail.getZ()-1*depth;
				zTo = toBecomeRail.getZ();
				break;
			case EAST:
				xFrom = toBecomeRail.getX();
				xTo = toBecomeRail.getX()+1*depth;
				break;
			case SOUTH:
				zFrom = toBecomeRail.getZ();
				zTo = toBecomeRail.getZ()+1*depth;
				break;
			case WEST:
				xFrom = toBecomeRail.getX()-1*depth;
				xTo = toBecomeRail.getX();
				break;
			default:
				Debug.warn("XZranges called with invalid direction! only NESW allowed...");
				break;
			}
		}
	}


	
}
