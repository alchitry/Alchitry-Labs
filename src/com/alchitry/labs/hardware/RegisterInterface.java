package com.alchitry.labs.hardware;

import java.util.List;

import org.usb4java.LibUsbException;

import com.alchitry.labs.hardware.usb.UsbSerial;
import com.alchitry.labs.hardware.usb.UsbUtil;
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor;

public class RegisterInterface {
	private UsbSerial serialPort;

	public RegisterInterface() {

	}

	public boolean isConnected() {
		if (serialPort == null)
			return false;
		return true;
	}
	
	public boolean connect() {
		return connect(UsbUtil.ALL_DEVICES);
	}

	public boolean connect(List<UsbDescriptor> devices) {
		serialPort = UsbUtil.openSerial(devices);
		if (serialPort == null)
			return false;
		serialPort.setBaudrate(1000000);
		serialPort.setTimeouts(1000, 1000);
		return true;
	}

	public boolean disconnect() {
		if (serialPort == null)
			return true;
		return serialPort.usbClose();
	}

	public boolean write(int address, int data) {
		byte[] buff = new byte[9];
		buff[0] = (byte) (1 << 7);
		buff[1] = (byte) (address & 0xff);
		buff[2] = (byte) ((address >> 8) & 0xff);
		buff[3] = (byte) ((address >> 16) & 0xff);
		buff[4] = (byte) ((address >> 24) & 0xff);
		buff[5] = (byte) (data & 0xff);
		buff[6] = (byte) ((data >> 8) & 0xff);
		buff[7] = (byte) ((data >> 16) & 0xff);
		buff[8] = (byte) ((data >> 24) & 0xff);
		return serialPort.writeData(buff) == buff.length;
	}

	public boolean write(int address, boolean increment, int[] data) {
		for (int i = 0; i < data.length; i += 64) {
			int length = Math.min(data.length - i, 64);
			if (!write64(address, increment, data, i, length))
				return false;
			if (increment)
				address += length;
		}
		return true;
	}

	private boolean write64(int address, boolean increment, int[] data, int start, int length) {
		byte[] buff = new byte[5 + length * 4];
		buff[0] = (byte) ((1 << 7) | (length - 1));
		if (increment)
			buff[0] |= (1 << 6);
		buff[1] = (byte) (address & 0xff);
		buff[2] = (byte) ((address >> 8) & 0xff);
		buff[3] = (byte) ((address >> 16) & 0xff);
		buff[4] = (byte) ((address >> 24) & 0xff);
		for (int i = 0; i < length; i++) {
			buff[i * 4 + 5] = (byte) (data[i + start] & 0xff);
			buff[i * 4 + 6] = (byte) ((data[i + start] >> 8) & 0xff);
			buff[i * 4 + 7] = (byte) ((data[i + start] >> 16) & 0xff);
			buff[i * 4 + 8] = (byte) ((data[i + start] >> 24) & 0xff);
		}

		return serialPort.writeData(buff) == buff.length;
	}

	public int read(int address) {
		byte[] buff = new byte[5];
		buff[0] = (byte) (0 << 7);
		buff[1] = (byte) (address & 0xff);
		buff[2] = (byte) ((address >> 8) & 0xff);
		buff[3] = (byte) ((address >> 16) & 0xff);
		buff[4] = (byte) ((address >> 24) & 0xff);
		if (serialPort.writeData(buff) != buff.length)
			throw new LibUsbException("readReg " + " failed to write address", -1);
		buff = new byte[4];
		serialPort.readDataWithTimeout(buff);
		return (buff[0] & 0xff) | (buff[1] & 0xff) << 8 | (buff[2] & 0xff) << 16 | (buff[3] & 0xff) << 24;
	}

	public void read(int address, boolean increment, int[] data) {
		for (int i = 0; i < data.length; i += 64) {
			int length = Math.min(data.length - i, 64);
			read64(address, increment, data, i, length);
			if (increment)
				address += length;
		}
	}

	private void read64(int address, boolean increment, int[] data, int start, int length) {
		byte[] buff = new byte[5];
		buff[0] = (byte) ((0 << 7) | (length - 1));
		if (increment)
			buff[0] |= (1 << 6);
		buff[1] = (byte) (address & 0xff);
		buff[2] = (byte) ((address >> 8) & 0xff);
		buff[3] = (byte) ((address >> 16) & 0xff);
		buff[4] = (byte) ((address >> 24) & 0xff);

		if (serialPort.writeData(buff) != buff.length)
			throw new LibUsbException("readReg " + " failed to write address", -1);

		buff = new byte[length * 4];
		serialPort.readDataWithTimeout(buff);

		for (int i = 0; i < buff.length; i += 4) {
			data[i / 4 + start] = (buff[i] & 0xff) | (buff[i + 1] & 0xff) << 8 | (buff[i + 2] & 0xff) << 16 | (buff[i + 3] & 0xff) << 24;
		}
	}
}
