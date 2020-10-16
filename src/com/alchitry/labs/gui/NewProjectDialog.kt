package com.alchitry.labs.gui

import com.alchitry.labs.Locations
import com.alchitry.labs.Settings
import com.alchitry.labs.Strings
import com.alchitry.labs.Util
import com.alchitry.labs.gui.main.MainWindow
import com.alchitry.labs.gui.main.MainWindow.openProject
import com.alchitry.labs.hardware.boards.Board
import com.alchitry.labs.project.Project
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.RegExUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import org.jdom2.Document
import org.jdom2.JDOMException
import org.jdom2.input.SAXBuilder
import java.io.File
import java.io.IOException
import java.util.*

class NewProjectDialog(parent: Shell, style: Int = SWT.DIALOG_TRIM, private val clone: Project? = null) : Dialog(parent, style) {
    companion object {
        private const val PROJECT_XML = "projects.xml"
        private const val PROJECT_TAG = "project"
        private const val NAME_ATTR = "name"
        fun createProject(projectName: String, workspace: String, boardType: String, language: String, example: String): Project? {
            val projectFolder = workspace + File.separator + projectName
            if (projectName == "") {
                Util.showError("Project name cannot be blank!")
                return null
            }
            val board = Board.getFromName(boardType)
            if (board == null) {
                Util.showError("Board type is invalid!")
                return null
            }
            val nameToFile = getExampleProjects(boardType, language, null)
            val srcDir = Util.assembleFile(Locations.BASE.toString(), board.exampleProjectDir, language, nameToFile!![example]!!)
            if (!srcDir.exists()) {
                Util.showError("Could not find starter code!")
                return null
            }
            val destDir = File(projectFolder)
            val parentDir = File(workspace)
            if (!parentDir.exists()) {
                if (Util.askQuestion("Project parent directory does not exist. Create it?")) {
                    if (!parentDir.mkdirs()) {
                        Util.showError("Could not create parent directory!")
                        return null
                    }
                } else {
                    return null
                }
            }
            if (destDir.exists()) {
                if (Util.askQuestion("Project directory already exists. Do you want to override it?")) {
                    try {
                        FileUtils.deleteDirectory(destDir)
                    } catch (ex: IOException) {
                        Util.showError("Could not remove project folder.")
                        return null
                    }
                }
            }
            val success = destDir.mkdir()
            if (!success) {
                Util.showError("Could not create project folder!")
                return null
            }
            try {
                FileUtils.copyDirectory(srcDir, destDir)
            } catch (e1: IOException) {
                Util.showError(e1.message!!)
                return null
            }
            val projFiles = destDir.list { _, name -> name.endsWith(".alp") }
            if (projFiles == null) {
                Util.showError("Could not open project files!")
                return null
            }
            if (projFiles.size != 1) {
                Util.showError("Found " + projFiles.size + " project files. Was expecting only one.")
                return null
            }
            val projFile = Util.assembleFile(destDir, projFiles[0])
            val newProjFile = Util.assembleFile(destDir, "$projectName.alp")
            if (!projFile.renameTo(newProjFile)) {
                Util.showError("Could not rename project file!")
                return null
            }
            try {
                val projContents = FileUtils.readFileToString(newProjFile)
                val newContents = RegExUtils.replaceFirst(projContents, "project name=\".*?\"", "project name=\"$projectName\"")
                FileUtils.writeStringToFile(newProjFile, newContents, false)
            } catch (e: IOException) {
                Util.showError(e.message!!)
                return null
            }

            openProject(newProjFile)
            Settings.WORKSPACE = workspace
            return MainWindow.project
        }

        private fun getExampleProjects(boardType: String, language: String, names: MutableList<String>?): HashMap<String, String>? {
            val board = Board.getFromName(boardType)
            if (board == null) {
                Util.showError("Invalid board of type $boardType!")
                return null
            }
            val map = HashMap<String, String>()
            val builder = SAXBuilder()
            val xmlFile = Util.assembleFile(Locations.BASE.path, board.exampleProjectDir, language, PROJECT_XML)
            Util.logger.info("xml ${xmlFile.absolutePath}")
            Util.logger.info("Board ${board.exampleProjectDir}")
            Util.logger.info("Base ${Locations.BASE.absolutePath}")
            val document: Document
            document = try {
                builder.build(xmlFile) as Document
            } catch (e: JDOMException) {
                Util.showError("Could not parse projects XML file! ${xmlFile.absolutePath}")
                return null
            } catch (e: IOException) {
                Util.showError("Could not parse projects XML file! ${xmlFile.absolutePath}")
                return null
            }
            val library = document.rootElement
            val cat = library.getChildren(PROJECT_TAG)
            for (node in cat) {
                val name = node.getAttributeValue(NAME_ATTR)
                val file = node.textTrim
                map[name] = file
                names?.add(name)
            }
            return map
        }
    }

    private var result: Project? = null
    private val shlNewProject = Shell(parent, SWT.DIALOG_TRIM or SWT.PRIMARY_MODAL)
    private val projFolder: Text
    private val projName: Text
    private val combo: Combo
    private val btnLucid: Button
    private val examples: Combo

    fun open(): Project? {
        if (shlNewProject.isDisposed) return null
        shlNewProject.open()
        shlNewProject.layout()
        val display = parent.display
        while (!shlNewProject.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
        return result
    }

    init {
        shlNewProject.setSize(459, 363)
        shlNewProject.text = "New Project"
        shlNewProject.layout = GridLayout(4, false)
        val lblProjectName = Label(shlNewProject, SWT.NONE)
        // lblProjectName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblProjectName.text = "Project Name:"
        projName = Text(shlNewProject, SWT.BORDER)
        val gdText1 = GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1)
        gdText1.widthHint = 341
        projName.layoutData = gdText1
        val lblProjectFolder = Label(shlNewProject, SWT.NONE)
        lblProjectFolder.text = "Workspace:"
        projFolder = Text(shlNewProject, SWT.BORDER)
        projFolder.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1)
        projFolder.text = Util.workspace
        val btnNewButton = Button(shlNewProject, SWT.NONE)
        btnNewButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val dialog = DirectoryDialog(shlNewProject)
                // dialog.setFilterPath(string)
                val path = dialog.open()
                if (path != null) {
                    projFolder.text = path
                }
            }
        })
        btnNewButton.layoutData = GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1)
        btnNewButton.text = "Browse..."
        val lblBoard = Label(shlNewProject, SWT.NONE)
        lblBoard.text = "Board:"
        combo = Combo(shlNewProject, SWT.READ_ONLY)
        combo.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1)
        val boards = arrayOfNulls<String>(Board.boards.size)
        for (i in boards.indices) {
            boards[i] = Board.boards[i].name
        }
        combo.setItems(*boards)
        combo.select(0)
        combo.addSelectionListener(object : SelectionListener {
            override fun widgetSelected(e: SelectionEvent) {
                updateExamples()
            }

            override fun widgetDefaultSelected(e: SelectionEvent) {}
        })
        val lblNewLabel = Label(shlNewProject, SWT.NONE)
        lblNewLabel.layoutData = GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1)
        lblNewLabel.text = "Language:"
        btnLucid = Button(shlNewProject, SWT.RADIO)
        btnLucid.selection = true
        btnLucid.text = "Lucid"
        btnLucid.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                updateExamples()
            }
        })
        val btnVerilog = Button(shlNewProject, SWT.RADIO)
        btnVerilog.text = "Verilog"
        btnVerilog.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                updateExamples()
            }
        })
        val lblFromExample = Label(shlNewProject, SWT.NONE)
        lblFromExample.layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        lblFromExample.text = "From Example:"
        examples = Combo(shlNewProject, SWT.READ_ONLY)
        examples.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1)
        val btnCancel = Button(shlNewProject, SWT.NONE)
        btnCancel.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                shlNewProject.close()
            }
        })
        val gdBtnCancel = GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1)
        gdBtnCancel.widthHint = 80
        btnCancel.layoutData = gdBtnCancel
        btnCancel.text = "Cancel"
        val btnCreate = Button(shlNewProject, SWT.NONE)
        btnCreate.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val projectName = projName.text
                val boardType = combo.text
                val language = if (btnLucid.selection) Strings.lucid else Strings.verilog
                val workspace = projFolder.text
                if (projectName.isEmpty()) {
                    Util.showError("Project name cannot be blank!")
                    return
                }
                if (!File(workspace).exists()) {
                    Util.showError("The specified workspace does not exist!")
                    return
                }
                result = if (clone != null) {
                    try {
                        clone.saveAsXML(Util.assembleFile(workspace, projectName), projectName)
                    } catch (e: IOException) {
                        Util.showError("Failed to clone project. ${e.message}")
                        null
                    }
                } else {
                    createProject(projectName, workspace, boardType, language, examples.text)
                }
                if (result != null) shlNewProject.close()
            }
        })
        val gdBtnCreate = GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1)
        gdBtnCreate.widthHint = 80
        btnCreate.layoutData = gdBtnCreate
        btnCreate.text = "Create"
        if (clone != null) {
            examples.isEnabled = false
            combo.isEnabled = false
            btnLucid.isEnabled = false
            btnVerilog.isEnabled = false
        }
        shlNewProject.pack()
        val parentSize = parent.bounds
        val shellSize = shlNewProject.bounds
        val locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x
        val locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y
        shlNewProject.location = Point(locationX, locationY)
        updateExamples() // update list
    }

    private fun updateExamples() {
        if (clone != null) {
            examples.setItems("Current Project")
            examples.select(0)
        } else {
            val boardType = combo.text
            val language = if (btnLucid.selection) Strings.lucid else Strings.verilog
            val names: MutableList<String> = ArrayList()
            getExampleProjects(boardType, language, names)
            examples.setItems(*names.toTypedArray())
            examples.select(0)
        }
    }


}