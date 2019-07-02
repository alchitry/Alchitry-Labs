package com.alchitry.labs.hardware.usb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.usb4java.Device;
import org.usb4java.LibUsb;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.DeviceSelector.DeviceSelectorRunnable;
import com.alchitry.labs.hardware.boards.AlchitryAu;
import com.alchitry.labs.hardware.boards.AlchitryCu;
import com.alchitry.labs.hardware.boards.Mojo;
import com.alchitry.labs.hardware.usb.ftdi.Ftdi;
import com.alchitry.labs.hardware.usb.ftdi.enums.PortInterfaceType;

public class UsbUtil {
	public static UsbDescriptor MOJO_DESC = new Mojo().getUsbDesciptor();
	public static UsbDescriptor AU_DESC = new AlchitryAu().getUsbDesciptor();
	public static UsbDescriptor CU_DESC = new AlchitryCu().getUsbDesciptor();
	public static List<UsbDescriptor> ALL_DEVICES = Arrays
			.asList(new UsbDescriptor[] { MOJO_DESC, AU_DESC, CU_DESC });

	public static class UsbDescriptor {
		public short vid;
		public short pid;
		public String product;
		public String name;

		public UsbDescriptor(String name, short vid, short pid, String product) {
			this.vid = vid;
			this.pid = pid;
			this.name = name;
			this.product = product;
		}
	}

	public static DeviceEntry getDevice(UsbDescriptor board) {
		List<UsbDescriptor> list = new ArrayList<>();
		list.add(board);
		return getDevice(list);
	}

	public static DeviceEntry getDevice(List<UsbDescriptor> boards) {
		DeviceEntry dev = null;
		DeviceEntry result = null;
		List<DeviceEntry> devs = UsbDevice.usbFindAll(boards);
		if (devs.size() == 1) {
			dev = devs.get(0);
		} else if (devs.size() > 1) {
			List<String> devList = new ArrayList<>();
			for (int i = 0; i < devs.size(); i++) {
				devList.add((i + 1) + ": " + devs.get(i).description.name);
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
			result = new DeviceEntry(dev.description, LibUsb.refDevice(dev.device));
		}
		UsbDevice.entryListFree(devs);
		return result;
	}

	public static class DeviceEntry {
		public Device device;
		public UsbDescriptor description;

		public DeviceEntry(UsbDescriptor description, Device device) {
			this.description = description;
			this.device = device;
		}
	}

	public static List<DeviceEntry> getDevices(List<UsbDescriptor> devices) {
		return null;
	}

	public static Ftdi openFtdiDevice(PortInterfaceType iface, UsbDescriptor board) {
		Ftdi ftdi = new Ftdi();
		ftdi.setInterface(iface);
		DeviceEntry dev = getDevice(board);
		if (dev == null) {
			return null;
		}
		ftdi.usbOpenDev(dev.device);
		LibUsb.unrefDevice(dev.device);

		return ftdi;
	}

	public static MojoSerial openMojoSerial() {
		MojoSerial mojo = new MojoSerial();
		DeviceEntry dev = getDevice(new Mojo().getUsbDesciptor());
		if (dev == null) {
			return null;
		}
		mojo.usbOpenDev(dev.device);
		LibUsb.unrefDevice(dev.device);

		return mojo;
	}

	public static UsbSerial openSerial() {
		DeviceEntry dev = getDevice(ALL_DEVICES);
		if (dev == null)
			return null;
		UsbSerial device = null;
		if (dev.description == MOJO_DESC) {
			device = new MojoSerial();
		} else {
			device = new Ftdi();
			((Ftdi)device).setInterface(PortInterfaceType.INTERFACE_B);
		}
		device.usbOpenDev(dev.device);
		LibUsb.unrefDevice(dev.device);
		return device;
	}

}
