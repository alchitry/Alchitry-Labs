package com.alchitry.labs.verilog.style;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

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

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.StyleUtil.StyleMerger;
import com.alchitry.labs.verilog.parser.Verilog2001Lexer;

public class VerilogStyleProvider implements LineStyleListener, ModifyListener {
	private ArrayList<StyleRange> styles = new ArrayList<StyleRange>();
	private int numBlockComments = -1;
	private StyledText editor;
	
	private Semaphore lock = new Semaphore(1);
	private StyleRange[] cachedStyles;
	private boolean newStyles = false;
	
	public VerilogStyleProvider(StyledCodeEditor editor) {
		this.editor = editor;
	}
	
	@Override
	public void lineGetStyle(LineStyleEvent event) {
		lock.acquireUninterruptibly();
		if (newStyles || ((event.data instanceof Boolean) && (Boolean) event.data)) {
			newStyles = false;
			List<StyleRange> styleCopy = StyleUtil.mergeStyles(styles, event.styles, styleMerger);
			lock.release();
			
			cachedStyles = styleCopy.toArray(new StyleRange[styleCopy.size()]);
			event.data = Boolean.valueOf(true);
		} else {
			lock.release();
			event.data = Boolean.valueOf(false);
		}
		event.styles = cachedStyles;
	}
	
	private StyleMerger styleMerger = new StyleMerger() {
		@Override
		public void mergeStyles(StyleRange dest, StyleRange src) {
			dest.underline = src.underline;
			dest.underlineColor = src.underlineColor;
			dest.underlineStyle = src.underlineStyle;
		}
	};
	
	private void redraw() {
		editor.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				editor.redraw();
			}
		});
	}


	private void updateStyles(String editorText) {
		newStyles = true;
		styles.clear();
		
		ANTLRInputStream input = new ANTLRInputStream(editorText);
		Verilog2001Lexer lexer = new Verilog2001Lexer(input);
		lexer.removeErrorListeners();
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		tokens.fill();

		for (Token t : tokens.getTokens()) {
			switch (t.getType()) {
			case Verilog2001Lexer.One_line_comment:
			case Verilog2001Lexer.Block_comment:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.commentColor);
				break;
			case Verilog2001Lexer.Real_number:
			case Verilog2001Lexer.Hex_number:
			case Verilog2001Lexer.Binary_number:
			case Verilog2001Lexer.Octal_number:
			case Verilog2001Lexer.Decimal_number:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.valueColor);
				break;
			case Verilog2001Lexer.String:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.stringColor);
				break;
			default:
				if (t.getText().matches("[*!~+#\\-/:@|&{}?^=><\\]\\[,();]+")) {
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.operatorColor);
				} else {
					switch (t.getText()){
					case "always":
					case "begin":
					case "end":
					case "assign":
					case "if":
					case "for":
					case "else":
					case "case":
					case "endcase":
					case "casex":
					case "posedge":
					case "negedge":
					case "generate":
					case "endgenerate":
						addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor, SWT.BOLD);
						break;
					case "input":
					case "inout":
					case "output":
					case "reg":
					case "wire":
					case "localparam":
					case "parameter":
					case "integer":
					case "genvar":
						addStyle(t.getStartIndex(), t.getStopIndex(), Theme.varTypeColor);
						break;
					case "module":
					case "endmodule":
						addStyle(t.getStartIndex(), t.getStopIndex(), Theme.moduleColor, SWT.BOLD);
						break;
					}
				}
			}
		}

		// if a block comment is created or destroyed, we need to refresh everything
		List<Token> blockComments = tokens.getTokens(0, tokens.size() - 1, Verilog2001Lexer.Block_comment);
		if ((blockComments != null && blockComments.size() != numBlockComments) || (blockComments == null && numBlockComments != 0)) {
			numBlockComments = blockComments == null ? 0 : blockComments.size();
			redraw();
		}
		
		lock.release();
	}

	private void addStyle(int start, int stop, Color foreground) {
		addStyle(start, stop, foreground, SWT.NONE);
	}

	private void addStyle(int start, int stop, Color foreground, int style) {
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
