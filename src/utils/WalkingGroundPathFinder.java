package utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import characters.Navigator;
import characters.Navigator.GoAndDo;
import characters.ShowBlockChange;

public class WalkingGroundPathFinder {
	public static final boolean useOpenGates = true;
	public static final int maxNumberOfBlocksOnPathBetweenWaypPoints = 5;

	public static class WalkingGroundPathFinderResult {
			public Block block;
			public BlockFace fromDirection;
			int nBlocksOnPathSinceLastWayPoint;
			public List<Block> wholePath = new LinkedList<Block>();
			public List<GoAndDo> wayPoints = new LinkedList<GoAndDo>();
			public WalkingGroundPathFinderResult(Block b, BlockFace from, int nBlocksOnPathSinceLastWayPoint) {
				block = b; fromDirection = from;
				this.nBlocksOnPathSinceLastWayPoint = nBlocksOnPathSinceLastWayPoint;
			}
			public WalkingGroundPathFinderResult nextBlock(final Block blockNext, BlockFace fromDirectionNext, boolean computeWholePath, boolean useGates) {
				WalkingGroundPathFinderResult ret = new WalkingGroundPathFinderResult(blockNext, fromDirectionNext, nBlocksOnPathSinceLastWayPoint+1);
				if (computeWholePath)
				{
					ret.wholePath = new LinkedList<Block>(this.wholePath);
					ret.wholePath.add(this.block);
				}
				ret.wayPoints = new LinkedList<GoAndDo>(this.wayPoints);
				if (	  (WalkingGroundUtils.isUsedGate(this.block.getRelative(BlockFace.UP)))  
						|| WalkingGroundUtils.isUsedGate(this.block.getRelative(BlockFace.UP, 2) )
					  && useGates) // close gate behind you
				{
					ret.nBlocksOnPathSinceLastWayPoint = 0;
					ret.wayPoints.add(new Navigator.GoAndCloseGate(blockNext.getLocation().add(.5, 1, .5), 
																block.getRelative(BlockFace.UP)));
				}
				if (	  (WalkingGroundUtils.isUsedGate(blockNext.getRelative(BlockFace.UP)))
						|| WalkingGroundUtils.isUsedGate(blockNext.getRelative(BlockFace.UP, 2))
					&& useGates) // open gate in front of you
				{
					ret.nBlocksOnPathSinceLastWayPoint = 0;
					ret.wayPoints.add(new Navigator.GoAndOpenGate(this.block.getLocation().add(.5, 1, .5), 
																blockNext.getRelative(BlockFace.UP)));
				}
	//			else if (wayPoints.get(wayPoints.size()-1).loc.distance(blockNext.getLocation()) > nBlocksOnPathBetweenWaypPoints)
				else if (nBlocksOnPathSinceLastWayPoint > maxNumberOfBlocksOnPathBetweenWaypPoints)
				{
					ret.nBlocksOnPathSinceLastWayPoint = 0;
					ret.wayPoints.add(GoAndDo.newNoAction(blockNext.getLocation().add(.5, 1, .5)));
				}
				return ret;
			}
			
		}

	static public WalkingGroundPathFinderResult closestBlockOnWalkableGround(Location origin, int max, BlockChecker blockChecker, boolean stayOnPath, boolean showSearchSpace, final HashSet<Player> playersToShowSearchSpace, boolean computeWholePath, boolean useGates, BlockChecker pathChecker2) {
			HashSet<Block> checked = new HashSet<Block>();
			Block startBlock = WalkingGroundUtils.getStandingGround(origin.getBlock());
			LinkedList<WalkingGroundPathFinderResult> todo = new LinkedList<WalkingGroundPathFinderResult>(); 
			
			WalkingGroundPathFinderResult preBlockFinderResult = new WalkingGroundPathFinderResult(startBlock.getRelative(BlockFace.UP), BlockFace.UP, 0);
			preBlockFinderResult.wayPoints.add(GoAndDo.newNoAction(origin));
			todo.addLast(preBlockFinderResult.nextBlock(startBlock, BlockFace.UP, computeWholePath, useGates) );
			
	//		BlockFinderResult startingBlockFinderResult = new BlockFinderResult(startBlock, BlockFace.UP, 0);
	//		startingBlockFinderResult.wayPoints.add(GoAndDo.newNoAction(origin));
	//		todo.addLast(startingBlockFinderResult);
			
			while (!todo.isEmpty() && checked.size()<max)
			{
				WalkingGroundPathFinderResult blockResultNow = todo.poll();
				Block blockNow = blockResultNow.block;
				if (checked.contains(blockNow)) continue;
				if (blockChecker.isValid(blockNow) && !showSearchSpace) {
	//                Debug.out("DON'T DELETE THIS!"); // I have seen this mesage!
					return blockResultNow;
				}
				checked.add(blockNow);
	//			if (Debug.debug) blockNow.getRelative(0,0,0).setType(selectedMaterial);
				
				
				
				boolean isStraightPath = isStraightPath(blockNow, pathChecker2);
				

				
				double fluentPathPriority = .9-.9*Math.pow(((double)checked.size())/((double)max), (stayOnPath)? 1 : 3);// pathPriority;
				
				for (BlockFace face : BlockUtils.gewesten4)
				{
					Block directlyNext = blockNow.getRelative(face);
					if (blockChecker.isValid(directlyNext)  && !showSearchSpace)
					{
						WalkingGroundPathFinderResult result = blockResultNow.nextBlock(directlyNext, face.getOppositeFace(), computeWholePath, useGates);
						return result;
					}
					Block next = WalkingGroundUtils.getGround(blockNow, directlyNext);
					if (next == null) continue;
					
					
					WalkingGroundPathFinderResult nextResult = blockResultNow.nextBlock(next, face.getOppositeFace(), computeWholePath, useGates);
					if (isStraightPath && stayOnPath) {
						if (pathChecker2.isValid(next))
							processWalkingBlock(nextResult, todo, checked, fluentPathPriority, pathChecker2); // pathPriority
					}
					else processWalkingBlock(nextResult, todo, checked, fluentPathPriority, pathChecker2); // pathPriority
				}
			}
	//        Debug.out("couldn't find block!");
			
	        if (showSearchSpace)
	        {
	        	ShowBlockChange.showAs(checked, Material.REDSTONE_BLOCK, playersToShowSearchSpace, 1000);
	        }
			return null;
		}

	private static boolean isStraightPath(Block blockNow, BlockChecker pathChecker2) {
		Block groundN = WalkingGroundUtils.getGround(blockNow, blockNow.getRelative(BlockFace.NORTH));
		Block groundE = WalkingGroundUtils.getGround(blockNow, blockNow.getRelative(BlockFace.EAST));
		Block groundS = WalkingGroundUtils.getGround(blockNow, blockNow.getRelative(BlockFace.SOUTH));
		Block groundW = WalkingGroundUtils.getGround(blockNow, blockNow.getRelative(BlockFace.WEST));
		boolean nsPath = false;
		boolean ewPath = false;
		if (groundN != null && groundS != null && pathChecker2.isValid(groundN) && pathChecker2.isValid(groundS))
			nsPath = true;
		if (groundE != null && groundW != null && pathChecker2.isValid(groundE) && pathChecker2.isValid(groundW))
		
		if (nsPath && ! ewPath)
			return true;
		if (!nsPath && ewPath)
			return true;
		
		return false;
	}

	public static final BlockChecker pathChecker = new BlockChecker(){
	@Override
	public boolean isValid(Block block) {
		return isPath(block);
	}};


	static void processWalkingBlock(WalkingGroundPathFinderResult blockFinderResult, LinkedList<WalkingGroundPathFinderResult> todo, HashSet<Block> checked, double pathPriority, BlockChecker prioritizer) {
		Block nextBlock = blockFinderResult.block;
		if (checked.contains(nextBlock)) return;
		
	
		
		if (prioritizer.isValid(nextBlock)) {
			int pos = (int) ((1.-pathPriority) *todo.size());
			if (pos>= todo.size()) pos = todo.size()-1;
			if (pos<0) pos = 0; // dont switch order or introduce else!!! (otherwise it can get -1!)
			todo.add(pos, blockFinderResult);
		}
		else
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
