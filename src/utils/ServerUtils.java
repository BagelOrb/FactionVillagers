package utils;

import generics.Tuple;

import java.text.NumberFormat;

public class ServerUtils {

	public static String formatMCtime(long time) {
		NumberFormat timeFormat = NumberFormat.getIntegerInstance();
		timeFormat.setMinimumIntegerDigits(2);
		Tuple<Long, Long> hm = mcTimeToHoursMinutes(time);
		String formatted = timeFormat.format(hm.fst)+":"+timeFormat.format(hm.snd);
		return formatted;
	}
	
	public static Tuple<Long, Long> mcTimeToHoursMinutes(long time) {
		long hours = time/1000;
		long minutes = time%1000 *60 / 1000;
		return new Tuple<Long, Long>(hours, minutes);
	}

}
