package com.alchitry.labs.gui.tools

import com.alchitry.labs.Util.showError
import com.alchitry.labs.hardware.RegisterInterface
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import org.usb4java.LibUsbException
import java.math.BigInteger

class RegInterface(parent: Shell) {
    private val shell = Shell(parent, SWT.CLOSE or SWT.RESIZE or SWT.MIN or SWT.TITLE or SWT.MAX)
    private val address: Text
    private val value: Text
    private val decAddr: Button
    private val decVal: Button
    private val regInt = RegisterInterface()

    init {
        shell.text = "Register Interface"
        shell.layout = GridLayout(3, false)
        val lblAddress = Label(shell, SWT.NONE)
        lblAddress.layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        lblAddress.text = "Address:"
        address = Text(shell, SWT.BORDER)
        address.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1)
        Label(shell, SWT.NONE)
        val composite = Composite(shell, SWT.NONE)
        composite.layoutData = GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1)
        composite.layout = FillLayout(SWT.HORIZONTAL)
        decAddr = Button(composite, SWT.RADIO)
        decAddr.text = "Decimal"
        decAddr.selection = true
        val hexAddr = Button(composite, SWT.RADIO)
        hexAddr.text = "Hex"
        val lblValue = Label(shell, SWT.NONE)
        lblValue.layoutData = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        lblValue.text = "Value:"
        value = Text(shell, SWT.BORDER)
        val gdText = GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1)
        gdText.widthHint = 223
        value.layoutData = gdText
        Label(shell, SWT.NONE)
        val composite2 = Composite(shell, SWT.NONE)
        composite2.layoutData = GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1)
        composite2.layout = FillLayout(SWT.HORIZONTAL)
        decVal = Button(composite2, SWT.RADIO)
        decVal.text = "Decimal"
        decVal.selection = true
        val hexVal = Button(composite2, SWT.RADIO)
        hexVal.text = "Hex"
        Label(shell, SWT.NONE)
        val writeButton = Button(shell, SWT.NONE)
        writeButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val address = getNumber(address.text, decAddr.selection)
                val `val` = getNumber(value.text, decVal.selection)
                if (address == -1L) {
                    showError("Address is not a valid " + (if (decAddr.selection) "decimal" else "hex") + " number", "Error!", shell)
                    setFocus()
                    return
                }
                if (`val` == -1L) {
                    showError("Value is not a valid " + (if (decVal.selection) "decimal" else "hex") + " number", "Error!", shell)
                    setFocus()
                    return
                }
                try {
                    regInt.write(address.toInt(), `val`.toInt())
                } catch (e1: LibUsbException) {
                    showError("Failed to write data!", "Error!", shell)
                }
            }
        })
        val gdDataWriteButton = GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1)
        gdDataWriteButton.minimumWidth = 100
        writeButton.layoutData = gdDataWriteButton
        writeButton.text = "Write"
        val readButton = Button(shell, SWT.NONE)
        readButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val addr = getNumber(address.text, decAddr.selection)
                if (addr == -1L) {
                    showError("Address is not a valid " + (if (decAddr.selection) "decimal" else "hex") + " number", "Error!", shell)
                    return
                }
                try {
                    val readValue = regInt.read(addr.toInt())
                    value.text = readValue.toString(if (decVal.selection) 10 else 16)
                } catch (e1: Exception) {
                    showError("Failed to read data!", "Error!", shell)
                }
            }
        })
        val gdDataReadButton = GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1)
        gdDataReadButton.widthHint = 100
        gdDataReadButton.minimumWidth = 100
        readButton.layoutData = gdDataReadButton
        readButton.text = "Read"
        shell.pack()
        shell.minimumSize = shell.size
        if (regInt.connect()) {
            shell.addDisposeListener {
                regInt.disconnect()
            }

            shell.open()
            shell.layout()
            shell.setFocus()
        }
    }

    private fun getNumber(s: String, dec: Boolean): Long {
        return try {
            val bigint = BigInteger(s, if (dec) 10 else 16)
            bigint.toLong()
        } catch (e: NumberFormatException) {
            -1
        } catch (e: ArithmeticException) {
            -1
        }
    }

    fun setFocus() {
        shell.setFocus()
    }

    val isDisposed: Boolean
        get() = shell.isDisposed


}