package com.alchitry.labs.parsers.errors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.SyntaxError;
import com.alchitry.labs.tools.ParserCache;
import com.alchitry.labs.tools.ParserCache.ParseError;

public abstract class ErrorProvider implements ModifyListener, LineStyleListener, ErrorListener {
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
		errors.clear();
		for (ParseError e : ParserCache.getErrors(file)) 
			underline(e.token, null, e.msg, Theme.errorTextColor, SyntaxError.ERROR);
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
	
	@Override
	public void reportDebug(TerminalNode node, String message) {
		underline(node.getSymbol(), node.getSymbol(), message, Theme.debugTextColor, SyntaxError.DEBUG);
	}

	@Override
	public void reportDebug(ParserRuleContext ctx, String message) {
		underline(ctx.start, ctx.stop, message, Theme.debugTextColor, SyntaxError.DEBUG);
	}

	@Override
	public void reportError(TerminalNode node, String message) {
		underlineError(node, message);
	}

	@Override
	public void reportWarning(TerminalNode node, String message) {
		underlineWarning(node, message);
	}

	@Override
	public void reportError(ParserRuleContext ctx, String message) {
		underlineError(ctx, message);
	}

	@Override
	public void reportWarning(ParserRuleContext ctx, String message) {
		underlineWarning(ctx, message);
	}

	private void underlineWarning(ParserRuleContext ctx, String message) {
		underline(ctx.start, ctx.stop, message, Theme.warningTextColor, SyntaxError.WARNING);
	}

	private void underlineError(ParserRuleContext ctx, String message) {
		underline(ctx.start, ctx.stop, message, Theme.errorTextColor, SyntaxError.ERROR);
	}

	private void underlineWarning(TerminalNode term, String message) {
		underline(term.getSymbol(), term.getSymbol(), message, Theme.warningTextColor, SyntaxError.WARNING);
	}

	private void underlineError(TerminalNode term, String message) {
		underline(term.getSymbol(), term.getSymbol(), message, Theme.errorTextColor, SyntaxError.ERROR);
	}

	private void underline(Token startToken, Token stopToken, String message, Color color, int type) {
		int start = startToken.getStartIndex();
		int stop = stopToken == null ? startToken.getStopIndex() : stopToken.getStopIndex();

		if (stop < start) // bad token, can happen with severe syntax errors
			return;

		if (start == -1 || stop == -1) {
			Util.log.severe("ERROR: Token start or stop was -1");
			return;
		}

		StyleRange style = new StyleRange();
		style.start = start;
		style.length = stop - start + 1;
		style.underline = true;
		style.underlineColor = color;
		style.underlineStyle = SWT.UNDERLINE_SINGLE;
		int line = startToken.getLine();
		int offset = startToken.getCharPositionInLine();
		errors.add(new SyntaxError(type, style, message, start, stop, line, offset));
	}
}
