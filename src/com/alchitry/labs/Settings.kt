package com.alchitry.labs

import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences
import javax.swing.filechooser.FileSystemView
import kotlin.reflect.KProperty

object Settings {
    private val pref = Preferences.userNodeForPackage(Settings::class.java)

    class Setting<T>(private val key: String, private val default: T) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            @Suppress("UNCHECKED_CAST")
            return when (default) {
                is Int -> pref.getInt(key, default)
                is Float -> pref.getFloat(key, default)
                is String? -> pref.get(key, default)
                is Boolean -> pref.getBoolean(key, default)
                else -> throw IllegalStateException("Unknown class type ${::default.javaClass}")
            } as T
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
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

    fun remove(key: String) {
       pref.remove(key)
    }

    var VERSION by Setting("VERSION", "0")
    var WINDOW_WIDTH by Setting("WINDOW_WIDTH", 1000)
    var WINDOW_HEIGHT by Setting("WINDOW_HEIGHT", 700)
    var FILE_LIST_WIDTH by Setting("FILE_LIST_WIDTH", 200)
    var CONSOLE_HEIGHT by Setting("CONSOLE_HEIGHT", 200)
    var MAXIMIZED by Setting("MAXIMIZED", false)
    var XILINX_LOC by Setting<String?>("XILINX_LOC", null)
    var OPEN_PROJECT by Setting<String?>("OPEN_PROJECT", null)
    var WORKSPACE by Setting("WORKSPACE", Util.assemblePath(FileSystemView.getFileSystemView().defaultDirectory.path, "alchitry"))
    var THEME by Setting("THEME", true)
    var WORD_WRAP by Setting("WORD_WRAP", true)
    var VIVADO_LOC by Setting<String?>("VIVADO_LOC", null)
    var ICECUBE_LOC by Setting<String?>("ICECUBE_LOC", null)
    var ICECUBE_LICENSE by Setting<String?>("ICECUBE_LICENSE", null)
    var USE_ICESTORM by Setting("USE_ICESTORM", false)
    var ICEPACK_LOC by Setting<String?>("ICEPACK_LOC", null)
    var YOSYS_LOC by Setting<String?>("YOSYS_LOC", null)
    var ARACHNE_LOC by Setting<String?>("ARACHNE_LOC", null)
    var EDITOR_FONT_SIZE by Setting("EDITOR_FONT_SIZE", 12)
    var CHECK_FOR_UPDATES by Setting("CHECK_FOR_UPDATES", true)
    var BETA_UPDATES by Setting("BETA_UPDATES", false)
}