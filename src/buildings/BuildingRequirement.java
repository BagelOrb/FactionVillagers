package buildings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import main.FactionVillagers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import utils.BlockChecker;
import utils.BlockUtils;
import utils.ChestUtils;
import utils.MatUtils;
import utils.Math2;
import utils.WalkingGroundFinder;
import utils.WalkingGroundFinder.WalkingGroundFinderResult;
import utils.WalkingGroundUtils;
import characters.ShowBlockChange;
import characters.Unemployed;
import city.City;

import com.massivecraft.factions.entity.Faction;

import factions.FactionUtils;

public abstract class BuildingRequirement {
	public String description;
	public abstract boolean isMetBy(Building building, boolean beforeBuilt);
	public BuildingRequirement(String desc) { this.description = desc; }
	
	
	
	public static BuildingRequirement minAirSpaceSize(final int size) {
		return new BuildingRequirement(" can't be this small! Min air blocks: "+size) {
			
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				return building.airSpaceSize >= size;
			}
		};
	}
	public static BuildingRequirement maxAirSpaceSize(final int size) {
		return new BuildingRequirement(" can't be this big! Max air blocks: "+size) {
			
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				return building.airSpaceSize <= size;
			}
		};
	}
	
	public static BuildingRequirement hasEnoughMaterial(final Material mat, final int amount) {
		return new BuildingRequirement(" must contain at least "+amount+" "+
				MatUtils.prettyPrint(mat) + ((amount==1)? "" : "s")  ) 
		{
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				int timesMet = 0;
				for (Block b : building.airSpaceChecker.borderBlocks)
				{
					if (b.getType() == mat)
						timesMet++;
					
					if(timesMet >= amount)
						return true;
				}
				return false;
			}
		};
	}
	
	public static BuildingRequirement hasMaterial(final Material mat) {
		return new BuildingRequirement(" must contain a "+
							MatUtils.prettyPrint(mat)) {
			
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				boolean ret = false;
				for (Block b : building.airSpaceChecker.borderBlocks)
					if (b.getType() == mat)
					{
//						MetaDataUtils.setBelongingTo(b, building);
						ret = true;
					}
				return ret;
			}
		};
	}
	public static BuildingRequirement hasDoor() {
		return new BuildingRequirement(" must contain a door!") {
			
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				boolean ret = false;
				for (Block b : building.airSpaceChecker.borderBlocks)
					if (BlockUtils.isDoorType(b.getType()))
					{
						ret = true;
					}
				return ret;
			}
		};
	}
	public static BuildingRequirement hasBlock(final BlockChecker blockChecker) {
		return new BuildingRequirement(" must contain a "+blockChecker) {
			
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				boolean ret = false;
				for (Block b : building.airSpaceChecker.borderBlocks)
					if (blockChecker.isValid(b))
					{
//						MetaDataUtils.setBelongingTo(b, building);
						ret = true;
					}
				return ret;
			}
		};
	}
	public static class HasBlock extends BuildingRequirement {
		
		final BlockChecker blockChecker;
		public Block validBlock;
		
		public HasBlock(final BlockChecker blockChecker) {
			super(" must contain a "+blockChecker);
			this.blockChecker = blockChecker;
		}
		
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
//			boolean ret = false;
			for (Block b : building.airSpaceChecker.borderBlocks)
				if (blockChecker.isValid(b))
				{
//					MetaDataUtils.setBelongingTo(b, building);
					validBlock = b;
//					ret = true;
					return true;
				}
//			return ret;
			return false;
		}
			
		
	}
	public static class HasMatWithFreeSpace extends BuildingRequirement {
		
		final Material mat;
		private  Location freeLocation;
		
		public HasMatWithFreeSpace(final Material mat) {
			super(" must contain a "+MatUtils.prettyPrint(mat)+ " with an adjacent free spot");
			this.mat = mat;
		}
		
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			Location homeLoc = building.homeLocationRequirement.getHomeLocation();
			if (homeLoc ==null)
			{
				return true; // we first need a home location!
			}
			LinkedList<Location> freeLocations = new LinkedList<Location>();
			for (Block b : building.airSpaceChecker.borderBlocks)
				if (b.getType() == mat)
				{
//					MetaDataUtils.setBelongingTo(b, building);
					for (BlockFace face : BlockUtils.gewesten4)
					{
						Block ground = WalkingGroundUtils.getSolidUnder(b.getRelative(face));
						if (WalkingGroundUtils.isValidWalkingGround(ground))
						{
							freeLocations.add(ground.getLocation().add(.5, 1, .5));
						}
					}
				}
			if (freeLocations.isEmpty())
			{
				return false;
			}
			else 
			{
				setLocation(freeLocations.getFirst());
				double bestDistance = getLocation().distanceSquared(homeLoc);
				for (Location loc : freeLocations)
					if (loc.distanceSquared(homeLoc) < bestDistance)
					{
						setLocation(loc);
						bestDistance = loc.distanceSquared(homeLoc);
					}
			}
			return true;
		}

		public Location getLocation() {
			return freeLocation;
		}

		public void setLocation(Location freeLocation) {
			this.freeLocation = freeLocation;
		}
		
		
	}
	public static class HasEnoughMatsWithFreeSpaces extends BuildingRequirement {
		
		private final Material mat;
		public HashMap<Block, Location> blocksWithFreeSpace;
		private final int amountNeeded;
		
		public HasEnoughMatsWithFreeSpaces(final int amount, final Material mat) {
			super(" must contain at least "+amount+" "+MatUtils.prettyPrint(mat)+ "s with an adjacent free spot");
			this.mat = mat;
			this.amountNeeded = amount;
		}
		
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			blocksWithFreeSpace = new HashMap<Block, Location>();
					
			Location homeLoc = building.homeLocationRequirement.getHomeLocation();
			if (homeLoc ==null)
			{
				return true; // we first need a home location!
			}
			for (Block b : building.airSpaceChecker.borderBlocks)
				if (b.getType() == mat)
				{
					LinkedList<Location> freeLocationsHere = new LinkedList<Location>();
//					MetaDataUtils.setBelongingTo(b, building);
					for (BlockFace face : BlockUtils.gewesten4)
					{
						Block ground = WalkingGroundUtils.getSolidUnder(b.getRelative(face));
						if (WalkingGroundUtils.isValidWalkingGround(ground))
						{
							freeLocationsHere.add(ground.getLocation().add(.5, 1, .5));
						}
					}
					
					if (!freeLocationsHere.isEmpty())
					{
						Location freeLocation = freeLocationsHere.getFirst();
						double bestDistance = freeLocation.distanceSquared(homeLoc) + 2*Math2.square(freeLocationsHere.getFirst().getY() - b.getY());
						for (Location loc : freeLocationsHere)
							if (loc.distanceSquared(homeLoc) < bestDistance)
							{
								freeLocation = loc;
								bestDistance = loc.distanceSquared(homeLoc) + 2*Math2.square(loc.getY() - b.getY());
							}
						blocksWithFreeSpace.put(b, freeLocation);
					}
				}
			if (blocksWithFreeSpace.size() < amountNeeded)
				return false;
			else 
				return true;
		}
		
		
	}
	public static final BuildingRequirement hasFreeStartingBlock = new BuildingRequirement(" needs space for its starting block") {
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			if (!beforeBuilt) return true;
			return building.startingBlock.getType() == Material.AIR;
		}
	};
	
	public static final BuildingRequirement hasAirOrSignAboveStartingBlock = new BuildingRequirement(" needs space for its sign above the starting block") {
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			if (!beforeBuilt) return true;
			Block blockAboveStartingBlock = building.startingBlock.getRelative(BlockFace.UP);
			
			return blockAboveStartingBlock.getType() == Material.AIR || blockAboveStartingBlock.getType() == Material.SIGN_POST || blockAboveStartingBlock.getType() == Material.WALL_SIGN;
		}
	};
	
	public static final BuildingRequirement hasFreeChestSpace = new BuildingRequirement("'s starting chest can't be connected to another chest") {
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			Location loc = building.startingBlock.getLocation();
			if (building.startingBlock.getWorld().getBlockAt(loc.getBlockX()+1, loc.getBlockY(), loc.getBlockZ()).getType() == Material.CHEST ) return false;
			if (building.startingBlock.getWorld().getBlockAt(loc.getBlockX()-1, loc.getBlockY(), loc.getBlockZ()).getType() == Material.CHEST ) return false;
			if (building.startingBlock.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()+1).getType() == Material.CHEST ) return false;
			if (building.startingBlock.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()-1).getType() == Material.CHEST ) return false;
			return true;
		}
	};
	
	public static final BuildingRequirement isOnlyBuildingInAirSpace = new BuildingRequirement(" cannot be a room with another villager registered") {
		
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			description = // " ("+building.getPrettyFullyQualifiedId()+")" +
                    " cannot be inside a room with another villager registered";
			
			
			// new code:
//			for (Block borderBlock : building.airSpaceChecker.borderBlocks)
//			{
//				if (MetaDataUtils.belongsToBuilding(borderBlock)) {
//					List<Building> otherBuildings = MetaDataUtils.getBuildings(borderBlock);
//					otherBuildings.remove(building);
//					boolean isStartingBlockOfOtherBuilding = false;
//					for (Building otherBuilding : otherBuildings) 
//					{
//						if (otherBuilding.startingBlock == borderBlock) //  && otherBuilding != building
//							isStartingBlockOfOtherBuilding = true;
//					}
//					if (!isStartingBlockOfOtherBuilding) continue;
//					
//					description += " ( ";
//					for (Building otherBuilding : otherBuildings)
//						description += otherBuilding.getPrettyFullyQualifiedId()+" ";
//					Location loc = borderBlock.getLocation();
//					description += " at "+loc.getX()+", "+loc.getY()+", "+loc.getZ()+")";
//					return false; // we dont even check for other buildings... the first other building generates the error
//
//				}
//			}
			
			// old code: (checking all buildings of all cities...
			for (Block borderBlock : building.airSpaceChecker.borderBlocks)
			{
				for (City city : FactionVillagers.allCities) // for each building in the world (one building can cover multiple factions!
				for (Building otherBuilding : city.getAllBuildings())
				{
					if (otherBuilding == building) continue;
					if (otherBuilding.startingBlock.equals(borderBlock))
					{
						description += " ("+otherBuilding.prettyPrintFullyQualifiedBuildingPath()+")";
						return false;
					}
				}
			}
			return true; // otherwise
		}
	};
	public static final BuildingRequirement canOnlyBeBuiltOnce = new BuildingRequirement(" can only be built once") {
		
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			int buildingsNeeded = (beforeBuilt)? 0 : 1;
			return building.city.buildings.get(building.getBuildingType()).size() == buildingsNeeded;
		}
	};

	public static final BuildingRequirement cityHasStorageRoom = new BuildingRequirement(" must be in a city with a storage room") {
		
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			if (!beforeBuilt)
				return true;
			else
				return building.city.storageRoom != null;
		}
	};

	/*
	 * checking whether there is an area on the level of the starting block with air blocks (2 high) if the supplied minimum size
	 */
	@Deprecated public static BuildingRequirement hasGroundFloorWalkSpace(final int area2D) {
		return compose(new HomeLocationRequirement(), new BuildingRequirement(" must have a walking area of "+area2D){
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				return building.airSpaceChecker.sizeWalkingSpace(building.startingBlock) >= area2D;
			}});
	}
	
	public static class HomeLocationRequirement extends BuildingRequirement {
		public HomeLocationRequirement() {
			super("'s chest must be accesible");
		}
		Block nextToChest;
		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			if (!beforeBuilt && building instanceof ChestBuilding) {
				nextToChest = building.startingBlock.getRelative(ChestUtils.getDirection(building.startingBlock));
				if (!nextToChest.getType().isSolid() && (nextToChest.getRelative(BlockFace.DOWN, 1).getType().isSolid() )) 
					return true;
			}
			Block b = building.startingBlock;
			for (BlockFace face : BlockUtils.gewesten4)
			{
				nextToChest = b.getRelative(face);
				if (!nextToChest.getType().isSolid() && (nextToChest.getRelative(BlockFace.DOWN, 1).getType().isSolid() )) 
					return true;
				nextToChest = b.getRelative(face).getRelative(BlockFace.DOWN);
				if (!nextToChest.getType().isSolid() && (nextToChest.getRelative(BlockFace.DOWN, 1).getType().isSolid() )) 
					return true;
			}
			// otherwise
			return false;
		}
		
		public Location getHomeLocation() {
			return nextToChest.getLocation().clone().add(.5, 0, .5);
		}
	}
	
	public static BuildingRequirement compose (final BuildingRequirement first, final BuildingRequirement second) {
		return new BuildingRequirement(first.description +" and it"+second.description){
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				return first.isMetBy(building, true) && second.isMetBy(building, true);
			}};
	}
	public static BuildingRequirement isOnFactionLand(final Faction faction) {
		return new BuildingRequirement(" must be built on your own faction terrain") {
			
			@Override
			public boolean isMetBy(Building building, boolean beforeBuilt) {
				return FactionUtils.getFactionAt(building.startingBlock.getLocation()) == faction;
			}
		};
	}
	
	public static class VillagerPoolSizeRequirement extends BuildingRequirement{
		
		final List<Unemployed> pool;
		List<Unemployed> chosen;
		final int minSize;
		 
		public VillagerPoolSizeRequirement(List<Unemployed> list, int minSize) {
			super(" needs "+ minSize + " worker"+((minSize==1)? "": "s"));
			this.pool = list;
			this.minSize = minSize;
		}

		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
//			Debug.out("beforeBuilt="+beforeBuilt);
			if (!beforeBuilt)
				return true;
			
			if (pool==null)
				return false;
//			Debug.out("pool.size()="+pool.size());
			if (pool.size() < minSize)
				return false;
			
			chosen = pool.subList(0, minSize);
			return true;
		}
		
	}
	
	
	public static class ConnectedToTownHallViaPath extends BuildingRequirement  {
		
		WalkingGroundFinderResult pathToHome;
		
		private BlockChecker WalkOnPathsOrBorderBlocksOfHomeBuilding(final Building home) {
			return new BlockChecker() {
				private final BlockChecker pathChecker = WalkingGroundFinder.pathChecker(home.city.getFaction());
				
				@Override
				public boolean isValid(Block block) {
					if (pathChecker.isValid(block))
						return true;
					
					if (home.airSpaceChecker.borderBlocks.contains(block))
						return true;
//					if (home.airSpaceChecker.borderBlocks.contains(block.getRelative(BlockFace.UP)))
//						return true;
					// otherwise
					return false;
				}
			};
		}
		
		public ConnectedToTownHallViaPath() {
			super(" must be connected to the Town Hall via paths");
		}

		@Override
		public boolean isMetBy(Building building, boolean beforeBuilt) {
			if (!beforeBuilt)
				return true;
			
			if (building instanceof TownHall)
				return true;
			
			if (!building.homeLocationRequirement.isMetBy(building, beforeBuilt))
				return true; // exception catching! TODO ? now we only show this requirement when the homeLocationRequirement is met!
			
			if (building.city.townHall == null)
				return false;
			
			Location origin = building.city.townHall.homeLocationRequirement.getHomeLocation();
			int max = 8000;
			BlockChecker blockChecker = BlockChecker.checkForExactLocation(building.homeLocationRequirement.getHomeLocation().clone().add(0, -1, 0));
			BlockChecker walkOnlyOn = WalkOnPathsOrBorderBlocksOfHomeBuilding(building);
			
			
			HashSet<Player> playersToShowSearchSpace = new HashSet<Player>();
			for (Player player : building.city.getFaction().getOnlinePlayers())
				if (player.isOp())
					playersToShowSearchSpace.add(player);
			
			ShowBlockChange.showAs(building.airSpaceChecker.borderBlocks, Material.LAPIS_BLOCK, playersToShowSearchSpace, 100);

			pathToHome = WalkingGroundFinder.closestBlockOnWalkableGround(origin , max , blockChecker, playersToShowSearchSpace , walkOnlyOn);
			
			if (pathToHome == null)
				return false;
			else
				return true;
		}
	}

	
//	public static class ConditionalRequirement extends BuildingRequirement {
//
//		boolean holds;
//		public ConditionalRequirement(BuildingRequirement condition, BuildingRequirement then) {
//			super(then.description);
//		}
//		
//	}
}
