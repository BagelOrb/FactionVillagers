package utils;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Entity;

public class EntityUtils {
	public static Entity getClosestEntity(Entity startEntity, EntityChecker entityChecker, double radius)
	{
		Entity closestEntity = null;
		double lowestDistanceSquared = Double.MAX_VALUE;
		List<Entity> nearbyEntities = startEntity.getNearbyEntities(radius * 2, radius * 2, radius * 2); 
		
		for(Entity entity : nearbyEntities)
		{
			if(entityChecker.isValid(entity))
			{
				double distanceSquared = startEntity.getLocation().distanceSquared(entity.getLocation());
				if(distanceSquared < lowestDistanceSquared)
				{
					lowestDistanceSquared = distanceSquared;
					closestEntity = entity;
				}
			}
			
			
		}
		
		return closestEntity;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> List<T> getCloseEntities(Entity startEntity, EntityChecker entityChecker, double radius, Class<T> clazz)
	{
		List<Entity> nearbyEntities = startEntity.getNearbyEntities(radius * 2, radius * 2, radius * 2); 
		List<T> nearbyCheckedEntities = new LinkedList<T>();
		for(Entity entity : nearbyEntities)
		{
			if(clazz.isInstance(entity) && entityChecker.isValid(entity))
			{
				nearbyCheckedEntities.add((T) entity);
			}
		}
	
		return nearbyCheckedEntities;
	}
}
