package com.alchitry.labs.hardware.usb;

import com.fazecast.jSerialComm.SerialPort;

public class GenericSerial implements SerialDevice {
	private SerialPort device;
	private int readTimeout = 2000;
	private int writeTimeout = 2000;

	public GenericSerial(SerialPort dev) {
		device = dev;
	}

	public boolean open() {
		if (!device.openPort())
			return false;
		device.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, readTimeout, writeTimeout);
		return true;
	}

	@Override
	public boolean close() {
		if (device == null)
			return false;
		return device.closePort();
	}

	@Override
	public int setBaudrate(int baud) {
		device.setBaudRate(baud);
		return device.getBaudRate();
	}

	@Override
	public void setDtrRts(boolean dtr, boolean rts) {
		if (dtr)
			device.setDTR();
		else
			device.clearDTR();
		if (rts)
			device.setRTS();
		else
			device.clearRTS();
	}

	@Override
	public void setTimeouts(int read, int write) {
		readTimeout = read;
		writeTimeout = write;
		device.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, read, write);
	}

	@Override
	public int writeData(byte[] data) {
		return device.writeBytes(data, data.length);
	}

	@Override
	public int readDataWithTimeout(byte[] data) {
		return device.readBytes(data, data.length);
	}

	@Override
	public int readData(byte[] buf) {
		device.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, readTimeout, writeTimeout);
		int count = device.readBytes(buf, buf.length);
		device.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, readTimeout, writeTimeout);
		return count;
	}

	@Override
	public void flushReadBuffer() {
		device.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, readTimeout, writeTimeout);
		byte[] buff = new byte[100];
		while (device.readBytes(buff, 100) > 0)
			;
		device.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, readTimeout, writeTimeout);
	}

}
