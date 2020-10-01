package com.alchitry.labs.widgets

import com.alchitry.labs.Util.showError
import com.alchitry.labs.gui.StyledCodeEditor
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.main.MainWindow
import com.alchitry.labs.gui.util.Images
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.dnd.*
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.events.ControlEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.widgets.*
import java.util.*
import kotlin.math.abs

class CustomTabs(parent: Composite, style: Int) : Composite(parent, style), Listener {
    private val tabs = ArrayList<CustomTab>()
    private val hiddenTabs = ArrayList<CustomTab>()
    val tabChildren = ArrayList<TabChild>()
    private val canvas: Canvas
    var opened = false
    private val overflowMenu: Menu
    private val overflowButton: CustomButton
    private var selected: Int

    init {
        selected = -1
        background = Theme.windowBackgroundColor
        addDisposeListener { tabs.forEach { it.dispose() } }
        addControlListener(object : ControlAdapter() {
            override fun controlResized(e: ControlEvent) {
                resize()
            }
        })
        overflowButton = CustomButton(this, SWT.POP_UP)
        overflowMenu = Menu(this)
        overflowButton.isVisible = false
        overflowMenu.isVisible = false
        overflowButton.background = Theme.tabBackgroundColor
        overflowButton.setIcon(Images.plusIcon)
        overflowButton.setIconHover(Images.plusIconHover)
        overflowButton.addListener(SWT.Selection) {
            val p = toDisplay(overflowButton.location)
            p.y += 25
            overflowMenu.setLocation(p)
            overflowMenu.isVisible = !overflowMenu.visible
            for (i in overflowMenu.items) i.dispose()
            for (t in hiddenTabs) {
                val mi = MenuItem(overflowMenu, SWT.PUSH)
                mi.text = t.text
                mi.data = t
                mi.addListener(SWT.Selection, overflowSelectionListener)
            }
        }
        canvas = Canvas(this, SWT.NONE)
        val dnd = DropTarget(this, DND.DROP_MOVE or DND.DROP_COPY or DND.DROP_DEFAULT)
        val tabTransfer = TabTransfer.getInstance()
        val types = arrayOf<Transfer>(tabTransfer)
        dnd.setTransfer(*types)
        dnd.addDropListener(object : DropTargetListener {
            override fun dropAccept(event: DropTargetEvent) {}
            override fun drop(event: DropTargetEvent) {
                // run async because windows in an unhappy child if you dispose
                // of the tab in the event handler >:(
                display.asyncExec {
                    if (tabTransfer.isSupportedType(event.currentDataType)) {
                        val idx = event.data as Int
                        val tab = MainWindow.getTabChild(idx)
                        val tabIndex = tabChildren.indexOf(tab)
                        if (tabIndex >= 0) {
                            moveTab(tabIndex, event.x, event.y)
                        } else {
                            tab!!.switchFolder(this@CustomTabs)
                        }
                        if (!opened && !tabChildren[0].isModified) close(0)
                        opened = true
                        canvas.setBounds(0, 0, 0, 0)
                    }
                }
            }

            override fun dragOver(event: DropTargetEvent) {
                event.feedback = DND.FEEDBACK_NONE
                val p = this@CustomTabs.toControl(event.x, event.y)
                if (tabs.size > 0 && p.y > 0 && p.y < 50 && p.x < size.x) {
                    event.detail = DND.DROP_MOVE
                    val idx = getTab(p)
                    val bounds: Rectangle
                    if (idx < tabs.size) {
                        val t = tabs[idx]
                        bounds = t.bounds
                        bounds.width = 3
                        if (idx > 0) bounds.x -= 2
                    } else {
                        val t = tabs[tabs.size - 1]
                        bounds = t.bounds
                        bounds.x += bounds.width - 1
                        bounds.width = 3
                    }
                    canvas.bounds = bounds
                    val gc = GC(canvas)
                    gc.foreground = Theme.editorForegroundColor
                    gc.drawRectangle(0, 0, 3, bounds.height)
                    gc.dispose()
                } else {
                    event.detail = DND.DROP_NONE
                    canvas.setBounds(0, 0, 0, 0)
                }
            }

            override fun dragOperationChanged(event: DropTargetEvent) {}
            override fun dragLeave(event: DropTargetEvent) {
                event.detail = DND.DROP_NONE
                canvas.setBounds(0, 0, 0, 0)
            }

            override fun dragEnter(event: DropTargetEvent) {
                // will accept text but prefer to have files dropped
                for (i in event.dataTypes.indices) {
                    if (tabTransfer.isSupportedType(event.dataTypes[i])) {
                        event.currentDataType = event.dataTypes[i]
                        break
                    }
                }
            }
        })
        val focusListener = Listener { event -> checkFocus(event) }
        display.addFilter(SWT.FocusIn, focusListener)
        addDisposeListener {
            display.removeFilter(SWT.FocusIn, focusListener)
            for (c in tabChildren) c.dispose()
            for (t in tabs) t.dispose()
        }
        addTraverseListener { e -> e.doit = false }
    }

    private val overflowSelectionListener = Listener { event ->
        for (i in tabs.indices) {
            val tab = tabs[i]
            if (tab.text == (event.widget as MenuItem).text) {
                setSelection(i)
                moveTab(i, 0)
                tab.background = Theme.tabSelectedBackgroundColor
            } else {
                tab.background = Theme.tabBackgroundColor
            }
        }
    }

    private fun checkFocus(event: Event) {
        if (event.widget !is Control) {
            return
        }
        var isOurChild = false
        var c: Control? = event.widget as Control
        while (c != null) {
            if (c === this) {
                isOurChild = true
                break
            }
            c = c.parent
        }
        if (isOurChild)
            MainWindow.tabFolder = this
    }

    fun split(vertical: Boolean) {
        val parent = parent as SashForm
        val weights = parent.weights
        val sash = SashForm(parent, if (vertical) SWT.VERTICAL else SWT.NONE)
        sash.moveAbove(this)
        sash.background = Theme.windowBackgroundColor
        if (!setParent(sash)) {
            showError("Vertical Splitting is unsupported on your platform!")
            sash.dispose()
            return
        }
        MainWindow.tabFolder = this
        val tabs = CustomTabs(sash, SWT.NONE)
        val codeEditor = StyledCodeEditor(tabs, SWT.V_SCROLL or SWT.MULTI or SWT.H_SCROLL, null, true)
        MainWindow.addEditor(codeEditor)
        parent.weights = weights
        sash.weights = intArrayOf(1, 1)
        parent.layout(true)
    }

    private fun <T> move(c: AbstractList<T>, from: Int, to: Int) {
        var index = from
        c.add(to, c[index])
        if (to < index) index++
        c.removeAt(index)
    }

    fun getTabChild(idx: Int): TabChild? {
        return tabChildren[idx]
    }

    private fun getTab(p: Point): Int {
        val breaks = IntArray(tabs.size + 1)
        breaks[0] = 0
        for (i in 0 until breaks.size - 1) breaks[i + 1] = breaks[i] + tabs[i].size.x + 1
        var minDist = Int.MAX_VALUE
        var pos = -1
        for (i in breaks.indices) {
            val d = abs(breaks[i] - p.x)
            if (d < minDist) {
                minDist = d
                pos = i
            }
        }
        return pos
    }

    private fun moveTab(fromIdx: Int, toIdx: Int) {
        var idx = toIdx
        move(tabs, fromIdx, idx)
        move(tabChildren, fromIdx, idx)
        if (idx > fromIdx) idx--
        if (selected == fromIdx) selected = idx else if (selected in (fromIdx + 1)..idx) selected-- else if (selected in idx until fromIdx) selected++
        resize()
        updateTabs()
    }

    private fun moveTab(tabIdx: Int, dropX: Int, dropY: Int) {
        val p = toControl(dropX, dropY)
        if (p.y < 50) {
            val pos = getTab(p)
            moveTab(tabIdx, pos)
        }
    }

    fun addTab(text: String, e: TabChild) {
        val tab = CustomTab(this, 0, tabs.size)
        tab.background = Theme.tabBackgroundColor
        tab.foreground = Theme.editorForegroundColor
        tab.text = text
        tab.addListener(SWT.Selection, this)
        tab.addListener(SWT.Close, this)
        tabs.add(tab)
        tabChildren.add(e)
        resize()
        if (selected == -1) setSelection(tabs.size - 1)
        updateTabs()
    }

    private fun resize() {
        var x = 0
        var h = 0
        if (!isDisposed) {
            val size = size
            var oversized = false
            for (tab in tabs) {
                val tabExtent = tab.computeSize(SWT.DEFAULT, SWT.DEFAULT, false)
                tab.setBounds(x, 0, tabExtent.x, tabExtent.y)
                if (tabExtent.x + x >= size.x) {
                    oversized = true
                    tab.isVisible = false
                } else {
                    tab.isVisible = true
                }
                x += tabExtent.x + 1
                h = h.coerceAtLeast(tabExtent.y)
            }
            if (oversized) {
                hiddenTabs.clear()
                for (tab in tabs) {
                    val bounds = tab.bounds
                    if (bounds.x + bounds.width >= size.x - 26) {
                        tab.isVisible = false
                        hiddenTabs.add(tab)
                    } else {
                        tab.isVisible = true
                    }
                }
                overflowButton.isVisible = true
                overflowButton.setBounds(size.x - 25, 0, 25, 25)
            } else {
                overflowButton.isVisible = false
            }
            for (i in tabs.indices) {
                val e = tabChildren[i]
                val p = getSize()
                e.setBounds(0, h, p.x, p.y - h)
                if (selected >= 0 && tabChildren[selected] === e) {
                    e.setVisible(true)
                } else {
                    e.setVisible(false)
                }
            }
        }
    }

    override fun computeSize(wHint: Int, hHint: Int, changed: Boolean): Point {
        var width = 0
        var height = 0
        for (tab in tabs) {
            val tabExtent = tab.computeSize(SWT.DEFAULT, SWT.DEFAULT, false)
            width += tabExtent.x
            height = height.coerceAtLeast(tabExtent.y)
        }
        if (wHint != SWT.DEFAULT) width = wHint
        if (hHint != SWT.DEFAULT) height = hHint
        return Point(width, height)
    }

    private fun updateTabs() {
        var sel: TabChild? = null
        if (selected >= 0) {
            sel = tabChildren[selected]
        }
        for (i in tabChildren.indices) {
            val c = tabChildren[i]
            val t = tabs[i]
            t.setIndex(i)
            if (c === sel) {
                c.setVisible(true)
                t.background = Theme.tabSelectedBackgroundColor
            } else {
                c.setVisible(false)
                t.background = Theme.tabBackgroundColor
            }
            t.redraw()
        }
        redraw()
    }

    override fun handleEvent(event: Event) {
        when (event.type) {
            SWT.Selection -> {
                var i = 0
                while (i < tabs.size) {
                    val tab = tabs[i]
                    if (tab == event.widget) {
                        setSelection(i)
                        tab.background = Theme.tabSelectedBackgroundColor
                    } else {
                        tab.background = Theme.tabBackgroundColor
                    }
                    i++
                }
            }
            SWT.Close -> {
                var i = 0
                while (i < tabs.size) {
                    if (tabs[i] === event.widget) {
                        val control = tabChildren[i]
                        close(control)
                        break
                    }
                    i++
                }
            }
        }
    }

    val selectedControl: TabChild?
        get() = if (selected < 0) null else tabChildren[selected]

    fun setSelection(c: Control) {
        for (i in tabChildren.indices) {
            if (tabChildren[i] == c) {
                setSelection(i)
                break
            }
        }
    }

    fun nextTab() {
        selected = (selected + 1) % tabs.size
        updateTabs()
        redraw()
        tabChildren[selected].forceFocus()
    }

    fun prevTab() {
        selected--
        if (selected < 0) selected = tabs.size - 1
        updateTabs()
        redraw()
        tabChildren[selected].forceFocus()
    }

    fun setSelection(i: Int) {
        selected = i
        updateTabs()
        redraw()
    }

    fun setText(c: Control, text: String?) {
        for (i in tabChildren.indices) {
            if (tabChildren[i] === c) {
                tabs[i].text = text
                tabs[i].redraw()
                break
            }
        }
        resize()
        // updateTabs();
    }

    fun getText(c: Control): String? {
        return tabChildren.indexOfFirst { it == c }.let { if (it >= 0) tabs[it].text else null }
    }

    fun remove(e: TabChild?) {
        tabChildren.indexOf(e).let { if (it >= 0) remove(it) }
    }

    fun remove(i: Int) {
        tabChildren.removeAt(i)
        tabs[i].dispose()
        tabs.removeAt(i)
        if (selected == i) {
            if (selected > 0) setSelection(selected - 1) else if (tabChildren.size > 0) setSelection(0) else setSelection(-1)
        } else if (selected > i) {
            setSelection(selected - 1)
        }
        val p = parent
        val pp = p.parent
        if (tabChildren.size == 0 && !MainWindow.isSideSash(p)) {
            val weights = (pp as SashForm).weights
            dispose()
            for (c in p.children) if (c !is Sash) {
                c.parent = pp
                c.moveAbove(p)
                break
            }
            p.dispose()
            pp.weights = weights
            pp.layout(true)
            MainWindow.setDefaultFolder()
        } else {
            if (tabChildren.size == 0) {
                opened = false
                val codeEditor = StyledCodeEditor(this, SWT.V_SCROLL or SWT.MULTI or SWT.H_SCROLL, null, true)
                MainWindow.addEditor(codeEditor)
            }
            resize()
            updateTabs()
        }
    }

    fun close() {
        if (selected < tabs.size) {
            close(selected)
            tabChildren[selected].forceFocus()
        }
    }

    fun close(i: Int) {
        val e = tabChildren[i]
        if (MainWindow.closeTab(e)) {
            e.dispose()
            remove(i)
        }
        resize()
    }

    fun close(c: TabChild?) {
        val idx = tabChildren.indexOf(c)
        if (idx >= 0) close(idx)
    }

    fun setTabTextColor(c: Control?, tabTextColor: Color?) {
        val idx = tabChildren.indexOf(c as Any?)
        if (idx < 0 || idx > tabs.size) return
        tabs[idx].foreground = tabTextColor
        tabs[idx].redraw()
    }
}