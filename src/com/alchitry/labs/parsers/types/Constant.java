package com.alchitry.labs.parsers.types;

import com.alchitry.labs.Named;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.lucid.SignalWidth;

import java.io.Serial;
import java.io.Serializable;

public class Constant implements Named, Serializable {
	@Serial
	private static final long serialVersionUID = 376452255402037480L;
	private final String name;
	private final ConstValue value;
	private SignalWidth width;

	public Constant(String name) {
		this(name, null);
	}

	public Constant(String name, ConstValue value) {
		this.name = name;
		this.value = value;
	}
	
	public void setWidth(SignalWidth w){
		width = w;
	}
	
	public SignalWidth getWidth() {
		return width;
	}

	public String getName() {
		return name;
	}

	public ConstValue getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return name + " = " + value.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o.getClass() != Constant.class)
			return false;
		Constant c = (Constant) o;
		return c.getName().equals(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
