package com.alchitry.labs.gui.main;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.alchitry.labs.Reporter;
import com.alchitry.labs.Settings;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.BoardSelector;
import com.alchitry.labs.gui.EmailMessage;
import com.alchitry.labs.gui.FeedbackDialog;
import com.alchitry.labs.gui.NewProjectDialog;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.ThemeSelectorDialog;
import com.alchitry.labs.gui.WelcomeDialog;
import com.alchitry.labs.gui.tools.ImageCapture;
import com.alchitry.labs.gui.tools.RegInterface;
import com.alchitry.labs.gui.tools.SerialMonitor;
import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.hardware.flashers.AuFlasher;
import com.alchitry.labs.hardware.flashers.CuFlasher;
import com.alchitry.labs.hardware.flashers.MojoFlasher;
import com.alchitry.labs.project.Project;

public class MainMenu {
	MainWindow parent;
	Menu menu;
	Board board;
	MenuItem iceCube;
	MenuItem iceStorm;

	public MainMenu(MainWindow parent) {
		this.parent = parent;
	}

	public void build() {
		if (menu != null)
			menu.dispose();

		Shell shell = parent.getShell();
		menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		Project p = MainWindow.project;
		if (p != null)
			board = p.getBoard();
		else
			board = null;

		buildFileMenu();
		buildProjectMenu();
		buildToolsMenu();
		buildSettingsMenu();
		buildHelpMenu();
	}

	private MenuItem createItem(Menu m, int style, String text, SelectionAdapter selectedListener) {
		MenuItem menuItem = new MenuItem(m, style);
		menuItem.addSelectionListener(selectedListener);
		menuItem.setText(text);
		return menuItem;
	}

	private MenuItem createCheckItem(Menu m, String text, SelectionAdapter selectedListener) {
		return createItem(m, SWT.CHECK, text, selectedListener);
	}

	private MenuItem createItem(Menu m, String text, SelectionAdapter selectedListener) {
		return createItem(m, SWT.NONE, text, selectedListener);
	}

	private Menu createSubMenu(Menu m, String text) {
		MenuItem menuItem = new MenuItem(m, SWT.CASCADE);
		menuItem.setText(text);

		Menu subMenu = new Menu(menuItem);
		menuItem.setMenu(subMenu);

		return subMenu;
	}

	private void buildFileMenu() {
		Menu subMenu = createSubMenu(menu, "File");
		
		createItem(subMenu, "New File...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.addNewFile();
			}
		});
		
		createItem(subMenu, "Open File...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.MULTI);
				dialog.setFilterExtensions(new String[] { "*.luc", "*.v", "*" });
				String path = dialog.open();
				if (path != null) {
					String[] files = dialog.getFileNames();
					String dir = dialog.getFilterPath();
					for (String f : files)
						parent.openFile(dir + File.separator + f, true);
				}
			}
		});
		
		createItem(subMenu, "Import Files...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MainWindow.getOpenProject() == null) {
					Util.showError("A project must be open to import a file!");
					return;
				}
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.MULTI);
				dialog.setFilterExtensions(new String[] { "*.luc;*.v;*.ucf", ".pcf", "*" });
				String path = dialog.open();
				if (path != null) {
					String[] files = dialog.getFileNames();
					String dir = dialog.getFilterPath();
					for (String f : files) {
						if (!MainWindow.getOpenProject().importFile(dir + File.separator + f))
							Util.showError("Failed to import \"" + f + "\"");
					}
					MainWindow.getOpenProject().updateTree();
				}
			}
		});
		
		createItem(subMenu, "Save", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((StyledCodeEditor) (parent.tabFolder.getSelectedControl())).save();
			}
		});

		createItem(subMenu, "New Project...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.createNewProject();
			}
		});

		createItem(subMenu, "Open Project...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.openProject();
			}
		});

		createItem(subMenu, "Clone Project...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MainWindow.project == null || !MainWindow.project.isOpen()) {
					Util.showError("You need to open or create a project first!");
					return;
				}
				NewProjectDialog dialog = new NewProjectDialog(parent.getShell(), SWT.DIALOG_TRIM, true);
				parent.getShell().setEnabled(false);
				Project p = dialog.open();
				if (p != null) {
					try {
						if (MainWindow.project.saveAsXML(p.getProjectFolder(), p.getProjectName()))
							parent.openProject(p.getProjectFolder() + File.separatorChar + p.getProjectName() + ".alp");
						else
							Util.showError("Failed to clone project! Project folder was null!");
					} catch (IOException e1) {
						Util.showError("Failed to clone project!");
						e1.printStackTrace();
					}
				}
				parent.getShell().setEnabled(true);
			}
		});

		createItem(subMenu, "Exit", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.close();
			}
		});
	}

	private void buildProjectMenu() {
		Menu subMenu = createSubMenu(menu, "Project");

		createItem(subMenu, "Add Components...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MainWindow.project == null || !MainWindow.project.isOpen()) {
					Util.showError("You need to open or create a project first!");
					return;
				}
				parent.addComponents();
			}
		});

		if (Board.isType(board, Board.MOJO))
			createItem(subMenu, "Launch CoreGen", new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (MainWindow.project == null || !MainWindow.project.isOpen()) {
						Util.showError("You need to open or create a project first!");
						return;
					}
					parent.coreGen.launch(MainWindow.project);
				}
			});
	}

	private void buildToolsMenu() {
		Menu subMenu = createSubMenu(menu, "Tools");

		createItem(subMenu, "Flash Firmware...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BoardSelector selector = new BoardSelector(parent.getShell(), SWT.APPLICATION_MODAL);
				Board b = selector.open();
				if (b == null)
					return;

				switch (b.getType()) {
				case Board.AU:
					new AuFlasher().flash();
					break;
				case Board.CU:
					new CuFlasher().flash();
					break;
				case Board.MOJO:
					new MojoFlasher().flash();
					break;
				}
			}
		});

		createItem(subMenu, "Serial Port Monitor...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (parent.monitor == null || parent.monitor.isDisposed()) {
					if (MainWindow.project.isBusy()) {
						Util.showError("Operation already in progress!");
					} else {
						if (!parent.display.isDisposed())
							parent.monitor = new SerialMonitor(parent.display);
					}
				} else {
					parent.monitor.setFocus();
				}
			}
		});

		createItem(subMenu, "Register Interface...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (parent.display.isDisposed())
					return;

				if (parent.regInterface == null || parent.regInterface.isDisposed()) {
					parent.regInterface = new RegInterface(parent.display);

				} else {
					parent.regInterface.setFocus();
				}
			}
		});

		createItem(subMenu, "Image Capture...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (parent.imgCapture == null || parent.imgCapture.isDisposed()) {
					parent.imgCapture = new ImageCapture(parent.display);

				} else {
					parent.imgCapture.setFocus();
				}
			}
		});

		if (Board.isType(board, Board.MOJO))
			createItem(subMenu, "Wave Capture...", new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					parent.openWave();
				}
			});
	}

	private void buildSettingsMenu() {
		Menu subMenu = createSubMenu(menu, "Settings");

		if (Board.isType(board, Board.MOJO))
			createItem(subMenu, "Serial Port...", new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					parent.selectSerialPort();
				}
			});

		createItem(subMenu, "ISE Location...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.updateISELocation();
			}
		});

		createItem(subMenu, "Vivado Location...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.updateVivadoLocation();
			}
		});

		createItem(subMenu, "iCEcube2 Location...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.updateIcecubeLocation();
			}
		});

		createItem(subMenu, "iCEcube2 License Location...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.updateIcecubeLicenseLocation();
			}
		});

		createItem(subMenu, "IceStorm Locations...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.updateYosysLocation();
				parent.updateArachneLocation();
				parent.updateIcepackLocation();
			}
		});

		Menu cuBuilder = createSubMenu(subMenu, "Cu Toolchain");

		iceCube = createCheckItem(cuBuilder, "iCEcube2", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Settings.pref.putBoolean(Settings.USE_ICESTORM, false);
				updateCuToolchainSelection();
			}
		});

		iceStorm = createCheckItem(cuBuilder, "IceStorm", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Settings.pref.putBoolean(Settings.USE_ICESTORM, true);
				updateCuToolchainSelection();
			}
		});

		updateCuToolchainSelection();

		createItem(subMenu, "Choose Theme...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ThemeSelectorDialog dialog = new ThemeSelectorDialog(parent.shlAlchitryLabs);
				dialog.open();
			}
		});
	}

	private void updateCuToolchainSelection() {
		boolean useStorm = Settings.pref.getBoolean(Settings.USE_ICESTORM, false);
		iceCube.setSelection(!useStorm);
		iceStorm.setSelection(useStorm);
	}

	private void buildHelpMenu() {
		Menu subMenu = createSubMenu(menu, "Help");

		createItem(subMenu, "Send Feedback...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FeedbackDialog feedbackDialog = new FeedbackDialog(parent.shlAlchitryLabs, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.CLOSE);
				EmailMessage message = feedbackDialog.open();
				if (message != null) {
					Reporter.sendFeedback(message);
				}
			}
		});

		createItem(subMenu, "About...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new WelcomeDialog(parent.getShell(), SWT.APPLICATION_MODAL).open();
			}
		});
	}
}
