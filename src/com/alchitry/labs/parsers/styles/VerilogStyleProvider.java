package com.alchitry.labs.parsers.styles;

import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.SWT;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.parsers.verilog.parser.Verilog2001Lexer;

public class VerilogStyleProvider extends StyleProvider {
	protected int numBlockComments = -1;

	public VerilogStyleProvider(StyledCodeEditor editor) {
		super(editor);
	}

	@Override
	protected void generateStyles(String editorText) {
		CharStream input = CharStreams.fromString(editorText);
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
					switch (t.getText()) {
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
	}
}
