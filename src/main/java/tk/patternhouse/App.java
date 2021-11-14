package tk.patternhouse;

import tk.patternhouse.util.DirectStreamReader;
import tk.patternhouse.util.VCache;
import tk.patternhouse.util.Writers;

public class App {
	
	public static int HEAD_C, FOOT_C, FPAGE_C, SRC_C, SISRC_C;
	
	public static String outputDir = "basegen";
	
	public static String versionString = "v2.01-beta-1";
	
	public static final String goalsConfigDir = "firerepo";
	public static final String langSourceDir = "firesource";
	public static final String goals[] = new String[] { "alphabetic", "numeric", "pyramid", "series", "spiral", "string", "symbol", "wave" };
	public static final String lang[] = new String[] { "c", "cpp", "cs", "java", "py" };
	
	public static void main(String[] args) {
		System.out.println("Fire Build System v2.01-beta-1");
		System.out.println("FIRE[CTRL]: Starting init() ... ");
		init();
		System.out.println("FIRE[CTRL]: Initialization Complete.");
		System.out.println("FIRE[CTRL]: Making Index Pages ... ");
		Writers.makeIndexPages();
		System.out.println("FIRE[CTRL]: Executing Writer Subunits ... ");
		Writers.executeSubunits();
	}
	
	public static void init() {
		System.out.print("FIRE[INIT]: Initializing VCache ... ");
		VCache.init();
		System.out.println("Done.");
		
		System.out.print("FIRE[INIT]: Reading Templates ... ");
		
		HEAD_C = VCache.storeCache(DirectStreamReader.read("header.firetemplate"));
		FOOT_C = VCache.storeCache(DirectStreamReader.read("footer.firetemplate"));
		FPAGE_C = VCache.storeCache(DirectStreamReader.read("frontpage.firetemplate"));
		SRC_C = VCache.storeCache(DirectStreamReader.read("source.firetemplate"));
		SISRC_C = VCache.storeCache(DirectStreamReader.read("singlesource.firetemplate"));
		
		System.out.println("Done.");
		
		System.out.print("FIRE[INIT]: Initializing Writers ... ");
		Writers.init();
		System.out.println("Done.");
	}
	
}
