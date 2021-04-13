package com.alchitry.labs

import com.alchitry.labs.gui.main.LoaderWindow
import com.alchitry.labs.gui.main.MainWindow
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.logging.Level
import kotlin.system.exitProcess

const val VERSION = "1.2.6"

fun main(args: Array<String>) {
    parseCommand(args)
    Util.isGUI = true
    try {
        MainWindow.open()
    } catch (e: Throwable) {
        Util.logger.log(Level.SEVERE, "", e)
        if (Util.envType != Util.OS.IDE) Reporter.reportException(e, true)
        MainWindow.saveOnCrash()
    } finally {
        Settings.commit()
    }
    runBlocking { Reporter.waitForAll() }
    return
}

private fun parseCommand(args: Array<String>) {
    if (args.isNotEmpty()) {
        when (args[0]) {
            "lin32" -> Util.envType = Util.OS.LIN32
            "lin64" -> Util.envType = Util.OS.LIN64
            "win32" -> Util.envType = Util.OS.WIN32
            "win64" -> Util.envType = Util.OS.WIN64
            "mac32" -> Util.envType = Util.OS.MAC32
            "mac64" -> Util.envType = Util.OS.MAC64
            "ide" -> Util.envType = Util.OS.IDE
        }
    }
    if (Util.envType == Util.OS.UNKNOWN) {
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
    } catch (e: Throwable) {
        Util.logger.log(Level.SEVERE, "", e)
        if (Util.envType != Util.OS.IDE) Reporter.reportException(e, true)
    }
    runBlocking { Reporter.waitForAll() }
    exitProcess(0)
}