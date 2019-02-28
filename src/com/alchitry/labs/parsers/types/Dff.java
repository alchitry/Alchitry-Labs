package com.alchitry.labs.parsers.types;

import java.io.Serializable;

import com.alchitry.labs.Named;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.HasWidth;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprContext;

public class Dff implements SyncLogic, Named, HasWidth, Serializable {
	private static final long serialVersionUID = -6529861040352194596L;
	private String name;
	transient private ExprContext clk;
	transient private ExprContext rst;
	private ConstValue init;
	private SignalWidth width;
	private boolean signed;
	private boolean iob;

	public Dff() {
	}

	public Dff(String name) {
		this.name = name;
		init = new ConstValue(0);
	}

	@Override
	public void setWidth(SignalWidth w) {
		width = w;
	}

	@Override
	public SignalWidth getWidth() {
		return width;
	}

	public String getWidthString() {
		if (width != null)
			return width.toString();
		return "";
	}

	public void setIOB(boolean b) {
		iob = b;
	}

	public boolean isIOB() {
		return iob;
	}

	public void setClk(ExprContext c) {
		clk = c;
	}

	public void setRst(ExprContext r) {
		rst = r;
	}

	public void setInit(ConstValue i) {
		init = i;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ExprContext getClk() {
		return clk;
	}

	public ExprContext getRst() {
		return rst;
	}

	public ConstValue getInit() {
		return init;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o.getClass() != Dff.class)
			return false;
		Dff d = (Dff) o;
		if (d.getName().equals(name))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
