package com.alchitry.labs.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;

public class VivadoIP {
	private static final String projectFile = "project.tcl";
	private static final String migFile = "mig_ip.tcl";
	private Process process;
	private Thread thread;

	public VivadoIP() {
	}

	public boolean isRunning() {
		return process != null && process.isAlive();
	}

	private boolean projectExists(Project project) {
		String projectPath = Util.assemblePath(project.getProjectFolder(), Project.CORES_FOLDER, "managed_ip_project", "managed_ip_project.xpr");
		File projFile = new File(projectPath);
		return projFile.exists();
	}

	private void createProject(Project project) throws InterruptedException, IOException {
		BufferedWriter out = null;
		String coresFolder = Util.assemblePath(project.getProjectFolder(), Project.CORES_FOLDER);
		String tclScript = Util.assemblePath(coresFolder, projectFile);

		File coresDir = new File(coresFolder);

		if (!coresDir.exists()) {
			if (!coresDir.mkdir()) {
				Util.println("Failed to create cores directory (" + coresDir.getAbsolutePath() + ")", true);
				return;
			}
		}

		try {
			FileWriter fstream = new FileWriter(tclScript);
			out = new BufferedWriter(fstream);

			generateProjectFile(out, coresDir.getAbsolutePath().replace("\\", "/"));

		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					Util.log.log(Level.SEVERE, "Failed to close stdout stream!", e);
				}
		}

		runTclScript(tclScript);
	}

	private void generateProjectFile(BufferedWriter file, String coresDir) throws IOException {
		final String nl = System.lineSeparator();
		file.write("cd \"" + coresDir + "\"" + nl);
		file.write("create_project managed_ip_project \"");
		file.write(Util.assembleLinuxPath(coresDir, "managed_ip_project"));
		file.write("\" -part xc7a35tftg256-1 -ip");
	}

	private void openProject(Project project) throws InterruptedException {
		String coresFolder = Util.assemblePath(project.getProjectFolder(), Project.CORES_FOLDER);
		String projectFile = Util.assemblePath(coresFolder, "managed_ip_project", "managed_ip_project.xpr");
		File p = new File(projectFile);
		if (!p.exists()) {
			Util.showError("Cores  project files does not exist!");
			return;
		}

		String vivado = Util.getVivadoLocation();

		if (vivado == null) {
			Util.log.severe("Couldn't find Vivado :(");
			Util.showError("Vivado's location must be set in the settings menu before you can open an IP core!");
			return;
		}

		ArrayList<String> cmd = new ArrayList<>();
		cmd.add(Util.assemblePath(vivado, "bin", Util.isWindows ? "vivado.bat" : "vivado"));
		cmd.add("-nojournal");
		cmd.add("-nolog");
		cmd.add(p.getAbsolutePath());

		process = Util.runCommand(cmd);

		process.waitFor();
	}

	private void runTclScript(String tclScriptPath) throws InterruptedException {
		String vivado = Util.getVivadoLocation();

		if (vivado == null) {
			Util.log.severe("Couldn't find Vivado :(");
			Util.showError("Vivado's location must be set in the settings menu before you can create IP cores!");
			return;
		}

		ArrayList<String> cmd = new ArrayList<>();
		cmd.add(Util.assemblePath(vivado, "bin", Util.isWindows ? "vivado.bat" : "vivado"));
		cmd.add("-nojournal");
		cmd.add("-nolog");
		cmd.add("-mode");
		cmd.add("batch");
		cmd.add("-source");
		cmd.add(tclScriptPath);

		Util.println(cmd.toString(), Theme.infoTextColor);

		process = Util.runCommand(cmd);

		process.waitFor();
	}

	public void launch(final Project project) {
		if (thread != null && thread.isAlive()) {
			Util.showError("Vivado is already running!");
			return;
		}

		thread = new Thread() {
			public void run() {
				try {
					Util.clearConsole();

					createIpProjectIfNeeded(project);

					Util.println("Launching Vivado...");
					openProject(project);

					Util.println("Vivado exited...");
					Util.println("Looking for new cores...");
					checkForNewCores(project);
				} catch (Exception e) {
					Util.print(e);
				}
			}
		};
		thread.start();
	}

	private void createIpProjectIfNeeded(final Project project) throws InterruptedException, IOException {
		if (!projectExists(project)) {
			Util.println("Creating IP project...");
			createProject(project);
		}
	}

	private static ArrayList<String> blackList = new ArrayList<String>(
			Arrays.asList(new String[] { "managed_ip_project", ".Xil", "ip_user_files", "project.tcl", "mig_ip.tcl" }));

	private File findPattern(File dir, String regex, boolean isFile) {
		for (File stub : dir.listFiles()) {
			if (isFile == stub.isFile() && stub.getName().matches(regex)) {
				return stub;
			}
			if (stub.isDirectory()) {
				File rStub = findPattern(stub, regex, isFile);
				if (rStub != null)
					return rStub;
			}

		}
		return null;
	}

	public void generateMigCore(final Project project) throws InterruptedException, IOException {
		String tclScript = Util.assemblePath(project.getProjectFolder(), Project.CORES_FOLDER, migFile);

		String coresFolder = Util.assembleLinuxPath(project.getProjectFolder(), Project.CORES_FOLDER);
		String projectFile = Util.assembleLinuxPath(coresFolder, "managed_ip_project", "managed_ip_project.xpr");
		String xciFile = Util.assembleLinuxPath(coresFolder, "mig_7series_0", "mig_7series_0.xci").replace(" ", "\\ ");
		String migPrjFile = Util.assemblePath(Locations.COMPONENTS, "mig.prj").replace("\\", "/");

		final String nl = System.lineSeparator();

		if (thread != null && thread.isAlive()) {
			Util.showError("Vivado is already running!");
			return;
		}

		thread = new Thread() {
			public void run() {
				try {
					Util.clearConsole();
					createIpProjectIfNeeded(project);

					try (FileWriter fstream = new FileWriter(tclScript)) {
						try (BufferedWriter out = new BufferedWriter(fstream)) {
							out.write("open_project {" + projectFile + "}" + nl);
							out.write("create_ip -name mig_7series -vendor xilinx.com -library ip -version 4.1 -module_name mig_7series_0 -dir {" + coresFolder + "}" + nl);
							out.write("set_property -dict [list CONFIG.XML_INPUT_FILE {" + migPrjFile
									+ "} CONFIG.RESET_BOARD_INTERFACE {Custom} CONFIG.MIG_DONT_TOUCH_PARAM {Custom} CONFIG.BOARD_MIG_PARAM {Custom}] [get_ips mig_7series_0]"
									+ nl);
							out.write("generate_target all [get_files {" + xciFile + "}]" + nl);
							out.write("catch { config_ip_cache -export [get_ips -all mig_7series_0] }" + nl);
							out.write("export_ip_user_files -of_objects [get_files {" + xciFile + "}] -no_script -sync -force -quiet" + nl);
							out.write("create_ip_run [get_files -of_objects [get_fileset sources_1] {" + xciFile + "}]" + nl);
							out.write("launch_runs -jobs 16 mig_7series_0_synth_1" + nl);
							out.write("wait_on_run mig_7series_0_synth_1" + nl);
							out.write("export_simulation -of_objects [get_files {" + xciFile + "}] -directory {"
									+ Util.assembleLinuxPath(coresFolder, "ip_user_files/sim_scripts}") + " -ip_user_files_dir {"
									+ Util.assembleLinuxPath(coresFolder, "ip_user_files") + "} -ipstatic_source_dir {"
									+ Util.assembleLinuxPath(coresFolder, "ip_user_files/ipstatic") + "} -lib_map_path [list {modelsim="
									+ Util.assembleLinuxPath(coresFolder, "managed_ip_project/managed_ip_project.cache/compile_simlib/modelsim") + "} {questa="
									+ Util.assembleLinuxPath(coresFolder, "managed_ip_project/managed_ip_project.cache/compile_simlib/questa") + "} {ies="
									+ Util.assembleLinuxPath(coresFolder, "managed_ip_project/managed_ip_project.cache/compile_simlib/ies") + "} {xcelium="
									+ Util.assembleLinuxPath(coresFolder, "managed_ip_project/managed_ip_project.cache/compile_simlib/xcelium") + "} {vcs="
									+ Util.assembleLinuxPath(coresFolder, "managed_ip_project/managed_ip_project.cache/compile_simlib/vcs") + "} {riviera="
									+ Util.assembleLinuxPath(coresFolder, "managed_ip_project/managed_ip_project.cache/compile_simlib/riviera")
									+ "}] -use_ip_compiled_libs -force -quiet" + nl);
						}
					}

					Util.println("Running TCL script...", Theme.infoTextColor);

					runTclScript(tclScript);

					Util.println("Looking for new cores...", Theme.infoTextColor);
					checkForNewCores(project);
				} catch (Exception e) {
					Util.print(e);
				}
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
				}

				Util.println("Done.", Theme.successTextColor);
			}
		};
		thread.start();
	}

	private void checkForNewCores(final Project project) {
		project.removeAllIPCores();
		File coresDir = new File(Util.assemblePath(project.getProjectFolder(), Project.CORES_FOLDER));
		for (File dir : coresDir.listFiles()) {
			if (blackList.contains(dir.getName()))
				continue;
			if (!dir.isDirectory()) {
				continue;
			}
			Util.println("Looking in " + dir.getName());
			final IPCore core = new IPCore(dir.getName());

			File xciFile = findPattern(dir, ".*\\.xci", true);

			if (xciFile != null) {
				Util.println("  Found core .xci file!");
				core.addFile(xciFile);

				File stub = findPattern(dir, ".*_stub\\.v", true);
				if (stub != null) {
					core.setStub(stub);
					Util.println("  Found stub file!");
				} else {
					Util.println("  Could not find stub file! Did you let synthesis finish before closing Vivado?", true);
				}
				Util.syncExec(new Runnable() {
					@Override
					public void run() {
						project.addIPCore(core);
					}
				});
			} else {
				Util.println("  Could not find a .xci file!", true);
			}
		}
		Util.asyncExec(new Runnable() {
			@Override
			public void run() {
				project.updateTree();
				try {
					project.saveXML();
				} catch (IOException e) {
					Util.showError("Failed to save project file!");
				}
			}
		});
	}

	public void kill() {
		if (process != null)
			process.destroy();
	}
}