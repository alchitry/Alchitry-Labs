package com.alchitry.labs

import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.main.LoaderWindow
import com.alchitry.labs.gui.main.MainWindow
import com.alchitry.labs.hardware.boards.Board
import com.alchitry.labs.parsers.BigFunctions
import com.alchitry.labs.widgets.CustomConsole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swt.SWT
import kotlinx.coroutines.swt.swt
import org.apache.batik.transcoder.SVGAbstractTranscoder
import org.apache.batik.transcoder.TranscoderException
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.commons.io.IOUtils
import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.apache.commons.lang3.exception.ExceptionUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyleRange
import org.eclipse.swt.events.VerifyListener
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.MessageBox
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text
import java.io.*
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.system.exitProcess

object Util {
    var display: Display? = Display.getDefault()

    @JvmField
    var console: CustomConsole? = null

    @JvmField
    var tmpDir: Path? = null

    @JvmField
    val isWindows: Boolean

    @JvmField
    val isLinux: Boolean

    @JvmField
    var osDir: String? = null

    @JvmField
    val logger: Logger

    @JvmField
    var isGUI = false
    const val logFile = "alchitry-labs.log"
    var loader: LoaderWindow? = null
    const val UNKNOWN = -1
    const val WIN32 = 0
    const val WIN64 = 1
    const val LIN32 = 2
    const val LIN64 = 3
    const val MAC32 = 4
    const val MAC64 = 5
    const val IDE = 6

    init {
        val os = System.getProperty("os.name")
        isWindows = os.startsWith("Windows")
        isLinux = os.startsWith("Linux")
        isGUI = false
        try {
            tmpDir = Files.createTempDirectory("Alchitry_IDE_")
            tmpDir?.toFile()?.deleteOnExit()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        logger = Logger.getLogger("alchitry")
        try {
            // This block configure the logger with handler and formatter
            val fh = FileHandler(logFile)
            logger.addHandler(fh)
            fh.formatter = SimpleFormatter()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    var envType = UNKNOWN
        set(env) {
            field = env
            when (field) {
                UNKNOWN -> osDir = null
                WIN32, WIN64 -> osDir = "windows"
                LIN32, LIN64, IDE -> osDir = "linux"
            }
        }

    val sourceSuffixes = arrayOf(".v", ".luc")
    val errorProviderSuffixes = arrayOf(".v", ".luc", ".acf")

    private var consoleLogger: BufferedWriter? = null

    @JvmStatic
    fun setConsoleLogger(stream: BufferedWriter?) {
        consoleLogger = stream
    }

    @JvmStatic
    val scalingFactor: Float
        get() = display?.dpi?.x?.toFloat() ?: 1.0f

    @JvmStatic
    @JvmOverloads
    fun askQuestion(message: String, title: String = "Question?", shell: Shell? = MainWindow.shell): Boolean {
        if (isGUI && shell != null) {
            return runBlocking(Dispatchers.swt(shell.display).immediate) {
                val confirm = MessageBox(shell, SWT.YES or SWT.NO or SWT.APPLICATION_MODAL)
                confirm.text = title
                confirm.message = message
                confirm.open() == SWT.YES
            }
        } else {
            println("$title: $message [y/n]")
            val br = BufferedReader(InputStreamReader(System.`in`))
            try {
                return when (br.readLine()) {
                    "y", "yes", "Yes", "YES", "Y" -> true
                    else -> false
                }
            } catch (e: IOException) {
                System.err.println("Failed to read answer from STDIN!")
                exitProcess(1)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showError(error: String, title: String = "Error!", shell: Shell? = MainWindow.shell) {
        if (isGUI && shell != null) {
            runBlocking(Dispatchers.swt(shell.display).immediate) {
                val b = MessageBox(shell, SWT.OK or SWT.ICON_ERROR or SWT.APPLICATION_MODAL)
                b.text = title
                b.message = error
                b.open()
            }
        } else {
            System.err.println("$title: $error")
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showInfo(text: String, title: String = "Info", shell: Shell? = MainWindow.shell) {
        if (isGUI && shell != null) {
            runBlocking(Dispatchers.swt(shell.display).immediate) {
                val b = MessageBox(shell, SWT.OK or SWT.ICON_INFORMATION or SWT.APPLICATION_MODAL)
                b.text = title
                b.message = text
                b.open()
            }
        } else {
            println("$title: $text")
        }
    }

    @JvmStatic
    fun clearConsole() {
        if (isGUI && console != null)
            display?.let {
                GlobalScope.launch(Dispatchers.swt(it)) {
                    console?.text = ""
                    console?.clearStyles()
                }
            }
    }

    @JvmOverloads
    @JvmStatic
    fun logException(e: Throwable?, message: String? = "An exception occurred:") {
        logger.log(Level.SEVERE, message, e)
        println(ExceptionUtils.getStackTrace(e), true)
    }

    @JvmOverloads
    @JvmStatic
    fun println(text: String?, red: Boolean = false) {
        if (red) println(text, Theme.errorTextColor) else println(text, null)
    }

    @JvmStatic
    fun println(text: String?, color: Color?) {
        print(text + System.lineSeparator(), color)
    }

    @JvmStatic
    fun print(text: String?, red: Boolean) {
        if (red) print(text, Theme.errorTextColor) else print(text, null)
    }

    @JvmStatic
    fun print(text: String?, color: Color? = null) {
        consoleLogger?.let {
            try {
                it.write(text ?: "null")
            } catch (e: Exception) {
                e.printStackTrace()
                consoleLogger = null
            }
        }
        if (isGUI) {
            if (loader?.run { setStatus(text) } == null) {
                display?.let {
                    GlobalScope.launch(Dispatchers.swt(it)) {
                        console?.let { console ->
                            val message = text ?: "null"
                            if (color != null) {
                                val end = console.charCount + message.length
                                val styleRange = StyleRange()
                                styleRange.start = end - message.length
                                styleRange.length = message.length
                                styleRange.foreground = color
                                console.addStyle(styleRange)
                            }
                            console.append(message)
                        }
                    }
                }
            }
        } else {
            kotlin.io.print(text)
        }
    }

    @JvmStatic
    fun minWidthNum(i: BigInteger): Int {
        return if (i.compareTo(BigInteger.ZERO) != 0) BigFunctions.ln(BigDecimal(i), 10).divide(BigFunctions.LOG2, RoundingMode.HALF_UP).setScale(0, RoundingMode.FLOOR).toInt() + 1 else 1
    }

    @JvmStatic
    fun minWidthNum(i: Long): Int {
        return if (i != 0L) floor(ln(i.toDouble()) / ln(2.0)).toInt() + 1 else 1
    }

    @JvmStatic
    fun minWidthNum(str: String, base: Int): Int {
        val i = str.toLong(base)
        return minWidthNum(i)
    }

    @JvmStatic
    fun widthOfMult(w1: Long, w2: Long): Int {
        return floor(ln((2.0.pow(w1.toDouble()) - 1) * (2.0.pow(w2.toDouble()) - 1)) / ln(2.0)).toInt() + 1 // max
        // value
    }

    @JvmStatic
    var workspace: String
        get() = Settings.WORKSPACE
        set(workspace) {
            Settings.WORKSPACE = workspace
        }

    @JvmStatic
    fun exceptionToString(e: Exception): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    @JvmStatic
    val isIseUpdated: Boolean
        get() {
            iSELocation?.let {
                val f = File(it)
                try {
                    val version = f.name.toFloat()
                    if (version == 14.7f) return true
                } catch (e: NumberFormatException) {
                }
            }
            return false
        }
    val isISESet: Boolean
        get() = Settings.XILINX_LOC != null

    @JvmStatic
    val iSELocation: String?
        get() {
            val xilinx = Settings.XILINX_LOC
            if (xilinx != null) return xilinx
            var path = if (isWindows) File("C:\\Xilinx") else File("/opt/Xilinx")
            if (!path.isDirectory) return null
            val subs = path.listFiles(DirectoryFileFilter.DIRECTORY as FileFilter)
            subs?.find {
                try {
                    it.name.toFloat()
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            }?.also { path = it }
            return path.absolutePath
        }

    @JvmStatic
    val vivadoLocation: String?
        get() {
            val vivado = Settings.VIVADO_LOC
            if (vivado != null) return vivado
            var path = if (isWindows) File("C:\\Xilinx\\Vivado") else File("/opt/Xilinx/Vivado")
            if (!path.isDirectory) return null
            val subs = path.listFiles(DirectoryFileFilter.DIRECTORY as FileFilter)
            subs?.find {
                try {
                    it.name.toFloat()
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            }?.also { path = it }
            return path.absolutePath
        }

    @JvmStatic
    val iceCubeFolder: String?
        get() {
            val iceCube = Settings.ICECUBE_LOC
            if (iceCube != null) return iceCube
            val path = if (isWindows) File("C:\\lscc\\iCEcube2.2017.08") else File("~/lscc/iCEcube2")
            return if (!path.isDirectory) null else path.absolutePath
        }

    @JvmStatic
    val iceCubeLicenseFile: String?
        get() = Settings.ICECUBE_LICENSE

    @JvmStatic
    val yosysCommand: String?
        get() {
            val yosys = Settings.YOSYS_LOC
            if (yosys != null) return yosys
            return if (isLinux) "yosys" else null
        }

    @JvmStatic
    val arachneCommand: String?
        get() {
            val arachne = Settings.ARACHNE_LOC
            if (arachne != null) return arachne
            return if (isLinux) "arachne-pnr" else null
        }

    @JvmStatic
    val icePackCommand: String?
        get() {
            val icepack = Settings.ICEPACK_LOC
            if (icepack != null) return icepack
            return if (isLinux) "icepack" else null
        }

    @JvmStatic
    val alchitryLoaderCommand: String?
        get() {
            if (isLinux) return "tools/linux/bin/loader"
            return if (isWindows) "tools\\windows\\bin\\loader.exe" else null
        }

    @JvmStatic
    fun <T : Named?> removeByName(list: MutableCollection<T>, name: String?): Boolean {
        val t = getByName(list, name)
        if (t != null) {
            list.remove(t)
            return true
        }
        return false
    }

    @JvmStatic
    fun findByName(list: List<Named>, name: String): Int {
        for (i in list.indices) if (name == list[i].name) return i
        return -1
    }

    @JvmStatic
    fun containsName(list: Collection<Named>?, name: String?): Boolean {
        return getByName(list, name) != null
    }

    @JvmStatic
    fun <T : Named?> getByName(list: Collection<T>?, name: String?): T? {
        list ?: return null
        for (t in list) if (t!!.name == name) return t
        return null
    }

    @JvmStatic
    @Throws(IOException::class)
    fun createTmpFont(name: String): String {
        val stream = MainWindow::class.java.getResourceAsStream("/fonts/$name")
        val f = Files.createTempFile(name.split("\\.".toRegex()).toTypedArray()[0], ".ttf").toFile()
        val out: OutputStream = FileOutputStream(f)
        IOUtils.copy(stream, out)
        out.close()
        f.deleteOnExit()
        return f.path
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readFile(path: File, encoding: Charset? = Charset.defaultCharset()): String {
        val encoded = Files.readAllBytes(Paths.get(path.absolutePath))
        return String(encoded, encoding!!)
    }

    @JvmStatic
    fun getFileText(file: File): String? {
        var t = MainWindow.getEditorText(file)
        if (t == null) try {
            t = readFile(file)
        } catch (e: IOException) {
            logger.severe(ExceptionUtils.getStackTrace(e))
        }
        return t
    }

    @JvmField
    val integerVerifier = VerifyListener { e ->
        val text = e.source as Text
        val old = text.text
        val edit = old.substring(0, e.start) + e.text + old.substring(e.end)
        if (edit.isEmpty()) {
            return@VerifyListener
        }
        try {
            edit.toInt()
        } catch (ex: NumberFormatException) {
            e.doit = false
        }
    }

    @JvmStatic
    fun startStreamPrinter(process: Process, s: InputStream, red: Boolean) {
        val printer: Thread = object : Thread() {
            override fun run() {
                val stringBuffer = StringBuffer(512)
                val buffer = CharBuffer.allocate(512)
                val stream = InputStreamReader(s)
                try {
                    while (process.isAlive) {
                        while (stream.ready()) {
                            val ct = stream.read(buffer)
                            buffer.flip()
                            if (ct == -1) return
                            val newText = buffer.toString()
                            stringBuffer.append(newText)
                            if (newText.contains("\n")) {
                                val lines = stringBuffer.toString().split("\\r?\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                for (i in 0 until lines.size - 1) println(lines[i], red)
                                stringBuffer.delete(0, stringBuffer.length - lines[lines.size - 1].length)
                            }
                        }
                        try {
                            sleep(100)
                        } catch (e: InterruptedException) {
                        }
                    }
                    while (stream.ready()) {
                        val ct = stream.read(buffer)
                        buffer.flip()
                        if (ct == -1) return
                        stringBuffer.append(buffer.toString())
                    }
                    print(stringBuffer.toString(), red)
                } catch (e: IOException) {
                } finally {
                    try {
                        stream.close()
                    } catch (e: IOException) {
                    }
                }
            }
        }
        printer.isDaemon = true
        printer.start()
    }

    @JvmStatic
    @JvmOverloads
    @Throws(InterruptedException::class)
    fun runCommand(cmd: List<String?>?, showRed: Boolean = true): Process? {
        val pb = ProcessBuilder(cmd)
        val process: Process
        process = try {
            pb.start()
        } catch (e: Exception) {
            logger.severe("Couldn't start " + pb.command()[0])
            showError("Could not start " + pb.command()[0] + "! Please check the location for " + pb.command()[0] + " is correctly set in the settings menu.")
            return null
        }
        startStreamPrinter(process, process.inputStream, false)
        startStreamPrinter(process, process.errorStream, showRed)
        return process
    }

    private fun endsWithSuffixList(testString: String?, suffixList: Array<String>): Boolean {
        testString ?: return false
        for (suffix in suffixList) {
            if (testString.endsWith(suffix)) return true
        }
        return false
    }

    @JvmStatic
    @JvmOverloads
    fun isConstraintFile(fileName: String?, board: Board? = null): Boolean {
        val set: Array<String> = if (board != null) board.supportedConstraintExtensions else Board.constraintExtensions.toTypedArray()
        return endsWithSuffixList(fileName, set)
    }

    @JvmStatic
    fun isSourceFile(file: File): Boolean {
        return isSourceFile(file.name)
    }

    @JvmStatic
    fun isSourceFile(fileName: String?): Boolean {
        return endsWithSuffixList(fileName, sourceSuffixes)
    }

    @JvmStatic
    fun hasErrorProvider(file: File): Boolean {
        return hasErrorProvider(file.name)
    }

    @JvmStatic
    fun hasErrorProvider(fileName: String?): Boolean {
        return endsWithSuffixList(fileName, errorProviderSuffixes)
    }

    @JvmStatic
    fun svgToImage(svgFile: String, width: Int, height: Int): Image? {
        val resultByteStream = ByteArrayOutputStream()
        val input = TranscoderInput(MainWindow::class.java.getResourceAsStream(svgFile))
        val output = TranscoderOutput(resultByteStream)
        val png = PNGTranscoder()
        if (height > 0) png.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height.toFloat())
        if (width > 0) png.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width.toFloat())
        try {
            png.transcode(input, output)
        } catch (e1: TranscoderException) {
            System.err.println("Failed to transcode image $svgFile!")
            return null
        }
        try {
            resultByteStream.flush()
        } catch (e1: IOException) {
            System.err.println("Failed to flush stream for image $svgFile!")
            return null
        }
        val imgStream = ByteArrayInputStream(resultByteStream.toByteArray())
        return Image(display, imgStream)
    }

    @JvmStatic
    fun assembleLinuxPath(vararg pieces: String?): String {
        val out = java.lang.String.join("/", *pieces)
        return out.replace("//", "/") // remove any duplicate separators
    }

    @JvmStatic
    fun assemblePath(parent: File, vararg pieces: String?): String {
        val out = parent.absolutePath + File.separator + java.lang.String.join(File.separator, *pieces)
        return out.replace(File.separator + File.separator, File.separator) // remove any duplicate separators
    }

    @JvmStatic
    fun assemblePath(vararg pieces: String?): String {
        val out = java.lang.String.join(File.separator, *pieces)
        return out.replace(File.separator + File.separator, File.separator) // remove any duplicate separators
    }

    @JvmStatic
    fun assembleFile(parent: File, vararg pieces: String?): File {
        return File(assemblePath(parent, *pieces))
    }

    @JvmStatic
    fun assembleFile(vararg pieces: String?): File {
        return File(assemblePath(*pieces))
    }

    @JvmStatic
    fun changeExt(file: File, ext: String): File {
        val name = file.name
        return assembleFile(file.parent, name.substring(0, name.lastIndexOf(".")) + ext)
    }

    private val hexArray = "0123456789ABCDEF".toCharArray()

    @JvmStatic
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    @JvmStatic
    fun stringToByte(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    @JvmStatic
    @Deprecated("Use coroutines instead.")
    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            logException(e, "Sleep interrupted but not important!")
        }
    }

    @JvmStatic
    @Deprecated("Use coroutines instead.", ReplaceWith("GlobalScope.launch(Dispatchers.SWT) { runnable.run() }", "kotlinx.coroutines.GlobalScope", "kotlinx.coroutines.launch", "kotlinx.coroutines.Dispatchers", "kotlinx.coroutines.swt.SWT"))
    fun asyncExec(runnable: Runnable) {
        GlobalScope.launch(Dispatchers.SWT) {
            runnable.run()
        }
    }

    @JvmStatic
    @Deprecated("Use coroutines instead.", ReplaceWith("runBlocking(Dispatchers.SWT) { runnable.run() }", "kotlinx.coroutines.runBlocking", "kotlinx.coroutines.Dispatchers", "kotlinx.coroutines.swt.SWT"))
    fun syncExec(runnable: Runnable) {
        runBlocking(Dispatchers.SWT.immediate) {
            runnable.run()
        }
    }
}