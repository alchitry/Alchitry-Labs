package com.alchitry.labs

import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences
import javax.swing.filechooser.FileSystemView

object Settings {
    private val pref = Preferences.userNodeForPackage(Settings::class.java)

    data class Setting<T>(val key: String, val default: T) {
        fun get(): T {
            @Suppress("UNCHECKED_CAST")
            return when (default) {
                is Int -> pref.getInt(key, default)
                is Float -> pref.getFloat(key, default)
                is String? -> pref.get(key, default)
                is Boolean -> pref.getBoolean(key, default)
                else -> throw IllegalStateException("Unknown class type ${::default.javaClass}")
            } as T
        }

        fun put(value: T?) {
            if (value == null) {
                pref.remove(key)
                return
            }
            when (value) {
                is Int -> pref.putInt(key, value)
                is Float -> pref.putFloat(key, value)
                is String -> pref.put(key, value)
                is Boolean -> pref.putBoolean(key, value)
                else -> throw IllegalStateException("Unknown class type ${::default.javaClass}")
            }
        }
    }

    fun commit() {
        try {
            pref.flush()
        } catch (e: BackingStoreException) {
            Util.showError("Failed to save settings!")
        }
    }

    val VERSION = Setting("VERSION", "0")
    val WINDOW_WIDTH = Setting("WINDOW_WIDTH", 1000)
    val WINDOW_HEIGHT = Setting("WINDOW_HEIGHT", 700)
    val FILE_LIST_WIDTH = Setting("FILE_LIST_WIDTH", 200)
    val CONSOLE_HEIGHT = Setting("CONSOLE_HEIGHT", 200)
    val MAXIMIZED = Setting("MAXIMIZED", false)
    val XILINX_LOC = Setting<String?>("XILINX_LOC", null)
    val OPEN_PROJECT = Setting<String?>("OPEN_PROJECT", null)
    val WORKSPACE = Setting("WORKSPACE", Util.assemblePath(FileSystemView.getFileSystemView().defaultDirectory.path, "alchitry"))
    val THEME = Setting("THEME", true)
    val WORD_WRAP = Setting("WORD_WRAP", true)
    val VIVADO_LOC = Setting<String?>("VIVADO_LOC", null)
    val ICECUBE_LOC = Setting<String?>("ICECUBE_LOC", null)
    val ICECUBE_LICENSE = Setting<String?>("ICECUBE_LICENSE", null)
    val USE_ICESTORM = Setting("USE_ICESTORM", false)
    val ICEPACK_LOC = Setting<String?>("ICEPACK_LOC", null)
    val YOSYS_LOC = Setting<String?>("YOSYS_LOC", null)
    val ARACHNE_LOC = Setting<String?>("ARACHNE_LOC", null)
    val EDITOR_FONT_SIZE = Setting("EDITOR_FONT_SIZE", 12)
    val CHECK_FOR_UPDATES = Setting("CHECK_FOR_UPDATES", true)
    val BETA_UPDATES = Setting("BETA_UPDATES", false)
}