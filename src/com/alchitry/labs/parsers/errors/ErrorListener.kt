package com.alchitry.labs.parsers.errors

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

interface ErrorListener {
    fun reportError(node: TerminalNode, message: String)
    fun reportWarning(node: TerminalNode, message: String)
    fun reportDebug(node: TerminalNode, message: String)
    fun reportError(ctx: ParseTree, message: String)
    fun reportWarning(ctx: ParseTree, message: String)
    fun reportDebug(ctx: ParseTree, message: String)
}

val dummyErrorListener = object : ErrorListener {
    override fun reportError(node: TerminalNode, message: String) {
    }

    override fun reportError(ctx: ParseTree, message: String) {
    }

    override fun reportWarning(node: TerminalNode, message: String) {
    }

    override fun reportWarning(ctx: ParseTree, message: String) {
    }

    override fun reportDebug(node: TerminalNode, message: String) {
    }

    override fun reportDebug(ctx: ParseTree, message: String) {
    }
}