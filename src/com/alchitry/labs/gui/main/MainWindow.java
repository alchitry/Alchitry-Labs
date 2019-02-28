package com.alchitry.labs.gui.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.alchitry.labs.Reporter;
import com.alchitry.labs.Settings;
import com.alchitry.labs.UpdateChecker;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.ComponentsDialog;
import com.alchitry.labs.gui.ConstraintsEditor;
import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.NewProjectDialog;
import com.alchitry.labs.gui.NewSourceDialog;
import com.alchitry.labs.gui.SerialPortSelector;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.WaveForm;
import com.alchitry.labs.gui.WelcomeDialog;
import com.alchitry.labs.gui.tools.ImageCapture;
import com.alchitry.labs.gui.tools.RegInterface;
import com.alchitry.labs.gui.tools.SerialMonitor;
import com.alchitry.labs.parsers.types.Constant;
import com.alchitry.labs.project.CoreGen;
import com.alchitry.labs.project.Project;
import com.alchitry.labs.project.SourceFile;
import com.alchitry.labs.style.ParseException;
import com.alchitry.labs.widgets.CustomConsole;
import com.alchitry.labs.widgets.CustomTabs;
import com.alchitry.labs.widgets.CustomTree;
import com.alchitry.labs.widgets.TabChild;

import jssc.SerialPortList;

public class MainWindow {
	public static final String VERSION = "1.0.1";
	public static final String LIB_VERSION = "1.0.0";

	protected final Display display = Display.getDefault();
	protected Shell shlAlchitryLabs;
	protected SashForm sideSashForm;
	protected SashForm bottomSashForm;
	protected CustomTabs tabFolder;
	protected CustomTree tree;
	protected static Project project;

	private int leftWidth, oldLeftWeight;
	private int bottomHeight, oldBottomWeight;
	protected ArrayList<TabChild> tabs;
	protected CustomConsole console;
	protected SerialMonitor monitor;
	protected ImageCapture imgCapture;
	protected RegInterface regInterface;
	protected MainMenu mainMenu;
	protected MainToolbar mainToolbar;

	public static MainWindow mainWindow;
	protected CoreGen coreGen;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) {
		parseCommand(args);

		Util.isGUI = true;
		try {
			mainWindow = new MainWindow();
			mainWindow.open();
		} catch (Exception e) {
			Util.log.log(Level.SEVERE, "", e);
			if (Util.getEnvType() != Util.ECLIPSE)
				Reporter.reportException(e);

			if (mainWindow != null)
				mainWindow.saveOnCrash();
		}
		return;
	}

	public MainWindow() {
	}

	public void close() {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				shlAlchitryLabs.close();
				if (monitor != null && !monitor.isDisposed())
					monitor.close();
			}
		});

	}

	public Shell getShell() {
		return shlAlchitryLabs;
	}

	public void saveOnCrash() {
		if (filesModified()) {
			Shell shell = new Shell(display);
			MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
			dialog.setText("Save files?");
			dialog.setMessage("Would you like to attempt to save your files before closing?");
			if (dialog.open() == SWT.OK) {
				if (!saveAll(false)) {
					dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("An error occured while trying to save your files :(");
					dialog.open();
				}
			}
		}

		display.dispose();
	}

	private static void parseCommand(String[] args) {
		if (args.length > 0) {
			switch (args[0]) {
			case "lin32":
				Util.setEnvType(Util.LIN32);
				break;
			case "lin64":
				Util.setEnvType(Util.LIN64);
				break;
			case "win32":
				Util.setEnvType(Util.WIN32);
				break;
			case "win64":
				Util.setEnvType(Util.WIN64);
				break;
			case "eclipse":
				Util.setEnvType(Util.ECLIPSE);
				break;
			}
		}
		

		if (Util.getEnvType() == Util.UNKNOWN) {
			if (args.length == 2 && args[0].equals("-u")) {
				try {
					UpdateChecker.copyLibrary(args[1]);
				} catch (IOException e) {
					System.exit(1);
				}
				System.exit(0);
			} else {
				System.err.println("Library value missing after -u!");
				System.exit(2);
			}
		}
	}

	public void setBuilding(final boolean building) {
		if (Util.isGUI)
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					mainToolbar.setBuilding(building);
				}
			});
	}

	private void upgrade() {
		new WelcomeDialog(shlAlchitryLabs, SWT.APPLICATION_MODAL).open();
		Settings.pref.put(Settings.VERSION, VERSION);
		Settings.pref.put(Settings.LIB_VERSION, LIB_VERSION);
		try {
			Settings.pref.flush();
		} catch (BackingStoreException e) {
		}
		if (Util.isWindows) {
			File updater = new File(Util.getWorkspace() + File.separator + "mojo-ide.exe");
			if (updater.exists())
				updater.delete();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		createContents();
		shlAlchitryLabs.open();
		shlAlchitryLabs.layout();

		// Settings.pref.put(Settings.LIB_VERSION, "1");

		if (!Settings.pref.get(Settings.VERSION, "").equals(VERSION)) {
			upgrade();
		}

		UpdateChecker.checkForUpdates();

		while (!shlAlchitryLabs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private boolean filesModified() {
		for (TabChild editor : tabs) {
			if (editor.isModified())
				return true;
		}
		return false;
	}

	public boolean saveAll(boolean ask) {
		for (TabChild editor : tabs) {
			if (editor instanceof StyledCodeEditor)
				switch (saveEditor((StyledCodeEditor) editor, ask)) {
				case SWT.YES:
				case SWT.NO:
					continue;
				case SWT.CANCEL:
				case SWT.ERROR:
					return false;
				}
		}
		return true;
	}

	public int saveEditor(StyledCodeEditor editor, boolean ask) {
		if (editor.isModified()) {
			int returnCode = SWT.YES;
			if (ask) {
				MessageBox dialog = new MessageBox(shlAlchitryLabs, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				dialog.setText(editor.getFileName() + " has been modified");
				dialog.setMessage("Do you want to save the changes to " + editor.getFileName() + "?");

				returnCode = dialog.open();
			}

			switch (returnCode) {
			case SWT.YES:
				if (!editor.save()) {
					Util.log.severe("Could not save file " + editor.getFileName());
					return SWT.ERROR;
				}
				return SWT.YES;
			case SWT.NO:
				return SWT.NO;
			case SWT.CANCEL:
			default:
				return SWT.CANCEL;
			}
		}
		return SWT.YES;
	}

	public boolean closeTab(TabChild tab) {
		if (tab instanceof StyledCodeEditor) {
			StyledCodeEditor editor = (StyledCodeEditor) tab;
			switch (saveEditor(editor, true)) {
			case SWT.YES:
			case SWT.NO:
				tabs.remove(editor);
				return true;
			case SWT.CANCEL:
			case SWT.ERROR:
				return false;
			}
		} else if (tab instanceof WaveForm) {
			tabs.remove(tab);
			return true;
		} else {
			Util.log.severe("Unknown TabChild type when closing tab in MainWindow!");
		}
		return false;
	}

	public void updateDirectoryLocation(String prompt, String startingDir, String prefName) {
		if (prompt != null)
			Util.showInfo(prompt);
		DirectoryDialog dialog = new DirectoryDialog(shlAlchitryLabs, SWT.OPEN | SWT.APPLICATION_MODAL);
		if (startingDir != null) {
			File current = new File(startingDir);
			if (current.getParent() != null)
				dialog.setFilterPath(current.getParent());
		}
		String result = dialog.open();
		if (result != null) {
			if (!result.endsWith(File.separator))
				result += File.separator;
			Settings.pref.put(prefName, result);
		}
	}

	public void updateFileLocation(String prompt, String startingDir, String prefName) {
		if (prompt != null)
			Util.showInfo(prompt);
		FileDialog dialog = new FileDialog(shlAlchitryLabs, SWT.OPEN | SWT.APPLICATION_MODAL);
		if (startingDir != null) {
			File current = new File(startingDir);
			if (current.getParent() != null)
				dialog.setFilterPath(current.getParent());
		}
		String result = dialog.open();
		if (result != null) {
			Settings.pref.put(prefName, result);
		}
	}

	public void updateISELocation() {
		updateDirectoryLocation("The next dialog will ask you for the location of where you installed ISE. Please point it to the "
				+ "directory whose name is the version number of ISE. This is the Xilinx/14.7 folder in most cases.", Util.getISELocation(), Settings.XILINX_LOC);
	}

	public void updateVivadoLocation() {
		updateDirectoryLocation(
				"The next dialog will ask you for the location of where you installed Vivado. Please point it to the "
						+ "directory whose name is the version number of Vivado. This is the Xilinx/Vivado/YEAR.MONTH folder in most cases.",
				Util.getVivadoLocation(), Settings.VIVADO_LOC);
	}

	public void updateIcecubeLocation() {
		updateDirectoryLocation("The next dialog will ask you for the location of where you installed iCEcube2. Please point it to the "
				+ "directory whose name is \"iCEcube2\". This is the lscc/iCEcube2 folder in most cases.", Util.getIceCubeFolder(), Settings.ICECUBE_LOC);
	}

	public void updateIcecubeLicenseLocation() {
		updateFileLocation("The next dialog will ask you for the location of iCEcube2's license file. You need to get your own file from Lattcie's website.",
				Util.getIceCubeLicenseFile(), Settings.ICECUBE_LICENSE);
	}
	
	public void updateYosysLocation() {
		updateFileLocation("The next dialog will ask for the location of the Yosys executable.", Settings.pref.get(Settings.YOSYS_LOC, null), Settings.YOSYS_LOC);
	}
	
	public void updateArachneLocation() {
		updateFileLocation("The next dialog will ask for the location of the Arachne PNR executable.", Settings.pref.get(Settings.ARACHNE_LOC, null), Settings.ARACHNE_LOC);
	}
	
	public void updateIcepackLocation() {
		updateFileLocation("The next dialog will ask for the location of the IcePack executable.", Settings.pref.get(Settings.ICEPACK_LOC, null), Settings.ICEPACK_LOC);
	}

	private void loadFonts() {
		int fontsLoaded = 0;
		String[] fonts = new String[] { "UbuntuMono-R.ttf", "UbuntuMono-RI.ttf", "UbuntuMono-B.ttf", "UbuntuMono-BI.ttf", "Ubuntu-R.ttf", "Ubuntu-RI.ttf", "Ubuntu-B.ttf",
				"Ubuntu-BI.ttf" };
		for (String font : fonts)
			try {
				fontsLoaded = display.loadFont(Util.createTmpFont(font)) ? fontsLoaded + 1 : fontsLoaded;
			} catch (IOException e) {
			}
		if (fontsLoaded != fonts.length) {
			Util.showError("Could not load the fonts! " + fontsLoaded + " out of " + fonts.length + " fonts were loaded.");
		}
	}

	private void saveSettings() {
		try {
			boolean max = shlAlchitryLabs.getMaximized();
			Settings.pref.putBoolean(Settings.MAXIMIZED, max);

			if (!max) {
				Rectangle r = shlAlchitryLabs.getBounds();
				Settings.pref.putInt(Settings.WINDOW_HEIGHT, r.height);
				Settings.pref.putInt(Settings.WINDOW_WIDTH, r.width);
			}

			int[] weights = sideSashForm.getWeights();
			Settings.pref.putInt(Settings.FILE_LIST_WIDTH,
					leftWidth = (int) Math.round((double) sideSashForm.getClientArea().width * (double) weights[0] / (double) (weights[0] + weights[1])));
			weights = bottomSashForm.getWeights();
			Settings.pref.putInt(Settings.CONSOLE_HEIGHT,
					bottomHeight = (int) Math.round((double) bottomSashForm.getClientArea().height * (double) weights[1] / (double) (weights[0] + weights[1])));

			if (project.isOpen())
				Settings.pref.put(Settings.OPEN_PROJECT, project.getProjectFile());
			else
				Settings.pref.remove(Settings.OPEN_PROJECT);

			Settings.pref.flush();
		} catch (BackingStoreException e1) {
			Util.log.severe("Failed to save settings! " + e1.getMessage());
		}
	}

	private void closeAllTabs() {
		int numEditors = tabs.size();
		for (int i = 0; i < numEditors; i++) {
			tabs.get(0).close();
		}
	}

	public void setTabFolder(CustomTabs tabs) {
		tabFolder = tabs;
	}

	public void setDefaultFolder() {
		Control[] children = sideSashForm.getChildren();
		Control sash = null;
		for (Control c : children)
			if (c instanceof SashForm || c instanceof CustomTabs)
				sash = c;
		if (sash == null)
			Util.log.severe("BUG couldn't find editor!");
		while (sash instanceof SashForm)
			sash = ((SashForm) sash).getChildren()[0];
		tabFolder = (CustomTabs) sash;
	}

	public CoreGen getCoreGen() {
		return coreGen;
	}

	public CustomTabs getTabFolder() {
		return tabFolder;
	}

	public List<TabChild> getTabs() {
		return tabs;
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		Util.setDisplay(display);
		shlAlchitryLabs = new Shell();
		Util.setShell(shlAlchitryLabs);
		loadFonts();
		Theme.init(display);
		Images.loadImages(display);
		tabs = new ArrayList<>();
		shlAlchitryLabs.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if (!saveAll(true)) {
					e.doit = false;
					return;
				}

				if (project != null && project.isOpen())
					try {
						project.saveXML();
					} catch (IOException e1) {
						if (!Util.askQuestion("Failed to save project file!", "Close anyways?")) {
							e.doit = false;
							return;
						}
					}

				if (monitor != null && !monitor.isDisposed())
					monitor.close();

				coreGen.kill();
				saveSettings();
				shlAlchitryLabs.getImage().dispose();
				Theme.dispose();
			}
		});

		shlAlchitryLabs.setImage(SWTResourceManager.getImage(MainWindow.class, "/images/icon.png"));

		shlAlchitryLabs.setMinimumSize(550, 200);
		shlAlchitryLabs.setText("Alchitry Labs Version " + VERSION);
		shlAlchitryLabs.setLayout(new GridLayout(1, false));

		int height = Settings.pref.getInt(Settings.WINDOW_HEIGHT, 700);
		int width = Settings.pref.getInt(Settings.WINDOW_WIDTH, 1000);
		shlAlchitryLabs.setSize(width, height);

		boolean max = Settings.pref.getBoolean(Settings.MAXIMIZED, false);
		if (max)
			shlAlchitryLabs.setMaximized(true);

		shlAlchitryLabs.setBackground(Theme.windowBackgroundColor);
		shlAlchitryLabs.setForeground(Theme.windowForgroundColor);

		mainMenu = new MainMenu(this);
		mainMenu.build();

		mainToolbar = new MainToolbar(this);
		mainToolbar.build();

		bottomSashForm = new SashForm(shlAlchitryLabs, SWT.VERTICAL);
		bottomSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		bottomSashForm.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int height = bottomSashForm.getClientArea().height;
				int[] weights = bottomSashForm.getWeights();

				double perBottom = (double) bottomHeight / (double) height;

				if (perBottom < 0.8) {
					weights[1] = (int) (perBottom * 1000.0);
					weights[0] = 1000 - weights[1];
				} else {
					weights[1] = 800;
					weights[0] = 200;
				}

				// oldWeights must be set before form.setWeights
				oldBottomWeight = weights[0];
				bottomSashForm.setWeights(weights);
			}
		});
		bottomSashForm.setBackground(Theme.windowBackgroundColor);

		sideSashForm = new SashForm(bottomSashForm, SWT.NONE);
		sideSashForm.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int width = sideSashForm.getClientArea().width;
				int[] weights = sideSashForm.getWeights();

				double perLeft = (double) leftWidth / (double) width;

				if (perLeft < 0.8) {
					weights[0] = (int) (perLeft * 1000.0);
					weights[1] = 1000 - weights[0];
				} else {
					weights[0] = 800;
					weights[1] = 200;
				}

				// oldWeights must be set before form.setWeights
				oldLeftWeight = weights[0];
				sideSashForm.setWeights(weights);
			}
		});
		sideSashForm.setBackground(Theme.windowBackgroundColor);

		final ScrolledComposite sc = new ScrolledComposite(sideSashForm, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(false);

		tree = new CustomTree(sc);
		sc.setContent(tree);
		tree.setBackground(Theme.editorBackgroundColor);
		tree.setForeground(Theme.editorForegroundColor);

		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		tree.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point p = ((CustomTree) event.widget).computeSize(SWT.DEFAULT, SWT.DEFAULT);
				sc.setMinSize(p);
			}
		});

		tree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int[] weights = sideSashForm.getWeights();
				if (oldLeftWeight != weights[0]) {
					oldLeftWeight = weights[0];
					leftWidth = (int) Math.round((double) sideSashForm.getClientArea().width * (double) weights[0] / (double) (weights[0] + weights[1]));
				}

				weights = bottomSashForm.getWeights();

				if (oldBottomWeight != weights[1]) {
					oldBottomWeight = weights[1];
					bottomHeight = (int) Math.round((double) bottomSashForm.getClientArea().height * (double) weights[1] / (double) (weights[0] + weights[1]));
				}
			}
		});

		project = new Project(shlAlchitryLabs);
		project.setTree(tree);
		String oldProject = Settings.pref.get(Settings.OPEN_PROJECT, null);

		tabFolder = new CustomTabs(sideSashForm, SWT.NONE);

		console = new CustomConsole(bottomSashForm, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);

		bottomSashForm.setWeights(new int[] { 8, 2 });

		Util.setConsole(console);

		leftWidth = Settings.pref.getInt(Settings.FILE_LIST_WIDTH, 200);
		bottomHeight = Settings.pref.getInt(Settings.CONSOLE_HEIGHT, 200);

		coreGen = new CoreGen();

		if (oldProject != null)
			openProject(oldProject);

		openFile(null, true);

		// openSVG();
	}

	public void headlessInit() {
		project = new Project();
		coreGen = new CoreGen();
	}

	public void programProject(boolean flash, boolean verify) {
		if (!project.isOpen()) {
			Util.showError("A project must be opened before you can load it!");
			return;
		}
		if (project.isBusy()) {
			Util.showError("Operation already in progress!");
			return;
		}

		project.load(flash, verify);
	}

	public void checkProject() {
		if (!project.isOpen()) {
			Util.showError("A project must be opened before you can check it!");
			return;
		}
		if (project.isBusy()) {
			Util.showError("Operation in progress!");
			return;
		}
		if (Util.isGUI && !saveAll(false)) {
			Util.showError("Could not save all open tabs before build!");
			MessageBox box = new MessageBox(shlAlchitryLabs, SWT.YES | SWT.NO);

			box.setMessage("Continue with the build anyway?");
			box.setText("All files not saved...");
			if (box.open() != SWT.YES) {
				return;
			}
		}
		project.checkProject();
	}

	public void buildProject() {
		if (!project.isOpen()) {
			Util.showError("A project must be opened before you can build it!");
			return;
		}
		if (project.isBuilding()) {
			project.stopBuild();
			return;
		}
		if (project.isBusy()) {
			Util.showError("Operation already in progress!");
			return;
		}
		if (Util.isGUI && !saveAll(false)) {
			Util.showError("Could not save all open tabs before build!");
			MessageBox box = new MessageBox(shlAlchitryLabs, SWT.YES | SWT.NO);

			box.setMessage("Continue with the build anyway?");
			box.setText("All files not saved...");
			if (box.open() != SWT.YES) {
				return;
			}
		}
		project.build(false);
	}

	public void addNewFile() {
		if (project.isOpen()) {
			NewSourceDialog dialog = new NewSourceDialog(shlAlchitryLabs, project.getBoard());
			shlAlchitryLabs.setEnabled(false);
			SourceFile file = dialog.open();
			if (file != null) {
				String filePath = null;
				switch (file.type) {
				case SourceFile.VERILOG:
				case SourceFile.LUCID:
					if ((filePath = project.addNewSourceFile(file.fileName)) == null)
						Util.showError("Could not create new source file!");
					break;
				case SourceFile.CONSTRAINT:
					if ((filePath = project.addNewConstraintFile(file.fileName)) == null)
						Util.showError("Could not create constraint file!");

					break;
				}

				if (filePath != null)
					openFile(filePath, true);
			}
			try {
				project.saveXML();
			} catch (IOException e) {
				Util.showError("Failed to save project file!");
			}
			shlAlchitryLabs.setEnabled(true);
		} else {
			Util.showError("A project must be open to add a new file.");
		}
	}

	public int getIndex(TabChild e) {
		return tabs.indexOf(e);
	}

	public TabChild getTabChild(int idx) {
		if (idx < 0 || idx >= tabs.size())
			return null;
		return tabs.get(idx);
	}

	public void addEditor(StyledCodeEditor e) {
		tabs.add(e);
	}

	public boolean isSideSash(Composite c) {
		return c == sideSashForm;
	}

	public void addComponents() {
		ComponentsDialog diag = new ComponentsDialog(shlAlchitryLabs);
		diag.open();
	}

	private void projectChanged() {
		project.setShell(shlAlchitryLabs);
		project.setTree(tree);
		project.updateTree();
		project.openTree();
		mainMenu.build();
		mainToolbar.build();
	}

	public void createNewProject() {
		NewProjectDialog dialog = new NewProjectDialog(shlAlchitryLabs, SWT.DIALOG_TRIM, false);
		shlAlchitryLabs.setEnabled(false);
		Project p = dialog.open();
		if (p != null) {
			closeAllTabs();
			if (project != null)
				project.close();
			project = p;

			projectChanged();
		}
		shlAlchitryLabs.setEnabled(true);
	}

	public void openProject(String path) {
		try {
			project.openXML(path);
			closeAllTabs();
			tabFolder.opened = false;
		} catch (ParseException e1) {
			MessageBox box = new MessageBox(shlAlchitryLabs, SWT.ICON_ERROR | SWT.OK);
			box.setText("Error opening file!");
			box.setMessage("Encountered an error while parsing " + path + " the error was: " + e1.getMessage());
			box.open();
		} catch (IOException e1) {
			MessageBox box = new MessageBox(shlAlchitryLabs, SWT.ICON_ERROR | SWT.OK);
			box.setText("Error opening file!");
			box.setMessage("Encountered an error while opening " + path + " the error was: " + e1.getMessage());
			box.open();
		}

		projectChanged();
	}

	public void openProject() {
		FileDialog dialog = new FileDialog(shlAlchitryLabs, SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.alp", "*" });
		dialog.setFilterPath(Util.getWorkspace());
		String path = dialog.open();
		if (path != null)
			openProject(path);
	}

	public void openProjectSilent(String path) throws ParseException, IOException {
		project.openXML(path);
	}

	public void selectSerialPort() {
		String[] ports = SerialPortList.getPortNames();
		if (ports.length > 0) {
			SerialPortSelector dialog = new SerialPortSelector(shlAlchitryLabs, ports);
			String port = dialog.open();
			if (port != null)
				Settings.pref.put(Settings.MOJO_PORT, port);
		} else {
			MessageBox box = new MessageBox(shlAlchitryLabs, SWT.ICON_ERROR | SWT.OK);
			box.setText("No Serial Ports Detected!");
			box.setMessage("No serial ports were detected. Make sure your Mojo is connected and the drivers are loaded.");
			box.open();
		}
	}

	public boolean openFile(String path, boolean write) {
		for (TabChild tab : tabs) {
			if (tab instanceof StyledCodeEditor) {
				StyledCodeEditor editor = (StyledCodeEditor) tab;

				if (editor.getFilePath() != null && editor.getFilePath().equals(path)) {
					editor.grabFocus();
					return true;
				}
			}
		}

		final StyledCodeEditor codeEditor = new StyledCodeEditor(tabFolder, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL, path, write);

		if (codeEditor.isOpen()) {
			tabs.add(codeEditor);
			if (path != null) {
				if (!tabFolder.opened && !tabFolder.getTabChildren().get(0).isModified()) {
					tabFolder.close(0);
				}
				tabFolder.opened = true;
			}

			return true;
		}
		return false;
	}

	public void openSVG() {
		ConstraintsEditor svg = new ConstraintsEditor(tabFolder);
		tabs.add(svg);
		svg.grabFocus();
		tabFolder.opened = true;
	}

	public boolean openWave() {
		final WaveForm waves = new WaveForm(tabFolder, false);
		tabs.add(waves);
		waves.grabFocus();
		if (!tabFolder.opened && !tabFolder.getTabChildren().get(0).isModified()) {
			tabFolder.close(0);
		}
		tabFolder.opened = true;
		return true;

	}

	public static HashMap<String, List<Constant>> getGlobalConstants() {
		if (getOpenProject() != null)
			return getOpenProject().getGlobalConstants();
		return new HashMap<>();
	}

	public static Project getOpenProject() {
		if (project != null && project.isOpen())
			return project;
		return null;
	}

	public void updateErrors() {
		try {
			project.updateGlobals();
		} catch (IOException e1) {
			Util.log.severe("Failed to update project globals!");
			return;
		}
		for (TabChild e : tabs)
			if (e instanceof StyledCodeEditor)
				((StyledCodeEditor) e).updateErrors();
	}

	public void enableMonitor(final boolean enable) {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (monitor != null && !monitor.isDisposed()) {
					monitor.enable(enable);
				}
			}
		});

	}

	public String getEditorText(String path) {
		if (Util.isGUI)
			for (TabChild tc : tabs) {
				if (tc instanceof StyledCodeEditor) {
					final StyledCodeEditor editor = (StyledCodeEditor) tc;
					if (path.equals(editor.getFilePath())) {
						final StringBuilder sb = new StringBuilder();
						Util.syncExec(new Runnable() {
							@Override
							public void run() {
								sb.append(editor.getText());
							}
						});
						return sb.toString();
					}
				}
			}
		return null;
	}
}
