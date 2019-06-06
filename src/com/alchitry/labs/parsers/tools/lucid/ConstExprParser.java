package com.alchitry.labs.parsers.tools.lucid;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringEscapeUtils;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.BigFunctions;
import com.alchitry.labs.parsers.BitValue;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.errors.ErrorListener;
import com.alchitry.labs.parsers.errors.ErrorStrings;
import com.alchitry.labs.parsers.lucid.Lucid;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidLexer;
import com.alchitry.labs.parsers.lucid.parser.LucidParser;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Array_indexContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Bit_selectionContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprAddSubContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprAndOrContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprArrayContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprCompareContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprCompressContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprConcatContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprDupContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprFunctionContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprGroupContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprInvertContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprLogicalContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprMultDivContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprNegateContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprNumContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprShiftContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprSignalContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprTernaryContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.FunctionContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.NumberContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_constraintContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.SignalContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.SourceContext;
import com.alchitry.labs.parsers.types.Constant;

public class ConstExprParser extends LucidBaseListener {
	private ErrorListener listener;
	private WidthProvider bitWidthChecker;
	private BoundsProvider boundsProvider;
	private ConstProvider paramsParser;
	private ConstProvider constParser;
	protected ParseTreeProperty<ConstValue> values;
	protected ParseTreeProperty<Boolean> constant;

	private static final ErrorListener dummyListener = new ErrorListener() {
		@Override
		public void reportWarning(ParserRuleContext ctx, String message) {
		}

		@Override
		public void reportWarning(TerminalNode node, String message) {
		}

		@Override
		public void reportError(ParserRuleContext ctx, String message) {
		}

		@Override
		public void reportError(TerminalNode node, String message) {
		}

		@Override
		public void reportDebug(ParserRuleContext ctx, String message) {
		}

		@Override
		public void reportDebug(TerminalNode node, String message) {
		}
	};

	public ConstExprParser(ErrorListener listen) {
		listener = listen;
		if (listener == null)
			listener = dummyListener;
	}

	public void setParamsParser(ConstProvider p) {
		paramsParser = p;
	}

	public void setConstParser(ConstProvider c) {
		constParser = c;
	}

	public void setBitWidthChecker(WidthProvider bwc) {
		bitWidthChecker = bwc;
	}

	public void setBoundsProvider(BoundsProvider bp) {
		boundsProvider = bp;
	}

	public ConstValue getValue(ParseTree node) {
		return values.get(node);
	}

	public boolean isConstant(ParseTree node) {
		return Boolean.TRUE.equals(constant.get(node));
	}

	private void debug(ParserRuleContext ctx) {
		// listener.onTokenDebugFound(ctx, ctx.getText() + " = " + (values.get(ctx) == null ? "null" : values.get(ctx).toString()));
	}

	private void debugNullConstant(ParserRuleContext ctx) {
		// listener.onTokenDebugFound(ctx, "This should have a known value!");
	}

	@Override
	public void enterSource(SourceContext ctx) {
		values = new ParseTreeProperty<>();
		constant = new ParseTreeProperty<>();
	}

	// Constant and attribute parser
	@Override
	public void exitSignal(SignalContext ctx) {
		if (ctx.name().size() > 0) {
			ConstValue cv = null;
			boolean isWidth = false;

			// is WIDTH
			if (ctx.name().size() > 1 && ctx.name(ctx.name().size() - 1).getText().equals(Lucid.WIDTH_ATTR)) {
				isWidth = true;
				SignalWidth aw = null;
				if (ctx.name(0).SPACE_ID() != null) { // is global
					if (ctx.name().size() == 3) {
						HashMap<String, List<Constant>> gC = MainWindow.getGlobalConstants();
						List<Constant> consts = gC.get(ctx.name(0).getText());
						if (consts != null) {
							Constant c = Util.getByName(consts, ctx.name(1).getText());
							if (c != null && c.getValue() != null) {
								aw = c.getValue().getArrayWidth();
							}
						}
					}
				} else {
					// TODO: fix for bitwidthchecker missing consts from inst modules
					String sigName = BitWidthChecker.getName(ctx, bitWidthChecker);
					if (sigName != null) {
						int max = ctx.children.size() - 1;
						// if the last child is a bit selector
						if (ctx.children.get(ctx.children.size() - 1) instanceof Bit_selectionContext)
							max--;

						aw = bitWidthChecker.getWidth(sigName);
						int i = sigName.split("\\.").length - 1;
						aw = new SignalWidth(aw);
						BitWidthChecker.getArrayWidth(aw, ctx, ctx.children.indexOf(ctx.name(i)) + 1, max, bitWidthChecker, BoundsProvider.dummyProvider, null);
					}
				}

				if (aw != null) {
					if (aw.isSimpleArray()) {
						if (aw.getDepth() > 1) {
							cv = new ConstValue(true);

							int max = Integer.MIN_VALUE;
							for (int i : aw.getWidths())
								max = Math.max(max, i);
							int width = Util.minWidthNum(max);
							for (int i : aw.getWidths()) {
								cv.add(new ConstValue(i, width));
							}
						} else {
							if (aw.getDepth() > 0)
								cv = new ConstValue(aw.getWidths().get(0));
						}
					} else {
						listener.reportError(ctx.name(ctx.name().size() - 1), ErrorStrings.WIDTH_NOT_SIMPLE_ARRAY);
					}
				}

				constant.put(ctx, true);
			} else if (ctx.name(0).SPACE_ID() != null && ctx.name().size() == 2 && ctx.name(1).CONST_ID() != null) {
				HashMap<String, List<Constant>> gC = MainWindow.getGlobalConstants();
				List<Constant> consts = gC.get(ctx.name(0).getText());
				if (consts != null) {
					Constant c = Util.getByName(consts, ctx.name(1).getText());
					if (c != null) {
						constant.put(ctx, true);
						cv = c.getValue();
					}
				}
			} else if (ctx.name().size() == 1) { // is constant
				constant.put(ctx, true);
				cv = constParser.getValue(ctx.name(0).getText());
				if (cv == null)
					cv = paramsParser.getValue(ctx.name(0).getText());
			}

			if (cv != null) {
				if (ctx.children.get(ctx.children.size() - 1) instanceof Bit_selectionContext) { // last child is a bit selector
					Bit_selectionContext lbsc = ctx.bit_selection(ctx.bit_selection().size() - 1); // last bit selection
					if (lbsc.getChildCount() <= cv.getDepth()) {
						cv = new ConstValue(cv);
						for (Array_indexContext aic : lbsc.array_index()) {
							ArrayBounds b = boundsProvider.getBounds(aic);
							if (b == null || b.getMax() >= cv.getValues().size() || b.getMin() < 0) {
								if (isWidth)
									listener.reportError(aic, ErrorStrings.WIDTH_COULD_NOT_BE_EVALUATED);
								return;
							}
							cv = cv.get(b.getMax());
						}
						if (lbsc.bit_selector() != null) {
							ArrayBounds b = boundsProvider.getBounds(lbsc.bit_selector());

							int size = cv.getWidth();
							if (b == null || b.getMax() >= size || b.getMin() < 0) {
								if (isWidth)
									listener.reportError(lbsc.bit_selector(), ErrorStrings.WIDTH_COULD_NOT_BE_EVALUATED);
								return;
							}

							for (int i = size - 1; i > b.getMax(); i--)
								cv.remove(i);
							for (int i = b.getMin() - 1; i >= 0; i--)
								cv.remove(i);
						}
					}
				}
			}
			values.put(ctx, cv);
		}
	}

	@Override
	public void exitParam_constraint(Param_constraintContext ctx) {
		if (ctx.expr() != null && values.get(ctx.expr()) == null && isConstant(ctx.expr()))
			debugNullConstant(ctx);

		if (ctx.expr() != null && !isConstant(ctx.expr())) {
			listener.reportError(ctx.expr(), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr().getText()));
		}
		values.put(ctx, values.get(ctx.expr()));
		constant.put(ctx, constant.get(ctx.expr()));
	}

	@Override
	public void exitNumber(NumberContext ctx) {
		String[] split = null;
		ConstValue width = null;
		int radix = 10;
		String value;

		constant.put(ctx, true);

		if (ctx.HEX() != null) {
			split = ctx.HEX().getText().split("h");
			radix = 16;
		} else if (ctx.BIN() != null) {
			split = ctx.BIN().getText().split("b");
			radix = 2;
		} else if (ctx.DEC() != null) {
			split = ctx.DEC().getText().split("d");
			radix = 10;
		}

		if (split != null) {
			value = split[1];
			if (!split[0].equals("")) {
				width = new ConstValue(split[0]);
				if (!width.isNumber()) {
					listener.reportError(ctx, ErrorStrings.NUM_WIDTH_NAN);
					return;
				}
			}
		} else {
			if (ctx.INT() != null) {
				value = ctx.INT().getText();
				radix = 10;
			} else { // String
				String str = StringEscapeUtils.unescapeJava(ctx.STRING().getText());
				value = str.substring(1, str.length() - 1);

				ConstValue cv;
				if (value.length() > 1) {
					cv = new ConstValue(true);
					for (int i = value.length() - 1; i >= 0; i--)
						cv.add(new ConstValue(value.charAt(i), 8));
				} else if (value.length() == 1) {
					cv = new ConstValue(value.charAt(0));
				} else {
					cv = new ConstValue(0);
					listener.reportError(ctx, ErrorStrings.STRING_CANNOT_BE_EMPTY);
				}
				values.put(ctx, cv);
				return;
			}
		}

		ConstValue cv;
		ConstValue unbound = new ConstValue(value, radix);
		if (width != null)
			cv = new ConstValue(value, radix, width.getBigInt().intValue());
		else
			cv = unbound;

		if (cv.getWidth() < unbound.getMinWidth()) {
			listener.reportWarning(ctx, String.format(ErrorStrings.VALUE_TOO_BIG, ctx.getText(), cv.getWidth()));
		}

		values.put(ctx, cv);
	}

	@Override
	public void exitFunction(FunctionContext ctx) {
		if (ctx.expr() == null || ctx.FUNCTION_ID() == null)
			return;

		boolean c = true;

		for (ExprContext e : ctx.expr())
			if (!Boolean.TRUE.equals(constant.get(e))) {
				c = false;
				break;
			}
		constant.put(ctx, c);

		String sfid = ctx.FUNCTION_ID().getText();

		ConstValue[] args = new ConstValue[ctx.expr().size()];
		for (int i = 0; i < args.length; i++)
			args[i] = values.get(ctx.expr(i));

		switch (sfid) {
		case "$clog2":
			if (args.length == 1) {
				if (args[0] != null) {
					BigInteger bi = null;
					if (args[0].isNumber())
						bi = args[0].getBigInt();
					if (bi != null) {
						if (bi.compareTo(BigInteger.ZERO) != 0)
							values.put(ctx, new ConstValue(
									BigFunctions.ln(new BigDecimal(bi), 32).divide(BigFunctions.LOG2, RoundingMode.HALF_UP).setScale(0, RoundingMode.CEILING).toBigInteger()));
						else
							listener.reportError(ctx.expr(0), String.format(ErrorStrings.FUNCTION_ARG_ZERO, ctx.expr(0).getText(), args[0].toString()));
					} else {
						listener.reportError(ctx.expr(0), String.format(ErrorStrings.FUNCTION_ARG_NAN, ctx.expr(0).getText(), args[0].toString()));
					}
				} else {
					debugNullConstant(ctx);
					if (!c)
						listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.CONST_FUNCTION, ctx.FUNCTION_ID()));
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 1));
			}
			break;
		case "$pow":
			if (args.length == 2) {
				if (args[0] != null && args[1] != null) {
					BigInteger b1 = null, b2 = null;
					if (args[0].isNumber())
						b1 = args[0].getBigInt();
					else
						listener.reportError(ctx.expr(0), String.format(ErrorStrings.FUNCTION_ARG_NAN, ctx.expr(0).getText(), args[0].toString()));

					if (args[1].isNumber())
						b2 = args[1].getBigInt();
					else
						listener.reportError(ctx.expr(1), String.format(ErrorStrings.FUNCTION_ARG_NAN, ctx.expr(1).getText(), args[1].toString()));

					try {
						if (b1 != null && b2 != null)
							values.put(ctx, new ConstValue(b1.pow(b2.intValueExact())));
					} catch (ArithmeticException e) {
						listener.reportError(ctx.expr(1), String.format(ErrorStrings.VALUE_BIGGER_THAN_INT, ctx.expr(1).getText()));
					}

				} else {
					debugNullConstant(ctx);
					if (!c)
						listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.CONST_FUNCTION, ctx.FUNCTION_ID()));
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 2));
			}
			break;
		case "$reverse":
			if (args.length == 1) {
				if (args[0] != null) {
					values.put(ctx, new ConstValue(args[0]).reverse());
				} else {
					debugNullConstant(ctx);
					if (!c)
						listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.CONST_FUNCTION, ctx.FUNCTION_ID()));
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 1));
			}
			break;
		case "$flatten":
			if (args.length == 1) {
				if (args[0] != null) {
					values.put(ctx, args[0].flatten());
					System.out.println("Flattened: " + values.get(ctx));
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 1));
			}
			break;
		case "$build":
			if (args.length >= 2) {
				if (args[0] != null) {
					boolean error = false;
					if (args[0].isArray()) {
						listener.reportError(ctx.expr(0), ErrorStrings.BUILD_MULTI_DIM);
						error = true;
					}
					for (int i = 1; i < args.length; i++) {
						if (args[i] != null) {
							if (!args[i].isNumber()) {
								listener.reportError(ctx.expr(i), String.format(ErrorStrings.FUNCTION_ARG_NAN, ctx.expr(i).getText(), args[i].toString()));
								error = true;
							}
						} else {
							error = true;
						}
					}
					if (error)
						break;
					int[] dims = new int[args.length - 1];

					for (int i = 1; i < args.length; i++)
						dims[dims.length - i] = args[i].getBigInt().intValue();

					long factor = 1;
					for (int d : dims)
						factor *= d;

					if (args[0].getWidth() % factor != 0) {
						listener.reportError(ctx.expr(0), String.format(ErrorStrings.ARRAY_NOT_DIVISIBLE, ctx.expr(0).getText()));
						break;
					}

					ConstValue builtValue = args[0].build(dims);
					values.put(ctx, builtValue);
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_MIN_ARG_COUNT, ctx.FUNCTION_ID(), 2));
			}
			break;
		case "$signed":
			if (args.length == 1) {
				if (args[0] != null) {
					ConstValue scv = new ConstValue(args[0]);
					scv.setSigned(true);
					values.put(ctx, scv);
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 1));
			}
			break;
		case "$unsigned":
			if (args.length == 1) {
				if (args[0] != null) {
					ConstValue scv = new ConstValue(args[0]);
					scv.setSigned(false);
					values.put(ctx, scv);
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 1));
			}
			break;
		case "$cdiv":
			if (args.length == 2) {
				if (args[0] != null && args[1] != null) {
					BigInteger b1 = null, b2 = null;
					if (args[0].isNumber())
						b1 = args[0].getBigInt();
					else
						listener.reportError(ctx.expr(0), String.format(ErrorStrings.FUNCTION_ARG_NAN, ctx.expr(0).getText(), args[0].toString()));

					if (args[1].isNumber())
						b2 = args[1].getBigInt();
					else
						listener.reportError(ctx.expr(1), String.format(ErrorStrings.FUNCTION_ARG_NAN, ctx.expr(1).getText(), args[1].toString()));

					if (b1 != null && b2 != null) {
						if (!b2.equals(BigInteger.ZERO)) {
							BigDecimal d1 = new BigDecimal(b1,10);
							BigDecimal d2 = new BigDecimal(b2,10);
							values.put(ctx, new ConstValue(d1.divide(d2, RoundingMode.HALF_UP).setScale(0, RoundingMode.CEILING).toBigInteger()));
						} else {
							listener.reportError(ctx.expr(1), String.format(ErrorStrings.FUNCTION_ARG_ZERO, ctx.expr(1).getText()));
						}
					}

				} else {
					debugNullConstant(ctx);
					if (!c)
						listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.CONST_FUNCTION, ctx.FUNCTION_ID()));
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 2));
			}
			break;
		case "$resize":
			if (args.length == 2) {
				if (args[0] != null && args[1] != null) {
					int size = 0;
					if (args[1].isNumber())
						try {
							size = args[1].getBigInt().intValueExact();
							if (size < 0)
								listener.reportError(ctx.expr(1), String.format(ErrorStrings.FUNCTION_ARG_NEG, ctx.expr(1).getText()));
							if (size == 0)
								listener.reportError(ctx.expr(1), String.format(ErrorStrings.FUNCTION_ARG_ZERO, ctx.expr(1).getText()));
						} catch (ArithmeticException e) {
							listener.reportError(ctx.expr(1), String.format(ErrorStrings.VALUE_BIGGER_THAN_INT, ctx.expr(1).getText()));
						}
					else
						listener.reportError(ctx.expr(1), String.format(ErrorStrings.FUNCTION_ARG_NAN, ctx.expr(1).getText(), args[1].toString()));

					if (!args[0].isArray()) {
						if (size > 0) {
							if (size < args[0].getMinWidth())
								listener.reportWarning(ctx.expr(1), String.format(ErrorStrings.TRUNC_WARN, ctx.expr(0).getText(), size));
							values.put(ctx, args[0].resize(size));
						}
					} else {
						listener.reportError(ctx.expr(0), String.format(ErrorStrings.FUNCTION_NO_ARRAY, ctx.FUNCTION_ID()));
					}
				} else {
					debugNullConstant(ctx);
					if (!c)
						listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.CONST_FUNCTION, ctx.FUNCTION_ID()));
				}
			} else {
				listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.FUNCTION_ARG_COUNT, ctx.FUNCTION_ID(), 2));
			}
			break;
		default:
			listener.reportError(ctx.FUNCTION_ID(), String.format(ErrorStrings.UNKNOWN_FUNCTION, ctx.FUNCTION_ID()));
			break;
		}

	}

	/**************** expr *******************/

	@Override
	public void exitExprSignal(ExprSignalContext ctx) {
		values.put(ctx, values.get(ctx.signal()));
		constant.put(ctx, constant.get(ctx.signal()));
		debug(ctx);
	}

	@Override
	public void exitExprFunction(ExprFunctionContext ctx) {
		values.put(ctx, values.get(ctx.function()));
		constant.put(ctx, constant.get(ctx.function()));

		debug(ctx);
	}

	@Override
	public void exitExprNum(ExprNumContext ctx) {
		values.put(ctx, values.get(ctx.number()));
		constant.put(ctx, constant.get(ctx.number()));

		debug(ctx);
	}

	@Override
	public void exitExprGroup(ExprGroupContext ctx) {
		if (ctx.expr() != null && values.get(ctx.expr()) != null) {
			values.put(ctx, values.get(ctx.expr()));
			constant.put(ctx, constant.get(ctx.expr()));
		}

		debug(ctx);
	}

	@Override
	public void exitExprConcat(ExprConcatContext ctx) {
		if (ctx.expr().size() > 0) {
			ArrayList<ConstValue> cvs = new ArrayList<ConstValue>();
			ArrayList<ParserRuleContext> nodes = new ArrayList<ParserRuleContext>();

			constant.put(ctx, true);

			for (ExprContext cec : ctx.expr())
				if (!isConstant(cec))
					constant.put(ctx, false);

			for (ExprContext cec : ctx.expr()) {
				ConstValue v = values.get(cec);

				if (v == null)
					return;
				cvs.add(v);
				nodes.add(cec);
			}

			ConstValue cv;

			if (cvs.size() > 0) {
				ConstValue base = cvs.get(0);
				ArrayList<Integer> baseDim = new ArrayList<Integer>(base.getWidths());
				baseDim.remove(0);
				ArrayList<Integer> tempList = new ArrayList<>(baseDim.size());
				boolean error = false;

				if (base.isArray()) {
					for (int i = 0; i < cvs.size(); i++) {
						tempList.clear();
						tempList.addAll(cvs.get(i).getWidths());
						tempList.remove(0);
						if (!baseDim.equals(tempList)) { // match all but first dim
							listener.reportError(nodes.get(i), ErrorStrings.ARRAY_CONCAT_DIM_MISMATCH);
							error = true;
						}
					}
					if (error)
						return;

					cv = new ConstValue(true);
					for (ConstValue v : cvs) {
						cv.addAllToFront(v.getValues());
					}
				} else {
					for (int i = 0; i < cvs.size(); i++) {
						if (cvs.get(i).isArray()) {
							listener.reportError(nodes.get(i), ErrorStrings.ARRAY_CONCAT_DIM_MISMATCH);
							error = true;
						}
					}
					if (error)
						return;

					int elemCount = cvs.size();
					cv = new ConstValue(false);

					for (int i = elemCount - 1; i >= 0; i--) {
						cv.appendBits(cvs.get(i));
					}

				}
				values.put(ctx, cv);
			}
		}

		debug(ctx);
	}

	@Override
	public void exitExprDup(ExprDupContext ctx) {
		if (ctx.expr().size() == 2) {
			ConstValue dupVal = values.get(ctx.expr(0));
			ConstValue val = values.get(ctx.expr(1));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)));

			if (dupVal == null)
				debugNullConstant(ctx.expr(0));

			if (!isConstant(ctx.expr(0)))
				listener.reportError(ctx.expr(0), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr(0).getText()));

			if (dupVal == null || val == null)
				return;

			ConstValue cv;

			if (dupVal.isArray()) {
				listener.reportError(ctx.expr(0), ErrorStrings.ARRAY_DUP_INDEX_MULTI_DIM);
				return;
			}
			if (!dupVal.isNumber()) {
				listener.reportError(ctx.expr(0), ErrorStrings.ARRAY_DUP_INDEX_NAN);
				return;
			}
			BigInteger bi = dupVal.getBigInt();
			int dup = (int) bi.intValue();

			if (val.isArray()) {
				cv = new ConstValue(true);
				ArrayList<ConstValue> list = val.getValues();
				for (int i = 0; i < dup; i++) {
					cv.addAll(list);
				}
			} else {
				cv = new ConstValue(false);
				for (int i = 0; i < dup; i++) {
					cv.appendBits(val);
				}

			}

			values.put(ctx, cv);
		}

		debug(ctx);
	}

	@Override
	public void exitExprArray(ExprArrayContext ctx) {
		if (ctx.expr().size() > 0) {
			ArrayList<ConstValue> cvs = new ArrayList<ConstValue>();
			ArrayList<ParserRuleContext> nodes = new ArrayList<ParserRuleContext>();

			constant.put(ctx, true);

			for (ExprContext cec : ctx.expr())
				if (!isConstant(cec))
					constant.put(ctx, false);

			for (ExprContext cec : ctx.expr()) {
				ConstValue v = values.get(cec);

				if (v == null)
					return;
				cvs.add(v);
				nodes.add(cec);
			}

			if (cvs.size() > 0) {
				ConstValue cv = new ConstValue(true);

				ConstValue base = cvs.get(0);

				if (base == null) {
					return;
				}

				ArrayList<Integer> dim = base.getWidths();

				for (int i = 0; i < cvs.size(); i++) {
					if (!dim.equals(cvs.get(i).getWidths())) {
						listener.reportError(nodes.get(i), ErrorStrings.ARRAY_BUILDING_DIM_MISMATCH);
					} else {
						cv.addToFront(cvs.get(i));
					}
				}
				values.put(ctx, cv);
			}
		}

		debug(ctx);
	}

	@Override
	public void exitExprNegate(ExprNegateContext ctx) {
		ConstValue value = values.get(ctx.expr());

		constant.put(ctx, constant.get(ctx.expr()));

		if (value == null)
			return;

		if (value.isArray()) {
			listener.reportError(ctx, ErrorStrings.NEG_MULTI_DIM);
			return;
		}

		if (!value.isNumber()) // how do you negate something that's not a number?
			return;

		int width = value.getWidth();

		BigInteger bigI = value.getBigInt();

		if (!value.isNegative())
			width = Math.max(Util.minWidthNum(bigI.longValue()) + 1, width);

		ConstValue cv = new ConstValue(value.getBigInt().negate(), width);
		values.put(ctx, cv);

		debug(ctx);
	}

	@Override
	public void exitExprInvert(ExprInvertContext ctx) {
		ConstValue value = values.get(ctx.expr());

		constant.put(ctx, constant.get(ctx.expr()));

		if (value == null)
			return;

		if (ctx.getChild(0).getText().equals("!")) {
			values.put(ctx, ConstValue.Not(value));
		} else { // ~ operator
			values.put(ctx, ConstValue.Invert(value));
		}

		debug(ctx);
	}

	@Override
	public void exitExprMultDiv(ExprMultDivContext ctx) {
		if (ctx.getChildCount() == 3) {
			ConstValue op1 = values.get(ctx.expr(0));
			ConstValue op2 = values.get(ctx.expr(1));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)));

			if (op1 == null || op2 == null)
				return;

			String operand = ctx.getChild(1).getText();

			if (op1.isArray() || op2.isArray())
				return;

			if (operand.equals("*")) {
				if (!op1.isNumber() || !op2.isNumber())
					values.put(ctx, new ConstValue(BitValue.Bx, Util.widthOfMult(op1.getWidth(), op2.getWidth())));
				else
					values.put(ctx, new ConstValue(op1.getBigInt().multiply(op2.getBigInt()), Util.widthOfMult(op1.getWidth(), op2.getWidth())));
			} else {
				if (!op1.isNumber() || !op2.isNumber())
					values.put(ctx, new ConstValue(BitValue.Bx, op1.getWidth()));
				else
					values.put(ctx, new ConstValue(op1.getBigInt().divide(op2.getBigInt()), op1.getWidth()));
			}
		}

		debug(ctx);
	}

	@Override
	public void exitExprAddSub(ExprAddSubContext ctx) {
		if (ctx.getChildCount() == 3) {
			ConstValue op1 = values.get(ctx.expr(0));
			ConstValue op2 = values.get(ctx.expr(1));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)));

			if (op1 == null || op2 == null)
				return;

			String operand = ctx.getChild(1).getText();

			if (op1.isArray() || op2.isArray())
				return;

			if (!op1.isNumber() || !op2.isNumber())
				values.put(ctx, new ConstValue(BitValue.Bx, Math.max(op1.getWidth() + 1, op2.getWidth() + 1)));
			else if (operand.equals("+")) {
				values.put(ctx, new ConstValue(op1.getBigInt().add(op2.getBigInt()), Math.max(op1.getWidth(), op2.getWidth()) + 1));
			} else {
				values.put(ctx, new ConstValue(op1.getBigInt().subtract(op2.getBigInt()), Math.max(op1.getWidth(), op2.getWidth()) + 1));
			}
		}

		debug(ctx);
	}

	@Override
	public void exitExprShift(ExprShiftContext ctx) {
		if (ctx.getChildCount() == 3) {
			ConstValue op1 = values.get(ctx.expr(0));
			ConstValue op2 = values.get(ctx.expr(1));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)));

			if (op1 == null || op2 == null)
				return;

			String operand = ctx.getChild(1).getText();

			if (op1.isArray()) {
				listener.reportError(ctx.expr(0), ErrorStrings.SHIFT_MULTI_DIM);
				return;
			}
			if (op2.isArray()) {
				listener.reportError(ctx.expr(1), ErrorStrings.SHIFT_MULTI_DIM);
				return;
			}

			if (!op1.isNumber() || !op2.isNumber())
				values.put(ctx, new ConstValue(BitValue.Bx, op1.getWidth()));
			else {
				ConstValue v = new ConstValue(op1);
				switch (operand) {
				case ">>":
					v.shiftRight(op2.getBigInt().intValue(), false);
					break;
				case ">>>":
					v.shiftRight(op2.getBigInt().intValue(), true);
					break;
				case "<<":
				case "<<<":
					v.shiftLeft(op2.getBigInt().intValue());
					break;
				default:
					Util.log.severe("BUG: Unknown shift operator!");
					break;
				}
				values.put(ctx, v);
			}
		}

		debug(ctx);
	}

	@Override
	public void exitExprAndOr(ExprAndOrContext ctx) {
		if (ctx.getChildCount() == 3) {
			ConstValue op1 = values.get(ctx.expr(0));
			ConstValue op2 = values.get(ctx.expr(1));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)));

			if (op1 == null || op2 == null)
				return;

			String operand = ctx.getChild(1).getText();

			if (op1.isArray() || op2.isArray()) {
				if (!op1.getWidths().equals(op2.getWidths())) {
					if (operand.equals("|"))
						listener.reportError(ctx.expr(1), ErrorStrings.OR_MULTI_DIM_MISMATCH);
					else
						listener.reportError(ctx.expr(1), ErrorStrings.AND_MULTI_DIM_MISMATCH);
					return;
				}
				switch (operand) {
				case "|":
					values.put(ctx, ConstValue.Or(op1, op2));
					break;
				case "&":
					values.put(ctx, ConstValue.And(op1, op2));
					break;
				case "^":
					values.put(ctx, ConstValue.Xor(op1, op2));
					break;
				case "~|":
					values.put(ctx, ConstValue.Nor(op1, op2));
					break;
				case "~&":
					values.put(ctx, ConstValue.Nand(op1, op2));
					break;
				case "~^":
					values.put(ctx, ConstValue.Xnor(op1, op2));
					break;
				default:
					Util.log.severe("BUG: Unknown and/or/xor operator!");
				}
			} else {
				switch (operand) {
				case "|":
					values.put(ctx, new ConstValue(BitValue.or(op1.getValue(), op2.getValue())));
					break;
				case "&":
					values.put(ctx, new ConstValue(BitValue.and(op1.getValue(), op2.getValue())));
					break;
				case "^":
					values.put(ctx, new ConstValue(BitValue.xor(op1.getValue(), op2.getValue())));
					break;
				case "~|":
					values.put(ctx, new ConstValue(BitValue.nor(op1.getValue(), op2.getValue())));
					break;
				case "~&":
					values.put(ctx, new ConstValue(BitValue.nand(op1.getValue(), op2.getValue())));
					break;
				case "~^":
					values.put(ctx, new ConstValue(BitValue.xnor(op1.getValue(), op2.getValue())));
					break;
				default:
					Util.log.severe("BUG: Unknown and/or/xor operator!");
				}
			}
		}

		debug(ctx);
	}

	@Override
	public void exitExprCompress(ExprCompressContext ctx) {
		if (ctx.expr() != null) {
			ConstValue op = values.get(ctx.expr());

			constant.put(ctx, constant.get(ctx.expr()));

			if (op == null)
				return;

			if (op.isArray()) {
				return;
			}

			switch (ctx.getChild(0).getText()) {
			case "|":
				values.put(ctx, new ConstValue(op.orReduce()));
				break;
			case "&":
				values.put(ctx, new ConstValue(op.andReduce()));
				break;
			case "^":
				values.put(ctx, new ConstValue(op.xorReduce()));
				break;
			case "~|":
				values.put(ctx, new ConstValue(op.norReduce()));
				break;
			case "~&":
				values.put(ctx, new ConstValue(op.nandReduce()));
				break;
			case "~^":
				values.put(ctx, new ConstValue(op.xnorReduce()));
				break;
			default:
				Util.log.severe("BUG: Unknown reduction operator!");
			}
		}

		debug(ctx);
	}

	@Override
	public void exitExprCompare(ExprCompareContext ctx) {
		if (ctx.getChildCount() == 3) {
			ConstValue op1 = values.get(ctx.expr(0));
			ConstValue op2 = values.get(ctx.expr(1));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)));

			if (op1 == null || op2 == null)
				return;

			String operand = ctx.getChild(1).getText();

			ConstValue cv = new ConstValue(false);

			switch (operand) {
			case "<":
				if (op1.isArray())
					listener.reportError(ctx.expr(0), ErrorStrings.OP_LT_ARRAY);
				if (op2.isArray())
					listener.reportError(ctx.expr(1), ErrorStrings.OP_LT_ARRAY);
				if (op1.isArray() || op2.isArray())
					return;

				cv.setValue(op1.lessThan(op2));
				break;
			case ">":
				if (op1.isArray())
					listener.reportError(ctx.expr(0), ErrorStrings.OP_GT_ARRAY);
				if (op2.isArray())
					listener.reportError(ctx.expr(1), ErrorStrings.OP_GT_ARRAY);
				if (op1.isArray() || op2.isArray())
					return;

				cv.setValue(op2.lessThan(op1));
				break;
			case "==":

				if (op1.isArray()) {
					if (!op1.getWidths().equals(op2.getWidths())) {
						listener.reportError(ctx.expr(1), ErrorStrings.OP_EQ_DIM_MISMATCH);
						return;
					}
				}
				cv.setValue(ConstValue.Equal(op1, op2));
				break;
			case "!=":
				if (op1.isArray()) {
					if (!op1.getWidths().equals(op2.getWidths())) {
						listener.reportError(ctx.expr(1), ErrorStrings.OP_NEQ_DIM_MISMATCH);
						return;
					}
				}
				cv.setValue(ConstValue.Equal(op1, op2).not());
				break;
			case ">=":
				if (op1.isArray())
					listener.reportError(ctx.expr(0), ErrorStrings.OP_GTE_ARRAY);
				if (op2.isArray())
					listener.reportError(ctx.expr(1), ErrorStrings.OP_GTE_ARRAY);
				if (op1.isArray() || op2.isArray())
					return;

				cv.setValue(op2.lessThan(op1).or(ConstValue.Equal(op1, op2)));
				break;
			case "<=":
				if (op1.isArray())
					listener.reportError(ctx.expr(0), ErrorStrings.OP_LTE_ARRAY);
				if (op2.isArray())
					listener.reportError(ctx.expr(1), ErrorStrings.OP_LTE_ARRAY);
				if (op1.isArray() || op2.isArray())
					return;

				cv.setValue(op1.lessThan(op2).or(ConstValue.Equal(op1, op2)));
				break;
			}
			values.put(ctx, cv);
		}

		debug(ctx);
	}

	@Override
	public void exitExprLogical(ExprLogicalContext ctx) {
		if (ctx.getChildCount() == 3) {
			ConstValue op1 = values.get(ctx.expr(0));
			ConstValue op2 = values.get(ctx.expr(1));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)));

			if (op1 == null || op2 == null)
				return;

			String operand = ctx.getChild(1).getText();

			ConstValue cv = new ConstValue(0);

			if (operand.equals("||")) {
				cv.setValue(!ConstValue.Zero(op1) || !ConstValue.Zero(op2) ? BitValue.B1 : BitValue.B0);
			} else { // && operator
				cv.setValue(!ConstValue.Zero(op1) && !ConstValue.Zero(op2) ? BitValue.B1 : BitValue.B0);
			}

			values.put(ctx, cv);
		}

		debug(ctx);
	}

	@Override
	public void exitExprTernary(ExprTernaryContext ctx) {
		if (ctx.expr().size() == 3) {
			ConstValue cond = values.get(ctx.expr(0));
			ConstValue op1 = values.get(ctx.expr(1));
			ConstValue op2 = values.get(ctx.expr(2));

			constant.put(ctx, isConstant(ctx.expr(0)) && isConstant(ctx.expr(1)) && isConstant(ctx.expr(2)));

			if (cond == null || op1 == null || op2 == null) {
				return;
			}

			if (!cond.isZero())
				values.put(ctx, op1);
			else
				values.put(ctx, op2);
		}

		debug(ctx);
	}

	/************** end expr *****************/

	public ConstValue parseExpr(String text) {
		return parseExpr(text, this);
	}

	public static ConstValue parseExpr(String text, ConstExprParser provider) {
		return parseExpr(text, provider.paramsParser, provider.constParser, provider.bitWidthChecker);
	}

	private static ConstProvider dummyConstProvider = new ConstProvider() {
		@Override
		public ConstValue getValue(String s) {
			return null;
		}
	};

	public static ConstValue parseExpr(String text, ConstProvider constants, WidthProvider width) {
		return parseExpr(text, null, constants, width);
	}

	public static ConstProvider globalConstProvider = new ConstProvider() {

		@Override
		public ConstValue getValue(String s) {
			HashMap<String, List<Constant>> consts = MainWindow.getGlobalConstants();

			String[] sp = s.split("\\.");
			if (sp.length == 2) {
				List<Constant> list = consts.get(sp[0]);
				if (list != null) {
					return Util.getByName(list, sp[1]).getValue();
				}
			}

			return null;
		}
	};

	public static ConstValue parseExpr(String text, ConstProvider params, ConstProvider constants, WidthProvider width) {
		if (text == null)
			return null;

		ConstExprParser cep = new ConstExprParser(null);

		if (params == null)
			params = dummyConstProvider;

		if (constants == null)
			constants = dummyConstProvider;

		if (width == null)
			width = WidthProvider.dummyProvider;

		final WidthProvider baseProvider = width;
		final ConstProvider cp = constants;
		final ConstProvider pp = params;

		// The Width provider doesn't have widths for constants
		// so use the constant and params providers for widths in
		// those cases.
		WidthProvider wp = new WidthProvider() {

			@Override
			public SignalWidth getWidth(String signal) {
				SignalWidth sw = baseProvider.getWidth(signal);
				if (sw == null) {
					ConstValue cv = cp.getValue(signal);
					if (cv == null)
						cv = pp.getValue(signal);
					if (cv != null)
						return cv.getArrayWidth();
				}
				return sw;
			}

			@Override
			public SignalWidth getWidth(ParserRuleContext ctx) {
				return baseProvider.getWidth(ctx);
			}

			@Override
			public SignalWidth checkWidthMap(String signal) {
				SignalWidth sw = baseProvider.checkWidthMap(signal);
				if (sw == null) {
					ConstValue cv = cp.getValue(signal);
					if (cv == null)
						cv = pp.getValue(signal);
					if (cv != null)
						return cv.getArrayWidth();
				}
				return sw;
			}
		};

		BoundsParser bp = new BoundsParser(cep, wp, null);
		cep.setBitWidthChecker(wp);
		cep.setBoundsProvider(bp);
		cep.setParamsParser(params);
		cep.setConstParser(constants);

		CharStream input = CharStreams.fromString(text);
		LucidLexer lexer = new LucidLexer(input);
		lexer.removeErrorListeners();
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		LucidParser parser = new LucidParser(tokens);
		parser.removeErrorListeners();

		parser.addParseListener(bp);
		parser.addParseListener(cep);

		cep.enterSource(null);
		bp.enterSource(null);

		ParseTree tree = parser.expr();

		return cep.getValue(tree);
	}
}
