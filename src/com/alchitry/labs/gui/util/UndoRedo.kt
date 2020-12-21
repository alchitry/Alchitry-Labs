package com.alchitry.labs.gui.util

import com.alchitry.labs.gui.StyledCodeEditor
import org.eclipse.swt.events.VerifyEvent
import org.eclipse.swt.events.VerifyListener
import java.util.*

class UndoRedo(private val editor: StyledCodeEditor) : VerifyListener {
    private val undoStack: Stack<Edit> = Stack()
    private val redoStack: Stack<Edit> = Stack()
    var isEditing: Boolean = false
        private set
    private var skip: Boolean = false
    private var lastEdit: Long = 0

    private inner class Edit {
        var position: Int = editor.caretOffset
        var text: String = editor.text
    }

    fun skipNext() {
        skip = true
    }

    override fun verifyText(e: VerifyEvent) {
        if (!skip && !isEditing) {
            val now = System.currentTimeMillis()
            if (lastEdit + 1000 < now) {
                lastEdit = now
                undoStack.push(Edit())
                redoStack.clear()
            }
        }
        skip = false
        isEditing = skip
    }

    private fun replace(popStack: Stack<Edit>, pushStack: Stack<Edit>) {
        if (!isEditing && popStack.size > 0) {
            val edit = popStack.pop()
            pushStack.push(Edit())
            isEditing = true
            editor.text = edit.text
            editor.caretOffset = edit.position
            editor.update()
        }
    }

    fun undo() {
        replace(undoStack, redoStack)
    }

    fun redo() {
        replace(redoStack, undoStack)
    }

    fun canUndo(): Boolean {
        return !undoStack.empty()
    }

    fun canRedo(): Boolean {
        return !redoStack.empty()
    }
}