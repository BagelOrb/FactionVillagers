package buildings;

import io.JsonAble;

import java.lang.reflect.InvocationTargetException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang.WordUtils;

import characters.CharacterType;

public enum BuildingType implements JsonAble<BuildingType>{
	STORAGE_ROOM 		(StorageRoom.class,			CharacterType.STORAGE_KEEPER),
	WOODCUTTER_HUT 		(WoodcutterHut.class, 		CharacterType.WOODCUTTER),
	WHEAT_FARM 			(WheatFarm.class, 			CharacterType.WHEAT_FARMER),
	BAKERY 				(Bakery.class, 				CharacterType.BAKER),
	WEAVERY				(Weavery.class, 			CharacterType.WEAVER),
	BLACKSMITH 			(Blacksmith.class,			CharacterType.BLACKSMITH),
	LIBRARY 			(Library.class,		 		CharacterType.LIBRARIAN),
	COW_FARM 			(CowFarm.class,				CharacterType.COW_FARMER),
	CHICKEN_FARM 		(ChickenFarm.class,			CharacterType.CHICKEN_FARMER),
	SHEEP_FARM 			(SheepFarm.class, 			CharacterType.SHEEP_HERDER),
	MINERS_LODGE		(MinersLodge.class,			CharacterType.MINER), 
	MINE				(Mine.class, 				CharacterType.MINE_WARDEN), 
	SAWMILL				(Sawmill.class, 			CharacterType.SAWMILL_WORKER), 
	SUGAR_CANE_FARM		(SugarCaneFarm.class, 		CharacterType.SUGAR_CANE_FARMER),
	BOOK_BINDERS_OFFICE (BookBindersOffice.class, 	CharacterType.BOOK_BINDER), 
	SMELTERY			(Smeltery.class,			CharacterType.SMELTER),
	TOWN_HALL			(TownHall.class,			CharacterType.MAYOR);

	public Class<? extends Building> buildingClass;
	public CharacterType characterType;
	
	BuildingType(Class<? extends Building> clazz, CharacterType charType){
		buildingClass = clazz;
		characterType = charType;
	}
	
	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		return Json.createObjectBuilder().add("val", this.name());
	}

	@Override
	public BuildingType fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return valueOf(o.getString("val"));
	}

	public String prettyPrint() {
		String str = this.name();
		str = str.replace('_', ' ').toLowerCase();
		str = WordUtils.capitalize(str);
		return str;
	}
}
