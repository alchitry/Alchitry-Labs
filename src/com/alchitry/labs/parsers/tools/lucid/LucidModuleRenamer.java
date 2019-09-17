package com.alchitry.labs.parsers.tools.lucid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ModuleContext;
import com.alchitry.labs.tools.ParserCache;

public class LucidModuleRenamer {

	private LucidModuleRenamer() {
	}

	public static String renameModule(File file, String newName) {
		List<ParseTreeListener> listeners = new ArrayList<>();
		CommonTokenStream tokens = ParserCache.getTokens(file);
		RenameWalker mrw = new RenameWalker(tokens, newName);
		listeners.add(mrw);

		ParserCache.walk(file, listeners);
		return mrw.getText();
	}

	private static class RenameWalker extends LucidBaseListener {
		private TokenStreamRewriter rewriter;
		private String name;

		public RenameWalker(TokenStream tokens, String newName) {
			rewriter = new TokenStreamRewriter(tokens);
			name = newName;
		}

		public String getText() {
			return rewriter.getText();
		}

		@Override
		public void exitModule(ModuleContext ctx) {
			rewriter.replace(ctx.name().start, ctx.name().stop, name);
		}
	}
}
