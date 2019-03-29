package com.alchitry.labs.hardware;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import com.alchitry.labs.Util;
import com.alchitry.labs.widgets.WaveSignal;
import com.alchitry.labs.widgets.WaveSignal.TriggerType;
import com.fazecast.jSerialComm.SerialPortIOException;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

public class LogicCapture extends RegisterInterface {
	private static final int BASE_ADDR = 0xfffffff0;
	private static final int STATUS_ADDR = BASE_ADDR;
	private static final int DATA_ADDR = BASE_ADDR + 1;
	private static final int WIDTH_ADDR = BASE_ADDR + 2;
	private static final int DEPTH_ADDR = BASE_ADDR + 3;
	private static final int TRIGGER_INDEX_ADDR = BASE_ADDR + 4;
	private static final int TRIGGER_ENABLE_ADDR = BASE_ADDR + 5;
	private static final int NONCE_ADDR = BASE_ADDR + 14;
	private static final int VERSION_ADDR = BASE_ADDR + 15;
	private int captureWidth = -1;
	private int captureDepth = -1;
	private int version = -1;
	private long nonce = -1;

	public boolean updateDeviceInfo() {
		if (!isConnected())
			return false;

		try {
			captureWidth = read(WIDTH_ADDR);
			captureDepth = read(DEPTH_ADDR);
			version = read(VERSION_ADDR);
			if (version == 2)
				nonce = (long) read(NONCE_ADDR) & 0xffffffffL;
		} catch (SerialPortIOException e) {
			Util.log.log(Level.SEVERE, "Failed to connect", e);
			return false;
		} catch (SerialPortTimeoutException e) {
			Util.log.log(Level.SEVERE, "Failed to connect", e);
			return false;
		}
		return true;
	}

	public long getNonce() {
		return nonce;
	}

	public int getWidth() {
		return captureWidth;
	}

	public int getDepth() {
		return captureDepth;
	}

	public int getVersion() {
		return version;
	}

	public byte[][] capture(boolean withTriggers, AtomicBoolean armed) throws SerialPortIOException, SerialPortTimeoutException {
		if (!updateDeviceInfo())
			return null;
		byte[][] dataArray = new byte[captureWidth][captureDepth];

		// ARM
		write(STATUS_ADDR, withTriggers ? 0x01 : 0x02);

		// Wait for captured
		while ((read(STATUS_ADDR) & (1 << 2)) == 0)
			if (!armed.get())
				return null;

		for (int i = 0; i < captureDepth; i++) {
			long data = 0;
			for (int j = 0; j < captureWidth; j++) {
				if (j % 32 == 0)
					data = read(DATA_ADDR);
				dataArray[j][i] = (byte) ((data >> j % 32) & 1);
			}
		}

		return dataArray;
	}
	
	private int getNumBits(List<WaveSignal> signals){
		int bits = 0;
		for (WaveSignal s : signals)
			bits += s.getWidth();
		return bits;
	}

	public boolean updateTriggers(List<WaveSignal> signals) throws SerialPortIOException {
		if (!isConnected()) {
			Util.log.log(Level.WARNING, "Failed to connect to Mojo!");
			return false;
		}
		if (!updateDeviceInfo()) {
			Util.log.log(Level.WARNING, "Failed to update capture width and depth info!");
			return false;
		}
		if (getNumBits(signals) != captureWidth) {
			Util.log.log(Level.WARNING, "Signals provided do not match expected width!");
			return false;
		}

		int i = 0;
		for (WaveSignal sig : signals) {
			for (int bit = 0; bit < sig.getWidth(); bit++) {
				write(TRIGGER_INDEX_ADDR, i);
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
				write(TRIGGER_ENABLE_ADDR, triggerValue);
				i++;
			}
		}

		return true;
	}
}
