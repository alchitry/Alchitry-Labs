package com.alchitry.labs.parsers.tools.lucid

import java.lang.Integer.max
import java.lang.Integer.min

class ArrayBounds() {
    var min = 0
        private set
    var max = 0
        private set

    constructor(i: Int, j: Int) : this() {
        setValues(i, j)
    }

    fun setValues(i: Int, j: Int) {
        max = max(i, j)
        min = min(i, j)
    }

    fun fitInWidth(w: Int): Boolean {
        return max < w && min >= 0
    }

    fun testBounds(ab: ArrayBounds): Boolean {
        if (max < ab.max) return false
        return min <= ab.min
    }

    fun testBounds(i: Int, j: Int): Boolean {
        val bmax = if (i > j) i else j
        val bmin = if (i > j) j else i
        if (max < bmax) return false
        return min <= bmin
    }

    fun testBounds(i: Int): Boolean {
        return !(i > max || i < min)
    }

    val width: Int
        get() = max - min + 1

    override fun toString(): String {
        return "[$max, $min]"
    }
}