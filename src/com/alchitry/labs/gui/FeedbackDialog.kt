package com.alchitry.labs.gui

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

class FeedbackDialog {
    private var result: FeedbackResult? = null
    private var shell: Shell = Shell(Display.getDefault(), SWT.APPLICATION_MODAL or SWT.DIALOG_TRIM or SWT.CLOSE)
    private var nameText: Text
    private var emailText: Text
    private var commentsText: Text
    private var submitButton: Button

    data class FeedbackResult(val name: String, val email: String, val message: String)

    /**
     * Open the dialog.
     *
     * @return the result
     */
    fun open(): FeedbackResult? {
        shell.open()
        shell.layout()
        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
        return result
    }

    /**
     * Create contents of the dialog.
     */
    init {
        shell.setSize(450, 300)
        shell.text = "FeedbackForm"
        shell.layout = GridLayout(2, false)
        val lblNewLabel = Label(shell, SWT.NONE)
        lblNewLabel.layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        lblNewLabel.text = "Name:"
        nameText = Text(shell, SWT.BORDER)
        nameText.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1)
        val emailLabel = Label(shell, SWT.NONE)
        emailLabel.layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        emailLabel.text = "Email:"
        emailText = Text(shell, SWT.BORDER)
        emailText.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1)
        val commentsLabel = Label(shell, SWT.NONE)
        commentsLabel.layoutData = GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1)
        commentsLabel.text = "Comments:"
        commentsText = Text(shell, SWT.BORDER or SWT.WRAP or SWT.MULTI or SWT.V_SCROLL or SWT.H_SCROLL)
        commentsText.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
        Label(shell, SWT.NONE)
        submitButton = Button(shell, SWT.NONE)
        submitButton.layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        submitButton.text = "Submit"
        submitButton.addSelectionListener(object : SelectionListener {
            override fun widgetSelected(e: SelectionEvent) {
                val email = if (emailText.text.isBlank()) "noreply@alchitry.com" else emailText.text
                result = FeedbackResult(nameText.text, email, commentsText.text)
                shell.dispose()
            }

            override fun widgetDefaultSelected(e: SelectionEvent) {
                // TODO Auto-generated method stub
            }
        })
    }
}