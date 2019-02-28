package com.alchitry.labs.parsers.errors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public class DummyErrorListener implements ErrorListener {

	@Override
	public void reportError(TerminalNode node, String message) {
	}

	@Override
	public void reportWarning(TerminalNode node, String message) {
	}

	@Override
	public void reportDebug(TerminalNode node, String message) {
	}

	@Override
	public void reportError(ParserRuleContext ctx, String message) {
	}

	@Override
	public void reportWarning(ParserRuleContext ctx, String message) {
	}

	@Override
	public void reportDebug(ParserRuleContext ctx, String message) {
	}

}
