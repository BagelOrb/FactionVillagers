package utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class WalkingGroundUtils {


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
		if (WalkingGroundUtils.isValidWalkingGround(nextCheckingBlock) && WalkingGroundUtils.isValidWalkingAirBlockOrGate(blockNow.getRelative(0,3,0))) return nextCheckingBlock;
		return null;
	}


	static public boolean isValidWalkingGround(Block block){
		Block above = block.getRelative(BlockFace.UP);
		Block above2 = above.getRelative(BlockFace.UP);
		return WalkingGroundUtils.isValidWalkingGroundBlock(block) && WalkingGroundUtils.isValidWalkingAirBlockOrGate(above) && WalkingGroundUtils.isValidWalkingAirBlockOrGate(above2);
	}

	static public boolean isValidWalkingAirBlockOrGate(Block block) {
		if (BlockUtils.isFenceType(block.getRelative(BlockFace.DOWN).getType()))
			return false;
		if (BlockUtils.isGateType(block.getType()))
			return true;
		return WalkingGroundUtils.isValidWalkingAirBlock(block);
	}

	static public boolean isValidWalkingAirBlock(Block block) {
			if (BlockUtils.isFenceType(block.getRelative(BlockFace.DOWN).getType()))
				return false;
			switch (block.getType()) {
			case WATER:
			case LAVA:
			case STATIONARY_LAVA:
			case STATIONARY_WATER:
				return false;
			case STONE_PLATE:
			case WOOD_PLATE:
			case IRON_PLATE:
			case GOLD_PLATE:
				return true;
			
			default:
				return !block.getType().isSolid() ;
			}
		}

	static public boolean isValidWalkingGroundBlock(Block block) {
		if (BlockUtils.isFenceType(block.getType()))
			return false;
		switch (block.getType()) {
		case STATIONARY_LAVA:
		case STATIONARY_WATER:
		case WATER:
		case LAVA:
			return false;
		default:
			return block.getType().isSolid() ;
		}
	}

	public static Block getStandingGround(Block block) {
		Block blockAbove = block.getRelative(BlockFace.UP);
		if (!isValidWalkingAirBlockOrGate(blockAbove)) // top/bottom =  air/air or air/ground
			return WalkingGroundUtils.getSolidUnder(block);
		else											// ground/air or ground/ground
			return WalkingGroundUtils.getAirAbove(block).getRelative(BlockFace.DOWN);
			
	}

	static public Block getSolidUnder(Block block) {
		while (!isValidWalkingGroundBlock(block))
			block = block.getRelative(BlockFace.DOWN);
		return block;
	}

	static public Block getAirAbove(Block block) {
		while (!isValidWalkingAirBlockOrGate(block))
			block = block.getRelative(BlockFace.UP);
		return block;
	}

	public static boolean isUsedGate(Block block) {
			if (WalkingGroundUtils.useOpenGates)
				return BlockUtils.isGateType(block.getType());
			else
			{
	//			Debug.out("dont use open gates!!!");
				if (!BlockUtils.isGateType(block.getType()))
					return false;
				// otherwise
				if (BlockUtils.isOpenGate(block))
					return false;
				else 
					return true;
			}
		}
	//	public static abstract class BlockValuator {
	//		public abstract double value(Block block);
	//	}

	public static final boolean useOpenGates = true;
	public static final int maxNumberOfBlocksOnPathBetweenWaypPoints = 5;

}
