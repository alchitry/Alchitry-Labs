package com.alchitry.labs.hardware.usb;

public abstract class UsbSerial extends UsbDevice {
	abstract public void setBaudrate(int baud);
	abstract public void setDtrRts(boolean dtr, boolean rts);
}
