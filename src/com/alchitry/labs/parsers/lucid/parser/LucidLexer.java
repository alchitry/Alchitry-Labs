// Generated from Lucid.g4 by ANTLR 4.7.2

package com.alchitry.labs.parsers.lucid.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LucidLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
			"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
			"T__33", "T__34", "T__35", "T__36", "T__37", "T__38", "T__39", "T__40", 
			"T__41", "T__42", "T__43", "T__44", "T__45", "T__46", "T__47", "T__48", 
			"T__49", "T__50", "T__51", "T__52", "T__53", "T__54", "T__55", "T__56", 
			"T__57", "T__58", "HEX", "BIN", "DEC", "REAL", "INT", "STRING", "SIGNED", 
			"TYPE_ID", "CONST_ID", "SPACE_ID", "FUNCTION_ID", "BLOCK_COMMENT", "COMMENT", 
			"WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'global'", "'{'", "'}'", "';'", "'module'", "'#('", "','", "')'", 
			"'('", "':'", "'input'", "'output'", "'inout'", "'='", "'['", "']'", 
			"'<'", "'.'", "'>'", "'const'", "'#'", "'var'", "'sig'", "'dff'", "'fsm'", 
			"'struct'", "'always'", "'+'", "'-'", "'case'", "'default'", "'if'", 
			"'else'", "'for'", "'c{'", "'x{'", "'~'", "'!'", "'*'", "'/'", "'>>'", 
			"'<<'", "'<<<'", "'>>>'", "'|'", "'&'", "'^'", "'~^'", "'~&'", "'~|'", 
			"'=='", "'!='", "'>='", "'<='", "'||'", "'&&'", "'?'", "'++'", "'--'", 
			null, null, null, null, null, null, "'signed'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"HEX", "BIN", "DEC", "REAL", "INT", "STRING", "SIGNED", "TYPE_ID", "CONST_ID", 
			"SPACE_ID", "FUNCTION_ID", "BLOCK_COMMENT", "COMMENT", "WS"
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
		case 59:
			return HEX_sempred((RuleContext)_localctx, predIndex);
		case 60:
			return BIN_sempred((RuleContext)_localctx, predIndex);
		case 66:
			return TYPE_ID_sempred((RuleContext)_localctx, predIndex);
		case 68:
			return SPACE_ID_sempred((RuleContext)_localctx, predIndex);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2K\u0204\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3"+
		"\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3"+
		"\30\3\30\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3"+
		"\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3"+
		"\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3 \3!\3!\3!\3\"\3\"\3\"\3"+
		"\"\3\"\3#\3#\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3"+
		"*\3+\3+\3+\3,\3,\3,\3,\3-\3-\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\61"+
		"\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65\3\66\3\66"+
		"\3\66\3\67\3\67\3\67\38\38\38\39\39\39\3:\3:\3;\3;\3;\3<\3<\3<\3=\3=\7"+
		"=\u015e\n=\f=\16=\u0161\13=\5=\u0163\n=\3=\3=\3=\3=\6=\u0169\n=\r=\16"+
		"=\u016a\3>\3>\7>\u016f\n>\f>\16>\u0172\13>\5>\u0174\n>\3>\3>\3>\3>\6>"+
		"\u017a\n>\r>\16>\u017b\3?\3?\7?\u0180\n?\f?\16?\u0183\13?\5?\u0185\n?"+
		"\3?\3?\6?\u0189\n?\r?\16?\u018a\3@\5@\u018e\n@\3@\7@\u0191\n@\f@\16@\u0194"+
		"\13@\3@\3@\6@\u0198\n@\r@\16@\u0199\3@\5@\u019d\n@\3@\6@\u01a0\n@\r@\16"+
		"@\u01a1\3@\3@\7@\u01a6\n@\f@\16@\u01a9\13@\5@\u01ab\n@\3A\6A\u01ae\nA"+
		"\rA\16A\u01af\3B\3B\3B\3B\7B\u01b6\nB\fB\16B\u01b9\13B\3B\3B\3C\3C\3C"+
		"\3C\3C\3C\3C\3D\3D\3D\3D\7D\u01c8\nD\fD\16D\u01cb\13D\3E\3E\7E\u01cf\n"+
		"E\fE\16E\u01d2\13E\3F\3F\3F\3F\7F\u01d8\nF\fF\16F\u01db\13F\3G\3G\3G\7"+
		"G\u01e0\nG\fG\16G\u01e3\13G\3H\3H\3H\3H\7H\u01e9\nH\fH\16H\u01ec\13H\3"+
		"H\3H\3H\3H\3H\3I\3I\3I\3I\7I\u01f7\nI\fI\16I\u01fa\13I\3I\3I\3J\6J\u01ff"+
		"\nJ\rJ\16J\u0200\3J\3J\3\u01ea\2K\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31"+
		"\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60"+
		"_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085"+
		"D\u0087E\u0089F\u008bG\u008dH\u008fI\u0091J\u0093K\3\2\16\3\2\63;\3\2"+
		"\62;\b\2\62;CHZZ\\\\ch||\6\2\62\63ZZ\\\\||\4\2\f\f\17\17\6\2\f\f\17\17"+
		"$$^^\3\2c|\7\2\62;C\\aacy{|\3\2C\\\5\2\62;C\\aa\6\2\62;C\\aac|\5\2\13"+
		"\f\17\17\"\"\2\u0221\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"+
		"\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2"+
		"\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2"+
		"\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2"+
		"\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q"+
		"\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2"+
		"\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2"+
		"\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w"+
		"\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2"+
		"\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\3\u0095\3\2\2\2\5\u009c\3\2\2\2\7\u009e\3\2\2\2\t\u00a0\3\2\2\2\13"+
		"\u00a2\3\2\2\2\r\u00a9\3\2\2\2\17\u00ac\3\2\2\2\21\u00ae\3\2\2\2\23\u00b0"+
		"\3\2\2\2\25\u00b2\3\2\2\2\27\u00b4\3\2\2\2\31\u00ba\3\2\2\2\33\u00c1\3"+
		"\2\2\2\35\u00c7\3\2\2\2\37\u00c9\3\2\2\2!\u00cb\3\2\2\2#\u00cd\3\2\2\2"+
		"%\u00cf\3\2\2\2\'\u00d1\3\2\2\2)\u00d3\3\2\2\2+\u00d9\3\2\2\2-\u00db\3"+
		"\2\2\2/\u00df\3\2\2\2\61\u00e3\3\2\2\2\63\u00e7\3\2\2\2\65\u00eb\3\2\2"+
		"\2\67\u00f2\3\2\2\29\u00f9\3\2\2\2;\u00fb\3\2\2\2=\u00fd\3\2\2\2?\u0102"+
		"\3\2\2\2A\u010a\3\2\2\2C\u010d\3\2\2\2E\u0112\3\2\2\2G\u0116\3\2\2\2I"+
		"\u0119\3\2\2\2K\u011c\3\2\2\2M\u011e\3\2\2\2O\u0120\3\2\2\2Q\u0122\3\2"+
		"\2\2S\u0124\3\2\2\2U\u0127\3\2\2\2W\u012a\3\2\2\2Y\u012e\3\2\2\2[\u0132"+
		"\3\2\2\2]\u0134\3\2\2\2_\u0136\3\2\2\2a\u0138\3\2\2\2c\u013b\3\2\2\2e"+
		"\u013e\3\2\2\2g\u0141\3\2\2\2i\u0144\3\2\2\2k\u0147\3\2\2\2m\u014a\3\2"+
		"\2\2o\u014d\3\2\2\2q\u0150\3\2\2\2s\u0153\3\2\2\2u\u0155\3\2\2\2w\u0158"+
		"\3\2\2\2y\u0162\3\2\2\2{\u0173\3\2\2\2}\u0184\3\2\2\2\177\u01aa\3\2\2"+
		"\2\u0081\u01ad\3\2\2\2\u0083\u01b1\3\2\2\2\u0085\u01bc\3\2\2\2\u0087\u01c3"+
		"\3\2\2\2\u0089\u01cc\3\2\2\2\u008b\u01d3\3\2\2\2\u008d\u01dc\3\2\2\2\u008f"+
		"\u01e4\3\2\2\2\u0091\u01f2\3\2\2\2\u0093\u01fe\3\2\2\2\u0095\u0096\7i"+
		"\2\2\u0096\u0097\7n\2\2\u0097\u0098\7q\2\2\u0098\u0099\7d\2\2\u0099\u009a"+
		"\7c\2\2\u009a\u009b\7n\2\2\u009b\4\3\2\2\2\u009c\u009d\7}\2\2\u009d\6"+
		"\3\2\2\2\u009e\u009f\7\177\2\2\u009f\b\3\2\2\2\u00a0\u00a1\7=\2\2\u00a1"+
		"\n\3\2\2\2\u00a2\u00a3\7o\2\2\u00a3\u00a4\7q\2\2\u00a4\u00a5\7f\2\2\u00a5"+
		"\u00a6\7w\2\2\u00a6\u00a7\7n\2\2\u00a7\u00a8\7g\2\2\u00a8\f\3\2\2\2\u00a9"+
		"\u00aa\7%\2\2\u00aa\u00ab\7*\2\2\u00ab\16\3\2\2\2\u00ac\u00ad\7.\2\2\u00ad"+
		"\20\3\2\2\2\u00ae\u00af\7+\2\2\u00af\22\3\2\2\2\u00b0\u00b1\7*\2\2\u00b1"+
		"\24\3\2\2\2\u00b2\u00b3\7<\2\2\u00b3\26\3\2\2\2\u00b4\u00b5\7k\2\2\u00b5"+
		"\u00b6\7p\2\2\u00b6\u00b7\7r\2\2\u00b7\u00b8\7w\2\2\u00b8\u00b9\7v\2\2"+
		"\u00b9\30\3\2\2\2\u00ba\u00bb\7q\2\2\u00bb\u00bc\7w\2\2\u00bc\u00bd\7"+
		"v\2\2\u00bd\u00be\7r\2\2\u00be\u00bf\7w\2\2\u00bf\u00c0\7v\2\2\u00c0\32"+
		"\3\2\2\2\u00c1\u00c2\7k\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4\7q\2\2\u00c4"+
		"\u00c5\7w\2\2\u00c5\u00c6\7v\2\2\u00c6\34\3\2\2\2\u00c7\u00c8\7?\2\2\u00c8"+
		"\36\3\2\2\2\u00c9\u00ca\7]\2\2\u00ca \3\2\2\2\u00cb\u00cc\7_\2\2\u00cc"+
		"\"\3\2\2\2\u00cd\u00ce\7>\2\2\u00ce$\3\2\2\2\u00cf\u00d0\7\60\2\2\u00d0"+
		"&\3\2\2\2\u00d1\u00d2\7@\2\2\u00d2(\3\2\2\2\u00d3\u00d4\7e\2\2\u00d4\u00d5"+
		"\7q\2\2\u00d5\u00d6\7p\2\2\u00d6\u00d7\7u\2\2\u00d7\u00d8\7v\2\2\u00d8"+
		"*\3\2\2\2\u00d9\u00da\7%\2\2\u00da,\3\2\2\2\u00db\u00dc\7x\2\2\u00dc\u00dd"+
		"\7c\2\2\u00dd\u00de\7t\2\2\u00de.\3\2\2\2\u00df\u00e0\7u\2\2\u00e0\u00e1"+
		"\7k\2\2\u00e1\u00e2\7i\2\2\u00e2\60\3\2\2\2\u00e3\u00e4\7f\2\2\u00e4\u00e5"+
		"\7h\2\2\u00e5\u00e6\7h\2\2\u00e6\62\3\2\2\2\u00e7\u00e8\7h\2\2\u00e8\u00e9"+
		"\7u\2\2\u00e9\u00ea\7o\2\2\u00ea\64\3\2\2\2\u00eb\u00ec\7u\2\2\u00ec\u00ed"+
		"\7v\2\2\u00ed\u00ee\7t\2\2\u00ee\u00ef\7w\2\2\u00ef\u00f0\7e\2\2\u00f0"+
		"\u00f1\7v\2\2\u00f1\66\3\2\2\2\u00f2\u00f3\7c\2\2\u00f3\u00f4\7n\2\2\u00f4"+
		"\u00f5\7y\2\2\u00f5\u00f6\7c\2\2\u00f6\u00f7\7{\2\2\u00f7\u00f8\7u\2\2"+
		"\u00f88\3\2\2\2\u00f9\u00fa\7-\2\2\u00fa:\3\2\2\2\u00fb\u00fc\7/\2\2\u00fc"+
		"<\3\2\2\2\u00fd\u00fe\7e\2\2\u00fe\u00ff\7c\2\2\u00ff\u0100\7u\2\2\u0100"+
		"\u0101\7g\2\2\u0101>\3\2\2\2\u0102\u0103\7f\2\2\u0103\u0104\7g\2\2\u0104"+
		"\u0105\7h\2\2\u0105\u0106\7c\2\2\u0106\u0107\7w\2\2\u0107\u0108\7n\2\2"+
		"\u0108\u0109\7v\2\2\u0109@\3\2\2\2\u010a\u010b\7k\2\2\u010b\u010c\7h\2"+
		"\2\u010cB\3\2\2\2\u010d\u010e\7g\2\2\u010e\u010f\7n\2\2\u010f\u0110\7"+
		"u\2\2\u0110\u0111\7g\2\2\u0111D\3\2\2\2\u0112\u0113\7h\2\2\u0113\u0114"+
		"\7q\2\2\u0114\u0115\7t\2\2\u0115F\3\2\2\2\u0116\u0117\7e\2\2\u0117\u0118"+
		"\7}\2\2\u0118H\3\2\2\2\u0119\u011a\7z\2\2\u011a\u011b\7}\2\2\u011bJ\3"+
		"\2\2\2\u011c\u011d\7\u0080\2\2\u011dL\3\2\2\2\u011e\u011f\7#\2\2\u011f"+
		"N\3\2\2\2\u0120\u0121\7,\2\2\u0121P\3\2\2\2\u0122\u0123\7\61\2\2\u0123"+
		"R\3\2\2\2\u0124\u0125\7@\2\2\u0125\u0126\7@\2\2\u0126T\3\2\2\2\u0127\u0128"+
		"\7>\2\2\u0128\u0129\7>\2\2\u0129V\3\2\2\2\u012a\u012b\7>\2\2\u012b\u012c"+
		"\7>\2\2\u012c\u012d\7>\2\2\u012dX\3\2\2\2\u012e\u012f\7@\2\2\u012f\u0130"+
		"\7@\2\2\u0130\u0131\7@\2\2\u0131Z\3\2\2\2\u0132\u0133\7~\2\2\u0133\\\3"+
		"\2\2\2\u0134\u0135\7(\2\2\u0135^\3\2\2\2\u0136\u0137\7`\2\2\u0137`\3\2"+
		"\2\2\u0138\u0139\7\u0080\2\2\u0139\u013a\7`\2\2\u013ab\3\2\2\2\u013b\u013c"+
		"\7\u0080\2\2\u013c\u013d\7(\2\2\u013dd\3\2\2\2\u013e\u013f\7\u0080\2\2"+
		"\u013f\u0140\7~\2\2\u0140f\3\2\2\2\u0141\u0142\7?\2\2\u0142\u0143\7?\2"+
		"\2\u0143h\3\2\2\2\u0144\u0145\7#\2\2\u0145\u0146\7?\2\2\u0146j\3\2\2\2"+
		"\u0147\u0148\7@\2\2\u0148\u0149\7?\2\2\u0149l\3\2\2\2\u014a\u014b\7>\2"+
		"\2\u014b\u014c\7?\2\2\u014cn\3\2\2\2\u014d\u014e\7~\2\2\u014e\u014f\7"+
		"~\2\2\u014fp\3\2\2\2\u0150\u0151\7(\2\2\u0151\u0152\7(\2\2\u0152r\3\2"+
		"\2\2\u0153\u0154\7A\2\2\u0154t\3\2\2\2\u0155\u0156\7-\2\2\u0156\u0157"+
		"\7-\2\2\u0157v\3\2\2\2\u0158\u0159\7/\2\2\u0159\u015a\7/\2\2\u015ax\3"+
		"\2\2\2\u015b\u015f\t\2\2\2\u015c\u015e\t\3\2\2\u015d\u015c\3\2\2\2\u015e"+
		"\u0161\3\2\2\2\u015f\u015d\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0163\3\2"+
		"\2\2\u0161\u015f\3\2\2\2\u0162\u015b\3\2\2\2\u0162\u0163\3\2\2\2\u0163"+
		"\u0164\3\2\2\2\u0164\u0168\7j\2\2\u0165\u0169\t\4\2\2\u0166\u0167\7z\2"+
		"\2\u0167\u0169\6=\2\2\u0168\u0165\3\2\2\2\u0168\u0166\3\2\2\2\u0169\u016a"+
		"\3\2\2\2\u016a\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016bz\3\2\2\2\u016c"+
		"\u0170\t\2\2\2\u016d\u016f\t\3\2\2\u016e\u016d\3\2\2\2\u016f\u0172\3\2"+
		"\2\2\u0170\u016e\3\2\2\2\u0170\u0171\3\2\2\2\u0171\u0174\3\2\2\2\u0172"+
		"\u0170\3\2\2\2\u0173\u016c\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0175\3\2"+
		"\2\2\u0175\u0179\7d\2\2\u0176\u017a\t\5\2\2\u0177\u0178\7z\2\2\u0178\u017a"+
		"\6>\3\2\u0179\u0176\3\2\2\2\u0179\u0177\3\2\2\2\u017a\u017b\3\2\2\2\u017b"+
		"\u0179\3\2\2\2\u017b\u017c\3\2\2\2\u017c|\3\2\2\2\u017d\u0181\t\2\2\2"+
		"\u017e\u0180\t\3\2\2\u017f\u017e\3\2\2\2\u0180\u0183\3\2\2\2\u0181\u017f"+
		"\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0185\3\2\2\2\u0183\u0181\3\2\2\2\u0184"+
		"\u017d\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0186\3\2\2\2\u0186\u0188\7f"+
		"\2\2\u0187\u0189\t\3\2\2\u0188\u0187\3\2\2\2\u0189\u018a\3\2\2\2\u018a"+
		"\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b~\3\2\2\2\u018c\u018e\7/\2\2\u018d"+
		"\u018c\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u0192\3\2\2\2\u018f\u0191\t\3"+
		"\2\2\u0190\u018f\3\2\2\2\u0191\u0194\3\2\2\2\u0192\u0190\3\2\2\2\u0192"+
		"\u0193\3\2\2\2\u0193\u0195\3\2\2\2\u0194\u0192\3\2\2\2\u0195\u0197\7\60"+
		"\2\2\u0196\u0198\t\3\2\2\u0197\u0196\3\2\2\2\u0198\u0199\3\2\2\2\u0199"+
		"\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u01ab\3\2\2\2\u019b\u019d\7/"+
		"\2\2\u019c\u019b\3\2\2\2\u019c\u019d\3\2\2\2\u019d\u019f\3\2\2\2\u019e"+
		"\u01a0\t\3\2\2\u019f\u019e\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1\u019f\3\2"+
		"\2\2\u01a1\u01a2\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a7\7\60\2\2\u01a4"+
		"\u01a6\t\3\2\2\u01a5\u01a4\3\2\2\2\u01a6\u01a9\3\2\2\2\u01a7\u01a5\3\2"+
		"\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01ab\3\2\2\2\u01a9\u01a7\3\2\2\2\u01aa"+
		"\u018d\3\2\2\2\u01aa\u019c\3\2\2\2\u01ab\u0080\3\2\2\2\u01ac\u01ae\t\3"+
		"\2\2\u01ad\u01ac\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\u01ad\3\2\2\2\u01af"+
		"\u01b0\3\2\2\2\u01b0\u0082\3\2\2\2\u01b1\u01b7\7$\2\2\u01b2\u01b3\7^\2"+
		"\2\u01b3\u01b6\n\6\2\2\u01b4\u01b6\n\7\2\2\u01b5\u01b2\3\2\2\2\u01b5\u01b4"+
		"\3\2\2\2\u01b6\u01b9\3\2\2\2\u01b7\u01b5\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8"+
		"\u01ba\3\2\2\2\u01b9\u01b7\3\2\2\2\u01ba\u01bb\7$\2\2\u01bb\u0084\3\2"+
		"\2\2\u01bc\u01bd\7u\2\2\u01bd\u01be\7k\2\2\u01be\u01bf\7i\2\2\u01bf\u01c0"+
		"\7p\2\2\u01c0\u01c1\7g\2\2\u01c1\u01c2\7f\2\2\u01c2\u0086\3\2\2\2\u01c3"+
		"\u01c9\t\b\2\2\u01c4\u01c8\t\t\2\2\u01c5\u01c6\7z\2\2\u01c6\u01c8\6D\4"+
		"\2\u01c7\u01c4\3\2\2\2\u01c7\u01c5\3\2\2\2\u01c8\u01cb\3\2\2\2\u01c9\u01c7"+
		"\3\2\2\2\u01c9\u01ca\3\2\2\2\u01ca\u0088\3\2\2\2\u01cb\u01c9\3\2\2\2\u01cc"+
		"\u01d0\t\n\2\2\u01cd\u01cf\t\13\2\2\u01ce\u01cd\3\2\2\2\u01cf\u01d2\3"+
		"\2\2\2\u01d0\u01ce\3\2\2\2\u01d0\u01d1\3\2\2\2\u01d1\u008a\3\2\2\2\u01d2"+
		"\u01d0\3\2\2\2\u01d3\u01d9\t\n\2\2\u01d4\u01d8\t\t\2\2\u01d5\u01d6\7z"+
		"\2\2\u01d6\u01d8\6F\5\2\u01d7\u01d4\3\2\2\2\u01d7\u01d5\3\2\2\2\u01d8"+
		"\u01db\3\2\2\2\u01d9\u01d7\3\2\2\2\u01d9\u01da\3\2\2\2\u01da\u008c\3\2"+
		"\2\2\u01db\u01d9\3\2\2\2\u01dc\u01dd\7&\2\2\u01dd\u01e1\t\b\2\2\u01de"+
		"\u01e0\t\f\2\2\u01df\u01de\3\2\2\2\u01e0\u01e3\3\2\2\2\u01e1\u01df\3\2"+
		"\2\2\u01e1\u01e2\3\2\2\2\u01e2\u008e\3\2\2\2\u01e3\u01e1\3\2\2\2\u01e4"+
		"\u01e5\7\61\2\2\u01e5\u01e6\7,\2\2\u01e6\u01ea\3\2\2\2\u01e7\u01e9\13"+
		"\2\2\2\u01e8\u01e7\3\2\2\2\u01e9\u01ec\3\2\2\2\u01ea\u01eb\3\2\2\2\u01ea"+
		"\u01e8\3\2\2\2\u01eb\u01ed\3\2\2\2\u01ec\u01ea\3\2\2\2\u01ed\u01ee\7,"+
		"\2\2\u01ee\u01ef\7\61\2\2\u01ef\u01f0\3\2\2\2\u01f0\u01f1\bH\2\2\u01f1"+
		"\u0090\3\2\2\2\u01f2\u01f3\7\61\2\2\u01f3\u01f4\7\61\2\2\u01f4\u01f8\3"+
		"\2\2\2\u01f5\u01f7\n\6\2\2\u01f6\u01f5\3\2\2\2\u01f7\u01fa\3\2\2\2\u01f8"+
		"\u01f6\3\2\2\2\u01f8\u01f9\3\2\2\2\u01f9\u01fb\3\2\2\2\u01fa\u01f8\3\2"+
		"\2\2\u01fb\u01fc\bI\2\2\u01fc\u0092\3\2\2\2\u01fd\u01ff\t\r\2\2\u01fe"+
		"\u01fd\3\2\2\2\u01ff\u0200\3\2\2\2\u0200\u01fe\3\2\2\2\u0200\u0201\3\2"+
		"\2\2\u0201\u0202\3\2\2\2\u0202\u0203\bJ\2\2\u0203\u0094\3\2\2\2!\2\u015f"+
		"\u0162\u0168\u016a\u0170\u0173\u0179\u017b\u0181\u0184\u018a\u018d\u0192"+
		"\u0199\u019c\u01a1\u01a7\u01aa\u01af\u01b5\u01b7\u01c7\u01c9\u01d0\u01d7"+
		"\u01d9\u01e1\u01ea\u01f8\u0200\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}