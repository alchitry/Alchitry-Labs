package com.alchitry.labs.widgets

import com.alchitry.labs.gui.Theme
import org.eclipse.swt.SWT
import org.eclipse.swt.events.*
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Event

class CustomTable(parent: Composite?, style: Int) : Canvas(parent, style) {
    companion object {
        private const val PADDING_X = 8
        private const val PADDING_Y = 4
        private const val NO_MOUSE = 0
        private const val MOUSE_HOVER = 1
        private const val MOUSE_CLICK = 2
    }

    private var mouse = NO_MOUSE
    private var hit = false
    private var items = listOf<String>()
    private var highlightColor: Color? = null
    private var selection: Int
    private var rowHeight = 0

    init {
        addPaintListener { e -> paintControl(e!!) }
        addMouseMoveListener { e -> mouseMove(e!!) }
        addMouseTrackListener(object : MouseTrackAdapter() {
            override fun mouseEnter(e: MouseEvent?) {
                this@CustomTable.mouseEnter(e!!)
            }

            override fun mouseExit(e: MouseEvent?) {
                this@CustomTable.mouseExit(e!!)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseDown(e: MouseEvent?) {
                this@CustomTable.mouseDown(e!!)
            }

            override fun mouseUp(e: MouseEvent?) {
                this@CustomTable.mouseUp(e!!)
            }
        })
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                this@CustomTable.keyPressed(e!!)
            }
        })
        if (Theme.set) highlightColor = Theme.autocompleteHighlightColor
        selection = -1
    }

    fun resetSelection() {
        selection = -1
        redraw()
    }

    fun getItemCount(): Int {
        return items.size
    }

    fun setSelection(s: Int) {
        selection = s
        redraw()
    }

    fun incrementSelection() {
        selection++
        selection %= items.size
        redraw()
    }

    fun decrementSelection() {
        selection--
        if (selection < 0) selection = items.size - 1
        redraw()
    }

    fun getSelectionIndex(): Int {
        return selection
    }

    fun setItems(items: List<String>) {
        this.items = items
        redraw()
    }

    fun getItems(): List<String> {
        return items
    }

    fun getItem(i: Int): String {
        return items[i]
    }

    fun getSelectedItem(): String? {
        return if (selection == -1) null else items[selection]
    }

    private fun paintControl(e: PaintEvent) {
        paint(e.gc)
    }

    private fun paint(gc: GC) {
        gc.foreground = foreground
        gc.background = background
        var yOffset = 0
        val bounds = clientArea
        gc.fillRectangle(bounds)
        for (i in items.indices) {
            val line = items[i]
            val se = gc.stringExtent(line)
            if (i == selection) {
                gc.background = highlightColor
                gc.fillRectangle(0, yOffset, bounds.width, se.y + 2 * PADDING_Y)
            } else {
                gc.background = background
            }
            gc.drawText(line, PADDING_X, yOffset + PADDING_Y)
            yOffset += se.y + PADDING_Y * 2
        }
    }

    private fun mouseMove(e: MouseEvent) {
        val bounds = bounds
        if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            mouse = NO_MOUSE
        } else {
            selection = e.y / rowHeight
            mouse = if (hit) MOUSE_CLICK else MOUSE_HOVER
        }
        redraw()
    }

    private fun mouseEnter(e: MouseEvent) {
        val bounds = bounds
        if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            mouse = NO_MOUSE
        } else {
            selection = e.y / rowHeight
            mouse = MOUSE_HOVER
        }
        redraw()
    }

    private fun mouseExit(e: MouseEvent) {
        mouse = NO_MOUSE
        redraw()
    }

    private fun mouseDown(e: MouseEvent) {
        hit = true
        if (mouse == MOUSE_HOVER) mouse = MOUSE_CLICK
        redraw()
    }

    private fun mouseUp(e: MouseEvent) {
        hit = false
        if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            mouse = NO_MOUSE
        } else if (mouse == MOUSE_CLICK) {
            selection = e.y / rowHeight
            mouse = MOUSE_HOVER
        }
        redraw()
        if (mouse == MOUSE_HOVER) notifyListeners(SWT.Selection, Event())
    }

    fun keyPressed(keyCode: Int): Boolean {
        when (keyCode) {
            SWT.ARROW_DOWN -> {
                incrementSelection()
                return true
            }
            SWT.ARROW_UP -> {
                decrementSelection()
                return true
            }
            SWT.CR.toInt(), SWT.KEYPAD_CR -> {
                val event = Event()
                notifyListeners(SWT.Selection, event)
                return true
            }
        }
        return false
    }

    fun keyPressed(e: KeyEvent) {
        keyPressed(e.keyCode)
    }

    override fun computeSize(wHint: Int, hHint: Int, changed: Boolean): Point {
        if (items.size > 0) {
            val gc = GC(this)
            var width = 0
            var height = 0
            for (s in items) {
                val textSize = gc.stringExtent(s)
                width = Math.max(width, textSize.x)
                height += textSize.y + PADDING_Y * 2
            }
            rowHeight = height / items.size
            return Point(width + 2 * PADDING_X, height)
        }
        return Point(25, 25)
    }

    fun setHighlightColor(c: Color?) {
        highlightColor = c
    }


}