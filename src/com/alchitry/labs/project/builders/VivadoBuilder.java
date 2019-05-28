package com.alchitry.labs.project.builders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

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
		File tclScript = Util.assembleFile(workFolder, projectFile);

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
		cmd.add(tclScript.getAbsolutePath());

		Util.println("Starting Vivado...", Theme.infoTextColor);

		builder = Util.runCommand(cmd);

		builder.waitFor();

		Thread.sleep(150);

		File binFile = Util.assembleFile(workFolder, projectDir, project.getProjectName(), project.getProjectName() + ".runs", "impl_1",
				project.getTopModule().getName() + "_0.bin");
		if (binFile.exists()) {
			FileUtils.copyFile(binFile, Util.assembleFile(workFolder, "alchitry.bin"));
			Util.println("");
			Util.println("Finished building project.", Theme.successTextColor);
		} else {
			Util.println("");
			Util.println("Bin file (" + binFile.getAbsolutePath() + ") could not be found! The build probably failed.", true);
		}

	}

	private String getSpacedList(Collection<File> list) {
		StringBuilder builder = new StringBuilder();
		for (File s : list) {
			builder.append("\"").append(getSanitizedPath(s.getAbsolutePath())).append("\" ");
		}
		return builder.toString();
	}
	//@formatter:off
	/*
	private String getSpacedListofCores(Collection<IPCore> list) {
		StringBuilder builder = new StringBuilder();
		for (IPCore core : list)
			for (File s : core.getFiles()) {
				builder.append("\"").append(s.getAbsolutePath()).append("\" ");
			}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	*/
	//@formatter:on

	private String getSanitizedPath(File f) {
		return getSanitizedPath(f.getAbsolutePath());
	}

	private String getSanitizedPath(String f) {
		return f.replace("\\", "/").replace(" ", "\\ ");
	}

	private boolean generateProjectFile(BufferedWriter file) throws IOException {
		final String nl = System.lineSeparator();
		final String ps = "/"; // the tcl script expects / for all OS's

		ArrayList<File> vFiles;
		ArrayList<File> cFiles;
		try {
			vFiles = getVerilogFiles();
			cFiles = getConstraintFiles();
		} catch (ParseException e) {
			Util.println("Error: " + e.getMessage(), true);
			return false;
		}
		if (vFiles == null || vFiles.size() == 0) {
			Util.showError("Error building the project", "Error with getting list of Verilog files!");
			return false;
		}

		if (cFiles == null) {
			Util.showError("Error building the project", "Error with getting list of constraint files!");
			return false;
		}

		file.write("set projDir \"" + getSanitizedPath(workFolder) + ps + projectDir + "\"" + nl);
		file.write("set projName \"" + project.getProjectName() + "\"" + nl);
		file.write("set topName top" + nl);
		file.write("set device " + project.getBoard().getFPGAName() + nl);
		file.write("if {[file exists \"$projDir" + ps + "$projName\"]} { file delete -force \"$projDir" + ps + "$projName\" }" + nl);
		file.write("create_project $projName \"$projDir" + ps + "$projName\" -part $device" + nl);
		file.write("set_property design_mode RTL [get_filesets sources_1]" + nl);
		file.write("set verilogSources [list " + getSpacedList(vFiles) + "]" + nl);
		file.write("import_files -fileset [get_filesets sources_1] -force -norecurse $verilogSources" + nl);
		file.write("set xdcSources [list " + getSpacedList(cFiles) + "]" + nl);
		file.write("read_xdc $xdcSources" + nl);
		if (project.getIPCores().size() > 0) {
			for (IPCore core : project.getIPCores()) {
				for (File s : core.getFiles()) {
					file.write("import_ip -srcset [get_filesets sources_1] [list \"" + getSanitizedPath(s.getAbsolutePath()) + "\"]" + nl);
					/*
					if (s.getName().endsWith(".xcix"))
						file.write("import_ip -srcset [get_filesets sources_1] [list \"" + getSanitizedPath(s.getAbsolutePath()) + "\"]" + nl);
					else
						file.write("import_files -fileset [get_filesets sources_1] [list \"" + getSanitizedPath(s.getAbsolutePath()) + "\" ]" + nl);
						*/
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
