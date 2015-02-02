package utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import buildings.Building;
import buildings.MetaDataUtils;
import characters.Navigator;
import characters.Navigator.GoAndDo;
import characters.ShowBlockChange;

import com.massivecraft.factions.entity.Faction;

public class WalkingGroundFinder {
	public static final int maxNumberOfBlocksOnPathBetweenWaypPoints = 5;

	public static class WalkingGroundFinderResult {
			public Block block;
			public BlockFace fromDirection;
			int nBlocksOnPathSinceLastWayPoint;
			public List<GoAndDo> wayPoints = new LinkedList<GoAndDo>();
			public WalkingGroundFinderResult(Block b, BlockFace from, int nBlocksOnPathSinceLastWayPoint) {
				block = b; fromDirection = from;
				this.nBlocksOnPathSinceLastWayPoint = nBlocksOnPathSinceLastWayPoint;
			}
			public WalkingGroundFinderResult nextBlock(final Block blockNext, BlockFace fromDirectionNext) {
				WalkingGroundFinderResult ret = new WalkingGroundFinderResult(blockNext, fromDirectionNext, nBlocksOnPathSinceLastWayPoint+1);
				ret.wayPoints = new LinkedList<GoAndDo>(this.wayPoints);
				if (	  (WalkingGroundUtils.isUsedGate(this.block.getRelative(BlockFace.UP)))  
						|| WalkingGroundUtils.isUsedGate(this.block.getRelative(BlockFace.UP, 2) )
					) // close gate behind you
				{
					ret.nBlocksOnPathSinceLastWayPoint = 0;
					ret.wayPoints.add(new Navigator.GoAndCloseGate(blockNext.getLocation().add(.5, 1, .5), 
																block.getRelative(BlockFace.UP)));
				}
				if (	  (WalkingGroundUtils.isUsedGate(blockNext.getRelative(BlockFace.UP)))
						|| WalkingGroundUtils.isUsedGate(blockNext.getRelative(BlockFace.UP, 2))
					) // open gate in front of you
				{
					ret.nBlocksOnPathSinceLastWayPoint = 0;
					ret.wayPoints.add(new Navigator.GoAndOpenGate(this.block.getLocation().add(.5, 1, .5), 
																blockNext.getRelative(BlockFace.UP)));
				}
	//			else if (wayPoints.get(wayPoints.size()-1).loc.distance(blockNext.getLocation()) > nBlocksOnPathBetweenWaypPoints)
				else if (fromDirection != fromDirectionNext 
						|| nBlocksOnPathSinceLastWayPoint > maxNumberOfBlocksOnPathBetweenWaypPoints)
				{
					ret.nBlocksOnPathSinceLastWayPoint = 0;
					ret.wayPoints.add(GoAndDo.newNoAction(block.getLocation().add(.5, 1, .5)));
				} 
					
				return ret;
			}
			
		}

	static public WalkingGroundFinderResult closestBlockOnWalkableGround(Location origin, int max, BlockChecker blockChecker, final HashSet<Player> playersToShowSearchSpace, BlockChecker walkOnlyOn) {
			HashSet<Block> checked = new HashSet<Block>();
			Block startBlock = WalkingGroundUtils.getStandingGround(origin.getBlock());
			LinkedList<WalkingGroundFinderResult> todo = new LinkedList<WalkingGroundFinderResult>(); 
			
			WalkingGroundFinderResult preBlockFinderResult = new WalkingGroundFinderResult(startBlock.getRelative(BlockFace.UP), BlockFace.UP, 0);
			preBlockFinderResult.wayPoints.add(GoAndDo.newNoAction(origin));
			todo.addLast(preBlockFinderResult.nextBlock(startBlock, BlockFace.UP) );
			
	//		BlockFinderResult startingBlockFinderResult = new BlockFinderResult(startBlock, BlockFace.UP, 0);
	//		startingBlockFinderResult.wayPoints.add(GoAndDo.newNoAction(origin));
	//		todo.addLast(startingBlockFinderResult);
			
			WalkingGroundFinderResult endResult = null;
			
			findingLoop:
			while (!todo.isEmpty() && checked.size()<max)
			{
				WalkingGroundFinderResult blockResultNow = todo.poll();
				Block blockNow = blockResultNow.block;
				if (checked.contains(blockNow)) 
					continue;
				if (blockChecker.isValid(blockNow)) {
					endResult =  blockResultNow;
					break findingLoop;
				}
				checked.add(blockNow);
				
				for (BlockFace face : BlockUtils.gewesten4)
				{
					Block directlyNext = blockNow.getRelative(face);
					if (blockChecker.isValid(directlyNext))
					{
						endResult = blockResultNow.nextBlock(directlyNext, face.getOppositeFace());
						break findingLoop;
					}
					Block next = getGround(blockNow, directlyNext);
					if (next == null) 
						continue;
					if (!walkOnlyOn.isValid(next)) 
						continue;
					
					
					WalkingGroundFinderResult nextResult = blockResultNow.nextBlock(next, face.getOppositeFace());
					processWalkingBlock(nextResult, todo, checked); // pathPriority
				}
			}
			
	        if (Debug.showSearchSpaceDebug && playersToShowSearchSpace != null && !playersToShowSearchSpace.isEmpty())
	        {
	        	ShowBlockChange.showAs(checked, Material.REDSTONE_BLOCK, playersToShowSearchSpace, 1000);
	        }
	        return endResult;
		}

	public static final BlockChecker walkEverywhere =
		new BlockChecker() {
			@Override
			public boolean isValid(Block block) {
				return true;
			}
		};
	
	public static BlockChecker pathChecker(final Faction faction) {
		return new BlockChecker() {
		
			@Override
			public boolean isValid(Block block) {
//				if (!WalkingGroundUtils.isValidWalkingGround(block)) return false;

				// belongsto building of own faction
				for (Building b : MetaDataUtils.getBuildings(block))
					if (b.city.getFaction().equals(faction))
						return true;
				for (Building b : MetaDataUtils.getBuildings(block.getRelative(BlockFace.UP))) // for when the ClosedAirSpaceChecker and WalkingGround are out of sync..?  TODO  
					if (b.city.getFaction().equals(faction))
						return true;
				
				
				// gate type
				if (BlockUtils.isGateType(block.getRelative(BlockFace.UP).getType())) return true;
				
				if (block.getRelative(BlockFace.UP).getType() == Material.CARPET) return true;
				if (block.getRelative(BlockFace.UP).getType() == Material.WATER_LILY) return true;
				
				switch (block.getType())
				{
				case GRAVEL:
				case COBBLESTONE:
				case BRICK:
				case SMOOTH_BRICK:
				case WOOD:
				case WOOL:
					
				case SANDSTONE:
					
				case DIAMOND_BLOCK:
				case EMERALD_BLOCK:
				case GOLD_BLOCK:
				case IRON_BLOCK:
				case QUARTZ_BLOCK:
				
				case CLAY:
				case STAINED_CLAY:
				case HARD_CLAY:
					
					
				case STEP:
				case DOUBLE_STEP:
				case WOOD_STEP:
				case WOOD_DOUBLE_STEP:
					
					
				case ACACIA_STAIRS:
				case BIRCH_WOOD_STAIRS:
				case BRICK_STAIRS:
				case COBBLESTONE_STAIRS:
				case DARK_OAK_STAIRS:
				case JUNGLE_WOOD_STAIRS:
				case NETHER_BRICK_STAIRS:
				case QUARTZ_STAIRS:
				case SANDSTONE_STAIRS:
				case SMOOTH_STAIRS:
				case SPRUCE_WOOD_STAIRS:
				case WOOD_STAIRS:
					
					
					return true;
				default:
					return false;
				}
			}
		};
	}

	/**
	 * utility function for closestBlockOnWalkableGround
	 * @param blockNow TODO
	 * @param next
	 * @return
	 */
	static Block getGround(Block blockNow, Block next) {
		Block nextCheckingBlock;
		nextCheckingBlock = next;
		if (WalkingGroundUtils.isValidWalkingGround(nextCheckingBlock)) return nextCheckingBlock;
		nextCheckingBlock = next.getRelative(BlockFace.UP);
		if (WalkingGroundUtils.isValidWalkingGround(nextCheckingBlock) && WalkingGroundUtils.isValidWalkingAirBlockOrGate(nextCheckingBlock.getRelative(0,2,0))) return nextCheckingBlock;
		nextCheckingBlock = next.getRelative(BlockFace.DOWN);
		if (WalkingGroundUtils.isValidWalkingGround(nextCheckingBlock) && WalkingGroundUtils.isValidWalkingAirBlockOrGate(nextCheckingBlock.getRelative(0,2,0))) return nextCheckingBlock;
		return null;
	}

	static void processWalkingBlock(WalkingGroundFinderResult blockFinderResult, LinkedList<WalkingGroundFinderResult> todo, HashSet<Block> checked) {
		Block nextBlock = blockFinderResult.block;
		if (checked.contains(nextBlock)) return;
		todo.addLast(blockFinderResult);
	}

	static public boolean isPath(Block block){
		if (!WalkingGroundUtils.isValidWalkingGround(block)) return false;
		if (block.getRelative(BlockFace.UP).getType() == Material.CARPET) return true;
		if (BlockUtils.isGateType(block.getRelative(BlockFace.UP).getType())) return true;
		switch(block.getType()){
		case GRAVEL:
		case COBBLESTONE_STAIRS:
		case BRICK_STAIRS:
		case SMOOTH_STAIRS:
			return true;
		default:
			return false;
		}
	}


}
