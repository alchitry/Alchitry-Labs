package com.alchitry.labs.parsers.lucid;

import java.io.Serializable;

import com.alchitry.labs.Named;
import com.alchitry.labs.parsers.ConstValue;

public class Constant implements Named, Serializable {
	private static final long serialVersionUID = 376452255402037480L;
	private String name;
	private ConstValue value;
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
		if (c.getName().equals(name))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
