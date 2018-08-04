package com.alchitry.labs.lucid.tools;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import com.alchitry.labs.Util;
import com.alchitry.labs.language.BitValue;
import com.alchitry.labs.language.ConstValue;
import com.alchitry.labs.lucid.SignalWidth;
import com.alchitry.labs.lucid.parser.LucidBaseListener;
import com.alchitry.labs.lucid.parser.LucidParser.Array_indexContext;
import com.alchitry.labs.lucid.parser.LucidParser.BitSelectorConstContext;
import com.alchitry.labs.lucid.parser.LucidParser.BitSelectorFixWidthContext;
import com.alchitry.labs.lucid.parser.LucidParser.SourceContext;
import com.alchitry.labs.lucid.style.TokenErrorListener;

public class BoundsParser extends LucidBaseListener implements BoundsProvider {
	protected ParseTreeProperty<ArrayBounds> bounds;
	private ConstExprParser constExprParser;
	private TokenErrorListener errorChecker;
	private WidthProvider widthProvider;

	public BoundsParser(ConstExprParser cep, WidthProvider wp, TokenErrorListener errorListener) {
		constExprParser = cep;
		errorChecker = errorListener;
		widthProvider = wp;
	}

	@Override
	public ArrayBounds getBounds(ParseTree node) {
		return bounds.get(node);
	}

	@Override
	public void enterSource(SourceContext ctx) {
		bounds = new ParseTreeProperty<>();
	}

	@Override
	public void exitBitSelectorConst(BitSelectorConstContext ctx) {
		if (ctx.expr().size() == 2) {
			ConstValue max = constExprParser.getValue(ctx.expr(0));
			ConstValue min = constExprParser.getValue(ctx.expr(1));

			if (!constExprParser.isConstant(ctx.expr(0)))
				errorChecker.onTokenErrorFound(ctx.expr(0), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr(0).getText()));

			if (!constExprParser.isConstant(ctx.expr(1)))
				errorChecker.onTokenErrorFound(ctx.expr(1), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr(1).getText()));

			if (max == null || min == null)
				return;

			if (max.isArray())
				errorChecker.onTokenErrorFound(ctx.expr(0), ErrorStrings.BIT_SELECTOR_ARRAY);
			if (min.isArray())
				errorChecker.onTokenErrorFound(ctx.expr(1), ErrorStrings.BIT_SELECTOR_ARRAY);
			if (max.isArray() || min.isArray())
				return;

			boolean maxNan = !max.isNumber();
			boolean minNan = !min.isNumber();
			if (maxNan)
				errorChecker.onTokenErrorFound(ctx.expr(0), ErrorStrings.BIT_SELECTOR_NAN);
			if (minNan)
				errorChecker.onTokenErrorFound(ctx.expr(1), ErrorStrings.BIT_SELECTOR_NAN);
			if (maxNan || minNan)
				return;

			if (max.lessThan(min) == BitValue.B1)
				errorChecker.onTokenErrorFound(ctx.expr(1), ErrorStrings.BIT_SELECTOR_ORDER);

			int maxInt, minInt;

			try {
				maxInt = max.getBigInt().intValue();
			} catch (ArithmeticException e) {
				errorChecker.onTokenWarningFound(ctx.expr(0), ErrorStrings.ARRAY_SIZE_TOO_BIG);
				return;
			}

			try {
				minInt = min.getBigInt().intValue();
			} catch (ArithmeticException e) {
				errorChecker.onTokenWarningFound(ctx.expr(1), ErrorStrings.ARRAY_SIZE_TOO_BIG);
				return;
			}

			ArrayBounds b = new ArrayBounds(maxInt, minInt);
			bounds.put(ctx, b);

		}
	}

	@Override
	public void exitBitSelectorFixWidth(BitSelectorFixWidthContext ctx) {
		if (ctx.expr().size() != 2)
			return;

		ConstValue width = constExprParser.getValue(ctx.expr(1));

		if (width == null) {
			if (!constExprParser.isConstant(ctx.expr(1)))
				errorChecker.onTokenErrorFound(ctx.expr(1), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr(1)));
			return;
		}

		if (widthProvider != null) {
			SignalWidth aw = widthProvider.getWidth(ctx.expr(0));
			if (aw == null) {
				Util.log.severe("Width of " + ctx.expr(0).getText() + " could not be determined!");
			} else {
				if (!aw.isSimpleArray()) {
					errorChecker.onTokenErrorFound(ctx.expr(0), ErrorStrings.BIT_SELECTOR_STRUCT);
					return;
				}
			}
			if (aw != null && aw.getWidths().size() != 1)
				errorChecker.onTokenErrorFound(ctx.expr(0), ErrorStrings.BIT_SELECTOR_ARRAY);
		}

		if (width.isArray()) {
			errorChecker.onTokenErrorFound(ctx.expr(1), ErrorStrings.BIT_SELECTOR_ARRAY);
			return;
		}

		ArrayBounds b = null;

		ConstValue start = constExprParser.getValue(ctx.expr(0));

		if (!width.isNumber()) {
			errorChecker.onTokenErrorFound(ctx.expr(1), ErrorStrings.BIT_SELECTOR_NAN);
			return;
		}

		int w = 0, s = 0;
		boolean error = false;
		try {
			w = width.getBigInt().intValue();
		} catch (ArithmeticException e) {
			errorChecker.onTokenWarningFound(ctx.expr(1), ErrorStrings.ARRAY_SIZE_TOO_BIG);
			error = true;
		}
		try {
			if (start != null) {
				if (start.isNumber()) {
					s = start.getBigInt().intValue();
				}
			}
		} catch (ArithmeticException e) {
			errorChecker.onTokenWarningFound(ctx.expr(0), ErrorStrings.ARRAY_SIZE_TOO_BIG);
			error = true;
		}

		if (error)
			return;

		if (start != null) {
			if (start.isNumber()) {
				if (ctx.getChild(2).getText().equals("+")) {
					b = new ArrayBounds(w + s - 1, s);
				} else {
					b = new ArrayBounds(s, s - w + 1);
				}
			} else {
				errorChecker.onTokenErrorFound(ctx.expr(0), ErrorStrings.BIT_SELECTOR_NAN);
			}
		}

		bounds.put(ctx, b);
	}

	@Override
	public void exitArray_index(Array_indexContext ctx) {
		if (widthProvider != null) {
			SignalWidth aw = widthProvider.getWidth(ctx.expr());

			if (aw != null && !aw.isSimpleArray()) {
				errorChecker.onTokenErrorFound(ctx.expr(), ErrorStrings.ARRAY_INDEX_STRUCT);
				return;
			}

			if (aw != null && aw.getWidths().size() != 1) {
				errorChecker.onTokenErrorFound(ctx.expr(), ErrorStrings.BIT_SELECTOR_ARRAY);
				return;
			}

			ConstValue val = constExprParser.getValue(ctx.expr());

			if (val != null) {
				if (!val.isNumber()) {
					errorChecker.onTokenErrorFound(ctx.expr(), ErrorStrings.ARRAY_INDEX_NAN);
					return;
				}
				try {
					int v = val.getBigInt().intValue();
					ArrayBounds b = new ArrayBounds(v, v);
					bounds.put(ctx, b);
				} catch (ArithmeticException e) {
					errorChecker.onTokenWarningFound(ctx.expr(), ErrorStrings.ARRAY_INDEX_TOO_BIG);
				}
			}
		}
	}
}
