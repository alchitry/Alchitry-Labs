package com.alchitry.labs.parsers.styles;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.SWT;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.parsers.lucid.parser.LucidLexer;

public class LucidStyleProvider extends StyleProvider {
	private int numBlockComments = -1;

	private Matcher operatorRgx;

	public LucidStyleProvider(StyledCodeEditor editor) {
		super(editor);
		operatorRgx = Pattern.compile("([*!~+#\\-/:@|&{}?^=><\\]\\[,();]+)|(c\\{|x\\{)").matcher("");
		this.editor = editor;
	}


	@Override
	protected void generateStyles(String editorText) {
		CharStream input = CharStreams.fromString(editorText);
		
		LucidLexer lexer = new LucidLexer(input);
		lexer.removeErrorListeners();
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		tokens.fill();

		for (Token t : tokens.getTokens()) {
			switch (t.getType()) {
			case LucidLexer.SIGNED:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor);
				break;
			case LucidLexer.COMMENT:
			case LucidLexer.BLOCK_COMMENT:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.commentColor);
				break;
			case LucidLexer.HEX:
			case LucidLexer.BIN:
			case LucidLexer.DEC:
			case LucidLexer.INT:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.valueColor);
				break;
			case LucidLexer.STRING:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.stringColor);
				break;
			case LucidLexer.CONST_ID:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.constColor);
				break;
			case LucidLexer.SPACE_ID:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.nameSpaceColor);
				break;
			case LucidLexer.FUNCTION_ID:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.functionColor);
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
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.varTypeColor);
					break;
				case "always":
				case "if":
				case "for":
				case "else":
				case "case":
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor, SWT.BOLD);
					break;
				case "module":
				case "global":
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.moduleColor, SWT.BOLD);
					break;
				case "default":
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor);
					break;
				default:
					if (operatorRgx.reset(text).matches())
						addStyle(t.getStartIndex(), t.getStopIndex(), Theme.operatorColor);
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
	}
}
