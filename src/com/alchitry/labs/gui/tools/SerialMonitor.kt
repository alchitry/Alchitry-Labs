package com.alchitry.labs.gui.tools

import com.alchitry.labs.Util.askQuestion
import com.alchitry.labs.Util.showError
import com.alchitry.labs.gui.BaudDialog
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.main.MainWindow
import com.alchitry.labs.hardware.boards.Board
import com.alchitry.labs.hardware.usb.SerialDevice
import com.alchitry.labs.hardware.usb.UsbUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swt.swt
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.VerifyEvent
import org.eclipse.swt.events.VerifyListener
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.eclipse.wb.swt.SWTResourceManager
import org.usb4java.LibUsbException

class SerialMonitor(display: Display) {
    private val shell: Shell = Shell(display, SWT.CLOSE or SWT.RESIZE or SWT.MIN or SWT.TITLE or SWT.MAX)
    private val text: StyledText

    // protected CustomCombo combo;
    private var cursorPos = 0
    private var port: SerialDevice? = null
    private var ignoreText = false

    init {
        shell.text = "Serial Port Monitor"
        shell.setSize(450, 300)
        shell.setMinimumSize(450, 300)
        val layout = GridLayout(1, false)
        shell.layout = layout
        shell.background = Theme.editorBackgroundColor
        shell.foreground = Theme.comboBackgroundColor
        shell.image = SWTResourceManager.getImage(MainWindow::class.java, "/images/icon.png")
        cursorPos = 0
        text = StyledText(shell, SWT.V_SCROLL or SWT.H_SCROLL)
        text.alwaysShowScrollBars = false
        text.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
        text.background = Theme.editorBackgroundColor
        text.foreground = Theme.editorForegroundColor
        text.addVerifyListener(object : VerifyListener {
            override fun verifyText(e: VerifyEvent) {
                if (port != null && !ignoreText) {
                    e.doit = false
                    var t = e.text
                    val len = e.end - e.start
                    if (t.isEmpty() && len > 0) {
                        t = "\b"
                    }
                    port!!.writeData(t.toByteArray())
                }
                run {
                    e.text = e.text.replace("\r", "")
                    text.topIndex = text.lineCount - 1
                }
            }
        })
        ignoreText = false

        shell.open()
        shell.layout()
        shell.setFocus()

        connect()
    }

    private fun connect() {
        val port = UsbUtil.openSerial()
        this.port = port
        if (port == null) {
            shell.dispose()
            return
        }
        MainWindow.project.let { project ->
            if (project == null) {
                showError("Please open a project before opening the serial port monitor!")
                shell.dispose()
                return
            }
            if (!project.board.isType(Board.MOJO)) {
                var baud = 1000000
                var setBaud: Int
                while (true) {
                    val bd = BaudDialog(shell, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL)
                    baud = bd.open(baud)
                    if (baud < 0) {
                        shell.dispose()
                        return
                    }
                    setBaud = try {
                        port.setBaudrate(baud)
                    } catch (e: LibUsbException) {
                        showError(e.message ?: "USB error!", "Failed to set baudrate!", shell)
                        continue
                    }
                    if (setBaud == baud) {
                        break
                    } else {
                        if (askQuestion("The requested baudrate could not be set. The actual baudrate is $setBaud. Is this acceptable?",
                                        "Baud Mismatch",
                                        shell)) break
                    }
                }
            }
        }
        port.setTimeouts(100, 2000)
        val t: Thread = object : Thread() {
            override fun run() {
                val buffer = ByteArray(1)
                while (!shell.isDisposed) {
                    try {
                        val len = port.readDataWithTimeout(buffer)
                        if (len > 0) {
                            val rx = String(buffer.copyOfRange(0, len))
                            GlobalScope.launch(Dispatchers.swt(shell.display)) { if (!shell.isDisposed) addText(rx) }
                        }
                    } catch (e: LibUsbException) {
                        if (!e.message!!.contains("timed out")) {
                            GlobalScope.launch(Dispatchers.swt(shell.display)) {
                                if (!shell.isDisposed) {
                                    showError("Error reading the serial port!", shell = shell)
                                    shell.dispose()
                                }
                            }
                            return
                        }
                    }
                }
                disconnect()
            }
        }
        t.isDaemon = true
        t.start()
    }

    fun close() {
        shell.dispose()
    }

    private fun disconnect() {
        if (port != null) {
            port!!.close()
        }
        port = null
    }

    fun setFocus() {
        shell.setFocus()
    }

    val isDisposed: Boolean
        get() = shell.isDisposed

    private fun addText(t: String) {
        val st = t.split("\b".toRegex()) // split on backspace
        var first = true
        ignoreText = true
        text.caretOffset = cursorPos
        for (s in st) {
            if (!first && cursorPos > 0) {
                text.replaceTextRange(cursorPos - 1, 1, "")
                text.caretOffset = --cursorPos
            }
            first = false
            text.insert(s)
            cursorPos += s.length
            text.caretOffset = cursorPos
            cursorPos = text.caretOffset
        }
        ignoreText = false
    }
}