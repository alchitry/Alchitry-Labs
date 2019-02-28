package com.alchitry.labs.parsers.lucid.toVerilog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.alchitry.labs.Util;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Param;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.parsers.lucid.AssignBlock;
import com.alchitry.labs.parsers.lucid.Lucid;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.AlwaysCaseContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.AlwaysForContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.AlwaysIfContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.AlwaysStatContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Always_blockContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Always_statContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Array_indexContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Array_sizeContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Assign_blockContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Assign_statContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.BitSelectorConstContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.BitSelectorFixWidthContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Bit_selectionContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Bit_selectorContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.BlockContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Case_elemContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Case_statContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ConnectionContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Const_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Dff_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Dff_singleContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Else_statContext;
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
import com.alchitry.labs.parsers.lucid.parser.LucidParser.For_statContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Fsm_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.FunctionContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.If_statContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Inout_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Input_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Inst_consContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ModuleContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Module_bodyContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Module_instContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.NameContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.NumberContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Output_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_conContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_listContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Param_nameContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Port_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Port_listContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Sig_conContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Sig_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.SignalContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.SourceContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatAlwaysContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatAssignContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatConstContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatDFFContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatFSMContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatModuleInstContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatSigContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatStructContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.StatVarContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Type_decContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Var_assignContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Var_decContext;
import com.alchitry.labs.parsers.tools.lucid.ArrayBounds;
import com.alchitry.labs.parsers.tools.lucid.BitWidthChecker;
import com.alchitry.labs.parsers.tools.lucid.ConstExprParser;
import com.alchitry.labs.parsers.tools.lucid.ConstProvider;
import com.alchitry.labs.parsers.tools.lucid.LucidExtractor;
import com.alchitry.labs.parsers.tools.verilog.VerilogConstExprParser;
import com.alchitry.labs.parsers.types.Connection;
import com.alchitry.labs.parsers.types.Constant;
import com.alchitry.labs.parsers.types.Dff;
import com.alchitry.labs.parsers.types.Fsm;
import com.alchitry.labs.parsers.types.SyncLogic;
import com.alchitry.labs.parsers.types.Var;
import com.alchitry.labs.project.Primitive;
import com.alchitry.labs.project.Primitive.Parameter;
import com.alchitry.labs.tools.ParserCache;

public class LucidToVerilog extends LucidBaseListener {
	private Stack<AssignBlock> assignBlocks;
	private HashSet<Dff> alwaysDffs;
	private HashSet<Fsm> alwaysFsms;
	private List<Module> modules;
	private List<Param> params;
	private List<InstModule> instModules;
	private ConstExprParser exprParser;
	private LucidExtractor extractor;
	private BitWidthChecker bitWidthChecker;

	private InstModule thisModule;
	private List<InstModule> projModules;

	public static String convert(String file, List<Module> modules, InstModule im, List<InstModule> list) {
		LucidExtractor lec = new LucidExtractor(im);
		lec.setModuleList(modules);
		LucidToVerilog l2v = new LucidToVerilog(modules, lec, im, list);

		List<ParseTreeListener> listeners = new ArrayList<ParseTreeListener>();
		lec.addToParser(listeners);
		listeners.add(l2v);

		ParseTree tree = ParserCache.walk(file, listeners);

		return l2v.getVerilog(tree);
	}

	protected ParseTreeProperty<String> verilog = new ParseTreeProperty<String>();

	private int tabCount = 0;

	public LucidToVerilog(List<Module> modules, LucidExtractor lec, InstModule thisModule, List<InstModule> list) {
		this.modules = modules;
		this.thisModule = thisModule;
		projModules = list;
		exprParser = lec.getExprParser();
		extractor = lec;
		bitWidthChecker = lec.getBitWidthChecker();
	}

	public LucidToVerilog(List<Module> modules, List<Param> params, LucidExtractor lec) {
		this.modules = modules;
		this.params = params;
		exprParser = lec.getExprParser();
		extractor = lec;
		bitWidthChecker = lec.getBitWidthChecker();
	}

	public LucidToVerilog(List<Module> modules, List<Param> params) {
		this.modules = modules;
		this.params = params;
	}

	public String getVerilog(ParseTree ctx) {
		return verilog.get(ctx);
	}

	private void newLine(StringBuilder sb) {
		sb.append("\n");
		for (int i = 0; i < tabCount; i++)
			sb.append("  ");
	}

	private String generateSyncBlocks() {
		StringBuilder sb = new StringBuilder();

		HashMap<ExprContext, HashMap<ExprContext, ArrayList<SyncLogic>>> hm = new HashMap<ExprContext, HashMap<ExprContext, ArrayList<SyncLogic>>>();

		for (Dff d : extractor.getDffs()) {
			HashMap<ExprContext, ArrayList<SyncLogic>> hash = hm.get(d.getClk());
			if (hash == null) {
				hash = new HashMap<ExprContext, ArrayList<SyncLogic>>();
				hm.put(d.getClk(), hash);
			}
			ArrayList<SyncLogic> list = hash.get(d.getRst());
			if (list == null) {
				list = new ArrayList<SyncLogic>();
				hash.put(d.getRst(), list);
			}
			list.add(d);
		}

		for (Fsm f : extractor.getFsms()) {
			HashMap<ExprContext, ArrayList<SyncLogic>> hash = hm.get(f.getClk());
			if (hash == null) {
				hash = new HashMap<ExprContext, ArrayList<SyncLogic>>();
				hm.put(f.getClk(), hash);
			}
			ArrayList<SyncLogic> list = hash.get(f.getRst());
			if (list == null) {
				list = new ArrayList<SyncLogic>();
				hash.put(f.getRst(), list);
			}
			list.add(f);
		}

		for (ExprContext clk : hm.keySet()) {
			newLine(sb);
			newLine(sb);
			sb.append("always @(posedge ").append(verilog.get(clk)).append(") begin");
			tabCount++;
			HashMap<ExprContext, ArrayList<SyncLogic>> h = hm.get(clk);
			boolean first = true;
			for (ExprContext rst : h.keySet()) {
				ArrayList<SyncLogic> list = h.get(rst);
				if (!first)
					newLine(sb);
				if (rst != null) {
					newLine(sb);
					sb.append("if (").append(verilog.get(rst)).append(" == 1'b1) begin");
					tabCount++;
					for (SyncLogic l : list) {
						newLine(sb);
						sb.append("M_").append(l.getName()).append("_q <= ").append(l.getInit().toVerilog()).append(";");
					}
					tabCount--;
					newLine(sb);
					sb.append("end else begin");
					tabCount++;
					for (SyncLogic l : list) {
						newLine(sb);
						sb.append("M_").append(l.getName()).append("_q <= ").append("M_").append(l.getName()).append("_d;");
					}
					tabCount--;
					newLine(sb);
					sb.append("end");
				} else {
					for (SyncLogic l : list) {
						newLine(sb);
						sb.append("M_").append(l.getName()).append("_q <= ").append("M_").append(l.getName()).append("_d;");
					}
				}
				first = false;
			}
			tabCount--;
			newLine(sb);
			sb.append("end");
			newLine(sb);
		}

		return sb.toString();
	}

	@Override
	public void exitSource(SourceContext ctx) {
		StringBuilder sb = new StringBuilder();
		for (ModuleContext c : ctx.module())
			sb.append(verilog.get(c)).append("\n");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void enterModule(ModuleContext ctx) {
		tabCount++;

		assignBlocks = new Stack<AssignBlock>();
		instModules = new ArrayList<InstModule>();
	}

	private boolean hasParentType(RuleContext ctx, Class<?> type) {
		while (ctx != null) {
			if (ctx.getClass() == type)
				return true;
			ctx = ctx.getParent();
		}
		return false;
	}

	@Override
	public void exitNumber(NumberContext ctx) {
		String text = ctx.getChild(0).getText();
		if (!text.startsWith("\"")) { // if it starts with " then it's a string
			text = text.toLowerCase();
			int idx = text.indexOf("h");
			String width;
			if (idx >= 0) {
				if (idx == 0)
					width = Integer.toString((text.length() - 1) * 4);
				else
					width = text.substring(0, idx);
				text = width + "'h" + text.substring(idx + 1);
			} else {
				idx = text.indexOf("b");
				if (idx >= 0) {
					if (idx == 0)
						width = Integer.toString(text.length() - 1);
					else
						width = text.substring(0, idx);
					text = width + "'b" + text.substring(idx + 1);
				} else {
					idx = text.indexOf("d");
					String number;
					if (idx >= 0) {
						width = text.substring(0, idx);
						number = text.substring(idx + 1);
						text = width + "'d" + number;
					} else if (!hasParentType(ctx, Param_decContext.class) && !hasParentType(ctx, Param_conContext.class)) {
						number = text;
						try {
							long i = Long.parseLong(number);
							int size = (int) Util.minWidthNum(i);
							width = Integer.toString(size);
							text = width + "'d" + number;
						} catch (NumberFormatException e) {
							Util.println("Error: The constant \"" + text + "\" could not be parsed!", true);
							text = "ERROR";
						}
					}
				}
			}
		}
		verilog.put(ctx, text);
	}

	private int widthToInt(SignalWidth w) {
		if (!w.isFixed()) {
			Util.log.severe("The width " + w + " is not a fixed width!");
			return 1;
		}
		return w.getTotalWidth();
	}

	private void addInouts(StringBuilder sb) {
		List<Sig> io = new ArrayList<>(extractor.getInouts());
		io.removeAll(extractor.getConnectedInouts());
		for (Sig inout : io) {
			sb.append("reg ");

			if (inout.getWidth().getDepth() > 0)
				sb.append("[").append(widthToInt(inout.getWidth()) - 1).append(":0] ");

			String enName = "IO_" + inout.getName() + "_enable";
			sb.append(enName).append(";");
			newLine(sb);

			sb.append("wire ");

			if (inout.isSigned())
				sb.append("signed ");

			if (inout.getWidth().getDepth() > 0)
				sb.append("[").append(widthToInt(inout.getWidth()) - 1).append(":0] ");

			String readName = "IO_" + inout.getName() + "_read";
			sb.append(readName).append(";");
			newLine(sb);

			sb.append("reg ");

			if (inout.isSigned())
				sb.append("signed ");

			if (inout.getWidth().getDepth() > 0)
				sb.append("[").append(widthToInt(inout.getWidth()) - 1).append(":0] ");

			String writeName = "IO_" + inout.getName() + "_write";
			sb.append(writeName).append(";");
			newLine(sb);

			// assign IO_iosig = IO_iosig_enable ? IO_iosig_write : {WIDTH{1'bz}};
			int bits = 1;
			if (inout.getWidth().getDepth() > 0)
				bits = widthToInt(inout.getWidth());

			if (bits > 1) {
				sb.append("genvar GEN_").append(inout.getName()).append(";");
				newLine(sb);
				sb.append("generate");
				tabCount++;
				newLine(sb);
				sb.append("for (GEN_").append(inout.getName()).append(" = 0; GEN_").append(inout.getName()).append(" < ").append(bits).append("; GEN_").append(inout.getName())
						.append(" = GEN_").append(inout.getName()).append(" + 1) begin");
				tabCount++;
				newLine(sb);
				sb.append("assign ").append(inout.getName()).append("[GEN_").append(inout.getName()).append("] = ").append(enName).append("[GEN_").append(inout.getName());
				sb.append("] ? ").append(writeName).append("[GEN_").append(inout.getName()).append("] : 1'bz;");
				tabCount--;
				newLine(sb);
				sb.append("end");
				tabCount--;
				newLine(sb);
				sb.append("endgenerate");
			} else {
				sb.append("assign ").append(inout.getName()).append(" = ").append(enName);
				sb.append(" ? ").append(writeName).append(" : 1'bz;");
			}
			newLine(sb);

			// assign IO_iosig_reag = IO_iosig;
			sb.append("assign ").append(readName).append(" = ").append(inout.getName()).append(";");
			newLine(sb);
		}
	}

	private void addPamameterHeader(StringBuilder sb) {
		if (thisModule.getParams().size() > 0) {
			sb.append("/*\n   Parameters:\n");
			for (Param p : thisModule.getParams()) {
				sb.append("     ").append(p.getName()).append(" = ").append(p.getStringValue()).append("\n");
			}
			sb.append("*/\n");
		}
	}

	private void addParameters(StringBuilder sb) {
		for (Param p : thisModule.getParams()) {
			if (p.getValue() != null || p.getStringValue() != null) {
				sb.append("localparam ").append(p.getName()).append(" = ");
				if (p.getValue() != null) {
					sb.append(p.getValue().toVerilog());
				} else {
					if (!p.valueSet()) // if value isn't set, convert the Lucid default to Verilog
						sb.append(exprParser.parseExpr(p.getStringValue()).toVerilog());
					else // if CV is null but value is set, the string value must be Verilog already
						sb.append(p.getStringValue());
				}
				sb.append(";");
				newLine(sb);
			}
		}
	}

	@Override
	public void exitModule(ModuleContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(LucidConvertedHeader.header).append("\n");
		addPamameterHeader(sb);
		sb.append("module");
		sb.append(" " + ctx.name().getText() + "_" + projModules.indexOf(thisModule) + " ");
		// Lucid doesn't use parameter lists
		// Instead the values are pre-processed into different modules
		// if (ctx.param_list() != null)
		// sb.append(verilog.get(ctx.param_list()));
		sb.append(verilog.get(ctx.port_list()));
		newLine(sb);
		newLine(sb);
		addParameters(sb);
		addInouts(sb);
		sb.append(verilog.get(ctx.module_body()));
		sb.append(generateSyncBlocks());
		tabCount--;
		newLine(sb);
		sb.append("endmodule");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void enterParam_list(Param_listContext ctx) {
		tabCount++;
	}

	@Override
	public void exitParam_list(Param_listContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("#(");
		List<Param_decContext> params = ctx.param_dec();
		int size = params.size();
		for (int i = 0; i < size - 1; i++) {
			sb.append(verilog.get(params.get(i)));
			sb.append(",");
		}
		sb.append(verilog.get(params.get(size - 1)));
		tabCount--;
		newLine(sb);
		sb.append(")");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitParam_dec(Param_decContext ctx) {
		verilog.put(ctx, verilog.get(ctx.param_name()));
	}

	@Override
	public void exitParam_name(Param_nameContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append("parameter ");
		sb.append(ctx.name().getText());
		sb.append(" = ");
		if (ctx.expr() != null) {
			sb.append(verilog.get(ctx.expr()));
		} else {
			sb.append("1");
		}
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void enterPort_list(Port_listContext ctx) {
		tabCount++;
	}

	@Override
	public void exitPort_list(Port_listContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		List<Port_decContext> ports = ctx.port_dec();
		int size = ports.size();
		for (int i = 0; i < size - 1; i++) {
			sb.append(verilog.get(ports.get(i)));
			sb.append(",");
		}
		sb.append(verilog.get(ports.get(size - 1)));
		tabCount--;
		newLine(sb);
		sb.append(");");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitPort_dec(Port_decContext ctx) {
		verilog.put(ctx, verilog.get(ctx.getChild(0))); // only one child
	}

	private String ioDec(String type, boolean signed, SignalWidth sw, String name, boolean array) {
		if (sw == null) {
			Util.log.severe("Width of io was null! " + name);
			return "";
		}

		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(type).append(" ");

		if (signed)
			sb.append("signed ");

		if (!sw.isFixed()) {
			Util.log.severe("Width of io isn't fixed! " + name);
			return "";
		}

		int bits = sw.getTotalWidth();
		if (bits > 1 || array)
			sb.append("[").append(bits - 1).append(":0] ");

		sb.append(sanitize(name));

		return sb.toString();
	}

	@Override
	public void exitInput_dec(Input_decContext ctx) {
		verilog.put(ctx, ioDec("input", ctx.SIGNED() != null, bitWidthChecker.getWidth(ctx), ctx.name().getText(), ctx.array_size().size() != 0));
	}

	@Override
	public void exitOutput_dec(Output_decContext ctx) {
		verilog.put(ctx, ioDec("output reg", ctx.SIGNED() != null, bitWidthChecker.getWidth(ctx), ctx.name().getText(), ctx.array_size().size() != 0));
	}

	@Override
	public void exitInout_dec(Inout_decContext ctx) {
		verilog.put(ctx, ioDec("inout", ctx.SIGNED() != null, bitWidthChecker.getWidth(ctx), ctx.name().getText(), ctx.array_size().size() != 0));
	}

	@Override
	public void exitFunction(FunctionContext ctx) {
		ConstValue cv = getValue(ctx);

		String function = ctx.FUNCTION_ID().getText();

		if (cv != null) {
			verilog.put(ctx, cv.toVerilog());
		} else if (ctx.expr().size() > 0) {
			if (function.equals("$flatten")) {
				verilog.put(ctx, verilog.get(ctx.expr(0)));
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(ctx.FUNCTION_ID().getText()).append("(");
				sb.append(verilog.get(ctx.expr(0)));
				for (int i = 1; i < ctx.expr().size(); i++)
					sb.append(", ").append(verilog.get(ctx.expr(i)));
				sb.append(")");
				verilog.put(ctx, sb.toString());
			}
		}
	}

	@Override
	public void exitExprNum(ExprNumContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.number()));
	}

	@Override
	public void exitExprSignal(ExprSignalContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.signal()));
	}

	@Override
	public void exitExprFunction(ExprFunctionContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.function()));
	}

	@Override
	public void exitExprGroup(ExprGroupContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, "(" + verilog.get(ctx.expr()) + ")");
	}

	@Override
	public void exitExprNegate(ExprNegateContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, "-" + verilog.get(ctx.expr()));
	}

	@Override
	public void exitExprInvert(ExprInvertContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, ctx.getChild(0).getText() + verilog.get(ctx.expr()));
	}

	@Override
	public void exitExprMultDiv(ExprMultDivContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.expr(0)) + " " + ctx.getChild(1).getText() + " " + verilog.get(ctx.expr(1)));
	}

	@Override
	public void exitExprAddSub(ExprAddSubContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.expr(0)) + " " + ctx.getChild(1).getText() + " " + verilog.get(ctx.expr(1)));
	}

	@Override
	public void exitExprShift(ExprShiftContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.expr(0)) + " " + ctx.getChild(1).getText() + " " + verilog.get(ctx.expr(1)));
	}

	@Override
	public void exitExprAndOr(ExprAndOrContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.expr(0)) + " " + ctx.getChild(1).getText() + " " + verilog.get(ctx.expr(1)));
	}

	@Override
	public void exitExprCompress(ExprCompressContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, "(" + ctx.getChild(0).getText() + verilog.get(ctx.expr()) + ")");
	}

	@Override
	public void exitExprCompare(ExprCompareContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.expr(0)) + " " + ctx.getChild(1).getText() + " " + verilog.get(ctx.expr(1)));
	}

	@Override
	public void exitExprLogical(ExprLogicalContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.expr(0)) + " " + ctx.getChild(1).getText() + " " + verilog.get(ctx.expr(1)));
	}

	@Override
	public void exitExprTernary(ExprTernaryContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null)
			verilog.put(ctx, cv.toVerilog());
		else
			verilog.put(ctx, verilog.get(ctx.expr(0)) + " ? " + verilog.get(ctx.expr(1)) + " : " + verilog.get(ctx.expr(2)));
	}

	@Override
	public void exitExprConcat(ExprConcatContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null) {
			verilog.put(ctx, cv.toVerilog());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			boolean first = true;
			for (ExprContext ec : ctx.expr()) {
				if (!first) {
					sb.append(", ");
				} else {
					first = false;
				}
				sb.append(verilog.get(ec));
			}
			sb.append("}");
			verilog.put(ctx, sb.toString());
		}
	}

	@Override
	public void exitExprArray(ExprArrayContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null) {
			verilog.put(ctx, cv.toVerilog());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			boolean first = true;
			for (ExprContext ec : ctx.expr()) {
				if (!first) {
					sb.append(", ");
				} else {
					first = false;
				}
				sb.append(verilog.get(ec));
			}
			sb.append("}");
			verilog.put(ctx, sb.toString());
		}
	}

	@Override
	public void exitExprDup(ExprDupContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null) {
			verilog.put(ctx, cv.toVerilog());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append(verilog.get(ctx.expr(0)));
			sb.append("{");
			sb.append(verilog.get(ctx.expr(1)));
			sb.append("}}");
			verilog.put(ctx, sb.toString());
		}
	}

	@Override
	public void exitVar_assign(Var_assignContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null) {
			verilog.put(ctx, cv.toVerilog());
		} else {
			if (ctx.assign_stat() != null) {
				verilog.put(ctx, verilog.get(ctx.assign_stat()));
			} else {
				String sig = verilog.get(ctx.signal());
				String op = ctx.getChild(1).getText().equals("++") ? "+" : "-";
				verilog.put(ctx, sig + " = " + sig + " " + op + " 1");
			}
		}
	}

	@Override
	public void exitArray_size(Array_sizeContext ctx) {
		ConstValue cv = getValue(ctx);

		if (cv != null) {
			verilog.put(ctx, cv.toVerilog());
		} else {
			verilog.put(ctx, "[" + verilog.get(ctx.expr()) + "-1:0]");
		}
	}

	@Override
	public void exitModule_body(Module_bodyContext ctx) {
		StringBuilder sb = new StringBuilder();
		for (StatContext stat : ctx.stat()) {
			newLine(sb);
			sb.append(verilog.get(stat));
		}
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitStatStruct(StatStructContext ctx) {
		verilog.put(ctx, ""); // nothing to see here, move along
	}

	@Override
	public void exitStatConst(StatConstContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(verilog.get(ctx.const_dec()));
		sb.append(";");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitStatFSM(StatFSMContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(verilog.get(ctx.fsm_dec()));
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitStatDFF(StatDFFContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(verilog.get(ctx.dff_dec()));
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitStatModuleInst(StatModuleInstContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(verilog.get(ctx.module_inst()));
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitStatSig(StatSigContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(verilog.get(ctx.sig_dec()));
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitStatVar(StatVarContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(verilog.get(ctx.var_dec()));
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitStatAssign(StatAssignContext ctx) {
		verilog.put(ctx, verilog.get(ctx.assign_block()));
	}

	@Override
	public void exitStatAlways(StatAlwaysContext ctx) {
		StringBuilder sb = new StringBuilder();
		newLine(sb);
		sb.append(verilog.get(ctx.always_block()));
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitConst_dec(Const_decContext ctx) {
		verilog.put(ctx, "localparam " + ctx.name().getText() + " = " + verilog.get(ctx.expr()));
	}

	@Override
	public void enterAssign_block(Assign_blockContext ctx) {
		assignBlocks.push(new AssignBlock());
	}

	@Override
	public void exitAssign_block(Assign_blockContext ctx) {
		assignBlocks.pop();
		StringBuilder sb = new StringBuilder();
		for (ParseTree tree : ctx.children) {
			if (tree.getClass() == Fsm_decContext.class) {
				sb.append(verilog.get(tree));
			} else if (tree.getClass() == Dff_decContext.class) {
				newLine(sb);
				sb.append(verilog.get(tree));
			} else if (tree.getClass() == Module_instContext.class) {
				newLine(sb);
				sb.append(verilog.get(tree));
			} else if (tree.getClass() == Assign_blockContext.class) {
				sb.append(verilog.get(tree));
			}
		}
		verilog.put(ctx, sb.toString());
	}

	private void addWidth(StringBuilder sb, List<Param> modParams, List<String> dimensions, Sig sig, boolean lucid) {
		boolean first = true;
		for (String d : dimensions) {
			if (!first)
				sb.append("*");
			else
				first = false;
			sb.append("(" + d + "+0)");
		}

		SignalWidth sw = convertToFixed(sig.getWidth(), modParams);
		int tw = sw.getTotalWidth();
		if (tw > 1 || first) {
			if (!first)
				sb.append("*");
			sb.append(tw);
		}

	}

	private void addDimension(StringBuilder sb, List<Param> modParams, List<String> dimensions, Sig sig, boolean lucid) {
		if (dimensions.size() > 0 || sig.getWidth().getDepth() > 0) {
			sb.append("[");
			addWidth(sb, modParams, dimensions, sig, lucid);
			sb.append("-1:0] ");
		}
	}

	private void addWidth(StringBuilder sb, List<Param> modParams, SignalWidth width) {
		boolean f = true;
		while (width != null) {
			if (!f)
				sb.append("*");
			else
				f = false;

			if (width.isText()) {
				String w = Param.replaceParam(width.getText(), modParams);
				VerilogConstExprParser vcep = new VerilogConstExprParser(thisModule);
				ConstValue cv = vcep.parseExpr(w);
				if (cv != null)
					sb.append("(" + cv.toVerilog() + ")");
				else
					sb.append("(" + w + ")");
			} else {
				boolean m = false;
				sb.append("(");
				for (int d : width.getDimensions()) {
					if (m)
						sb.append("*");
					else
						m = true;
					sb.append(d);
				}
				sb.append(")");
			}
			width = width.getNext();
		}
	}

	@Override
	public void exitModule_inst(Module_instContext ctx) {
		if (modules == null)
			return;

		StringBuilder sb = new StringBuilder();
		String instName = ctx.name(1).getText(); // name of instance

		Module module = Util.getByName(modules, ctx.name(0).getText());
		if (module == null) {
			Util.log.severe("Error: unknown module " + ctx.name(0).getText());
			return;
		}

		InstModule instModule = new InstModule(instName, module, ctx);
		instModules.add(instModule);

		ArrayList<Sig> unassignedInputs = new ArrayList<>(module.getInputs());
		ArrayList<Sig> outputs = module.getOutputs();
		ArrayList<Sig> inouts = module.getInouts();

		instModule.addParams(module.getParams());

		final List<Param> modParams = instModule.getParams();
		ArrayList<Connection> params = new ArrayList<>();
		ArrayList<Connection> connetions = new ArrayList<>();

		for (AssignBlock block : assignBlocks) {
			if (block != null) {
				for (Connection c : block.connections) {
					if (!c.param) { // is a signal
						Util.removeByName(unassignedInputs, c.port);
						Util.removeByName(inouts, c.port);
						// if (Util.removeByName(inouts, c.port))
						// Util.removeByName(this.inouts, c.signal);
						connetions.add(c);
					} else { // is a param
						params.add(c);
					}
				}
			}
		}

		for (Connection p : params) { // set the value for each specified parameter
			int i = Util.findByName(modParams, p.port);
			if (i >= 0) {
				Param param = modParams.get(i);
				param.setValue(p.signal);
				param.setValue(exprParser.getValue(p.signalNode));
			}
		}

		for (Param p : modParams) {
			if (!p.valueSet()) {
				p.setValue(exprParser.parseExpr(p.getStringValue()));
			}
		}

		for (Array_sizeContext asc : ctx.array_size()) {
			instModule.addWidth(verilog.get(asc.expr()));
		}

		int id = projModules.indexOf(instModule);

		// id will be -1 if the module is a core from coreGen
		// if (id == -1) {
		// Util.println("Error couldn't find module: " + instModule.toString(), true);
		// return;
		// }

		boolean isLucid = false;

		if (id >= 0)
			isLucid = projModules.get(id).isLucid();

		List<String> dimensions = instModule.getWidths();

		for (Sig inout : inouts) {
			sb.append("wire ");

			if (inout.isSigned())
				sb.append("signed ");

			addDimension(sb, modParams, dimensions, inout, isLucid);

			String ioName = "MIO_" + instName + "_" + inout.getName();
			sb.append(ioName).append(";");
			newLine(sb);

			sb.append("reg ");

			addDimension(sb, modParams, dimensions, inout, isLucid);

			String enName = "MIO_" + instName + "_" + inout.getName() + "_enable";
			sb.append(enName).append(";");
			newLine(sb);

			sb.append("wire ");

			if (inout.isSigned())
				sb.append("signed ");

			addDimension(sb, modParams, dimensions, inout, isLucid);

			String readName = "MIO_" + instName + "_" + inout.getName() + "_read";
			sb.append(readName).append(";");
			newLine(sb);

			sb.append("reg ");

			if (inout.isSigned())
				sb.append("signed ");

			addDimension(sb, modParams, dimensions, inout, isLucid);

			String writeName = "MIO_" + instName + "_" + inout.getName() + "_write";
			sb.append(writeName).append(";");
			newLine(sb);

			// assign M_iosig = M_iosig_enable ? M_iosig_write : {WIDTH{1'bz}};

			sb.append("assign ").append(sanitize(ioName)).append(" = ").append(enName);
			sb.append(" ? ").append(writeName).append(" : {");
			addWidth(sb, modParams, dimensions, inout, isLucid);
			sb.append("{1'bz}};");
			newLine(sb);

			// assign M_iosig_reag = M_iosig;
			sb.append("assign ").append(readName).append(" = ").append(sanitize(ioName)).append(";");
			newLine(sb);
		}

		for (Sig output : outputs) {
			sb.append("wire ");

			if (output.isSigned())
				sb.append("signed ");

			addDimension(sb, modParams, dimensions, output, isLucid);

			sb.append("M_").append(instName).append("_").append(sanitize(output.getName())).append(";");
			newLine(sb);
		}

		for (Sig input : unassignedInputs) {
			sb.append("reg ");

			if (input.isSigned())
				sb.append("signed ");

			addDimension(sb, modParams, dimensions, input, isLucid);

			sb.append("M_").append(instName).append("_").append(sanitize(input.getName())).append(";");
			newLine(sb);
		}

		if (dimensions.size() > 0) {
			newLine(sb);
			sb.append("genvar ");
			for (int i = 0; i < dimensions.size(); i++) {
				if (i > 0)
					sb.append(", ");
				sb.append("GEN_").append(instName).append(i);
			}
			sb.append(";");

			newLine(sb);
			sb.append("generate");
			newLine(sb);
			for (int i = 0; i < dimensions.size(); i++) {
				String index = "GEN_" + instName + i;
				sb.append("for (" + index + "=0;" + index + "<" + dimensions.get(i) + ";" + index + "=" + index + "+1) begin: " + instName + "_gen_" + i);
				tabCount++;
				newLine(sb);
			}
		}

		if (module.getPrimitive() != null)
			sb.append(module.getPrimitive().getName());
		else
			sb.append(module.getName());

		if (id >= 0 && !projModules.get(id).getType().isPrimitive() && !projModules.get(id).getType().isNgc())
			sb.append("_").append(id);

		sb.append(" ");

		if (!isLucid) { // Lucid modules don't use parameter lists
			if (params.size() > 0) {
				sb.append("#(");
				boolean first = true;
				for (Connection p : params) {
					if (!first)
						sb.append(", ");
					else
						first = false;
					if (module.getPrimitive() == null) {
						sb.append(".").append(p.port).append("(").append(p.signal).append(")");
					} else {// if primitive
						Primitive prim = module.getPrimitive();
						Parameter pParam = Util.getByName(prim.getParameters(), p.port);
						Param pv = Util.getByName(modParams, p.port);
						if (pParam.getType().equals(Parameter.TYPE_STRING)) // String types should be copied directly
							sb.append(".").append(p.port).append("(").append(p.signalNode.getText()).append(")");
						else if (pv.getValue() != null) {
							String val;
							if (pv.getValue().isNumber()) // need values for primitives to be in decimal
								val = Integer.toString(pv.getValue().getBigInt().intValue());
							else
								val = p.value.toVerilog();
							sb.append(".").append(p.port).append("(").append(val).append(")");
						} else
							sb.append(".").append(p.port).append("(").append(p.signal).append(")");
					}
				}
				sb.append(") ");
			}
		}

		sb.append(sanitize(instName)).append(" (");
		tabCount++;

		boolean first = true;

		for (Connection c : connetions) {
			if (!first)
				sb.append(",");
			else
				first = false;
			newLine(sb);
			sb.append(".").append(c.port).append("(").append(c.signal).append(")");
		}

		unassignedInputs.addAll(outputs); // the rest of the connections
		unassignedInputs.addAll(inouts);

		for (Sig con : unassignedInputs) {
			if (!first)
				sb.append(",");
			else
				first = false;
			newLine(sb);
			sb.append(".").append(con.getName()).append("(").append("M_").append(instName).append("_").append(con.getName());
			if (dimensions.size() > 0) {
				sb.append("[");
				boolean f = true;
				for (int i = 0; i < dimensions.size(); i++) {
					if (!f)
						sb.append("+");
					else
						f = false;
					sb.append("GEN_").append(instName).append(i);
					for (int j = i + 1; j < dimensions.size(); j++) {
						sb.append("*").append("(" + dimensions.get(j) + ")");
					}
					if (!f)
						sb.append("*");

					addWidth(sb, modParams, con.getWidth());
				}

				if (con.getWidth().getDepth() > 0) {
					if (!f)
						sb.append("+");

					SignalWidth width = con.getWidth();
					addWidth(sb, modParams, width);
					sb.append("-1-:");
					addWidth(sb, modParams, width);
				}
				sb.append("]");
			}
			sb.append(")");
		}

		tabCount--;
		newLine(sb);
		sb.append(");");

		if (dimensions.size() > 0) {
			for (int i = 0; i < dimensions.size(); i++) {
				tabCount--;
				newLine(sb);
				sb.append("end");
			}
			newLine(sb);
			sb.append("endgenerate");
		}

		if (assignBlocks.size() > 0 && assignBlocks.lastElement().instCon) {
			assignBlocks.pop();
		}

		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitFsm_dec(Fsm_decContext ctx) {
		String name = ctx.name().getText();
		Fsm fsm = Util.getByName(extractor.getFsms(), name);
		int stateWidth = Util.minWidthNum(fsm.getStates().size() - 1);

		if (assignBlocks.size() > 0 && assignBlocks.lastElement().instCon) {
			assignBlocks.pop();
		}

		StringBuilder sb = new StringBuilder();

		ArrayList<Constant> states = fsm.getStates();
		int size = states.size();
		for (int i = 0; i < size; i++) {
			newLine(sb);
			sb.append("localparam ").append(states.get(i).getName()).append("_").append(fsm.getName()).append(" = ");
			sb.append(stateWidth).append("'d").append(i).append(";");
		}

		newLine(sb);
		newLine(sb);

		int bits = bitWidthChecker.getWidth(name + ".d").getTotalWidth();

		if (bits > 1 || ctx.array_size().size() != 0)
			sb.append("reg [").append(bits - 1).append(":0] ");
		else
			sb.append("reg ");
		sb.append("M_").append(fsm.getName()).append("_d, ").append("M_").append(fsm.getName()).append("_q");

		if (fsm.getDefState() == null)
			fsm.setDefState(states.get(0));

		sb.append(" = ").append(fsm.getDefState().getName()).append("_").append(fsm.getName()).append(";");

		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitDff_dec(Dff_decContext ctx) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		if (assignBlocks.size() > 0 && assignBlocks.lastElement().instCon) {
			assignBlocks.pop();
		}

		for (Dff_singleContext dc : ctx.dff_single()) {
			String name = dc.name().getText();
			SignalWidth sw = bitWidthChecker.getWidth(name + ".d");
			Dff dff = Util.getByName(extractor.getDffs(), name);

			if (!first)
				newLine(sb);
			else
				first = false;

			if (dff.isIOB()) {
				sb.append("reg ");

				if (ctx.SIGNED() != null)
					sb.append("signed ");

				int bits = sw.getTotalWidth();

				if (bits > 1)
					sb.append("[").append(bits - 1).append(":0] ");

				sb.append("M_").append(name).append("_d;");
				newLine(sb);
				sb.append("(* IOB = \"TRUE\" *)");
				newLine(sb);
				sb.append("reg ");

				if (ctx.SIGNED() != null)
					sb.append("signed ");

				if (bits > 1 || dc.array_size().size() != 0)
					sb.append("[").append(bits - 1).append(":0] ");

				sb.append("M_").append(name).append("_q");

				if (dff.getInit() != null)
					sb.append(" = ").append(dff.getInit().toVerilog());
				sb.append(";");
			} else {
				sb.append("reg ");

				if (ctx.SIGNED() != null)
					sb.append("signed ");

				int bits = sw.getTotalWidth();

				if (bits > 1 || dc.array_size().size() != 0)
					sb.append("[").append(bits - 1).append(":0] ");

				sb.append("M_").append(name).append("_d, ").append("M_").append(name).append("_q");

				if (dff.getInit() != null)
					sb.append(" = ").append(dff.getInit().toVerilog());
				sb.append(";");
			}

		}
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitSig_dec(Sig_decContext ctx) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (Type_decContext dc : ctx.type_dec()) {
			if (!first)
				newLine(sb);
			else
				first = false;

			SignalWidth sw = bitWidthChecker.getWidth(dc.name().getText());

			sb.append("reg ");
			if (ctx.SIGNED() != null)
				sb.append("signed ");

			int bits = sw.getTotalWidth();
			if (bits > 1 || dc.array_size().size() != 0)
				sb.append("[").append(bits - 1).append(":0] ");
			sb.append(sanitize(dc.name().getText()));
			sb.append(";");
		}
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitVar_dec(Var_decContext ctx) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (Type_decContext dc : ctx.type_dec()) {
			if (!first)
				newLine(sb);
			else
				first = false;

			SignalWidth sw = bitWidthChecker.getWidth(dc.name().getText());

			sb.append("integer ");
			int bits = sw.getTotalWidth();

			if (bits > 1)
				sb.append("[").append(bits - 1).append(":0] ");
			sb.append(sanitize(dc.name().getText()));
			sb.append(";");
		}

		verilog.put(ctx, sb.toString());
	}

	@Override
	public void enterInst_cons(Inst_consContext ctx) {
		assignBlocks.push(new AssignBlock(true));
	}

	@Override
	public void exitParam_con(Param_conContext ctx) {
		ArrayList<Connection> con = assignBlocks.peek().connections;
		if (ctx.expr() != null) {
			con.add(new Connection(ctx.name().getText(), verilog.get(ctx.expr()), true, null, ctx.name(), ctx.expr(), (ConnectionContext) ctx.parent));
		} else
			con.add(new Connection(ctx.name().getText(), ctx.REAL().getText(), true, null, ctx.name(), ctx, (ConnectionContext) ctx.parent));
	}

	@Override
	public void exitSig_con(Sig_conContext ctx) {
		ArrayList<Connection> con = assignBlocks.peek().connections;
		con.add(new Connection(ctx.name().getText(), verilog.get(ctx.expr()), false, null, ctx.name(), ctx.expr(), (ConnectionContext) ctx.parent));
	}

	@Override
	public void enterAlways_block(Always_blockContext ctx) {
		alwaysFsms = new HashSet<Fsm>();
		alwaysDffs = new HashSet<Dff>();
	}

	@Override
	public void exitAlways_block(Always_blockContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("always @* ");
		sb.append(verilog.get(ctx.block()));
		verilog.put(ctx, sb.toString());
		alwaysDffs = null;
		alwaysFsms = null;
	}

	@Override
	public void enterBlock(BlockContext ctx) {
		tabCount++;
	}

	@Override
	public void exitBlock(BlockContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("begin");
		newLine(sb);
		if (ctx.parent.getClass() == Always_blockContext.class) {
			for (Fsm f : alwaysFsms) {
				sb.append("M_").append(f.getName()).append("_d = ").append("M_").append(f.getName()).append("_q;");
				newLine(sb);
			}
			for (Dff d : alwaysDffs) {
				sb.append("M_").append(d.getName()).append("_d = ").append("M_").append(d.getName()).append("_q;");
				newLine(sb);
			}
			if (alwaysFsms.size() > 0 || alwaysDffs.size() > 0)
				newLine(sb);
		}
		boolean first = true;
		for (Always_statContext c : ctx.always_stat()) {
			if (!first)
				newLine(sb);
			else
				first = false;
			sb.append(verilog.get(c));
		}
		tabCount--;
		newLine(sb);
		sb.append("end");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitAlwaysStat(AlwaysStatContext ctx) {
		verilog.put(ctx, verilog.get(ctx.assign_stat()) + ";");
	}

	@Override
	public void exitAssign_stat(Assign_statContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(verilog.get(ctx.signal()));
		sb.append(" = ");
		sb.append(verilog.get(ctx.expr()));
		verilog.put(ctx, sb.toString());
	}

	// private void parseBitSelection(StringBuilder sb, SignalWidth widths, Bit_selectionContext ctx) {
	// if (ctx != null) { // are any indices specified?
	// String max = null;
	// String width = null;
	//
	// sb.append("[");
	// boolean first = true;
	// int levels = 0; // number of levels we have indexed
	//
	// List<Array_indexContext> aic = ctx.array_index();
	// Bit_selectorContext bic = ctx.bit_selector();
	// for (int i = 0; i < aic.size(); i++) { // for each group []
	// if (!first)
	// sb.append("+"); // stick + signs between values
	// else
	// first = false;
	// sb.append("(");
	// sb.append("(" + verilog.get(aic.get(i).expr()) + "+0)");
	// for (int j = i + 1; j < widths.size(); j++) { // multiply the index by the widths of each lower dimension
	// sb.append("*").append("(" + widths.get(j) + "+0)");
	// }
	// sb.append("+0)");
	// levels++;
	// }
	// // these are used for bit width selectors (ie [3:2] or [4-:2])
	//
	// if (bic != null) { // is it a range?
	// if (bic.getClass().equals(BitSelectorConstContext.class)) { // min/max range type [3:0]
	// BitSelectorConstContext bscc = (BitSelectorConstContext) bic;
	// max = verilog.get(bscc.expr(0));
	// width = "((" + max + "+0)-(" + verilog.get(bscc.expr(1)) + "+0)+1)";
	// } else { // start/width range type [3+:4]
	// BitSelectorFixWidthContext bsfwc = (BitSelectorFixWidthContext) bic;
	// width = verilog.get(bsfwc.expr(1));
	// if (bic.getChild(2).getText().equals("+")) { // +/- selector direction
	// max = "((" + verilog.get(bsfwc.expr(0)) + "+0)+(" + width + "+0)-1)";
	// } else {
	// max = verilog.get(bsfwc.expr(0));
	// }
	// }
	//
	// if (!first)
	// sb.append("+");
	// else
	// first = false;
	// sb.append("(");
	// sb.append("(" + max + "+0)"); // index always starts at the max and goes
	// // down
	// for (int j = aic.size() + 1; j < widths.size(); j++) {
	// sb.append("*").append("(" + widths.get(j) + "+0)");
	// }
	// sb.append(")");
	// levels++;
	// }
	//
	// if (levels < widths.size()) { // dimensions that weren't explicitly set
	// if (!first)
	// sb.append("+");
	// sb.append("(");
	// boolean f = true;
	// for (int i = levels; i < widths.size(); i++) { // for each unspecified dimension
	// if (!f)
	// sb.append("*");
	// sb.append("(" + widths.get(i) + "+0)"); // multiply all the widths
	// f = false;
	// }
	// sb.append("-1)-:"); // subtract one to get the max and use downto operator
	// f = true;
	// if (width != null) { // if we have a width from a range selector
	// f = false;
	// sb.append(width);
	// }
	// for (int i = levels; i < widths.size(); i++) {
	// if (!f)
	// sb.append("*");
	// else
	// f = false;
	// sb.append("(" + widths.get(i) + "+0)");
	// }
	// } else if (width != null) { // all dimensions specified but last dim in a range
	// sb.append("-:").append(width); // append the width of that range
	// }
	// sb.append("]");
	// }
	// }

	private SignalWidth convertToFixed(SignalWidth sw, List<Param> p) {
		if (sw.isFixed())
			return sw;

		SignalWidth fw = new SignalWidth(sw);
		for (SignalWidth ptr = fw; ptr != null; ptr = ptr.getNext()) {
			if (ptr.isText()) {
				ConstValue cv = lucidExprParser(ptr.getText(), p);
				if (cv == null)
					Util.log.severe("Could not parse width " + ptr.getText());
				else
					ptr.set(cv.getBigInt().intValue());
			}
		}

		fw.simplify(); // merge arrays

		return fw;
	}

	private SignalWidth getSigWidths(SignalContext ctx) {
		String name = ctx.name(0).getText();

		// Dff d = Util.getByName(extractor.getDffs(), name);
		// if (d != null)
		// return d.getWidths();
		//
		// Fsm f = Util.getByName(extractor.getFsms(), name);
		// if (f != null)
		// return f.getWidths();

		Sig s = Util.getByName(extractor.getSigs(), name);
		if (s != null)
			return s.getWidth();

		s = Util.getByName(extractor.getInputs(), name);
		if (s != null)
			return s.getWidth();

		s = Util.getByName(extractor.getOutputs(), name);
		if (s != null)
			return s.getWidth();

		s = Util.getByName(extractor.getInouts(), name);
		if (s != null)
			return s.getWidth();

		Var v = Util.getByName(extractor.getVars(), name);
		if (v != null)
			return v.getWidth();

		InstModule im = Util.getByName(instModules, name);
		if (im != null) {
			if (ctx.name().size() > 1) {
				String sigName = ctx.name(1).getText();

				s = Util.getByName(im.getType().getInputs(), sigName);

				if (s == null)
					s = Util.getByName(im.getType().getOutputs(), sigName);

				if (s == null)
					s = Util.getByName(im.getType().getInouts(), sigName);

				if (s != null)
					return s.getWidth();
			}
		}

		return null;
	}

	// parses Dff or Sig width that may contain structs
	private void parseComplexWidth(StringBuilder sb, SignalContext ctx, SignalWidth width, int offset) {
		int childOffset = ctx.children.indexOf(ctx.name(offset - 1)) + 1;
		StringBuilder bitOffset = new StringBuilder();
		int bitWidth = bitWidthChecker.getWidth(ctx).getTotalWidth();
		width = convertToFixed(width, params);
		SignalWidth current = new SignalWidth(width);
		boolean first = true;

		for (int i = childOffset; i < ctx.children.size(); i++) {
			ParseTree pt = ctx.children.get(i);
			if (pt instanceof Bit_selectionContext) {
				for (Array_indexContext aic : ((Bit_selectionContext) pt).array_index()) {
					ArrayBounds b = bitWidthChecker.getBoundsProvider().getBounds(aic);
					current.getWidths().remove(0);
					if (!first)
						bitOffset.append("+");
					else
						first = false;
					if (b == null)
						bitOffset.append("(").append(verilog.get(aic.expr())).append(")*").append(current.getTotalWidth());
					else
						bitOffset.append(b.getMin() * current.getTotalWidth());
				}
				if (((Bit_selectionContext) pt).bit_selector() != null) {
					Bit_selectorContext bsc = ((Bit_selectionContext) pt).bit_selector();
					ArrayBounds b = bitWidthChecker.getBoundsProvider().getBounds(bsc);
					current.getWidths().set(0, bitWidthChecker.getWidth(bsc).getWidths().get(0));
					if (!first)
						bitOffset.append("+");
					else
						first = false;

					if (b == null) {
						if (bsc instanceof BitSelectorConstContext) {
							BitSelectorConstContext bscc = (BitSelectorConstContext) bsc;
							bitOffset.append("(").append(verilog.get(bscc.expr(1))).append(")*").append(current.getTotalWidth() / current.getWidths().get(0));
						} else {
							BitSelectorFixWidthContext bsfw = (BitSelectorFixWidthContext) bsc;
							if (bsfw.getChild(2).getText().equals("+")) { // +: selector
								bitOffset.append("(").append(verilog.get(bsfw.expr(0))).append(")*").append(current.getTotalWidth() / current.getWidths().get(0));
							} else { // -: selector
								bitOffset.append("((").append(verilog.get(bsfw.expr(0))).append(")-(").append(verilog.get(bsfw.expr(1))).append("-1))*")
										.append(current.getTotalWidth() / current.getWidths().get(0));
							}
						}

					} else {
						bitOffset.append(b.getMin() * (current.getTotalWidth() / current.getWidths().get(0)));
					}
				}
				if (current.getWidths().size() == 0)
					current = current.getNext();
			} else if (pt instanceof NameContext) {
				String name = pt.getText();
				if (!current.isStruct() && current.getWidths().size() == 1 && current.getWidths().get(0) == 1 && current.getNext() != null)
					current = current.getNext();

				if (!first)
					bitOffset.append("+");
				else
					first = false;
				bitOffset.append(current.getStruct().getOffsetOfMember(name));
				current = new SignalWidth(current.getStruct().getWidthOfMember(name));
			} else if (pt instanceof TerminalNode) {
				continue;
			} else {
				Util.log.severe("Uknown " + ctx.getText());
			}
		}
		if (bitOffset.length() > 0 || bitWidth != width.getTotalWidth()) {
			sb.append("[").append(bitOffset.toString()).append("+").append(bitWidth - 1).append("-:").append(bitWidth).append("]");
		}
	}

	@Override
	public void exitSignal(SignalContext ctx) {
		NameContext attribute = ctx.name().get(ctx.name().size() - 1);
		boolean hasAttr = attribute.CONST_ID() != null;
		boolean isWidth = attribute.getText().equals(Lucid.WIDTH_ATTR);
		boolean isInstModule = false;

		if (ctx.name(0).CONST_ID() != null) { // it's a constant
			ConstValue cv = exprParser.getValue(ctx);
			if (cv != null) {
				verilog.put(ctx, cv.toVerilog());
				return;
			}

			StringBuilder sb = new StringBuilder();

			String cName = ctx.name(0).CONST_ID().getText();

			if (ctx.bit_selection().size() > 0) {
				sb.append(cName);
				SignalWidth w = bitWidthChecker.getWidth(cName);
				if (w == null) {
					return;
				}

				parseComplexWidth(sb, ctx, w, 1);
			}

			verilog.put(ctx, sb.toString());
			return;
		}

		String prefix = "";
		boolean write = false;
		if (ctx.parent instanceof ExprSignalContext)
			;// read = true;
		else if (ctx.parent instanceof Assign_statContext || ctx.parent instanceof Var_assignContext)
			write = true;
		else
			Util.log.severe("Error unknown signal usage on line " + ctx.start.getLine() + ". Class: " + ctx.parent.getClass());

		SignalWidth widths = getSigWidths(ctx);
		int offset = 1;

		String name = ctx.name(0).getText();

		Dff d = Util.getByName(extractor.getDffs(), name);
		if (d != null && !hasAttr) {
			prefix = "M_";
			if (alwaysDffs != null && write)
				alwaysDffs.add(d);
			StringBuilder sb = new StringBuilder();
			sb.append("M_").append(name).append("_").append(ctx.name(1).getText());
			widths = d.getWidth();
			offset = 2;
		} else {
			Fsm f = Util.getByName(extractor.getFsms(), name);
			if (f != null) {
				widths = f.getWidth();
				offset = 2;
				if (hasAttr) { // special case for FSM states
					String state = attribute.getText() + "_" + ctx.name(0).getText();
					verilog.put(ctx, state);
					return;
				}
				prefix = "M_";
				if (alwaysFsms != null && write) {
					alwaysFsms.add(f);
				}
			} else {
				Sig io = Util.getByName(extractor.getInouts(), name);
				if (io != null) {
					widths = io.getWidth();
					offset = 2;
					if (ctx.name().size() > 1) {
						prefix = "IO_";
					}
				} else {
					InstModule im = Util.getByName(instModules, name);
					if (im != null) {
						isInstModule = true;
						prefix = "M_";
						Sig s = Util.getByName(im.getType().getInputs(), ctx.name(1).getText());
						if (s == null)
							s = Util.getByName(im.getType().getOutputs(), ctx.name(1).getText());
						if (s != null) {
							widths = convertToFixed(s.getWidth(), im.getParams());
							offset = 2;
						}
						if (ctx.name().size() > 1) {
							s = Util.getByName(im.getType().getInouts(), ctx.name(1).getText());
							if (s != null) {
								prefix = "MIO_";
								widths = convertToFixed(s.getWidth(), im.getParams());
								offset = 3;
							}
						}
						im = Util.getByName(extractor.getInstModules(), im.getName());
						if (im.getModuleWidth() != null && im.isArray()) {
							widths = new SignalWidth(widths);
							widths.getWidths().addAll(0, im.getModuleWidth().getWidths());
						}
					}
				}
			}
		}

		if (!isInstModule && hasAttr && !isWidth) {
			if (Util.containsName(extractor.getFsms(), ctx.name(0).getText()))
				verilog.put(ctx, attribute.getText() + "_" + ctx.name(0).getText());
			return;
		} else if (isWidth) {
			ConstValue cv = exprParser.getValue(ctx);
			if (cv != null) {
				verilog.put(ctx, cv.toVerilog());
				return;
			}

			Util.log.severe("BUG: Couldn't get " + Lucid.WIDTH_ATTR + " of " + ctx.getText());
			return;
			/*
			 * SignalWidth w = getSigWidths(ctx); if (w == null || w.getDepth() == 0) { Util.log.severe("BUG: Couldn't get " + Lucid.WIDTH_ATTR + " of " + ctx.getText());
			 * return; }
			 * 
			 * StringBuilder sb = new StringBuilder();
			 * 
			 * sb.append("(");
			 * 
			 * if (ctx.bit_selection().size() > 0) { Bit_selectionContext lbsc = ctx.bit_selection(ctx.bit_selection().size() - 1); if (lbsc ==
			 * ctx.children.get(ctx.children.size() - 1)) {
			 * 
			 * if (lbsc.bit_selector() != null) { Bit_selectorContext bsc = lbsc.bit_selector(); sb.append("("); if (bsc instanceof BitSelectorConstContext) {
			 * sb.append("(").append(verilog.get(((BitSelectorConstContext) bsc).expr(0))).append(")"); sb.append("-(").append(verilog.get(((BitSelectorConstContext)
			 * bsc).expr(1))).append(")"); sb.append("+1"); } else { sb.append(verilog.get(((BitSelectorFixWidthContext) bsc).expr(1))); } sb.append(")"); } else { int dim =
			 * lbsc.array_index().size(); if (widths.getDepth() > dim) { SignalWidth sw = widths.getDimension(dim); if (sw == null) { Util.log.severe(
			 * "Signal width was null when trying to get WIDTH."); } else { if (!sw.isArray()) { Util.log.severe("Signal width was not an array when trying to get WIDTH!"); }
			 * else { sb.append(sw.getWidths().get(0)); } }
			 * 
			 * } else { sb.append("1"); } } } } else { if (ctx.bit_selection().size() > 1) Util.log.severe(
			 * "Multiple bit selectors on constants aren't suppored for widths yet!"); if (!widths.isArray()) { Util.log.severe(
			 * "Signal width was not an array when trying to get WIDTH with no bit selection!"); } else { sb.append(widths.getWidths().get(0)); } }
			 * 
			 * sb.append(")");
			 * 
			 * verilog.put(ctx, sb.toString()); return;
			 */
		}

		if (widths == null)
			return;

		StringBuilder sb = new StringBuilder();
		List<NameContext> list = ctx.name();

		sb.append(prefix);

		String sname = ctx.name(0).getText();
		if (prefix.isEmpty())
			sname = sanitize(sname);

		sb.append(sname);

		for (int i = 1; i < offset && i < list.size(); i++) {
			sb.append("_").append(list.get(i).getText());
		}

		if (offset < ctx.name().size() || ctx.bit_selection().size() != 0)
			parseComplexWidth(sb, ctx, widths, offset);
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitAlwaysCase(AlwaysCaseContext ctx) {
		verilog.put(ctx, verilog.get(ctx.case_stat()));
	}

	@Override
	public void enterCase_stat(Case_statContext ctx) {
		tabCount++;
	}

	@Override
	public void exitCase_stat(Case_statContext ctx) {
		StringBuilder sb = new StringBuilder();
		tabCount--;
		newLine(sb);
		tabCount++;
		sb.append("case (").append(verilog.get(ctx.expr())).append(")");
		for (Case_elemContext c : ctx.case_elem()) {
			newLine(sb);
			sb.append(verilog.get(c));
		}
		tabCount--;
		newLine(sb);
		sb.append("endcase");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void enterCase_elem(Case_elemContext ctx) {
		tabCount++;
	}

	@Override
	public void exitCase_elem(Case_elemContext ctx) {
		StringBuilder sb = new StringBuilder();
		if (ctx.expr() != null) {
			sb.append(verilog.get(ctx.expr()));
		} else {
			sb.append("default");
		}
		sb.append(": begin");
		// tabCount++;
		for (Always_statContext c : ctx.always_stat()) {
			newLine(sb);
			sb.append(verilog.get(c));
		}
		tabCount--;
		newLine(sb);
		sb.append("end");
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitAlwaysIf(AlwaysIfContext ctx) {
		verilog.put(ctx, verilog.get(ctx.if_stat()));
	}

	@Override
	public void exitIf_stat(If_statContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("if (").append(verilog.get(ctx.expr())).append(") ");
		sb.append(verilog.get(ctx.block()));
		if (ctx.else_stat() != null) {
			sb.append(" ").append(verilog.get(ctx.else_stat()));
		}
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitAlwaysFor(AlwaysForContext ctx) {
		verilog.put(ctx, verilog.get(ctx.for_stat()));
	}

	@Override
	public void exitFor_stat(For_statContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("for (").append(verilog.get(ctx.assign_stat())).append("; ");
		sb.append(verilog.get(ctx.expr())).append("; ");
		sb.append(verilog.get(ctx.var_assign())).append(") ");
		sb.append(verilog.get(ctx.block()));
		verilog.put(ctx, sb.toString());
	}

	@Override
	public void exitElse_stat(Else_statContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("else ").append(verilog.get(ctx.block()));
		verilog.put(ctx, sb.toString());
	}

	private ConstValue getValue(ParseTree ctx) {
		if (exprParser == null)
			return null;
		return exprParser.getValue(ctx);
	}

	private static ConstValue lucidExprParser(String text, final List<Param> params) {
		ConstValue cv = ConstExprParser.parseExpr(text, new ConstProvider() {

			@Override
			public ConstValue getValue(String s) {
				if (params == null)
					return null;
				Param p = Util.getByName(params, s);
				if (p != null)
					return p.getValue();

				return null;
			}
		}, null, null);
		return cv;
	}

	private static String sanitize(String s) {
		if (ReservedWords.check(s))
			return "L_" + s;
		return s;
	}

	// public static String constExprParser(String text, final List<Param> params, boolean isLucid) {
	// if (text == null)
	// return null;
	//
	// if (isLucid) {
	// ConstValue cv = lucidExprParser(text, params);
	// return cv.toVerilog();
	// }
	//
	// LucidToVerilog ltv = new LucidToVerilog(new ArrayList<Module>(), params);
	//
	// ANTLRInputStream input = new ANTLRInputStream(text);
	// LucidLexer lexer = new LucidLexer(input);
	// lexer.removeErrorListeners();
	// final CommonTokenStream tokens = new CommonTokenStream(lexer);
	// LucidParser parser = new LucidParser(tokens);
	// parser.removeErrorListeners();
	//
	// parser.addParseListener(ltv);
	//
	// ltv.enterModule(null);
	//
	// ParseTree tree = parser.expr();
	//
	// return ltv.verilog.get(tree);
	// }

}
