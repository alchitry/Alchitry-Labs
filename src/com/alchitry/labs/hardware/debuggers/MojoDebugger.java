package com.alchitry.labs.hardware.debuggers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.usb4java.LibUsbException;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.RegisterInterface;
import com.alchitry.labs.hardware.usb.UsbUtil;
import com.alchitry.labs.widgets.WaveSignal;
import com.alchitry.labs.widgets.WaveSignal.TriggerType;

public class MojoDebugger extends Debugger {
	private static final int BASE_ADDR = 0xfffffff0;
	private static final int STATUS_ADDR = BASE_ADDR;
	private static final int DATA_ADDR = BASE_ADDR + 1;
	private static final int WIDTH_ADDR = BASE_ADDR + 2;
	private static final int DEPTH_ADDR = BASE_ADDR + 3;
	private static final int TRIGGER_INDEX_ADDR = BASE_ADDR + 4;
	private static final int TRIGGER_ENABLE_ADDR = BASE_ADDR + 5;
	private static final int NONCE_ADDR = BASE_ADDR + 14;
	private static final int VERSION_ADDR = BASE_ADDR + 15;
	
	private RegisterInterface regInt;

	public void updateDeviceInfo() {
		if (!regInt.isConnected())
			throw new RuntimeException("device must be connected before updating info");

		try {
			captureWidth = regInt.read(WIDTH_ADDR);
			captureDepth = regInt.read(DEPTH_ADDR);
			version = regInt.read(VERSION_ADDR);
			if (version == 2)
				nonce = (long) regInt.read(NONCE_ADDR) & 0xffffffffL;
		} catch (LibUsbException e) {
			Util.logException(e, "Failed to connect");
		}
	}

	public byte[][] capture(boolean withTriggers, AtomicBoolean armed) {
		updateDeviceInfo();

		byte[][] dataArray = new byte[captureWidth][captureDepth];

		// ARM
		regInt.write(STATUS_ADDR, withTriggers ? 0x01 : 0x02);

		// Wait for captured
		while ((regInt.read(STATUS_ADDR) & (1 << 2)) == 0)
			if (!armed.get())
				return null;

		for (int i = 0; i < captureDepth; i++) {
			long data = 0;
			for (int j = 0; j < captureWidth; j++) {
				if (j % 32 == 0)
					data = regInt.read(DATA_ADDR);
				dataArray[j][i] = (byte) ((data >> j % 32) & 1);
			}	
		}

		return dataArray;
	}

	public void updateTriggers(List<WaveSignal> signals) {
		if (!regInt.isConnected()) {
			Util.println("Failed to connect to Mojo!", true);
			return;
		}
		updateDeviceInfo();
		if (getNumBits(signals) != captureWidth) {
			Util.println("Signals provided do not match expected width!", true);
		}

		int i = 0;
		for (WaveSignal sig : signals) {
			for (int bit = 0; bit < sig.getWidth(); bit++) {
				regInt.write(TRIGGER_INDEX_ADDR, i);
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
				regInt.write(TRIGGER_ENABLE_ADDR, triggerValue);
				i++;
			}
		}
	}

	@Override
	public void init() {
		if (regInt != null)
			throw new RuntimeException("init has already been called");
		regInt = new RegisterInterface();
		if (!regInt.connect(UsbUtil.MOJO_DEVICES))
			throw new RuntimeException("Failed to connect to device");
	}

	@Override
	public void close() {
		if (!regInt.disconnect())
			throw new RuntimeException("Failed to disconnect from device");
	}
}
