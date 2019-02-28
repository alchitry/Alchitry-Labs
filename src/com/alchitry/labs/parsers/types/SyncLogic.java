package com.alchitry.labs.parsers.types;

import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprContext;

public interface SyncLogic {
	public String getName();

	public ExprContext getClk();

	public ExprContext getRst();

	public ConstValue getInit();
}
