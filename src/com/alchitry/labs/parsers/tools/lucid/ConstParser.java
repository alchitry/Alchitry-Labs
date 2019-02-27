package com.alchitry.labs.parsers.tools.lucid;

import java.util.HashMap;

import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.lucid.Constant;
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Const_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ModuleContext;

public class ConstParser extends LucidBaseListener implements ConstProvider {
	private HashMap<String, Constant> constMap;
	private ConstExprParser constParser;

	public ConstParser(ConstExprParser cep) {
		constMap = new HashMap<String, Constant>();
		constParser = cep;
	}

	public Constant getConstant(String name) {
		return constMap.get(name);
	}

	public ConstValue getValue(String name) {
		Constant c = getConstant(name);
		if (c == null)
			return null;
		return c.getValue();
	}

	@Override
	public void enterModule(ModuleContext ctx) {
		constMap.clear();
	}

	@Override
	public void exitConst_dec(Const_decContext ctx) {
		if (ctx.name() != null && ctx.expr() != null) {
			String name = ctx.name().getText();
			ConstValue cv = constParser.getValue(ctx.expr());
			Constant c = new Constant(name, cv);
			constMap.put(name, c);
		}
	}

	// @Override
	// public void exitParam_dec(Param_decContext ctx) {
	// if (ctx.param_name() != null && ctx.param_name().name() != null) {
	// if (ctx.param_name().name().CONST_ID() == null)
	// errorListener.onTokenErrorFound(ctx.param_name().name(), String.format(ErrorStrings.NAME_NOT_CONST, ctx.param_name().name().getText()));
	// String name = ctx.param_name().name().toString();
	// ConstValue cv = constParser.getValue(ctx.param_name().expr());
	// if (cv == null)
	// errorListener.onTokenErrorFound(ctx.param_name().expr(), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.param_name().expr().getText()));
	// Constant c = new Constant(name, cv);
	// constMap.put(name, c);
	// }
	// }
}
