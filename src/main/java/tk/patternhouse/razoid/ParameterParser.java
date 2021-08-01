package tk.patternhouse.razoid;

import java.util.Vector;

public class ParameterParser {

    private String[] args;

    private Vector<String> flags;

    private Vector<String> parametricFlags;

    public ParameterParser(String[] a) {
        args = a;
        flags = new Vector<>(1,1);
        parametricFlags = new Vector<>(1,1);
        parse();
    }

    private void parse() {
        for(int i = 0; i < args.length - 1; i++) {
            if(args[i].startsWith("--") && args[i+1].startsWith("--")) {
                flags.addElement(args[i]);
                args[i] = "";
            }
        }
        if(args[args.length-1].startsWith("--")) flags.addElement(args[args.length-1]);
        for(String st:args) {
            if(!st.equals("")) parametricFlags.addElement(st);
        }

    }

    public Vector<String> getFlags() {
        return flags;
    }

    public Vector<String> getParametricFlags() {
        return parametricFlags;
    }
}
