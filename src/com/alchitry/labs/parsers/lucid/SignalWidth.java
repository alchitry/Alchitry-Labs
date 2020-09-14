package com.alchitry.labs.parsers.lucid;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.types.Struct;

public class SignalWidth implements Serializable {
	private static final long serialVersionUID = -3098905378574513053L;
	private Struct struct;
	private ArrayList<Integer> widths;
	private SignalWidth next;
	private String text;

	/* Creates a new SignalWidth and assumes it is an array. */
	public SignalWidth() {
		widths = new ArrayList<>();
	}

	public SignalWidth(Struct s) {
		struct = s;
	}

	public SignalWidth(int w) {
		widths = new ArrayList<>();
		widths.add(w);
	}

	public SignalWidth(int... ws) {
		widths = new ArrayList<>();
		for (int w : ws)
			widths.add(w);
	}

	public SignalWidth(Collection<Integer> c) {
		widths = new ArrayList<>(c);
	}

	public SignalWidth(SignalWidth w) {
		this(w, true);
	}

	public SignalWidth(SignalWidth w, boolean deep) {
		set(w, deep);
	}

	public SignalWidth(String t) {
		text = t;
	}

	public SignalWidth(ConstValue cv) {
		if (cv.isStruct())
			struct = cv.getStruct();
		else
			widths = cv.getWidths();
	}

	@Override
	public SignalWidth clone() {
		SignalWidth w;
		if (struct != null) {
			w = new SignalWidth(struct);
		} else if (widths != null) {
			w = new SignalWidth(widths);
		} else {
			w = new SignalWidth(text);
		}

		if (next != null)
			w.next = next.clone();

		return w;
	}

	public void set(Struct s) {
		struct = s;
		widths = null;
		text = null;
	}

	public void set(SignalWidth w) {
		set(w, true);
	}

	public void set(int i) {
		struct = null;
		widths = new ArrayList<>();
		widths.add(i);
		text = null;
	}

	public void set(SignalWidth w, boolean deep) {
		struct = null;
		widths = null;
		next = null;
		text = null;
		if (w.struct != null) {
			struct = w.struct;
		} else if (w.widths != null) {
			widths = new ArrayList<>(w.widths);
		} else {
			text = w.text;
		}

		if (deep && w.next != null)
			next = w.next.clone();
	}

	// Returns the ith dimension offset from this width
	// Note that child dimensions are not necessarily preserved
	public SignalWidth getDimension(int i) {
		if (isArray()) {
			if (widths.size() < i)
				return new SignalWidth(widths.get(i));
			if (next != null)
				return next.getDimension(i - widths.size());
		}
		if (i == 0)
			return this;
		if (next != null)
			return next.getDimension(i - 1);
		return null;
	}

	public void setNext(SignalWidth s) {
		next = s;
	}

	public SignalWidth getNext() {
		return next;
	}

	public boolean isStruct() {
		return struct != null;
	}

	public boolean isArray() {
		return widths != null;
	}

	public boolean isText() {
		return text != null;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setStruct(Struct struct) {
		this.struct = struct;
	}

	public Struct getStruct() {
		return struct;
	}

	public void setWidths(ArrayList<Integer> widths) {
		this.widths = widths;
	}

	public ArrayList<Integer> getWidths() {
		return widths;
	}

	public List<Integer> getDimensions() {
		if (!isFixed())
			throw new InvalidParameterException("SignalWidth is not fixed!");

		List<Integer> dim = new ArrayList<>();
		addDimensions(dim);

		return dim;
	}

	private void addDimensions(List<Integer> dim) {
		if (isArray())
			dim.addAll(widths);
		if (isStruct())
			dim.add(struct.getWidth());

		if (next != null)
			next.addDimensions(dim);
	}

	public int getTotalWidth() {
		if (!isFixed())
			throw new InvalidParameterException("Signal width is not fixed in size!");

		int w = 1;
		if (next != null)
			w = next.getTotalWidth();

		w *= getWidth();
		return w;
	}

	public int getWidth() {
		if (isStruct()) {
			return struct.getWidth();
		} else {
			int w = 1;
			for (int i : widths)
				w *= i;
			return w;
		}
	}

	public SignalWidth build(int... dimensions) {
		if (!isSimpleArray() || widths.size() != 1)
			return null;

		int factor = 1;

		for (int d : dimensions)
			factor *= d;
		int bits = widths.get(0);
		
		if (factor == 0)
			return null;

		if (bits % factor != 0)
			return null;

		ArrayList<Integer> newDims = new ArrayList<>(dimensions.length + 1);
		for (int d : dimensions)
			newDims.add(d);
		newDims.add(bits / factor);

		return new SignalWidth(newDims);
	}

	public SignalWidth flatten() {
		return new SignalWidth(getTotalWidth());
	}

	public boolean hasStruct() {
		return isStruct() || (next != null && next.hasStruct());
	}

	public boolean is1D() {
		return isSimpleArray() && getDepth() == 1;
	}

	public int getDepth() {
		int d = 1;

		if (isArray())
			d = widths.size();

		if (next == null)
			return d;

		return next.getDepth() + d;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SignalWidth) {
			SignalWidth s = (SignalWidth) o;
			if (!((struct != null && struct.equals(s.struct)) || (widths != null && widths.equals(s.widths)) || text != null && text.equals(s.text)))
				return false;
			if ((next != null && next.equals(s.next)) || (next == null && s.next == null))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int h = 0;
		if (struct != null)
			h ^= struct.hashCode();
		if (widths != null)
			h ^= widths.hashCode();
		if (next != null)
			h ^= next.hashCode();
		if (text != null)
			h ^= text.hashCode();
		return h;
	}

	public boolean isFixed() {
		if (isText() || (next != null && !next.isFixed()))
			return false;
		return true;
	}

	public boolean isSimpleArray() {
		if (next != null || !isArray())
			return false;
		return true;
	}

	public void assertSimpleArray() {
		if (!isSimpleArray())
			throw new InvalidParameterException(toString() + " is not a simple array!");
	}

	public void assertIsFixed() {
		if (!isFixed())
			throw new InvalidParameterException(toString() + " is not a fixed size!");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (struct != null)
			sb.append('[').append(struct).append(']');
		if (widths != null)
			sb.append(widths);
		if (text != null)
			sb.append('[').append(text).append(']');
		if (next != null)
			sb.append(next);

		return sb.toString();
	}

	public void simplify() {
		for (SignalWidth ptr = this; ptr != null; ptr = ptr.next) {
			if (ptr.widths != null && ptr.next != null && ptr.next.widths != null) {
				ptr.widths.addAll(ptr.next.widths);
				ptr.next = ptr.next.next;
			}
		}
	}
}
