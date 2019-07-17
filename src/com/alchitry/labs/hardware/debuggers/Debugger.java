package com.alchitry.labs.hardware.debuggers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alchitry.labs.widgets.WaveSignal;

public abstract class Debugger {
	protected int captureWidth = -1;
	protected int captureDepth = -1;
	protected int version = -1;
	protected long nonce = -1;
	
	public abstract void init();
	public abstract void close();
	public abstract void updateDeviceInfo();
	public abstract byte[][] capture(boolean withTriggers, AtomicBoolean armed);
	public abstract void updateTriggers(List<WaveSignal> signals);
	
	protected int getNumBits(List<WaveSignal> signals) {
		int bits = 0;
		for (WaveSignal s : signals)
			bits += s.getWidth();
		return bits;
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
}
