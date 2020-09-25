package com.alchitry.labs.gui;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import java.util.Stack;

public class UndoRedo implements VerifyListener {
	private StyledCodeEditor editor;
	private Stack<Edit> undoStack;
	private Stack<Edit> redoStack;
	private boolean editing;
	private boolean skip;
	private long lastEdit = 0;

	private class Edit {
		public int position;
		public String text;

		public Edit() {
			position = editor.getCaretOffset();
			text = editor.getText();
		}
	}

	public boolean isEditing() {
		return editing;
	}

	public UndoRedo(StyledCodeEditor editor) {
		this.editor = editor;
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		editing = false;
		skip = false;
	}

	public void skipNext() {
		skip = true;
	}

	@Override
	public void verifyText(VerifyEvent e) {
		if (!skip && !editing) {
			long now = System.currentTimeMillis();
			if ((lastEdit + 1000) < now) {
				lastEdit = now;
				undoStack.push(new Edit());
				redoStack.clear();
			}
		} 
		editing = skip = false;
	}

	private void replace(Stack<Edit> popStack, Stack<Edit> pushStack) {
		if (!editing && popStack.size() > 0) {
			Edit edit = popStack.pop();

			pushStack.push(new Edit());

			editing = true;
			editor.replaceTextRange(0, editor.getCharCount(), edit.text);
			editor.setCaretOffset(edit.position);
			editor.update();
		}
	}

	public void undo() {
		replace(undoStack, redoStack);
	}

	public void redo() {
		replace(redoStack, undoStack);
	}

	public boolean canUndo() {
		return !undoStack.empty();
	}

	public boolean canRedo() {
		return !redoStack.empty();
	}

}
