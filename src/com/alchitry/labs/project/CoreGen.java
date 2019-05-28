package com.alchitry.labs.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.boards.Board;

public class CoreGen {
	private static final String PROJECT_FILE = "coregen.cgc";

	private Thread thread;
	private Process process;

	public CoreGen() {
	}

	public boolean projectExists(Project project) {
		String projectPath = Util.assemblePath(project.getFolder(), Project.CORES_FOLDER, PROJECT_FILE);
		File projFile = new File(projectPath);
		return projFile.exists();
	}

	private boolean createProject(final Project project) {

		try {
			Util.println("Creating new CoreGen project");
			Util.println("");
			String xilinx = Util.getISELocation();

			if (xilinx == null) {
				Util.showError("ISE's location must be set in the settings menu before you can open CoreGen!");
				return false;
			}

			if (!xilinx.endsWith(File.separator))
				xilinx += File.separator;

			String coregen = xilinx + Environment.COREGEN_PATH;
			Board board = project.getBoard();
			String projSettingsFile = Util.assemblePath(Locations.BASE, board.getExampleProjectDir(), "coregen_prop");

			String coreGenDir = Util.assemblePath(project.getFolder(), Project.CORES_FOLDER);
			String coreGenProjFile = coreGenDir + File.separatorChar + PROJECT_FILE;
			File coreGenFile = new File(coreGenDir);
			if (!coreGenFile.exists())
				if (!coreGenFile.mkdir()) {
					Util.showError("Could not create coreGen file in project directory!");
					return false;
				}

			BufferedReader reader = new BufferedReader(new FileReader(new File(projSettingsFile)));

			File script = File.createTempFile("coregen_proj", ".cmd");
			BufferedWriter writer = new BufferedWriter(new FileWriter(script));
			writer.write("NEWPROJECT \"" + coreGenProjFile + "\"");
			writer.newLine();

			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}

			reader.close();

			writer.write("SET workingdirectory=\".\"");
			writer.close();

			ProcessBuilder pb = new ProcessBuilder(coregen, "-b", script.getAbsolutePath());

			try {
				process = pb.start();
			} catch (Exception e) {
				Util.showError("Could not start CoreGenerator! Please check the location for ISE is correctly set in the settings menu. Tried " + coregen);
				Util.log.severe(ExceptionUtils.getStackTrace(e));
				return false;
			}

			new Thread() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					try {
						while ((line = br.readLine()) != null) {
							Util.println(line, false);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

			new Thread() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					String line;
					try {
						while ((line = br.readLine()) != null) {
							Util.println(line, true);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

			process.waitFor();

			Util.println("");
			Util.println("Finished creating CoreGen project");

		} catch (InterruptedException e) {
			e.printStackTrace();
			Util.println(Util.exceptionToString(e), true);
			return false;
		} catch (IOException e1) {
			e1.printStackTrace();
			Util.println(Util.exceptionToString(e1), true);
			return false;
		}
		return true;
	}

	public void launch(final Project project) {
		if (thread != null && thread.isAlive()) {
			Util.showError("CoreGen is already running!");
			return;
		}

		thread = new Thread() {
			public void run() {

				if (!projectExists(project)) {
					if (!createProject(project))
						return;
				}

				final AtomicBoolean isOpen = new AtomicBoolean(true);
				try {
					String xilinx = Util.getISELocation();

					if (xilinx == null) {
						Util.showError("ISE's location must be set in the settings menu before you can build!");
						return;
					}

					if (!xilinx.endsWith(File.separator))
						xilinx += File.separator;

					String coregen = xilinx + Environment.COREGEN_PATH;

					String projectFile = Util.assemblePath(project.getFolder(), Project.CORES_FOLDER, "coregen.cgc");

					ProcessBuilder pb = new ProcessBuilder(coregen, "-q", Util.tmpDir.toString(), "-p", projectFile);
					// ProcessBuilder pb = new
					// ProcessBuilder(coregen,"-b","coregen.cmd","-d");

					// Util.showError("Coregen path is "+coregen);

					try {
						process = pb.start();
					} catch (Exception e) {
						Util.showError("Could not start CoreGenerator! Please check the location for ISE is correctly set in the settings menu.");
						return;
					}

					new Thread() {
						public void run() {
							BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String line;
							try {
								while ((line = br.readLine()) != null) {
									Util.print(line + System.lineSeparator(), false);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}.start();

					new Thread() {
						public void run() {
							BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							String line;
							try {
								while ((line = br.readLine()) != null) {
									Util.print(line + System.lineSeparator(), true);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}.start();

					new Thread() {
						public void run() {
							while (isOpen.get()) {
								try {
									sleep(500);
								} catch (InterruptedException e) {
								}
								checkForNewCores(project);
							}
							checkForNewCores(project);
						}
					}.start();

					process.waitFor();

					isOpen.set(false);

				} catch (InterruptedException e) {
					e.printStackTrace();
					Util.print(e.getMessage(), true);
				}
			}
		};

		thread.start();
	}

	private void checkForNewCores(final Project project) {
		File tmpFile = Util.tmpDir.toFile();
		File[] fList = tmpFile.listFiles();
		boolean updated = false;
		for (File f : fList) {
			if (f.isFile() && f.getName().endsWith(".fin")) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line1 = reader.readLine();
					String line2 = reader.readLine();
					if (line1 == null || line2 == null || !line2.equals("SUCCESS"))
						continue;
					String coreName = line1.split(" ")[0];
					final IPCore core = new IPCore(coreName);
					File coreFolder = project.getIPCoreFolder();
					for (File cf : coreFolder.listFiles()) {
						if (cf.isFile()) {
							String name = cf.getName();
							if (name.startsWith(coreName)) {
								if (name.endsWith(".v") || name.endsWith(".ngc")) {
									core.addFile(cf);
								}
							}
						}
					}
					updated = true;
					Util.syncExec(new Runnable() {
						@Override
						public void run() {
							project.addIPCore(core);
						}
					});
					reader.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				f.delete();
			}
		}
		if (updated)
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
