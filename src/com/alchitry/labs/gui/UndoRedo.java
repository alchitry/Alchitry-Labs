package com.alchitry.labs.gui;

import java.util.Stack;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.alchitry.labs.Util;

public class UndoRedo implements ExtendedModifyListener, VerifyListener {

	private StyledCodeEditor editor;
	private Stack<Edit> undoStack;
	private Stack<Edit> redoStack;
	private boolean editing;
	private boolean skip;
	private int lastHash;

	private class Edit {
		public int start;
		public int length;
		public String text;
		public int editorHash;

		public Edit(int start, int length, String text, int editorHash) {
			this.start = start;
			this.length = length;
			this.text = text;
			this.editorHash = editorHash;
		}

		public Edit(ExtendedModifyEvent event) {
			editorHash = editor.getText().hashCode();
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
	public void verifyText(VerifyEvent event) {
		lastHash = editor.getText().hashCode();
	}

	@Override
	public void modifyText(ExtendedModifyEvent event) {
		if (!skip && !editing) {
			Edit e = new Edit(event);
			e.editorHash = lastHash;
			undoStack.push(e);
			redoStack.clear();
		}
		editing = skip = false;
	}

	private void replace(Stack<Edit> popStack, Stack<Edit> pushStack, boolean undo) {
		if (!editing && popStack.size() > 0) {
			Edit edit = popStack.pop();
			
			String replacedText = "";
			if (edit.length > 0)
				replacedText = editor.getText(edit.start, edit.start + edit.length - 1);
			
			pushStack.push(new Edit(edit.start, edit.text.length(), replacedText, editor.getText().hashCode()));
			
			// System.out.println("Replacing from: " + edit.start+" for: "+edit.length+" with "+edit.text);
			editing = true;
			if (editor.getCharCount() > 0)
				editor.replaceTextRange(edit.start, edit.length, edit.text);
			else
				editor.insert(edit.text);
			editor.setCaretOffset(edit.start + edit.text.length());
			editor.update();
			
			if (editor.getText().hashCode() != edit.editorHash) {
				Util.showError("Undo/Redo Error", "An error occured with the undo/redo stack. It is out of sync with the editor.");
				return;
			}
			//while (Util.getDisplay().readAndDispatch());
			
		}
	}

	public void undo() {
		replace(undoStack, redoStack, true);
	}

	public void redo() {
		replace(redoStack, undoStack, false);
	}

	public boolean canUndo() {
		return !undoStack.empty();
	}

	public boolean canRedo() {
		return !redoStack.empty();
	}


}
