package com.alchitry.labs.hardware.usb.ftdi;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.usb.ftdi.JtagState.Transistions;
import com.alchitry.labs.hardware.usb.ftdi.enums.MpsseCommand;

public class Jtag extends Mpsse {
	private JtagState currentState = JtagState.RUN_TEST_IDLE;;

	public Jtag(Ftdi ftdi) {
		super(ftdi);
	}

	@Override
	public void init() {
		super.init();
		configJtag();
	}

	private void configJtag() {
		// Set up the Hi-Speed specific commands for the FTx232H
		ftdi.writeData(new byte[] { MpsseCommand.TCK_X5.getCommand(), MpsseCommand.DIS_ADPT_CLK.getCommand(), MpsseCommand.DIS_3PH_CLK.getCommand() });
		// Set initial states of the MPSSE interface - low byte, both pin directions and output values
		ftdi.writeData(new byte[] { MpsseCommand.SETB_LOW.getCommand(), (byte) 0x08, (byte) 0x0B });
		// Set initial states of the MPSSE interface - high byte, both pin directions and output values
		ftdi.writeData(new byte[] { MpsseCommand.SETB_HIGH.getCommand(), (byte) 0x00, (byte) 0x00 });
		// Set default frequency
		setFreq(1000000);
		// Disable internal loop-back
		ftdi.writeData(new byte[] { MpsseCommand.LOOPBACK_DIS.getCommand() });
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

	public void shiftData(int bitCount, byte[] tdi, byte[] tdo) {
		if (bitCount == 0)
			return;
		switch (currentState) {
		case SHIFT_DR:
		case SHIFT_IR:
			break;
		default:
			throw new MpsseException("jtag fsm is in state " + currentState.name() + " which is not a shift state");
		}

		int reqBytes = bitCount / 8 + ((bitCount % 8 > 0) ? 1 : 0);
		boolean read = tdo != null;
		if (read) {
			ftdi.readData(tdo);
		}
		int tdoBytes = 0;

		if (read && (tdo.length != tdi.length))
			throw new MpsseException("tdo length do not match tdi length");

		if (bitCount < 9) {
			if (bitCount > 1)
				ftdi.writeData(new byte[] { (byte) (read ? 0x3B : 0x1B), (byte) (bitCount - 2), tdi[0] });
			byte lastBit = (byte) ((tdi[0] >>> ((bitCount - 1) % 8)) & 0x01);
			ftdi.writeData(new byte[] { (byte) (read ? 0x6E : 0x4B), 0x00, (byte) (0x03 | (lastBit << 7)) });

			if (read) {
				byte[] inputBuffer = new byte[bitCount > 1 ? 2 : 1];
				if (ftdi.readDataWithTimeout(inputBuffer) != inputBuffer.length)
					throw new RuntimeException("Read of "+inputBuffer.length + " bytes timed out!");
				tdo[0] = (byte) (inputBuffer[inputBuffer.length - 1] >>> (8 - bitCount));
				tdoBytes = 1;
			}
		} else {
			int fullBytes = (bitCount - 1) / 8;
			int remBytes = fullBytes;
			int offset = 0;

			byte[] writeBuffer = null;
			while (remBytes > 0) {
				int bct = remBytes > 4096 ? 4096 : remBytes;
				if (writeBuffer == null || writeBuffer.length != bct + 3)
					writeBuffer = new byte[bct + 3];
				writeBuffer[0] = (byte) (read ? 0x39 : 0x19);
				writeBuffer[1] = (byte) ((bct - 1) & 0xff);
				writeBuffer[2] = (byte) (((bct - 1) >> 8) & 0xff);
				System.arraycopy(tdi, offset, writeBuffer, 3, bct);
				if (ftdi.writeData(writeBuffer) != writeBuffer.length)
					throw new MpsseException("failed to write entire buffer");
				remBytes -= bct;
				offset += bct;

				if (read) {
					byte[] readBuffer = new byte[bct];
					ftdi.readDataWithTimeout(readBuffer);
					System.arraycopy(readBuffer, 0, tdo, tdoBytes, bct);
					tdoBytes += bct;
				}
			}

			int partialBits = bitCount - 1 - (fullBytes * 8);

			if (fullBytes * 8 + 1 != bitCount) {
				ftdi.writeData(new byte[] { (byte) (read ? 0x3B : 0x1B), (byte) (partialBits - 1), tdi[reqBytes - 1] });
			}

			byte lastBit = (byte) ((tdi[reqBytes - 1] >> ((bitCount - 1) % 8)) & 0x01);
			ftdi.writeData(new byte[] { (byte) (read ? 0x6E : 0x4B), (byte) 0, (byte) (0x03 | (lastBit << 7)) });

			if (read) {
				int bytesToRead = (fullBytes * 8 + 1 != bitCount) ? 2 : 1;
				byte[] readBuffer = new byte[bytesToRead];
				ftdi.readDataWithTimeout(readBuffer);
				if (bytesToRead == 2)
					tdo[tdoBytes] = (byte) (readBuffer[1] >>> (8 - (partialBits + 1)));
				else
					tdo[tdoBytes] = (byte) (readBuffer[0] >>> (8 - partialBits));
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

	public void shiftDataWithCheck(int bitCount, byte[] tdi, byte[] tdo, byte[] mask) {
		boolean read = tdo != null;
		byte[] tdoBuffer = null;
		if (read)
			tdoBuffer = new byte[tdo.length];

		if (read && mask.length != tdi.length)
			throw new MpsseException("mask length does not match tdi length");

		shiftData(bitCount, tdi, tdoBuffer);

		if (read) {
			for (int i = 0; i < tdo.length; i++)
				if ((tdoBuffer[tdoBuffer.length - 1 - i] & mask[i]) != (tdo[i] & mask[i])) {
					throw new MpsseException(
							String.format("TDO didn't match. Got %02X expected %02X with mask %02X at byte %d", tdoBuffer[tdoBuffer.length - 1 - i], tdo[i], mask[i], i));
				}

		}

	}

	public void sendClocks(long cycles) {
		if (cycles / 8 > 65536) {
			sendClocks(cycles - 65536 * 8);
			cycles = 65536 * 8;
		}
		cycles /= 8;
		ftdi.writeData(new byte[] { MpsseCommand.CLK_N8.getCommand(), (byte) ((cycles - 1) & 0xff), (byte) ((cycles - 1 >> 8) & 0xff) });
	}

	public void shiftDRWithCheck(int bits, String write, String read, String mask) {
		byte[] bWrite, bRead = null, bMask = null;
		bWrite = Util.stringToByte(write);
		if (read != null)
			bRead = Util.stringToByte(read);
		if (mask != null)
			bMask = Util.stringToByte(mask);
		shiftDRWithCheck(bits, bWrite, bRead, bMask);
	}

	public void shiftDRWithCheck(int bits, byte[] write, byte[] read, byte[] mask) {
		navitageToState(JtagState.SHIFT_DR);
		shiftDataWithCheck(bits, write, read, mask);
		navitageToState(JtagState.RUN_TEST_IDLE);
	}

	public void shiftIRWithCheck(int bits, String write, String read, String mask) {
		byte[] bWrite, bRead = null, bMask = null;
		bWrite = Util.stringToByte(write);
		if (read != null)
			bRead = Util.stringToByte(read);
		if (mask != null)
			bMask = Util.stringToByte(mask);
		shiftIRWithCheck(bits, bWrite, bRead, bMask);
	}

	public void shiftIRWithCheck(int bits, byte[] write, byte[] read, byte[] mask) {
		navitageToState(JtagState.SHIFT_IR);
		shiftDataWithCheck(bits, write, read, mask);
		navitageToState(JtagState.RUN_TEST_IDLE);
	}

	public void shiftIR(int bits, byte[] write) {
		shiftIR(bits, write, null);
	}

	public void shiftIR(int bits, byte[] write, byte[] read) {
		navitageToState(JtagState.SHIFT_IR);
		shiftData(bits, write, read);
		navitageToState(JtagState.RUN_TEST_IDLE);
	}

	public void shiftDR(int bits, byte[] write) {
		shiftDR(bits, write, null);
	}

	public void shiftDR(int bits, byte[] write, byte[] read) {
		navitageToState(JtagState.SHIFT_DR);
		shiftData(bits, write, read);
		navitageToState(JtagState.RUN_TEST_IDLE);
	}

}
