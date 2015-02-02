package buildings;

import java.util.LinkedList;
import java.util.List;

import main.Debug;
import main.FactionVillagers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;


public class MetaDataUtils {

	
	@SuppressWarnings("serial")
	public static class MetadataException extends RuntimeException {

		public MetadataException(String string) {
			super(string);
		}

	}
	public static final String belongsToBuildingString = "belongsToBuilding"; // TODO: make private?
	
	
	public static void setBelongingTo(Metadatable entity, Building building) {
		String stringId = building2stringId(building);
		if (belongsToBuilding(entity))
		{
			List<MetadataValue> metaDataValues = entity.getMetadata(belongsToBuildingString);
			if (metaDataValues.size()==0)
			{
				setBelongingToMetadata(entity, belongsToBuildingString, stringId);
				return;
			}
			String metaData = metaDataValues.get(0).asString();
			for (String id : metaData.split(","))
				if (id.equalsIgnoreCase(stringId)) return;
			setBelongingToMetadata(entity, belongsToBuildingString, metaData+","+stringId);
		}
		else
		{
			setBelongingToMetadata(entity, belongsToBuildingString, stringId);
		}
	}
	
	
	private static void setBelongingToMetadata(Metadatable entity, String var, String ids) {
		removeAllBelongingTo(entity);
		entity.setMetadata(var, new FixedMetadataValue(FactionVillagers.getCurrentPlugin(), ids));
	}


	public static String building2stringId(Building building) {
		return building.getFullyQualifiedId();
	}
	
	public static List<Building> getBuildings(Metadatable entity) {
		if (!belongsToBuilding(entity)) return new LinkedList<Building>();
		List<MetadataValue> metaDataValues = entity.getMetadata(belongsToBuildingString);
		if (metaDataValues.size()==0) return new LinkedList<Building>();
		String metaDataValue = metaDataValues.get(0).asString();
		if (metaDataValue.equals(""))
		{
			removeAllBelongingTo(entity);
			return new LinkedList<Building>();
		}
		String[] ids = metaDataValue.split(",");
		LinkedList<String> validIds = new LinkedList<String>();
		
		LinkedList<Building> buildings = new LinkedList<Building>(); 
		for (String bId : ids )
		{
//            Debug.out("checking: \""+bId+"\"");
			Building building = Building.parseBuildingId(bId);
			if (building!=null && !validIds.contains(bId))
			{
				validIds.add(bId);
				buildings.add(building);
			}
			else
			{
				Debug.warn("Could not find building: \""+bId+"\"... Removing meta-data...");
			}
		}
		removeAllBelongingTo(entity);
		if (validIds.size()>0)
			setBelongingToMetadata(entity, belongsToBuildingString, StringUtils.join(validIds,","));
		return buildings;
	}
	
	public static void removeAllBelongingTo(Metadatable entity) {
		entity.removeMetadata(belongsToBuildingString, FactionVillagers.getCurrentPlugin());
	}
	public static boolean belongsToBuilding(Metadatable entity) {
		return entity.hasMetadata(belongsToBuildingString);
	}
	public static void removeBelongingTo(Metadatable entity, Building building) {
		if (!entity.hasMetadata(belongsToBuildingString)) return;
		
		String buildingId = building2stringId(building);
		
		String metaData = entity.getMetadata(belongsToBuildingString).get(0).asString();
		LinkedList<String> leftOvers = new LinkedList<String>();
		for (String id : metaData.split(","))
			if (!id.equalsIgnoreCase(buildingId) && !leftOvers.contains(buildingId))
				leftOvers.add(id);
		
		removeAllBelongingTo(entity);
		if (leftOvers.size()>0)
			setBelongingToMetadata(entity, belongsToBuildingString, StringUtils.join(leftOvers,","));
	}
	
//	public static void main(String[] args) {
//		LinkedList<String> l = new LinkedList<String>();
//		String qw1 = "qw";
//		l.add(qw1);
//		l.add("qwe");
//		String qw = "eqw".substring(1);
//        Debug.out(l.contains(qw));
//        Debug.out(qw==qw1);
//		l.remove(qw);
//        Debug.out(l);
//	}
	
	
	
	
	
	
	public static void setBelongingTo(Entity entity, Building building) {
		String stringId = building2stringId(building);
		if (belongsToBuilding(entity))
		{
			List<MetadataValue> metaDataValues = entity.getMetadata(belongsToBuildingString);
			if (metaDataValues.size()==0)
			{
				setMetadata(entity, belongsToBuildingString, stringId);
				return;
			}
			String metaData = metaDataValues.get(0).asString();
			for (String id : metaData.split(","))
				if (id.equalsIgnoreCase(stringId)) return;
			setMetadata(entity, belongsToBuildingString, metaData+","+stringId);
		}
		else
		{
			setMetadata(entity, belongsToBuildingString, stringId);
		}
	}
	
	
	private static void setMetadata(Entity entity, String var, String ids) {
		removeAllBelongingTo(entity);
		entity.setMetadata(var, new FixedMetadataValue(FactionVillagers.getCurrentPlugin(), ids));
	}


	public static List<Building> getBuildings(Entity entity) {
		if (!belongsToBuilding(entity)) return new LinkedList<Building>();
		List<MetadataValue> metaDataValues = entity.getMetadata(belongsToBuildingString);
		if (metaDataValues.size()==0) return new LinkedList<Building>();
		String metaDataValue = metaDataValues.get(0).asString();
		if (metaDataValue.equals(""))
		{
			removeAllBelongingTo(entity);
			return new LinkedList<Building>();
		}
		String[] ids = metaDataValue.split(",");
		LinkedList<String> validIds = new LinkedList<String>();
		
		LinkedList<Building> buildings = new LinkedList<Building>(); 
		for (String bId : ids )
		{
//            Debug.out("checking: \""+bId+"\"");
			Building building = Building.parseBuildingId(bId);
			if (building!=null && !validIds.contains(bId))
			{
				validIds.add(bId);
				buildings.add(building);
			}
			else
			{
				Debug.warn("Could not find building: \""+bId+"\"... Removing meta-data... Adding null to list of Buildings");
				buildings.add(null); // necessary when checking for bugs : villager should see whether is has buggy meta data
			}
		}
		removeAllBelongingTo(entity);
		if (validIds.size()>0)
			setMetadata(entity, belongsToBuildingString, StringUtils.join(validIds,","));
		return buildings;
	}
	
	public static void removeAllBelongingTo(Entity entity) {
		entity.removeMetadata(belongsToBuildingString, FactionVillagers.getCurrentPlugin());
	}
	public static boolean belongsToBuilding(Entity entity) {
		return entity.hasMetadata(belongsToBuildingString);
	}
	public static void removeBelongingTo(Entity entity, Building building) {
		if (!entity.hasMetadata(belongsToBuildingString)) return;
		
		String buildingId = building2stringId(building);
		
		String metaData = entity.getMetadata(belongsToBuildingString).get(0).asString();
		LinkedList<String> leftOvers = new LinkedList<String>();
		for (String id : metaData.split(","))
			if (!id.equalsIgnoreCase(buildingId) && !leftOvers.contains(buildingId))
				leftOvers.add(id);
		
		removeAllBelongingTo(entity);
		if (leftOvers.size()>0)
			setMetadata(entity, belongsToBuildingString, StringUtils.join(leftOvers,","));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private static final String isFake = "isFake"; 
//
//	public static void setFake(Block block, boolean fake) {
//		if (fake)
//			block.setMetadata(isFake, new FixedMetadataValue(FactionVillagers.getCurrentPlugin(), true));
//		else
//			block.removeMetadata(isFake, FactionVillagers.getCurrentPlugin());
//	}
//	
//	public static boolean isFake(Block block) {
//		if (block.hasMetadata(isFake))
//		{
//			return block.getMetadata(isFake).get(0).asBoolean();
//		}
//		return false;
//	}
	
}
