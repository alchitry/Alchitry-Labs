package com.alchitry.labs.parsers;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.alchitry.labs.parsers.lucid.Dff;
import com.alchitry.labs.parsers.lucid.Fsm;

public class ProjectSignal implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7683656137652269631L;
	private ArrayList<InstModule> path;
	private Dff dff;
	private Fsm fsm;
	private Sig sig;
	private InstModule parent;

	public ProjectSignal() {
		path = new ArrayList<>();
	}

	public void addParent(InstModule parent) {
		path.add(0, parent);
	}

	public void append(InstModule child) {
		path.add(child);
	}

	public void addAll(Collection<InstModule> subPath) {
		path.addAll(subPath);
	}

	public void setPath(ArrayList<InstModule> path) {
		this.path = path;
	}

	public ArrayList<InstModule> getPath() {
		return path;
	}

	public Dff getDff() {
		return dff;
	}

	public void setDff(Dff dff) {
		this.dff = dff;
	}

	public Fsm getFsm() {
		return fsm;
	}

	public void setFsm(Fsm fsm) {
		this.fsm = fsm;
	}

	public Sig getSig() {
		return sig;
	}

	public void setSig(Sig sig) {
		this.sig = sig;
	}

	public InstModule getParent() {
		return parent;
	}

	public void setParent(InstModule parent) {
		this.parent = parent;
	}

	public String getName() {
		if (dff != null)
			return dff.getName() + ".q";
		if (fsm != null)
			return fsm.getName() + ".q";
		if (sig != null)
			return sig.getName();
		return "";
	}

	public void set(Object o) {
		if (o instanceof Dff)
			setDff((Dff) o);
		else if (o instanceof Fsm)
			setFsm((Fsm) o);
		else if (o instanceof Sig)
			setSig((Sig) o);
		else if (o instanceof ProjectSignal) {
			setSig(((ProjectSignal) o).getSig());
			setParent(((ProjectSignal) o).getParent());
		} else
			throw new InvalidParameterException("Set can only be used with Dff, Fsm, Sigs, or ProjectSignals.");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (InstModule im : path) {
			sb.append(im.getName()).append(".");
		}
		if (dff != null)
			sb.append(dff.getName()).append(".q");
		if (fsm != null)
			sb.append(fsm.getName()).append(".q");
		if (sig != null)
			sb.append(sig.getName());

		return sb.toString();
	}

	public int getTotalWidth() {
		if (dff != null)
			return dff.getWidth().getTotalWidth();
		if (fsm != null)
			return fsm.getWidth().getTotalWidth();
		if (sig != null)
			return sig.getWidth().getTotalWidth();
		return -1;
	}

	public boolean equalIngorePath(Object o) {
		if (o instanceof ProjectSignal) {
			ProjectSignal ps = (ProjectSignal) o;
			if (Objects.equals(getSig(), ps.getSig()) && Objects.equals(getFsm(), ps.getFsm()) && Objects.equals(getDff(), ps.getDff())
					&& Objects.equals(getParent(), ps.getParent()))
				return true;
		}
		return false;
	}

	public boolean isSigned() {
		if (dff != null)
			return dff.isSigned();
		if (fsm != null)
			return false;
		if (sig != null)
			return sig.isSigned();
		return false;
	}
}
