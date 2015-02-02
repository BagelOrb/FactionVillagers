package characters;

public class LibrarianHelper extends FoodSupplier {

	public LibrarianHelper() {
		super(traitName);
	}

	public static final String traitName = "librarianHelper";
	
	@Override
	String getTraitName()
	{
		return traitName;
	}

	@Override
	public CharacterType getCharacterType() {
		return CharacterType.LIBRARIAN_HELPER;
	}

}
