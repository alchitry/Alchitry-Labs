package com.alchitry.labs.parsers.styles;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.UndoRedo;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentBaseListener;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentLexer;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.AlwaysBlockContext;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.Always_lineContext;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.ElemContext;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.Else_blockContext;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.IndentContext;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.ModuleContext;
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.SingleBlockContext;
import com.alchitry.labs.style.IndentProvider;

public class LucidNewLineIndenter extends LucidIndentBaseListener implements VerifyListener, ExtendedModifyListener, IndentProvider {

	private StyledCodeEditor editor;
	private CommonTokenStream tokens;
	private int[] tabs = new int[10];
	private int[] lineOffsets = new int[10];
	private String text;
	private UndoRedo undo;

	public LucidNewLineIndenter(StyledCodeEditor e, UndoRedo undo) {
		editor = e;
		this.undo = undo;
	}

	private void updateLineOffsets(String text) {
		int pos = 0;
		int line = 0;
		lineOffsets[line++] = pos;
		while ((pos = text.indexOf("\n", pos) + 1) != 0) {
			lineOffsets[line++] = pos - 1;
		}
		lineOffsets[line] = Integer.MAX_VALUE;
	}

	private int getLineAtOffset(int offset) {
		int line = 1;
		while (lineOffsets[line++] < offset)
			;
		return line - 2;
	}

	private void updateIndents(String text) {
		updateLineOffsets(text);
		Arrays.fill(tabs, 0);
		this.text = text;
		CharStream charStream = CharStreams.fromString(text);
		LucidIndentLexer lexer = new LucidIndentLexer(charStream);
		tokens = new CommonTokenStream(lexer);
		LucidIndentParser parser = new LucidIndentParser(tokens);
		parser.addParseListener(this);
		parser.removeErrorListeners(); // don't print errors
		parser.lucid();
	}

	private void addIndentsToLines(int start, int end) {
		addIndentsToLines(start, end, 2);
	}

	private void addIndentsToLines(int start, int end, int num) {
		for (int i = start + 1; i <= end; i++) {
			tabs[i] += num;
		}
	}

	private void addIndents(List<ParseTree> children) {
		addIndents(children, 0);
	}

	private void addIndents(List<ParseTree> children, int exclude) {
		if (children != null && children.size() > 2 + exclude) {
			int a = children.get(0).getSourceInterval().a;
			int b = Math.max(children.get(children.size() - 1 - exclude).getSourceInterval().a, children.get(children.size() - 1 - exclude).getSourceInterval().b);
			int c = Math.max(children.get(children.size() - 2 - exclude).getSourceInterval().a, children.get(children.size() - 2 - exclude).getSourceInterval().b);
			int start = tokens.get(a).getLine()-1;
			int end = tokens.get(b).getLine()-1;
			int end2 = end;
			if (c >= 0)
				end2 = tokens.get(c).getLine()-1;
			if (end > end2)
				addIndentsToLines(start, end - 1);
			else
				addIndentsToLines(start, end);
		}
	}

	private void addIndentsToEnd(List<ParseTree> children) {
		addIndentsToEnd(children, 0);
	}

	private void addIndentsToEnd(List<ParseTree> children, int exclude) {
		if (children != null && children.size() > 1 + exclude) {
			int a = children.get(0).getSourceInterval().a;
			int b = children.get(children.size() - 1 - exclude).getSourceInterval().b;
			Token start = tokens.get(a);
			Token end = tokens.get(b);
			addIndentsToLines(start.getLine()-1, end.getLine()-1);
		}
	}

	private void indentComment(TerminalNode node) {
		int a = node.getSourceInterval().a;
		int b = node.getSourceInterval().b;
		Token start = tokens.get(a);
		Token end = tokens.get(b);
		int endIdx = end.getStopIndex() - 2; // skip * and /
		while (Character.isWhitespace(text.charAt(endIdx--)))
			;
		addIndentsToLines(start.getLine()-1, getLineAtOffset(endIdx), 3);
	}

	@Override
	public void exitModule(ModuleContext ctx) {
		Token start = tokens.get(ctx.children.get(0).getSourceInterval().a);
		for (IndentContext ic : ctx.indent()) {
			if (ic.getChild(0).getText().equals("{")) {
				Token end = tokens.get(ic.getChild(0).getSourceInterval().b);
				addIndentsToLines(start.getLine()-1, end.getLine()-1);
				break;
			}
		}
	}

	@Override
	public void exitAlways_line(Always_lineContext ctx) {
		int exclude = 0;
		if (ctx.else_block() != null)
			exclude = 1;
		if (ctx.block() != null)
			if (ctx.block().getClass() == SingleBlockContext.class || ctx.block().getClass() == AlwaysBlockContext.class)
				addIndentsToEnd(ctx.children, exclude);
			else
				addIndents(ctx.block().children);
		else
			addIndents(ctx.children);
	}

	@Override
	public void exitElse_block(Else_blockContext ctx) {
		if (ctx.block().getClass() == AlwaysBlockContext.class && ((AlwaysBlockContext) ctx.block()).always_line().getChild(0).getText().equals("if")) {
			return; // if is else-if don't indent else
		}

		if (ctx.block() != null)
			if (ctx.block().getClass() == SingleBlockContext.class || ctx.block().getClass() == AlwaysBlockContext.class)
				addIndentsToEnd(ctx.children);
			else
				addIndents(ctx.block().children);
		else
			addIndents(ctx.children);
	}

	@Override
	public void exitIndent(IndentContext ctx) {
		if (ctx.always_line() != null)
			return;
		if (ctx.block() != null)
			if (ctx.block().getClass() == SingleBlockContext.class || ctx.block().getClass() == AlwaysBlockContext.class)
				addIndentsToEnd(ctx.children);
			else
				addIndents(ctx.block().children);
		else if (ctx.BLOCK_COMMENT() != null)
			indentComment(ctx.BLOCK_COMMENT());
		else
			addIndents(ctx.children);

	}

	@Override
	public void exitElem(ElemContext ctx) {
		addIndentsToEnd(ctx.children);
	}

	private void resizeBuffers(int lines) {
		if (tabs.length <= lines) {
			tabs = new int[lines * 2];
			lineOffsets = new int[lines * 2];
		}
	}

	@Override
	public void verifyText(VerifyEvent e) {
		if (e.text.equals("\n") || e.text.equals("\r\n")) {
			resizeBuffers(editor.getLineCount() + 1);
			StringBuilder sb = new StringBuilder(editor.getText());
			sb.replace(e.start, e.end, e.text + "l;");
			updateIndents(sb.toString());

			int indents = tabs[getLineAtOffset(e.end + 1)];

			StringBuilder newText = new StringBuilder(e.text);
			for (int i = 0; i < indents; i++) {
				newText.append(' ');
			}
			e.text = newText.toString();
		}
	}

	private int countSpaces(String line) {
		int ct = 0;
		int idx = 0;
		for (idx = 0; idx < line.length(); idx++) {
			char c = line.charAt(idx);
			if (c == ' ')
				ct++;
			else if (c == '\t')
				ct += 2;
			else
				break;
		}
		return ct;
	}

	private void unindent(ExtendedModifyEvent e) {
		resizeBuffers(editor.getLineCount());
		String text = editor.getText();
		updateIndents(text);

		int lineNum = getLineAtOffset(e.start);
		int indents = tabs[lineNum];
		String line = editor.getLine(lineNum);

		int curIndents = countSpaces(line);

		if (indents != curIndents) {
			String newText = new String(new char[indents]).replace("\0", " "); // create string of all spaces
			editor.replaceTextRange(editor.getOffsetAtLine(lineNum), curIndents, newText);
		}
	}

	@Override
	public void updateIndentList(StyledCodeEditor editor) {
		resizeBuffers(editor.getLineCount() + 1);
		updateIndents(editor.getText());
	}

	@Override
	public int getTabs(int lineNum) {
		return tabs[lineNum];
	}

	@Override
	public void modifyText(ExtendedModifyEvent e) {
		if (e.length == 1 && !undo.isEditing()) {
			String editedText = editor.getText(e.start, e.start);
			if (editedText.matches("[}\\])\\/]")) {
				String line = editor.getLine(editor.getLineAtOffset(e.start));
				if (line.trim().matches("([}\\])]|\\*\\/)")) // only indent an empty line
					unindent(e);
			} else if (editedText.equals(":")) {
				String line = editor.getLine(editor.getLineAtOffset(e.start)).trim();
				for (int i = line.length() - 2; i >= 0; i--)
					if (!Character.isLetterOrDigit(line.charAt(i)) && !(line.charAt(i) == '_') && !(line.charAt(i) == '.'))
						return;
				unindent(e);
			}
		}
	}

}
