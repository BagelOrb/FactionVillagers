package happiness;

import interaction.StatisticsBookListener;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;

import buildings.Building;
import buildings.BuildingWithStorage;
import buildings.TownHall;
import characters.ChestCharacter;
import city.City;

import com.massivecraft.massivecore.util.Txt;

public class Happiness {

	public static String happinessToString(ChestCharacter charTrait, String resetColor) {
		double happiness = charTrait.getHappiness();
		
		return happinessToString(happiness, resetColor);
	}
	
	private static String happinessToString(double happiness, String resetColor) {
		return happinessToString(happiness, resetColor, true);
	}

	private static String happinessToString(double happiness, String resetColor, boolean oneLine) {
		int percentage = (int) (getHappinessPercentage(happiness)*100);
		
		DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.0" );
		
		
		String smiley;
		String color;
		
		double happinessScaled = happiness *.2;
		if (happinessScaled < -3.5)
		{
			color = "<bad>";
			smiley = color+">:(";
		}
		else if (happinessScaled < -2.5)
		{
			color = "<bad>";
			smiley = color+":'(";
		}
		else if (happinessScaled < -1.5)
		{
			color = "<bad>";
			smiley = color+":(";
		}
		else if (happinessScaled < -.5)
		{
			color = "<bad>";
			smiley = color+":/";
		}
		else if (happinessScaled < .5)
		{
			color = "<gold>";
			smiley = color+":l";
		}
		else if (happinessScaled < 1.5)
		{
			color = "<good>";
			smiley = color+":)";
		}
		else if (happinessScaled < 2.5)
		{
			color = "<good>";
			smiley = color+":>";
		}
		else if (happinessScaled < 3.5)
		{
			color = "<good>";
			smiley = color+":D";
		}
		else
		{
			color = "<good>";
			smiley = color+":')";
		}
		
		String percentageStr = color+percentage +"% "+resetColor+"("+color+df2.format(happiness)+resetColor+")";
		
		return smiley+(oneLine ? " " : "\n")+percentageStr;
	}
	
	private static String happinessToStringSimple(double happiness) {
		
		DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.0" );
		
		String happinessStr = df2.format(happiness);
		
		if(happiness<0)
			happinessStr = "<bad>"+happinessStr;
		else
			happinessStr = "<good>"+happinessStr;
		
		return happinessStr;
	}

	public static double getHappinessPercentage(double happiness) {
		return Math.tanh(happiness*.02)*.5+.5;
	}

	public static Collection<? extends String> getStatisticsBookDescription(City city) {
		LinkedList<String> pages = new LinkedList<String>();
		
		
		
		StringBuilder eachBuildingHappiness = new StringBuilder();
		double total = 0;
		double nVillagers = 0;
		for (Building b : city.getAllBuildings())
			if (b instanceof BuildingWithStorage && !(b instanceof TownHall))
			{
				double happiness = ((BuildingWithStorage) b).getChestCharacter().getHappiness(); 
				
				eachBuildingHappiness.append("<black>"+b+": "+happinessToString(happiness, "<black>", false)+"\n");
				total += happiness;
				nVillagers++;
			}
		double average = total / nVillagers;

		StringBuilder page = new StringBuilder();
		
		page.append("<gold>Happiness overview \n");
		page.append(StatisticsBookListener.delimiter);
		page.append("<black>Number of villagers penalty: "+happinessToStringSimple(city.getNpcHappinessEnhancement())+"\n");
		page.append("\n");
		page.append("<black>Average happiness: "+happinessToString(average, "<black>", false)+"\n");
		page.append("\n");
		
		page.append(eachBuildingHappiness);
		
		pages.addAll(StatisticsBookListener.breakPage(Txt.parse(page.toString())));
		
		return pages;
	}

}
