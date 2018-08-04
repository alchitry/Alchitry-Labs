package com.alchitry.labs.tools;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ParseTreeMultiWalker {

	public static void walk(List<ParseTreeListener> listeners, ParseTree t) {
		if (t instanceof ErrorNode) {
			for (ParseTreeListener listener : listeners)
				listener.visitErrorNode((ErrorNode) t);
			return;
		} else if (t instanceof TerminalNode) {
			for (ParseTreeListener listener : listeners)
				listener.visitTerminal((TerminalNode) t);
			return;
		}
		RuleNode r = (RuleNode) t;
		enterRule(listeners, r);
		int n = r.getChildCount();
		for (int i = 0; i < n; i++) {
			walk(listeners, r.getChild(i));
		}
		exitRule(listeners, r);
	}

	/**
	 * The discovery of a rule node, involves sending two events: the generic {@link ParseTreeListener#enterEveryRule} and a {@link RuleContext}-specific event. First we
	 * trigger the generic and then the rule specific. We to them in reverse order upon finishing the node.
	 */
	protected static void enterRule(List<ParseTreeListener> listeners, RuleNode r) {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		for (ParseTreeListener listener : listeners) {
			listener.enterEveryRule(ctx);
			ctx.enterRule(listener);
		}
	}

	protected static void exitRule(List<ParseTreeListener> listeners, RuleNode r) {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		for (ParseTreeListener listener : listeners) {
			ctx.exitRule(listener);
			listener.exitEveryRule(ctx);
		}
	}
}