package com.alchitry.labs.gui;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.alchitry.labs.Reporter;
import com.alchitry.labs.Settings;
import com.alchitry.labs.UpdateChecker;
import com.alchitry.labs.Util;
import com.alchitry.labs.boards.Board;
import com.alchitry.labs.gui.tools.ImageCapture;
import com.alchitry.labs.gui.tools.RegInterface;
import com.alchitry.labs.gui.tools.SerialMonitor;
import com.alchitry.labs.hardware.MojoFlasher;
import com.alchitry.labs.lucid.Constant;
import com.alchitry.labs.project.CoreGen;
import com.alchitry.labs.project.Project;
import com.alchitry.labs.project.SourceFile;
import com.alchitry.labs.style.ParseException;
import com.alchitry.labs.widgets.CustomButton;
import com.alchitry.labs.widgets.CustomConsole;
import com.alchitry.labs.widgets.CustomTabs;
import com.alchitry.labs.widgets.CustomTree;
import com.alchitry.labs.widgets.TabChild;

import jssc.SerialPortList;

public class MainWindow {
	public static final String VERSION = "1.0";
	public static final String LIB_VERSION = "1.0";

	protected final Display display = Display.getDefault();
	protected Shell shlAlchitryLabs;
	protected SashForm sideSashForm;
	protected SashForm bottomSashForm;
	protected CustomTabs tabFolder;
	protected CustomTree tree;
	protected static Project project;

	private int leftWidth, oldLeftWeight;
	private int bottomHeight, oldBottomWeight;
	private ArrayList<TabChild> tabs;
	private CustomConsole console;
	private MojoFlasher flasher;
	private SerialMonitor monitor;
	private ImageCapture imgCapture;
	private RegInterface regInterface;

	private CustomButton buildbtn;

	public static MainWindow mainWindow;
	private CoreGen coreGen;

	private static String projToOpen;
	// private MojoLoader loader;

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
			if (Util.ideType != Util.ECLIPSE)
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

	private void saveOnCrash() {
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
				Util.ideType = Util.LIN32;
				break;
			case "lin64":
				Util.ideType = Util.LIN64;
				break;
			case "win32":
				Util.ideType = Util.WIN32;
				break;
			case "win64":
				Util.ideType = Util.WIN64;
				break;
			case "eclipse":
				Util.ideType = Util.ECLIPSE;
				break;
			}
		}
	}

	public void setBuilding(final boolean building) {
		if (Util.isGUI)
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (building) {
						buildbtn.setIcon(Images.cancelIcon);
						buildbtn.setIconHover(Images.cancelIconHover);
						buildbtn.setToolTipText("Cancel Build");
					} else {
						buildbtn.setIcon(Images.buildIcon);
						buildbtn.setIconHover(Images.buildIconHover);
						buildbtn.setToolTipText("Build Project");
					}
					buildbtn.redraw();
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

	// private void updatePorts(MenuItem portMenu) {
	// ArrayList<String> ports = MojoLoader.listPorts();
	// for (MenuItem i : items)
	// i.dispose();
	// if (ports.size() != 0) {
	// Object[] array = ports.toArray();
	// String selectedPort = Settings.settings.get(Settings.MOJO_PORT, "");
	//
	// for (int i = 0; i < array.length; i++) {
	// MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
	// menuItem.setText((String) array[i]);
	// menuItem.addSelectionListener(new SelectionListener() {
	// @Override
	// public void widgetDefaultSelected(SelectionEvent event) {
	// widgetSelected(event);
	// }
	//
	// @Override
	// public void widgetSelected(SelectionEvent event) {
	// Settings.settings.put(Settings.MOJO_PORT,
	// ((MenuItem) event.widget).getText());
	// }
	// });
	// if (menuItem.getText().equals(selectedPort))
	// menuItem.setSelection(true);
	// }
	// } else {
	// MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
	// menuItem.setText("No Serial Ports!");
	// }
	// }

	private boolean filesModified() {
		for (TabChild editor : tabs) {
			if (editor.isModified())
				return true;
		}
		return false;
	}

	private boolean saveAll(boolean ask) {
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

	private void updateXilinxLocation() {
		Util.showInfo("ISE Location", "The next dialog will ask you for the location of where you installed ISE. "
				+ "Please point it to the directory whose name is the version number of ISE. This is the Xilinx/14.7 " + "folder in most cases.");
		DirectoryDialog dialog = new DirectoryDialog(shlAlchitryLabs, SWT.OPEN | SWT.PRIMARY_MODAL);
		if (Util.isISESet() && Util.getISELocation() != null) {
			File current = new File(Util.getISELocation());
			if (current.getParent() != null)
				dialog.setFilterPath(current.getParent());
		}
		String result = dialog.open();
		if (result != null) {
			if (!result.endsWith(File.separator))
				result += File.separator;
			Settings.pref.put(Settings.XILINX_LOC, result);
		}
	}

	private void loadFonts() {
		int fontsLoaded = 0;
		String[] fonts = new String[] { "UbuntuMono-R.ttf", "UbuntuMono-RI.ttf", "UbuntuMono-B.ttf", "UbuntuMono-BI.ttf" };
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
		Theme.initColors(display);
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

		shlAlchitryLabs.setMinimumSize(450, 178);
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

		createMenuBar();

		Composite composite = new Composite(shlAlchitryLabs, SWT.NONE);
		composite.setBackground(Theme.windowBackgroundColor);
		composite.setForeground(Theme.windowForgroundColor);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(rl_composite);

		CustomButton newbtn = new CustomButton(composite, SWT.NONE);
		newbtn.setIcon(Images.fileIcon);
		newbtn.setIconHover(Images.fileIconHover);
		newbtn.setToolTipText("New File");
		newbtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				addNewFile();
			}
		});

		CustomButton savebtn = new CustomButton(composite, SWT.NONE);
		savebtn.setIcon(Images.saveIcon);
		savebtn.setIconHover(Images.saveIconHover);
		savebtn.setToolTipText("Save File");
		savebtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TabChild tc = tabFolder.getSelectedControl();
				if (tc instanceof StyledCodeEditor)
					saveEditor((StyledCodeEditor) tc, false);
			}
		});

		CustomButton saveallbtn = new CustomButton(composite, SWT.NONE);
		saveallbtn.setIcon(Images.saveAllIcon);
		saveallbtn.setIconHover(Images.saveAllIconHover);
		saveallbtn.setToolTipText("Save All");
		saveallbtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				saveAll(false);
			}
		});

		CustomButton checkbtn = new CustomButton(composite, SWT.NONE);
		checkbtn.setIcon(Images.checkIcon);
		checkbtn.setIconHover(Images.checkIconHover);
		checkbtn.setToolTipText("Check Syntax");
		checkbtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				checkProject();
			}
		});

		buildbtn = new CustomButton(composite, SWT.NONE);
		setBuilding(false);
		buildbtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				buildProject();
			}
		});

		CustomButton debugbtn = new CustomButton(composite, SWT.NONE);
		debugbtn.setIcon(Images.debugIcon);
		debugbtn.setIconHover(Images.debugIconHover);
		debugbtn.setToolTipText("Debug Project");
		debugbtn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (!project.isOpen()) {
					Util.showError("A project must be opened before you can build it!");
					return;
				}
				if (project.isBusy()) {
					Util.showError("Can't debug while operation is in progress!");
					return;
				}
				if (!saveAll(false)) {
					Util.showError("Could not save all open tabs before debugging!");
					MessageBox box = new MessageBox(shlAlchitryLabs, SWT.YES | SWT.NO);

					box.setMessage("Continue anyway?");
					box.setText("All files not saved...");
					if (box.open() != SWT.YES) {
						return;
					}
				}
				project.build(true);
			}
		});

		CustomButton loadtempbtn = new CustomButton(composite, SWT.NONE);
		loadtempbtn.setIcon(Images.loadTempIcon);
		loadtempbtn.setIconHover(Images.loadTempIconHover);
		loadtempbtn.setToolTipText("Program (Temporary)");
		loadtempbtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				programProject(false, false);
			}
		});

		CustomButton loadbtn = new CustomButton(composite, SWT.NONE);
		loadbtn.setIcon(Images.loadIcon);
		loadbtn.setIconHover(Images.loadIconHover);
		loadbtn.setToolTipText("Program (Flash)");
		loadbtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				programProject(true, event.button == 2);
			}
		});

		CustomButton erasebtn = new CustomButton(composite, SWT.NONE);
		erasebtn.setIcon(Images.eraseIcon);
		erasebtn.setIconHover(Images.eraseIconHover);
		erasebtn.setToolTipText("Erase");
		erasebtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (project.isBusy()) {
					Util.showError("Operation already in progress!");
					return;
				}
				project.erase();
			}
		});

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
		if (projToOpen != null)
			oldProject = projToOpen;
		if (oldProject != null)
			try {
				project.openXML(oldProject);
				project.updateTree();
				project.openTree();
			} catch (ParseException | IOException e1) {
				Util.showError("Could not open project file " + oldProject);
				Util.log.severe("Error: could not open old project file " + oldProject);
			}

		tabFolder = new CustomTabs(sideSashForm, SWT.NONE);

		console = new CustomConsole(bottomSashForm, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);

		bottomSashForm.setWeights(new int[] { 8, 2 });

		Util.setConsole(console);

		leftWidth = Settings.pref.getInt(Settings.FILE_LIST_WIDTH, 200);
		bottomHeight = Settings.pref.getInt(Settings.CONSOLE_HEIGHT, 200);

		flasher = new MojoFlasher();

		coreGen = new CoreGen();

		openFile(null, true);
	}

	public void headlessInit() {
		project = new Project();
		flasher = new MojoFlasher();
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
			NewSourceDialog dialog = new NewSourceDialog(shlAlchitryLabs);
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

	private void createMenuBar() {
		Menu menu = new Menu(shlAlchitryLabs, SWT.BAR);
		shlAlchitryLabs.setMenuBar(menu);

		/******************** File Menu ***********************/

		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		MenuItem mntmNewProject = new MenuItem(menu_1, SWT.NONE);
		mntmNewProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewProject();
			}
		});
		mntmNewProject.setText("New Project...");

		MenuItem mntmOpenProject = new MenuItem(menu_1, SWT.NONE);
		mntmOpenProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openProject();
			}
		});
		mntmOpenProject.setText("Open Project...");

		MenuItem mntmImportFile = new MenuItem(menu_1, SWT.NONE);
		mntmImportFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!project.isOpen()) {
					Util.showError("A project must be open to import a file!");
					return;
				}
				FileDialog dialog = new FileDialog(shlAlchitryLabs, SWT.MULTI);
				dialog.setFilterExtensions(new String[] { "*.luc;*.v;*.ucf", "*" });
				String path = dialog.open();
				if (path != null) {
					String[] files = dialog.getFileNames();
					String dir = dialog.getFilterPath();
					for (String f : files) {
						if (!project.importFile(dir + File.separator + f))
							Util.showError("Failed to import \"" + f + "\"");
					}
					project.updateTree();
				}
			}
		});
		mntmImportFile.setText("Import Files...");

		MenuItem mntmOpenFile = new MenuItem(menu_1, SWT.NONE);
		mntmOpenFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shlAlchitryLabs, SWT.MULTI);
				dialog.setFilterExtensions(new String[] { "*.luc", "*.v", "*" });
				String path = dialog.open();
				if (path != null) {
					String[] files = dialog.getFileNames();
					String dir = dialog.getFilterPath();
					for (String f : files)
						openFile(dir + File.separator + f, true);
				}
			}
		});
		mntmOpenFile.setText("Open File...");

		MenuItem mntmSaveFile = new MenuItem(menu_1, SWT.NONE);
		mntmSaveFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((StyledCodeEditor) (tabFolder.getSelectedControl())).save();
			}
		});
		mntmSaveFile.setText("Save");

		MenuItem mntmCloneProject = new MenuItem(menu_1, SWT.NONE);
		mntmCloneProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewProjectDialog dialog = new NewProjectDialog(shlAlchitryLabs, SWT.DIALOG_TRIM, true);
				shlAlchitryLabs.setEnabled(false);
				Project p = dialog.open();
				if (p != null) {
					try {
						project.saveAsXML(p.getProjectFolder(), p.getProjectName());
						openProject(p.getProjectFolder() + File.separatorChar + p.getProjectName() + ".mojo");
					} catch (IOException e1) {
						Util.showError("Failed to clone project!");
						e1.printStackTrace();
					}
				}
				shlAlchitryLabs.setEnabled(true);
			}
		});
		mntmCloneProject.setText("Clone Project");

		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
		mntmExit.setText("Exit");

		/******************** Project Menu ***********************/

		MenuItem mntmProject = new MenuItem(menu, SWT.CASCADE);
		mntmProject.setText("Project");

		final Menu menu_3 = new Menu(mntmProject);
		mntmProject.setMenu(menu_3);

		MenuItem mntmAddComponent = new MenuItem(menu_3, SWT.NONE);
		mntmAddComponent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (project == null || !project.isOpen()) {
					Util.showError("You need to open or create a project first!");
					return;
				}
				addComponents();
			}
		});
		mntmAddComponent.setText("Add Components...");

		MenuItem mntmCoreGen = new MenuItem(menu_3, SWT.NONE);
		mntmCoreGen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (project == null || !project.isOpen()) {
					Util.showError("You need to open or create a project first!");
					return;
				}
				coreGen.launch(project);
			}
		});
		mntmCoreGen.setText("Launch CoreGen");

		/******************** Tools Menu ***********************/

		MenuItem mntmTools = new MenuItem(menu, SWT.CASCADE);
		mntmTools.setText("Tools");

		final Menu menu_4 = new Menu(mntmTools);
		mntmTools.setMenu(menu_4);

		MenuItem mntmFlash = new MenuItem(menu_4, SWT.NONE);
		mntmFlash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String port = Settings.pref.get(Settings.MOJO_PORT, null);
				if (port == null) {
					Util.showError("You need to select the serial port the Mojo is connected to in the settings menu.");
					return;
				}

				BoardSelector selector = new BoardSelector(shlAlchitryLabs, SWT.APPLICATION_MODAL);
				Board b = selector.open();
				if (b != null)
					flasher.flashMojo(port, b);
			}
		});
		mntmFlash.setText("Flash Firmware...");

		MenuItem mntmMonitor = new MenuItem(menu_4, SWT.NONE);
		mntmMonitor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (monitor == null || monitor.isDisposed()) {
					if (project.isBusy()) {
						Util.showError("Operation already in progress!");
					} else {
						if (!display.isDisposed())
							monitor = new SerialMonitor(display);
					}
				} else {
					monitor.setFocus();
				}
			}
		});
		mntmMonitor.setText("Serial Port Monitor...");

		MenuItem mntmRegInt = new MenuItem(menu_4, SWT.NONE);
		mntmRegInt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (display.isDisposed())
					return;

				if (regInterface == null || regInterface.isDisposed()) {
					regInterface = new RegInterface(display);

				} else {
					regInterface.setFocus();
				}
			}
		});
		mntmRegInt.setText("Register Interface...");

		MenuItem mntmImageCapture = new MenuItem(menu_4, SWT.NONE);
		mntmImageCapture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (imgCapture == null || imgCapture.isDisposed()) {
					imgCapture = new ImageCapture(display);

				} else {
					imgCapture.setFocus();
				}
			}
		});
		mntmImageCapture.setText("Image Capture...");

		MenuItem mntmWaveCapture = new MenuItem(menu_4, SWT.NONE);
		mntmWaveCapture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openWave();
			}
		});
		mntmWaveCapture.setText("Wave Capture...");

		/******************** Settings Menu ***********************/

		MenuItem mntmSettings = new MenuItem(menu, SWT.CASCADE);
		mntmSettings.setText("Settings");

		final Menu menu_2 = new Menu(mntmSettings);
		mntmSettings.setMenu(menu_2);

		MenuItem mntmSerialPort = new MenuItem(menu_2, SWT.NONE);
		mntmSerialPort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectSerialPort();
			}
		});
		mntmSerialPort.setText("Serial Port...");

		MenuItem mntmPlanaheadLocation = new MenuItem(menu_2, SWT.NONE);
		mntmPlanaheadLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateXilinxLocation();
			}
		});
		mntmPlanaheadLocation.setText("ISE Location...");

		MenuItem mntmTheme = new MenuItem(menu_2, SWT.NONE);
		mntmTheme.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ThemeSelectorDialog dialog = new ThemeSelectorDialog(shlAlchitryLabs);
				dialog.open();
			}
		});
		mntmTheme.setText("Choose Theme...");

		/******************** Help Menu ***********************/

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		final Menu menu_5 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_5);

		MenuItem mntmFeedback = new MenuItem(menu_5, SWT.NONE);
		mntmFeedback.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FeedbackDialog feedbackDialog = new FeedbackDialog(shlAlchitryLabs, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.CLOSE);
				EmailMessage message = feedbackDialog.open();
				if (message != null) {
					Reporter.sendFeedback(message);
				}
			}
		});
		mntmFeedback.setText("Send Feedback...");

		MenuItem mntmAbout = new MenuItem(menu_5, SWT.NONE);
		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new WelcomeDialog(shlAlchitryLabs, SWT.APPLICATION_MODAL).open();
			}
		});
		mntmAbout.setText("About Alchitry Labs");
	}

	public void addComponents() {
		ComponentsDialog diag = new ComponentsDialog(shlAlchitryLabs);
		diag.open();
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
			project.setShell(shlAlchitryLabs);
			project.setTree(tree);
			project.updateTree();
		}
		shlAlchitryLabs.setEnabled(true);
	}

	private void openProject(String path) {

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

		project.updateTree();
		project.openTree();
	}

	public void openProject() {
		FileDialog dialog = new FileDialog(shlAlchitryLabs, SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.mojo", "*" });
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
		if (project.isOpen())
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
