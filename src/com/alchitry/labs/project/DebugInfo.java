package com.alchitry.labs.project;

import java.io.Serializable;
import java.util.ArrayList;

import com.alchitry.labs.parsers.ProjectSignal;

public class DebugInfo implements Serializable {
	private static final long serialVersionUID = -1858662763771188884L;
	private ArrayList<ProjectSignal> signals;
	private ProjectSignal clock;
	private long nonce;
	private int samples;

	public DebugInfo() {
		signals = new ArrayList<>();
		nonce = (long) (Math.random() * 0xffffffffL);
	}

	public DebugInfo(ArrayList<ProjectSignal> debugSignals, ProjectSignal clock, long nonce, int samples) {
		signals = debugSignals;
		this.nonce = nonce;
		this.samples = samples;
		this.clock = clock;
	}

	public ArrayList<ProjectSignal> getSignals() {
		return signals;
	}

	public void setSignals(ArrayList<ProjectSignal> ds) {
		signals = ds;
	}
	
	public ProjectSignal getClock() {
		return clock;
	}
	
	public void setClock(ProjectSignal clock) {
		this.clock = clock;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long n) {
		nonce = n;
	}

	public int getSamples() {
		return samples;
	}

	public void setSamples(int samples) {
		this.samples = samples;
	}
}
