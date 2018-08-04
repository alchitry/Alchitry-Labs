package com.alchitry.labs.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.custom.StyleRange;

public class SyntaxError {
	public static final int ERROR = 0;
	public static final int WARNING = 1;
	public static final int INFO = 2;
	public static final int DEBUG = -1;

	public StyleRange style;
	public String message;
	public int start;
	public int stop;
	public int line;
	public int column;
	public int type;

	public SyntaxError() {
	}

	public SyntaxError(SyntaxError e) {
		type = e.type;
		style = StyleUtil.duplicate(e.style);
		message = e.message;
		start = e.start;
		stop = e.stop;
		line = e.line;
		column = e.column;
	}

	public SyntaxError(int type, StyleRange style, String msg, int start, int stop, int line, int column) {
		this.type = type;
		this.style = style;
		this.message = msg;
		this.start = start;
		this.stop = stop;
		this.line = line;
		this.column = column;
	}
	
	@Override
	public String toString() {
		return "type: " +type+" start: "+start+" stop: "+stop+" line: "+line+" message: "+message;
	}

	private static Comparator<SyntaxError> comp = new Comparator<SyntaxError>() {
		@Override
		public int compare(SyntaxError o1, SyntaxError o2) {
			return o2.start - o1.start;
		}
	};

	private static void insert(List<SyntaxError> list, SyntaxError e, Comparator<SyntaxError> c) {
		for (int i = list.size() - 1; i >= 0; i--) {
			SyntaxError s = list.get(i);
			if (c.compare(e, s) >= 0) {
				list.add(i + 1, e);
				return;
			}
		}
	}

	public static void reverseSort(List<SyntaxError> styles) {
		Collections.sort(styles, comp);
	}

	// This function fixes overlapping styles
	public static void spliceErrors(List<SyntaxError> errors) {
		reverseSort(errors);
		ArrayList<SyntaxError> fixed = new ArrayList<>();

		while (errors.size() > 1) {
			int size = errors.size();
			SyntaxError s = errors.get(size - 1);
			SyntaxError e = errors.get(size - 2);
			if (e.start <= s.stop) { // overlap?
				if (s.type <= e.type) { // is s higher priority?
					errors.remove(size - 2);
					if (e.stop > s.stop) { // is there a tail
						e.start = s.stop + 1;
						e.style.start = e.start;
						e.style.length = e.stop - e.start + 1;
						insert(errors, e, comp);
					}
				} else { // e higher priority
					errors.remove(size - 1);
					boolean tail = e.stop < s.stop;
					if (s.start < e.start) { // is there a head
						SyntaxError ne = tail ? new SyntaxError(s) : s;
						ne.stop = e.start - 1;
						ne.style.start = ne.start;
						ne.style.length = ne.stop - ne.start + 1;
						insert(errors, ne, comp);
					}

					if (tail) { // is there a tail
						s.start = e.stop + 1;
						s.style.start = s.start;
						s.style.length = s.stop - s.start + 1;
						insert(errors, s, comp);
					}
				}
			} else {
				errors.remove(size - 1);
				fixed.add(s);
			}
		}

		errors.addAll(fixed);
	}
}
