package com.alchitry.labs.gui

import com.alchitry.labs.Util
import com.alchitry.labs.dictionaries.AlchitryConstraintsDictionary
import com.alchitry.labs.dictionaries.LucidDictionary
import com.alchitry.labs.gui.main.MainWindow
import com.alchitry.labs.gui.util.HotKeys
import com.alchitry.labs.gui.util.Search
import com.alchitry.labs.gui.util.UndoRedo
import com.alchitry.labs.parsers.errors.AlchitryConstraintsErrorProvider
import com.alchitry.labs.parsers.errors.ErrorProvider
import com.alchitry.labs.parsers.errors.LucidErrorProvider
import com.alchitry.labs.parsers.errors.VerilogErrorProvider
import com.alchitry.labs.parsers.styles.*
import com.alchitry.labs.style.*
import com.alchitry.labs.tools.ParserCache
import com.alchitry.labs.widgets.CustomSearchAndReplace
import com.alchitry.labs.widgets.CustomTabs
import com.alchitry.labs.widgets.TabChild
import com.alchitry.labs.widgets.TabHotKeys
import kotlinx.coroutines.*
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.ExtendedModifyListener
import org.eclipse.swt.custom.LineStyleListener
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.*
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.printing.PrintDialog
import org.eclipse.swt.printing.Printer
import org.eclipse.swt.widgets.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.util.*

class StyledCodeEditor(private var tabFolder: CustomTabs, style: Int, var file: File?, private var writable: Boolean) : StyledText(tabFolder, style), ModifyListener, TabChild {
    private var edited = false
    private var skipEdit = false
    val isOpen: Boolean
    private var formatter: AutoFormatter? = null
    private val undoRedo = UndoRedo(this)
    val fileName: String
        get() = file?.name ?: "Untitled"
    private var autoComplete: AutoComplete? = null
    private val highlighter = TextHighlighter(this)
    private val search: CustomSearchAndReplace = CustomSearchAndReplace(parent, SWT.NONE)
    private val doubleClick: DoubleClickHighlighter
    private val lineStyleListeners = ArrayList<LineStyleListener>()
    private var projectSaveListener: Listener? = null
    private var errorChecker: ErrorProvider? = null
    private var hasErrors: Boolean = false
    private var searchActive = false
    private var searchResults: Search? = null
    var isLucid = false
    var isVerilog = false
    private var isConstraint = false
    private val rightClickMenu = Menu(this)
    private val monitorScope = CoroutineScope(Dispatchers.IO)
    private var monitorJob: Job? = null

    private val noWriteListener = VerifyListener { e ->
        file?.let {
            if (Util.askQuestion("This file is read only! Would you like to make a local copy to edit?", "Create editable copy?"))
                if (MainWindow.project?.copyLibraryFile(it)?.also {
                            GlobalScope.launch(Dispatchers.SWT) { openFile(it) }
                        } == null) {
                    Util.showError("Failed to copy file into project!")
                }
        }
        e.doit = false
    }

    init {
        // attach search to parent so that it doesn't scroll with the text
        if (!Util.isWindows) // Windows has a bug where hidden scroll bars flash
            alwaysShowScrollBars = false
        background = Theme.editorBackgroundColor
        foreground = Theme.editorForegroundColor
        selectionBackground = Theme.editorTextSelectedColor
        selectionForeground = null

        bottomMargin = 5

        val lineHighlighter = LineHighlighter(this)
        addCaretListener(lineHighlighter)
        addExtendedModifyListener(lineHighlighter)

        var indentProvider: IndentProvider? = null
        var newLineIndenter: VerifyListener? = null
        var unindentProvider: ExtendedModifyListener? = null

        addVerifyListener(undoRedo)

        file?.let {
            when {
                it.name.endsWith(".luc") -> {
                    val dict = LucidDictionary(this)
                    val lErrorChecker = LucidErrorProvider(this, dict)
                    lineStyleListeners.add(lErrorChecker)
                    addModifyListener(lErrorChecker)
                    errorChecker = lErrorChecker
                    val lsp = LucidStyleProvider(this)
                    addModifyListener(lsp)
                    lineStyleListeners.add(lsp)
                    // indentProvider = new LucidIndentProvider();
                    val nli = LucidNewLineIndenter(this, undoRedo)
                    newLineIndenter = nli
                    unindentProvider = nli
                    indentProvider = nli
                    isLucid = true
                    autoComplete = AutoComplete(this, dict)
                }
                it.name.endsWith(".v") -> {
                    val vErrorChecker = VerilogErrorProvider(this)
                    lineStyleListeners.add(vErrorChecker)
                    addModifyListener(vErrorChecker)
                    errorChecker = vErrorChecker
                    val vsp = VerilogStyleProvider(this)
                    lineStyleListeners.add(vsp)
                    addModifyListener(vsp)
                    indentProvider = VerilogIndentProvider()
                    newLineIndenter = VerilogNewLineIndenter(this, undoRedo)
                    isVerilog = true
                }
                it.name.endsWith(".acf") -> {
                    val aErrorChecker = AlchitryConstraintsErrorProvider(this)
                    lineStyleListeners.add(aErrorChecker)
                    addModifyListener(aErrorChecker)
                    errorChecker = aErrorChecker
                    val asp = AlchitryConstraintStyleProvider(this)
                    lineStyleListeners.add(asp)
                    addModifyListener(asp)
                    val dict = AlchitryConstraintsDictionary()
                    val p = MainWindow.project
                    p?.saveListeners?.add(Listener { dict.updatePortNames() }.also { listener -> projectSaveListener = listener })
                    autoComplete = AutoComplete(this, dict)
                    isConstraint = true
                }
                Util.isConstraintFile(it.name) -> {
                    isConstraint = true
                    // TODO : native constraint checking
                }
                else -> {
                    Util.logger.info("UNSUPPORTED FILE TYPE. $it")
                }
            }
        }

        val tooltips = ToolTipListener(this, errorChecker)
        addMouseTrackListener(tooltips)
        addMouseMoveListener(tooltips)

        val styler = LineStyler(this)
        addLineStyleListener(styler)
        addModifyListener(styler)

        tabs = 2
        font = Theme.monoFont
        autoComplete?.updateFont()
        tabFolder.addTab(fileName, this)
        if (indentProvider != null) formatter = AutoFormatter(this, indentProvider)
        newLineIndenter?.let { addVerifyListener(it) }
        unindentProvider?.let { addExtendedModifyListener(it) }
        isOpen = openFile(file)
        addModifyListener(this)
        addVerifyKeyListener(HotKeys(this))
        addKeyListener(TabHotKeys(this))

        lineStyleListeners.add(highlighter)
        addModifyListener(highlighter)
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(event: KeyEvent) {
                if (event.keyCode == 'p'.toInt() && event.stateMask == SWT.CTRL) {
                    print()
                }
            }
        })
        addFocusListener(object : FocusListener {
            override fun focusLost(arg0: FocusEvent) {}
            override fun focusGained(arg0: FocusEvent) {
                MainWindow.lastActiveEditor = this@StyledCodeEditor
            }
        })
        doubleClick = DoubleClickHighlighter(this, isLucid, isVerilog)
        addListener(SWT.MouseDown, doubleClick)
        lineStyleListeners.add(doubleClick)

        val bracketUnderliner = BracketUnderliner(this)
        addCaretListener(bracketUnderliner)
        lineStyleListeners.add(bracketUnderliner)

        addLineStyleListener { for (l in lineStyleListeners) l.lineGetStyle(it) }
        addVerifyKeyListener { event ->
            if (autoComplete?.isActive == true) {
                event.doit = event.doit && autoComplete?.keyPressed(event.keyCode) == false
            }
        }

        BlockIndenter(this) // converts tabs into spaces and multiline tabs to indents
        search.addModifyListener(this)
        search.addKeyListener(object : KeyListener {
            override fun keyReleased(e: KeyEvent) {}
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == SWT.ESC.toInt())
                    setSearch(false)
                else if (e.keyCode == SWT.CR.toInt() || e.keyCode == SWT.KEYPAD_CR)
                    search(true)
            }
        })
        search.addNextListener { search(true) }
        search.addPrevListener { search(false) }
        search.addReplaceOnceListener { replace(false) }
        search.addReplaceAllListener { replace(true) }
        val invalidateSearchListener = Listener {
            searchResults = null
            updateSearchResults()
        }
        search.addCaseSensitiveListener(invalidateSearchListener)
        search.addRegexListener(invalidateSearchListener)
        search.isVisible = false
        addDisposeListener { search.dispose() }

        setupMenu()

        addTraverseListener { e -> e.doit = false }
        addModifyListener { ParserCache.invalidate(file) }

        monitorScope.startFileMonitor()
        addDisposeListener {
            monitorScope.cancel()
        }
    }

    fun CoroutineScope.startFileMonitor() {
        file?.let {
            monitorJob = launch {
                var fileTimestamp = it.lastModified()
                while (true) {
                    if (fileTimestamp == 0L) {
                        Util.showError("Failed to read file ${it.name}!")
                        launch(Dispatchers.SWT) {
                            tabFolder.close(this@StyledCodeEditor)
                        }
                        return@launch
                    }
                    delay(1000)
                    val currentTime = it.lastModified()
                    if (fileTimestamp != currentTime) {
                        withContext(Dispatchers.SWT) {
                            if (this@StyledCodeEditor.isFocusControl) {
                                if (Util.askQuestion("${it.name} has changed on disk. Would you like to reload it?")) {
                                    openFile(it)
                                }
                                fileTimestamp = currentTime
                            }
                        }
                    }
                }

            }
        }
    }



    override fun dispose() {
        autoComplete?.dispose()
        search.dispose()
        super.dispose()
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        search.isVisible = searchActive
    }

    override fun switchFolder(folder: CustomTabs) {
        if (folder !== tabFolder) {
            if (!setParent(folder) || !search.setParent(folder)) {
                Util.showError("Moving tabs between windows isn't supported on your platform!")
            }
            tabFolder.remove(this)
            tabFolder = folder
            tabFolder.addTab(fileName, this)
        }
    }

    fun setSearch(s: Boolean) {
        if (!s) {
            if (!searchActive) return
            highlighter.setMatches(null)
        } else if (!searchActive || doubleClick.word != null) {
            val selection = doubleClick.word
            if (selection?.isEmpty() == false) {
                search.setSearchText(selection)
                doubleClick.clearWord()
            }
        }
        searchActive = s
        if (searchActive) {
            bottomMargin = CustomSearchAndReplace.HEIGHT + 5
            search.isVisible = true
            search.setFocus()
            updateSearchResults()
        } else {
            bottomMargin = 5
            searchResults = null
            search.isVisible = false
            setFocus()
        }
    }

    private fun placeSearch() {
        var p = size
        val fs = search.computeSize(SWT.DEFAULT, SWT.DEFAULT)
        p.x = 0
        p.y -= fs.y
        p = display.map(this, parent, p)
        search.setBounds(p.x, p.y, fs.x, fs.y)
        search.moveAbove(this)
    }

    override fun setBounds(x: Int, y: Int, w: Int, h: Int) {
        super.setBounds(x, y, w, h)
        placeSearch()
    }

    fun hasErrors(): Boolean {
        return hasErrors
    }

    override fun isModified(): Boolean {
        return edited
    }

    fun formatText() {
        if (formatter == null) return
        var caret = caretOffset
        var line = getLineAtOffset(caret)
        val origLength = getLine(line).length
        var offset = caret - getOffsetAtLine(line)
        formatter?.fixIndent()
        line = line.coerceAtMost(lineCount - 1)
        caret = getOffsetAtLine(line)
        val newLength = getLine(line).length
        offset += newLength - origLength
        caret += newLength.coerceAtMost(offset)
        caretOffset = caret
    }

    fun grabFocus() {
        tabFolder.setSelection(this)
        setFocus()
    }

    fun openFile(path: File?, writable: Boolean = this.writable): Boolean {
        this.writable = writable
        removeVerifyListener(noWriteListener)
        val fileContents: String = if (path != null) {
            try {
                Util.readFile(path)
            } catch (e1: IOException) {
                Util.logger.severe("Could not open file $path")
                tabFolder.close(this)
                return false
            }
        } else {
            ""
        }
        file = path
        undoRedo.skipNext()
        text = fileContents
        edited = false
        tabFolder.setText(this, fileName)
        tabFolder.setSelection(this)
        if (!writable)
            addVerifyListener(noWriteListener)
        return true
    }

    fun save(): Boolean {
        if (!writable)
            return false

        if (file == null) {
            val dialog = FileDialog(shell, SWT.SAVE)
            dialog.filterExtensions = arrayOf("*.luc", "*.v", "*")
            dialog.text = "Save File"
            val path = dialog.open() ?: return false
            file = File(path)
            tabFolder.setText(this, fileName)
            edited = false
        }
        file?.let {
            monitorJob?.cancel()
            try {
                val out = PrintWriter(it)
                out.print(text)
                out.close()
            } catch (e1: FileNotFoundException) {
                return false
            } finally {
                monitorScope.startFileMonitor()
            }
        }
        if (edited) {
            tabFolder.setText(this, fileName)
        }
        edited = false
        MainWindow.updateErrors()
        return true
    }

    override fun modifyText(e: ModifyEvent) {
        if (skipEdit) {
            skipEdit = false
            return
        }
        if (e.widget === this) {
            if (!edited) {
                tabFolder.setText(this, "*$fileName")
            }
            edited = true
            searchResults = null
            updateSearchResults()

            // work around for selectAll -> delete bug
            if (text.isEmpty()) redraw()
        } else { // modify from search
            searchResults = null
            updateSearchResults()
        }
    }

    private fun updateSearchResults() {
        if (!searchActive) return
        val pattern = search.getSearchText()
        if (pattern.isEmpty()) {
            highlighter.setMatches(null)
            return
        }
        if (searchResults == null) {
            var error = false
            try {
                searchResults = Search(text, pattern, search.isRegex, search.isCaseSensitive)
                GlobalScope.launch(Dispatchers.Default) {
                    searchResults?.let {
                        highlighter.setMatches(it.deferredMatches.await())
                    }
                }
            } catch (e: Exception) {
                error = true
            }
            search.setSearchError(error)
        }
    }

    private fun replace(all: Boolean) {
        updateSearchResults()

        searchResults?.let {
            val replaceText = search.getReplaceText()
            val editorText = text
            val caretOffset = caretOffset
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    var replacement = replaceText
                    if (!all) {
                        val result = it.lastResult ?: it.next(caretOffset)
                        if (result != null) {
                            launch(Dispatchers.SWT) {
                                val m = it.pattern.matcher(getTextRange(result.start(), result.end() - result.start()))
                                if (m.find()) {
                                    replacement = m.replaceFirst(replacement)
                                    search.setReplaceError(false)
                                    autoComplete?.skipNext()
                                    replaceTextRange(result.start(), result.end() - result.start(), replacement)
                                    setCaretOffset(result.end())
                                    search(true)
                                }
                            }
                        }
                    } else {
                        val m = it.pattern.matcher(editorText)
                        if (m.find()) {
                            val newText = m.replaceAll(replacement)
                            launch(Dispatchers.SWT) {
                                search.setReplaceError(false)
                                autoComplete?.skipNext()
                                replaceTextRange(0, charCount, newText)
                            }
                        }
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.SWT) { search.setReplaceError(true) }
                    Util.println(e.message, true)
                }
            }
        }

    }

    private fun search(forward: Boolean) {
        updateSearchResults()

        searchResults?.let {
            val startIdx = caretOffset
            GlobalScope.launch(Dispatchers.Default) {
                val match = if (forward) it.next(startIdx) else it.previous(startIdx - 1)
                if (match != null) {
                    launch(Dispatchers.SWT) {
                        setSelection(match.start(), match.end())
                        placeSearch()
                    }
                }
            }
        }
    }

    override fun close() {
        tabFolder.close(this)
        val p = MainWindow.project
        if (p != null && projectSaveListener != null) p.saveListeners.remove(projectSaveListener)
    }

    fun updateTextColor() {
        hasErrors = false
        when {
            errorChecker?.hasErrors() == true -> {
                hasErrors = true
                tabFolder.setTabTextColor(this, Theme.tabErrorTextColor)
            }
            errorChecker?.hasWarnings() == true -> {
                tabFolder.setTabTextColor(this, Theme.tabWarningTextColor)
            }
            else -> {
                tabFolder.setTabTextColor(this, Theme.tabNormalTextColor)
            }
        }
    }

    fun undo() {
        undoRedo.undo()
    }

    fun redo() {
        undoRedo.redo()
    }

    fun updateErrors() {
        errorChecker?.updateErrors()
    }

    fun getLineColor(line: Int): Color? {
        return errorChecker?.getLineColor(line)
    }

    private fun setupMenu() {
        menu = rightClickMenu
        val undo = MenuItem(rightClickMenu, SWT.NONE)
        undo.text = "&Undo\tCtrl+Z"
        undo.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                undo()
            }
        })
        val redo = MenuItem(rightClickMenu, SWT.NONE)
        redo.text = "&Redo\tCtrl+Shift+Z"
        redo.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                redo()
            }
        })
        MenuItem(rightClickMenu, SWT.SEPARATOR)
        var item = MenuItem(rightClickMenu, SWT.NONE)
        item.text = "Cut\tCtrl+X"
        item.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                cut()
            }
        })
        item = MenuItem(rightClickMenu, SWT.NONE)
        item.text = "&Copy\tCtrl+C"
        item.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                copy()
            }
        })
        val paste = MenuItem(rightClickMenu, SWT.NONE)
        paste.text = "&Paste\tCtrl+V"
        paste.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                paste()
            }
        })
        MenuItem(rightClickMenu, SWT.SEPARATOR)
        item = MenuItem(rightClickMenu, SWT.NONE)
        item.text = "&Save\tCtrl+S"
        item.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                save()
            }
        })
        MenuItem(rightClickMenu, SWT.SEPARATOR)
        item = MenuItem(rightClickMenu, SWT.NONE)
        item.text = "Split &Horizontal\tCtrl+H"
        item.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                tabFolder.split(false)
            }
        })
        item = MenuItem(rightClickMenu, SWT.NONE)
        item.text = "Split &Vertical\tCtrl+G"
        item.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                tabFolder.split(true)
            }
        })
        MenuItem(rightClickMenu, SWT.SEPARATOR)
        item = MenuItem(rightClickMenu, SWT.NONE)
        item.text = "&Search\tCtrl+F"
        item.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                setSearch(true)
            }
        })
        val format = MenuItem(rightClickMenu, SWT.NONE)
        format.text = "&Auto-format Code\tCtrl+Shift+F"
        format.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                formatText()
            }
        })
        rightClickMenu.addMenuListener(object : MenuAdapter() {
            override fun menuShown(e: MenuEvent) {
                redo.isEnabled = undoRedo.canRedo()
                undo.isEnabled = undoRedo.canUndo()
                format.isEnabled = formatter != null
            }
        })
    }

    override fun print() {
        val optionsDialog = PrintOptionsDialog(shell, SWT.DIALOG_TRIM)
        val printOptions = optionsDialog.open() ?: return
        printOptions.jobName = fileName
        if (printOptions.header != null) printOptions.header = "File: $fileName"
        val dialog = PrintDialog(shell, SWT.NULL)
        val data = dialog.open() ?: return
        val printer = Printer(data)
        val printJob = print(printer, printOptions)
        skipEdit = true
        undoRedo.skipNext()
        autoComplete?.skipNext()
        // for some reason this is needed as color data is disposed after printing
        notifyListeners(SWT.Modify, Event())
        val thread = Thread {
            Util.clearConsole()
            Util.println("Printing $fileName...")
            printJob.run()
            Util.println("Complete.", Theme.successTextColor)
        }
        thread.start()
    }


}