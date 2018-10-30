package com.alchitry.labs.project;

import java.io.File;

import com.alchitry.labs.Util;

public class Environment {
	public static final String PLANAHEAD_PATH;
	public static final String COREGEN_PATH;
	public static final String VIVADO_PATH;
	

	static {
		if (Util.isLinux) {
			PLANAHEAD_PATH = "ISE_DS" + File.separatorChar + "PlanAhead" + File.separatorChar + "bin" + File.separatorChar + "planAhead";
			COREGEN_PATH = "ISE_DS" + File.separatorChar + "ISE" + File.separatorChar + "bin" + File.separatorChar + "lin64" + File.separatorChar + "coregen";
			VIVADO_PATH = "bin" + File.separatorChar + "vivado";
		} else if (Util.isWindows) {
			PLANAHEAD_PATH = "ISE_DS" + File.separatorChar + "PlanAhead" + File.separatorChar + "bin" + File.separatorChar + "planAhead.bat";
			COREGEN_PATH = "ISE_DS" + File.separatorChar + "ISE" + File.separatorChar + "bin" + File.separatorChar + "nt" + File.separatorChar + "coregen";
			VIVADO_PATH = "bin" + File.separatorChar + "vivado";
		} else {
			PLANAHEAD_PATH = null;
			COREGEN_PATH = null;
			VIVADO_PATH = null;
		}

	}

}
