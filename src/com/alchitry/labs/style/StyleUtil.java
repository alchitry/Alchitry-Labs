package com.alchitry.labs.style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.custom.StyleRange;

public class StyleUtil {
	public static void sort(List<StyleRange> styles) {
		Collections.sort(styles, new Comparator<StyleRange>() {
			@Override
			public int compare(StyleRange o1, StyleRange o2) {
				return o1.start - o2.start;
			}
		});
	}

	private static int check(List<StyleRange> list, StyleRange e, int i, Comparator<StyleRange> c) {
		if (list.size() == 0)
			return 0;

		if (i > 0 && i < list.size()) {
			StyleRange b = list.get(i - 1);
			StyleRange a = list.get(i);
			if (c.compare(e, b) >= 0 && c.compare(e, a) <= 0) {
				return 0;
			}
			return e.start - b.start;
		} else if (i == list.size()) {
			StyleRange b = list.get(i - 1);
			if (c.compare(e, b) >= 0) {
				return 0;
			}
			return e.start - b.start;
		} else {
			StyleRange a = list.get(i);
			if (c.compare(e, a) <= 0) {
				return 0;
			}
			return e.start - a.start;
		}
	}

	public static void insert(List<StyleRange> list, StyleRange e, int near, Comparator<StyleRange> c) {
		int idx = near;
		int p;
		while ((p = check(list, e, idx, c)) != 0) {
			if (p < 0)
				idx++;
			else
				idx--;
		}
		list.add(idx, e);
	}

	public static boolean stylesOverlap(StyleRange s1, StyleRange s2) {
		int s1b = s1.start;
		int s1e = s1b + s1.length - 1;
		int s2b = s2.start;
		int s2e = s2b + s2.length - 1;
		return (s1b >= s2b && s1b <= s2e) || (s1e >= s2b && s1e <= s2e) || (s2b >= s1b && s2b <= s1e) || (s2e >= s1b && s2e <= s1e);
	}

	public static void copy(StyleRange dest, StyleRange src) {
		dest.borderColor = src.borderColor;
		dest.foreground = src.foreground;
		dest.underline = src.underline;
		dest.underlineColor = src.underlineColor;
		dest.font = src.font;
		dest.background = src.background;
		dest.fontStyle = src.fontStyle;
		dest.rise = src.rise;
	}

	public static StyleRange duplicate(StyleRange s) {
		StyleRange style = new StyleRange(s);
		style.fontStyle = s.fontStyle; // new doesn't copy StyleRange members
		style.length = s.length;
		style.start = s.start;
		return style;
	}

	public interface StyleMerger {
		public void mergeStyles(StyleRange dest, StyleRange src);
	}

	private static class StyleItem {
		public StyleRange style;
		public boolean parent;

		public StyleItem(StyleRange s, boolean p) {
			style = s;
			parent = p;
		}

		@Override
		public String toString() {
			return style.toString() + " : " + Boolean.toString(parent);
		}
	}

	// This function is magical... It takes syntax styles and merges them.
	public static List<StyleRange> mergeStyles(List<StyleRange> styles, StyleRange[] baseStyles, StyleMerger merger) {
		List<StyleRange> mStyles = new ArrayList<>();
		if (baseStyles == null || baseStyles.length == 0) {
			mStyles.addAll(styles);
			return mStyles;
		}

		if (styles.size() == 0) {
			mStyles.addAll(Arrays.asList(baseStyles));
			sort(mStyles);
			return mStyles;
		}

		List<StyleItem> list = new ArrayList<StyleItem>();
		for (StyleRange s : styles)
			list.add(new StyleItem(s, true));
		for (StyleRange s : baseStyles)
			list.add(new StyleItem(s, false));

		Collections.sort(list, new Comparator<StyleItem>() {
			@Override
			public int compare(StyleItem o1, StyleItem o2) {
				return o1.style.start - o2.style.start;
			}
		});

		StyleItem current = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			StyleItem next = list.get(i);

			if (stylesOverlap(current.style, next.style)) {
				int cs = current.style.start;
				int ns = next.style.start;
				int ce = current.style.start + current.style.length - 1;
				int ne = next.style.start + next.style.length - 1;

				StyleRange style;
				if (cs < ns) {
					style = duplicate(current.style);
					style.start = cs;
					style.length = ns - cs;
					mStyles.add(style);
				}

				if (current.parent) {
					style = duplicate(current.style);
					merger.mergeStyles(style, next.style);
				} else {
					style = duplicate(next.style);
					merger.mergeStyles(style, current.style);
				}
				style.start = ns;
				if (ne > ce)
					style.length = ce - ns + 1;
				else
					style.length = ne - ns + 1;

				mStyles.add(style);

				if (ne > ce) {
					current = next;
					current.style = duplicate(current.style);
					current.style.start = ce + 1;
					current.style.length = ne - ce;
				} else if (ce > ne) {
					current.style = duplicate(current.style);
					current.style.start = ne + 1;
					current.style.length = ce - ne;
				} else {
					i++;
					if (i < list.size()) {
						current = list.get(i);
						if (i == list.size() - 1)
							mStyles.add(current.style);
					} else {
						current.style = style;
					}
				}

			} else {
				mStyles.add(current.style);
				current = next;
			}
		}
		
		if (!mStyles.contains(current.style))
			mStyles.add(current.style);

		StyleUtil.sort(mStyles);
		return mStyles;
	}
}
