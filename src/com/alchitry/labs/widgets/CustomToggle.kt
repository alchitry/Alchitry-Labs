package com.alchitry.labs.widgets

import com.alchitry.labs.gui.Theme
import org.eclipse.swt.SWT
import org.eclipse.swt.events.*
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Event

class CustomToggle(parent: Composite?, style: Int) : Canvas(parent, style) {
    private var mouse = 0
    private var hit = false
    private var icon: Image? = null
    private var iconHover: Image? = null
    var checked = false
        set(value) {
            field = value
            redraw()
        }

    fun setIcon(icon: Image?) {
        this.icon = icon
    }

    fun setIconHover(icon: Image?) {
        iconHover = icon
    }

    private fun paintControl(e: PaintEvent) {
        val x = ((e.width - 25) / 2)
        val y = ((e.height - 25) / 2)
        when (mouse) {
            0 -> {
                if (Theme.set) if (checked) e.gc.background = Theme.toolBarHoverColor else e.gc.background = background
                e.gc.fillRectangle(clientArea)
                if (icon != null) e.gc.drawImage(icon, x, y)
            }
            1 -> {
                if (Theme.set) e.gc.background = Theme.toolBarHoverColor
                e.gc.fillRectangle(clientArea)
                if (iconHover != null) e.gc.drawImage(iconHover, x, y)
            }
            2 -> {
                if (Theme.set) e.gc.background = Theme.toolBarClickColor
                e.gc.fillRectangle(clientArea)
                if (iconHover != null) e.gc.drawImage(iconHover, x, y)
            }
        }
    }

    private fun mouseMove(e: MouseEvent) {
        if (!hit) return
        mouse = 2
        if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            mouse = 0
        }
        redraw()
    }

    private fun mouseEnter(e: MouseEvent) {
        mouse = 1
        redraw()
    }

    private fun mouseExit(e: MouseEvent) {
        mouse = 0
        redraw()
    }

    private fun mouseDown(e: MouseEvent) {
        hit = true
        mouse = 2
        redraw()
    }

    private fun mouseUp(e: MouseEvent) {
        hit = false
        mouse = 1
        if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            mouse = 0
        }
        redraw()
        if (mouse == 1) {
            checked = !checked
            val ev = Event()
            ev.button = if (e.stateMask and SWT.SHIFT != 0) 2 else 1
            notifyListeners(SWT.Selection, ev)
        }
    }

    private fun keyPressed(e: KeyEvent) {
        if (e.keyCode == '\r'.toInt() || e.character == ' ') {
            checked = !checked
            val event = Event()
            notifyListeners(SWT.Selection, event)
        }
    }

    override fun computeSize(wHint: Int, hHint: Int, changed: Boolean): Point {
        return Point(25, 25)
    }

    init {
        if (Theme.set) {
            background = Theme.windowBackgroundColor
            foreground = Theme.windowForegroundColor
        }
        addPaintListener { e -> paintControl(e) }
        addMouseMoveListener { e -> mouseMove(e) }
        addMouseTrackListener(object : MouseTrackAdapter() {
            override fun mouseEnter(e: MouseEvent) {
                this@CustomToggle.mouseEnter(e)
            }

            override fun mouseExit(e: MouseEvent) {
                this@CustomToggle.mouseExit(e)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseDown(e: MouseEvent) {
                this@CustomToggle.mouseDown(e)
            }

            override fun mouseUp(e: MouseEvent) {
                this@CustomToggle.mouseUp(e)
            }
        })
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                this@CustomToggle.keyPressed(e)
            }
        })
    }
}