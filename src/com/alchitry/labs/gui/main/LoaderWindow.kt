package com.alchitry.labs.gui.main

import com.alchitry.labs.Util
import com.alchitry.labs.Util.showError
import com.alchitry.labs.VERSION
import com.alchitry.labs.hardware.boards.AlchitryAu
import com.alchitry.labs.hardware.boards.AlchitryCu
import com.alchitry.labs.hardware.boards.Board
import com.alchitry.labs.hardware.boards.Mojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import org.eclipse.wb.swt.SWTResourceManager
import java.io.File

class LoaderWindow {
    private val shell = Shell()
    private val binText: Text
    private val btnAuButton: Button
    private val btnCuButton: Button
    private val btnEraseButton: Button
    private val btnFlashCheckbox: Button
    private val btnProgramButton: Button
    private val btnOpenBinButton: Button
    private val lblStatus: Label
    private val btnMojoButton: Button

    /**
     * Open the window.
     *
     * @wbp.parser.entryPoint
     */
    fun open() {
        val display = Display.getDefault()
        Util.display = display
        shell.open()
        shell.layout()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
    }

    private fun setEnabled(enabled: Boolean) {
        btnAuButton.isEnabled = enabled
        btnCuButton.isEnabled = enabled
        btnEraseButton.isEnabled = enabled
        btnProgramButton.isEnabled = enabled
        btnOpenBinButton.isEnabled = enabled
        btnFlashCheckbox.isEnabled = enabled && !btnCuButton.selection
        binText.isEnabled = enabled
        shell.isEnabled = enabled
    }

    fun setStatus(status: String?) {
        runBlocking(Dispatchers.SWT.immediate) {
            lblStatus.text = status
        }
    }

    /**
     * Create contents of the window.
     */
    init {
        shell.text = "Alchitry Loader V$VERSION"
        shell.layout = GridLayout(5, false)
        shell.image = SWTResourceManager.getImage(LoaderWindow::class.java, "/images/icon.png")
        binText = Text(shell, SWT.BORDER)
        binText.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1)
        btnOpenBinButton = Button(shell, SWT.NONE)
        btnOpenBinButton.layoutData = GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1)
        btnOpenBinButton.text = "Open Bin File"
        btnOpenBinButton.addListener(SWT.Selection) {
            val fd = FileDialog(shell)
            fd.filterExtensions = arrayOf("*.bin", "*.*")
            fd.filterNames = arrayOf("Bin Files", "All Files")
            val file = fd.open()
            if (file != null) binText.text = file
        }
        Label(shell, SWT.NONE).run{text = "Board:"}
        btnAuButton = Button(shell, SWT.RADIO)
        btnAuButton.selection = true
        btnAuButton.text = "Alchitry Au"

        btnCuButton = Button(shell, SWT.RADIO)
        btnCuButton.text = "Alchitry Cu"

        btnMojoButton = Button(shell, SWT.RADIO)
        btnMojoButton.text = "Mojo"
        Label(shell, SWT.NONE)
        btnFlashCheckbox = Button(shell, SWT.CHECK)
        btnFlashCheckbox.selection = true
        btnFlashCheckbox.layoutData = GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1)
        btnFlashCheckbox.text = "Program Flash"

        btnAuButton.addListener(SWT.Selection) { btnFlashCheckbox.isEnabled = true }
        btnCuButton.addListener(SWT.Selection) {
            btnFlashCheckbox.selection = true
            btnFlashCheckbox.isEnabled = false
        }

        Label(shell, SWT.NONE)
        btnEraseButton = Button(shell, SWT.NONE)
        btnEraseButton.layoutData = GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1)
        btnEraseButton.text = "Erase"
        btnEraseButton.addListener(SWT.Selection) {
            setEnabled(false)
            setStatus("Starting...")
            erase(board)
        }
        Label(shell, SWT.NONE).run {
            layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
            text = "Status:"
        }
        this.lblStatus = Label(shell, SWT.NONE)
        this.lblStatus.layoutData = GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1)
        Label(shell, SWT.NONE)
        btnProgramButton = Button(shell, SWT.NONE)
        btnProgramButton.layoutData = GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1)
        btnProgramButton.text = "Program"
        btnProgramButton.addListener(SWT.Selection, Listener {
            if (!checkBinFileExists()) return@Listener
            setEnabled(false)
            setStatus("Starting...")
            program(binText.text, btnFlashCheckbox.selection, board)
        })
        shell.pack()
        val s = shell.size
        shell.minimumSize = s
        s.x = s.x.coerceAtLeast(500)
        shell.size = s
    }

    private val board: Board
        get() = if (btnAuButton.selection) AlchitryAu else if (btnCuButton.selection) AlchitryCu else Mojo

    private fun checkBinFileExists(): Boolean {
        val f = File(binText.text)
        if (!f.exists()) {
            showError("Bin file could not be opened!")
            return false
        }
        return true
    }

    private fun program(binFile: String, flash: Boolean, board: Board) {
        GlobalScope.launch(Dispatchers.Default) {
            val loader = board.loader
            loader.setProgressCallback { percent -> setStatus("$percent%") }
            loader.load(binFile, flash, false)
            launch(Dispatchers.SWT) { setEnabled(true) }
        }
    }

    private fun erase(board: Board) {
        GlobalScope.launch(Dispatchers.Default) {
            board.loader.erase()
            launch(Dispatchers.SWT) { setEnabled(true) }
        }
    }
}