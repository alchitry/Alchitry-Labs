package com.alchitry.labs

import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences
import kotlin.reflect.KProperty

object Settings {
    private val pref = Preferences.userNodeForPackage(Settings::class.java)

    class StringSetting(private val key: String, private val default: String?) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String? = pref.get(key, default)
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) = if (value != null) pref.put(key, value) else pref.remove(key)
    }

    class BooleanSetting(private val key: String, private val default: Boolean) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = pref.getBoolean(key, default)
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) = pref.putBoolean(key, value)
    }

    class IntSetting(private val key: String, private val default: Int) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int = pref.getInt(key, default)
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) = pref.putInt(key, value)
    }


    fun commit() {
        try {
            pref.flush()
        } catch (e: BackingStoreException) {
            Util.showError("Failed to save settings!")
        }
    }

    var VERSION by StringSetting("VERSION", "0")
    var WINDOW_WIDTH by IntSetting("WINDOW_WIDTH", 1000)
    var WINDOW_HEIGHT by IntSetting("WINDOW_HEIGHT", 700)
    var FILE_LIST_WIDTH by IntSetting("FILE_LIST_WIDTH", 200)
    var CONSOLE_HEIGHT by IntSetting("CONSOLE_HEIGHT", 200)
    var MAXIMIZED by BooleanSetting("MAXIMIZED", false)
    var XILINX_LOC by StringSetting("XILINX_LOC", null)
    var OPEN_PROJECT by StringSetting("OPEN_PROJECT", null)
    var WORKSPACE by StringSetting("WORKSPACE", null)
    var THEME by BooleanSetting("THEME", false)
    var WORD_WRAP by BooleanSetting("WORD_WRAP", true)
    var VIVADO_LOC by StringSetting("VIVADO_LOC", null)
    var ICECUBE_LOC by StringSetting("ICECUBE_LOC", null)
    var ICECUBE_LICENSE by StringSetting("ICECUBE_LICENSE", null)
    var USE_ICESTORM by BooleanSetting("USE_ICESTORM", false)
    var ICEPACK_LOC by StringSetting("ICEPACK_LOC", null)
    var YOSYS_LOC by StringSetting("YOSYS_LOC", null)
    var ARACHNE_LOC by StringSetting("ARACHNE_LOC", null)
    var EDITOR_FONT_SIZE by IntSetting("EDITOR_FONT_SIZE", 12)
    var CHECK_FOR_UPDATES by BooleanSetting("CHECK_FOR_UPDATES", true)
    var BETA_UPDATES by BooleanSetting("BETA_UPDATES", false)
    var BETA_UPDATES_PROMPTED by BooleanSetting("BETA_UPDATES_PROMPTED", false)
    var ERROR_REPORTING by BooleanSetting("ERROR_REPORTING", false)
    var ERROR_REPORTING_PROMPTED by BooleanSetting("ERROR_REPORTING_PROMPTED", false)
}