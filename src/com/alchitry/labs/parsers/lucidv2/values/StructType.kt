package com.alchitry.labs.parsers.lucidv2.values

data class StructType(
        private val members: MutableList<StructMember> = mutableListOf()
) : MutableList<StructMember> by members

data class StructMember(
        val name: String,
        val signed: Boolean,
        val width: SignalWidth
)