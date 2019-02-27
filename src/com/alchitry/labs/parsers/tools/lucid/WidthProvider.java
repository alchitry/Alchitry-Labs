package com.alchitry.labs.parsers.tools.lucid;

import org.antlr.v4.runtime.ParserRuleContext;

import com.alchitry.labs.parsers.lucid.SignalWidth;

public interface WidthProvider {
	public SignalWidth getWidth(ParserRuleContext ctx);
	public SignalWidth getWidth(String signal);
	public SignalWidth checkWidthMap(String signal);
	
	static WidthProvider dummyProvider = new WidthProvider() {

		@Override
		public SignalWidth getWidth(String signal) {
			return null;
		}

		@Override
		public SignalWidth getWidth(ParserRuleContext ctx) {
			return null;
		}


		@Override
		public SignalWidth checkWidthMap(String signal) {
			return null;
		}
	};
}
