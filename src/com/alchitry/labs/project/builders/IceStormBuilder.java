package com.alchitry.labs.project.builders;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.ParseException;

public class IceStormBuilder extends ProjectBuilder {

	@Override
	protected void projectBuilder() throws Exception {
		ArrayList<File> vFiles;
		ArrayList<File> cFiles;
		try {
			vFiles = getVerilogFiles();
			cFiles = getConstraintFiles();
		} catch (ParseException e) {
			Util.println("Error: " + e.getMessage(), true);
			return;
		}
		if (vFiles == null || vFiles.size() == 0) {
			Util.showError("Error building the project", "Error with getting list of Verilog files!");
		}
		if (cFiles == null) {
			Util.showError("Error building the project", "Error with getting list of constraint files!");
		}

		String escapedWorkFolder = workFolder.getAbsolutePath().replace(" ", "\\ ");
		
		String topModuleName = project.getTop().getName().substring(0, project.getTop().getName().lastIndexOf('.')) + "_0";

		ArrayList<String> yosysCommand = new ArrayList<>();
		yosysCommand.add(Util.getYosysCommand());
		yosysCommand.add("-p");
		yosysCommand.add("synth_ice40 -top " + topModuleName + " -blif " + escapedWorkFolder + File.separator + "alchitry.blif");
		for (File file : vFiles)
			yosysCommand.add(file.getAbsolutePath());

		Util.println(yosysCommand.toString());

		builder = Util.runCommand(yosysCommand);
		builder.waitFor();

		Util.println("");
		Util.println("Finished synthesis.");
		Util.println("");

		ArrayList<String> arachneCommand = new ArrayList<>();
		arachneCommand.add(Util.getArachneCommand());
		arachneCommand.add("-d");
		arachneCommand.add("8k");
		arachneCommand.add("-P");
		arachneCommand.add("cb132");
		arachneCommand.add("-o");
		arachneCommand.add(workFolder + File.separator + "alchitry.txt");

		removeUnsupportedConstraints(cFiles);
		for (File con : cFiles) {
			arachneCommand.add("-p");
			arachneCommand.add(con.getAbsolutePath());
		}

		arachneCommand.add(workFolder + File.separator + "alchitry.blif");

		builder = Util.runCommand(arachneCommand, false);
		builder.waitFor();

		Util.println("");
		Util.println("Finished placement.");

		ArrayList<String> icepacCommand = new ArrayList<>();

		icepacCommand.add(Util.getIcePackCommand());
		icepacCommand.add(workFolder + File.separator + "alchitry.txt");
		icepacCommand.add(workFolder + File.separator + "alchitry.bin");

		builder = Util.runCommand(icepacCommand);
		builder.waitFor();

		File binFile = new File(workFolder + File.separator + "alchitry.bin");

		if (binFile.exists()) {
			Util.println("");
			Util.println("Finished building project.", Theme.successTextColor);
		} else {
			Util.println("");
			Util.println("Bin file (" + binFile.getAbsolutePath() + ") could not be found! The build probably failed.", true);
		}
	}

	private void removeUnsupportedConstraints(List<File> constraints) {
		String ext = ".sdc";
		for (Iterator<File> it = constraints.iterator(); it.hasNext();) {
			File c = it.next();
			if (c.getName().endsWith(ext)) {
				it.remove();
				Util.println("Project IceStorm doesn't support .sdc constraints. \"" + c + "\" will be ignored.", true);
			}
		}
	}
}
