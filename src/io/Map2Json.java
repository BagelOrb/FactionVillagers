package io;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class Map2Json {

	
//	public static <K,V extends JsonAble<V>> JsonObjectBuilder simpleMapToJsonObjectBuilder(Map<K,V> map) {
//		JsonObjectBuilder ret = Json.createObjectBuilder();
//		for (Entry<K, V> entry : map.entrySet())
//			ret.add(entry.getKey()+"", entry.getValue().toJsonObjectBuilder());
//		return ret;
//	}
	public static <K extends JsonAble<K>,V extends JsonAble<V>> JsonArrayBuilder simpleMapToJsonArrayBuilder(Map<K,V> map) {
		JsonArrayBuilder ret = Json.createArrayBuilder();
		for (Entry<K, V> entry : map.entrySet())
			ret		.add(Json.createObjectBuilder().add("key", entry.getKey().toJsonObjectBuilder()))
					.add(Json.createObjectBuilder().add("value", entry.getValue().toJsonObjectBuilder()));
		return ret;
	}
//	public static <K,V extends JsonAble<V>> Map<K,V> simpleMapToJsonObjectBuilder(JsonObject o, Class<? extends Map<K,V>> clazz) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
//		Map<K,V> ret = Reflect.newClassInstance(clazz);
//		
//		return ret;
//	}
	public static <K extends JsonAble<K>,V extends JsonAble<V>> Map<K,V> simpleJsonMapArrayToMap(JsonArray o, K exampleK, V exampleV) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Map<K,V> ret = new HashMap<K,V>();
		for (int i = 0; i<o.size(); i++)
		{
			JsonObject kv = o.getJsonObject(i);
			ret.put(exampleK.fromJsonObject(kv.getJsonObject("key")), exampleV.fromJsonObject(kv.getJsonObject("val")));
		}
		return ret;
	}
}
