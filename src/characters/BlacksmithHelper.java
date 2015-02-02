package characters;

public class BlacksmithHelper extends FoodSupplier {

	public BlacksmithHelper() {
		super(traitName);
	}

	public static final String traitName = "blacksmithHelper";
	
	@Override
	String getTraitName()
	{
		return traitName;
	}

	@Override
	public CharacterType getCharacterType() {
		return CharacterType.BLACKSMITH_HELPER;
	}

}
