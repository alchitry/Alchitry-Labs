package com.alchitry.labs.lucid;

import com.alchitry.labs.language.ConstValue;
import com.alchitry.labs.lucid.parser.LucidParser.ExprContext;

public interface SyncLogic {
	public String getName();

	public ExprContext getClk();

	public ExprContext getRst();

	public ConstValue getInit();
}
