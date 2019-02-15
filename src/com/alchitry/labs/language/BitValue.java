package com.alchitry.labs.language;

import java.util.ArrayList;

import com.alchitry.labs.Util;

public enum BitValue {
	B0, B1, Bx, Bz;

	public BitValue or(BitValue b) {
		if (this == B1 || b == B1)
			return B1;

		if (this == Bz || b == Bz || this == Bx || b == Bx)
			return Bx;

		return B0;
	}

	public BitValue and(BitValue b) {
		if (this == B1 && b == B1)
			return B1;

		if (this == Bz || b == Bz || this == Bx || b == Bx)
			return Bx;

		return B0;
	}

	public BitValue xor(BitValue b) {
		if ((this == B1) != (b == B1))
			return B1;

		if (this == Bz || b == Bz || this == Bx || b == Bx)
			return Bx;

		return B0;
	}

	public char getChar() {
		switch (this) {
		case B0:
			return '0';
		case B1:
			return '1';
		case Bx:
			return 'x';
		case Bz:
			return 'z';
		default:
			Util.log.severe("Unknown bit type " + this.toString());
			return '?';
		}
	}

	public BitValue nor(BitValue b) {
		return or(b).not();
	}

	public BitValue nand(BitValue b) {
		return and(b).not();
	}

	public BitValue xnor(BitValue b) {
		return xor(b).not();
	}

	public BitValue not() {
		if (this == B1)
			return B0;
		if (this == B0)
			return B1;
		return this;
	}

	public BitValue invert() {
		switch (this) {
		case B0:
			return B1;
		case B1:
			return B0;
		case Bx:
			return Bx;
		case Bz:
			return Bz;
		}
		return null;
	}

	public static BitValue or(BitValue b1, BitValue b2) {
		return b1.or(b2);
	}

	public static BitValue and(BitValue b1, BitValue b2) {
		return b1.and(b2);
	}

	public static BitValue xor(BitValue b1, BitValue b2) {
		return b1.xor(b2);
	}

	public static BitValue nor(BitValue b1, BitValue b2) {
		return b1.nor(b2);
	}

	public static BitValue nand(BitValue b1, BitValue b2) {
		return b1.nand(b2);
	}

	public static BitValue xnor(BitValue b1, BitValue b2) {
		return b1.xnor(b2);
	}

	public static ArrayList<BitValue> or(ArrayList<BitValue> b1, ArrayList<BitValue> b2) {
		int size = Math.max(b1.size(), b2.size());
		ArrayList<BitValue> bv = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			if (i < b1.size()) {
				if (i < b2.size()) {
					bv.add(b1.get(i).or(b2.get(i)));
				} else {
					bv.add(b1.get(i).or(B0));
				}
			} else {
				bv.add(B0.or(b2.get(i)));
			}

		}
		return bv;
	}

	public static ArrayList<BitValue> and(ArrayList<BitValue> b1, ArrayList<BitValue> b2) {
		int size = Math.max(b1.size(), b2.size());
		ArrayList<BitValue> bv = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			if (i < b1.size()) {
				if (i < b2.size()) {
					bv.add(b1.get(i).and(b2.get(i)));
				} else {
					bv.add(b1.get(i).and(B0));
				}
			} else {
				bv.add(B0.and(b2.get(i)));
			}

		}
		return bv;
	}

	public static ArrayList<BitValue> xor(ArrayList<BitValue> b1, ArrayList<BitValue> b2) {
		int size = Math.max(b1.size(), b2.size());
		ArrayList<BitValue> bv = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			if (i < b1.size()) {
				if (i < b2.size()) {
					bv.add(b1.get(i).xor(b2.get(i)));
				} else {
					bv.add(b1.get(i).xor(B0));
				}
			} else {
				bv.add(B0.xor(b2.get(i)));
			}

		}
		return bv;
	}

	public static ArrayList<BitValue> nor(ArrayList<BitValue> b1, ArrayList<BitValue> b2) {
		return invert(or(b1, b2));
	}

	public static ArrayList<BitValue> nand(ArrayList<BitValue> b1, ArrayList<BitValue> b2) {
		return invert(and(b1, b2));
	}

	public static ArrayList<BitValue> xnor(ArrayList<BitValue> b1, ArrayList<BitValue> b2) {
		return invert(xor(b1, b2));
	}

	public static BitValue equal(ArrayList<BitValue> b1, ArrayList<BitValue> b2, boolean s1, boolean s2) {
		int size = Math.max(b1.size(), b2.size());
		BitValue se1 = s1 ? b1.get(b1.size()-1) : B0;
		BitValue se2 = s2 ? b2.get(b2.size()-1) : B0;

		for (int i = 0; i < size; i++) {
			if (i < b1.size()) {
				if (i < b2.size()) {
					if (b1.get(i) != b2.get(i))
						return B0;
				} else {
					if (b1.get(i) != se2)
						return B0;
				}
			} else {
				if (b2.get(i) != se1)
					return B0;
			}

		}
		return B1;
	}

	public static ArrayList<BitValue> invert(ArrayList<BitValue> val) {
		ArrayList<BitValue> bv = new ArrayList<>(val.size());

		for (BitValue b : val)
			bv.add(b.invert());

		return bv;
	}

	public static BitValue not(ArrayList<BitValue> val) {
		boolean hasX = false;
		for (BitValue b : val) {
			if (b == B1)
				return B0;
			else if (b == Bx || b == Bz)
				hasX = true;
		}
		if (hasX)
			return Bx;
		return B1;
	}

	public static boolean isZero(ArrayList<BitValue> val) {
		for (BitValue b : val)
			if (b != B0)
				return false;

		return true;
	}

	public static String toString(ArrayList<BitValue> val) {
		StringBuilder sb = new StringBuilder();

		sb.append('{');
		for (int i = val.size() - 1; i >= 0; i--) {
			BitValue bv = val.get(i);
			sb.append(bv.toString().substring(1));

		}
		sb.append('}');
		return sb.toString();
	}
}
