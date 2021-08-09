package com.alchitry.labs.gui.main

import com.alchitry.labs.Settings
import com.alchitry.labs.UpdateChecker
import com.alchitry.labs.Util
import com.alchitry.labs.Util.assembleFile
import com.alchitry.labs.Util.reportException
import com.alchitry.labs.Util.showError
import com.alchitry.labs.gui.*
import com.alchitry.labs.gui.tools.ImageCapture
import com.alchitry.labs.gui.tools.RegInterface
import com.alchitry.labs.gui.tools.SerialMonitor
import com.alchitry.labs.hardware.boards.Board
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.FileDialog
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import java.io.IOException

class MainMenu {
    companion object {
        fun createSelectionAdapter(block: (SelectionEvent) -> Unit): SelectionAdapter {
            return object : SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent) {
                    block.invoke(e)
                }
            }
        }
    }

    private var menu: Menu? = null
    private var board: Board? = null
    private var iceCube: MenuItem? = null
    private var iceStorm: MenuItem? = null

    fun build() {
        menu?.dispose()
        menu = Menu(MainWindow.shell, SWT.BAR)
        MainWindow.shell.menuBar = menu
        board = MainWindow.project?.board
        buildFileMenu()
        buildProjectMenu()
        buildToolsMenu()
        buildSettingsMenu()
        buildHelpMenu()
    }

    private fun createItem(m: Menu, style: Int, text: String, selectedListener: SelectionAdapter): MenuItem {
        val menuItem = MenuItem(m, style)
        menuItem.addSelectionListener(selectedListener)
        menuItem.text = text
        return menuItem
    }

    private fun createCheckItem(m: Menu, text: String, selectedListener: SelectionAdapter): MenuItem {
        return createItem(m, SWT.CHECK, text, selectedListener)
    }

    private fun createItem(m: Menu, text: String, selectedListener: SelectionAdapter): MenuItem {
        return createItem(m, SWT.NONE, text, selectedListener)
    }

    private fun createSubMenu(m: Menu?, text: String): Menu {
        val menuItem = MenuItem(m, SWT.CASCADE)
        menuItem.text = text
        val subMenu = Menu(menuItem)
        menuItem.menu = subMenu
        return subMenu
    }

    private fun buildFileMenu() {
        val subMenu = createSubMenu(menu, "File")
        createItem(subMenu, "New File", createSelectionAdapter { MainWindow.addNewFile() })
        createItem(subMenu, "Open File", createSelectionAdapter {
            val dialog = FileDialog(MainWindow.shell, SWT.MULTI)
            dialog.filterExtensions = arrayOf("*.luc", "*.v", "*")
            val path = dialog.open()
            if (path != null) {
                val files = dialog.fileNames
                val dir = dialog.filterPath
                for (f in files) MainWindow.openFile(assembleFile(dir, f), true)
            }
        })
        createItem(subMenu, "Import Files", createSelectionAdapter {
            if (MainWindow.project == null) {
                showError("A project must be open to import a file!")
                return@createSelectionAdapter
            }
            val dialog = FileDialog(MainWindow.shell, SWT.MULTI)
            dialog.filterExtensions = arrayOf(Util.allFileSuffixes.joinToString(";") { "*$it" }, "*")
            val path = dialog.open()
            if (path != null) {
                val files = dialog.fileNames
                val dir = dialog.filterPath
                for (f in files) {
                    if (MainWindow.project?.importFile(assembleFile(dir, f)) == null) showError("Failed to import \"$f\"")
                }
                MainWindow.project?.updateTree()
            }
        })
        createItem(subMenu, "Save", createSelectionAdapter { (MainWindow.tabFolder.selectedControl as StyledCodeEditor).save() })
        createItem(subMenu, "Print\tCtrl+P", createSelectionAdapter { MainWindow.printOpen() })
        createItem(subMenu, "Exit", createSelectionAdapter { MainWindow.close() })
    }

    private fun buildProjectMenu() {
        val subMenu = createSubMenu(menu, "Project")
        createItem(subMenu, "New Project", createSelectionAdapter {
            MainWindow.createNewProject()
        })
        createItem(subMenu, "Open Project", createSelectionAdapter {
            MainWindow.openProjectDialog()
        })
        createItem(subMenu, "Clone Project", createSelectionAdapter {
            MainWindow.project.let { project ->
                if (project == null) {
                    showError("You need to open or create a project first!")
                    return@createSelectionAdapter
                }
                val dialog = NewProjectDialog(MainWindow.shell, clone = project)
                MainWindow.shell.isEnabled = false
                dialog.open()?.let { MainWindow.openProject(it) }
                MainWindow.shell.isEnabled = true
            }
        })
        createItem(subMenu, "Add Components", createSelectionAdapter {
            if (MainWindow.project == null) {
                showError("You need to open or create a project first!")
                return@createSelectionAdapter
            }
            MainWindow.addComponents()
        })
        if (Board.isType(board, Board.MOJO)) createItem(subMenu, "Launch CoreGen", createSelectionAdapter {
            if (MainWindow.project == null) {
                showError("You need to open or create a project first!")
                return@createSelectionAdapter
            }
            MainWindow.coreGen.launch(MainWindow.project)
        })
        if (Board.isType(board, Board.AU or Board.AU_PLUS)) createItem(subMenu, "Vivado IP Catalog", createSelectionAdapter {
            MainWindow.project.let { project ->
                if (project == null) {
                    showError("You need to open or create a project first!")
                    return@createSelectionAdapter
                }
                MainWindow.vivadoIP.launch(project)
            }
        })
        if (Board.isType(board, Board.AU or Board.AU_PLUS)) createItem(subMenu, "Add Memory Controller", createSelectionAdapter {
            MainWindow.project.let { project ->
                if (project == null) {
                    showError("You need to open or create a project first!")
                    return@createSelectionAdapter
                }
                try {
                    MainWindow.vivadoIP.generateMigCore(project)
                } catch (e1: InterruptedException) {
                    reportException(e1, "Failed to generate MIG core!")
                    showError("Failed to generate MIG core!")
                } catch (e1: IOException) {
                    reportException(e1, "Failed to generate MIG core!")
                    showError("Failed to generate MIG core!")
                }
            }
        })
    }

    private fun buildToolsMenu() {
        val subMenu = createSubMenu(menu, "Tools")
        createItem(subMenu, "Serial Port Monitor", createSelectionAdapter {
            MainWindow.monitor.let { monitor ->
                if (monitor?.isDisposed != false) {
                    if (MainWindow.project?.isBusy == true) {
                        showError("Operation already in progress!")
                    } else {
                        if (!MainWindow.display.isDisposed) MainWindow.monitor = SerialMonitor(MainWindow.display)
                    }
                } else {
                    monitor.setFocus()
                }
            }
        })
        createItem(subMenu, "Register Interface", createSelectionAdapter {
            MainWindow.regInterface.let { regInterface ->
                if (MainWindow.display.isDisposed) return@createSelectionAdapter
                if (regInterface?.isDisposed != false) {
                    MainWindow.regInterface = RegInterface(MainWindow.shell)
                } else {
                    regInterface.setFocus()
                }
            }
        })
        createItem(subMenu, "Image Capture", createSelectionAdapter {
            MainWindow.imgCapture.let { imgCapture ->
                if (imgCapture?.isDisposed != false) {
                    MainWindow.imgCapture = ImageCapture(MainWindow.display)
                } else {
                    imgCapture.setFocus()
                }
            }
        })
        if (Board.isType(board, Board.MOJO or Board.AU or Board.AU_PLUS)) createItem(subMenu, "Wave Capture", createSelectionAdapter {
            MainWindow.openWave()
        })
    }

    private fun buildSettingsMenu() {
        val subMenu = createSubMenu(menu, "Settings")
        createItem(subMenu, "ISE Location", createSelectionAdapter {
            MainWindow.updateISELocation()
        })
        createItem(subMenu, "Vivado Location", createSelectionAdapter {
            MainWindow.updateVivadoLocation()
        })
        createItem(subMenu, "iCEcube2 Location", createSelectionAdapter {
            MainWindow.updateIcecubeLocation()
        })
        createItem(subMenu, "iCEcube2 License Location", createSelectionAdapter {
            MainWindow.updateIcecubeLicenseLocation()
        })
        createItem(subMenu, "IceStorm Locations", createSelectionAdapter {
            MainWindow.updateYosysLocation()
            MainWindow.updateArachneLocation()
            MainWindow.updateIcepackLocation()
        })
        val cuBuilder = createSubMenu(subMenu, "Cu Toolchain")
        iceCube = createCheckItem(cuBuilder, "iCEcube2", createSelectionAdapter {
            Settings.USE_ICESTORM = false
            updateCheckMenu(cuBuilder.items, it.widget as MenuItem)
        })
        iceStorm = createCheckItem(cuBuilder, "IceStorm", createSelectionAdapter {
            Settings.USE_ICESTORM = true
            updateCheckMenu(cuBuilder.items, it.widget as MenuItem)
        })
        updateCuToolchainSelection()
        createItem(subMenu, "Choose Theme", createSelectionAdapter {
            val dialog = ThemeSelectorDialog(MainWindow.shell)
            dialog.open()
        })
        val fontMenu = createSubMenu(subMenu, "Editor Font Size")
        listOf(0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f).forEach { scale ->
            createCheckItem(fontMenu, "${scale}x", createSelectionAdapter {
                Settings.FONT_SCALE = scale
                Util.showInfo("Restart the IDE to update the font size.")
                updateCheckMenu(fontMenu.items, it.widget as MenuItem)
            })
        }
        val fontPercent = Settings.FONT_SCALE
        for (mi in fontMenu.items) {
            val mip = mi.text.substring(0, mi.text.length - 1).toFloat()
            mi.selection = mip == fontPercent
        }

        val updateMenu = createSubMenu(subMenu, "Check for Updates")
        createCheckItem(updateMenu, "Never", createSelectionAdapter {
            Settings.CHECK_FOR_UPDATES = false
            updateCheckMenu(updateMenu.items, it.widget as MenuItem)
        }).also { it.selection = !Settings.CHECK_FOR_UPDATES }
        createCheckItem(updateMenu, "Stable", createSelectionAdapter {
            Settings.BETA_UPDATES = false
            Settings.CHECK_FOR_UPDATES = true
            updateCheckMenu(updateMenu.items, it.widget as MenuItem)
            UpdateChecker.checkForUpdates()
        }).also { it.selection = Settings.CHECK_FOR_UPDATES && !Settings.BETA_UPDATES }
        createCheckItem(updateMenu, "Beta", createSelectionAdapter {
            Settings.BETA_UPDATES = true
            Settings.CHECK_FOR_UPDATES = true
            updateCheckMenu(updateMenu.items, it.widget as MenuItem)
            UpdateChecker.checkForUpdates()
        }).also { it.selection = Settings.CHECK_FOR_UPDATES && Settings.BETA_UPDATES }

        val reportingMenu = createSubMenu(subMenu, "Anonymous Error Reporting")
        createCheckItem(reportingMenu, "Enable", createSelectionAdapter {
            Settings.ERROR_REPORTING = true
            updateCheckMenu(reportingMenu.items, it.widget as MenuItem)
        }).also { it.selection = Settings.ERROR_REPORTING }
        createCheckItem(reportingMenu, "Disable", createSelectionAdapter {
            Settings.ERROR_REPORTING = false
            updateCheckMenu(reportingMenu.items, it.widget as MenuItem)
        }).also { it.selection = !Settings.ERROR_REPORTING }

        /*
		
		if (Board.isType(board, Board.MOJO)) {
			MenuItem menuItem = new MenuItem(subMenu, SWT.CASCADE);
			menuItem.setText("Serial Port");

			Menu portMenu = new Menu(menuItem);
			menuItem.setMenu(portMenu);
			
			SelectionAdapter portSelectedAdapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Settings.pref.put(Settings.SERIAL_PORT, ((MenuItem) e.widget).getText());
				}
			};

			menuItem.addArmListener(new ArmListener() {

				@Override
				public void widgetArmed(ArmEvent e) {
					for (MenuItem mi : portMenu.getItems()) 
						mi.dispose();
					for (SerialPort p : SerialPort.getCommPorts()) 
						createCheckItem(portMenu, p.getDescriptivePortName(), portSelectedAdapter);
					String currentPort = Settings.pref.get(Settings.SERIAL_PORT, null);
					for (MenuItem mi : portMenu.getItems()) 
						mi.setSelection(mi.getText().equals(currentPort));
				}
			});
		}
		
		*/
    }

    private fun updateCheckMenu(items: Array<MenuItem>, selectedItem: MenuItem) {
        for (mi in items) mi.selection = mi === selectedItem
    }

    private fun updateCuToolchainSelection() {
        val useStorm = Settings.USE_ICESTORM
        iceCube!!.selection = !useStorm
        iceStorm!!.selection = useStorm
    }

    private fun buildHelpMenu() {
        val subMenu = createSubMenu(menu, "Help")
        createItem(subMenu, "About", createSelectionAdapter {
            WelcomeDialog(MainWindow.shell).open()
        })
    }
}