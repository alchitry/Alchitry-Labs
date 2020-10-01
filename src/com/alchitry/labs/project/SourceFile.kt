package com.alchitry.labs.project

data class SourceFile(var fileName: String, var type: Int = 0) {
    companion object {
        const val LUCID = 0
        const val VERILOG = 1
        const val CONSTRAINT = 2
    }
}