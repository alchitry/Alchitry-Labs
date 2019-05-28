package com.alchitry.labs.parsers.tools.verilog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Param;
import com.alchitry.labs.parsers.errors.ErrorStrings;
import com.alchitry.labs.parsers.verilog.Verilog2001BaseListener;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_declarationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Module_instantiationContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Named_parameter_assignmentContext;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser.Ordered_parameter_assignmentContext;
import com.alchitry.labs.style.SyntaxError;
import com.alchitry.labs.tools.ParserCache;

public class VerilogLucidModuleFixer {

	private VerilogLucidModuleFixer() {

	}

	public static String replaceModuleNames(InstModule thisModule, File file, List<Module> modules, List<InstModule> instModules) {
		List<ParseTreeListener> listeners = new ArrayList<>();
		CommonTokenStream tokens = ParserCache.getTokens(file);
		VerilogConstExprParser cep = new VerilogConstExprParser(thisModule);
		ModuleReplaceWalker mrw = new ModuleReplaceWalker(thisModule, modules, instModules, tokens, cep);
		listeners.add(mrw);
		listeners.add(cep);

		ParserCache.walk(file, listeners);
		return mrw.getText();
	}

	public static List<SyntaxError> getErrors(InstModule thisModule, File file, List<Module> modules, List<InstModule> instModules) {
		List<ParseTreeListener> listeners = new ArrayList<>();
		CommonTokenStream tokens = ParserCache.getTokens(file);
		VerilogConstExprParser cep = new VerilogConstExprParser(thisModule);
		ModuleReplaceWalker mrw = new ModuleReplaceWalker(thisModule, modules, instModules, tokens, cep);
		listeners.add(mrw);
		listeners.add(cep);

		ParserCache.walk(file, listeners);
		return mrw.getErrors();
	}

	private static class ModuleReplaceWalker extends Verilog2001BaseListener {
		private List<InstModule> instModules;
		private List<Module> modules;
		private TokenStreamRewriter rewriter;
		private List<SyntaxError> errors;
		private InstModule thisModule;
		private VerilogConstExprParser constExpr;

		public ModuleReplaceWalker(InstModule thisModule, List<Module> modules, List<InstModule> instModules, TokenStream tokens, VerilogConstExprParser cosntExpr) {
			this.modules = modules;
			this.instModules = instModules;
			this.thisModule = thisModule;
			rewriter = new TokenStreamRewriter(tokens);
			errors = new ArrayList<>();
			this.constExpr = cosntExpr;
		}

		public String getText() {
			return rewriter.getText();
		}

		public List<SyntaxError> getErrors() {
			return errors;
		}

		@Override
		public void exitModule_declaration(Module_declarationContext ctx) {
			if (ctx.module_identifier() != null)
				rewriter.insertAfter(ctx.module_identifier().stop, "_" + instModules.indexOf(thisModule));
		}

		@Override
		public void exitModule_instantiation(Module_instantiationContext ctx) {
			if (modules != null) {
				String name = ctx.module_identifier().getText();
				int i = Util.findByName(modules, name);
				if (i >= 0) {
					Module module = modules.get(i);
					boolean isLucid = module.getFile().getName().endsWith(".luc");
					ArrayList<Param> params = new ArrayList<>(module.getParams());

					if (ctx.parameter_value_assignment() != null) {
						int pc = 0;
						for (Ordered_parameter_assignmentContext opac : ctx.parameter_value_assignment().list_of_parameter_assignments().ordered_parameter_assignment()) {
							params.get(pc).setValue(opac.getText());
							ConstValue cv = constExpr.parseExpr(opac.expression().getText());
							if (isLucid && cv == null)
								underlineError(opac.expression(), "Failed to parse parameter value \"" + opac.expression().getText() + "\"");
							params.get(pc).setValue(cv);
							pc++;
						}

						for (Named_parameter_assignmentContext npac : ctx.parameter_value_assignment().list_of_parameter_assignments().named_parameter_assignment()) {
							Param p = Util.getByName(params, npac.parameter_identifier().getText());
							if (p != null) {
								p.setValue(npac.expression().getText());
								ConstValue cv = constExpr.parseExpr(npac.expression().getText());
								if (isLucid && cv == null)
									underlineError(npac.expression(), "Failed to parse parameter value \"" + npac.expression().getText() + "\"");
								p.setValue(cv);
							}
						}

						if (isLucid)
							rewriter.delete(ctx.parameter_value_assignment().start, ctx.parameter_value_assignment().stop);
					}

					InstModule im = new InstModule("", modules.get(i), null);
					im.addParams(params);

					for (Param p : params) {
						if (p.valueSet() && !im.checkParameterConstraint(p))
							underlineError(ctx.module_identifier(), String.format(ErrorStrings.PARAMETER_CONSTRAINT_FAILED, p.getConstraint(), p.getName(), p.getValue()));
						else if (p.getDefValue() == null && !p.valueSet())
							underlineError(ctx.module_identifier(), String.format(ErrorStrings.MODULE_MISSING_REQ_PARAM, p.getName()));
					}

					int idx = instModules.indexOf(im);
					if (idx >= 0 && !instModules.get(idx).getType().isPrimitive() && !instModules.get(idx).getType().isNgc()) {
						rewriter.insertAfter(ctx.module_identifier().stop, "_" + idx);
					}

				}
			}
		}

		private void underlineError(ParserRuleContext ctx, String message) {
			underline(ctx.start, ctx.stop, message, Theme.errorTextColor, SyntaxError.ERROR);
		}

		private void underline(Token startToken, Token stopToken, String message, Color color, int type) {
			int start = startToken.getStartIndex();
			int stop = stopToken == null ? startToken.getStopIndex() : stopToken.getStopIndex();

			if (stop < start) // bad token, can happen with severe syntax errors
				return;

			if (start == -1 || stop == -1) {
				Util.log.severe("ERROR: Token start or stop was -1");
				return;
			}

			StyleRange style = new StyleRange();
			style.start = start;
			style.length = stop - start + 1;
			style.underline = true;
			style.underlineColor = color;
			style.underlineStyle = SWT.UNDERLINE_SINGLE;
			int line = startToken.getLine();
			int offset = startToken.getCharPositionInLine();
			errors.add(new SyntaxError(type, style, message, start, stop, line, offset));
		}
	}

}
