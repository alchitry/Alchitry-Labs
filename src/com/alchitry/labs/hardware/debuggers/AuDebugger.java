package com.alchitry.labs.hardware.debuggers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.usb.UsbUtil;
import com.alchitry.labs.hardware.usb.ftdi.Ftdi;
import com.alchitry.labs.hardware.usb.ftdi.Jtag;
import com.alchitry.labs.hardware.usb.ftdi.JtagState;
import com.alchitry.labs.hardware.usb.ftdi.XilinxJtag.Instruction;
import com.alchitry.labs.hardware.usb.ftdi.enums.PortInterfaceType;
import com.alchitry.labs.widgets.WaveSignal;
import com.alchitry.labs.widgets.WaveSignal.TriggerType;

public class AuDebugger extends Debugger {
	private static final byte INFO_SCAN = Instruction.USER1.getCode();
	private static final byte CONFIG_SCAN = Instruction.USER2.getCode();
	private static final byte DATA_SCAN = Instruction.USER3.getCode();
	private static final byte CAPTURE_SCAN = Instruction.USER4.getCode();

	private Ftdi ftdi;
	private Jtag jtag;

	public AuDebugger() {

	}

	public void init() {
		ftdi = UsbUtil.openFtdiDevice(PortInterfaceType.INTERFACE_A, UsbUtil.AU_DEVICES);
		if (ftdi == null)
			throw new RuntimeException("Failed to connect to device!");
		jtag = new Jtag(ftdi);
		jtag.init();
		jtag.setFreq(30000000);
		jtag.resetState();
	}

	public void close() {
		ftdi.usbClose();
	}

	private void setIR(byte inst) {
		jtag.shiftIR(6, new byte[] { inst });
	}

	private int intFromBytes(byte[] b, int start) {
		int d = 0;
		for (int i = start; i < start + 4; i++)
			d |= ((((int) b[i]) & 0xff) << 8 * i);
		return d;
	}

	public void updateDeviceInfo() {
		setIR(INFO_SCAN);
		byte[] data = new byte[1 + 4 + 4 + 4];
		jtag.shiftDR(8 + 32 + 32 + 32, new byte[data.length], data);
		nonce = Integer.toUnsignedLong(intFromBytes(data, 0));
		captureWidth = intFromBytes(data, 4);
		captureDepth = intFromBytes(data, 8);
		version = data[data.length - 1];
		if (version != 3)
			throw new RuntimeException("Version "+version + " is invalid. Ensure that the .bin loaded was generated with the debugger of this version of Alchitry Labs.");
	}

	private boolean waitForCapture(boolean force, AtomicBoolean armed) {
		jtag.resetState();
		setIR(CAPTURE_SCAN);

		byte[] write = new byte[1];
		byte[] read = new byte[1];
		if (force)
			write[0] = (byte) 0xff;

		while (read[0] == 0) {
			if (armed != null && !armed.get())
				return false;
			jtag.navitageToState(JtagState.SHIFT_DR);
			jtag.shiftData(1, write, read);
		}
		jtag.navitageToState(JtagState.RUN_TEST_IDLE);
		return true;
	}

	private byte[] getData() {
		int bitCount = captureWidth * captureDepth;
		byte[] data = new byte[(int) Math.ceil((double) bitCount / 8)];
		byte[] dummy = new byte[data.length];
		setIR(DATA_SCAN);
		jtag.shiftDR(bitCount, dummy, data);
		return data;
	}

	@Override
	public long getNonce() {
		return nonce;
	}

	@Override
	public int getWidth() {
		return captureWidth;
	}

	@Override
	public int getDepth() {
		return captureDepth;
	}

	@Override
	public int getVersion() {
		return version;
	}

	private byte getBit(byte[] data, int bit) {
		byte d = data[bit / 8];
		if ((d & (1 << (bit % 8))) != 0)
			return 1;
		else
			return 0;
	}

	@Override
	public byte[][] capture(boolean withTriggers, AtomicBoolean armed) {
		updateDeviceInfo();
		byte[][] dataArray = new byte[captureWidth][captureDepth];
		if (!waitForCapture(!withTriggers, armed))
			return null;
		byte[] raw = getData();
		if (raw == null)
			throw new RuntimeException("Failed to read data from device!");
		for (int i = 0; i < captureDepth; i++) {
			for (int j = 0; j < captureWidth; j++) {
				dataArray[j][i] = getBit(raw, j + i * captureWidth);
			}
		}

		return dataArray;
	}

	private void writeTrigger(byte[] triggerData, int triggerValue, int index) {
		int bct = index / 2;
		int offset = index % 2;
		triggerData[bct] |= (triggerValue & 0x0F) << (4 * offset);
	}

	@Override
	public void updateTriggers(List<WaveSignal> signals) {
		updateDeviceInfo();
		if (getNumBits(signals) != captureWidth) {
			Util.println("Signals provided do not match expected width!", true);
		}
		int byteCt = captureWidth / 2 + captureWidth % 2;
		byte[] triggerData = new byte[byteCt];
		int i = 0;
		for (WaveSignal sig : signals) {
			for (int bit = 0; bit < sig.getWidth(); bit++) {
				int triggerValue = 0;
				for (TriggerType trig : sig.getTriggers(bit)) {
					switch (trig) {
					case RISING:
						triggerValue |= 1 << 0;
						break;
					case FALLING:
						triggerValue |= 1 << 1;
						break;
					case LOW:
						triggerValue |= 1 << 2;
						break;
					case HIGH:
						triggerValue |= 1 << 3;
					}
				}
				writeTrigger(triggerData, triggerValue, i++);
			}
		}

		setIR(CONFIG_SCAN);
		jtag.shiftDR(4 * captureWidth, triggerData);
	}
}
