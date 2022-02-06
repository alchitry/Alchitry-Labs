package com.alchitry.labs.style;

import com.alchitry.labs.gui.StyledCodeEditor;

public class AutoFormatter {
	private StyledCodeEditor editor;
	private IndentProvider indentProvider;

	public AutoFormatter(StyledCodeEditor editor, IndentProvider indent) {
		this.editor = editor;
		indentProvider = indent;
	}

	public void fixIndent() {
		/*if (editor.hasSyntaxErrors()){
			Util.println("Please fix errors before auto-formatting", true);
			return;
		}*/
		StringBuilder builder = new StringBuilder();
		indentProvider.updateIndentList(editor);
		String[] lines = editor.getText().split("(?:\r)?\n");
		for (int lineNum = 0; lineNum < lines.length; lineNum++) {
			int indent = indentProvider.getTabs(lineNum);
			builder.append(appendSpaces(lines[lineNum], indent) + System.lineSeparator());
		}
		int separatorLen = System.lineSeparator().length();
		builder.delete(builder.length() - separatorLen, builder.length());
		editor.replaceTextRange(0, editor.getCharCount(), builder.toString());
	}

	private String appendSpaces(String line, int spaces) {
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < spaces; i++) {
			text.append(' ');
		}
		text.append(line.trim());
		return text.toString();
	}

	
}