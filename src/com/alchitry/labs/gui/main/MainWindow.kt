package com.alchitry.labs.gui.main

import com.alchitry.labs.Settings
import com.alchitry.labs.UpdateChecker
import com.alchitry.labs.Util
import com.alchitry.labs.VERSION
import com.alchitry.labs.gui.*
import com.alchitry.labs.gui.tools.ImageCapture
import com.alchitry.labs.gui.tools.RegInterface
import com.alchitry.labs.gui.tools.SerialMonitor
import com.alchitry.labs.gui.util.Images
import com.alchitry.labs.parsers.types.Constant
import com.alchitry.labs.project.CoreGen
import com.alchitry.labs.project.Project
import com.alchitry.labs.project.SourceFile
import com.alchitry.labs.project.VivadoIP
import com.alchitry.labs.widgets.CustomConsole
import com.alchitry.labs.widgets.CustomTabs
import com.alchitry.labs.widgets.CustomTree
import com.alchitry.labs.widgets.TabChild
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.custom.ScrolledComposite
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.events.ControlEvent
import org.eclipse.swt.events.ShellAdapter
import org.eclipse.swt.events.ShellEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import org.eclipse.wb.swt.SWTResourceManager
import java.io.File
import java.io.IOException
import java.util.*
import java.util.prefs.BackingStoreException
import kotlin.math.roundToInt

object MainWindow {
    var project: Project? = null

    @JvmStatic
    val globalConstants: HashMap<String, List<Constant>>
        get() = project?.globalConstants ?: HashMap()

    @JvmStatic
    lateinit var shell: Shell
    lateinit var sideSashForm: SashForm
    lateinit var bottomSashForm: SashForm
    lateinit var tabFolder: CustomTabs
    private lateinit var tree: CustomTree
    private var leftWidth = 0
    private var oldLeftWeight = 0
    private var bottomHeight = 0
    private var oldBottomWeight = 0
    val tabs = ArrayList<TabChild>()
    private lateinit var console: CustomConsole
    val display: Display = Display.getDefault()
    var monitor: SerialMonitor? = null
    var imgCapture: ImageCapture? = null
    var regInterface: RegInterface? = null
    private lateinit var mainMenu: MainMenu
    private lateinit var mainToolbar: MainToolbar
    val coreGen: CoreGen = CoreGen()
    val vivadoIP: VivadoIP = VivadoIP()
    var lastActiveEditor: StyledCodeEditor? = null

    fun close() {
        runBlocking(Dispatchers.SWT.immediate) {
            shell.close()
            monitor?.close()
        }
    }

    fun saveOnCrash() {
        if (filesModified()) {
            val shell = Shell(display)
            var dialog = MessageBox(shell, SWT.ICON_QUESTION or SWT.OK or SWT.CANCEL)
            dialog.text = "Save files?"
            dialog.message = "Would you like to attempt to save your files before closing?"
            if (dialog.open() == SWT.OK) {
                if (!saveAll(false)) {
                    dialog = MessageBox(shell, SWT.ICON_ERROR or SWT.OK)
                    dialog.text = "Error"
                    dialog.message = "An error occurred while trying to save your files :("
                    dialog.open()
                }
            }
        }
        display.dispose()
    }

    fun setBuilding(building: Boolean) {
        if (Util.isGUI) GlobalScope.launch(Dispatchers.SWT) { mainToolbar.setBuilding(building) }
    }

    private fun upgrade() {
        GlobalScope.launch(Dispatchers.SWT) {
            WelcomeDialog(shell).open()
        }
        Settings.VERSION = VERSION
        try {
            Settings.commit()
        } catch (e: BackingStoreException) {
        }
        if (Util.isWindows) {
            val updater = Util.assembleFile(Util.workspace, "mojo-ide.exe")
            if (updater.exists()) updater.delete()
        }
    }

    private fun promptSettings() {
        var prompted = false

        if (!Settings.ERROR_REPORTING_PROMPTED) {
            Settings.ERROR_REPORTING_PROMPTED = true
            Settings.ERROR_REPORTING = Util.askQuestion("Help us improve the IDE by enabling anonymous error reporting?", "Anonymous Reporting")
            prompted = true
        }

        if (!Settings.BETA_UPDATES_PROMPTED) {
            Settings.BETA_UPDATES_PROMPTED = true
            Settings.BETA_UPDATES = Util.askQuestion("Help us improve the IDE by getting beta updates?", "Beta Updates").also { if (it) Settings.CHECK_FOR_UPDATES = true }
            prompted = true
        }

        if (prompted) mainMenu.build()
    }

    /**
     * Open the window.
     */
    fun open() {
        createContents()
        if (shell.isDisposed) return
        shell.open()
        shell.layout()

        // FIXME
        //   Cannot access 'pref': it is private in 'Settings'
        //   Unresolved reference: LIB_VERSION
        // Settings.pref.put(Settings.LIB_VERSION, "1");
        // if (Settings.VERSION != VERSION) {
        //     upgrade()
        // }

        promptSettings()

        UpdateChecker.checkForUpdates()

        Settings.OPEN_PROJECT?.let { openProject(File(it)) }
        openFile(null, true)

        if (project == null)
            GlobalScope.launch(Dispatchers.SWT) {
                if (Util.askQuestion("Open existing project (yes) or create a new project (no)?"))
                    openProjectDialog()
                else
                    createNewProject()
                if (project == null)
                    close()
            }

        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
    }

    private fun filesModified(): Boolean {
        for (editor in tabs) {
            if (editor.isModified) return true
        }
        return false
    }

    fun saveAll(ask: Boolean): Boolean {
        for (editor in tabs) {
            if (editor is StyledCodeEditor) when (saveEditor(editor, ask)) {
                SWT.YES, SWT.NO -> continue
                SWT.CANCEL, SWT.ERROR -> return false
            }
        }
        return true
    }

    fun saveEditor(editor: StyledCodeEditor, ask: Boolean): Int {
        if (editor.isModified) {
            var returnCode = SWT.YES
            if (ask) {
                val dialog = MessageBox(this.shell, SWT.ICON_QUESTION or SWT.YES or SWT.NO or SWT.CANCEL)
                dialog.text = editor.fileName + " has been modified"
                dialog.message = "Do you want to save the changes to " + editor.fileName + "?"
                returnCode = dialog.open()
            }
            return when (returnCode) {
                SWT.YES -> {
                    if (!editor.save()) {
                        Util.logger.severe("Could not save file " + editor.fileName)
                        return SWT.ERROR
                    }
                    SWT.YES
                }
                SWT.NO -> SWT.NO
                SWT.CANCEL -> SWT.CANCEL
                else -> SWT.CANCEL
            }
        }
        return SWT.YES
    }

    fun closeTab(tab: TabChild?): Boolean {
        when (tab) {
            is StyledCodeEditor -> {
                when (saveEditor(tab, true)) {
                    SWT.YES, SWT.NO -> {
                        tabs.remove(tab)
                        return true
                    }
                    SWT.CANCEL, SWT.ERROR -> return false
                }
            }
            is WaveForm -> {
                tabs.remove(tab)
                return true
            }
            else -> {
                Util.logger.severe("Unknown TabChild type when closing tab in MainWindow!")
            }
        }
        return false
    }

    private fun updateDirectoryLocation(prompt: String?, startingDir: String?): String? {
        if (prompt != null) Util.showInfo(prompt)
        val dialog = DirectoryDialog(this.shell, SWT.OPEN or SWT.APPLICATION_MODAL)
        if (startingDir != null) {
            val current = File(startingDir)
            if (current.parent != null) dialog.filterPath = current.parent
        }
        var result: String? = dialog.open()

        if (result?.endsWith(File.separator) == false) result += File.separator
        return result
    }

    private fun updateFileLocation(prompt: String?, startingDir: String?): String? {
        if (prompt != null) Util.showInfo(prompt)
        val dialog = FileDialog(this.shell, SWT.OPEN or SWT.APPLICATION_MODAL)
        if (startingDir != null) {
            val current = File(startingDir)
            if (current.parent != null) dialog.filterPath = current.parent
        }
        return dialog.open()
    }

    fun updateISELocation() {
        updateDirectoryLocation("The next dialog will ask you for the location of where you installed ISE. Please point it to the "
                + "directory whose name is the version number of ISE. This is the Xilinx/14.7 folder in most cases.", Util.iSELocation)?.also { Settings.XILINX_LOC = it }
    }

    fun updateVivadoLocation() {
        updateDirectoryLocation("The next dialog will ask you for the location of where you installed Vivado. Please point it to the "
                + "directory whose name is the version number of Vivado. This is the Xilinx/Vivado/YEAR.MONTH folder in most cases.",
                Util.vivadoLocation)?.also { Settings.VIVADO_LOC = it }
    }

    fun updateIcecubeLocation() {
        updateDirectoryLocation("The next dialog will ask you for the location of where you installed iCEcube2. Please point it to the "
                + "directory whose name is \"iCEcube2\". This is the lscc/iCEcube2 folder in most cases.", Util.iceCubeFolder)?.also { Settings.ICECUBE_LOC = it }
    }

    fun updateIcecubeLicenseLocation() {
        updateFileLocation("The next dialog will ask you for the location of iCEcube2's license file. You need to get your own file from Lattcie's website.",
                Util.iceCubeLicenseFile)?.also { Settings.ICECUBE_LICENSE = it }
    }

    fun updateYosysLocation() {
        updateFileLocation("The next dialog will ask for the location of the Yosys executable.", Settings.YOSYS_LOC)?.also { Settings.YOSYS_LOC = it }
    }

    fun updateArachneLocation() {
        updateFileLocation("The next dialog will ask for the location of the Arachne PNR executable.", Settings.ARACHNE_LOC)?.also { Settings.ARACHNE_LOC = it }
    }

    fun updateIcepackLocation() {
        updateFileLocation("The next dialog will ask for the location of the IcePack executable.", Settings.ICEPACK_LOC)?.also { Settings.ICEPACK_LOC = it }
    }

    private fun loadFonts() {
        var fontsLoaded = 0
        val fonts = arrayOf("UbuntuMono-R.ttf", "UbuntuMono-RI.ttf", "UbuntuMono-B.ttf", "UbuntuMono-BI.ttf", "Ubuntu-R.ttf", "Ubuntu-RI.ttf", "Ubuntu-B.ttf",
                "Ubuntu-BI.ttf")
        for (font in fonts) try {
            fontsLoaded = if (display.loadFont(Util.createTmpFont(font))) fontsLoaded + 1 else fontsLoaded
        } catch (e: IOException) {
        }
        if (fontsLoaded != fonts.size) {
            Util.showError("Could not load the fonts! " + fontsLoaded + " out of " + fonts.size + " fonts were loaded.")
        }
    }

    private fun saveSettings() {
        try {
            val max = shell.maximized
            Settings.MAXIMIZED = max
            if (!max) {
                val r = shell.bounds
                Settings.WINDOW_HEIGHT = r.height
                Settings.WINDOW_WIDTH = r.width
            }
            var weights = sideSashForm.weights
            Settings.FILE_LIST_WIDTH =
                    (sideSashForm.clientArea.width.toDouble() * weights[0].toDouble() / (weights[0] + weights[1]).toDouble()).roundToInt().also {
                        leftWidth = it
                    }
            weights = bottomSashForm.weights
            Settings.CONSOLE_HEIGHT =
                    (bottomSashForm.clientArea.height.toDouble() * weights[1].toDouble() / (weights[0] + weights[1]).toDouble()).roundToInt().also {
                        bottomHeight = it
                    }
            project.let {
                if (it != null)
                    Settings.OPEN_PROJECT = it.projectFile.absolutePath
                else
                    Settings.OPEN_PROJECT = null
            }
            Settings.commit()
        } catch (e1: BackingStoreException) {
            Util.logger.severe("Failed to save settings! " + e1.message)
        }
    }

    private fun closeAllTabs() {
        val numEditors = tabs.size
        for (i in 0 until numEditors) {
            tabs[0].close()
        }
    }

    fun setDefaultFolder() {
        val children = sideSashForm.children
        var sash: Control? = null
        for (c in children) if (c is SashForm || c is CustomTabs) sash = c
        if (sash == null) Util.logger.severe("BUG couldn't find editor!")
        while (sash is SashForm) sash = sash.children[0]
        tabFolder = sash as CustomTabs
    }

    fun getTabs(): List<TabChild>? {
        return tabs
    }

    /**
     * Create contents of the window.
     *
     * @wbp.parser.entryPoint
     */
    private fun createContents() {
        shell = Shell()
        loadFonts()
        Theme.init(display)
        Images.loadImages(display)
        shell.addShellListener(object : ShellAdapter() {
            override fun shellClosed(e: ShellEvent) {
                if (!saveAll(true)) {
                    e.doit = false
                    return
                }
                project?.let {
                    try {
                        it.saveXML()
                    } catch (e1: IOException) {
                        if (!Util.askQuestion("Failed to save project file!", "Close anyways?")) {
                            e.doit = false
                            return
                        }
                    }
                }
                monitor?.close()
                coreGen.kill()
                vivadoIP.kill()
                saveSettings()
                shell.image.dispose()
                Theme.dispose()
            }
        })
        shell.image = SWTResourceManager.getImage(MainWindow::class.java, "/images/icon.png")
        shell.setMinimumSize(550, 200)
        shell.text = "Alchitry Labs Version $VERSION"
        shell.layout = GridLayout(1, false)
        val height = Settings.WINDOW_HEIGHT
        val width = Settings.WINDOW_WIDTH
        shell.setSize(width, height)
        val max = Settings.MAXIMIZED
        if (max) shell.maximized = true
        shell.background = Theme.windowBackgroundColor
        shell.foreground = Theme.windowForegroundColor
        mainMenu = MainMenu()
        mainMenu.build()
        mainToolbar = MainToolbar()
        mainToolbar.build()
        bottomSashForm = SashForm(this.shell, SWT.VERTICAL)
        bottomSashForm.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
        bottomSashForm.addControlListener(object : ControlAdapter() {
            override fun controlResized(e: ControlEvent) {
                val sashHeight = bottomSashForm.clientArea.height
                val weights = bottomSashForm.weights
                val perBottom = bottomHeight.toDouble() / sashHeight.toDouble()
                if (perBottom < 0.8) {
                    weights[1] = (perBottom * 1000.0).toInt()
                    weights[0] = 1000 - weights[1]
                } else {
                    weights[1] = 800
                    weights[0] = 200
                }

                // oldWeights must be set before form.setWeights
                oldBottomWeight = weights[0]
                bottomSashForm.weights = weights
            }
        })
        bottomSashForm.background = Theme.windowBackgroundColor
        sideSashForm = SashForm(bottomSashForm, SWT.NONE)
        sideSashForm.addControlListener(object : ControlAdapter() {
            override fun controlResized(e: ControlEvent) {
                val sashWidth = sideSashForm.clientArea.width
                val weights = sideSashForm.weights
                val perLeft = leftWidth.toDouble() / sashWidth.toDouble()
                if (perLeft < 0.8) {
                    weights[0] = (perLeft * 1000.0).toInt()
                    weights[1] = 1000 - weights[0]
                } else {
                    weights[0] = 800
                    weights[1] = 200
                }

                // oldWeights must be set before form.setWeights
                oldLeftWeight = weights[0]
                sideSashForm.weights = weights
            }
        })
        sideSashForm.background = Theme.windowBackgroundColor
        val sc = ScrolledComposite(sideSashForm, SWT.H_SCROLL or SWT.V_SCROLL)
        sc.alwaysShowScrollBars = false
        tree = CustomTree(sc)
        sc.content = tree
        tree.background = Theme.editorBackgroundColor
        tree.foreground = Theme.editorForegroundColor
        sc.expandHorizontal = true
        sc.expandVertical = true
        tree.addListener(SWT.Resize) { event ->
            val p = (event.widget as CustomTree).computeSize(SWT.DEFAULT, SWT.DEFAULT)
            sc.setMinSize(p)
        }
        tree.addControlListener(object : ControlAdapter() {
            override fun controlResized(e: ControlEvent) {
                var weights = sideSashForm.weights
                if (oldLeftWeight != weights[0]) {
                    oldLeftWeight = weights[0]
                    leftWidth = (sideSashForm.clientArea.width.toDouble() * weights[0].toDouble() / (weights[0] + weights[1]).toDouble()).roundToInt()
                }
                weights = bottomSashForm.weights
                if (oldBottomWeight != weights[1]) {
                    oldBottomWeight = weights[1]
                    bottomHeight = (bottomSashForm.clientArea.height.toDouble() * weights[1].toDouble() / (weights[0] + weights[1]).toDouble()).roundToInt()
                }
            }
        })

        tabFolder = CustomTabs(sideSashForm, SWT.NONE)
        console = CustomConsole(bottomSashForm, SWT.READ_ONLY or SWT.H_SCROLL or SWT.V_SCROLL or SWT.CANCEL or SWT.MULTI)
        bottomSashForm.weights = intArrayOf(8, 2)
        Util.console = console
        leftWidth = Settings.FILE_LIST_WIDTH
        bottomHeight = Settings.CONSOLE_HEIGHT
    }

    fun programProject(flash: Boolean, verify: Boolean) {
        project.let {
            if (it == null) {
                Util.showError("A project must be opened before you can load it!")
                return
            }
            if (it.isBusy) {
                Util.showError("Operation already in progress!")
                return
            }
            it.load(flash, verify)
        }
    }

    fun checkProject() {
        project.let {
            if (it == null) {
                Util.showError("A project must be opened before you can check it!")
                return
            }
            if (it.isBusy) {
                Util.showError("Operation in progress!")
                return
            }
            if (Util.isGUI && !saveAll(false)) {
                Util.showError("Could not save all open tabs before build!")
                val box = MessageBox(shell, SWT.YES or SWT.NO)
                box.message = "Continue with the build anyway?"
                box.text = "All files not saved..."
                if (box.open() != SWT.YES) {
                    return
                }
            }
            it.checkProject()
        }
    }

    fun buildProject() {
        project.let {
            if (it == null) {
                Util.showError("A project must be opened before you can build it!")
                return
            }
            if (it.isBuilding) {
                it.stopBuild()
                return
            }
            if (it.isBusy) {
                Util.showError("Operation already in progress!")
                return
            }
            if (Util.isGUI && !saveAll(false)) {
                Util.showError("Could not save all open tabs before build!")
                val box = MessageBox(this.shell, SWT.YES or SWT.NO)
                box.message = "Continue with the build anyway?"
                box.text = "All files not saved..."
                if (box.open() != SWT.YES) {
                    return
                }
            }
            it.build(false)
        }
    }

    fun addNewFile() {
        project.let { project ->
            if (project != null) {
                val dialog = NewSourceDialog(this.shell, project.board)
                shell.isEnabled = false
                val file = dialog.open()
                if (file != null) {
                    var filePath: File? = null
                    when (file.type) {
                        SourceFile.VERILOG, SourceFile.LUCID -> if (project.addNewSourceFile(file.fileName).also { filePath = it } == null) Util.showError("Could not create new source file!")
                        SourceFile.CONSTRAINT -> if (project.addNewConstraintFile(file.fileName).also { filePath = it } == null) Util.showError("Could not create constraint file!")
                    }
                    if (filePath != null) openFile(filePath, true)
                }
                try {
                    project.saveXML()
                } catch (e: IOException) {
                    Util.showError("Failed to save project file!")
                }
                shell.isEnabled = true
            } else {
                Util.showError("A project must be open to add a new file.")
            }
        }
    }

    fun getIndex(e: TabChild): Int {
        return tabs.indexOf(e)
    }

    fun getTabChild(idx: Int): TabChild? {
        return if (idx < 0 || idx >= tabs.size) null else tabs[idx]
    }

    fun addEditor(e: StyledCodeEditor) {
        tabs.add(e)
    }

    fun isSideSash(c: Composite): Boolean {
        return c === sideSashForm
    }

    fun addComponents() {
        ComponentsDialog(shell).open()
    }

    private fun projectChanged() {
        project?.updateTree()
        project?.openTree()
        mainMenu.build()
        mainToolbar.build()
        shell.redraw()
        GlobalScope.launch(Dispatchers.SWT) {
            shell.layout()
        }
    }

    private fun saveProject(): Boolean {
        try {
            project?.saveXML()
        } catch (e: IOException) {
            Util.reportException(e)
            return Util.askQuestion("Failed to save project! Continue opening new one?")
        }
        return true
    }

    fun createNewProject() {
        val dialog = NewProjectDialog(shell)
        shell.isEnabled = false
        val p = dialog.open()
        if (p != null) openProject(p)
        shell.isEnabled = true
    }

    fun openProject(project: Project) {
        if (!saveProject()) return
        closeAllTabs()
        this.project = project
        tabFolder.opened = false
        projectChanged()
    }

    fun openProject(path: File) {
        if (!saveProject()) return
        closeAllTabs()
        try {
            project = Project.openXML(path, shell, tree)
            tabFolder.opened = false
        } catch (e: Exception) {
            Util.printException(e)
            Util.showError("Encountered an error while parsing $path the error was: ${e.message}", "Error opening file!")
        }
        projectChanged()
    }

    fun openProjectDialog() {
        val dialog = FileDialog(this.shell, SWT.OPEN)
        dialog.filterExtensions = arrayOf("*.alp", "*")
        dialog.filterPath = Util.workspace
        val path = dialog.open()
        if (path != null) openProject(File(path))
    }

    fun openFile(file: File?, write: Boolean): Boolean {
        for (tab in tabs) {
            if (tab is StyledCodeEditor) {
                if (tab.file != null && tab.file == file) {
                    tab.grabFocus()
                    return true
                }
            }
        }
        if (file != null && !file.exists()) {
            Util.println("File ${file.absolutePath} doesn't exist!", true)
            Util.showError("This file doesn't exist!")
            return false
        }
        val codeEditor = StyledCodeEditor(tabFolder, SWT.V_SCROLL or SWT.MULTI or SWT.H_SCROLL, file, write)
        if (codeEditor.isOpen) {
            tabs.add(codeEditor)
            if (file != null) {
                if (!tabFolder.opened && !tabFolder.tabChildren[0].isModified) {
                    tabFolder.close(0)
                }
                tabFolder.opened = true
            }
            return true
        }
        return false
    }

    fun openSVG() {
        val svg = ConstraintsEditor(tabFolder)
        tabs.add(svg)
        svg.grabFocus()
        tabFolder.opened = true
    }

    fun openWave(): Boolean {
        val waves = WaveForm(tabFolder, false)
        tabs.add(waves)
        waves.grabFocus()
        if (!tabFolder.opened && !tabFolder.tabChildren[0].isModified) {
            tabFolder.close(0)
        }
        tabFolder.opened = true
        return true
    }

    fun printOpen() {
        if (lastActiveEditor != null && !lastActiveEditor!!.isDisposed) lastActiveEditor!!.print() else Util.showInfo("No active text editors to print!")
    }

    fun updateErrors() {
        try {
            project!!.updateGlobals()
        } catch (e1: IOException) {
            Util.logger.severe("Failed to update project globals!")
            return
        }
        for (e in tabs) if (e is StyledCodeEditor) e.updateErrors()
    }

    fun getEditor(file: File): StyledCodeEditor? {
        if (Util.isGUI) for (tc in tabs) {
            if (tc is StyledCodeEditor) {
                if (file == tc.file) {
                    return tc
                }
            }
        }
        return null
    }

    @JvmStatic
    @Deprecated("Access with project member", ReplaceWith("project", "com.alchitry.labs.gui.main.MainWindow.project"))
    fun getOpenProject(): Project? {
        return project
    }


}