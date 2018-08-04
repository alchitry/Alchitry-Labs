// Generated from LucidIndent.g4 by ANTLR 4.5

package com.alchitry.labs.lucid.indent;

import java.util.List;

import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
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
public class LucidIndentParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, BLOCK_COMMENT=17, 
		COMMENT=18, WS=19, STUFF=20;
	public static final int
		RULE_lucid = 0, RULE_module = 1, RULE_global = 2, RULE_elem = 3, RULE_indent = 4, 
		RULE_always_line = 5, RULE_else_block = 6, RULE_block = 7, RULE_fluff = 8;
	public static final String[] ruleNames = {
		"lucid", "module", "global", "elem", "indent", "always_line", "else_block", 
		"block", "fluff"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'module'", "'global'", "':'", "'always'", "'{'", "'}'", "'('", 
		"')'", "'['", "']'", "'if'", "'for'", "'case'", "'else'", "';'", "'\\'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "BLOCK_COMMENT", "COMMENT", "WS", "STUFF"
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
			setState(31);
			match(T__0);
			setState(35);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					{
					setState(32);
					fluff();
					}
					} 
				}
				setState(37);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__6) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << BLOCK_COMMENT))) != 0)) {
				{
				{
				setState(38);
				indent();
				}
				}
				setState(43);
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
			setState(44);
			match(T__1);
			setState(48);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					{
					setState(45);
					fluff();
					}
					} 
				}
				setState(50);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__6) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << BLOCK_COMMENT))) != 0)) {
				{
				{
				setState(51);
				indent();
				}
				}
				setState(56);
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
			setState(58); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(57);
				match(STUFF);
				}
				}
				setState(60); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STUFF );
			setState(62);
			match(T__2);
			setState(67);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					setState(65);
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
						setState(63);
						indent();
						}
						break;
					case T__2:
					case T__14:
					case T__15:
					case STUFF:
						{
						setState(64);
						fluff();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(69);
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
			setState(101);
			switch (_input.LA(1)) {
			case BLOCK_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				match(BLOCK_COMMENT);
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				match(T__3);
				setState(72);
				block();
				}
				break;
			case T__10:
			case T__11:
			case T__12:
				enterOuterAlt(_localctx, 3);
				{
				setState(73);
				always_line();
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 4);
				{
				setState(74);
				match(T__4);
				setState(79);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(77);
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
							setState(75);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(76);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(81);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				}
				setState(82);
				match(T__5);
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 5);
				{
				setState(83);
				match(T__6);
				setState(88);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(86);
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
							setState(84);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(85);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(90);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
				}
				setState(91);
				match(T__7);
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 6);
				{
				setState(92);
				match(T__8);
				setState(97);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(95);
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
							setState(93);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(94);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(99);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
				}
				setState(100);
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
			setState(124);
			switch (_input.LA(1)) {
			case T__10:
				enterOuterAlt(_localctx, 1);
				{
				setState(103);
				match(T__10);
				setState(104);
				indent();
				setState(105);
				block();
				setState(107);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(106);
					else_block();
					}
					break;
				}
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(109);
				match(T__11);
				setState(110);
				indent();
				setState(111);
				block();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 3);
				{
				setState(113);
				match(T__12);
				setState(114);
				indent();
				setState(115);
				match(T__4);
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==STUFF) {
					{
					{
					setState(116);
					elem();
					}
					}
					setState(121);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(122);
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
			setState(126);
			match(T__13);
			setState(127);
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
			setState(147);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				_localctx = new AlwaysBlockContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				always_line();
				}
				break;
			case 2:
				_localctx = new MultiBlockContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				match(T__4);
				setState(135);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(133);
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
							setState(131);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(132);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(137);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
				}
				setState(138);
				match(T__5);
				}
				break;
			case 3:
				_localctx = new SingleBlockContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(143);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						setState(141);
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
							setState(139);
							indent();
							}
							break;
						case T__2:
						case T__14:
						case T__15:
						case STUFF:
							{
							setState(140);
							fluff();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(145);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				}
				setState(146);
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
			setState(150); 
			_errHandler.sync(this);
			_alt = 1+1;
			do {
				switch (_alt) {
				case 1+1:
					{
					{
					setState(149);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__14) | (1L << T__15) | (1L << STUFF))) != 0)) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(152); 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\26\u009d\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\7"+
		"\2\26\n\2\f\2\16\2\31\13\2\3\2\3\2\7\2\35\n\2\f\2\16\2 \13\2\3\3\3\3\7"+
		"\3$\n\3\f\3\16\3\'\13\3\3\3\7\3*\n\3\f\3\16\3-\13\3\3\4\3\4\7\4\61\n\4"+
		"\f\4\16\4\64\13\4\3\4\7\4\67\n\4\f\4\16\4:\13\4\3\5\6\5=\n\5\r\5\16\5"+
		">\3\5\3\5\3\5\7\5D\n\5\f\5\16\5G\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6"+
		"P\n\6\f\6\16\6S\13\6\3\6\3\6\3\6\3\6\7\6Y\n\6\f\6\16\6\\\13\6\3\6\3\6"+
		"\3\6\3\6\7\6b\n\6\f\6\16\6e\13\6\3\6\5\6h\n\6\3\7\3\7\3\7\3\7\5\7n\n\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7x\n\7\f\7\16\7{\13\7\3\7\3\7\5\7\177"+
		"\n\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\7\t\u0088\n\t\f\t\16\t\u008b\13\t\3\t"+
		"\3\t\3\t\7\t\u0090\n\t\f\t\16\t\u0093\13\t\3\t\5\t\u0096\n\t\3\n\6\n\u0099"+
		"\n\n\r\n\16\n\u009a\3\n\13%\62EQZc\u0089\u0091\u009a\2\13\2\4\6\b\n\f"+
		"\16\20\22\2\3\5\2\5\5\21\22\26\26\u00b3\2\27\3\2\2\2\4!\3\2\2\2\6.\3\2"+
		"\2\2\b<\3\2\2\2\ng\3\2\2\2\f~\3\2\2\2\16\u0080\3\2\2\2\20\u0095\3\2\2"+
		"\2\22\u0098\3\2\2\2\24\26\5\n\6\2\25\24\3\2\2\2\26\31\3\2\2\2\27\25\3"+
		"\2\2\2\27\30\3\2\2\2\30\36\3\2\2\2\31\27\3\2\2\2\32\35\5\4\3\2\33\35\5"+
		"\6\4\2\34\32\3\2\2\2\34\33\3\2\2\2\35 \3\2\2\2\36\34\3\2\2\2\36\37\3\2"+
		"\2\2\37\3\3\2\2\2 \36\3\2\2\2!%\7\3\2\2\"$\5\22\n\2#\"\3\2\2\2$\'\3\2"+
		"\2\2%&\3\2\2\2%#\3\2\2\2&+\3\2\2\2\'%\3\2\2\2(*\5\n\6\2)(\3\2\2\2*-\3"+
		"\2\2\2+)\3\2\2\2+,\3\2\2\2,\5\3\2\2\2-+\3\2\2\2.\62\7\4\2\2/\61\5\22\n"+
		"\2\60/\3\2\2\2\61\64\3\2\2\2\62\63\3\2\2\2\62\60\3\2\2\2\638\3\2\2\2\64"+
		"\62\3\2\2\2\65\67\5\n\6\2\66\65\3\2\2\2\67:\3\2\2\28\66\3\2\2\289\3\2"+
		"\2\29\7\3\2\2\2:8\3\2\2\2;=\7\26\2\2<;\3\2\2\2=>\3\2\2\2><\3\2\2\2>?\3"+
		"\2\2\2?@\3\2\2\2@E\7\5\2\2AD\5\n\6\2BD\5\22\n\2CA\3\2\2\2CB\3\2\2\2DG"+
		"\3\2\2\2EF\3\2\2\2EC\3\2\2\2F\t\3\2\2\2GE\3\2\2\2Hh\7\23\2\2IJ\7\6\2\2"+
		"Jh\5\20\t\2Kh\5\f\7\2LQ\7\7\2\2MP\5\n\6\2NP\5\22\n\2OM\3\2\2\2ON\3\2\2"+
		"\2PS\3\2\2\2QR\3\2\2\2QO\3\2\2\2RT\3\2\2\2SQ\3\2\2\2Th\7\b\2\2UZ\7\t\2"+
		"\2VY\5\n\6\2WY\5\22\n\2XV\3\2\2\2XW\3\2\2\2Y\\\3\2\2\2Z[\3\2\2\2ZX\3\2"+
		"\2\2[]\3\2\2\2\\Z\3\2\2\2]h\7\n\2\2^c\7\13\2\2_b\5\n\6\2`b\5\22\n\2a_"+
		"\3\2\2\2a`\3\2\2\2be\3\2\2\2cd\3\2\2\2ca\3\2\2\2df\3\2\2\2ec\3\2\2\2f"+
		"h\7\f\2\2gH\3\2\2\2gI\3\2\2\2gK\3\2\2\2gL\3\2\2\2gU\3\2\2\2g^\3\2\2\2"+
		"h\13\3\2\2\2ij\7\r\2\2jk\5\n\6\2km\5\20\t\2ln\5\16\b\2ml\3\2\2\2mn\3\2"+
		"\2\2n\177\3\2\2\2op\7\16\2\2pq\5\n\6\2qr\5\20\t\2r\177\3\2\2\2st\7\17"+
		"\2\2tu\5\n\6\2uy\7\7\2\2vx\5\b\5\2wv\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2"+
		"\2\2z|\3\2\2\2{y\3\2\2\2|}\7\b\2\2}\177\3\2\2\2~i\3\2\2\2~o\3\2\2\2~s"+
		"\3\2\2\2\177\r\3\2\2\2\u0080\u0081\7\20\2\2\u0081\u0082\5\20\t\2\u0082"+
		"\17\3\2\2\2\u0083\u0096\5\f\7\2\u0084\u0089\7\7\2\2\u0085\u0088\5\n\6"+
		"\2\u0086\u0088\5\22\n\2\u0087\u0085\3\2\2\2\u0087\u0086\3\2\2\2\u0088"+
		"\u008b\3\2\2\2\u0089\u008a\3\2\2\2\u0089\u0087\3\2\2\2\u008a\u008c\3\2"+
		"\2\2\u008b\u0089\3\2\2\2\u008c\u0096\7\b\2\2\u008d\u0090\5\n\6\2\u008e"+
		"\u0090\5\22\n\2\u008f\u008d\3\2\2\2\u008f\u008e\3\2\2\2\u0090\u0093\3"+
		"\2\2\2\u0091\u0092\3\2\2\2\u0091\u008f\3\2\2\2\u0092\u0094\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0094\u0096\7\21\2\2\u0095\u0083\3\2\2\2\u0095\u0084\3"+
		"\2\2\2\u0095\u0091\3\2\2\2\u0096\21\3\2\2\2\u0097\u0099\t\2\2\2\u0098"+
		"\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009b\3\2\2\2\u009a\u0098\3\2"+
		"\2\2\u009b\23\3\2\2\2\34\27\34\36%+\628>CEOQXZacgmy~\u0087\u0089\u008f"+
		"\u0091\u0095\u009a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}