package com.alchitry.labs.hardware.loaders

import com.alchitry.labs.Util.printException
import com.alchitry.labs.Util.println
import com.alchitry.labs.Util.reportException
import com.alchitry.labs.gui.Theme
import com.alchitry.labs.hardware.boards.AlchitryAu
import com.alchitry.labs.hardware.boards.AlchitryAuPlus
import com.alchitry.labs.hardware.usb.UsbUtil.openFtdiDevice
import com.alchitry.labs.hardware.usb.ftdi.Ftdi
import com.alchitry.labs.hardware.usb.ftdi.Mpsse.MpsseException
import com.alchitry.labs.hardware.usb.ftdi.XilinxJtag
import com.alchitry.labs.hardware.usb.ftdi.enums.PortInterfaceType
import org.usb4java.LibUsbException
import java.io.IOException

class AuLoader(val isPlus: Boolean = false) : ProjectLoader() {
    val idCode = if (isPlus) "13631093" else "0362D093"
    val deviceList = listOf(if (isPlus) AlchitryAuPlus.usbDescriptor else AlchitryAu.usbDescriptor)
    val name = if (isPlus) AlchitryAuPlus.name else AlchitryAu.name
    val loaderFile = if (isPlus) XilinxJtag.AU_PLUS_LOADER_FILE else XilinxJtag.AU_LOADER_FILE

    override fun eraseFlash() {
        var ftdi: Ftdi? = null
        try {
            ftdi = openFtdiDevice(PortInterfaceType.INTERFACE_A, deviceList)
            if (ftdi == null) {
                println("Could not detect an $name!", true)
                return
            }
            val xil = XilinxJtag(ftdi)
            xil.checkIDCODE(idCode)
            try {
                xil.eraseFlash(loaderFile)
            } catch (e: IOException) {
                reportException(e)
                println("Failed to erase flash!", true)
            }
        } catch (e: LibUsbException) {
            printException(e)
        } catch (e: MpsseException) {
            printException(e)
        } finally {
            ftdi?.usbClose()
        }
    }

    override fun program(binFile: String, flash: Boolean, verify: Boolean) {
        if (verify) {
            println("Verify isn't currently supported on the $name!", Theme.infoTextColor)
        }
        var ftdi: Ftdi? = null
        try {
            ftdi = openFtdiDevice(PortInterfaceType.INTERFACE_A, deviceList)
            if (ftdi == null) {
                println("Could not detect an $name!", true)
                return
            }
            val xil = XilinxJtag(ftdi)
            xil.checkIDCODE(idCode)
            try {
                xil.writeBin(binFile, flash, loaderFile)
            } catch (e: IOException) {
                reportException(e)
                println("Failed to write bin file!", true)
            }
        } catch (e: LibUsbException) {
            printException(e)
        } catch (e: MpsseException) {
            printException(e)
        } finally {
            ftdi?.usbClose()
        }
    }
}