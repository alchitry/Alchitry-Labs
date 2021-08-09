package com.alchitry.labs.style;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolTip;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.parsers.errors.ErrorProvider;

public class ToolTipListener implements MouseTrackListener, MouseMoveListener {

	private StyledCodeEditor editor;
	private ErrorProvider errorChecker;
	private ToolTip toolTip;
	private SyntaxError error;

	public ToolTipListener(StyledCodeEditor editor, ErrorProvider errorChecker) {
		this.editor = editor;
		this.errorChecker = errorChecker;
		toolTip = new ToolTip(editor.getShell(), SWT.ICON_ERROR);
		toolTip.setAutoHide(true);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
	}

	@Override
	public void mouseExit(MouseEvent e) {
	}

	@Override
	public void mouseHover(MouseEvent e) {
		// FIXME:
		//   error: cannot find symbol "editor.getOffsetAtPoint"
		//
		// try {
		// 	int offset = editor.getOffsetAtPoint(new Point(e.x, e.y));
		// 	if (errorChecker != null)
		// 		error = errorChecker.getErrorAtOffset(offset);
		// 	if (error != null) {
		// 		toolTip.setMessage(error.message);
		// 		Point base = editor.toDisplay(editor.getLocation());
		// 		toolTip.setLocation(base.x + e.x, base.y + e.y);
		// 		toolTip.setVisible(true);
		// 	} else {
		// 		toolTip.setVisible(false);
		// 		error = null;
		// 	}
		// } catch (IllegalArgumentException ex) {
		// 	toolTip.setVisible(false);
		// 	error = null;
		// }
	}

	@Override
	public void mouseMove(MouseEvent e) {
		// FIXME:
		//   error: cannot find symbol "editor.getOffsetAtPoint"
		//
		// if (error != null) {
		// 	try {
		// 		int offset = editor.getOffsetAtPoint(new Point(e.x, e.y));
		// 		if (error.start > offset || error.stop < offset) {
		// 			toolTip.setVisible(false);
		// 			error = null;
		// 		}
		// 	} catch (IllegalArgumentException ex) {
		// 		toolTip.setVisible(false);
		// 		error = null;
		// 	}
		// }
	}

}
