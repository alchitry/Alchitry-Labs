package com.alchitry.labs.project.builders;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VirtualBuilder extends ProjectBuilder {

	@Override
	protected void projectBuilder() throws Exception {
		ArrayList<File> vFiles;
		try {
			vFiles = getVerilogFiles();
		} catch (ParseException e) {
			Util.println("Error: " + e.getMessage(), true);
			return;
		}
		if (vFiles == null || vFiles.size() == 0) {
			Util.showError("Error building the project", "Error with getting list of Verilog files!");
		}
	}
}
