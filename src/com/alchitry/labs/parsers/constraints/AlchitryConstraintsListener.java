// Generated from /home/justin/IdeaProjects/Alchitry Labs/source/src/com/alchitry/labs/parsers/constraints/AlchitryConstraints.g4 by ANTLR 4.9.1

package com.alchitry.labs.parsers.constraints;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AlchitryConstraintsParser}.
 */
public interface AlchitryConstraintsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#alchitry_constraints}.
	 * @param ctx the parse tree
	 */
	void enterAlchitry_constraints(AlchitryConstraintsParser.Alchitry_constraintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#alchitry_constraints}.
	 * @param ctx the parse tree
	 */
	void exitAlchitry_constraints(AlchitryConstraintsParser.Alchitry_constraintsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#pin}.
	 * @param ctx the parse tree
	 */
	void enterPin(AlchitryConstraintsParser.PinContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#pin}.
	 * @param ctx the parse tree
	 */
	void exitPin(AlchitryConstraintsParser.PinContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#clock}.
	 * @param ctx the parse tree
	 */
	void enterClock(AlchitryConstraintsParser.ClockContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#clock}.
	 * @param ctx the parse tree
	 */
	void exitClock(AlchitryConstraintsParser.ClockContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(AlchitryConstraintsParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(AlchitryConstraintsParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#port_name}.
	 * @param ctx the parse tree
	 */
	void enterPort_name(AlchitryConstraintsParser.Port_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#port_name}.
	 * @param ctx the parse tree
	 */
	void exitPort_name(AlchitryConstraintsParser.Port_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#pin_name}.
	 * @param ctx the parse tree
	 */
	void enterPin_name(AlchitryConstraintsParser.Pin_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#pin_name}.
	 * @param ctx the parse tree
	 */
	void exitPin_name(AlchitryConstraintsParser.Pin_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#frequency}.
	 * @param ctx the parse tree
	 */
	void enterFrequency(AlchitryConstraintsParser.FrequencyContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#frequency}.
	 * @param ctx the parse tree
	 */
	void exitFrequency(AlchitryConstraintsParser.FrequencyContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#array_index}.
	 * @param ctx the parse tree
	 */
	void enterArray_index(AlchitryConstraintsParser.Array_indexContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#array_index}.
	 * @param ctx the parse tree
	 */
	void exitArray_index(AlchitryConstraintsParser.Array_indexContext ctx);
	/**
	 * Enter a parse tree produced by {@link AlchitryConstraintsParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(AlchitryConstraintsParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link AlchitryConstraintsParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(AlchitryConstraintsParser.NumberContext ctx);
}