package com.alchitry.labs.widgets

import com.alchitry.labs.Settings
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.util.Images
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.events.KeyListener
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Listener
import kotlin.math.roundToInt

class CustomSearchAndReplace(parent: Composite, style: Int) : Composite(parent, style) {
    companion object {
        val HEIGHT = (70 * Settings.FONT_SCALE).roundToInt().coerceAtLeast(70)
    }
    private var width = 300
    private val searchText = StyledText(this, SWT.SINGLE)
    private val replaceText = StyledText(this, SWT.SINGLE)
    private val nextBtn = CustomButton(this, SWT.NONE)
    private val prevBtn = CustomButton(this, SWT.NONE)
    private val replaceOneBtn = CustomButton(this, SWT.NONE)
    private val replaceAllBtn = CustomButton(this, SWT.NONE)
    private val regexToggle = CustomToggle(this, SWT.NONE)
    private val caseSensitiveToggle = CustomToggle(this, SWT.NONE)
    private val searchLabel = Label(this, SWT.NONE)
    private val replaceLabel = Label(this, SWT.NONE)

    init {
        background = Theme.windowBackgroundColor
        foreground = Theme.editorForegroundColor

        nextBtn.setIcon(Images.arrowDown)
        nextBtn.setIconHover(Images.arrowDownHover)
        nextBtn.toolTipText = "Next"

        prevBtn.setIcon(Images.arrowUp)
        prevBtn.setIconHover(Images.arrowUpHover)
        prevBtn.toolTipText = "Previous"

        replaceOneBtn.setIcon(Images.replaceOneIcon)
        replaceOneBtn.setIconHover(Images.replaceOneHover)
        replaceOneBtn.toolTipText = "Replace once"

        replaceAllBtn.setIcon(Images.replaceAllIcon)
        replaceAllBtn.setIconHover(Images.replaceAllHover)
        replaceAllBtn.toolTipText = "Replace all"

        regexToggle.setIcon(Images.regexIcon)
        regexToggle.setIconHover(Images.regexHover)
        regexToggle.toolTipText = "Regular expression"

        caseSensitiveToggle.setIcon(Images.caseSensitiveIcon)
        caseSensitiveToggle.setIconHover(Images.caseSensitiveHover)
        caseSensitiveToggle.toolTipText = "Case sensitive"

        regexToggle.addListener(SWT.Selection) {
            if (regexToggle.checked) caseSensitiveToggle.checked = true
        }

        caseSensitiveToggle.addListener(SWT.Selection) {
            if (!caseSensitiveToggle.checked) regexToggle.checked = false
        }

        font = Theme.defaultFont

        listOf(searchText, replaceText).forEach {
            it.background = Theme.searchBackgroundColor
            it.foreground = Theme.searchForegroundColor
            it.selectionBackground = Theme.editorTextSelectedColor
            it.font = font
            it.addKeyListener(
                    object : KeyAdapter() {
                        override fun keyPressed(e: KeyEvent) {
                            if (e.stateMask == SWT.CTRL && e.keyCode == 'a'.toInt()) {
                                (e.widget as StyledText).selectAll()
                                e.doit = false
                            }
                        }
                    }
            )
        }

        searchLabel.font = font
        searchLabel.text = "Search:"
        searchLabel.background = background
        searchLabel.foreground = foreground
        replaceLabel.font = font
        replaceLabel.text = "Replace:"
        replaceLabel.background = background
        replaceLabel.foreground = foreground

        addDisposeListener {
            searchText.dispose()
            replaceText.dispose()
            nextBtn.dispose()
            prevBtn.dispose()
        }

        parent.addListener(SWT.Resize) { calcLayout() }

        calcLayout()
    }

    private fun calcLayout() {
        if (isDisposed) return

        width = parent.size.x
        setSize(width, HEIGHT)

        val p = nextBtn.computeSize(SWT.DEFAULT, SWT.DEFAULT)
        val rowHeight = (HEIGHT / 2 - 10).coerceAtLeast(p.y)
        val padding = (HEIGHT - rowHeight * 2) / 3
        val buttonWidth = rowHeight.coerceAtLeast(p.x)

        nextBtn.setBounds(width - buttonWidth - padding, padding, buttonWidth, rowHeight)
        prevBtn.setBounds(width - (padding * 2) - (buttonWidth * 2), padding, buttonWidth, rowHeight)
        replaceOneBtn.setBounds(width - (padding * 2) - (buttonWidth * 2), padding * 2 + rowHeight, buttonWidth, rowHeight)
        replaceAllBtn.setBounds(width - buttonWidth - padding, padding * 2 + rowHeight, buttonWidth, rowHeight)

        caseSensitiveToggle.setBounds(padding, padding, buttonWidth, rowHeight)
        regexToggle.setBounds(padding, padding * 2 + rowHeight, buttonWidth, rowHeight)

        val replaceSize = replaceLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT)
        val searchSize = searchLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT)
        val leftMargin = replaceSize.x.coerceAtLeast(searchSize.x) + (3 * padding) + buttonWidth
        val margin = (rowHeight - replaceSize.y) / 2

        searchLabel.setBounds(leftMargin - padding - searchSize.x, padding + margin, searchSize.x, rowHeight)
        replaceLabel.setBounds(leftMargin - padding - replaceSize.x, (padding * 2) + rowHeight + margin, replaceSize.x, rowHeight)

        val buttonMargin = padding * 3 + buttonWidth * 2
        val textBoxWidth = width - buttonMargin - leftMargin

        searchText.topMargin = margin
        searchText.bottomMargin = margin
        searchText.leftMargin = margin
        searchText.rightMargin = margin
        searchText.setBounds(leftMargin, padding, textBoxWidth, rowHeight)
        replaceText.topMargin = margin
        replaceText.bottomMargin = margin
        replaceText.leftMargin = margin
        replaceText.rightMargin = margin
        replaceText.setBounds(leftMargin, padding * 2 + rowHeight, textBoxWidth, rowHeight)
    }

    override fun computeSize(wHint: Int, hHint: Int): Point {
        width = parent.size.x
        return Point(width, HEIGHT)
    }

    fun addModifyListener(listener: ModifyListener?) {
        searchText.addModifyListener(listener)
    }

    fun setSearchText(text: String?) {
        searchText.text = text
    }

    fun getSearchText(): String {
        return searchText.text
    }

    fun getReplaceText(): String {
        return replaceText.text
    }

    val isRegex: Boolean
        get() = regexToggle.checked
    val isCaseSensitive: Boolean
        get() = caseSensitiveToggle.checked

    override fun addKeyListener(listener: KeyListener) {
        super.addKeyListener(listener)
        searchText.addKeyListener(listener)
        replaceText.addKeyListener(listener)
        nextBtn.addKeyListener(listener)
        prevBtn.addKeyListener(listener)
        replaceOneBtn.addKeyListener(listener)
        replaceAllBtn.addKeyListener(listener)
        regexToggle.addKeyListener(listener)
        caseSensitiveToggle.addKeyListener(listener)
    }

    fun addNextListener(listener: Listener?) {
        nextBtn.addListener(SWT.Selection, listener)
    }

    fun addPrevListener(listener: Listener?) {
        prevBtn.addListener(SWT.Selection, listener)
    }

    fun addReplaceOnceListener(listener: Listener?) {
        replaceOneBtn.addListener(SWT.Selection, listener)
    }

    fun addReplaceAllListener(listener: Listener?) {
        replaceAllBtn.addListener(SWT.Selection, listener)
    }

    fun addCaseSensitiveListener(listener: Listener?) {
        caseSensitiveToggle.addListener(SWT.Selection, listener)
    }

    fun addRegexListener(listener: Listener?) {
        regexToggle.addListener(SWT.Selection, listener)
    }

    fun setSearchError(error: Boolean) {
        if (error) {
            searchText.foreground = Theme.errorTextColor
        } else {
            searchText.foreground = Theme.searchForegroundColor
        }
    }

    fun setReplaceError(error: Boolean) {
        if (error) {
            replaceText.foreground = Theme.errorTextColor
        } else {
            replaceText.foreground = Theme.searchForegroundColor
        }
    }

    override fun setFocus(): Boolean {
        return if (searchText.isDisposed) false else searchText.setFocus()
    }




}