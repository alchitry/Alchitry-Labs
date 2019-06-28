package com.alchitry.labs.hardware.loaders;

import java.util.ArrayList;
import java.util.List;

import org.usb4java.Device;
import org.usb4java.LibUsb;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.DeviceSelector.DeviceSelectorRunnable;
import com.alchitry.labs.hardware.ftdi.Ftdi;
import com.alchitry.labs.hardware.ftdi.enums.PortInterfaceType;

public abstract class AlchitryLoader extends ProjectLoader {
	protected abstract String getDeviceDesciption();

	protected Device getDevice(Ftdi ftdi) {
		Device dev = null;
		List<Device> devs = ftdi.usbFindAll((short) 0x0403, (short) 0x6010, getDeviceDesciption(), null);
		if (devs.size() == 1) {
			dev = devs.get(0);
		} else if (devs.size() > 1) {
			List<String> devList = new ArrayList<>();
			for (int i = 0; i < devs.size(); i++) {
				devList.add((i + 1) + ": " + getDeviceDesciption());
			}

			DeviceSelectorRunnable dsr = new DeviceSelectorRunnable(devList);

			Util.syncExec(dsr);

			if (dsr.result != null) {
				dev = devs.get(devList.indexOf(dsr.result));
				Util.println("Selected " + dsr.result);
			}
		} else {
			Util.println("Couldn't find device!", true);
		}
		if (dev != null) {
			dev = LibUsb.refDevice(dev);
		}
		ftdi.listFree(devs);
		return dev;
	}

	protected Ftdi openDevice() {
		Ftdi ftdi = new Ftdi();
		ftdi.setInterface(PortInterfaceType.INTERFACE_A);
		Device dev = getDevice(ftdi);
		if (dev == null) {
			return null;
		}
		ftdi.usbOpenDev(dev);
		LibUsb.unrefDevice(dev);

		return ftdi;
	}

}
