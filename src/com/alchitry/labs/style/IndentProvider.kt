package com.alchitry.labs.style

import com.alchitry.labs.gui.StyledCodeEditor

interface IndentProvider {
    fun updateIndentList(editor: StyledCodeEditor)
    fun getTabs(lineNum: Int): Int
}