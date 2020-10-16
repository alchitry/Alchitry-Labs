package com.alchitry.labs.hardware.usb

import com.alchitry.labs.Util
import com.alchitry.labs.gui.DeviceSelector.DeviceSelectorRunnable
import com.alchitry.labs.hardware.boards.AlchitryAu
import com.alchitry.labs.hardware.boards.AlchitryCu
import com.alchitry.labs.hardware.boards.Mojo
import com.alchitry.labs.hardware.usb.ftdi.Ftdi
import com.alchitry.labs.hardware.usb.ftdi.FtdiD2xx
import com.alchitry.labs.hardware.usb.ftdi.FtdiLibUSB
import com.alchitry.labs.hardware.usb.ftdi.enums.PortInterfaceType
import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swt.SWT
import net.sf.yad2xx.Device
import net.sf.yad2xx.FTDIException
import net.sf.yad2xx.FTDIInterface
import org.usb4java.LibUsb
import org.usb4java.LibUsbException
import java.util.*

object UsbUtil {
    private val MOJO_DESC = Mojo.usbDescriptor
    private val AU_DESC = AlchitryAu.usbDescriptor
    private val CU_DESC = AlchitryCu.usbDescriptor

    @JvmField
    var ALL_DEVICES = listOf(MOJO_DESC, AU_DESC, CU_DESC)

    @JvmField
    var MOJO_DEVICES = listOf(MOJO_DESC)
    var ALCHITRY_DEVICES = listOf(AU_DESC, CU_DESC)

    @JvmField
    var AU_DEVICES = listOf(AU_DESC)

    @JvmField
    var CU_DEVICES = listOf(CU_DESC)
    fun getDevice(boards: List<UsbDescriptor>?): DeviceEntry? {
        var dev: DeviceEntry? = null
        var result: DeviceEntry? = null
        val devs = UsbDevice.usbFindAll(boards)
        if (devs.size == 1) {
            dev = devs[0]
        } else if (devs.size > 1) {
            val devList: MutableList<String> = ArrayList()
            devs.forEachIndexed { i, it -> devList.add("${i + 1}: ${it.description.name}") }
            val dsr = DeviceSelectorRunnable(devList)
            runBlocking(Dispatchers.SWT) { dsr.run() }
            if (dsr.result != null) {
                dev = devs[devList.indexOf(dsr.result)]
                Util.println("Selected ${dsr.result}")
            }
        }
        if (dev != null) {
            result = DeviceEntry(dev.description, LibUsb.refDevice(dev.device))
        }
        UsbDevice.entryListFree(devs)
        return result
    }

    @Throws(FTDIException::class)
    private fun findD2xxDevice(iface: PortInterfaceType, board: List<UsbDescriptor>): Device? {
        val devices = FTDIInterface.getDevices()
        for (d in devices) {
            val desc = d.description
            if (desc.isNotEmpty() && iface.letterMatches(desc[(desc.length - 1).coerceAtLeast(0)])) {
                val product = desc.substring(0, desc.length - 2)
                for (b in board) if (product == b.product) {
                    return d
                }
            }
        }
        return null
    }

    @JvmStatic
    fun openFtdiDevice(iface: PortInterfaceType, board: List<UsbDescriptor>): Ftdi? {
        if (Util.isWindows) {
            try {
                val dev = findD2xxDevice(iface, board)
                if (dev != null) {
                    dev.open()
                    return FtdiD2xx(dev)
                }
            } catch (e: FTDIException) {
                Util.reportException(e)
            }
        }
        try {
            val ftdi = FtdiLibUSB()
            ftdi.setInterface(iface)
            val dev = getDevice(board) ?: return null
            ftdi.usbOpenDev(dev.device)
            LibUsb.unrefDevice(dev.device)
            return ftdi
        } catch (e: LibUsbException) {
            Util.reportException(e)
        }
        return null
    }

    @JvmStatic
    fun openMojoSerial(): SerialDevice? {
        return openSerial(MOJO_DEVICES)
    }

    @JvmStatic
    @JvmOverloads
    fun openSerial(devices: List<UsbDescriptor> = ALL_DEVICES): SerialDevice? {
        return try {
            val dev = getDevice(devices)
            if (dev == null) {
                var portName: String? = null
                if (Util.isWindows) {
                    try {
                        val d2xx = findD2xxDevice(PortInterfaceType.INTERFACE_B, devices)
                        if (d2xx != null) {
                            d2xx.open()
                            portName = "COM" + d2xx.comPortNumber
                            d2xx.close()
                            Util.println("Found board on $portName")
                        }
                    } catch (e: FTDIException) {
                        Util.printException(e)
                    }
                }
                if (portName == null) for (d in devices) {
                    if (d === MOJO_DESC) {
                        for (sp in SerialPort.getCommPorts()) {
                            if (sp.descriptivePortName.contains("Mojo V")) {
                                portName = sp.systemPortName
                                break
                            }
                        }
                    }
                }
                if (portName != null) {
                    val port = SerialPort.getCommPort(portName)
                    if (port != null) {
                        val serial = GenericSerial(port)
                        if (serial.open()) return serial else Util.println("Failed to open serial port $portName", true)
                    }
                }
                Util.println("No devices found...", true)
                return null
            }
            val device: SerialDevice
            if (dev.description === MOJO_DESC) {
                device = MojoLibUsbSerial()
                device.usbOpenDev(dev.device)
            } else {
                device = FtdiLibUSB()
                device.setInterface(PortInterfaceType.INTERFACE_B)
                device.usbOpenDev(dev.device)
            }
            LibUsb.unrefDevice(dev.device)
            device
        } catch (e: LibUsbException) {
            Util.printException(e)
            null
        }
    }

    data class UsbDescriptor(val name: String, val vid: Short, val pid: Short, val product: String?)
    data class DeviceEntry(val description: UsbDescriptor, val device: org.usb4java.Device)
}