package com.alchitry.labs.parsers.styles;

import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.SWT;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsLexer;

public class AlchitryConstraintStyleProvider extends StyleProvider {
	protected int numBlockComments = -1;

	public AlchitryConstraintStyleProvider(StyledCodeEditor editor) {
		super(editor);
	}

	@Override
	protected void generateStyles(String editorText) {
		CharStream input = CharStreams.fromString(editorText);
		AlchitryConstraintsLexer lexer = new AlchitryConstraintsLexer(input);
		lexer.removeErrorListeners();
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		tokens.fill();

		for (Token t : tokens.getTokens()) {
			switch (t.getType()) {
			case AlchitryConstraintsLexer.COMMENT:
			case AlchitryConstraintsLexer.BLOCK_COMMENT:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.commentColor);
				break;
			case AlchitryConstraintsLexer.REAL:
			case AlchitryConstraintsLexer.INT:
			case AlchitryConstraintsLexer.FREQ_UNIT:
				addStyle(t.getStartIndex(), t.getStopIndex(), Theme.valueColor);
				break;
			default:
				if (t.getText().matches("[\\]\\[;]+")) {
					addStyle(t.getStartIndex(), t.getStopIndex(), Theme.operatorColor);
				} else {
					switch (t.getText()) {
					case "pin":
					case "clock":
						addStyle(t.getStartIndex(), t.getStopIndex(), Theme.keyWordColor, SWT.BOLD);
						break;
					case "pullup":
					case "pulldown":
						addStyle(t.getStartIndex(), t.getStopIndex(), Theme.varTypeColor);
						break;
					}
				}
			}
		}

		// if a block comment is created or destroyed, we need to refresh everything
		List<Token> blockComments = tokens.getTokens(0, tokens.size() - 1, AlchitryConstraintsLexer.BLOCK_COMMENT);
		if ((blockComments != null && blockComments.size() != numBlockComments) || (blockComments == null && numBlockComments != 0)) {
			numBlockComments = blockComments == null ? 0 : blockComments.size();
			redraw();
		}
	}
}
