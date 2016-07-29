package de.marcbreisinger.AffinityDiagramCreator;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.prefs.Preferences;

public class ADBPreferences {
	
	public static String SHOW_TUTORIAL = "Show tutorial at program start";
	public static String WINDOW_WIDTH = "WindowWidth";
	public static String WINDOW_HEIGHT = "WindowHeight";
	public static String PAGE_WIDTH_INCH = "Page Width (inch)";
	public static String PAGE_COLOR = "Page Color";
	public static String PAGE_HEIGHT_INCH = "Page Height (inch)";
	public static String COLOR_LEVEL_1 = "Postit Color Level 1";
	public static String COLOR_LEVEL_2 = "Postit Color Level 2";
	public static String COLOR_LEVEL_3 = "Postit Color Level 3";
	public static String COLOR_LEVEL_4 = "Postit Color Level 4";
	public static String POSTIT_SIDELENGTH_INCH = "PostIt Side Length (inch)";
	public static String POSTIT_MARGIN = "PostIt Margin (inch)";
	public static String POSTIT_OFFSET = "PostIt Offset (inch)";
	public static String PAGE_MARGIN = "Page Margin (inch)";
	public static String CANVAS_MARGIN = "Canvas Margin (inch)";
	public static String CLUSTER_OFFSET = "Cluster Offset (inch)";
	public static String FONT_SIZE = "Font Size";
	public static String GRID_SIZE_INCH = "Grid Size (inches, 0 = no grid)";
	public static String TEXT_TOO_LONG_MARKER_COLOR = "Text Truncated Marker Color";
	
	
	
	private Hashtable<String, String> prefList = new Hashtable<>();
	private static ADBPreferences singleton = null;
	
	public static ADBPreferences getPreferenes(){
		if(singleton == null)
			singleton = new ADBPreferences();
		return singleton;
	}
	private ADBPreferences(){
		
		

		prefList.put(WINDOW_HEIGHT, "1280");
		prefList.put(WINDOW_WIDTH, "754");
		
		prefList.put(PAGE_COLOR, "#ffffff");
		prefList.put(COLOR_LEVEL_1, "#aaf090");
		prefList.put(COLOR_LEVEL_2, "#ffabe3");
		prefList.put(COLOR_LEVEL_3, "#70cdee");
		prefList.put(COLOR_LEVEL_4, "#e5d175");
		prefList.put(PAGE_WIDTH_INCH, "32");
		prefList.put(PAGE_HEIGHT_INCH, "76");
		prefList.put(POSTIT_SIDELENGTH_INCH, "3.0");
		prefList.put(POSTIT_MARGIN, "0.5");
		prefList.put(POSTIT_OFFSET, "0.5");
		prefList.put(PAGE_MARGIN, "0.5");
		prefList.put(CANVAS_MARGIN, "0.5");
		prefList.put(CLUSTER_OFFSET, "0.5");
		prefList.put(FONT_SIZE, "20");
		prefList.put(GRID_SIZE_INCH, "0");
		prefList.put(TEXT_TOO_LONG_MARKER_COLOR, "#ff0000");
		
		
		
		Preferences prefs = Preferences.userNodeForPackage(de.marcbreisinger.AffinityDiagramCreator.ADBPreferences.class);
		Enumeration<String> keys = prefList.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = prefs.get(key, prefList.get(key));
			prefList.put(key, value);
			prefs.put(key, value);
		}
	}

	public Hashtable<String, String> getDialogList() {
		return prefList;
	}
	public static String getPreference(String key){
		Preferences prefs = Preferences.userNodeForPackage(de.marcbreisinger.AffinityDiagramCreator.ADBPreferences.class);
		return prefs.get(key, "na");
	}
	public void saveValues(){
		Preferences prefs = Preferences.userNodeForPackage(de.marcbreisinger.AffinityDiagramCreator.ADBPreferences.class);
		Enumeration<String> keys = prefList.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) prefList.get(key);
			prefs.put(key, value);
		}
	}

	public void saveValue(String key, String value) {
		Preferences prefs = Preferences.userNodeForPackage(de.marcbreisinger.AffinityDiagramCreator.ADBPreferences.class);
		prefs.put(key, value);
	}
}
