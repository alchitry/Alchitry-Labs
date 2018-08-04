package com.alchitry.labs.verilog.tools;

import java.util.HashMap;

import com.alchitry.labs.language.ConstValue;
import com.alchitry.labs.language.InstModule;
import com.alchitry.labs.language.Param;
import com.alchitry.labs.lucid.tools.ConstExprParser;
import com.alchitry.labs.lucid.tools.ConstProvider;
import com.alchitry.labs.verilog.parser.Verilog2001BaseListener;
import com.alchitry.labs.verilog.parser.Verilog2001Parser.Local_parameter_declarationContext;
import com.alchitry.labs.verilog.parser.Verilog2001Parser.Param_assignmentContext;

public class VerilogConstExprParser extends Verilog2001BaseListener implements ConstProvider {
	private HashMap<String, ConstValue> constants = new HashMap<>();

	public VerilogConstExprParser(InstModule thisModule) {
		for (Param p : thisModule.getParams())
			constants.put(p.getName(), p.getValue());
	}

	public ConstValue parseExpr(String text) {
		String ltext = text.replace("'", "");
		ConstValue cv = ConstExprParser.parseExpr(ltext, this, null);
		return cv;
	}

	@Override
	public void exitParam_assignment(Param_assignmentContext ctx) {
		if (ctx.getParent().getParent() instanceof Local_parameter_declarationContext) {
			String name = ctx.parameter_identifier().getText();
			String expr = ctx.constant_expression().getText();
			ConstValue cv = parseExpr(expr);
			constants.put(name, cv);
		}
	}

	@Override
	public ConstValue getValue(String s) {
		return constants.get(s);
	}
}
