package com.alchitry.labs.parsers.types;

import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.hardware.pinout.PinConverter;

public class PinConstraint {
	private String pin;
	private String port;
	private int portBit;
	private boolean isMultiBit;
	private boolean pullUp;
	private boolean pullDown;

	public PinConstraint(String pin, String port) {
		this(pin, port, 0, false, false, false);
	}

	public PinConstraint(String pin, String port, int portBit, boolean isMultiBit, boolean pullUp, boolean pullDown) {
		this.pin = pin;
		this.port = port;
		this.portBit = portBit;
		this.isMultiBit = isMultiBit;
		this.pullUp = pullUp;
		this.pullDown = pullDown;
	}
	
	public String getPin() {
		return pin;
	}
	
	public String getPort() {
		return port;
	}
	
	public String getBoardConstraint(Board board) {
		StringBuilder sb = new StringBuilder();
		PinConverter pc = board.getPinConverter();
		
		String portName = port + (isMultiBit ? ("[" + portBit + "]") : "");
		sb.append("# ").append(portName).append(" => ").append(pin).append(System.lineSeparator());
		
		if (board.isType(Board.AU)) {
			sb.append("set_property PACKAGE_PIN ").append(pc.getFPGAPin(pin)).append(" [get_ports {").append(portName).append("}]").append(System.lineSeparator());
			sb.append("set_property IOSTANDARD LVCMOS33 [get_ports {").append(portName).append("}]").append(System.lineSeparator());
			if (pullUp)
				sb.append("set_property PULLUP true [get_ports {").append(portName).append("}]").append(System.lineSeparator());
			if (pullDown)
				sb.append("set_property PULLDOWN true [get_ports {").append(portName).append("}]").append(System.lineSeparator());
		} else if (board.isType(Board.CU)) {
			sb.append("set_io ").append(portName).append(" ").append(pc.getFPGAPin(pin));
			if (pullUp)
				sb.append(" -pullup yes");
			sb.append(System.lineSeparator());
		}
		
		return sb.toString();
	}
}
