package com.alchitry.labs.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.style.ParseException;

public class IceStormBuilder extends ProjectBuilder {

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

		ArrayList<String> yosysCommand = new ArrayList<>();
		yosysCommand.add(Util.getYosysCommand());
		yosysCommand.add("-p");
		yosysCommand.add("synth_ice40 -top mojo_top_0 -blif " + workFolder + File.separator + "alchitry.blif");
		for (String file : vFiles)
			yosysCommand.add(srcFolder + File.separatorChar + file);

		runCommand(yosysCommand, "Yosys");
		
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

		HashSet<String> constraints = project.getConstraintFiles(false);
		for (String con : constraints) {
			arachneCommand.add("-p");
			arachneCommand.add(project.getConstraintFolder() + File.separatorChar + con);
		}
		constraints = project.getConstraintFiles(true);
		for (String con : constraints) {
			arachneCommand.add("-p");
			arachneCommand.add(Locations.COMPONENTS + File.separatorChar + con);
		}
		arachneCommand.add(workFolder + File.separator + "alchitry.blif");

		runCommand(arachneCommand, "Arachne");
		
		Util.println("");
		Util.println("Finished placement.");
		Util.println("");

		ArrayList<String> icepacCommand = new ArrayList<>();

		icepacCommand.add(Util.getIcePackCommand());
		icepacCommand.add(workFolder + File.separator + "alchitry.txt");
		icepacCommand.add(workFolder + File.separator + "alchitry.bin");
		
		runCommand(icepacCommand, "IcePac");

		Util.println("");
		Util.println("Finished building project.");
	}

	private void runCommand(List<String> cmd, String name) throws InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(cmd);

		try {
			builder = pb.start();
		} catch (Exception e) {
			Util.log.severe("Couldn't start " + cmd.get(0));
			Util.showError("Could not start " + name + "! Please check the location for " + name + " is correctly set in the settings menu.");
			return;
		}

		startStreamPrinter(builder.getInputStream(), false);
		startStreamPrinter(builder.getErrorStream(), true);

		builder.waitFor();
	}

}
