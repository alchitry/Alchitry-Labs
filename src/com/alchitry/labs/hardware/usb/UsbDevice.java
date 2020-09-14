package com.alchitry.labs.hardware.usb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.usb4java.BufferUtils;
import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.Interface;
import org.usb4java.InterfaceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.usb.UsbUtil.DeviceEntry;
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor;
import com.alchitry.labs.hardware.usb.ftdi.enums.DetachMode;

public class UsbDevice {
	public class TransferControl {
		public IntBuffer completed = BufferUtils.allocateIntBuffer();
		public byte[] buf;
		public int offset;
		public UsbDevice usbDev;
		public Transfer transfer;
	}

	protected static Context context;
	protected DeviceHandle device;
	protected int readTimeout;
	protected int writeTimeout;
	protected ByteBuffer readBuffer;
	protected int readBufferChunksize;
	protected int writeBufferChunksize;
	protected int maxPacketSize;

	protected int iface;
	protected byte inEndPoint;
	protected byte outEndPoint;

	protected DetachMode detachMode;

	public static final ByteBuffer EMPTY_BUF = ByteBuffer.allocateDirect(0);

	public class SizeAndTime {
		public long totalBytes;
		public long time;
	}

	public class ProgressInfo {
		public SizeAndTime first;
		public SizeAndTime prev;
		public SizeAndTime current;
		public double totalTime;
		public double totalRate;
		public double currentRate;
	}

	public class DeviceStrings {
		public String manufacture;
		public String product;
		public String serial;
	}

	public interface StreamCallback {
		void update(byte[] buffer, ProgressInfo progress, byte[] userData);
	}

	protected static void UsbCloseInternal(DeviceHandle dev) {
		if (dev != null) {
			LibUsb.close(dev);
			dev = null;
		}
	}

	protected void UsbCloseInternal() {
		UsbCloseInternal(device);
	}

	static {
		if (LibUsb.init(context) < 0)
			throw new LibUsbException("LibUsb.init() failed", -3);
	}

	protected void init() {
		device = null;
		readTimeout = 5000;
		writeTimeout = 5000;
		readBuffer = null;
		writeBufferChunksize = 16384;
		maxPacketSize = 0;
		detachMode = DetachMode.AUTO_DETACH_REATACH_SIO_MODULE;

		iface = 0;
		readDataSetChunkSize(16384);
	}

	public UsbDevice() {
		init();
	}

	public void setDetachMode(DetachMode mode) {
		if (device != null) {
			if (!mode.equals(detachMode))
				throw new LibUsbException("Detach mode can not be changed on an already open device", -3);
		}
		detachMode = mode;
	}

	public void deinit() {
		UsbCloseInternal();
		if (context != null) {
			LibUsb.exit(context);
			context = null;
		}
	}

	public void setUsbDev(DeviceHandle dev) {
		device = dev;
	}

	public static List<Device> usbFindAll(short vendor, short product, String description, String serial) {
		DeviceHandle device;
		// Read the USB device list
		DeviceList list = new DeviceList();
		int result = LibUsb.getDeviceList(null, list);
		if (result < 0)
			throw new LibUsbException("Unable to get device list", result);
		ArrayList<Device> devices = new ArrayList<>();

		try {
			// Iterate over all devices and scan for the right one
			for (Device dev : list) {
				DeviceDescriptor desc = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(dev, desc);
				if (result != LibUsb.SUCCESS)
					throw new LibUsbException("Unable to read device descriptor", result);
				if (((vendor != 0 || product != 0) && desc.idVendor() == vendor && desc.idProduct() == product)
						|| ((vendor == 0 && product == 0) && (desc.idVendor() == (short) 0x403) && (desc.idProduct() == (short) 0x6001 || desc.idProduct() == (short) 0x6010
								|| desc.idProduct() == (short) 0x6011 || desc.idProduct() == (short) 0x6014 || desc.idProduct() == (short) 0x6015))) {
					device = new DeviceHandle();
					if (LibUsb.open(dev, device) < 0)
						throw new LibUsbException("LibUsb.open() failed", -4);

					if (description != null) {
						String sDesc = LibUsb.getStringDescriptor(device, desc.iProduct());
						if (sDesc == null) {
							UsbCloseInternal(device);
							throw new LibUsbException("unalbe to fetch product description", -8);
						}
						if (!description.equals(sDesc)) {
							UsbCloseInternal(device);
							continue;
						}
					}
					if (serial != null) {
						String sSer = LibUsb.getStringDescriptor(device, desc.iSerialNumber());
						if (sSer == null) {
							UsbCloseInternal(device);
							throw new LibUsbException("unalbe to fetch serial number", -9);
						}
						if (!serial.equals(sSer)) {
							UsbCloseInternal(device);
							continue;
						}
					}
					UsbCloseInternal(device);

					devices.add(dev);
					LibUsb.refDevice(dev);
				}
			}
		} finally {
			// Ensure the allocated device list is freed
			LibUsb.freeDeviceList(list, true);
		}

		// Device not found
		return devices;
	}

	public static List<DeviceEntry> usbFindAll(List<UsbDescriptor> descriptions) {
		DeviceHandle device;
		// Read the USB device list
		DeviceList list = new DeviceList();
		int result = LibUsb.getDeviceList(null, list);
		if (result < 0)
			throw new LibUsbException("Unable to get device list", result);
		ArrayList<DeviceEntry> devices = new ArrayList<>();

		try {
			// Iterate over all devices and scan for the right one
			for (Device dev : list) {
				DeviceDescriptor desc = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(dev, desc);
				if (result != LibUsb.SUCCESS)
					throw new LibUsbException("Unable to read device descriptor", result);
				boolean vidPidMatch = false;
				for (UsbDescriptor udes : descriptions) {
					if (udes.vid == desc.idVendor() && udes.pid == desc.idProduct()) {
						vidPidMatch = true;
						break;
					}
				}
				if (vidPidMatch) {
					device = new DeviceHandle();
					int code = LibUsb.open(dev, device);
					if (code < 0) {
						continue;
					}

					String sDesc = LibUsb.getStringDescriptor(device, desc.iProduct());
					if (sDesc == null) {
						UsbCloseInternal(device);
						throw new LibUsbException("unable to fetch product description", -8);
					}
					UsbDescriptor match = null;
					for (UsbDescriptor udes : descriptions) {
						if (udes.vid == desc.idVendor() && udes.pid == desc.idProduct() && (udes.product == null || udes.product.equals(sDesc))) {
							match = udes;
							break;
						}
					}

					UsbCloseInternal(device);

					if (match == null)
						continue;

					devices.add(new DeviceEntry(match, dev));
					LibUsb.refDevice(dev);
				}
			}
		} finally {
			// Ensure the allocated device list is freed
			LibUsb.freeDeviceList(list, true);
		}

		// Device not found
		return devices;
	}

	public static void listFree(List<Device> devList) {
		for (Device dev : devList)
			LibUsb.unrefDevice(dev);
	}

	public static void entryListFree(List<DeviceEntry> devList) {
		for (DeviceEntry dev : devList)
			LibUsb.unrefDevice(dev.device);
	}

	public DeviceStrings usbGetStrings(Device dev) {
		boolean needOpen = device == null;

		if (needOpen) {
			device = new DeviceHandle();
			if (LibUsb.open(dev, device) < 0)
				throw new LibUsbException("LibUsb.open() failed", -4);
		}

		DeviceDescriptor desc = new DeviceDescriptor();
		if (LibUsb.getDeviceDescriptor(dev, desc) < 0)
			throw new LibUsbException("LibUsb.getDeviceDescriptor() failed", -11);

		DeviceStrings strings = new DeviceStrings();
		strings.product = LibUsb.getStringDescriptor(device, desc.iProduct());
		strings.manufacture = LibUsb.getStringDescriptor(device, desc.iManufacturer());
		strings.serial = LibUsb.getStringDescriptor(device, desc.iSerialNumber());

		if (needOpen)
			UsbCloseInternal();

		return strings;
	}

	protected int determinMaxPacketSize(Device dev, int defSize) {
		int packetSize = defSize;
		DeviceDescriptor desc = new DeviceDescriptor();
		ConfigDescriptor config = new ConfigDescriptor();

		if (dev == null)
			return 64;

		if (LibUsb.getDeviceDescriptor(dev, desc) < 0)
			return packetSize;
		if (LibUsb.getConfigDescriptor(dev, (byte) 0, config) < 0)
			return packetSize;

		if (desc.bNumConfigurations() > 0) {
			if (iface < desc.bNumConfigurations()) {
				Interface iface = config.iface()[0];
				if (iface.numAltsetting() > 0) {
					InterfaceDescriptor descriptor = iface.altsetting()[0];
					if (descriptor.bNumEndpoints() > 0) {
						packetSize = descriptor.endpoint()[0].wMaxPacketSize();
					}
				}
			}
		}

		LibUsb.freeConfigDescriptor(config);

		return packetSize;
	}

	public void setTimeouts(int read, int write) {
		readTimeout = read;
		writeTimeout = write;
	}

	public void usbOpenDev(Device dev) {
		DeviceDescriptor desc = new DeviceDescriptor();
		ConfigDescriptor config = new ConfigDescriptor();
		int cfg, cfg0, detachErrno = 0, errno;
		IntBuffer cfgBuf = IntBuffer.allocate(1);

		device = new DeviceHandle();
		if (LibUsb.open(dev, device) < 0)
			throw new LibUsbException("LibUsb.open() failed", -8);

		if (LibUsb.getDeviceDescriptor(dev, desc) < 0)
			throw new LibUsbException("LibUsb.getDeviceDescriptior() failed", -9);

		if (LibUsb.getConfigDescriptor(dev, (byte) 0, config) < 0)
			throw new LibUsbException("LibUsb.getConfigDescriptor() failed", -10);
		cfg0 = config.bConfigurationValue();
		LibUsb.freeConfigDescriptor(config);

		if (DetachMode.AUTO_DETACH_SIO_MODULE.equals(detachMode)) {
			if ((errno = LibUsb.detachKernelDriver(device, iface)) != LibUsb.SUCCESS)
				detachErrno = errno;
		} else if (DetachMode.AUTO_DETACH_REATACH_SIO_MODULE.equals(detachMode)) {
			if ((errno = LibUsb.setAutoDetachKernelDriver(device, true)) != LibUsb.SUCCESS)
				detachErrno = errno;
			if (detachErrno == LibUsb.ERROR_NOT_SUPPORTED)
				if ((errno = LibUsb.detachKernelDriver(device, iface)) != LibUsb.SUCCESS)
					detachErrno = errno;
		}

		if (LibUsb.getConfiguration(device, cfgBuf) < 0)
			throw new LibUsbException("LibUsb.getConfiguration() failed", -12);
		cfg = cfgBuf.get(0);

		if (desc.bNumConfigurations() > 0 && cfg != cfg0) {
			if (LibUsb.setConfiguration(device, cfg0) < 0) {
				UsbCloseInternal();
				if (detachErrno == LibUsb.ERROR_ACCESS)
					throw new LibUsbException("inappropriate permissions on device!", -8);
				else
					throw new LibUsbException("unable to set usb configuration. Make sure the default Ftdi driver is not in use", -3);
			}
		}

		if (LibUsb.claimInterface(device, iface) < 0) {
			UsbCloseInternal();
			if (detachErrno == LibUsb.ERROR_ACCESS)
				throw new LibUsbException("inappropriate permissions on device!", -8);
			else
				throw new LibUsbException("unable to claim usb device. Make sure the default Ftdi driver is not in use", -5);
		}

		maxPacketSize = determinMaxPacketSize(dev, 512);
	}

	public boolean usbOpen(short vendor, short product) {
		return usbOpen(vendor, product, null, null);
	}

	public boolean usbOpen(short vendor, short product, String description, String serial) {
		return usbOpen(vendor, product, description, serial, 0);
	}

	public boolean usbOpen(short vendor, short product, String description, String serial, int index) {
		DeviceList devs = new DeviceList();

		if (LibUsb.getDeviceList(context, devs) < 0)
			throw new LibUsbException("LibUsb.getDeviceList() failed", -12);

		try {
			for (Device dev : devs) {
				DeviceDescriptor desc = new DeviceDescriptor();

				if (LibUsb.getDeviceDescriptor(dev, desc) < 0)
					throw new LibUsbException("LibUsb.getDeviceDescriptor() failed", -13);

				if (desc.idVendor() == vendor && desc.idProduct() == product) {
					device = new DeviceHandle();
					if (LibUsb.open(dev, device) < 0)
						throw new LibUsbException("LibUsb.open() failed", -4);

					if (description != null) {
						String sDesc = LibUsb.getStringDescriptor(device, desc.iProduct());
						if (sDesc == null) {
							UsbCloseInternal();
							throw new LibUsbException("unalbe to fetch product description", -8);
						}
						if (!description.equals(sDesc)) {
							UsbCloseInternal();
							continue;
						}
					}
					if (serial != null) {
						String sSer = LibUsb.getStringDescriptor(device, desc.iSerialNumber());
						if (sSer == null) {
							UsbCloseInternal();
							throw new LibUsbException("unalbe to fetch serial number", -9);
						}
						if (!serial.equals(sSer)) {
							UsbCloseInternal();
							continue;
						}
					}
					UsbCloseInternal();

					if (index > 0) {
						index--;
						continue;
					}

					usbOpenDev(dev);
					return true;
				}
			}
		} finally {
			LibUsb.freeDeviceList(devs, true);
		}
		return false;
	}

	public boolean usbOpenBusAddr(int bus, int addr) {
		DeviceList devs = new DeviceList();

		if (LibUsb.getDeviceList(context, devs) < 0)
			throw new LibUsbException("LibUsb.getDeviceList() failed", -12);

		try {
			for (Device dev : devs) {
				if (LibUsb.getBusNumber(dev) == bus && LibUsb.getDeviceAddress(dev) == addr) {
					usbOpenDev(dev);
					return true;
				}
			}
		} finally {
			LibUsb.freeDeviceList(devs, true);
		}

		return false;
	}

	public boolean usbOpen(String description) {
		if (description == null || description.isEmpty() || description.charAt(1) != ':')
			throw new LibUsbException("illegal description format", -12);

		if (description.charAt(0) == 'd') {
			DeviceList devs = new DeviceList();

			Pattern p = Pattern.compile(".:(\\d+)\\/(\\d+)");
			Matcher m = p.matcher(description);

			if (!m.matches() || m.groupCount() != 2)
				throw new LibUsbException("illegal description format", -11);

			int busNumber, deviceAddress;

			try {
				busNumber = Integer.parseInt(m.group(1));
				deviceAddress = Integer.parseInt(m.group(2));
			} catch (NumberFormatException e) {
				throw new LibUsbException("illegal description format", -11);
			}

			if (LibUsb.getDeviceList(context, devs) < 0)
				throw new LibUsbException("LibUsb.getDeviceList() failed", -12);

			try {
				for (Device dev : devs) {
					if (busNumber == LibUsb.getBusNumber(dev) && deviceAddress == LibUsb.getDeviceAddress(dev)) {
						usbOpenDev(dev);
						return true;
					}
				}

			} finally {
				LibUsb.freeDeviceList(devs, true);
			}
		} else if (description.charAt(0) == 'i' || description.charAt(0) == 's') {
			Pattern p = Pattern.compile(".:([\\dx]+):([\\dx]+)(:(.+))?");
			Matcher m = p.matcher(description);
			if (!m.matches())
				throw new LibUsbException("illegal description format", -11);

			int vendor, product, index = 0;
			String serial = null;

			try {
				vendor = Integer.decode(m.group(1));
				product = Integer.decode(m.group(2));
			} catch (NumberFormatException e) {
				throw new LibUsbException("illegal description format", -11);
			}

			if (description.charAt(0) == 'i' && m.groupCount() == 4) {
				try {
					index = Integer.decode(m.group(4));
				} catch (NumberFormatException e) {
					throw new LibUsbException("illegal description format", -11);
				}
			}

			if (description.charAt(0) == 's') {
				if (m.groupCount() != 4)
					throw new LibUsbException("illegal description format", -11);

				serial = m.group(4);
			}

			return usbOpen((short) vendor, (short) product, null, serial, index);
		}

		throw new LibUsbException("illegal description format", -11);
	}

	public boolean usbClose() {
		boolean rtn = true;
		if (device != null)
			if (LibUsb.releaseInterface(device, iface) < 0)
				rtn = false;

		UsbCloseInternal();
		return rtn;
	}

	public int writeData(byte[] data) {
		int offset = 0;
		ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
		buf.put(data);
		IntBuffer transferred = IntBuffer.allocate(1);

		if (device == null)
			throw new LibUsbException("USB device unavailable", -666);

		while (offset < data.length) {
			int writeSize = writeBufferChunksize;

			if (offset + writeSize > data.length)
				writeSize = data.length - offset;

			buf.position(offset);
			int code;
			if ((code = LibUsb.bulkTransfer(device, inEndPoint, buf, transferred, writeTimeout)) < 0)
				throw new LibUsbException("usb bulk write failed", code);

			offset += transferred.get();
		}

		return offset;
	}

	public int transferDataDone(TransferControl tc) {
		int ret;
		while (tc.completed.get(0) == 0) {
			ret = LibUsb.handleEventsTimeoutCompleted(context, 0, tc.completed);
			if (ret < 0) {
				if (ret == LibUsb.ERROR_INTERRUPTED)
					continue;
				LibUsb.cancelTransfer(tc.transfer);
				while (tc.completed.get(0) == 0)
					if (LibUsb.handleEventsTimeoutCompleted(context, 0, tc.completed) < 0)
						break;
				LibUsb.freeTransfer(tc.transfer);
				return ret;
			}
		}

		ret = tc.offset;

		if (tc.transfer != null) {
			if (tc.transfer.status() != LibUsb.TRANSFER_COMPLETED)
				ret = -1;
			LibUsb.freeTransfer(tc.transfer);
		}
		return ret;
	}

	public void transferDataCancel(TransferControl tc, long timeout) {
		if (tc.completed.get(0) == 0 && tc.transfer != null) {
			LibUsb.cancelTransfer(tc.transfer);
			while (tc.completed.get(0) == 0) {
				if (LibUsb.handleEventsTimeoutCompleted(context, timeout, tc.completed) < 0)
					break;
			}
		}
		if (tc.transfer != null)
			LibUsb.freeTransfer(tc.transfer);
	}

	public void writeDataSetChunksize(int chunksize) {
		writeBufferChunksize = chunksize;
	}

	public int writeDataGetChunksize() {
		return writeBufferChunksize;
	}

	public int readDataWithTimeout(byte[] data) {
		byte[] buffer = new byte[data.length];
		int reqBytes = data.length;
		int offset = 0;
		long startTime = System.currentTimeMillis();
		while (reqBytes > 0) {
			if (buffer.length != reqBytes)
				buffer = new byte[reqBytes];
			int ct = readData(buffer);
			if (ct > 0) {
				System.arraycopy(buffer, 0, data, offset, ct);
				offset += ct;
				reqBytes -= ct;
			}
			if (System.currentTimeMillis() - startTime > readTimeout) {
				return data.length - reqBytes;
			}
		}
		return data.length;
	}

	public int readData(byte[] buf) {
		int offset = 0, ret;
		int packet_size;
		IntBuffer actual_length_buf = IntBuffer.allocate(1);
		int actual_length = 1;

		if (device == null)
			throw new LibUsbException("USB device unavailable", -666);

		// Packet size sanity check (avoid division by zero)
		packet_size = maxPacketSize;
		if (packet_size == 0)
			throw new LibUsbException("max_packet_size is bogus (zero)", -1);

		// everything we want is still in the readbuffer?
		if (buf.length <= readBuffer.remaining()) {
			readBuffer.get(buf);
			return buf.length;
		}
		// something still in the readbuffer, but not enough to satisfy 'size'?
		if (readBuffer.remaining() != 0) {
			offset += readBuffer.remaining();
			readBuffer.get(buf, 0, readBuffer.remaining());
		}
		// do the actual USB read
		while (offset < buf.length && actual_length > 0) {
			readBuffer.clear();
			/* returns how much received */
			actual_length_buf.clear();
			ret = LibUsb.bulkTransfer(device, outEndPoint, readBuffer, actual_length_buf, readTimeout);
			actual_length = actual_length_buf.get();
			readBuffer.limit(actual_length);
			if (ret < 0)
				throw new LibUsbException("usb bulk read failed", ret);

			if (actual_length <= 0) {
				// no more data to read?
				resetReadBuffer();

				return offset;
			}
			if (readBuffer.remaining() > 0) {
				// data still fits in buf?
				if (offset + actual_length <= buf.length) {
					readBuffer.get(buf, offset, actual_length);
					offset += actual_length;

					/* Did we read exactly the right amount of bytes? */
					if (offset == buf.length)
						return offset;
				} else {
					// only copy part of the data or size <= readbuffer_chunksize
					int part_size = buf.length - offset;
					readBuffer.get(buf, offset, part_size);
					offset += part_size;
					return offset;
				}
			}
		}
		// never reached
		return -127;
	}

	public void flushReadBuffer() {
		resetReadBuffer();
		byte[] dummyBuffer = new byte[maxPacketSize];
		int oldTimeout = readTimeout;
		readTimeout = 100;
		try {
			readData(dummyBuffer); // flush the buffer
		} catch (LibUsbException e) {

		}
		readTimeout = oldTimeout;
	}

	public void resetReadBuffer() {
		readBuffer.clear();
		readBuffer.limit(0);
	}

	public void readDataSetChunkSize(int chunksize) {
		/*
		 * We can't set readbuffer_chunksize larger than MAX_BULK_BUFFER_LENGTH, which is defined in libusb-1.0. Otherwise, each USB read request will be divided into multiple
		 * URBs. This will cause issues on Linux kernel older than 2.6.32.
		 */
		if (Util.isLinux && chunksize > 16384)
			chunksize = 16384;
		readBuffer = ByteBuffer.allocateDirect(chunksize);
		readBuffer.limit(0);
		readBufferChunksize = chunksize;
	}

	public int readDataGetChunkSize() {
		return readBufferChunksize;
	}
}
