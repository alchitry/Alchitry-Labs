package com.alchitry.labs.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IPCore {
	private String name;
	private ArrayList<String> files;

	public IPCore(String name) {
		this.name = name;
		files = new ArrayList<>();
	}

	public void addFile(String file) {
		files.add(file);
	}

	public final ArrayList<String> getFiles() {
		return files;
	}

	public String getName() {
		return name;
	}

	public static boolean delete(String core, String projectFolder) {
		File coreGenFolder = new File(projectFolder + File.separatorChar + Project.CORES_FOLDER);
		File[] fileList = coreGenFolder.listFiles();
		if (fileList != null)
			for (File f : fileList) {
				String name = f.getName();
				if (name.startsWith(core)) {
					try {
						delete(f);
					} catch (IOException e) {
						return false;
					}
				}
			}
		return true;
	}

	private static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
			} else {
				// list all the directory contents
				String files[] = file.list();
				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);
					// recursive delete
					delete(fileDelete);
				}
				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}
		} else if (file.isFile()) {
			// if file, then delete it
			file.delete();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof IPCore) {
			if (((IPCore) o).name.equals(name))
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
