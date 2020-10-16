package com.alchitry.labs.widgets

import com.alchitry.labs.Settings
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.util.Images
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.KeyListener
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Listener
import kotlin.math.roundToInt

class CustomSearch(parent: Composite?, style: Int) : Composite(parent, style) {
    companion object {
        private val HEIGHT = (35 * Settings.FONT_SCALE).roundToInt().coerceAtLeast(35)
        private val WIDTH = (300 * Settings.FONT_SCALE).roundToInt().coerceAtLeast(300)
    }

    private val text: StyledText
    private var nextBtn: CustomButton
    var prevBtn: CustomButton

    init {
        setSize(WIDTH, HEIGHT)
        font = Theme.defaultFont
        background = Theme.windowBackgroundColor
        foreground = Theme.editorForegroundColor
        nextBtn = CustomButton(this, SWT.NONE)
        nextBtn.setIcon(Images.arrowDown)
        nextBtn.setIconHover(Images.arrowDownHover)
        nextBtn.toolTipText = "Next"

        val padding = (5 * Settings.FONT_SCALE).roundToInt().coerceAtLeast(5)
        val rowHeight = HEIGHT - padding * 2

        nextBtn.setBounds(WIDTH - rowHeight - padding, padding, rowHeight, rowHeight)
        prevBtn = CustomButton(this, SWT.NONE)
        prevBtn.setIcon(Images.arrowUp)
        prevBtn.setIconHover(Images.arrowUpHover)
        prevBtn.toolTipText = "Previous"
        prevBtn.setBounds(WIDTH - padding * 2 - rowHeight * 2, padding, rowHeight, rowHeight)

        text = StyledText(this, SWT.SINGLE)
        text.font = font
        text.background = Theme.searchBackgroundColor
        text.foreground = Theme.searchForegroundColor

        text.setBounds(padding, padding, WIDTH - padding * 4 - rowHeight * 2, rowHeight)


        val gc = GC(this)
        gc.font = font
        val size = gc.stringExtent("T")
        gc.dispose()
        val margin = (rowHeight - size.y) / 2
        text.topMargin = margin
        text.bottomMargin = margin
        text.leftMargin = margin
        text.rightMargin = margin
        addDisposeListener {
            text.dispose()
            nextBtn.dispose()
            prevBtn.dispose()
        }
    }

    override fun computeSize(wHint: Int, hHint: Int): Point {
        return Point(WIDTH, HEIGHT)
    }

    fun addModifyListener(listener: ModifyListener?) {
        text.addModifyListener(listener)
    }

    fun setText(text: String?) {
        this.text.text = text
    }

    fun getText(): String {
        return text.text
    }

    override fun addKeyListener(listener: KeyListener) {
        super.addKeyListener(listener)
        text.addKeyListener(listener)
        nextBtn.addKeyListener(listener)
        prevBtn.addKeyListener(listener)
    }

    fun addNextListener(listener: Listener?) {
        nextBtn.addListener(SWT.Selection, listener)
    }

    fun addPrevListener(listener: Listener?) {
        prevBtn.addListener(SWT.Selection, listener)
    }

    override fun setFocus(): Boolean {
        return if (text.isDisposed) false else text.setFocus()
    }
}