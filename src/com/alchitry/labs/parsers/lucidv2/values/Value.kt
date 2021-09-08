package com.alchitry.labs.parsers.lucidv2.values

sealed class Value {
    companion object {
        val Zero = BitValue.B0.toSimpleValue()
        val One = BitValue.B1.toSimpleValue()
    }

    open val signed = false

    fun isNumber(): Boolean {
        when (this) {
            is ArrayValue -> {
                this.elements.forEach { if (!it.isNumber()) return false }
                return true
            }
            is SimpleValue -> {
                return this.bits.isNumber()
            }
            is StructValue -> {
                this.elements.forEach { (k, v) -> if (!v.isNumber()) return false }
                return true
            }
            is UndefinedValue -> {
                return false
            }
        }
    }

    val signalWidth: SignalWidth
        get() {
            return when (this) {
                is SimpleValue -> ArrayWidth(bits.size)
                is ArrayValue -> ArrayWidth(elements.size, elements[0].signalWidth)
                is StructValue -> StructWidth(type)
                is UndefinedValue -> width
            }
        }

    fun invert(): Value {
        return when (this) {
            is SimpleValue -> SimpleValue(bits.invert())
            is ArrayValue -> ArrayValue((List(elements.size) { elements[it].invert() }))
            is StructValue -> StructValue(type, elements.mapValues { (k, v) -> v.invert() })
            is UndefinedValue -> this
        }
    }

    private fun isTrueBit(): BitValue {
        return when (this) {
            is SimpleValue -> !bits
            is ArrayValue -> MutableBitArray(false, elements.size) { elements[it].isTrueBit() }.isTrue()
            is StructValue -> {
                if (isComplete())
                    MutableBitArray().also { elements.forEach { e -> it.add(e.value.isTrueBit()) } }.isTrue()
                else
                    BitValue.Bx
            }
            is UndefinedValue -> BitValue.Bx
        }
    }

    fun not(): SimpleValue {
        return isTrueBit().not().toSimpleValue()
    }

    fun isTrue(): SimpleValue {
        return isTrueBit().toSimpleValue()
    }

    infix fun and(other: Value): Value {
        check(other::class == this::class) { "And operator can't be used on different value types" }
        return when (this) {
            is SimpleValue -> SimpleValue(bits and (other as SimpleValue).bits)
            is ArrayValue -> ArrayValue(List(elements.size) { elements[it] and (other as ArrayValue).elements[it] })
            is StructValue -> {
                if (isComplete() && (other as StructValue).isComplete()) {
                    StructValue(type, elements.mapValues { (k, v) -> v and (other.elements[k] as Value) })
                } else {
                    error("Both structs are not complete")
                }
            }
            is UndefinedValue -> this
        }
    }

    infix fun or(other: Value): Value {
        check(other::class == this::class) { "Or operator can't be used on different value types" }
        return when (this) {
            is SimpleValue -> SimpleValue(bits or (other as SimpleValue).bits)
            is ArrayValue -> ArrayValue(List(elements.size) { elements[it] or (other as ArrayValue).elements[it] })
            is StructValue -> {
                if (isComplete() && (other as StructValue).isComplete()) {
                    StructValue(type, elements.mapValues { (k, v) -> v or (other.elements[k] as Value) })
                } else {
                    error("Both structs are not complete")
                }
            }
            is UndefinedValue -> this
        }
    }

    infix fun xor(other: Value): Value {
        check(other::class == this::class) { "Xor operator can't be used on different value types" }
        return when (this) {
            is SimpleValue -> SimpleValue(bits xor (other as SimpleValue).bits)
            is ArrayValue -> ArrayValue(List(elements.size) { elements[it] xor (other as ArrayValue).elements[it] })
            is StructValue -> {
                if (isComplete() && (other as StructValue).isComplete()) {
                    StructValue(type, elements.mapValues { (k, v) -> v xor (other.elements[k] as Value) })
                } else {
                    error("Both structs are not complete")
                }
            }
            is UndefinedValue -> this
        }
    }

    infix fun isNotEqualTo(other: Value): SimpleValue {
        return xor(other).orReduce()
    }

    infix fun isEqualTo(other: Value): SimpleValue {
        return isNotEqualTo(other).lsb.not().toSimpleValue()
    }

    private fun reduceOp(op: (BitArray) -> BitValue): BitValue {
        return when (this) {
            is SimpleValue -> op(bits)
            is ArrayValue -> op(MutableBitArray(false, elements.size) { elements[it].reduceOp(op) })
            is StructValue -> {
                if (isComplete()) {
                    op(MutableBitArray(false).also { it.addAll(elements.map { (k, v) -> v.reduceOp(op) }) })
                } else {
                    BitValue.Bx
                }
            }
            is UndefinedValue -> BitValue.Bx
        }
    }

    fun andReduce(): SimpleValue {
        return reduceOp { it.reduce { a, b -> a and b } }.toSimpleValue()
    }

    fun orReduce(): SimpleValue {
        return reduceOp { it.reduce { a, b -> a or b } }.toSimpleValue()
    }

    fun xorReduce(): SimpleValue {
        return reduceOp { it.reduce { a, b -> a xor b } }.toSimpleValue()
    }
}

data class ArrayValue(
    val elements: List<Value>
) : Value(), List<Value> by elements

data class SimpleValue(
    val bits: BitArray
) : Value(), List<BitValue> by bits {
    override val signed: Boolean
        get() = bits.signed

    fun resize(width: Int) = bits.resize(width).toSimpleValue()

    /** Changes sign without changing bits */
    fun setSign(signed: Boolean): SimpleValue {
        return SimpleValue(MutableBitArray(signed, bits))
    }

    infix fun isLessThan(other: SimpleValue): SimpleValue {
        val longest = size.coerceAtLeast(other.size)
        val signedOp = signed && other.signed
        val op1 = setSign(signedOp).resize(longest)
        val op2 = other.setSign(signedOp).resize(longest)

        val neg1 = signedOp && bits.isNegative()
        val neg2 = signedOp && other.bits.isNegative()

        if (neg1 && !neg2) // negative < positive
            return BitValue.B1.toSimpleValue()

        if (!neg1 && neg2) // positive !< negative
            return BitValue.B0.toSimpleValue()

        // negative to negative or positive to positive can be directly compared
        for (i in op1.indices.reversed()) {
            if (!op1[i].isNumber() || !op2[i].isNumber())
                return BitValue.Bx.toSimpleValue()

            if (op1[i] == BitValue.B1 && op2[i] == BitValue.B0)
                return BitValue.B0.toSimpleValue()

            if (op1[i] == BitValue.B0 && op2[i] == BitValue.B1)
                return BitValue.B1.toSimpleValue()
        }
        return BitValue.B0.toSimpleValue()
    }

    infix fun isGreaterThan(other: SimpleValue): SimpleValue {
        return isLessThan(other).lsb.not().toSimpleValue()
    }

    infix fun isLessOrEqualTo(other: SimpleValue): SimpleValue {
        return (isLessThan(other).lsb or isEqualTo(other).lsb).toSimpleValue()
    }

    infix fun isGreaterOrEqualTo(other: SimpleValue): SimpleValue {
        return (isGreaterThan(other).lsb or isEqualTo(other).lsb).toSimpleValue()
    }
}

data class StructValue(
        val type: StructType,
        val elements: Map<String, Value>
) : Value() {
    fun isComplete(): Boolean {
        type.members.forEach { (k, _) ->
            if (!elements.containsKey(k))
                return false
        }
        return true
    }
}

data class UndefinedValue(
    val expression: String,
    val width: SignalWidth = UndefinedSimpleWidth,
    override val signed: Boolean = false
) : Value()
