package com.alchitry.labs.widgets;

import org.eclipse.swt.widgets.Composite;


public interface TabChild {
	public void setVisible(boolean visible);
	public boolean isModified();
	public void dispose();
	public void close();
	public void setBounds(int x, int y, int w, int h);
	public boolean forceFocus();
	public void switchFolder(CustomTabs folder);
	public Composite getParent();
}
