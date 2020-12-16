package com.alchitry.labs.hardware.boards

import com.alchitry.labs.hardware.loaders.ProjectLoader
import com.alchitry.labs.hardware.pinout.PinConverter
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor
import com.alchitry.labs.project.builders.ProjectBuilder
import com.alchitry.labs.widgets.IoRegion
import java.util.*

abstract class Board {
    companion object {
        const val ANY = -0x1
        const val AU = 1 shl 0
        const val AU_PLUS = 1 shl 1
        const val CU = 1 shl 2
        const val MOJO = 1 shl 3
        val constraintExtensions: Set<String> by lazy {
            HashSet<String>().also { list ->
                boards.forEach { board -> board.supportedConstraintExtensions.forEach { list.add(it) } }
            }
        }

        @JvmField
        val boards = listOf(AlchitryAu, AlchitryAuPlus, AlchitryCu, Mojo)

        @JvmStatic
        fun getFromName(board: String): Board? {
            for (b in boards) {
                if (b.name == board) return b
            }
            return null
        }

        fun getFromProjectName(board: String): Board? {
            for (b in boards) {
                if (b.exampleProjectDir == board) return b
            }
            return null
        }

        fun getType(board: Board?): Int {
            return when (board) {
                is AlchitryAu -> AU
                is AlchitryAuPlus -> AU_PLUS
                is AlchitryCu -> CU
                is Mojo -> MOJO
                else -> 0
            }
        }

        fun isType(board: Board?, type: Int): Boolean {
            return type and getType(board) != 0
        }
    }

    abstract val usbDescriptor: UsbDescriptor
    abstract val fpgaName: String
    abstract val name: String
    abstract val exampleProjectDir: String
    abstract val builder: ProjectBuilder
    abstract val loader: ProjectLoader
    abstract val ioRegions: Array<IoRegion>
    abstract val sVGPath: String
    abstract val supportedConstraintExtensions: Array<String>
    abstract val pinConverter: PinConverter
    val type: Int
        get() = getType(this)

    fun isType(type: Int): Boolean {
        return isType(this, type)
    }
}