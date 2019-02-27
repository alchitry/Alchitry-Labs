package com.alchitry.labs.project;

import java.io.Serializable;
import java.util.ArrayList;

import com.alchitry.labs.parsers.ProjectSignal;

public class DebugInfo implements Serializable {
	private static final long serialVersionUID = -1858662763771188844L;
	private ArrayList<ProjectSignal> signals;
	private long nonce;
	
	public DebugInfo() {
		
	}
	
	public DebugInfo(ArrayList<ProjectSignal> debugSignals, long nonce) {
		signals = debugSignals;
		this.nonce = nonce;
	}
	
	public ArrayList<ProjectSignal> getSignals() {
		return signals;
	}
	
	public void setSignals(ArrayList<ProjectSignal> ds){
		signals = ds;
	}
	
	public long getNonce() {
		return nonce;
	}
	
	public void setNonce(long n) {
		nonce = n;
	}
}
