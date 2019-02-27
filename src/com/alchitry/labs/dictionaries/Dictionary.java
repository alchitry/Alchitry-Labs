package com.alchitry.labs.dictionaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

public class Dictionary {
	protected Set<String> dict;

	public Dictionary() {
		dict = new HashSet<String>();
	}

	public Dictionary(List<String> list) {
		dict = new HashSet<String>(list);
	}

	public void add(String word) {
		dict.add(word);
	}

	public void clear() {
		dict.clear();
	}

	public void addAll(Collection<String> list) {
		dict.addAll(list);
	}

	public void addAll(String[] list) {
		dict.addAll(Arrays.asList(list));
	}

	protected static int calculateMatch(String str, String key) {
		char[] letters = key.toCharArray();
		if (letters.length > 0) {
			int offset = StringUtils.indexOfIgnoreCase(str, String.valueOf(letters[0]));
			if (offset == -1)
				return -1;

			offset++;

			int edit = offset * 10;

			if (letters[0] != str.charAt(offset-1))
				edit++;

			for (int i = 1; i < letters.length; i++) {
				int idx = StringUtils.indexOfIgnoreCase(str, String.valueOf(letters[i]), offset);
				if (idx == -1 || idx == str.length())
					return -1;

				edit += (idx - offset) * 10;
				offset = idx + 1;
				if (letters[i] != str.charAt(idx))
					edit++;
			}
			return edit;
		}
		return -1;
	}

	protected static class StringValuePair {
		public String string;
		public int value;

		public StringValuePair(String s, int i) {
			string = s;
			value = i;
		}
	}

	protected static Comparator<StringValuePair> comp = new Comparator<StringValuePair>() {

		@Override
		public int compare(StringValuePair o1, StringValuePair o2) {
			if (o1.value == o2.value)
				return o1.string.compareTo(o2.string);
			return o1.value - o2.value;
		}

	};

	public List<String> findMatches(String partial) {
		return findMatches(partial, dict);
	}

	public static List<String> findMatches(String partial, Set<String> dict) {
		if (partial.isEmpty())
			return new ArrayList<String>(dict);

		TreeSet<StringValuePair> list = new TreeSet<StringValuePair>(comp);
		for (String s : dict) {
			int match = calculateMatch(s, partial);
			if (match != -1) {
				list.add(new StringValuePair(s, match));
			}
		}
		ArrayList<String> best = new ArrayList<>();

		Iterator<StringValuePair> itr = list.iterator();

		while (itr.hasNext() && best.size() < 5) {
			StringValuePair pair = itr.next();
			best.add(pair.string);
		}

		return best;
	}
}
