package com.alchitry.labs.gui;

import java.util.Stack;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;

public class UndoRedo implements ExtendedModifyListener {

	private StyledCodeEditor editor;
	private Stack<Edit> undoStack;
	private Stack<Edit> redoStack;
	private boolean editing;
	private boolean skip;

	private class Edit {
		public int start;
		public int length;
		public String text;

		public Edit(int start, int length, String text) {
			this.start = start;
			this.length = length;
			this.text = text;

		}

		public Edit(ExtendedModifyEvent event) {

			start = event.start;
			length = event.length;
			text = event.replacedText;
		}

		@Override
		public String toString() {
			return "Start " + start + " Length " + length + " Text \"" + text + "\"";
		}
	}

	public boolean isEditing() {
		return editing;
	}

	public UndoRedo(StyledCodeEditor editor) {
		this.editor = editor;
		undoStack = new Stack<Edit>();
		redoStack = new Stack<Edit>();
		editing = false;
		skip = false;
	}

	public void skipNext() {
		skip = true;
	}

	@Override
	public void modifyText(ExtendedModifyEvent event) {
		if (!skip && !editing) {
			undoStack.push(new Edit(event));
			redoStack.clear();
		}
		editing = skip = false;
	}

	private void replace(Stack<Edit> popStack, Stack<Edit> pushStack) {
		if (popStack.size() > 0) {
			Edit edit = popStack.pop();
			String replacedText = "";
			if (edit.length > 0)
				replacedText = editor.getText(edit.start, edit.start + edit.length - 1);
			pushStack.push(new Edit(edit.start, edit.text.length(), replacedText));
			// System.out.println("Replacing from: " + edit.start+" for: "+edit.length+" with "+edit.text);
			editing = true;
			if (editor.getCharCount() > 0)
				editor.replaceTextRange(edit.start, edit.length, edit.text);
			else
				editor.insert(edit.text);
			editor.setCaretOffset(edit.start + edit.text.length());
			editor.update();
		}
	}

	public void undo() {
		if (!editing)
			replace(undoStack, redoStack);
	}

	public void redo() {
		if (!editing)
			replace(redoStack, undoStack);
	}

	public boolean canUndo() {
		return !undoStack.empty();
	}

	public boolean canRedo() {
		return !redoStack.empty();
	}
}
