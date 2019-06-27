package com.alchitry.labs.hardware.ftdi;

import org.apache.commons.lang3.ArrayUtils;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.ftdi.JtagState.Transistions;
import com.alchitry.labs.hardware.ftdi.enums.BitMode;

public class Jtag {
	private Ftdi ftdi;
	private JtagState currentState;

	public Jtag(Ftdi ftdi) {
		this.ftdi = ftdi;
		currentState = JtagState.RUN_TEST_IDLE;
	}

	public class JtagException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public JtagException(String string) {
			super(string);
		}
	}

	public void init() {
		ftdi.usbReset();
		ftdi.readDataSetChunkSize(16384);
		ftdi.writeDataSetChunksize(16384);
		ftdi.setErrorChar((byte) 0, false);
		ftdi.setEventChar((byte) 0, false);
		ftdi.setLatencyTimer((byte) 16);
		ftdi.setBitmode((byte) 0, BitMode.RESET);
		ftdi.setBitmode((byte) 0, BitMode.MPSSE);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		if (!syncMpsse())
			throw new JtagException("failed to sync with MPSSE");

		configJtag();
	}

	private boolean syncMpsse() {
		ftdi.writeData(new byte[] { (byte) 0xAA });
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		byte[] data = new byte[8];

		int read = 0;

		while (read == 0)
			read = ftdi.readData(data);

		for (int i = 0; i < read - 1; i++) {
			if (data[i] == (byte) 0xFA && data[i + 1] == (byte) 0xAA)
				return true;
		}

		return false;
	}

	private void configJtag() {
		final int clockDivisor = 0x05DB; // Value of clock divisor, SCL Frequency = 60/((1+0x05DB)*2) (MHz) = 20khz
		// Set up the Hi-Speed specific commands for the FTx232H
		ftdi.writeData(new byte[] { (byte) 0x8D, (byte) 0x97, (byte) 0x8D });
		// Set initial states of the MPSSE interface - low byte, both pin directions and output values
		ftdi.writeData(new byte[] { (byte) 0x80, (byte) 0x08, (byte) 0x0B });
		// Set initial states of the MPSSE interface - high byte, both pin directions and output values
		ftdi.writeData(new byte[] { (byte) 0x82, (byte) 0x00, (byte) 0x00 });
		// set TCK = 60MHz /((1 + [(1 +0xValueH*256) OR 0xValueL])*2)
		ftdi.writeData(new byte[] { (byte) 0x86, (byte) (clockDivisor & 0xff), (byte) ((clockDivisor >> 8) & 0xff) });
		// Disable internal loop-back
		ftdi.writeData(new byte[] { (byte) 0x85 });
	}

	public void setFreq(double freq) {
		int clockDivisor = (int) (30.0 / (freq / 1000000.0) - 1.0);
		// set TCK = 60MHz /((1 + [(1 +0xValueH*256) OR 0xValueL])*2)
		ftdi.writeData(new byte[] { (byte) 0x86, (byte) (clockDivisor & 0xff), (byte) ((clockDivisor >> 8) & 0xff) });
	}

	public void resetState() {
		currentState = JtagState.CAPTURE_DR;
		navitageToState(JtagState.TEST_LOGIC_RESET);
	}

	public void navitageToState(JtagState state) {
		Transistions transistions = currentState.getTransitions(state);

		if (transistions.moves > 0) {
			if (transistions.moves < 8) {
				ftdi.writeData(new byte[] { (byte) 0x4B, (byte) (transistions.moves - 1), (byte) (0x7F & transistions.tms) });
			} else {
				ftdi.writeData(new byte[] { (byte) 0x4B, (byte) 6, (byte) (0x7F & transistions.tms) });
				ftdi.writeData(new byte[] { (byte) 0x4B, (byte) (transistions.moves - 8), (byte) (0x7F & (transistions.tms >> 7)) });
			}
		}
		currentState = state;
	}

	public void shiftData(int bitCount, byte[] tdi, byte[] tdo, byte[] mask) {
		switch (currentState) {
		case SHIFT_DR:
		case SHIFT_IR:
			break;
		default:
			throw new JtagException("jtag fsm is in state " + currentState.name() + " which is not a shift state");
		}

		int reqBytes = bitCount / 8 + ((bitCount % 8 > 0) ? 1 : 0);
		boolean read = tdo != null;
		byte[] tdoBuffer = null;
		if (read) {
			tdoBuffer = new byte[tdo.length];
			ftdi.readData(tdoBuffer);
		}
		int tdoBytes = 0;

		if (read && (tdo.length != tdi.length || mask.length != tdi.length))
			throw new JtagException("tdo or mask lengths do not match tdi lenght");

		if (bitCount < 9) {
			ftdi.writeData(new byte[] { (byte) (read ? 0x3B : 0x1B), (byte) (bitCount - 2), tdi[0] });
			byte lastBit = (byte) ((tdi[0] >> ((bitCount - 1) % 8)) & 0x01);
			ftdi.writeData(new byte[] { (byte) (read ? 0x6E : 0x4E), 0x00, (byte) (0x03 | (lastBit << 7)) });

			if (read) {
				byte[] inputBuffer = new byte[2];
				if (ftdi.readData(inputBuffer) != 2)
					throw new JtagException("failed to read two bytes");
				tdoBuffer[0] = (byte) ((inputBuffer[0] >> (8 - (bitCount - 1))) | (inputBuffer[1] >> (7 - (bitCount - 1))));
				tdoBytes = 1;
			}
		} else {
			int fullBytes = (bitCount - 1) / 8;
			int remBytes = fullBytes;
			int offset = 0;

			if (fullBytes > 65536 && read) {
				System.out.println("Large transfers with reads may not work!");
			}

			byte[] writeBuffer = null;
			while (remBytes > 0) {
				int bct = remBytes > 65536 ? 65536 : remBytes;
				if (writeBuffer == null || writeBuffer.length != bct + 3)
					writeBuffer = new byte[bct + 3];
				writeBuffer[0] = (byte) (read ? 0x39 : 0x19);
				writeBuffer[1] = (byte) ((bct - 1) & 0xff);
				writeBuffer[2] = (byte) (((bct - 1) >> 8) & 0xff);
				System.arraycopy(tdi, offset, writeBuffer, 3, bct);
				if (ftdi.writeData(writeBuffer) != writeBuffer.length)
					throw new JtagException("failed to write entire buffer");
				remBytes -= bct;
				offset += bct;

				if (read) {
					int bytesToRead = bct;
					byte[] readBuffer = new byte[bytesToRead];
					while (bytesToRead > 0) {
						int count = ftdi.readData(readBuffer);
						bytesToRead -= count;
						System.arraycopy(readBuffer, 0, tdoBuffer, tdoBytes, count);
						tdoBytes += count;
					}
				}
			}

			int partialBits = bitCount - 1 - (fullBytes * 8);

			if (fullBytes * 8 + 1 != bitCount) {
				ftdi.writeData(new byte[] { (byte) (read ? 0x3B : 0x1B), (byte) (partialBits - 1), tdi[reqBytes - 1] });
			}

			byte lastBit = (byte) ((tdi[reqBytes - 1] >> ((bitCount - 1) % 8)) & 0x01);
			ftdi.writeData(new byte[] { (byte) (read ? 0x6e : 0x4E), (byte) 0, (byte) (0x03 | (lastBit << 7)) });

			if (read) {
				int bytesToRead = (fullBytes * 8 + 1 != bitCount) ? 2 : 1;
				byte[] readBuffer = new byte[bytesToRead];
				if (ftdi.readData(readBuffer) != 2)
					throw new JtagException("failed to read final two bytes");
				tdoBuffer[tdoBytes] = (byte) ((readBuffer[1] >> (8 - (partialBits - 1))) | (readBuffer[0] >> (7 - (partialBits - 1))));
			}
		}

		if (read) {
			for (int i = 0; i < tdo.length; i++)
				if ((tdoBuffer[tdoBuffer.length - 1 - i] & mask[i]) != (tdo[i] & mask[i])) {
					throw new JtagException("TDO didn't match. Got " + Util.bytesToHex(tdoBuffer) + " expected " + Util.bytesToHex(tdo) + " with mask " + Util.bytesToHex(mask) + " at byte " + i);
				}

		}
		switch (currentState) {
		case SHIFT_DR:
			currentState = JtagState.EXIT1_DR;
			break;
		case SHIFT_IR:
			currentState = JtagState.EXIT1_IR;
			break;
		default:
			break;
		}
	}

	public void sendClocks(long cycles) {
		if (cycles / 8 > 65536) {
			sendClocks(cycles - 65536 * 8);
			cycles = 65536 * 8;
		}
		cycles /= 8;
		ftdi.writeData(new byte[] { (byte) 0x8F, (byte) ((cycles - 1) & 0xff), (byte) ((cycles - 1 >> 8) & 0xff) });
	}

}
