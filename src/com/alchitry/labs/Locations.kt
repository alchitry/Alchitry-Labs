package com.alchitry.labs

import com.alchitry.labs.Util.logger
import com.alchitry.labs.Util.osDir
import com.alchitry.labs.gui.main.MainWindow
import java.io.File
import java.net.URISyntaxException

object Locations {
    @JvmField
    val progDir: File?
    @JvmField
    val progPrefix: String
    @JvmField
    val LIBRARY: File
    @JvmField
    val BASE: File
    @JvmField
    val COMPONENTS: File
    @JvmField
    val FIRMWARE: File
    @JvmField
    val TOOLS: File
    @JvmField
    val BIN: File
    @JvmField
    val ETC: File
    @JvmField
    val LIB: File
    @JvmField
    val RESOURCES: File
    @JvmField
    val TEMPLATE_DIR: File

    init {
        var prog: File? = null
        try {
            prog = File(MainWindow::class.java.protectionDomain.codeSource.location.toURI())
        } catch (e1: URISyntaxException) {
            e1.printStackTrace()
            logger.severe("Could not detect program directory!")
        }
        prog = prog?.parentFile

        progDir = if (prog != null && !File(prog.path + File.separator + "lib").exists())
            prog.parentFile
        else
            prog

        progPrefix = if (progDir != null) progDir.path + File.separatorChar else ""

        LIBRARY = File(progPrefix + "library")
        BASE = File(LIBRARY.toString() + File.separator + "base")
        COMPONENTS = File(LIBRARY.toString() + File.separator + "components")
        FIRMWARE = File(LIBRARY.toString() + File.separator + "firmware")
        TOOLS = File(progPrefix + "tools")
        BIN = File(TOOLS.toString() + File.separator + (osDir ?: "") + File.separator + "bin")
        ETC = File(TOOLS.toString() + File.separator + "etc")
        LIB = File(TOOLS.toString() + File.separator + (osDir ?: "") + File.separator + "lib")
        RESOURCES = File(progPrefix + "res")
        TEMPLATE_DIR = File(BASE.toString() + File.separator + "templates")
    }
}