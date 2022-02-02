package com.alchitry.labs.parsers.types;

import com.alchitry.labs.Named;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.HasWidth;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.lucid.parser.LucidParser.ExprContext;

import java.io.Serializable;
import java.util.ArrayList;

public class Fsm implements SyncLogic, Named, HasWidth, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7967153282393432822L;
	private final String name;
	private final ArrayList<Constant> states;
	private Constant defState;
	transient private ExprContext clk;
	transient private ExprContext rst;
	private SignalWidth width;

	public Fsm(String name) {
		this.name = name;
		states = new ArrayList<Constant>();
		defState = null;
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

	public void setClk(ExprContext c) {
		clk = c;
	}

	public void setRst(ExprContext r) {
		rst = r;
	}

	public void addState(Constant state) {
		states.add(state);
	}

	public void setDefState(Constant state) {
		defState = state;
	}

	public boolean contains(String state) {
		return states.contains((Object)state);
	}
	
	public int getNumStates() {
		return states.size();
	}

	@Override
	public String getName() {
		return name;
	}

	public Constant getDefState() {
		return defState;
	}

	public ConstValue getInit() {
		return new ConstValue(states.indexOf(defState));
	}

	public ArrayList<Constant> getStates() {
		return states;
	}

	public boolean hasState(Constant state) {
		return states.contains(state);
	}

	public ExprContext getClk() {
		return clk;
	}

	public ExprContext getRst() {
		return rst;
	}
	
	

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o.getClass() != Fsm.class)
			return false;
		Fsm f = (Fsm) o;
		return f.getName().equals(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
