package com.alchitry.labs.parsers.tools.lucid;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.ProjectSignal;
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ModuleContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Module_instContext;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.Port_listContext;
import com.alchitry.labs.parsers.tools.verilog.VerilogConstExprParser;
import com.alchitry.labs.project.DebugInfo;
import com.alchitry.labs.project.builders.ProjectBuilder.DebugFile;
import com.alchitry.labs.tools.ParserCache;

public class LucidDebugModifier {

	private LucidDebugModifier() {
	}

	public static String modifyForDebug(File file, DebugInfo debugInfo, InstModule thisModule, boolean isTop, Collection<DebugFile> debugFiles) {
		List<ParseTreeListener> listeners = new ArrayList<>();
		CommonTokenStream tokens = ParserCache.getTokens(file);
		VerilogConstExprParser cep = new VerilogConstExprParser(thisModule);
		DebugWalker mrw = new DebugWalker(thisModule, debugInfo, tokens, isTop, debugFiles);
		listeners.add(mrw);
		listeners.add(cep);

		ParserCache.walk(file, listeners);
		return mrw.getText();
	}

	private static class DebugWalker extends LucidBaseListener {
		private InstModule thisModule;
		private DebugInfo debugInfo;
		private TokenStreamRewriter rewriter;
		private boolean isTop;
		private Collection<DebugFile> debugFiles;
		private Board board;

		public DebugWalker(InstModule thisModule, DebugInfo debugInfo, TokenStream tokens, boolean isTop, Collection<DebugFile> debugFiles) {
			this.thisModule = thisModule;
			this.debugInfo = debugInfo;
			this.isTop = isTop;
			this.debugFiles = debugFiles;
			board = MainWindow.getOpenProject().getBoard();
			rewriter = new TokenStreamRewriter(tokens);
		}

		public String getText() {
			return rewriter.getText();
		}

		private int getTotalDebugWidth(InstModule module) {
			int width = 0;
			for (ProjectSignal s : debugInfo.getSignals()) {
				if (s.getPath().contains(module)) {
					width += s.getTotalWidth();
				}
			}
			return width;
		}

		@Override
		public void exitPort_list(Port_listContext ctx) {
			if (!isTop)
				rewriter.insertAfter(ctx.port_dec(ctx.port_dec().size() - 1).getStop(), ", output debug__ [" + getTotalDebugWidth(thisModule) + "]");
		}

		private int getIndex(InstModule module) {
			for (DebugFile df : debugFiles)
				if (df.instModule == module)
					return df.index;
			return -1;
		}

		private InstModule hasChildInstModule(String name) {
			for (ProjectSignal s : debugInfo.getSignals()) {
				int index = s.getPath().indexOf(thisModule);
				if (index >= 0 && index < s.getPath().size() - 1) {
					InstModule child = s.getPath().get(index + 1);
					if (child.getName().equals(name)) {
						return child;
					}
				}
			}
			return null;
		}

		@Override
		public void exitModule_inst(Module_instContext ctx) {
			InstModule child = hasChildInstModule(ctx.name(1).getText());
			if (child != null)
				rewriter.insertAfter(ctx.name(0).getStop(), "_" + getIndex(child) + "_debug");

			if (isTop && board.isType(Board.MOJO)) {
				InstModule regInterfaceMod = null;
				for (InstModule im : thisModule.getChildren())
					if (im.getType().getName().equals("reg_interface")) {
						regInterfaceMod = im;
						break;
					}
				if (ctx.name(1).getText().equals(regInterfaceMod.getName())) {
					rewriter.insertAfter(ctx.name(0).getStop(), "_debug");
					StringBuilder sb = new StringBuilder();
					if (ctx.inst_cons() == null) {
						sb.append("(");
					} else if (ctx.inst_cons().con_list().connection().size() > 0) {
						sb.append(",");
					}
					sb.append("#NONCE(").append(debugInfo.getNonce()).append("), ");
					sb.append("#DATA_WIDTH(").append(getTotalDebugWidth(thisModule));
					sb.append("), #CAPTURE_DEPTH(").append(debugInfo.getSamples()).append(")");
					if (ctx.inst_cons() == null) {
						sb.append(")");
						rewriter.insertAfter(ctx.getStop(), sb.toString());
					} else {
						rewriter.insertBefore(ctx.inst_cons().getStop(), sb.toString());
					}
				}
			}
		}

		@Override
		public void exitModule(ModuleContext ctx) {
			if (!isTop)
				rewriter.insertAfter(ctx.name().getStop(), "_" + getIndex(thisModule) + "_debug");

			StringBuilder sb = new StringBuilder();

			if (isTop && board.isType(Board.AU)) {
				sb.append("au_debugger debugger (#NONCE(").append(debugInfo.getNonce()).append("), ");
				sb.append("#DATA_WIDTH(").append(getTotalDebugWidth(thisModule));
				sb.append("), #CAPTURE_DEPTH(").append(debugInfo.getSamples()).append("), ");
				sb.append(".clk(").append(debugInfo.getClock().getName()).append("));");
			}

			sb.append("always ");
			if (isTop) {
				if (board.isType(Board.MOJO)) {
					InstModule regInterfaceMod = null;
					for (InstModule im : thisModule.getChildren())
						if (im.getType().getName().equals("reg_interface")) {
							regInterfaceMod = im;
							break;
						}
					sb.append(regInterfaceMod.getName());
					sb.append(".debug");
				} else {
					sb.append("debugger.data");
				}
			} else {
				sb.append("debug__");
			}
			sb.append(" = c{");
			boolean first = true;
			HashSet<InstModule> includedMods = new HashSet<>();
			for (ProjectSignal s : debugInfo.getSignals()) {
				if (s.getPath().contains(thisModule)) {
					boolean isSig = s.getPath().get(s.getPath().size() - 1) == thisModule;

					if (!isSig && !includedMods.add(s.getPath().get(s.getPath().indexOf(thisModule) + 1)))
						continue;

					if (first)
						first = false;
					else
						sb.append(", ");

					sb.append("$flatten(");

					if (isSig) {
						if (s.getDff() != null)
							sb.append(s.getDff().getName()).append(".q");
						else if (s.getFsm() != null)
							sb.append(s.getFsm().getName()).append(".q");
						else if (s.getSig() != null) {
							if (s.getParent() != null) {
								sb.append(s.getParent().getName());
								sb.append(".");
								sb.append(s.getSig().getName());
							} else
								sb.append(s.getSig().getName());
						}
					} else {
						int thisIdx = s.getPath().indexOf(thisModule);
						sb.append(s.getPath().get(thisIdx + 1).getName());
						sb.append(".debug__");
					}

					sb.append(")");
				}
			}
			sb.append("};").append(System.lineSeparator());
			rewriter.insertBefore(ctx.module_body().getStop(), sb.toString());
		}
	}
}
