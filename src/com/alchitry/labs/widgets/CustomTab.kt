package com.alchitry.labs.widgets

import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.main.MainWindow.getIndex
import com.alchitry.labs.gui.util.Images
import org.eclipse.swt.SWT
import org.eclipse.swt.dnd.*
import org.eclipse.swt.events.*
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event

class CustomTab(private val folder: CustomTabs, style: Int, private var index: Int) : Canvas(folder, style) {
    private enum class MouseState {
        NONE, HOVER_TAB, CLICK_TAB, HOVER_X, CLICK_X
    }

    private var mouse = MouseState.NONE
    private var hit = false
    var text: String? = null
    private var hoverColor: Color? = null
    private var clickColor: Color? = null

    init {
        if (Theme.set) {
            background = Theme.windowBackgroundColor
            foreground = Theme.windowForegroundColor
            hoverColor = Theme.mainAccentColor
            clickColor = Theme.darkAccentColor
        }
        addPaintListener { e -> paintControl(e) }
        addMouseMoveListener { e -> mouseMove(e) }
        addMouseTrackListener(object : MouseTrackAdapter() {
            override fun mouseEnter(e: MouseEvent) {
                this@CustomTab.mouseEnter(e)
            }

            override fun mouseExit(e: MouseEvent) {
                this@CustomTab.mouseExit()
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseDown(e: MouseEvent) {
                this@CustomTab.mouseDown()
            }

            override fun mouseUp(e: MouseEvent) {
                this@CustomTab.mouseUp(e)
            }
        })
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                this@CustomTab.keyPressed(e)
            }
        })
        val dnd = DragSource(this, DND.DROP_MOVE or DND.DROP_COPY)
        val types = arrayOf<Transfer>(TabTransfer.getInstance())
        dnd.setTransfer(*types)
        dnd.addDragListener(object : DragSourceListener {
            var gc: GC? = null
            var image: Image? = null
            override fun dragStart(event: DragSourceEvent) {
                // getting control widget - Composite in this case
                val composite = (event.source as DragSource).control as Composite
                // Getting dimensions of this widget
                val compositeSize = composite.size
                // creating new GC
                gc = GC(composite)
                // Creating new Image
                image = Image(Display.getCurrent(), compositeSize.x, compositeSize.y)
                // Rendering widget to image
                gc!!.copyArea(image, 0, 0)
                // Setting widget to DnD image
                event.image = image
            }

            override fun dragSetData(event: DragSourceEvent) {
                if (TabTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = getIndex(folder.getTabChild(index)!!)
                }
            }

            override fun dragFinished(event: DragSourceEvent) {
                if (gc != null) {
                    gc!!.dispose()
                    gc = null
                }
                if (image != null) {
                    image!!.dispose()
                    image = null
                }
            }
        })
    }

    fun setIndex(index: Int) {
        this.index = index
    }

    private fun paintControl(e: PaintEvent) {
        paint(e.gc)
    }

    private fun paint(gc: GC) {
        gc.font = Theme.defaultFont
        val ts = gc.stringExtent(text)
        val iconHeight = (size.y - 13) / 2
        if (Theme.set) gc.foreground = Theme.tabNormalTextColor
        when (mouse) {
            MouseState.NONE -> {
                gc.foreground = foreground
                if (Theme.set) gc.background = background
                gc.fillRectangle(clientArea)
            }
            MouseState.HOVER_TAB -> {
                if (Theme.set) {
                    gc.background = hoverColor
                    gc.foreground = Theme.tabHoverTextColor
                }
                gc.fillRectangle(clientArea)
                gc.drawImage(Images.xIcon, ts.x + 12, iconHeight)
            }
            MouseState.CLICK_TAB -> {
                if (Theme.set) {
                    gc.background = clickColor
                    gc.foreground = Theme.tabHoverTextColor
                }
                gc.fillRectangle(clientArea)
                gc.drawImage(Images.xIcon, ts.x + 12, iconHeight)
            }
            MouseState.HOVER_X -> {
                if (Theme.set) {
                    gc.background = hoverColor
                    gc.foreground = Theme.tabHoverTextColor
                }
                gc.fillRectangle(clientArea)
                gc.drawImage(Images.xGreyIcon, ts.x + 12, iconHeight)
            }
            MouseState.CLICK_X -> {
                if (Theme.set) {
                    gc.background = clickColor
                    gc.foreground = Theme.tabHoverTextColor
                }
                gc.fillRectangle(clientArea)
                gc.drawImage(Images.xRedIcon, ts.x + 12, iconHeight)
            }
        }
        if (text != null) {
            val textSize = gc.stringExtent(text)
            gc.drawText(text, 6, (25 - textSize.y) / 2)
        }
    }

    private fun mouseMove(e: MouseEvent) {
        val bounds = bounds
        mouse = if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            MouseState.NONE
        } else if (e.x < bounds.width - (9 + 13)) {
            if (hit) MouseState.CLICK_TAB else MouseState.HOVER_TAB
        } else {
            if (hit) MouseState.CLICK_X else MouseState.HOVER_X
        }
        redraw()
    }

    private fun mouseEnter(e: MouseEvent) {
        val bounds = bounds
        mouse = if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            MouseState.NONE
        } else if (e.x < bounds.width - (9 + 13)) {
            MouseState.HOVER_TAB
        } else {
            MouseState.HOVER_X
        }
        redraw()
    }

    private fun mouseExit() {
        mouse = MouseState.NONE
        redraw()
    }

    private fun mouseDown() {
        hit = true
        if (mouse == MouseState.HOVER_TAB) mouse = MouseState.CLICK_TAB else if (mouse == MouseState.HOVER_X) mouse = MouseState.CLICK_X
        redraw()
    }

    private fun mouseUp(e: MouseEvent) {
        hit = false
        if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
            mouse = MouseState.NONE
        } else if (mouse == MouseState.CLICK_TAB) mouse = MouseState.HOVER_TAB else if (mouse == MouseState.CLICK_X) mouse = MouseState.HOVER_X
        redraw()
        if (mouse == MouseState.HOVER_TAB) notifyListeners(SWT.Selection, Event()) else if (mouse == MouseState.HOVER_X) notifyListeners(SWT.Close, Event())
    }

    private fun keyPressed(e: KeyEvent) {
        if (e.keyCode == '\r'.toInt() || e.character == ' ') {
            val event = Event()
            notifyListeners(SWT.Selection, event)
        }
    }

    override fun computeSize(wHint: Int, hHint: Int, changed: Boolean): Point {
        if (text != null) {
            val gc = GC(this)
            gc.font = Theme.defaultFont
            val textSize = gc.stringExtent(text)
            gc.dispose()
            return Point(textSize.x + 18 + 13, 25)
        }
        return Point(25, 25)
    }
}