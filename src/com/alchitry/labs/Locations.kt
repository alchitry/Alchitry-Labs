package com.alchitry.labs

import com.alchitry.labs.Util.logger
import com.alchitry.labs.Util.osDir
import com.alchitry.labs.gui.main.MainWindow
import java.io.File
import java.net.URISyntaxException
import java.net.URL

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
            var uri = MainWindow::class.java.protectionDomain.codeSource.location.toURI()
            if (Util.isWindows && uri.authority != null && uri.authority.isNotEmpty()) {
                // Hack for UNC Path
                uri = URL("file://" + uri.toString().substring("file:".length)).toURI()
            }
            prog = File(uri)
        } catch (e1: URISyntaxException) {
            e1.printStackTrace()
            logger.severe("Could not detect program directory!")
        }
        prog = prog?.parentFile

        progDir = if (prog != null && !Util.assembleFile(prog.path, "lib").exists())
            prog.parentFile
        else
            prog

        progPrefix = progDir?.path ?: ""

        LIBRARY = Util.assembleFile(progPrefix, "library")
        BASE = Util.assembleFile(LIBRARY, "base")
        COMPONENTS = Util.assembleFile(LIBRARY, "components")
        FIRMWARE = Util.assembleFile(LIBRARY, "firmware")
        TOOLS = Util.assembleFile(progPrefix + "tools")
        BIN = Util.assembleFile(TOOLS, (osDir ?: ""), "bin")
        ETC = Util.assembleFile(TOOLS, "etc")
        LIB = Util.assembleFile(TOOLS, (osDir ?: ""), "lib")
        RESOURCES = Util.assembleFile(progPrefix + "res")
        TEMPLATE_DIR = Util.assembleFile(BASE, "templates")
    }
}