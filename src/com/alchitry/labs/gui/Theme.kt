package com.alchitry.labs.gui

import com.alchitry.labs.Settings
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.widgets.Display

object Theme {
    @JvmField
	var set = false
    @JvmField
	var mainAccentColor: Color? = null
    var darkAccentColor: Color? = null
    @JvmField
	var moduleColor: Color? = null
    @JvmField
	var keyWordColor: Color? = null
    @JvmField
	var valueColor: Color? = null
    @JvmField
	var varTypeColor: Color? = null
    @JvmField
	var operatorColor: Color? = null
    @JvmField
	var commentColor: Color? = null
    @JvmField
	var stringColor: Color? = null
    @JvmField
	var constColor: Color? = null
    @JvmField
	var nameSpaceColor: Color? = null
    @JvmField
	var functionColor: Color? = null
    var instantiationColor: Color? = null
    @JvmField
	var editorBackgroundColor: Color? = null
    @JvmField
	var editorForegroundColor: Color? = null
    @JvmField
	var comboBackgroundColor: Color? = null
    @JvmField
	var editorTextSelectedColor: Color? = null
    @JvmField
	var bulletTextColor: Color? = null
    @JvmField
	var highlightedLineColor: Color? = null
    @JvmField
	var highlightedWordColor: Color? = null
    var tabBackgroundColor: Color? = null
    var tabForegroundColor: Color? = null
    var tabSelectedForegroundColor: Color? = null
    var tabSelectedBackgroundColor: Color? = null
    @JvmField
	var windowBackgroundColor: Color? = null
    @JvmField
	var windowForegroundColor: Color? = null
    @JvmField
	var treeSelectedFocusedColor: Color? = null
    @JvmField
	var treeSelectedColor: Color? = null
    @JvmField
	var treeSelectedForegroundColor: Color? = null
    @JvmField
	var treeHoverColor: Color? = null
    @JvmField
	var toolBarHoverColor: Color? = null
    @JvmField
	var toolBarClickColor: Color? = null
    @JvmField
	var consoleBackgroundColor: Color? = null
    @JvmField
	var consoleForegoundColor: Color? = null
    @JvmField
	var successTextColor: Color? = null
    @JvmField
	var errorTextColor: Color? = null
    @JvmField
	var warningTextColor: Color? = null
    @JvmField
	var infoTextColor: Color? = null
    @JvmField
	var debugTextColor: Color? = null
    @JvmField
	var autocompleteBackgroundColor: Color? = null
    @JvmField
	var autocompleteForegroundColor: Color? = null
    @JvmField
	var autocompleteHighlightColor: Color? = null
    @JvmField
	var searchBackgroundColor: Color? = null
    @JvmField
	var searchForegroundColor: Color? = null
    var bracketUnderlineColor: Color? = null
    var clockMarkerColor: Color? = null
    var tabErrorTextColor: Color? = null
    var tabWarningTextColor: Color? = null
    var tabNormalTextColor: Color? = null
    var tabHoverTextColor: Color? = null
    @JvmField
	var waveButtonHoverColor: Color? = null
    @JvmField
	var waveButtonActiveColor: Color? = null
    @JvmField
	var waveGridColor: Color? = null
    @JvmField
	var waveCursorColor: Color? = null
    @JvmField
	var defaultFont: Font? = null
    @JvmField
	var boldFont: Font? = null
    fun init(display: Display) {
        initColors(display)
        initFonts(display)
        set = true
    }

    private fun initColors(display: Display) {
        if (Settings.THEME) {
            mainAccentColor = Color(display, 230, 184, 0)
            darkAccentColor = Color(display, 191, 152, 29)
            moduleColor = Color(display, 217, 165, 11)
            keyWordColor = Color(display, 8, 153, 153)
            valueColor = Color(display, 110, 0, 224)
            varTypeColor = Color(display, 0, 131, 182)
            operatorColor = Color(display, 204, 21, 21)
            commentColor = Color(display, 110, 110, 110)
            stringColor = Color(display, 179, 179, 18)
            constColor = Color(display, 192, 0, 203)
            nameSpaceColor = Color(display, 203, 0, 99)
            functionColor = Color(display, 0, 162, 123)
            instantiationColor = Color(display, 166, 96, 17)
            comboBackgroundColor = Color(display, 240, 240, 240)
            editorBackgroundColor = Color(display, 250, 250, 250)
            editorForegroundColor = Color(display, 0, 0, 0)
            editorTextSelectedColor = Color(display, 160, 160, 160)
            bulletTextColor = Color(display, 25, 25, 25)
            highlightedLineColor = Color(display, 210, 210, 208)
            highlightedWordColor = Color(display, 200, 200, 190)
            tabSelectedForegroundColor = bulletTextColor
            tabSelectedBackgroundColor = editorBackgroundColor
            windowBackgroundColor = Color(display, 230, 230, 228)
            windowForegroundColor = Color(display, 245, 245, 243)
            tabBackgroundColor = highlightedLineColor
            tabForegroundColor = windowForegroundColor
            treeSelectedFocusedColor = mainAccentColor
            treeSelectedColor = editorTextSelectedColor
            treeSelectedForegroundColor = editorBackgroundColor
            treeHoverColor = Color(display, 220, 220, 220)
            toolBarHoverColor = mainAccentColor
            toolBarClickColor = darkAccentColor
            consoleBackgroundColor = editorBackgroundColor
            consoleForegoundColor = editorForegroundColor
            successTextColor = Color(display, 25, 255, 25)
            errorTextColor = Color(display, 255, 25, 25)
            warningTextColor = Color(display, 222, 175, 0)
            debugTextColor = Color(display, 128, 255, 0)
            infoTextColor = Color(display, 0, 189, 255)
            tabErrorTextColor = errorTextColor
            tabWarningTextColor = warningTextColor
            tabNormalTextColor = editorForegroundColor
            tabHoverTextColor = editorBackgroundColor
            autocompleteBackgroundColor = Color(display, 220, 220, 220)
            autocompleteForegroundColor = consoleForegoundColor
            autocompleteHighlightColor = mainAccentColor
            searchBackgroundColor = Color(display, 240, 240, 240)
            searchForegroundColor = editorForegroundColor
            bracketUnderlineColor = editorForegroundColor
            clockMarkerColor = Color(display, 240, 240, 240)
            waveButtonHoverColor = treeHoverColor
            waveButtonActiveColor = mainAccentColor
            waveGridColor = Color(display, 220, 220, 220)
            waveCursorColor = Color(display, 0, 0, 0)
        } else {
            mainAccentColor = Color(display, 230, 184, 0)
            darkAccentColor = Color(display, 191, 152, 29)
            moduleColor = Color(display, 217, 165, 11)
            keyWordColor = Color(display, 10, 191, 191)
            valueColor = Color(display, 162, 105, 220)
            varTypeColor = Color(display, 10, 141, 191)
            operatorColor = Color(display, 237, 67, 67)
            commentColor = Color(display, 150, 150, 150)
            stringColor = Color(display, 191, 191, 10)
            constColor = Color(display, 212, 90, 218)
            nameSpaceColor = Color(display, 218, 90, 140)
            functionColor = Color(display, 27, 221, 175)
            instantiationColor = Color(display, 234, 182, 123)
            comboBackgroundColor = Color(display, 50, 50, 45)
            editorBackgroundColor = Color(display, 40, 40, 35)
            editorForegroundColor = Color(display, 255, 255, 255)
            editorTextSelectedColor = Color(display, 120, 120, 110)
            bulletTextColor = Color(display, 200, 200, 200)
            highlightedLineColor = editorTextSelectedColor
            highlightedWordColor = Color(display, 80, 80, 70)
            tabSelectedForegroundColor = bulletTextColor
            tabSelectedBackgroundColor = editorBackgroundColor
            windowBackgroundColor = Color(display, 80, 80, 75)
            windowForegroundColor = Color(display, 10, 10, 8)
            tabBackgroundColor = highlightedLineColor
            tabForegroundColor = windowForegroundColor
            treeSelectedFocusedColor = mainAccentColor
            treeSelectedColor = highlightedLineColor
            treeSelectedForegroundColor = editorForegroundColor
            treeHoverColor = Color(display, 75, 75, 65)
            toolBarHoverColor = mainAccentColor
            toolBarClickColor = darkAccentColor
            consoleBackgroundColor = editorBackgroundColor
            consoleForegoundColor = editorForegroundColor
            successTextColor = Color(display, 25, 255, 25)
            errorTextColor = Color(display, 255, 25, 25)
            warningTextColor = Color(display, 255, 255, 25)
            infoTextColor = Color(display, 0, 189, 255)
            debugTextColor = Color(display, 128, 255, 0)
            tabErrorTextColor = Color(display, 255, 100, 100)
            tabWarningTextColor = Color(display, 255, 255, 100)
            tabNormalTextColor = editorForegroundColor
            tabHoverTextColor = tabNormalTextColor
            autocompleteBackgroundColor = Color(display, 55, 55, 50)
            autocompleteForegroundColor = consoleForegoundColor
            autocompleteHighlightColor = mainAccentColor
            searchBackgroundColor = Color(display, 60, 60, 55)
            searchForegroundColor = editorForegroundColor
            bracketUnderlineColor = editorForegroundColor
            clockMarkerColor = Color(display, 50, 50, 45)
            waveButtonHoverColor = treeHoverColor
            waveButtonActiveColor = mainAccentColor
            waveGridColor = Color(display, 80, 80, 70)
            waveCursorColor = Color(display, 255, 255, 255)
        }
    }

    private fun initFonts(display: Display) {
        defaultFont = Font(display, "Ubuntu", 12, SWT.NORMAL)
        boldFont = Font(display, "Ubuntu", 12, SWT.BOLD)
    }

    fun dispose() {
        mainAccentColor!!.dispose()
        darkAccentColor!!.dispose()
        moduleColor!!.dispose()
        keyWordColor!!.dispose()
        valueColor!!.dispose()
        varTypeColor!!.dispose()
        operatorColor!!.dispose()
        commentColor!!.dispose()
        stringColor!!.dispose()
        constColor!!.dispose()
        nameSpaceColor!!.dispose()
        functionColor!!.dispose()
        instantiationColor!!.dispose()
        comboBackgroundColor!!.dispose()
        editorBackgroundColor!!.dispose()
        editorForegroundColor!!.dispose()
        editorTextSelectedColor!!.dispose()
        bulletTextColor!!.dispose()
        highlightedLineColor!!.dispose()
        highlightedWordColor!!.dispose()
        tabSelectedBackgroundColor!!.dispose()
        tabSelectedForegroundColor!!.dispose()
        windowBackgroundColor!!.dispose()
        windowForegroundColor!!.dispose()
        tabBackgroundColor!!.dispose()
        tabForegroundColor!!.dispose()
        treeSelectedFocusedColor!!.dispose()
        treeSelectedColor!!.dispose()
        treeSelectedForegroundColor!!.dispose()
        treeHoverColor!!.dispose()
        toolBarHoverColor!!.dispose()
        toolBarClickColor!!.dispose()
        consoleBackgroundColor!!.dispose()
        consoleForegoundColor!!.dispose()
        successTextColor!!.dispose()
        errorTextColor!!.dispose()
        warningTextColor!!.dispose()
        infoTextColor!!.dispose()
        debugTextColor!!.dispose()
        tabErrorTextColor!!.dispose()
        tabWarningTextColor!!.dispose()
        tabNormalTextColor!!.dispose()
        tabHoverTextColor!!.dispose()
        autocompleteBackgroundColor!!.dispose()
        autocompleteForegroundColor!!.dispose()
        autocompleteHighlightColor!!.dispose()
        searchBackgroundColor!!.dispose()
        searchForegroundColor!!.dispose()
        bracketUnderlineColor!!.dispose()
        clockMarkerColor!!.dispose()
        waveButtonHoverColor!!.dispose()
        waveButtonActiveColor!!.dispose()
        waveGridColor!!.dispose()
        waveCursorColor!!.dispose()
        defaultFont!!.dispose()
        boldFont!!.dispose()
        set = false
    }
}