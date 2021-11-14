package tk.patternhouse.util;

import java.util.Vector;

public class VCache {

	private static Vector<Vector<String>> vcache;
	
	public static void init() {
		vcache = new Vector<>(1,1);
	}
	
	public static int newCache() {
		return storeCache(new Vector<String>(1,1));
	}
	
	public static int storeCache(Vector<String> vec) {
		int index = vcache.size();
		vcache.addElement(vec);
		return index;
	}
	
	public static Vector<String> getCache(int i) {
		return vcache.elementAt(i);
	}
	
}
