package com.alchitry.labs.project

import com.alchitry.labs.Locations
import com.alchitry.labs.Util
import com.alchitry.labs.Util.assembleFile
import com.alchitry.labs.Util.assembleLinuxPath
import com.alchitry.labs.Util.assemblePath
import com.alchitry.labs.Util.clearConsole
import com.alchitry.labs.Util.println
import com.alchitry.labs.Util.reportException
import com.alchitry.labs.Util.runCommand
import com.alchitry.labs.Util.showError
import com.alchitry.labs.Util.vivadoLocation
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.hardware.boards.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swt.SWT
import org.apache.commons.io.FileUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.logging.Level

class VivadoIP {
    private var process: Process? = null
    private var thread: Thread? = null
    val isRunning: Boolean
        get() = process != null && process!!.isAlive

    private fun projectExists(project: Project): Boolean {
        val projectPath = assemblePath(project.projectFolder, Project.CORES_FOLDER, "managed_ip_project",
                "managed_ip_project.xpr")
        val projFile = File(projectPath)
        return projFile.exists()
    }

    @Throws(InterruptedException::class, IOException::class)
    private fun createProject(project: Project) {
        var out: BufferedWriter? = null
        val coresFolder = assemblePath(project.projectFolder, Project.CORES_FOLDER)
        val tclScript = assemblePath(coresFolder, projectFile)
        val coresDir = File(coresFolder)
        if (!coresDir.exists()) {
            if (!coresDir.mkdir()) {
                println("Failed to create cores directory (" + coresDir.absolutePath + ")", true)
                return
            }
        }
        try {
            val fstream = FileWriter(tclScript)
            out = BufferedWriter(fstream)
            generateProjectFile(project.board, out, coresDir.absolutePath.replace("\\", "/"))
        } catch (e: IOException) {
            throw e
        } finally {
            if (out != null) try {
                out.close()
            } catch (e: IOException) {
                Util.logger.log(Level.SEVERE, "Failed to close stdout stream!", e)
            }
        }
        runTclScript(tclScript)
    }

    @Throws(IOException::class)
    private fun generateProjectFile(board: Board, file: BufferedWriter, coresDir: String) {
        val nl = System.lineSeparator()
        file.write("cd \"$coresDir\"$nl")
        file.write("create_project managed_ip_project \"")
        file.write(assembleLinuxPath(coresDir, "managed_ip_project"))
        file.write("\" -part ${board.fpgaName} -ip")
    }

    @Throws(InterruptedException::class)
    private fun openProject(project: Project) {
        val coresFolder = assemblePath(project.projectFolder, Project.CORES_FOLDER)
        val projectFile = assemblePath(coresFolder, "managed_ip_project", "managed_ip_project.xpr")
        val p = File(projectFile)
        if (!p.exists()) {
            showError("Cores  project files does not exist!")
            return
        }
        val vivado = vivadoLocation
        if (vivado == null) {
            Util.logger.severe("Couldn't find Vivado :(")
            showError("Vivado's location must be set in the settings menu before you can open an IP core!")
            return
        }
        val cmd = ArrayList<String?>()
        cmd.add(assemblePath(vivado, "bin", if (Util.isWindows) "vivado.bat" else "vivado"))
        cmd.add("-nojournal")
        cmd.add("-nolog")
        cmd.add(p.absolutePath)
        process = runCommand(cmd)
        process!!.waitFor()
    }

    @Throws(InterruptedException::class)
    private fun runTclScript(tclScriptPath: String) {
        val vivado = vivadoLocation
        if (vivado == null) {
            Util.logger.severe("Couldn't find Vivado :(")
            showError("Vivado's location must be set in the settings menu before you can create IP cores!")
            return
        }
        val cmd = ArrayList<String?>()
        cmd.add(assemblePath(vivado, "bin", if (Util.isWindows) "vivado.bat" else "vivado"))
        cmd.add("-nojournal")
        cmd.add("-nolog")
        cmd.add("-mode")
        cmd.add("batch")
        cmd.add("-source")
        cmd.add(tclScriptPath)
        println(cmd.toString(), Theme.infoTextColor)
        process = runCommand(cmd)
        process!!.waitFor()
    }

    fun launch(project: Project) {
        if (thread != null && thread!!.isAlive) {
            showError("Vivado is already running!")
            return
        }
        thread = object : Thread() {
            override fun run() {
                try {
                    clearConsole()
                    createIpProjectIfNeeded(project)
                    println("Launching Vivado...")
                    openProject(project)
                    println("Vivado exited...")
                    println("Looking for new cores...")
                    checkForNewCores(project)
                } catch (e: Exception) {
                    reportException(e)
                }
            }
        }.also { it.start() }
    }

    @Throws(InterruptedException::class, IOException::class)
    private fun createIpProjectIfNeeded(project: Project) {
        if (!projectExists(project)) {
            println("Creating IP project...")
            createProject(project)
        }
    }

    private fun findPattern(dir: File, regex: Regex, isFile: Boolean): File? {
        for (stub in dir.listFiles()) {
            if (isFile == stub.isFile && stub.name.matches(regex)) {
                return stub
            }
            if (stub.isDirectory) {
                val rStub = findPattern(stub, regex, isFile)
                if (rStub != null) return rStub
            }
        }
        return null
    }

    @Throws(InterruptedException::class, IOException::class)
    fun generateMigCore(project: Project) {
        val migCompFile = if (project.board.isType(Board.AU_PLUS)) "mig_plus.prj" else "mig.prj"
        val tclScript = assemblePath(project.projectFolder, Project.CORES_FOLDER, migFile)
        val coresFolder = assembleLinuxPath(project.projectFolder.absolutePath, Project.CORES_FOLDER).replace("\\",
                "/")
        val projectFile = assembleLinuxPath(coresFolder, "managed_ip_project", "managed_ip_project.xpr")
        val xciFile = assembleLinuxPath(coresFolder, "mig_7series_0", "mig_7series_0.xci").replace(" ", "\\ ")
        val migSrcPrjFile = assembleFile(Locations.COMPONENTS, migCompFile)
        val migDstPrjFile = assembleFile(project.projectFolder, Project.CORES_FOLDER, "managed_ip_project", "mig.prj")
        FileUtils.copyFile(migSrcPrjFile, migDstPrjFile)
        val migPrjFile = "../managed_ip_project/mig.prj"
        //String migPrjFile = Util.assembleLinuxPath(Locations.COMPONENTS, "mig.prj").replace("\\", "/");
        val nl = System.lineSeparator()
        if (thread != null && thread!!.isAlive) {
            showError("Vivado is already running!")
            return
        }
        thread = object : Thread() {
            override fun run() {
                try {
                    clearConsole()
                    createIpProjectIfNeeded(project)
                    FileWriter(tclScript).use { fstream ->
                        BufferedWriter(fstream).use { out ->
                            out.write("open_project {$projectFile}$nl")
                            out.write(
                                    "create_ip -name mig_7series -vendor xilinx.com -library ip -module_name mig_7series_0 -dir {"
                                            + coresFolder + "}" + nl)
                            out.write("set_property -dict [list CONFIG.XML_INPUT_FILE {" + migPrjFile
                                    + "} CONFIG.RESET_BOARD_INTERFACE {Custom} CONFIG.MIG_DONT_TOUCH_PARAM {Custom} CONFIG.BOARD_MIG_PARAM {Custom}] [get_ips mig_7series_0]"
                                    + nl)
                            out.write("generate_target all [get_files {$xciFile}]$nl")
                            out.write("catch { config_ip_cache -export [get_ips -all mig_7series_0] }$nl")
                            out.write("export_ip_user_files -of_objects [get_files {" + xciFile
                                    + "}] -no_script -sync -force -quiet" + nl)
                            out.write("create_ip_run [get_files -of_objects [get_fileset sources_1] {" + xciFile + "}]"
                                    + nl)
                            out.write("launch_runs -jobs 16 mig_7series_0_synth_1$nl")
                            out.write("wait_on_run mig_7series_0_synth_1$nl")
                            out.write("export_simulation -of_objects [get_files {" + xciFile + "}] -directory {"
                                    + assembleLinuxPath(coresFolder, "ip_user_files/sim_scripts}")
                                    + " -ip_user_files_dir {" + assembleLinuxPath(coresFolder, "ip_user_files")
                                    + "} -ipstatic_source_dir {"
                                    + assembleLinuxPath(coresFolder, "ip_user_files/ipstatic")
                                    + "} -lib_map_path [list {modelsim="
                                    + assembleLinuxPath(coresFolder,
                                    "managed_ip_project/managed_ip_project.cache/compile_simlib/modelsim")
                                    + "} {questa="
                                    + assembleLinuxPath(coresFolder,
                                    "managed_ip_project/managed_ip_project.cache/compile_simlib/questa")
                                    + "} {ies="
                                    + assembleLinuxPath(coresFolder,
                                    "managed_ip_project/managed_ip_project.cache/compile_simlib/ies")
                                    + "} {xcelium="
                                    + assembleLinuxPath(coresFolder,
                                    "managed_ip_project/managed_ip_project.cache/compile_simlib/xcelium")
                                    + "} {vcs="
                                    + assembleLinuxPath(coresFolder,
                                    "managed_ip_project/managed_ip_project.cache/compile_simlib/vcs")
                                    + "} {riviera="
                                    + assembleLinuxPath(coresFolder,
                                    "managed_ip_project/managed_ip_project.cache/compile_simlib/riviera")
                                    + "}] -use_ip_compiled_libs -force -quiet" + nl)
                        }
                    }
                    println("Running TCL script...", Theme.infoTextColor)
                    runTclScript(tclScript)
                    println("Looking for new cores...", Theme.infoTextColor)
                    checkForNewCores(project)
                } catch (e: Exception) {
                    reportException(e)
                }
                try {
                    sleep(150)
                } catch (e: InterruptedException) {
                }
                println("Done.", Theme.successTextColor)
            }
        }.also { it.start() }
    }

    private fun checkForNewCores(project: Project) {
        project.removeAllIPCores()
        val coresDir = File(assemblePath(project.projectFolder, Project.CORES_FOLDER))
        for (dir in coresDir.listFiles()) {
            if (blackList.contains(dir.name)) continue
            if (!dir.isDirectory) {
                continue
            }
            println("Looking in " + dir.name)
            val core = IPCore(dir.name)
            val xciFile = findPattern(dir, Regex(".*\\.xci"), true)
            if (xciFile != null) {
                println("  Found core .xci file!")
                core.addFile(xciFile)
                val stub = findPattern(dir, Regex(".*_stub\\.v"), true)
                if (stub != null) {
                    core.stub = stub
                    println("  Found stub file!")
                } else {
                    println("  Could not find stub file! Did you let synthesis finish before closing Vivado?",
                            true)
                }
                runBlocking<Unit>(Dispatchers.SWT) { { project.addIPCore(core) }.run() }
            } else {
                println("  Could not find a .xci file!", true)
            }
        }
        GlobalScope.launch(Dispatchers.SWT) {
            {
                project.updateTree()
                try {
                    project.saveXML()
                } catch (e: IOException) {
                    showError("Failed to save project file!")
                }
            }.run()
        }
    }

    fun kill() {
        if (process != null) process!!.destroy()
    }

    companion object {
        private const val projectFile = "project.tcl"
        private const val migFile = "mig_ip.tcl"
        private val blackList = ArrayList(
                Arrays.asList("managed_ip_project", ".Xil", "ip_user_files", "project.tcl", "mig_ip.tcl"))
    }
}