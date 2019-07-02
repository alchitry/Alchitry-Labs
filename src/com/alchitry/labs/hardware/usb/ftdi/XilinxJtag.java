package com.alchitry.labs.hardware.usb.ftdi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;

public class XilinxJtag {
	private static final String LOADER_FILE;
	static {
		URL resource = XilinxJtag.class.getResource("/fpga/au_loader.bin");
		String loaderBin = null;
		try {
			loaderBin = URLDecoder.decode(resource.getFile(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to decode path for Au loader bin file!");
		}
		LOADER_FILE = loaderBin;
	}

	private enum Instruction {
		EXTEST((byte) 0x26), EXTEST_PULSE((byte) 0x3C), EXTEST_TRAIN((byte) 0x3D), SAMPLE((byte) 0x01), USER1((byte) 0x02), USER2((byte) 0x03), USER3((byte) 0x22), USER4(
				(byte) 0x23), CFG_OUT((byte) 0x04), CFG_IN((byte) 0x05), USERCODE((byte) 0x08), IDCODE((byte) 0x09), HIGHZ_IO((byte) 0x0A), JPROGRAM((byte) 0x0B), JSTART(
						(byte) 0x0C), JSHUTDOWN((byte) 0x0D), XADC_DRP((byte) 0x37), ISC_ENABLE((byte) 0x10), ISC_PROGRAM((byte) 0x11), XSC_PROGRAM_KEY(
								(byte) 0x12), XSC_DNA((byte) 0x17), FUSE_DNA((byte) 0x32), ISC_NOOP((byte) 0x14), ISC_DISABLE((byte) 0x16), BYPASS((byte) 0x2F);

		private byte code;

		public byte getCode() {
			return code;
		}

		private Instruction(byte code) {
			this.code = code;
		}
	}

	private Jtag jtag;
	private Ftdi ftdi;

	public XilinxJtag(Ftdi ftdi) {
		this.ftdi = ftdi;
		jtag = new Jtag(ftdi);
		jtag.init();
		jtag.resetState();
	}

	private void setIR(Instruction inst) {
		jtag.navitageToState(JtagState.SHIFT_IR);
		jtag.shiftData(6, new byte[] { inst.getCode() }, null, null);
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
	}

	private void shiftDR(int bits, String write, String read, String mask) {
		byte[] bWrite, bRead = null, bMask = null;
		bWrite = Util.stringToByte(write);
		if (read != null)
			bRead = Util.stringToByte(read);
		if (mask != null)
			bMask = Util.stringToByte(mask);
		shiftDR(bits, bWrite, bRead, bMask);
	}

	private void shiftDR(int bits, byte[] write, byte[] read, byte[] mask) {
		jtag.navitageToState(JtagState.SHIFT_DR);
		jtag.shiftData(bits, write, read, mask);
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
	}

	private void shiftIR(int bits, String write, String read, String mask) {
		byte[] bWrite, bRead = null, bMask = null;
		bWrite = Util.stringToByte(write);
		if (read != null)
			bRead = Util.stringToByte(read);
		if (mask != null)
			bMask = Util.stringToByte(mask);
		shiftIR(bits, bWrite, bRead, bMask);
	}

	private void shiftIR(int bits, byte[] write, byte[] read, byte[] mask) {
		jtag.navitageToState(JtagState.SHIFT_IR);
		jtag.shiftData(bits, write, read, mask);
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
	}

	public void checkIDCODE() {
		ftdi.usbPurgeBuffers();
		setIR(Instruction.IDCODE);
		shiftDR(32, "00000000", "0362D093", "0FFFFFFF");
	}

	private byte reverse(byte b) {
		b = (byte) ((b & 0xF0) >>> 4 | (b & 0x0F) << 4);
		b = (byte) ((b & 0xCC) >>> 2 | (b & 0x33) << 2);
		b = (byte) ((b & 0xAA) >>> 1 | (b & 0x55) << 1);
		return b;
	}

	private void loadBin(String binPath) throws IOException {
		byte[] binData = Files.readAllBytes(Paths.get(binPath));
		for (int i = 0; i < binData.length; i++)
			binData[i] = reverse(binData[i]);

		ftdi.usbPurgeBuffers();
		jtag.setFreq(30000000);
		jtag.resetState();
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
		setIR(Instruction.JPROGRAM);
		setIR(Instruction.ISC_NOOP);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		// config/jprog/poll
		jtag.sendClocks(10000);
		shiftIR(6, "14", "11", "31");

		// config/slr
		setIR(Instruction.CFG_IN);
		shiftDR(binData.length * 8, binData, null, null);

		// config/start
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
		jtag.sendClocks(10000);
		setIR(Instruction.JSTART);
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
		jtag.sendClocks(100);
		shiftIR(6, "09", "31", "11");
		jtag.navitageToState(JtagState.TEST_LOGIC_RESET);
	}
	
	private void erase() throws IOException {
		Util.println("Loading bridge configuration...");
		loadBin(LOADER_FILE);
		Util.println("Erasing...");
		jtag.setFreq(1500000);
		setIR(Instruction.USER1);
		shiftDR(1, new byte[] {0}, null, null);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Util.logException(e, "Sleep interrupted, don't really care.");
		}
	}

	public void eraseFlash() throws IOException {
		erase();
		setIR(Instruction.JPROGRAM); // reset the FPGA
		jtag.resetState();
		Util.println("Done.",Theme.successTextColor);
	}

	public void writeBin(String binFile, boolean flash) throws IOException {
		if (flash) {
			erase(); // configure the FPGA with the bridge and erase the flash
			Util.println("Writing flash...");
			setIR(Instruction.USER2);
			byte[] binData = Files.readAllBytes(Paths.get(binFile));
			shiftDR(binData.length*8, binData, null, null);
			Util.println("Resetting FPGA...");
			jtag.resetState();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Util.logException(e, "Sleep interrupted, don't really care.");
			}
			setIR(Instruction.JPROGRAM);
		} else {
			Util.println("Loading bin...");
			loadBin(binFile);
		}
		jtag.resetState();
		Util.println("Done.",Theme.successTextColor);
	}
}
