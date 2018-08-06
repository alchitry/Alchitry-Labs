package com.alchitry.labs.boards;

import com.alchitry.labs.project.ISEBuilder;
import com.alchitry.labs.project.IceStormBuilder;
import com.alchitry.labs.project.ProjectBuilder;

public class MojoV3 extends Board {

	@Override
	public String getFPGAName() {
		return "xc6slx9-2tqg144";
	}

	@Override
	public String getName() {
		return "Mojo V3";
	}

	@Override
	public String getBaseProjectName() {
		return "mojo-v3";
	}

	@Override
	public String getAVRName() {
		return "atmega32u4";
	}

	@Override
	public String getHexFile() {
		return "mojo-v3-loader.hex";
	}

	@Override
	public ProjectBuilder getBuilder() {
		return new IceStormBuilder();
	}

	@Override
	public ProjectBuilder getOpenBuilder() {
		return null;
	}
	
}
