package com.alchitry.labs.gui;

import org.eclipse.swt.graphics.Color;

public class Style {
	public final String[] regex;
	public final Color color;
	public final int fontStyle;
	
	public Style(final String[] regex, final Color color){
		this(regex, color, 0);
	}
	
	public Style(final String[] regex, final Color color, final int style){
		this.regex = regex;
		this.color = color;
		this.fontStyle = style;
	}
}
