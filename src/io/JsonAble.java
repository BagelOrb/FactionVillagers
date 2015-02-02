package io;

import java.lang.reflect.InvocationTargetException;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public interface JsonAble<T extends JsonAble<T>> {

	public abstract JsonObjectBuilder toJsonObjectBuilder();

	public abstract T fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException;

}