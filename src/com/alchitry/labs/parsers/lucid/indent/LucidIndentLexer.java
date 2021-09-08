// Generated from /home/justin/IdeaProjects/Alchitry Labs/source/src/com/alchitry/labs/parsers/lucid/indent/LucidIndent.g4 by ANTLR 4.9.1

package com.alchitry.labs.parsers.lucid.indent;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LucidIndentLexer extends Lexer {
	static {
		RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
			T__9 = 10, T__10 = 11, T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, BLOCK_COMMENT = 17,
			COMMENT = 18, WS = 19, STUFF = 20;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8",
				"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "BLOCK_COMMENT",
				"COMMENT", "WS", "STUFF"
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


	public LucidIndentLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "LucidIndent.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\26\u008b\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\21\3\21\3\22"+
		"\3\22\3\22\3\22\7\22h\n\22\f\22\16\22k\13\22\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\23\7\23t\n\23\f\23\16\23w\13\23\3\23\5\23z\n\23\3\23\3\23\3\23"+
		"\3\23\3\24\6\24\u0081\n\24\r\24\16\24\u0082\3\24\3\24\3\25\6\25\u0088"+
		"\n\25\r\25\16\25\u0089\3i\2\26\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26\3\2\5\4\2\f\f"+
		"\17\17\5\2\13\f\17\17\"\"\13\2\13\f\17\17\"\"*+<=]]__}}\177\177\2\u008f"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\3+\3\2\2\2\5\62\3\2\2\2\79\3\2"+
		"\2\2\t;\3\2\2\2\13B\3\2\2\2\rD\3\2\2\2\17F\3\2\2\2\21H\3\2\2\2\23J\3\2"+
		"\2\2\25L\3\2\2\2\27N\3\2\2\2\31Q\3\2\2\2\33U\3\2\2\2\35Z\3\2\2\2\37_\3"+
		"\2\2\2!a\3\2\2\2#c\3\2\2\2%o\3\2\2\2\'\u0080\3\2\2\2)\u0087\3\2\2\2+,"+
		"\7o\2\2,-\7q\2\2-.\7f\2\2./\7w\2\2/\60\7n\2\2\60\61\7g\2\2\61\4\3\2\2"+
		"\2\62\63\7i\2\2\63\64\7n\2\2\64\65\7q\2\2\65\66\7d\2\2\66\67\7c\2\2\67"+
		"8\7n\2\28\6\3\2\2\29:\7<\2\2:\b\3\2\2\2;<\7c\2\2<=\7n\2\2=>\7y\2\2>?\7"+
		"c\2\2?@\7{\2\2@A\7u\2\2A\n\3\2\2\2BC\7}\2\2C\f\3\2\2\2DE\7\177\2\2E\16"+
		"\3\2\2\2FG\7*\2\2G\20\3\2\2\2HI\7+\2\2I\22\3\2\2\2JK\7]\2\2K\24\3\2\2"+
		"\2LM\7_\2\2M\26\3\2\2\2NO\7k\2\2OP\7h\2\2P\30\3\2\2\2QR\7h\2\2RS\7q\2"+
		"\2ST\7t\2\2T\32\3\2\2\2UV\7e\2\2VW\7c\2\2WX\7u\2\2XY\7g\2\2Y\34\3\2\2"+
		"\2Z[\7g\2\2[\\\7n\2\2\\]\7u\2\2]^\7g\2\2^\36\3\2\2\2_`\7=\2\2` \3\2\2"+
		"\2ab\7^\2\2b\"\3\2\2\2cd\7\61\2\2de\7,\2\2ei\3\2\2\2fh\13\2\2\2gf\3\2"+
		"\2\2hk\3\2\2\2ij\3\2\2\2ig\3\2\2\2jl\3\2\2\2ki\3\2\2\2lm\7,\2\2mn\7\61"+
		"\2\2n$\3\2\2\2op\7\61\2\2pq\7\61\2\2qu\3\2\2\2rt\n\2\2\2sr\3\2\2\2tw\3"+
		"\2\2\2us\3\2\2\2uv\3\2\2\2vy\3\2\2\2wu\3\2\2\2xz\7\17\2\2yx\3\2\2\2yz"+
		"\3\2\2\2z{\3\2\2\2{|\7\f\2\2|}\3\2\2\2}~\b\23\2\2~&\3\2\2\2\177\u0081"+
		"\t\3\2\2\u0080\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0080\3\2\2\2\u0082"+
		"\u0083\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\b\24\3\2\u0085(\3\2\2\2"+
		"\u0086\u0088\n\4\2\2\u0087\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087"+
		"\3\2\2\2\u0089\u008a\3\2\2\2\u008a*\3\2\2\2\b\2iuy\u0082\u0089\4\2\3\2"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}