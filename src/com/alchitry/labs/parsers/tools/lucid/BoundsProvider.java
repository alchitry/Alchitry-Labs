package com.alchitry.labs.parsers.tools.lucid;

import org.antlr.v4.runtime.tree.ParseTree;

import com.alchitry.labs.Util;

public interface BoundsProvider {
	public ArrayBounds getBounds(ParseTree node);
	
	static BoundsProvider dummyProvider = new BoundsProvider() {
		@Override
		public ArrayBounds getBounds(ParseTree node) {
			Util.log.severe("Dummy bounds provider used: " + node.getText());
			return null;
		}
	};
}
