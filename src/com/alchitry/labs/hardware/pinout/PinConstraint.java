package com.alchitry.labs.hardware.pinout;

public class PinConstraint {
	private boolean isClock;
	private float frequency;
	private String pin;
	private String signal;

	public PinConstraint() {
	}

	public PinConstraint(String pin, String signal, boolean isClock, float frequency) {
		this.pin = pin;
		this.signal = signal;
		this.isClock = isClock;
		this.frequency = frequency;
	}
	
	public PinConstraint(PinConstraint pc) {
		pin = pc.pin;
		signal = pc.signal;
		frequency = pc.frequency;
		isClock = pc.isClock;
	}
	
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public String getPin() {
		return pin;
	}
	
	public void setSignal(String signal) {
		this.signal = signal;
	}
	
	public String getSignal() {
		return signal;
	}
	
	public void setIsClock(boolean isClock) {
		this.isClock = isClock;
	}
	
	public boolean isClock() {
		return isClock;
	}
	
	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}
	
	public float getFrequency() {
		return frequency;
	}

}
