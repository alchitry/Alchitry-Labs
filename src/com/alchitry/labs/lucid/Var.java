package com.alchitry.labs.lucid;

import com.alchitry.labs.Named;

public class Var implements Named {
	private String name;
	private SignalWidth width;

	public Var(String name) {
		this.name = name;
	}

	public void setWidth(SignalWidth w) {
		width = w;
	}

	public SignalWidth getWidth() {
		return width;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o.getClass() != Var.class)
			return false;
		Var d = (Var) o;
		if (d.getName().equals(name))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
