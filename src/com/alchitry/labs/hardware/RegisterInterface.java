package com.alchitry.labs.hardware;

import com.alchitry.labs.Settings;
import com.alchitry.labs.Util;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortIOException;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

public class RegisterInterface {
	private SerialPort serialPort;

	public RegisterInterface() {

	}

	public boolean isConnected() {
		if (serialPort == null || !serialPort.isOpen())
			return false;
		return true;
	}

	public boolean connect(String port) {
		if (port == null)
			port = Settings.pref.get(Settings.SERIAL_PORT, null);
		if (port == null) {
			Util.showError("You need to select the serial port the Mojo is connected to in the settings menu.");
			return false;
		}
		try {
			serialPort = Util.connect(port, 1000000);
			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 3000, 3000);
		} catch (SerialPortIOException e) {
			return false;
		}
		return true;
	}

	public boolean disconnect() {
		if (serialPort == null)
			return true;
		return serialPort.closePort();
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
		return serialPort.writeBytes(buff, buff.length) == buff.length;
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

		return serialPort.writeBytes(buff, buff.length) == buff.length;
	}

	public int read(int address) throws SerialPortIOException, SerialPortTimeoutException {
		byte[] buff = new byte[5];
		buff[0] = (byte) (0 << 7);
		buff[1] = (byte) (address & 0xff);
		buff[2] = (byte) ((address >> 8) & 0xff);
		buff[3] = (byte) ((address >> 16) & 0xff);
		buff[4] = (byte) ((address >> 24) & 0xff);
		if (serialPort.writeBytes(buff, buff.length) != buff.length)
			throw new SerialPortIOException(serialPort.getSystemPortName() + " readReg " + " failed to write address");
		if (serialPort.readBytes(buff, 4) != 4)
			throw new SerialPortTimeoutException("Timed out while reading register");
		return (buff[0] & 0xff) | (buff[1] & 0xff) << 8 | (buff[2] & 0xff) << 16 | (buff[3] & 0xff) << 24;
	}

	public void read(int address, boolean increment, int[] data) throws SerialPortIOException, SerialPortTimeoutException {
		for (int i = 0; i < data.length; i += 64) {
			int length = Math.min(data.length - i, 64);
			read64(address, increment, data, i, length);
			if (increment)
				address += length;
		}
	}

	private void read64(int address, boolean increment, int[] data, int start, int length) throws SerialPortIOException, SerialPortTimeoutException {
		byte[] buff = new byte[5];
		buff[0] = (byte) ((0 << 7) | (length - 1));
		if (increment)
			buff[0] |= (1 << 6);
		buff[1] = (byte) (address & 0xff);
		buff[2] = (byte) ((address >> 8) & 0xff);
		buff[3] = (byte) ((address >> 16) & 0xff);
		buff[4] = (byte) ((address >> 24) & 0xff);

		if (serialPort.writeBytes(buff, buff.length) != buff.length)
			throw new SerialPortIOException(serialPort.getSystemPortName() + " readReg " + " failed to write address");

		serialPort.readBytes(buff, length * 4);

		for (int i = 0; i < buff.length; i += 4) {
			data[i / 4 + start] = (buff[i] & 0xff) | (buff[i + 1] & 0xff) << 8 | (buff[i + 2] & 0xff) << 16 | (buff[i + 3] & 0xff) << 24;
		}
	}
}
