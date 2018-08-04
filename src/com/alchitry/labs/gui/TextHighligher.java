package com.alchitry.labs.gui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.StyleUtil.StyleMerger;

public class TextHighligher extends CachedStyleListner implements ModifyListener {
	private StyledText editor;
	private String highlightWord;

	public TextHighligher(StyledText e) {
		super();
		editor = e;
	}

	public void setText(String text) {
		highlightWord = text;
		update();
		editor.redraw();
	}

	protected void update() {
		final String text = editor.getText();
		lock.acquireUninterruptibly();
		new Thread(new Runnable() {
			@Override
			public void run() {
				invalidateStyles();
				styles.clear();

				if (highlightWord == null || highlightWord.isEmpty()) {
					lock.release();
					return;
				}

				int length = highlightWord.length();
				for (int index = StringUtils.indexOfIgnoreCase(text, highlightWord); index >= 0; index = StringUtils.indexOfIgnoreCase(text, highlightWord, index + length)) {
					StyleRange style = new StyleRange();
					style.background = Theme.highlightedWordColor;
					style.start = index;
					style.length = length;
					styles.add(style);
				}

				lock.release();
			}
		}).start();
	}

	@Override
	protected StyleMerger getStyleMerger() {
		return new StyleMerger() {
			@Override
			public void mergeStyles(StyleRange dest, StyleRange src) {
				Color bg = dest.background;
				StyleUtil.copy(dest, src);
				dest.background = bg;
			}
		};
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		update();
	}
}
