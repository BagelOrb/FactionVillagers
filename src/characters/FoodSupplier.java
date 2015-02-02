package characters;

import main.Debug;

import org.bukkit.Location;


public abstract class FoodSupplier extends ChestCharacter {
		
	
	public FoodSupplier(String traitName) {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	
	@Override
	abstract String getTraitName();
	
	
	@Override
	Location findJob() {
		Location nextLocation = getHomeLocation(); // homeBuilding.homeLocationRequirement
		this.homeToJobNavigator =  new Navigator();
		return nextLocation;
	}
	

	@Override
	Action doJob() {
		consume();
//		produce();
		return new Action(){

			@Override
			public Location getLocation() {
				return getHomeLocation();
			}

			@Override
			public Action doAction() {
				return doHomeMoveThrough;
			}

			@Override
			public long getWaitingTime() {
				long fullTime = getHomeLocation().getWorld().getFullTime();
				long periodNow =  fullTime / getJobWaitingTime();
				long timeInNextPeriod = (long) (random.nextDouble()*.8 *getJobWaitingTime());
				return (periodNow + 1)*getJobWaitingTime() + timeInNextPeriod - fullTime; 
			}

			@Override
			public Action cantNavigate() {
				Debug.out("can't navigate from home to home?!");
				return doJob();
			}

			@Override
			public ActionType getActionType() {
				return ActionType.HOME;
			}};
	}
	@Override
	protected void cancelCurrentJob() {
	}

}
