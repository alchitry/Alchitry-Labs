package com.alchitry.labs.parsers.errors;

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
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.SyntaxError;

public abstract class ErrorProvider implements ModifyListener, LineStyleListener {
	protected ArrayList<SyntaxError> errors;
	protected StyledCodeEditor editor;
	protected Display display;
	protected StyleRange[] styles;
	protected boolean newStyles = false;
	protected HashMap<Integer, SyntaxError> lineErrors = new HashMap<>();
	
	protected ErrorProvider() {
		errors = new ArrayList<>();
	}
	
	protected ErrorProvider(StyledCodeEditor editor) {
		this();
		this.editor = editor;
		display = editor.getDisplay();
	}
	
	public ArrayList<SyntaxError> getErrors(String file) {
		errors = ErrorUtil.getErrors(file);
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

	public boolean hasErrors() {
		for (SyntaxError se : errors) {
			if (se.type == SyntaxError.ERROR)
				return true;
		}
		return false;
	}

	public boolean hasWarnings() {
		for (SyntaxError se : errors) {
			if (se.type == SyntaxError.WARNING)
				return true;
		}
		return false;
	}

	public SyntaxError getErrorAtOffset(int pos) {
		if (errors != null)
			for (SyntaxError e : errors) {
				if (e.start <= pos && e.stop >= pos)
					return e;
			}
		return null;
	}

	public Color getLineColor(int line) {
		if (lineErrors.get(line) != null)
			return lineErrors.get(line).style.underlineColor;
		return null;
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		display.timerExec(-1, updateTask);
		display.timerExec(500, updateTask);
	}
	
	private Runnable updateTask = new Runnable() {
		@Override
		public void run() {
			updateErrors();
		}
	};
	
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
}
