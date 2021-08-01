package tk.patternhouse.razoid;

import java.util.Vector;

public class VectorUtils {

    public static String[] convertVector(Vector<String> vector) {
        String[] array = new String[vector.size()];
        for(int i=0;i<vector.size();i++) array[i] = vector.elementAt(i);
        return array;
    }

    public static void convertVector(Vector<String> vector, String[] array) {
        for(int i=0;i<vector.size();i++) array[i] = vector.elementAt(i);
    }

    public static <T> void convertVector(Vector<T> vector, T[] array) {
        for(int i=0;i<vector.size();i++) array[i] = vector.elementAt(i);
    }

    public static Vector<String> clone(Vector<String> v) {
        Vector<String> vec = new Vector<>(1,1);
        for(int i=0;i<v.size();i++) vec.addElement(v.elementAt(i));
        return vec;
    }
}
