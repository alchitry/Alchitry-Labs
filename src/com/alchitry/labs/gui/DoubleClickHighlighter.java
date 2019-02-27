package com.alchitry.labs.gui;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.alchitry.labs.parsers.lucid.parser.LucidLexer;
import com.alchitry.labs.parsers.verilog.parser.Verilog2001Lexer;
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.StyleUtil.StyleMerger;

public class DoubleClickHighlighter extends CachedStyleListner implements Listener, LineStyleListener{
	StyledText editor;
	String highlightWord;
	private boolean isLucid, isVerilog;
	
	public DoubleClickHighlighter(StyledText e, boolean lucid, boolean verilog) {
		editor = e;
		this.isLucid = lucid;
		this.isVerilog = verilog;
	}
	
	private void update(final String text) {
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

				int lineLen = text.length();
				int length = highlightWord.length();
				styles.clear();
				for (int index = text.indexOf(highlightWord); index >= 0; index = text.indexOf(highlightWord, index + length)) {
					if (index > 0) {
						char before = text.charAt(index - 1);
						if (Character.isLetterOrDigit(before) || before == '_')
							continue;
					}
					if (index + length < lineLen) {
						char after = text.charAt(index + length);
						if (Character.isLetterOrDigit(after) || after == '_')
							continue;
					}
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
	
	public String getWord() {
		return highlightWord;
	}
	
	public void clearWord() {
		highlightWord = null;
		update(editor.getText());
	}

	@Override
	public void handleEvent(Event event) {
		int clickCount = event.count;

		if ((clickCount & 1) == 0) {
			if (editor.getSelectionText().trim().equals(""))
				return;

			Point selection = editor.getSelection();
			int start = selection.x;
			int lineNum = editor.getLineAtOffset(start);
			String line = editor.getLine(lineNum);
			int offset = editor.getOffsetAtLine(lineNum);
			start -= editor.getOffsetAtLine(lineNum);

			Lexer lexer = null;

			CharStream stream = CharStreams.fromString(line);

			if (isLucid)
				lexer = new LucidLexer(stream);
			else if (isVerilog)
				lexer = new Verilog2001Lexer(stream);

			if (lexer != null) {
				lexer.removeErrorListeners();
				Token t;
				while ((t = lexer.nextToken()).getType() != Lexer.EOF) {
					if (t.getStartIndex() <= start && start <= t.getStopIndex()) {
						editor.setSelection(new Point(t.getStartIndex() + offset, t.getStopIndex() + offset + 1));
						break;
					}
				}
			}

			highlightWord = editor.getSelectionText();
		} else {
			highlightWord = null;
		}
		update(editor.getText());
		editor.redraw();
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
}
