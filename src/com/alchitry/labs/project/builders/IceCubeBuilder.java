package com.alchitry.labs.project.builders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.ParseException;

public class IceCubeBuilder extends ProjectBuilder {
	private static final String SYN_PROJECT_FILE = "alchitry_syn.prj";
	private static final String TCL_SCRIPT = "iCEcube2_flow.tcl";
	private static final String BASH_SCRIPT = "build.sh";
	private static final String DOS_SCRIPT = "build.cmd";
	private static final String IMP_DIR = "alchitry_imp";

	@Override
	protected void projectBuilder() throws Exception {
		BufferedWriter out = null;
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

		if (Util.getIceCubeFolder() == null) {
			Util.log.severe("Couldn't find iCEcube2 :(");
			Util.showError("iCEcube2's location must be set in the settings menu before you can build!");
			return;
		}

		if (Util.getIceCubeLicenseFile() == null) {
			Util.log.severe("Couldn't find the license file for iCEcube2 :(");
			Util.showError("iCEcube2's license file's location must be set in the settings menu before you can build!");
			return;
		}

		String lseProjectFile = Util.assemblePath(workFolder, SYN_PROJECT_FILE);

		try {
			FileWriter fstream = new FileWriter(lseProjectFile);
			out = new BufferedWriter(fstream);

			generateSynProjectFile(out, vFiles, cFiles);
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

		String tclScript = Util.assemblePath(workFolder, TCL_SCRIPT);

		try {
			FileWriter fstream = new FileWriter(tclScript);
			out = new BufferedWriter(fstream);

			generateTclScript(out, cFiles);
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

		String bashScript = Util.assemblePath(workFolder, Util.isWindows ? DOS_SCRIPT : BASH_SCRIPT);

		try {
			FileWriter fstream = new FileWriter(bashScript);
			out = new BufferedWriter(fstream);

			generateBashScript(out);
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

		ArrayList<String> cmd = new ArrayList<>();
		if (Util.isLinux)
			cmd.add("bash");
		cmd.add(bashScript);

		builder = Util.runCommand(cmd);
		builder.waitFor();

		String topModuleName = project.getTop().getAbsolutePath().substring(0, project.getTop().getAbsolutePath().lastIndexOf('.')) + "_0";

		File binFile = new File(Util.assemblePath(workFolder, IMP_DIR, "sbt", "outputs", "bitmap", topModuleName + "_bitmap.bin"));
		if (binFile.exists()) {
			FileUtils.copyFile(binFile, new File(Util.assemblePath(workFolder, "alchitry.bin")));
			Util.println("");
			Util.println("Finished building project.", Theme.successTextColor);
		} else {
			Util.println("");
			Util.println("Bin file (" + binFile.getAbsolutePath() + ") could not be found! The build probably failed.", true);
		}
	}

	private void generateBashScript(BufferedWriter file) throws IOException {
		final String nl = System.lineSeparator();
		final String export = Util.isLinux ? "export" : "SET";

		if (Util.isLinux) {
			file.write("#!/bin/bash\n");

			file.write(export + " LD_LIBRARY_PATH=");
			file.write(Util.assemblePath(Util.getIceCubeFolder(), "sbt_backend", "bin", "linux", "opt", "synpwrap"));
			file.write(":$LD_LIBRARY_PATH");
			file.write(nl);
		} else {
			file.write(export + " TCL_LIBRARY=");
			file.write(Util.assemblePath(Util.getIceCubeFolder(), "Aldec", "Active-HDL", "tcl", "lib", "tcl"));
			file.write(nl);
		}

		file.write(export + " LM_LICENSE_FILE=");
		file.write(Util.getIceCubeLicenseFile());
		file.write(nl);

		file.write(export + " SYNPLIFY_PATH=");
		file.write(Util.assemblePath(Util.getIceCubeFolder(), "synpbase"));
		file.write(nl);

		file.write(export + " SBT_DIR=");
		file.write(Util.assemblePath(Util.getIceCubeFolder(), "sbt_backend"));
		file.write(nl);

		file.write(Util.assemblePath(Util.getIceCubeFolder(), "sbt_backend", "bin", (Util.isWindows ? "win32" : "linux"), "opt", "synpwrap",
				"synpwrap" + (Util.isWindows ? ".exe" : "")));
		file.write(" -prj \"");
		file.write(Util.assemblePath(workFolder, SYN_PROJECT_FILE));
		file.write("\" -log \"");
		file.write(Util.assemblePath(workFolder, "icelog.log"));
		file.write('"');
		file.write(nl);

		if (Util.isWindows)
			file.write(Util.assemblePath(Util.getIceCubeFolder(), "Aldec", "Active-HDL", "BIN", "tclsh85.exe") + " ");
		else
			file.write("tclsh ");
		file.write('"');
		file.write(Util.assemblePath(workFolder, TCL_SCRIPT));
		file.write('"');
		file.write(nl);
	}

	private void generateTclScript(BufferedWriter file, List<File> cFiles) throws IOException {
		final String nl = System.lineSeparator();
		String topModuleName = project.getTop().getName().substring(0, project.getTop().getName().lastIndexOf('.')) + "_0";

		file.write("#!usr/bin/tclsh8.4" + nl);
		file.write(nl);
		file.write("set device iCE40HX8K-CB132" + nl);
		file.write("set top_module " + topModuleName + nl);
		file.write("set proj_dir " + workFolder.getAbsolutePath().replace("\\", "/").replace(" ", "\\ ") + nl);
		file.write("set output_dir \"" + IMP_DIR + '"' + nl);
		file.write("set edif_file \"" + topModuleName + '"' + nl);
		file.write("set tool_options \":edifparser ");
		
		file.write("-y \\\"" );
		for (File cf : cFiles)
			if (cf.getName().endsWith(".pcf"))
				file.write(cf.getAbsolutePath().replace("\\", "/").replace(" ", "\\ ") + " ");
		file.write("\\\"\"\n");

		file.write("set sbt_root $::env(SBT_DIR)" + nl);
		file.write("append sbt_tcl $sbt_root \"/tcl/sbt_backend_synpl.tcl\"" + nl);
		file.write("source $sbt_tcl" + nl);
		file.write("run_sbt_backend_auto $device $top_module $proj_dir $output_dir $tool_options $edif_file" + nl);
		file.write("exit" + nl);

	}

	private void generateSynProjectFile(BufferedWriter file, List<File> vFiles, List<File> cFiles) throws IOException {
		final String nl = System.lineSeparator();
		final File srcFolder = Util.assembleFile(workFolder, "verilog");
		String topModuleName = project.getTop().getName().substring(0, project.getTop().getName().lastIndexOf('.')) + "_0";

		file.write("#project files" + nl);

		String prefix = srcFolder.getAbsolutePath().replace('\\', '/') + '/';

		for (File vf : vFiles)
			file.write("add_file -verilog -lib work \"" + prefix + vf.getAbsolutePath() + '"' + nl);
		for (File cf : cFiles)
			if (cf.getName().endsWith(".sdc"))
				file.write("add_file -constraint -lib work \"" + cf.getAbsolutePath().replace("\\", "/").replace(" ", "\\ ") + '"' + nl);

		file.write("#options" + nl);
		file.write("impl -add " + IMP_DIR + " -type fpga" + nl);
		file.write("set_option -vlog_std v2001" + nl);
		file.write("set_option -project_relative_includes 1" + nl);
		file.write("set_option -technology SBTiCE40" + nl);
		file.write("set_option -part iCE40HX8K" + nl);
		file.write("set_option -package CB132" + nl);
		file.write("set_option -speed_grade" + nl);
		file.write("set_option -part_companion \"\"" + nl);
		file.write("set_option -frequency auto" + nl);
		file.write("set_option -write_verilog 0" + nl);
		file.write("set_option -write_vhdl 0" + nl);
		file.write("set_option -maxfan 10000" + nl);
		file.write("set_option -disable_io_insertion 0" + nl);
		file.write("set_option -pipe 1" + nl);
		file.write("set_option -retiming 0" + nl);
		file.write("set_option -update_models_cp 0" + nl);
		file.write("set_option -fixgatedclocks 2" + nl);
		file.write("set_option -fixgeneratedclocks 0" + nl);
		file.write("set_option -popfeed 0" + nl);
		file.write("set_option -constprop 0" + nl);
		file.write("set_option -createhierarchy 0" + nl);
		file.write("set_option -symbolic_fsm_compiler 1" + nl);
		file.write("set_option -compiler_compatible 0" + nl);
		file.write("set_option -resource_sharing 1" + nl);
		file.write("set_option -write_apr_constraint 1" + nl);

		file.write("project -result_format \"edif\"" + nl);
		file.write("project -result_file ./" + IMP_DIR + "/" + topModuleName + ".edf" + nl);
		file.write("project -log_file \"./" + IMP_DIR + "/" + topModuleName + ".srr\"" + nl);

		file.write("impl -active " + IMP_DIR + nl);
		file.write("project -run synthesis -clean" + nl);
	}

}
