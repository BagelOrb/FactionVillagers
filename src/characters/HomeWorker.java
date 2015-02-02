package characters;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import utils.BlockChecker;
import utils.BlockUtils;
import utils.WalkingGroundFinder;
import buildings.HomeWorkBuilding;
import buildings.MetaDataUtils;
import factions.FactionUtils;

public abstract class HomeWorker extends ChestCharacter {
		
	public HomeWorker(String traitName) {
		super(traitName); 
	}
	private final BlockChecker walkOnlyAtHome = new BlockChecker() {
		
		@Override
		public boolean isValid(Block block) {
			return MetaDataUtils.getBuildings(block).contains(homeBuilding);
		}
	};
	
	Location jobLoc;
	@Override
	Location findJob() {
		
		try
		{
			final Block validBlock = ((HomeWorkBuilding) homeBuilding).jobBlockRequirement.validBlock;
			if (homeToJobNavigator == null)
			{
				BlockChecker blockChecker = new BlockChecker(){
				@Override
				public boolean isValid(Block block) {
					for (BlockFace face : BlockUtils.gewesten4)
						if (block.getRelative(BlockFace.UP).getRelative(face).equals(validBlock))
							return true;
					return false;
				}};
				
				WalkingGroundFinder.WalkingGroundFinderResult result = WalkingGroundFinder.closestBlockOnWalkableGround(
						getHomeLocation(), 1000, blockChecker, playersToShowSearchSpace, walkOnlyAtHome);
				this.homeToJobNavigator = new Navigator(result.wayPoints);
				jobLoc = result.block.getLocation().add(.5, 1, .5);
			}
			return jobLoc;
		}
		catch (Exception e)
		{
			if(Debug.debug)
				FactionUtils.sendMessage(homeBuilding.city.getFaction(), npc.getName() + " couldn't find his job!");
//				plugin.getServer().getLogger().info();	
			return null;
		}
	}

	@Override
	protected void cancelCurrentJob() {
	}

}
