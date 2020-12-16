package com.alchitry.labs.parsers.types

import com.alchitry.labs.hardware.boards.Board

class PinConstraint @JvmOverloads constructor(val pin: String, val port: String, private val portBit: Int = 0, private val isMultiBit: Boolean = false, private val pullUp: Boolean = false, private val pullDown: Boolean = false) {
    fun getBoardConstraint(board: Board): String {
        val sb = StringBuilder()
        val pc = board.pinConverter
        val portName = port + if (isMultiBit) "[$portBit]" else ""
        when {
            board.isType(Board.AU or Board.AU_PLUS) -> {
                sb.append("set_property PACKAGE_PIN ").append(pc.getFPGAPin(pin)).append(" [get_ports {").append(portName).append("}]").append(System.lineSeparator())
                sb.append("set_property IOSTANDARD LVCMOS33 [get_ports {").append(portName).append("}]").append(System.lineSeparator())
                if (pullUp) sb.append("set_property PULLUP true [get_ports {").append(portName).append("}]").append(System.lineSeparator())
                if (pullDown) sb.append("set_property PULLDOWN true [get_ports {").append(portName).append("}]").append(System.lineSeparator())
            }
            board.isType(Board.CU) -> {
                sb.append("set_io ").append(portName).append(" ").append(pc.getFPGAPin(pin))
                if (pullUp) sb.append(" -pullup yes")
                sb.append(System.lineSeparator())
            }
            else -> {
                error("Unsupported board type")
            }
        }
        return sb.toString()
    }
}