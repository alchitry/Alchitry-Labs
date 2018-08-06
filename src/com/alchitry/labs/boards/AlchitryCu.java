package com.alchitry.labs.boards;

import com.alchitry.labs.hardware.CopperLoader;
import com.alchitry.labs.hardware.ProjectLoader;
import com.alchitry.labs.project.IceStormBuilder;
import com.alchitry.labs.project.ProjectBuilder;

public class AlchitryCu extends Board {

	@Override
	public String getFPGAName() {
		return "ICE40HX8K-CB132IC";
	}

	@Override
	public String getAVRName() {
		return null;
	}

	@Override
	public String getName() {
		return "Alchitry Cu";
	}

	@Override
	public String getBaseProjectName() {
		return "alchitry-cu";
	}

	@Override
	public String getHexFile() {
		return null;
	}

	@Override
	public ProjectBuilder getBuilder() {
		return getOpenBuilder();
	}

	@Override
	public ProjectBuilder getOpenBuilder() {
		return new IceStormBuilder();
	}

	@Override
	public ProjectLoader getLoader() {
		return new CopperLoader();
	}

}
