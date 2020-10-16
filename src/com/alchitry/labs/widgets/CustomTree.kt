package com.alchitry.labs.widgets

import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.util.Images
import com.alchitry.labs.widgets.CustomTree
import org.eclipse.swt.SWT
import org.eclipse.swt.events.*
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.graphics.Transform
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import java.io.File
import java.security.InvalidParameterException
import java.util.*

class CustomTree(parent: Composite) : Canvas(parent, SWT.DOUBLE_BUFFERED) {
    private val root: MutableList<TreeElement> = ArrayList()
    private val visibleNodes: MutableList<TreeElement> = ArrayList()
    private var hoverElement: TreeElement? = null
    private var activeElement: TreeElement? = null
    private var oldHeight = -1

    init {
        font = Theme.defaultFont
        addPaintListener { e -> paintControl(e) }
        addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent) {
                redraw()
            }

            override fun focusGained(e: FocusEvent) {
                redraw()
            }
        })
        addMouseMoveListener { e ->
            val mouseEle = getElement(e.x, e.y)
            val newEle = hoverElement !== mouseEle
            when (mouseState) {
                MouseState.NONE -> {
                    mouseState = MouseState.HOVER
                    if (newEle) {
                        redraw(hoverElement?.bounds)
                    }
                    hoverElement = mouseEle
                    if (newEle) {
                        redraw(hoverElement?.bounds)
                    }
                }
                MouseState.HOVER -> {
                    if (newEle) {
                        redraw(hoverElement?.bounds)
                    }
                    hoverElement = mouseEle
                    if (newEle) {
                        redraw(hoverElement?.bounds)
                    }
                }
                MouseState.CLICK_ACTIVE -> if (newEle) mouseState = MouseState.CLICK_INACTIVE
                MouseState.CLICK_INACTIVE -> if (!newEle) mouseState = MouseState.CLICK_ACTIVE
            }
        }
        addMouseTrackListener(object : MouseTrackAdapter() {
            override fun mouseExit(e: MouseEvent) {
                when (mouseState) {
                    MouseState.HOVER, MouseState.NONE -> {
                        redraw(hoverElement?.bounds)
                        hoverElement = null

                        mouseState = MouseState.NONE
                    }
                    MouseState.CLICK_ACTIVE, MouseState.CLICK_INACTIVE -> mouseState = MouseState.CLICK_INACTIVE
                }
            }

            override fun mouseEnter(e: MouseEvent) {
                val mouseEle = getElement(e.x, e.y)
                when (mouseState) {
                    MouseState.NONE, MouseState.HOVER -> {
                        if (mouseEle != null) {
                            hoverElement = mouseEle
                        }
                        mouseState = MouseState.HOVER
                        redraw()
                    }
                    MouseState.CLICK_INACTIVE, MouseState.CLICK_ACTIVE -> {
                        mouseState = MouseState.CLICK_ACTIVE
                        redraw(activeElement?.bounds)
                    }
                }
            }
        })
        addMouseListener(object : MouseListener {
            override fun mouseUp(e: MouseEvent) {
                val mouseEle = getElement(e.x, e.y)
                if (mouseEle != null && mouseEle === activeElement) {
                    activeElement = mouseEle
                    mouseState = MouseState.HOVER
                    activeElement?.let {
                        if (it.isNode) {
                            val activeNode = it as TreeNode
                            activeNode.isOpen = !activeNode.isOpen
                            updateTree()
                            val event = Event()
                            event.widget = this@CustomTree
                            notifyListeners(SWT.Resize, event)
                            redraw()
                        } else {
                            redraw(it.bounds)
                        }
                    }
                }
            }

            override fun mouseDown(e: MouseEvent) {
                setFocus()
                val mouseEle = getElement(e.x, e.y)
                val newEle = mouseEle !== activeElement
                if (mouseEle != null) {
                    if (newEle) {
                        redraw(activeElement?.bounds)
                    }
                    activeElement = mouseEle
                    mouseState = MouseState.CLICK_ACTIVE
                    redraw(activeElement?.bounds)
                    if (e.button == 3) {
                        mouseEle.clicked(this@CustomTree, e.button)
                    }
                } else {
                    if (e.button == 3) {
                        for (mi in menu.items) mi.dispose()
                    }
                }
            }

            override fun mouseDoubleClick(e: MouseEvent) {
                val mouseEle = getElement(e.x, e.y)
                if (mouseEle != null && !mouseEle.isNode && e.button == 1) {
                    mouseEle.clicked(this@CustomTree, e.button)
                }
            }
        })
        addListener(SWT.Resize) { updateBounds() }
    }

    private enum class MouseState {
        NONE, HOVER, CLICK_ACTIVE, CLICK_INACTIVE
    }

    private var mouseState = MouseState.NONE
    private fun redraw(r: Rectangle?) {
        if (r == null) return
        redraw(r.x, r.y, r.width, r.height, false)
    }

    private fun getElement(x: Int, y: Int): TreeElement? {
        for (e in visibleNodes) {
            if (e.bounds?.contains(x, y) == true) {
                return e
            }
        }
        return null
    }

    private fun paintControl(e: PaintEvent) {
        val gc = e.gc
        val textHeight = gc.stringExtent("T").y
        if (oldHeight != textHeight) updateBounds(textHeight)
        gc.background = background
        val clientArea = clientArea
        gc.fillRectangle(clientArea)
        gc.foreground = foreground
        for (ele in visibleNodes) {
            ele.bounds?.let { bounds ->
                var selected = false
                val x = ele.depth * 15 + 5
                if (ele === activeElement) {
                    selected = true
                    if (isFocusControl) {
                        gc.background = Theme.treeSelectedFocusedColor
                    } else {
                        gc.background = Theme.treeSelectedColor
                    }
                    gc.fillRectangle(ele.bounds)
                } else if (ele === hoverElement) {
                    gc.background = Theme.treeHoverColor
                    gc.fillRectangle(ele.bounds)
                }
                gc.background = background
                if (ele.isNode && (ele as TreeNode).hasChildren()) {

                    val arrowIcon = if (selected) Images.treeArrowIconSelected else Images.treeArrowIcon
                    val yOffset = bounds.y + (bounds.height - arrowIcon.bounds.height) / 2 - 1
                    val oldTransform = Transform(gc.device)
                    if (ele.isOpen) setRotation(gc, x, yOffset, arrowIcon.bounds, 90f)
                    gc.drawImage(arrowIcon, x, yOffset)
                    gc.setTransform(oldTransform)

                }
                if (selected) gc.foreground = Theme.treeSelectedForegroundColor else gc.foreground = foreground
                gc.drawText(ele.name, x + 20, bounds.y + 2, true)
            }
        }
    }

    private fun setRotation(gc: GC, x: Int, y: Int, bounds: Rectangle, angle: Float) {
        val transform = Transform(gc.device)
        transform.translate(x + bounds.width / 2.toFloat(), y + bounds.height / 2.toFloat())
        transform.rotate(angle)
        transform.translate(-x - bounds.width / 2.toFloat(), -y - bounds.height / 2.toFloat())
        gc.setTransform(transform)
    }

    private fun updateBounds(textHeight: Int = oldHeight) {
        if (textHeight <= 0) return
        oldHeight = textHeight
        var y = 0
        val clientArea = clientArea
        for (te in root) {
            val itr = getTreeIterator(te)
            while (itr.hasNext()) {
                val ele = itr.next()
                val bounds = Rectangle(0, y, clientArea.width, 6 + textHeight)
                ele.bounds = bounds
                y += 6 + textHeight
            }
        }
    }

    // private int getChildCount(List<TreeElement> root, boolean openOnly) {
    // int i = 0;
    // for (TreeElement e : root)
    // i += getChildCount(e, openOnly);
    // return i;
    // }
    // private int getChildCount(TreeElement root, boolean openOnly) {
    // int i = 0;
    // Iterator<TreeElement> itr = getTreeIterator(root, openOnly);
    // while (itr.hasNext()) {
    // itr.next();
    // i++;
    // }
    // return i;
    // }
    private fun getTreeIterator(root: TreeElement): Iterator<TreeElement> {
        return object : MutableIterator<TreeElement> {
            private var next: TreeElement? = root
            private var current: TreeElement? = null
            override fun hasNext(): Boolean {
                return next != null
            }

            override fun next(): TreeElement {
                val curNext = this.next ?: throw IllegalStateException("No next element!")
                var ptr = curNext
                current = curNext
                next = null

                if (ptr !is TreeNode || !ptr.hasChildren() || !ptr.isOpen) {
                    while (true) {
                        val parent = ptr.parent ?: return curNext
                        val idx = parent.children.indexOf(ptr)
                        if (idx + 1 >= parent.children.size) {
                            ptr = parent
                            if (parent === root) return curNext
                        } else {
                            next = parent.children[idx + 1]
                            return curNext
                        }
                    }
                } else { // must be open node with children
                    next = ptr.children[0]
                    return curNext
                }
            }

            override fun remove() {
                current?.let {
                    var ptr = it
                    next = null
                    while (true) {
                        val parent = ptr.parent ?: break
                        val idx = parent.children.indexOf(ptr)
                        if (idx + 1 >= parent.children.size) {
                            ptr = parent
                            if (parent === root) break
                        } else {
                            next = parent.children[idx + 1]
                            break
                        }
                    }
                    val parent = it.parent
                    parent?.remove(it)
                    it.parent = null
                }
            }
        }
    }

    fun getDepth(e: TreeElement): Int {
        val parent = e.parent ?: return 0
        return 1 + getDepth(parent)
    }

    override fun computeSize(wHint: Int, hHint: Int, changed: Boolean): Point {
        // int openRows = getChildCount(root, true);
        val gc = GC(this)
        var maxWidth = 0
        var height = 0
        for (te in root) {
            val itr = getTreeIterator(te)
            while (itr.hasNext()) {
                val ele = itr.next()
                ele.bounds?.let {
                    height += it.height
                }
                val textSize = gc.stringExtent(ele.name)
                maxWidth = Math.max(textSize.x + 15 * getDepth(ele) + 20, maxWidth)
            }
        }
        gc.dispose()
        return Point(maxWidth + 10, height)
    }

    fun updateTree() {
        visibleNodes.clear()
        for (te in root) {
            val itr = getTreeIterator(te)
            while (itr.hasNext()) {
                val ele = itr.next()
                visibleNodes.add(ele)
                ele.depth = getDepth(ele)
            }
        }
        updateBounds()
    }

    fun getElement(i: Int): TreeElement {
        return root[i]
    }

    val rootSize: Int
        get() = root.size

    fun addElement(ele: TreeElement) {
        root.add(ele)
    }

    fun removeAll() {
        root.clear()
    }

    abstract class TreeElement(val name: String) {
        private val listeners = mutableListOf<Listener>()
        var parent: TreeNode? = null
        var bounds: Rectangle? = null
        var depth = 0
        abstract val isNode: Boolean
        fun addClickListener(listener: Listener) {
            listeners.add(listener)
        }

        fun clicked(tree: CustomTree?, mouseButton: Int) {
            val e = Event()
            e.data = this
            e.widget = tree
            e.button = mouseButton
            for (l in listeners) l.handleEvent(e)
        }
    }

    class TreeNode(name: String) : TreeElement(name) {
        val children = mutableListOf<TreeElement>()
        var isOpen = false
        override val isNode: Boolean
            get() = true

        fun hasChildren(): Boolean {
            return children.size > 0
        }

        fun add(ele: TreeElement) {
            add(children.size, ele)
        }

        fun add(i: Int, ele: TreeElement) {
            if (ele.parent != null) throw InvalidParameterException("Can't add TreeElements that already have a parent!")
            ele.parent = this
            children.add(i, ele)
        }

        fun remove(i: Int) {
            children[i].parent = null
            children.removeAt(i)
        }

        fun remove(ele: TreeElement): Boolean {
            if (children.remove(ele)) {
                ele.parent = null
                return true
            }
            return false
        }
    }

    class TreeLeaf(val file: File) : TreeElement(file.name) {
        override val isNode: Boolean
            get() = false
    }
}