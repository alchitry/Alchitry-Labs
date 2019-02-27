// Generated from AlchitryConstraints.g4 by ANTLR 4.7.1

package com.alchitry.labs.parsers.constraints.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AlchitryConstraintsParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, FREQ_UNIT=8, BASIC_NAME=9, 
		REAL=10, INT=11, BLOCK_COMMENT=12, COMMENT=13, WS=14;
	public static final int
		RULE_alchitry_constraints = 0, RULE_pin = 1, RULE_clock = 2, RULE_port_name = 3, 
		RULE_pin_name = 4, RULE_frequency = 5, RULE_array_index = 6, RULE_number = 7;
	public static final String[] ruleNames = {
		"alchitry_constraints", "pin", "clock", "port_name", "pin_name", "frequency", 
		"array_index", "number"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'pin'", "'pullup'", "'pulldown'", "';'", "'clock'", "'['", "']'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "FREQ_UNIT", "BASIC_NAME", 
		"REAL", "INT", "BLOCK_COMMENT", "COMMENT", "WS"
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
	public String getGrammarFileName() { return "AlchitryConstraints.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AlchitryConstraintsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Alchitry_constraintsContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(AlchitryConstraintsParser.EOF, 0); }
		public List<PinContext> pin() {
			return getRuleContexts(PinContext.class);
		}
		public PinContext pin(int i) {
			return getRuleContext(PinContext.class,i);
		}
		public List<ClockContext> clock() {
			return getRuleContexts(ClockContext.class);
		}
		public ClockContext clock(int i) {
			return getRuleContext(ClockContext.class,i);
		}
		public Alchitry_constraintsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alchitry_constraints; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterAlchitry_constraints(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitAlchitry_constraints(this);
		}
	}

	public final Alchitry_constraintsContext alchitry_constraints() throws RecognitionException {
		Alchitry_constraintsContext _localctx = new Alchitry_constraintsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_alchitry_constraints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__4) {
				{
				setState(18);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(16);
					pin();
					}
					break;
				case T__4:
					{
					setState(17);
					clock();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(22);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(23);
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

	public static class PinContext extends ParserRuleContext {
		public Port_nameContext port_name() {
			return getRuleContext(Port_nameContext.class,0);
		}
		public Pin_nameContext pin_name() {
			return getRuleContext(Pin_nameContext.class,0);
		}
		public PinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterPin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitPin(this);
		}
	}

	public final PinContext pin() throws RecognitionException {
		PinContext _localctx = new PinContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_pin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			match(T__0);
			setState(26);
			port_name();
			setState(27);
			pin_name();
			setState(29);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(28);
				match(T__1);
				}
			}

			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(31);
				match(T__2);
				}
			}

			setState(34);
			match(T__3);
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

	public static class ClockContext extends ParserRuleContext {
		public Port_nameContext port_name() {
			return getRuleContext(Port_nameContext.class,0);
		}
		public FrequencyContext frequency() {
			return getRuleContext(FrequencyContext.class,0);
		}
		public ClockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterClock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitClock(this);
		}
	}

	public final ClockContext clock() throws RecognitionException {
		ClockContext _localctx = new ClockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_clock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(T__4);
			setState(37);
			port_name();
			setState(38);
			frequency();
			setState(39);
			match(T__3);
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

	public static class Port_nameContext extends ParserRuleContext {
		public TerminalNode BASIC_NAME() { return getToken(AlchitryConstraintsParser.BASIC_NAME, 0); }
		public TerminalNode FREQ_UNIT() { return getToken(AlchitryConstraintsParser.FREQ_UNIT, 0); }
		public Array_indexContext array_index() {
			return getRuleContext(Array_indexContext.class,0);
		}
		public Port_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_port_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterPort_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitPort_name(this);
		}
	}

	public final Port_nameContext port_name() throws RecognitionException {
		Port_nameContext _localctx = new Port_nameContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_port_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			_la = _input.LA(1);
			if ( !(_la==FREQ_UNIT || _la==BASIC_NAME) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(43);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(42);
				array_index();
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

	public static class Pin_nameContext extends ParserRuleContext {
		public TerminalNode BASIC_NAME() { return getToken(AlchitryConstraintsParser.BASIC_NAME, 0); }
		public TerminalNode FREQ_UNIT() { return getToken(AlchitryConstraintsParser.FREQ_UNIT, 0); }
		public Pin_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pin_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterPin_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitPin_name(this);
		}
	}

	public final Pin_nameContext pin_name() throws RecognitionException {
		Pin_nameContext _localctx = new Pin_nameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_pin_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			_la = _input.LA(1);
			if ( !(_la==FREQ_UNIT || _la==BASIC_NAME) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
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

	public static class FrequencyContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode FREQ_UNIT() { return getToken(AlchitryConstraintsParser.FREQ_UNIT, 0); }
		public FrequencyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_frequency; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterFrequency(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitFrequency(this);
		}
	}

	public final FrequencyContext frequency() throws RecognitionException {
		FrequencyContext _localctx = new FrequencyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_frequency);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			number();
			setState(48);
			match(FREQ_UNIT);
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
		public TerminalNode INT() { return getToken(AlchitryConstraintsParser.INT, 0); }
		public Array_indexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_index; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterArray_index(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitArray_index(this);
		}
	}

	public final Array_indexContext array_index() throws RecognitionException {
		Array_indexContext _localctx = new Array_indexContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_array_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(T__5);
			setState(51);
			match(INT);
			setState(52);
			match(T__6);
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
		public TerminalNode INT() { return getToken(AlchitryConstraintsParser.INT, 0); }
		public TerminalNode REAL() { return getToken(AlchitryConstraintsParser.REAL, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			_la = _input.LA(1);
			if ( !(_la==REAL || _la==INT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\20;\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\7\2\25\n\2"+
		"\f\2\16\2\30\13\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3 \n\3\3\3\5\3#\n\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\5\5.\n\5\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3"+
		"\b\3\b\3\t\3\t\3\t\2\2\n\2\4\6\b\n\f\16\20\2\4\3\2\n\13\3\2\f\r\2\67\2"+
		"\26\3\2\2\2\4\33\3\2\2\2\6&\3\2\2\2\b+\3\2\2\2\n/\3\2\2\2\f\61\3\2\2\2"+
		"\16\64\3\2\2\2\208\3\2\2\2\22\25\5\4\3\2\23\25\5\6\4\2\24\22\3\2\2\2\24"+
		"\23\3\2\2\2\25\30\3\2\2\2\26\24\3\2\2\2\26\27\3\2\2\2\27\31\3\2\2\2\30"+
		"\26\3\2\2\2\31\32\7\2\2\3\32\3\3\2\2\2\33\34\7\3\2\2\34\35\5\b\5\2\35"+
		"\37\5\n\6\2\36 \7\4\2\2\37\36\3\2\2\2\37 \3\2\2\2 \"\3\2\2\2!#\7\5\2\2"+
		"\"!\3\2\2\2\"#\3\2\2\2#$\3\2\2\2$%\7\6\2\2%\5\3\2\2\2&\'\7\7\2\2\'(\5"+
		"\b\5\2()\5\f\7\2)*\7\6\2\2*\7\3\2\2\2+-\t\2\2\2,.\5\16\b\2-,\3\2\2\2-"+
		".\3\2\2\2.\t\3\2\2\2/\60\t\2\2\2\60\13\3\2\2\2\61\62\5\20\t\2\62\63\7"+
		"\n\2\2\63\r\3\2\2\2\64\65\7\b\2\2\65\66\7\r\2\2\66\67\7\t\2\2\67\17\3"+
		"\2\2\289\t\3\2\29\21\3\2\2\2\7\24\26\37\"-";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}