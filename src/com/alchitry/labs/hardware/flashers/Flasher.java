package com.alchitry.labs.hardware.flashers;

public abstract class Flasher {
	protected Thread thread;
	
	public Flasher() {
		
	}
	
	public boolean isFlashing() {
		return thread != null && thread.isAlive();
	}
	
	public abstract void flash();
}
