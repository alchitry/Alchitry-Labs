package com.alchitry.labs.parsers.lucidv2

import com.alchitry.labs.Util.widthOfMult
import com.alchitry.labs.parsers.errors.ErrorListener
import com.alchitry.labs.parsers.errors.ErrorStrings
import com.alchitry.labs.parsers.errors.dummyErrorListener
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener
import com.alchitry.labs.parsers.lucid.parser.LucidParser.*
import com.alchitry.labs.parsers.lucidv2.values.*
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.apache.commons.text.StringEscapeUtils
import java.math.BigInteger

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
        var width: MutableBitArray? = null

        if (split != null) {
            valueString = split[1]
            if (split[0].isNotBlank()) {
                width = MutableBitArray(split[0])
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
                        val elements = mutableListOf<Value>()
                        repeat(valueString.length) {
                            elements.add(
                                SimpleValue(
                                    MutableBitArray(
                                        valueString[it].toLong(),
                                        8
                                    )
                                )
                            )
                        }
                        value = ArrayValue(elements)
                    }
                    valueString.length == 1 -> {
                        value = SimpleValue(MutableBitArray(valueString[0].toLong(), 8))
                    }
                    else -> {
                        value = SimpleValue(MutableBitArray())
                        errorListener.reportError(ctx, ErrorStrings.STRING_CANNOT_BE_EMPTY)
                    }
                }
                values[ctx] = value
                return
            }
        }

        val unbound = MutableBitArray(valueString, radix)
        val value = if (width != null) {
            SimpleValue(MutableBitArray(valueString, radix, width.toBigInt().intValueExact()))
        } else {
            SimpleValue(unbound)
        }
        if (value.bits.size < unbound.size) {
            errorListener.reportWarning(ctx, String.format(ErrorStrings.VALUE_TOO_BIG, ctx.text, value.bits.size))
        }
        values[ctx] = value
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

    // always returns an unsigned value
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
            errorListener.reportError(operands[0].second, ErrorStrings.ARRAY_CONCAT_STRUCT)
            return
        }

        // if width is array, value is array or simple
        assert(baseSigWidth is ArrayWidth || baseSigWidth is UndefinedSimpleWidth)
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

                val value = mutableListOf<Value>()
                operands.asReversed().forEach { value.addAll((it.first as ArrayValue).elements) }
                values[ctx] = ArrayValue(value)
            }
            is SimpleValue, is UndefinedValue -> {
                var bitCount = 0
                var definedWidth = true

                operands.forEach {
                    val sigWidth = it.first.signalWidth
                    if (!sigWidth.isFlatArray()) {
                        errorListener.reportError(it.second, ErrorStrings.ARRAY_CONCAT_DIM_MISMATCH)
                        error = true
                    }
                    if (sigWidth is ArrayWidth)
                        bitCount += sigWidth.size
                    else // UndefinedWidth
                        definedWidth = false
                }

                if (error) return

                if (operands.any { it.first is UndefinedValue }) {
                    values[ctx] = if (definedWidth)
                        UndefinedValue(ctx.text, ArrayWidth(bitCount))
                    else
                        UndefinedValue(ctx.text)

                    return
                }

                val bits = MutableBitArray()
                operands.asReversed().forEach { bits.addAll((it.first as SimpleValue).bits) }
                values[ctx] = SimpleValue(bits)
            }
            else -> {
                error("Value with array width isn't an array or simple value")
            }
        }
    }

    // always returns an unsigned value
    override fun exitExprDup(ctx: ExprDupContext) {
        if (ctx.expr().size != 2)
            return

        constant[ctx] = constant[ctx.expr(1)] == true

        val dupCount = values[ctx.expr(0)] ?: return
        val dupValue = values[ctx.expr(1)] ?: return

        if (constant[ctx.expr(0)] != true) {
            errorListener.reportError(ctx.expr(0), ErrorStrings.EXPR_NOT_CONSTANT.format(ctx.expr(0).text))
            return
        }

        val valWidth = dupValue.signalWidth

        if (!valWidth.isArray()) {
            errorListener.reportError(ctx.expr(0), ErrorStrings.ARRAY_DUP_STRUCT)
            return
        }

        val dupWidth = dupCount.signalWidth

        if (!dupWidth.isFlatArray()) {
            errorListener.reportError(ctx.expr(0), ErrorStrings.ARRAY_DUP_INDEX_MULTI_DIM)
            return
        }

        // if the duplication value is undefined we have no idea what the width will be
        if (dupCount is UndefinedValue) {
            values[ctx] = UndefinedValue(ctx.text)
            return
        }

        assert(dupCount is SimpleValue) { "Duplication count is flat array but not a SimpleValue!" }
        dupCount as SimpleValue

        if (!dupCount.bits.isNumber()) {
            errorListener.reportError(ctx.expr(0), ErrorStrings.ARRAY_DUP_INDEX_NAN)
            return
        }

        val dupTimes = dupCount.bits.toBigInt().toInt()

        if (dupValue is UndefinedValue) {
            values[ctx] = UndefinedValue(
                ctx.text,
                width = if (valWidth is ArrayWidth) {
                    ArrayWidth(valWidth.size * dupTimes, valWidth.next)
                } else {
                    UndefinedSimpleWidth
                }
            )
            return
        }

        if (dupValue is ArrayValue) {
            val elements = mutableListOf<Value>()
            repeat(dupTimes) {
                elements.addAll(dupValue.elements)
            }
            values[ctx] = ArrayValue(elements)
        } else if (dupValue is SimpleValue) {
            val bits = MutableBitArray(dupValue.signed)
            repeat(dupTimes) {
                bits.addAll(dupValue.bits)
            }
            values[ctx] = SimpleValue(bits)
        }
        debug(ctx)
    }

    override fun exitExprArray(ctx: ExprArrayContext) {
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

        val firstDim = operands[0].first.signalWidth

        var error = false
        operands.forEach {
            if (it.first.signalWidth != firstDim) {
                error = true
                errorListener.reportError(it.second, ErrorStrings.ARRAY_BUILDING_DIM_MISMATCH)
            }
        }
        if (error) return

        val elements = mutableListOf<Value>()
        operands.forEach { elements.add(it.first) }
        values[ctx] = ArrayValue(elements)
    }

    override fun exitExprNegate(ctx: ExprNegateContext) {
        constant[ctx] = constant[ctx.expr()] == true
        val expr = values[ctx.expr()] ?: return

        if (!expr.signalWidth.isFlatArray()) {
            errorListener.reportError(ctx, ErrorStrings.NEG_MULTI_DIM)
            return
        }

        if (expr is UndefinedValue) {
            values[ctx] = UndefinedValue(ctx.text, expr.width, expr.signed)
            return
        }

        assert(expr is SimpleValue) { "Expression assumed to be SimpleValue" }
        expr as SimpleValue

        if (!expr.bits.isNumber()) {
            values[ctx] = SimpleValue(MutableBitArray(expr.signed, expr.size) { BitValue.Bx })
            return
        }

        values[ctx] = SimpleValue(MutableBitArray(expr.bits.toBigInt().negate(), expr.size, expr.signed))
        debug(ctx)
    }

    override fun exitExprInvert(ctx: ExprInvertContext) {
        constant[ctx] = constant[ctx.expr()] == true
        val expr = values[ctx.expr()] ?: return

        values[ctx] = if (ctx.getChild(0).text == "!") {
            expr.not()
        } else { // ~ operator
            expr.invert()
        }
        debug(ctx)
    }

    override fun exitExprAddSub(ctx: ExprAddSubContext) {
        if (ctx.childCount != 3 || ctx.expr().size != 2)
            return

        // is constant if both operands are constant
        constant[ctx] = !ctx.expr().any { constant[it] != true }

        val op1 = values[ctx.expr(0)] ?: return
        val op2 = values[ctx.expr(1)] ?: return

        val operand = ctx.getChild(1).text

        if (!checkFlat(*ctx.expr().toTypedArray()) {
                errorListener.reportError(
                    it,
                    if (operand == "+") ErrorStrings.ADD_MULTI_DIM else ErrorStrings.SUB_MULTI_DIM
                )
            }) return

        val op1Width = op1.signalWidth
        val op2Width = op2.signalWidth
        val signed = op1.signed && op2.signed

        if (op1 is UndefinedValue || op2 is UndefinedValue) {
            if (op1Width is ArrayWidth && op2Width is ArrayWidth)
                values[ctx] =
                    UndefinedValue(ctx.text, ArrayWidth(op1Width.size.coerceAtLeast(op2Width.size) + 1), signed)
            else
                values[ctx] = UndefinedValue(ctx.text, signed = signed)
            return
        }

        if (op1 !is SimpleValue || op2 !is SimpleValue)
            error("One (or both) of the operands isn't a simple array. This shouldn't be possible.")

        val width = op1.bits.size.coerceAtLeast(op2.bits.size) + 1

        values[ctx] = when {
            !op1.isNumber() || !op2.isNumber() -> SimpleValue(MutableBitArray(BitValue.Bx, width, signed))
            operand == "+" -> SimpleValue(MutableBitArray(op1.bits.toBigInt().add(op2.bits.toBigInt()), width, signed))
            else -> SimpleValue(MutableBitArray(op1.bits.toBigInt().subtract(op2.bits.toBigInt()), width, signed))
        }
    }

    override fun exitExprMultDiv(ctx: ExprMultDivContext) {
        if (ctx.childCount != 3 || ctx.expr().size != 2) return

        // is constant if both operands are constant
        constant[ctx] = !ctx.expr().any { constant[it] != true }

        val op1 = values[ctx.expr(0)] ?: return
        val op2 = values[ctx.expr(1)] ?: return

        val multOp = ctx.getChild(1).text == "*"

        if (!checkFlat(*ctx.expr().toTypedArray()) {
                errorListener.reportError(
                    it,
                    if (multOp) ErrorStrings.MUL_MULTI_DIM else ErrorStrings.DIV_MULTI_DIM
                )
            }) return

        val op1Width = op1.signalWidth
        val op2Width = op2.signalWidth
        val signed = op1.signed && op2.signed

        if (op1 is UndefinedValue || op2 is UndefinedValue) {
            if (op1Width is ArrayWidth && op2Width is ArrayWidth)
                values[ctx] = UndefinedValue(ctx.text, ArrayWidth(widthOfMult(op1Width.size, op2Width.size)), signed)
            else
                values[ctx] = UndefinedValue(ctx.text, signed = signed)
            return
        }

        if (op1 !is SimpleValue || op2 !is SimpleValue)
            error("One (or both) of the operands isn't a simple array. This shouldn't be possible.")


        values[ctx] = if (multOp) {
            val width = widthOfMult(op1.bits.size, op2.bits.size)
            if (!op1.isNumber() || !op2.isNumber())
                SimpleValue(MutableBitArray(BitValue.Bx, width, signed))
            else
                SimpleValue(MutableBitArray(op1.bits.toBigInt().multiply(op2.bits.toBigInt()), width, signed))
        } else {
            val width = op1.bits.size
            if (!op1.isNumber() || !op2.isNumber() || op2.bits.toBigInt() == BigInteger.ZERO)
                SimpleValue(MutableBitArray(BitValue.Bx, width, signed))
            else
                SimpleValue(MutableBitArray(op1.bits.toBigInt().divide(op2.bits.toBigInt()), width, signed))
        }
        debug(ctx)
    }

    override fun exitExprShift(ctx: ExprShiftContext) {
        if (ctx.childCount != 3 || ctx.expr().size != 2) return

        // is constant if both operands are constant
        constant[ctx] = !ctx.expr().any { constant[it] != true }

        val value = values[ctx.expr(0)] ?: return
        val shift = values[ctx.expr(1)] ?: return

        val operand = ctx.getChild(1).text

        if (!checkFlat(*ctx.expr().toTypedArray()) {
                errorListener.reportError(it, ErrorStrings.SHIFT_MULTI_DIM)
            }) return

        val isSigned = value.signed && (operand == ">>>" || operand == "<<<")

        if (shift is UndefinedValue) {
            values[ctx] = UndefinedValue(ctx.text, signed = isSigned)
            return
        }

        check(shift is SimpleValue) { "Shift value is flat array but not SimpleValue or UndefinedValue" }

        if (value is UndefinedValue) {
            val vWidth = value.signalWidth
            if (vWidth is ArrayWidth) {
                val w = if (operand == "<<" || operand == "<<<") vWidth.size + shift.bits.toBigInt()
                    .toInt() else vWidth.size
                values[ctx] = UndefinedValue(ctx.text, ArrayWidth(w), signed = isSigned)
            } else
                values[ctx] = UndefinedValue(ctx.text, signed = isSigned)
        }

        check(value is SimpleValue) { "Value is flat array but not SimpleValue or UndefinedValue" }

        if (!shift.bits.isNumber()) {
            values[ctx] = SimpleValue(MutableBitArray(isSigned, value.size) { BitValue.Bx })
            return
        }

        val shiftAmount = shift.bits.toBigInt().toInt()

        values[ctx] = when (operand) {
            ">>" -> SimpleValue(value.bits ushr shiftAmount)
            ">>>" -> SimpleValue(value.bits shr shiftAmount)
            "<<" -> SimpleValue(value.bits ushl shiftAmount)
            "<<<" -> SimpleValue(value.bits shl shiftAmount)
            else -> {
                errorListener.reportError(ctx.getChild(ParserRuleContext::class.java, 1), "Unknown operator $operand")
                return
            }
        }
        debug(ctx)
    }

    override fun exitExprBitwise(ctx: ExprBitwiseContext) {
        if (ctx.childCount != 3 || ctx.expr().size != 2) return

        // is constant if all operands are constant
        constant[ctx] = !ctx.expr().any { constant[it] != true }

        val op1 = values[ctx.expr(0)] ?: return
        val op2 = values[ctx.expr(1)] ?: return

        val operand = ctx.getChild(1).text

        if (!checkUndefinedMatchingDims(ctx.expr(0), ctx.expr(1)) {
                errorListener.reportError(it, ErrorStrings.OP_DIM_MISMATCH.format(operand))
            }) return

        if (op1 is UndefinedValue || op2 is UndefinedValue) {
            if (!checkFlat(*ctx.expr().toTypedArray()) {
                    errorListener.reportError(it, ErrorStrings.OP_DIM_MISMATCH.format(operand))
                }) return

            val isSigned = op1.signed && op2.signed

            val op1Width = op1.signalWidth
            val op2Width = op2.signalWidth

            if (op1Width is ArrayWidth && op2Width is ArrayWidth) {
                if (op1Width != op2Width) {
                    errorListener.reportError(ctx.expr(1), ErrorStrings.OP_DIM_MISMATCH.format(operand))
                    return
                }
                values[ctx] = UndefinedValue(ctx.text, op1Width, isSigned)
            } else {
                values[ctx] = UndefinedValue(ctx.text, signed = isSigned)
            }
            return
        }

        if (!checkFlatOrMatchingDims(*ctx.expr().toTypedArray()) {
                errorListener.reportError(it, ErrorStrings.OP_DIM_MISMATCH.format(operand))
            }) return

        values[ctx] = when (operand) {
            "&" -> op1 and op2
            "|" -> op1 or op2
            "^" -> op1 xor op2
            else -> {
                errorListener.reportError(ctx.getChild(ParserRuleContext::class.java, 1), "Unknown operator $operand")
                return
            }
        }
        debug(ctx)
    }

    override fun exitExprReduction(ctx: ExprReductionContext) {
        if (ctx.childCount != 2 || ctx.expr() == null) return

        constant[ctx] = constant[ctx.expr()] == true

        val value = values[ctx.expr()] ?: return

        if (value is UndefinedValue) {
            values[ctx] = UndefinedValue(ctx.text, ArrayWidth(1), false)
            return
        }

        values[ctx] = when (ctx.getChild(0).text) {
            "&" -> value.andReduce()
            "|" -> value.orReduce()
            "^" -> value.xorReduce()
            else -> {
                errorListener.reportError(
                    ctx.getChild(ParserRuleContext::class.java, 0),
                    "Unknown operator ${ctx.getChild(0).text}"
                )
                return
            }
        }

        debug(ctx)

    }

    private fun checkFlat(vararg exprCtx: ExprContext, onError: (ExprContext) -> Unit): Boolean {
        return !exprCtx.map {
            val op = values[it] ?: throw IllegalArgumentException("exprCtx wasn't defined")
            if (!op.signalWidth.isFlatArray()) {
                onError(it)
                true
            } else {
                false
            }
        }.any { it }
    }

    private fun checkSimpleValue(vararg exprCtx: ExprContext, onError: (ExprContext) -> Unit): Boolean {
        return !exprCtx.map {
            val op = values[it] ?: throw IllegalArgumentException("exprCtx wasn't defined")
            if (op !is SimpleValue) {
                onError(it)
                true
            } else {
                false
            }
        }.any { it }
    }

    // checks that all expressions have the same widths or are flat arrays
    private fun checkFlatOrMatchingDims(vararg exprCtx: ExprContext, onError: (ExprContext) -> Unit): Boolean {
        if (exprCtx.isEmpty())
            return true

        val first = values[exprCtx.first()]?.signalWidth ?: throw IllegalArgumentException("exprCtx wasn't defined")

        return !exprCtx.map {
            val op = values[it]?.signalWidth ?: throw IllegalArgumentException("exprCtx wasn't defined")
            if (!((op.isFlatArray() && first.isFlatArray()) || op == first)) {
                onError(it)
                return@map true
            }
            return@map false
        }.any { it }
    }

    // checks if any widths are undefined and if so, flags any non-flat widths as errors
    private fun checkUndefinedMatchingDims(
        vararg exprCtx: ExprContext,
        onError: (ExprContext) -> Unit
    ): Boolean {
        val widths = exprCtx.map { values[it]?.signalWidth ?: throw IllegalArgumentException("exprCtx wasn't defined") }
        val hasUndefinedWidth = widths.any { it is UndefinedSimpleWidth }
        if (!hasUndefinedWidth)
            return true

        return !widths.mapIndexed { index, signalWidth ->
            if (!signalWidth.isFlatArray()) {
                onError(exprCtx[index])
                true
            } else {
                false
            }
        }.any { it }
    }

    override fun exitExprCompare(ctx: ExprCompareContext) {
        if (ctx.childCount != 3 || ctx.expr().size != 2) return

        // is constant if all operands are constant
        constant[ctx] = !ctx.expr().any { constant[it] != true }

        val op1 = values[ctx.expr(0)] ?: return
        val op2 = values[ctx.expr(1)] ?: return

        when (val operand = ctx.getChild(1).text) {
            "<", ">", "<=", ">=" -> {
                if (!checkFlat(*ctx.expr().toTypedArray()) {
                        errorListener.reportError(it, ErrorStrings.OP_NOT_NUMBER.format(operand))
                    }) return


                if (op1 is UndefinedValue || op2 is UndefinedValue) {
                    values[ctx] = UndefinedValue(ctx.text, ArrayWidth(1), false)
                    return
                }

                if (!checkSimpleValue(*ctx.expr().toTypedArray()) {
                        errorListener.reportError(it, ErrorStrings.OP_NOT_NUMBER.format(operand))
                    }) return

                op1 as SimpleValue
                op2 as SimpleValue

                values[ctx] = when (operand) {
                    "<" -> op1 isLessThan op2
                    ">" -> op1 isGreaterThan op2
                    "<=" -> op1 isLessOrEqualTo op2
                    ">=" -> op1 isGreaterOrEqualTo op2
                    else -> throw IllegalStateException()
                }
            }
            "==", "!=" -> {
                if (!checkUndefinedMatchingDims(ctx.expr(0), ctx.expr(1)) {
                        errorListener.reportError(it, ErrorStrings.OP_DIM_MISMATCH.format(operand))
                    }) return

                if (op1 is UndefinedValue || op2 is UndefinedValue) {
                    values[ctx] = UndefinedValue(ctx.text, ArrayWidth(1), false)
                    return
                }

                if (!checkFlatOrMatchingDims(*ctx.expr().toTypedArray()) {
                        errorListener.reportError(it, ErrorStrings.OP_DIM_MISMATCH.format(operand))
                    }) return

                values[ctx] = when (operand) {
                    "==" -> op1 isEqualTo op2
                    "!=" -> op1 isNotEqualTo op2
                    else -> throw IllegalStateException()
                }
            }
        }

        debug(ctx)

    }

    override fun exitExprLogical(ctx: ExprLogicalContext) {

    }

    override fun exitExprTernary(ctx: ExprTernaryContext) {

    }

    override fun exitFunction(ctx: FunctionContext) {

    }
}