package com.alchitry.labs.lucid;

import java.util.ArrayList;

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
