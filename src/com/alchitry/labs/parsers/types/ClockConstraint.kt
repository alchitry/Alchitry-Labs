package com.alchitry.labs.parsers.types;

import com.alchitry.labs.hardware.boards.Board;

public class ClockConstraint {
	private String port;
	private int portBit;
	private boolean isMultiBit;
	private int frequency;

	public ClockConstraint(String port, int frequency) {
		this(port, 0, false, frequency);
	}

	public ClockConstraint(String port, int portBit, boolean isMultiBit, int frequency) {
		this.port = port;
		this.portBit = portBit;
		this.isMultiBit = isMultiBit;
		this.frequency = frequency;
	}

	public String getBoardConstraint(Board board) {
		StringBuilder sb = new StringBuilder();

		String portName = port + (isMultiBit ? ("[" + portBit + "]") : "");
		sb.append("# ").append(portName).append(" => ").append(frequency).append("Hz").append(System.lineSeparator());
		float nsPeriod = 1000000000.0f / frequency;

		if (board.isType(Board.AU)) {
			sb.append("create_clock -period ").append(nsPeriod).append(" -name ").append(port).append("_").append(portBit).append(" -waveform {0.000 5.000} [get_ports ")
					.append(portName).append("]").append(System.lineSeparator());
		} else if (board.isType(Board.CU)) {
			sb.append("create_clock -name ").append(port).append("_").append(portBit).append(" -period ").append(Float.toString(nsPeriod)).append(" [get_ports ").append(portName).append("]")
					.append(System.lineSeparator());
		}

		return sb.toString();
	}

}
