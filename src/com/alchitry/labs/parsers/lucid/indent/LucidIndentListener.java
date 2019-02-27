// Generated from LucidIndent.g4 by ANTLR 4.7.1

package com.alchitry.labs.parsers.lucid.indent;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LucidIndentParser}.
 */
public interface LucidIndentListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#lucid}.
	 * @param ctx the parse tree
	 */
	void enterLucid(LucidIndentParser.LucidContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#lucid}.
	 * @param ctx the parse tree
	 */
	void exitLucid(LucidIndentParser.LucidContext ctx);
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#module}.
	 * @param ctx the parse tree
	 */
	void enterModule(LucidIndentParser.ModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#module}.
	 * @param ctx the parse tree
	 */
	void exitModule(LucidIndentParser.ModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#global}.
	 * @param ctx the parse tree
	 */
	void enterGlobal(LucidIndentParser.GlobalContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#global}.
	 * @param ctx the parse tree
	 */
	void exitGlobal(LucidIndentParser.GlobalContext ctx);
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#elem}.
	 * @param ctx the parse tree
	 */
	void enterElem(LucidIndentParser.ElemContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#elem}.
	 * @param ctx the parse tree
	 */
	void exitElem(LucidIndentParser.ElemContext ctx);
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#indent}.
	 * @param ctx the parse tree
	 */
	void enterIndent(LucidIndentParser.IndentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#indent}.
	 * @param ctx the parse tree
	 */
	void exitIndent(LucidIndentParser.IndentContext ctx);
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#always_line}.
	 * @param ctx the parse tree
	 */
	void enterAlways_line(LucidIndentParser.Always_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#always_line}.
	 * @param ctx the parse tree
	 */
	void exitAlways_line(LucidIndentParser.Always_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#else_block}.
	 * @param ctx the parse tree
	 */
	void enterElse_block(LucidIndentParser.Else_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#else_block}.
	 * @param ctx the parse tree
	 */
	void exitElse_block(LucidIndentParser.Else_blockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AlwaysBlock}
	 * labeled alternative in {@link LucidIndentParser#block}.
	 * @param ctx the parse tree
	 */
	void enterAlwaysBlock(LucidIndentParser.AlwaysBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AlwaysBlock}
	 * labeled alternative in {@link LucidIndentParser#block}.
	 * @param ctx the parse tree
	 */
	void exitAlwaysBlock(LucidIndentParser.AlwaysBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultiBlock}
	 * labeled alternative in {@link LucidIndentParser#block}.
	 * @param ctx the parse tree
	 */
	void enterMultiBlock(LucidIndentParser.MultiBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultiBlock}
	 * labeled alternative in {@link LucidIndentParser#block}.
	 * @param ctx the parse tree
	 */
	void exitMultiBlock(LucidIndentParser.MultiBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SingleBlock}
	 * labeled alternative in {@link LucidIndentParser#block}.
	 * @param ctx the parse tree
	 */
	void enterSingleBlock(LucidIndentParser.SingleBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SingleBlock}
	 * labeled alternative in {@link LucidIndentParser#block}.
	 * @param ctx the parse tree
	 */
	void exitSingleBlock(LucidIndentParser.SingleBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link LucidIndentParser#fluff}.
	 * @param ctx the parse tree
	 */
	void enterFluff(LucidIndentParser.FluffContext ctx);
	/**
	 * Exit a parse tree produced by {@link LucidIndentParser#fluff}.
	 * @param ctx the parse tree
	 */
	void exitFluff(LucidIndentParser.FluffContext ctx);
}