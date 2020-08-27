package com.alchitry.labs.hardware.usb;

public interface SerialDevice {
	abstract public boolean close();
	abstract public int setBaudrate(int baud);
	abstract public void setDtrRts(boolean dtr, boolean rts);
	abstract public void setTimeouts(int read, int write);
	abstract public int writeData(byte[] data);
	abstract public int readDataWithTimeout(byte[] data);
	abstract public int readData(byte[] buf);
	abstract public void flushReadBuffer();
}
