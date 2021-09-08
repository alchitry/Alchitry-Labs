// Generated from /home/justin/IdeaProjects/Alchitry Labs/source/src/com/alchitry/labs/parsers/constraints/AlchitryConstraints.g4 by ANTLR 4.9.1

package com.alchitry.labs.parsers.constraints;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AlchitryConstraintsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface AlchitryConstraintsVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#alchitry_constraints}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAlchitry_constraints(AlchitryConstraintsParser.Alchitry_constraintsContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#pin}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPin(AlchitryConstraintsParser.PinContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#clock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitClock(AlchitryConstraintsParser.ClockContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(AlchitryConstraintsParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#port_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPort_name(AlchitryConstraintsParser.Port_nameContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#pin_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPin_name(AlchitryConstraintsParser.Pin_nameContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#frequency}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFrequency(AlchitryConstraintsParser.FrequencyContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#array_index}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArray_index(AlchitryConstraintsParser.Array_indexContext ctx);

    /**
     * Visit a parse tree produced by {@link AlchitryConstraintsParser#number}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNumber(AlchitryConstraintsParser.NumberContext ctx);
}