// Generated from LucidIndent.g4 by ANTLR 4.8

package com.alchitry.labs.parsers.lucid.indent;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LucidIndentParser extends Parser {
	static {
		RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
			T__9 = 10, T__10 = 11, T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, BLOCK_COMMENT = 17,
			COMMENT = 18, WS = 19, STUFF = 20;
	public static final int
			RULE_lucid = 0, RULE_module = 1, RULE_global = 2, RULE_elem = 3, RULE_indent = 4,
			RULE_always_line = 5, RULE_else_block = 6, RULE_block = 7, RULE_fluff = 8;

	private static String[] makeRuleNames() {
		return new String[]{
				"lucid", "module", "global", "elem", "indent", "always_line", "else_block",
				"block", "fluff"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, "'module'", "'global'", "':'", "'always'", "'{'", "'}'", "'('",
				"')'", "'['", "']'", "'if'", "'for'", "'case'", "'else'", "';'", "'\\'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, "BLOCK_COMMENT", "COMMENT", "WS", "STUFF"
		};
	}

	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
	public String getGrammarFileName() { return "LucidIndent.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LucidIndentParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class LucidContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(LucidIndentParser.EOF, 0); }
		public List<IndentContext> indent() {
			return getRuleContexts(IndentContext.class);
		}
		public IndentContext indent(int i) {
			return getRuleContext(IndentContext.class,i);
		}
		public List<ModuleContext> module() {
			return getRuleContexts(ModuleContext.class);
		}
		public ModuleContext module(int i) {
			return getRuleContext(ModuleContext.class,i);
		}
		public List<GlobalContext> global() {
			return getRuleContexts(GlobalContext.class);
		}
		public GlobalContext global(int i) {
			return getRuleContext(GlobalContext.class,i);
		}
		public LucidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lucid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterLucid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitLucid(this);
		}
	}

	public final LucidContext lucid() throws RecognitionException {
		LucidContext _localctx = new LucidContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_lucid);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__6) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << BLOCK_COMMENT))) != 0)) {
				{
				{
				setState(18);
				indent();
				}
				}
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(28);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__1) {
				{
				setState(26);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(24);
					module();
					}
					break;
				case T__1:
					{
					setState(25);
					global();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(30);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(31);
			match(EOF);
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
		public List<FluffContext> fluff() {
			return getRuleContexts(FluffContext.class);
		}
		public FluffContext fluff(int i) {
			return getRuleContext(FluffContext.class,i);
		}
		public List<IndentContext> indent() {
			return getRuleContexts(IndentContext.class);
		}
		public IndentContext indent(int i) {
			return getRuleContext(IndentContext.class,i);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitModule(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_module);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			match(T__0);
			setState(37);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					{
					setState(34);
					fluff();
					}
					} 
				}
				setState(39);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(43);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__6) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << BLOCK_COMMENT))) != 0)) {
				{
				{
				setState(40);
				indent();
				}
				}
				setState(45);
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
		public List<FluffContext> fluff() {
			return getRuleContexts(FluffContext.class);
		}
		public FluffContext fluff(int i) {
			return getRuleContext(FluffContext.class,i);
		}
		public List<IndentContext> indent() {
			return getRuleContexts(IndentContext.class);
		}
		public IndentContext indent(int i) {
			return getRuleContext(IndentContext.class,i);
		}
		public GlobalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterGlobal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitGlobal(this);
		}
	}

	public final GlobalContext global() throws RecognitionException {
		GlobalContext _localctx = new GlobalContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_global);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(T__1);
			setState(50);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					{
					setState(47);
					fluff();
					}
					} 
				}
				setState(52);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__6) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << BLOCK_COMMENT))) != 0)) {
				{
				{
				setState(53);
				indent();
				}
				}
				setState(58);
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

	public static class ElemContext extends ParserRuleContext {
		public List<TerminalNode> STUFF() { return getTokens(LucidIndentParser.STUFF); }
		public TerminalNode STUFF(int i) {
			return getToken(LucidIndentParser.STUFF, i);
		}
		public List<IndentContext> indent() {
			return getRuleContexts(IndentContext.class);
		}
		public IndentContext indent(int i) {
			return getRuleContext(IndentContext.class,i);
		}
		public List<FluffContext> fluff() {
			return getRuleContexts(FluffContext.class);
		}
		public FluffContext fluff(int i) {
			return getRuleContext(FluffContext.class,i);
		}
		public ElemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterElem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitElem(this);
		}
	}

	public final ElemContext elem() throws RecognitionException {
		ElemContext _localctx = new ElemContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_elem);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(60); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(59);
				match(STUFF);
				}
				}
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STUFF );
			setState(64);
			match(T__2);
			setState(69);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					setState(67);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case T__3:
					case T__4:
					case T__6:
					case T__8:
					case T__10:
					case T__11:
					case T__12:
					case BLOCK_COMMENT:
						{
						setState(65);
						indent();
						}
						break;
					case T__2:
					case T__14:
					case T__15:
					case STUFF:
						{
						setState(66);
						fluff();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(71);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
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

	public static class IndentContext extends ParserRuleContext {
		public TerminalNode BLOCK_COMMENT() { return getToken(LucidIndentParser.BLOCK_COMMENT, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Always_lineContext always_line() {
			return getRuleContext(Always_lineContext.class,0);
		}
		public List<IndentContext> indent() {
			return getRuleContexts(IndentContext.class);
		}
		public IndentContext indent(int i) {
			return getRuleContext(IndentContext.class,i);
		}
		public List<FluffContext> fluff() {
			return getRuleContexts(FluffContext.class);
		}
		public FluffContext fluff(int i) {
			return getRuleContext(FluffContext.class,i);
		}
		public IndentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterIndent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitIndent(this);
		}
	}

	public final IndentContext indent() throws RecognitionException {
		IndentContext _localctx = new IndentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_indent);
		try {
			int _alt;
			setState(103);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BLOCK_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				match(BLOCK_COMMENT);
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(73);
				match(T__3);
				setState(74);
				block();
				}
				break;
			case T__10:
			case T__11:
			case T__12:
				enterOuterAlt(_localctx, 3);
				{
				setState(75);
				always_line();
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 4);
				{
				setState(76);
				match(T__4);
				setState(81);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(79);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case T__3:
						case T__4:
						case T__6:
						case T__8:
						case T__10:
						case T__11:
						case T__12:
						case BLOCK_COMMENT:
							{
							setState(77);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(78);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(83);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				}
				setState(84);
				match(T__5);
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 5);
				{
				setState(85);
				match(T__6);
				setState(90);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(88);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case T__3:
						case T__4:
						case T__6:
						case T__8:
						case T__10:
						case T__11:
						case T__12:
						case BLOCK_COMMENT:
							{
							setState(86);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(87);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(92);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
				}
				setState(93);
				match(T__7);
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 6);
				{
				setState(94);
				match(T__8);
				setState(99);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(97);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case T__3:
						case T__4:
						case T__6:
						case T__8:
						case T__10:
						case T__11:
						case T__12:
						case BLOCK_COMMENT:
							{
							setState(95);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(96);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(101);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
				}
				setState(102);
				match(T__9);
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

	public static class Always_lineContext extends ParserRuleContext {
		public IndentContext indent() {
			return getRuleContext(IndentContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Else_blockContext else_block() {
			return getRuleContext(Else_blockContext.class,0);
		}
		public List<ElemContext> elem() {
			return getRuleContexts(ElemContext.class);
		}
		public ElemContext elem(int i) {
			return getRuleContext(ElemContext.class,i);
		}
		public Always_lineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_always_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterAlways_line(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitAlways_line(this);
		}
	}

	public final Always_lineContext always_line() throws RecognitionException {
		Always_lineContext _localctx = new Always_lineContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_always_line);
		int _la;
		try {
			setState(126);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__10:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				match(T__10);
				setState(106);
				indent();
				setState(107);
				block();
				setState(109);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(108);
					else_block();
					}
					break;
				}
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(111);
				match(T__11);
				setState(112);
				indent();
				setState(113);
				block();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 3);
				{
				setState(115);
				match(T__12);
				setState(116);
				indent();
				setState(117);
				match(T__4);
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==STUFF) {
					{
					{
					setState(118);
					elem();
					}
					}
					setState(123);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(124);
				match(T__5);
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

	public static class Else_blockContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Else_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterElse_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitElse_block(this);
		}
	}

	public final Else_blockContext else_block() throws RecognitionException {
		Else_blockContext _localctx = new Else_blockContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_else_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(T__13);
			setState(129);
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

	public static class BlockContext extends ParserRuleContext {
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	 
		public BlockContext() { }
		public void copyFrom(BlockContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SingleBlockContext extends BlockContext {
		public List<IndentContext> indent() {
			return getRuleContexts(IndentContext.class);
		}
		public IndentContext indent(int i) {
			return getRuleContext(IndentContext.class,i);
		}
		public List<FluffContext> fluff() {
			return getRuleContexts(FluffContext.class);
		}
		public FluffContext fluff(int i) {
			return getRuleContext(FluffContext.class,i);
		}
		public SingleBlockContext(BlockContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterSingleBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitSingleBlock(this);
		}
	}
	public static class MultiBlockContext extends BlockContext {
		public List<IndentContext> indent() {
			return getRuleContexts(IndentContext.class);
		}
		public IndentContext indent(int i) {
			return getRuleContext(IndentContext.class,i);
		}
		public List<FluffContext> fluff() {
			return getRuleContexts(FluffContext.class);
		}
		public FluffContext fluff(int i) {
			return getRuleContext(FluffContext.class,i);
		}
		public MultiBlockContext(BlockContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterMultiBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitMultiBlock(this);
		}
	}
	public static class AlwaysBlockContext extends BlockContext {
		public Always_lineContext always_line() {
			return getRuleContext(Always_lineContext.class,0);
		}
		public AlwaysBlockContext(BlockContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterAlwaysBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitAlwaysBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_block);
		try {
			int _alt;
			setState(149);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				_localctx = new AlwaysBlockContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(131);
				always_line();
				}
				break;
			case 2:
				_localctx = new MultiBlockContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(132);
				match(T__4);
				setState(137);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(135);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case T__3:
						case T__4:
						case T__6:
						case T__8:
						case T__10:
						case T__11:
						case T__12:
						case BLOCK_COMMENT:
							{
							setState(133);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(134);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(139);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
				}
				setState(140);
				match(T__5);
				}
				break;
			case 3:
				_localctx = new SingleBlockContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(145);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(143);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case T__3:
						case T__4:
						case T__6:
						case T__8:
						case T__10:
						case T__11:
						case T__12:
						case BLOCK_COMMENT:
							{
							setState(141);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(142);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(147);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				}
				setState(148);
				match(T__14);
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

	public static class FluffContext extends ParserRuleContext {
		public List<TerminalNode> STUFF() { return getTokens(LucidIndentParser.STUFF); }
		public TerminalNode STUFF(int i) {
			return getToken(LucidIndentParser.STUFF, i);
		}
		public FluffContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fluff; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).enterFluff(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LucidIndentListener ) ((LucidIndentListener)listener).exitFluff(this);
		}
	}

	public final FluffContext fluff() throws RecognitionException {
		FluffContext _localctx = new FluffContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_fluff);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(152); 
			_errHandler.sync(this);
			_alt = 1+1;
			do {
				switch (_alt) {
				case 1+1:
					{
					{
					setState(151);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__14) | (1L << T__15) | (1L << STUFF))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(154); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			} while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\26\u009f\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\7"+
		"\2\26\n\2\f\2\16\2\31\13\2\3\2\3\2\7\2\35\n\2\f\2\16\2 \13\2\3\2\3\2\3"+
		"\3\3\3\7\3&\n\3\f\3\16\3)\13\3\3\3\7\3,\n\3\f\3\16\3/\13\3\3\4\3\4\7\4"+
		"\63\n\4\f\4\16\4\66\13\4\3\4\7\49\n\4\f\4\16\4<\13\4\3\5\6\5?\n\5\r\5"+
		"\16\5@\3\5\3\5\3\5\7\5F\n\5\f\5\16\5I\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\7\6R\n\6\f\6\16\6U\13\6\3\6\3\6\3\6\3\6\7\6[\n\6\f\6\16\6^\13\6\3\6\3"+
		"\6\3\6\3\6\7\6d\n\6\f\6\16\6g\13\6\3\6\5\6j\n\6\3\7\3\7\3\7\3\7\5\7p\n"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7z\n\7\f\7\16\7}\13\7\3\7\3\7\5\7"+
		"\u0081\n\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\7\t\u008a\n\t\f\t\16\t\u008d\13"+
		"\t\3\t\3\t\3\t\7\t\u0092\n\t\f\t\16\t\u0095\13\t\3\t\5\t\u0098\n\t\3\n"+
		"\6\n\u009b\n\n\r\n\16\n\u009c\3\n\13\'\64GS\\e\u008b\u0093\u009c\2\13"+
		"\2\4\6\b\n\f\16\20\22\2\3\5\2\5\5\21\22\26\26\2\u00b5\2\27\3\2\2\2\4#"+
		"\3\2\2\2\6\60\3\2\2\2\b>\3\2\2\2\ni\3\2\2\2\f\u0080\3\2\2\2\16\u0082\3"+
		"\2\2\2\20\u0097\3\2\2\2\22\u009a\3\2\2\2\24\26\5\n\6\2\25\24\3\2\2\2\26"+
		"\31\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2\2\30\36\3\2\2\2\31\27\3\2\2\2\32"+
		"\35\5\4\3\2\33\35\5\6\4\2\34\32\3\2\2\2\34\33\3\2\2\2\35 \3\2\2\2\36\34"+
		"\3\2\2\2\36\37\3\2\2\2\37!\3\2\2\2 \36\3\2\2\2!\"\7\2\2\3\"\3\3\2\2\2"+
		"#\'\7\3\2\2$&\5\22\n\2%$\3\2\2\2&)\3\2\2\2\'(\3\2\2\2\'%\3\2\2\2(-\3\2"+
		"\2\2)\'\3\2\2\2*,\5\n\6\2+*\3\2\2\2,/\3\2\2\2-+\3\2\2\2-.\3\2\2\2.\5\3"+
		"\2\2\2/-\3\2\2\2\60\64\7\4\2\2\61\63\5\22\n\2\62\61\3\2\2\2\63\66\3\2"+
		"\2\2\64\65\3\2\2\2\64\62\3\2\2\2\65:\3\2\2\2\66\64\3\2\2\2\679\5\n\6\2"+
		"8\67\3\2\2\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;\7\3\2\2\2<:\3\2\2\2=?\7\26"+
		"\2\2>=\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3\2\2\2AB\3\2\2\2BG\7\5\2\2CF\5\n"+
		"\6\2DF\5\22\n\2EC\3\2\2\2ED\3\2\2\2FI\3\2\2\2GH\3\2\2\2GE\3\2\2\2H\t\3"+
		"\2\2\2IG\3\2\2\2Jj\7\23\2\2KL\7\6\2\2Lj\5\20\t\2Mj\5\f\7\2NS\7\7\2\2O"+
		"R\5\n\6\2PR\5\22\n\2QO\3\2\2\2QP\3\2\2\2RU\3\2\2\2ST\3\2\2\2SQ\3\2\2\2"+
		"TV\3\2\2\2US\3\2\2\2Vj\7\b\2\2W\\\7\t\2\2X[\5\n\6\2Y[\5\22\n\2ZX\3\2\2"+
		"\2ZY\3\2\2\2[^\3\2\2\2\\]\3\2\2\2\\Z\3\2\2\2]_\3\2\2\2^\\\3\2\2\2_j\7"+
		"\n\2\2`e\7\13\2\2ad\5\n\6\2bd\5\22\n\2ca\3\2\2\2cb\3\2\2\2dg\3\2\2\2e"+
		"f\3\2\2\2ec\3\2\2\2fh\3\2\2\2ge\3\2\2\2hj\7\f\2\2iJ\3\2\2\2iK\3\2\2\2"+
		"iM\3\2\2\2iN\3\2\2\2iW\3\2\2\2i`\3\2\2\2j\13\3\2\2\2kl\7\r\2\2lm\5\n\6"+
		"\2mo\5\20\t\2np\5\16\b\2on\3\2\2\2op\3\2\2\2p\u0081\3\2\2\2qr\7\16\2\2"+
		"rs\5\n\6\2st\5\20\t\2t\u0081\3\2\2\2uv\7\17\2\2vw\5\n\6\2w{\7\7\2\2xz"+
		"\5\b\5\2yx\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|~\3\2\2\2}{\3\2\2\2~"+
		"\177\7\b\2\2\177\u0081\3\2\2\2\u0080k\3\2\2\2\u0080q\3\2\2\2\u0080u\3"+
		"\2\2\2\u0081\r\3\2\2\2\u0082\u0083\7\20\2\2\u0083\u0084\5\20\t\2\u0084"+
		"\17\3\2\2\2\u0085\u0098\5\f\7\2\u0086\u008b\7\7\2\2\u0087\u008a\5\n\6"+
		"\2\u0088\u008a\5\22\n\2\u0089\u0087\3\2\2\2\u0089\u0088\3\2\2\2\u008a"+
		"\u008d\3\2\2\2\u008b\u008c\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008e\3\2"+
		"\2\2\u008d\u008b\3\2\2\2\u008e\u0098\7\b\2\2\u008f\u0092\5\n\6\2\u0090"+
		"\u0092\5\22\n\2\u0091\u008f\3\2\2\2\u0091\u0090\3\2\2\2\u0092\u0095\3"+
		"\2\2\2\u0093\u0094\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0096\3\2\2\2\u0095"+
		"\u0093\3\2\2\2\u0096\u0098\7\21\2\2\u0097\u0085\3\2\2\2\u0097\u0086\3"+
		"\2\2\2\u0097\u0093\3\2\2\2\u0098\21\3\2\2\2\u0099\u009b\t\2\2\2\u009a"+
		"\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009d\3\2\2\2\u009c\u009a\3\2"+
		"\2\2\u009d\23\3\2\2\2\34\27\34\36\'-\64:@EGQSZ\\ceio{\u0080\u0089\u008b"+
		"\u0091\u0093\u0097\u009c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}