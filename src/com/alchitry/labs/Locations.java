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

	public static final File LIBRARY = new File(progPrefix + "library");
	public static final File BASE = new File(LIBRARY + File.separator + "base");
	public static final File COMPONENTS = new File(LIBRARY + File.separator + "components");
	public static final File FIRMWARE = new File(LIBRARY + File.separator + "firmware");

	public static final File TOOLS = new File(progPrefix + "tools");
	public static final File BIN = new File(TOOLS + File.separator + Util.osDir + File.separator + "bin");
	public static final File ETC = new File(TOOLS + File.separator + "etc");
	public static final File LIB = new File(TOOLS + File.separator + Util.osDir + File.separator + "lib");

	public static final File RESOURCES = new File(progPrefix + "res");

	public final static File TEMPLATE_DIR = new File(BASE + File.separator + "templates");

	private Locations() {
	}
}
