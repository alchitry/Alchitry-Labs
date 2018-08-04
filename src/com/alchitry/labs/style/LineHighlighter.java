package com.alchitry.labs.style;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;

import com.alchitry.labs.gui.StyledCodeEditor;

public class LineHighlighter implements CaretListener, ExtendedModifyListener {

	private StyledCodeEditor styledText;
	private int curActiveLine;
	private int numDigits;

	public LineHighlighter(StyledCodeEditor styledText) {
		this.styledText = styledText;
		curActiveLine = 0;
		numDigits = 3;
	}

	@Override
	public void caretMoved(CaretEvent event) {
		int activeLine = styledText.getLineAtOffset(event.caretOffset);
 
		if (curActiveLine != activeLine) {
			int digits = 3;
			if (styledText.getLineCount() > 999)
				digits = (int) (Math
						.floor(Math.log10(styledText.getLineCount())) + 1);
			int width = digits * 12;
			styledText.redraw(0, styledText.getLinePixel(activeLine), width,
					styledText.getLineHeight(), true);
			styledText.redraw(0, styledText.getLinePixel(curActiveLine), width,
					styledText.getLineHeight(), true);
			curActiveLine = activeLine;
		}
	}

	@Override
	public void modifyText(ExtendedModifyEvent event) {
		int digits = 3;
		int lineCount = styledText.getLineCount();
		if (lineCount > 999)
			digits = (int) (Math.floor(Math.log10(styledText.getLineCount())) + 1);
		if (numDigits != digits) {
			numDigits = digits;
			styledText.redraw();
			return;
		}

		int startLine = styledText.getLineAtOffset(event.start);
		int endLine = styledText
				.getLineAtOffset(event.start + event.length);
		if (startLine != endLine || event.replacedText.contains(System.lineSeparator())) {
			styledText.redraw(0, styledText.getLinePixel(startLine), digits * 12,
					styledText.getLineHeight() * (lineCount-startLine), true);
		}
	}
}
