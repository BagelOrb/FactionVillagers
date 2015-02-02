package characters;


public class Baker extends HomeWorker {
		
	public Baker() {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	public CharacterType getCharacterType() { return CharacterType.BAKER; }
	
	public static final String traitName = "baker";
	
	@Override
	String getTraitName() {
		return traitName;
	}
	
	

	@Override
	Action doJob() {
		consume();
		produce();
		return getJobToHomeNavigatorAction();
	}
	@Override
	protected void cancelCurrentJob() {
	}


}
