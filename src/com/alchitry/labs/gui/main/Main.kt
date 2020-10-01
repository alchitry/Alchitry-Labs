package com.alchitry.labs.gui.main

import com.alchitry.labs.Reporter
import com.alchitry.labs.UpdateChecker
import com.alchitry.labs.Util
import java.io.IOException
import java.util.logging.Level
import kotlin.system.exitProcess

const val VERSION = "B1.2.2"

fun main(args: Array<String>) {
    parseCommand(args)
    Util.isGUI = true
    try {
        MainWindow.open()
    } catch (e: Exception) {
        Util.logger.log(Level.SEVERE, "", e)
        if (Util.envType != Util.IDE) Reporter.reportException(e)
        MainWindow.saveOnCrash()
    }
    return
}

private fun parseCommand(args: Array<String>) {
    if (args.isNotEmpty()) {
        when (args[0]) {
            "lin32" -> Util.envType = Util.LIN32
            "lin64" -> Util.envType = Util.LIN64
            "win32" -> Util.envType = Util.WIN32
            "win64" -> Util.envType = Util.WIN64
            "mac32" -> Util.envType = Util.MAC32
            "mac64" -> Util.envType = Util.MAC64
            "ide" -> Util.envType = Util.IDE
        }
    }
    if (Util.envType == Util.UNKNOWN) {
        if (args.size == 2 && args[0] == "-u") {
            try {
                UpdateChecker.copyLibrary(args[1])
            } catch (e: IOException) {
                exitProcess(1)
            }
            exitProcess(0)
        } else {
            System.err.println("Library value missing after -u!")
            exitProcess(2)
        }
    } else if (args.size > 1 && args[1] == "--loader") {
        runLoader()
    }
}

private fun runLoader() {
    val loader: LoaderWindow
    Util.isGUI = true
    try {
        loader = LoaderWindow()
        Util.loader = loader
        loader.open()
    } catch (e: Exception) {
        Util.logger.log(Level.SEVERE, "", e)
        if (Util.envType != Util.IDE) Reporter.reportException(e)
    }
    System.exit(0)
}