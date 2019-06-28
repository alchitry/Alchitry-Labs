package com.alchitry.labs.hardware.ftdi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.hardware.ftdi.enums.FlashCommand;
import com.alchitry.labs.hardware.ftdi.enums.MpsseCommand;

public class Spi extends Mpsse {
	//private static final byte DATA_TMS = (byte) 0x40; /* When set use TMS mode */
	private static final byte DATA_IN = (byte) 0x20; /* When set read data (Data IN) */
	private static final byte DATA_OUT = (byte) 0x10; /* When set write data (Data OUT) */
	//private static final byte DATA_LSB = (byte) 0x08; /* When set input/output data LSB first. */
	//private static final byte DATA_ICN = (byte) 0x04; /* When set receive data on negative clock edge */
	private static final byte DATA_BITS = (byte) 0x02; /* When set count bits not bytes */
	private static final byte DATA_OCN = (byte) 0x01; /* When set update data on negative clock edge */

	public Spi(Ftdi ftdi) {
		super(ftdi);
		init();
	}

	@Override
	public void init() {
		super.init();
		configSpi();
	}

	private void configSpi() {
		// Set up the Hi-Speed specific commands for the FTx232H
		ftdi.writeData(new byte[] { MpsseCommand.TCK_X5.getCommand(), MpsseCommand.DIS_ADPT_CLK.getCommand(), MpsseCommand.DIS_3PH_CLK.getCommand() });
		// Set initial states of the MPSSE interface - low byte, both pin directions and output values
		ftdi.writeData(new byte[] { MpsseCommand.SETB_LOW.getCommand(), (byte) 0x00, (byte) 0xBB });
		// Set initial states of the MPSSE interface - high byte, both pin directions and output values
		ftdi.writeData(new byte[] { MpsseCommand.SETB_HIGH.getCommand(), (byte) 0x00, (byte) 0x00 });
		// Set default frequency
		setFreq(30000000);
		// Disable internal loop-back
		ftdi.writeData(new byte[] { MpsseCommand.LOOPBACK_DIS.getCommand() });
	}

	private byte rxByte() {
		byte[] buf = new byte[1];
		ftdi.readDataWithTimeout(buf);
		return buf[0];
	}

	private void txByte(byte b) {
		byte[] buf = new byte[1];
		buf[0] = b;
		if (ftdi.writeData(buf) != 1)
			throw new MpsseException("Failed to write single byte");
	}

	private void sendSpi(byte[] data) {
		byte[] buf = new byte[3];
		buf[0] = DATA_OUT | DATA_OCN;
		buf[1] = (byte) ((data.length - 1) & 0xff);
		buf[2] = (byte) (((data.length - 1) >>> 8) & 0xff);
		ftdi.writeData(buf);

		if (ftdi.writeData(data) != data.length)
			throw new MpsseException("Failed to write full chunk!");
	}

	private void xferSpi(byte[] data) {
		byte[] buf = new byte[3];
		buf[0] = DATA_IN | DATA_OUT | DATA_OCN;
		buf[1] = (byte) ((data.length - 1) & 0xff);
		buf[2] = (byte) (((data.length - 1) >>> 8) & 0xff);
		ftdi.writeData(buf);

		if (ftdi.writeData(data) != data.length)
			throw new MpsseException("Failed to write full chunk!");

		ftdi.readDataWithTimeout(data);
	}

	private byte xferSpiBits(byte data, int n) {
		if (n < 1)
			return 0;

		byte[] buf = new byte[3];
		buf[0] = DATA_IN | DATA_OUT | DATA_OCN | DATA_BITS;
		buf[1] = (byte) (n - 1);
		buf[2] = data;
		ftdi.writeData(buf);

		return rxByte();
	}

	private void setGpio(boolean slaveSel, boolean creset) {
		byte gpio = 0;
		if (slaveSel)
			gpio |= 0x10;
		if (creset)
			gpio |= 0x80;
		byte[] buf = new byte[3];
		buf[0] = MpsseCommand.SETB_LOW.getCommand();
		buf[1] = gpio;
		buf[2] = (byte) 0x93;
		ftdi.writeData(buf);
	}

	public boolean getCdone() {
		txByte(MpsseCommand.READB_LOW.getCommand());
		byte data = rxByte();
		return (data & 0x40) != 0;
	}

	private void flashReleaseReset() {
		setGpio(true, true);
	}

	private void flashChipSelect() {
		setGpio(false, false);
	}

	private void flashChipDeselect() {
		setGpio(true, false);
	}

	public void flashReadId() {
		byte[] buf = new byte[5];
		byte[] ext = null;
		buf[0] = FlashCommand.JEDECID.getCommand();
		flashChipSelect();
		xferSpi(buf);
		if (buf[4] == (byte) 0xFF) {
			Util.println("Extended device string lenght is 0xff. This is likely an error. Ignoring...", Theme.infoTextColor);
		} else if (buf[4] != 0) {
			ext = new byte[buf[4]];
			xferSpi(ext);
		}
		flashChipDeselect();

		if (buf[1] != (byte) 0xEF || buf[2] != (byte) 0x40 || buf[3] != (byte) 0x16 || buf[4] != (byte) 0x00) {
			StringBuilder sb = new StringBuilder();
			sb.append("0x");
			for (int i = 1; i < 5; i++)
				sb.append(String.format("%02X", buf[i]));
			if (ext != null)
				for (int i = 0; i < ext.length; i++)
					sb.append(String.format("%02X", ext[i]));
			throw new MpsseException("Flash ID was " + sb.toString() + " expected 0xEF401600");
		}
	}

	private void flashReset() {
		flashChipSelect();
		xferSpiBits((byte) 0xff, 8);
		flashChipDeselect();

		flashChipSelect();
		xferSpiBits((byte) 0xff, 2);
		flashChipDeselect();
	}

	private void flashPowerUp() {
		byte[] data = new byte[] { FlashCommand.RPD.getCommand() };
		flashChipSelect();
		sendSpi(data);
		flashChipDeselect();
	}

	private void flashPowerDown() {
		byte[] data = new byte[] { FlashCommand.PD.getCommand() };
		flashChipSelect();
		sendSpi(data);
		flashChipDeselect();
	}

	private byte flashReadStatus() {
		byte[] data = new byte[2];
		data[0] = FlashCommand.RSR1.getCommand();

		flashChipSelect();
		xferSpi(data);
		flashChipDeselect();

		return data[1];
	}

	private void flashWriteEnable() {
		byte[] data = new byte[] { FlashCommand.WE.getCommand() };
		flashChipSelect();
		sendSpi(data);
		flashChipDeselect();
	}

	private void flashBulkErase() {
		byte[] data = new byte[] { FlashCommand.CE.getCommand() };
		flashChipSelect();
		sendSpi(data);
		flashChipDeselect();
	}

	private void flash64KbSectorErase(int addr) {
		byte[] data = new byte[] { FlashCommand.BE64.getCommand(), (byte) (addr >>> 16), (byte) (addr >>> 8), (byte) addr };
		flashChipSelect();
		sendSpi(data);
		flashChipDeselect();
	}

	private void flashProg(int addr, byte[] data) {
		byte[] cmd = new byte[] { FlashCommand.PP.getCommand(), (byte) (addr >>> 16), (byte) (addr >>> 8), (byte) addr };
		flashChipSelect();
		sendSpi(cmd);
		sendSpi(data);
		flashChipDeselect();
	}

	@SuppressWarnings("unused")
	private void flashRead(int addr, byte[] data) {
		byte[] cmd = new byte[] { FlashCommand.RD.getCommand(), (byte) (addr >>> 16), (byte) (addr >>> 8), (byte) addr };
		flashChipSelect();
		sendSpi(cmd);
		xferSpi(data);
		flashChipDeselect();
	}

	private void flashWait() {
		int count = 0;
		byte[] data = new byte[2];
		while (true) {
			data[0] = FlashCommand.RSR1.getCommand();
			flashChipSelect();
			xferSpi(data);
			flashChipDeselect();
			if ((data[1] & 0x01) == 0) {
				if (count < 2) {
					count++;
				} else {
					break;
				}
			} else {
				count = 0;
			}
			Util.sleep(1);
		}
	}

	@SuppressWarnings("unused")
	private void flashDisableProtection() {
		byte[] data = new byte[] { FlashCommand.WSR1.getCommand(), 0x00 };
		flashChipSelect();
		xferSpi(data);
		flashChipDeselect();
		flashWait();

		if (flashReadStatus() != 0)
			throw new MpsseException(String.format("Failed to disable write protection. SR is 0x%02X (expected 0x00)", data[1]));
	}

	public void eraseFlash() {
		Util.println("Resetting...");
		flashChipDeselect();
		Util.sleep(250);
		Util.println("Erasing...");
		flashReset();
		flashPowerUp();
		flashReadId();
		flashWriteEnable();
		flashBulkErase();
		flashWait();
		flashPowerDown();
		flashReleaseReset();
		Util.sleep(250);
		Util.println("Done...", Theme.successTextColor);
	}

	public void writeBin(String binFile) throws IOException {
		byte[] binData = Files.readAllBytes(Paths.get(binFile));
		Util.println("Resetting...");
		flashChipDeselect();
		Util.sleep(250);
		flashReset();
		flashPowerUp();
		flashReadId();

		int begin_addr = 0;
		int end_addr = (binData.length + 0xffff) & ~0xffff;

		Util.println("Erasing...");

		for (int addr = begin_addr; addr < end_addr; addr += 0x10000) {
			flashWriteEnable();
			flash64KbSectorErase(addr);
			flashWait();
		}

		Util.println("Programming...");

		byte[] pageBuf = new byte[256];
		int offset = 0;
		while (offset < binData.length) {
			int ct = Math.min(256, binData.length - offset);
			if (pageBuf.length != ct)
				pageBuf = new byte[ct];
			System.arraycopy(binData, offset, pageBuf, 0, pageBuf.length);
			flashWriteEnable();
			flashProg(offset, pageBuf);
			flashWait();
			offset += pageBuf.length;
		}

		Util.println("Resetting...");

		flashPowerDown();
		flashReleaseReset();
		Util.sleep(250);

		Util.println("Done...", Theme.successTextColor);
	}

}
