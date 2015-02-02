package characters;

import org.bukkit.Location;
import org.bukkit.block.Block;

import utils.BlockChecker;
import utils.WalkingGroundFinder;

public class Roamer extends ChestCharacter {

	public static final String traitName = "roamer";
	@Override String getTraitName() {
		return traitName;
	}
	public Roamer() {
		super(traitName);
	}
	
	
	public CharacterType getCharacterType() { return CharacterType.MINE_WARDEN; }
	
//	@Override
//	public ItemStack[] getItemsNeededToConsume() {
//		return new ItemStack[0];
//	}
	@Override
	public void destroy(){}

	@Override
	public Location findJob() {
		this.homeToJobNavigator =  new Navigator();
		BlockChecker blockChecker = new BlockChecker() {
			@Override
			public boolean isValid(Block block) {
				return random.nextDouble() < .1;
			}
		};
		WalkingGroundFinder.WalkingGroundFinderResult result = WalkingGroundFinder.closestBlockOnWalkableGround(
				getHomeLocation(), 100, blockChecker, playersToShowSearchSpace, WalkingGroundFinder.walkEverywhere);
		Location nextLoc;
		if (result==null)
			nextLoc = getHomeLocation();
		else
			nextLoc = result.block.getLocation().add(.5, 1, .5);
		return nextLoc;
//		if (homeBuilding instanceof Warehouse)
//		{
//			ArrayList<Chest> chestList = new ArrayList<Chest>();
//			chestList.addAll(((Warehouse) homeBuilding).chests);
//			return chestList.get(random.nextInt(chestList.size())).getLocation().add(.5, 0, .5) ;
//		}
//		else 
//			return this.homeLocation.add(random.nextDouble()*6-3, random.nextDouble()*2-1, random.nextDouble()*6-3) ;
	}


	@Override
	Action doJob() {
		produce();
		return doGetReadyToWork; //  getJobToHomeNavigatorAction();
	}
	@Override
	protected void cancelCurrentJob() {
	}



}
