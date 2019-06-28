package com.alchitry.labs.hardware.ftdi;

import com.alchitry.labs.hardware.ftdi.enums.BitMode;
import com.alchitry.labs.hardware.ftdi.enums.MpsseCommand;

public abstract class Mpsse {
	protected Ftdi ftdi;

	public Mpsse(Ftdi ftdi) {
		this.ftdi = ftdi;
	}
	
	public class MpsseException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public MpsseException(String string) {
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
			throw new MpsseException("failed to sync with MPSSE");
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
	
	protected void setFreq(double freq) {
		int clockDivisor = (int) (30.0 / (freq / 1000000.0) - 1.0);
		// set TCK = 60MHz /((1 + [(1 +0xValueH*256) OR 0xValueL])*2)
		ftdi.writeData(new byte[] { MpsseCommand.SET_CLK_DIV.getCommand(), (byte) (clockDivisor & 0xff), (byte) ((clockDivisor >>> 8) & 0xff) });
	}

}
