package com.alchitry.labs.parsers.errors;

import java.util.ArrayList;

import org.antlr.v4.runtime.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.SyntaxError;
import com.alchitry.labs.tools.ParserCache;
import com.alchitry.labs.tools.ParserCache.ParseError;

public class ErrorUtil {

	private ErrorUtil() {
	}

	private static SyntaxError underlineError(Token offendingToken, String message) {
		int start = offendingToken.getStartIndex();
		int stop = offendingToken.getStopIndex();
		StyleRange style = new StyleRange();
		style.start = start;
		style.length = stop - start + 1;
		style.underline = true;
		style.underlineColor = Theme.errorTextColor;
		style.underlineStyle = SWT.UNDERLINE_SINGLE;
		int line = offendingToken.getLine();
		int offset = offendingToken.getCharPositionInLine();
		return new SyntaxError(SyntaxError.ERROR, style, message, start, stop, line, offset);
	}

	public static ArrayList<SyntaxError> getErrors(String file) {
		ArrayList<SyntaxError> errors = new ArrayList<SyntaxError>();
		for (ParseError e : ParserCache.getErrors(file)) {
			errors.add(underlineError(e.token, e.msg));
		}
		return errors;
	}
}
