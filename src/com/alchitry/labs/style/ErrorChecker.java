package com.alchitry.labs.style;

import org.eclipse.swt.graphics.Color;

public interface ErrorChecker {
	public SyntaxError getErrorAtOffset(int offset);
	public boolean hasErrors();
	public boolean hasWarnings();
	public boolean hasSyntaxErrors();
	public void updateErrors();
	public Color getLineColor(int line);
}
