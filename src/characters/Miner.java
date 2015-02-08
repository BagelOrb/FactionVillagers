package characters;

import generics.Tuple;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import utils.BlockChecker;
import utils.BlockMatState;
import utils.BlockUtils;
import utils.WalkingGroundFinder;
import utils.WalkingGroundUtils;
import buildings.Mine;
import characters.MineUtils.HomeFindResult;
import characters.MineUtils.MineFindResult;
import characters.MineUtils.MineFindResult.Sort;

public class Miner extends ChestCharacter {
	static final Random rnd = new Random();

	public static final String traitName = "miner";
	@Override
	String getTraitName() {
		return traitName;
	}

	private static final int numberOfCollectsPerBatch = 							plugin.getConfig().getInt("character."+traitName+".numberOfCollectsPerBatch");
	private static final int jobRange = 											plugin.getConfig().getInt("character."+traitName+".jobRange");
	protected static final int maxRailsChecked = 									plugin.getConfig().getInt("character."+traitName+".maxRailsChecked");
	protected static final byte minLightLevel = (byte) 								plugin.getConfig().getInt("character."+traitName+".minLightLevel");
	protected static final long mineOrPutWaitingTime = 								plugin.getConfig().getLong("character."+traitName+".mineOrPutWaitingTime");
	
	private static final double chanceToCreateNewPath = 							plugin.getConfig().getDouble("character."+traitName+".chanceToCreateNewPath");
	private static final double chanceToFixLighting = 								plugin.getConfig().getDouble("character."+traitName+".chanceToFixLighting");
	private static final double chanceToCreateNewPathFromCornerInsteadOfJunction = 	plugin.getConfig().getDouble("character."+traitName+".chanceToCreateNewPathFromCornerInsteadOfJunction");
	private static final double chanceToStartStairs = 								plugin.getConfig().getDouble("character."+traitName+".chanceToStartStairs");
	private static final double chanceToKeepMakingStairs = 							plugin.getConfig().getDouble("character."+traitName+".chanceToKeepMakingStairs");
	protected static final double chanceToKeepDiggingForWard = 						plugin.getConfig().getDouble("character."+traitName+".chanceToKeepDiggingForWard");
	
	protected ItemStack toolHeld = new ItemStack(Material.IRON_PICKAXE);
	
//	private static final double chanceToWorkOnExistingPath = 1-.85-.05 = .1; 

	private int collectRemaining;
	
	private Block getPoweredRailBlock() {
		return homeBuilding.city.mine.poweredRailRequirement.validBlock;
	};

	private static final int maxMiningY = 54;

	private static final double takeJobsCloserToHome = 1.2; // 0 - 1 is go further away ; 1 - infinity is take closer jobs.. normal is between 1 and 2 

	private static MineFindResult getRandomJob(LinkedList<MineFindResult> jobs) {
		double r = rnd.nextDouble();
		
		double totalValue = 0;
		for (MineFindResult job : jobs)
			totalValue += job.valueOfJob;
		
		double totalValueNow = 0;
		for (MineFindResult job : jobs)
		{
			totalValueNow += job.valueOfJob;
			if (r < totalValueNow / totalValue)
				return job;
		}
		return jobs.getLast();
		
		// code for randomly picking from list:
//		double p = r*r;
//		int pos = (int) (p*lightingJobs.size());
//		if (pos < 0) pos = 0;
//		if (pos >= lightingJobs.size()) pos = lightingJobs.size()-1;
//		return lightingJobs.get(pos);
	}
	
//	private Navigator homeToJobNavigator;
//	private Navigator jobToHomeNavigator;

	public Miner() {
		super(traitName);
	}
	public CharacterType getCharacterType() { return CharacterType.MINER; }
	
	
	public boolean isFinishedMining() {
		if (!InventoryTraitUtils.containsAtLeast(inventory, Material.WOOD, 1))
		{	
//			Debug.out("not enough wood! inventory="+inventory);
			return true;
		}
		if (!InventoryTraitUtils.containsAtLeast(inventory, Material.COBBLESTONE, 1))
		{
//			Debug.out("not enough cobble! inventory="+inventory);
			return true;
		}
		if (collectRemaining <= 0)
		{
//			Debug.out("collected all!");
			return true;
		}
		return false;
	}
	
//	@Override
//	public void construct(BuildingWithStorage building) {
//		super.construct(building);
//	}
	


	@Override
	public Location findJob() { // = find mine
		if (homeBuilding.city.mine == null)
			return null;
		else 
		{
			if (this.homeToJobNavigator == null)
			{
				setNewHomeToJobNavigator();
			}
			return getPoweredRailBlock().getLocation().add(.5, 0, .5);
		}
	}

	private void setNewHomeToJobNavigator() {
		WalkingGroundFinder.WalkingGroundFinderResult result = 
				WalkingGroundFinder.closestBlockOnWalkableGround(
						getHomeLocation(), 
						4000, 
						BlockChecker.checkForExactLocation(getPoweredRailBlock().getLocation().add(0, -1, 0)), 
						playersToShowSearchSpace, 
						WalkingGroundFinder.pathChecker(homeBuilding.city.getFaction()));
		if (result == null)
		{
			Debug.out(this.getName()+" couldn't find the mine!");
			homeToJobNavigator = new Navigator(); // is simply home to home ... navigator is empty.. no waypoints...
			jobToHomeNavigator = homeToJobNavigator.reversed();
		}
		else
		{
			//Debug.out("finding new route for home to mine");
			//Debug.out("\\_> waypoints:");
			//for (GoAndDo q : result.wayPoints)
			//	Debug.out(q.getActionType() +" @ "+q.loc);
			homeToJobNavigator = new Navigator(result.wayPoints);
			jobToHomeNavigator = homeToJobNavigator.reversed();
		}
	}
	@Override
	public void onSpawn() {
		super.onSpawn();
	}
	
	@Override
	public boolean consume() {
		boolean hasConsumption = true;
		
		if(selectedProduction != null)
		{
			for (ItemStack toBeConsumed : selectedProduction.getItemsNeededToConsume())
				hasConsumption = hasConsumption && InventoryTraitUtils.containsAtLeast(inventory, toBeConsumed.getType(), toBeConsumed.getAmount());
			
			if (hasConsumption)
			{
				
				List<ItemStack> couldntRemove = new LinkedList<ItemStack>();
				
				for (ItemStack consumed : selectedProduction.getItemsNeededToConsume()) 
					if(!MineUtils.isConsumedDuringMiningType(consumed.getType()))
					{
						homeBuilding.city.statistics.consume(homeBuilding, consumed.getType(), consumed.getAmount()); // register consumption
						couldntRemove.addAll(InventoryTraitUtils.getItem(inventory, consumed).snd);
						Debug.out("miner removes " +consumed.getAmount() + " "+ consumed.getType()+ "from inv for consumption");
					}
				
				if (!couldntRemove.isEmpty()) 
					Debug.out("couldnt remove consumption from own inv eventhough he checked!");
			}
		}
		
		return hasConsumption;
	}

	@Override
	Action doJob() { // when arrived at mine
//		consume();
		
//		Debug.out("Miner inv content: ");
//		for (ItemStack is : inventory.getContents())
//		{
//			if (is == null)
//				Debug.out("none");
//			else
//				Debug.out(is.getAmount()+" " +is.getType());
//		}
		
		Mine mine = homeBuilding.city.mine;
		boolean isActive = mine.recheckAndShowMessage(null, false, true);
		
		if (!isActive || !consume())
		{
			return getJobToHomeNavigatorAction();
		}
		else 
		{
			collectRemaining = numberOfCollectsPerBatch;
			
			Iterable<MineFindResult> possibleJobs = MineUtils.findMineJobs(this.playersToShowSearchSpace, getPoweredRailBlock(), false, jobRange);
			MineFindResult chosenJob = chooseJob(possibleJobs);
	
			if (chosenJob == null)
				return doGetReadyToWork; // TODO: do something else! try to make a new road somewhere?
			
			if (chosenJob.fromDirection == BlockFace.SELF)
				Debug.warn("First time looking for job returned the first block! WTF?!!!"); 
			
			return goTo(chosenJob);
		}
	}

//	private static class MineFindResult {
//		Navigator
//	}
	
	private Action goTo(MineFindResult chosenJob) {
		return new Navigator(chosenJob.wayPoints).toAction(this, mine(chosenJob), doGetReadyToWork); // TODO check cantNavigate (now its doGetReadyToWork...)
//				jobToHomeNavigator.toAction(this, doHomeMoveThrough, doHomeMoveThrough);
	}
	
	private MineFindResult chooseJob(Iterable<MineFindResult> possibleJobs) {
		LinkedList<MineFindResult> cornerJobs = new LinkedList<MineFindResult>();
		LinkedList<MineFindResult> junctionJobs = new LinkedList<MineFindResult>();
		LinkedList<MineFindResult> endPointJobs = new LinkedList<MineFindResult>();
		LinkedList<MineFindResult> lightingJobs = new LinkedList<MineFindResult>();
		for (MineFindResult res : possibleJobs)
			switch (res.sort) {
			case CORNER:
				cornerJobs.addLast(res); // we want to preserve the order!
				break;
			case JUNCTION:
				junctionJobs.addLast(res);
				break;
			case END_POINT:
				endPointJobs.addLast(res);
				break;
			case LIGHTING:
				lightingJobs.addLast(res);
				break;
			case NONE:
			}
		
		double r = Math.pow(rnd.nextDouble(), takeJobsCloserToHome);
		if ((cornerJobs.size()>0 || junctionJobs.size() >0) && 
				(r<chanceToCreateNewPath || 
						(endPointJobs.size() == 0 && lightingJobs.size() == 0)) )
			if (cornerJobs.size() > 0 
					&& (rnd.nextDouble() < chanceToCreateNewPathFromCornerInsteadOfJunction 
							|| junctionJobs.size()==0))
				return getRandomJob(cornerJobs);
			else
				return getRandomJob(junctionJobs);
		else if (lightingJobs.size()>0 &&
				(r< chanceToCreateNewPath + chanceToFixLighting || endPointJobs.size() == 0))
			return getRandomJob(lightingJobs); 
		else if (endPointJobs.size()>0)
			return getRandomJob(endPointJobs);
		else 
		{
			Debug.out("miner can't find job!");
			return null;
		}
	}

	
	private Action mine(final MineFindResult chosenJob) {
		return new Action(){

			@Override
			public Location getLocation() {
				return chosenJob.block.getLocation().add(.5, 0, .5); // you can stand in the carpet/rail
			}

			@Override
			public Action doAction() {
				//check whether we should still do the job, maybe some other miner has changed the situation
				Sort jobSortNow = MineUtils.getJobSort(chosenJob.block);
				
//				if (chosenJob.sort != Sort.LIGHTING && jobSortNow != chosenJob.sort)
				if (jobSortNow != chosenJob.sort)
				{
					boolean jobFinished = true;
					return getNextJobAction(chosenJob, jobFinished);
				}
				else
				{
					switch(chosenJob.sort) {
					case END_POINT:
						return doEndPoint(chosenJob);
					case CORNER:
						return doJunction(chosenJob);
					case JUNCTION:
						return doJunction(chosenJob);
					case LIGHTING:
						return doLighting(chosenJob);
					default: 
						Debug.warn("non-Job doAction called!! cannot perform any further action!");
						return null;
					}
				}
			}



			@Override
			public long getWaitingTime() {
				return mineOrPutWaitingTime;
			}

			@Override
			public Action cantNavigate() {
				// TODO do it like this? teleport to mine instead of home?
				teleport(getPoweredRailBlock().getLocation().add(.5, 0, .5));
				return doJob();
			}

			@Override
			public ActionType getActionType() { return ActionType.JOB;}
		};
		
	}

	private Action getNextJobAction(MineFindResult chosenJob, boolean jobFinished) {
		if (isFinishedMining())
			return getToMineBuildingNavigatorAction(chosenJob.block); 
		
		// find next job
		Block startingBlock;
//		startingBlock = chosenJob.block.getRelative(chosenJob.fromDirection);
		if (chosenJob.toDirection != null)
			startingBlock = chosenJob.block.getRelative(chosenJob.toDirection);
		else
			startingBlock = chosenJob.block.getRelative(chosenJob.fromDirection.getOppositeFace());
		
		
		boolean findOnlyNearestJob = !jobFinished; 
//		if (jobFinished)
//			Debug.out("finding totally new job");
//		else
//			Debug.out("continuing nearest job");
		List<MineFindResult> nearestJobs = MineUtils.findMineJobs(playersToShowSearchSpace, startingBlock, findOnlyNearestJob, maxRailsChecked);
		if (nearestJobs.size()== 0 )
			return getToMineBuildingNavigatorAction(chosenJob.block); 
		
		MineFindResult nearestJob = nearestJobs.get(0);
		
		if (nearestJob.fromDirection == BlockFace.SELF) // in case the block is the first block searched
			if (chosenJob.toDirection == null)
				nearestJob.fromDirection = chosenJob.fromDirection;
			else 
				nearestJob.fromDirection = chosenJob.toDirection.getOppositeFace();
		
		// go do job
		return goTo(nearestJob);
	}
	
//	public static class MiningResult {
//		boolean jobFinished;
//	}
	
	@Deprecated // cantNavigate Navigator action should teleport!!
	public Action teleportToMineAndGoHome() {
		return new Action(){

			@Override
			public Location getLocation() {
				return getPoweredRailBlock().getLocation().add(.5, 0, .5);
			}

			@Override
			public Action doAction() {
				// TODO do it like this? teleport to mine instead of home?
				teleport(getPoweredRailBlock().getLocation().add(.5, 0, .5));
				return getJobToHomeNavigatorAction();
			}

			@Override
			public long getWaitingTime() {
				return 0;
			}

			@Override
			public Action cantNavigate() {
				teleport(getPoweredRailBlock().getLocation().add(.5, 0, .5));
				return getJobToHomeNavigatorAction();
			}

			@Override
			public ActionType getActionType() {
				return ActionType.MOVE_THROUGH;
			}};
	}

	protected Action getToMineBuildingNavigatorAction(Block startBlock) {
		HomeFindResult result = MineUtils.findMineBuilding(playersToShowSearchSpace, startBlock, this.getPoweredRailBlock());
		if (result == null)
		{
			if(Debug.showCouldntFindJobDebug)
				Debug.out(getName()+" couldn't find mine-home!! = "+this.getPoweredRailBlock());
			return getJobToHomeNavigatorAction();
		}
		return new Navigator(result.wayPoints).toAction(this, getJobToHomeNavigatorAction(), doGetReadyToWork); // teleportToMineAndGoHome()
	}

	private Action doLighting(MineFindResult chosenJob) {
		LinkedList<Tuple<Block, BlockMatState>> neededToMake;
		if (chosenJob.prevBlock.getY() != chosenJob.block.getY())
			neededToMake = MineUtils.neededToMakePilars(chosenJob.block, chosenJob.fromDirection.getOppositeFace());
		else
			neededToMake = MineUtils.neededToMakeLighting(chosenJob.block, chosenJob.fromDirection.getOppositeFace());
			
		chosenJob.toDirection = chosenJob.fromDirection.getOppositeFace();
		return mineAndMake(chosenJob, neededToMake);
//		return true;
	}

	
	
	private Action doJunction(MineFindResult chosenJob) {
//		Debug.out("doJunction");
		Block fromRail = chosenJob.block;
		
		// get possible dirs
		LinkedList<BlockFace> possibleDirs = new LinkedList<BlockFace>();
		for (BlockFace dir : BlockUtils.gewesten4) {
			Block next = fromRail.getRelative(dir);
			if (
//					! MineUtils.isValidRailPosition(next) 
//					&& 
					! MineUtils.hasRailAt(next))
				possibleDirs.add(dir);
		}
			
		if (possibleDirs.size() == 0)
			return getNextJobAction(chosenJob, true);
		
		// choose dir
		final BlockFace chosenDiggingDir = possibleDirs.get(rnd.nextInt(possibleDirs.size()));
		chosenJob.toDirection = chosenDiggingDir;
		
		return tryDigging(fromRail, chosenDiggingDir, BlockFace.SELF, chosenJob);
	}

	private Action doEndPoint(MineFindResult chosenJob) {
		Block fromRail = chosenJob.block;
		// choose digging direction
		final BlockFace chosenDiggingDir;
	
		double r = rnd.nextDouble();
		
		BlockFace left = BlockUtils.getNextDirClockwise4(chosenJob.fromDirection);
		BlockFace right = BlockUtils.getNextDirAntiClockwise4(chosenJob.fromDirection);
		
		Block bLeft = fromRail.getRelative(left);
		Block bRight = fromRail.getRelative(right);
		Block bForward = fromRail.getRelative(chosenJob.fromDirection.getOppositeFace());
		boolean canGoLeft = ! BlockUtils.isMiningStop(bLeft.getType());
		boolean canGoRight = ! BlockUtils.isMiningStop(bRight.getType());
		boolean canGoForward = ! BlockUtils.isMiningStop(bForward.getType());
//		boolean canGoLeft = ! MineUtils.isValidRailPosition(bLeft);
//		boolean canGoRight = ! MineUtils.isValidRailPosition(bRight);
//		boolean canGoForward = ! MineUtils.isValidRailPosition(bForward);
		
		BlockFace verticalDir = BlockFace.SELF;
		if (chosenJob.prevBlock.getY() != chosenJob.block.getY())
		{
			chosenDiggingDir = chosenJob.fromDirection.getOppositeFace();
			if (rnd.nextDouble() < chanceToKeepMakingStairs)
				if (chosenJob.prevBlock.getY() == chosenJob.block.getY()-1)
					verticalDir = BlockFace.UP;
				else if (chosenJob.prevBlock.getY() == chosenJob.block.getY()+1)
					verticalDir = BlockFace.DOWN;
				else
					verticalDir = BlockFace.SELF;
		}
		else
		{
			if (canGoForward  
					&& (  r<chanceToKeepDiggingForWard ||
							( !canGoLeft && !canGoRight)))
			{
				if (rnd.nextDouble() < chanceToStartStairs)
				{
					if (fromRail.getY() < maxMiningY && rnd.nextBoolean())
						verticalDir = BlockFace.UP;
					else
						verticalDir = BlockFace.DOWN;
				}
				chosenDiggingDir = chosenJob.fromDirection.getOppositeFace();
			}
			else if (canGoLeft
					&& (rnd.nextBoolean() || !canGoRight))
				chosenDiggingDir = left;
			else if (canGoRight)
				chosenDiggingDir = right; 
			else
			{
				Debug.warn("Couldn't find a job direction from endPoint while finding-algorithm did think this was a good endPoint!");
				chosenDiggingDir = null;
				return getNextJobAction(chosenJob, true);
			}
		}
		chosenJob.toDirection = chosenDiggingDir;

		Debug.out("doEndPoint in dir "+chosenDiggingDir+ " from "+chosenJob.block.getX()+", "+chosenJob.block.getY()+", "+chosenJob.block.getZ());

		return tryDigging(fromRail, chosenDiggingDir, verticalDir, chosenJob);
	}

	
	enum MineStopReason { INTERRUPTS_OTHER_MINE, BRIDGE_TOO_HIGH, NONE, BUILDING, OTHER_FACTION_TERRAIN, UNMINABLE_TYPE, TOO_MANY_RAILS_HERE, HAS_COBBLE_WALL }; 
	
	private Action tryDigging(Block fromRail, BlockFace chosenDiggingDir, BlockFace vDir, MineFindResult chosenJob) {
		// check whether we can dig
		Block nextToJobPoint = fromRail.getRelative(chosenDiggingDir);
		Block toBecomeRail = nextToJobPoint.getRelative(vDir);
		
		Debug.out("tryDigging > toBecomeRail = "+toBecomeRail.getX()+", "+toBecomeRail.getY()+", "+toBecomeRail.getZ());
		
		MineStopReason mineStopReason = MineUtils.canMakeMine(toBecomeRail, chosenDiggingDir, Miner.this.homeBuilding.city.getFaction());
		if (mineStopReason == MineStopReason.NONE && !MineUtils.isValidRailPosition(toBecomeRail))
			mineStopReason = MineStopReason.INTERRUPTS_OTHER_MINE;
		if (mineStopReason == MineStopReason.NONE && toBecomeRail.getY() >= maxMiningY && !MineUtils.hasGround(toBecomeRail, chosenDiggingDir))
			mineStopReason = MineStopReason.BRIDGE_TOO_HIGH;
			
		
		boolean canDig = mineStopReason == MineStopReason.NONE;
		if (!canDig)
		{
			if (! BlockUtils.isMiningStop(nextToJobPoint.getType()) 
					&& mineStopReason != MineStopReason.HAS_COBBLE_WALL)
			{
				if (BlockUtils.isRailType(nextToJobPoint.getType()) 
						|| BlockUtils.isRailType(nextToJobPoint.getRelative(BlockFace.DOWN).getType()) )
					Debug.warn("Trying to set rail to mineStop!!!");
				else 
				{
					boolean hasBlockToPut = putBlock(nextToJobPoint, new BlockMatState(Material.COBBLE_WALL,-1));
					if (!hasBlockToPut)
						return getNextJobAction(chosenJob, true); // no shite to make cobble wall, continue...
					
					Block signBlock = nextToJobPoint.getRelative(BlockFace.UP);
					hasBlockToPut = putBlock(signBlock, new BlockMatState(Material.SIGN_POST,-1));
					if (hasBlockToPut)
					{
						org.bukkit.material.Sign matSign  = new org.bukkit.material.Sign(Material.SIGN_POST);
						Sign sign = (Sign) nextToJobPoint.getRelative(BlockFace.UP).getState();
						matSign.setFacingDirection(chosenDiggingDir.getOppositeFace());
						sign.setData(matSign);
						switch (mineStopReason)
						{
						case BRIDGE_TOO_HIGH:
							sign.setLine(0, "CAUTION!");
							sign.setLine(2, "Jumping");
							sign.setLine(3, "discouraged!");
							break;
						case INTERRUPTS_OTHER_MINE:
							sign.setLine(0, "CAUTION!");
							sign.setLine(1, "Tunnel ahead!");
							break;
						case BUILDING:
							sign.setLine(0, "CAUTION!");
							sign.setLine(1, "Private");
							sign.setLine(2, "proterty!");
							break;
						case NONE:
							sign.setLine(0, "CAUTION!");
							sign.setLine(2, "Cobble wall!");
							break;
						case OTHER_FACTION_TERRAIN:
							sign.setLine(0, "CAUTION!");
							sign.setLine(1, "Enemy");
							sign.setLine(2, "terrain!");
							break;
						case TOO_MANY_RAILS_HERE:
							sign.setLine(0, "CAUTION!");
							sign.setLine(1, "Sharp corner!");
							break;
						case UNMINABLE_TYPE:
							sign.setLine(0, "CAUTION!");
							sign.setLine(1, "No diggity!");
							break;
						case HAS_COBBLE_WALL:
							sign.setLine(0, "CAUTION!");
							sign.setLine(1, "See other");
							sign.setLine(2, "sign!");
							break;
						default:
							break;			
						}
						sign.update();
						
					}
					else
						return getNextJobAction(chosenJob, true); // no shite to make cobble wall, continue...
				}
			}
			else
			{
//				Debug.out("can't set mining stop, because we cant mine there! : "+toBocomeRail);
			}
			return getNextJobAction(chosenJob, true);
		}
		
		// do some diggin'
        // TODO: make separate blockChangables for stairs up / down!
		LinkedList<Tuple<Block, BlockMatState>> blockChangables = MineUtils.neededToMakeMine(toBecomeRail, chosenDiggingDir, Miner.this.homeBuilding.city.getFaction());
        return mineAndMake(chosenJob, blockChangables);
		
//		if (BlockUtils.isRailType(toBocomeRail.getType())
//				&& !MineUtils.isEndpoint(toBocomeRail))
//			return true;
//		else
//			return false;
			
	}

	

	private Action mineAndMake(final MineFindResult chosenJob, final LinkedList<Tuple<Block, BlockMatState>> blockChangables) {
		return new Action() {
			
			@Override
			public long getWaitingTime() {
				return mineOrPutWaitingTime;
			}
			
			@Override
			public Location getLocation() {
				return chosenJob.block.getLocation().add(.5, 0, .5); // you an stand in the rail
			}
			
			@Override
			public ActionType getActionType() {
				return ActionType.JOB;
			}
			
			@Override
			public Action doAction() {
				if (blockChangables.isEmpty())
					return getNextJobAction(chosenJob, false);
				Tuple<Block, BlockMatState> firstChangable = blockChangables.removeFirst();
				
				Block from = firstChangable.fst;
				BlockMatState to = firstChangable.snd;
				
				if (to.mat == Material.CARPET)
					Debug.out("changing "+from.getX()+","+from.getY()+","+from.getZ()+" to carpetje");
				
				if (BlockUtils.isRailType(from.getType()))
				{
					Debug.out("from is rail type!");
					return mineAndMake(chosenJob, blockChangables);
				}
				// TODO what if it needs to replace rail with a block?!
				
				if (!WalkingGroundUtils.isValidWalkingAirBlockOrGate(from))
				{
					mineBlock(from);
				}
				boolean hasBlockToPut = putBlock(from, to);
				
				if (!hasBlockToPut)
				{
					Debug.out("didn't put block!");
					return getNextJobAction(chosenJob, true); 
				}
				if (isFinishedMining()) // TODO: check if inventory is full??
				{
					Debug.out("finished mining!");
					return getToMineBuildingNavigatorAction(chosenJob.block);
				}
				// otherwise: 
				return mineAndMake(chosenJob, blockChangables);
			}
			
			@Override
			public Action cantNavigate() {
				return doGetReadyToWork; // teleportToMineAndGoHome();
			}
		};
	}

	
	
	
	/**
	 * @param block
	 * @return whether it could actually finish mining the block and water around it..
	 */
	private boolean mineBlock(Block block) {
		// TODO: add waiting time ?
		this.collectRemaining--;
		
		Material nextType = block.getType();
		if (nextType == Material.WATER || nextType == Material.LAVA || nextType == Material.STATIONARY_WATER || nextType == Material.STATIONARY_LAVA)
		{
			boolean hasBlockToPut = putBlock(block, new BlockMatState(Material.COBBLESTONE, -1));
			if (!hasBlockToPut)
				return false;
			block = block.getWorld().getBlockAt(block.getLocation()); // reset the block to the cobble block
		}
		
		Collection<ItemStack> drops = block.getDrops(toolHeld);
		block.setType(Material.AIR);
		HashMap<Integer, ItemStack> leftOvers = InventoryTraitUtils.addItem(inventory, drops.toArray(new ItemStack[0]));
		for (ItemStack drop : drops)
			homeBuilding.city.statistics.produce(homeBuilding, drop.getType(), drop.getAmount());
		
		for (ItemStack leftOver : leftOvers.values())
			block.getWorld().dropItem(Miner.this.getNPC().getEntity().getLocation(), leftOver); // TODO: drop leftover items that he couldnt store on ground?
		
		// put cobblestone if block next to this block is liquid!
		for (BlockFace face : BlockUtils.cubeFaces)
		{
			Block next = block.getRelative(face);
			nextType = next.getType();
			if (nextType == Material.WATER || nextType == Material.LAVA || nextType == Material.STATIONARY_WATER || nextType == Material.STATIONARY_LAVA)
			{
				boolean hasBlockToPut = putBlock(next, new BlockMatState(Material.COBBLESTONE, -1));
				if (!hasBlockToPut)
					return false;
			}
		}
		return true;
	}

	/**
	 * @param block
	 * @param to
	 * @return if it could put the block..
	 */
	@SuppressWarnings("deprecation")
	private boolean putBlock(Block block, BlockMatState to) {
		if (to.mat == Material.AIR)
			return true;
			
//		Debug.out("putting "+to.mat);
//		boolean isRail = BlockUtils.isRailType(to.mat); 
//		if (!isRail)
//		{
			ItemStack toTakeFromInv;
			switch(to.mat) {
			case COBBLE_WALL:
				toTakeFromInv = new ItemStack(Material.COBBLESTONE, 1);
				break;
			case SIGN_POST:
				toTakeFromInv = new ItemStack(Material.WOOD, 2);
				break;
			case COBBLESTONE:
				toTakeFromInv = new ItemStack(Material.COBBLESTONE, 1);
				break;
			case WOOD:
				toTakeFromInv = new ItemStack(Material.WOOD, 1);
				break;
			case FENCE:
				toTakeFromInv = new ItemStack(Material.WOOD, 1);
				break;
			case TORCH:
				toTakeFromInv = new ItemStack(Material.WOOD, 1);
				break;
			case CARPET:
				toTakeFromInv = new ItemStack(Material.CARPET, 1);
				break;
//			case AIR:
//				toTakeFromInv = new ItemStack(Material.AIR, 0);
//				break;
			default:
				Debug.warn("unknown putBlock! : "+to.mat);
				toTakeFromInv = new ItemStack(Material.AIR, 0);
			}
			if (!InventoryTraitUtils.containsAtLeast(inventory, toTakeFromInv.getType(), toTakeFromInv.getAmount()))
			{
				Debug.out("inv didnt have "+toTakeFromInv.getAmount()+" "+toTakeFromInv.getType()+"!");
				return false;
			}
			Tuple<List<ItemStack>, List<ItemStack>> gottenNunremoved = InventoryTraitUtils.get(inventory, toTakeFromInv.getType(), toTakeFromInv.getAmount());
			homeBuilding.city.statistics.consume(homeBuilding, toTakeFromInv.getType(), toTakeFromInv.getAmount());

			to.setBlock(block);
			if (to.mat == Material.WOOD)
			{
				block.setData((byte) BlockUtils.logToWood(gottenNunremoved.fst.get(0)).getDurability());
			}
			if (to.mat == Material.CARPET)
			{
				block.setData((byte) gottenNunremoved.fst.get(0).getDurability());
			}
			
			return true;
//		}
//		else {
//		
//			to.setBlock(block);
//			MetaDataUtils.setFake(block, true);
//			
//			return true;
//		}
	}
	
	
	
	
	
	
	

	
	@Override
	protected void cancelCurrentJob() {
	}



}
