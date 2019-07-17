package com.alchitry.labs.project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Param;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.parsers.errors.AlchitryConstraintsErrorProvider;
import com.alchitry.labs.parsers.errors.LucidErrorProvider;
import com.alchitry.labs.parsers.errors.VerilogErrorProvider;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.tools.lucid.LucidExtractor;
import com.alchitry.labs.parsers.tools.lucid.LucidGlobalExtractor;
import com.alchitry.labs.parsers.tools.lucid.LucidModuleExtractor;
import com.alchitry.labs.parsers.tools.verilog.VerilogLucidModuleFixer;
import com.alchitry.labs.parsers.tools.verilog.VerilogModuleListener;
import com.alchitry.labs.parsers.types.Constant;
import com.alchitry.labs.parsers.types.Struct;
import com.alchitry.labs.project.Primitive.Parameter;
import com.alchitry.labs.project.Primitive.Port;
import com.alchitry.labs.project.builders.ProjectBuilder;
import com.alchitry.labs.style.ParseException;
import com.alchitry.labs.style.SyntaxError;
import com.alchitry.labs.widgets.CustomTree;
import com.alchitry.labs.widgets.CustomTree.TreeElement;
import com.alchitry.labs.widgets.CustomTree.TreeLeaf;
import com.alchitry.labs.widgets.CustomTree.TreeNode;
import com.alchitry.labs.widgets.TabChild;

public class Project {
	public static final int VERSION_ID = 2;

	public static final String SOURCE_PARENT = "Source";
	public static final String CONSTRAINTS_PARENT = "Constraints";
	public static final String COMPONENTS_PARENT = "Components";
	public static final String CORES_PARENT = "Cores";

	public static final String SOURCE_FOLDER = "source";
	public static final String CONSTRAINTS_FOLDER = "constraint";
	public static final String CORES_FOLDER = "cores";
	public static final String WORK_FOLDER = "work";

	public static final String LANG_LUCID = "Lucid";
	public static final String LANG_VERILOG = "Verilog";

	private HashSet<File> sourceFiles;
	private HashSet<File> constraintFiles;
	private HashSet<IPCore> ipCores;
	private HashSet<Primitive> primitives;

	private String language;

	private File topSource;
	private String projectName;
	private File projectFolder;
	private File projectFile;
	private Board boardType;
	private boolean open;
	private CustomTree tree;
	private Shell shell;
	private LucidGlobalExtractor globalExtractor = new LucidGlobalExtractor();
	private Menu treeMenu;
	private ProjectBuilder builder;
	private DebugInfo debugInfo;

	private Thread thread;

	private List<Listener> saveListeners = new ArrayList<>();

	private enum FileType {
		SOURCE, CONSTRAINT, COMPONENT, CORE
	};

	public Project(String name, File folder, String board, String lang) {
		this();
		projectName = name;
		projectFolder = folder;
		setBoardType(board);
		projectFile = new File(Util.assemblePath(folder.getAbsolutePath(), name + ".alp"));
		language = lang;
		open = true;

	}

	public Project() {
		this(null);
	}

	public Project(Shell shell) {
		this.shell = shell;
		sourceFiles = new HashSet<>();
		constraintFiles = new HashSet<>();
		ipCores = new HashSet<>();
		primitives = new HashSet<>();
		open = false;
	}

	public void setTree(CustomTree tree) {
		this.tree = tree;
		treeMenu = new Menu(shell, SWT.POP_UP);
		tree.setMenu(treeMenu);
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public void addControlListener(ControlListener listener) {
		tree.addControlListener(listener);
	}

	public void close() {
		open = false;
		sourceFiles.clear();
		constraintFiles.clear();
		ipCores.clear();
		primitives.clear();
		topSource = null;
		projectName = null;
	}

	public boolean isOpen() {
		return open;
	}

	public String getLanguage() {
		return language;
	}

	public File getFolder() {
		return projectFolder;
	}

	public void addSaveListener(Listener l) {
		saveListeners.add(l);
	}

	public void removeSaveListener(Listener l) {
		saveListeners.remove(l);
	}

	public String getBinFile() {
		File binFile = new File(Util.assemblePath(projectFolder.getAbsolutePath(), WORK_FOLDER, "alchitry.bin"));
		if (binFile.exists())
			return binFile.getAbsolutePath();
		return null;
	}

	private File createFile(File file, HashSet<File> list) {
		File parentFolder = file.getParentFile();
		try {
			if (!parentFolder.exists()) {
				if (!parentFolder.mkdir()) {
					Util.log.severe("Failed to create parent directory " + parentFolder.getAbsolutePath());
					return null;
				}
			}
			if (file.exists()) {
				if (!Util.askQuestion("File " + file.getName() + " exists. Overwrite?"))
					return null;

				file.delete();
			}
			if (file.createNewFile()) {
				if (!list.contains(file))
					list.add(file);
				updateTree();
				return file;
			}
		} catch (IOException e) {
			Util.log.severe(ExceptionUtils.getStackTrace(e));
			return null;
		}
		Util.log.severe("Could not open file " + file.getAbsolutePath());
		return null;
	}

	public boolean isLibFile(File file) {
		try {
			File libVersion = Util.assembleFile(Locations.COMPONENTS, file.getName());
			if (!libVersion.exists())
				return false;
			return Files.isSameFile(libVersion.toPath(), file.toPath());
		} catch (IOException e) {
			Util.log.log(Level.SEVERE, "", e);
			return false;
		}
	}

	private boolean removeFile(File file, HashSet<File> list) {
		if (topSource.equals(file)) {
			Util.showError("You can't delete the top file!");
			return true;
		}
		if (!isLibFile(file) && file.exists() && !file.delete()) {
			return false;
		}
		boolean ret = list.remove(file);
		updateTree();
		return ret;
	}

	public void removeAllIPCores() {
		ipCores.clear();
	}

	public boolean removeIPCore(String coreName) {
		if (IPCore.delete(coreName, projectFolder.getAbsolutePath())) {
			for (IPCore core : ipCores)
				if (core.getName().equals(coreName)) {
					ipCores.remove(core);
					break;
				}
			updateTree();
			return true;
		}
		return false;
	}

	public boolean renameFile(File file, File newFile) {
		if (isLibFile(file)) {
			Util.showError("Can't rename a library file!");
			return false;
		}
		if (constraintFiles.contains(file)) {
			constraintFiles.remove(file);
			try {
				FileUtils.copyFile(file, newFile);
				file.delete();
			} catch (IOException e) {
				Util.log.log(Level.SEVERE, "", e);
				Util.showError("Failed to rename file!");
			}
			constraintFiles.add(newFile);
			updateTree();
			return true;
		} else if (sourceFiles.contains(file)) {
			sourceFiles.remove(file);
			try {
				FileUtils.copyFile(file, newFile);
				file.delete();
			} catch (IOException e) {
				Util.log.log(Level.SEVERE, "", e);
				Util.showError("Failed to rename file!");
			}
			sourceFiles.add(newFile);
			updateTree();
			return true;
		}
		return false;
	}

	private boolean copyTemplate(File path, String fileName) {
		if (path.getName().endsWith(".luc") || path.getName().endsWith(".v")) {
			File template;
			if (path.getName().endsWith(".luc"))
				template = new File(Locations.TEMPLATE_DIR + File.separator + "module.luc");
			else
				template = new File(Locations.TEMPLATE_DIR + File.separator + "module.v");
			File dest = path;

			if (!template.exists() || !dest.exists()) {
				Util.println("Error: Could not open template or source file!", true);
				return false;
			}

			try {
				BufferedReader br = new BufferedReader(new FileReader(template));
				BufferedWriter bw = new BufferedWriter(new FileWriter(dest));

				String[] name = fileName.split("\\.");

				String line;
				while ((line = br.readLine()) != null) {
					bw.write(line.replace("%NAME%", name[0]));
					bw.write("\n");
				}
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean importFile(File src) {
		File dest = null;
		String fileName = src.getName();
		if (Util.isConstraintFile(fileName)) {
			dest = new File(getConstraintFolder() + File.separator + fileName);
		} else if (Util.isSourceFile(fileName)) {
			dest = new File(getSourceFolder() + File.separator + fileName);
		} else {
			return false;
		}

		boolean exists = dest.exists();

		if (!dest.equals(src)) {
			if (exists && !Util.askQuestion("File Exists", "A file with the same name as \"" + fileName + "\" exsists in the project. Overwrite this file?"))
				return false;

			try {
				FileUtils.copyFile(src, dest);
			} catch (IOException e) {
				Util.logException(e, "Failed to copy file!");
				return false;
			}
		}

		if (Util.isConstraintFile(fileName) && !constraintFiles.contains(src)) {
			addExistingConstraintFile(src);
		} else if (Util.isSourceFile(fileName) && !sourceFiles.contains(src)) {
			addExistingSourceFile(src);
		}

		try {
			saveXML();
		} catch (IOException e1) {
			Util.showError("Failed to save project file!");
		}

		return true;
	}

	public File addNewSourceFile(String fileName) {
		File path = createFile(Util.assembleFile(projectFolder, SOURCE_FOLDER, fileName), sourceFiles);
		if (path == null)
			return null;
		copyTemplate(path, fileName);
		return path;
	}

	public File addNewConstraintFile(String fileName) {
		File cFile = new File(Util.assemblePath(projectFolder, CONSTRAINTS_FOLDER, fileName));
		return createFile(cFile, constraintFiles);
	}

	public File getSourceFolder() {
		return Util.assembleFile(projectFolder, SOURCE_FOLDER);
	}

	public File getConstraintFolder() {
		return Util.assembleFile(projectFolder, CONSTRAINTS_FOLDER);
	}

	public File getIPCoreFolder() {
		return Util.assembleFile(projectFolder, CORES_FOLDER);
	}

	public void addIPCore(IPCore core) {
		if (ipCores.contains(core)) {
			ipCores.remove(core);
		}

		ipCores.add(core);
	}

	public void addExistingSourceFile(File file) {
		sourceFiles.add(file);
	}

	public void addExistingConstraintFile(File file) {
		constraintFiles.add(file);
	}

	public boolean setTopFile(File file) {
		if (sourceFiles.contains(file)) {
			topSource = file;
			return true;
		}
		return false;
	}

	public File getTop() {
		return topSource;
	}

	public HashSet<File> getSourceFiles() {
		return sourceFiles;
	}

	public HashSet<File> getConstraintFiles() {
		HashSet<File> hs = new HashSet<>(constraintFiles);
		removeUnsupportedConstraints(hs);
		return hs;
	}

	private boolean endsWithExt(File file, String[] ext) {
		for (String e : ext)
			if (file.getName().endsWith(e))
				return true;
		return false;
	}

	private void removeUnsupportedConstraints(HashSet<File> constraints) {
		String[] ext = boardType.getSupportedConstraintExtensions();
		for (Iterator<File> it = constraints.iterator(); it.hasNext();) {
			File c = it.next();
			if (!endsWithExt(c, ext)) {
				it.remove();
				Util.println("Constraint \"" + c + "\" is of an unsupported type. It will be ignored.", true);
			}
		}
	}

	public HashSet<File> getDebugFiles() {
		HashSet<File> debugList = new HashSet<>();
		if (boardType.isType(Board.MOJO)) {
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "reg_interface_debug.luc"));
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "reg_interface.luc"));
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "wave_capture.luc"));
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "simple_dual_ram.v"));
		} else if (boardType.isType(Board.AU)) {
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "au_debugger.luc"));
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "reset_conditioner.luc"));
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "async_fifo.luc"));
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "pipeline.luc"));
			debugList.add(Util.assembleFile(Locations.COMPONENTS, "simple_dual_ram.v"));
		}
		return debugList;
	}

	public HashSet<IPCore> getIPCores() {
		return ipCores;
	}

	public File getProjectFolder() {
		return projectFolder;
	}

	public String getProjectName() {
		return projectName;
	}

	public Board getBoard() {
		return boardType;
	}

	public void setProjectName(String name) {
		projectName = name;
	}

	public File getProjectFile() {
		return projectFile;
	}

	public boolean setBoardType(String type) {
		boardType = Board.getFromName(type);
		if (boardType == null)
			return false;
		return true;
	}

	public void setProjectFolder(File folder) {
		projectFolder = folder;
	}

	public void setProjectFile(File file) {
		projectFile = file;
	}

	private class SortFilesIgnoreCase implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String s1 = ((File) o1).getName();
			String s2 = ((File) o2).getName();
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}

	private class SortIPCores implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			IPCore s1 = (IPCore) o1;
			IPCore s2 = (IPCore) o2;
			return s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
		}
	}

	private void addRemoveFile(CustomTree.TreeLeaf item, final FileType type) {
		MenuItem mi = new MenuItem(treeMenu, SWT.NONE);
		mi.setText("Remove " + item.getName());
		mi.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				File file = item.getFile();
				boolean result = Util.askQuestion("Confirm Delete", "Are you sure you want to remove " + file + "?" + System.lineSeparator() + "This cannot be undone.");

				if (result) {
					switch (type) {
					case SOURCE:
					case COMPONENT:
						if (!removeFile(file, sourceFiles))
							Util.showError("Could not remove file!");
						break;
					case CONSTRAINT:
						if (!removeFile(file, constraintFiles))
							Util.showError("Could not remove file!");
						break;
					case CORE:
						if (!removeIPCore(item.getName()))
							Util.showError("Could not remove IP Core!");
						break;
					}
					for (TabChild editor : MainWindow.mainWindow.getTabs()) {
						if (editor instanceof StyledCodeEditor)
							if (((StyledCodeEditor) editor).getFileName().equals(file)) {
								MainWindow.mainWindow.getTabFolder().close(editor); // close file if open
								break;
							}
					}
					try {
						saveXML();
					} catch (IOException e1) {
						Util.showError("Failed to save project file!");
					}
					MainWindow.mainWindow.updateErrors();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private Listener sourceListner = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CustomTree.TreeElement item = (CustomTree.TreeElement) event.data;
			if (event.button == 1 && !item.isNode()) {
				MainWindow.mainWindow.openFile(Util.assembleFile(getSourceFolder(), item.getName()), true);
			} else if (event.button == 3) {
				for (MenuItem i : treeMenu.getItems())
					i.dispose();
				MenuItem mi = new MenuItem(treeMenu, SWT.NONE);
				mi.setText("New source...");
				mi.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						MainWindow.mainWindow.addNewFile();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				if (!item.isNode())
					addRemoveFile((CustomTree.TreeLeaf) item, FileType.SOURCE);
			}
		}
	};

	private Listener constraintsListner = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CustomTree.TreeElement item = (CustomTree.TreeElement) event.data;
			if (event.button == 1 && !item.isNode()) {
				File compFile = Util.assembleFile(Locations.COMPONENTS, item.getName());
				if (isLibFile(compFile))
					MainWindow.mainWindow.openFile(compFile, false);
				else
					MainWindow.mainWindow.openFile(Util.assembleFile(getConstraintFolder(), item.getName()), true);
			} else if (event.button == 3) {
				for (MenuItem i : treeMenu.getItems())
					i.dispose();
				MenuItem mi = new MenuItem(treeMenu, SWT.NONE);
				mi.setText("New constraint...");
				mi.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						MainWindow.mainWindow.addNewFile();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				if (!item.isNode())
					addRemoveFile((CustomTree.TreeLeaf) item, FileType.CONSTRAINT);
			}
		}
	};

	private Listener componentsListner = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CustomTree.TreeElement item = (CustomTree.TreeElement) event.data;
			if (event.button == 1 && !item.isNode()) {
				MainWindow.mainWindow.openFile(Util.assembleFile(Locations.COMPONENTS, item.getName()), false);
			} else if (event.button == 3) {
				for (MenuItem i : treeMenu.getItems())
					i.dispose();
				MenuItem mi = new MenuItem(treeMenu, SWT.NONE);
				mi.setText("Add component...");
				mi.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (!isOpen()) {
							Util.showError("You need to open or create a project first!");
							return;
						}
						MainWindow.mainWindow.addComponents();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				if (!item.isNode())
					addRemoveFile((CustomTree.TreeLeaf) item, FileType.COMPONENT);
			}
		}
	};

	private Listener coresListner = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CustomTree.TreeElement item = (CustomTree.TreeElement) event.data;
			if (event.button == 1 && !item.isNode()) {
				if (boardType.isType(Board.MOJO)) {
					MainWindow.mainWindow.openFile(Util.assembleFile(getFolder(), CORES_FOLDER, item.getName()), true);
				} else {
					MainWindow.mainWindow.openFile(Util.assembleFile(getFolder(), CORES_FOLDER, item.getParent().getName(), item.getName()), true);
				}
			} else if (event.button == 3) {
				for (MenuItem i : treeMenu.getItems())
					i.dispose();
				MenuItem mi = new MenuItem(treeMenu, SWT.NONE);
				mi.setText("Recustomize core");
				mi.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						if (boardType.isType(Board.MOJO))
							MainWindow.mainWindow.getCoreGen().launch(Project.this);
						else if (boardType.isType(Board.AU))
							MainWindow.mainWindow.getVivadoIP().launch(Project.this);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
				// TODO: Add removal of Mojo cores
				// if (boardType.isType(Board.MOJO) && item.isNode() && !item.getName().equals(CORES_PARENT))
				// addRemoveFile((CustomTree.TreeLeaf) item, FileType.CORE);
			}
		}
	};

	public void updateTree() {
		if (open && Util.isGUI) {
			if (tree.getRootSize() != 1 || !tree.getElement(0).getName().equals(projectName)) {
				tree.removeAll();
				TreeNode project = new TreeNode(projectName);
				tree.addElement(project);
			}

			TreeNode project = (TreeNode) tree.getElement(0);
			List<TreeElement> projectNodes = project.getChildren();

			if (projectNodes.size() < 1 || !projectNodes.get(0).getName().equals(SOURCE_PARENT)) {
				TreeNode sourceBranch = new TreeNode(SOURCE_PARENT);
				sourceBranch.addClickListener(sourceListner);
				project.add(sourceBranch);
			}

			TreeNode sourceBranch = (TreeNode) projectNodes.get(0);
			List<TreeElement> sourceLeafs = sourceBranch.getChildren();

			ArrayList<File> libFiles = new ArrayList<>();
			int leafCt = 0;

			ArrayList<File> files = new ArrayList<>(sourceFiles);
			Collections.sort(files, new SortFilesIgnoreCase());
			for (File source : files) {
				if (isLibFile(source)) {
					libFiles.add(source);
				} else {
					if (sourceLeafs.size() < leafCt + 1 || !sourceLeafs.get(leafCt).getName().equals(source.getName())) {
						TreeLeaf leaf = new TreeLeaf(source);
						sourceBranch.add(leafCt, leaf);
						leaf.addClickListener(sourceListner);
					}
					leafCt++;
				}
			}

			while (leafCt < sourceLeafs.size())
				sourceBranch.remove(sourceLeafs.size() - 1);

			int categoryIdx = 1;
			leafCt = 0;

			if (libFiles.size() > 0) {
				if (projectNodes.size() < categoryIdx + 1 || !projectNodes.get(categoryIdx).getName().equals(COMPONENTS_PARENT)) {
					TreeNode compBranch = new TreeNode(COMPONENTS_PARENT);
					compBranch.addClickListener(componentsListner);
					project.add(categoryIdx, compBranch);
				}

				TreeNode compBranch = (TreeNode) projectNodes.get(categoryIdx++);
				List<TreeElement> compLeafs = compBranch.getChildren();

				for (File comp : libFiles) {
					if (compLeafs.size() < leafCt + 1 || !compLeafs.get(leafCt).getName().equals(comp.getName())) {
						TreeLeaf leaf = new TreeLeaf(comp);
						compBranch.add(leafCt, leaf);
						leaf.addClickListener(componentsListner);
					}
					leafCt++;
				}
				while (leafCt < compLeafs.size())
					compBranch.remove(compLeafs.size() - 1);
			}

			if (ipCores.size() > 0) {
				if (projectNodes.size() < categoryIdx + 1 || !projectNodes.get(categoryIdx).getName().equals(CORES_PARENT)) {
					TreeNode coreBranch = new TreeNode(CORES_PARENT);
					coreBranch.addClickListener(coresListner);
					project.add(categoryIdx, coreBranch);
				}

				TreeNode coreBranch = (TreeNode) projectNodes.get(categoryIdx++);
				List<TreeElement> coreLeafs = coreBranch.getChildren();

				ArrayList<IPCore> cores = new ArrayList<IPCore>(ipCores);
				Collections.sort(cores, new SortIPCores());
				for (int j = 0; j < cores.size(); j++) {
					IPCore core = cores.get(j);

					if (coreLeafs.size() < j + 1 || !coreLeafs.get(j).getName().equals(core.getName())) {
						TreeNode coreParent = new TreeNode(core.getName());
						coreParent.addClickListener(coresListner);
						coreBranch.add(j, coreParent);
					}

					TreeNode coreParent = (TreeNode) coreLeafs.get(j);
					List<TreeElement> coreParentLeafs = coreParent.getChildren();

					leafCt = 0;

					if (core.getStub() != null) {
						TreeLeaf leaf = new TreeLeaf(core.getStub());
						coreParent.add(leafCt++, leaf);
						leaf.addClickListener(coresListner);
					} else {
						ArrayList<File> cfiles = core.getFiles();
						for (int i = 0; i < cfiles.size(); i++) {
							File cfile = cfiles.get(i);
							if (cfile.isFile()) {
								if (coreParentLeafs.size() < i + 1 || !coreParentLeafs.get(i).getName().equals(cfile.getName())) {
									TreeLeaf leaf = new TreeLeaf(cfile);
									coreParent.add(leafCt, leaf);
									leaf.addClickListener(coresListner);
								}
								leafCt++;
							}
						}
					}

					while (leafCt < coreParentLeafs.size())
						coreParent.remove(coreParentLeafs.size() - 1);
				}
				while (cores.size() < coreLeafs.size())
					coreBranch.remove(coreLeafs.size() - 1);
			}

			if (projectNodes.size() < categoryIdx + 1 || !projectNodes.get(categoryIdx).getName().equals(CONSTRAINTS_PARENT)) {
				TreeNode constBranch = new TreeNode(CONSTRAINTS_PARENT);
				constBranch.addClickListener(constraintsListner);
				project.add(categoryIdx, constBranch);
			}

			TreeNode ucfBranch = (TreeNode) projectNodes.get(categoryIdx++);
			List<TreeElement> ucfLeafs = ucfBranch.getChildren();

			files = new ArrayList<>(constraintFiles);
			Collections.sort(files, new SortFilesIgnoreCase());
			for (int i = 0; i < files.size(); i++) {
				File ucf = files.get(i);
				if (ucfLeafs.size() < i + 1 || !ucfLeafs.get(i).getName().equals(ucf.getName())) {
					TreeLeaf leaf = new TreeLeaf(ucf);
					ucfBranch.add(i, leaf);
					leaf.addClickListener(constraintsListner);
				}
			}

			while (constraintFiles.size() < ucfLeafs.size())
				ucfBranch.remove(ucfLeafs.size() - 1);

			while (projectNodes.size() > categoryIdx) { // remove extras
				project.remove(projectNodes.size() - 1);
			}

			tree.updateTree();
			tree.redraw();
		}
	}

	public void openTree() {
		for (int i = 0; i < tree.getRootSize(); i++)
			((TreeNode) tree.getElement(i)).setOpen(true);
		tree.updateTree();
	}

	public void openXML(File xmlFile) throws ParseException, IOException {
		close();

		SAXBuilder builder = new SAXBuilder();
		projectFolder = xmlFile.getParentFile();
		projectFile = xmlFile;

		Document document;
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException e) {
			throw new ParseException(e.getMessage());
		}
		Element project = document.getRootElement();

		if (!project.getName().equals(Tags.project)) {
			throw new ParseException("Root element not project tag");
		}

		Attribute projName = project.getAttribute(Tags.Attributes.name);
		if (projName == null) {
			throw new ParseException("Project name is missing");
		}
		projectName = projName.getValue();

		Attribute brdType = project.getAttribute(Tags.Attributes.board);
		if (brdType == null) {
			throw new ParseException("Board type is missing");
		}
		if (!setBoardType(brdType.getValue()))
			throw new ParseException("Unknown board type: " + brdType.getValue());

		Attribute langType = project.getAttribute(Tags.Attributes.language);
		if (langType == null)
			language = "Lucid";
		else
			language = langType.getValue();

		int version = 0;
		Attribute versionAttr = project.getAttribute(Tags.Attributes.version);
		if (versionAttr != null)
			try {
				version = Integer.parseInt(versionAttr.getValue());
				if (version > VERSION_ID)
					throw new ParseException("Project file is from a future version!");
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid version ID!");
			}

		final List<Element> list = project.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element node = list.get(i);

			switch (node.getName()) {
			case Tags.files:
				final List<Element> files = node.getChildren();
				for (int j = 0; j < files.size(); j++) {
					Element file = files.get(j);
					switch (file.getName()) {
					case Tags.source:
						Attribute att = file.getAttribute(Tags.Attributes.top);
						if (att != null && att.getValue().equals("true")) {
							if (topSource != null)
								throw new ParseException("Multiple \"top\" source files");
							topSource = Util.assembleFile(getSourceFolder(), file.getText());
						}
						sourceFiles.add(Util.assembleFile(getSourceFolder(), file.getText()));
						break;
					case Tags.constraint:
						att = file.getAttribute(Tags.Attributes.library);
						boolean isLib = Boolean.valueOf(att != null && att.getValue().equals("true"));
						File cstFile = Util.assembleFile(isLib ? Locations.COMPONENTS : getConstraintFolder(), file.getText());
						constraintFiles.add(cstFile);
						break;
					case Tags.component:
						sourceFiles.add(Util.assembleFile(Locations.COMPONENTS, file.getText()));
						break;
					case Tags.core:
						final List<Element> cfiles = file.getChildren();
						String coreName = file.getAttributeValue(Tags.Attributes.name);
						if (coreName == null)
							throw new ParseException("Missing core name");

						String coreDir = Util.assemblePath(projectFolder, CORES_FOLDER, coreName);

						IPCore ipCore = new IPCore(coreName);
						for (int k = 0; k < cfiles.size(); k++) {
							Element cfile = cfiles.get(k);
							File coreFile = new File(Util.assemblePath(coreDir, cfile.getText()));
							switch (cfile.getName()) {
							case Tags.source:
								ipCore.addFile(coreFile);
								break;
							case Tags.stub:
								ipCore.setStub(coreFile);
								break;
							default:
								throw new ParseException("Unknown tag in core " + cfile.getName());
							}
						}
						ipCores.add(ipCore);
						break;
					default:
						throw new ParseException("Unknown tag " + file.getName());
					}
				}
				break;
			default:
				throw new ParseException("Unknown tag " + node.getName());
			}
		}

		if (version != VERSION_ID) {
			if (Util.askQuestion("This project is from a previous version of the IDE. Would you like to attempt to update it?")) {
				if (version == 0) {
					File coresDir = new File(Util.assemblePath(getProjectFolder(), "coreGen"));
					File newCoresDir = new File(Util.assemblePath(getProjectFolder(), CORES_FOLDER));
					if (coresDir.exists() && coresDir.isDirectory())
						if (!coresDir.renameTo(newCoresDir))
							throw new ParseException("Failed to rename coreGen directory to cores");
				}
				saveXML();
			} else {
				throw new ParseException("Incompatible version ID!");
			}
		}

		readDebugInfo();
		open = true;
	}

	private List<Module> getVerilogModules(File file) throws IOException {
		VerilogModuleListener converter = new VerilogModuleListener();
		List<Module> modules = converter.extractModules(file);
		for (Module m : modules)
			m.setFile(file);

		return modules;
	}

	private Module getLucidModule(File file) throws IOException {
		LucidModuleExtractor converter = new LucidModuleExtractor();
		Module m = converter.getModule(file);
		if (m != null)
			m.setFile(file);

		return m;
	}

	private void addGlobals(Collection<File> files) throws IOException {
		for (File file : files) {
			if (file.getName().endsWith(".luc")) {
				globalExtractor.parseGlobals(file);
			}
		}
	}

	public void updateGlobals() throws IOException {
		globalExtractor.reset();
		addGlobals(getSourceFiles());
	}

	public List<SyntaxError> getGlobalErrors(File file) {
		return globalExtractor.getErrors(file);
	}

	public HashMap<String, List<Constant>> getGlobalConstants() {
		return globalExtractor.getConstants();
	}

	public HashMap<String, List<Struct>> getGlobalStructs() {
		return globalExtractor.getStructs();
	}

	private void addModule(ArrayList<Module> modules, File file) throws IOException {
		for (Module m : modules)
			if (m.getFile().getCanonicalPath().equals(file.getCanonicalPath()))
				return;
		if (file.getName().endsWith(".luc")) {
			Module m = getLucidModule(file);
			if (m != null)
				modules.add(m);
		} else if (file.getName().endsWith(".v")) {
			List<Module> ml = getVerilogModules(file);
			for (Module m : ml)
				if (m.getName().endsWith("_bb"))
					m.setNgc(true);
			modules.addAll(ml);
		} else if (file.getName().endsWith(".ngc")) {

		}
	}

	private void addModules(ArrayList<Module> modules, Collection<File> files) throws IOException {
		for (File file : files)
			addModule(modules, file);
	}

	private Module primToModule(Primitive p) {
		String name = "xil_" + p.getName();
		Module m = new Module(name);
		m.setPrimitive(p);
		for (Parameter pParam : p.getParameters()) {
			Param param = new Param(pParam.getName());
			param.setDefault("");
			m.addParam(param);
		}
		for (Port pPort : p.getPorts()) {
			Sig s = new Sig(pPort.getName());
			s.setWidth(new SignalWidth(pPort.getWidth()));
			switch (pPort.getDirection()) {
			case Port.DIR_INPUT:
				m.addInput(s);
				break;
			case Port.DIR_OUTPUT:
				m.addOutput(s);
				break;
			case Port.DIR_INOUT:
				m.addInout(s);
				break;
			}
		}
		return m;
	}

	public Module getTopModule() throws IOException {
		List<Module> modules = getModules(null);
		return getModuleFromFile(topSource, modules);
	}

	public ArrayList<Module> getModules(List<File> debugFiles) throws IOException {
		ArrayList<Module> modules = new ArrayList<Module>();
		addModules(modules, getSourceFiles());

		if (debugFiles != null) {
			addModules(modules, getDebugFiles());
			addModules(modules, debugFiles);
		}

		for (IPCore ipcore : getIPCores()) {
			if (ipcore.getStub() != null)
				addModule(modules, ipcore.getStub());
			else
				addModules(modules, ipcore.getFiles());
		}

		try {
			HashMap<String, HashSet<Primitive>> prims = Primitive.getAvailable();
			for (HashSet<Primitive> pSet : prims.values())
				for (Primitive p : pSet)
					modules.add(primToModule(p));
		} catch (ParseException e) {
			Util.println(e.getMessage(), true);
			e.printStackTrace();
		}

		return modules;
	}

	private Module getModuleFromFile(File file, List<Module> list) {
		for (Module m : list)
			if (m.getFile().equals(file))
				return m;

		return null;
	}

	private List<InstModule> getVerilogInstModules(InstModule im, List<Module> modules) throws IOException {
		VerilogModuleListener converter = new VerilogModuleListener();
		return converter.extractInstModules(im, modules);
	}

	private List<InstModule> getLucidInstModules(InstModule im, List<Module> modules) throws IOException {
		File file = im.getType().getFile();
		LucidExtractor converter = new LucidExtractor(im);
		return converter.getInstModules(file, modules);
	}

	public List<InstModule> getModuleList(List<Module> modules, boolean mergeDupes, Module topModule) throws IOException {
		Queue<InstModule> queue = new LinkedList<>();
		List<InstModule> outList = new ArrayList<>();

		if (topModule == null) {
			Module top = getModuleFromFile(topSource, modules);
			if (top == null) {
				Util.showError("Could not find top module file!");
				return null;
			}

			queue.add(new InstModule(top.getName(), top, null));
		} else {
			queue.add(new InstModule(topModule.getName(), topModule, null));
		}

		while (!queue.isEmpty()) {
			InstModule im = queue.remove();
			outList.add(im);
			List<InstModule> list = null;
			if (im.isLucid()) {
				list = getLucidInstModules(im, modules);
			} else if (im.isVerilog()) {
				list = getVerilogInstModules(im, modules);
			}
			// outQueue.addAll(list);
			if (list != null) {
				for (InstModule m : list) {
					boolean add = true;

					// Skip IP core files
					if (m.getType().getFile() == null
							|| m.getType().getFile().getCanonicalPath().startsWith(Util.assembleFile(projectFolder, Project.CORES_FOLDER).getCanonicalPath()))
						add = false;

					if (add && mergeDupes)
						for (InstModule om : outList) {
							if (om.equals(m)) {
								add = false;
								im.addChild(om);
								break;
							}
						}

					if (add) {
						if (!queue.contains(m))
							queue.add(m);
						im.addChild(m);
					}
				}
			}
		}

		return outList;
	}

	public boolean saveAsXML(File folder, String name) throws IOException {
		File oldFolder = projectFolder;
		File oldFile = projectFile;

		if (folder == null || oldFolder == null || name == null) {
			return false;
		}

		projectFile = Util.assembleFile(folder, name + ".alp");
		projectFolder = folder;

		File srcDir = oldFolder;
		File dstDir = projectFolder;
		File oldProj = Util.assembleFile(dstDir, oldFile.getName());

		FileUtils.copyDirectory(srcDir, dstDir);
		oldProj.delete();

		String oldName = projectName;
		projectName = name;
		
		// Need to update core file references before saving so they don't point to the old project
		for (IPCore core : ipCores) {
			Path oldCorePath = Paths.get(new File(Util.assemblePath(oldFolder, CORES_FOLDER, core.getName())).getAbsolutePath());
			ArrayList<File> updatedFiles = new ArrayList<>(core.getFiles().size());
			for (File corefile : core.getFiles()) {
				String p = oldCorePath.relativize(Paths.get(corefile.getAbsolutePath())).toString();
				updatedFiles.add(Util.assembleFile(projectFolder, CORES_FOLDER, core.getName(), p));
			}
			core.setFiles(updatedFiles);
			if (core.getStub() != null) {
				String p = oldCorePath.relativize(Paths.get(core.getStub().getAbsolutePath())).toString();
				core.setStub(Util.assembleFile(projectFolder, CORES_FOLDER, core.getName(), p));
			}
		}

		saveXML();

		projectName = oldName;
		projectFile = oldFile;
		projectFolder = oldFolder;

		return true;
	}

	public void saveXML() throws IOException {
		saveXML(projectFile);
	}

	public void saveXML(File file) throws IOException {
		Element project = new Element(Tags.project);

		project.setAttribute(new Attribute(Tags.Attributes.name, projectName));
		project.setAttribute(new Attribute(Tags.Attributes.board, boardType.getName()));
		project.setAttribute(new Attribute(Tags.Attributes.language, language));
		project.setAttribute(new Attribute(Tags.Attributes.version, Integer.toString(VERSION_ID)));
		Document doc = new Document(project);

		Element source = new Element(Tags.files);
		for (File sourceFile : sourceFiles) {
			if (isLibFile(sourceFile)) {
				source.addContent(new Element(Tags.component).setText(sourceFile.getName()));
			} else {
				Element ele = new Element(Tags.source).setText(sourceFile.getName());
				if (sourceFile.equals(topSource))
					ele.setAttribute(new Attribute(Tags.Attributes.top, "true"));
				source.addContent(ele);
			}
		}

		for (File ucfFile : constraintFiles) {
			Element ele = new Element(Tags.constraint).setText(ucfFile.getName());
			if (isLibFile(ucfFile))
				ele.setAttribute(new Attribute(Tags.Attributes.library, "true"));
			source.addContent(ele);
		}

		for (IPCore core : ipCores) {
			Element coreElement = new Element(Tags.core).setAttribute(Tags.Attributes.name, core.getName());
			Path corePath = Paths.get(new File(Util.assemblePath(projectFolder, CORES_FOLDER, core.getName())).getAbsolutePath());
			for (File corefile : core.getFiles()) {
				String p = corePath.relativize(Paths.get(corefile.getAbsolutePath())).toString();
				coreElement.addContent(new Element(Tags.source).setText(p));
			}
			if (core.getStub() != null) {
				String p = corePath.relativize(Paths.get(core.getStub().getAbsolutePath())).toString();
				coreElement.addContent(new Element(Tags.stub).setText(p));
			}
			source.addContent(coreElement);
		}

		project.addContent(source);

		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());

		xmlOutput.output(doc, new FileWriter(file));

		for (Listener l : saveListeners)
			l.handleEvent(null);
	}

	public boolean hasGlobalErrors() {
		for (List<SyntaxError> e : globalExtractor.getAllErrors().values()) {
			if (e.size() > 0)
				return true;
		}
		return false;
	}

	public void setDebugInfo(DebugInfo dbi) {
		debugInfo = dbi;

		try {
			ObjectOutputStream e = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(Util.assembleFile(projectFolder, "work", "debug", "debug.data"))));
			e.writeObject(dbi);
			e.close();
		} catch (Exception o) {
			Util.log.log(Level.INFO, "Failed to write debug info", o);
		}
	}

	public boolean readDebugInfo() {
		try {
			File debugFile = Util.assembleFile(projectFolder, "work", "debug", "debug.data");
			if (!debugFile.exists())
				return false;
			ObjectInputStream e = new ObjectInputStream(new BufferedInputStream(new FileInputStream(debugFile.getPath())));
			DebugInfo dbi = (DebugInfo) e.readObject();
			e.close();
			debugInfo = dbi;
			return true;
		} catch (Exception o) {
			Util.log.log(Level.INFO, "Failed to recover debug info", o);
		}
		return false;
	}

	public DebugInfo getDebugInfo() {
		return debugInfo;
	}

	public void checkProject() {
		Thread thread = new Thread() {
			public void run() {
				Util.clearConsole();
				try {
					if (!checkForErrors()) {
						Util.println("No errors detected.", Theme.successTextColor);
					}
				} catch (IOException e) {
				}
			}
		};
		thread.start();
	}

	private boolean checkforErrors(File file, boolean printErrors) throws IOException {
		boolean hasErrors = false;

		if (Util.hasErrorProvider(file)) {
			List<SyntaxError> errors = null;
			if (file.getName().endsWith(".luc")) {
				LucidErrorProvider errorChecker = new LucidErrorProvider();
				errors = errorChecker.getErrors(file);
			} else if (file.getName().endsWith(".v")) {
				VerilogErrorProvider errorChecker = new VerilogErrorProvider();
				errors = errorChecker.getErrors(file);
			} else if (file.getName().endsWith(".acf")) {
				AlchitryConstraintsErrorProvider errorChecker = new AlchitryConstraintsErrorProvider();
				errors = errorChecker.getErrors(file);
			} else {
				Util.println("Unknown source file extension " + file + "!", true);
				return false;
			}

			List<SyntaxError> ge = getGlobalErrors(file);

			if (ge != null)
				if (errors == null && ge.size() > 0)
					errors = ge;
				else
					errors.addAll(ge);
			hasErrors = addErrors(errors, file, printErrors);

		}
		return hasErrors;
	}

	private boolean checkForIMErrors(InstModule im, List<Module> modules, List<InstModule> instModules) {
		File file = im.getType().getFile();
		List<SyntaxError> errors = VerilogLucidModuleFixer.getErrors(im, file, modules, instModules);
		return addErrors(errors, file, true);
	}

	public boolean checkForErrors() throws IOException {
		updateGlobals();
		List<Module> modules = getModules(null);
		List<InstModule> list = getModuleList(modules, true, null);
		boolean hasErrors = false;
		for (File file : getSourceFiles()) {
			hasErrors = hasErrors | checkforErrors(file, true);
		}
		for (InstModule im : list)
			if (!im.getType().isPrimitive() && im.getType().getFile().getName().endsWith(".v"))
				hasErrors = hasErrors | checkForIMErrors(im, modules, list);

		for (File file : getConstraintFiles()) {
			hasErrors = hasErrors | checkforErrors(file, true);
		}

		return hasErrors;
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

	private boolean addErrors(List<SyntaxError> errors, File file, boolean printErrors) {
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

	public InstModule getLucidSourceTree() throws IOException {
		updateGlobals();
		List<Module> modules = getModules(null);
		List<InstModule> list = getModuleList(modules, false, null);

		for (File file : getSourceFiles())
			checkforErrors(file, false);

		return list.get(0); // top level IM
	}

	public void build(final boolean debug) {
		if (isBusy()) {
			Util.showError("Operation already in progress!");
			return;
		}

		builder = boardType.getBuilder();

		if (Util.isGUI) {
			thread = new Thread() {
				public void run() {
					try {
						builder.build(Project.this, debug);
					} catch (Exception e) {
						Util.logException(e, "Exception with project builder!");
					}
				}
			};

			thread.start();
		} else {
			builder.build(Project.this, debug);
		}
	}

	public boolean isBuilding() {
		return isBusy() && (builder != null) && builder.isBuilding();
	}

	public boolean isBusy() {
		return thread != null && thread.isAlive();
	}

	public void stopBuild() {
		if (isBusy() && builder != null && builder.isBuilding()) {
			builder.stopBuild();
		}
	}

	public void load(final boolean flash, final boolean verify) {
		if (isBusy()) {
			Util.showError("Operation already in progress!");
			return;
		}

		final String binFile = getBinFile();
		if (binFile == null) {
			Util.showError("Could not find the bin file! Make sure the project is built.");
			return;
		}

		if (Util.isGUI) {
			thread = new Thread() {
				public void run() {
					boardType.getLoader().load(binFile, flash, verify);
				}
			};
			thread.start();
		} else {
			boardType.getLoader().load(binFile, flash, verify);
		}
	}

	public void erase() {
		if (isBusy()) {
			Util.showError("Operation already in progress!");
			return;
		}

		if (Util.isGUI) {
			thread = new Thread() {
				public void run() {
					boardType.getLoader().erase();
				}
			};
			thread.start();
		} else {
			boardType.getLoader().erase();
		}
	}
}
