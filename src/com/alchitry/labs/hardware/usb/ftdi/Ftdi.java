package com.alchitry.labs.hardware.usb.ftdi;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import com.alchitry.labs.hardware.usb.UsbSerial;
import com.alchitry.labs.hardware.usb.ftdi.enums.BitMode;
import com.alchitry.labs.hardware.usb.ftdi.enums.ChipType;
import com.alchitry.labs.hardware.usb.ftdi.enums.FlowControl;
import com.alchitry.labs.hardware.usb.ftdi.enums.LineBreak;
import com.alchitry.labs.hardware.usb.ftdi.enums.LineDatabit;
import com.alchitry.labs.hardware.usb.ftdi.enums.LineParity;
import com.alchitry.labs.hardware.usb.ftdi.enums.LineStopbit;
import com.alchitry.labs.hardware.usb.ftdi.enums.PortInterfaceType;

public class Ftdi extends UsbSerial {
	/* Shifting commands IN MPSSE Mode */
	public static final byte MPSSE_WRITE_NEG = (byte) 0x01; /* Write TDI/DO on negative TCK/SK edge */
	public static final byte MPSSE_BITMODE = (byte) 0x02; /* Write bits, not bytes */
	public static final byte MPSSE_READ_NEG = (byte) 0x04; /* Sample TDO/DI on negative TCK/SK edge */
	public static final byte MPSSE_LSB = (byte) 0x08; /* LSB first */
	public static final byte MPSSE_DO_WRITE = (byte) 0x10; /* Write TDI/DO */
	public static final byte MPSSE_DO_READ = (byte) 0x20; /* Read TDO/DI */
	public static final byte MPSSE_WRITE_TMS = (byte) 0x40; /* Write TMS/CS */

	/* Ftdi MPSSE commands */
	public static final byte SET_BITS_LOW = (byte) 0x80;
	/* BYTE DATA */
	/* BYTE Direction */
	public static final byte SET_BITS_HIGH = (byte) 0x82;
	/* BYTE DATA */
	/* BYTE Direction */
	public static final byte GET_BITS_LOW = (byte) 0x81;
	public static final byte GET_BITS_HIGH = (byte) 0x83;
	public static final byte LOOPBACK_START = (byte) 0x84;
	public static final byte LOOPBACK_END = (byte) 0x85;
	public static final byte TCK_DIVISOR = (byte) 0x86;
	/* H Type specific commands */
	public static final byte DIS_DIV_5 = (byte) 0x8a;
	public static final byte EN_DIV_5 = (byte) 0x8b;
	public static final byte EN_3_PHASE = (byte) 0x8c;
	public static final byte DIS_3_PHASE = (byte) 0x8d;
	public static final byte CLK_BITS = (byte) 0x8e;
	public static final byte CLK_BYTES = (byte) 0x8f;
	public static final byte CLK_WAIT_HIGH = (byte) 0x94;
	public static final byte CLK_WAIT_LOW = (byte) 0x95;
	public static final byte EN_ADAPTIVE = (byte) 0x96;
	public static final byte DIS_ADAPTIVE = (byte) 0x97;
	public static final byte CLK_BYTES_OR_HIGH = (byte) 0x9c;
	public static final byte CLK_BYTES_OR_LOW = (byte) 0x9d;
	/* FT232H specific commands */
	public static final byte DRIVE_OPEN_COLLECTOR = (byte) 0x9e;

	/* Value Low */
	/* Value HIGH */ /* rate is 12000000/((1+value)*2) */
	public static short DIV_VALUE(int rate) {
		return (short) ((rate > 6000000) ? 0 : ((6000000 / rate - 1) > 0xffff) ? 0xffff : (6000000 / rate - 1));
	}

	/* Commands in MPSSE and Host Emulation Mode */
	public static final byte SEND_IMMEDIATE = (byte) 0x87;
	public static final byte WAIT_ON_HIGH = (byte) 0x88;
	public static final byte WAIT_ON_LOW = (byte) 0x89;

	/* Commands in Host Emulation Mode */
	public static final byte READ_SHORT = (byte) 0x90;
	/* Address_Low */
	public static final byte READ_EXTENDED = (byte) 0x91;
	/* Address High */
	/* Address Low */
	public static final byte WRITE_SHORT = (byte) 0x92;
	/* Address_Low */
	public static final byte WRITE_EXTENDED = (byte) 0x93;
	/* Address High */
	/* Address Low */

	/* Definitions for flow control */
	public static final byte SIO_RESET = (byte) 0; /* Reset the port */
	public static final byte SIO_MODEM_CTRL = (byte) 1; /* Set the modem control register */
	public static final byte SIO_SET_FLOW_CTRL = (byte) 2; /* Set flow control register */
	public static final byte SIO_SET_BAUD_RATE = (byte) 3; /* Set baud rate */
	public static final byte SIO_SET_DATA = (byte) 4; /* Set the data characteristics of the port */

	public static final byte FTDI_DEVICE_OUT_REQTYPE = (byte) (LibUsb.REQUEST_TYPE_VENDOR | LibUsb.RECIPIENT_DEVICE | LibUsb.ENDPOINT_OUT);
	public static final byte FTDI_DEVICE_IN_REQTYPE = (byte) (LibUsb.REQUEST_TYPE_VENDOR | LibUsb.RECIPIENT_DEVICE | LibUsb.ENDPOINT_IN);

	/* Requests */
	public static final byte SIO_RESET_REQUEST = (byte) SIO_RESET;
	public static final byte SIO_SET_BAUDRATE_REQUEST = (byte) SIO_SET_BAUD_RATE;
	public static final byte SIO_SET_DATA_REQUEST = (byte) SIO_SET_DATA;
	public static final byte SIO_SET_FLOW_CTRL_REQUEST = (byte) SIO_SET_FLOW_CTRL;
	public static final byte SIO_SET_MODEM_CTRL_REQUEST = (byte) SIO_MODEM_CTRL;
	public static final byte SIO_POLL_MODEM_STATUS_REQUEST = (byte) 0x05;
	public static final byte SIO_SET_EVENT_CHAR_REQUEST = (byte) 0x06;
	public static final byte SIO_SET_ERROR_CHAR_REQUEST = (byte) 0x07;
	public static final byte SIO_SET_LATENCY_TIMER_REQUEST = (byte) 0x09;
	public static final byte SIO_GET_LATENCY_TIMER_REQUEST = (byte) 0x0A;
	public static final byte SIO_SET_BITMODE_REQUEST = (byte) 0x0B;
	public static final byte SIO_READ_PINS_REQUEST = (byte) 0x0C;
	public static final byte SIO_READ_EEPROM_REQUEST = (byte) 0x90;
	public static final byte SIO_WRITE_EEPROM_REQUEST = (byte) 0x91;
	public static final byte SIO_ERASE_EEPROM_REQUEST = (byte) 0x92;

	public static final short SIO_RESET_SIO = (short) 0;
	public static final short SIO_RESET_PURGE_RX = (short) 1;
	public static final short SIO_RESET_PURGE_TX = (short) 2;

	public static final short SIO_DISABLE_FLOW_CTRL = (short) 0x0;
	public static final short SIO_RTS_CTS_HS = (short) (0x1 << 8);
	public static final short SIO_DTR_DSR_HS = (short) (0x2 << 8);
	public static final short SIO_XON_XOFF_HS = (short) (0x4 << 8);

	public static final short SIO_SET_DTR_MASK = (short) 0x1;
	public static final short SIO_SET_DTR_HIGH = (short) (1 | (SIO_SET_DTR_MASK << 8));
	public static final short SIO_SET_DTR_LOW = (short) (0 | (SIO_SET_DTR_MASK << 8));
	public static final short SIO_SET_RTS_MASK = (short) 0x2;
	public static final short SIO_SET_RTS_HIGH = (short) (2 | (SIO_SET_RTS_MASK << 8));
	public static final short SIO_SET_RTS_LOW = (short) (0 | (SIO_SET_RTS_MASK << 8));

	private ChipType type;
	private boolean bitbangEnabled;
	private PortInterfaceType interfaceType;
	private FtdiEeprom eeprom;

	/** Invert TXD# */
	public static final int INVERT_TXD = 0x01;
	/** Invert RXD# */
	public static final int INVERT_RXD = 0x02;
	/** Invert RTS# */
	public static final int INVERT_RTS = 0x04;
	/** Invert CTS# */
	public static final int INVERT_CTS = 0x08;
	/** Invert DTR# */
	public static final int INVERT_DTR = 0x10;
	/** Invert DSR# */
	public static final int INVERT_DSR = 0x20;
	/** Invert DCD# */
	public static final int INVERT_DCD = 0x40;
	/** Invert RI# */
	public static final int INVERT_RI = 0x80;

	/** Interface Mode. */
	public static final int CHANNEL_IS_UART = 0x0;
	public static final int CHANNEL_IS_FIFO = 0x1;
	public static final int CHANNEL_IS_OPTO = 0x2;
	public static final int CHANNEL_IS_CPU = 0x4;
	public static final int CHANNEL_IS_FT1284 = 0x8;

	public static final int CHANNEL_IS_RS485 = 0x10;

	public static final int DRIVE_4MA = 0;
	public static final int DRIVE_8MA = 1;
	public static final int DRIVE_12MA = 2;
	public static final int DRIVE_16MA = 3;
	public static final int SLOW_SLEW = 4;
	public static final int IS_SCHMITT = 8;

	/** Driver Type. */
	public static final int DRIVER_VCP = 0x08;
	public static final int DRIVER_VCPH = 0x10; /* FT232H has moved the VCP bit */

	public static final int USE_USB_VERSION_BIT = 0x10;

	public static final int SUSPEND_DBUS7_BIT = 0x80;

	/** High current drive. */
	public static final int HIGH_CURRENT_DRIVE = 0x10;
	public static final int HIGH_CURRENT_DRIVE_R = 0x04;

	public static final ByteBuffer EMPTY_BUF = ByteBuffer.allocateDirect(0);

	@Override
	protected void UsbCloseInternal() {
		super.UsbCloseInternal();
		if (eeprom != null)
			eeprom.initialized_for_connected_device = 0;
	}

	@Override
	protected void init() {
		super.init();
		type = ChipType.TYPE_BM;
		bitbangEnabled = false;

		setInterface(PortInterfaceType.INTERFACE_ANY);
		eeprom = new FtdiEeprom();
	}

	public Ftdi() {
		super();
	}

	public void setInterface(PortInterfaceType newInterface) {
		if (device != null) {
			PortInterfaceType checkInterface = newInterface;
			if (checkInterface.equals(PortInterfaceType.INTERFACE_ANY))
				checkInterface = PortInterfaceType.INTERFACE_A;

			if (!interfaceType.equals(checkInterface))
				throw new LibUsbException("Interface can not be changed on an already open device", -3);
		}

		switch (newInterface) {
		case INTERFACE_ANY:
		case INTERFACE_A:
			interfaceType = PortInterfaceType.INTERFACE_A;
			iface = 0;
			inEndPoint = 0x02;
			outEndPoint = (byte) 0x81;
			break;
		case INTERFACE_B:
			interfaceType = PortInterfaceType.INTERFACE_B;
			iface = 1;
			inEndPoint = 0x04;
			outEndPoint = (byte) 0x83;
			break;
		case INTERFACE_C:
			interfaceType = PortInterfaceType.INTERFACE_C;
			iface = 2;
			inEndPoint = 0x06;
			outEndPoint = (byte) 0x85;
			break;
		case INTERFACE_D:
			interfaceType = PortInterfaceType.INTERFACE_D;
			iface = 3;
			inEndPoint = 0x08;
			outEndPoint = (byte) 0x87;
			break;
		default:
			throw new LibUsbException("Unknown interface", -1);
		}
	}

	@Override
	protected int determinMaxPacketSize(Device dev, int defSize) {
		if (ChipType.TYPE_2232H.equals(type) || ChipType.TYPE_4232H.equals(type) || ChipType.TYPE_232H.equals(type))
			defSize = 512;
		else
			defSize = 64;
		return super.determinMaxPacketSize(dev, defSize);
	}

	@Override
	public void usbOpenDev(Device dev) {
		super.usbOpenDev(dev);

		DeviceDescriptor desc = new DeviceDescriptor();

		if (LibUsb.getDeviceDescriptor(dev, desc) < 0)
			throw new LibUsbException("LibUsb.getDeviceDescriptior() failed", -9);

		if (desc.bcdDevice() == 0x400 || (desc.bcdDevice() == 0x200 && desc.iSerialNumber() == 0))
			type = ChipType.TYPE_BM;
		else if (desc.bcdDevice() == 0x200)
			type = ChipType.TYPE_AM;
		else if (desc.bcdDevice() == 0x500)
			type = ChipType.TYPE_2232C;
		else if (desc.bcdDevice() == 0x600)
			type = ChipType.TYPE_R;
		else if (desc.bcdDevice() == 0x700)
			type = ChipType.TYPE_2232H;
		else if (desc.bcdDevice() == 0x800)
			type = ChipType.TYPE_4232H;
		else if (desc.bcdDevice() == 0x900)
			type = ChipType.TYPE_232H;
		else if (desc.bcdDevice() == 0x1000)
			type = ChipType.TYPE_230X;

		try {
			usbReset();
		} catch (LibUsbException e) {
			UsbCloseInternal();
			throw e;
		}

		try {
			setBaudrate(9600);
		} catch (LibUsbException e) {
			UsbCloseInternal();
			throw new LibUsbException("set baudrate failed", -7);
		}
	}

	public void usbReset() {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		int code;
		if ((code = LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_RESET_REQUEST, SIO_RESET_SIO, (short) interfaceType.getIndex(), EMPTY_BUF, writeTimeout)) < 0)
			throw new LibUsbException("Ftdi reset failed", code);

		resetReadBuffer();
	}

	public void usbPurgeRxBuffer() {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);

		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_RESET_REQUEST, SIO_RESET_PURGE_RX, (short) interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("Ftdi purge of RX buffer failed", -1);

		resetReadBuffer();
	}

	public void usbPurgeTxBuffer() {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);

		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_RESET_REQUEST, SIO_RESET_PURGE_TX, (short) interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("Ftdi purge of TX buffer failed", -1);
	}

	public void usbPurgeBuffers() {
		usbPurgeRxBuffer();
		usbPurgeTxBuffer();
	}

	public int readData(byte[] buf) {
		int offset = 0, ret, i, num_of_chunks, chunk_remains;
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

			if (actual_length > 2) {
				// skip Ftdi status bytes.
				// Maybe stored in the future to enable modem use
				num_of_chunks = actual_length / packet_size;
				chunk_remains = actual_length % packet_size;

				readBuffer.position(readBuffer.position() + 2);
				actual_length -= 2;

				if (actual_length > packet_size - 2) {
					byte[] buffer = new byte[readBuffer.remaining()];
					readBuffer.get(buffer);
					for (i = 1; i < num_of_chunks; i++)
						System.arraycopy(buffer, packet_size * i, buffer, (packet_size - 2) * i, packet_size - 2);
					if (chunk_remains > 2) {
						System.arraycopy(buffer, packet_size * i, buffer, (packet_size - 2) * i, chunk_remains - 2);
						actual_length -= 2 * num_of_chunks;
					} else {
						actual_length -= 2 * (num_of_chunks - 1) + chunk_remains;
					}
					readBuffer.clear();
					readBuffer.put(buffer, 0, actual_length);
					readBuffer.limit(actual_length);
					readBuffer.rewind();
				}
			} else if (actual_length <= 2) {
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

	private class BaudCalcResults {
		public int bestBaud;
		public long encodedDivisor;
		public short index;
		public short value;
	}

	private BaudCalcResults toClkbitsAm(int baudrate) {
		BaudCalcResults res = new BaudCalcResults();
		final int frac_code[] = { 0, 3, 2, 4, 1, 5, 6, 7 };
		final int am_adjust_up[] = { 0, 0, 0, 1, 0, 3, 2, 1 };
		final int am_adjust_dn[] = { 0, 0, 0, 1, 0, 1, 2, 3 };

		int divisor, bestDivisor, bestBaud, bestBaudDiff;

		divisor = 24000000 / baudrate;

		// Round down to supported fraction (AM only)
		divisor -= am_adjust_dn[divisor & 7];

		// Try this divisor and the one above it (because division rounds down)
		bestDivisor = 0;
		bestBaud = 0;
		bestBaudDiff = 0;
		for (int i = 0; i < 2; i++) {
			int try_divisor = divisor + i;
			int baud_estimate;
			int baud_diff;

			// Round up to supported divisor value
			if (try_divisor <= 8) {
				// Round up to minimum supported divisor
				try_divisor = 8;
			} else if (divisor < 16) {
				// AM doesn't support divisors 9 through 15 inclusive
				try_divisor = 16;
			} else {
				// Round up to supported fraction (AM only)
				try_divisor += am_adjust_up[try_divisor & 7];
				if (try_divisor > 0x1FFF8) {
					// Round down to maximum supported divisor value (for AM)
					try_divisor = 0x1FFF8;
				}
			}
			// Get estimated baud rate (to nearest integer)
			baud_estimate = (24000000 + (try_divisor / 2)) / try_divisor;
			// Get absolute difference from requested baud rate
			if (baud_estimate < baudrate) {
				baud_diff = baudrate - baud_estimate;
			} else {
				baud_diff = baud_estimate - baudrate;
			}
			if (i == 0 || baud_diff < bestBaudDiff) {
				// Closest to requested baud rate so far
				bestDivisor = try_divisor;
				bestBaud = baud_estimate;
				bestBaudDiff = baud_diff;
				if (baud_diff == 0) {
					// Spot on! No point trying
					break;
				}
			}
		}
		// Encode the best divisor value
		res.encodedDivisor = (bestDivisor >> 3) | (frac_code[bestDivisor & 7] << 14);
		// Deal with special cases for encoded value
		if (res.encodedDivisor == 1) {
			res.encodedDivisor = 0; // 3000000 baud
		} else if (res.encodedDivisor == 0x4001) {
			res.encodedDivisor = 1; // 2000000 baud (BM only)
		}
		res.bestBaud = bestBaud;

		return res;
	}

	private BaudCalcResults toClkbits(int baudrate, int clk, int clkDiv) {
		final int fracCode[] = { 0, 3, 2, 4, 1, 5, 6, 7 };
		int bestBaud = 0;
		int divisor, bestDivisor;
		BaudCalcResults res = new BaudCalcResults();
		if (baudrate >= clk / clkDiv) {
			res.encodedDivisor = 0;
			bestBaud = clk / clkDiv;
		} else if (baudrate >= clk / (clkDiv + clkDiv / 2)) {
			res.encodedDivisor = 1;
			bestBaud = clk / (clkDiv + clkDiv / 2);
		} else if (baudrate >= clk / (2 * clkDiv)) {
			res.encodedDivisor = 2;
			bestBaud = clk / (2 * clkDiv);
		} else {
			/* We divide by 16 to have 3 fractional bits and one bit for rounding */
			divisor = clk * 16 / clkDiv / baudrate;
			if ((divisor & 1) == 1) /* Decide if to round up or down */
				bestDivisor = divisor / 2 + 1;
			else
				bestDivisor = divisor / 2;
			if (bestDivisor > 0x20000)
				bestDivisor = 0x1ffff;
			bestBaud = clk * 16 / clkDiv / bestDivisor;
			if ((bestBaud & 1) == 1) /* Decide if to round up or down */
				bestBaud = bestBaud / 2 + 1;
			else
				bestBaud = bestBaud / 2;
			res.encodedDivisor = (bestDivisor >> 3) | (fracCode[bestDivisor & 0x7] << 14);
		}
		res.bestBaud = bestBaud;
		return res;
	}

	private BaudCalcResults convertBaudrate(int baudrate) {
		BaudCalcResults res;

		if (baudrate <= 0) {
			// Return error
			return null;
		}

		final int H_CLK = 120000000;
		final int C_CLK = 48000000;
		if (ChipType.TYPE_2232H.equals(type) || ChipType.TYPE_4232H.equals(type) || ChipType.TYPE_232H.equals(type)) {
			if (baudrate * 10 > H_CLK / 0x3fff) {
				/*
				 * On H Devices, use 12 000 000 Baudrate when possible We have a 14 bit divisor, a 1 bit divisor switch (10 or 16) three fractional bits and a 120 MHz clock
				 * Assume AN_120 "Sub-integer divisors between 0 and 2 are not allowed" holds for DIV/10 CLK too, so /1, /1.5 and /2 can be handled the same
				 */
				res = toClkbits(baudrate, H_CLK, 10);
				res.encodedDivisor |= 0x20000; /* switch on CLK/10 */
			} else
				res = toClkbits(baudrate, C_CLK, 16);
		} else if (ChipType.TYPE_BM.equals(type) || ChipType.TYPE_2232C.equals(type) || ChipType.TYPE_R.equals(type) || ChipType.TYPE_230X.equals(type)) {
			res = toClkbits(baudrate, C_CLK, 16);
		} else {
			res = toClkbitsAm(baudrate);
		}
		// Split into "value" and "index" values
		res.value = (short) (res.encodedDivisor & 0xFFFF);
		if (ChipType.TYPE_2232H.equals(type) || ChipType.TYPE_4232H.equals(type) || ChipType.TYPE_232H.equals(type)) {
			res.index = (short) (res.encodedDivisor >> 8);
			res.index &= 0xFF00;
			res.index |= interfaceType.getIndex();
		} else
			res.index = (short) (res.encodedDivisor >> 16);

		// Return the nearest baud rate
		return res;
	}

	public void setBaudrate(int baudrate) {
		BaudCalcResults res;

		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);

		if (bitbangEnabled) {
			baudrate = baudrate * 4;
		}

		res = convertBaudrate(baudrate);
		if (res.bestBaud <= 0)
			throw new LibUsbException("Silly baudrate <= 0.", -1);

		// Check within tolerance (about 5%)
		if ((res.bestBaud * 2 < baudrate /* Catch overflows */ ) || ((res.bestBaud < baudrate) ? (res.bestBaud * 21 < baudrate * 20) : (baudrate * 21 < res.bestBaud * 20)))
			throw new LibUsbException("Unsupported baudrate. Note: bitbang baudrates are automatically multiplied by 4", -1);

		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_BAUDRATE_REQUEST, res.value, res.index, EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("Setting new baudrate failed", -2);
	}

	public void setLineProperty(LineDatabit bits, LineStopbit sbit, LineParity parity) {
		setLineProperty(bits, sbit, parity, LineBreak.BREAK_OFF);
	}

	public void setLineProperty(LineDatabit bits, LineStopbit sbit, LineParity parity, LineBreak breakType) {
		short value = (short) bits.getBits();

		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);

		switch (parity) {
		case NONE:
			value |= (0x00 << 8);
			break;
		case ODD:
			value |= (0x01 << 8);
			break;
		case EVEN:
			value |= (0x02 << 8);
			break;
		case MARK:
			value |= (0x03 << 8);
			break;
		case SPACE:
			value |= (0x04 << 8);
			break;
		}

		switch (sbit) {
		case STOP_BIT_1:
			value |= (0x00 << 11);
			break;
		case STOP_BIT_15:
			value |= (0x01 << 11);
			break;
		case STOP_BIT_2:
			value |= (0x02 << 11);
			break;
		}

		switch (breakType) {
		case BREAK_OFF:
			value |= (0x00 << 14);
			break;
		case BREAK_ON:
			value |= (0x01 << 14);
			break;
		}

		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_DATA_REQUEST, value, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("Setting new line property failed", -1);
	}

	public void setBitmode(byte mask, BitMode mode) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);

		short usbVal = (short) (mask | ((short) mode.getMask() << 8));
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_BITMODE_REQUEST, usbVal, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("unable to configure bitbang mode. Perhaps not a BM type chip?", -1);

		bitbangEnabled = !BitMode.RESET.equals(mode);
	}

	public void disableBitmode() {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);

		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_BITMODE_REQUEST, (short) 0, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("unable to leave bitbang mode. Perhaps not a BM type chip?", -1);

		bitbangEnabled = false;
	}

	public byte readPins() {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		ByteBuffer pins = ByteBuffer.allocateDirect(1);
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_IN_REQTYPE, SIO_READ_PINS_REQUEST, (short) 0, interfaceType.getIndex(), pins, readTimeout) != 1)
			throw new LibUsbException("read pins failed", -1);
		return pins.get();
	}

	public void setLatencyTimer(byte latency) {
		if (latency == 0)
			throw new LibUsbException("latency out of range. Only valid for 1-255", -1);
		if (device == null)
			throw new LibUsbException("USB device unavailable", -3);
		short usbVal = (short) (0x00FF & latency);
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_LATENCY_TIMER_REQUEST, usbVal, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("unable to set latency timer", -1);
	}

	public byte getLatencyTimer() {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		ByteBuffer latency = ByteBuffer.allocateDirect(1);
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_IN_REQTYPE, SIO_GET_LATENCY_TIMER_REQUEST, (short) 0, interfaceType.getIndex(), latency, readTimeout) != 1)
			throw new LibUsbException("reading latency timer failed", -1);
		return latency.get();
	}

	public short pullModemStatus() {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		ByteBuffer status = ByteBuffer.allocateDirect(2);
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_IN_REQTYPE, SIO_POLL_MODEM_STATUS_REQUEST, (short) 0, interfaceType.getIndex(), status, readTimeout) != 2)
			throw new LibUsbException("getting modemfailed", -1);
		return (short) (((short) status.get(1) << 8) | ((short) status.get(0) & 0x00FF));
	}

	public void setFlowCtrl(FlowControl flowctrl) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_FLOW_CTRL_REQUEST, (short) 0, (short) (flowctrl.getBytecode() | interfaceType.getIndex()),
				EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("unable to set flow control", -1);
	}

	public void setFlowCtrlXonXoff(byte xon, byte xoff) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		short xonxoff = (short) (((short) xon & 0x00ff) | ((short) xoff << 8));
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_FLOW_CTRL_REQUEST, xonxoff, (short) (SIO_XON_XOFF_HS | interfaceType.getIndex()), EMPTY_BUF,
				writeTimeout) < 0)
			throw new LibUsbException("unable to set flow control xon xoff", -1);
	}

	public void setDtr(boolean state) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		short usbVal = state ? SIO_SET_DTR_HIGH : SIO_SET_DTR_LOW;
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_MODEM_CTRL_REQUEST, usbVal, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("set dtr failed", -1);
	}

	public void setRts(boolean state) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		short usbVal = state ? SIO_SET_RTS_HIGH : SIO_SET_RTS_LOW;
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_MODEM_CTRL_REQUEST, usbVal, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("set rts failed", -1);
	}

	public void setDtrRts(boolean dtr, boolean rts) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		short usbVal = dtr ? SIO_SET_DTR_HIGH : SIO_SET_DTR_LOW;
		usbVal |= rts ? SIO_SET_RTS_HIGH : SIO_SET_RTS_LOW;
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_MODEM_CTRL_REQUEST, usbVal, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("set dtr and rts failed", -1);
	}

	public void setEventChar(byte eventChar, boolean enable) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		short usbVal = (short) (eventChar & 0x00ff);
		if (enable)
			usbVal |= 1 << 8;
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_EVENT_CHAR_REQUEST, usbVal, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("setting event character failed", -1);
	}

	public void setErrorChar(byte errorChar, boolean enable) {
		if (device == null)
			throw new LibUsbException("USB device unavailable", -2);
		short usbVal = (short) (errorChar & 0x00ff);
		if (enable)
			usbVal |= 1 << 8;
		if (LibUsb.controlTransfer(device, FTDI_DEVICE_OUT_REQTYPE, SIO_SET_ERROR_CHAR_REQUEST, usbVal, interfaceType.getIndex(), EMPTY_BUF, writeTimeout) < 0)
			throw new LibUsbException("setting error character failed", -1);
	}


}
