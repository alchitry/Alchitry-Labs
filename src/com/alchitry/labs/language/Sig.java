package com.alchitry.labs.language;

import java.io.Serializable;

import org.antlr.v4.runtime.ParserRuleContext;

import com.alchitry.labs.Named;
import com.alchitry.labs.lucid.SignalWidth;

public class Sig implements Named, HasWidth, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5442400598563642610L;
	private String name;
	private SignalWidth width;
	transient private ParserRuleContext node;
	private boolean signed;
	private boolean fullyDriven;

	public Sig() {
		
	}
	
	public Sig(String name) {
		this(name, false);
	}

	public Sig(String name, ParserRuleContext node) {
		this(name, node, false);
	}

	public Sig(String name, int w) {
		this(name, w, false);
	}

	public Sig(String name, boolean signed) {
		this.name = name;
		this.signed = signed;
	}

	public Sig(String name, ParserRuleContext node, boolean signed) {
		this.name = name;
		this.node = node;
		this.signed = signed;
	}

	public Sig(String name, int w, boolean signed) {
		this.name = name;
		setWidth(new SignalWidth(w));
		this.signed = signed;
	}

	public Sig(Sig s) {
		name = s.name;
		if (s.width != null)
			width = new SignalWidth(s.width);
		node = s.node;
		signed = s.signed;
	}

	@Override
	public void setWidth(SignalWidth w) {
		width = w;
	}

	@Override
	public SignalWidth getWidth() {
		return width;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setFullyDriven(boolean fullyDriven) {
		this.fullyDriven = fullyDriven;
	}

	public boolean isFullyDriven() {
		return fullyDriven;
	}

	public void setNode(ParserRuleContext ctx) {
		node = ctx;
	}

	public ParserRuleContext getNode() {
		return node;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if (width != null)
			return width.toString();
		return "";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Sig) {
			Sig d = (Sig) o;
			if (d.getName().equals(name))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
