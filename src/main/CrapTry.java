package main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;


public class CrapTry {

	static class Q {
		int i;
		public Q(int i) { this.i=i; }
		public String toString() { return ""+i; }
	}
	
	public static void main(String[] args) {
//		HashMap<Q, Integer> qwe = new HashMap<Q, Integer>();
//		qwe.put(new Q(2), 2);
//		qwe.put(new Q(1), 1);
//		
//		TreeMap<Q, Integer> sorted = new TreeMap<Q, Integer>(new Comparator<Q>(){
//
//			@Override
//			public int compare(Q o1, Q o2) {
//				return Integer.compare(o1.i, o2.i);
//			}});
//		sorted.putAll(qwe);
//		System.out.println(qwe);
//		System.out.println(sorted);
		
//		LinkedList<Q> qs = new LinkedList<Q>();
//		qs.addAll(Arrays.asList(new Q[]{new Q(1),null, null}));
//		System.out.println(qs);
		
		HashMap<Q,Integer> m = new HashMap<Q,Integer>();
		m.put(null, 3);
//		int w = m.get(new Q(5));
		System.out.println(m);
		
		
		System.out.println("\n".length());
	}
	
	public static void main2(String[] args) {
		
		
		String buildingString = "dfgsd sgdgsd sgdgd";
		LinkedList<String> lines = new LinkedList<String>();
		String[] words = buildingString.split(" ");
		System.out.println(Arrays.toString(words));
		String newLine = words[0];
		for (int w = 1; w<words.length; w++) {
			String word = words[w];
			if (newLine.length()+word.length()+1 > 16) 
			{
				lines.add(newLine);
				newLine = word;
			} else
			{
				newLine+= " "+word;
			}
		}
		lines.add(newLine);
		System.out.println(Arrays.toString(lines.toArray()));
		for (String l : lines)
			System.out.println("\""+l+"\"");
	}
}
