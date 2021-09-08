// Generated from /home/justin/IdeaProjects/Alchitry Labs/source/src/com/alchitry/labs/parsers/lucid/indent/LucidIndent.g4 by ANTLR 4.9.1

package com.alchitry.labs.parsers.lucid.indent;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LucidIndentParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface LucidIndentVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link LucidIndentParser#lucid}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLucid(LucidIndentParser.LucidContext ctx);

    /**
     * Visit a parse tree produced by {@link LucidIndentParser#module}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitModule(LucidIndentParser.ModuleContext ctx);

    /**
     * Visit a parse tree produced by {@link LucidIndentParser#global}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGlobal(LucidIndentParser.GlobalContext ctx);

    /**
     * Visit a parse tree produced by {@link LucidIndentParser#elem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitElem(LucidIndentParser.ElemContext ctx);

    /**
     * Visit a parse tree produced by {@link LucidIndentParser#indent}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIndent(LucidIndentParser.IndentContext ctx);

    /**
     * Visit a parse tree produced by {@link LucidIndentParser#always_line}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAlways_line(LucidIndentParser.Always_lineContext ctx);

    /**
     * Visit a parse tree produced by {@link LucidIndentParser#else_block}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitElse_block(LucidIndentParser.Else_blockContext ctx);

    /**
     * Visit a parse tree produced by the {@code AlwaysBlock}
     * labeled alternative in {@link LucidIndentParser#block}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAlwaysBlock(LucidIndentParser.AlwaysBlockContext ctx);

    /**
     * Visit a parse tree produced by the {@code MultiBlock}
     * labeled alternative in {@link LucidIndentParser#block}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiBlock(LucidIndentParser.MultiBlockContext ctx);

    /**
     * Visit a parse tree produced by the {@code SingleBlock}
     * labeled alternative in {@link LucidIndentParser#block}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSingleBlock(LucidIndentParser.SingleBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link LucidIndentParser#fluff}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFluff(LucidIndentParser.FluffContext ctx);
}