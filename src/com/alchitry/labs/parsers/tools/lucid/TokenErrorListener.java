package com.alchitry.labs.parsers.tools.lucid;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public interface TokenErrorListener {
	public void onTokenErrorFound(TerminalNode node, String message);
	public void onTokenWarningFound(TerminalNode node, String message);
	public void onTokenDebugFound(TerminalNode node, String message);
	
	public void onTokenErrorFound(ParserRuleContext ctx, String message);
	public void onTokenWarningFound(ParserRuleContext ctx, String message);
	public void onTokenDebugFound(ParserRuleContext ctx, String message);
}
