// Generated from AlchitryConstraints.g4 by ANTLR 4.7.1

package com.alchitry.labs.parsers.constraints;

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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, PULLUP=7, PULLDOWN=8, 
		FREQ_UNIT=9, BASIC_NAME=10, REAL=11, INT=12, BLOCK_COMMENT=13, COMMENT=14, 
		WS=15;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "PULLUP", "PULLDOWN", 
		"FREQ_UNIT", "BASIC_NAME", "REAL", "INT", "BLOCK_COMMENT", "COMMENT", 
		"WS", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
		"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\21\u00fc\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\3\2"+
		"\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n"+
		"\3\n\5\n{\n\n\3\n\3\n\3\n\3\13\3\13\7\13\u0082\n\13\f\13\16\13\u0085\13"+
		"\13\3\f\7\f\u0088\n\f\f\f\16\f\u008b\13\f\3\f\3\f\6\f\u008f\n\f\r\f\16"+
		"\f\u0090\3\f\6\f\u0094\n\f\r\f\16\f\u0095\3\f\3\f\7\f\u009a\n\f\f\f\16"+
		"\f\u009d\13\f\5\f\u009f\n\f\3\r\6\r\u00a2\n\r\r\r\16\r\u00a3\3\16\3\16"+
		"\3\16\3\16\7\16\u00aa\n\16\f\16\16\16\u00ad\13\16\3\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\7\17\u00b6\n\17\f\17\16\17\u00b9\13\17\3\17\5\17\u00bc"+
		"\n\17\3\17\3\17\3\17\3\17\3\20\6\20\u00c3\n\20\r\20\16\20\u00c4\3\20\3"+
		"\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3"+
		"\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3"+
		"\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3"+
		"(\3)\3)\3*\3*\3\u00ab\2+\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21!\2#\2%\2\'\2)\2+\2-\2/\2\61\2\63\2\65\2"+
		"\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2S\2\3\2!\4\2C\\c|\6\2\62;"+
		"C\\aac|\3\2\62;\4\2\f\f\17\17\5\2\13\f\17\17\"\"\4\2CCcc\4\2DDdd\4\2E"+
		"Eee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4"+
		"\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVv"+
		"v\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u00ef\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3"+
		"\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\3U\3\2\2\2\5Y\3\2\2\2\7[\3\2"+
		"\2\2\ta\3\2\2\2\13c\3\2\2\2\re\3\2\2\2\17g\3\2\2\2\21n\3\2\2\2\23z\3\2"+
		"\2\2\25\177\3\2\2\2\27\u009e\3\2\2\2\31\u00a1\3\2\2\2\33\u00a5\3\2\2\2"+
		"\35\u00b1\3\2\2\2\37\u00c2\3\2\2\2!\u00c8\3\2\2\2#\u00ca\3\2\2\2%\u00cc"+
		"\3\2\2\2\'\u00ce\3\2\2\2)\u00d0\3\2\2\2+\u00d2\3\2\2\2-\u00d4\3\2\2\2"+
		"/\u00d6\3\2\2\2\61\u00d8\3\2\2\2\63\u00da\3\2\2\2\65\u00dc\3\2\2\2\67"+
		"\u00de\3\2\2\29\u00e0\3\2\2\2;\u00e2\3\2\2\2=\u00e4\3\2\2\2?\u00e6\3\2"+
		"\2\2A\u00e8\3\2\2\2C\u00ea\3\2\2\2E\u00ec\3\2\2\2G\u00ee\3\2\2\2I\u00f0"+
		"\3\2\2\2K\u00f2\3\2\2\2M\u00f4\3\2\2\2O\u00f6\3\2\2\2Q\u00f8\3\2\2\2S"+
		"\u00fa\3\2\2\2UV\7r\2\2VW\7k\2\2WX\7p\2\2X\4\3\2\2\2YZ\7=\2\2Z\6\3\2\2"+
		"\2[\\\7e\2\2\\]\7n\2\2]^\7q\2\2^_\7e\2\2_`\7m\2\2`\b\3\2\2\2ab\7\60\2"+
		"\2b\n\3\2\2\2cd\7]\2\2d\f\3\2\2\2ef\7_\2\2f\16\3\2\2\2gh\7r\2\2hi\7w\2"+
		"\2ij\7n\2\2jk\7n\2\2kl\7w\2\2lm\7r\2\2m\20\3\2\2\2no\7r\2\2op\7w\2\2p"+
		"q\7n\2\2qr\7n\2\2rs\7f\2\2st\7q\2\2tu\7y\2\2uv\7p\2\2v\22\3\2\2\2w{\5"+
		"9\35\2x{\5\65\33\2y{\5-\27\2zw\3\2\2\2zx\3\2\2\2zy\3\2\2\2z{\3\2\2\2{"+
		"|\3\2\2\2|}\5/\30\2}~\5S*\2~\24\3\2\2\2\177\u0083\t\2\2\2\u0080\u0082"+
		"\t\3\2\2\u0081\u0080\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\26\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0088\t\4\2"+
		"\2\u0087\u0086\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a"+
		"\3\2\2\2\u008a\u008c\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008e\7\60\2\2"+
		"\u008d\u008f\t\4\2\2\u008e\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u008e"+
		"\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u009f\3\2\2\2\u0092\u0094\t\4\2\2\u0093"+
		"\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2"+
		"\2\2\u0096\u0097\3\2\2\2\u0097\u009b\7\60\2\2\u0098\u009a\t\4\2\2\u0099"+
		"\u0098\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2"+
		"\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2\2\2\u009e\u0089\3\2\2\2\u009e"+
		"\u0093\3\2\2\2\u009f\30\3\2\2\2\u00a0\u00a2\t\4\2\2\u00a1\u00a0\3\2\2"+
		"\2\u00a2\u00a3\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\32"+
		"\3\2\2\2\u00a5\u00a6\7\61\2\2\u00a6\u00a7\7,\2\2\u00a7\u00ab\3\2\2\2\u00a8"+
		"\u00aa\13\2\2\2\u00a9\u00a8\3\2\2\2\u00aa\u00ad\3\2\2\2\u00ab\u00ac\3"+
		"\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00ae\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae"+
		"\u00af\7,\2\2\u00af\u00b0\7\61\2\2\u00b0\34\3\2\2\2\u00b1\u00b2\7\61\2"+
		"\2\u00b2\u00b3\7\61\2\2\u00b3\u00b7\3\2\2\2\u00b4\u00b6\n\5\2\2\u00b5"+
		"\u00b4\3\2\2\2\u00b6\u00b9\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2"+
		"\2\2\u00b8\u00bb\3\2\2\2\u00b9\u00b7\3\2\2\2\u00ba\u00bc\7\17\2\2\u00bb"+
		"\u00ba\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00be\7\f"+
		"\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c0\b\17\2\2\u00c0\36\3\2\2\2\u00c1\u00c3"+
		"\t\6\2\2\u00c2\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4"+
		"\u00c5\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c7\b\20\3\2\u00c7 \3\2\2\2"+
		"\u00c8\u00c9\t\7\2\2\u00c9\"\3\2\2\2\u00ca\u00cb\t\b\2\2\u00cb$\3\2\2"+
		"\2\u00cc\u00cd\t\t\2\2\u00cd&\3\2\2\2\u00ce\u00cf\t\n\2\2\u00cf(\3\2\2"+
		"\2\u00d0\u00d1\t\13\2\2\u00d1*\3\2\2\2\u00d2\u00d3\t\f\2\2\u00d3,\3\2"+
		"\2\2\u00d4\u00d5\t\r\2\2\u00d5.\3\2\2\2\u00d6\u00d7\t\16\2\2\u00d7\60"+
		"\3\2\2\2\u00d8\u00d9\t\17\2\2\u00d9\62\3\2\2\2\u00da\u00db\t\20\2\2\u00db"+
		"\64\3\2\2\2\u00dc\u00dd\t\21\2\2\u00dd\66\3\2\2\2\u00de\u00df\t\22\2\2"+
		"\u00df8\3\2\2\2\u00e0\u00e1\t\23\2\2\u00e1:\3\2\2\2\u00e2\u00e3\t\24\2"+
		"\2\u00e3<\3\2\2\2\u00e4\u00e5\t\25\2\2\u00e5>\3\2\2\2\u00e6\u00e7\t\26"+
		"\2\2\u00e7@\3\2\2\2\u00e8\u00e9\t\27\2\2\u00e9B\3\2\2\2\u00ea\u00eb\t"+
		"\30\2\2\u00ebD\3\2\2\2\u00ec\u00ed\t\31\2\2\u00edF\3\2\2\2\u00ee\u00ef"+
		"\t\32\2\2\u00efH\3\2\2\2\u00f0\u00f1\t\33\2\2\u00f1J\3\2\2\2\u00f2\u00f3"+
		"\t\34\2\2\u00f3L\3\2\2\2\u00f4\u00f5\t\35\2\2\u00f5N\3\2\2\2\u00f6\u00f7"+
		"\t\36\2\2\u00f7P\3\2\2\2\u00f8\u00f9\t\37\2\2\u00f9R\3\2\2\2\u00fa\u00fb"+
		"\t \2\2\u00fbT\3\2\2\2\17\2z\u0083\u0089\u0090\u0095\u009b\u009e\u00a3"+
		"\u00ab\u00b7\u00bb\u00c4\4\2\3\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}