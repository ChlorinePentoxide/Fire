package tk.patternhouse;

import tk.patternhouse.razoid.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

public class App extends RootExtensions
{
    private static Vector<String> header = new Vector<>(1,1);
    private static Vector<String> cache = new Vector<>(1,1);
    private static Vector<String> cache3 = new Vector<>(1,1);
    private static Vector<String> source = new Vector<>(1,1);
    private static Vector<String> footer = new Vector<>(1,1);

    static final String goalsConfigDir = "firerepo";
    static final String langSourceDir = "firesource";
    static final String outputDir = "basegen";

    static Date buildDate;

    public static void main( String[] args ) throws IOException {
        RootController rc = new RootController(false);
        registerRootController(rc);
        buildDate = new Date();

        // Get BASE templates

        DirectStreamReader dsr;
        DirectStreamWriter dsw;

        // Get Header template
        dsr = new DirectStreamReader("header.firetemplate");
        header = basicParse(dsr.read());

        // Get Footer template
        dsr = new DirectStreamReader("footer.firetemplate");
        footer = basicParse(dsr.read());

        // Create BASE frontpage
        dsr = new DirectStreamReader("frontpage.firetemplate");
        dsw = new DirectStreamWriter(outputDir+"/index.html");
        dsw.write(basicParse(dsr.read()));

        // Get Source templates
        dsr = new DirectStreamReader("source.firetemplate");
        cache = dsr.read();

        // Get Source templates
        dsr = new DirectStreamReader("singlesource.firetemplate");
        cache3 = dsr.read();

        // The Difficult Part
        final String goals[] = new String[] { "alphabetic", "numeric", "pyramid", "series", "spiral", "string", "symbol", "wave" };
        final String lang[] = new String[] { "c", "cpp", "cs", "java", "py" };
        for(String goal:goals) {
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
                for(File goalfile:goalfiles) cache4.addElement(iterable.replace("$(FIRE_GOAL_FILE)", goalfile.getName()).replace("$(FIRE_GOAL)", goal.toLowerCase()).replace("$(FIRE_GOAL_NAME)", goalfile.getName().substring(0,goalfile.getName().lastIndexOf('.'))));
            } else {
                cache4.addElement(che);
            }
        }
        beautifyWrite(cache4, writefile);
    }

    private static void sourceParseWrite(String[] langs, String goal) throws IOException {
        Vector<String> bufferCache;
        System.out.println("Current goal: "+goal);
        String currentGoalDir = goalsConfigDir + "/" + goal.toLowerCase() + "/";
        System.out.println("Current goaldir: "+currentGoalDir);
        File goalDir = new File(currentGoalDir);
        File[] goalfiles = goalDir.listFiles();
        for(File goalfile:goalfiles) {
            if(goalfile.getName().endsWith(".PNG")) {
                bufferCache = new Vector<>(1,1);
                String vergoal = goalfile.getName().replace(".PNG", "");
                String writefile= outputDir + "/" + goal + "/" + vergoal + ".html";
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
                            if(!f.exists()) {
                                bufferCache.addElement("We are currently working on this source. Check back later.");
                            } else {
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
        }
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
            if(st.contains("$(FIRE_BUILD_DATE)")) {
                vec.addElement(st.replace("$(FIRE_BUILD_DATE)", buildDate.toString()));
            } else {
                vec.addElement(st);
            }
        }
        return vec;
    }
}
