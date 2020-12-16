package com.alchitry.labs.parsers.types

import com.alchitry.labs.hardware.boards.Board

data class ClockConstraint(private val port: String, private val portBit: Int, private val isMultiBit: Boolean, private val frequency: Int) {
    fun getBoardConstraint(board: Board): String {
        val sb = StringBuilder()
        val portName = port + if (isMultiBit) "[$portBit]" else ""
        sb.append("# ").append(portName).append(" => ").append(frequency).append("Hz").append(System.lineSeparator())
        val nsPeriod = 1000000000.0f / frequency
        when {
            board.isType(Board.AU or Board.AU_PLUS) -> {
                sb.append("create_clock -period ").append(nsPeriod).append(" -name ").append(port).append("_").append(portBit).append(" -waveform {0.000 5.000} [get_ports ")
                        .append(portName).append("]").append(System.lineSeparator())
            }
            board.isType(Board.CU) -> {
                sb.append("create_clock -name ").append(port).append("_").append(portBit).append(" -period ").append(java.lang.Float.toString(nsPeriod)).append(" [get_ports ").append(portName).append("]")
                        .append(System.lineSeparator())
            }
            else -> {
                error("Unknown board type")
            }
        }
        return sb.toString()
    }
}