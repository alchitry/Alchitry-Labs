package com.alchitry.labs.parsers;

import java.io.Serializable;
import java.util.List;

import com.alchitry.labs.Named;

public class Param implements Named, Serializable {
	private static final long serialVersionUID = 7611006679165234644L;
	private String name;
	private String defaultValue;
	private String curValue;
	private ConstValue curCV;
	private String constraint;

	public Param(String name) {
		this(name, null, null);
	}

	public Param(String name, String defValue) {
		this(name, defValue, null);
	}

	public Param(String name, String defValue, ConstValue cur) {
		this.name = name;
		defaultValue = defValue;
		curCV = cur;
	}

	public Param(Param param) {
		name = param.name;
		defaultValue = param.defaultValue;
		curValue = param.curValue;
		if (param.curCV != null)
			curCV = new ConstValue(param.curCV);
		constraint = param.constraint;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setConstraint(String c) {
		constraint = c;
	}

	public String getConstraint() {
		return constraint;
	}

	public String getStringValue() {
		if (curValue != null)
			return curValue;
		else
			return defaultValue;
	}

	public void setValue(String value) {
		curValue = value;
	}

	public boolean valueSet() {
		if (curValue != null || curCV != null)
			return true;
		return false;
	}

	public String getDefValue() {
		return defaultValue;
	}
	
	public void setDefault(String def){
		defaultValue = def;
	}

	public void setValue(ConstValue i) {
		curCV = i;
	}

	public ConstValue getValue() {
		if (valueSet())
			return curCV;
		return null;
	}

	public static String replaceParam(String in, List<Param> list) {
		for (Param p : list) {
			if (in.contains(p.getName())) {
				in = in.replaceAll(p.getName(), "(" + p.getStringValue() + ")");
			}
		}

		return in;
	}

	public boolean equalsValue(Param p) {
		if (equals(p)) {
			if (curCV != null)
				return curCV.equals(p.curCV);
			
			if (getStringValue() == null)
				return p.getStringValue() == null;

			return getStringValue().equals(p.getStringValue());
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o.getClass() != Param.class)
			return false;
		Param p = (Param) o;
		if (p.getName().equals(name))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" { defValue : ");
		sb.append(defaultValue).append(", curValue : ");
		sb.append(curValue);
		sb.append(", curCV : ").append(curCV).append(" }");
		return sb.toString();
	}
}
