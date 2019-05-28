package com.alchitry.labs.parsers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;

import com.alchitry.labs.Named;
import com.alchitry.labs.Util;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.tools.lucid.BitWidthChecker;
import com.alchitry.labs.parsers.tools.lucid.ConstExprParser;
import com.alchitry.labs.parsers.tools.lucid.ConstProvider;
import com.alchitry.labs.parsers.tools.lucid.WidthProvider;
import com.alchitry.labs.parsers.types.Connection;
import com.alchitry.labs.parsers.types.Dff;
import com.alchitry.labs.parsers.types.Fsm;

public class InstModule implements Named, Serializable {
	private static final long serialVersionUID = 3100960916372359031L;
	private Module type;
	private String name;
	private ArrayList<String> width;
	private ArrayList<Param> params;
	private ArrayList<Connection> connections;
	transient private ParserRuleContext declarationNode;
	private ArrayList<InstModule> children;
	private SignalWidth moduleWidth;
	private boolean isArray;
	private ArrayList<Dff> dffs;
	private ArrayList<Fsm> fsms;
	private ArrayList<Sig> sigs;
	private ArrayList<Sig> drivenSigs;

	public InstModule() {
	}

	public InstModule(String name, Module type, ParserRuleContext node) {
		this.name = name;
		this.type = type;
		declarationNode = node;
		width = new ArrayList<String>();
		params = new ArrayList<>();
		connections = new ArrayList<>();
		children = new ArrayList<>();
	}

	@Override
	public String getName() {
		return name;
	}

	public Module getType() {
		return type;
	}

	public ParserRuleContext getNode() {
		return declarationNode;
	}

	public void addWidth(String w) {
		width.add(w);
	}

	public ArrayList<String> getWidths() {
		return width;
	}

	public void addChild(InstModule im) {
		children.add(im);
	}

	public List<InstModule> getChildren() {
		return children;
	}

	public void resetChildren() {
		children.clear();
	}

	public String getWidthText() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		if (width.size() > 0) {
			boolean first = true;
			for (int i = 0; i < width.size(); i++) {
				if (!first)
					sb.append("*");
				sb.append("(");
				sb.append(width.get(i));
				sb.append(")");
				first = false;
			}
		} else {
			sb.append("1");
		}
		sb.append(")");
		return sb.toString();
	}

	public void addParams(Collection<Param> p) {
		for (Param param : p)
			params.add(new Param(param));
	}

	public List<Param> getParams() {
		return params;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void addConnection(Connection c) {
		connections.add(c);
	}

	public int paramHash() {
		return name.hashCode() | params.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.getName());
		if (params != null && params.size() > 0) {
			sb.append(" #(");
			boolean first = true;
			for (Param p : params) {
				if (!first)
					sb.append(", ");
				else
					first = false;

				sb.append(p.toString());
			}
			sb.append(")");
		}
		return sb.toString();

	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o.getClass() != InstModule.class)
			return false;
		InstModule im = (InstModule) o;
		if (im.type.equals(type)) {
			for (Param p : im.getParams()) {
				int i = params.indexOf(p);
				if (i < 0)
					return false;

				if (!params.get(i).equalsValue(p))
					return false;

			}
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return type.hashCode() ^ params.hashCode();
	}

	public boolean isPrimitive() {
		return type.getPrimitive() != null;
	}

	public boolean isLucid() {
		if (type.getFile() != null)
			return type.getFile().getName().endsWith(".luc");
		return false;
	}

	public boolean isVerilog() {
		if (type.getFile() != null)
			return type.getFile().getName().endsWith(".v");
		return false;
	}

	public static boolean containsType(List<InstModule> list, Module type) {
		for (InstModule im : list) {
			if (im.getType().equals(type)) {
				return true;
			}
		}
		return false;
	}

	public String treeToString() {
		StringBuilder sb = new StringBuilder();
		printTree(sb, 0);
		return sb.toString();
	}

	private void printTree(StringBuilder sb, int indent) {
		String indentString = StringUtils.repeat(' ', indent * 2);
		sb.append(indentString);
		sb.append(type.getName() + ": " + name);
		indentString += " ";
		if (sigs != null)
			for (Sig s : sigs)
				sb.append("\n").append(indentString).append(s.getName()).append(" ").append(s);
		if (dffs != null)
			for (Dff d : dffs)
				sb.append("\n").append(indentString).append(d.getName()).append(" ").append(d.getWidthString());
		if (fsms != null)
			for (Fsm f : fsms)
				sb.append("\n").append(indentString).append(f.getName()).append(" ").append(f.getWidthString());
		for (InstModule im : children) {
			sb.append("\n");
			im.printTree(sb, indent + 1);
		}
	}

	public void setIsArray(boolean isArray) {
		this.isArray = isArray;
	}

	public boolean isArray() {
		return isArray;
	}

	public SignalWidth getModuleWidth() {
		return moduleWidth;
	}

	public void setModuleWidth(SignalWidth moduleWidth) {
		this.moduleWidth = moduleWidth;
	}

	transient private ConstProvider cp = new ConstProvider() {
		@Override
		public ConstValue getValue(String s) {
			Param p = Util.getByName(params, s);
			if (p != null)
				return p.getValue();
			return null;
		}
	};

	transient private WidthProvider wp = new WidthProvider() {
		@Override
		public SignalWidth getWidth(String signal) {
			Param p = Util.getByName(params, signal);
			if (p != null && p.getValue() != null)
				return p.getValue().getArrayWidth();
			return null;
		}

		@Override
		public SignalWidth getWidth(ParserRuleContext ctx) {
			return null;
		}

		@Override
		public SignalWidth checkWidthMap(String signal) {
			return getWidth(signal);
		}
	};

	public boolean checkParameterConstraint(Param p) {
		if (p.getConstraint() != null) {
			ConstValue cv = ConstExprParser.parseExpr(p.getConstraint(), cp, wp);
			if (cv != null)
				return !cv.isZero();
			return false;
		}
		return true;
	}

	public List<Dff> getDffs() {
		return dffs;
	}

	public void setDffs(ArrayList<Dff> dffs) {
		this.dffs = dffs;
	}

	public List<Fsm> getFsms() {
		return fsms;
	}

	public void setFsms(ArrayList<Fsm> fsms) {
		this.fsms = fsms;
	}

	public List<Sig> getSigs() {
		return sigs;
	}

	public void setSigs(ArrayList<Sig> sigs) {
		this.sigs = sigs;
	}
	
	public List<Sig> getDrivenSigs() {
		return drivenSigs;
	}

	public void setDrivenSigs(ArrayList<Sig> drivenSigs) {
		this.drivenSigs = drivenSigs;
	}
	
	public ArrayList<Sig> getOutputs() {
		return convertPortList(type.getOutputs());
	}
	
	public ArrayList<Sig> getInputs() {
		return convertPortList(type.getInputs());
	}
	
	public ArrayList<Sig> getInouts() {
		return convertPortList(type.getInouts());
	}
	
	private ArrayList<Sig> convertPortList(List<Sig> list) {
		ArrayList<Sig> newList = new ArrayList<>(list.size());
		for (Sig s : list)
			newList.add(new Sig(s));
		for (Sig s : newList){
			SignalWidth sw = BitWidthChecker.convertToFixed(s.getWidth(), cp, ConstExprParser.globalConstProvider);
			if (sw != null)
				s.setWidth(sw);
		}
		return newList;
	}
}
