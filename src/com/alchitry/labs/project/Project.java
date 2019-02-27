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
import java.util.Set;
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
import com.alchitry.labs.parsers.errors.VerilogErrorProvider;
import com.alchitry.labs.parsers.lucid.Constant;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.lucid.Struct;
import com.alchitry.labs.parsers.tools.lucid.LucidErrorChecker;
import com.alchitry.labs.parsers.tools.lucid.LucidGlobalExtractor;
import com.alchitry.labs.parsers.tools.lucid.LucidModuleExtractor;
import com.alchitry.labs.parsers.tools.verilog.VerilogLucidModuleFixer;
import com.alchitry.labs.parsers.tools.verilog.VerilogModuleListener;
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
	public static final String SOURCE_PARENT = "Source";
	public static final String CONSTRAINTS_PARENT = "Constraints";
	public static final String COMPONENTS_PARENT = "Components";
	public static final String CORES_PARENT = "Cores";

	public static final String SOURCE_FOLDER = "source";
	public static final String CONSTRAINTS_FOLDER = "constraint";
	public static final String CORES_FOLDER = "coreGen";

	public static final String LANG_LUCID = "Lucid";
	public static final String LANG_VERILOG = "Verilog";

	private HashSet<String> sourceFiles;
	private HashSet<String> constraintFiles;
	private HashMap<String, Boolean> constraintLib;
	private HashSet<String> componentFiles;
	private HashSet<IPCore> ipCores;
	private HashSet<Primitive> primitives;

	private String language;

	private String topSource;
	private String projectName;
	private String projectFolder;
	private String projectFile;
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

	public Project(String name, String folder, String board, String lang) {
		this();
		projectName = name;
		projectFolder = folder;
		setBoardType(board);
		projectFile = name + ".alp";
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
		constraintLib = new HashMap<>();
		componentFiles = new HashSet<>();
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
		constraintLib.clear();
		componentFiles.clear();
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

	public String getFolder() {
		return projectFolder;
	}
	
	public void addSaveListener(Listener l) {
		saveListeners.add(l);
	}
	
	public void removeSaveListener(Listener l) {
		saveListeners.remove(l);
	}

	public String getBinFile() {
		File binFile = new File(projectFolder + File.separatorChar + "work" + File.separatorChar + "alchitry.bin");
		if (binFile.exists())
			return binFile.getAbsolutePath();
		return null;
	}

	private String addFile(String fileName, String folder, HashSet<String> list) {
		File file = new File(projectFolder + File.separatorChar + folder + File.separatorChar + fileName);
		try {
			if (file.exists()) {
				if (!Util.askQuestion("File " + fileName + " exists. Overwrite?"))
					return null;

				file.delete();
			}
			if (file.createNewFile()) {
				if (!list.contains(fileName))
					list.add(fileName);
				updateTree();
				return file.getAbsolutePath();
			}
		} catch (IOException e) {
			Util.log.severe(ExceptionUtils.getStackTrace(e));
			return null;
		}
		Util.log.severe("Could not open file " + file.getAbsolutePath());
		return null;
	}

	public boolean removeSourceFile(String fileName) {
		if (topSource.equals(fileName)) {
			Util.showError("You can't delete the top file!");
			return true;
		}
		return removeFile(fileName, sourceFiles, SOURCE_FOLDER);
	}

	public boolean removeConstaintFile(String fileName) {
		if (Boolean.TRUE.equals(constraintLib.get(fileName))) {
			constraintLib.remove(fileName);
			boolean ret = constraintFiles.remove(fileName);
			updateTree();
			return ret;
		}

		return removeFile(fileName, constraintFiles, CONSTRAINTS_FOLDER);
	}

	private boolean removeFile(String fileName, HashSet<String> list, String folder) {
		File file = new File(projectFolder + File.separatorChar + folder + File.separatorChar + fileName);
		if (file.exists() && !file.delete()) {
			return false;
		}
		boolean ret = list.remove(fileName);
		updateTree();
		return ret;
	}

	public boolean removeComponentFile(String fileName) {
		boolean ret = componentFiles.remove(fileName);
		updateTree();
		return ret;
	}

	public boolean removeIPCore(String coreName) {
		if (IPCore.delete(coreName, projectFolder)) {
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

	private boolean copyTemplate(String path, String fileName) {
		if (path.endsWith(".luc") || path.endsWith(".v")) {
			File template;
			if (path.endsWith(".luc"))
				template = new File(Locations.TEMPLATE_DIR + File.separator + "module.luc");
			else
				template = new File(Locations.TEMPLATE_DIR + File.separator + "module.v");
			File dest = new File(path);

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

	public boolean importFile(String filePath) {
		File dest = null;
		File src = new File(filePath);
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
				Util.print(e);
				return false;
			}
		}

		if (Util.isConstraintFile(fileName) && !constraintFiles.contains(fileName)) {
			addExistingConstraintFile(fileName, false);
		} else if (Util.isSourceFile(fileName) && !sourceFiles.contains(fileName)) {
			addExistingSourceFile(fileName);
		}

		try {
			saveXML();
		} catch (IOException e1) {
			Util.showError("Failed to save project file!");
		}

		return true;
	}

	public String addNewSourceFile(String fileName) {
		String path = addFile(fileName, SOURCE_FOLDER, sourceFiles);
		if (path == null)
			return null;
		copyTemplate(path, fileName);
		return path;
	}

	public String addNewConstraintFile(String fileName) {
		constraintLib.put(fileName, Boolean.FALSE);
		return addFile(fileName, CONSTRAINTS_FOLDER, constraintFiles);
	}

	public String getSourceFolder() {
		return projectFolder + File.separatorChar + SOURCE_FOLDER;
	}

	public String getConstraintFolder() {
		return projectFolder + File.separatorChar + CONSTRAINTS_FOLDER;
	}

	public String getIPCoreFolder() {
		return projectFolder + File.separatorChar + CORES_FOLDER;
	}

	public void addIPCore(String coreName) {
		IPCore core = new IPCore(coreName);
		if (ipCores.contains(core)) {
			ipCores.remove(core);
		}
		File coreGenFolder = new File(projectFolder + File.separatorChar + CORES_FOLDER);
		File[] fileList = coreGenFolder.listFiles();
		for (File f : fileList) {
			if (f.isFile()) {
				String name = f.getName();
				if (name.startsWith(coreName)) {
					if (name.endsWith(".v") || name.endsWith(".ngc")) {
						core.addFile(name);
					}
				}
			}
		}
		ipCores.add(core);
	}

	public void addExistingSourceFile(String file) {
		sourceFiles.add(file);
	}

	public void addExistingConstraintFile(String file, boolean lib) {
		constraintLib.put(file, Boolean.valueOf(lib));
		constraintFiles.add(file);
	}

	public void addExistingComponentFile(String file) {
		componentFiles.add(file);
		if (file.endsWith(".ngc"))
			componentFiles.add(file.substring(0, file.length() - 3) + "v");
	}

	public boolean setTopFile(String file) {
		if (sourceFiles.contains(file)) {
			topSource = file;
			return true;
		}
		return false;
	}

	public String getTop() {
		return topSource;
	}

	public HashSet<String> getSourceFiles() {
		return sourceFiles;
	}

	public HashSet<String> getConstraintFiles(boolean lib) {
		HashSet<String> hs = new HashSet<>();
		for (String s : constraintFiles)
			if (Boolean.valueOf(lib).equals(constraintLib.get(s)))
				hs.add(s);
		removeUnsupportedConstraints(hs);
		return hs;
	}

	private boolean endsWithExt(String str, String[] ext) {
		for (String e : ext)
			if (str.endsWith(e))
				return true;
		return false;
	}

	private void removeUnsupportedConstraints(HashSet<String> constraints) {
		String[] ext = boardType.getSupportedConstraintExtensions();
		for (Iterator<String> it = constraints.iterator(); it.hasNext();) {
			String c = it.next();
			if (!endsWithExt(c, ext)) {
				it.remove();
				Util.println("Constraint \"" + c + "\" is of an unsupported type. It will be ignored.", true);
			}
		}
	}

	public boolean isConstraintFromLib(String c) {
		return Boolean.TRUE.equals(constraintLib.get(c));
	}

	public HashSet<String> getComponentFiles(boolean debug) {
		if (debug) {
			HashSet<String> debugList = new HashSet<>(componentFiles);
			debugList.add("reg_interface_debug.luc");
			debugList.add("reg_interface.luc");
			debugList.add("wave_capture.luc");
			debugList.add("simple_dual_ram.v");
			return debugList;
		}

		return componentFiles;
	}

	public HashSet<IPCore> getIPCores() {
		return ipCores;
	}

	public String getProjectFolder() {
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

	public String getProjectFile() {
		return projectFolder + File.separatorChar + projectFile;
	}

	public boolean setBoardType(String type) {
		boardType = Board.getFromName(type);
		if (boardType == null) 
			return false;
		return true;
	}

	public void setProjectFolder(String folder) {
		projectFolder = folder;
	}

	public void setProjectFile(String file) {
		projectFile = file;
	}

	private class SortIgnoreCase implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
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

	private void addRemoveFile(CustomTree.TreeElement item, final FileType type) {
		MenuItem mi = new MenuItem(treeMenu, SWT.NONE);
		mi.setText("Remove " + item.getName());
		mi.setData(item.getName());
		mi.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String file = (String) ((MenuItem) e.getSource()).getData();
				boolean result = Util.askQuestion("Confirm Delete", "Are you sure you want to remove " + file + "?" + System.lineSeparator() + "This cannot be undone.");

				if (result) {
					switch (type) {
					case SOURCE:
						if (!removeSourceFile(file))
							Util.showError("Could not remove file!");
						break;
					case CONSTRAINT:
						if (!removeConstaintFile(file))
							Util.showError("Could not remove file!");
						break;
					case COMPONENT:
						if (!removeComponentFile(file))
							Util.showError("Could not remove component!");
						break;
					case CORE:
						if (!removeIPCore(file))
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
				MainWindow.mainWindow.openFile(getSourceFolder() + File.separatorChar + item.getName(), true);
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
					addRemoveFile(item, FileType.SOURCE);
			}
		}
	};

	private Listener constraintsListner = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CustomTree.TreeElement item = (CustomTree.TreeElement) event.data;
			if (event.button == 1 && !item.isNode()) {
				if (isConstraintFromLib(item.getName()))
					MainWindow.mainWindow.openFile(Locations.COMPONENTS + File.separatorChar + item.getName(), false);
				else
					MainWindow.mainWindow.openFile(getConstraintFolder() + File.separatorChar + item.getName(), true);
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
					addRemoveFile(item, FileType.CONSTRAINT);
			}
		}
	};

	private Listener componentsListner = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CustomTree.TreeElement item = (CustomTree.TreeElement) event.data;
			if (event.button == 1 && !item.isNode()) {
				MainWindow.mainWindow.openFile(Locations.COMPONENTS + File.separatorChar + item.getName(), false);
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
					addRemoveFile(item, FileType.COMPONENT);
			}
		}
	};

	private Listener coresListner = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CustomTree.TreeElement item = (CustomTree.TreeElement) event.data;
			if (event.button == 1 && !item.isNode()) {
				MainWindow.mainWindow.openFile(getFolder() + File.separatorChar + Project.CORES_FOLDER + File.separatorChar + item.getName(), true);
			} else if (event.button == 3) {
				for (MenuItem i : treeMenu.getItems())
					i.dispose();
				MenuItem mi = new MenuItem(treeMenu, SWT.NONE);
				mi.setText("Launch CoreGen");
				mi.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						MainWindow.mainWindow.getCoreGen().launch(Project.this);

					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
				if (item.isNode() && !item.getName().equals(CORES_PARENT))
					addRemoveFile(item, FileType.CORE);
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

			ArrayList<String> files = new ArrayList<String>(sourceFiles);
			Collections.sort(files, new SortIgnoreCase());
			for (int i = 0; i < files.size(); i++) {
				String source = files.get(i);
				if (sourceLeafs.size() < i + 1 || !sourceLeafs.get(i).getName().equals(source)) {
					TreeLeaf leaf = new TreeLeaf(source);
					sourceBranch.add(i, leaf);
					leaf.addClickListener(sourceListner);
				}
			}

			while (files.size() < sourceLeafs.size())
				sourceBranch.remove(sourceLeafs.size() - 1);

			int categoryIdx = 1;

			if (componentFiles.size() > 0) {
				if (projectNodes.size() < categoryIdx + 1 || !projectNodes.get(categoryIdx).getName().equals(COMPONENTS_PARENT)) {
					TreeNode compBranch = new TreeNode(COMPONENTS_PARENT);
					compBranch.addClickListener(componentsListner);
					project.add(categoryIdx, compBranch);
				}

				TreeNode compBranch = (TreeNode) projectNodes.get(categoryIdx++);
				List<TreeElement> compLeafs = compBranch.getChildren();

				files = new ArrayList<String>(componentFiles);
				Collections.sort(files, new SortIgnoreCase());
				for (int i = 0; i < files.size(); i++) {
					String comp = files.get(i);
					if (compLeafs.size() < i + 1 || !compLeafs.get(i).getName().equals(comp)) {
						TreeLeaf leaf = new TreeLeaf(comp);
						compBranch.add(i, leaf);
						leaf.addClickListener(componentsListner);
					}
				}
				while (files.size() < compLeafs.size())
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

					files = core.getFiles();
					for (int i = 0; i < files.size(); i++) {
						String file = files.get(i);
						if (coreParentLeafs.size() < i + 1 || !coreParentLeafs.get(i).getName().equals(file)) {
							TreeLeaf leaf = new TreeLeaf(file);
							coreParent.add(i, leaf);
							leaf.addClickListener(coresListner);
						}
					}
					while (files.size() < coreParentLeafs.size())
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

			files = new ArrayList<String>(constraintFiles);
			Collections.sort(files, new SortIgnoreCase());
			for (int i = 0; i < files.size(); i++) {
				String ucf = files.get(i);
				if (ucfLeafs.size() < i + 1 || !ucfLeafs.get(i).getName().equals(ucf)) {
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

	public void openXML(String xmlPath) throws ParseException, IOException {
		close();

		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(xmlPath);
		projectFolder = xmlFile.getParent();
		projectFile = xmlFile.getName();

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
							topSource = file.getText();
						}
						sourceFiles.add(file.getText());
						break;
					case Tags.constraint:
						att = file.getAttribute(Tags.Attributes.library);
						constraintLib.put(file.getText(), Boolean.valueOf(att != null && att.getValue().equals("true")));
						constraintFiles.add(file.getText());
						break;
					case Tags.component:
						componentFiles.add(file.getText());
						break;
					case Tags.core:
						final List<Element> cfiles = file.getChildren();
						String coreName = file.getAttributeValue(Tags.Attributes.name);
						if (coreName == null)
							throw new ParseException("Missing core name");

						IPCore ipCore = new IPCore(coreName);
						for (int k = 0; k < cfiles.size(); k++) {
							Element cfile = cfiles.get(k);
							switch (cfile.getName()) {
							case Tags.source:
								ipCore.addFile(cfile.getText());
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

		readDebugInfo();
		open = true;
	}

	private List<Module> getVerilogModules(String folder, String file) throws IOException {
		VerilogModuleListener converter = new VerilogModuleListener();
		List<Module> modules = converter.extractModules(new File(folder + File.separatorChar + file).getAbsolutePath());
		for (Module m : modules) {
			m.setFileName(file);
			m.setFolder(folder);
		}
		return modules;
	}

	private Module getLucidModule(String folder, String file) throws IOException {
		LucidModuleExtractor converter = new LucidModuleExtractor();
		Module m = converter.getModule(new File(folder + File.separatorChar + file).getAbsolutePath());
		if (m != null) {
			m.setFileName(file);
			m.setFolder(folder);
		}
		return m;
	}

	private void addGlobals(Collection<String> files, String folder) throws IOException {
		for (String file : files) {
			if (file.endsWith(".luc")) {
				globalExtractor.parseGlobals(new File(folder + File.separatorChar + file).getAbsolutePath());
			}
		}
	}

	public void updateGlobals() throws IOException {
		globalExtractor.reset();
		addGlobals(getSourceFiles(), getSourceFolder());
		addGlobals(getComponentFiles(false), Locations.COMPONENTS);

	}

	public List<SyntaxError> getGlobalErrors(String file) {
		return globalExtractor.getErrors(file);
	}

	public HashMap<String, List<Constant>> getGlobalConstants() {
		return globalExtractor.getConstants();
	}

	public HashMap<String, List<Struct>> getGlobalStructs() {
		return globalExtractor.getStructs();
	}

	private void addModules(ArrayList<Module> modules, Collection<File> files) throws IOException {
		for (File file : files) {
			if (file.getName().endsWith(".luc")) {
				Module m = getLucidModule(file.getParentFile().getPath(), file.getName());
				if (m != null)
					modules.add(m);
			} else if (file.getName().endsWith(".v")) {
				modules.addAll(getVerilogModules(file.getParentFile().getPath(), file.getName()));
			}
		}
	}

	private void addModules(ArrayList<Module> modules, Collection<String> files, String folder) throws IOException {
		for (String file : files) {
			if (file.endsWith(".luc")) {
				Module m = getLucidModule(folder, file);
				if (m != null)
					modules.add(m);
			} else if (file.endsWith(".v")) {
				List<Module> ml = getVerilogModules(folder, file);
				for (Module m : ml)
					if (m.getName().endsWith("_bb"))
						m.setNgc(true);
				modules.addAll(ml);
			} else if (file.endsWith(".ngc")) {

			}
		}
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
		return getModuleFromFile(getSourceFolder(), topSource, modules);
	}

	public ArrayList<Module> getModules(List<File> debugFiles) throws IOException {
		ArrayList<Module> modules = new ArrayList<Module>();
		addModules(modules, getSourceFiles(), getSourceFolder());
		addModules(modules, getComponentFiles(debugFiles != null), Locations.COMPONENTS);
		if (debugFiles != null) {
			addModules(modules, debugFiles);
		}

		String folder = getIPCoreFolder();
		for (IPCore ipcore : getIPCores())
			addModules(modules, ipcore.getFiles(), folder);

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

	private Module getModuleFromFile(String folder, String file, List<Module> list) {
		for (Module m : list)
			if (m.getFolder().equals(folder) && m.getFileName().equals(file))
				return m;

		return null;
	}

	private List<InstModule> getVerilogInstModules(InstModule im, List<Module> modules) throws IOException {
		VerilogModuleListener converter = new VerilogModuleListener();
		return converter.extractInstModules(im, modules);
	}

	private List<InstModule> getLucidInstModules(InstModule im, List<Module> modules) throws IOException {
		String folder = im.getType().getFolder();
		String file = im.getType().getFileName();
		LucidErrorChecker converter = new LucidErrorChecker(im);
		return converter.getInstModules(new File(folder + File.separatorChar + file).getAbsolutePath(), modules);
	}

	public List<InstModule> getModuleList(List<Module> modules, boolean mergeDupes, Module topModule) throws IOException {
		Queue<InstModule> queue = new LinkedList<>();
		List<InstModule> outList = new ArrayList<>();

		if (topModule == null) {
			Module top = getModuleFromFile(getSourceFolder(), topSource, modules);
			if (top == null) {
				Util.showError("Could not find top module file!");
				return null;
			}

			queue.add(new InstModule(top.getName(), top, null));
		} else {
			queue.add(new InstModule(topModule.getName(), topModule, null));
		}

		Set<IPCore> ipCores = getIPCores();

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

					// Skip IP Core files
					for (IPCore ip : ipCores) {
						String name = m.getType().getFileName();
						if (ip.getFiles().contains(name))
							add = false;
					}

					if (add && mergeDupes)
						for (InstModule om : outList) {
							if (om.equals(m)) {
								add = false;
								im.addChild(om);
								break;
							}
						}

					if (add) {
						queue.add(m);
						im.addChild(m);
					}
				}
			}
		}

		return outList;
	}

	public void saveAsXML(String folder, String name) throws IOException {
		String oldFolder = projectFolder;
		String oldFile = projectFile;

		projectFile = name + ".alp";
		projectFolder = folder;

		File srcDir = new File(oldFolder);
		File dstDir = new File(projectFolder);
		File oldProj = new File(projectFolder + File.separatorChar + oldFile);

		FileUtils.copyDirectory(srcDir, dstDir);
		oldProj.delete();

		String oldName = projectName;
		projectName = name;

		saveXML();

		projectName = oldName;
		projectFile = oldFile;
		projectFolder = oldFolder;
	}

	public void saveXML() throws IOException {
		saveXML(projectFolder + File.separatorChar + projectFile);
	}

	public void saveXML(String file) throws IOException {
		Element project = new Element(Tags.project);

		project.setAttribute(new Attribute(Tags.Attributes.name, projectName));
		project.setAttribute(new Attribute(Tags.Attributes.board, boardType.getName()));
		project.setAttribute(new Attribute(Tags.Attributes.language, language));
		Document doc = new Document(project);

		Element source = new Element(Tags.files);
		for (String sourceFile : sourceFiles) {
			Element ele = new Element(Tags.source).setText(sourceFile);
			if (sourceFile.equals(topSource))
				ele.setAttribute(new Attribute(Tags.Attributes.top, "true"));
			source.addContent(ele);
		}

		for (String ucfFile : constraintFiles) {
			Element ele = new Element(Tags.constraint).setText(ucfFile);
			if (Boolean.TRUE.equals(constraintLib.get(ucfFile)))
				ele.setAttribute(new Attribute(Tags.Attributes.library, "true"));
			source.addContent(ele);
		}

		for (String compFile : componentFiles) {
			source.addContent(new Element(Tags.component).setText(compFile));
		}

		for (IPCore core : ipCores) {
			Element coreElement = new Element(Tags.core).setAttribute(Tags.Attributes.name, core.getName());
			for (String corefile : core.getFiles()) {
				coreElement.addContent(new Element(Tags.source).setText(corefile));
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
			ObjectOutputStream e = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(projectFolder + File.separatorChar + "work" + File.separatorChar + "debug" + File.separator + "debug.data")));
			e.writeObject(dbi);
			e.close();
		} catch (Exception o) {
			Util.log.log(Level.INFO, "Failed to write debug info", o);
		}
	}

	public boolean readDebugInfo() {
		try {
			File debugFile = new File(projectFolder + File.separatorChar + "work" + File.separatorChar + "debug" + File.separator + "debug.data");
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
						Util.println("No errors detected.");
					}
				} catch (IOException e) {
				}
			}
		};
		thread.start();
	}

	private boolean checkforErrors(String folder, String file, boolean printErrors) throws IOException {
		boolean hasErrors = false;

		if (Util.hasErrorProvider(file)) {
			List<SyntaxError> errors = null;
			String fullPath = new File(folder + File.separatorChar + file).getAbsolutePath();
			if (file.endsWith(".luc")) {
				LucidErrorChecker errorChecker = new LucidErrorChecker(null);
				errors = errorChecker.getErrors(fullPath);
			} else if (file.endsWith(".v")) {
				VerilogErrorProvider errorChecker = new VerilogErrorProvider();
				errors = errorChecker.getErrors(fullPath);
			} else if (file.endsWith(".acf")) {
				AlchitryConstraintsErrorProvider errorChecker = new AlchitryConstraintsErrorProvider();
				errors = errorChecker.getErrors(fullPath);
			} else {
				Util.println("Unknown source file extension " + file + "!", true);
				return false;
			}

			List<SyntaxError> ge = getGlobalErrors(fullPath);

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
		String fullPath = new File(im.getType().getFolder() + File.separatorChar + im.getType().getFileName()).getAbsolutePath();
		List<SyntaxError> errors = VerilogLucidModuleFixer.getErrors(im, fullPath, modules, instModules);
		return addErrors(errors, im.getType().getFileName(), true);
	}

	public boolean checkForErrors() throws IOException {
		updateGlobals();
		String folder = getSourceFolder();
		List<Module> modules = getModules(null);
		List<InstModule> list = getModuleList(modules, true, null);
		boolean hasErrors = false;
		for (String file : getSourceFiles()) {
			hasErrors = hasErrors | checkforErrors(folder, file, true);
		}
		folder = getConstraintFolder();
		for (String file : getConstraintFiles(false)) {
			hasErrors = hasErrors | checkforErrors(folder, file, true);
		}
		for (InstModule im : list)
			if (!im.getType().isPrimitive() && im.getType().getFileName().endsWith(".v"))
				hasErrors = hasErrors | checkForIMErrors(im, modules, list);

		folder = Locations.COMPONENTS;
		for (String file : getComponentFiles(false)) {
			hasErrors = hasErrors | checkforErrors(folder, file, true);
		}
		for (String file : getConstraintFiles(true)) {
			hasErrors = hasErrors | checkforErrors(folder, file, true);
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

	public InstModule getLucidSourceTree() throws IOException {
		updateGlobals();
		String folder = getSourceFolder();
		List<Module> modules = getModules(null);
		List<InstModule> list = getModuleList(modules, false, null);

		for (String file : getSourceFiles())
			checkforErrors(folder, file, false);

		folder = Locations.COMPONENTS;
		for (String file : getComponentFiles(true))
			checkforErrors(folder, file, false);

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
						Util.print(e);
						Util.log.log(Level.SEVERE, "Exception with project builder!", e);
					}
				}
			};

			thread.start();
		} else {
			builder.build(Project.this, debug);
		}
	}

	public boolean isBuilding() {
		return isBusy() &&  (builder != null) &&  builder.isBuilding();
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
