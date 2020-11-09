package com.alchitry.labs.parsers.errors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public interface ErrorListener {
	public void reportError(TerminalNode node, String message);
	public void reportWarning(TerminalNode node, String message);
	public void reportDebug(TerminalNode node, String message);
	
	public void reportError(ParserRuleContext ctx, String message);
	public void reportWarning(ParserRuleContext ctx, String message);
	public void reportDebug(ParserRuleContext ctx, String message);
}
