// Generated from Lucid.g4 by ANTLR 4.9.3

package com.alchitry.labs.parsers.lucid.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LucidLexer extends Lexer {
	static {
		RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
			T__9 = 10, T__10 = 11, T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, T__16 = 17,
			T__17 = 18, T__18 = 19, T__19 = 20, T__20 = 21, T__21 = 22, T__22 = 23, T__23 = 24,
			T__24 = 25, T__25 = 26, T__26 = 27, T__27 = 28, T__28 = 29, T__29 = 30, T__30 = 31,
			T__31 = 32, T__32 = 33, T__33 = 34, T__34 = 35, T__35 = 36, T__36 = 37, T__37 = 38,
			T__38 = 39, T__39 = 40, T__40 = 41, T__41 = 42, T__42 = 43, T__43 = 44, T__44 = 45,
			T__45 = 46, T__46 = 47, T__47 = 48, T__48 = 49, T__49 = 50, T__50 = 51, T__51 = 52,
			T__52 = 53, T__53 = 54, T__54 = 55, T__55 = 56, HEX = 57, BIN = 58, DEC = 59, REAL = 60,
			INT = 61, STRING = 62, SIGNED = 63, TYPE_ID = 64, CONST_ID = 65, SPACE_ID = 66, FUNCTION_ID = 67,
			BLOCK_COMMENT = 68, COMMENT = 69, WS = 70;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8",
				"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16",
				"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24",
				"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32",
				"T__33", "T__34", "T__35", "T__36", "T__37", "T__38", "T__39", "T__40",
				"T__41", "T__42", "T__43", "T__44", "T__45", "T__46", "T__47", "T__48",
				"T__49", "T__50", "T__51", "T__52", "T__53", "T__54", "T__55", "HEX",
				"BIN", "DEC", "REAL", "INT", "STRING", "SIGNED", "TYPE_ID", "CONST_ID",
				"SPACE_ID", "FUNCTION_ID", "BLOCK_COMMENT", "COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, "'global'", "'{'", "'}'", "';'", "'module'", "'#('", "','", "')'",
				"'('", "':'", "'input'", "'output'", "'inout'", "'='", "'['", "']'",
				"'<'", "'.'", "'>'", "'const'", "'#'", "'var'", "'sig'", "'dff'", "'fsm'",
				"'struct'", "'always'", "'+'", "'-'", "'case'", "'default'", "'if'",
				"'else'", "'for'", "'c{'", "'x{'", "'~'", "'!'", "'*'", "'/'", "'>>'",
				"'<<'", "'<<<'", "'>>>'", "'|'", "'&'", "'^'", "'=='", "'!='", "'>='",
				"'<='", "'||'", "'&&'", "'?'", "'++'", "'--'", null, null, null, null,
				null, null, "'signed'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[]{
				null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, "HEX", "BIN", "DEC",
				"REAL", "INT", "STRING", "SIGNED", "TYPE_ID", "CONST_ID", "SPACE_ID",
				"FUNCTION_ID", "BLOCK_COMMENT", "COMMENT", "WS"
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


	public LucidLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Lucid.g4"; }

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

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
			case 56:
				return HEX_sempred((RuleContext) _localctx, predIndex);
			case 57:
				return BIN_sempred((RuleContext) _localctx, predIndex);
			case 63:
				return TYPE_ID_sempred((RuleContext) _localctx, predIndex);
			case 65:
				return SPACE_ID_sempred((RuleContext) _localctx, predIndex);
		}
		return true;
	}
	private boolean HEX_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return _input.LA(1) != '{';
		}
		return true;
	}
	private boolean BIN_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return _input.LA(1) != '{';
		}
		return true;
	}
	private boolean TYPE_ID_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return _input.LA(1) != '{';
		}
		return true;
	}
	private boolean SPACE_ID_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return _input.LA(1) != '{';
		}
		return true;
	}

	public static final String _serializedATN =
			"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2H\u01f5\b\1\4\2\t" +
					"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
					"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
					"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
					"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
					"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
					",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
					"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=" +
					"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\3\2\3\2\3" +
					"\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6" +
					"\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f" +
					"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3" +
					"\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25\3" +
					"\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3" +
					"\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3" +
					"\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3\37\3" +
					"\37\3 \3 \3 \3 \3 \3 \3 \3 \3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3" +
					"$\3$\3$\3%\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3*\3+\3+\3+\3,\3,\3," +
					"\3,\3-\3-\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3" +
					"\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65\3\66\3\66\3\66\3\67\3\67\3" +
					"8\38\38\39\39\39\3:\3:\7:\u014f\n:\f:\16:\u0152\13:\5:\u0154\n:\3:\3:" +
					"\3:\3:\6:\u015a\n:\r:\16:\u015b\3;\3;\7;\u0160\n;\f;\16;\u0163\13;\5;" +
					"\u0165\n;\3;\3;\3;\3;\6;\u016b\n;\r;\16;\u016c\3<\3<\7<\u0171\n<\f<\16" +
					"<\u0174\13<\5<\u0176\n<\3<\3<\6<\u017a\n<\r<\16<\u017b\3=\5=\u017f\n=" +
					"\3=\7=\u0182\n=\f=\16=\u0185\13=\3=\3=\6=\u0189\n=\r=\16=\u018a\3=\5=" +
					"\u018e\n=\3=\6=\u0191\n=\r=\16=\u0192\3=\3=\7=\u0197\n=\f=\16=\u019a\13" +
					"=\5=\u019c\n=\3>\6>\u019f\n>\r>\16>\u01a0\3?\3?\3?\3?\7?\u01a7\n?\f?\16" +
					"?\u01aa\13?\3?\3?\3@\3@\3@\3@\3@\3@\3@\3A\3A\3A\3A\7A\u01b9\nA\fA\16A" +
					"\u01bc\13A\3B\3B\7B\u01c0\nB\fB\16B\u01c3\13B\3C\3C\3C\3C\7C\u01c9\nC" +
					"\fC\16C\u01cc\13C\3D\3D\3D\7D\u01d1\nD\fD\16D\u01d4\13D\3E\3E\3E\3E\7" +
					"E\u01da\nE\fE\16E\u01dd\13E\3E\3E\3E\3E\3E\3F\3F\3F\3F\7F\u01e8\nF\fF" +
					"\16F\u01eb\13F\3F\3F\3G\6G\u01f0\nG\rG\16G\u01f1\3G\3G\3\u01db\2H\3\3" +
					"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21" +
					"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!" +
					"A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s" +
					";u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089F\u008bG\u008dH\3\2" +
					"\16\3\2\63;\3\2\62;\b\2\62;CHZZ\\\\ch||\6\2\62\63ZZ\\\\||\4\2\f\f\17\17" +
					"\6\2\f\f\17\17$$^^\3\2c|\7\2\62;C\\aacy{|\3\2C\\\5\2\62;C\\aa\6\2\62;" +
					"C\\aac|\5\2\13\f\17\17\"\"\2\u0212\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2" +
					"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3" +
					"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2" +
					"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2" +
					"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2" +
					"\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2" +
					"\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2" +
					"O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3" +
					"\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2" +
					"\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2" +
					"u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2" +
					"\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089" +
					"\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\3\u008f\3\2\2\2\5\u0096\3\2\2" +
					"\2\7\u0098\3\2\2\2\t\u009a\3\2\2\2\13\u009c\3\2\2\2\r\u00a3\3\2\2\2\17" +
					"\u00a6\3\2\2\2\21\u00a8\3\2\2\2\23\u00aa\3\2\2\2\25\u00ac\3\2\2\2\27\u00ae" +
					"\3\2\2\2\31\u00b4\3\2\2\2\33\u00bb\3\2\2\2\35\u00c1\3\2\2\2\37\u00c3\3" +
					"\2\2\2!\u00c5\3\2\2\2#\u00c7\3\2\2\2%\u00c9\3\2\2\2\'\u00cb\3\2\2\2)\u00cd" +
					"\3\2\2\2+\u00d3\3\2\2\2-\u00d5\3\2\2\2/\u00d9\3\2\2\2\61\u00dd\3\2\2\2" +
					"\63\u00e1\3\2\2\2\65\u00e5\3\2\2\2\67\u00ec\3\2\2\29\u00f3\3\2\2\2;\u00f5" +
					"\3\2\2\2=\u00f7\3\2\2\2?\u00fc\3\2\2\2A\u0104\3\2\2\2C\u0107\3\2\2\2E" +
					"\u010c\3\2\2\2G\u0110\3\2\2\2I\u0113\3\2\2\2K\u0116\3\2\2\2M\u0118\3\2" +
					"\2\2O\u011a\3\2\2\2Q\u011c\3\2\2\2S\u011e\3\2\2\2U\u0121\3\2\2\2W\u0124" +
					"\3\2\2\2Y\u0128\3\2\2\2[\u012c\3\2\2\2]\u012e\3\2\2\2_\u0130\3\2\2\2a" +
					"\u0132\3\2\2\2c\u0135\3\2\2\2e\u0138\3\2\2\2g\u013b\3\2\2\2i\u013e\3\2" +
					"\2\2k\u0141\3\2\2\2m\u0144\3\2\2\2o\u0146\3\2\2\2q\u0149\3\2\2\2s\u0153" +
					"\3\2\2\2u\u0164\3\2\2\2w\u0175\3\2\2\2y\u019b\3\2\2\2{\u019e\3\2\2\2}" +
					"\u01a2\3\2\2\2\177\u01ad\3\2\2\2\u0081\u01b4\3\2\2\2\u0083\u01bd\3\2\2" +
					"\2\u0085\u01c4\3\2\2\2\u0087\u01cd\3\2\2\2\u0089\u01d5\3\2\2\2\u008b\u01e3" +
					"\3\2\2\2\u008d\u01ef\3\2\2\2\u008f\u0090\7i\2\2\u0090\u0091\7n\2\2\u0091" +
					"\u0092\7q\2\2\u0092\u0093\7d\2\2\u0093\u0094\7c\2\2\u0094\u0095\7n\2\2" +
					"\u0095\4\3\2\2\2\u0096\u0097\7}\2\2\u0097\6\3\2\2\2\u0098\u0099\7\177" +
					"\2\2\u0099\b\3\2\2\2\u009a\u009b\7=\2\2\u009b\n\3\2\2\2\u009c\u009d\7" +
					"o\2\2\u009d\u009e\7q\2\2\u009e\u009f\7f\2\2\u009f\u00a0\7w\2\2\u00a0\u00a1" +
					"\7n\2\2\u00a1\u00a2\7g\2\2\u00a2\f\3\2\2\2\u00a3\u00a4\7%\2\2\u00a4\u00a5" +
					"\7*\2\2\u00a5\16\3\2\2\2\u00a6\u00a7\7.\2\2\u00a7\20\3\2\2\2\u00a8\u00a9" +
					"\7+\2\2\u00a9\22\3\2\2\2\u00aa\u00ab\7*\2\2\u00ab\24\3\2\2\2\u00ac\u00ad" +
					"\7<\2\2\u00ad\26\3\2\2\2\u00ae\u00af\7k\2\2\u00af\u00b0\7p\2\2\u00b0\u00b1" +
					"\7r\2\2\u00b1\u00b2\7w\2\2\u00b2\u00b3\7v\2\2\u00b3\30\3\2\2\2\u00b4\u00b5" +
					"\7q\2\2\u00b5\u00b6\7w\2\2\u00b6\u00b7\7v\2\2\u00b7\u00b8\7r\2\2\u00b8" +
					"\u00b9\7w\2\2\u00b9\u00ba\7v\2\2\u00ba\32\3\2\2\2\u00bb\u00bc\7k\2\2\u00bc" +
					"\u00bd\7p\2\2\u00bd\u00be\7q\2\2\u00be\u00bf\7w\2\2\u00bf\u00c0\7v\2\2" +
					"\u00c0\34\3\2\2\2\u00c1\u00c2\7?\2\2\u00c2\36\3\2\2\2\u00c3\u00c4\7]\2" +
					"\2\u00c4 \3\2\2\2\u00c5\u00c6\7_\2\2\u00c6\"\3\2\2\2\u00c7\u00c8\7>\2" +
					"\2\u00c8$\3\2\2\2\u00c9\u00ca\7\60\2\2\u00ca&\3\2\2\2\u00cb\u00cc\7@\2" +
					"\2\u00cc(\3\2\2\2\u00cd\u00ce\7e\2\2\u00ce\u00cf\7q\2\2\u00cf\u00d0\7" +
					"p\2\2\u00d0\u00d1\7u\2\2\u00d1\u00d2\7v\2\2\u00d2*\3\2\2\2\u00d3\u00d4" +
					"\7%\2\2\u00d4,\3\2\2\2\u00d5\u00d6\7x\2\2\u00d6\u00d7\7c\2\2\u00d7\u00d8" +
					"\7t\2\2\u00d8.\3\2\2\2\u00d9\u00da\7u\2\2\u00da\u00db\7k\2\2\u00db\u00dc" +
					"\7i\2\2\u00dc\60\3\2\2\2\u00dd\u00de\7f\2\2\u00de\u00df\7h\2\2\u00df\u00e0" +
					"\7h\2\2\u00e0\62\3\2\2\2\u00e1\u00e2\7h\2\2\u00e2\u00e3\7u\2\2\u00e3\u00e4" +
					"\7o\2\2\u00e4\64\3\2\2\2\u00e5\u00e6\7u\2\2\u00e6\u00e7\7v\2\2\u00e7\u00e8" +
					"\7t\2\2\u00e8\u00e9\7w\2\2\u00e9\u00ea\7e\2\2\u00ea\u00eb\7v\2\2\u00eb" +
					"\66\3\2\2\2\u00ec\u00ed\7c\2\2\u00ed\u00ee\7n\2\2\u00ee\u00ef\7y\2\2\u00ef" +
					"\u00f0\7c\2\2\u00f0\u00f1\7{\2\2\u00f1\u00f2\7u\2\2\u00f28\3\2\2\2\u00f3" +
					"\u00f4\7-\2\2\u00f4:\3\2\2\2\u00f5\u00f6\7/\2\2\u00f6<\3\2\2\2\u00f7\u00f8" +
					"\7e\2\2\u00f8\u00f9\7c\2\2\u00f9\u00fa\7u\2\2\u00fa\u00fb\7g\2\2\u00fb" +
					">\3\2\2\2\u00fc\u00fd\7f\2\2\u00fd\u00fe\7g\2\2\u00fe\u00ff\7h\2\2\u00ff" +
					"\u0100\7c\2\2\u0100\u0101\7w\2\2\u0101\u0102\7n\2\2\u0102\u0103\7v\2\2" +
					"\u0103@\3\2\2\2\u0104\u0105\7k\2\2\u0105\u0106\7h\2\2\u0106B\3\2\2\2\u0107" +
					"\u0108\7g\2\2\u0108\u0109\7n\2\2\u0109\u010a\7u\2\2\u010a\u010b\7g\2\2" +
					"\u010bD\3\2\2\2\u010c\u010d\7h\2\2\u010d\u010e\7q\2\2\u010e\u010f\7t\2" +
					"\2\u010fF\3\2\2\2\u0110\u0111\7e\2\2\u0111\u0112\7}\2\2\u0112H\3\2\2\2" +
					"\u0113\u0114\7z\2\2\u0114\u0115\7}\2\2\u0115J\3\2\2\2\u0116\u0117\7\u0080" +
					"\2\2\u0117L\3\2\2\2\u0118\u0119\7#\2\2\u0119N\3\2\2\2\u011a\u011b\7,\2" +
					"\2\u011bP\3\2\2\2\u011c\u011d\7\61\2\2\u011dR\3\2\2\2\u011e\u011f\7@\2" +
					"\2\u011f\u0120\7@\2\2\u0120T\3\2\2\2\u0121\u0122\7>\2\2\u0122\u0123\7" +
					">\2\2\u0123V\3\2\2\2\u0124\u0125\7>\2\2\u0125\u0126\7>\2\2\u0126\u0127" +
					"\7>\2\2\u0127X\3\2\2\2\u0128\u0129\7@\2\2\u0129\u012a\7@\2\2\u012a\u012b" +
					"\7@\2\2\u012bZ\3\2\2\2\u012c\u012d\7~\2\2\u012d\\\3\2\2\2\u012e\u012f" +
					"\7(\2\2\u012f^\3\2\2\2\u0130\u0131\7`\2\2\u0131`\3\2\2\2\u0132\u0133\7" +
					"?\2\2\u0133\u0134\7?\2\2\u0134b\3\2\2\2\u0135\u0136\7#\2\2\u0136\u0137" +
					"\7?\2\2\u0137d\3\2\2\2\u0138\u0139\7@\2\2\u0139\u013a\7?\2\2\u013af\3" +
					"\2\2\2\u013b\u013c\7>\2\2\u013c\u013d\7?\2\2\u013dh\3\2\2\2\u013e\u013f" +
					"\7~\2\2\u013f\u0140\7~\2\2\u0140j\3\2\2\2\u0141\u0142\7(\2\2\u0142\u0143" +
					"\7(\2\2\u0143l\3\2\2\2\u0144\u0145\7A\2\2\u0145n\3\2\2\2\u0146\u0147\7" +
					"-\2\2\u0147\u0148\7-\2\2\u0148p\3\2\2\2\u0149\u014a\7/\2\2\u014a\u014b" +
					"\7/\2\2\u014br\3\2\2\2\u014c\u0150\t\2\2\2\u014d\u014f\t\3\2\2\u014e\u014d" +
					"\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151" +
					"\u0154\3\2\2\2\u0152\u0150\3\2\2\2\u0153\u014c\3\2\2\2\u0153\u0154\3\2" +
					"\2\2\u0154\u0155\3\2\2\2\u0155\u0159\7j\2\2\u0156\u015a\t\4\2\2\u0157" +
					"\u0158\7z\2\2\u0158\u015a\6:\2\2\u0159\u0156\3\2\2\2\u0159\u0157\3\2\2" +
					"\2\u015a\u015b\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015ct" +
					"\3\2\2\2\u015d\u0161\t\2\2\2\u015e\u0160\t\3\2\2\u015f\u015e\3\2\2\2\u0160" +
					"\u0163\3\2\2\2\u0161\u015f\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0165\3\2" +
					"\2\2\u0163\u0161\3\2\2\2\u0164\u015d\3\2\2\2\u0164\u0165\3\2\2\2\u0165" +
					"\u0166\3\2\2\2\u0166\u016a\7d\2\2\u0167\u016b\t\5\2\2\u0168\u0169\7z\2" +
					"\2\u0169\u016b\6;\3\2\u016a\u0167\3\2\2\2\u016a\u0168\3\2\2\2\u016b\u016c" +
					"\3\2\2\2\u016c\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016dv\3\2\2\2\u016e" +
					"\u0172\t\2\2\2\u016f\u0171\t\3\2\2\u0170\u016f\3\2\2\2\u0171\u0174\3\2" +
					"\2\2\u0172\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0176\3\2\2\2\u0174" +
					"\u0172\3\2\2\2\u0175\u016e\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\3\2" +
					"\2\2\u0177\u0179\7f\2\2\u0178\u017a\t\3\2\2\u0179\u0178\3\2\2\2\u017a" +
					"\u017b\3\2\2\2\u017b\u0179\3\2\2\2\u017b\u017c\3\2\2\2\u017cx\3\2\2\2" +
					"\u017d\u017f\7/\2\2\u017e\u017d\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0183" +
					"\3\2\2\2\u0180\u0182\t\3\2\2\u0181\u0180\3\2\2\2\u0182\u0185\3\2\2\2\u0183" +
					"\u0181\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0186\3\2\2\2\u0185\u0183\3\2" +
					"\2\2\u0186\u0188\7\60\2\2\u0187\u0189\t\3\2\2\u0188\u0187\3\2\2\2\u0189" +
					"\u018a\3\2\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u019c\3\2" +
					"\2\2\u018c\u018e\7/\2\2\u018d\u018c\3\2\2\2\u018d\u018e\3\2\2\2\u018e" +
					"\u0190\3\2\2\2\u018f\u0191\t\3\2\2\u0190\u018f\3\2\2\2\u0191\u0192\3\2" +
					"\2\2\u0192\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0194\3\2\2\2\u0194" +
					"\u0198\7\60\2\2\u0195\u0197\t\3\2\2\u0196\u0195\3\2\2\2\u0197\u019a\3" +
					"\2\2\2\u0198\u0196\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019c\3\2\2\2\u019a" +
					"\u0198\3\2\2\2\u019b\u017e\3\2\2\2\u019b\u018d\3\2\2\2\u019cz\3\2\2\2" +
					"\u019d\u019f\t\3\2\2\u019e\u019d\3\2\2\2\u019f\u01a0\3\2\2\2\u01a0\u019e" +
					"\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1|\3\2\2\2\u01a2\u01a8\7$\2\2\u01a3\u01a4" +
					"\7^\2\2\u01a4\u01a7\n\6\2\2\u01a5\u01a7\n\7\2\2\u01a6\u01a3\3\2\2\2\u01a6" +
					"\u01a5\3\2\2\2\u01a7\u01aa\3\2\2\2\u01a8\u01a6\3\2\2\2\u01a8\u01a9\3\2" +
					"\2\2\u01a9\u01ab\3\2\2\2\u01aa\u01a8\3\2\2\2\u01ab\u01ac\7$\2\2\u01ac" +
					"~\3\2\2\2\u01ad\u01ae\7u\2\2\u01ae\u01af\7k\2\2\u01af\u01b0\7i\2\2\u01b0" +
					"\u01b1\7p\2\2\u01b1\u01b2\7g\2\2\u01b2\u01b3\7f\2\2\u01b3\u0080\3\2\2" +
					"\2\u01b4\u01ba\t\b\2\2\u01b5\u01b9\t\t\2\2\u01b6\u01b7\7z\2\2\u01b7\u01b9" +
					"\6A\4\2\u01b8\u01b5\3\2\2\2\u01b8\u01b6\3\2\2\2\u01b9\u01bc\3\2\2\2\u01ba" +
					"\u01b8\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb\u0082\3\2\2\2\u01bc\u01ba\3\2" +
					"\2\2\u01bd\u01c1\t\n\2\2\u01be\u01c0\t\13\2\2\u01bf\u01be\3\2\2\2\u01c0" +
					"\u01c3\3\2\2\2\u01c1\u01bf\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2\u0084\3\2" +
					"\2\2\u01c3\u01c1\3\2\2\2\u01c4\u01ca\t\n\2\2\u01c5\u01c9\t\t\2\2\u01c6" +
					"\u01c7\7z\2\2\u01c7\u01c9\6C\5\2\u01c8\u01c5\3\2\2\2\u01c8\u01c6\3\2\2" +
					"\2\u01c9\u01cc\3\2\2\2\u01ca\u01c8\3\2\2\2\u01ca\u01cb\3\2\2\2\u01cb\u0086" +
					"\3\2\2\2\u01cc\u01ca\3\2\2\2\u01cd\u01ce\7&\2\2\u01ce\u01d2\t\b\2\2\u01cf" +
					"\u01d1\t\f\2\2\u01d0\u01cf\3\2\2\2\u01d1\u01d4\3\2\2\2\u01d2\u01d0\3\2" +
					"\2\2\u01d2\u01d3\3\2\2\2\u01d3\u0088\3\2\2\2\u01d4\u01d2\3\2\2\2\u01d5" +
					"\u01d6\7\61\2\2\u01d6\u01d7\7,\2\2\u01d7\u01db\3\2\2\2\u01d8\u01da\13" +
					"\2\2\2\u01d9\u01d8\3\2\2\2\u01da\u01dd\3\2\2\2\u01db\u01dc\3\2\2\2\u01db" +
					"\u01d9\3\2\2\2\u01dc\u01de\3\2\2\2\u01dd\u01db\3\2\2\2\u01de\u01df\7," +
					"\2\2\u01df\u01e0\7\61\2\2\u01e0\u01e1\3\2\2\2\u01e1\u01e2\bE\2\2\u01e2" +
					"\u008a\3\2\2\2\u01e3\u01e4\7\61\2\2\u01e4\u01e5\7\61\2\2\u01e5\u01e9\3" +
					"\2\2\2\u01e6\u01e8\n\6\2\2\u01e7\u01e6\3\2\2\2\u01e8\u01eb\3\2\2\2\u01e9" +
					"\u01e7\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01ec\3\2\2\2\u01eb\u01e9\3\2" +
					"\2\2\u01ec\u01ed\bF\2\2\u01ed\u008c\3\2\2\2\u01ee\u01f0\t\r\2\2\u01ef" +
					"\u01ee\3\2\2\2\u01f0\u01f1\3\2\2\2\u01f1\u01ef\3\2\2\2\u01f1\u01f2\3\2" +
					"\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f4\bG\2\2\u01f4\u008e\3\2\2\2!\2\u0150" +
					"\u0153\u0159\u015b\u0161\u0164\u016a\u016c\u0172\u0175\u017b\u017e\u0183" +
					"\u018a\u018d\u0192\u0198\u019b\u01a0\u01a6\u01a8\u01b8\u01ba\u01c1\u01c8" +
					"\u01ca\u01d2\u01db\u01e9\u01f1\3\2\3\2";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}