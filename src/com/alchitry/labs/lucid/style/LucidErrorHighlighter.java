package com.alchitry.labs.lucid.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.alchitry.labs.gui.MainWindow;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.lucid.LucidDictionary;
import com.alchitry.labs.lucid.tools.LucidErrorChecker;
import com.alchitry.labs.style.ErrorChecker;
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.SyntaxError;

public class LucidErrorHighlighter implements ErrorChecker, ModifyListener, LineStyleListener, Runnable {
	private List<SyntaxError> errors = new ArrayList<SyntaxError>();
	private StyledCodeEditor editor;
	private Display display;
	private LucidErrorChecker errorChecker;
	private StyleRange[] styles;
	private boolean newStyles = false;
	private HashMap<Integer, SyntaxError> lineErrors = new HashMap<>();

	public LucidErrorHighlighter(StyledCodeEditor editor, LucidDictionary dict) {
		this.editor = editor;
		display = editor.getDisplay();
		errorChecker = new LucidErrorChecker(dict, null);
	}

	public void updateErrors() {
		try {
			MainWindow.getOpenProject().updateGlobals();
		} catch (Exception e) {
		}
		errors.clear();
		errors.addAll(errorChecker.getErrors(editor.getFilePath()));
		List<SyntaxError> gErrors = null;
		if (MainWindow.getOpenProject() != null)
			gErrors = MainWindow.getOpenProject().getGlobalErrors(editor.getFilePath());
		if (gErrors != null)
			errors.addAll(gErrors);
		SyntaxError.spliceErrors(errors);
		newStyles = true;

		lineErrors.clear();
		for (SyntaxError e : errors) {
			SyntaxError o = lineErrors.get(e.line);
			if (o == null || (o != null && o.type > e.type)) {
				lineErrors.put(e.line, e);
			}
		}

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!editor.isDisposed()) {
					editor.updateTextColor();
					editor.redraw();
				}
			}
		});
	}

	@Override
	public boolean hasErrors() {
		for (SyntaxError se : errors) {
			if (se.type == SyntaxError.ERROR)
				return true;
		}
		return false;
	}

	@Override
	public boolean hasWarnings() {
		for (SyntaxError se : errors) {
			if (se.type == SyntaxError.WARNING)
				return true;
		}
		return false;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		display.timerExec(-1, this);
		display.timerExec(750, this);
	}

	@Override
	public void run() {
		synchronized (errors) {
			updateErrors();
		}
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		synchronized (errors) {
			event.data = Boolean.valueOf(newStyles);
			if (newStyles) {
				ArrayList<StyleRange> styleList = new ArrayList<StyleRange>();

				for (SyntaxError e : errors) {
					styleList.add(e.style);
				}
				StyleUtil.sort(styleList);
				styles = styleList.toArray(new StyleRange[styleList.size()]);
				newStyles = false;
			}
			event.styles = styles;
		}
	}

	public SyntaxError getErrorAtOffset(int pos) {
		for (SyntaxError e : errors) {
			if (e.start <= pos && e.stop >= pos)
				return e;
		}
		return null;
	}

	@Override
	public boolean hasSyntaxErrors() {
		return errorChecker.checkSyntaxErrors(editor.getFilePath());
	}

	@Override
	public Color getLineColor(int line) {
		if (lineErrors.get(line) != null)
			return lineErrors.get(line).style.underlineColor;
		return null;
	}
}
