package com.alchitry.labs.parsers.styles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.gui.CachedStyleListner;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.style.StyleUtil.StyleMerger;

public abstract class StyleProvider extends CachedStyleListner implements ModifyListener {
	protected StyledText editor;

	public StyleProvider(StyledCodeEditor editor) {
		super();
		this.editor = editor;
	}

	@Override
	protected StyleMerger getStyleMerger() {
		return new StyleMerger() {
			@Override
			public void mergeStyles(StyleRange dest, StyleRange src) {
				dest.underline = src.underline;
				dest.underlineColor = src.underlineColor;
				dest.underlineStyle = src.underlineStyle;
			}
		};
	}

	protected void redraw() {
		editor.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				editor.redraw();
			}
		});
	}

	abstract protected void generateStyles(String editorText);

	private void updateStyles(String editorText) {
		invalidateStyles();
		styles.clear();

		generateStyles(editorText);

		lock.release();
	}

	protected void addStyle(int start, int stop, Color foreground) {
		addStyle(start, stop, foreground, SWT.NONE);
	}



	protected void addStyle(int start, int stop, Color foreground, int style) {
		int length = stop - start + 1;
		StyleRange styleRange = new StyleRange();
		styleRange.start = start;
		styleRange.length = length;
		styleRange.foreground = foreground;
		styleRange.fontStyle = style;
		styles.add(styleRange);
	}

	@Override
	public void modifyText(ModifyEvent e) {
		final String text = editor.getText();
		lock.acquireUninterruptibly();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				updateStyles(text);
			}
		});

		t.start();
	}
}
