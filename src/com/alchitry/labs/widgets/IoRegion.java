package com.alchitry.labs.widgets;

public class IoRegion extends Region {
	public final String name;
	public String[] signals;

	public IoRegion(String name, int id, double x, double y, double width, double height) {
		super(id, x, y, width, height);
		this.name = name;
	}

}
