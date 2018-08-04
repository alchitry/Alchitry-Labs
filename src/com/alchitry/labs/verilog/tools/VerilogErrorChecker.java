package com.alchitry.labs.verilog.tools;

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

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.lucid.style.ErrorToStyle;
import com.alchitry.labs.style.ErrorChecker;
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.SyntaxError;

public class VerilogErrorChecker implements ErrorChecker, ModifyListener, LineStyleListener, Runnable {
	private ArrayList<SyntaxError> errors;
	private StyledCodeEditor editor;
	private Display display;
	private StyleRange[] styles;
	private boolean newStyles = false;
	private HashMap<Integer, SyntaxError> lineErrors = new HashMap<>();

	public VerilogErrorChecker(StyledCodeEditor editor) {
		this.editor = editor;
		display = editor.getDisplay();
	}

	public VerilogErrorChecker() {
	}

	public ArrayList<SyntaxError> getErrors(String file) {
		errors = ErrorToStyle.getErrors(file);
		return errors;
	}

	public void updateErrors() {
		getErrors(editor.getFilePath());

		lineErrors.clear();
		for (SyntaxError e : errors) {
			SyntaxError o = lineErrors.get(e.line);
			if (o == null || (o != null && o.type > e.type)) {
				lineErrors.put(e.line, e);
			}
		}

		newStyles = true;

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
		display.timerExec(500, this);
	}

	@Override
	public void run() {
		updateErrors();
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		List<SyntaxError> errors = this.errors;
		if (errors != null) {
			event.data = Boolean.valueOf(newStyles);
			if (newStyles) {
				ArrayList<StyleRange> styleList = new ArrayList<StyleRange>(errors.size());
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
		if (errors != null)
			for (SyntaxError e : errors) {
				if (e.start <= pos && e.stop >= pos)
					return e;
			}
		return null;
	}

	@Override
	public boolean hasSyntaxErrors() {
		return hasErrors();
	}

	@Override
	public Color getLineColor(int line) {
		if (lineErrors.get(line) != null)
			return lineErrors.get(line).style.underlineColor;
		return null;
	}
}
