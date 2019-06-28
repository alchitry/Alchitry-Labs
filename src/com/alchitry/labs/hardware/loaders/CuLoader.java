package com.alchitry.labs.hardware.loaders;

import java.io.IOException;

import org.usb4java.LibUsbException;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.hardware.ftdi.Ftdi;
import com.alchitry.labs.hardware.ftdi.LatticeSpi;
import com.alchitry.labs.hardware.ftdi.Mpsse.MpsseException;

public class CuLoader extends AlchitryLoader {
	@Override
	protected void eraseFlash() {
		Ftdi ftdi = null;
		try {
			ftdi = openDevice();
			if (ftdi == null)
				return;

			LatticeSpi spi = new LatticeSpi(ftdi);
			spi.eraseFlash();
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
		if (verify)
			Util.println("Verify isn't currently supported on the Cu!", Theme.infoTextColor);

		if (!flash)
			Util.println("FPGA programming isn't supported on the Cu!", Theme.infoTextColor);

		Ftdi ftdi = null;
		try {
			ftdi = openDevice();
			if (ftdi == null)
				return;

			LatticeSpi spi = new LatticeSpi(ftdi);
			try {
				spi.writeBin(binFile);
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
		return "Alchitry Cu";
	}

}
