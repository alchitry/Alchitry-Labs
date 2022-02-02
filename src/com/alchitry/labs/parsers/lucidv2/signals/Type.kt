package com.alchitry.labs.parsers.lucidv2.signals

import com.alchitry.labs.parsers.lucidv2.values.ArrayWidth
import com.alchitry.labs.parsers.lucidv2.values.SignalWidth
import com.alchitry.labs.parsers.lucidv2.values.SimpleWidth
import com.alchitry.labs.parsers.lucidv2.values.StructWidth

sealed class Type {
    abstract val width: SignalWidth
}

data class SimpleType(
    override val width: SimpleWidth
) : Type()

data class ArrayType(
    val size: Int,
    val type: Type
) : Type() {
    override val width: SignalWidth = ArrayWidth(size, type.width)
}

data class StructType(
    val fullName: String, // includes namespace
    private val members: MutableMap<String, Type> = mutableMapOf()
) : Type(), MutableMap<String, Type> by members {
    override val width: SignalWidth = StructWidth(this)
    val name: String = fullName.split(".").last()
}