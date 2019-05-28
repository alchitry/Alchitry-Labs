package com.alchitry.labs.parsers;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import com.alchitry.labs.Named;
import com.alchitry.labs.project.Primitive;

public class Module implements Named, Serializable {
	private static final long serialVersionUID = 2992584574513063431L;
	private ArrayList<Sig> inputs;
	private ArrayList<Sig> outputs;
	private ArrayList<Sig> inouts;
	private ArrayList<Param> params;
	private String name;
	private File file;
	private Primitive primitive;
	private boolean ngc;

	public Module(String name) {
		this();
		setName(name);
	}

	public Module() {
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
		inouts = new ArrayList<>();
		params = new ArrayList<>();
	}
	
	public Module(Module m){
		inputs = new ArrayList<>(m.inputs.size());
		for (Sig s : m.inputs)
			inputs.add(new Sig(s));
		outputs = new ArrayList<>(m.outputs.size());
		for (Sig s : m.outputs)
			outputs.add(new Sig(s));
		inouts = new ArrayList<>(m.inouts.size());
		for (Sig s : m.inouts)
			inouts.add(new Sig(s));
		params = new ArrayList<>(m.params.size());
		for (Param p : m.params)
			params.add(new Param(p));
		name = m.name;
		file = m.file;
	}
	
	public void setNgc(boolean ngc) {
		this.ngc = ngc;
	}
	
	public boolean isNgc() {
		return ngc;
	}
	
	public void setPrimitive(Primitive p){
		primitive = p;
	}
	
	public Primitive getPrimitive() {
		return primitive;
	}
	
	public boolean isPrimitive() {
		return primitive != null;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}


	public void setFile(File f) {
		file = f;
	}

	public boolean hasSignal(Sig s) {
		return inputs.contains(s) || outputs.contains(s) || inouts.contains(s);
	}

	public ArrayList<Sig> getInputs() {
		return inputs;
	}

	public ArrayList<Sig> getOutputs() {
		return outputs;
	}

	public ArrayList<Sig> getInouts() {
		return inouts;
	}

	public ArrayList<Param> getParams() {
		return params;
	}

	public void addInput(Sig in) {
		inputs.add(in);
	}

	public void addOutput(Sig out) {
		outputs.add(out);
	}

	public void addInout(Sig in) {
		inouts.add(in);
	}

	public void addParam(Param p) {
		params.add(p);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String nl = System.lineSeparator();

		sb.append("Name: ").append(nl + "  ").append(name).append(nl);

		sb.append("Params: ");
		for (Param p : params)
			sb.append(nl + "  ").append(p.getName()).append(" = ").append(p.getDefValue());
		sb.append(nl);

		sb.append("Inputs: ");
		for (Sig s : inputs)
			sb.append(nl + "  ").append(s.getName()).append(s);
		sb.append(nl);

		sb.append("Outputs: ");
		for (Sig s : outputs)
			sb.append(nl + "  ").append(s.getName()).append(s);
		sb.append(nl);

		sb.append("Inouts: ");
		for (Sig s : inouts)
			sb.append(nl + "  ").append(s.getName()).append(s);

		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o.getClass() != Module.class)
			return false;
		Module m = (Module) o;
		if (m.getName() == null)
			return false;
		if (m.getName().equals(name))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
