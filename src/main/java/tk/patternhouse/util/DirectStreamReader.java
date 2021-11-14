package tk.patternhouse.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class DirectStreamReader {

    public static Vector<String> read(String file) {
        Vector<String> vector = new Vector<>(1,1);
        try {
	        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
	        while(true) {
	            String st = br.readLine();
	            if(st == null) break;
	            vector.addElement(st.trim());
	        }
	        br.close();
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
        return vector;
    }
    
    public static Vector<String> read(File file) {
        Vector<String> vector = new Vector<>(1,1);
        try {
	        BufferedReader br = new BufferedReader(new FileReader(file));
	        while(true) {
	            String st = br.readLine();
	            if(st == null) break;
	            vector.addElement(st.trim());
	        }
	        br.close();
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
        return vector;
    }
    
}
