package com.alchitry.labs.lucid.style;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.gui.CachedStyleListner;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.lucid.parser.LucidLexer;
import com.alchitry.labs.style.StyleUtil.StyleMerger;

public class LucidStyleProvider extends CachedStyleListner implements LineStyleListener, ModifyListener {
	private int numBlockComments = -1;
	private StyledText editor;

	private Matcher operatorRgx;

	public LucidStyleProvider(StyledText editor) {
		operatorRgx = Pattern.compile("([*!~+#\\-/:@|&{}?^=><\\]\\[,();]+)|(c\\{|x\\{)").matcher("");
		this.editor = editor;
	}

	private void addStyle(int start, int stop, Color foreground, int offset) {
		addStyle(start, stop, foreground, SWT.NONE, offset);
	}

	private void addStyle(int start, int stop, Color foreground, int style, int offset) {
		int length = stop - start + 1;
		StyleRange styleRange = new StyleRange();
		styleRange.start = start + offset;
		styleRange.length = length;
		styleRange.foreground = foreground;
		styleRange.fontStyle = style;
		styles.add(styleRange);
	}

	private void redraw() {
		editor.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				editor.redraw();
			}
		});
	}
	
	@Override
	public void lineGetStyle(LineStyleEvent event) {
		super.lineGetStyle(event);
	}

	private void updateStyles(String editorText) {
		invalidateStyles();
		styles.clear();

		ANTLRInputStream input = new ANTLRInputStream(editorText);
		LucidLexer lexer = new LucidLexer(input);
		lexer.removeErrorListeners();
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		tokens.fill();

		// int offset = styledText.getOffsetAtLine(line);
		int offset = 0;

		for (Token t : tokens.getTokens()) {
			switch (t.getType()) {
			case LucidLexer.SIGNED:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor, offset);
				break;
			case LucidLexer.COMMENT:
			case LucidLexer.BLOCK_COMMENT:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.commentColor, offset);
				break;
			case LucidLexer.HEX:
			case LucidLexer.BIN:
			case LucidLexer.DEC:
			case LucidLexer.INT:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.valueColor, offset);
				break;
			case LucidLexer.STRING:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.stringColor, offset);
				break;
			case LucidLexer.CONST_ID:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.constColor, offset);
				break;
			case LucidLexer.SPACE_ID:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.nameSpaceColor, offset);
				break;
			case LucidLexer.FUNCTION_ID:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.functionColor, offset);
				break;
			default:
				String text = t.getText();

				switch (text) {
				case "input":
				case "output":
				case "inout":
				case "sig":
				case "dff":
				case "fsm":
				case "const":
				case "var":
				case "struct":
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.varTypeColor, offset);
					break;
				case "always":
				case "if":
				case "for":
				case "else":
				case "case":
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor, SWT.BOLD, offset);
					break;
				case "module":
				case "global":
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.moduleColor, SWT.BOLD, offset);
					break;
				case "default":
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor, offset);
					break;
				default:
					if (operatorRgx.reset(text).matches())
						addStyle(t.getStartIndex(), t.getStopIndex(), Theme.operatorColor, offset);
					break;

				}
			}
		}

		// if a block comment is created or destroyed, we need to refresh everything
		List<Token> blockComments = tokens.getTokens(0, tokens.size() - 1, LucidLexer.BLOCK_COMMENT);
		if ((blockComments != null && blockComments.size() != numBlockComments) || (blockComments == null && numBlockComments != 0)) {
			numBlockComments = blockComments == null ? 0 : blockComments.size();
			redraw();
		}
		lock.release();
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
}
