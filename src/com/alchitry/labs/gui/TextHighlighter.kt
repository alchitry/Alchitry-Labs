package com.alchitry.labs.gui;

import com.alchitry.labs.gui.util.Search;
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.StyleUtil.StyleMerger;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;

import java.util.List;
import java.util.regex.MatchResult;

public class TextHighligher extends CachedStyleListner implements ModifyListener {
	private StyledText editor;
	private List<MatchResult> matchList;

	public TextHighligher(StyledText e) {
		super();
		editor = e;
	}
	
	public void setText(String text) {
		setMatches(new Search(editor.getText(), text, false, false).getMatches());
	}

	public void setMatches(List<MatchResult> matches) {
		matchList = matches;
		update();
		editor.redraw();
	}

	protected void update() {
		lock.acquireUninterruptibly();
		new Thread(new Runnable() {
			@Override
			public void run() {
				invalidateStyles();
				styles.clear();

				if (matchList == null) {
					lock.release();
					return;
				}

				for (MatchResult m : matchList) {
					StyleRange style = new StyleRange();
					style.background = Theme.highlightedWordColor;
					style.start = m.start();
					style.length = m.end() - m.start();
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
