package com.alchitry.labs.parsers.styles;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.util.UndoRedo;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class VerilogNewLineIndenter implements VerifyListener {

	private StyledCodeEditor editor;
	private UndoRedo undo;

	public VerilogNewLineIndenter(StyledCodeEditor e, UndoRedo undo) {
		editor = e;
		this.undo = undo;
	}

	private void unindent(VerifyEvent e) {

	}

	private int countSpaces(String line) {
		int ct = 0;
		int idx = 0;
		for (idx = 0; idx < line.length(); idx++) {
			char c = line.charAt(idx);
			if (c == ' ')
				ct++;
			else if (c == '\t')
				ct += 2;
			else
				break;
		}
		return ct;
	}

	@Override
	public void verifyText(VerifyEvent e) {
		if (!undo.isEditing())
			if (e.text.equals("\n") || e.text.equals("\r\n")) {
				String prevLine = editor.getLine(editor.getLineAtOffset(e.start));
				int indents = countSpaces(prevLine);

				StringBuilder newText = new StringBuilder(e.text);
				for (int i = 0; i < indents; i++) {
					newText.append(' ');
				}
				e.text = newText.toString();
			} else {
				unindent(e);
			}
	}
}
