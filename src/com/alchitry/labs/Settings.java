package com.alchitry.labs;

import java.util.prefs.Preferences;

public class Settings {
	public static Preferences pref = Preferences
			.userNodeForPackage(com.alchitry.labs.Settings.class);
	
	public static final String VERSION = "VERSION";
	public static final String LIB_VERSION = "LIB_VERSION";
	public static final String MOJO_PORT = "MOJO_PORT";
	public static final String WINDOW_WIDTH = "WINDOW_WIDTH";
	public static final String WINDOW_HEIGHT = "WINDOW_HEIGHT";
	public static final String FILE_LIST_WIDTH = "FILE_LIST_WIDTH";
	public static final String CONSOLE_HEIGHT = "CONSOLE_HEIGHT";
	public static final String MAXIMIZED = "MAXIMIZED";
	public static final String XILINX_LOC = "XILINX_LOC";
	public static final String OPEN_PROJECT = "OPEN_PROJECT";
	public static final String WORKSPACE = "WORKSPACE";
	public static final String THEME = "THEME";
	public static final String WORD_WRAP = "WORD_WRAP";
}
