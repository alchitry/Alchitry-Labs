package com.alchitry.labs.parsers.tools.lucid;

public class ArrayBounds {
	private int min, max;

	public ArrayBounds() {

	}

	public ArrayBounds(int i, int j) {
		setValues(i, j);
	}

	public void setValues(int i, int j) {
		max = i > j ? i : j;
		min = i > j ? j : i;
	}

	public boolean fitInWidth(int w) {
		if (max < w && min >= 0)
			return true;
		return false;
	}

	public boolean testBounds(ArrayBounds ab) {
		if (max < ab.getMax())
			return false;
		if (min > ab.getMin())
			return false;
		return true;
	}

	public boolean testBounds(int i, int j) {
		int bmax = i > j ? i : j;
		int bmin = i > j ? j : i;
		if (max < bmax)
			return false;
		if (min > bmin)
			return false;
		return true;
	}

	public boolean testBounds(int i) {
		if (i > max || i < min)
			return false;
		return true;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public int getWidth() {
		return max - min + 1;
	}

	@Override
	public String toString() {
		return "[" + max + ", " + min + "]";
	}
}
