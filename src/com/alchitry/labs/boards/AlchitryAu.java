package com.alchitry.labs.boards;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.hardware.AuLoader;
import com.alchitry.labs.hardware.ProjectLoader;
import com.alchitry.labs.project.ProjectBuilder;
import com.alchitry.labs.project.VivadoBuilder;

public class AlchitryAu extends Board {

	@Override
	public String getFPGAName() {
		return "xc7a35tftg256-1";
	}

	@Override
	public String getAVRName() {
		return null;
	}

	@Override
	public String getName() {
		return "Alchitry Au";
	}

	@Override
	public String getBaseProjectName() {
		return "alchitry-au";
	}

	@Override
	public String getHexFile() {
		return null;
	}

	@Override
	public ProjectBuilder getBuilder() {
		return new VivadoBuilder();
	}

	@Override
	public ProjectBuilder getOpenBuilder() {
		return null;
	}

	@Override
	public ProjectLoader getLoader() {
		return new AuLoader();
	}

}
