package characters;

import main.Debug;
import net.citizensnpcs.api.util.DataKey;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

import buildings.TownHall;


public class Unemployed extends FoodSupplier {



	public Unemployed() {
		super(traitName);
	}

	public static final String traitName = "unemployed";
	
	
	
	public void save(DataKey key) {
		super.save(key);
	}
	
	public void load(DataKey key) {
		super.load(key);
		
	}
	
	@Override
	String getTraitName()
	{
		return traitName;
	}

	@Override
	public void onSpawn() {
		super.onSpawn();
		
		Entity ent = npc.getEntity();
		if(ent instanceof Villager)
		{
			Villager villager = (Villager) ent;
			villager.setProfession(CharacterType.UNEMPLOYED.profession);
		}
	}

	
	@Override
	public CharacterType getCharacterType() {
		return CharacterType.UNEMPLOYED;
	}
	
	
	@Override
	public boolean recheck() {
		boolean valid = super.recheck();
		if (!valid)
			return false;
		
		boolean needsToBeDestroyed = checkPoolPlace();
		if (needsToBeDestroyed )
		{
			Debug.out("Invalid Unemployed! Destroying...");
			getNPC().destroy();
			return false;
		}
		
		return true;
			
	}
	
	private boolean checkPoolPlace() {
		TownHall townHall = (TownHall) homeBuilding;
		if (!townHall .getPool().contains(this))
		{
			Debug.out("Unemployed not in the town hall pool!");
			return true;
		}
		
		
		return false;
	}
	
	
	@Override
	public void destroy() {
		super.destroy();
		((TownHall) homeBuilding).pool.remove((Integer) getNPC().getId());
	}

	


}
