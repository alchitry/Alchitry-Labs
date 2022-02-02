// Generated from Lucid.g4 by ANTLR 4.9.3

package com.alchitry.labs.parsers.lucidv2.grammar;

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
            HEX = 53, BIN = 54, DEC = 55, REAL = 56, INT = 57, STRING = 58, SEMICOLON = 59, NL = 60,
            SIGNED = 61, TYPE_ID = 62, CONST_ID = 63, SPACE_ID = 64, FUNCTION_ID = 65, BLOCK_COMMENT = 66,
            COMMENT = 67, WS = 68;
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
                "T__49", "T__50", "T__51", "HEX", "BIN", "DEC", "REAL", "INT", "STRING",
                "SEMICOLON", "NL", "SIGNED", "TYPE_ID", "CONST_ID", "SPACE_ID", "FUNCTION_ID",
                "BLOCK_COMMENT", "COMMENT", "WS"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'global'", "'{'", "'}'", "'module'", "'#('", "','", "')'", "'('",
                "':'", "'input'", "'output'", "'inout'", "'='", "'['", "']'", "'<'",
                "'.'", "'>'", "'const'", "'#'", "'sig'", "'dff'", "'fsm'", "'struct'",
                "'always'", "'+'", "'-'", "'case'", "'default'", "'if'", "'else'", "'repeat'",
                "'c{'", "'x{'", "'~'", "'!'", "'*'", "'/'", "'>>'", "'<<'", "'<<<'",
                "'>>>'", "'|'", "'&'", "'^'", "'=='", "'!='", "'>='", "'<='", "'||'",
                "'&&'", "'?'", null, null, null, null, null, null, "';'", null, "'signed'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, "HEX", "BIN", "DEC", "REAL", "INT", "STRING",
                "SEMICOLON", "NL", "SIGNED", "TYPE_ID", "CONST_ID", "SPACE_ID", "FUNCTION_ID",
                "BLOCK_COMMENT", "COMMENT", "WS"
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
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Lucid.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    @Override
    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 52:
                return HEX_sempred((RuleContext) _localctx, predIndex);
            case 53:
                return BIN_sempred((RuleContext) _localctx, predIndex);
            case 61:
                return TYPE_ID_sempred((RuleContext) _localctx, predIndex);
            case 63:
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
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2F\u01ef\b\1\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
                    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
                    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=" +
                    "\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\3\2\3\2\3\2\3\2\3\2\3" +
                    "\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7" +
                    "\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f" +
                    "\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21" +
                    "\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26" +
                    "\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31" +
                    "\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\34" +
                    "\3\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36" +
                    "\3\37\3\37\3\37\3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3!\3!\3\"\3\"\3\"\3#\3#" +
                    "\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3*\3*\3+\3+\3+\3" +
                    "+\3,\3,\3-\3-\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3" +
                    "\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\66\3\66\7\66\u0142\n\66" +
                    "\f\66\16\66\u0145\13\66\5\66\u0147\n\66\3\66\3\66\3\66\3\66\6\66\u014d" +
                    "\n\66\r\66\16\66\u014e\3\67\3\67\7\67\u0153\n\67\f\67\16\67\u0156\13\67" +
                    "\5\67\u0158\n\67\3\67\3\67\3\67\3\67\6\67\u015e\n\67\r\67\16\67\u015f" +
                    "\38\38\78\u0164\n8\f8\168\u0167\138\58\u0169\n8\38\38\68\u016d\n8\r8\16" +
                    "8\u016e\39\59\u0172\n9\39\79\u0175\n9\f9\169\u0178\139\39\39\69\u017c" +
                    "\n9\r9\169\u017d\39\59\u0181\n9\39\69\u0184\n9\r9\169\u0185\39\39\79\u018a" +
                    "\n9\f9\169\u018d\139\59\u018f\n9\3:\6:\u0192\n:\r:\16:\u0193\3;\3;\3;" +
                    "\3;\7;\u019a\n;\f;\16;\u019d\13;\3;\3;\3<\3<\3=\3=\3=\5=\u01a6\n=\3>\3" +
                    ">\3>\3>\3>\3>\3>\3?\3?\3?\3?\7?\u01b3\n?\f?\16?\u01b6\13?\3@\3@\7@\u01ba" +
                    "\n@\f@\16@\u01bd\13@\3A\3A\3A\3A\7A\u01c3\nA\fA\16A\u01c6\13A\3B\3B\3" +
                    "B\7B\u01cb\nB\fB\16B\u01ce\13B\3C\3C\3C\3C\7C\u01d4\nC\fC\16C\u01d7\13" +
                    "C\3C\3C\3C\3C\3C\3D\3D\3D\3D\7D\u01e2\nD\fD\16D\u01e5\13D\3D\3D\3E\6E" +
                    "\u01ea\nE\rE\16E\u01eb\3E\3E\3\u01d5\2F\3\3\5\4\7\5\t\6\13\7\r\b\17\t" +
                    "\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27" +
                    "-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W" +
                    "-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083" +
                    "C\u0085D\u0087E\u0089F\3\2\16\3\2\63;\3\2\62;\b\2\62;CHZZ\\\\ch||\6\2" +
                    "\62\63ZZ\\\\||\4\2\f\f\17\17\6\2\f\f\17\17$$^^\3\2c|\7\2\62;C\\aacy{|" +
                    "\3\2C\\\5\2\62;C\\aa\6\2\62;C\\aac|\4\2\13\13\"\"\2\u020d\2\3\3\2\2\2" +
                    "\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2" +
                    "\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2" +
                    "\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2" +
                    "\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2" +
                    "\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2" +
                    "\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2" +
                    "\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W" +
                    "\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2" +
                    "\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2" +
                    "\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}" +
                    "\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2" +
                    "\2\u0087\3\2\2\2\2\u0089\3\2\2\2\3\u008b\3\2\2\2\5\u0092\3\2\2\2\7\u0094" +
                    "\3\2\2\2\t\u0096\3\2\2\2\13\u009d\3\2\2\2\r\u00a0\3\2\2\2\17\u00a2\3\2" +
                    "\2\2\21\u00a4\3\2\2\2\23\u00a6\3\2\2\2\25\u00a8\3\2\2\2\27\u00ae\3\2\2" +
                    "\2\31\u00b5\3\2\2\2\33\u00bb\3\2\2\2\35\u00bd\3\2\2\2\37\u00bf\3\2\2\2" +
                    "!\u00c1\3\2\2\2#\u00c3\3\2\2\2%\u00c5\3\2\2\2\'\u00c7\3\2\2\2)\u00cd\3" +
                    "\2\2\2+\u00cf\3\2\2\2-\u00d3\3\2\2\2/\u00d7\3\2\2\2\61\u00db\3\2\2\2\63" +
                    "\u00e2\3\2\2\2\65\u00e9\3\2\2\2\67\u00eb\3\2\2\29\u00ed\3\2\2\2;\u00f2" +
                    "\3\2\2\2=\u00fa\3\2\2\2?\u00fd\3\2\2\2A\u0102\3\2\2\2C\u0109\3\2\2\2E" +
                    "\u010c\3\2\2\2G\u010f\3\2\2\2I\u0111\3\2\2\2K\u0113\3\2\2\2M\u0115\3\2" +
                    "\2\2O\u0117\3\2\2\2Q\u011a\3\2\2\2S\u011d\3\2\2\2U\u0121\3\2\2\2W\u0125" +
                    "\3\2\2\2Y\u0127\3\2\2\2[\u0129\3\2\2\2]\u012b\3\2\2\2_\u012e\3\2\2\2a" +
                    "\u0131\3\2\2\2c\u0134\3\2\2\2e\u0137\3\2\2\2g\u013a\3\2\2\2i\u013d\3\2" +
                    "\2\2k\u0146\3\2\2\2m\u0157\3\2\2\2o\u0168\3\2\2\2q\u018e\3\2\2\2s\u0191" +
                    "\3\2\2\2u\u0195\3\2\2\2w\u01a0\3\2\2\2y\u01a5\3\2\2\2{\u01a7\3\2\2\2}" +
                    "\u01ae\3\2\2\2\177\u01b7\3\2\2\2\u0081\u01be\3\2\2\2\u0083\u01c7\3\2\2" +
                    "\2\u0085\u01cf\3\2\2\2\u0087\u01dd\3\2\2\2\u0089\u01e9\3\2\2\2\u008b\u008c" +
                    "\7i\2\2\u008c\u008d\7n\2\2\u008d\u008e\7q\2\2\u008e\u008f\7d\2\2\u008f" +
                    "\u0090\7c\2\2\u0090\u0091\7n\2\2\u0091\4\3\2\2\2\u0092\u0093\7}\2\2\u0093" +
                    "\6\3\2\2\2\u0094\u0095\7\177\2\2\u0095\b\3\2\2\2\u0096\u0097\7o\2\2\u0097" +
                    "\u0098\7q\2\2\u0098\u0099\7f\2\2\u0099\u009a\7w\2\2\u009a\u009b\7n\2\2" +
                    "\u009b\u009c\7g\2\2\u009c\n\3\2\2\2\u009d\u009e\7%\2\2\u009e\u009f\7*" +
                    "\2\2\u009f\f\3\2\2\2\u00a0\u00a1\7.\2\2\u00a1\16\3\2\2\2\u00a2\u00a3\7" +
                    "+\2\2\u00a3\20\3\2\2\2\u00a4\u00a5\7*\2\2\u00a5\22\3\2\2\2\u00a6\u00a7" +
                    "\7<\2\2\u00a7\24\3\2\2\2\u00a8\u00a9\7k\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab" +
                    "\7r\2\2\u00ab\u00ac\7w\2\2\u00ac\u00ad\7v\2\2\u00ad\26\3\2\2\2\u00ae\u00af" +
                    "\7q\2\2\u00af\u00b0\7w\2\2\u00b0\u00b1\7v\2\2\u00b1\u00b2\7r\2\2\u00b2" +
                    "\u00b3\7w\2\2\u00b3\u00b4\7v\2\2\u00b4\30\3\2\2\2\u00b5\u00b6\7k\2\2\u00b6" +
                    "\u00b7\7p\2\2\u00b7\u00b8\7q\2\2\u00b8\u00b9\7w\2\2\u00b9\u00ba\7v\2\2" +
                    "\u00ba\32\3\2\2\2\u00bb\u00bc\7?\2\2\u00bc\34\3\2\2\2\u00bd\u00be\7]\2" +
                    "\2\u00be\36\3\2\2\2\u00bf\u00c0\7_\2\2\u00c0 \3\2\2\2\u00c1\u00c2\7>\2" +
                    "\2\u00c2\"\3\2\2\2\u00c3\u00c4\7\60\2\2\u00c4$\3\2\2\2\u00c5\u00c6\7@" +
                    "\2\2\u00c6&\3\2\2\2\u00c7\u00c8\7e\2\2\u00c8\u00c9\7q\2\2\u00c9\u00ca" +
                    "\7p\2\2\u00ca\u00cb\7u\2\2\u00cb\u00cc\7v\2\2\u00cc(\3\2\2\2\u00cd\u00ce" +
                    "\7%\2\2\u00ce*\3\2\2\2\u00cf\u00d0\7u\2\2\u00d0\u00d1\7k\2\2\u00d1\u00d2" +
                    "\7i\2\2\u00d2,\3\2\2\2\u00d3\u00d4\7f\2\2\u00d4\u00d5\7h\2\2\u00d5\u00d6" +
                    "\7h\2\2\u00d6.\3\2\2\2\u00d7\u00d8\7h\2\2\u00d8\u00d9\7u\2\2\u00d9\u00da" +
                    "\7o\2\2\u00da\60\3\2\2\2\u00db\u00dc\7u\2\2\u00dc\u00dd\7v\2\2\u00dd\u00de" +
                    "\7t\2\2\u00de\u00df\7w\2\2\u00df\u00e0\7e\2\2\u00e0\u00e1\7v\2\2\u00e1" +
                    "\62\3\2\2\2\u00e2\u00e3\7c\2\2\u00e3\u00e4\7n\2\2\u00e4\u00e5\7y\2\2\u00e5" +
                    "\u00e6\7c\2\2\u00e6\u00e7\7{\2\2\u00e7\u00e8\7u\2\2\u00e8\64\3\2\2\2\u00e9" +
                    "\u00ea\7-\2\2\u00ea\66\3\2\2\2\u00eb\u00ec\7/\2\2\u00ec8\3\2\2\2\u00ed" +
                    "\u00ee\7e\2\2\u00ee\u00ef\7c\2\2\u00ef\u00f0\7u\2\2\u00f0\u00f1\7g\2\2" +
                    "\u00f1:\3\2\2\2\u00f2\u00f3\7f\2\2\u00f3\u00f4\7g\2\2\u00f4\u00f5\7h\2" +
                    "\2\u00f5\u00f6\7c\2\2\u00f6\u00f7\7w\2\2\u00f7\u00f8\7n\2\2\u00f8\u00f9" +
                    "\7v\2\2\u00f9<\3\2\2\2\u00fa\u00fb\7k\2\2\u00fb\u00fc\7h\2\2\u00fc>\3" +
                    "\2\2\2\u00fd\u00fe\7g\2\2\u00fe\u00ff\7n\2\2\u00ff\u0100\7u\2\2\u0100" +
                    "\u0101\7g\2\2\u0101@\3\2\2\2\u0102\u0103\7t\2\2\u0103\u0104\7g\2\2\u0104" +
                    "\u0105\7r\2\2\u0105\u0106\7g\2\2\u0106\u0107\7c\2\2\u0107\u0108\7v\2\2" +
                    "\u0108B\3\2\2\2\u0109\u010a\7e\2\2\u010a\u010b\7}\2\2\u010bD\3\2\2\2\u010c" +
                    "\u010d\7z\2\2\u010d\u010e\7}\2\2\u010eF\3\2\2\2\u010f\u0110\7\u0080\2" +
                    "\2\u0110H\3\2\2\2\u0111\u0112\7#\2\2\u0112J\3\2\2\2\u0113\u0114\7,\2\2" +
                    "\u0114L\3\2\2\2\u0115\u0116\7\61\2\2\u0116N\3\2\2\2\u0117\u0118\7@\2\2" +
                    "\u0118\u0119\7@\2\2\u0119P\3\2\2\2\u011a\u011b\7>\2\2\u011b\u011c\7>\2" +
                    "\2\u011cR\3\2\2\2\u011d\u011e\7>\2\2\u011e\u011f\7>\2\2\u011f\u0120\7" +
                    ">\2\2\u0120T\3\2\2\2\u0121\u0122\7@\2\2\u0122\u0123\7@\2\2\u0123\u0124" +
                    "\7@\2\2\u0124V\3\2\2\2\u0125\u0126\7~\2\2\u0126X\3\2\2\2\u0127\u0128\7" +
                    "(\2\2\u0128Z\3\2\2\2\u0129\u012a\7`\2\2\u012a\\\3\2\2\2\u012b\u012c\7" +
                    "?\2\2\u012c\u012d\7?\2\2\u012d^\3\2\2\2\u012e\u012f\7#\2\2\u012f\u0130" +
                    "\7?\2\2\u0130`\3\2\2\2\u0131\u0132\7@\2\2\u0132\u0133\7?\2\2\u0133b\3" +
                    "\2\2\2\u0134\u0135\7>\2\2\u0135\u0136\7?\2\2\u0136d\3\2\2\2\u0137\u0138" +
                    "\7~\2\2\u0138\u0139\7~\2\2\u0139f\3\2\2\2\u013a\u013b\7(\2\2\u013b\u013c" +
                    "\7(\2\2\u013ch\3\2\2\2\u013d\u013e\7A\2\2\u013ej\3\2\2\2\u013f\u0143\t" +
                    "\2\2\2\u0140\u0142\t\3\2\2\u0141\u0140\3\2\2\2\u0142\u0145\3\2\2\2\u0143" +
                    "\u0141\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0147\3\2\2\2\u0145\u0143\3\2" +
                    "\2\2\u0146\u013f\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2\2\2\u0148" +
                    "\u014c\7j\2\2\u0149\u014d\t\4\2\2\u014a\u014b\7z\2\2\u014b\u014d\6\66" +
                    "\2\2\u014c\u0149\3\2\2\2\u014c\u014a\3\2\2\2\u014d\u014e\3\2\2\2\u014e" +
                    "\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014fl\3\2\2\2\u0150\u0154\t\2\2\2" +
                    "\u0151\u0153\t\3\2\2\u0152\u0151\3\2\2\2\u0153\u0156\3\2\2\2\u0154\u0152" +
                    "\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u0158\3\2\2\2\u0156\u0154\3\2\2\2\u0157" +
                    "\u0150\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015d\7d" +
                    "\2\2\u015a\u015e\t\5\2\2\u015b\u015c\7z\2\2\u015c\u015e\6\67\3\2\u015d" +
                    "\u015a\3\2\2\2\u015d\u015b\3\2\2\2\u015e\u015f\3\2\2\2\u015f\u015d\3\2" +
                    "\2\2\u015f\u0160\3\2\2\2\u0160n\3\2\2\2\u0161\u0165\t\2\2\2\u0162\u0164" +
                    "\t\3\2\2\u0163\u0162\3\2\2\2\u0164\u0167\3\2\2\2\u0165\u0163\3\2\2\2\u0165" +
                    "\u0166\3\2\2\2\u0166\u0169\3\2\2\2\u0167\u0165\3\2\2\2\u0168\u0161\3\2" +
                    "\2\2\u0168\u0169\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016c\7f\2\2\u016b" +
                    "\u016d\t\3\2\2\u016c\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u016c\3\2" +
                    "\2\2\u016e\u016f\3\2\2\2\u016fp\3\2\2\2\u0170\u0172\7/\2\2\u0171\u0170" +
                    "\3\2\2\2\u0171\u0172\3\2\2\2\u0172\u0176\3\2\2\2\u0173\u0175\t\3\2\2\u0174" +
                    "\u0173\3\2\2\2\u0175\u0178\3\2\2\2\u0176\u0174\3\2\2\2\u0176\u0177\3\2" +
                    "\2\2\u0177\u0179\3\2\2\2\u0178\u0176\3\2\2\2\u0179\u017b\7\60\2\2\u017a" +
                    "\u017c\t\3\2\2\u017b\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017b\3\2" +
                    "\2\2\u017d\u017e\3\2\2\2\u017e\u018f\3\2\2\2\u017f\u0181\7/\2\2\u0180" +
                    "\u017f\3\2\2\2\u0180\u0181\3\2\2\2\u0181\u0183\3\2\2\2\u0182\u0184\t\3" +
                    "\2\2\u0183\u0182\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0183\3\2\2\2\u0185" +
                    "\u0186\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u018b\7\60\2\2\u0188\u018a\t" +
                    "\3\2\2\u0189\u0188\3\2\2\2\u018a\u018d\3\2\2\2\u018b\u0189\3\2\2\2\u018b" +
                    "\u018c\3\2\2\2\u018c\u018f\3\2\2\2\u018d\u018b\3\2\2\2\u018e\u0171\3\2" +
                    "\2\2\u018e\u0180\3\2\2\2\u018fr\3\2\2\2\u0190\u0192\t\3\2\2\u0191\u0190" +
                    "\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0191\3\2\2\2\u0193\u0194\3\2\2\2\u0194" +
                    "t\3\2\2\2\u0195\u019b\7$\2\2\u0196\u0197\7^\2\2\u0197\u019a\n\6\2\2\u0198" +
                    "\u019a\n\7\2\2\u0199\u0196\3\2\2\2\u0199\u0198\3\2\2\2\u019a\u019d\3\2" +
                    "\2\2\u019b\u0199\3\2\2\2\u019b\u019c\3\2\2\2\u019c\u019e\3\2\2\2\u019d" +
                    "\u019b\3\2\2\2\u019e\u019f\7$\2\2\u019fv\3\2\2\2\u01a0\u01a1\7=\2\2\u01a1" +
                    "x\3\2\2\2\u01a2\u01a3\7\17\2\2\u01a3\u01a6\7\f\2\2\u01a4\u01a6\t\6\2\2" +
                    "\u01a5\u01a2\3\2\2\2\u01a5\u01a4\3\2\2\2\u01a6z\3\2\2\2\u01a7\u01a8\7" +
                    "u\2\2\u01a8\u01a9\7k\2\2\u01a9\u01aa\7i\2\2\u01aa\u01ab\7p\2\2\u01ab\u01ac" +
                    "\7g\2\2\u01ac\u01ad\7f\2\2\u01ad|\3\2\2\2\u01ae\u01b4\t\b\2\2\u01af\u01b3" +
                    "\t\t\2\2\u01b0\u01b1\7z\2\2\u01b1\u01b3\6?\4\2\u01b2\u01af\3\2\2\2\u01b2" +
                    "\u01b0\3\2\2\2\u01b3\u01b6\3\2\2\2\u01b4\u01b2\3\2\2\2\u01b4\u01b5\3\2" +
                    "\2\2\u01b5~\3\2\2\2\u01b6\u01b4\3\2\2\2\u01b7\u01bb\t\n\2\2\u01b8\u01ba" +
                    "\t\13\2\2\u01b9\u01b8\3\2\2\2\u01ba\u01bd\3\2\2\2\u01bb\u01b9\3\2\2\2" +
                    "\u01bb\u01bc\3\2\2\2\u01bc\u0080\3\2\2\2\u01bd\u01bb\3\2\2\2\u01be\u01c4" +
                    "\t\n\2\2\u01bf\u01c3\t\t\2\2\u01c0\u01c1\7z\2\2\u01c1\u01c3\6A\5\2\u01c2" +
                    "\u01bf\3\2\2\2\u01c2\u01c0\3\2\2\2\u01c3\u01c6\3\2\2\2\u01c4\u01c2\3\2" +
                    "\2\2\u01c4\u01c5\3\2\2\2\u01c5\u0082\3\2\2\2\u01c6\u01c4\3\2\2\2\u01c7" +
                    "\u01c8\7&\2\2\u01c8\u01cc\t\b\2\2\u01c9\u01cb\t\f\2\2\u01ca\u01c9\3\2" +
                    "\2\2\u01cb\u01ce\3\2\2\2\u01cc\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd" +
                    "\u0084\3\2\2\2\u01ce\u01cc\3\2\2\2\u01cf\u01d0\7\61\2\2\u01d0\u01d1\7" +
                    ",\2\2\u01d1\u01d5\3\2\2\2\u01d2\u01d4\13\2\2\2\u01d3\u01d2\3\2\2\2\u01d4" +
                    "\u01d7\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d5\u01d3\3\2\2\2\u01d6\u01d8\3\2" +
                    "\2\2\u01d7\u01d5\3\2\2\2\u01d8\u01d9\7,\2\2\u01d9\u01da\7\61\2\2\u01da" +
                    "\u01db\3\2\2\2\u01db\u01dc\bC\2\2\u01dc\u0086\3\2\2\2\u01dd\u01de\7\61" +
                    "\2\2\u01de\u01df\7\61\2\2\u01df\u01e3\3\2\2\2\u01e0\u01e2\n\6\2\2\u01e1" +
                    "\u01e0\3\2\2\2\u01e2\u01e5\3\2\2\2\u01e3\u01e1\3\2\2\2\u01e3\u01e4\3\2" +
                    "\2\2\u01e4\u01e6\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e6\u01e7\bD\2\2\u01e7" +
                    "\u0088\3\2\2\2\u01e8\u01ea\t\r\2\2\u01e9\u01e8\3\2\2\2\u01ea\u01eb\3\2" +
                    "\2\2\u01eb\u01e9\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u01ed\3\2\2\2\u01ed" +
                    "\u01ee\bE\3\2\u01ee\u008a\3\2\2\2\"\2\u0143\u0146\u014c\u014e\u0154\u0157" +
                    "\u015d\u015f\u0165\u0168\u016e\u0171\u0176\u017d\u0180\u0185\u018b\u018e" +
                    "\u0193\u0199\u019b\u01a5\u01b2\u01b4\u01bb\u01c2\u01c4\u01cc\u01d5\u01e3" +
                    "\u01eb\4\2\3\2\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}