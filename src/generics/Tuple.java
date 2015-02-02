package generics;


public class Tuple<L,R> { // implements ConfigurationSerializable  {
//	@Deprecated public Tuple() {} // used in serialization
	public L fst;
	public R snd;
	public Tuple(L ll, R rr) {
		fst = ll; snd = rr;
	}

	public String toString() {
		return "("+fst+", "+snd+")";
	}

	// doesn't seem to work :S  :
//	@Override
//	public Map<String, Object> serialize() {
//		HashMap<String, Object> ret = new HashMap<String, Object>();
//		ret.put("fst", fst);
//		ret.put("snd", snd);
//		return ret;
//	}
//	@SuppressWarnings("unchecked")
//	public static Tuple<?,?> deserialize(Map<String, Object> map) {
//		Object l = map.get("fst");
//		Object r = map.get("and");
//		return new Tuple(l,r);
//	}
}
