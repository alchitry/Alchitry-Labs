package com.alchitry.labs.lucid.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.text.AbstractDocument.AttributeContext;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.language.ConstValue;
import com.alchitry.labs.language.InstModule;
import com.alchitry.labs.language.Module;
import com.alchitry.labs.language.Param;
import com.alchitry.labs.language.Sig;
import com.alchitry.labs.lucid.AssignBlock;
import com.alchitry.labs.lucid.Connection;
import com.alchitry.labs.lucid.Constant;
import com.alchitry.labs.lucid.Dff;
import com.alchitry.labs.lucid.Fsm;
import com.alchitry.labs.lucid.Lucid;
import com.alchitry.labs.lucid.LucidDictionary;
import com.alchitry.labs.lucid.Struct;
import com.alchitry.labs.lucid.Var;
import com.alchitry.labs.lucid.parser.LucidBaseListener;
import com.alchitry.labs.lucid.parser.LucidParser.Always_blockContext;
import com.alchitry.labs.lucid.parser.LucidParser.Array_sizeContext;
import com.alchitry.labs.lucid.parser.LucidParser.Assign_blockContext;
import com.alchitry.labs.lucid.parser.LucidParser.Assign_statContext;
import com.alchitry.labs.lucid.parser.LucidParser.Case_elemContext;
import com.alchitry.labs.lucid.parser.LucidParser.Case_statContext;
import com.alchitry.labs.lucid.parser.LucidParser.ConnectionContext;
import com.alchitry.labs.lucid.parser.LucidParser.Const_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Dff_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Dff_singleContext;
import com.alchitry.labs.lucid.parser.LucidParser.Else_statContext;
import com.alchitry.labs.lucid.parser.LucidParser.ExprContext;
import com.alchitry.labs.lucid.parser.LucidParser.ExprSignalContext;
import com.alchitry.labs.lucid.parser.LucidParser.Fsm_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.GlobalContext;
import com.alchitry.labs.lucid.parser.LucidParser.If_statContext;
import com.alchitry.labs.lucid.parser.LucidParser.Inout_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Input_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Inst_consContext;
import com.alchitry.labs.lucid.parser.LucidParser.ModuleContext;
import com.alchitry.labs.lucid.parser.LucidParser.Module_instContext;
import com.alchitry.labs.lucid.parser.LucidParser.NameContext;
import com.alchitry.labs.lucid.parser.LucidParser.Output_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Param_conContext;
import com.alchitry.labs.lucid.parser.LucidParser.Param_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Param_nameContext;
import com.alchitry.labs.lucid.parser.LucidParser.Sig_conContext;
import com.alchitry.labs.lucid.parser.LucidParser.Sig_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.SignalContext;
import com.alchitry.labs.lucid.parser.LucidParser.SourceContext;
import com.alchitry.labs.lucid.parser.LucidParser.Struct_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Struct_memberContext;
import com.alchitry.labs.lucid.parser.LucidParser.Type_decContext;
import com.alchitry.labs.lucid.parser.LucidParser.Var_assignContext;
import com.alchitry.labs.lucid.parser.LucidParser.Var_decContext;
import com.alchitry.labs.lucid.style.ErrorToStyle;
import com.alchitry.labs.lucid.style.TokenErrorListener;
import com.alchitry.labs.project.Primitive;
import com.alchitry.labs.project.Primitive.Parameter;
import com.alchitry.labs.style.SyntaxError;
import com.alchitry.labs.tools.ParserCache;

public class LucidErrorChecker extends LucidBaseListener implements ErrorProvider, TokenErrorListener {
	private ArrayList<SyntaxError> errors;
	private ArrayList<SyntaxError> errorList;

	private ArrayList<Dff> dffs;
	private ArrayList<Fsm> fsms;
	private List<Constant> constants;
	private ArrayList<Sig> sigs;
	private List<Sig> inputs;
	private List<Sig> outputs;
	private List<Sig> inouts;
	private List<Sig> connectedInouts;
	private List<Var> vars;
	private List<Struct> structs;
	private List<InstModule> instModules;
	private List<Param> params;

	private List<Module> modules = new ArrayList<Module>();

	private Stack<AssignBlock> assignBlocks;

	private Stack<ArrayList<HashSet<Sig>>> assignedSigs;
	private Stack<Boolean> hasDefaultCase;

	private HashSet<Sig> drivenSigs;
	private HashSet<Sig> writtenSigs;
	private HashSet<Sig> reqSigs;

	private HashSet<Sig> unusedSigs;

	private BitWidthChecker bitWidthChecker;
	private ParamsParser paramsParser;
	private ConstParser constParser;
	private ConstExprParser constExprParser;
	private BoundsParser boundsParser;

	private LucidDictionary dictionary;

	private InstModule thisModule;

	public LucidErrorChecker(InstModule thisModule) {
		this(null, thisModule);
	}

	public LucidErrorChecker(LucidDictionary dict, InstModule thisModule) {
		errors = new ArrayList<SyntaxError>();
		errorList = new ArrayList<SyntaxError>();

		constExprParser = new ConstExprParser(this);
		bitWidthChecker = new BitWidthChecker(this, constExprParser);
		boundsParser = new BoundsParser(constExprParser, bitWidthChecker, this);

		paramsParser = new ParamsParser(constExprParser, thisModule);
		constParser = new ConstParser(constExprParser);

		bitWidthChecker.setBoundsProvider(boundsParser);
		bitWidthChecker.setConstParser(constParser);
		constExprParser.setBoundsProvider(boundsParser);
		constExprParser.setParamsParser(paramsParser);
		constExprParser.setConstParser(constParser);
		constExprParser.setBitWidthChecker(bitWidthChecker);

		dffs = new ArrayList<>();
		fsms = new ArrayList<>();
		constants = new ArrayList<>();
		sigs = new ArrayList<>();
		vars = new ArrayList<>();
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
		inouts = new ArrayList<>();
		connectedInouts = new ArrayList<>();
		instModules = new ArrayList<>();
		structs = new ArrayList<>();
		params = new ArrayList<>();

		assignBlocks = new Stack<>();
		reqSigs = new HashSet<>();
		drivenSigs = new HashSet<>();
		assignedSigs = new Stack<>();
		hasDefaultCase = new Stack<>();
		unusedSigs = new HashSet<>();

		this.thisModule = thisModule;

		if (thisModule != null) {
			thisModule.setDffs(dffs);
			thisModule.setFsms(fsms);
			thisModule.setSigs(sigs);
		}

		if (dict != null) {
			dict.setSignals(this, instModules);
			dictionary = dict;
		}
	}

	public ConstProvider getConstProvider() {
		return constParser;
	}

	public BitWidthChecker getBitWidthChecker() {
		return bitWidthChecker;
	}

	public List<Dff> getDffs() {
		return dffs;
	}

	public List<Fsm> getFsms() {
		return fsms;
	}

	public List<Sig> getSigs() {
		return sigs;
	}

	public List<Sig> getInputs() {
		return inputs;
	}

	public List<Sig> getOutputs() {
		return outputs;
	}

	public List<Sig> getInouts() {
		return inouts;
	}

	public List<Param> getParams() {
		return params;
	}

	public List<InstModule> getInstModules() {
		return instModules;
	}

	public List<Sig> getConnectedInouts() {
		return connectedInouts;
	}

	public List<Var> getVars() {
		return vars;
	}

	public List<InstModule> getInstModules(String file, List<Module> modules) {
		setModuleList(modules);
		parseAll(file);
		return instModules;
	}

	public void addToParser(List<ParseTreeListener> listeners) {
		reset();

		listeners.add(constParser);
		listeners.add(paramsParser);
		listeners.add(constExprParser);
		listeners.add(boundsParser);
		listeners.add(bitWidthChecker);
		listeners.add(this);

		paramsParser.reset();
	}

	// public void addToParser(LucidParser parser) {
	// parser.removeErrorListeners();
	// errorListener.reset();
	// //parser.addErrorListener(errorListener);
	// reset();
	//
	// parser.addParseListener(this);
	// parser.addParseListener(bitWidthChecker);
	// parser.addParseListener(constExprParser);
	// parser.addParseListener(paramsParser);
	// parser.addParseListener(constParser);
	//
	// paramsParser.reset();
	// }

	public boolean checkSyntaxErrors(String file) {
		if (ParserCache.getErrors(file).length > 0) {
			return true;
		}
		return false;
	}

	public ArrayList<SyntaxError> getErrors(String file) {
		try {
			if (MainWindow.getOpenProject() != null)
				setModuleList(MainWindow.getOpenProject().getModules(null));
			else
				setModuleList(new ArrayList<Module>());
		} catch (IOException e) {
			setModuleList(new ArrayList<Module>());
			Util.log.severe("Could not parse project's modules!");
			e.printStackTrace();
		}

		parseAll(file);

		errorList.clear();
		errorList.addAll(ErrorToStyle.getErrors(file));
		errorList.addAll(getErrors());
		return errorList;
	}

	private void parseAll(String file) {
		List<ParseTreeListener> listeners = new ArrayList<>();
		addToParser(listeners);
		ParserCache.walk(file, listeners);
	}

	public Stack<AssignBlock> getAssignBlock() {
		return assignBlocks;
	}

	@Override
	public void onTokenDebugFound(TerminalNode node, String message) {
		underline(node.getSymbol(), node.getSymbol(), message, Theme.debugTextColor, SyntaxError.DEBUG);
	}

	@Override
	public void onTokenDebugFound(ParserRuleContext ctx, String message) {
		underline(ctx.start, ctx.stop, message, Theme.debugTextColor, SyntaxError.DEBUG);
	}

	@Override
	public void onTokenErrorFound(TerminalNode node, String message) {
		underlineError(node, message);
	}

	@Override
	public void onTokenWarningFound(TerminalNode node, String message) {
		underlineWarning(node, message);
	}

	@Override
	public void onTokenErrorFound(ParserRuleContext ctx, String message) {
		underlineError(ctx, message);
	}

	@Override
	public void onTokenWarningFound(ParserRuleContext ctx, String message) {
		underlineWarning(ctx, message);
	}

	private void underlineWarning(ParserRuleContext ctx, String message) {
		underline(ctx.start, ctx.stop, message, Theme.warningTextColor, SyntaxError.WARNING);
	}

	private void underlineError(ParserRuleContext ctx, String message) {
		underline(ctx.start, ctx.stop, message, Theme.errorTextColor, SyntaxError.ERROR);
	}

	private void underlineWarning(TerminalNode term, String message) {
		underline(term.getSymbol(), term.getSymbol(), message, Theme.warningTextColor, SyntaxError.WARNING);
	}

	private void underlineError(TerminalNode term, String message) {
		underline(term.getSymbol(), term.getSymbol(), message, Theme.errorTextColor, SyntaxError.ERROR);
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

	public void clearModuleList() {
		modules.clear();
	}

	public void setModuleList(List<Module> list) {
		modules = list;
	}

	@Override
	public void reset() {
		errors.clear();
	}

	@Override
	public void exitSource(SourceContext ctx) {
		if (ctx.module().size() > 1) {
			underlineError(ctx.module(1), ErrorStrings.MULTIPLE_MODULES);
		}
	}

	private void clear() {
		assignBlocks.clear();
		dffs.clear();
		fsms.clear();
		constants.clear();
		sigs.clear();
		vars.clear();
		inputs.clear();
		outputs.clear();
		inouts.clear();
		connectedInouts.clear();
		instModules.clear();
		reqSigs.clear();
		drivenSigs.clear();
		assignedSigs.clear();
		hasDefaultCase.clear();
		params.clear();
		unusedSigs.clear();
		structs.clear();

		if (dictionary != null) {
			dictionary.clear();
			if (modules != null)
				for (Module m : modules)
					dictionary.add(m.getName());
				HashMap<String, List<Constant>> gC = MainWindow.getGlobalConstants();
				for (String global : gC.keySet())
					dictionary.add(global);
			}
		
	}

	@Override
	public void enterGlobal(GlobalContext ctx) {
		clear();
	}

	@Override
	public void enterModule(ModuleContext ctx) {
		clear();
	}

	private String getMissingSigString(Collection<Sig> hs) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Sig s : hs) {
			if (first)
				first = false;
			else
				sb.append(", ");
			int offset = s.getName().indexOf(".");
			sb.append(s.getName().substring(offset + 1));
		}
		return sb.toString();
	}

	public Struct getStruct(String name) {
		return Util.getByName(structs, name);
	}

	@Override
	public void exitStruct_dec(Struct_decContext ctx) {
		if (ctx.name() != null) {
			if (ctx.name().TYPE_ID() == null)
				underlineError(ctx.name(), ErrorStrings.STRUCT_NAME_CASE);

			Struct struct = new Struct(ctx.name().getText());
			for (Struct_memberContext smc : ctx.struct_member()) {
				if (smc.name() != null) {
					if (smc.name().TYPE_ID() == null)
						underlineError(smc.name(), ErrorStrings.STRUCT_MEMBER_CASE);

					Struct.Member m = new Struct.Member(smc.name().getText());
					m.width = bitWidthChecker.getArrayWidth(smc.array_size(), smc.struct_type());
					struct.addMember(m);
				}
			}

			structs.add(struct);
			if (dictionary != null)
				dictionary.add(struct.getName());
		}
	}

	@Override
	public void exitModule(ModuleContext ctx) {
		if (ctx.name() != null) {
			if (ctx.name().TYPE_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_TYPE, ctx.name().getText()));
		}

		for (Sig s : unusedSigs) {
			underlineWarning(s.getNode(), String.format(ErrorStrings.NEVER_USED, s.getName()));
		}

		if (thisModule != null) {
			ArrayList<Sig> dSigs = new ArrayList<>();
			for (Sig s : sigs) {
				if (drivenSigs.contains(s))
					dSigs.add(s);
			}
			thisModule.setDrivenSigs(dSigs);
		}

		// This is a list of all modules that are missing input assignments
		HashMap<String, ArrayList<Sig>> moduleList = new HashMap<>();

		// remove the signals that are being driven
		reqSigs.removeAll(drivenSigs);

		for (Sig s : reqSigs) {
			String[] name = s.getName().split("\\.");
			if (name.length > 1) { // is a module (ie counter.out)
				ArrayList<Sig> list = moduleList.get(name[0]);
				if (list == null)
					list = new ArrayList<>();
				list.add(s);
				moduleList.put(name[0], list);
			} else {
				underlineError(s.getNode(), String.format(ErrorStrings.OUPUT_NEVER_ASSIGNED, s.getName())); // it is a signal
			}
		}

		for (Map.Entry<String, ArrayList<Sig>> entry : moduleList.entrySet()) {
			if (entry.getValue().size() > 0) {
				if (entry.getValue().size() != 1)
					underlineError(entry.getValue().get(0).getNode(), String.format(ErrorStrings.MODULE_INPUTS_NOT_ASSIGNED, getMissingSigString(entry.getValue())));
				else
					underlineError(entry.getValue().get(0).getNode(), String.format(ErrorStrings.MODULE_INPUT_NOT_ASSIGNED, getMissingSigString(entry.getValue())));
			}
		}

	}

	@Override
	public void enterAlways_block(Always_blockContext ctx) {
		writtenSigs = new HashSet<>();
	}

	@Override
	public void exitAlways_block(Always_blockContext ctx) {
		drivenSigs.addAll(writtenSigs);
		writtenSigs = null;
	}

	@Override
	public List<SyntaxError> getErrors() {
		return errors;
	}

	public boolean isFSM(String name) {
		return Util.containsName(fsms, name);
	}

	public boolean nameUsed(String name) {
		if (Util.containsName(inputs, name) || Util.containsName(outputs, name) || Util.containsName(inouts, name) || Util.containsName(sigs, name)
				|| Util.containsName(vars, name) || Util.containsName(instModules, name) || Util.containsName(dffs, name) || Util.containsName(fsms, name)
				|| Util.containsName(constants, name) || Util.containsName(structs, name) || MainWindow.getGlobalConstants().get(name) != null)
			return true;

		return false;
	}

	private void nameUsedError(ParserRuleContext ctx) {
		underlineError(ctx, String.format(ErrorStrings.NAME_TAKEN, ctx.getText()));
	}

	@Override
	public void exitInput_dec(Input_decContext ctx) {
		if (ctx.name() != null) {
			if (ctx.name().TYPE_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_TYPE, ctx.name().getText()));
			Sig s = new Sig(ctx.name().getText(), ctx);
			if (ctx.SIGNED() != null)
				s.setSigned(true);
			if (nameUsed(s.getName()))
				nameUsedError(ctx.name());
			else {
				inputs.add(s);
				s.setWidth(bitWidthChecker.getWidth(ctx));
				unusedSigs.add(s);
				if (dictionary != null)
					dictionary.add(s.getName());
			}
		}
	};

	@Override
	public void exitOutput_dec(Output_decContext ctx) {
		if (ctx.name() != null) {
			if (ctx.name().TYPE_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_TYPE, ctx.name().getText()));
			Sig s = new Sig(ctx.name().getText(), ctx);
			if (ctx.SIGNED() != null)
				s.setSigned(true);
			if (nameUsed(s.getName()))
				nameUsedError(ctx.name());
			else {
				outputs.add(s);
				s.setWidth(bitWidthChecker.getWidth(ctx));
				reqSigs.add(s);
				unusedSigs.add(s);
				if (dictionary != null)
					dictionary.add(s.getName());
			}
		}
	}

	@Override
	public void exitInout_dec(Inout_decContext ctx) {
		if (ctx.name() != null) {
			if (ctx.name().TYPE_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_TYPE, ctx.name().getText()));
			Sig s = new Sig(ctx.name().getText(), ctx);
			if (ctx.SIGNED() != null)
				s.setSigned(true);
			if (nameUsed(s.getName()))
				nameUsedError(ctx.name());
			else {
				inouts.add(s);
				s.setWidth(bitWidthChecker.getWidth(ctx));
				reqSigs.add(new Sig(ctx.name().getText() + ".enable", ctx));
				reqSigs.add(new Sig(ctx.name().getText() + ".write", ctx));
				unusedSigs.add(s);
				if (dictionary != null)
					dictionary.add(s.getName());
			}
		}
	}

	@Override
	public void exitParam_name(Param_nameContext ctx) {
		if (ctx.name() != null) {

			if (ctx.name().CONST_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_CONST, ctx.name().getText()));

			ConstValue cv = constExprParser.getValue(ctx.expr());

			if (cv == null && ctx.expr() != null)
				underlineError(ctx.expr(), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr().getText()));

			if (Util.containsName(constants, ctx.name().getText()))
				nameUsedError(ctx.name());
			else {
				String name = ctx.name().getText();
				constants.add(new Constant(name, cv));
				String text = null;
				if (ctx.expr() != null)
					text = ctx.expr().getText();
				Param p = new Param(name, text, cv);

				params.add(p);
				unusedSigs.add(new Sig(name, ctx));
				if (dictionary != null)
					dictionary.add(name);
			}
		}
	};

	@Override
	public void exitParam_dec(Param_decContext ctx) {
		if (ctx.param_constraint() != null && ctx.param_name().name() != null) {
			Param p = Util.getByName(params, ctx.param_name().name().getText());
			if (p != null) {
				p.setConstraint(ctx.param_constraint().getText());

				ConstValue constraint = constExprParser.getValue(ctx.param_constraint());
				if (constraint != null) {
					if (constraint.isZero()) {
						ConstValue cv = constExprParser.getValue(ctx.param_name().expr());
						underlineError(ctx.param_constraint(),
								String.format(ErrorStrings.PARAMETER_CONSTRAINT_FAILED, ctx.param_constraint().getText(), p.getName(), cv.toString()));
					}
				}
			}
		}
	};

	@Override
	public void exitConst_dec(Const_decContext ctx) {
		if (ctx.name() != null) {
			if (ctx.name().CONST_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_CONST, ctx.name().getText()));

			if (ctx.expr() != null && !constExprParser.isConstant(ctx.expr()))
				underlineError(ctx.expr(), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr().getText()));

			Constant c = new Constant(ctx.name().getText());

			if (nameUsed(c.getName()))
				nameUsedError(ctx.name());
			else {
				constants.add(c);
				c.setWidth(bitWidthChecker.getWidth(ctx));
				unusedSigs.add(new Sig(c.getName(), ctx));
				if (dictionary != null)
					dictionary.add(c.getName());
			}
		}
	}

	@Override
	public void exitSig_dec(Sig_decContext ctx) {
		if (ctx.type_dec() != null)
			for (Type_decContext dc : ctx.type_dec()) {
				if (dc.name() != null) {
					if (dc.name().TYPE_ID() == null)
						underlineError(dc.name(), String.format(ErrorStrings.NAME_NOT_TYPE, dc.name().getText()));

					Sig sig = new Sig(dc.name().getText(), ctx);
					if (ctx.SIGNED() != null)
						sig.setSigned(true);
					if (nameUsed(sig.getName()))
						nameUsedError(dc.name());
					else {
						sigs.add(sig);
						sig.setWidth(bitWidthChecker.getWidth(dc));
						unusedSigs.add(sig);
						if (dictionary != null)
							dictionary.add(sig.getName());
					}
				}
			}
	}

	@Override
	public void exitVar_dec(Var_decContext ctx) {
		for (Type_decContext dc : ctx.type_dec()) {
			if (dc.name() != null) {
				if (dc.name().TYPE_ID() == null)
					underlineError(dc.name(), String.format(ErrorStrings.NAME_NOT_TYPE, dc.name().getText()));

				Var var = new Var(dc.name().getText());
				if (nameUsed(var.getName()))
					nameUsedError(dc.name());
				else {
					vars.add(var);
					var.setWidth(bitWidthChecker.getWidth(dc));
					unusedSigs.add(new Sig(var.getName(), dc));
					if (dictionary != null)
						dictionary.add(var.getName());
				}
			}
		}
	}

	@Override
	public void exitModule_inst(Module_instContext ctx) {
		if (ctx.name().size() == 2) {
			String instName = ctx.name(1).getText();
			String moduleName = ctx.name(0).getText();

			if (ctx.name(1).TYPE_ID() == null)
				underlineError(ctx.name(1), String.format(ErrorStrings.NAME_NOT_TYPE, ctx.name(1).getText()));

			Module module = Util.getByName(modules, moduleName);

			if (module == null) {
				underlineError(ctx.name(0), String.format(ErrorStrings.UNDECLARED_MODULE, moduleName));
				return;
			}

			Primitive primitive = module.getPrimitive();

			if (nameUsed(instName)) {
				nameUsedError(ctx.name(1));
				return;
			}

			// module = new Module(module);

			InstModule instModule = new InstModule(instName, module, ctx);
			instModules.add(instModule);

			if (dictionary != null)
				dictionary.add(instName);

			List<Sig> reqIO = new ArrayList<>(module.getInouts());

			for (Sig s : module.getInputs())
				reqSigs.add(new Sig(instName + "." + s.getName(), ctx));

			if (module.getInouts().size() == 0)
				unusedSigs.add(new Sig(instName, ctx));

			instModule.addParams(module.getParams());

			ArrayList<Param> reqParams = new ArrayList<>();

			for (Param p : module.getParams()) {
				if (p.getDefValue() == null) {
					reqParams.add(p);
				}

			}

			for (Array_sizeContext asc : ctx.array_size()) {
				instModule.addWidth(asc.expr().getText());
			}

			for (AssignBlock block : assignBlocks) {
				if (block != null) {
					for (Connection c : block.connections) {
						if (c.param) {
							Param p = Util.getByName(instModule.getParams(), c.port);
							if (p == null) {
								if (block.instCon)
									underlineError(c.portNode, String.format(ErrorStrings.MODULE_UNKNOWN_PARAM, c.port, moduleName));
								else
									underlineError(c.portNode, String.format(ErrorStrings.MODULE_UNKNOWN_PARAM, c.port, moduleName));
							} else {
								Util.removeByName(reqParams, p.getName());
								if (p.valueSet())
									underlineError(c.portNode, String.format(ErrorStrings.MODULE_PARAM_DEFINED, c.port));
								p.setValue(c.signal);
								p.setValue(constExprParser.getValue(c.signalNode));

								if (primitive != null) {
									Parameter par = Util.getByName(primitive.getParameters(), c.port);
									if (par != null) {
										if (par.getType().equals(Parameter.TYPE_STRING)) {
											if (c.signal.length() > 2 && !par.getOptions().contains(c.signal.substring(1, c.signal.length() - 1))) {
												underlineError(c.signalNode, String.format(ErrorStrings.PRIMITIVE_INVALID_OPTION, c.signal, par.getOptions().toString()));
											}
										} else if (par.getType().equals(Parameter.TYPE_INTEGER)) {
											ConstValue cv = constExprParser.getValue(c.signalNode);

											if (cv == null || !cv.isNumber()) {
												underlineError(c.signalNode, String.format(ErrorStrings.PRIMITIVE_NAI, c.port));
											} else {
												int i = cv.getBigInt().intValue();
												if (!par.inRange(i)) {
													underlineError(c.signalNode, String.format(ErrorStrings.PRIMITIVE_OUT_OF_RANGE, c.port, par.getRanges().toString()));
												}
											}
										} else { // Real type
											try {
												double d = Double.parseDouble(c.signal);
												if (!par.inRange(d))
													underlineError(c.signalNode, String.format(ErrorStrings.PRIMITIVE_OUT_OF_RANGE, c.port, par.getRanges().toString()));
											} catch (NumberFormatException e) {
												underlineError(c.signalNode, String.format(ErrorStrings.PRIMITIVE_NAD, c.port));
											}
										}
									}
								}
							}
						} else {
							if (Util.containsName(module.getInputs(), c.port)) {
								instModule.addConnection(c);
								if (!Util.removeByName(reqSigs, instName + "." + c.port))
									underlineError(c.portNode, String.format(ErrorStrings.MODULE_INPUT_DEFINED, c.port));
							} else if (Util.containsName(module.getInouts(), c.port)) {
								if (!Util.containsName(inouts, c.signal))
									underlineError(c.signalNode, ErrorStrings.MODULE_INOUT_CONNECT_ONLY_INOUT);
								else
									connectedInouts.add(Util.getByName(inouts, c.signal));
								instModule.addConnection(c);
								Util.removeByName(reqSigs, c.signal + ".enable"); // inout binds to signal
								Util.removeByName(reqSigs, c.signal + ".write");

								if (!Util.removeByName(reqIO, c.port))
									underlineError(c.portNode, String.format(ErrorStrings.MODULE_INOUT_DEFINED, c.port));
							} else {
								if (block.instCon)
									underlineError(c.portNode, String.format(ErrorStrings.MODULE_UNKNOWN_INPUT, c.port, moduleName));
								else
									underlineError(c.portNode, String.format(ErrorStrings.MODULE_UNKNOWN_INPUT, c.port, moduleName));
							}
						}
					}
				}
			}

			if (reqIO.size() > 0) {
				StringBuilder iosb = new StringBuilder();
				boolean first = true;
				for (Sig s : reqIO) {
					if (!first)
						iosb.append(", ");
					else
						first = false;
					iosb.append(s.getName());
				}
				underlineError(ctx, String.format(ErrorStrings.MODULE_IO_MISSING, iosb.toString()));
			}

			for (Param p : instModule.getParams())
				if (!p.valueSet())
					p.setValue(constExprParser.parseExpr(p.getStringValue()));

			if (reqParams.size() > 0) {
				if (reqParams.size() == 1) {
					underlineError(ctx.name(1), String.format(ErrorStrings.MODULE_MISSING_REQ_PARAM, reqParams.get(0).getName()));
				} else {
					StringBuilder sb = new StringBuilder();
					boolean first = true;
					for (Param p : reqParams) {
						if (!first)
							sb.append(", ");
						else
							first = false;

						sb.append(p.getName());
					}
					underlineError(ctx.name(1), String.format(ErrorStrings.MODULE_MISSING_REQ_PARAMS, sb.toString()));
				}
			} else {
				final List<Param> iparams = instModule.getParams();

				ConstProvider cp = new ConstProvider() {
					@Override
					public ConstValue getValue(String s) {
						int idx = Util.findByName(iparams, s);
						if (idx >= 0) {
							Param p = iparams.get(idx);
							return p.getValue();
						}
						Util.log.severe("Param " + s + " wasn't found!");
						return null;

					}
				};

				for (Param p : instModule.getParams()) {
					if (p.getValue() == null && p.getStringValue() != null) {
						p.setValue(ConstExprParser.parseExpr(p.getStringValue(), cp, null, null));
					}
					if (p.getConstraint() != null) {

						ConstValue cv = ConstExprParser.parseExpr(p.getConstraint(), cp, null, null);

						if (cv == null) {
							underlineError(ctx.name(1), String.format(ErrorStrings.PARAMETER_CONSTRAINT_PARSE_FAIL, p.getConstraint()));
						} else if (cv.isZero()) {
							underlineError(ctx.name(1), String.format(ErrorStrings.PARAMETER_CONSTRAINT_FAILED, p.getConstraint(), p.getName(), p.getValue()));
						}
					}

				}
			}
			bitWidthChecker.module_inst(ctx, instModule);
		}

		if (assignBlocks.size() > 0 && assignBlocks.lastElement().instCon) {
			assignBlocks.pop();
		}
	}

	@Override
	public void exitDff_dec(Dff_decContext ctx) {
		for (Dff_singleContext dc : ctx.dff_single()) {
			if (dc.name() != null) {
				if (dc.name().TYPE_ID() == null)
					underlineError(dc.name(), String.format(ErrorStrings.NAME_NOT_TYPE, dc.name().getText()));

				Dff dff = new Dff(dc.name().getText());
				if (ctx.SIGNED() != null)
					dff.setSigned(true);
				if (nameUsed(dff.getName())) {
					nameUsedError(dc.name());
					return;
				}
				dffs.add(dff);
				dff.setWidth(bitWidthChecker.getWidth(dc));
				if (dictionary != null)
					dictionary.add(dff.getName());

				// if (dc.array_size().size() > 0) {
				// for (Array_sizeContext asc : dc.array_size()) {
				// dff.addWidth(asc.expr().getText());
				// }
				// }

				unusedSigs.add(new Sig(dff.getName(), ctx));

				// reqSigs.add(new Sig(dff.getName() + ".d", ctx)); // d input is required

				for (AssignBlock block : assignBlocks) {
					if (block != null) {
						for (Connection c : block.connections) {
							if (c.param) {
								if (c.port.equals("INIT"))
									dff.setInit(constExprParser.getValue(c.signalNode));
								else if (c.port.equals("IOB"))
									dff.setIOB(constExprParser.getValue(c.signalNode).getBigInt().intValue() != 0);
								else if (block.instCon)
									underlineError(c.portNode, String.format(ErrorStrings.DFF_UNKNOWN_PARAM, c.port));
								else
									underlineError(dc.name(), String.format(ErrorStrings.DFF_UNKNOWN_PARAM, c.port));
							} else {
								if (c.port.equals("clk")) {
									dff.setClk((ExprContext) c.signalNode);
								} else if (c.port.equals("rst")) {
									dff.setRst((ExprContext) c.signalNode);
								} else if (c.port.equals("d")) {

								} else {
									if (block.instCon)
										underlineError(c.portNode, String.format(ErrorStrings.DFF_UNKNOWN_INPUT, c.port));
									else
										underlineError(dc.name(), String.format(ErrorStrings.DFF_UNKNOWN_INPUT, c.port));
								}

							}
						}
						if (block.instCon) {
							assignBlocks.remove(block);
							break;
						}
					}
				}
				if (dff.getClk() == null) {
					underlineError(dc.name(), ErrorStrings.DFF_MISSING_CLK);
				}
			}
		}
	}

	@Override
	public void exitFsm_dec(Fsm_decContext ctx) {
		if (ctx.name() != null && ctx.fsm_states() != null && ctx.fsm_states().name() != null) {
			if (ctx.name().TYPE_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_TYPE, ctx.name().getText()));

			Fsm fsm = new Fsm(ctx.name().getText());
			if (nameUsed(fsm.getName())) {
				nameUsedError(ctx.name());
				return;
			}
			fsms.add(fsm);
			fsm.setWidth(bitWidthChecker.getWidth(ctx));
			if (dictionary != null)
				dictionary.add(fsm.getName());

			// if (ctx.array_size().size() > 0) {
			// for (Array_sizeContext asc : ctx.array_size()) {
			// fsm.addWidth(asc.expr().getText());
			// }
			// }

			unusedSigs.add(new Sig(fsm.getName(), ctx));

			// reqSigs.add(new Sig(fsm.getName() + ".d", ctx)); // d input required

			for (NameContext state : ctx.fsm_states().name()) {
				if (state.CONST_ID() == null)
					underlineError(state, String.format(ErrorStrings.NAME_NOT_CONST, state.getText()));

				if (state.getText().equals(Lucid.WIDTH_ATTR))
					underlineError(state, String.format(ErrorStrings.FSM_INVALID_STATE_NAME, state.getText()));
				else
					fsm.addState(new Constant(state.getText()));
			}

			// fsm.addWidth(Integer.toString(fsm.getNumStates()));

			for (AssignBlock block : assignBlocks) {
				if (block != null) {
					for (Connection c : block.connections) {
						if (c.param) {
							if (c.port.equals("INIT")) {
								if (Util.containsName(fsm.getStates(), c.signal)) {
									fsm.setDefState(new Constant(c.signal));
								} else {
									if (block.instCon)
										underlineError(c.signalNode, String.format(ErrorStrings.FSM_INVALID_STATE, c.signal, fsm.getName()));
									else
										underlineError(ctx.name(), String.format(ErrorStrings.FSM_INVALID_STATE, c.signal, fsm.getName()));
								}

							} else {
								if (block.instCon)
									underlineError(c.portNode, String.format(ErrorStrings.FSM_UNKNOWN_PARAM, c.port));
								else
									underlineError(ctx.name(), String.format(ErrorStrings.FSM_UNKNOWN_PARAM, c.port));
							}
						} else {
							if (c.port.equals("clk")) {
								fsm.setClk((ExprContext) c.signalNode);
							} else if (c.port.equals("rst")) {
								fsm.setRst((ExprContext) c.signalNode);
							} else if (c.port.equals("d")) {

							} else {
								if (block.instCon)
									underlineError(c.portNode, String.format(ErrorStrings.FSM_UNKNOWN_INPUT, c.port));
								else
									underlineError(ctx.name(), String.format(ErrorStrings.FSM_UNKNOWN_INPUT, c.port));
							}

						}
					}
				}
			}

			if (assignBlocks.size() > 0 && assignBlocks.lastElement().instCon) {
				assignBlocks.pop();
			}

			ArrayList<Constant> states = fsm.getStates();

			if (fsm.getDefState() == null && states.size() > 0)
				fsm.setDefState(states.get(0));

			if (fsm.getClk() == null) {
				underlineError(ctx.name(), ErrorStrings.FSM_MISSING_CLK);
			}
		}
	}

	/* Assign block management */
	@Override
	public void enterAssign_block(Assign_blockContext ctx) {
		assignBlocks.push(new AssignBlock());
	}

	@Override
	public void exitAssign_block(Assign_blockContext ctx) {
		assignBlocks.pop();
	}

	@Override
	public void enterInst_cons(Inst_consContext ctx) {
		assignBlocks.push(new AssignBlock(true));
	}

	@Override
	public void exitParam_con(Param_conContext ctx) {
		if ((ctx.expr() != null || ctx.REAL() != null) && ctx.name() != null) {
			if (ctx.name().CONST_ID() == null)
				underlineError(ctx.name(), String.format(ErrorStrings.NAME_NOT_CONST, ctx.name().getText()));
			AssignBlock block = assignBlocks.peek();

			// Check if the parameter belongs to FSM declaration
			RuleContext p = ctx;
			for (int i = 0; i < 4; i++) {
				p = p.parent;
				if (p == null)
					break;
			}

			if (ctx.expr() != null) {
				ConstValue cv = constExprParser.getValue(ctx.expr());

				if (p == null || !(p instanceof Fsm_decContext)) { // FSM states will have a null value so ignore them
					if (cv == null)
						underlineError(ctx.expr(), String.format(ErrorStrings.EXPR_NOT_CONSTANT, ctx.expr().getText()));
				}
				block.connections.add(new Connection(ctx.name().getText(), ctx.expr().getText(), true, cv, ctx.name(), ctx.expr(), (ConnectionContext) ctx.parent));
			} else {
				block.connections.add(new Connection(ctx.name().getText(), ctx.REAL().getText(), true, null, ctx.name(), ctx, (ConnectionContext) ctx.parent));
			}
		}
	}

	@Override
	public void exitSig_con(Sig_conContext ctx) {
		if (ctx.name() != null && ctx.expr() != null) {
			AssignBlock block = assignBlocks.peek();
			block.connections.add(new Connection(ctx.name().getText(), ctx.expr().getText(), false, null, ctx.name(), ctx.expr(), (ConnectionContext) ctx.parent));
		}
	}

	private void sigWritten(String sig, ParserRuleContext node) {
		if (Util.containsName(drivenSigs, sig)) {
			underlineError(node, String.format(ErrorStrings.MULTIPLE_DRIVERS, node.getText()));
		}
		if (assignedSigs.size() > 0) {
			ArrayList<HashSet<Sig>> list = assignedSigs.peek(); // get top of stack
			if (list.size() > 0) { // at least one set
				list.get(list.size() - 1).add(new Sig(sig, node)); // add sig to set
			}
		} else {
			if (writtenSigs != null)
				writtenSigs.add(new Sig(sig, node));
		}
	}

	private void checkSignal(SignalContext ctx, List<NameContext> names) {
		if (names.size() > 0) {

			boolean read = false, write = false;
			if (ctx.parent instanceof ExprSignalContext || ctx.parent instanceof Var_assignContext || ctx.parent instanceof AttributeContext)
				read = true;
			else if (ctx.parent instanceof Assign_statContext)
				write = true;
			else
				Util.log.severe("Error unknown signal usage on line " + ctx.start.getLine() + ". Class: " + ctx.parent.getClass());

			String name = names.get(0).getText();

			boolean constant = names.get(0).CONST_ID() != null;
			boolean global = names.get(0).SPACE_ID() != null;

			NameContext attribute = names.get(names.size() - 1);
			boolean hasAttr = attribute.CONST_ID() != null;
			boolean isWidth = attribute.getText().equals(Lucid.WIDTH_ATTR);

			if (constant) {
				if (write)
					underlineError(ctx, ErrorStrings.CONST_READ_ONLY);

				if (ctx.name().size() > 1 && !ctx.name(1).getText().equals(Lucid.WIDTH_ATTR))
					underlineError(ctx.name(0), String.format(ErrorStrings.CONST_NO_MEMBERS, ctx.name(0).getText(), ctx.name(1).getText()));

				if (!Util.containsName(constants, ctx.name(0).getText()))
					underlineError(ctx.name(0), ErrorStrings.UNDECLARED_CONST);
				else {
					Util.removeByName(unusedSigs, ctx.name(0).getText());
					return;
				}
			}

			if (global) {
				if (write)
					underlineError(ctx, ErrorStrings.CONST_READ_ONLY);

				HashMap<String, List<Constant>> gC = MainWindow.getGlobalConstants();
				List<Constant> consts = gC.get(names.get(0).getText());

				if (names.size() < 2)
					underlineError(ctx.name(0), ErrorStrings.NAMESPACE_DIRECT);

				if ((names.size() > 2 && !isWidth) || names.size() > 3)
					underlineError(ctx.name(ctx.name().size() - 1), String.format(ErrorStrings.CONST_NO_MEMBERS, ctx.name(1).getText(), ctx.name(2).getText()));

				if (consts == null)
					underlineError(ctx.name(0), String.format(ErrorStrings.UNKNOWN_NAMESPACE, ctx.name(0).getText()));

				if (names.size() >= 2 && consts != null && !Util.containsName(consts, names.get(1).getText()))
					underlineError(ctx.name(1), String.format(ErrorStrings.NOT_IN_NAMESPACE, names.get(1).getText(), names.get(0).getText()));

				return;
			}

			// check for DFFs
			if (Util.containsName(dffs, name)) {
				if (hasAttr && !isWidth)
					underlineError(attribute, String.format(ErrorStrings.INVALID_ATTRIBUTE, attribute.getText()));

				if (names.size() < 2) {
					if (!hasAttr)
						underlineError(names.get(0), ErrorStrings.DFF_NO_SIG_SELECTED);
					return;
				}

				if (!hasAttr && read)
					Util.removeByName(unusedSigs, names.get(0).getText());

				String sig = names.get(1).getText();
				if (sig.equals("q")) {
					if (ctx.getParent().getClass() == Assign_statContext.class) {
						underlineError(names.get(1), ErrorStrings.DFF_Q_ASSIGNED);
					}
					if (write) {
						underlineError(names.get(1), String.format(ErrorStrings.SIG_READ_ONLY, names.get(1).getText()));
					}
				} else if (sig.equals("d")) {
					if (write) {
						sigWritten(name + ".d", ctx);
					}
					if (ctx.getParent().getClass() != Assign_statContext.class) {
						underlineError(names.get(1), ErrorStrings.DFF_D_READ);
					}
					if (read) {
						underlineError(names.get(1), String.format(ErrorStrings.SIG_WRITE_ONLY, names.get(1).getText()));
					}
				} else if (!hasAttr || names.get(1) != attribute) {
					underlineError(names.get(1), String.format(ErrorStrings.DFF_INVALID_SIG, sig));
					// return;
				}

				// if (isWidth) {
				// if (names.size() > 3) {
				// underlineError(names.get(2), ErrorStrings.DFF_INVALID_SIG);
				// }
				// return;
				// }
				return;
			}

			// check for FSMs
			Fsm f = Util.getByName(fsms, name);
			if (f != null) {
				if (hasAttr && !isWidth) {
					if (!Util.containsName(f.getStates(), attribute.getText()))
						underlineError(attribute, String.format(ErrorStrings.FSM_INVALID_STATE, attribute.getText(), name));
					if (names.size() > 2) {
						underlineError(names.get(1), ErrorStrings.FSM_INVALID_SIG);
					}
					return;
				}

				if (isWidth) {
					if (names.size() > 3) {
						underlineError(names.get(2), ErrorStrings.FSM_INVALID_SIG);
					}
					return;
				}

				if (!hasAttr && read)
					Util.removeByName(unusedSigs, names.get(0).getText());

				if (names.size() != 2) {
					if (!hasAttr)
						underlineError(names.get(0), ErrorStrings.FSM_NO_SIG_SELECTED);
					return;
				}
				String sig = names.get(1).getText();
				if (sig.equals("q")) {
					if (ctx.getParent().getClass() == Assign_statContext.class) {
						underlineError(names.get(1), ErrorStrings.FSM_Q_ASSIGNED);
					}
					if (write) {
						underlineError(names.get(1), String.format(ErrorStrings.SIG_READ_ONLY, names.get(1).getText()));
					}
				} else if (sig.equals("d")) {
					if (write) {
						sigWritten(name + ".d", ctx);
					}
					if (ctx.getParent().getClass() != Assign_statContext.class) {
						underlineError(names.get(1), ErrorStrings.FSM_D_READ);
					}
					if (read) {
						underlineError(names.get(1), String.format(ErrorStrings.SIG_WRITE_ONLY, names.get(1).getText()));
					}
				} else if (!hasAttr || names.get(1) != attribute) {
					underlineError(names.get(1), String.format(ErrorStrings.FSM_INVALID_SIG, sig));
				}
				return;
			}

			// check for Signals, Inputs, Outputs, Inouts
			if (Util.containsName(sigs, name)) {
				if (hasAttr && !isWidth)
					underlineError(attribute, String.format(ErrorStrings.INVALID_ATTRIBUTE, attribute.getText()));

				if (!hasAttr)
					Util.removeByName(unusedSigs, names.get(0).getText());

				// if (names.size() > 2 || (names.size() > 1 && !isWidth)) {
				// underlineError(ctx, String.format(ErrorStrings.SIG_NOT_MODULE, name));

				// } else
				if (writtenSigs != null)
					if (write) {
						sigWritten(name, ctx);
					} else if (read) {
						boolean written = false;
						if (Util.containsName(writtenSigs, name)) {
							written = true;
						} else if (assignedSigs.size() > 0) {
							for (ArrayList<HashSet<Sig>> list : assignedSigs) {
								// ArrayList<HashSet<Sig>> list = assignedSigs.peek(); // get top of stack
								if (list.size() > 0) { // at least one set
									if (Util.containsName(list.get(list.size() - 1), name)) { // written in active block
										written = true;
										break;
									}
								}
							}
						}
						if (!hasAttr && !written && !Util.containsName(drivenSigs, name))
							underlineError(names.get(0), String.format(ErrorStrings.READ_BEFORE_WRITE, names.get(0).getText()));

					}
				return; // valid signal
			}

			// Inputs
			if (Util.containsName(inputs, name)) {
				if (hasAttr && !isWidth)
					underlineError(attribute, String.format(ErrorStrings.INVALID_ATTRIBUTE, attribute.getText()));

				if (!hasAttr)
					Util.removeByName(unusedSigs, names.get(0).getText());

				// if (names.size() > 2 || (names.size() > 1 && !isWidth)) {
				// underlineError(ctx, String.format(ErrorStrings.SIG_NOT_MODULE, name));
				//
				// } else
				if (write) {
					underlineError(names.get(0), String.format(ErrorStrings.SIG_READ_ONLY, name));
				}
				return; // valid signal
			}

			// Outputs
			if (Util.containsName(outputs, name)) {
				if (hasAttr && !isWidth)
					underlineError(attribute, String.format(ErrorStrings.INVALID_ATTRIBUTE, attribute.getText()));

				if (!hasAttr)
					Util.removeByName(unusedSigs, names.get(0).getText());

				// if (names.size() > 2 || (names.size() > 1 && !isWidth)) {
				// underlineError(ctx, String.format(ErrorStrings.SIG_NOT_MODULE, name));

				// } else
				if (write && !hasAttr) {
					sigWritten(name, ctx);
				}
				if (read && !hasAttr) {
					underlineError(names.get(0), String.format(ErrorStrings.SIG_WRITE_ONLY, name));
				}
				return; // valid signal
			}

			// Inouts
			if (Util.containsName(inouts, name)) {
				if (hasAttr) {
					if (!isWidth) {
						underlineError(attribute, String.format(ErrorStrings.INVALID_ATTRIBUTE, attribute.getText()));
					}
					// else {
					// if (names.size() != 2) {
					// underlineError(ctx, String.format(ErrorStrings.SIG_NOT_MODULE, name));
					// }
					// }
				}

				if (!hasAttr)
					Util.removeByName(unusedSigs, names.get(0).getText());

				if (ctx.parent.parent instanceof Sig_conContext) {
					if (names.size() != 1) {
						underlineError(ctx, ErrorStrings.INOUT_CONNECTION_INDIRECT);
					}
					return;
				}
				if (names.size() == 1) {
					underlineError(ctx, String.format(ErrorStrings.INOUT_ACCESSED_DIRECTLY, name));
				} else {
					String member = names.get(1).getText();
					switch (member) {
					case "enable":
					case "write":
						if (!hasAttr) {
							if (read)
								underlineError(names.get(1), String.format(ErrorStrings.SIG_WRITE_ONLY, member));
							if (write)
								sigWritten(name + "." + member, ctx);
						}
						break;
					case "read":
						if (write && !hasAttr)
							underlineError(names.get(1), String.format(ErrorStrings.SIG_READ_ONLY, member));
						break;
					default:
						underlineError(ctx, String.format(ErrorStrings.INOUT_UNKNOWN_SIG, member));
						break;
					}
				}
				return; // valid signal
			}

			// check for Vars
			if (Util.containsName(vars, name)) {
				if (hasAttr && !isWidth)
					underlineError(attribute, String.format(ErrorStrings.INVALID_ATTRIBUTE, attribute.getText()));

				if (!hasAttr)
					Util.removeByName(unusedSigs, names.get(0).getText());

				if (names.size() > 2 || (names.size() > 1 && !isWidth)) {
					underlineError(ctx, String.format(ErrorStrings.VAR_NOT_MODULE, name));

				}
				return; // valid var
			}

			// check for modules
			InstModule iModule = Util.getByName(instModules, name);
			if (iModule != null) {

				if (!isWidth && read)
					Util.removeByName(unusedSigs, names.get(0).getText());

				Module module = iModule.getType();

				if (names.size() < 2) {
					underlineError(names.get(0), ErrorStrings.MODULE_NO_SIG_SELECTED);
					return;
				}

				if (names.size() == 2 && isWidth)
					return;

				// if (names.size() > 2 && !hasAttr || names.size() > 3)
				// underlineError(names.get(2), String.format(ErrorStrings.SIG_UNDEFINED, names.get(2).getText(), names.get(1).getText()));

				for (Connection c : iModule.getConnections()) {
					if (c.port.equals(names.get(1).getText())) {
						underlineError(names.get(1), String.format(ErrorStrings.MODULE_SIG_ALREADY_ASSIGNED, names.get(1).getText()));
						break;
					}
				}

				if (write)
					sigWritten(names.get(0).getText() + "." + names.get(1).getText(), ctx);
				String sigName = names.get(1).getText();

				if (Util.containsName(module.getInouts(), sigName)) {
					if (names.size() != 3) {
						underlineError(names.get(1), ErrorStrings.INOUT_ACCESSED_DIRECTLY);
					} else {
						switch (names.get(2).getText()) {
						case "enable":
						case "write":
							if (!hasAttr) {
								if (read)
									underlineError(names.get(2), String.format(ErrorStrings.SIG_WRITE_ONLY, names.get(2).getText()));
								if (write)
									sigWritten(names.get(0).getText() + "." + names.get(1).getText() + "." + names.get(2), ctx);
							}
							break;

						case "read":
							if (write && !hasAttr)
								underlineError(names.get(2), String.format(ErrorStrings.SIG_READ_ONLY, names.get(2).getText()));
							break;
						default:
							underlineError(names.get(1), String.format(ErrorStrings.INOUT_UNKNOWN_SIG, names.get(2).getText()));
							break;
						}
					}
				} else if (Util.containsName(module.getOutputs(), sigName)) {
					if (write) {
						underlineError(names.get(1), String.format(ErrorStrings.SIG_READ_ONLY, names.get(1).getText()));
					}
				} else if (Util.containsName(module.getInputs(), sigName)) {
					if (read) {
						underlineError(names.get(1), String.format(ErrorStrings.SIG_WRITE_ONLY, names.get(1).getText()));
					}
				} else {
					underlineError(names.get(1), String.format(ErrorStrings.MODULE_SIG_UNDEFINED, name, module.getName()));
				}

				return;
			}

			if (hasAttr && write)
				underlineError(attribute, ErrorStrings.ATTRIBUTE_READ_ONLY);

			if (hasAttr && ctx.bit_selection().size() > 0 && !isWidth) {
				underlineError(ctx.bit_selection(0), ErrorStrings.ATTRIBUTE_BIT_SELECT);
			}

			underlineError(names.get(0), String.format(ErrorStrings.UNDECLARED_NAME, name));
		}
	}

	/* Checking */
	@Override
	public void exitSignal(SignalContext ctx) {
		// Check if the signal is from a FSM parameter and ignore if it is.
		if (ctx.parent instanceof ExprContext && ctx.parent.parent instanceof Param_conContext) {
			RuleContext p = ctx;
			for (int i = 0; i < 6; i++) {
				p = p.parent;
				if (p == null)
					break;
			}

			if (p != null && p instanceof Fsm_decContext) {
				return;
			}
		}

		checkSignal(ctx, ctx.name());
	}

	@Override
	public void enterIf_stat(If_statContext ctx) {
		ArrayList<HashSet<Sig>> list = new ArrayList<HashSet<Sig>>();
		assignedSigs.push(list);
		list.add(new HashSet<Sig>());
	}

	@Override
	public void exitIf_stat(If_statContext ctx) {
		ArrayList<HashSet<Sig>> list = assignedSigs.pop();
		if (list.size() == 2) {
			HashSet<Sig> h1, h2;
			h1 = list.get(0);
			h2 = list.get(1);
			h1.retainAll(h2);
			writtenSigs.addAll(h1);
		}
	}

	@Override
	public void enterElse_stat(Else_statContext ctx) {
		assignedSigs.peek().add(new HashSet<Sig>());
	}

	@Override
	public void enterCase_stat(Case_statContext ctx) {
		ArrayList<HashSet<Sig>> list = new ArrayList<HashSet<Sig>>();
		assignedSigs.push(list);
		hasDefaultCase.push(false);
	}

	@Override
	public void exitCase_stat(Case_statContext ctx) {
		ArrayList<HashSet<Sig>> list = assignedSigs.pop();
		if (hasDefaultCase.pop()) {
			HashSet<Sig> hs = list.get(0);
			for (int i = 1; i < list.size(); i++) {
				hs.retainAll(list.get(i));
			}
			writtenSigs.addAll(hs);
		}
	}

	@Override
	public void enterCase_elem(Case_elemContext ctx) {
		assignedSigs.peek().add(new HashSet<Sig>());
	}

	@Override
	public void exitCase_elem(Case_elemContext ctx) {
		if (ctx.getChild(0).getText().equals("default")) { // is default state
			hasDefaultCase.pop(); // remove old value
			hasDefaultCase.push(true); // it has def state
		}
	}

	public ConstExprParser getExprParser() {
		return constExprParser;
	}

}
