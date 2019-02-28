package com.alchitry.labs.parsers.tools.verilog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeListener;

import com.alchitry.labs.Util;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Param;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.verilog.Verilog2001BaseListener;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Inout_declarationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Input_declarationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_declarationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_instanceContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_instantiationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Named_parameter_assignmentContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Ordered_parameter_assignmentContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Output_declarationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Param_assignmentContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Port_identifierContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.RangeContext;
import com.alchitry.labs.tools.ParserCache;

public class VerilogModuleListener extends Verilog2001BaseListener {
	private static final int NONE = -1;
	private static final int INPUT = 0;
	private static final int OUTPUT = 1;
	private static final int INOUT = 2;

	private Module module;
	private List<Module> modules;
	private List<InstModule> instModules;
	private String widthText;
	private int portType;
	private List<Module> moduleList;
	private VerilogConstExprParser constExpr;

	public VerilogModuleListener() {
	}

	public List<Module> extractModules(String file) {
		moduleList = null;
		modules = new ArrayList<>();
		instModules = new ArrayList<InstModule>();
		ParserCache.walk(file, this);
		return modules;
	}

	public List<InstModule> extractInstModules(InstModule thisModule, List<Module> modules) {
		moduleList = modules;
		this.modules = new ArrayList<>();
		instModules = new ArrayList<InstModule>();
		List<ParseTreeListener> listeners = new ArrayList<>();
		constExpr = new VerilogConstExprParser(thisModule);
		listeners.add(this);
		listeners.add(constExpr);
		ParserCache.walk(new File(thisModule.getType().getFolder() + File.separatorChar + thisModule.getType().getFileName()).getAbsolutePath(), listeners);
		return instModules;
	}

	@Override
	public void exitModule_instantiation(Module_instantiationContext ctx) {
		if (moduleList != null) {
			String name = ctx.module_identifier().getText();
			int i = Util.findByName(moduleList, name);
			if (i >= 0) {
				Module module = moduleList.get(i);
				ArrayList<Param> params = new ArrayList<>(module.getParams());

				if (ctx.parameter_value_assignment() != null) {
					int pc = 0;
					for (Ordered_parameter_assignmentContext opac : ctx.parameter_value_assignment().list_of_parameter_assignments().ordered_parameter_assignment()) {
						params.get(pc).setValue(opac.getText());
						params.get(pc).setValue(constExpr.parseExpr(opac.getText()));
						pc++;
					}

					for (Named_parameter_assignmentContext npac : ctx.parameter_value_assignment().list_of_parameter_assignments().named_parameter_assignment()) {
						Param p = Util.getByName(params, npac.parameter_identifier().getText());
						if (p != null) {
							p.setValue(npac.expression().getText());
							p.setValue(constExpr.parseExpr(npac.expression().getText()));
						}
					}
				}

				for (Module_instanceContext mic : ctx.module_instance()) {
					InstModule im = new InstModule(mic.name_of_instance().getText(), moduleList.get(i), null);
					im.addParams(params);
					if (!instModules.contains(im))
						instModules.add(im);
				}

			}
		}
	}

	@Override
	public void enterModule_declaration(Module_declarationContext ctx) {
		module = new Module();
		modules.add(module);
		portType = NONE;
	}

	@Override
	public void exitModule_declaration(Module_declarationContext ctx) {
		if (ctx.module_identifier() != null)
			module.setName(ctx.module_identifier().getText());
	}

	@Override
	public void exitParam_assignment(Param_assignmentContext ctx) {
		if (ctx.parameter_identifier() != null && ctx.constant_expression() != null) {
			String name = ctx.parameter_identifier().getText();
			String defValue = ctx.constant_expression().getText();
			Param p = new Param(name, defValue);
			module.addParam(p);
		}
	}

	@Override
	public void enterInput_declaration(Input_declarationContext ctx) {
		portType = INPUT;
	}

	@Override
	public void enterOutput_declaration(Output_declarationContext ctx) {
		portType = OUTPUT;
	}

	@Override
	public void enterInout_declaration(Inout_declarationContext ctx) {
		portType = INOUT;
	}

	@Override
	public void exitInput_declaration(Input_declarationContext ctx) {
		portType = NONE;
	}

	@Override
	public void exitOutput_declaration(Output_declarationContext ctx) {
		portType = NONE;
	}

	@Override
	public void exitInout_declaration(Inout_declarationContext ctx) {
		portType = NONE;
	}

	@Override
	public void exitRange(RangeContext ctx) {
		if (ctx.msb_constant_expression() != null && ctx.lsb_constant_expression() != null) {
			String msb = ctx.msb_constant_expression().getText();
			String lsb = ctx.lsb_constant_expression().getText();
			widthText = "(" + msb + ")-(" + lsb + ")+1";
		}
	}

	@Override
	public void exitPort_identifier(Port_identifierContext ctx) {
		if (portType == NONE)
			return;
		Sig s = new Sig(ctx.getText());
		if (widthText != null)
			s.setWidth(new SignalWidth(widthText));
		else
			s.setWidth(new SignalWidth(1));
		widthText = null;
		switch (portType) {
		case INPUT:
			module.addInput(s);
			break;
		case OUTPUT:
			module.addOutput(s);
			break;
		case INOUT:
			module.addInout(s);
			break;
		}
	}
}
