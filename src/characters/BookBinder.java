package characters;



public class BookBinder extends HomeWorker {
		
	public BookBinder() {
		super(traitName); // homeBuilding2.getFullyQualifiedId()+
	}
	public CharacterType getCharacterType() { return CharacterType.BOOK_BINDER; }
	
	public static final String traitName = "bookBinder";
	
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
