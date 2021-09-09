package com.alchitry.labs.parsers.lucidv2.values

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class SignalWidth {
    /**
     * Returns true if this is a 1D array
     */
    fun isFlatArray(): Boolean {
        return (this is ArrayWidth && next == null) || this is UndefinedSimpleWidth
    }

    /**
     *  Returns true if this is JUST an array (no structs)
     */
    fun isSimpleArray(): Boolean {
        return (this is ArrayWidth && (next == null || next.isSimpleArray())) || this is UndefinedSimpleWidth
    }

    /**
     * Returns true if this is an array. May be an array of anything, including structs.
     */
    fun isArray(): Boolean {
        return this is ArrayWidth || this is UndefinedSimpleWidth
    }

    /**
     * Returns true if this is a defined 1D array
     */
    @OptIn(ExperimentalContracts::class)
    fun isDefinedFlatArray(): Boolean {
        contract {
            returns(true) implies (this@SignalWidth is ArrayWidth)
        }
        return this is ArrayWidth && next == null
    }

    /**
     *  Returns true if this is JUST an array (no structs) and defined
     */
    @OptIn(ExperimentalContracts::class)
    fun isDefinedSimpleArray(): Boolean {
        contract {
            returns(true) implies (this@SignalWidth is ArrayWidth)
        }
        return (this is ArrayWidth && (next == null || next.isDefinedSimpleArray()))
    }

    /**
     * Returns true if the width is well defined.
     */
    fun isDefined(): Boolean {
        return when (this) {
            is ArrayWidth -> next?.isDefined() ?: true
            is StructWidth -> struct.all { it.width.isDefined() }
            UndefinedSimpleWidth -> false
        }
    }

    fun getDimensions(): List<Int> {
        require(isDefinedFlatArray()) { "getDimensions can only be called on arrays" }
        val dims = mutableListOf<Int>()
        var array: ArrayWidth? = this as ArrayWidth
        while (array != null) {
            dims.add(array.size)
            array = array.next as ArrayWidth?
        }
        return dims
    }

    fun getBitCount(): Int {
        return when (this) {
            is ArrayWidth -> size * (next?.getBitCount() ?: 1)
            is StructWidth -> struct.sumOf { it.width.getBitCount() }
            UndefinedSimpleWidth -> error("getBitCount() can't be used when width isn't well defined")
        }
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