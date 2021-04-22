package com.alchitry.labs.parsers.lucidv2.values

sealed class SignalWidth {
    // specifies if width is 1D array
    fun isFlatArray(): Boolean {
        return (this is ArrayWidth && next == null) || this is UndefinedSimpleWidth
    }

    // specifies if width is JUST and array (no structs)
    fun isSimpleArray(): Boolean {
        return (this is ArrayWidth && (next == null || next.isSimpleArray())) || this is UndefinedSimpleWidth
    }

    fun isArray(): Boolean {
        return this is ArrayWidth || this is UndefinedSimpleWidth
    }

    fun getDimensions(): List<Int> {
        require(isSimpleArray()) { "getDimensions can only be called on arrays" }
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

        return when {
            other is ArrayWidth && this is ArrayWidth -> other.size == this.size && other.next == this.next
            other is StructWidth && this is StructWidth -> other.struct == this.struct
            else -> false
        }
    }

    fun shallowEquals(other: SignalWidth): Boolean {
        return when {
            other is ArrayWidth && this is ArrayWidth -> other.size == this.size
            other is StructWidth && this is StructWidth -> other.struct == this.struct
            else -> false
        }
    }
}

data class ArrayWidth(
        val size: Int,
        val next: SignalWidth? = null
) : SignalWidth()

data class StructWidth(
        val struct: StructType
) : SignalWidth()

object UndefinedSimpleWidth : SignalWidth()