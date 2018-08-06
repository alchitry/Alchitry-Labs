package com.alchitry.labs.boards;

import com.alchitry.labs.Util;
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
		Util.showError("There is no builder for Alchitry Au yet!");
		return null;
	}

	@Override
	public ProjectBuilder getOpenBuilder() {
		return null;
	}

}
