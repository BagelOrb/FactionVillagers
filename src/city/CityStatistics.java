package city;

import generics.Tuple;
import interaction.StatisticsBookListener;
import io.JsonAble;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.bukkit.Material;

import utils.MatUtils;
import utils.SetUtils;
import buildings.Building;

import com.massivecraft.massivecore.util.Txt;

public class CityStatistics implements JsonAble<CityStatistics>{

//	private final City city;
	public CityStatistics(City city2) {
//		city = city2;
	}
	
	HashMap<Material, ProdCons> productionConsumptionLastDay 	= new HashMap<Material, ProdCons>();
	HashMap<Material, ProdCons> productionConsumption		 	= new HashMap<Material, ProdCons>();

	private class ProdCons extends Tuple<Integer, Integer> {
		public ProdCons(Integer ll, Integer rr) {
			super(ll, rr);
		}
		private void addToProd(int add) {
			this.fst += add;
		}
		private void addToCons(int add) {
			this.snd += add;
		}
		private int getProd() { return fst; }
		private int getCons() { return snd; }
	}
	
	public void produce(Building building, Material mat, int amount) {
		addToProd(productionConsumption, mat, amount);
	}
	

	public void consume(Building building, Material mat, int amount) {
		addToCons(productionConsumption, mat, amount);
	}
	

	private void addToProd(HashMap<Material, ProdCons> map,
			Material mat, int amount) {
		
		ProdCons value = map.get(mat);
		if (value == null)
			map.put(mat, new ProdCons(amount, 0));
		else
			value.addToProd(amount);
	}
	
	private void addToCons(
			HashMap<Material, ProdCons> map,
			Material mat, int amount) {
		
		ProdCons value = map.get(mat);
		if (value == null)
			map.put(mat, new ProdCons(0, amount));
		else
			value.addToCons(amount);
	}

	public void processDay() {
		computeStatistics();
		
		productionConsumptionLastDay = productionConsumption;
		productionConsumption 	= new HashMap<Material, ProdCons>();
	}
	
	// compute more statistics from known statistics
	public void computeStatistics() {
	}
	
	

	

	public static final Comparator<? super Entry<Material, ProdCons>> compareByKeyAlphabetically = new Comparator<Entry<Material, ProdCons>>(){
		@Override
		public int compare(Entry<Material, ProdCons> o1, Entry<Material, ProdCons> o2) {
			return o1.getKey().toString().compareTo(o2.getKey().toString());
		}};

	public String toStringNetto(String resetColor) {
		return toStringNetto(productionConsumption,  "<gold>Production today \n" +   StatisticsBookListener.delimiter, resetColor);
	}
	public String toStringNettoLastDay(String resetColor) {
		return toStringNetto(productionConsumptionLastDay,   "<gold>Production yesterday\n" +  StatisticsBookListener.delimiter, resetColor );
	}
	public String toStringNetto(HashMap<Material, ProdCons> prodCons, String prefix, String resetColor) {
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		
		String ret = prefix;
		for (Entry<Material, ProdCons> materialEntry : SetUtils.asSortedList(prodCons.entrySet(), compareByKeyAlphabetically))
		{
			Material mat = materialEntry.getKey();
			ret +=  resetColor+MatUtils.prettyPrint(mat)+": ";
			ProdCons val = materialEntry.getValue();
			int netto = val.getProd() - val.getCons();
			if (netto <0)
				ret += "<bad>";
			else if (netto >0)
				ret += "<good>";
			else 
				ret += "<good>";
			String computation = " <silver>("+val.getProd()+" - "+val.getCons()+")";
			ret +=formatter.format(netto)+computation+resetColor+"\n";
		}
		ret.subSequence(0, ret.length()-2);
		return Txt.parse(ret);
	}
	

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		return Json.createObjectBuilder()
			.add("productionConsumptionLastDay", toJsonArrayBuilder(productionConsumptionLastDay))
			.add("productionConsumption", toJsonArrayBuilder(productionConsumption));
	}
	private JsonArrayBuilder toJsonArrayBuilder(HashMap<Material, ProdCons> productionConsumptionLastDay2) {
		JsonArrayBuilder materialArrayBuilder = Json.createArrayBuilder();
			
		for (Entry<Material, ProdCons> materialEntry : productionConsumptionLastDay2.entrySet())
		{
			JsonObjectBuilder materialEntryJson = Json.createObjectBuilder()
					.add("key", materialEntry.getKey().toString())
					.add("prod", materialEntry.getValue().getProd())
					.add("cons", materialEntry.getValue().getCons())
					;
			materialArrayBuilder.add(materialEntryJson);
		}
		return materialArrayBuilder;
	}
	@Override
	public CityStatistics fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		productionConsumption = hashMapFromJsonArray(o.getJsonArray("productionConsumption"));
		productionConsumptionLastDay = hashMapFromJsonArray(o.getJsonArray("productionConsumptionLastDay"));
		return this;
	}
	private HashMap<Material, ProdCons> hashMapFromJsonArray(JsonArray materialMapArray) {
		HashMap<Material, ProdCons> ret = new HashMap<Material, ProdCons>();
		for (JsonValue matMapVal : materialMapArray)
		{
			JsonObject matMapObject = (JsonObject) matMapVal;
			Material matKey = Material.valueOf(matMapObject.getString("key"));
			Integer prod = matMapObject.getInt("prod");
			Integer cons = matMapObject.getInt("cons");
			ret.put(matKey, new ProdCons(prod, cons));
		}
		return ret;
	}
	
	
	
	
}
