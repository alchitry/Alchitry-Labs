package com.alchitry.labs.gui

import com.alchitry.labs.VERSION
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell
import org.eclipse.wb.swt.SWTResourceManager

class WelcomeDialog(parent: Shell) : Dialog(parent, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL) {
    private val shell: Shell = Shell(parent, style)
    fun open() {
        shell.open()
        shell.layout()
        val display = parent.display
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
    }

    init {
        text = "Welcome"
        shell.minimumSize = Point(700, 450)
        shell.setSize(700, 450)
        shell.text = text
        shell.layout = GridLayout(1, false)
        val lblTitle = Label(shell, SWT.NONE)
        lblTitle.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1)
        lblTitle.alignment = SWT.CENTER
        lblTitle.font = SWTResourceManager.getFont("Ubuntu", 24, SWT.BOLD)
        lblTitle.text = "Welcome to Alchitry Labs"
        val lblVersion = Label(shell, SWT.NONE)
        lblVersion.layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        lblVersion.text = "Version $VERSION"
        val lblReleaseInfo = Label(shell, SWT.WRAP)
        lblReleaseInfo.layoutData = GridData(SWT.HORIZONTAL, SWT.TOP, true, true, 1, 1)
        lblReleaseInfo.text = """|Welcome to version $VERSION of Alchitry Labs! We are actively working to improve the IDE so please send us your feedback at bugspray@alchitry.com
                                 |
                                 |Bug fixes. Lots of them.
                                 |
                                 |As always, we hope you enjoy this version!""".trimMargin()
        val btnOk = Button(shell, SWT.NONE)
        btnOk.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                shell.close()
            }
        })
        val bgBtnCk = GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1)
        bgBtnCk.widthHint = 100
        btnOk.layoutData = bgBtnCk
        btnOk.text = "Ok"
        val parentSize = parent.bounds
        val shellSize = shell.bounds
        val locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x
        val locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y
        shell.location = Point(locationX, locationY)
    }
}