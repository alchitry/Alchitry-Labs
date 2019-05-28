package com.alchitry.labs.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import com.alchitry.labs.Util;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsLexer;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsParser;
import com.alchitry.labs.parsers.lucid.parser.LucidLexer;
import com.alchitry.labs.parsers.lucid.parser.LucidParser;
import com.alchitry.labs.parsers.verilog.Verilog2001Lexer;
import com.alchitry.labs.parsers.verilog.Verilog2001Parser;

public class ParserCache {
	private static HashMap<File, CacheEntry> treeMap = new HashMap<>();

	private ParserCache() {
	}

	private static class CacheEntry {
		public ParseTree tree;
		public CommonTokenStream tokens;
		public File file;
		public long timeStamp;
		public List<ParseError> errors;

		public CacheEntry(File f) {
			file = f;
			timeStamp = -1;
			errors = new ArrayList<>();
		}
	}

	public static class ParseError {
		public final String msg;
		public final Token token;

		public ParseError(Token t, String m) {
			token = t;
			msg = m;
		}
	}

	static public void flush() {
		synchronized (treeMap) {
			treeMap.clear();
		}
	}

	static public void invalidate(File file) {
		synchronized (treeMap) {
			CacheEntry entry = treeMap.get(file);
			if (entry != null)
				synchronized (entry) {
					entry.timeStamp = -1;
				}
		}
	}

	static public ParseError[] getErrors(File file) {
		CacheEntry entry;
		synchronized (treeMap) {
			entry = treeMap.get(file);
			if (entry == null) {
				entry = new CacheEntry(file);
				treeMap.put(file, entry);
			}
			synchronized (entry) {
				updateEntry(entry);
				return entry.errors.toArray(new ParseError[entry.errors.size()]);
			}
		}
	}

	static public CommonTokenStream getTokens(File file) {
		CacheEntry entry;

		synchronized (treeMap) {
			entry = treeMap.get(file);

			if (entry == null) {
				entry = new CacheEntry(file);
				treeMap.put(file, entry);
			}

			synchronized (entry) {
				updateEntry(entry);
				return entry.tokens;
			}
		}

	}
	
	static public ParseTree walk(File file, ParseTreeListener listener) {
		List<ParseTreeListener> listeners = new ArrayList<>();
		listeners.add(listener);
		return walk(file, listeners);
	}

	static public ParseTree walk(File file, List<ParseTreeListener> listeners) {
		CacheEntry entry;

		synchronized (treeMap) {
			entry = treeMap.get(file);

			if (entry == null) {
				entry = new CacheEntry(file);
				treeMap.put(file, entry);
			}
		}

		synchronized (entry) {
			updateEntry(entry);

			if (entry.tree == null)
				return null;

			ParseTreeMultiWalker.walk(listeners, entry.tree);
			return entry.tree;
		}
	}

	static private void updateEntry(CacheEntry entry) {
		long mod = entry.file.lastModified();

		if (mod != entry.timeStamp || entry.tree == null) {
			// System.out.println("Cache miss!");
			try {
				entry.tree = parseFile(entry);
			} catch (IOException e) {

			}
			if (entry.tree != null)
				entry.timeStamp = mod;
		} else {
			 //System.out.println("Cache hit!");
		}
	}

	static private ParseTree parseFile(final CacheEntry entry) throws IOException {
		String[] parts = entry.file.getName().split("\\.");
		if (parts.length != 2) {
			Util.log.severe("File \"" + entry.file.getName() + "\" suffix could not be detected.");
			return null;
		}

		String text = Util.getFileText(entry.file);

		if (text == null) {
			Util.showError("Could not read file " + entry);
			return null;
		}

		ParseTree tree = null;

		CharStream input = CharStreams.fromString(text);

		entry.errors.clear();

		ANTLRErrorListener errorListener = new ANTLRErrorListener() {

			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
				entry.errors.add(new ParseError((Token) offendingSymbol, msg));
			}

			@Override
			public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {

			}

			@Override
			public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {

			}

			@Override
			public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {

			}
		};

		switch (parts[1]) {
		case "luc":
			LucidLexer llexer = new LucidLexer(input);
			CommonTokenStream ltokens = new CommonTokenStream(llexer);
			entry.tokens = ltokens;
			LucidParser lparser = new LucidParser(ltokens);
			lparser.removeErrorListeners();
			lparser.addErrorListener(errorListener);
			tree = lparser.source();
			break;
		case "v":
			Verilog2001Lexer vlexer = new Verilog2001Lexer(input);
			CommonTokenStream vtokens = new CommonTokenStream(vlexer);
			entry.tokens = vtokens;
			Verilog2001Parser vparser = new Verilog2001Parser(vtokens);
			vparser.removeErrorListeners();
			vparser.addErrorListener(errorListener);
			tree = vparser.source_text();
			break;
		case "acf":
			AlchitryConstraintsLexer alexer = new AlchitryConstraintsLexer(input);
			CommonTokenStream atoken = new CommonTokenStream(alexer);
			entry.tokens = atoken;
			AlchitryConstraintsParser aparser = new AlchitryConstraintsParser(atoken);
			aparser.removeErrorListeners();
			aparser.addErrorListener(errorListener);
			tree = aparser.alchitry_constraints();
			break;
		case "ucf":
			break;
		default:
			break;
		}

		return tree;
	}
}
