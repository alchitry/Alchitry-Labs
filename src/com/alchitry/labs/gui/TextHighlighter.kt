package com.alchitry.labs.gui

import com.alchitry.labs.gui.util.Search
import com.alchitry.labs.style.StyleUtil
import com.alchitry.labs.style.StyleUtil.StyleMerger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swt.SWT
import org.eclipse.swt.custom.StyleRange
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import java.util.regex.MatchResult

class TextHighlighter(private val editor: StyledText) : CachedStyleListner(), ModifyListener {
    private var matchList: List<MatchResult>? = null

    fun setText(text: String) {
        GlobalScope.launch(Dispatchers.Default) {
            try {
                val matches = Search(editor.text, text, regex = false, caseSensitive = false).deferredMatches.await()
                launch(Dispatchers.SWT) {
                    setMatches(matches)
                }
            } catch (e: Exception) {

            }
        }
    }

    fun setMatches(matches: List<MatchResult>?) {
        lock.acquireUninterruptibly()
        matchList = matches
        GlobalScope.launch(Dispatchers.Default) {
            invalidateStyles()
            styles.clear()
            matchList?.let {
                for (m in it) {
                    val style = StyleRange()
                    style.background = Theme.highlightedWordColor
                    style.start = m.start()
                    style.length = m.end() - m.start()
                    styles.add(style)
                }
            }
            lock.release()
            launch(Dispatchers.SWT) { editor.redraw() }
        }
    }

    override fun getStyleMerger(): StyleMerger {
        return StyleMerger { dest, src ->
            val bg = dest.background
            StyleUtil.copy(dest, src)
            dest.background = bg
        }
    }

    override fun modifyText(e: ModifyEvent) {
        setMatches(matchList) // redraw
    }
}