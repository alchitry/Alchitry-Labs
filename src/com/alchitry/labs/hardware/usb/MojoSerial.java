package com.alchitry.labs.hardware.usb;

import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import com.alchitry.labs.hardware.usb.ftdi.enums.DetachMode;

public class MojoSerial extends UsbSerial {

	static final byte ACM_CTRL_DTR = 0x01;
	static final byte ACM_CTRL_RTS = 0x02;

	static final byte DEVICE_OUT_REQUEST = (byte) (LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE | LibUsb.ENDPOINT_OUT);

	static final byte CDC_SET_LINE_CODING = 0x20;
	static final byte CDC_GET_LINE_CODING = 0x21;
	static final byte CDC_SET_CONTROL_LINE_STATE = 0x22;
	static final byte CDC_SEND_BREAK = 0x23;

	public MojoSerial() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		iface = 1;

		outEndPoint = (byte) 0x83;
		inEndPoint = (byte) 0x02;

		maxPacketSize = 64;
	}

	@Override
	public void usbOpenDev(Device dev) {
		DeviceDescriptor desc = new DeviceDescriptor();
		int detachErrno = 0, errno;

		device = new DeviceHandle();
		if (LibUsb.open(dev, device) < 0)
			throw new LibUsbException("LibUsb.open() failed", -8);

		if (LibUsb.getDeviceDescriptor(dev, desc) < 0)
			throw new LibUsbException("LibUsb.getDeviceDescriptior() failed", -9);

		for (int iface = 0; iface < 2; iface++) {
			if (DetachMode.AUTO_DETACH_SIO_MODULE.equals(detachMode)) {
				if ((errno = LibUsb.detachKernelDriver(device, iface)) != LibUsb.SUCCESS)
					detachErrno = errno;
			} else if (DetachMode.AUTO_DETACH_REATACH_SIO_MODULE.equals(detachMode)) {
				if ((errno = LibUsb.setAutoDetachKernelDriver(device, true)) != LibUsb.SUCCESS)
					detachErrno = errno;
			}

			if (detachErrno < 0 && detachErrno != LibUsb.ERROR_NOT_SUPPORTED)
				throw new LibUsbException("Failed to set detach mode.", detachErrno);

			if ((errno = LibUsb.claimInterface(device, iface)) < 0) {
				UsbCloseInternal();
				throw new LibUsbException("unable to claim usb interface " + iface + ". Make sure the default driver is not in use", errno);
			}
		}

		setDtrRts(true, true);
		setBaudrate(1000000);
	}

	@Override
	public boolean usbClose() {
		boolean rtn = true;
		if (device != null)
			for (int iface = 0; iface < 2; iface++)
				if (LibUsb.releaseInterface(device, iface) < 0)
					rtn = false;

		UsbCloseInternal();
		return rtn;
	}

	@Override
	public void flushReadBuffer() {
		resetReadBuffer();
		byte[] dummyBuffer = new byte[64];
		int oldTimeout = readTimeout;
		readTimeout = 100;
		try {
			readData(dummyBuffer); // flush the buffer
		} catch (LibUsbException e) {

		}
		readTimeout = oldTimeout;
	}

	@Override
	public int setBaudrate(int baud) {
		// The Mojo ignores baud rate requests
		// ByteBuffer bbuf = ByteBuffer.allocateDirect(7);
		// bbuf.put(new byte[] { (byte) (baud & 0xff), (byte) ((baud >>> 8) & 0xff), (byte) ((baud >>> 16) & 0xff), (byte) ((baud >>> 24) & 0xff), 0x00, 0x00, 0x08 });
		// LibUsb.controlTransfer(device, DEVICE_OUT_REQUEST, CDC_SET_LINE_CODING, (short) 0, (short) 0, bbuf, writeTimeout);
		return 1000000; // always sets 1000000 baud
	}

	@Override
	public void setDtrRts(boolean dtr, boolean rts) {
		short value = (short) ((dtr ? ACM_CTRL_DTR : 0x00) | (rts ? ACM_CTRL_RTS : 0x00));

		LibUsb.controlTransfer(device, DEVICE_OUT_REQUEST, CDC_SET_CONTROL_LINE_STATE, value, (short) 0, EMPTY_BUF, writeTimeout);
	}
}
