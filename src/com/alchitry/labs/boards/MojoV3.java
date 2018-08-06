package com.alchitry.labs.boards;

import com.alchitry.labs.hardware.MojoLoader;
import com.alchitry.labs.hardware.ProjectLoader;
import com.alchitry.labs.project.ISEBuilder;
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
		return new ISEBuilder();
	}

	@Override
	public ProjectBuilder getOpenBuilder() {
		return null;
	}
	
	@Override
	public ProjectLoader getLoader() {
		return new MojoLoader();
	}
	
}
