// Generated from AlchitryConstraints.g4 by ANTLR 4.7.1

package com.alchitry.labs.parsers.constraints;

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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, PULLUP=7, PULLDOWN=8, 
		FREQ_UNIT=9, BASIC_NAME=10, REAL=11, INT=12, BLOCK_COMMENT=13, COMMENT=14, 
		WS=15;
	public static final int
		RULE_alchitry_constraints = 0, RULE_pin = 1, RULE_clock = 2, RULE_name = 3, 
		RULE_port_name = 4, RULE_pin_name = 5, RULE_frequency = 6, RULE_array_index = 7, 
		RULE_number = 8;
	public static final String[] ruleNames = {
		"alchitry_constraints", "pin", "clock", "name", "port_name", "pin_name", 
		"frequency", "array_index", "number"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'pin'", "';'", "'clock'", "'.'", "'['", "']'", "'pullup'", "'pulldown'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "PULLUP", "PULLDOWN", "FREQ_UNIT", 
		"BASIC_NAME", "REAL", "INT", "BLOCK_COMMENT", "COMMENT", "WS"
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
			setState(22);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__2) {
				{
				setState(20);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(18);
					pin();
					}
					break;
				case T__2:
					{
					setState(19);
					clock();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(24);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(25);
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
		public TerminalNode PULLUP() { return getToken(AlchitryConstraintsParser.PULLUP, 0); }
		public TerminalNode PULLDOWN() { return getToken(AlchitryConstraintsParser.PULLDOWN, 0); }
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
			setState(27);
			match(T__0);
			setState(28);
			port_name();
			setState(29);
			pin_name();
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PULLUP) {
				{
				setState(30);
				match(PULLUP);
				}
			}

			setState(34);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PULLDOWN) {
				{
				setState(33);
				match(PULLDOWN);
				}
			}

			setState(36);
			match(T__1);
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
			setState(38);
			match(T__2);
			setState(39);
			port_name();
			setState(40);
			frequency();
			setState(41);
			match(T__1);
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
		public TerminalNode BASIC_NAME() { return getToken(AlchitryConstraintsParser.BASIC_NAME, 0); }
		public TerminalNode FREQ_UNIT() { return getToken(AlchitryConstraintsParser.FREQ_UNIT, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AlchitryConstraintsListener ) ((AlchitryConstraintsListener)listener).exitName(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
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

	public static class Port_nameContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<Array_indexContext> array_index() {
			return getRuleContexts(Array_indexContext.class);
		}
		public Array_indexContext array_index(int i) {
			return getRuleContext(Array_indexContext.class,i);
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
		enterRule(_localctx, 8, RULE_port_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			name();
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(46);
				array_index();
				}
				}
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(52);
				match(T__3);
				setState(53);
				name();
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(54);
					array_index();
					}
					}
					setState(59);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				setState(64);
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

	public static class Pin_nameContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
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
		enterRule(_localctx, 10, RULE_pin_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			name();
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
		enterRule(_localctx, 12, RULE_frequency);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			number();
			setState(68);
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
		enterRule(_localctx, 14, RULE_array_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(T__4);
			setState(71);
			match(INT);
			setState(72);
			match(T__5);
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
		enterRule(_localctx, 16, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\21O\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\7\2"+
		"\27\n\2\f\2\16\2\32\13\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3\"\n\3\3\3\5\3%\n"+
		"\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\7\6\62\n\6\f\6\16\6\65"+
		"\13\6\3\6\3\6\3\6\7\6:\n\6\f\6\16\6=\13\6\7\6?\n\6\f\6\16\6B\13\6\3\7"+
		"\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\2\2\13\2\4\6\b\n\f\16\20"+
		"\22\2\4\3\2\13\f\3\2\r\16\2L\2\30\3\2\2\2\4\35\3\2\2\2\6(\3\2\2\2\b-\3"+
		"\2\2\2\n/\3\2\2\2\fC\3\2\2\2\16E\3\2\2\2\20H\3\2\2\2\22L\3\2\2\2\24\27"+
		"\5\4\3\2\25\27\5\6\4\2\26\24\3\2\2\2\26\25\3\2\2\2\27\32\3\2\2\2\30\26"+
		"\3\2\2\2\30\31\3\2\2\2\31\33\3\2\2\2\32\30\3\2\2\2\33\34\7\2\2\3\34\3"+
		"\3\2\2\2\35\36\7\3\2\2\36\37\5\n\6\2\37!\5\f\7\2 \"\7\t\2\2! \3\2\2\2"+
		"!\"\3\2\2\2\"$\3\2\2\2#%\7\n\2\2$#\3\2\2\2$%\3\2\2\2%&\3\2\2\2&\'\7\4"+
		"\2\2\'\5\3\2\2\2()\7\5\2\2)*\5\n\6\2*+\5\16\b\2+,\7\4\2\2,\7\3\2\2\2-"+
		".\t\2\2\2.\t\3\2\2\2/\63\5\b\5\2\60\62\5\20\t\2\61\60\3\2\2\2\62\65\3"+
		"\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64@\3\2\2\2\65\63\3\2\2\2\66\67\7\6"+
		"\2\2\67;\5\b\5\28:\5\20\t\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<?"+
		"\3\2\2\2=;\3\2\2\2>\66\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3\2\2\2A\13\3\2\2"+
		"\2B@\3\2\2\2CD\5\b\5\2D\r\3\2\2\2EF\5\22\n\2FG\7\13\2\2G\17\3\2\2\2HI"+
		"\7\7\2\2IJ\7\16\2\2JK\7\b\2\2K\21\3\2\2\2LM\t\3\2\2M\23\3\2\2\2\t\26\30"+
		"!$\63;@";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}