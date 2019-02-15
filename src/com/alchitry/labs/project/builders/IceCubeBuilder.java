package com.alchitry.labs.project.builders;

import java.io.File;
import java.util.ArrayList;

import com.alchitry.labs.Util;
import com.alchitry.labs.style.ParseException;

public class IceCubeBuilder extends ProjectBuilder {

	@Override
	protected void projectBuilder() throws Exception {
		ArrayList<String> vFiles;
		try {
			vFiles = getVerilogFiles();
		} catch (ParseException e) {
			Util.println("Error: " + e.getMessage(), true);
			return;
		}
		if (vFiles == null || vFiles.size() == 0) {
			Util.showError("Error building the project", "Error with getting list of Verilog files!");
		}
		String srcFolder = workFolder + File.separatorChar + "verilog";
		
		String escapedWorkFolder = workFolder.replace(" ", "\\ ");
	}

}
