package characters;

public class MineWarden extends FoodSupplier {

	public MineWarden() {
		super(traitName);
	}

	public static final String traitName = "mineWarden";
	
	@Override
	String getTraitName()
	{
		return traitName;
	}

	@Override
	public CharacterType getCharacterType() {
		return CharacterType.MINE_WARDEN;
	}

}
