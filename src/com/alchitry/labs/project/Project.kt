package com.alchitry.labs.project

import com.alchitry.labs.Locations
import com.alchitry.labs.Util
import com.alchitry.labs.gui.RenameDialog
import com.alchitry.labs.gui.StyledCodeEditor
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.main.MainWindow
import com.alchitry.labs.hardware.boards.Board
import com.alchitry.labs.parsers.InstModule
import com.alchitry.labs.parsers.Module
import com.alchitry.labs.parsers.Param
import com.alchitry.labs.parsers.Sig
import com.alchitry.labs.parsers.errors.AlchitryConstraintsErrorProvider
import com.alchitry.labs.parsers.errors.LucidErrorProvider
import com.alchitry.labs.parsers.errors.VerilogErrorProvider
import com.alchitry.labs.parsers.lucid.SignalWidth
import com.alchitry.labs.parsers.tools.lucid.LucidExtractor
import com.alchitry.labs.parsers.tools.lucid.LucidGlobalExtractor
import com.alchitry.labs.parsers.tools.lucid.LucidModuleExtractor
import com.alchitry.labs.parsers.tools.lucid.LucidModuleRenamer
import com.alchitry.labs.parsers.tools.verilog.VerilogLucidModuleFixer
import com.alchitry.labs.parsers.tools.verilog.VerilogModuleListener
import com.alchitry.labs.parsers.types.Constant
import com.alchitry.labs.parsers.types.Struct
import com.alchitry.labs.project.builders.ProjectBuilder
import com.alchitry.labs.style.ParseException
import com.alchitry.labs.style.SyntaxError
import com.alchitry.labs.widgets.CustomTree
import com.alchitry.labs.widgets.CustomTree.TreeElement
import com.alchitry.labs.widgets.CustomTree.TreeLeaf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.events.ControlListener
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import org.eclipse.swt.widgets.Shell
import org.jdom2.Attribute
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.JDOMException
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.*
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import java.util.*
import java.util.logging.Level

class Project(val projectName: String, val projectFolder: File, val board: Board, val language: String, val shell: Shell? = null, private val tree: CustomTree? = null) {
    companion object {
        const val VERSION_ID = 3
        const val SOURCE_PARENT = "Source"
        const val CONSTRAINTS_PARENT = "Constraints"
        const val COMPONENTS_PARENT = "Components"
        const val CORES_PARENT = "Cores"
        const val SOURCE_FOLDER = "source"
        const val CONSTRAINTS_FOLDER = "constraint"
        const val CORES_FOLDER = "cores"
        const val WORK_FOLDER = "work"
        const val LANG_LUCID = "Lucid"
        const val LANG_VERILOG = "Verilog"

        @Throws(ParseException::class, IOException::class)
        @JvmOverloads
        fun openXML(xmlFile: File, shell: Shell? = null, tree: CustomTree? = null): Project {
            val builder = SAXBuilder()
            val folder = xmlFile.parentFile
            val document: Document
            document = try {
                builder.build(xmlFile) as Document
            } catch (e: JDOMException) {
                throw ParseException(e.message)
            }
            val projectXml = document.rootElement
            if (projectXml.name != Tags.project) {
                throw ParseException("Root element not project tag")
            }
            val projName = projectXml.getAttribute(Tags.Attributes.name)
                    ?: throw ParseException("Project name is missing")
            val projectName = projName.value
            val brdType = projectXml.getAttribute(Tags.Attributes.board)
                    ?: throw ParseException("Board type is missing")
            val board = Board.getFromName(brdType.value) ?: throw ParseException("Unknown board type: " + brdType.value)

            val langType = projectXml.getAttribute(Tags.Attributes.language)
            val language = if (langType == null) "Lucid" else langType.value
            val project = Project(projectName, folder, board, language, shell, tree)
            var version = 0
            val versionAttr = projectXml.getAttribute(Tags.Attributes.version)
            if (versionAttr != null) try {
                version = versionAttr.value.toInt()
                if (version > VERSION_ID) throw ParseException("Project file is from a future version!")
            } catch (e: NumberFormatException) {
                throw ParseException("Invalid version ID!")
            }
            val list = projectXml.children
            for (i in list.indices) {
                val node = list[i]
                when (node.name) {
                    Tags.files -> {
                        val files = node.children
                        var j = 0
                        while (j < files.size) {
                            val file = files[j]
                            when (file.name) {
                                Tags.source -> {
                                    val att = file.getAttribute(Tags.Attributes.top)
                                    if (att != null && att.value == "true") {
                                        if (project.top != null) throw ParseException("Multiple \"top\" source files")
                                        project.top = Util.assembleFile(project.sourceFolder, file.text)
                                    }
                                    project.sourceFiles.add(Util.assembleFile(project.sourceFolder, file.text))
                                }
                                Tags.constraint -> {
                                    val att = file.getAttribute(Tags.Attributes.library)
                                    val isLib = java.lang.Boolean.valueOf(att != null && att.value == "true")
                                    val cstFile = Util.assembleFile(if (isLib) Locations.COMPONENTS else project.constraintFolder, file.text)
                                    project.constraintFiles.add(cstFile)
                                }
                                Tags.component -> project.sourceFiles.add(Util.assembleFile(Locations.COMPONENTS, file.text))
                                Tags.core -> {
                                    val cFiles = file.children
                                    val coreName = file.getAttributeValue(Tags.Attributes.name)
                                            ?: throw ParseException("Missing core name")
                                    val coreDir = Util.assemblePath(folder, CORES_FOLDER, coreName)
                                    val ipCore = IPCore(coreName)
                                    var k = 0
                                    while (k < cFiles.size) {
                                        val cFile = cFiles[k]
                                        val coreFile = File(Util.assemblePath(coreDir, cFile.text))
                                        when (cFile.name) {
                                            Tags.source -> ipCore.addFile(coreFile)
                                            Tags.stub -> ipCore.stub = coreFile
                                            else -> throw ParseException("Unknown tag in core " + cFile.name)
                                        }
                                        k++
                                    }
                                    project.iPCores.add(ipCore)
                                }
                                else -> throw ParseException("Unknown tag " + file.name)
                            }
                            j++
                        }
                    }
                    else -> throw ParseException("Unknown tag " + node.name)
                }
            }
            if (version != VERSION_ID) {
                if (Util.askQuestion("This project is from a previous version of the IDE. Would you like to attempt to update it?")) {
                    if (version == 0) {
                        val coresDir = File(Util.assemblePath(folder, "coreGen"))
                        val newCoresDir = File(Util.assemblePath(folder, CORES_FOLDER))
                        if (coresDir.exists() && coresDir.isDirectory) if (!coresDir.renameTo(newCoresDir)) throw ParseException("Failed to rename coreGen directory to cores")
                    }
                    if (version < 3) {
                        val filesToAdd: MutableList<File> = ArrayList()
                        val i = project.sourceFiles.iterator()
                        while (i.hasNext()) {
                            val f = i.next()
                            if (isLibFile(f, true)) {
                                when (f.name) {
                                    "spi_master.luc" -> {
                                        Util.println("Replacing spi_master.luc with spi_controller.luc")
                                        filesToAdd.add(Util.assembleFile(Locations.COMPONENTS, "spi_controller.luc"))
                                        i.remove()
                                    }
                                    "spi_slave.luc" -> {
                                        Util.println("Replacing spi_slave.luc with spi_peripheral.luc")
                                        filesToAdd.add(Util.assembleFile(Locations.COMPONENTS, "spi_peripheral.luc"))
                                        i.remove()
                                    }
                                    "i2c_master.luc" -> {
                                        Util.println("Replacing i2c_master.luc with i2c_controller.luc")
                                        filesToAdd.add(Util.assembleFile(Locations.COMPONENTS, "i2c_controller.luc"))
                                        i.remove()
                                    }
                                    "i2c_slave.luc" -> {
                                        Util.println("Replacing i2c_slave.luc with i2c_peripheral.luc")
                                        filesToAdd.add(Util.assembleFile(Locations.COMPONENTS, "i2c_peripheral.luc"))
                                        i.remove()
                                    }
                                }
                            }
                        }
                        project.sourceFiles.addAll(filesToAdd)
                    }
                    project.saveXML()
                } else {
                    throw ParseException("Incompatible version ID!")
                }
            }
            project.readDebugInfo()
            return project
        }

        fun isLibFile(file: File, skipExistCheck: Boolean = false): Boolean {
            return try {
                val libVersion = Util.assembleFile(Locations.COMPONENTS, file.name)
                if (!skipExistCheck && !libVersion.exists())
                    false
                else
                    Files.isSameFile(libVersion.toPath(), file.toPath())
            } catch (e: NoSuchFileException) {
                false
            } catch (e: IOException) {
                Util.logger.log(Level.SEVERE, "", e)
                false
            }
        }
    }

    var sourceFiles = HashSet<File>()
    private var constraintFiles = HashSet<File>()
    var iPCores = HashSet<IPCore>()
    private var primitives = HashSet<Primitive>()
    var top: File? = null
        private set
    val projectFile = File(Util.assemblePath(projectFolder.absolutePath, "$projectName.alp"))
    private val globalExtractor = LucidGlobalExtractor()
    private var treeMenu: Menu? = null
    private var builder: ProjectBuilder? = null
    private var debugInfo: DebugInfo? = null
    val saveListeners: MutableList<Listener> = ArrayList()
    private var buildJob: Job? = null


    private enum class FileType {
        SOURCE, CONSTRAINT, COMPONENT, CORE
    }

    init {
        if (shell != null)
            if (tree != null) {
                treeMenu = Menu(shell, SWT.POP_UP)
                tree.menu = treeMenu
                updateTree()
                openTree()
            }
    }

    fun addControlListener(listener: ControlListener?) {
        tree?.addControlListener(listener)
    }

    val binFile: String?
        get() {
            val binFile = File(Util.assemblePath(projectFolder.absolutePath, WORK_FOLDER, "alchitry.bin"))
            return if (binFile.exists()) binFile.absolutePath else null
        }

    private fun createFile(file: File, list: HashSet<File>): File? {
        val parentFolder = file.parentFile
        try {
            if (!parentFolder.exists()) {
                if (!parentFolder.mkdir()) {
                    Util.logger.severe("Failed to create parent directory " + parentFolder.absolutePath)
                    return null
                }
            }
            if (file.exists()) {
                if (!Util.askQuestion("File " + file.name + " exists. Overwrite?")) return null
                file.delete()
            }
            if (file.createNewFile()) {
                if (!list.contains(file)) list.add(file)
                updateTree()
                return file
            }
        } catch (e: IOException) {
            Util.logger.severe(ExceptionUtils.getStackTrace(e))
            return null
        }
        Util.logger.severe("Could not open file " + file.absolutePath)
        return null
    }


    private fun removeFile(file: File, list: HashSet<File>): Boolean {
        if (top == file) {
            Util.showError("You can't delete the top file!")
            return true
        }
        if (!isLibFile(file) && file.exists() && !file.delete()) {
            return false
        }
        val ret = list.remove(file)
        updateTree()
        return ret
    }

    fun removeAllIPCores() {
        iPCores.clear()
    }

    fun removeIPCore(coreName: String): Boolean {
        if (IPCore.delete(coreName, projectFolder.absolutePath)) {
            for (core in iPCores) if (core.name == coreName) {
                iPCores.remove(core)
                break
            }
            updateTree()
            return true
        }
        return false
    }

    fun renameFile(file: File, newFile: File): Boolean {
        if (isLibFile(file)) {
            Util.showError("Can't rename a library file!")
            return false
        }
        val list = when {
            constraintFiles.contains(file) -> constraintFiles
            sourceFiles.contains(file) -> sourceFiles
            else -> return false
        }
        list.remove(file)
        try {
            FileUtils.copyFile(file, newFile)
            file.delete()
        } catch (e: IOException) {
            Util.logger.log(Level.SEVERE, "", e)
            Util.showError("Failed to rename file!")
        }
        list.add(newFile)
        updateTree()
        return true
    }

    private fun copyTemplate(path: File, fileName: String): Boolean {
        if (path.name.endsWith(".luc") || path.name.endsWith(".v")) {
            val template: File = if (path.name.endsWith(".luc"))
                File(Locations.TEMPLATE_DIR.toString() + File.separator + "module.luc")
            else
                File(Locations.TEMPLATE_DIR.toString() + File.separator + "module.v")

            if (!template.exists() || !path.exists()) {
                Util.println("Error: Could not open template or source file!", true)
                return false
            }

            var br: BufferedReader? = null
            var bw: BufferedWriter? = null
            try {
                br = BufferedReader(FileReader(template))
                bw = BufferedWriter(FileWriter(path))
                val name = fileName.split("\\.".toRegex()).toTypedArray()
                var line: String
                while (br.readLine().also { line = it } != null) {
                    bw.write(line.replace("%NAME%", name[0]))
                    bw.write("\n")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                bw?.close()
                br?.close()
            }
            return true
        }
        return false
    }

    fun copyLibraryFile(src: File): Boolean {
        val isSrc = sourceFiles.remove(src)
        if (!isSrc) constraintFiles.remove(src)
        if (!importFile(src)) {
            println("Import returned false!")
            if (isSrc) sourceFiles.add(src) else constraintFiles.add(src)
            return false
        }
        updateTree()
        return true
    }

    fun importFile(src: File): Boolean {
        val fileName = src.name
        val dest = when {
            Util.isConstraintFile(fileName) -> Util.assembleFile(constraintFolder.toString(), fileName)
            Util.isSourceFile(fileName) -> Util.assembleFile(sourceFolder.toString(), fileName)
            else -> return false
        }
        val exists = dest.exists()
        if (dest != src) {
            if (exists && !Util.askQuestion("A file with the same name as \"$fileName\" exists in the project. Overwrite this file?", "File Exists")) return false
            try {
                FileUtils.copyFile(src, dest)
            } catch (e: IOException) {
                Util.logException(e, "Failed to copy file!")
                return false
            }
        }
        if (Util.isConstraintFile(fileName) && !constraintFiles.contains(dest)) {
            addExistingConstraintFile(dest)
        } else if (Util.isSourceFile(fileName) && !sourceFiles.contains(dest)) {
            addExistingSourceFile(dest)
        }
        try {
            saveXML()
        } catch (e1: IOException) {
            Util.showError("Failed to save project file!")
        }
        return true
    }

    fun addNewSourceFile(fileName: String): File? {
        val path = createFile(Util.assembleFile(projectFolder, SOURCE_FOLDER, fileName), sourceFiles)
                ?: return null
        copyTemplate(path, fileName)
        return path
    }

    fun addNewConstraintFile(fileName: String?): File? {
        val cFile = File(Util.assemblePath(projectFolder, CONSTRAINTS_FOLDER, fileName))
        return createFile(cFile, constraintFiles)
    }

    val sourceFolder: File
        get() = Util.assembleFile(projectFolder, SOURCE_FOLDER)
    val constraintFolder: File
        get() = Util.assembleFile(projectFolder, CONSTRAINTS_FOLDER)
    val iPCoreFolder: File
        get() = Util.assembleFile(projectFolder, CORES_FOLDER)

    fun addIPCore(core: IPCore) {
        if (iPCores.contains(core)) {
            iPCores.remove(core)
        }
        iPCores.add(core)
    }

    fun addExistingSourceFile(file: File) {
        sourceFiles.add(file)
    }

    fun addExistingConstraintFile(file: File) {
        constraintFiles.add(file)
    }

    fun setTopFile(file: File): Boolean {
        if (sourceFiles.contains(file)) {
            top = file
            return true
        }
        return false
    }

    fun getConstraintFiles(): HashSet<File> {
        val hs = HashSet(constraintFiles)
        removeUnsupportedConstraints(hs)
        return hs
    }

    private fun endsWithExt(file: File, ext: Array<String>): Boolean {
        for (e in ext) if (file.name.endsWith(e)) return true
        return false
    }

    private fun removeUnsupportedConstraints(constraints: HashSet<File>) {
        val ext = board.supportedConstraintExtensions
        val it = constraints.iterator()
        while (it.hasNext()) {
            val c = it.next()
            if (!endsWithExt(c, ext)) {
                it.remove()
                Util.println("Constraint \"$c\" is of an unsupported type. It will be ignored.", true)
            }
        }
    }

    val debugFiles: HashSet<File>
        get() {
            val debugList = HashSet<File>()
            if (board.isType(Board.MOJO)) {
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "reg_interface_debug.luc"))
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "reg_interface.luc"))
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "wave_capture.luc"))
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "simple_dual_ram.v"))
            } else if (board.isType(Board.AU)) {
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "au_debugger.luc"))
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "reset_conditioner.luc"))
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "async_fifo.luc"))
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "pipeline.luc"))
                debugList.add(Util.assembleFile(Locations.COMPONENTS, "simple_dual_ram.v"))
            }
            return debugList
        }

    private inner class SortFilesIgnoreCase : Comparator<Any> {
        override fun compare(o1: Any, o2: Any): Int {
            val s1 = (o1 as File).name
            val s2 = (o2 as File).name
            return s1.toLowerCase().compareTo(s2.toLowerCase())
        }
    }

    private inner class SortIPCores : Comparator<Any> {
        override fun compare(o1: Any, o2: Any): Int {
            val s1 = o1 as IPCore
            val s2 = o2 as IPCore
            return s1.name.toLowerCase().compareTo(s2.name.toLowerCase())
        }
    }

    private fun addRenameFile(item: TreeLeaf) {
        val file = item.file
        if (isLibFile(file)) return
        val mi = MenuItem(treeMenu, SWT.NONE)
        mi.text = "Raname " + item.name
        mi.addSelectionListener(object : SelectionListener {
            override fun widgetSelected(e: SelectionEvent) {
                val ogFileName = file.name
                val ogName = ogFileName.substring(0, ogFileName.lastIndexOf('.'))
                val ogExt = ogFileName.substring(ogFileName.lastIndexOf('.'))
                val renameDialog = RenameDialog(treeMenu!!.shell, SWT.DIALOG_TRIM)
                val newName = renameDialog.open(ogName)
                for (editor in MainWindow.tabs) {
                    if (editor is StyledCodeEditor) if (file == editor.file) {
                        MainWindow.tabFolder.close(editor) // close file if open
                        break
                    }
                }
                val newFile = Util.assembleFile(file.parent, newName + ogExt)
                if (!renameFile(file, newFile)) {
                    Util.showError("Failed to rename file!")
                    return
                }
                if (ogExt == ".luc") {
                    val newText = LucidModuleRenamer.renameModule(newFile, newName)
                    try {
                        FileUtils.write(newFile, newText, false)
                    } catch (e1: IOException) {
                        Util.showError("Failed to rewrite module name!")
                    }
                }
                try {
                    saveXML()
                } catch (e1: IOException) {
                    Util.showError("Failed to save project file!")
                }
                MainWindow.updateErrors()
            }

            override fun widgetDefaultSelected(e: SelectionEvent) {}
        })
    }

    private fun addRemoveFile(item: TreeLeaf, type: FileType) {
        val mi = MenuItem(treeMenu, SWT.NONE)
        mi.text = "Remove " + item.name
        mi.addSelectionListener(object : SelectionListener {
            override fun widgetSelected(e: SelectionEvent) {
                val file = item.file
                val result = if (isLibFile(file))
                    Util.askQuestion("Are you sure you want to remove the component ${file.name}?", "Confirm Removal")
                else
                    Util.askQuestion("Are you sure you want to delete $file?${System.lineSeparator()}${System.lineSeparator()}This cannot be undone.", "Confirm Delete")

                if (result) {
                    for (editor in MainWindow.tabs) {
                        if (editor is StyledCodeEditor) if (file == editor.file) {
                            MainWindow.tabFolder.close(editor) // close file if open
                            break
                        }
                    }
                    when (type) {
                        FileType.SOURCE, FileType.COMPONENT -> if (!removeFile(file, sourceFiles)) Util.showError("Could not remove file!")
                        FileType.CONSTRAINT -> if (!removeFile(file, constraintFiles)) Util.showError("Could not remove file!")
                        FileType.CORE -> if (!removeIPCore(item.name)) Util.showError("Could not remove IP Core!")
                    }
                    try {
                        saveXML()
                    } catch (e1: IOException) {
                        Util.showError("Failed to save project file!")
                    }
                    MainWindow.updateErrors()
                }
            }

            override fun widgetDefaultSelected(e: SelectionEvent) {}
        })
    }

    private val sourceListener = Listener { event ->
        val item = event.data as TreeElement
        if (event.button == 1 && !item.isNode) {
            MainWindow.openFile(Util.assembleFile(sourceFolder, item.name), true)
        } else if (event.button == 3) {
            for (i in treeMenu!!.items) i.dispose()
            val mi = MenuItem(treeMenu, SWT.NONE)
            mi.text = "New source..."
            mi.addSelectionListener(object : SelectionListener {
                override fun widgetSelected(e: SelectionEvent) {
                    MainWindow.addNewFile()
                }

                override fun widgetDefaultSelected(e: SelectionEvent) {}
            })
            if (!item.isNode) {
                addRemoveFile(item as TreeLeaf, FileType.SOURCE)
                addRenameFile(item)
            }
        }
    }
    private val constraintsListener = Listener { event ->
        val item = event.data as TreeElement
        if (event.button == 1 && !item.isNode) {
            val compFile = Util.assembleFile(Locations.COMPONENTS, item.name)
            val constFile = Util.assembleFile(constraintFolder, item.name)
            if (isLibFile(constFile)) MainWindow.openFile(compFile, false) else MainWindow.openFile(Util.assembleFile(constraintFolder, item.name), true)
        } else if (event.button == 3) {
            for (i in treeMenu!!.items) i.dispose()
            val mi = MenuItem(treeMenu, SWT.NONE)
            mi.text = "New constraint..."
            mi.addSelectionListener(object : SelectionListener {
                override fun widgetSelected(e: SelectionEvent) {
                    MainWindow.addNewFile()
                }

                override fun widgetDefaultSelected(e: SelectionEvent) {}
            })
            if (!item.isNode) {
                addRemoveFile(item as TreeLeaf, FileType.CONSTRAINT)
                addRenameFile(item)
            }
        }
    }
    private val componentsListener = Listener { event ->
        val item = event.data as TreeElement
        if (event.button == 1 && !item.isNode) {
            MainWindow.openFile(Util.assembleFile(Locations.COMPONENTS, item.name), false)
        } else if (event.button == 3) {
            for (i in treeMenu!!.items) i.dispose()
            val mi = MenuItem(treeMenu, SWT.NONE)
            mi.text = "Add component..."
            mi.addSelectionListener(object : SelectionListener {
                override fun widgetSelected(e: SelectionEvent) {
                    MainWindow.addComponents()
                }

                override fun widgetDefaultSelected(e: SelectionEvent) {}
            })
            if (!item.isNode) addRemoveFile(item as TreeLeaf, FileType.COMPONENT)
        }
    }
    private val coresListener = Listener { event ->
        val item = event.data as TreeElement
        if (event.button == 1 && !item.isNode) {
            if (board.isType(Board.MOJO)) {
                MainWindow.openFile(Util.assembleFile(projectFolder, CORES_FOLDER, item.name), true)
            } else {
                MainWindow.openFile(Util.assembleFile(projectFolder, CORES_FOLDER, item.parent.name, item.name), true)
            }
        } else if (event.button == 3) {
            for (i in treeMenu!!.items) i.dispose()
            val mi = MenuItem(treeMenu, SWT.NONE)
            mi.text = "Recustomize core"
            mi.addSelectionListener(object : SelectionListener {
                override fun widgetSelected(e: SelectionEvent) {
                    if (board.isType(Board.MOJO)) MainWindow.coreGen.launch(this@Project) else if (board.isType(Board.AU)) MainWindow.vivadoIP.launch(this@Project)
                }

                override fun widgetDefaultSelected(e: SelectionEvent) {}
            })
            // TODO: Add removal of Mojo cores
            // if (boardType.isType(Board.MOJO) && item.isNode() &&
            // !item.getName().equals(CORES_PARENT))
            // addRemoveFile((CustomTree.TreeLeaf) item, FileType.CORE);
        }
    }

    fun updateTree() {
        if (tree != null) {
            if (tree.rootSize != 1 || tree.getElement(0).name != projectName) {
                tree.removeAll()
                val project = CustomTree.TreeNode(projectName)
                tree.addElement(project)
            }
            val project = tree.getElement(0) as CustomTree.TreeNode
            val projectNodes = project.children
            if (projectNodes.size < 1 || projectNodes[0].name != SOURCE_PARENT) {
                val sourceBranch = CustomTree.TreeNode(SOURCE_PARENT)
                sourceBranch.addClickListener(sourceListener)
                project.add(sourceBranch)
            }
            val sourceBranch = projectNodes[0] as CustomTree.TreeNode
            val sourceLeaves = sourceBranch.children
            val libFiles = ArrayList<File>()
            var leafCt = 0
            var files = ArrayList(sourceFiles)
            Collections.sort(files, SortFilesIgnoreCase())
            for (source in files) {
                if (isLibFile(source)) {
                    libFiles.add(source)
                } else {
                    if (sourceLeaves.size < leafCt + 1 || sourceLeaves[leafCt].name != source.name) {
                        val leaf = TreeLeaf(source)
                        sourceBranch.add(leafCt, leaf)
                        leaf.addClickListener(sourceListener)
                    }
                    leafCt++
                }
            }
            while (leafCt < sourceLeaves.size) sourceBranch.remove(sourceLeaves.size - 1)
            var categoryIdx = 1
            leafCt = 0
            if (libFiles.size > 0) {
                if (projectNodes.size < categoryIdx + 1 || projectNodes[categoryIdx].name != COMPONENTS_PARENT) {
                    val compBranch = CustomTree.TreeNode(COMPONENTS_PARENT)
                    compBranch.addClickListener(componentsListener)
                    project.add(categoryIdx, compBranch)
                }
                val compBranch = projectNodes[categoryIdx++] as CustomTree.TreeNode
                val compLeafs = compBranch.children
                for (comp in libFiles) {
                    if (compLeafs.size < leafCt + 1 || compLeafs[leafCt].name != comp.name) {
                        val leaf = TreeLeaf(comp)
                        compBranch.add(leafCt, leaf)
                        leaf.addClickListener(componentsListener)
                    }
                    leafCt++
                }
                while (leafCt < compLeafs.size) compBranch.remove(compLeafs.size - 1)
            }
            if (iPCores.size > 0) {
                if (projectNodes.size < categoryIdx + 1 || projectNodes[categoryIdx].name != CORES_PARENT) {
                    val coreBranch = CustomTree.TreeNode(CORES_PARENT)
                    coreBranch.addClickListener(coresListener)
                    project.add(categoryIdx, coreBranch)
                }
                val coreBranch = projectNodes[categoryIdx++] as CustomTree.TreeNode
                val coreLeafs = coreBranch.children
                val cores = ArrayList(iPCores)
                Collections.sort(cores, SortIPCores())
                for (j in cores.indices) {
                    val core = cores[j]
                    if (coreLeafs.size < j + 1 || coreLeafs[j].name != core.name) {
                        val coreParent = CustomTree.TreeNode(core.name)
                        coreParent.addClickListener(coresListener)
                        coreBranch.add(j, coreParent)
                    }
                    val coreParent = coreLeafs[j] as CustomTree.TreeNode
                    val coreParentLeafs = coreParent.children
                    leafCt = 0
                    if (core.stub != null) {
                        val leaf = TreeLeaf(core.stub)
                        coreParent.add(leafCt++, leaf)
                        leaf.addClickListener(coresListener)
                    } else {
                        val cfiles = core.files
                        for (i in cfiles.indices) {
                            val cfile = cfiles[i]
                            if (cfile.isFile) {
                                if (coreParentLeafs.size < i + 1 || coreParentLeafs[i].name != cfile.name) {
                                    val leaf = TreeLeaf(cfile)
                                    coreParent.add(leafCt, leaf)
                                    leaf.addClickListener(coresListener)
                                }
                                leafCt++
                            }
                        }
                    }
                    while (leafCt < coreParentLeafs.size) coreParent.remove(coreParentLeafs.size - 1)
                }
                while (cores.size < coreLeafs.size) coreBranch.remove(coreLeafs.size - 1)
            }
            if (projectNodes.size < categoryIdx + 1 || projectNodes[categoryIdx].name != CONSTRAINTS_PARENT) {
                val constBranch = CustomTree.TreeNode(CONSTRAINTS_PARENT)
                constBranch.addClickListener(constraintsListener)
                project.add(categoryIdx, constBranch)
            }
            val ucfBranch = projectNodes[categoryIdx++] as CustomTree.TreeNode
            val ucfLeafs = ucfBranch.children
            files = ArrayList(constraintFiles)
            Collections.sort(files, SortFilesIgnoreCase())
            for (i in files.indices) {
                val ucf = files[i]
                if (ucfLeafs.size < i + 1 || ucfLeafs[i].name != ucf.name) {
                    val leaf = TreeLeaf(ucf)
                    ucfBranch.add(i, leaf)
                    leaf.addClickListener(constraintsListener)
                }
            }
            while (constraintFiles.size < ucfLeafs.size) ucfBranch.remove(ucfLeafs.size - 1)
            while (projectNodes.size > categoryIdx) { // remove extras
                project.remove(projectNodes.size - 1)
            }
            tree.updateTree()
            tree.redraw()
        }

    }

    fun openTree() {
        if (tree == null) return
        for (i in 0 until tree.rootSize) (tree.getElement(i) as CustomTree.TreeNode).isOpen = true
        tree.updateTree()
    }

    private fun getVerilogModules(file: File): List<Module> {
        val converter = VerilogModuleListener()
        val modules = converter.extractModules(file)
        for (m in modules) m.file = file
        return modules
    }

    private fun getLucidModule(file: File): Module? {
        val converter = LucidModuleExtractor()
        val m = converter.getModule(file)
        if (m != null) m.file = file
        return m
    }

    private fun addGlobals(files: Collection<File>) {
        files.forEach {
            if (it.name.endsWith(".luc")) {
                globalExtractor.parseGlobals(it)
            }
        }
    }

    fun updateGlobals() {
        globalExtractor.reset()
        addGlobals(sourceFiles)
    }

    fun getGlobalErrors(file: File): List<SyntaxError>? {
        return globalExtractor.getErrors(file)
    }

    val globalConstants: HashMap<String, List<Constant>>
        get() = globalExtractor.constants
    val globalStructs: HashMap<String, List<Struct>>
        get() = globalExtractor.structs

    private fun addModule(modules: ArrayList<Module>, file: File) {
        for (m in modules) if (m.file.canonicalPath == file.canonicalPath) return
        if (file.name.endsWith(".luc")) {
            val m = getLucidModule(file)
            if (m != null) modules.add(m)
        } else if (file.name.endsWith(".v")) {
            val ml = getVerilogModules(file)
            for (m in ml) if (m.name.endsWith("_bb")) m.isNgc = true
            modules.addAll(ml)
        }
    }

    private fun addModules(modules: ArrayList<Module>, files: Collection<File>?) {
        for (file in files!!) addModule(modules, file)
    }

    private fun primToModule(p: Primitive): Module {
        val name = "xil_" + p.name
        val m = Module(name)
        m.primitive = p
        for (pParam in p.parameters) {
            val param = Param(pParam.name)
            param.setDefault("")
            m.addParam(param)
        }
        for (pPort in p.ports) {
            val s = Sig(pPort.name)
            s.width = SignalWidth(pPort.width)
            when (pPort.direction) {
                Primitive.Port.DIR_INPUT -> m.addInput(s)
                Primitive.Port.DIR_OUTPUT -> m.addOutput(s)
                Primitive.Port.DIR_INOUT -> m.addInout(s)
            }
        }
        return m
    }

    val topModule: Module?
        get() {
            val modules: List<Module> = getModules(null)
            return getModuleFromFile(top, modules)
        }

    @Throws(IOException::class)
    fun getModules(debugFiles: List<File>?): ArrayList<Module> {
        val modules = ArrayList<Module>()
        addModules(modules, sourceFiles)
        if (debugFiles != null) {
            addModules(modules, debugFiles)
            addModules(modules, debugFiles)
        }
        for (ipcore in iPCores) {
            if (ipcore.stub != null) addModule(modules, ipcore.stub) else addModules(modules, ipcore.files)
        }
        try {
            val prims = Primitive.getAvailable()
            for (pSet in prims.values) for (p in pSet) modules.add(primToModule(p))
        } catch (e: ParseException) {
            Util.println(e.message, true)
            e.printStackTrace()
        }
        return modules
    }

    private fun getModuleFromFile(file: File?, list: List<Module>): Module? {
        for (m in list) if (m.file == file) return m
        return null
    }

    @Throws(IOException::class)
    private fun getVerilogInstModules(im: InstModule, modules: List<Module>): List<InstModule> {
        val converter = VerilogModuleListener()
        return converter.extractInstModules(im, modules)
    }

    @Throws(IOException::class)
    private fun getLucidInstModules(im: InstModule, modules: List<Module>): List<InstModule> {
        val file = im.type.file
        val converter = LucidExtractor(im)
        return converter.getInstModules(file, modules)
    }

    @Throws(IOException::class)
    fun getModuleList(modules: List<Module>, mergeDupes: Boolean, topModule: Module?): List<InstModule>? {
        val queue: Queue<InstModule> = LinkedList()
        val outList: MutableList<InstModule> = ArrayList()
        if (topModule == null) {
            val top = getModuleFromFile(top, modules)
            if (top == null) {
                Util.showError("Could not find top module file!")
                return null
            }
            queue.add(InstModule(top.name, top, null))
        } else {
            queue.add(InstModule(topModule.name, topModule, null))
        }
        while (!queue.isEmpty()) {
            val im = queue.remove()
            outList.add(im)
            var list: List<InstModule>? = null
            if (im.isLucid) {
                list = getLucidInstModules(im, modules)
            } else if (im.isVerilog) {
                list = getVerilogInstModules(im, modules)
            }
            // outQueue.addAll(list);
            if (list != null) {
                for (m in list) {
                    var add = true

                    // Skip IP core files
                    if (m.type.file == null
                            || m.type.file.canonicalPath.startsWith(Util.assembleFile(projectFolder, CORES_FOLDER).canonicalPath)) add = false
                    if (add && mergeDupes) for (om in outList) {
                        if (om == m) {
                            add = false
                            im.addChild(om)
                            break
                        }
                    }
                    if (add) {
                        if (!queue.contains(m)) queue.add(m)
                        im.addChild(m)
                    }
                }
            }
        }
        return outList
    }

    @Throws(IOException::class)
    fun saveAsXML(folder: File, name: String): Project? {
        val oldProjectFile = Util.assembleFile(folder, projectFile.name)
        FileUtils.copyDirectory(projectFolder, folder)
        oldProjectFile.delete()

        val newProject = Project(name, folder, board, language, shell, tree)
        newProject.iPCores.addAll(iPCores)
        newProject.sourceFiles.addAll(sourceFiles)
        newProject.constraintFiles.addAll(constraintFiles)
        newProject.debugFiles.addAll(debugFiles)
        newProject.primitives.addAll(primitives)
        newProject.top = top
        newProject.debugInfo = debugInfo

        // Need to update core file references before saving so they don't point to the
        // old project
        newProject.iPCores.forEach { core ->
            val oldCorePath = Paths.get(File(Util.assemblePath(projectFile, CORES_FOLDER, core.name)).absolutePath)
            val updatedFiles = ArrayList<File>(core.files.size)
            for (coreFile in core.files) {
                val p = oldCorePath.relativize(Paths.get(coreFile.absolutePath)).toString()
                updatedFiles.add(Util.assembleFile(folder, CORES_FOLDER, core.name, p))
            }
            core.files = updatedFiles
            if (core.stub != null) {
                val p = oldCorePath.relativize(Paths.get(core.stub.absolutePath)).toString()
                core.stub = Util.assembleFile(folder, CORES_FOLDER, core.name, p)
            }
        }
        newProject.saveXML()
        return newProject
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun saveXML(file: File = projectFile) {
        val project = Element(Tags.project)
        project.setAttribute(Attribute(Tags.Attributes.name, projectName))
        project.setAttribute(Attribute(Tags.Attributes.board, board.name))
        project.setAttribute(Attribute(Tags.Attributes.language, language))
        project.setAttribute(Attribute(Tags.Attributes.version, VERSION_ID.toString()))
        val doc = Document(project)
        val source = Element(Tags.files)
        for (sourceFile in sourceFiles) {
            if (isLibFile(sourceFile)) {
                source.addContent(Element(Tags.component).setText(sourceFile.name))
            } else {
                val ele = Element(Tags.source).setText(sourceFile.name)
                if (sourceFile == top) ele.setAttribute(Attribute(Tags.Attributes.top, "true"))
                source.addContent(ele)
            }
        }
        for (ucfFile in constraintFiles) {
            val ele = Element(Tags.constraint).setText(ucfFile.name)
            if (isLibFile(ucfFile)) ele.setAttribute(Attribute(Tags.Attributes.library, "true"))
            source.addContent(ele)
        }
        for (core in iPCores) {
            val coreElement = Element(Tags.core).setAttribute(Tags.Attributes.name, core.name)
            val corePath = Paths.get(File(Util.assemblePath(projectFolder, CORES_FOLDER, core.name)).absolutePath)
            for (coreFile in core.files) {
                val p = corePath.relativize(Paths.get(coreFile.absolutePath)).toString()
                coreElement.addContent(Element(Tags.source).setText(p))
            }
            if (core.stub != null) {
                val p = corePath.relativize(Paths.get(core.stub.absolutePath)).toString()
                coreElement.addContent(Element(Tags.stub).setText(p))
            }
            source.addContent(coreElement)
        }
        project.addContent(source)
        val xmlOutput = XMLOutputter()
        xmlOutput.format = Format.getPrettyFormat()
        xmlOutput.output(doc, FileWriter(file))
        for (l in saveListeners) l.handleEvent(null)
    }

    fun setDebugInfo(dbi: DebugInfo?) {
        debugInfo = dbi
        try {
            val e = ObjectOutputStream(BufferedOutputStream(FileOutputStream(Util.assembleFile(projectFolder, "work", "debug", "debug.data"))))
            e.writeObject(dbi)
            e.close()
        } catch (o: Exception) {
            Util.logger.log(Level.INFO, "Failed to write debug info", o)
        }
    }

    fun readDebugInfo(): Boolean {
        try {
            val debugFile = Util.assembleFile(projectFolder, "work", "debug", "debug.data")
            if (!debugFile.exists()) return false
            val e = ObjectInputStream(BufferedInputStream(FileInputStream(debugFile.path)))
            val dbi = e.readObject() as DebugInfo
            e.close()
            debugInfo = dbi
            return true
        } catch (o: Exception) {
            Util.logger.log(Level.INFO, "Failed to recover debug info", o)
        }
        return false
    }

    fun getDebugInfo(): DebugInfo? {
        return debugInfo
    }

    fun checkProject() {
        val thread: Thread = object : Thread() {
            override fun run() {
                Util.clearConsole()
                try {
                    if (!checkForErrors()) {
                        Util.println("No errors detected.", Theme.successTextColor)
                    }
                } catch (e: IOException) {
                }
            }
        }
        thread.start()
    }

    @Throws(IOException::class)
    private fun checkForErrors(file: File, printErrors: Boolean): Boolean {
        var hasErrors = false
        if (Util.hasErrorProvider(file)) {
            val errors: MutableList<SyntaxError> = when {
                file.name.endsWith(".luc") -> {
                    val errorChecker = LucidErrorProvider()
                    errorChecker.getErrors(file)
                }
                file.name.endsWith(".v") -> {
                    val errorChecker = VerilogErrorProvider()
                    errorChecker.getErrors(file)
                }
                file.name.endsWith(".acf") -> {
                    val errorChecker = AlchitryConstraintsErrorProvider()
                    errorChecker.getErrors(file)
                }
                else -> {
                    Util.println("Unknown source file extension $file!", true)
                    return false
                }
            }
            getGlobalErrors(file)?.let { errors.addAll(it) }
            hasErrors = addErrors(errors, file, printErrors)
        }
        return hasErrors
    }

    private fun checkForIMErrors(im: InstModule, modules: List<Module>, instModules: List<InstModule>?): Boolean {
        val file = im.type.file
        val errors = VerilogLucidModuleFixer.getErrors(im, file, modules, instModules)
        return addErrors(errors, file, true)
    }

    @Throws(IOException::class)
    fun checkForErrors(): Boolean {
        updateGlobals()
        val modules: List<Module> = getModules(null)
        val list = getModuleList(modules, true, null)
        var hasErrors = false
        for (file in sourceFiles) {
            hasErrors = hasErrors or checkForErrors(file, true)
        }
        for (im in list!!) if (!im.type.isPrimitive && im.type.file.name.endsWith(".v")) hasErrors = hasErrors or checkForIMErrors(im, modules, list)
        for (file in getConstraintFiles()) {
            hasErrors = hasErrors or checkForErrors(file, true)
        }
        return hasErrors
    }

    private fun addError(text: String, type: Int) {
        var color = Theme.editorForegroundColor
        when (type) {
            SyntaxError.ERROR -> color = Theme.errorTextColor
            SyntaxError.WARNING -> color = Theme.warningTextColor
            SyntaxError.INFO -> color = Theme.infoTextColor
            SyntaxError.DEBUG -> color = Theme.debugTextColor
        }
        Util.print(text, color)
    }

    private fun addErrors(errors: List<SyntaxError>?, file: File, printErrors: Boolean): Boolean {
        var hasErrors = false
        var hasWarnings = false
        var hasInfo = false
        if (errors != null) {
            for (se in errors) {
                when (se.type) {
                    SyntaxError.ERROR -> hasErrors = true
                    SyntaxError.WARNING -> hasWarnings = true
                    SyntaxError.INFO -> hasInfo = true
                }
            }
            if ((hasErrors || hasWarnings || hasInfo) && printErrors) {
                if (hasErrors) addError(String.format("Errors in file %s:%s", file, System.lineSeparator()), SyntaxError.ERROR) else if (hasWarnings) addError(String.format("Warnings in file %s:%s", file, System.lineSeparator()), SyntaxError.WARNING) else addError(String.format("Info in file %s:%s", file, System.lineSeparator()), SyntaxError.INFO)
                for (se in errors) {
                    if (se.type != SyntaxError.DEBUG) addError(String.format("    Line %d, Column %d : %s%s", se.line, se.column, se.message, System.lineSeparator()), se.type)
                }
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }
        return hasErrors
    }

    // top level IM
    @get:Throws(IOException::class)
    val lucidSourceTree: InstModule
        get() {
            updateGlobals()
            val modules: List<Module> = getModules(null)
            val list = getModuleList(modules, false, null)
            for (file in sourceFiles) checkForErrors(file, false)
            return list!![0] // top level IM
        }

    fun build(debug: Boolean) {
        if (isBusy) {
            Util.showError("Operation already in progress!")
            return
        }
        builder = board.builder
        if (Util.isGUI) {
            buildJob = GlobalScope.launch(Dispatchers.Default) {
                try {
                    builder?.build(this@Project, debug)
                } catch (e: Exception) {
                    Util.logException(e, "Exception with project builder!")
                }
            }
        } else {
            builder?.build(this@Project, debug)
        }
    }

    val isBuilding: Boolean
        get() = isBusy && builder?.isBuilding == true
    val isBusy: Boolean
        get() = buildJob?.isActive == true

    fun stopBuild() {
        if (isBuilding) {
            builder?.stopBuild()
        }
    }

    fun load(flash: Boolean, verify: Boolean) {
        if (isBusy) {
            Util.showError("Operation already in progress!")
            return
        }
        val binFile = binFile
        if (binFile == null) {
            Util.showError("Could not find the bin file! Make sure the project is built.")
            return
        }
        if (Util.isGUI) {
            buildJob = GlobalScope.launch(Dispatchers.IO) {
                board.loader.load(binFile, flash, verify)
            }
        } else {
            board.loader.load(binFile, flash, verify)
        }
    }

    fun erase() {
        if (isBusy) {
            Util.showError("Operation already in progress!")
            return
        }
        if (Util.isGUI) {
            buildJob = GlobalScope.launch(Dispatchers.IO) {
                board.loader.erase()
            }
        } else {
            board.loader.erase()
        }
    }


}