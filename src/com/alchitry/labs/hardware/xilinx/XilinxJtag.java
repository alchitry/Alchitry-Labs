package com.alchitry.labs.hardware.xilinx;

import java.io.File;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.ftdi.Ftdi;
import com.alchitry.labs.hardware.ftdi.Jtag;
import com.alchitry.labs.hardware.ftdi.JtagState;

public class XilinxJtag {
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

	private void shiftDR(int bits, byte[] write, byte[] read, byte[] mask) {
		jtag.navitageToState(JtagState.SHIFT_DR);
		jtag.shiftData(bits, write, read, mask);
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
	}

	private void shiftIR(int bits, byte[] write, byte[] read, byte[] mask) {
		jtag.navitageToState(JtagState.SHIFT_IR);
		jtag.shiftData(bits, write, read, mask);
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
	}

	public void checkIDCODE() {
		ftdi.usbPurgeBuffers();
		setIR(Instruction.IDCODE);
		shiftDR(32, new byte[4], Util.stringToByte("0362D093"), Util.stringToByte("0FFFFFFF"));
	}
	
	public void loadBin(File bin) {
		
	}
}
