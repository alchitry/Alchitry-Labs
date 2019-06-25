package com.alchitry.labs.hardware.serial;

import java.util.List;

public interface SerialInterface {
	public void connect(String device, int baud);
	public void close();
	public List<String> listDevices();
	public default int write(byte[] data) {
		return write(data, data.length);
	}
	public int write(byte[] data, int length);
	public int read(byte[] buffer, int count);
	public byte[] read();
	public void flush();
}
