package com.alchitry.labs.parsers.lucidv2.values

sealed class SignalWidth {
    fun isSimpleArray(): Boolean {
        return (this is ArrayWidth && next == null) || this is UndefinedSimpleArray
    }

    fun isArray(): Boolean {
        return this is ArrayWidth && (next == null || next.isArray())
    }

    fun getDimensions(): List<Int> {
        require(isArray()) { "getDimensions can only be called on arrays" }
        val dims = mutableListOf<Int>()
        var array: ArrayWidth? = this as ArrayWidth
        while (array != null) {
            dims.add(array.size)
            array = array.next as ArrayWidth?
        }
        return dims
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SignalWidth)
            return false

        if (other is ArrayWidth && this is ArrayWidth) {
            return other.size == this.size && other.next == this.next
        } else if (other is StructWidth && this is StructWidth) {
            return other.struct == this.struct
        }

        return false
    }
}

data class ArrayWidth(
        val size: Int,
        val next: SignalWidth? = null
) : SignalWidth()

data class StructWidth(
        val struct: StructType
) : SignalWidth()

class UndefinedSimpleArray : SignalWidth()