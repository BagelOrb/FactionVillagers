package io;

import generics.Reflect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class JsonUtils {


	public static void writeToFile(JsonObjectBuilder builder, File file) throws IOException {
		Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory fact = Json.createWriterFactory(properties);
		JsonWriter writer = fact.createWriter(new FileWriter(file));
		writer.write(builder.build());
		writer.close();
			
	}

	public static <T extends JsonAble<T>> T readFromFile(File dir, Class<T> clazz) throws IllegalArgumentException, SecurityException, FileNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		JsonReader reader = Json.createReader(new FileInputStream(dir));
		T object = Reflect.newClassInstance(clazz).fromJsonObject( // 
			(JsonObject) reader
				.read() );
		reader.close();
		return object; 
	}
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		final JsonObjectBuilder q = Json.createObjectBuilder().add("q" , 1).add("w" , "w").add("e", 1.5).add("recur", Json.createObjectBuilder().add("a", 1).add("s", 1.2).addNull("wo0ot?"));
		try {
			writeToFile(new JsonAble(){

				@Override
				public JsonAble fromJsonObject(JsonObject o)
						throws IllegalArgumentException, SecurityException,
						InstantiationException, IllegalAccessException,
						InvocationTargetException, NoSuchMethodException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public JsonObjectBuilder toJsonObjectBuilder() {
					// TODO Auto-generated method stub
					return q;
				}}.toJsonObjectBuilder(), new File("BStry.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
