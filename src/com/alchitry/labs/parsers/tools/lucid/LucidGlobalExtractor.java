package com.alchitry.labs.parsers.tools.lucid;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.errors.ErrorStrings;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Array_sizeContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Const_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.GlobalContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Struct_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Struct_memberContext;
import com.alchitry.labs.parsers.types.Constant;
import com.alchitry.labs.parsers.types.Struct;
import com.alchitry.labs.style.SyntaxError;
import com.alchitry.labs.tools.ParserCache;

public class LucidGlobalExtractor extends LucidBaseListener {
	private List<SyntaxError> errors;
	private HashMap<File, List<SyntaxError>> fileErrors;
	private HashMap<String, List<Constant>> globalConsts;
	private HashMap<String, List<Struct>> globalStructs;
	private List<Constant> consts;
	private List<Struct> structs;
	private boolean inGlobal = false;

	public LucidGlobalExtractor() {
		globalConsts = new HashMap<>();
		globalStructs = new HashMap<>();
		fileErrors = new HashMap<>();
	}

	public void parseGlobals(File file) {
		errors = new ArrayList<>();
		fileErrors.put(file, errors);
		ParserCache.walk(file, this);
	}

	public HashMap<String, List<Constant>> getConstants() {
		return globalConsts;
	}

	public HashMap<String, List<Struct>> getStructs() {
		return globalStructs;
	}

	@Override
	public void enterGlobal(GlobalContext ctx) {
		consts = new ArrayList<>();
		structs = new ArrayList<>();
		inGlobal = true;
	}

	@Override
	public void exitGlobal(GlobalContext ctx) {
		if (ctx.name() != null) {
			String name = ctx.name().getText();
			if (globalConsts.put(name, consts) != null || globalStructs.put(name, structs) != null) {
				underlineError(ctx.name(), String.format(ErrorStrings.NAMESPACE_IN_USE, ctx.name().getText()));
			}
			if (ctx.name().SPACE_ID() == null)
				underlineError(ctx.name(), ErrorStrings.NAMESPACE_CASE);
		}

		inGlobal = false;
	}

	private ConstProvider constProvider = new ConstProvider() {
		@Override
		public ConstValue getValue(String s) {
			Constant c = Util.getByName(consts, s);
			if (c != null)
				return c.getValue();

			// String[] spt = s.split("\\.");
			// if (spt.length == 2) {
			// List<Constant> lc = globalConsts.get(spt[0]);
			// if (lc != null) {
			// c = Util.getByName(lc, spt[1]);
			// if (c != null)
			// return c.getValue();
			// }
			// }
			return null;
		}
	};

	@Override
	public void exitConst_dec(Const_decContext ctx) {
		if (!inGlobal)
			return;

		if (ctx.name() == null || ctx.expr() == null)
			return;

		ConstValue cv = ConstExprParser.parseExpr(ctx.expr().getText(), constProvider, null, null);
		Constant c = new Constant(ctx.name().getText(), cv);

		consts.add(c);
	}

	@Override
	public void exitStruct_dec(Struct_decContext ctx) {
		if (!inGlobal)
			return;

		if (ctx.name() != null) {

			Struct struct = new Struct(ctx.name().getText());
			for (Struct_memberContext smc : ctx.struct_member()) {
				if (smc.name() != null) {
					Struct.Member m = new Struct.Member(smc.name().getText());
					SignalWidth width = new SignalWidth();
					if (smc.struct_type() != null) {
						if (smc.struct_type().name().size() != 1) {
							underlineError(smc.struct_type(), ErrorStrings.LOCAL_STRUCT_ONLY);
						}

						Struct s = Util.getByName(structs, smc.struct_type().name(0).getText());
						if (s != null) {
							width.set(s);
						}
					}

					if (smc.array_size().size() > 0) {
						SignalWidth pwidth = width;
						if (!pwidth.isArray()) {
							pwidth = new SignalWidth();
							width.setNext(pwidth);
						}

						for (Array_sizeContext asc : smc.array_size()) {
							ConstValue cv = ConstExprParser.parseExpr(asc.expr().getText(), constProvider, null, null);
							if (cv == null) {
								Util.log.severe("Failed to parse width " + asc.getText());
							} else {
								if (!cv.isArray())
									pwidth.getWidths().add(cv.getBigInt().intValue());
							}
						}
					}
					if (width.getDepth() == 0)
						width.getWidths().add(1);
					m.width = width;
					m.signed = smc.SIGNED() != null;
					struct.addMember(m);
				}
			}
			structs.add(struct);
		}
	}

//	private void underlineWarning(ParserRuleContext ctx, String message) {
//		underline(ctx.start, ctx.stop, message, Theme.warningTextColor, SyntaxError.WARNING);
//	}

	private void underlineError(ParserRuleContext ctx, String message) {
		underline(ctx.start, ctx.stop, message, Theme.errorTextColor, SyntaxError.ERROR);
	}

//	private void underlineWarning(TerminalNode term, String message) {
//		underline(term.getSymbol(), term.getSymbol(), message, Theme.warningTextColor, SyntaxError.WARNING);
//	}
//
//	private void underlineError(TerminalNode term, String message) {
//		underline(term.getSymbol(), term.getSymbol(), message, Theme.errorTextColor, SyntaxError.ERROR);
//	}

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

	public List<SyntaxError> getErrors(File file) {
		return fileErrors.get(file);
	}

	public HashMap<File, List<SyntaxError>> getAllErrors() {
		return fileErrors;
	}

	public void reset() {
		fileErrors.clear();
		globalConsts.clear();
		globalStructs.clear();
	}

}
