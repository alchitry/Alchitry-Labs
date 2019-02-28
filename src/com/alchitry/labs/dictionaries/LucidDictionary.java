package com.alchitry.labs.dictionaries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.parsers.lucid.Lucid;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.tools.lucid.LucidExtractor;
import com.alchitry.labs.parsers.types.Constant;
import com.alchitry.labs.parsers.types.Dff;
import com.alchitry.labs.parsers.types.Fsm;
import com.alchitry.labs.parsers.types.Struct;
import com.alchitry.labs.parsers.types.Struct.Member;

public class LucidDictionary extends Dictionary {
	private static final String[] LUCID_KEYWORDS = { "module", "dff", "fsm", "input", "output", "inout", "signed", "for", "case", "if", "default", "const", "var", "sig",
			"always", "else" };

	private List<Dff> dffs;
	private List<Fsm> fsms;
	private List<Sig> inouts;
	private List<InstModule> instModules;

	private StyledCodeEditor editor;
	private LucidExtractor errorChecker;

	private int position;

	public LucidDictionary(StyledCodeEditor e) {
		super();
		addAll(LUCID_KEYWORDS);
		editor = e;
	}

	public LucidDictionary(List<String> list, StyledCodeEditor e) {
		super(list);
		addAll(LUCID_KEYWORDS);
		editor = e;
	}

	public void setSignals(LucidExtractor checker, List<InstModule> im) {
		dffs = checker.getDffs();
		fsms = checker.getFsms();
		inouts = checker.getInouts();
		instModules = im;
		errorChecker = checker;
	}

	@Override
	public void clear() {
		super.clear();
		addAll(LUCID_KEYWORDS);
	}

	// Finds the next word starting from a "."
	private String getNextWord(String text) {
		int start, end;
		end = Math.min(position, text.length());
		start = Math.max(end - 1, 0);
		while (start > 0 && (Character.isLetterOrDigit(text.charAt(start)) || text.charAt(start) == '_'))
			start--;

		if (text.charAt(start) == '.') {
			start--;
			if (text.charAt(start) == ']') { // Skip bit indexes like [6] or [4:0]
				while (start > 0 && (text.charAt(start) != '['))
					start--;
				start--;
			}

			while (start >= 0 && Character.isWhitespace(text.charAt(start)))
				start--;
			end = start + 1;
			while (start > 0 && (Character.isLetterOrDigit(text.charAt(start)) || text.charAt(start) == '_'))
				start--;
			start++;
			if (start > end)
				start = end;
			position = start;
			return text.substring(start, end);
		}
		return null;
	}

	// gets a list of all words before the cursor in a signal name
	private List<String> getWordsBefore() {
		ArrayList<String> list = new ArrayList<>();

		String text = editor.getText();
		position = editor.getCaretOffset();

		String word;
		while ((word = getNextWord(text)) != null)
			list.add(word);

		Collections.reverse(list);

		return list;
	}

	private SignalWidth getWidth(List<String> list) {
		StringBuilder sb = new StringBuilder();
		int idx;
		SignalWidth width = null;
		for (idx = 0; idx < list.size(); idx++) {
			if (idx != 0)
				sb.append(".");
			sb.append(list.get(idx));
			width = errorChecker.getBitWidthChecker().checkWidthMap(sb.toString());
			if (width != null)
				break;
		}
		idx++;
		for (int i = idx; i < list.size(); i++) {
			while (width != null && !width.isStruct())
				width = width.getNext();
			if (width != null) {
				Struct s = width.getStruct();
				width = s.getWidthOfMember(list.get(i));
			}
		}

		return width;
	}

	private boolean addStructMembers(Set<String> dict, SignalWidth width, boolean addWidth) {
		if (width != null) {
			if (width.isStruct()) {
				for (Member m : width.getStruct().getMembers())
					dict.add(m.getName());
				addWidth = false;
			}
		}
		return addWidth;
	}

	@Override
	public List<String> findMatches(String partial) {
		List<String> words = getWordsBefore();

		if (!words.isEmpty() && !words.get(0).isEmpty()) {
			boolean addWidth = true;
			String name = words.get(0);
			Set<String> dict = new HashSet<>();
			if (errorChecker.nameUsed(name)) {
				SignalWidth width = getWidth(words);

				switch (words.size()) {
				case 1: // Simple name

					if (Util.containsName(dffs, name)) {
						dict.add("d");
						dict.add("q");
					} else {
						Fsm f;
						if ((f = Util.getByName(fsms, name)) != null) {
							dict.add("d");
							dict.add("q");
							for (Constant state : f.getStates())
								dict.add(state.getName());
						} else {
							InstModule im;
							if ((im = Util.getByName(instModules, name)) != null) {
								Module m = im.getType();
								for (Sig s : m.getInputs())
									dict.add(s.getName());
								for (Sig s : m.getOutputs())
									dict.add(s.getName());
								for (Sig s : m.getInouts())
									dict.add(s.getName());
							} else if (Util.containsName(inouts, name)) {
								dict.add("enable");
								dict.add("read");
								dict.add("write");
							} else {
								HashMap<String, List<Struct>> gS = MainWindow.getOpenProject().getGlobalStructs();
								if (gS.get(name) != null) {
									List<Constant> gC = MainWindow.getGlobalConstants().get(name);
									List<Struct> st = gS.get(name);
									for (Constant c : gC)
										dict.add(c.getName());
									for (Struct strt : st)
										dict.add(strt.getName());
									addWidth = false;
								} else {
									addWidth = addStructMembers(dict, width, addWidth);
								}
							}
						}
					}

					if (addWidth)
						dict.add(Lucid.WIDTH_ATTR);
					break;
				case 2: // Module with signal
					String sig = words.get(1);
					if (Util.containsName(dffs, name) || Util.containsName(fsms, name)) {
						switch (sig) {
						case "d":
						case "q":
							dict.add(Lucid.WIDTH_ATTR);
							addWidth = addStructMembers(dict, width, addWidth);
						}
					} else {
						InstModule im;
						if ((im = Util.getByName(instModules, name)) != null) {
							Module m = im.getType();
							if (Util.containsName(m.getInputs(), sig) || Util.containsName(m.getOutputs(), sig))
								dict.add(Lucid.WIDTH_ATTR);

							if (Util.containsName(m.getInouts(), sig)) {
								dict.add("enable");
								dict.add("read");
								dict.add("write");
								dict.add(Lucid.WIDTH_ATTR);
							} else if (Util.containsName(inouts, name)) {
								switch (sig) {
								case "enable":
								case "read":
								case "write":
									dict.add(Lucid.WIDTH_ATTR);
								}
							} else {
								addWidth = addStructMembers(dict, width, addWidth);
							}
						} else {
							addWidth = addStructMembers(dict, width, addWidth);
						}
					}

					break;
				case 3: // Module with inout, i.e. myMod.ioSig.read.
					sig = words.get(1);
					InstModule im;
					if ((im = Util.getByName(instModules, name)) != null) {
						Module m = im.getType();

						if (Util.containsName(m.getInouts(), sig)) {
							switch (words.get(2)) {
							case "enable":
							case "read":
							case "write":
								dict.add(Lucid.WIDTH_ATTR);
							}
						} else {
							addWidth = addStructMembers(dict, width, addWidth);
						}
					} else {
						addWidth = addStructMembers(dict, width, addWidth);
					}
				}
				return Dictionary.findMatches(partial, dict);
			} else {
				return null;
			}
		}
		if (!partial.isEmpty())
			return super.findMatches(partial);
		return null;
	}
}
