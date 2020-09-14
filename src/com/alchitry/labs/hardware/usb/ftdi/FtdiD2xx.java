package com.alchitry.labs.hardware.usb.ftdi;

import com.alchitry.labs.hardware.usb.SerialDevice;
import com.alchitry.labs.hardware.usb.ftdi.enums.BitMode;

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIConstants;
import net.sf.yad2xx.FTDIException;

public class FtdiD2xx implements Ftdi, SerialDevice {
	private Device device;
	protected int readTimeout;
	protected int writeTimeout;

	public FtdiD2xx(Device device) {
		this.device = device;
	}

	@Override
	public void setTimeouts(int read, int write) {
		readTimeout = read;
		writeTimeout = write;
		try {
			device.setTimeouts(read, write);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void usbReset() {
		try {
			device.reset();
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setChars(byte eventChar, boolean eventEnable, byte errorChar, boolean errorEnable) {
		try {
			device.setChars((char) eventChar, eventEnable, (char) errorChar, errorEnable);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setLatencyTimer(byte b) {
		try {
			device.setLatencyTimer(b);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBitmode(byte pinDirection, BitMode mode) {
		try {
			device.setBitMode(pinDirection, mode.getFTDIMode());
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public int writeData(byte[] bs) {
		try {
			return device.write(bs);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int readData(byte[] data) {
		try {
			int bytesToRead = Math.min(device.getQueueStatus(), data.length);
			if (bytesToRead == 0)
				return 0;
			byte[] buff = new byte[bytesToRead];
			int bytesRead = device.read(buff);
			System.arraycopy(buff, 0, data, 0, bytesRead);
			return bytesRead;
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int readDataWithTimeout(byte[] inputBuffer) {
		try {
			return device.read(inputBuffer);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean usbClose() {
		try {
			device.close();
		} catch (FTDIException e) {
			return false;
		}
		return true;
	}

	@Override
	public void usbPurgeBuffers() {
		try {
			device.purge(FTDIConstants.FT_PURGE_RX | FTDIConstants.FT_PURGE_TX);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean close() {
		return usbClose();
	}

	@Override
	public int setBaudrate(int baud) {
		try {
			device.setBaudRate(baud);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
		return baud;
	}

	@Override
	public void setDtrRts(boolean dtr, boolean rts) {
		try {
			device.setDtr(dtr);
			device.setRts(rts);
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void flushReadBuffer() {
		try {
			device.purge(FTDIConstants.FT_PURGE_RX);
			while (device.getQueueStatus() > 0) {
				device.read(new byte[device.getQueueStatus()]);
			}
		} catch (FTDIException e) {
			throw new RuntimeException(e);
		}
	}
}
