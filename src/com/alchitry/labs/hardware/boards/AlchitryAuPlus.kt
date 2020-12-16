package com.alchitry.labs.hardware.boards

import com.alchitry.labs.hardware.loaders.AuLoader
import com.alchitry.labs.hardware.pinout.AlchitryAuPinConverter
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor
import com.alchitry.labs.project.builders.VivadoBuilder
import com.alchitry.labs.widgets.IoRegion

object AlchitryAuPlus : Board() {

    override val ioRegions = arrayOf(IoRegion("Bank A", 0, 0.289769230769231, 0.0274666666666666, 0.235846153846154, 0.145044444444444).also {
        it.signals = arrayOf("A2", "A3", "A5", "A6", "A8", "A9", "A11", "A12", "A14", "A15", "A17", "A18", "A20", "A21", "A23", "A24", "A27", "A28", "A30",
                "A31", "A33", "A34", "A36", "A37", "A39", "A40", "A42", "A43", "A45", "A46", "A48", "A49")
    },
            IoRegion("Bank B", 1, 0.682076923076923, 0.0274666666666666, 0.235846153846154, 0.145044444444444).also {
                it.signals = arrayOf("B2", "B3", "B5", "B6", "B8", "B9", "B11", "B12", "B14", "B15", "B17", "B18", "B20", "B21", "B23", "B24", "B27", "B28", "B30",
                        "B31", "B33", "B34", "B36", "B37", "B39", "B40", "B42", "B43", "B45", "B46", "B48", "B49")
            },
            IoRegion("Bank C", 2, 0.351307692307692, 0.827466666666667, 0.235846153846154, 0.145044444444444).also {
                it.signals = arrayOf("C2", "C3", "C5", "C6", "C8", "C9", "C11", "C12", "C14", "C15", "C17", "C18", "C20", "C21", "C23", "C24", "C27", "C28", "C30",
                        "C31", "C33", "C34", "C36", "C37", "C39", "C40", "C42", "C43", "C45", "C46", "C48", "C49")
            },
            IoRegion("Bank D", 3, 0.682076923076923, 0.827466666666667, 0.235846153846154, 0.145044444444444).also {
                it.signals = arrayOf("LED 0", "LED 1", "LED 2", "LED 3", "LED 4", "LED 5", "LED 6", "LED 7")
            },
            IoRegion("LEDs", 4, 0.930676923076923, 0.230355555555556, 0.0463538461538462, 0.339288888888889).also {
                it.signals = arrayOf("LED 0", "LED 1", "LED 2", "LED 3", "LED 4", "LED 5", "LED 6", "LED 7")
            },
            IoRegion("Reset", 5, 0.908246153846154, 0.634244444444444, 0.0757846153846154, 0.175933333333333).also {
                it.signals = arrayOf("Reset")
            },
            IoRegion("Clock", 6, 0.605984615384615, 0.748888888888889, 0.0557076923076923, 0.115555555555556).also {
                it.signals = arrayOf("100Mhz Clock")
            },
            IoRegion("USB", 7, 0.00189230769230769, 0.107711111111111, 0.137446153846154, 0.221177777777778),
            IoRegion("Flash", 8, 0.756538461538461, 0.566666666666667, 0.135384615384615, 0.111111111111111),
            IoRegion("RAM", 9, 0.769230769230769, 0.233333333333333, 0.123076923076923, 0.311111111111111))
    override val usbDescriptor = UsbDescriptor("Alchitry Au+", 0x0403.toShort(), 0x6010.toShort(), "Alchitry Au+")


    //return "xc7a100tftg256-1";
    override val fpgaName = "xc7a100tftg256-1"

    override val name = "Alchitry Au+"
    override val exampleProjectDir = "alchitry-au-plus"
    override val builder = VivadoBuilder()
    override val loader = AuLoader(isPlus = true)
    override val sVGPath = "/images/au.svg"
    override val supportedConstraintExtensions = arrayOf(".acf", ".xdc")
    override val pinConverter = AlchitryAuPinConverter()
}