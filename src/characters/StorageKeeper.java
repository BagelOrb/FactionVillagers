package characters;

public class StorageKeeper extends FoodSupplier {

	public StorageKeeper() {
		super(traitName);
	}

	public static final String traitName = "storageKeeper";
	
	@Override
	String getTraitName()
	{
		return traitName;
	}

	@Override
	public CharacterType getCharacterType() {
		return CharacterType.STORAGE_KEEPER;
	}

}
