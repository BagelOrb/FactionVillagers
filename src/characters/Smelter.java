package characters;



public class Smelter extends HomeWorker {
		
	public Smelter() {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	public CharacterType getCharacterType() { return CharacterType.SMELTER; }
	
	public static final String traitName = "smelter";
	
	@Override
	String getTraitName() {
		return traitName;
	}
	
	

	@Override
	Action doJob() {
//		Material.iron_in
		consume();
		produce();
		return getJobToHomeNavigatorAction();
	}
	@Override
	protected void cancelCurrentJob() {
	}

}
