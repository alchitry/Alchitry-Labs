package com.alchitry.labs.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.MainWindow;
import com.alchitry.labs.gui.SignalSelectionDialog;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.language.InstModule;
import com.alchitry.labs.language.Module;
import com.alchitry.labs.language.ProjectSignal;
import com.alchitry.labs.lucid.toVerilog.LucidToVerilog;
import com.alchitry.labs.lucid.tools.LucidDebugModifier;
import com.alchitry.labs.lucid.tools.LucidErrorChecker;
import com.alchitry.labs.style.ParseException;
import com.alchitry.labs.style.SyntaxError;
import com.alchitry.labs.verilog.tools.VerilogErrorChecker;
import com.alchitry.labs.verilog.tools.VerilogLucidModuleFixer;

public class ProjectBuilder {

	private static final String projectFile = "project.tcl";
	private static final String projectDir = "planAhead";

	private Project project;
	private String workFolder;
	private Thread thread;
	private Process builder;
	private ArrayList<ProjectSignal> debugSignals;
	private int samples;
	private List<File> debugSource;

	public ProjectBuilder() {

	}

	public void checkProject(Project project) {
		this.project = project;
		thread = new Thread() {
			public void run() {
				Util.clearConsole();
				try {
					if (!checkForErrors()) {
						Util.println("No errors detected.");
					}
				} catch (IOException e) {
				}
			}
		};
		thread.start();
	}

	public void buildProject(Project openProject, final boolean debug) {
		project = openProject;

		if (Util.isGUI) {
			thread = new Thread() {
				public void run() {
					try {
						projectBuilder(debug);
					} catch (Exception e) {
						Util.print(e);
						Util.log.log(Level.SEVERE, "Exception with project builder!", e);
					}
				}
			};

			thread.start();
		} else {
			projectBuilder(debug);
		}

	}

	private void projectBuilder(final boolean debug) {
		MainWindow.mainWindow.setBuilding(true);
		BufferedWriter out = null;
		try {
			Util.clearConsole();
			InstModule top = null;
			if (debug) {
				if (!Util.isGUI) {
					Util.showError("Debug builds only work in GUI mode!");
					return;
				}

				final InstModule ftop = getLucidSourceTree(project);
				top = ftop;
				boolean hasRegInt = false;
				boolean hasDebugRegInt = false;
				for (InstModule im : ftop.getChildren()) {
					if (im.getType().getName().equals("reg_interface"))
						hasRegInt = true;
					else if (im.getType().getName().equals("reg_interface_debug"))
						hasDebugRegInt = true;
				}
				if (hasDebugRegInt) {
					Util.showError("Your project can't contain the reg_interface_debug module!");
					return;
				}
				if (!hasRegInt) {
					Util.showError("Your project must contain the reg_interface module in mojo_top!");
					return;
				}
				Util.syncExec(new Runnable() {
					@Override
					public void run() {
						SignalSelectionDialog dialog = new SignalSelectionDialog(MainWindow.mainWindow.getShell());
						debugSignals = dialog.open(ftop);
						samples = dialog.getSamples();
					}
				});

				if (debugSignals == null)
					return;
			}

			workFolder = project.getFolder() + File.separatorChar + "work";
			File destDir = new File(workFolder);
			if (destDir.exists())
				FileUtils.deleteDirectory(destDir);
			if (!destDir.exists() || !destDir.isDirectory()) {
				boolean success = destDir.mkdir();
				if (!success) {
					Util.showError("Could not create project folder!");
					return;
				}
			}

			if (checkForErrors()) {
				return;
			}

			String tclScript = workFolder + File.separatorChar + projectFile;
			FileWriter fstream = new FileWriter(tclScript);
			out = new BufferedWriter(fstream);

			if (debug) {
				File debugDir = new File(workFolder + File.separatorChar + "debug");
				if (!debugDir.exists() || !debugDir.isDirectory()) {
					boolean success = debugDir.mkdir();
					if (!success) {
						Util.showError("Could not create debug folder!");
						return;
					}
				}

				if ((debugSource = createDebugFiles(debugDir, top)) == null)
					return;
			} else {
				debugSignals = null;
				debugSource = null;
			}

			if (!generateProjectFile(out))
				return;

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

			Thread inputPrinter = new Thread() {

				public void run() {
					BufferedReader inputStream = new BufferedReader(new InputStreamReader(builder.getInputStream()));
					String line;
					try {
						while ((line = inputStream.readLine()) != null) {
							Util.println(line, false);
						}
					} catch (IOException e) {
						Util.log.log(Level.INFO, "Exception with project builder input stream", e);
					} finally {
						try {
							inputStream.close();
						} catch (IOException e) {
						}
					}
				}
			};
			inputPrinter.start();

			Thread errorPrinter = new Thread() {
				public void run() {
					BufferedReader errorStream = new BufferedReader(new InputStreamReader(builder.getErrorStream()));
					String line;
					try {
						while ((line = errorStream.readLine()) != null) {
							Util.println(line, true);
						}
					} catch (IOException e) {
						Util.log.log(Level.INFO, "Exception with project builder error stream", e);
					} finally {
						try {
							errorStream.close();
						} catch (IOException e) {
						}
					}
				}
			};
			errorPrinter.start();
			builder.waitFor();

			Util.println("");
			Util.println("Finished building project.");

		} catch (Exception e) {
			Util.print(e);
			Util.log.log(Level.SEVERE, "Exception with project builder!", e);
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					Util.log.log(Level.SEVERE, "Failed to close stdout stream!", e);
				}
			MainWindow.mainWindow.setBuilding(false);
		}

	}

	public boolean isBuilding() {
		return thread != null && thread.isAlive();
	}

	public void stopBuild() {
		if (builder != null && isBuilding()) {
			try {
				builder.getOutputStream().close();
				builder.getInputStream().close();
				builder.getErrorStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			builder.destroyForcibly();
			Util.println("");
			Util.println("Build aborted by user.", true);
		}
	}

	private void addError(final String text, final int type) {
		Color color = Theme.editorForegroundColor;
		switch (type) {
		case SyntaxError.ERROR:
			color = Theme.errorTextColor;
			break;
		case SyntaxError.WARNING:
			color = Theme.warningTextColor;
			break;
		case SyntaxError.INFO:
			color = Theme.infoTextColor;
			break;
		case SyntaxError.DEBUG:
			color = Theme.debugTextColor;
			break;
		}
		Util.print(text, color);
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

		file.close();
		return true;
	}

	private String getVerilogFile(String folder, String file, String srcFolder, List<Module> modules, InstModule im, List<InstModule> list) throws IOException {
		String vName;
		File src = new File(folder + File.separatorChar + file);
		if (im.getType().isNgc()) {
			vName = file;
			File dest = new File(srcFolder + File.separatorChar + file);

			if (!dest.exists() && !dest.createNewFile()) {
				Util.showError("Error building the project", "Impossible error? File exists but doesn't!");
				return null;
			}
			FileUtils.copyFile(new File(folder + File.separatorChar + file), dest);
		} else if (file.endsWith(".luc")) {
			vName = file.substring(0, file.length() - 4) + "_" + list.indexOf(im) + ".v";
			File dest = new File(srcFolder + File.separatorChar + vName);

			if (!dest.exists() && !dest.createNewFile()) {
				Util.showError("Error building the project", "Impossible error? File exists but doesn't!");
				return null;
			}

			String verilog = LucidToVerilog.convert(src.getAbsolutePath(), modules, im, list);
			FileUtils.writeStringToFile(dest, verilog);
		} else if (file.endsWith(".v")) {
			vName = file.substring(0, file.length() - 2) + "_" + list.indexOf(im) + ".v";
			File dest = new File(srcFolder + File.separatorChar + vName);

			String verilog = VerilogLucidModuleFixer.replaceModuleNames(im, src.getAbsolutePath(), modules, list);

			FileUtils.writeStringToFile(dest, verilog);

		} else {
			Util.showError("Error building the project", "Unknown file type!");
			return null;
		}

		return vName;
	}

	private ArrayList<String> getVerilogFiles() throws IOException, ParseException {
		String srcFolder = workFolder + File.separatorChar + "verilog";
		ArrayList<String> verilogFiles = new ArrayList<String>();
		File destDir = new File(srcFolder);
		if (!destDir.exists() || !destDir.isDirectory()) {
			boolean success = destDir.mkdir();
			if (!success) {
				Util.showError("Error building the project", "Could not create source folder!");
				return null;
			}
		}

		// clean up old files
		for (File f : destDir.listFiles()) {
			f.delete();
		}

		project.updateGlobals();

		List<Module> modules = project.getModules(debugSource);
		Module topModule = null;
		for (Module m : modules) {
			if (Objects.equals(m.getFileName(), "mojo_top_0_debug.luc")) {
				topModule = m;
				break;
			}
		}
		List<InstModule> list = project.getModuleList(modules, true, topModule);

		for (InstModule im : list) {
			if (im.isPrimitive())
				continue;
			String folder = im.getType().getFolder();
			String file = im.getType().getFileName();
			String vFile = getVerilogFile(folder, file, srcFolder, modules, im, list);
			if (vFile != null) {
				verilogFiles.add(vFile);
				if (im.getType().isNgc()) {
					vFile = getVerilogFile(folder, file.substring(0, vFile.lastIndexOf(".")) + ".ngc", srcFolder, modules, im, list);
					verilogFiles.add(vFile);
				}
			}
		}

		// if (!addVerilogFiles(verilogFiles, project.getSourceFolder(), project.getSourceFiles(), srcFolder, modules))
		// return null;
		// if (!addVerilogFiles(verilogFiles, Locations.COMPONENTS, project.getComponentFiles(), srcFolder, modules))
		// return null;

		return verilogFiles;
	}

	private boolean checkforErrors(String folder, String file, boolean printErrors) throws IOException {
		boolean hasErrors = false;

		if (file.endsWith(".luc") || file.endsWith(".v")) {
			List<SyntaxError> errors = null;
			String fullPath = new File(folder + File.separatorChar + file).getAbsolutePath();
			if (file.endsWith(".luc")) {
				LucidErrorChecker errorChecker = new LucidErrorChecker(null);
				errors = errorChecker.getErrors(fullPath);
			} else if (file.endsWith(".v")) {
				VerilogErrorChecker errorChecker = new VerilogErrorChecker();
				errors = errorChecker.getErrors(fullPath);
			}

			List<SyntaxError> ge = project.getGlobalErrors(fullPath);

			if (ge != null)
				if (errors == null && ge.size() > 0)
					errors = ge;
				else
					errors.addAll(ge);
			hasErrors = addErrors(errors, file, printErrors);

		}
		return hasErrors;
	}

	private boolean addErrors(List<SyntaxError> errors, String file, boolean printErrors) {
		boolean hasErrors = false;
		boolean hasWarnings = false;
		boolean hasInfo = false;
		if (errors != null) {
			for (SyntaxError se : errors) {
				switch (se.type) {
				case SyntaxError.ERROR:
					hasErrors = true;
					break;
				case SyntaxError.WARNING:
					hasWarnings = true;
					break;
				case SyntaxError.INFO:
					hasInfo = true;
					break;
				}
			}

			if ((hasErrors || hasWarnings || hasInfo) && printErrors) {

				if (hasErrors)
					addError(String.format("Errors in file %s:%s", file, System.lineSeparator()), SyntaxError.ERROR);
				else if (hasWarnings)
					addError(String.format("Warnings in file %s:%s", file, System.lineSeparator()), SyntaxError.WARNING);
				else
					addError(String.format("Info in file %s:%s", file, System.lineSeparator()), SyntaxError.INFO);

				for (SyntaxError se : errors) {
					if (se.type != SyntaxError.DEBUG)
						addError(String.format("    Line %d, Column %d : %s%s", se.line, se.column, se.message, System.lineSeparator()), se.type);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
		return hasErrors;
	}

	private boolean checkForIMErrors(InstModule im, List<Module> modules, List<InstModule> instModules) {
		String fullPath = new File(im.getType().getFolder() + File.separatorChar + im.getType().getFileName()).getAbsolutePath();
		List<SyntaxError> errors = VerilogLucidModuleFixer.getErrors(im, fullPath, modules, instModules);
		return addErrors(errors, im.getType().getFileName(), true);
	}

	private boolean checkForErrors() throws IOException {
		project.updateGlobals();
		String folder = project.getSourceFolder();
		List<Module> modules = project.getModules(null);
		List<InstModule> list = project.getModuleList(modules, true, null);
		boolean hasErrors = false;
		for (String file : project.getSourceFiles()) {
			hasErrors = hasErrors | checkforErrors(folder, file, true);
		}
		for (InstModule im : list)
			if (!im.getType().isPrimitive() && im.getType().getFileName().endsWith(".v"))
				hasErrors = hasErrors | checkForIMErrors(im, modules, list);

		folder = Locations.COMPONENTS;
		for (String file : project.getComponentFiles(false)) {
			hasErrors = hasErrors | checkforErrors(folder, file, true);
		}

		return hasErrors;
	}

	public InstModule getLucidSourceTree(Project project) throws IOException {
		this.project = project;
		project.updateGlobals();
		String folder = project.getSourceFolder();
		List<Module> modules = project.getModules(null);
		List<InstModule> list = project.getModuleList(modules, false, null);

		for (String file : project.getSourceFiles())
			checkforErrors(folder, file, false);

		folder = Locations.COMPONENTS;
		for (String file : project.getComponentFiles(true))
			checkforErrors(folder, file, false);

		return list.get(0); // top level IM
	}

	public static class DebugFile {
		public File file;
		public String projectPath;
		public int index;
		public InstModule instModule;

		public DebugFile(InstModule im, File f, String ppath, int idx) {
			file = f;
			projectPath = ppath;
			index = idx;
			instModule = im;
		}

		@Override
		public int hashCode() {
			if (file != null && projectPath != null)
				return file.hashCode() ^ projectPath.hashCode();
			if (file != null)
				return file.hashCode();
			if (projectPath != null)
				return projectPath.hashCode();
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof DebugFile) {
				DebugFile df = (DebugFile) o;
				return Objects.equals(df.file, file) && Objects.equals(df.projectPath, projectPath);
			}
			return false;
		}
	}

	private List<File> createDebugFiles(File debugDir, InstModule topModule) {
		HashSet<DebugFile> debugFiles = new HashSet<>();
		List<File> debugSource = new ArrayList<>();
		int index = 0;
		for (ProjectSignal sig : debugSignals) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (InstModule im : sig.getPath()) {
				if (!first)
					sb.append(".");
				else
					first = false;
				sb.append(im.getName());
				if (debugFiles.add(new DebugFile(im, new File(im.getType().getFolder() + File.separator + im.getType().getFileName()), sb.toString(), index)))
					index++;
			}
		}
		long nonce = (long) (Math.random() * 0xffffffffL);
		for (DebugFile f : debugFiles) {
			String origName = f.file.getName();
			String newName = origName.substring(0, origName.length() - 4) + "_" + f.index + "_debug.luc"; // add index to name
			File destFile = new File(debugDir.getPath() + File.separator + newName);
			debugSource.add(destFile);

			String modifiedFile = LucidDebugModifier.modifyForDebug(f.file.getPath(), debugSignals, f.instModule, f.instModule == topModule, debugFiles, nonce, samples);
			try {
				FileUtils.write(destFile, modifiedFile);
			} catch (IOException e) {
				Util.showError("Failed to copy files for debugging!");
				Util.log.log(Level.SEVERE, "Failed to copy files for debugging", e);
				return null;
			}
		}

		project.setDebugInfo(new DebugInfo(debugSignals, nonce));

		return debugSource;
	}

}
