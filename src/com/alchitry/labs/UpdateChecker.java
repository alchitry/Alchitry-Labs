package com.alchitry.labs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alchitry.labs.gui.FileDownloaderDialog;
import com.alchitry.labs.gui.main.MainWindow;

public class UpdateChecker {
	private static final String BASE_URL = "https://cdn.alchitry.com/labs/";
	private static final String LIB_VERSION_URL = BASE_URL + "libVersion";
	private static final String IDE_VERSION_URL = BASE_URL + "ideVersion";

	private UpdateChecker() {

	}

	public static void checkForUpdates() {
		if (Util.getEnvType() != Util.ECLIPSE)
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (checkForIDEUpdates())
						return;
					if (checkForLibUpdates())
						return;
				}
			}).start();
	}

	private static class DownloadResult {
		boolean status;

		public void setStatus(boolean status) {
			this.status = status;
		}

		public boolean getStatus() {
			return status;
		}
	}

	public static boolean copyLibrary(String from) throws IOException {
		File srcBaseFile = new File(from);
		File srcLibFile = new File(from + "library");
		File dstBaseFile = Locations.LIBRARY;

		if (!Util.askQuestion("Update Library", "All files in " + dstBaseFile + " will be replaced with the new library files.\n\nContinue?"))
			return false;

		if (dstBaseFile.exists())
			FileUtils.deleteDirectory(dstBaseFile);

		FileUtils.copyDirectory(srcLibFile, dstBaseFile);
		FileUtils.deleteDirectory(srcBaseFile);
		return true;
	}

	public static boolean checkForLibUpdates() {
		InputStream in;
		try {
			in = new URL(LIB_VERSION_URL).openStream();
		} catch (IOException e) {
			return false;
		}

		String libVersion = null;

		try {
			libVersion = IOUtils.toString(in);

		} catch (IOException e) {
			Util.showError("Error while checking for update", e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
		}

		if (libVersion == null)
			return false;

		String curVersion = Settings.pref.get(Settings.LIB_VERSION, MainWindow.LIB_VERSION);
		if (!libVersion.equals(curVersion)) {
			boolean result = Util.askQuestion("New Library Version Avaiable", "Would you like to update the library of projects and components now?");
			if (result) {
				URL website;
				try {
					website = new URL(BASE_URL + "/lib-" + libVersion + ".zip");
				} catch (MalformedURLException e) {
					Util.showError("Failed to open URL.");
					return false;
				}
				Path tempDir;
				try {
					tempDir = Files.createTempDirectory("alchitry_lib_");
				} catch (IOException e1) {
					Util.showError("Could not create temporary directory");
					return false;
				}
				try {
					String stringPath = tempDir.toString();
					if (!stringPath.endsWith(File.separator))
						stringPath += File.separator;

					File libZip = new File(stringPath + "lib.zip");
					if (libZip.exists())
						libZip.delete();

					final URL site = website;
					final String dlFile = libZip.getPath();
					final DownloadResult dlRes = new DownloadResult();

					Util.syncExec(new Runnable() {
						@Override
						public void run() {
							FileDownloaderDialog downloader = new FileDownloaderDialog(Util.getShell(), site, dlFile, "Update");
							dlRes.setStatus(downloader.open());
						}
					});

					if (!dlRes.getStatus())
						throw new IOException();

					ExtractUtility.unzip(stringPath + "lib.zip", stringPath);
					boolean success = false;

					if (Util.isWindows) {
						ProcessBuilder pb = new ProcessBuilder("elevate.exe", "alchitry-labs.exe", "-u", stringPath);
						Util.log.log(Level.ALL, pb.toString());
						final Process p;
						try {
							p = pb.start();
						} catch (Exception e) {
							Util.log.severe(ExceptionUtils.getStackTrace(e));
							return false;
						}
						new Thread() {
							public void run() {
								BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
								String line;
								try {
									while ((line = br.readLine()) != null) {
										Util.log.severe(line);
									}
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									try {
										br.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}.start();

						new Thread() {
							public void run() {
								BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
								String line;
								try {
									while ((line = br.readLine()) != null) {
										Util.log.severe(line);
									}
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									try {
										br.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}.start();
						try {
							int ret = p.waitFor();
							if (ret != 0) {
								Util.showError("Failed to copy the library files! Exit code: " + ret);
								success = false;
							} else {
								success = true;
							}
						} catch (InterruptedException e) {
							Util.log.severe("Interrupted while waiting for copy program");
						}
					} else {
						success = copyLibrary(stringPath);
					}

					if (success) {
						Settings.pref.put(Settings.LIB_VERSION, libVersion);
						try {
							Settings.pref.flush();
						} catch (BackingStoreException e) {
						}

						Util.showInfo("Success", "The library has successfully been updated.");
					}

					return success;
				} catch (IOException e) {
					Util.showError("Failed to download new version.\n\n" + e.getMessage());
				} finally {
					if (Util.isLinux)
						try {
							FileUtils.deleteDirectory(tempDir.toFile());
						} catch (IOException e) {
						}
				}
			}
		}
		return false;
	}

	private static void copyDirectory(File src, File dst) throws IOException {
		if (!dst.exists())
			if (!dst.mkdir())
				throw new IOException("Failed to make directory " + dst.getPath());
		for (final File entry : src.listFiles()) {
			if (entry.isDirectory()) {
				File d = new File(dst.getPath() + File.separator + entry.getName());
				if (!d.mkdir())
					throw new IOException("Failed to make directory " + d.getPath());
				copyDirectory(entry, d);
			} else if (entry.isFile()) {
				copyFileToDirectory(entry, dst);
			}
		}
	}

	private static void copyFileToDirectory(File src, File dst) throws IOException {
		Path s = Paths.get(src.getPath());
		Path d = Paths.get(dst.getPath() + File.separator + src.getName());
		Files.copy(s, d, StandardCopyOption.COPY_ATTRIBUTES);
	}

	public static boolean checkForIDEUpdates() {
		InputStream in;
		try {
			in = new URL(IDE_VERSION_URL).openStream();
		} catch (IOException e) {
			return false;
		}

		String version = null;

		try {
			version = IOUtils.toString(in);

		} catch (IOException e) {
			Util.showError("Error while checking for update", e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
		}

		if (version == null)
			return false;

		String curVersion = Settings.pref.get(Settings.VERSION, "");
		if (!version.equals(curVersion)) {
			boolean result = Util.askQuestion("New Alchitry Labs Avaiable", "Would you like to update the IDE to version " + version + " now?");
			if (result) {
				URL website;
				try {
					switch (Util.getEnvType()) {
					case Util.LIN64:
						website = new URL(BASE_URL+"alchitry-labs-" + version + "-linux.tgz");
						break;
					case Util.WIN64:
						website = new URL(BASE_URL+"alchitry-labs-" + version + "-windows.msi");
						break;
					default:
						Util.showError("Unknown IDE Type");
						return false;
					}
				} catch (MalformedURLException e) {
					Util.showError("Failed to open URL.");
					return false;
				}
				ReadableByteChannel rbc = null;
				FileOutputStream fos = null;
				Path tempDir;
				try {
					if (Util.getEnvType() == Util.LIN32 || Util.getEnvType() == Util.LIN64) {
						tempDir = Files.createTempDirectory("alchitry_labs_");
					} else {
						tempDir = Paths.get(Util.getWorkspace());
						File f = tempDir.toFile();
						if (!f.exists()) {
							Files.createDirectories(tempDir);
						}
					}
				} catch (IOException e1) {
					Util.showError("Could not create temporary directory");
					return false;
				}
				try {
					String stringPath = tempDir.toString();
					if (!stringPath.endsWith(File.separator))
						stringPath += File.separator;

					String arcName = null;

					switch (Util.getEnvType()) {
					case Util.LIN32:
					case Util.LIN64:
						arcName = "alchitry-labs.tgz";
						break;
					case Util.WIN32:
					case Util.WIN64:
						arcName = "alchitry-labs.msi";
						break;
					}

					File libZip = new File(stringPath + arcName);
					if (libZip.exists())
						libZip.delete();

					final URL site = website;
					final String dlFile = libZip.getPath();
					final DownloadResult dlRes = new DownloadResult();

					Util.syncExec(new Runnable() {
						@Override
						public void run() {
							FileDownloaderDialog downloader = new FileDownloaderDialog(Util.getShell(), site, dlFile, "Update");
							dlRes.setStatus(downloader.open());
						}
					});

					if (!dlRes.getStatus())
						throw new IOException();

					String cmd[] = {};

					if (Util.getEnvType() == Util.LIN32 || Util.getEnvType() == Util.LIN64) {
						ExtractUtility.untargz(libZip.getPath(), stringPath);

						String ideFolder = null;
						File dstDir = Locations.progDir;

						int matched = 0;

						for (File f : dstDir.listFiles()) {
							switch (f.getName()) {
							case "lib":
							case "library":
							case "alchitry-labs":
								matched++;
							}
						}

						if (matched != 3) {
							if (!Util.askQuestion("Unknown Install Location",
									"The directory " + dstDir.getPath() + " does not seem to be a valid Alchitry Labs install. Continue anyways?"))
								return false;
						}

						for (final File entry : new File(stringPath).listFiles()) {
							if (entry.isDirectory() && entry.getName().startsWith("alchitry-labs")) {
								ideFolder = entry.getName();
								break;
							}
						}

						if (ideFolder == null) {
							Util.showError("Could not find IDE directory");
							return false;
						}

						if (!Util.askQuestion("Continue with Update?",
								"The files in " + dstDir.getPath() + " will all be deleted and replaced with the new Alchitry Labs files.\n\nContinue?"))
							return false;

						// Flush the folder
						for (final File entry : dstDir.listFiles()) {
							if (entry.isDirectory())
								FileUtils.deleteDirectory(entry);
							else
								entry.delete();
						}

						for (final File entry : new File(stringPath + ideFolder).listFiles()) {
							File dest = new File(entry.getName());
							if (entry.isDirectory())
								copyDirectory(entry, dest);
							else
								copyFileToDirectory(entry, dstDir);
						}

						cmd = new String[] { "nohup", Locations.progPrefix + "alchitry-labs" };
					} else { // windows
						cmd = new String[] { "msiexec.exe", "/i", libZip.getAbsolutePath() };
					}

					MainWindow.mainWindow.close();

					ProcessBuilder pb = new ProcessBuilder(cmd);
					try {
						pb.start();
					} catch (Exception e) {
						Util.log.severe(ExceptionUtils.getStackTrace(e));
					}

					return true;
				} catch (IOException e) {
					Util.log.severe(ExceptionUtils.getStackTrace(e));
					Util.showError("Failed to download new version.");
				} finally {
					IOUtils.closeQuietly(rbc);
					IOUtils.closeQuietly(fos);
					if (Util.getEnvType() == Util.LIN32 || Util.getEnvType() == Util.LIN64) {
						try {
							FileUtils.deleteDirectory(tempDir.toFile());
						} catch (IOException e) {
						}
					}
				}
			}

		}
		return false;
	}
}
