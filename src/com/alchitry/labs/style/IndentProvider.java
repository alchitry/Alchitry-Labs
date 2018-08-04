package com.alchitry.labs.style;

import com.alchitry.labs.gui.StyledCodeEditor;

public interface IndentProvider {
	public void updateIndentList(StyledCodeEditor editor);
	public int getTabs(int lineNum);

}
