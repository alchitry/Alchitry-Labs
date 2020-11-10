package com.alchitry.labs.parsers.lucidv2

import com.alchitry.labs.parsers.errors.ErrorListener
import com.alchitry.labs.parsers.errors.ErrorStrings
import com.alchitry.labs.parsers.errors.dummyErrorListener
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener
import com.alchitry.labs.parsers.lucid.parser.LucidParser.*
import com.alchitry.labs.parsers.lucidv2.values.*
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.apache.commons.text.StringEscapeUtils

class ExprParser(val errorListener: ErrorListener = dummyErrorListener) : LucidBaseListener() {
    val values = mutableMapOf<ParseTree, Value>()
    val constant = mutableMapOf<ParseTree, Boolean>()

    private fun debug(ctx: ParserRuleContext) {
        errorListener.reportDebug(ctx, "${if (constant[ctx] == true) "const " else ""}${values[ctx]}")
    }

    override fun exitSignal(ctx: SignalContext) {

    }

    override fun exitNumber(ctx: NumberContext) {
        constant[ctx] = true
        val radix: Int
        val split: List<String>?
        when {
            ctx.HEX() != null -> {
                radix = 16
                split = ctx.HEX().text.split("h")
            }
            ctx.BIN() != null -> {
                radix = 2
                split = ctx.BIN().text.split("b")
            }
            ctx.DEC() != null -> {
                radix = 10
                split = ctx.DEC().text.split("d")
            }
            else -> {
                radix = 10
                split = null
            }
        }

        val valueString: String
        var width: BitArray? = null

        if (split != null) {
            valueString = split[1]
            if (split[0].isNotBlank()) {
                width = BitArray(split[0])
                if (!width.isNumber()) {
                    errorListener.reportError(ctx, ErrorStrings.NUM_WIDTH_NAN)
                    return
                }
            }
        } else {
            if (ctx.INT() != null) {
                valueString = ctx.INT().text
            } else {
                // String
                val str = StringEscapeUtils.unescapeJava(ctx.STRING().text)
                valueString = str.substring(1, str.length - 1)

                val value: Value
                when {
                    valueString.length > 1 -> {
                        value = ArrayValue()
                        repeat(valueString.length) { value.elements.add(SimpleValue(BitArray(valueString[it].toLong(), 8))) }
                    }
                    valueString.length == 1 -> {
                        value = SimpleValue(BitArray(valueString[0].toLong(), 8))
                    }
                    else -> {
                        value = SimpleValue(BitArray())
                        errorListener.reportError(ctx, ErrorStrings.STRING_CANNOT_BE_EMPTY)
                    }
                }
                values[ctx] = value
                return
            }
        }

        val unbound = BitArray(valueString, radix)
        val value = if (width != null) {
            SimpleValue(BitArray(valueString, radix, width.toBigInt().intValueExact()))
        } else {
            SimpleValue(unbound)
        }
        if (value.bits.size < unbound.size) {
            errorListener.reportWarning(ctx, String.format(ErrorStrings.VALUE_TOO_BIG, ctx.text, value.bits.size))
        }
        values[ctx] = value

        debug(ctx)
    }

    override fun exitParam_constraint(ctx: Param_constraintContext) {

    }

    override fun exitStruct_const(ctx: Struct_constContext) {

    }

    override fun exitExprSignal(ctx: ExprSignalContext) {
        values[ctx.signal()]?.let { values[ctx] = it }
        constant[ctx.signal()]?.let { constant[ctx] = it }
    }

    override fun exitExprStruct(ctx: ExprStructContext) {
        values[ctx.struct_const()]?.let { values[ctx] = it }
        constant[ctx.struct_const()]?.let { constant[ctx] = it }
    }

    override fun exitExprFunction(ctx: ExprFunctionContext) {
        values[ctx.function()]?.let { values[ctx] = it }
        constant[ctx.function()]?.let { constant[ctx] = it }
    }

    override fun exitExprNum(ctx: ExprNumContext) {
        values[ctx.number()]?.let { values[ctx] = it }
        constant[ctx.number()]?.let { constant[ctx] = it }
    }

    override fun exitExprGroup(ctx: ExprGroupContext) {
        if (ctx.expr() == null)
            return
        values[ctx.expr()]?.let { values[ctx] = it }
        constant[ctx.expr()]?.let { constant[ctx] = it }
    }

    override fun exitExprAddSub(ctx: ExprAddSubContext) {
        if (ctx.childCount != 3 || ctx.expr().size != 2)
            return

        val op1 = values[ctx.expr(0)]
        val op2 = values[ctx.expr(1)]

        // is constant if both operands are constant
        constant[ctx] = !ctx.expr().any { constant[it] != true }

        if (op1 == null || op2 == null)
            return

        val operand = ctx.getChild(1).text

        var error = false

        if (!op1.signalWidth.isSimpleArray()) {
            errorListener.reportError(ctx.expr(0), if (operand == "+") ErrorStrings.ADD_MULTI_DIM else ErrorStrings.SUB_MULTI_DIM)
            error = true
        }

        if (!op2.signalWidth.isSimpleArray()) {
            errorListener.reportError(ctx.expr(1), if (operand == "+") ErrorStrings.ADD_MULTI_DIM else ErrorStrings.SUB_MULTI_DIM)
            error = true
        }

        if (error) return

        if (op1 is UndefinedValue || op2 is UndefinedValue) {
            values[ctx] = UndefinedValue(ctx.text)
        } else {
            if (op1 !is SimpleValue || op2 !is SimpleValue)
                error("One (or both) of the operands isn't a simple array. This shouldn't be possible.")

            values[ctx] = when {
                !op1.isNumber() || !op2.isNumber() -> SimpleValue(BitArray(BitValue.Bx, op1.bits.size))
                operand == "+" -> SimpleValue(BitArray(op1.bits.toBigInt().add(op2.bits.toBigInt()), op1.bits.size.coerceAtLeast(op2.bits.size) + 1))
                else -> SimpleValue(BitArray(op1.bits.toBigInt().subtract(op2.bits.toBigInt()), op1.bits.size.coerceAtLeast(op2.bits.size) + 1))
            }
        }

        debug(ctx)
    }

    override fun exitExprConcat(ctx: ExprConcatContext) {
        if (ctx.expr().isEmpty())
            return

        // is constant if all operands are constant
        constant[ctx] = !ctx.expr().any { constant[it] != true }

        val operands = mutableListOf<Pair<Value, ParserRuleContext>>()
        ctx.expr().forEach {
            val v = values[it] ?: return
            operands.add(Pair(v, it))
        }

        if (operands.isEmpty())
            return

        val base = operands[0].first
        val baseSigWidth = base.signalWidth
        var error = false

        if (baseSigWidth is StructWidth) {
            operands.forEach {
                if (it.first.signalWidth != baseSigWidth) {
                    errorListener.reportError(it.second, ErrorStrings.ARRAY_CONCAT_DIM_MISMATCH)
                    error = true
                }
            }
            if (error) return

            val value = ArrayValue()
            operands.asReversed().forEach { value.elements.add(it.first) }
            values[ctx] = value
        } else if (baseSigWidth is ArrayWidth || baseSigWidth is UndefinedSimpleArray) { // if width is array, value is array or simple
            when (base) {
                is ArrayValue -> {
                    assert(baseSigWidth is ArrayWidth) { "The ArrayValue has a width that isn't an ArrayWidth" }
                    operands.forEach {
                        val sigWidth = it.first.signalWidth
                        if (sigWidth !is ArrayWidth || sigWidth.next != (baseSigWidth as ArrayWidth).next) {
                            errorListener.reportError(it.second, ErrorStrings.ARRAY_CONCAT_DIM_MISMATCH)
                            error = true
                        }
                    }
                    if (error) return

                    val value = ArrayValue()
                    operands.asReversed().forEach { value.elements.addAll((it.first as ArrayValue).elements) }
                    values[ctx] = value
                }
                is SimpleValue -> {
                    operands.forEach {
                        val sigWidth = it.first.signalWidth
                        if (!sigWidth.isSimpleArray()) {
                            errorListener.reportError(it.second, ErrorStrings.ARRAY_CONCAT_DIM_MISMATCH)
                            error = true
                        }
                    }
                    if (error) return

                    if (operands.any { it.first is UndefinedValue }) {
                        values[ctx] = UndefinedValue(ctx.text)
                        return
                    }

                    val bits = BitArray()
                    operands.asReversed().forEach { bits.addAll((it.first as SimpleValue).bits) }
                    values[ctx] = SimpleValue(bits)
                }
                else -> {
                    error("Value with array width isn't an array or simple value")
                }
            }
        }

        debug(ctx)
    }

    override fun exitExprDup(ctx: ExprDupContext) {

    }

    override fun exitExprArray(ctx: ExprArrayContext) {

    }

    override fun exitExprNegate(ctx: ExprNegateContext) {

    }

    override fun exitExprInvert(ctx: ExprInvertContext) {

    }

    override fun exitExprMultDiv(ctx: ExprMultDivContext) {

    }

    override fun exitExprShift(ctx: ExprShiftContext) {

    }

    override fun exitExprAndOr(ctx: ExprAndOrContext) {

    }

    override fun exitExprCompress(ctx: ExprCompressContext) {

    }

    override fun exitExprCompare(ctx: ExprCompareContext) {

    }

    override fun exitExprLogical(ctx: ExprLogicalContext) {

    }

    override fun exitExprTernary(ctx: ExprTernaryContext) {

    }

    override fun exitFunction(ctx: FunctionContext) {

    }
}