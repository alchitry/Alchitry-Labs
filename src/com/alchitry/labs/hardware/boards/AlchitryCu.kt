package com.alchitry.labs.hardware.boards

import com.alchitry.labs.Settings.USE_ICESTORM
import com.alchitry.labs.hardware.loaders.CuLoader
import com.alchitry.labs.hardware.pinout.AlchitryCuPinConverter
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor
import com.alchitry.labs.project.builders.IceCubeBuilder
import com.alchitry.labs.project.builders.IceStormBuilder
import com.alchitry.labs.project.builders.ProjectBuilder
import com.alchitry.labs.widgets.IoRegion

object AlchitryCu : Board() {
    override val ioRegions = arrayOf(IoRegion("Bank A", 0, 0.289769230769231, 0.0274666666666666, 0.235846153846154, 0.145044444444444).also { it.signals = arrayOf("A2", "A3") },
            IoRegion("Bank B", 1, 0.682076923076923, 0.0274666666666666, 0.235846153846154, 0.145044444444444),
            IoRegion("Bank C", 2, 0.351307692307692, 0.827466666666667, 0.235846153846154, 0.145044444444444),
            IoRegion("Bank D", 3, 0.682076923076923, 0.827466666666667, 0.235846153846154, 0.145044444444444),
            IoRegion("LEDs", 4, 0.930676923076923, 0.230355555555556, 0.0463538461538462, 0.339288888888889),
            IoRegion("Reset", 5, 0.908246153846154, 0.634244444444444, 0.0757846153846154, 0.175933333333333),
            IoRegion("Clock", 6, 0.583353846153846, 0.664444444444445, 0.0557076923076923, 0.115555555555556),
            IoRegion("USB", 7, 0.00189230769230769, 0.107711111111111, 0.137446153846154, 0.221177777777778),
            IoRegion("Flash", 8, 0.724230769230769, 0.575555555555555, 0.135384615384615, 0.111111111111111))

    override val usbDescriptor = UsbDescriptor("Alchitry Cu", 0x0403.toShort(), 0x6010.toShort(), "Alchitry Cu")

    override val fPGAName = "ICE40HX8K-CB132IC"
    override val name = "Alchitry Cu"
    override val exampleProjectDir = "alchitry-cu"
    override val builder: ProjectBuilder
        get() = if (USE_ICESTORM) IceStormBuilder() else IceCubeBuilder()
    override val loader = CuLoader()
    override val sVGPath = "/images/cu.svg"
    override val supportedConstraintExtensions = arrayOf(".acf", ".pcf", ".sdc")
    override val pinConverter = AlchitryCuPinConverter()
}