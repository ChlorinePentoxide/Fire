package tk.patternhouse.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class DirectStreamWriter {

	public static void write(Vector<String> vector, String file) {
        try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file)));
			for(String st:vector) bw.write(st+"\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}
