package tk.patternhouse.util;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import tk.patternhouse.App;

public class Writers {

    private static Vector<String> cache = new Vector<>(1,1);
    private static Vector<String> cache3 = new Vector<>(1,1);
	
    public static void init() {
    	cache = VCache.getCache(App.SRC_C);
    	cache3 = VCache.getCache(App.SISRC_C);
    }
    
	public static void makeIndexPages() {
		DirectStreamWriter.write(commonParse(VCache.getCache(App.FPAGE_C)), App.outputDir+"/index.html");
	}
	
	public static void executeSubunits() {
		for(String goal:App.goals) {
            System.out.println("FIRE : Executing Job \""+goal+"\" ... ");
            sourceParseWrite(App.lang, goal);
            singleSourceParseWrite(goal);
        }
	}
	
	private static void singleSourceParseWrite(String goal) {
        String currentGoalDir = App.goalsConfigDir + "/" + goal.toLowerCase();
        String writefile = App.outputDir + "/" + goal + ".html";
        File f = new File(App.outputDir+"/"+goal);
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
        basicAppendWrite(commonParse(cache4), writefile);
    }

    private static void sourceParseWrite(String[] langs, String goal) {
        System.out.println("FIRE::SPW : Current Job: "+goal);
        String currentGoalDir = App.goalsConfigDir + "/" + goal.toLowerCase() + "/";
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
    
    private static void parseForLanguage(String extension, File goalfile, String goal, String[] langs) {
        Vector<String> bufferCache = new Vector<>(1,1);
        String vergoal = goalfile.getName().replace(extension, "");
        String writefile= App.outputDir + "/" + goal + "/" + vergoal + ".html";
        System.out.println("FIRE::SPW : Current Write File : "+writefile);
        File fl = new File(App.outputDir+"/"+goal);
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
                    bufferCache.addElement("<pre class=\"prettyprint p-2\" style=\"overflow-wrap: break-word;\">");
                    File f = new File(App.langSourceDir + "/" + lang + "/" + goal + "/"+ vergoal +"."+lang);
                    System.out.print("FIRE::SPW (GOAL \""+goal+"\") : Scanning \""+lang+"\" source file \""+f.getPath()+"\" ... ");
                    if(!f.exists()) {
                        System.out.println("ABSENT");
                        bufferCache.addElement("// We are currently working on this source. Check back later.");
                    } else {
                        System.out.println("FOUND");
                        Vector<String> cache2 = DirectStreamReader.read(f);
                        for(String st:cache2)  bufferCache.addElement(st);
                    }
                    bufferCache.addElement("</pre><br>");
                }
                repl = "";
            } 
            bufferCache.addElement(repl);
        }
        basicAppendWrite(bufferCache, writefile);
    }
	
	private static void basicAppendWrite(Vector<String> content, String filename) {
		Vector<String> finalcache = new Vector<>(1,1);
        for(String st:VCache.getCache(App.HEAD_C)) finalcache.addElement(st);
        for(String st:content) finalcache.addElement(st);
        for(String st:VCache.getCache(App.FOOT_C)) finalcache.addElement(st);
        DirectStreamWriter.write(finalcache, filename);
	}
	
	private static Vector<String> commonParse(Vector<String> v) {
		Date buildDate = new Date();
		Vector<String> vec = new Vector<>(1,1);
        for(String st:v) {
            String string = st;
            if(st.contains("$(FIRE_BUILD_DATE)")) {
                string = string.replace("$(FIRE_BUILD_DATE)", buildDate.toString());
            } 
            if(st.contains("$(FIRE_VERSION)")) { 
                string = string.replace("$(FIRE_VERSION)", App.versionString);
            }
            vec.addElement(string);
        }
        return vec;
	}
	
}
