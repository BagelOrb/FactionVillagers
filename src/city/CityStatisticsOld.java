package city;

import interaction.StatisticsBookListener;
import io.JsonAble;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
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
import buildings.BuildingType;

import com.massivecraft.massivecore.util.Txt;

public class CityStatisticsOld implements JsonAble<CityStatisticsOld>{

//	private final City city;
	public CityStatisticsOld(City city2) {
//		city = city2;
	}
	
	HashMap<BuildingType, HashMap<Material, Integer>> consumptionPerBuildingLastDay = new HashMap<BuildingType, HashMap<Material, Integer>>();
	HashMap<BuildingType, HashMap<Material, Integer>> productionPerBuildingLastDay = new HashMap<BuildingType, HashMap<Material, Integer>>();
	HashMap<BuildingType, HashMap<Material, Integer>> consumptionPerBuilding = new HashMap<BuildingType, HashMap<Material, Integer>>();
	HashMap<BuildingType, HashMap<Material, Integer>> productionPerBuilding = new HashMap<BuildingType, HashMap<Material, Integer>>();
	public Statistics statistics;
	
	
	static <N extends Number> void addTo(HashMap<BuildingType, HashMap<Material, N>>  map, BuildingType type, Material mat, N amount, Class<N> clazz) {
		HashMap<Material, N> buildingConsumption = map.get(type);
		if (buildingConsumption == null)
		{
			buildingConsumption = new HashMap<Material, N>();
			buildingConsumption.put(mat, amount);
			map.put(type, buildingConsumption);
			return;
		}
		addTo(buildingConsumption, mat, amount, clazz);
	}
	static <N extends Number> void addTo(HashMap<Material, N>  map, Material mat, N amount, Class<N> clazz) {
		N matConsumption = map.get(mat);
		if (matConsumption == null)
		{
			map.put(mat, amount);
			return;
		}
		map.put(mat, genericAdd(matConsumption, amount, clazz));
	}
	@SuppressWarnings("unchecked")
	static <N> N genericAdd(N matConsumption, N amount, Class<N> clazz) {
		if (clazz.equals(Integer.class))
			return (N) (Integer) (((Integer) matConsumption) +((Integer) amount)); 
		if (clazz.equals(Double.class))
			return (N) (Double) (((Double) matConsumption) +((Double) amount)); 
		return null;
	}
	
	public void consume(BuildingType type, Material mat, int amount) {
		addTo(consumptionPerBuilding, type, mat, amount, Integer.class);
	}
	
	public void produce(BuildingType type, Material mat, int amount) {
		addTo(productionPerBuilding, type, mat, amount, Integer.class);
	}
	public void processDay() {
		computeStatistics(24000);
		
		consumptionPerBuildingLastDay = consumptionPerBuilding;
		productionPerBuildingLastDay = productionPerBuilding;
		consumptionPerBuilding = new HashMap<BuildingType, HashMap<Material, Integer>>();
		productionPerBuilding = new HashMap<BuildingType, HashMap<Material, Integer>>();
	}
	
	private void computeStatistics(int ticks) {
		double hours = ticks/1000.;
		statistics = new Statistics(this);
//		statistics.computeNettoPerBuilding(hours);
		statistics.computeNetto(hours);
	}
	
	public static class Statistics {
		HashMap<BuildingType, HashMap<Material, Double>> nettoPerBuildingLastDay;
		HashMap<BuildingType, HashMap<Material, Double>> nettoPerBuilding;
		HashMap<Material, Double> nettoLastDay;
		HashMap<Material, Double> netto;
//		HashMap<Material, Double> nettoLastDay;
//		HashMap<Material, Double> netto;
//		HashMap<Material, Double> nettoLastDay;
//		HashMap<Material, Double> netto;
		private final CityStatisticsOld cityStatistics;
		
		public Statistics(CityStatisticsOld cityStatistics) {
			this.cityStatistics = cityStatistics;
		}

		public void compute() {
			computeNettoPerBuilding(1);  // (24000-MCity.defaultWorld.getTime())/1000.);
//			computeNettoPerBuildingLastDay(1);
			computeNetto(1);
			computeNettoLastDay(1);
		}

		void computeNettoPerBuilding(double hours) {
			nettoPerBuilding = new HashMap<BuildingType, HashMap<Material, Double>>();
			computeNettoPerBuilding(nettoPerBuilding, hours);
		}
		void computeNettoPerBuildingLastDay(double hours) {
			computeNettoPerBuilding(nettoPerBuildingLastDay, hours);
		}
		void computeNettoPerBuilding(HashMap<BuildingType, HashMap<Material, Double>> nettoPerBuilding2, double hours) {
			for (Entry<BuildingType, HashMap<Material, Integer>> buildingEntry : cityStatistics.consumptionPerBuilding.entrySet()) {
				HashMap<Material, Double> nettoForThisBuilding = new HashMap<Material, Double>();
				nettoPerBuilding2.put(buildingEntry.getKey(), nettoForThisBuilding );
				for (Entry<Material, Integer> materialEntry : buildingEntry.getValue().entrySet())
					addTo(nettoForThisBuilding, materialEntry.getKey(), -1.* materialEntry.getValue(), Double.class);
//					addTo(nettoPerBuilding, buildingEntry.getKey(), materialEntry.getKey(), -1.* materialEntry.getValue(), Double.class);
			}
			for (Entry<BuildingType, HashMap<Material, Integer>> buildingEntry : cityStatistics.productionPerBuilding.entrySet())
			{
				
				HashMap<Material, Double> nettoForThisBuilding = nettoPerBuilding2.get(buildingEntry.getKey());
				if (nettoForThisBuilding==null)
				{
					nettoForThisBuilding = new HashMap<Material, Double>();
					nettoPerBuilding2.put(buildingEntry.getKey(), nettoForThisBuilding );
				}
				for (Entry<Material, Integer> materialEntry : buildingEntry.getValue().entrySet())
					addTo(nettoForThisBuilding, materialEntry.getKey(), 0.+materialEntry.getValue(), Double.class);
//					addTo(nettoPerBuilding, buildingEntry.getKey(), materialEntry.getKey(), 0.+materialEntry.getValue(), Double.class);
			}
//			for (Entry<BuildingType, HashMap<Material, Double>> buildingEntry : nettoPerBuilding.entrySet())
//				for (Entry<Material, Double> materialEntry : buildingEntry.getValue().entrySet())
//					materialEntry.setValue(materialEntry.getValue()/hours);
		}
		void computeNetto(double hours) {
			netto = new HashMap<Material, Double>();
			computeNetto(netto, hours, false);
		}
		void computeNettoLastDay(double hours) {
			nettoLastDay = new HashMap<Material, Double>();
			computeNetto(nettoLastDay, hours, true);
		}
		void computeNetto(HashMap<Material, Double> netto2, double hours, boolean lastDay) {
			for (Entry<BuildingType, HashMap<Material, Integer>> buildingEntry : ((lastDay)? cityStatistics.consumptionPerBuildingLastDay : cityStatistics.consumptionPerBuilding).entrySet())
				for (Entry<Material, Integer> materialEntry : buildingEntry.getValue().entrySet())
					addTo(netto2, materialEntry.getKey(), -1.* materialEntry.getValue(), Double.class);
			for (Entry<BuildingType, HashMap<Material, Integer>> buildingEntry : ((lastDay)? cityStatistics.productionPerBuildingLastDay : cityStatistics.productionPerBuilding).entrySet())
				for (Entry<Material, Integer> materialEntry : buildingEntry.getValue().entrySet())
					addTo(netto2, materialEntry.getKey(), 0.+materialEntry.getValue(), Double.class);
			
//			for (Entry<Material, Double> materialEntry : netto.entrySet())
//				materialEntry.setValue(materialEntry.getValue()/hours);
		}
		void computeProduction(HashMap<Material, Integer> production2, double hours) {
			for (Entry<BuildingType, HashMap<Material, Integer>> buildingEntry : cityStatistics.productionPerBuilding.entrySet())
				for (Entry<Material, Integer> materialEntry : buildingEntry.getValue().entrySet())
					addTo(production2, materialEntry.getKey(), materialEntry.getValue(), Integer.class);
		}
		void computeConsumption(HashMap<Material, Integer> consumption2, double hours) {
			for (Entry<BuildingType, HashMap<Material, Integer>> buildingEntry : cityStatistics.consumptionPerBuilding.entrySet())
				for (Entry<Material, Integer> materialEntry : buildingEntry.getValue().entrySet())
					addTo(consumption2, materialEntry.getKey(), materialEntry.getValue(), Integer.class);
		}
		

		public static final Comparator<? super Entry<Material, Double>> compareByKeyAlphabetically = new Comparator<Entry<Material, Double>>(){
			@Override
			public int compare(Entry<Material, Double> o1, Entry<Material, Double> o2) {
				return o1.getKey().toString().compareTo(o2.getKey().toString());
			}};

		public String toStringNetto(String resetColor) {
			return toStringNetto(netto,  "<gold>Production today \n" +   StatisticsBookListener.delimiter, resetColor);
		}
		public String toStringNettoLastDay(String resetColor) {
			return toStringNetto(nettoLastDay,   "<gold>Production yesterday\n" +  StatisticsBookListener.delimiter, resetColor );
		}
		public String toStringNetto(HashMap<Material, Double> netto2, String prefix, String resetColor) {
			NumberFormat formatter = NumberFormat.getInstance();
			formatter.setMaximumFractionDigits(2);
			
			String ret = prefix;
			for (Entry<Material, Double> materialEntry : SetUtils.asSortedList(netto2.entrySet(), compareByKeyAlphabetically))
			{
				Material mat = materialEntry.getKey();
				ret +=  resetColor+MatUtils.prettyPrint(mat)+": ";
				double netRatio = materialEntry.getValue();
				if (netRatio <0)
					ret += "<bad>";
				else if (netRatio >0)
					ret += "<good>";
				else 
					ret += "<good>";
				ret +=formatter.format(netRatio)+resetColor+"\n";
			}
			ret.subSequence(0, ret.length()-2);
			return Txt.parse(ret);
		}
		public LinkedList<String> toStringNettoPerBuilding() {
			return toStringNettoPerBuilding(nettoPerBuilding);
		}
//		public LinkedList<String> toStringNettoPerBuildingLastDay() {
//			return toStringNettoPerBuilding(nettoPerBuildingLastDay);
//		}
		public LinkedList<String> toStringNettoPerBuilding(HashMap<BuildingType, HashMap<Material, Double>> nettoPerBuilding2) {
			LinkedList<String> ret = new LinkedList<String>();
			
			NumberFormat formatter = NumberFormat.getInstance();
			formatter.setMaximumFractionDigits(2);
			
			for (Entry<BuildingType, HashMap<Material, Double>> entry : nettoPerBuilding2.entrySet())
			{
				String buildingName = entry.getKey().prettyPrint();
				String page = buildingName+" \n" + StatisticsBookListener.delimiter;
//						+   StringUtils.repeat("-", buildingName.length()) + "-+\n";
				for (Entry<Material, Double> materialEntry : SetUtils.asSortedList(entry.getValue().entrySet(), compareByKeyAlphabetically))
				{
					Material mat = materialEntry.getKey();
					double netRatio = materialEntry.getValue();
					if (netRatio <0)
						page += "<bad>";
					else if (netRatio >0)
						page += "<good>";
					else 
						page += "<good>";
					page += MatUtils.prettyPrint(mat)+": "+formatter.format(netRatio)+"<black>\n";
				}
				
				ret.add(Txt.parse(page));
			}
			return ret;
		}


	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		return Json.createObjectBuilder()
			.add("consumptionPerBuildingLastDay", toJsonArrayBuilder(consumptionPerBuildingLastDay))
			.add("productionPerBuildingLastDay", toJsonArrayBuilder(productionPerBuildingLastDay))
			.add("consumptionPerBuilding", toJsonArrayBuilder(consumptionPerBuilding))
			.add("productionPerBuilding", toJsonArrayBuilder(productionPerBuilding));
	}
	private JsonArrayBuilder toJsonArrayBuilder(
			HashMap<BuildingType, HashMap<Material, Integer>> consumptionPerBuilding2) {
		JsonArrayBuilder buildingArrayBuilder = Json.createArrayBuilder();
		for (Entry<BuildingType, HashMap<Material, Integer>> buildingEntry : consumptionPerBuilding2.entrySet())
		{
			JsonArrayBuilder materialArrayBuilder = Json.createArrayBuilder();
			
			for (Entry<Material, Integer> materialEntry : buildingEntry.getValue().entrySet())
			{
				JsonObjectBuilder materialEntryJson = Json.createObjectBuilder()
						.add("key", materialEntry.getKey().toString())
						.add("value", materialEntry.getValue());
				materialArrayBuilder.add(materialEntryJson);
			}
			JsonObjectBuilder buildingEntryJson = Json.createObjectBuilder()
					.add("key", buildingEntry.getKey().toString())
					.add("value", materialArrayBuilder);
			buildingArrayBuilder.add(buildingEntryJson);
		}
		return buildingArrayBuilder;
	}
	@Override
	public CityStatisticsOld fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		consumptionPerBuilding = hashMapFromJsonArray(o.getJsonArray("consumptionPerBuilding"));
		productionPerBuilding = hashMapFromJsonArray(o.getJsonArray("productionPerBuilding"));
		consumptionPerBuildingLastDay = hashMapFromJsonArray(o.getJsonArray("consumptionPerBuildingLastDay"));
		productionPerBuildingLastDay = hashMapFromJsonArray(o.getJsonArray("productionPerBuildingLastDay"));
		return this;
	}
	private HashMap<BuildingType, HashMap<Material, Integer>> hashMapFromJsonArray(JsonArray buildingMapArray) {
		HashMap<BuildingType, HashMap<Material, Integer>> ret = new HashMap<BuildingType, HashMap<Material, Integer>>();
		for (JsonValue buildingMapVal : buildingMapArray)
		{
			JsonObject buildingMapObject = (JsonObject) buildingMapVal;
			BuildingType buildingKey = BuildingType.valueOf(buildingMapObject.getString("key"));
			JsonArray materialMapArray = buildingMapObject.getJsonArray("value");

			HashMap<Material, Integer> matMap = new HashMap<Material, Integer>();
			for (JsonValue matMapVal : materialMapArray)
			{
				JsonObject matMapObject = (JsonObject) matMapVal;
				Material matKey = Material.valueOf(matMapObject.getString("key"));
				Integer matVal = matMapObject.getInt("value");
				matMap.put(matKey, matVal);
			}
			ret.put(buildingKey, matMap);
		}
		return ret;
	}
	
	
	
	
}
