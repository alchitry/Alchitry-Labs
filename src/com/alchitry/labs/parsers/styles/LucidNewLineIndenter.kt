package com.alchitry.labs.parsers.styles

import com.alchitry.labs.Util.println
import com.alchitry.labs.Util.reportException
import com.alchitry.labs.gui.StyledCodeEditor
import com.alchitry.labs.gui.util.UndoRedo
import com.alchitry.labs.parsers.lucid.indent.LucidIndentBaseListener
import com.alchitry.labs.parsers.lucid.indent.LucidIndentLexer
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser
import com.alchitry.labs.parsers.lucid.indent.LucidIndentParser.*
import com.alchitry.labs.style.IndentProvider
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.eclipse.swt.custom.ExtendedModifyEvent
import org.eclipse.swt.custom.ExtendedModifyListener
import org.eclipse.swt.events.VerifyEvent
import org.eclipse.swt.events.VerifyListener
import java.util.*

class LucidNewLineIndenter(private val editor: StyledCodeEditor, private val undo: UndoRedo) : LucidIndentBaseListener(), VerifyListener, ExtendedModifyListener, IndentProvider {
    private var tokens: CommonTokenStream? = null
    private var tabs = IntArray(10)
    private var lineOffsets = IntArray(10)
    private var text: String? = null

    private fun updateLineOffsets(text: String) {
        var pos = 0
        var line = 0
        lineOffsets[line++] = pos
        while ((text.indexOf("\n", pos) + 1).also { pos = it } != 0) {
            lineOffsets[line++] = pos - 1
        }
        lineOffsets[line] = Int.MAX_VALUE
    }

    private fun getLineAtOffset(offset: Int): Int {
        var line = 1
        while (lineOffsets[line++] < offset);
        return line - 2
    }

    private fun updateIndents(text: String) {
        updateLineOffsets(text)
        Arrays.fill(tabs, 0)
        this.text = text
        val charStream: CharStream = CharStreams.fromString(text)
        val lexer = LucidIndentLexer(charStream)
        tokens = CommonTokenStream(lexer)
        val parser = LucidIndentParser(tokens)
        parser.addParseListener(this)
        parser.removeErrorListeners() // don't print errors
        parser.lucid()
    }

    private fun addIndentsToLines(start: Int, end: Int, num: Int = 2) {
        for (i in start + 1..end) {
            tabs[i] += num
        }
    }

    private fun addIndents(children: List<ParseTree>?, exclude: Int = 0) {
        if (children != null && children.size > 2 + exclude) {
            val interval0 = children[0].sourceInterval
            val interval1 = children[children.size - 1 - exclude].sourceInterval
            val interval2 = children[children.size - 2 - exclude].sourceInterval
            if (interval0 === Interval.INVALID || interval1 === Interval.INVALID || interval2 === Interval.INVALID) return
            val a = interval0.a
            val b = interval1.a.coerceAtLeast(interval1.b)
            val c = interval2.a.coerceAtLeast(interval2.b)
            if (a < 0 || b < 0 || c < 0) return
            val start = tokens!![a].line - 1
            val end = tokens!![b].line - 1
            var end2 = end
            if (c >= 0) end2 = tokens!![c].line - 1
            if (end > end2) addIndentsToLines(start, end - 1) else addIndentsToLines(start, end)
        }
    }

    private fun addIndentsToEnd(children: List<ParseTree>?, exclude: Int = 0) {
        if (children != null && children.size > 1 + exclude) {
            val a = children[0].sourceInterval.a
            val b = children[children.size - 1 - exclude].sourceInterval.b
            val start = tokens!![a]
            val end = tokens!![b]
            addIndentsToLines(start.line - 1, end.line - 1)
        }
    }

    private fun indentComment(node: TerminalNode) {
        val a = node.sourceInterval.a
        val b = node.sourceInterval.b
        val start = tokens!![a]
        val end = tokens!![b]
        var endIdx = end.stopIndex - 2 // skip * and /
        while (Character.isWhitespace(text!![endIdx--]));
        addIndentsToLines(start.line - 1, getLineAtOffset(endIdx), 3)
    }

    override fun exitModule(ctx: LucidIndentParser.ModuleContext) {
        val start = tokens!![ctx.children[0].sourceInterval.a]
        for (ic in ctx.indent()) {
            if (ic.getChild(0).text == "{") {
                val end = tokens!![ic.getChild(0).sourceInterval.b]
                addIndentsToLines(start.line - 1, end.line - 1)
                break
            }
        }
    }

    override fun exitAlways_line(ctx: Always_lineContext) {
        var exclude = 0
        if (ctx.else_block() != null) exclude = 1
        if (ctx.block() != null) if (ctx.block().javaClass == SingleBlockContext::class.java || ctx.block().javaClass == AlwaysBlockContext::class.java) addIndentsToEnd(ctx.children, exclude) else addIndents(ctx.block().children) else addIndents(ctx.children)
    }

    override fun exitElse_block(ctx: Else_blockContext) {
        if (ctx.block().javaClass == AlwaysBlockContext::class.java && (ctx.block() as AlwaysBlockContext).always_line().getChild(0).text == "if") {
            return  // if is else-if don't indent else
        }
        if (ctx.block() != null) if (ctx.block().javaClass == SingleBlockContext::class.java || ctx.block().javaClass == AlwaysBlockContext::class.java) addIndentsToEnd(ctx.children) else addIndents(ctx.block().children) else addIndents(ctx.children)
    }

    override fun exitIndent(ctx: IndentContext) {
        if (ctx.always_line() != null) return
        if (ctx.block() != null) if (ctx.block().javaClass == SingleBlockContext::class.java || ctx.block().javaClass == AlwaysBlockContext::class.java) addIndentsToEnd(ctx.children) else addIndents(ctx.block().children) else if (ctx.BLOCK_COMMENT() != null) indentComment(ctx.BLOCK_COMMENT()) else addIndents(ctx.children)
    }

    override fun exitElem(ctx: LucidIndentParser.ElemContext) {
        addIndentsToEnd(ctx.children)
    }

    private fun resizeBuffers(lines: Int) {
        if (tabs.size <= lines) {
            tabs = IntArray(lines * 2)
            lineOffsets = IntArray(lines * 2)
        }
    }

    override fun verifyText(e: VerifyEvent) {
        if (e.text == "\n" || e.text == "\r\n") {
            try {
                resizeBuffers(editor.lineCount + 1)
                val sb = StringBuilder(editor.text)
                sb.replace(e.start, e.end, e.text + "l;")
                updateIndents(sb.toString())
                val indents = tabs[getLineAtOffset(e.end + 1)]
                val newText = StringBuilder(e.text)
                for (i in 0 until indents) {
                    newText.append(' ')
                }
                e.text = newText.toString()
            } catch (ex: Exception) {
                reportException(ex, "Failed to add indents to new line!")
                println("Auto indenting a new line failed! Check the logs for more details.", true)
                System.err.println(ex.toString())
            }
        }
    }

    private fun countSpaces(line: String): Int {
        var ct = 0
        var idx = 0
        idx = 0
        while (idx < line.length) {
            val c = line[idx]
            if (c == ' ') ct++ else if (c == '\t') ct += 2 else break
            idx++
        }
        return ct
    }

    private fun unindent(e: ExtendedModifyEvent) {
        resizeBuffers(editor.lineCount)
        val text = editor.text
        updateIndents(text)
        val lineNum = getLineAtOffset(e.start)
        val indents = tabs[lineNum]
        val line = editor.getLine(lineNum)
        val curIndents = countSpaces(line)
        if (indents != curIndents) {
            val newText = String(CharArray(indents)).replace("\u0000", " ") // create string of all spaces
            editor.replaceTextRange(editor.getOffsetAtLine(lineNum), curIndents, newText)
        }
    }

    override fun updateIndentList(editor: StyledCodeEditor) {
        resizeBuffers(editor.lineCount + 1)
        updateIndents(editor.text)
    }

    override fun getTabs(lineNum: Int): Int {
        return tabs[lineNum]
    }

    override fun modifyText(e: ExtendedModifyEvent) {
        if (e.length == 1 && !undo.isEditing) {
            val editedText = editor.getText(e.start, e.start)
            if (editedText.matches(Regex("[}\\])/]"))) {
                val line = editor.getLine(editor.getLineAtOffset(e.start))
                if (line.trim { it <= ' ' }.matches(Regex("([}\\])]|\\*/)"))) // only indent an empty line
                    unindent(e)
            } else if (editedText == ":") {
                val line = editor.getLine(editor.getLineAtOffset(e.start)).trim { it <= ' ' }
                for (i in line.length - 2 downTo 0) if (!Character.isLetterOrDigit(line[i]) && line[i] != '_' && line[i] != '.') return
                unindent(e)
            }
        }
    }
}