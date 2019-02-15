package com.alchitry.labs;

import java.io.File;
import java.net.URISyntaxException;

import com.alchitry.labs.gui.main.MainWindow;

public class Locations {
	public static final File progDir;
	public static final String progPrefix;

	static {
		File prog = null;
		try {
			prog = new File(MainWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI());

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			Util.log.severe("Could not detect program directory!");
		}

		prog = prog.getParentFile();
		if (prog != null && !new File(prog.getPath() + File.separator + "lib").exists())
			progDir = prog.getParentFile();
		else
			progDir = prog;

		if (progDir != null)
			progPrefix = progDir.getPath() + File.separatorChar;
		else
			progPrefix = "";

	}

	public static final String LIBRARY = progPrefix + "library";
	public static final String BASE = LIBRARY + File.separator + "base";
	public static final String COMPONENTS = LIBRARY + File.separator + "components";
	public static final String FIRMWARE = LIBRARY + File.separator + "firmware";

	public static final String TOOLS = progPrefix + "tools";
	public static final String BIN = TOOLS + File.separator + Util.osDir + File.separator + "bin";
	public static final String ETC = TOOLS + File.separator + "etc";
	public static final String LIB = TOOLS + File.separator + Util.osDir + File.separator + "lib";

	public static final String RESOURCES = progPrefix + "res";

	public final static String TEMPLATE_DIR = BASE + File.separator + "templates";

	private Locations() {
	}
}
