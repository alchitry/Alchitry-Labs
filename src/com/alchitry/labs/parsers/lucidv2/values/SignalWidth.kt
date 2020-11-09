package com.alchitry.labs.parsers.lucidv2.values

sealed class SignalWidth

data class ArrayWidth(
        val size: Int,
        val next: SignalWidth? = null
) : SignalWidth()

data class StructWidth(
        val struct: StructType,
        val next: SignalWidth? = null
) : SignalWidth()