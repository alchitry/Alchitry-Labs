package com.alchitry.labs.gui.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import java.util.regex.MatchResult
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.absoluteValue

class Search(text: String, expression: String, regex: Boolean, caseSensitive: Boolean) {
    var lastResult: MatchResult? = null
        private set

    val pattern: Pattern = if (regex)
        Pattern.compile(expression)
    else
        Pattern.compile(Pattern.quote(expression), if (caseSensitive) 0 else Pattern.CASE_INSENSITIVE)

    val deferredMatches = GlobalScope.async(Dispatchers.Default) {
        val matchList = ArrayList<MatchResult>()
        val matcher: Matcher = pattern.matcher(text)
        while (matcher.find()) matchList.add(matcher.toMatchResult())
        matchList
    }

    suspend fun next(start: Int): MatchResult? {
        return find(start, true)
    }

    suspend fun previous(start: Int): MatchResult? {
        return find(start, false)
    }

    private suspend fun find(start: Int, forward: Boolean = true): MatchResult? {
        val matches = if (forward) deferredMatches.await() else deferredMatches.await().asReversed()

        val result = (matches.binarySearch {
            if (forward) it.start() - start else start - it.end()
        }.absoluteValue - 1).coerceAtLeast(0)

        lastResult = when {
            result < matches.size -> matches[result]
            matches.size > 0 -> matches[0]
            else -> null
        }
        return lastResult
    }


}