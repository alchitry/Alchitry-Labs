package com.alchitry.labs.hardware.usb.ftdi;

import com.alchitry.labs.hardware.usb.ftdi.enums.BitMode;

public interface Ftdi {

	public void setTimeouts(int read, int write);
	
	public void usbReset();

	public void setChars(byte eventChar, boolean eventEnable, byte errorChar, boolean errorEnable);

	public void setLatencyTimer(byte time);

	public void setBitmode(byte pinDirection, BitMode reset);

	public int writeData(byte[] data);

	public int readData(byte[] data);

	public int readDataWithTimeout(byte[] inputBuffer);

	public boolean usbClose();

	public void usbPurgeBuffers();


}
