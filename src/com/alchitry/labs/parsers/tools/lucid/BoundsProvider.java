package com.alchitry.labs.parsers.tools.lucid;

import com.alchitry.labs.Util;
import org.antlr.v4.runtime.tree.ParseTree;

public interface BoundsProvider {
	public ArrayBounds getBounds(ParseTree node);
	
	static BoundsProvider dummyProvider = new BoundsProvider() {
		@Override
		public ArrayBounds getBounds(ParseTree node) {
			Util.logger.severe("Dummy bounds provider used: " + node.getText());
			return null;
		}
	};
}
