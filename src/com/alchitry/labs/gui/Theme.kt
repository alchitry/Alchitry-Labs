package com.alchitry.labs.gui

import com.alchitry.labs.Settings
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.widgets.Display
import kotlin.math.roundToInt

object Theme {
    var set = false
    lateinit var mainAccentColor: Color
    lateinit var darkAccentColor: Color
    lateinit var moduleColor: Color
    lateinit var keyWordColor: Color
    lateinit var valueColor: Color
    lateinit var varTypeColor: Color
    lateinit var operatorColor: Color
    lateinit var commentColor: Color
    lateinit var stringColor: Color
    lateinit var constColor: Color
    lateinit var nameSpaceColor: Color
    lateinit var functionColor: Color
    lateinit var instantiationColor: Color
    lateinit var editorBackgroundColor: Color
    lateinit var editorForegroundColor: Color
    lateinit var comboBackgroundColor: Color
    lateinit var editorTextSelectedColor: Color
    lateinit var bulletTextColor: Color
    lateinit var highlightedLineColor: Color
    lateinit var highlightedWordColor: Color
    lateinit var tabBackgroundColor: Color
    lateinit var tabForegroundColor: Color
    lateinit var tabSelectedForegroundColor: Color
    lateinit var tabSelectedBackgroundColor: Color
    lateinit var windowBackgroundColor: Color
    lateinit var windowForegroundColor: Color
    lateinit var treeSelectedFocusedColor: Color
    lateinit var treeSelectedColor: Color
    lateinit var treeSelectedForegroundColor: Color
    lateinit var treeHoverColor: Color
    lateinit var toolBarHoverColor: Color
    lateinit var toolBarClickColor: Color
    lateinit var consoleBackgroundColor: Color
    lateinit var consoleForegroundColor: Color
    lateinit var successTextColor: Color
    lateinit var errorTextColor: Color
    lateinit var warningTextColor: Color
    lateinit var infoTextColor: Color
    lateinit var debugTextColor: Color
    lateinit var autocompleteBackgroundColor: Color
    lateinit var autocompleteForegroundColor: Color
    lateinit var autocompleteHighlightColor: Color
    lateinit var searchBackgroundColor: Color
    lateinit var searchForegroundColor: Color
    lateinit var bracketUnderlineColor: Color
    lateinit var clockMarkerColor: Color
    lateinit var tabErrorTextColor: Color
    lateinit var tabWarningTextColor: Color
    lateinit var tabNormalTextColor: Color
    lateinit var tabHoverTextColor: Color
    lateinit var waveButtonHoverColor: Color
    lateinit var waveButtonActiveColor: Color
    lateinit var waveGridColor: Color
    lateinit var waveCursorColor: Color
    lateinit var defaultFont: Font
    lateinit var boldFont: Font
    lateinit var monoFont: Font

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
            consoleForegroundColor = editorForegroundColor
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
            autocompleteForegroundColor = consoleForegroundColor
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
            consoleForegroundColor = editorForegroundColor
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
            autocompleteForegroundColor = consoleForegroundColor
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
        val fontSize = (12 * Settings.FONT_SCALE).roundToInt()
        defaultFont = Font(display, "Ubuntu", fontSize, SWT.NORMAL)
        boldFont = Font(display, "Ubuntu", fontSize, SWT.BOLD)
        monoFont = Font(display, "Ubuntu Mono", fontSize, SWT.NORMAL)
    }

    fun dispose() {
        mainAccentColor.dispose()
        darkAccentColor.dispose()
        moduleColor.dispose()
        keyWordColor.dispose()
        valueColor.dispose()
        varTypeColor.dispose()
        operatorColor.dispose()
        commentColor.dispose()
        stringColor.dispose()
        constColor.dispose()
        nameSpaceColor.dispose()
        functionColor.dispose()
        instantiationColor.dispose()
        comboBackgroundColor.dispose()
        editorBackgroundColor.dispose()
        editorForegroundColor.dispose()
        editorTextSelectedColor.dispose()
        bulletTextColor.dispose()
        highlightedLineColor.dispose()
        highlightedWordColor.dispose()
        tabSelectedBackgroundColor.dispose()
        tabSelectedForegroundColor.dispose()
        windowBackgroundColor.dispose()
        windowForegroundColor.dispose()
        tabBackgroundColor.dispose()
        tabForegroundColor.dispose()
        treeSelectedFocusedColor.dispose()
        treeSelectedColor.dispose()
        treeSelectedForegroundColor.dispose()
        treeHoverColor.dispose()
        toolBarHoverColor.dispose()
        toolBarClickColor.dispose()
        consoleBackgroundColor.dispose()
        consoleForegroundColor.dispose()
        successTextColor.dispose()
        errorTextColor.dispose()
        warningTextColor.dispose()
        infoTextColor.dispose()
        debugTextColor.dispose()
        tabErrorTextColor.dispose()
        tabWarningTextColor.dispose()
        tabNormalTextColor.dispose()
        tabHoverTextColor.dispose()
        autocompleteBackgroundColor.dispose()
        autocompleteForegroundColor.dispose()
        autocompleteHighlightColor.dispose()
        searchBackgroundColor.dispose()
        searchForegroundColor.dispose()
        bracketUnderlineColor.dispose()
        clockMarkerColor.dispose()
        waveButtonHoverColor.dispose()
        waveButtonActiveColor.dispose()
        waveGridColor.dispose()
        waveCursorColor.dispose()
        defaultFont.dispose()
        boldFont.dispose()
        monoFont.dispose()
        set = false
    }
}