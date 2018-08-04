package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;

public class HotKeys implements VerifyKeyListener {
	private StyledCodeEditor editor;

	public HotKeys(StyledCodeEditor editor) {
		this.editor = editor;
	}

	@Override
	public void verifyKey(VerifyEvent e) {
		if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
			if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT) { // ctrl + shift
				switch (e.keyCode) {
				case 'z':
					editor.redo(); // CTRL + SHIFT + z
					e.doit = false;
					break;
				case 'f':
					editor.formatText();
					e.doit = false;
					break;
				case SWT.TAB:
					e.doit = false;
					break;
				}
			} else { // ctrl only
				switch (e.keyCode) {
				case 'a':
					editor.selectAll();
					e.doit = false;
					break;
				case 's':
					editor.save();
					e.doit = false;
					break;
				case 'z':
					editor.undo();
					e.doit = false;
					break;
				case 'f':
					editor.setSearch(true);
					e.doit = false;
					break;
				case SWT.TAB:
					e.doit = false;
					break;
				}
			}
		} else {
			switch (e.keyCode) {
			case SWT.ESC:
				editor.setSearch(false);
				e.doit = false;
				break;
			}
		}
	}

}
