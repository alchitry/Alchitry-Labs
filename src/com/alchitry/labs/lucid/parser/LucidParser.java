// Generated from Lucid.g4 by ANTLR 4.5.3

package com.alchitry.labs.lucid.parser;

import java.util.List;

import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LucidParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		HEX=60, BIN=61, DEC=62, REAL=63, INT=64, STRING=65, SIGNED=66, TYPE_ID=67, 
		CONST_ID=68, SPACE_ID=69, FUNCTION_ID=70, BLOCK_COMMENT=71, COMMENT=72, 
		WS=73;
	public static final int
		RULE_source = 0, RULE_global = 1, RULE_global_stat = 2, RULE_module = 3, 
		RULE_param_list = 4, RULE_port_list = 5, RULE_param_dec = 6, RULE_port_dec = 7, 
		RULE_input_dec = 8, RULE_output_dec = 9, RULE_inout_dec = 10, RULE_param_name = 11, 
		RULE_param_constraint = 12, RULE_array_size = 13, RULE_struct_type = 14, 
		RULE_module_body = 15, RULE_stat = 16, RULE_const_dec = 17, RULE_assign_block = 18, 
		RULE_sig_con = 19, RULE_param_con = 20, RULE_type_dec = 21, RULE_dff_single = 22, 
		RULE_var_dec = 23, RULE_sig_dec = 24, RULE_dff_dec = 25, RULE_fsm_dec = 26, 
		RULE_fsm_states = 27, RULE_module_inst = 28, RULE_inst_cons = 29, RULE_con_list = 30, 
		RULE_connection = 31, RULE_struct_member = 32, RULE_struct_dec = 33, RULE_always_block = 34, 
		RULE_always_stat = 35, RULE_block = 36, RULE_assign_stat = 37, RULE_array_index = 38, 
		RULE_bit_selector = 39, RULE_bit_selection = 40, RULE_signal = 41, RULE_case_stat = 42, 
		RULE_case_elem = 43, RULE_if_stat = 44, RULE_else_stat = 45, RULE_for_stat = 46, 
		RULE_function = 47, RULE_number = 48, RULE_expr = 49, RULE_var_assign = 50, 
		RULE_name = 51;
	public static final String[] ruleNames = {
		"source", "global", "global_stat", "module", "param_list", "port_list", 
		"param_dec", "port_dec", "input_dec", "output_dec", "inout_dec", "param_name", 
		"param_constraint", "array_size", "struct_type", "module_body", "stat", 
		"const_dec", "assign_block", "sig_con", "param_con", "type_dec", "dff_single", 
		"var_dec", "sig_dec", "dff_dec", "fsm_dec", "fsm_states", "module_inst", 
		"inst_cons", "con_list", "connection", "struct_member", "struct_dec", 
		"always_block", "always_stat", "block", "assign_stat", "array_index", 
		"bit_selector", "bit_selection", "signal", "case_stat", "case_elem", "if_stat", 
		"else_stat", "for_stat", "function", "number", "expr", "var_assign", "name"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'global'", "'{'", "'}'", "';'", "'module'", "'#('", "','", "')'", 
		"'('", "':'", "'input'", "'output'", "'inout'", "'='", "'['", "']'", "'<'", 
		"'.'", "'>'", "'const'", "'#'", "'var'", "'sig'", "'dff'", "'fsm'", "'struct'", 
		"'always'", "'+'", "'-'", "'case'", "'default'", "'if'", "'else'", "'for'", 
		"'c{'", "'x{'", "'~'", "'!'", "'*'", "'/'", "'>>'", "'<<'", "'<<<'", "'>>>'", 
		"'|'", "'&'", "'^'", "'~^'", "'~&'", "'~|'", "'=='", "'!='", "'>='", "'<='", 
		"'||'", "'&&'", "'?'", "'++'", "'--'", null, null, null, null, null, null, 
		"'signed'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"HEX", "BIN", "DEC", "REAL", "INT", "STRING", "SIGNED", "TYPE_ID", "CONST_ID", 
		"SPACE_ID", "FUNCTION_ID", "BLOCK_COMMENT", "COMMENT", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Lucid.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LucidParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SourceContext extends ParserRuleContext {
		public List<GlobalContext> global() {
			return getRuleContexts(GlobalContext.class);
		}
		public GlobalContext global(int i) {
			return getRuleContext(GlobalContext.class,i);
		}
		public List<ModuleContext> module() {
			return getRuleContexts(ModuleContext.class);
		}
		public ModuleContext module(int i) {
			return getRuleContext(ModuleContext.class,i);
		}
		public SourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_source; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterSource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitSource(this);
		}
	}

	public final SourceContext source() throws RecognitionException {
		SourceContext _localctx = new SourceContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_source);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__4) {
				{
				setState(106);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(104);
					global();
					}
					break;
				case T__4:
					{
					setState(105);
					module();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GlobalContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<Global_statContext> global_stat() {
			return getRuleContexts(Global_statContext.class);
		}
		public Global_statContext global_stat(int i) {
			return getRuleContext(Global_statContext.class,i);
		}
		public GlobalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterGlobal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitGlobal(this);
		}
	}

	public final GlobalContext global() throws RecognitionException {
		GlobalContext _localctx = new GlobalContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_global);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(T__0);
			setState(112);
			name();
			setState(113);
			match(T__1);
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__19 || _la==T__25) {
				{
				{
				setState(114);
				global_stat();
				}
				}
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(120);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Global_statContext extends ParserRuleContext {
		public Struct_decContext struct_dec() {
			return getRuleContext(Struct_decContext.class,0);
		}
		public Const_decContext const_dec() {
			return getRuleContext(Const_decContext.class,0);
		}
		public Global_statContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterGlobal_stat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitGlobal_stat(this);
		}
	}

	public final Global_statContext global_stat() throws RecognitionException {
		Global_statContext _localctx = new Global_statContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_global_stat);
		try {
			setState(126);
			switch (_input.LA(1)) {
			case T__25:
				enterOuterAlt(_localctx, 1);
				{
				setState(122);
				struct_dec();
				}
				break;
			case T__19:
				enterOuterAlt(_localctx, 2);
				{
				setState(123);
				const_dec();
				setState(124);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Port_listContext port_list() {
			return getRuleContext(Port_listContext.class,0);
		}
		public Module_bodyContext module_body() {
			return getRuleContext(Module_bodyContext.class,0);
		}
		public Param_listContext param_list() {
			return getRuleContext(Param_listContext.class,0);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitModule(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_module);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(T__4);
			setState(129);
			name();
			setState(131);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(130);
				param_list();
				}
			}

			setState(133);
			port_list();
			setState(134);
			module_body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_listContext extends ParserRuleContext {
		public List<Param_decContext> param_dec() {
			return getRuleContexts(Param_decContext.class);
		}
		public Param_decContext param_dec(int i) {
			return getRuleContext(Param_decContext.class,i);
		}
		public Param_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterParam_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitParam_list(this);
		}
	}

	public final Param_listContext param_list() throws RecognitionException {
		Param_listContext _localctx = new Param_listContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_param_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(T__5);
			setState(137);
			param_dec();
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(138);
				match(T__6);
				setState(139);
				param_dec();
				}
				}
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(145);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Port_listContext extends ParserRuleContext {
		public List<Port_decContext> port_dec() {
			return getRuleContexts(Port_decContext.class);
		}
		public Port_decContext port_dec(int i) {
			return getRuleContext(Port_decContext.class,i);
		}
		public Port_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_port_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterPort_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitPort_list(this);
		}
	}

	public final Port_listContext port_list() throws RecognitionException {
		Port_listContext _localctx = new Port_listContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_port_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(T__8);
			setState(148);
			port_dec();
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(149);
				match(T__6);
				setState(150);
				port_dec();
				}
				}
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(156);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_decContext extends ParserRuleContext {
		public Param_nameContext param_name() {
			return getRuleContext(Param_nameContext.class,0);
		}
		public Param_constraintContext param_constraint() {
			return getRuleContext(Param_constraintContext.class,0);
		}
		public Param_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterParam_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitParam_dec(this);
		}
	}

	public final Param_decContext param_dec() throws RecognitionException {
		Param_decContext _localctx = new Param_decContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_param_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			param_name();
			setState(161);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(159);
				match(T__9);
				setState(160);
				param_constraint();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Port_decContext extends ParserRuleContext {
		public Input_decContext input_dec() {
			return getRuleContext(Input_decContext.class,0);
		}
		public Output_decContext output_dec() {
			return getRuleContext(Output_decContext.class,0);
		}
		public Inout_decContext inout_dec() {
			return getRuleContext(Inout_decContext.class,0);
		}
		public Port_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_port_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterPort_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitPort_dec(this);
		}
	}

	public final Port_decContext port_dec() throws RecognitionException {
		Port_decContext _localctx = new Port_decContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_port_dec);
		try {
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(163);
				input_dec();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				output_dec();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(165);
				inout_dec();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Input_decContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode SIGNED() { return getToken(LucidParser.SIGNED, 0); }
		public Struct_typeContext struct_type() {
			return getRuleContext(Struct_typeContext.class,0);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Input_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterInput_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitInput_dec(this);
		}
	}

	public final Input_decContext input_dec() throws RecognitionException {
		Input_decContext _localctx = new Input_decContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_input_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			_la = _input.LA(1);
			if (_la==SIGNED) {
				{
				setState(168);
				match(SIGNED);
				}
			}

			setState(171);
			match(T__10);
			setState(173);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(172);
				struct_type();
				}
			}

			setState(175);
			name();
			setState(179);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(176);
				array_size();
				}
				}
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Output_decContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode SIGNED() { return getToken(LucidParser.SIGNED, 0); }
		public Struct_typeContext struct_type() {
			return getRuleContext(Struct_typeContext.class,0);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Output_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_output_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterOutput_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitOutput_dec(this);
		}
	}

	public final Output_decContext output_dec() throws RecognitionException {
		Output_decContext _localctx = new Output_decContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_output_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			_la = _input.LA(1);
			if (_la==SIGNED) {
				{
				setState(182);
				match(SIGNED);
				}
			}

			setState(185);
			match(T__11);
			setState(187);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(186);
				struct_type();
				}
			}

			setState(189);
			name();
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(190);
				array_size();
				}
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Inout_decContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode SIGNED() { return getToken(LucidParser.SIGNED, 0); }
		public Struct_typeContext struct_type() {
			return getRuleContext(Struct_typeContext.class,0);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Inout_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inout_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterInout_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitInout_dec(this);
		}
	}

	public final Inout_decContext inout_dec() throws RecognitionException {
		Inout_decContext _localctx = new Inout_decContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_inout_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			_la = _input.LA(1);
			if (_la==SIGNED) {
				{
				setState(196);
				match(SIGNED);
				}
			}

			setState(199);
			match(T__12);
			setState(201);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(200);
				struct_type();
				}
			}

			setState(203);
			name();
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(204);
				array_size();
				}
				}
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_nameContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Param_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterParam_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitParam_name(this);
		}
	}

	public final Param_nameContext param_name() throws RecognitionException {
		Param_nameContext _localctx = new Param_nameContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_param_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			name();
			setState(213);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(211);
				match(T__13);
				setState(212);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_constraintContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Param_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_constraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterParam_constraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitParam_constraint(this);
		}
	}

	public final Param_constraintContext param_constraint() throws RecognitionException {
		Param_constraintContext _localctx = new Param_constraintContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_param_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Array_sizeContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Array_sizeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_size; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterArray_size(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitArray_size(this);
		}
	}

	public final Array_sizeContext array_size() throws RecognitionException {
		Array_sizeContext _localctx = new Array_sizeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_array_size);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(T__14);
			setState(218);
			expr(0);
			setState(219);
			match(T__15);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Struct_typeContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public Struct_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_struct_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStruct_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStruct_type(this);
		}
	}

	public final Struct_typeContext struct_type() throws RecognitionException {
		Struct_typeContext _localctx = new Struct_typeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_struct_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			match(T__16);
			setState(222);
			name();
			setState(225);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(223);
				match(T__17);
				setState(224);
				name();
				}
			}

			setState(227);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Module_bodyContext extends ParserRuleContext {
		public List<StatContext> stat() {
			return getRuleContexts(StatContext.class);
		}
		public StatContext stat(int i) {
			return getRuleContext(StatContext.class,i);
		}
		public Module_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterModule_body(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitModule_body(this);
		}
	}

	public final Module_bodyContext module_body() throws RecognitionException {
		Module_bodyContext _localctx = new Module_bodyContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_module_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			match(T__1);
			setState(233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 18)) & ~0x3f) == 0 && ((1L << (_la - 18)) & ((1L << (T__17 - 18)) | (1L << (T__19 - 18)) | (1L << (T__20 - 18)) | (1L << (T__21 - 18)) | (1L << (T__22 - 18)) | (1L << (T__23 - 18)) | (1L << (T__24 - 18)) | (1L << (T__25 - 18)) | (1L << (T__26 - 18)) | (1L << (SIGNED - 18)) | (1L << (TYPE_ID - 18)) | (1L << (CONST_ID - 18)) | (1L << (SPACE_ID - 18)))) != 0)) {
				{
				{
				setState(230);
				stat();
				}
				}
				setState(235);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(236);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatContext extends ParserRuleContext {
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }
	 
		public StatContext() { }
		public void copyFrom(StatContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StatModuleInstContext extends StatContext {
		public Module_instContext module_inst() {
			return getRuleContext(Module_instContext.class,0);
		}
		public StatModuleInstContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatModuleInst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatModuleInst(this);
		}
	}
	public static class StatConstContext extends StatContext {
		public Const_decContext const_dec() {
			return getRuleContext(Const_decContext.class,0);
		}
		public StatConstContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatConst(this);
		}
	}
	public static class StatDFFContext extends StatContext {
		public Dff_decContext dff_dec() {
			return getRuleContext(Dff_decContext.class,0);
		}
		public StatDFFContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatDFF(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatDFF(this);
		}
	}
	public static class StatVarContext extends StatContext {
		public Var_decContext var_dec() {
			return getRuleContext(Var_decContext.class,0);
		}
		public StatVarContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatVar(this);
		}
	}
	public static class StatFSMContext extends StatContext {
		public Fsm_decContext fsm_dec() {
			return getRuleContext(Fsm_decContext.class,0);
		}
		public StatFSMContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatFSM(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatFSM(this);
		}
	}
	public static class StatAlwaysContext extends StatContext {
		public Always_blockContext always_block() {
			return getRuleContext(Always_blockContext.class,0);
		}
		public StatAlwaysContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatAlways(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatAlways(this);
		}
	}
	public static class StatStructContext extends StatContext {
		public Struct_decContext struct_dec() {
			return getRuleContext(Struct_decContext.class,0);
		}
		public StatStructContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatStruct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatStruct(this);
		}
	}
	public static class StatSigContext extends StatContext {
		public Sig_decContext sig_dec() {
			return getRuleContext(Sig_decContext.class,0);
		}
		public StatSigContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatSig(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatSig(this);
		}
	}
	public static class StatAssignContext extends StatContext {
		public Assign_blockContext assign_block() {
			return getRuleContext(Assign_blockContext.class,0);
		}
		public StatAssignContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStatAssign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStatAssign(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		StatContext _localctx = new StatContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_stat);
		try {
			setState(259);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new StatConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(238);
				const_dec();
				setState(239);
				match(T__3);
				}
				break;
			case 2:
				_localctx = new StatVarContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(241);
				var_dec();
				setState(242);
				match(T__3);
				}
				break;
			case 3:
				_localctx = new StatSigContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(244);
				sig_dec();
				setState(245);
				match(T__3);
				}
				break;
			case 4:
				_localctx = new StatFSMContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(247);
				fsm_dec();
				setState(248);
				match(T__3);
				}
				break;
			case 5:
				_localctx = new StatDFFContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(250);
				dff_dec();
				setState(251);
				match(T__3);
				}
				break;
			case 6:
				_localctx = new StatModuleInstContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(253);
				module_inst();
				setState(254);
				match(T__3);
				}
				break;
			case 7:
				_localctx = new StatAssignContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(256);
				assign_block();
				}
				break;
			case 8:
				_localctx = new StatAlwaysContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(257);
				always_block();
				}
				break;
			case 9:
				_localctx = new StatStructContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(258);
				struct_dec();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Const_decContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Const_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_const_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterConst_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitConst_dec(this);
		}
	}

	public final Const_decContext const_dec() throws RecognitionException {
		Const_decContext _localctx = new Const_decContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_const_dec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(T__19);
			setState(262);
			name();
			setState(263);
			match(T__13);
			setState(264);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_blockContext extends ParserRuleContext {
		public Con_listContext con_list() {
			return getRuleContext(Con_listContext.class,0);
		}
		public List<Assign_blockContext> assign_block() {
			return getRuleContexts(Assign_blockContext.class);
		}
		public Assign_blockContext assign_block(int i) {
			return getRuleContext(Assign_blockContext.class,i);
		}
		public List<Dff_decContext> dff_dec() {
			return getRuleContexts(Dff_decContext.class);
		}
		public Dff_decContext dff_dec(int i) {
			return getRuleContext(Dff_decContext.class,i);
		}
		public List<Fsm_decContext> fsm_dec() {
			return getRuleContexts(Fsm_decContext.class);
		}
		public Fsm_decContext fsm_dec(int i) {
			return getRuleContext(Fsm_decContext.class,i);
		}
		public List<Module_instContext> module_inst() {
			return getRuleContexts(Module_instContext.class);
		}
		public Module_instContext module_inst(int i) {
			return getRuleContext(Module_instContext.class,i);
		}
		public Assign_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterAssign_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitAssign_block(this);
		}
	}

	public final Assign_blockContext assign_block() throws RecognitionException {
		Assign_blockContext _localctx = new Assign_blockContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_assign_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			con_list();
			setState(267);
			match(T__1);
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 18)) & ~0x3f) == 0 && ((1L << (_la - 18)) & ((1L << (T__17 - 18)) | (1L << (T__20 - 18)) | (1L << (T__23 - 18)) | (1L << (T__24 - 18)) | (1L << (SIGNED - 18)) | (1L << (TYPE_ID - 18)) | (1L << (CONST_ID - 18)) | (1L << (SPACE_ID - 18)))) != 0)) {
				{
				setState(276);
				switch (_input.LA(1)) {
				case T__23:
				case T__24:
				case SIGNED:
				case TYPE_ID:
				case CONST_ID:
				case SPACE_ID:
					{
					setState(271);
					switch (_input.LA(1)) {
					case T__23:
					case SIGNED:
						{
						setState(268);
						dff_dec();
						}
						break;
					case T__24:
						{
						setState(269);
						fsm_dec();
						}
						break;
					case TYPE_ID:
					case CONST_ID:
					case SPACE_ID:
						{
						setState(270);
						module_inst();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(273);
					match(T__3);
					}
					break;
				case T__17:
				case T__20:
					{
					setState(275);
					assign_block();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(281);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sig_conContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Sig_conContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sig_con; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterSig_con(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitSig_con(this);
		}
	}

	public final Sig_conContext sig_con() throws RecognitionException {
		Sig_conContext _localctx = new Sig_conContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_sig_con);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			match(T__17);
			setState(284);
			name();
			setState(285);
			match(T__8);
			setState(286);
			expr(0);
			setState(287);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_conContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode REAL() { return getToken(LucidParser.REAL, 0); }
		public Param_conContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_con; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterParam_con(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitParam_con(this);
		}
	}

	public final Param_conContext param_con() throws RecognitionException {
		Param_conContext _localctx = new Param_conContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_param_con);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			match(T__20);
			setState(290);
			name();
			setState(291);
			match(T__8);
			setState(294);
			switch (_input.LA(1)) {
			case T__1:
			case T__8:
			case T__28:
			case T__34:
			case T__36:
			case T__37:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case HEX:
			case BIN:
			case DEC:
			case INT:
			case STRING:
			case TYPE_ID:
			case CONST_ID:
			case SPACE_ID:
			case FUNCTION_ID:
				{
				setState(292);
				expr(0);
				}
				break;
			case REAL:
				{
				setState(293);
				match(REAL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(296);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_decContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Type_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterType_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitType_dec(this);
		}
	}

	public final Type_decContext type_dec() throws RecognitionException {
		Type_decContext _localctx = new Type_decContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_type_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			name();
			setState(302);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(299);
				array_size();
				}
				}
				setState(304);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Dff_singleContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Inst_consContext inst_cons() {
			return getRuleContext(Inst_consContext.class,0);
		}
		public Dff_singleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dff_single; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterDff_single(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitDff_single(this);
		}
	}

	public final Dff_singleContext dff_single() throws RecognitionException {
		Dff_singleContext _localctx = new Dff_singleContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_dff_single);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			name();
			setState(309);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(306);
				array_size();
				}
				}
				setState(311);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(313);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(312);
				inst_cons();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Var_decContext extends ParserRuleContext {
		public List<Type_decContext> type_dec() {
			return getRuleContexts(Type_decContext.class);
		}
		public Type_decContext type_dec(int i) {
			return getRuleContext(Type_decContext.class,i);
		}
		public Var_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterVar_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitVar_dec(this);
		}
	}

	public final Var_decContext var_dec() throws RecognitionException {
		Var_decContext _localctx = new Var_decContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_var_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			match(T__21);
			setState(316);
			type_dec();
			setState(321);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(317);
				match(T__6);
				setState(318);
				type_dec();
				}
				}
				setState(323);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sig_decContext extends ParserRuleContext {
		public List<Type_decContext> type_dec() {
			return getRuleContexts(Type_decContext.class);
		}
		public Type_decContext type_dec(int i) {
			return getRuleContext(Type_decContext.class,i);
		}
		public TerminalNode SIGNED() { return getToken(LucidParser.SIGNED, 0); }
		public Struct_typeContext struct_type() {
			return getRuleContext(Struct_typeContext.class,0);
		}
		public Sig_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sig_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterSig_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitSig_dec(this);
		}
	}

	public final Sig_decContext sig_dec() throws RecognitionException {
		Sig_decContext _localctx = new Sig_decContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_sig_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(325);
			_la = _input.LA(1);
			if (_la==SIGNED) {
				{
				setState(324);
				match(SIGNED);
				}
			}

			setState(327);
			match(T__22);
			setState(329);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(328);
				struct_type();
				}
			}

			setState(331);
			type_dec();
			setState(336);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(332);
				match(T__6);
				setState(333);
				type_dec();
				}
				}
				setState(338);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Dff_decContext extends ParserRuleContext {
		public List<Dff_singleContext> dff_single() {
			return getRuleContexts(Dff_singleContext.class);
		}
		public Dff_singleContext dff_single(int i) {
			return getRuleContext(Dff_singleContext.class,i);
		}
		public TerminalNode SIGNED() { return getToken(LucidParser.SIGNED, 0); }
		public Struct_typeContext struct_type() {
			return getRuleContext(Struct_typeContext.class,0);
		}
		public Dff_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dff_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterDff_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitDff_dec(this);
		}
	}

	public final Dff_decContext dff_dec() throws RecognitionException {
		Dff_decContext _localctx = new Dff_decContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_dff_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(340);
			_la = _input.LA(1);
			if (_la==SIGNED) {
				{
				setState(339);
				match(SIGNED);
				}
			}

			setState(342);
			match(T__23);
			setState(344);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(343);
				struct_type();
				}
			}

			setState(346);
			dff_single();
			setState(351);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(347);
				match(T__6);
				setState(348);
				dff_single();
				}
				}
				setState(353);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fsm_decContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Fsm_statesContext fsm_states() {
			return getRuleContext(Fsm_statesContext.class,0);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Inst_consContext inst_cons() {
			return getRuleContext(Inst_consContext.class,0);
		}
		public Fsm_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fsm_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterFsm_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitFsm_dec(this);
		}
	}

	public final Fsm_decContext fsm_dec() throws RecognitionException {
		Fsm_decContext _localctx = new Fsm_decContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_fsm_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(354);
			match(T__24);
			setState(355);
			name();
			setState(359);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(356);
				array_size();
				}
				}
				setState(361);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(363);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(362);
				inst_cons();
				}
			}

			setState(365);
			match(T__13);
			setState(366);
			match(T__1);
			setState(367);
			fsm_states();
			setState(368);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fsm_statesContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public Fsm_statesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fsm_states; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterFsm_states(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitFsm_states(this);
		}
	}

	public final Fsm_statesContext fsm_states() throws RecognitionException {
		Fsm_statesContext _localctx = new Fsm_statesContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_fsm_states);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(370);
			name();
			setState(375);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(371);
				match(T__6);
				setState(372);
				name();
				}
				}
				setState(377);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Module_instContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Inst_consContext inst_cons() {
			return getRuleContext(Inst_consContext.class,0);
		}
		public Module_instContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module_inst; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterModule_inst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitModule_inst(this);
		}
	}

	public final Module_instContext module_inst() throws RecognitionException {
		Module_instContext _localctx = new Module_instContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_module_inst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(378);
			name();
			setState(379);
			name();
			setState(383);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(380);
				array_size();
				}
				}
				setState(385);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(387);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(386);
				inst_cons();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Inst_consContext extends ParserRuleContext {
		public Con_listContext con_list() {
			return getRuleContext(Con_listContext.class,0);
		}
		public Inst_consContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inst_cons; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterInst_cons(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitInst_cons(this);
		}
	}

	public final Inst_consContext inst_cons() throws RecognitionException {
		Inst_consContext _localctx = new Inst_consContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_inst_cons);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
			match(T__8);
			setState(390);
			con_list();
			setState(391);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Con_listContext extends ParserRuleContext {
		public List<ConnectionContext> connection() {
			return getRuleContexts(ConnectionContext.class);
		}
		public ConnectionContext connection(int i) {
			return getRuleContext(ConnectionContext.class,i);
		}
		public Con_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_con_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterCon_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitCon_list(this);
		}
	}

	public final Con_listContext con_list() throws RecognitionException {
		Con_listContext _localctx = new Con_listContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_con_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(393);
			connection();
			setState(398);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(394);
				match(T__6);
				setState(395);
				connection();
				}
				}
				setState(400);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConnectionContext extends ParserRuleContext {
		public Param_conContext param_con() {
			return getRuleContext(Param_conContext.class,0);
		}
		public Sig_conContext sig_con() {
			return getRuleContext(Sig_conContext.class,0);
		}
		public ConnectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterConnection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitConnection(this);
		}
	}

	public final ConnectionContext connection() throws RecognitionException {
		ConnectionContext _localctx = new ConnectionContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_connection);
		try {
			setState(403);
			switch (_input.LA(1)) {
			case T__20:
				enterOuterAlt(_localctx, 1);
				{
				setState(401);
				param_con();
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 2);
				{
				setState(402);
				sig_con();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Struct_memberContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Struct_typeContext struct_type() {
			return getRuleContext(Struct_typeContext.class,0);
		}
		public List<Array_sizeContext> array_size() {
			return getRuleContexts(Array_sizeContext.class);
		}
		public Array_sizeContext array_size(int i) {
			return getRuleContext(Array_sizeContext.class,i);
		}
		public Struct_memberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_struct_member; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStruct_member(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStruct_member(this);
		}
	}

	public final Struct_memberContext struct_member() throws RecognitionException {
		Struct_memberContext _localctx = new Struct_memberContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_struct_member);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			name();
			setState(407);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(406);
				struct_type();
				}
			}

			setState(412);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(409);
				array_size();
				}
				}
				setState(414);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Struct_decContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<Struct_memberContext> struct_member() {
			return getRuleContexts(Struct_memberContext.class);
		}
		public Struct_memberContext struct_member(int i) {
			return getRuleContext(Struct_memberContext.class,i);
		}
		public Struct_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_struct_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterStruct_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitStruct_dec(this);
		}
	}

	public final Struct_decContext struct_dec() throws RecognitionException {
		Struct_decContext _localctx = new Struct_decContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_struct_dec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			match(T__25);
			setState(416);
			name();
			setState(417);
			match(T__1);
			setState(418);
			struct_member();
			setState(423);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(419);
				match(T__6);
				setState(420);
				struct_member();
				}
				}
				setState(425);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(426);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Always_blockContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Always_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_always_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterAlways_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitAlways_block(this);
		}
	}

	public final Always_blockContext always_block() throws RecognitionException {
		Always_blockContext _localctx = new Always_blockContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_always_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			match(T__26);
			setState(429);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Always_statContext extends ParserRuleContext {
		public Always_statContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_always_stat; }
	 
		public Always_statContext() { }
		public void copyFrom(Always_statContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AlwaysStatContext extends Always_statContext {
		public Assign_statContext assign_stat() {
			return getRuleContext(Assign_statContext.class,0);
		}
		public AlwaysStatContext(Always_statContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterAlwaysStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitAlwaysStat(this);
		}
	}
	public static class AlwaysIfContext extends Always_statContext {
		public If_statContext if_stat() {
			return getRuleContext(If_statContext.class,0);
		}
		public AlwaysIfContext(Always_statContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterAlwaysIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitAlwaysIf(this);
		}
	}
	public static class AlwaysForContext extends Always_statContext {
		public For_statContext for_stat() {
			return getRuleContext(For_statContext.class,0);
		}
		public AlwaysForContext(Always_statContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterAlwaysFor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitAlwaysFor(this);
		}
	}
	public static class AlwaysCaseContext extends Always_statContext {
		public Case_statContext case_stat() {
			return getRuleContext(Case_statContext.class,0);
		}
		public AlwaysCaseContext(Always_statContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterAlwaysCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitAlwaysCase(this);
		}
	}

	public final Always_statContext always_stat() throws RecognitionException {
		Always_statContext _localctx = new Always_statContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_always_stat);
		try {
			setState(437);
			switch (_input.LA(1)) {
			case TYPE_ID:
			case CONST_ID:
			case SPACE_ID:
				_localctx = new AlwaysStatContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(431);
				assign_stat();
				setState(432);
				match(T__3);
				}
				break;
			case T__29:
				_localctx = new AlwaysCaseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(434);
				case_stat();
				}
				break;
			case T__31:
				_localctx = new AlwaysIfContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(435);
				if_stat();
				}
				break;
			case T__33:
				_localctx = new AlwaysForContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(436);
				for_stat();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public List<Always_statContext> always_stat() {
			return getRuleContexts(Always_statContext.class);
		}
		public Always_statContext always_stat(int i) {
			return getRuleContext(Always_statContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_block);
		int _la;
		try {
			setState(448);
			switch (_input.LA(1)) {
			case T__1:
				enterOuterAlt(_localctx, 1);
				{
				setState(439);
				match(T__1);
				setState(443);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 30)) & ~0x3f) == 0 && ((1L << (_la - 30)) & ((1L << (T__29 - 30)) | (1L << (T__31 - 30)) | (1L << (T__33 - 30)) | (1L << (TYPE_ID - 30)) | (1L << (CONST_ID - 30)) | (1L << (SPACE_ID - 30)))) != 0)) {
					{
					{
					setState(440);
					always_stat();
					}
					}
					setState(445);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(446);
				match(T__2);
				}
				break;
			case T__29:
			case T__31:
			case T__33:
			case TYPE_ID:
			case CONST_ID:
			case SPACE_ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(447);
				always_stat();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_statContext extends ParserRuleContext {
		public SignalContext signal() {
			return getRuleContext(SignalContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Assign_statContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterAssign_stat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitAssign_stat(this);
		}
	}

	public final Assign_statContext assign_stat() throws RecognitionException {
		Assign_statContext _localctx = new Assign_statContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_assign_stat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(450);
			signal();
			setState(451);
			match(T__13);
			setState(452);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Array_indexContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Array_indexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_index; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterArray_index(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitArray_index(this);
		}
	}

	public final Array_indexContext array_index() throws RecognitionException {
		Array_indexContext _localctx = new Array_indexContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_array_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
			match(T__14);
			setState(455);
			expr(0);
			setState(456);
			match(T__15);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Bit_selectorContext extends ParserRuleContext {
		public Bit_selectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bit_selector; }
	 
		public Bit_selectorContext() { }
		public void copyFrom(Bit_selectorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BitSelectorConstContext extends Bit_selectorContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public BitSelectorConstContext(Bit_selectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterBitSelectorConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitBitSelectorConst(this);
		}
	}
	public static class BitSelectorFixWidthContext extends Bit_selectorContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public BitSelectorFixWidthContext(Bit_selectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterBitSelectorFixWidth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitBitSelectorFixWidth(this);
		}
	}

	public final Bit_selectorContext bit_selector() throws RecognitionException {
		Bit_selectorContext _localctx = new Bit_selectorContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_bit_selector);
		int _la;
		try {
			setState(471);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				_localctx = new BitSelectorConstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(458);
				match(T__14);
				setState(459);
				expr(0);
				setState(460);
				match(T__9);
				setState(461);
				expr(0);
				setState(462);
				match(T__15);
				}
				break;
			case 2:
				_localctx = new BitSelectorFixWidthContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(464);
				match(T__14);
				setState(465);
				expr(0);
				setState(466);
				_la = _input.LA(1);
				if ( !(_la==T__27 || _la==T__28) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(467);
				match(T__9);
				setState(468);
				expr(0);
				setState(469);
				match(T__15);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Bit_selectionContext extends ParserRuleContext {
		public List<Array_indexContext> array_index() {
			return getRuleContexts(Array_indexContext.class);
		}
		public Array_indexContext array_index(int i) {
			return getRuleContext(Array_indexContext.class,i);
		}
		public Bit_selectorContext bit_selector() {
			return getRuleContext(Bit_selectorContext.class,0);
		}
		public Bit_selectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bit_selection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterBit_selection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitBit_selection(this);
		}
	}

	public final Bit_selectionContext bit_selection() throws RecognitionException {
		Bit_selectionContext _localctx = new Bit_selectionContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_bit_selection);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(476);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(473);
					array_index();
					}
					} 
				}
				setState(478);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			}
			setState(481);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				setState(479);
				array_index();
				}
				break;
			case 2:
				{
				setState(480);
				bit_selector();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SignalContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<Bit_selectionContext> bit_selection() {
			return getRuleContexts(Bit_selectionContext.class);
		}
		public Bit_selectionContext bit_selection(int i) {
			return getRuleContext(Bit_selectionContext.class,i);
		}
		public SignalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterSignal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitSignal(this);
		}
	}

	public final SignalContext signal() throws RecognitionException {
		SignalContext _localctx = new SignalContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_signal);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(483);
			name();
			setState(485);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
			case 1:
				{
				setState(484);
				bit_selection();
				}
				break;
			}
			setState(494);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(487);
					match(T__17);
					setState(488);
					name();
					setState(490);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
					case 1:
						{
						setState(489);
						bit_selection();
						}
						break;
					}
					}
					} 
				}
				setState(496);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Case_statContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<Case_elemContext> case_elem() {
			return getRuleContexts(Case_elemContext.class);
		}
		public Case_elemContext case_elem(int i) {
			return getRuleContext(Case_elemContext.class,i);
		}
		public Case_statContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_case_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterCase_stat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitCase_stat(this);
		}
	}

	public final Case_statContext case_stat() throws RecognitionException {
		Case_statContext _localctx = new Case_statContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_case_stat);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(497);
			match(T__29);
			setState(498);
			match(T__8);
			setState(499);
			expr(0);
			setState(500);
			match(T__7);
			setState(501);
			match(T__1);
			setState(503); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(502);
				case_elem();
				}
				}
				setState(505); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__8) | (1L << T__28) | (1L << T__30) | (1L << T__34) | (1L << T__36) | (1L << T__37) | (1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47) | (1L << T__48) | (1L << T__49) | (1L << HEX) | (1L << BIN) | (1L << DEC))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (INT - 64)) | (1L << (STRING - 64)) | (1L << (TYPE_ID - 64)) | (1L << (CONST_ID - 64)) | (1L << (SPACE_ID - 64)) | (1L << (FUNCTION_ID - 64)))) != 0) );
			setState(507);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Case_elemContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<Always_statContext> always_stat() {
			return getRuleContexts(Always_statContext.class);
		}
		public Always_statContext always_stat(int i) {
			return getRuleContext(Always_statContext.class,i);
		}
		public Case_elemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_case_elem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterCase_elem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitCase_elem(this);
		}
	}

	public final Case_elemContext case_elem() throws RecognitionException {
		Case_elemContext _localctx = new Case_elemContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_case_elem);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(511);
			switch (_input.LA(1)) {
			case T__1:
			case T__8:
			case T__28:
			case T__34:
			case T__36:
			case T__37:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case HEX:
			case BIN:
			case DEC:
			case INT:
			case STRING:
			case TYPE_ID:
			case CONST_ID:
			case SPACE_ID:
			case FUNCTION_ID:
				{
				setState(509);
				expr(0);
				}
				break;
			case T__30:
				{
				setState(510);
				match(T__30);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(513);
			match(T__9);
			setState(515); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(514);
					always_stat();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(517); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_statContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Else_statContext else_stat() {
			return getRuleContext(Else_statContext.class,0);
		}
		public If_statContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterIf_stat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitIf_stat(this);
		}
	}

	public final If_statContext if_stat() throws RecognitionException {
		If_statContext _localctx = new If_statContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_if_stat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			match(T__31);
			setState(520);
			match(T__8);
			setState(521);
			expr(0);
			setState(522);
			match(T__7);
			setState(523);
			block();
			setState(525);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(524);
				else_stat();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Else_statContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Else_statContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterElse_stat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitElse_stat(this);
		}
	}

	public final Else_statContext else_stat() throws RecognitionException {
		Else_statContext _localctx = new Else_statContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_else_stat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(527);
			match(T__32);
			setState(528);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class For_statContext extends ParserRuleContext {
		public Assign_statContext assign_stat() {
			return getRuleContext(Assign_statContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Var_assignContext var_assign() {
			return getRuleContext(Var_assignContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public For_statContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterFor_stat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitFor_stat(this);
		}
	}

	public final For_statContext for_stat() throws RecognitionException {
		For_statContext _localctx = new For_statContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_for_stat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(530);
			match(T__33);
			setState(531);
			match(T__8);
			setState(532);
			assign_stat();
			setState(533);
			match(T__3);
			setState(534);
			expr(0);
			setState(535);
			match(T__3);
			setState(536);
			var_assign();
			setState(537);
			match(T__7);
			setState(538);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode FUNCTION_ID() { return getToken(LucidParser.FUNCTION_ID, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitFunction(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
			match(FUNCTION_ID);
			setState(541);
			match(T__8);
			setState(542);
			expr(0);
			setState(547);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(543);
				match(T__6);
				setState(544);
				expr(0);
				}
				}
				setState(549);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(550);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode HEX() { return getToken(LucidParser.HEX, 0); }
		public TerminalNode BIN() { return getToken(LucidParser.BIN, 0); }
		public TerminalNode DEC() { return getToken(LucidParser.DEC, 0); }
		public TerminalNode INT() { return getToken(LucidParser.INT, 0); }
		public TerminalNode STRING() { return getToken(LucidParser.STRING, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(552);
			_la = _input.LA(1);
			if ( !(((((_la - 60)) & ~0x3f) == 0 && ((1L << (_la - 60)) & ((1L << (HEX - 60)) | (1L << (BIN - 60)) | (1L << (DEC - 60)) | (1L << (INT - 60)) | (1L << (STRING - 60)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExprTernaryContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprTernaryContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprTernary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprTernary(this);
		}
	}
	public static class ExprNumContext extends ExprContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public ExprNumContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprNum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprNum(this);
		}
	}
	public static class ExprConcatContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprConcatContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprConcat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprConcat(this);
		}
	}
	public static class ExprCompressContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprCompressContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprCompress(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprCompress(this);
		}
	}
	public static class ExprInvertContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprInvertContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprInvert(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprInvert(this);
		}
	}
	public static class ExprAndOrContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprAndOrContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprAndOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprAndOr(this);
		}
	}
	public static class ExprArrayContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprArrayContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprArray(this);
		}
	}
	public static class ExprShiftContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprShiftContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprShift(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprShift(this);
		}
	}
	public static class ExprAddSubContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprAddSubContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprAddSub(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprAddSub(this);
		}
	}
	public static class ExprLogicalContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprLogicalContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprLogical(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprLogical(this);
		}
	}
	public static class ExprNegateContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprNegateContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprNegate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprNegate(this);
		}
	}
	public static class ExprGroupContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprGroupContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprGroup(this);
		}
	}
	public static class ExprFunctionContext extends ExprContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ExprFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprFunction(this);
		}
	}
	public static class ExprCompareContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprCompareContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprCompare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprCompare(this);
		}
	}
	public static class ExprDupContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprDupContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprDup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprDup(this);
		}
	}
	public static class ExprMultDivContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprMultDivContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprMultDiv(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprMultDiv(this);
		}
	}
	public static class ExprSignalContext extends ExprContext {
		public SignalContext signal() {
			return getRuleContext(SignalContext.class,0);
		}
		public ExprSignalContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterExprSignal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitExprSignal(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 98;
		enterRecursionRule(_localctx, 98, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(590);
			switch (_input.LA(1)) {
			case TYPE_ID:
			case CONST_ID:
			case SPACE_ID:
				{
				_localctx = new ExprSignalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(555);
				signal();
				}
				break;
			case HEX:
			case BIN:
			case DEC:
			case INT:
			case STRING:
				{
				_localctx = new ExprNumContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(556);
				number();
				}
				break;
			case FUNCTION_ID:
				{
				_localctx = new ExprFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(557);
				function();
				}
				break;
			case T__8:
				{
				_localctx = new ExprGroupContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(558);
				match(T__8);
				setState(559);
				expr(0);
				setState(560);
				match(T__7);
				}
				break;
			case T__34:
				{
				_localctx = new ExprConcatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(562);
				match(T__34);
				setState(563);
				expr(0);
				setState(568);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__6) {
					{
					{
					setState(564);
					match(T__6);
					setState(565);
					expr(0);
					}
					}
					setState(570);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(571);
				match(T__2);
				}
				break;
			case T__1:
				{
				_localctx = new ExprArrayContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(573);
				match(T__1);
				setState(574);
				expr(0);
				setState(579);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__6) {
					{
					{
					setState(575);
					match(T__6);
					setState(576);
					expr(0);
					}
					}
					setState(581);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(582);
				match(T__2);
				}
				break;
			case T__36:
			case T__37:
				{
				_localctx = new ExprInvertContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(584);
				_la = _input.LA(1);
				if ( !(_la==T__36 || _la==T__37) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(585);
				expr(10);
				}
				break;
			case T__28:
				{
				_localctx = new ExprNegateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(586);
				match(T__28);
				setState(587);
				expr(9);
				}
				break;
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
				{
				_localctx = new ExprCompressContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(588);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47) | (1L << T__48) | (1L << T__49))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(589);
				expr(4);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(623);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(621);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
					case 1:
						{
						_localctx = new ExprMultDivContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(592);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(593);
						_la = _input.LA(1);
						if ( !(_la==T__38 || _la==T__39) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(594);
						expr(9);
						}
						break;
					case 2:
						{
						_localctx = new ExprAddSubContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(595);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(596);
						_la = _input.LA(1);
						if ( !(_la==T__27 || _la==T__28) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(597);
						expr(8);
						}
						break;
					case 3:
						{
						_localctx = new ExprShiftContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(598);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(599);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__40) | (1L << T__41) | (1L << T__42) | (1L << T__43))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(600);
						expr(7);
						}
						break;
					case 4:
						{
						_localctx = new ExprAndOrContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(601);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(602);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(603);
						expr(6);
						}
						break;
					case 5:
						{
						_localctx = new ExprCompareContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(604);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(605);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__18) | (1L << T__50) | (1L << T__51) | (1L << T__52) | (1L << T__53))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(606);
						expr(4);
						}
						break;
					case 6:
						{
						_localctx = new ExprLogicalContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(607);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(608);
						_la = _input.LA(1);
						if ( !(_la==T__54 || _la==T__55) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(609);
						expr(3);
						}
						break;
					case 7:
						{
						_localctx = new ExprTernaryContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(610);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(611);
						match(T__56);
						setState(612);
						expr(0);
						setState(613);
						match(T__9);
						setState(614);
						expr(2);
						}
						break;
					case 8:
						{
						_localctx = new ExprDupContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(616);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(617);
						match(T__35);
						setState(618);
						expr(0);
						setState(619);
						match(T__2);
						}
						break;
					}
					} 
				}
				setState(625);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Var_assignContext extends ParserRuleContext {
		public SignalContext signal() {
			return getRuleContext(SignalContext.class,0);
		}
		public Assign_statContext assign_stat() {
			return getRuleContext(Assign_statContext.class,0);
		}
		public Var_assignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_assign; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterVar_assign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitVar_assign(this);
		}
	}

	public final Var_assignContext var_assign() throws RecognitionException {
		Var_assignContext _localctx = new Var_assignContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_var_assign);
		int _la;
		try {
			setState(630);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(626);
				signal();
				setState(627);
				_la = _input.LA(1);
				if ( !(_la==T__57 || _la==T__58) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(629);
				assign_stat();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public TerminalNode TYPE_ID() { return getToken(LucidParser.TYPE_ID, 0); }
		public TerminalNode CONST_ID() { return getToken(LucidParser.CONST_ID, 0); }
		public TerminalNode SPACE_ID() { return getToken(LucidParser.SPACE_ID, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidListener ) ((LucidListener)listener).exitName(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			_la = _input.LA(1);
			if ( !(((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (TYPE_ID - 67)) | (1L << (CONST_ID - 67)) | (1L << (SPACE_ID - 67)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 49:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 3);
		case 5:
			return precpred(_ctx, 2);
		case 6:
			return precpred(_ctx, 1);
		case 7:
			return precpred(_ctx, 12);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3K\u027d\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\3\2\3\2\7\2m\n\2\f\2\16\2p\13\2\3\3\3\3\3\3\3\3\7\3v\n\3"+
		"\f\3\16\3y\13\3\3\3\3\3\3\4\3\4\3\4\3\4\5\4\u0081\n\4\3\5\3\5\3\5\5\5"+
		"\u0086\n\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\7\6\u008f\n\6\f\6\16\6\u0092\13"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\7\7\7\u009a\n\7\f\7\16\7\u009d\13\7\3\7\3\7\3"+
		"\b\3\b\3\b\5\b\u00a4\n\b\3\t\3\t\3\t\5\t\u00a9\n\t\3\n\5\n\u00ac\n\n\3"+
		"\n\3\n\5\n\u00b0\n\n\3\n\3\n\7\n\u00b4\n\n\f\n\16\n\u00b7\13\n\3\13\5"+
		"\13\u00ba\n\13\3\13\3\13\5\13\u00be\n\13\3\13\3\13\7\13\u00c2\n\13\f\13"+
		"\16\13\u00c5\13\13\3\f\5\f\u00c8\n\f\3\f\3\f\5\f\u00cc\n\f\3\f\3\f\7\f"+
		"\u00d0\n\f\f\f\16\f\u00d3\13\f\3\r\3\r\3\r\5\r\u00d8\n\r\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\20\3\20\3\20\3\20\5\20\u00e4\n\20\3\20\3\20\3\21\3\21"+
		"\7\21\u00ea\n\21\f\21\16\21\u00ed\13\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\5\22\u0106\n\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24"+
		"\3\24\3\24\5\24\u0112\n\24\3\24\3\24\3\24\7\24\u0117\n\24\f\24\16\24\u011a"+
		"\13\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26"+
		"\5\26\u0129\n\26\3\26\3\26\3\27\3\27\7\27\u012f\n\27\f\27\16\27\u0132"+
		"\13\27\3\30\3\30\7\30\u0136\n\30\f\30\16\30\u0139\13\30\3\30\5\30\u013c"+
		"\n\30\3\31\3\31\3\31\3\31\7\31\u0142\n\31\f\31\16\31\u0145\13\31\3\32"+
		"\5\32\u0148\n\32\3\32\3\32\5\32\u014c\n\32\3\32\3\32\3\32\7\32\u0151\n"+
		"\32\f\32\16\32\u0154\13\32\3\33\5\33\u0157\n\33\3\33\3\33\5\33\u015b\n"+
		"\33\3\33\3\33\3\33\7\33\u0160\n\33\f\33\16\33\u0163\13\33\3\34\3\34\3"+
		"\34\7\34\u0168\n\34\f\34\16\34\u016b\13\34\3\34\5\34\u016e\n\34\3\34\3"+
		"\34\3\34\3\34\3\34\3\35\3\35\3\35\7\35\u0178\n\35\f\35\16\35\u017b\13"+
		"\35\3\36\3\36\3\36\7\36\u0180\n\36\f\36\16\36\u0183\13\36\3\36\5\36\u0186"+
		"\n\36\3\37\3\37\3\37\3\37\3 \3 \3 \7 \u018f\n \f \16 \u0192\13 \3!\3!"+
		"\5!\u0196\n!\3\"\3\"\5\"\u019a\n\"\3\"\7\"\u019d\n\"\f\"\16\"\u01a0\13"+
		"\"\3#\3#\3#\3#\3#\3#\7#\u01a8\n#\f#\16#\u01ab\13#\3#\3#\3$\3$\3$\3%\3"+
		"%\3%\3%\3%\3%\5%\u01b8\n%\3&\3&\7&\u01bc\n&\f&\16&\u01bf\13&\3&\3&\5&"+
		"\u01c3\n&\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3)\3)\3)\3"+
		")\3)\3)\5)\u01da\n)\3*\7*\u01dd\n*\f*\16*\u01e0\13*\3*\3*\5*\u01e4\n*"+
		"\3+\3+\5+\u01e8\n+\3+\3+\3+\5+\u01ed\n+\7+\u01ef\n+\f+\16+\u01f2\13+\3"+
		",\3,\3,\3,\3,\3,\6,\u01fa\n,\r,\16,\u01fb\3,\3,\3-\3-\5-\u0202\n-\3-\3"+
		"-\6-\u0206\n-\r-\16-\u0207\3.\3.\3.\3.\3.\3.\5.\u0210\n.\3/\3/\3/\3\60"+
		"\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61"+
		"\7\61\u0224\n\61\f\61\16\61\u0227\13\61\3\61\3\61\3\62\3\62\3\63\3\63"+
		"\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\7\63\u0239\n\63\f\63"+
		"\16\63\u023c\13\63\3\63\3\63\3\63\3\63\3\63\3\63\7\63\u0244\n\63\f\63"+
		"\16\63\u0247\13\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u0251"+
		"\n\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63"+
		"\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63"+
		"\3\63\3\63\7\63\u0270\n\63\f\63\16\63\u0273\13\63\3\64\3\64\3\64\3\64"+
		"\5\64\u0279\n\64\3\65\3\65\3\65\2\3d\66\2\4\6\b\n\f\16\20\22\24\26\30"+
		"\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfh\2\r\3\2\36\37"+
		"\4\2>@BC\3\2\'(\3\2/\64\3\2)*\3\2+.\3\2/\62\5\2\23\23\25\25\658\3\29:"+
		"\3\2<=\3\2EG\u02a2\2n\3\2\2\2\4q\3\2\2\2\6\u0080\3\2\2\2\b\u0082\3\2\2"+
		"\2\n\u008a\3\2\2\2\f\u0095\3\2\2\2\16\u00a0\3\2\2\2\20\u00a8\3\2\2\2\22"+
		"\u00ab\3\2\2\2\24\u00b9\3\2\2\2\26\u00c7\3\2\2\2\30\u00d4\3\2\2\2\32\u00d9"+
		"\3\2\2\2\34\u00db\3\2\2\2\36\u00df\3\2\2\2 \u00e7\3\2\2\2\"\u0105\3\2"+
		"\2\2$\u0107\3\2\2\2&\u010c\3\2\2\2(\u011d\3\2\2\2*\u0123\3\2\2\2,\u012c"+
		"\3\2\2\2.\u0133\3\2\2\2\60\u013d\3\2\2\2\62\u0147\3\2\2\2\64\u0156\3\2"+
		"\2\2\66\u0164\3\2\2\28\u0174\3\2\2\2:\u017c\3\2\2\2<\u0187\3\2\2\2>\u018b"+
		"\3\2\2\2@\u0195\3\2\2\2B\u0197\3\2\2\2D\u01a1\3\2\2\2F\u01ae\3\2\2\2H"+
		"\u01b7\3\2\2\2J\u01c2\3\2\2\2L\u01c4\3\2\2\2N\u01c8\3\2\2\2P\u01d9\3\2"+
		"\2\2R\u01de\3\2\2\2T\u01e5\3\2\2\2V\u01f3\3\2\2\2X\u0201\3\2\2\2Z\u0209"+
		"\3\2\2\2\\\u0211\3\2\2\2^\u0214\3\2\2\2`\u021e\3\2\2\2b\u022a\3\2\2\2"+
		"d\u0250\3\2\2\2f\u0278\3\2\2\2h\u027a\3\2\2\2jm\5\4\3\2km\5\b\5\2lj\3"+
		"\2\2\2lk\3\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2\2\2o\3\3\2\2\2pn\3\2\2\2qr"+
		"\7\3\2\2rs\5h\65\2sw\7\4\2\2tv\5\6\4\2ut\3\2\2\2vy\3\2\2\2wu\3\2\2\2w"+
		"x\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\7\5\2\2{\5\3\2\2\2|\u0081\5D#\2}~\5$\23"+
		"\2~\177\7\6\2\2\177\u0081\3\2\2\2\u0080|\3\2\2\2\u0080}\3\2\2\2\u0081"+
		"\7\3\2\2\2\u0082\u0083\7\7\2\2\u0083\u0085\5h\65\2\u0084\u0086\5\n\6\2"+
		"\u0085\u0084\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088"+
		"\5\f\7\2\u0088\u0089\5 \21\2\u0089\t\3\2\2\2\u008a\u008b\7\b\2\2\u008b"+
		"\u0090\5\16\b\2\u008c\u008d\7\t\2\2\u008d\u008f\5\16\b\2\u008e\u008c\3"+
		"\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091"+
		"\u0093\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0094\7\n\2\2\u0094\13\3\2\2"+
		"\2\u0095\u0096\7\13\2\2\u0096\u009b\5\20\t\2\u0097\u0098\7\t\2\2\u0098"+
		"\u009a\5\20\t\2\u0099\u0097\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3"+
		"\2\2\2\u009b\u009c\3\2\2\2\u009c\u009e\3\2\2\2\u009d\u009b\3\2\2\2\u009e"+
		"\u009f\7\n\2\2\u009f\r\3\2\2\2\u00a0\u00a3\5\30\r\2\u00a1\u00a2\7\f\2"+
		"\2\u00a2\u00a4\5\32\16\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4"+
		"\17\3\2\2\2\u00a5\u00a9\5\22\n\2\u00a6\u00a9\5\24\13\2\u00a7\u00a9\5\26"+
		"\f\2\u00a8\u00a5\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a7\3\2\2\2\u00a9"+
		"\21\3\2\2\2\u00aa\u00ac\7D\2\2\u00ab\u00aa\3\2\2\2\u00ab\u00ac\3\2\2\2"+
		"\u00ac\u00ad\3\2\2\2\u00ad\u00af\7\r\2\2\u00ae\u00b0\5\36\20\2\u00af\u00ae"+
		"\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b5\5h\65\2\u00b2"+
		"\u00b4\5\34\17\2\u00b3\u00b2\3\2\2\2\u00b4\u00b7\3\2\2\2\u00b5\u00b3\3"+
		"\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\23\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b8"+
		"\u00ba\7D\2\2\u00b9\u00b8\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bb\3\2"+
		"\2\2\u00bb\u00bd\7\16\2\2\u00bc\u00be\5\36\20\2\u00bd\u00bc\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c3\5h\65\2\u00c0\u00c2\5\34"+
		"\17\2\u00c1\u00c0\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3"+
		"\u00c4\3\2\2\2\u00c4\25\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c8\7D\2\2"+
		"\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00cb"+
		"\7\17\2\2\u00ca\u00cc\5\36\20\2\u00cb\u00ca\3\2\2\2\u00cb\u00cc\3\2\2"+
		"\2\u00cc\u00cd\3\2\2\2\u00cd\u00d1\5h\65\2\u00ce\u00d0\5\34\17\2\u00cf"+
		"\u00ce\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2"+
		"\2\2\u00d2\27\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00d7\5h\65\2\u00d5\u00d6"+
		"\7\20\2\2\u00d6\u00d8\5d\63\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2"+
		"\u00d8\31\3\2\2\2\u00d9\u00da\5d\63\2\u00da\33\3\2\2\2\u00db\u00dc\7\21"+
		"\2\2\u00dc\u00dd\5d\63\2\u00dd\u00de\7\22\2\2\u00de\35\3\2\2\2\u00df\u00e0"+
		"\7\23\2\2\u00e0\u00e3\5h\65\2\u00e1\u00e2\7\24\2\2\u00e2\u00e4\5h\65\2"+
		"\u00e3\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e6"+
		"\7\25\2\2\u00e6\37\3\2\2\2\u00e7\u00eb\7\4\2\2\u00e8\u00ea\5\"\22\2\u00e9"+
		"\u00e8\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2"+
		"\2\2\u00ec\u00ee\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00ef\7\5\2\2\u00ef"+
		"!\3\2\2\2\u00f0\u00f1\5$\23\2\u00f1\u00f2\7\6\2\2\u00f2\u0106\3\2\2\2"+
		"\u00f3\u00f4\5\60\31\2\u00f4\u00f5\7\6\2\2\u00f5\u0106\3\2\2\2\u00f6\u00f7"+
		"\5\62\32\2\u00f7\u00f8\7\6\2\2\u00f8\u0106\3\2\2\2\u00f9\u00fa\5\66\34"+
		"\2\u00fa\u00fb\7\6\2\2\u00fb\u0106\3\2\2\2\u00fc\u00fd\5\64\33\2\u00fd"+
		"\u00fe\7\6\2\2\u00fe\u0106\3\2\2\2\u00ff\u0100\5:\36\2\u0100\u0101\7\6"+
		"\2\2\u0101\u0106\3\2\2\2\u0102\u0106\5&\24\2\u0103\u0106\5F$\2\u0104\u0106"+
		"\5D#\2\u0105\u00f0\3\2\2\2\u0105\u00f3\3\2\2\2\u0105\u00f6\3\2\2\2\u0105"+
		"\u00f9\3\2\2\2\u0105\u00fc\3\2\2\2\u0105\u00ff\3\2\2\2\u0105\u0102\3\2"+
		"\2\2\u0105\u0103\3\2\2\2\u0105\u0104\3\2\2\2\u0106#\3\2\2\2\u0107\u0108"+
		"\7\26\2\2\u0108\u0109\5h\65\2\u0109\u010a\7\20\2\2\u010a\u010b\5d\63\2"+
		"\u010b%\3\2\2\2\u010c\u010d\5> \2\u010d\u0118\7\4\2\2\u010e\u0112\5\64"+
		"\33\2\u010f\u0112\5\66\34\2\u0110\u0112\5:\36\2\u0111\u010e\3\2\2\2\u0111"+
		"\u010f\3\2\2\2\u0111\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0114\7\6"+
		"\2\2\u0114\u0117\3\2\2\2\u0115\u0117\5&\24\2\u0116\u0111\3\2\2\2\u0116"+
		"\u0115\3\2\2\2\u0117\u011a\3\2\2\2\u0118\u0116\3\2\2\2\u0118\u0119\3\2"+
		"\2\2\u0119\u011b\3\2\2\2\u011a\u0118\3\2\2\2\u011b\u011c\7\5\2\2\u011c"+
		"\'\3\2\2\2\u011d\u011e\7\24\2\2\u011e\u011f\5h\65\2\u011f\u0120\7\13\2"+
		"\2\u0120\u0121\5d\63\2\u0121\u0122\7\n\2\2\u0122)\3\2\2\2\u0123\u0124"+
		"\7\27\2\2\u0124\u0125\5h\65\2\u0125\u0128\7\13\2\2\u0126\u0129\5d\63\2"+
		"\u0127\u0129\7A\2\2\u0128\u0126\3\2\2\2\u0128\u0127\3\2\2\2\u0129\u012a"+
		"\3\2\2\2\u012a\u012b\7\n\2\2\u012b+\3\2\2\2\u012c\u0130\5h\65\2\u012d"+
		"\u012f\5\34\17\2\u012e\u012d\3\2\2\2\u012f\u0132\3\2\2\2\u0130\u012e\3"+
		"\2\2\2\u0130\u0131\3\2\2\2\u0131-\3\2\2\2\u0132\u0130\3\2\2\2\u0133\u0137"+
		"\5h\65\2\u0134\u0136\5\34\17\2\u0135\u0134\3\2\2\2\u0136\u0139\3\2\2\2"+
		"\u0137\u0135\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137"+
		"\3\2\2\2\u013a\u013c\5<\37\2\u013b\u013a\3\2\2\2\u013b\u013c\3\2\2\2\u013c"+
		"/\3\2\2\2\u013d\u013e\7\30\2\2\u013e\u0143\5,\27\2\u013f\u0140\7\t\2\2"+
		"\u0140\u0142\5,\27\2\u0141\u013f\3\2\2\2\u0142\u0145\3\2\2\2\u0143\u0141"+
		"\3\2\2\2\u0143\u0144\3\2\2\2\u0144\61\3\2\2\2\u0145\u0143\3\2\2\2\u0146"+
		"\u0148\7D\2\2\u0147\u0146\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u0149\3\2"+
		"\2\2\u0149\u014b\7\31\2\2\u014a\u014c\5\36\20\2\u014b\u014a\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u0152\5,\27\2\u014e\u014f\7\t"+
		"\2\2\u014f\u0151\5,\27\2\u0150\u014e\3\2\2\2\u0151\u0154\3\2\2\2\u0152"+
		"\u0150\3\2\2\2\u0152\u0153\3\2\2\2\u0153\63\3\2\2\2\u0154\u0152\3\2\2"+
		"\2\u0155\u0157\7D\2\2\u0156\u0155\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0158"+
		"\3\2\2\2\u0158\u015a\7\32\2\2\u0159\u015b\5\36\20\2\u015a\u0159\3\2\2"+
		"\2\u015a\u015b\3\2\2\2\u015b\u015c\3\2\2\2\u015c\u0161\5.\30\2\u015d\u015e"+
		"\7\t\2\2\u015e\u0160\5.\30\2\u015f\u015d\3\2\2\2\u0160\u0163\3\2\2\2\u0161"+
		"\u015f\3\2\2\2\u0161\u0162\3\2\2\2\u0162\65\3\2\2\2\u0163\u0161\3\2\2"+
		"\2\u0164\u0165\7\33\2\2\u0165\u0169\5h\65\2\u0166\u0168\5\34\17\2\u0167"+
		"\u0166\3\2\2\2\u0168\u016b\3\2\2\2\u0169\u0167\3\2\2\2\u0169\u016a\3\2"+
		"\2\2\u016a\u016d\3\2\2\2\u016b\u0169\3\2\2\2\u016c\u016e\5<\37\2\u016d"+
		"\u016c\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0170\7\20"+
		"\2\2\u0170\u0171\7\4\2\2\u0171\u0172\58\35\2\u0172\u0173\7\5\2\2\u0173"+
		"\67\3\2\2\2\u0174\u0179\5h\65\2\u0175\u0176\7\t\2\2\u0176\u0178\5h\65"+
		"\2\u0177\u0175\3\2\2\2\u0178\u017b\3\2\2\2\u0179\u0177\3\2\2\2\u0179\u017a"+
		"\3\2\2\2\u017a9\3\2\2\2\u017b\u0179\3\2\2\2\u017c\u017d\5h\65\2\u017d"+
		"\u0181\5h\65\2\u017e\u0180\5\34\17\2\u017f\u017e\3\2\2\2\u0180\u0183\3"+
		"\2\2\2\u0181\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0185\3\2\2\2\u0183"+
		"\u0181\3\2\2\2\u0184\u0186\5<\37\2\u0185\u0184\3\2\2\2\u0185\u0186\3\2"+
		"\2\2\u0186;\3\2\2\2\u0187\u0188\7\13\2\2\u0188\u0189\5> \2\u0189\u018a"+
		"\7\n\2\2\u018a=\3\2\2\2\u018b\u0190\5@!\2\u018c\u018d\7\t\2\2\u018d\u018f"+
		"\5@!\2\u018e\u018c\3\2\2\2\u018f\u0192\3\2\2\2\u0190\u018e\3\2\2\2\u0190"+
		"\u0191\3\2\2\2\u0191?\3\2\2\2\u0192\u0190\3\2\2\2\u0193\u0196\5*\26\2"+
		"\u0194\u0196\5(\25\2\u0195\u0193\3\2\2\2\u0195\u0194\3\2\2\2\u0196A\3"+
		"\2\2\2\u0197\u0199\5h\65\2\u0198\u019a\5\36\20\2\u0199\u0198\3\2\2\2\u0199"+
		"\u019a\3\2\2\2\u019a\u019e\3\2\2\2\u019b\u019d\5\34\17\2\u019c\u019b\3"+
		"\2\2\2\u019d\u01a0\3\2\2\2\u019e\u019c\3\2\2\2\u019e\u019f\3\2\2\2\u019f"+
		"C\3\2\2\2\u01a0\u019e\3\2\2\2\u01a1\u01a2\7\34\2\2\u01a2\u01a3\5h\65\2"+
		"\u01a3\u01a4\7\4\2\2\u01a4\u01a9\5B\"\2\u01a5\u01a6\7\t\2\2\u01a6\u01a8"+
		"\5B\"\2\u01a7\u01a5\3\2\2\2\u01a8\u01ab\3\2\2\2\u01a9\u01a7\3\2\2\2\u01a9"+
		"\u01aa\3\2\2\2\u01aa\u01ac\3\2\2\2\u01ab\u01a9\3\2\2\2\u01ac\u01ad\7\5"+
		"\2\2\u01adE\3\2\2\2\u01ae\u01af\7\35\2\2\u01af\u01b0\5J&\2\u01b0G\3\2"+
		"\2\2\u01b1\u01b2\5L\'\2\u01b2\u01b3\7\6\2\2\u01b3\u01b8\3\2\2\2\u01b4"+
		"\u01b8\5V,\2\u01b5\u01b8\5Z.\2\u01b6\u01b8\5^\60\2\u01b7\u01b1\3\2\2\2"+
		"\u01b7\u01b4\3\2\2\2\u01b7\u01b5\3\2\2\2\u01b7\u01b6\3\2\2\2\u01b8I\3"+
		"\2\2\2\u01b9\u01bd\7\4\2\2\u01ba\u01bc\5H%\2\u01bb\u01ba\3\2\2\2\u01bc"+
		"\u01bf\3\2\2\2\u01bd\u01bb\3\2\2\2\u01bd\u01be\3\2\2\2\u01be\u01c0\3\2"+
		"\2\2\u01bf\u01bd\3\2\2\2\u01c0\u01c3\7\5\2\2\u01c1\u01c3\5H%\2\u01c2\u01b9"+
		"\3\2\2\2\u01c2\u01c1\3\2\2\2\u01c3K\3\2\2\2\u01c4\u01c5\5T+\2\u01c5\u01c6"+
		"\7\20\2\2\u01c6\u01c7\5d\63\2\u01c7M\3\2\2\2\u01c8\u01c9\7\21\2\2\u01c9"+
		"\u01ca\5d\63\2\u01ca\u01cb\7\22\2\2\u01cbO\3\2\2\2\u01cc\u01cd\7\21\2"+
		"\2\u01cd\u01ce\5d\63\2\u01ce\u01cf\7\f\2\2\u01cf\u01d0\5d\63\2\u01d0\u01d1"+
		"\7\22\2\2\u01d1\u01da\3\2\2\2\u01d2\u01d3\7\21\2\2\u01d3\u01d4\5d\63\2"+
		"\u01d4\u01d5\t\2\2\2\u01d5\u01d6\7\f\2\2\u01d6\u01d7\5d\63\2\u01d7\u01d8"+
		"\7\22\2\2\u01d8\u01da\3\2\2\2\u01d9\u01cc\3\2\2\2\u01d9\u01d2\3\2\2\2"+
		"\u01daQ\3\2\2\2\u01db\u01dd\5N(\2\u01dc\u01db\3\2\2\2\u01dd\u01e0\3\2"+
		"\2\2\u01de\u01dc\3\2\2\2\u01de\u01df\3\2\2\2\u01df\u01e3\3\2\2\2\u01e0"+
		"\u01de\3\2\2\2\u01e1\u01e4\5N(\2\u01e2\u01e4\5P)\2\u01e3\u01e1\3\2\2\2"+
		"\u01e3\u01e2\3\2\2\2\u01e4S\3\2\2\2\u01e5\u01e7\5h\65\2\u01e6\u01e8\5"+
		"R*\2\u01e7\u01e6\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e8\u01f0\3\2\2\2\u01e9"+
		"\u01ea\7\24\2\2\u01ea\u01ec\5h\65\2\u01eb\u01ed\5R*\2\u01ec\u01eb\3\2"+
		"\2\2\u01ec\u01ed\3\2\2\2\u01ed\u01ef\3\2\2\2\u01ee\u01e9\3\2\2\2\u01ef"+
		"\u01f2\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f0\u01f1\3\2\2\2\u01f1U\3\2\2\2"+
		"\u01f2\u01f0\3\2\2\2\u01f3\u01f4\7 \2\2\u01f4\u01f5\7\13\2\2\u01f5\u01f6"+
		"\5d\63\2\u01f6\u01f7\7\n\2\2\u01f7\u01f9\7\4\2\2\u01f8\u01fa\5X-\2\u01f9"+
		"\u01f8\3\2\2\2\u01fa\u01fb\3\2\2\2\u01fb\u01f9\3\2\2\2\u01fb\u01fc\3\2"+
		"\2\2\u01fc\u01fd\3\2\2\2\u01fd\u01fe\7\5\2\2\u01feW\3\2\2\2\u01ff\u0202"+
		"\5d\63\2\u0200\u0202\7!\2\2\u0201\u01ff\3\2\2\2\u0201\u0200\3\2\2\2\u0202"+
		"\u0203\3\2\2\2\u0203\u0205\7\f\2\2\u0204\u0206\5H%\2\u0205\u0204\3\2\2"+
		"\2\u0206\u0207\3\2\2\2\u0207\u0205\3\2\2\2\u0207\u0208\3\2\2\2\u0208Y"+
		"\3\2\2\2\u0209\u020a\7\"\2\2\u020a\u020b\7\13\2\2\u020b\u020c\5d\63\2"+
		"\u020c\u020d\7\n\2\2\u020d\u020f\5J&\2\u020e\u0210\5\\/\2\u020f\u020e"+
		"\3\2\2\2\u020f\u0210\3\2\2\2\u0210[\3\2\2\2\u0211\u0212\7#\2\2\u0212\u0213"+
		"\5J&\2\u0213]\3\2\2\2\u0214\u0215\7$\2\2\u0215\u0216\7\13\2\2\u0216\u0217"+
		"\5L\'\2\u0217\u0218\7\6\2\2\u0218\u0219\5d\63\2\u0219\u021a\7\6\2\2\u021a"+
		"\u021b\5f\64\2\u021b\u021c\7\n\2\2\u021c\u021d\5J&\2\u021d_\3\2\2\2\u021e"+
		"\u021f\7H\2\2\u021f\u0220\7\13\2\2\u0220\u0225\5d\63\2\u0221\u0222\7\t"+
		"\2\2\u0222\u0224\5d\63\2\u0223\u0221\3\2\2\2\u0224\u0227\3\2\2\2\u0225"+
		"\u0223\3\2\2\2\u0225\u0226\3\2\2\2\u0226\u0228\3\2\2\2\u0227\u0225\3\2"+
		"\2\2\u0228\u0229\7\n\2\2\u0229a\3\2\2\2\u022a\u022b\t\3\2\2\u022bc\3\2"+
		"\2\2\u022c\u022d\b\63\1\2\u022d\u0251\5T+\2\u022e\u0251\5b\62\2\u022f"+
		"\u0251\5`\61\2\u0230\u0231\7\13\2\2\u0231\u0232\5d\63\2\u0232\u0233\7"+
		"\n\2\2\u0233\u0251\3\2\2\2\u0234\u0235\7%\2\2\u0235\u023a\5d\63\2\u0236"+
		"\u0237\7\t\2\2\u0237\u0239\5d\63\2\u0238\u0236\3\2\2\2\u0239\u023c\3\2"+
		"\2\2\u023a\u0238\3\2\2\2\u023a\u023b\3\2\2\2\u023b\u023d\3\2\2\2\u023c"+
		"\u023a\3\2\2\2\u023d\u023e\7\5\2\2\u023e\u0251\3\2\2\2\u023f\u0240\7\4"+
		"\2\2\u0240\u0245\5d\63\2\u0241\u0242\7\t\2\2\u0242\u0244\5d\63\2\u0243"+
		"\u0241\3\2\2\2\u0244\u0247\3\2\2\2\u0245\u0243\3\2\2\2\u0245\u0246\3\2"+
		"\2\2\u0246\u0248\3\2\2\2\u0247\u0245\3\2\2\2\u0248\u0249\7\5\2\2\u0249"+
		"\u0251\3\2\2\2\u024a\u024b\t\4\2\2\u024b\u0251\5d\63\f\u024c\u024d\7\37"+
		"\2\2\u024d\u0251\5d\63\13\u024e\u024f\t\5\2\2\u024f\u0251\5d\63\6\u0250"+
		"\u022c\3\2\2\2\u0250\u022e\3\2\2\2\u0250\u022f\3\2\2\2\u0250\u0230\3\2"+
		"\2\2\u0250\u0234\3\2\2\2\u0250\u023f\3\2\2\2\u0250\u024a\3\2\2\2\u0250"+
		"\u024c\3\2\2\2\u0250\u024e\3\2\2\2\u0251\u0271\3\2\2\2\u0252\u0253\f\n"+
		"\2\2\u0253\u0254\t\6\2\2\u0254\u0270\5d\63\13\u0255\u0256\f\t\2\2\u0256"+
		"\u0257\t\2\2\2\u0257\u0270\5d\63\n\u0258\u0259\f\b\2\2\u0259\u025a\t\7"+
		"\2\2\u025a\u0270\5d\63\t\u025b\u025c\f\7\2\2\u025c\u025d\t\b\2\2\u025d"+
		"\u0270\5d\63\b\u025e\u025f\f\5\2\2\u025f\u0260\t\t\2\2\u0260\u0270\5d"+
		"\63\6\u0261\u0262\f\4\2\2\u0262\u0263\t\n\2\2\u0263\u0270\5d\63\5\u0264"+
		"\u0265\f\3\2\2\u0265\u0266\7;\2\2\u0266\u0267\5d\63\2\u0267\u0268\7\f"+
		"\2\2\u0268\u0269\5d\63\4\u0269\u0270\3\2\2\2\u026a\u026b\f\16\2\2\u026b"+
		"\u026c\7&\2\2\u026c\u026d\5d\63\2\u026d\u026e\7\5\2\2\u026e\u0270\3\2"+
		"\2\2\u026f\u0252\3\2\2\2\u026f\u0255\3\2\2\2\u026f\u0258\3\2\2\2\u026f"+
		"\u025b\3\2\2\2\u026f\u025e\3\2\2\2\u026f\u0261\3\2\2\2\u026f\u0264\3\2"+
		"\2\2\u026f\u026a\3\2\2\2\u0270\u0273\3\2\2\2\u0271\u026f\3\2\2\2\u0271"+
		"\u0272\3\2\2\2\u0272e\3\2\2\2\u0273\u0271\3\2\2\2\u0274\u0275\5T+\2\u0275"+
		"\u0276\t\13\2\2\u0276\u0279\3\2\2\2\u0277\u0279\5L\'\2\u0278\u0274\3\2"+
		"\2\2\u0278\u0277\3\2\2\2\u0279g\3\2\2\2\u027a\u027b\t\f\2\2\u027bi\3\2"+
		"\2\2Dlnw\u0080\u0085\u0090\u009b\u00a3\u00a8\u00ab\u00af\u00b5\u00b9\u00bd"+
		"\u00c3\u00c7\u00cb\u00d1\u00d7\u00e3\u00eb\u0105\u0111\u0116\u0118\u0128"+
		"\u0130\u0137\u013b\u0143\u0147\u014b\u0152\u0156\u015a\u0161\u0169\u016d"+
		"\u0179\u0181\u0185\u0190\u0195\u0199\u019e\u01a9\u01b7\u01bd\u01c2\u01d9"+
		"\u01de\u01e3\u01e7\u01ec\u01f0\u01fb\u0201\u0207\u020f\u0225\u023a\u0245"+
		"\u0250\u026f\u0271\u0278";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}