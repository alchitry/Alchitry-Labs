package com.alchitry.labs.parsers.lucidv2.values

data class StructType(
        val members: MutableMap<String, StructMember> = mutableMapOf()
)

data class StructMember(
        val name: String,
        val signed: Boolean,
        val width: SignalWidth
)