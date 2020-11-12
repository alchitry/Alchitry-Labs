package com.alchitry.labs.parsers;

import com.alchitry.labs.Util;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.types.Struct;
import com.alchitry.labs.parsers.types.Struct.Member;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.*;

public class ConstValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4659118146920352487L;
	private boolean isArray;
	private Struct struct;
	private HashMap<String, ConstValue> structValues;
	private ArrayList<BitValue> value;
	private ArrayList<ConstValue> values;
	private boolean signed = false;

	public ConstValue() {
	}

	public ConstValue(boolean isArray) {
		if (isArray)
			values = new ArrayList<ConstValue>();
		else
			value = new ArrayList<BitValue>();
		this.isArray = isArray;
	}

	public ConstValue(Struct struct) {
		setStruct(struct);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ConstValue) {
			ConstValue cv = (ConstValue) o;
			if (cv.signed != signed)
				return false;
			if (cv.value != null) {
				if (!cv.value.equals(value))
					return false;
			} else if (value != null) {
				return false;
			}
			if (cv.values != null) {
				if (!cv.values.equals(values))
					return false;
			} else if (values != null) {
				return false;
			}
			if (cv.struct != null) {
				if (!cv.struct.equals(struct))
					return false;
			} else if (struct != null) {
				return false;
			}
			if (cv.structValues != null) {
				return cv.structValues.equals(structValues);
			} else return structValues == null;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int h1 = value == null ? 0 : value.hashCode();
		int h2 = values == null ? 0 : values.hashCode();
		int h3 = structValues == null ? 0 : structValues.hashCode();
		int h4 = struct == null ? 0 : struct.hashCode();
		return (isArray ? 51613846 : 0) ^ h1 ^ h2 ^ h3 ^ h4 ^ (signed ? 135413 : 0);
	}

	public ConstValue(long i) {
		this(i, Util.minWidthNum(i));
	}

	public ConstValue(long val, int w) {
		this(false);
		for (int i = 0; i < w; i++) {
			if ((val & (1 << i)) != 0)
				value.add(BitValue.B1);
			else
				value.add(BitValue.B0);
		}
	}

	public ConstValue(String str) {
		this(false);
		set(str);
	}

	public ConstValue(String str, int radix) {
		this(false);
		set(str, radix);
	}

	public ConstValue(String str, int radix, int width) {
		this(false);
		set(str, radix, width);
	}

	public ConstValue(ConstValue cv) {
		isArray = cv.isArray;
		signed = cv.signed;
		struct = cv.struct;
		if (cv.value != null)
			value = new ArrayList<>(cv.value);
		if (cv.values != null)
			values = new ArrayList<>(cv.values);
		if (cv.structValues != null)
			structValues = new HashMap<>(cv.structValues);
	}

	public ConstValue(List<BitValue> list) {
		isArray = false;
		value = new ArrayList<>(list);
	}

	public ConstValue(BitValue bitValue) {
		this(false);
		value.add(bitValue);
	}

	public ConstValue(BitValue bitValue, int width) {
		this(false);
		for (int i = 0; i < width; i++)
			value.add(bitValue);
	}

	public ConstValue(BigInteger bigInt) {
		constructFromBigInt(bigInt);
	}

	public ConstValue(BigInteger bigInt, int width) {
		constructFromBigInt(bigInt, width);
	}

	public void setStruct(Struct struct) {
		this.struct = struct;
		structValues = new HashMap<>();
	}

	public Struct getStruct() {
		return struct;
	}

	public boolean isStruct() {
		return struct != null;
	}

	public HashMap<String, ConstValue> getStructValues() {
		return structValues;
	}

	public void setSigned(boolean isSigned) {
		signed = isSigned;
	}

	private void constructFromBigInt(BigInteger bigInt) {
		int w = bigInt.bitLength(); // doesn't include sign bit
		if (bigInt.signum() == -1)
			w++;
		w = Math.max(1, w);
		constructFromBigInt(bigInt, w);
	}

	private void constructFromBigInt(BigInteger bigInt, int width) {
		byte[] bList = bigInt.toByteArray();

		value = new ArrayList<>();
		isArray = false;

		signed = bigInt.signum() == -1;

		for (int i = 0; i < width; i++) {
			int idx = i / 8;
			int offset = i % 8;
			byte b = 0;
			if (bList.length > idx)
				b = bList[bList.length - 1 - idx];
			else if (signed)
				b = (byte) ((bList[0] & (1 << 7)) != 0 ? -1 : 0); // sign extend
			if ((b & (1 << offset)) != 0) {
				value.add(BitValue.B1);
			} else {
				value.add(BitValue.B0);
			}
		}
	}

	public void set(String str, boolean isSigned) {
		set(str, 10, isSigned);
	}

	public void set(String str) {
		set(str, 10);
	}

	public void set(String str, int radix, boolean isSigned) {
		signed = isSigned;
		set(str, radix);
	}

	public void set(String str, int radix) {

		switch (radix) {
		case 10:
			constructFromBigInt(new BigInteger(str));
			break;
		case 16:
			set(str, 16, str.length() * 4);
			break;
		case 2:
			set(str, 2, str.length());
			break;
		case 256:
			set(str, 256, str.length() * 8);
			break;
		default:
			throw (new IllegalArgumentException("Radix must be 2, 10, or 16"));
		}
	}

	public void set(String str, int radix, int width, boolean isSigned) {
		signed = isSigned;
		set(str, radix, width);
	}

	public void set(String str, int radix, int width) {
		String strl = str.toLowerCase();
		switch (radix) {
		case 10:
			constructFromBigInt(new BigInteger(strl), width);
			break;
		case 16:
			for (int idx = 0; idx < width; idx++) {
				int charIdx = idx / 4;
				int bitIdx = idx % 4;
				char c = '0';
				if (strl.length() > charIdx)
					c = strl.charAt(strl.length() - 1 - charIdx);
				else if (strl.charAt(0) == 'x' || strl.charAt(0) == 'z')
					c = strl.charAt(0);

				if (c == 'x') {
					value.add(BitValue.Bx);
				} else if (c == 'z') {
					value.add(BitValue.Bz);
				} else {
					int v = Integer.parseInt(Character.toString(c), 16);
					if ((v & (1 << bitIdx)) != 0) {
						value.add(BitValue.B1);
					} else {
						value.add(BitValue.B0);
					}
				}
			}
			break;
		case 2:
			for (int idx = 0; idx < width; idx++) {
				char c = '0';
				if (strl.length() > idx)
					c = strl.charAt(strl.length() - 1 - idx);
				else if (strl.charAt(0) == 'x' || strl.charAt(0) == 'z')
					c = strl.charAt(0);

				switch (c) {
				case '0':
					value.add(BitValue.B0);
					break;
				case '1':
					value.add(BitValue.B1);
					break;
				case 'x':
					value.add(BitValue.Bx);
					break;
				case 'z':
					value.add(BitValue.Bz);
					break;
				default:
					throw (new NumberFormatException());
				}
			}
			break;
		case 256:
			for (int idx = 0; idx < width; idx++) {
				int charIdx = idx / 8;
				int bitIdx = idx % 8;
				char c = 0;
				if (str.length() > charIdx)
					c = str.charAt(str.length() - 1 - charIdx);
				if ((c & (1 << bitIdx)) != 0) {
					value.add(BitValue.B1);
				} else {
					value.add(BitValue.B0);
				}
			}
			break;
		default:
			throw (new IllegalArgumentException("Radix must be 16, 10, or 2"));
		}
	}

	public boolean isArray() {
		return isArray;
	}

	public void prependBits(ConstValue cv) {
		value.addAll(0, cv.value);
	}

	public void appendBits(ConstValue cv) {
		value.addAll(cv.value);
	}

	public boolean add(ConstValue cv) {
		if (cv == null || (values.size() > 0 && !values.get(0).getWidths().equals(cv.getWidths()))) {
			return false;
		}
		values.add(cv);
		return true;
	}

	public boolean addToFront(ConstValue cv) {
		if (cv == null || (values.size() > 0 && !values.get(0).getWidths().equals(cv.getWidths()))) {
			return false;
		}
		values.add(0, cv);
		return true;
	}

	public void remove(int i) {
		if (isArray)
			values.remove(i);
		value.remove(i);
	}

	public boolean addAll(Collection<ConstValue> list) {
		boolean addedAll = true;
		for (ConstValue cv : list) {
			if (!add(cv))
				addedAll = false;
		}
		return addedAll;
	}

	public boolean addAllToFront(ArrayList<ConstValue> list) {
		boolean addedAll = true;
		for (int i = list.size() - 1; i >= 0; i--) {
			ConstValue cv = list.get(i);
			if (!addToFront(cv))
				addedAll = false;
		}
		return addedAll;
	}

	public ArrayList<ConstValue> getValues() {
		if (isArray)
			return values;
		else {
			ArrayList<ConstValue> list = new ArrayList<ConstValue>();
			list.add(this);
			return list;
		}
	}

	public ArrayList<Integer> getWidths() {
		if (isArray) {
			if (values.size() > 0) {
				ArrayList<Integer> ar = values.get(0).getWidths();
				ar.add(0, values.size());
				return ar;
			} else {
				ArrayList<Integer> ar = new ArrayList<>();
				ar.add(0, 0);
				return ar;
			}
		} else {
			ArrayList<Integer> ar = new ArrayList<>();
			ar.add(value.size());
			return ar;
		}
	}

	public int getDepth() {
		if (isArray) {
			if (values.size() > 0)
				return values.get(0).getDepth() + 1;
			else
				return 2;
		} else {
			return 1;
		}
	}

	public ConstValue get(int i) {
		if (!isArray) {
			if (i >= value.size() || i < 0)
				throw new IndexOutOfBoundsException();
			return new ConstValue(value.get(i));
		}
		return values.get(i);
	}
	
	public boolean isSimple() {
		return !isArray() && !isStruct();
	}

	public boolean isNumber() {
		if (isArray)
			return false;
		if (struct != null)
			return false;
		for (BitValue bv : value)
			switch (bv) {
			case B0:
			case B1:
				break;
			case Bx:
			case Bz:
				return false;
			}
		return true;
	}

	public BigInteger getBigInt() {
		if (isArray)
			throw new IllegalStateException("The function getBigInt() can't be used on arrays");
		if (struct != null)
			throw new IllegalStateException("The function getBigInt() can't be used on structs");

		if (!isNumber())
			throw new IllegalStateException("The value is not a number (it contains x and z values)");

		byte[] bytes = new byte[(int) Math.ceil((double) (value.size() + (signed ? 0 : 1)) / 8.0)]; // if not signed need extra 0 sign bit

		if (signed && value.get(value.size() - 1) == BitValue.B1) // sign extension
			Arrays.fill(bytes, (byte) 255);
		else
			Arrays.fill(bytes, (byte) 0);

		for (int i = 0; i < value.size(); i++) {
			int idx = i / 8;
			if (value.get(i) == BitValue.B1)
				bytes[bytes.length - 1 - idx] |= (1 << (i % 8));
			else
				bytes[bytes.length - 1 - idx] &= ~(1 << (i % 8));
		}

		return new BigInteger(bytes);
	}

	public ArrayList<BitValue> getValue() {
		if (isArray)
			throw new IllegalStateException("The function getValue() can't be used on arrays");
		if (struct != null)
			throw new IllegalStateException("The function getValue() can't be used on structs");

		return value;
	}

	public int getMinWidth() {
		if (isArray)
			throw new IllegalStateException("The function getMinWidth() can't be used on arrays");
		if (struct != null)
			throw new IllegalStateException("The function getMinWidth() can't be used on structs");
		if (signed && value.get(value.size() - 1).equals(BitValue.B1))
			for (int i = value.size() - 1; i >= 0; i--) {
				if (value.get(i) != BitValue.B1)
					return i + 2;
			}
		else
			for (int i = value.size() - 1; i >= 0; i--) {
				if (value.get(i) != BitValue.B0)
					return i + 1 + (signed ? 1 : 0);
			}
		return 1;
	}

	public int getWidth() {
		if (struct != null)
			throw new IllegalStateException("The function getWidth() can't be used on structs");
		if (isArray)
			return values.size();
		return value.size();
	}

	public SignalWidth getArrayWidth() {
		if (struct != null)
			throw new IllegalStateException("The function getArrayWidth() can't be used on structs");
		ConstValue cv = this;
		SignalWidth width = new SignalWidth();

		while (cv.isArray) {
			width.getWidths().add(cv.values.size());
			cv = cv.get(0);
		}
		width.getWidths().add(cv.value.size());
		return width;
	}

	public void setValue(List<BitValue> v) {
		if (struct != null)
			throw new IllegalStateException("The function setValue() can't be used on structs");
		if (isArray)
			throw new IllegalStateException("The function setValue() can't be used on arrays");
		value.clear();
		value.addAll(v);
	}

	public void setValue(BitValue v) {
		if (struct != null)
			throw new IllegalStateException("The function setValue() can't be used on structs");
		if (isArray)
			throw new IllegalStateException("The function setValue() can't be used on arrays");
		value.clear();
		value.add(v);
	}

	public static ConstValue Or(ConstValue v1, ConstValue v2) {
		if (v1.isStruct() || v2.isStruct())
			throw new IllegalArgumentException("Arguments can't be structs");
		if (!v1.getWidths().equals(v2.getWidths()))
			throw new IllegalArgumentException("Arguments must have the same dimensions");

		if (v1.isArray()) {
			ConstValue cv = new ConstValue(true);
			for (int i = 0; i < v1.getValues().size(); i++) {
				cv.add(Or(v1.get(i), v1.get(i)));
			}
			return cv;
		} else {
			ConstValue cv = new ConstValue(false);
			cv.setValue(BitValue.or(v1.getValue(), v2.getValue()));
			return cv;
		}
	}

	public static ConstValue And(ConstValue v1, ConstValue v2) {
		if (v1.isStruct() || v2.isStruct())
			throw new IllegalArgumentException("Arguments can't be structs");
		if (!v1.getWidths().equals(v2.getWidths()))
			throw new IllegalArgumentException("Arguments must have the same dimensions");

		if (v1.isArray()) {
			ConstValue cv = new ConstValue(true);
			for (int i = 0; i < v1.getValues().size(); i++) {
				cv.add(And(v1.get(i), v1.get(i)));
			}
			return cv;
		} else {
			ConstValue cv = new ConstValue(false);
			cv.setValue(BitValue.and(v1.getValue(), v2.getValue()));
			return cv;
		}
	}

	public static ConstValue Xor(ConstValue v1, ConstValue v2) {
		if (v1.isStruct() || v2.isStruct())
			throw new IllegalArgumentException("Arguments can't be structs");
		if (!v1.getWidths().equals(v2.getWidths()))
			throw new IllegalArgumentException("Arguments must have the same dimensions");

		if (v1.isArray()) {
			ConstValue cv = new ConstValue(true);
			for (int i = 0; i < v1.getValues().size(); i++) {
				cv.add(Xor(v1.get(i), v1.get(i)));
			}
			return cv;
		} else {
			ConstValue cv = new ConstValue(false);
			cv.setValue(BitValue.xor(v1.getValue(), v2.getValue()));
			return cv;
		}
	}

	public static ConstValue Nor(ConstValue v1, ConstValue v2) {
		return Invert(Or(v1, v2));
	}

	public static ConstValue Nand(ConstValue v1, ConstValue v2) {
		return Invert(And(v1, v2));
	}

	public static ConstValue Xnor(ConstValue v1, ConstValue v2) {
		return Invert(Xor(v1, v2));
	}

	public static ConstValue Invert(ConstValue v) {
		if (v.isStruct())
			throw new IllegalArgumentException("Argument can't be a struct");
		ConstValue cv;
		if (v.isArray()) {
			cv = new ConstValue(true);
			for (int i = 0; i < v.getValues().size(); i++) {
				cv.add(Invert(v.get(i)));
			}
		} else {
			cv = new ConstValue(false);
			cv.setValue(BitValue.invert(v.getValue()));
		}
		return cv;
	}

	private static BitValue NotRecursive(ConstValue v) {
		if (v.isStruct())
			throw new IllegalArgumentException("Argument can't be a struct");
		if (v.isArray()) {
			boolean hasX = false;
			for (int i = 0; i < v.getValues().size(); i++) {
				BitValue bv = NotRecursive(v.get(i));
				if (bv == BitValue.B0)
					return BitValue.B0;
				else if (bv == BitValue.Bx || bv == BitValue.Bz)
					hasX = true;
			}
			if (hasX)
				return BitValue.Bx;
			else
				return BitValue.B1;
		} else {
			return BitValue.not(v.getValue());
		}
	}

	public static ConstValue Not(ConstValue v) {
		if (v.isStruct())
			throw new IllegalArgumentException("Argument can't be a struct");
		ConstValue cv = new ConstValue(NotRecursive(v));
		return cv;
	}

	public static BitValue Equal(ConstValue v1, ConstValue v2) {
		if ((v1.isArray() || v2.isArray()) && !v1.getWidths().equals(v2.getWidths()))
			return BitValue.B0;

		if (v1.isArray()) {
			for (int i = 0; i < v1.getValues().size(); i++) {
				if (Equal(v1.get(i), v2.get(i)) == BitValue.B0)
					return BitValue.B0;
			}
			return BitValue.B1;
		} else if (v1.isStruct()) {
			if (!v2.isStruct())
				return BitValue.B0;
			if (!v1.getStruct().equals(v2.getStruct()))
				return BitValue.B0;
			if (!v1.getStructValues().keySet().equals(v2.getStructValues().keySet()))
				return BitValue.B0;
			for (String key : v1.getStructValues().keySet())
				if (Equal(v1.getStructValues().get(key), v2.getStructValues().get(key)) == BitValue.B0)
					return BitValue.B0;
			return BitValue.B1;
		} else {
			if (v1.isNegative() ^ v2.isNegative())
				return BitValue.B0;
			return BitValue.equal(v1.getValue(), v2.getValue(), v1.signed, v2.signed);
		}
	}

	public boolean isZero() {
		return Zero(this);
	}

	public static boolean Zero(ConstValue v) {
		if (v.isStruct())
			throw new IllegalArgumentException("Argument can't be a struct");
		if (v.isArray) {
			for (int i = 0; i < v.getValues().size(); i++) {
				if (!Zero(v.get(i)))
					return false;
			}
			return true;
		} else {
			return BitValue.isZero(v.getValue());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (isArray) {
			boolean first = true;
			sb.append("[");
			for (int i = values.size() - 1; i >= 0; i--) {
				if (!first) {
					sb.append(", ");
				} else {
					first = false;
				}
				sb.append(values.get(i).toString());
			}
			sb.append("]");
		} else if (struct != null) {
			sb.append("<").append(struct.getName()).append(">(");
			boolean first = true;
			for (Map.Entry<String,ConstValue> entry : structValues.entrySet()) {
				if (!first)
					sb.append(", ");
				else
					first = false;
				sb.append(entry.getKey()).append("(").append(entry.getValue().toString()).append(")");
			}
			sb.append(")");
		} else {
			if (isNumber()) {
				sb.append(getBigInt().toString()).append(": ");
			}
			sb.append(BitValue.toString(value));
		}
		return sb.toString();
	}

	public void shiftRight(int n, boolean signExtend) {
		if (isArray)
			throw new IllegalStateException("The function shiftRight() can't be used on arrays");
		if (struct != null)
			throw new IllegalStateException("The function shiftRight() can't be used on structs");

		if (value.size() < n)
			n = value.size();

		BitValue bv;
		if (signExtend)
			bv = value.get(value.size() - 1); // msb
		else
			bv = BitValue.B0;

		for (int i = 0; i < n; i++) {
			value.remove(0);
			value.add(value.size(), bv);
		}
	}

	public void shiftLeft(int n) {
		if (isArray)
			throw new IllegalStateException("The function shiftLeft() can't be used on arrays");
		if (struct != null)
			throw new IllegalStateException("The function shiftLeft() can't be used on structs");

		for (int i = 0; i < n; i++)
			value.add(0, BitValue.B0);
	}

	public BitValue orReduce() {
		if (struct != null)
			throw new IllegalStateException("The function orReduce() can't be used on structs");
		if (isArray) {
			BitValue b = BitValue.B0;
			for (int i = 0; i < values.size(); i++) {
				b = b.or(values.get(i).orReduce());
				if (b == BitValue.B1)
					return BitValue.B1;
			}
			return b;
		} else {
			BitValue bv = BitValue.B0;
			for (BitValue b : value) {
				bv = bv.or(b);
			}
			return bv;
		}
	}

	public BitValue andReduce() {
		if (struct != null)
			throw new IllegalStateException("The function andReduce() can't be used on structs");
		if (isArray) {
			BitValue b = BitValue.B1;
			for (int i = 0; i < values.size(); i++) {
				b = b.and(values.get(i).andReduce());
				if (b == BitValue.B0)
					return BitValue.B0;
			}
			return b;
		} else {
			BitValue bv = BitValue.B1;
			for (BitValue b : value) {
				bv = bv.and(b);
			}
			return bv;
		}
	}

	public BitValue xorReduce() {
		if (struct != null)
			throw new IllegalStateException("The function xorReduce() can't be used on structs");
		if (isArray) {
			BitValue b = BitValue.B1;
			for (int i = 0; i < values.size(); i++) {
				b = b.and(values.get(i).xorReduce());
				if (b == BitValue.B0)
					return BitValue.B0;
			}
			return b;
		} else {
			BitValue bv = BitValue.B0;
			for (BitValue b : value) {
				bv = bv.xor(b);
			}
			return bv;
		}
	}

	public BitValue norReduce() {
		return orReduce().not();
	}

	public BitValue nandReduce() {
		return andReduce().not();
	}

	public BitValue xnorReduce() {
		return xorReduce().not();
	}

	public boolean isNegative() {
		if (struct != null)
			throw new IllegalStateException("The function isNegative() can't be used on structs");
		if (isArray)
			throw new IllegalStateException("The function isNegative() can't be used on arrays");
		return signed && value.get(value.size() - 1) == BitValue.B1;
	}

	public BitValue lessThan(ConstValue cv) {
		if (cv.isArray || isArray)
			throw new IllegalStateException("The function lessThan() can't be used on arrays");
		if (struct != null)
			throw new IllegalStateException("The function lessThan() can't be used on structs");

		if (!isNumber() || !cv.isNumber())
			return BitValue.Bx;

		boolean neg = isNegative();
		boolean cvneg = cv.isNegative();

		if (neg && !cvneg)
			return BitValue.B1;
		if (!neg && cvneg)
			return BitValue.B0;

		int w = Math.max(cv.value.size(), value.size());
		for (int i = w - 1; i >= 0; i--) {
			BitValue b1, b2;
			if (neg)
				b1 = b2 = BitValue.B1;
			else
				b1 = b2 = BitValue.B0;

			if (value.size() > i)
				b1 = value.get(i);
			if (cv.value.size() > i)
				b2 = cv.value.get(i);

			if (b2 == BitValue.B1) {
				if (b1 == BitValue.B0) {
					return BitValue.B1;
				}
			} else if (b1 == BitValue.B1) {
				return BitValue.B0;
			}
		}
		return BitValue.B0;

	}

	private ConstValue build(List<BitValue> values, int[] dims) {
		int d = dims[dims.length - 1];
		int vCt = values.size();
		int step = vCt / d;
		ConstValue root = new ConstValue(true);
		if (step * d != vCt)
			throw new InvalidParameterException("Dimensions don't split evenly for build()");
		if (dims.length == 1) {
			for (int i = 0; i < d; i++) {
				root.add(new ConstValue(values.subList(step * i, step * i + step)));
			}
		} else {
			for (int i = 0; i < d; i++) {
				root.add(build(values.subList(step * i, step * i + step), Arrays.copyOfRange(dims, 0, dims.length - 1)));
			}
		}
		return root;
	}

	public ConstValue build(int... dimensions) {
		if (dimensions.length < 1)
			throw new InvalidParameterException("Dimensions must be specified when building an array!");
		if (isArray)
			throw new InvalidParameterException("The function build() can't be called on array values!");
		if (struct != null)
			throw new IllegalStateException("The function build() can't be used on structs");
		for (int d : dimensions)
			if (d == 0)
				throw new InvalidParameterException("Dimensions supplied to build() can't be 0!");

		return build(value, dimensions);
	}

	public ConstValue flatten() {
		if (isArray) {
			ConstValue cv = new ConstValue(false);
			for (ConstValue c : values) {
				if (c.isArray)
					cv.appendBits(c.flatten());
				else
					cv.appendBits(c);

			}
			return cv;
		} else if (struct != null) {
			ConstValue cv = new ConstValue(false);
			for (Member m : struct.getMembers()) {
				ConstValue c = structValues.get(m.name);
				if (c.isArray)
					cv.appendBits(c.flatten());
				else
					cv.appendBits(c);
			}
			return cv;
		}
		return this;
	}

	private static String getHex(List<BitValue> list, int start) {
		int end = start + 4;
		if (end > list.size())
			end = list.size();
		int value = 0;

		for (int i = start; i < end; i++) {
			if (list.get(i).equals(BitValue.B1))
				value += Math.pow(2, i - start);
		}

		return Integer.toHexString(value);
	}

	public String toVerilog() {
		StringBuilder sb = new StringBuilder();

		boolean isNeg = false;
		if (!isArray && struct == null && isNegative())
			isNeg = true;

		if (isNeg || signed)
			sb.append("$signed(");

		ConstValue cv = this.flatten();
		int bits = cv.getWidth();
		sb.append(bits);

		if (cv.isNumber()) {
			sb.append("'h");
			int start = ((bits - 1) / 4) * 4;
			for (int i = start; i >= 0; i -= 4) {
				sb.append(getHex(cv.value, i));
			}
		} else {
			sb.append("'b");
			for (int i = bits - 1; i >= 0; i--)
				sb.append(cv.value.get(i).getChar());
		}
		if (isNeg || signed)
			sb.append(")");
		return sb.toString();
	}

	public ConstValue reverse() {
		if (isArray)
			Collections.reverse(values);
		else
			Collections.reverse(value);
		return this;
	}

	public ConstValue resize(int size) {
		if (isArray)
			throw new IllegalStateException("The function resize() can't be used on arrays");
		if (struct != null)
			throw new IllegalStateException("The function resize() can't be used on structs");
		ConstValue cv = new ConstValue(this);
		int origWidth = cv.getWidth();
		if (origWidth > size) {
			cv.value.subList(size, cv.value.size()).clear();
		} else if (origWidth < size) {
			BitValue bv = cv.signed ? cv.value.get(cv.value.size() - 1) : BitValue.B0;
			for (int i = 0; i < size - origWidth; i++) {
				cv.value.add(bv);
			}
		}
		return cv;
	}
}
