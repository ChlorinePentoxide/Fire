package tk.patternhouse;

import tk.patternhouse.razoid.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class App extends RootExtensions
{
    private static Vector<String> header = new Vector<>(1,1);
    private static Vector<String> cache = new Vector<>(1,1);
    private static Vector<String> cache3 = new Vector<>(1,1);
    private static Vector<String> source = new Vector<>(1,1);
    private static Vector<String> footer = new Vector<>(1,1);

    private static final String goalsConfigDir = "firerepo";
    private static final String langSourceDir = "firesource";
    private static final String outputDir = "basegen";

    private static Date buildDate;
    private static String version = "1.05c-12";

    public static void main( String[] args ) throws IOException {
        RootController rc = new RootController(false);
        registerRootController(rc);
        buildDate = new Date();
        
        System.out.print("RAZOID: Preparing PatternHouse FIRE Version "+version+" ... ");
        
        System.out.println("DONE.");
        
        // Get BASE templates

        System.out.print("FIRE: Prepating DirectStreams ... ");
        
        DirectStreamReader dsr;
        DirectStreamWriter dsw;
        
        System.out.println("DONE.");
        
        System.out.print("FIRE: Reading header templates ... ");
        
        // Get Header template
        dsr = new DirectStreamReader("header.firetemplate");
        header = basicParse(dsr.read());
        
        System.out.println("DONE.");
        
        System.out.print("FIRE: Reading footer templates ... ");

        // Get Footer template
        dsr = new DirectStreamReader("footer.firetemplate");
        footer = basicParse(dsr.read());
        
        System.out.println("DONE.");
        
        System.out.print("FIRE: Reading frontpage templates ... ");

        // Create BASE frontpage
        dsr = new DirectStreamReader("frontpage.firetemplate");
        
        System.out.println("DONE.");
        
        System.out.print("FIRE: Writing frontpages ... ");
        
        dsw = new DirectStreamWriter(outputDir+"/index.html");
        dsw.write(basicParse(dsr.read()));
        
        System.out.println("DONE.");
        
        System.out.print("FIRE: Reading source files ... ");

        // Get Source templates
        dsr = new DirectStreamReader("source.firetemplate");
        cache = dsr.read();
        
        System.out.println("DONE.");
        
        System.out.print("FIRE: Reading singlesource files ... ");

        // Get Source templates
        dsr = new DirectStreamReader("singlesource.firetemplate");
        cache3 = dsr.read();
        
        System.out.println("DONE.");
        
        
        System.out.println("FIRE: Executing parsing jobs ... ");
        
        // The Difficult Part
        final String goals[] = new String[] { "alphabetic", "numeric", "pyramid", "series", "spiral", "string", "symbol", "wave" };
        final String lang[] = new String[] { "c", "cpp", "cs", "java", "py" };
        for(String goal:goals) {
            System.out.println("FIRE : Executing Job \""+goal+"\" ... ");
            sourceParseWrite(lang, goal);
            singleSourceParseWrite(goal);
        }

    }

    private static void singleSourceParseWrite(String goal) throws IOException {
        String currentGoalDir = goalsConfigDir + "/" + goal.toLowerCase();
        String writefile = outputDir + "/" + goal + ".html";
        File f = new File(outputDir+"/"+goal);
        if(!f.exists()) f.mkdirs();
        Vector<String> cache4 = new Vector<>(1,1);
        for(String che:cache3) {
            if(che.startsWith("$(FIRE_SS_ITER)")) {
                String iterable = che.replace("$(FIRE_SS_ITER)", "").trim();
                File goalDir = new File(currentGoalDir);
                File[] goalfiles = goalDir.listFiles();
                Arrays.sort(goalfiles);
                for(File goalfile:goalfiles) cache4.addElement(iterable.replace("$(FIRE_GOAL_FILE)", goalfile.getName()).replace("$(FIRE_GOAL)", goal.toLowerCase()).replace("$(FIRE_GOAL_NAME)", goalfile.getName().substring(0,goalfile.getName().lastIndexOf('.'))));
            } else {
                cache4.addElement(che);
            }
        }
        beautifyWrite(cache4, writefile);
    }

    private static void sourceParseWrite(String[] langs, String goal) throws IOException {
        System.out.println("FIRE::SPW : Current Job: "+goal);
        String currentGoalDir = goalsConfigDir + "/" + goal.toLowerCase() + "/";
        System.out.println("FIRE::SPW : Current Job Directory: "+currentGoalDir);
        File goalDir = new File(currentGoalDir);
        File[] goalfiles = goalDir.listFiles();
        Arrays.sort(goalfiles);
        for(File goalfile:goalfiles) {
            if(goalfile.getName().endsWith(".PNG")) {
                parseForLanguage(".PNG", goalfile, goal, langs);
            }
            if(goalfile.getName().endsWith(".png")) {
                parseForLanguage(".png", goalfile, goal, langs);
            }
            if(goalfile.getName().endsWith(".jpg")) {
                parseForLanguage(".jpg", goalfile, goal, langs);
            }
        }
    }
    
    private static void parseForLanguage(String extension, File goalfile, String goal, String[] langs) throws IOException {
        Vector<String> bufferCache = new Vector<>(1,1);
        String vergoal = goalfile.getName().replace(extension, "");
        String writefile= outputDir + "/" + goal + "/" + vergoal + ".html";
        System.out.println("FIRE::SPW : Current Write File : "+writefile);
        File fl = new File(outputDir+"/"+goal);
        if(!fl.exists()) fl.mkdirs();
        for(int i=0;i<cache.size();i++) {
            String cacheelement = cache.elementAt(i);
            String repl = cacheelement;
            if(repl.contains("$(FIRE_GOAL_FILE)")) repl = repl.replace("$(FIRE_GOAL_FILE)", goalfile.getName());
            if(repl.contains("$(FIRE_GOAL_NAME)")) repl = repl.replace("$(FIRE_GOAL_NAME)", vergoal);
            if(repl.contains("$(FIRE_GOAL)")) repl = repl.replace("$(FIRE_GOAL)", goal.toLowerCase());
            if(repl.contains("$(FIRE_SOURCES)")) {
                for(String lang:langs) {
                    bufferCache.addElement("Source: "+vergoal+"."+lang);
                    bufferCache.addElement("<pre>");
                    File f = new File(langSourceDir + "/" + lang + "/" + goal + "/"+ vergoal +"."+lang);
                    System.out.print("FIRE::SPW (GOAL \""+goal+"\") : Scanning \""+lang+"\" source file \""+f.getPath()+"\" ... ");
                    if(!f.exists()) {
                        System.out.println("ABSENT");
                        bufferCache.addElement("We are currently working on this source. Check back later.");
                    } else {
                        System.out.println("FOUND");
                        Vector<String> cache2 = (new DirectStreamReader(f)).read();
                        for(String st:cache2)  bufferCache.addElement(st);
                    }
                    bufferCache.addElement("</pre>");
                }
                repl = "";
            } 
            bufferCache.addElement(repl);
        }
        beautifyWrite(bufferCache, writefile);
    }

    private static void beautifyWrite(Vector<String> bufferCache, String s) throws IOException {
        Vector<String> finalcache = new Vector<>(1,1);
        for(String st:header) finalcache.addElement(st);
        for(String st:bufferCache) finalcache.addElement(st);
        for(String st:footer) finalcache.addElement(st);
        DirectStreamWriter dsw = new DirectStreamWriter(s);
        dsw.write(finalcache);
    }

    private static Vector<String> basicParse(Vector<String> v) {
        Vector<String> vec = new Vector<>(1,1);
        for(String st:v) {
            String string = st;
            if(st.contains("$(FIRE_BUILD_DATE)")) {
                string = string.replace("$(FIRE_BUILD_DATE)", buildDate.toString());
            } 
            if(st.contains("$(FIRE_VERSION)")) { 
                string = string.replace("$(FIRE_VERSION)", version);
            }
            vec.addElement(string);
        }
        return vec;
    }
}
