package com.alchitry.labs.parsers.lucidv2

import com.alchitry.labs.parsers.errors.ErrorListener
import com.alchitry.labs.parsers.lucid.parser.LucidLexer
import com.alchitry.labs.parsers.lucid.parser.LucidParser
import com.alchitry.labs.parsers.lucidv2.values.ArrayValue
import com.alchitry.labs.parsers.lucidv2.values.MutableBitList
import com.alchitry.labs.parsers.lucidv2.values.SimpleValue
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ExprParserTest {

    private class ErrorCollector : ErrorListener {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val debugs = mutableListOf<String>()

        fun clear() {
            errors.clear()
            warnings.clear()
            debugs.clear()
        }

        override fun reportError(node: TerminalNode, message: String) {
            errors.add("${node.text}: $message")
        }

        override fun reportError(ctx: ParserRuleContext, message: String) {
            errors.add("${ctx.text}: $message")
        }

        override fun reportWarning(node: TerminalNode, message: String) {
            warnings.add("${node.text}: $message")
        }

        override fun reportWarning(ctx: ParserRuleContext, message: String) {
            warnings.add("${ctx.text}: $message")
        }

        override fun reportDebug(node: TerminalNode, message: String) {
            debugs.add("${node.text}: $message")
        }

        override fun reportDebug(ctx: ParserRuleContext, message: String) {
            debugs.add("${ctx.text}: $message")
        }

        val hasNoErrors: Boolean get() = errors.isEmpty()
        val hasNoWarnings: Boolean get() = warnings.isEmpty()
        val hasErrors: Boolean get() = errors.isNotEmpty()
        val hasWarnings: Boolean get() = warnings.isNotEmpty()
    }

    private fun runExpr(expr: String, exprParser: ExprParser): ParseTree {
        val input: CharStream = CharStreams.fromString(expr)
        val lexer = LucidLexer(input)
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = LucidParser(tokens)
        parser.removeErrorListeners()

        parser.addParseListener(exprParser)

        return parser.expr()
    }

    @Test
    fun testNumbers() {
        val errorCollector = ErrorCollector()
        val exprParser = ExprParser(errorCollector)
        var tree = runExpr("5b11011", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("11011", 2, 5)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("hFE01", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("65025", 10, 16)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("8hFFF", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("255", 10, 8)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasWarnings)
        errorCollector.clear()

        tree = runExpr("152", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("152", 10, 8)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("0", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("0", 10, 1)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("20d12", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("12", 10, 20)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()
    }

    @Test
    fun testAddition() {
        val errorCollector = ErrorCollector()
        val exprParser = ExprParser(errorCollector)
        val tree = runExpr("5b1101 + 4b0010", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("1111", 2, 6)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
    }

    @Test
    fun testSubtraction() {
        val errorCollector = ErrorCollector()
        val exprParser = ExprParser(errorCollector)
        val tree = runExpr("5b1101 - 4b0010", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("1011", 2, 6)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
    }

    @Test
    fun testConcat() {
        val errorCollector = ErrorCollector()
        val exprParser = ExprParser(errorCollector)
        var tree = runExpr("c{b1101, b0010, 0}", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("110100100", 2, 9)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("c{{b1101}, b0010, 0}", exprParser)
        assertEquals(exprParser.values[tree], null)
        assert(errorCollector.hasErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("c{{b1101}, {b0010}, {0}}", exprParser)
        assertEquals(exprParser.values[tree], null)
        assert(errorCollector.hasErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("c{{b1101}, {b0010}, {4b0}}", exprParser)
        assertEquals(
            exprParser.values[tree], ArrayValue(
                listOf(
                    SimpleValue(MutableBitList("0000", 2, 4)),
                    SimpleValue(MutableBitList("0010", 2, 4)),
                    SimpleValue(MutableBitList("1101", 2, 4))
                )
            )
        )
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()
    }

    @Test
    fun testDup() {
        val errorCollector = ErrorCollector()
        val exprParser = ExprParser(errorCollector)
        var tree = runExpr("2x{0}", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("00", 2, 2)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("8x{2b10}", exprParser)
        assertEquals(exprParser.values[tree], SimpleValue(MutableBitList("1010101010101010", 2, 16)))
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("{8}x{2b10}", exprParser)
        assertEquals(exprParser.values[tree], null)
        assert(errorCollector.hasErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()
    }

    @Test
    fun testArray() {
        val errorCollector = ErrorCollector()
        val exprParser = ExprParser(errorCollector)
        var tree = runExpr("{0}", exprParser)
        assertEquals(
            exprParser.values[tree], ArrayValue(
                listOf(
                    SimpleValue(MutableBitList("0", 2, 1)),
                )
            )
        )
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("{{0}}", exprParser)
        assertEquals(
            exprParser.values[tree],
            ArrayValue(
                listOf(
                    ArrayValue(
                        listOf(
                            SimpleValue(MutableBitList("0", 2, 1)),
                        )
                    )
                )
            )
        )
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        // values of different sizes = error
        tree = runExpr("{0, 2b10, 2b11}", exprParser)
        assertEquals(exprParser.values[tree], null)
        assert(errorCollector.hasErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()

        tree = runExpr("{b00,b01,b10}", exprParser)
        assertEquals(
            exprParser.values[tree], ArrayValue(
                listOf(
                    SimpleValue(MutableBitList("10", 2, 2)),
                    SimpleValue(MutableBitList("01", 2, 2)),
                    SimpleValue(MutableBitList("00", 2, 2)),
                )
            )
        )
        assert(errorCollector.hasNoErrors)
        assert(errorCollector.hasNoWarnings)
        errorCollector.clear()
    }
}