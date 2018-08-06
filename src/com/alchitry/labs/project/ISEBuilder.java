package com.alchitry.labs.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.MainWindow;
import com.alchitry.labs.style.ParseException;

public class ISEBuilder extends ProjectBuilder {
	private static final String projectFile = "project.tcl";
	private static final String projectDir = "planAhead";

	public ISEBuilder() {

	}

	protected void projectBuilder() throws Exception {
		BufferedWriter out = null;
		String tclScript = workFolder + File.separatorChar + projectFile;

		try {
			FileWriter fstream = new FileWriter(tclScript);
			out = new BufferedWriter(fstream);

			if (!generateProjectFile(out))
				return;
		} catch (Exception e) {
			throw e;
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					Util.log.log(Level.SEVERE, "Failed to close stdout stream!", e);
				}
		}

		String xilinx = Util.getISELocation();

		if (xilinx == null) {
			Util.log.severe("Couldn't find ISE :(");
			Util.showError("ISE's location must be set in the settings menu before you can build!");
			return;
		}

		if (!xilinx.endsWith(File.separator))
			xilinx = xilinx + File.separator;

		String planAhead = xilinx + Environment.PLANAHEAD_PATH;
		// System.out.println(planAhead);

		if (!Util.isIseUpdated()) // only ISE 14.7 supported
			if (!Util.askQuestion("ISE is Out of Date", "Only version 14.7 of ISE is supported. Continue anyway?"))
				return;

		ProcessBuilder pb = new ProcessBuilder(planAhead, "-nojournal", "-nolog", "-mode", "batch", "-source", tclScript);

		try {
			builder = pb.start();
		} catch (Exception e) {
			Util.log.severe("Couldn't start PlanAhead. Tried " + planAhead);
			Util.showError("Could not start PlanAhead! Please check the location for ISE is correctly set in the settings menu.");
			return;
		}

		startStreamPrinter(builder.getInputStream(), false);
		startStreamPrinter(builder.getErrorStream(), true);

		builder.waitFor();

		Util.println("");
		Util.println("Finished building project.");
	}

	private String getSpacedList(Collection<String> list, String prefix) {
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append("\"").append(prefix).append(s).append("\" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	private String getSpacedListofCores(Collection<IPCore> list, String prefix) {
		StringBuilder builder = new StringBuilder();
		for (IPCore core : list)
			for (String s : core.getFiles()) {
				builder.append("\"").append(prefix).append(s).append("\" ");
			}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	private boolean generateProjectFile(BufferedWriter file) throws IOException {
		final String nl = System.lineSeparator();
		final String ps = "/"; // the tcl script expects / for all OS's

		ArrayList<String> vFiles;
		try {
			vFiles = getVerilogFiles();
		} catch (ParseException e) {
			Util.println("Error: " + e.getMessage(), true);
			return false;
		}
		if (vFiles == null || vFiles.size() == 0) {
			Util.showError("Error building the project", "Error with getting list of Verilog files!");
		}
		String srcFolder = workFolder + File.separatorChar + "verilog";

		file.write("set projDir \"" + workFolder.replace("\\", "/") + ps + projectDir + "\"" + nl);
		file.write("set projName \"" + project.getProjectName() + "\"" + nl);
		file.write("set topName top" + nl);
		file.write("set device " + project.getBoard().getFPGAName() + nl);
		file.write("if {[file exists \"$projDir" + ps + "$projName\"]} { file delete -force \"$projDir" + ps + "$projName\" }" + nl);
		file.write("create_project $projName \"$projDir" + ps + "$projName\" -part $device" + nl);
		// file.write("set_property source_mgmt_mode None [current_project]" + nl);
		file.write("set_property design_mode RTL [get_filesets sources_1]" + nl);
		file.write("set verilogSources [list " + getSpacedList(vFiles, srcFolder.replace("\\", "/") + ps) + "]" + nl);
		file.write("import_files -fileset [get_filesets sources_1] -force -norecurse $verilogSources" + nl);
		file.write("set ucfSources [list ");
		HashSet<String> localUcf = project.getConstraintFiles(false);
		if (localUcf.size() > 0)
			file.write(getSpacedList(project.getConstraintFiles(false), project.getConstraintFolder().replace("\\", "/").replace(" ", "\\ ") + ps));
		HashSet<String> libUcf = project.getConstraintFiles(true);
		if (libUcf.size() > 0)
			file.write(" " + getSpacedList(project.getConstraintFiles(true), Locations.COMPONENTS.replace("\\", "/").replace(" ", "\\ ") + ps));
		file.write("]" + nl);
		file.write("import_files -fileset [get_filesets constrs_1] -force -norecurse $ucfSources" + nl);
		if (project.getIPCores().size() > 0) {
			file.write(
					"set coreSources [list " + getSpacedListofCores(project.getIPCores(), project.getIPCoreFolder().replace("\\", "/").replace(" ", "\\ ") + ps) + "]" + nl);
			file.write("import_files -fileset [get_filesets sources_1] -force -norecurse $coreSources" + nl);
		}
		// file.write("set_property top " + project.getTop().split("\\.")[0] + " [get_property srcset [current_run]]" + nl);
		file.write("set_property -name {steps.bitgen.args.More Options} -value {-g Binary:Yes -g Compress} -objects [get_runs impl_1]" + nl);
		file.write("set_property steps.map.args.mt on [get_runs impl_1]" + nl);
		file.write("set_property steps.map.args.pr b [get_runs impl_1]" + nl);
		file.write("set_property steps.par.args.mt on [get_runs impl_1]" + nl);
		file.write("update_compile_order -fileset sources_1" + nl);
		file.write("launch_runs -runs synth_1" + nl);
		file.write("wait_on_run synth_1" + nl);
		file.write("launch_runs -runs impl_1" + nl);
		file.write("wait_on_run impl_1" + nl);
		file.write("launch_runs impl_1 -to_step Bitgen" + nl);
		file.write("wait_on_run impl_1" + nl);

		return true;
	}
}
