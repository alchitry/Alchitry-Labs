package com.alchitry.labs.hardware.loaders;

import java.io.IOException;

import org.usb4java.LibUsbException;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.hardware.ftdi.Ftdi;
import com.alchitry.labs.hardware.ftdi.Mpsse.MpsseException;
import com.alchitry.labs.hardware.ftdi.XilinxJtag;

public class AuLoader extends AlchitryLoader {

	@Override
	protected void eraseFlash() {
		Ftdi ftdi = null;
		try {
			ftdi = openDevice();
			if (ftdi == null) 
				return;
			
			XilinxJtag xil = new XilinxJtag(ftdi);
			xil.checkIDCODE();
			try {
				xil.eraseFlash();
			} catch (IOException e) {
				Util.logException(e);
				Util.println("Failed to erase flash!", true);
			}
		} catch (LibUsbException | MpsseException e) {
			Util.logException(e);
		} finally {
			if (ftdi != null) {
				ftdi.usbClose();
			}
		}
	}

	@Override
	protected void program(String binFile, boolean flash, boolean verify) {
		if (verify) {
			Util.println("Verify isn't currently supported on the Au!", Theme.infoTextColor);
		}

		Ftdi ftdi = null;
		try {
			ftdi = openDevice();
			if (ftdi == null) 
				return;
			
			XilinxJtag xil = new XilinxJtag(ftdi);
			xil.checkIDCODE();
			try {
				xil.writeBin(binFile, flash);
			} catch (IOException e) {
				Util.logException(e);
				Util.println("Failed to write bin file!", true);
			}
		} catch (LibUsbException | MpsseException e) {
			Util.logException(e);
		} finally {
			if (ftdi != null) {
				ftdi.usbClose();
			}
		}
	}

	@Override
	protected String getDeviceDesciption() {
		return "Alchitry Au";
	}

}
