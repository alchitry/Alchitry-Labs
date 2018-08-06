package com.alchitry.labs.boards;

import com.alchitry.labs.Util;
import com.alchitry.labs.project.ProjectBuilder;

public class AlchitryAu extends Board {

	@Override
	public String getFPGAName() {
		return "xc7a35t-1ftg256c";
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
		Util.showError("There is no builder for Alchitry Au yet!");
		return null;
	}

	@Override
	public ProjectBuilder getOpenBuilder() {
		return null;
	}

}
