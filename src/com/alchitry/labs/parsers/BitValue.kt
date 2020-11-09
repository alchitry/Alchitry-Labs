package com.alchitry.labs.parsers

import com.alchitry.labs.Util
import java.util.*

enum class BitValue {
    B0, B1, Bx, Bz;

    fun or(b: BitValue): BitValue {
        if (this == B1 || b == B1) return B1
        return if (this == Bz || b == Bz || this == Bx || b == Bx) Bx else B0
    }

    fun and(b: BitValue): BitValue {
        if (this == B1 && b == B1) return B1
        return if (this == Bz || b == Bz || this == Bx || b == Bx) Bx else B0
    }

    fun xor(b: BitValue): BitValue {
        if (this == B1 != (b == B1)) return B1
        return if (this == Bz || b == Bz || this == Bx || b == Bx) Bx else B0
    }

    val char: Char
        get() = when (this) {
            B0 -> '0'
            B1 -> '1'
            Bx -> 'x'
            Bz -> 'z'
            else -> {
                Util.logger.severe("Unknown bit type $this")
                '?'
            }
        }

    fun nor(b: BitValue): BitValue {
        return or(b).not()
    }

    fun nand(b: BitValue): BitValue {
        return and(b).not()
    }

    fun xnor(b: BitValue): BitValue {
        return xor(b).not()
    }

    operator fun not(): BitValue {
        if (this == B1) return B0
        return if (this == B0) B1 else this
    }

    fun invert(): BitValue? {
        return when (this) {
            B0 -> B1
            B1 -> B0
            Bx -> Bx
            Bz -> Bz
        }
        return null
    }

    companion object {
        fun or(b1: BitValue, b2: BitValue): BitValue {
            return b1.or(b2)
        }

        fun and(b1: BitValue, b2: BitValue): BitValue {
            return b1.and(b2)
        }

        fun xor(b1: BitValue, b2: BitValue): BitValue {
            return b1.xor(b2)
        }

        fun nor(b1: BitValue, b2: BitValue): BitValue {
            return b1.nor(b2)
        }

        fun nand(b1: BitValue, b2: BitValue): BitValue {
            return b1.nand(b2)
        }

        fun xnor(b1: BitValue, b2: BitValue): BitValue {
            return b1.xnor(b2)
        }

        @JvmStatic
        fun or(b1: ArrayList<BitValue>, b2: ArrayList<BitValue>): ArrayList<BitValue> {
            val size = Math.max(b1.size, b2.size)
            val bv = ArrayList<BitValue>(size)
            for (i in 0 until size) {
                if (i < b1.size) {
                    if (i < b2.size) {
                        bv.add(b1[i].or(b2[i]))
                    } else {
                        bv.add(b1[i].or(B0))
                    }
                } else {
                    bv.add(B0.or(b2[i]))
                }
            }
            return bv
        }

        @JvmStatic
        fun and(b1: ArrayList<BitValue>, b2: ArrayList<BitValue>): ArrayList<BitValue> {
            val size = Math.max(b1.size, b2.size)
            val bv = ArrayList<BitValue>(size)
            for (i in 0 until size) {
                if (i < b1.size) {
                    if (i < b2.size) {
                        bv.add(b1[i].and(b2[i]))
                    } else {
                        bv.add(b1[i].and(B0))
                    }
                } else {
                    bv.add(B0.and(b2[i]))
                }
            }
            return bv
        }

        @JvmStatic
        fun xor(b1: ArrayList<BitValue>, b2: ArrayList<BitValue>): ArrayList<BitValue> {
            val size = Math.max(b1.size, b2.size)
            val bv = ArrayList<BitValue>(size)
            for (i in 0 until size) {
                if (i < b1.size) {
                    if (i < b2.size) {
                        bv.add(b1[i].xor(b2[i]))
                    } else {
                        bv.add(b1[i].xor(B0))
                    }
                } else {
                    bv.add(B0.xor(b2[i]))
                }
            }
            return bv
        }

        @JvmStatic
        fun nor(b1: ArrayList<BitValue>, b2: ArrayList<BitValue>): ArrayList<BitValue?> {
            return invert(or(b1, b2))
        }

        @JvmStatic
        fun nand(b1: ArrayList<BitValue>, b2: ArrayList<BitValue>): ArrayList<BitValue?> {
            return invert(and(b1, b2))
        }

        @JvmStatic
        fun xnor(b1: ArrayList<BitValue>, b2: ArrayList<BitValue>): ArrayList<BitValue?> {
            return invert(xor(b1, b2))
        }

        @JvmStatic
        fun equal(b1: ArrayList<BitValue>, b2: ArrayList<BitValue>, s1: Boolean, s2: Boolean): BitValue {
            val size = Math.max(b1.size, b2.size)
            val se1 = if (s1) b1[b1.size - 1] else B0
            val se2 = if (s2) b2[b2.size - 1] else B0
            for (i in 0 until size) {
                if (i < b1.size) {
                    if (i < b2.size) {
                        if (b1[i] != b2[i]) return B0
                    } else {
                        if (b1[i] != se2) return B0
                    }
                } else {
                    if (b2[i] != se1) return B0
                }
            }
            return B1
        }

        @JvmStatic
        fun invert(`val`: ArrayList<BitValue>): ArrayList<BitValue?> {
            val bv = ArrayList<BitValue?>(`val`.size)
            for (b in `val`) bv.add(b.invert())
            return bv
        }

        @JvmStatic
        fun not(`val`: ArrayList<BitValue>): BitValue {
            var hasX = false
            for (b in `val`) {
                if (b == B1) return B0 else if (b == Bx || b == Bz) hasX = true
            }
            return if (hasX) Bx else B1
        }

        @JvmStatic
        fun isZero(`val`: ArrayList<BitValue>): Boolean {
            for (b in `val`) if (b != B0) return false
            return true
        }

        @JvmStatic
        fun toString(`val`: ArrayList<BitValue>): String {
            val sb = StringBuilder()
            sb.append('{')
            for (i in `val`.indices.reversed()) {
                val bv = `val`[i]
                sb.append(bv.toString().substring(1))
            }
            sb.append('}')
            return sb.toString()
        }
    }
}