package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SetUtils {

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	public static <T> List<T> asSortedList(Collection<T> c, Comparator<? super T> comparator) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list, comparator);;
		return list;
	}
}
