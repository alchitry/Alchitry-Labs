package com.alchitry.labs.parsers.lucidv2

import com.alchitry.labs.parsers.errors.DummyErrorListener
import com.alchitry.labs.parsers.errors.ErrorListener
import com.alchitry.labs.parsers.errors.ErrorStrings
import com.alchitry.labs.parsers.lucid.parser.LucidBaseListener
import com.alchitry.labs.parsers.lucid.parser.LucidParser
import com.alchitry.labs.parsers.lucidv2.values.ArrayValue
import com.alchitry.labs.parsers.lucidv2.values.BitArray
import com.alchitry.labs.parsers.lucidv2.values.SimpleValue
import com.alchitry.labs.parsers.lucidv2.values.Value
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.apache.commons.text.StringEscapeUtils

class ExprParser(val errorListener: ErrorListener = DummyErrorListener) : LucidBaseListener() {
    val values = ParseTreeProperty<Value>()
    val constant = ParseTreeProperty<Boolean>()

    override fun exitNumber(ctx: LucidParser.NumberContext) {
        constant.put(ctx, true)
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
                values.put(ctx, value)
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
        values.put(ctx, value)
    }
}