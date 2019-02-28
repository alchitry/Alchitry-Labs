package com.alchitry.labs.parsers.lucid;

import java.util.ArrayList;

import com.alchitry.labs.parsers.types.Connection;

public class AssignBlock {
	public ArrayList<Connection> connections = new ArrayList<Connection>();
	public boolean instCon;

	public AssignBlock() {
		this(false);
	}

	public AssignBlock(boolean isInst) {
		instCon = isInst;
	}
}
