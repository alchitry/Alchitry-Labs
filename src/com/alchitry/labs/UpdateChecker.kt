package com.alchitry.labs

import com.alchitry.labs.gui.FileDownloaderDialog
import com.alchitry.labs.gui.main.MainWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swt.SWT
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.nio.channels.ReadableByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object UpdateChecker {
    private val BASE_URL
        get() = if (Settings.BETA_UPDATES.get()) "https://cdn.alchitry.com/labs/beta/" else "https://cdn.alchitry.com/labs/"
    private val IDE_VERSION_URL
        get() = "${BASE_URL}ideVersion"

    @Throws(IOException::class)
    fun copyLibrary(from: String): Boolean {
        val srcBaseFile = File(from)
        val srcLibFile = File(from + "library")
        val dstBaseFile = Locations.LIBRARY
        if (!Util.askQuestion("Update Library", "All files in $dstBaseFile will be replaced with the new library files.\n\nContinue?")) return false
        if (dstBaseFile.exists()) FileUtils.deleteDirectory(dstBaseFile)
        FileUtils.copyDirectory(srcLibFile, dstBaseFile)
        FileUtils.deleteDirectory(srcBaseFile)
        return true
    }

    @Throws(IOException::class)
    private fun copyDirectory(src: File, dst: File) {
        if (!dst.exists()) if (!dst.mkdir()) throw IOException("Failed to make directory " + dst.path)
        src.listFiles()?.forEach { entry ->
            if (entry.isDirectory) {
                val d = File(dst.path + File.separator + entry.name)
                if (!d.mkdir()) throw IOException("Failed to make directory " + d.path)
                copyDirectory(entry, d)
            } else if (entry.isFile) {
                copyFileToDirectory(entry, dst)
            }
        } ?: throw IOException("Failed to read source files!")
    }

    @Throws(IOException::class)
    private fun copyFileToDirectory(src: File, dst: File) {
        val s = Paths.get(src.path)
        val d = Paths.get(dst.path + File.separator + src.name)
        Files.copy(s, d, StandardCopyOption.COPY_ATTRIBUTES)
    }

    fun checkForUpdates() {
        if (Util.envType != Util.IDE && Settings.CHECK_FOR_UPDATES.get()) GlobalScope.launch(Dispatchers.IO) {
            val inputStream: InputStream = try {
                URL(IDE_VERSION_URL).openStream()
            } catch (e: IOException) {
                return@launch
            }
            var version: String? = null
            try {
                version = IOUtils.toString(inputStream)
            } catch (e: IOException) {
                Util.showError("Error while checking for update", e.message!!)
            } finally {
                IOUtils.closeQuietly(inputStream)
            }
            if (version == null) return@launch
            val curVersion = Settings.VERSION.get()
            if (version != curVersion) {
                val result = Util.askQuestion("New Alchitry Labs Available", "Would you like to update the IDE to version $version now?")
                if (result) {
                    val website: URL = try {
                        when (Util.envType) {
                            Util.LIN64 -> URL(BASE_URL + "alchitry-labs-" + version + "-linux.tgz")
                            Util.WIN64 -> URL(BASE_URL + "alchitry-labs-" + version + "-windows.msi")
                            else -> {
                                Util.showError("Unknown IDE Type")
                                return@launch
                            }
                        }
                    } catch (e: MalformedURLException) {
                        Util.showError("Failed to open URL.")
                        return@launch
                    }
                    val rbc: ReadableByteChannel? = null
                    val fos: FileOutputStream? = null
                    val tempDir: Path
                    try {
                        if (Util.envType == Util.LIN32 || Util.envType == Util.LIN64) {
                            tempDir = Files.createTempDirectory("alchitry_labs_")
                        } else {
                            tempDir = Paths.get(Util.workspace)
                            val f = tempDir.toFile()
                            if (!f.exists()) {
                                Files.createDirectories(tempDir)
                            }
                        }
                    } catch (e1: IOException) {
                        Util.showError("Could not create temporary directory")
                        return@launch
                    }
                    try {
                        var stringPath = tempDir.toString()
                        if (!stringPath.endsWith(File.separator)) stringPath += File.separator
                        var arcName: String? = null
                        when (Util.envType) {
                            Util.LIN32, Util.LIN64 -> arcName = "alchitry-labs.tgz"
                            Util.WIN32, Util.WIN64 -> arcName = "alchitry-labs.msi"
                        }
                        val libZip = File(stringPath + arcName)
                        if (libZip.exists()) libZip.delete()
                        val dlFile = libZip.path
                        val dlRes = DownloadResult()
                        runBlocking(Dispatchers.SWT) {
                            val downloader = FileDownloaderDialog(MainWindow.shell, website, dlFile, "Update")
                            dlRes.status = downloader.open()
                        }
                        if (!dlRes.status) throw IOException()
                        val cmd: Array<String?>
                        if (Util.envType == Util.LIN32 || Util.envType == Util.LIN64) {
                            ExtractUtility.untargz(libZip.path, stringPath)
                            var ideFolder: String? = null
                            val dstDir = Locations.progDir ?: throw IOException("Couldn't find project directory!")
                            var matched = 0
                            dstDir.listFiles()?.forEach {
                                when (it.name) {
                                    "lib", "library", "alchitry-labs" -> matched++
                                }
                            }
                            if (matched != 3) {
                                if (!Util.askQuestion("The directory ${dstDir.path} does not seem to be a valid Alchitry Labs install. Continue anyways?", "Unknown Install Location")) return@launch
                            }
                            File(stringPath).listFiles()?.also { files ->
                                for (entry in files) {
                                    if (entry.isDirectory && entry.name.startsWith("alchitry-labs")) {
                                        ideFolder = entry.name
                                        break
                                    }
                                }
                            } ?: throw IOException("Failed to read temp files!")
                            if (ideFolder == null) {
                                Util.showError("Could not find IDE directory")
                                return@launch
                            }
                            if (!Util.askQuestion("""|The files in ${dstDir.path} will all be deleted and replaced with the new Alchitry Labs files.
                                                     |
                                                     |Continue?""".trimMargin(), "Continue with Update?")) return@launch

                            // Flush the folder
                            dstDir.listFiles()?.forEach {
                                if (it.isDirectory) FileUtils.deleteDirectory(it) else it.delete()
                            } ?: throw IOException("Failed to list temp files!")
                            File(stringPath + ideFolder).listFiles()?.forEach {
                                val dest = File(it.name)
                                if (it.isDirectory) copyDirectory(it, dest) else copyFileToDirectory(it, dstDir)
                            } ?: throw IOException("Failed to list IDE files!")
                            cmd = arrayOf("nohup", Locations.progPrefix + "alchitry-labs")
                        } else { // windows
                            cmd = arrayOf("msiexec.exe", "/i", libZip.absolutePath)
                        }
                        MainWindow.close()
                        val pb = ProcessBuilder(*cmd)
                        try {
                            pb.start()
                        } catch (e: Exception) {
                            Util.logger.severe(ExceptionUtils.getStackTrace(e))
                        }
                        return@launch
                    } catch (e: IOException) {
                        Util.logger.severe(ExceptionUtils.getStackTrace(e))
                        Util.showError("Failed to download new version.")
                    } finally {
                        IOUtils.closeQuietly(rbc)
                        IOUtils.closeQuietly(fos)
                        if (Util.envType == Util.LIN32 || Util.envType == Util.LIN64) {
                            try {
                                FileUtils.deleteDirectory(tempDir.toFile())
                            } catch (e: IOException) {
                            }
                        }
                    }
                }
            }
            return@launch
        }
    }

    private class DownloadResult {
        var status = false
    }
}