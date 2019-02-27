// Generated from AlchitryConstraints.g4 by ANTLR 4.7.1

package com.alchitry.labs.parsers.constraints.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AlchitryConstraintsLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, FREQ_UNIT=8, BASIC_NAME=9, 
		REAL=10, INT=11, BLOCK_COMMENT=12, COMMENT=13, WS=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "FREQ_UNIT", "BASIC_NAME", 
		"REAL", "INT", "BLOCK_COMMENT", "COMMENT", "WS", "A", "B", "C", "D", "E", 
		"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", 
		"T", "U", "V", "W", "X", "Y", "Z"
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


	public AlchitryConstraintsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AlchitryConstraints.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\20\u00f8\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\3\2\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\5\tw\n\t"+
		"\3\t\3\t\3\t\3\n\3\n\7\n~\n\n\f\n\16\n\u0081\13\n\3\13\7\13\u0084\n\13"+
		"\f\13\16\13\u0087\13\13\3\13\3\13\6\13\u008b\n\13\r\13\16\13\u008c\3\13"+
		"\6\13\u0090\n\13\r\13\16\13\u0091\3\13\3\13\7\13\u0096\n\13\f\13\16\13"+
		"\u0099\13\13\5\13\u009b\n\13\3\f\6\f\u009e\n\f\r\f\16\f\u009f\3\r\3\r"+
		"\3\r\3\r\7\r\u00a6\n\r\f\r\16\r\u00a9\13\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\7\16\u00b2\n\16\f\16\16\16\u00b5\13\16\3\16\5\16\u00b8\n\16\3\16"+
		"\3\16\3\16\3\16\3\17\6\17\u00bf\n\17\r\17\16\17\u00c0\3\17\3\17\3\20\3"+
		"\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3"+
		"\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3"+
		"\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3"+
		"(\3)\3)\3\u00a7\2*\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\20\37\2!\2#\2%\2\'\2)\2+\2-\2/\2\61\2\63\2\65\2\67\29"+
		"\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2\3\2!\4\2C\\c|\6\2\62;C\\aac|\3"+
		"\2\62;\4\2\f\f\17\17\5\2\13\f\17\17\"\"\4\2CCcc\4\2DDdd\4\2EEee\4\2FF"+
		"ff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2"+
		"OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4"+
		"\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u00eb\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\3S\3\2\2\2\5W\3\2\2\2\7^\3\2\2\2\tg\3\2\2\2\13i\3\2"+
		"\2\2\ro\3\2\2\2\17q\3\2\2\2\21v\3\2\2\2\23{\3\2\2\2\25\u009a\3\2\2\2\27"+
		"\u009d\3\2\2\2\31\u00a1\3\2\2\2\33\u00ad\3\2\2\2\35\u00be\3\2\2\2\37\u00c4"+
		"\3\2\2\2!\u00c6\3\2\2\2#\u00c8\3\2\2\2%\u00ca\3\2\2\2\'\u00cc\3\2\2\2"+
		")\u00ce\3\2\2\2+\u00d0\3\2\2\2-\u00d2\3\2\2\2/\u00d4\3\2\2\2\61\u00d6"+
		"\3\2\2\2\63\u00d8\3\2\2\2\65\u00da\3\2\2\2\67\u00dc\3\2\2\29\u00de\3\2"+
		"\2\2;\u00e0\3\2\2\2=\u00e2\3\2\2\2?\u00e4\3\2\2\2A\u00e6\3\2\2\2C\u00e8"+
		"\3\2\2\2E\u00ea\3\2\2\2G\u00ec\3\2\2\2I\u00ee\3\2\2\2K\u00f0\3\2\2\2M"+
		"\u00f2\3\2\2\2O\u00f4\3\2\2\2Q\u00f6\3\2\2\2ST\7r\2\2TU\7k\2\2UV\7p\2"+
		"\2V\4\3\2\2\2WX\7r\2\2XY\7w\2\2YZ\7n\2\2Z[\7n\2\2[\\\7w\2\2\\]\7r\2\2"+
		"]\6\3\2\2\2^_\7r\2\2_`\7w\2\2`a\7n\2\2ab\7n\2\2bc\7f\2\2cd\7q\2\2de\7"+
		"y\2\2ef\7p\2\2f\b\3\2\2\2gh\7=\2\2h\n\3\2\2\2ij\7e\2\2jk\7n\2\2kl\7q\2"+
		"\2lm\7e\2\2mn\7m\2\2n\f\3\2\2\2op\7]\2\2p\16\3\2\2\2qr\7_\2\2r\20\3\2"+
		"\2\2sw\5\67\34\2tw\5\63\32\2uw\5+\26\2vs\3\2\2\2vt\3\2\2\2vu\3\2\2\2v"+
		"w\3\2\2\2wx\3\2\2\2xy\5-\27\2yz\5Q)\2z\22\3\2\2\2{\177\t\2\2\2|~\t\3\2"+
		"\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080\3\2\2\2\u0080\24\3"+
		"\2\2\2\u0081\177\3\2\2\2\u0082\u0084\t\4\2\2\u0083\u0082\3\2\2\2\u0084"+
		"\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0088\3\2"+
		"\2\2\u0087\u0085\3\2\2\2\u0088\u008a\7\60\2\2\u0089\u008b\t\4\2\2\u008a"+
		"\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2"+
		"\2\2\u008d\u009b\3\2\2\2\u008e\u0090\t\4\2\2\u008f\u008e\3\2\2\2\u0090"+
		"\u0091\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0093\3\2"+
		"\2\2\u0093\u0097\7\60\2\2\u0094\u0096\t\4\2\2\u0095\u0094\3\2\2\2\u0096"+
		"\u0099\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u009b\3\2"+
		"\2\2\u0099\u0097\3\2\2\2\u009a\u0085\3\2\2\2\u009a\u008f\3\2\2\2\u009b"+
		"\26\3\2\2\2\u009c\u009e\t\4\2\2\u009d\u009c\3\2\2\2\u009e\u009f\3\2\2"+
		"\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\30\3\2\2\2\u00a1\u00a2"+
		"\7\61\2\2\u00a2\u00a3\7,\2\2\u00a3\u00a7\3\2\2\2\u00a4\u00a6\13\2\2\2"+
		"\u00a5\u00a4\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a7\u00a5"+
		"\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ab\7,\2\2\u00ab"+
		"\u00ac\7\61\2\2\u00ac\32\3\2\2\2\u00ad\u00ae\7\61\2\2\u00ae\u00af\7\61"+
		"\2\2\u00af\u00b3\3\2\2\2\u00b0\u00b2\n\5\2\2\u00b1\u00b0\3\2\2\2\u00b2"+
		"\u00b5\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b7\3\2"+
		"\2\2\u00b5\u00b3\3\2\2\2\u00b6\u00b8\7\17\2\2\u00b7\u00b6\3\2\2\2\u00b7"+
		"\u00b8\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00ba\7\f\2\2\u00ba\u00bb\3\2"+
		"\2\2\u00bb\u00bc\b\16\2\2\u00bc\34\3\2\2\2\u00bd\u00bf\t\6\2\2\u00be\u00bd"+
		"\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00c2\3\2\2\2\u00c2\u00c3\b\17\3\2\u00c3\36\3\2\2\2\u00c4\u00c5\t\7\2"+
		"\2\u00c5 \3\2\2\2\u00c6\u00c7\t\b\2\2\u00c7\"\3\2\2\2\u00c8\u00c9\t\t"+
		"\2\2\u00c9$\3\2\2\2\u00ca\u00cb\t\n\2\2\u00cb&\3\2\2\2\u00cc\u00cd\t\13"+
		"\2\2\u00cd(\3\2\2\2\u00ce\u00cf\t\f\2\2\u00cf*\3\2\2\2\u00d0\u00d1\t\r"+
		"\2\2\u00d1,\3\2\2\2\u00d2\u00d3\t\16\2\2\u00d3.\3\2\2\2\u00d4\u00d5\t"+
		"\17\2\2\u00d5\60\3\2\2\2\u00d6\u00d7\t\20\2\2\u00d7\62\3\2\2\2\u00d8\u00d9"+
		"\t\21\2\2\u00d9\64\3\2\2\2\u00da\u00db\t\22\2\2\u00db\66\3\2\2\2\u00dc"+
		"\u00dd\t\23\2\2\u00dd8\3\2\2\2\u00de\u00df\t\24\2\2\u00df:\3\2\2\2\u00e0"+
		"\u00e1\t\25\2\2\u00e1<\3\2\2\2\u00e2\u00e3\t\26\2\2\u00e3>\3\2\2\2\u00e4"+
		"\u00e5\t\27\2\2\u00e5@\3\2\2\2\u00e6\u00e7\t\30\2\2\u00e7B\3\2\2\2\u00e8"+
		"\u00e9\t\31\2\2\u00e9D\3\2\2\2\u00ea\u00eb\t\32\2\2\u00ebF\3\2\2\2\u00ec"+
		"\u00ed\t\33\2\2\u00edH\3\2\2\2\u00ee\u00ef\t\34\2\2\u00efJ\3\2\2\2\u00f0"+
		"\u00f1\t\35\2\2\u00f1L\3\2\2\2\u00f2\u00f3\t\36\2\2\u00f3N\3\2\2\2\u00f4"+
		"\u00f5\t\37\2\2\u00f5P\3\2\2\2\u00f6\u00f7\t \2\2\u00f7R\3\2\2\2\17\2"+
		"v\177\u0085\u008c\u0091\u0097\u009a\u009f\u00a7\u00b3\u00b7\u00c0\4\2"+
		"\3\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}