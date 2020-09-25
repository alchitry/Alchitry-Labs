package com.alchitry.labs.gui.util

import java.util.*
import java.util.regex.MatchResult
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class Search(text: String, pattern: String, regex: Boolean, caseSensitive: Boolean) {
    var pattern: Pattern? = null
        private set
    val matches = ArrayList<MatchResult>()
    var error = false
        private set
    var lastResult: MatchResult? = null
        private set

    private fun first(): MatchResult? {
        return if (matches.size > 0) matches[0] else null
    }

    private fun last(): MatchResult? {
        return if (matches.size > 0) matches[matches.size - 1] else null
    }

    fun next(start: Int): MatchResult? {
        matches.forEach {
            if (it.start() >= start) return it
        }
        lastResult = first()
        return lastResult
    }

    fun previous(start: Int): MatchResult? {
        matches.asReversed().forEach {
            if (it.end() <= start) return it
        }
        lastResult = last()
        return lastResult
    }

    init {
        try {
            if (regex)
                this.pattern = Pattern.compile(pattern)
            else
                this.pattern = Pattern.compile(Pattern.quote(pattern), if (caseSensitive) 0 else Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = this.pattern!!.matcher(text)
            while (matcher.find()) matches.add(matcher.toMatchResult())
        } catch (e: PatternSyntaxException) {
            error = true
        }
    }
}