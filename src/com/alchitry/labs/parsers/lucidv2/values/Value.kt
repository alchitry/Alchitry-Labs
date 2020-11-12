package com.alchitry.labs.parsers.lucidv2.values

sealed class Value {
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
}

data class ArrayValue(
        val elements: List<Value>
) : Value()

data class SimpleValue(
        val bits: BitArray
) : Value()

data class StructValue(
        val type: StructType,
        val elements: Map<String, Value>
) : Value()

data class UndefinedValue(
        val expression: String,
        val width: SignalWidth = UndefinedSimpleWidth()
) : Value()