package com.alchitry.labs.hardware.boards

import com.alchitry.labs.hardware.loaders.MojoLoader
import com.alchitry.labs.hardware.pinout.PinConverter
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor
import com.alchitry.labs.project.builders.ISEBuilder
import com.alchitry.labs.widgets.IoRegion

object Mojo : Board() {
    override val usbDescriptor = UsbDescriptor("Mojo", 0x29DD.toShort(), 0x8001.toShort(), null)
    override val fPGAName = "xc6slx9-2tqg144"
    override val name = "Mojo"
    override val exampleProjectDir = "mojo"
    override val builder = ISEBuilder()
    override val loader = MojoLoader()
    override val supportedConstraintExtensions = arrayOf(".ucf")
    override val pinConverter: PinConverter
        get() = TODO("Not yet implemented")
    override val ioRegions: Array<IoRegion>
        get() = TODO("Not yet implemented")
    override val sVGPath: String
        get() = TODO("Not yet implemented")
}