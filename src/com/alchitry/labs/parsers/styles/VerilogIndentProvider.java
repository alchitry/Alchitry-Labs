package com.alchitry.labs.parsers.styles;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.swt.custom.StyledText;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.parsers.verilog.Verilog2001BaseListener;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Always_constructContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Case_statementContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Conditional_statementContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Generated_instantiationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.List_of_param_assignmentsContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.List_of_parameter_assignmentsContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.List_of_port_declarationsContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.List_of_variable_identifiersContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_declarationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_instanceContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_parameter_port_listContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Parameter_value_assignmentContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Procedural_timing_control_statementContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Seq_blockContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.StatementContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Statement_or_nullContext;
import com.alchitry.labs.style.IndentProvider;
import com.alchitry.labs.tools.ParserCache;

public class VerilogIndentProvider extends Verilog2001BaseListener implements IndentProvider {
	private int[] tabs;
	private CommonTokenStream tokens;
	private StyledText text;

	public void initWalk(StyledText text, CommonTokenStream tokens) {
		tabs = new int[text.getLineCount()];
		Arrays.fill(tabs, 0);
		this.tokens = tokens;
		this.text = text;
	}

	public void updateIndentList(StyledCodeEditor editor) {
		initWalk(editor, ParserCache.getTokens(editor.getFilePath()));
		ParserCache.walk(editor.getFilePath(), this);
	}

	public int getTabs(int line) {
		return tabs[line] * 2;
	}

	private void addIndentstoLines(int start, int end) {
		for (int i = start + 1; i <= end; i++) {
			tabs[i]++;
		}
	}

	// private void addIndents(int start, int end) {
	// for (int i = text.getLineAtOffset(start) + 1; i < text
	// .getLineAtOffset(end); i++) {
	// tabs[i]++;
	// }
	// }

	private void addIndentsToEnd(int start, int end) {
		for (int i = text.getLineAtOffset(start) + 1; i <= text.getLineAtOffset(end); i++) {
			tabs[i]++;
		}
	}

	private void addIndents(List<ParseTree> children) {
		addIndents(children, 1);
	}

	private void addIndents(List<ParseTree> children, int ignore) {
		if (children != null && children.size() > ignore + 1) {
			int a = children.get(0).getSourceInterval().a;
			int b = children.get(children.size() - ignore).getSourceInterval().b;
			int c = children.get(children.size() - ignore - 1).getSourceInterval().b;
			int start = text.getLineAtOffset(tokens.get(a).getStartIndex());
			int end = 0, end2 = 0;
			if (b >= 0)
				end = text.getLineAtOffset(tokens.get(b).getStopIndex());
			if (c >= 0)
				end2 = text.getLineAtOffset(tokens.get(c).getStopIndex());
			if (end > end2)
				addIndentstoLines(start, end - 1);
			else
				addIndentstoLines(start, end);
		}
	}
	
	private void addIndentsToEnd(List<ParseTree> children) {
		addIndentsToEnd(children, 0);
	}

	private void addIndentsToEnd(List<ParseTree> children, int ignore) {
		if (children != null && children.size() > 1 + ignore) {
			int a = children.get(0).getSourceInterval().a;
			int b = children.get(children.size() - 1 - ignore).getSourceInterval().b;
			Token start = tokens.get(a);
			Token end = tokens.get(b);
			addIndentsToEnd(start.getStartIndex(), end.getStopIndex());
		}
	}

	@Override
	public void exitModule_declaration(Module_declarationContext ctx) {
		addIndents(ctx.children);
	}

	@Override
	public void exitList_of_port_declarations(List_of_port_declarationsContext ctx) {
		addIndents(ctx.children);
	}

	@Override
	public void exitModule_parameter_port_list(Module_parameter_port_listContext ctx) {
		addIndents(ctx.children);
	}

	@Override
	public void exitList_of_parameter_assignments(List_of_parameter_assignmentsContext ctx) {
		addIndentsToEnd(ctx.children);
	}

	@Override
	public void exitList_of_param_assignments(List_of_param_assignmentsContext ctx) {
		addIndentsToEnd(ctx.children);
	}

	@Override
	public void exitList_of_variable_identifiers(List_of_variable_identifiersContext ctx) {
		addIndentsToEnd(ctx.children);
	}

	@Override
	public void exitSeq_block(Seq_blockContext ctx) {
		addIndents(ctx.children);
	}

	@Override
	public void exitConditional_statement(Conditional_statementContext ctx) {
		if (ctx.children != null) {
			int startIf = tokens.get(ctx.getSourceInterval().a).getStartIndex();
			int endIf = tokens.get(ctx.getChild(ctx.getChildCount() - 1).getSourceInterval().a).getStartIndex();

			for (int i = 0; i < ctx.children.size(); i++) {
				ParseTree child = ctx.children.get(i);
				if (child.getChildCount() == 0 && child.getText().equals("else")) {
					int endElse = endIf;
					int startElse = tokens.get(child.getSourceInterval().a).getStartIndex();
					addIndentsToEnd(startElse, endElse);
					endIf = tokens.get(ctx.getChild(i - 1).getSourceInterval().a).getStartIndex();
					addIndentsToEnd(startIf, endIf);
					return;
				}
			}
			addIndentsToEnd(startIf, endIf);
		}
	}

	@Override
	public void exitAlways_construct(Always_constructContext ctx) {
		if (ctx.getChildCount() > 1) {
			StatementContext statement = ctx.getChild(StatementContext.class, 0);
			if (statement != null) {
				Procedural_timing_control_statementContext proc = statement.getChild(Procedural_timing_control_statementContext.class, 0);
				if (proc != null) {
					Statement_or_nullContext snc = proc.getChild(Statement_or_nullContext.class, 0);
					if (snc != null) {
						statement = snc.getChild(StatementContext.class, 0);
						if (statement != null)
							if (statement.getChild(Seq_blockContext.class, 0) != null) {
								return;
							}
					}
				}
			}
		}
		addIndentsToEnd(ctx.children);
	}

	@Override
	public void exitCase_statement(Case_statementContext ctx) {
		addIndents(ctx.children);
	}

	@Override
	public void exitModule_instance(Module_instanceContext ctx){
		addIndents(ctx.children);
	}
	
	@Override
	public void exitParameter_value_assignment(Parameter_value_assignmentContext ctx){
		addIndents(ctx.children);
	}

	@Override
	public void exitGenerated_instantiation(Generated_instantiationContext ctx) {
		addIndents(ctx.children);
	}
}
