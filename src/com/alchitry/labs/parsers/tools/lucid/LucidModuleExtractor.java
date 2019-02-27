package com.alchitry.labs.parsers.tools.lucid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeListener;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Param;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.lucid.Struct;
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Array_sizeContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Inout_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Input_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ModuleContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Output_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_nameContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Struct_typeContext;
import com.alchitry.labs.tools.ParserCache;

public class LucidModuleExtractor extends LucidBaseListener {
	private Module module;

	public LucidModuleExtractor() {
	}

	public Module getModule(String file) {
		List<ParseTreeListener> listeners = new ArrayList<>();
		listeners.add(this);
		ParserCache.walk(file, listeners);
		return module;
	}

	@Override
	public void enterModule(ModuleContext ctx) {
		module = new Module();
	}

	@Override
	public void exitModule(ModuleContext ctx) {
		if (ctx.name() != null) {
			module.setName(ctx.name().getText());
		}
	}

	private SignalWidth getWidths(List<Array_sizeContext> asc, Struct_typeContext stc) {
		SignalWidth base = null;
		SignalWidth current = null;

		for (Array_sizeContext c : asc) {
			if (current == null) {
				current = new SignalWidth(c.expr().getText());
				base = current;
			} else {
				current.setNext(new SignalWidth(c.expr().getText()));
				current = current.getNext();
			}
		}

		if (stc != null) {
			Struct s = null;
			if (stc.name().size() == 2) {
				HashMap<String, List<Struct>> gS = MainWindow.getOpenProject().getGlobalStructs();
				List<Struct> structs = gS.get(stc.name(0).getText());
				if (structs != null) {
					s = Util.getByName(structs, stc.name(1).getText());
				}
			}

			if (s != null) {
				SignalWidth sw = new SignalWidth(s);
				if (current == null) {
					current = sw;
					base = current;
				} else {
					current.setNext(sw);

				}
			}
		}

		if (base == null)
			base = new SignalWidth(1);

		return base;
	}

	@Override
	public void exitInput_dec(Input_decContext ctx) {
		if (ctx.name() != null) {
			Sig s = new Sig(ctx.name().getText());
			module.addInput(s);
			s.setWidth(getWidths(ctx.array_size(), ctx.struct_type()));
		}
	}

	@Override
	public void exitOutput_dec(Output_decContext ctx) {
		if (ctx.name() != null) {
			Sig s = new Sig(ctx.name().getText());
			module.addOutput(s);
			s.setWidth(getWidths(ctx.array_size(), ctx.struct_type()));
		}
	}

	@Override
	public void exitInout_dec(Inout_decContext ctx) {
		if (ctx.name() != null) {
			Sig s = new Sig(ctx.name().getText());
			module.addInout(s);
			s.setWidth(getWidths(ctx.array_size(), ctx.struct_type()));
		}
	}

	@Override
	public void exitParam_name(Param_nameContext ctx) {
		if (ctx.name() != null) {
			String name = ctx.name().getText();
			String text = null;
			if (ctx.expr() != null)
				text = ctx.expr().getText();
			Param p = new Param(name, text, null);

			module.addParam(p);
		}
	};

	@Override
	public void exitParam_dec(Param_decContext ctx) {
		if (ctx.param_constraint() != null && ctx.param_name().name() != null) {
			int idx = Util.findByName(module.getParams(), ctx.param_name().name().getText());
			if (idx >= 0) {
				Param p = module.getParams().get(idx);
				p.setConstraint(ctx.param_constraint().getText());
				if (ctx.param_name().expr() != null)
					p.setDefault(ctx.param_name().expr().getText());
			}
		}
	};

}
