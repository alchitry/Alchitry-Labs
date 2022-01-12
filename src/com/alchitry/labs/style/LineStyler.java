package com.alchitry.labs.style;

import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;

public class LineStyler implements LineStyleListener, ModifyListener {

	private StyledCodeEditor styledText;

	public LineStyler(StyledCodeEditor text) {
		styledText = text;
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		// Set the line number
		int activeLine = styledText.getLineAtOffset(styledText.getCaretOffset());
		int currentLine = styledText.getLineAtOffset(event.lineOffset);
		event.bulletIndex = currentLine;

		int width = 36;
		if (styledText.getLineCount() > 999)
			width = (int) ((Math.floor(Math.log10(styledText.getLineCount())) + 1) * 12);

		// Set the style, 12 pixels wide for each digit
		StyleRange style = new StyleRange();
		style.metrics = new GlyphMetrics(0, 0, width);

		if (activeLine == currentLine) {
			style.background = Theme.highlightedLineColor;
		}

		style.foreground = Theme.bulletTextColor;
		
		Color eColor = styledText.getLineColor(currentLine+1);
		if (eColor != null)
			style.foreground = eColor;

		// Create and set the bullet
		event.bullet = new Bullet(ST.BULLET_NUMBER, style);
	}

	@Override
	public void modifyText(ModifyEvent e) {
	}

}
