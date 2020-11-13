package com.alchitry.labs.parsers.lucidv2.values

sealed class Value {
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

    fun not(): Value {
        return SimpleValue(MutableBitArray(!isTrueBit()))
    }

    fun isTrue(): Value {
        return SimpleValue(MutableBitArray(isTrueBit()))
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
        return SimpleValue(MutableBitArray(false, 1) { reduceOp { it.reduce { a, b -> a and b } } })
    }

    fun orReduce(): SimpleValue {
        return SimpleValue(MutableBitArray(false, 1) { reduceOp { it.reduce { a, b -> a or b } } })
    }

    fun xorReduce(): SimpleValue {
        return SimpleValue(MutableBitArray(false, 1) { reduceOp { it.reduce { a, b -> a xor b } } })
    }
}

data class ArrayValue(
        val elements: List<Value>
) : Value()

data class SimpleValue(
        val bits: BitArray
) : Value() {
    override val signed: Boolean
        get() = bits.signed
    val size: Int
        get() = bits.size
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
        val width: SignalWidth = UndefinedSimpleWidth(),
        override val signed: Boolean = false
) : Value()