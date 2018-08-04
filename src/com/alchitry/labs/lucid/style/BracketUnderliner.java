package com.alchitry.labs.lucid.style;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.gui.CachedStyleListner;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.StyleUtil.StyleMerger;

public class BracketUnderliner extends CachedStyleListner implements CaretListener {
	private StyledText editor;
	private static final String BRACKETS = "{}()[]";
	private int caretPosition = -1;
	private int oldStart, oldStop;

	public BracketUnderliner(StyledText e) {
		super();
		editor = e;
		oldStart = oldStop = -1;
	}
	
	@Override 
	public void lineGetStyle(LineStyleEvent event) {
		super.lineGetStyle(event);
	}

	private void update() {
		final String text = editor.getText();
		final int offset = caretPosition;
		lock.acquireUninterruptibly();
		new Thread(new Runnable() {
			@Override
			public void run() {
				invalidateStyles();
				styles.clear();

				int offidx = offset - 1;
				int bidx = -1;

				if (offidx >= 0 && offidx < text.length()) {
					bidx = BRACKETS.indexOf(text.charAt(offidx));
				}

				if (bidx < 0) {
					offidx = offset;
					if (offidx < text.length()) {
						bidx = BRACKETS.indexOf(text.charAt(offidx));
					}
				}

				int end = -1;

				if (bidx >= 0) {
					boolean forward = bidx % 2 == 0;
					int pidx = forward ? bidx + 1 : bidx - 1;

					StyleRange style = new StyleRange();
					style.background = Theme.highlightedWordColor;
					style.start = offidx;
					style.length = 1;
					styles.add(style);

					end = findMatching(text, BRACKETS.charAt(bidx), BRACKETS.charAt(pidx), offidx, forward);
					if (end >= 0) {
						style = new StyleRange();
						style.background = Theme.highlightedWordColor;
						style.start = end;
						style.length = 1;
						styles.add(style);
					}
				} else {
					offidx = -1;
				}

				if (oldStart != offidx || oldStop != end) {
					final int a[] = new int[4];
					a[0] = oldStart;
					a[1] = oldStop;
					a[2] = offidx;
					a[3] = end;
					editor.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							for (int i = 0; i < a.length; i++) {
								if (a[i] >= 0 && a[i] < editor.getCharCount())
									editor.redrawRange(a[i], 1, true);
							}
						}
					});
					oldStart = offidx;
					oldStop = end;
				}

				lock.release();
			}
		}).start();
	}

	private int findMatching(String text, char s, char e, int start, boolean forward) {
		int idx = start;
		int length = text.length();
		int diff = forward ? 1 : -1;
		int bracketCount = 0;
		for (idx = start; idx < length && idx >= 0; idx += diff) {
			char c = text.charAt(idx);
			if (c == s)
				bracketCount++;
			if (c == e)
				bracketCount--;
			if (bracketCount == 0)
				return idx;
		}
		return -1;
	}

	@Override
	protected StyleMerger getStyleMerger() {
		return new StyleMerger() {
			@Override
			public void mergeStyles(StyleRange dest, StyleRange src) {
				Color color = dest.background;
				StyleUtil.copy(dest, src);
				dest.background = color;
			}
		};
	}

	@Override
	public void caretMoved(CaretEvent event) {
		if (event.caretOffset != caretPosition) {
			caretPosition = event.caretOffset;
			update();
		}
	}
}
