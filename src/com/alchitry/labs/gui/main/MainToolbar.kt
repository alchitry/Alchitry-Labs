package com.alchitry.labs.gui.main

import com.alchitry.labs.Util.showError
import com.alchitry.labs.gui.StyledCodeEditor
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.gui.util.Images
import com.alchitry.labs.hardware.boards.Board
import com.alchitry.labs.widgets.CustomButton
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.MessageBox
import java.util.*

class MainToolbar {
    private val toolbar = Composite(MainWindow.shell, SWT.NONE)
    private var buildButton: CustomButton? = null
    private val buttons = ArrayList<CustomButton>()

    init {
        toolbar.background = Theme.windowBackgroundColor
        toolbar.foreground = Theme.windowForegroundColor
        val layout = RowLayout(SWT.HORIZONTAL)
        toolbar.layout = layout
    }

    private fun createButton(icon: Image, hover: Image, toolTip: String, selectionListener: Listener): CustomButton {
        val button = CustomButton(toolbar, SWT.NONE)
        buttons.add(button)
        button.setIcon(icon)
        button.setIconHover(hover)
        button.toolTipText = toolTip
        button.addListener(SWT.Selection, selectionListener)
        return button
    }

    fun setBuilding(building: Boolean) {
        buildButton?.let { buildButton ->
            if (building) {
                buildButton.setIcon(Images.cancelIcon)
                buildButton.setIconHover(Images.cancelIconHover)
                buildButton.toolTipText = "Cancel Build"
            } else {
                buildButton.setIcon(Images.buildIcon)
                buildButton.setIconHover(Images.buildIconHover)
                buildButton.toolTipText = "Build Project"
            }
            buildButton.redraw()
        }
    }

    fun build() {
        for (b in buttons) b.dispose()
        buttons.clear()
        MainWindow.project?.let { project ->
            val board = project.board
            createButton(Images.fileIcon, Images.fileIconHover, "New File") { MainWindow.addNewFile() }
            createButton(Images.saveIcon, Images.saveIconHover, "Save File") {
                val tc = MainWindow.tabFolder.selectedControl
                if (tc is StyledCodeEditor) MainWindow.saveEditor(tc, false)
            }
            createButton(Images.saveAllIcon, Images.saveAllIconHover, "Save All") { MainWindow.saveAll(false) }
            createButton(Images.checkIcon, Images.checkIconHover, "Check Syntax") { MainWindow.checkProject() }
            if (Board.isType(board, Board.ANY)) buildButton = createButton(Images.buildIcon, Images.buildIconHover, "Build Project") { MainWindow.buildProject() }
            if (Board.isType(board, Board.MOJO or Board.AU)) createButton(Images.debugIcon, Images.debugIconHover, "Debug Project", Listener {
                if (project.isBusy) {
                    showError("Can't debug while operation is in progress!")
                    return@Listener
                }
                if (!MainWindow.saveAll(false)) {
                    showError("Could not save all open tabs before debugging!")
                    val box = MessageBox(MainWindow.shell, SWT.YES or SWT.NO)
                    box.message = "Continue anyway?"
                    box.text = "All files not saved..."
                    if (box.open() != SWT.YES) {
                        return@Listener
                    }
                }
                project.build(true)
            })
            if (Board.isType(board, Board.MOJO or Board.AU or Board.AU_PLUS)) createButton(Images.loadTempIcon, Images.loadTempIconHover, "Program (Temporary)") { MainWindow.programProject(flash = false, verify = false) }
            if (Board.isType(board, Board.ANY)) createButton(Images.loadIcon, Images.loadIconHover, "Program (Flash)") { event -> MainWindow.programProject(true, event.button == 2) }
            if (Board.isType(board, Board.ANY)) createButton(Images.eraseIcon, Images.eraseIconHover, "Erase", Listener {
                if (project.isBusy) {
                    showError("Operation already in progress!")
                    return@Listener
                }
                project.erase()
            })
        }

        toolbar.requestLayout()
    }


}