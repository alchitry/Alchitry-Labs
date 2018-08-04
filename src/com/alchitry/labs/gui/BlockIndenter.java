package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class BlockIndenter {
	private StyledCodeEditor editor;
	private boolean shift;

	public BlockIndenter(StyledCodeEditor editor) {
		this.editor = editor;
		editor.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				BlockIndenter.this.verifyText(e);
			}
		});

		editor.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
					e.doit = false; // allows verify listener to fire
				}
			}
		});

		editor.addVerifyKeyListener(new VerifyKeyListener() {

			@Override
			public void verifyKey(VerifyEvent event) {
				shift = (event.stateMask & SWT.SHIFT) != 0;
			}
		});
	}

	private void verifyText(VerifyEvent e) {
		if (e.text.equals("\t")) {
			int firstLine = editor.getLineAtOffset(e.start);
			int lastLine = editor.getLineAtOffset(e.end);

			if (firstLine == lastLine) {
				e.text = "  ";
			} else if (shift) {
				e.doit = false;
				StringBuilder sb = new StringBuilder();
				for (int i = firstLine; i <= lastLine; i++) {
					String line = editor.getLine(i);
					int length = line.length();
					int offset = 0;
					if (length > 0 && line.charAt(0) == '\t') {
						offset = 1;
					} else {
						for (int j = 0; j < 2; j++) {
							if (length > j && line.charAt(j) == ' ')
								offset++;
							else
								break;
						}
					}
					sb.append(line, offset, length);
					if (i != lastLine)
						sb.append(System.lineSeparator());
				}
				int start = editor.getOffsetAtLine(firstLine);
				int end = editor.getOffsetAtLine(lastLine) + editor.getLine(lastLine).length();
				editor.replaceTextRange(start, end - start, sb.toString());
				editor.setSelection(start, start + sb.length());
			} else {
				e.doit = false;
				StringBuilder sb = new StringBuilder();
				for (int i = firstLine; i <= lastLine; i++) {
					sb.append("  ");
					sb.append(editor.getLine(i));
					if (i != lastLine)
						sb.append(System.lineSeparator());
				}
				int start = editor.getOffsetAtLine(firstLine);
				int end = editor.getOffsetAtLine(lastLine) + editor.getLine(lastLine).length();
				editor.replaceTextRange(start, end - start, sb.toString());
				editor.setSelection(start, start + sb.length());
			}
		}
	}
}
