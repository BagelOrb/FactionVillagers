package characters;

public class ContractorHelper extends FoodSupplier {

	public ContractorHelper() {
		super(traitName);
	}

	public static final String traitName = "contractorHelper";
	
	@Override
	String getTraitName()
	{
		return traitName;
	}

	@Override
	public CharacterType getCharacterType() {
		return CharacterType.CONTRACTOR_HELPER;
	}

}
