package com.alchitry.labs.project.builders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.project.IPCore;
import com.alchitry.labs.style.ParseException;

public class VivadoBuilder extends ProjectBuilder {
	private static final String projectFile = "project.tcl";
	private static final String projectDir = "vivado";

	public VivadoBuilder() {

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

		String vivado = Util.getVivadoLocation();

		if (vivado == null) {
			Util.log.severe("Couldn't find Vivado :(");
			Util.showError("Vivado's location must be set in the settings menu before you can build!");
			return;
		}

		ArrayList<String> cmd = new ArrayList<>();
		cmd.add(Util.assemblePath(vivado, "bin", Util.isWindows ? "vivado.bat" : "vivado"));
		cmd.add("-nojournal");
		cmd.add("-nolog");
		cmd.add("-mode");
		cmd.add("batch");
		cmd.add("-source");
		cmd.add(tclScript);

		builder = Util.runCommand(cmd);

		builder.waitFor();

		File binFile = new File(workFolder + File.separatorChar + projectDir + File.separatorChar + project.getProjectName() + File.separatorChar + project.getProjectName()
				+ ".runs" + File.separatorChar + "impl_1" + File.separatorChar + project.getTop().split("\\.")[0] + "_0.bin");
		if (binFile.exists()) {
			FileUtils.copyFile(binFile, new File(workFolder + File.separatorChar + "alchitry.bin"));
			Util.println("");
			Util.println("Finished building project.", Theme.successTextColor);
		} else {
			Util.println("");
			Util.println("Bin file (" + binFile.getAbsolutePath() + ") could not be found! The build probably failed.", true);
		}

	}

	private String getSpacedList(Collection<String> list, String prefix) {
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append("\"").append(prefix).append(s).append("\" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	//@formatter:off
	/*
	private String getSpacedListofCores(Collection<IPCore> list, String prefix) {
		StringBuilder builder = new StringBuilder();
		for (IPCore core : list)
			for (String s : core.getFiles()) {
				if (!s.endsWith(".v"))
					builder.append("\"").append(prefix).append(s).append("\" ");
			}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	*/
	//@formatter:on

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
		file.write("set_property design_mode RTL [get_filesets sources_1]" + nl);
		file.write("set verilogSources [list " + getSpacedList(vFiles, srcFolder.replace("\\", "/") + ps) + "]" + nl);
		file.write("import_files -fileset [get_filesets sources_1] -force -norecurse $verilogSources" + nl);
		file.write("set xdcSources [list ");
		HashSet<String> localConstraint = project.getConstraintFiles(false);
		if (localConstraint.size() > 0)
			file.write(getSpacedList(project.getConstraintFiles(false), project.getConstraintFolder().replace("\\", "/").replace(" ", "\\ ") + ps));
		HashSet<String> libConstraint = project.getConstraintFiles(true);
		if (libConstraint.size() > 0)
			file.write(" " + getSpacedList(project.getConstraintFiles(true), Locations.COMPONENTS.replace("\\", "/").replace(" ", "\\ ") + ps));
		file.write("]" + nl);
		file.write("read_xdc $xdcSources" + nl);
		if (project.getIPCores().size() > 0) {
			String prefix = project.getIPCoreFolder().replace("\\", "/").replace(" ", "\\ ") + ps;
			for (IPCore core : project.getIPCores())
				for (String s : core.getFiles()) {
					if (!s.endsWith(".v")) {
						file.write("import_files -fileset [get_filesets sources_1] [list \"" + prefix + s + "\" ]" + nl);
					} else if (s.endsWith(".xcix")) {
						file.write("import_ip -srcset [get_filesets sources_1] [list \"" + prefix + s + "\"]" + nl);
					}
				}
		}
		file.write("set_property STEPS.WRITE_BITSTREAM.ARGS.BIN_FILE true [get_runs impl_1]" + nl);
		file.write("update_compile_order -fileset sources_1" + nl);

		file.write("launch_runs -runs synth_1 -jobs 8" + nl);
		file.write("wait_on_run synth_1" + nl);
		file.write("launch_runs impl_1 -to_step write_bitstream -jobs 8" + nl);
		file.write("wait_on_run impl_1" + nl);

		return true;
	}
}
