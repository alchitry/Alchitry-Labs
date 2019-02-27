package com.alchitry.labs.parsers.tools.lucid;

import java.util.HashMap;

import com.alchitry.labs.Util;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.Param;
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_nameContext;

public class ParamsParser extends LucidBaseListener implements ConstProvider {
	private HashMap<String, Param> paramMap;

	private ConstExprParser constParser;

	private InstModule thisModule;

	public ParamsParser(ConstExprParser cep, InstModule thisModule) {
		paramMap = new HashMap<String, Param>();
		constParser = cep;
		this.thisModule = thisModule;
	}

	public void reset() {
		paramMap.clear();
	}

	public void setParam(String name, Param param) {
		paramMap.put(name, param);
	}

	public ConstValue getValue(String name) {
		Param p = getParam(name);
		if (p == null)
			return null;
		return p.getValue();
	}

	public Param getParam(String name) {
		return paramMap.get(name);
	}

	@Override
	public void exitParam_name(Param_nameContext ctx) {
		if (ctx.name() != null) {
			String name = ctx.name().getText();
			String defValue = null;
			ConstValue value = null;
			if (thisModule != null) {
				Param p = Util.getByName(thisModule.getParams(), name);
				if (p != null)
					value = p.getValue();
			}
			if (ctx.expr() != null) {
				defValue = ctx.expr().getText();
				if (value == null)
					value = constParser.getValue(ctx.expr());
			}
			Param p = new Param(name, defValue, value);
			if (!paramMap.containsKey(name))
				paramMap.put(name, p);

		}
	}

}
