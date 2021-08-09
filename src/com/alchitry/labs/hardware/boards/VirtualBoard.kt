package com.alchitry.labs.hardware.boards

import com.alchitry.labs.project.builders.VirtualBuilder
import com.alchitry.labs.hardware.loaders.ProjectLoader
import com.alchitry.labs.hardware.pinout.PinConverter
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor
import com.alchitry.labs.project.builders.ProjectBuilder
import com.alchitry.labs.widgets.IoRegion


object VirtualBoard : Board() {
    override val name = "Lucid HDL"
    override val builder: ProjectBuilder
        get() = VirtualBuilder()
    override val supportedConstraintExtensions = arrayOf(".acf", ".pcf", ".sdc")

    override val usbDescriptor: UsbDescriptor
        get() = TODO("Not yet implemented")
    override val fpgaName: String
        get() = TODO("Not yet implemented")
    override val exampleProjectDir: String
        get() = TODO("Not yet implemented")
    override val loader: ProjectLoader
        get() = TODO("Not yet implemented")
    override val ioRegions: Array<IoRegion>
        get() = TODO("Not yet implemented")
    override val sVGPath: String
        get() = TODO("Not yet implemented")
    override val pinConverter: PinConverter
        get() = TODO("Not yet implemented")
}