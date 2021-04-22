package com.alchitry.labs.parsers.lucidv2.values

import java.math.BigInteger
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.ceil

interface BitArray : List<BitValue> {
    val signed: Boolean

    fun toBigInt(): BigInteger {
        check(isNumber()) { "The value is not a number (it contains x and z values)" }
        val bytes = ByteArray(ceil((size + if (signed) 0 else 1).toDouble() / 8.0).toInt()) // if not signed need extra 0 sign bit
        if (signed && msb() == BitValue.B1) // sign extension
            Arrays.fill(bytes, 255.toByte()) else Arrays.fill(bytes, 0.toByte())
        repeat(size) { i ->
            val idx: Int = i / 8
            bytes[bytes.size - 1 - idx] = if (this[i] == BitValue.B1)
                bytes[bytes.size - 1 - idx] or (1 shl i % 8).toByte()
            else
                bytes[bytes.size - 1 - idx] and (1 shl i % 8).inv().toByte()
        }
        return BigInteger(bytes)
    }

    operator fun not(): BitValue {
        return isTrue().not()
    }

    fun isTrue(): BitValue {
        var hasX = false
        this.forEach {
            if (it == BitValue.B1)
                return BitValue.B1
            else if (it == BitValue.Bx || it == BitValue.Bz)
                hasX = true
        }
        return if (hasX) BitValue.Bx else BitValue.B0
    }

    fun invert(): BitArray {
        return MutableBitArray(this.signed, this.size) { !this[it] }
    }

    private inline fun doOp(b: BitArray, crossinline op: (BitValue, BitValue) -> BitValue): BitArray {
        val size = this.size.coerceAtLeast(b.size)
        return MutableBitArray(this.signed && b.signed, size) { i ->
            val op1 = if (i < this.size) this[i] else BitValue.B0
            val op2 = if (i < b.size) b[i] else BitValue.B0
            op(op1, op2)
        }
    }

    infix fun or(b: BitArray): BitArray {
        return doOp(b) { b1, b2 -> b1 or b2 }
    }

    infix fun and(b: BitArray): BitArray {
        return doOp(b) { b1, b2 -> b1 and b2 }
    }

    infix fun xor(b: BitArray): BitArray {
        return doOp(b) { b1, b2 -> b1 xor b2 }
    }

    infix fun nor(b: BitArray): BitArray {
        return doOp(b) { b1, b2 -> b1 nor b2 }
    }

    infix fun nand(b: BitArray): BitArray {
        return doOp(b) { b1, b2 -> b1 nand b2 }
    }

    infix fun xnor(b: BitArray): BitArray {
        return doOp(b) { b1, b2 -> b1 xnor b2 }
    }

    infix fun shl(n: Int): BitArray {
        return MutableBitArray(this.signed, n) { BitValue.B0 }.also { it.addAll(this) }
    }

    infix fun ushl(n: Int): BitArray {
        return MutableBitArray(false, n) { BitValue.B0 }.also { it.addAll(this) }
    }

    infix fun shr(n: Int): BitArray {
        val value = MutableBitArray(signed)
        val signBit = if (signed) last() else BitValue.B0
        value.addAll(subList(n, size))
        repeat(n) { value.add(signBit) }
        return value
    }

    infix fun ushr(n: Int): BitArray {
        val value = MutableBitArray(false)
        value.addAll(subList(n, size))
        repeat(n) { value.add(BitValue.B0) }
        return value
    }

    fun equal(b: BitArray): BitValue {
        val size = this.size.coerceAtLeast(b.size)
        val se1 = if (this.signed) this[this.size - 1] else BitValue.B0
        val se2 = if (b.signed) b[b.size - 1] else BitValue.B0
        for (i in 0 until size) {
            if (i < this.size) {
                if (i < b.size) {
                    if (this[i] != b[i]) return BitValue.B0
                } else {
                    if (this[i] != se2) return BitValue.B0
                }
            } else {
                if (b[i] != se1) return BitValue.B0
            }
        }
        return BitValue.B1
    }

    fun isNumber(): Boolean {
        this.forEach { if (it != BitValue.B0 && it != BitValue.B1) return false }
        return true
    }

    fun toMutableBitArray(): MutableBitArray = MutableBitArray(this)
}

class MutableBitArray(override var signed: Boolean = false, width: Int = 0) : ArrayList<BitValue>(width), BitArray {
    constructor(signed: Boolean, size: Int, init: (Int) -> BitValue) : this(signed, size) {
        for (i in 0 until size) add(init(i))
    }

    constructor(str: String, radix: Int = 10, signed: Boolean = false) : this(signed, 0) {
        when (radix) {
            10 -> fromBigInt(BigInteger(str))
            16 -> set(str, 16, str.length * 4, signed)
            2 -> set(str, 2, str.length, signed)
            256 -> set(str, 256, str.length * 8, signed)
            else -> throw IllegalArgumentException("Radix must be 2, 10, 16, or 256")
        }
    }

    constructor(str: String, radix: Int = 10, width: Int, signed: Boolean = false) : this(signed, width) {
        set(str, radix, width, signed)
    }

    constructor(value: Long, width: Int, signed: Boolean = false) : this(signed, width) {
        repeat(width) {
            add(if ((value and (1 shr it).toLong()) != 0.toLong()) BitValue.B1 else BitValue.B0)
        }
    }

    constructor(value: BitValue, width: Int = 1, signed: Boolean = false) : this(signed, width) {
        repeat(width) { add(value) }
    }

    constructor(value: BigInteger) : this() {
        fromBigInt(value)
    }

    constructor(value: BigInteger, width: Int, signed: Boolean = value.signum() == -1) : this(width = width) {
        fromBigInt(value, width, signed)
    }

    constructor(signed: Boolean, bits: List<BitValue>) : this(signed, bits.size) {
        addAll(bits)
    }

    constructor(bitArray: BitArray) : this(bitArray.signed, bitArray)

    private fun fromBigInt(bigInt: BigInteger) {
        var w = bigInt.bitLength() // doesn't include sign bit

        if (bigInt.signum() == -1) w++
        w = w.coerceAtLeast(1)
        fromBigInt(bigInt, w)
    }

    private fun fromBigInt(bigInt: BigInteger, width: Int, signed: Boolean = bigInt.signum() == -1) {
        val bList = bigInt.toByteArray()

        for (i in 0 until width) {
            val idx = i / 8
            val offset = i % 8
            var b: Byte = 0
            if (bList.size > idx)
                b = bList[bList.size - 1 - idx]
            else if (signed)
                b = (if (bList[0].toInt() and (1 shl 7) != 0) -1 else 0).toByte() // sign extend

            if (b and (1 shl offset).toByte() != 0.toByte()) {
                add(BitValue.B1)
            } else {
                add(BitValue.B0)
            }
        }
    }

    private fun set(str: String, radix: Int, width: Int, signed: Boolean) {
        this.signed = signed
        val strl = str.toLowerCase()
        when (radix) {
            10 -> fromBigInt(BigInteger(strl), width)
            16 -> {
                var idx = 0
                while (idx < width) {
                    val charIdx = idx / 4
                    val bitIdx = idx % 4
                    var c = '0'
                    if (strl.length > charIdx) c = strl[strl.length - 1 - charIdx] else if (strl[0] == 'x' || strl[0] == 'z') c = strl[0]
                    if (c == 'x') {
                        add(BitValue.Bx)
                    } else if (c == 'z') {
                        add(BitValue.Bz)
                    } else {
                        val v = c.toString().toInt(16)
                        if (v and (1 shl bitIdx) != 0) {
                            add(BitValue.B1)
                        } else {
                            add(BitValue.B0)
                        }
                    }
                    idx++
                }
            }
            2 -> {
                var idx = 0
                while (idx < width) {
                    var c = '0'
                    if (strl.length > idx) c = strl[strl.length - 1 - idx] else if (strl[0] == 'x' || strl[0] == 'z') c = strl[0]
                    when (c) {
                        '0' -> add(BitValue.B0)
                        '1' -> add(BitValue.B1)
                        'x' -> add(BitValue.Bx)
                        'z' -> add(BitValue.Bz)
                        else -> throw NumberFormatException()
                    }
                    idx++
                }
            }
            256 -> {
                var idx = 0
                while (idx < width) {
                    val charIdx = idx / 8
                    val bitIdx = idx % 8
                    var c = 0.toChar()
                    if (str.length > charIdx) c = str[str.length - 1 - charIdx]
                    if (c.toInt() and (1 shl bitIdx) != 0) {
                        add(BitValue.B1)
                    } else {
                        add(BitValue.B0)
                    }
                    idx++
                }
            }
            else -> throw IllegalArgumentException("Radix must be 16, 10, or 2")
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (signed)
            sb.append("signed ")
        sb.append('{')
        for (i in this.indices.reversed()) {
            val bv = this[i]
            sb.append(bv.toString().substring(1))
        }
        sb.append('}')
        return sb.toString()
    }
}

fun List<BitValue>.lsb() = first()
fun List<BitValue>.msb() = last()