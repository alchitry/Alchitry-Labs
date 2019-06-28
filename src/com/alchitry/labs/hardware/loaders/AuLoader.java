package com.alchitry.labs.hardware.loaders;

import java.io.IOException;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.hardware.ftdi.Ftdi;
import com.alchitry.labs.hardware.ftdi.XilinxJtag;
import com.alchitry.labs.hardware.ftdi.enums.PortInterfaceType;

public class AuLoader extends ProjectLoader {

	@Override
	protected void eraseFlash() {
		Ftdi ftdi = new Ftdi();
		ftdi.setInterface(PortInterfaceType.INTERFACE_A);
		if (!ftdi.usbOpen(0x0403, 0x6010, "Alchitry Au", null)) {
			Util.println("Couldn't find device!",true);
			return;
		}
		XilinxJtag xil = new XilinxJtag(ftdi);
		xil.checkIDCODE();
		try {
			xil.eraseFlash();
		} catch (IOException e) {
			Util.logException(e);
			Util.println("Failed to erase flash!", true);
		}
		ftdi.usbClose();
	}

	@Override
	protected void program(String binFile, boolean flash, boolean verify) {
		if (verify) {
			Util.println("Verify isn't currently supported on the Au!", Theme.infoTextColor);
		}
		
		Ftdi ftdi = new Ftdi();
		ftdi.setInterface(PortInterfaceType.INTERFACE_A);
		if (!ftdi.usbOpen(0x0403, 0x6010, "Alchitry Au", null)) {
			Util.println("Couldn't find device!",true);
			return;
		}
		XilinxJtag xil = new XilinxJtag(ftdi);
		xil.checkIDCODE();
		try {
			xil.writeBin(binFile, flash);
		} catch (IOException e) {
			Util.logException(e);
			Util.println("Failed to write bin file!", true);
		}
		ftdi.usbClose();
	}

}
