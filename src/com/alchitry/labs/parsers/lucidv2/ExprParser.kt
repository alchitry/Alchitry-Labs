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
                        repeat(valueString.length) { elements.add(SimpleValue(MutableBitArray(valueString[it].toLong(), 8))) }
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

        if (!op1.signalWidth.isFlatArray()) {
            errorListener.reportError(ctx.expr(0), if (operand == "+") ErrorStrings.ADD_MULTI_DIM else ErrorStrings.SUB_MULTI_DIM)
            error = true
        }

        if (!op2.signalWidth.isFlatArray()) {
            errorListener.reportError(ctx.expr(1), if (operand == "+") ErrorStrings.ADD_MULTI_DIM else ErrorStrings.SUB_MULTI_DIM)
            error = true
        }

        if (error) return

        if (op1 is UndefinedValue || op2 is UndefinedValue) {
            val op1Width = op1.signalWidth
            val op2Width = op2.signalWidth
            if (op1Width is ArrayWidth && op2Width is ArrayWidth)
                values[ctx] = UndefinedValue(ctx.text, ArrayWidth(op1Width.size.coerceAtLeast(op2Width.size) + 1))
            else
                values[ctx] = UndefinedValue(ctx.text)
        } else {
            if (op1 !is SimpleValue || op2 !is SimpleValue)
                error("One (or both) of the operands isn't a simple array. This shouldn't be possible.")

            val width = op1.bits.size.coerceAtLeast(op2.bits.size) + 1

            values[ctx] = when {
                !op1.isNumber() || !op2.isNumber() -> SimpleValue(MutableBitArray(BitValue.Bx, width))
                operand == "+" -> SimpleValue(MutableBitArray(op1.bits.toBigInt().add(op2.bits.toBigInt()), width))
                else -> SimpleValue(MutableBitArray(op1.bits.toBigInt().subtract(op2.bits.toBigInt()), width))
            }
        }
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
            values[ctx] = UndefinedValue(ctx.text,
                    width = if (valWidth is ArrayWidth) {
                        ArrayWidth(valWidth.size * dupTimes, valWidth.next)
                    } else {
                        UndefinedSimpleWidth()
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
            val bits = MutableBitArray()
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
        debug(ctx)
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