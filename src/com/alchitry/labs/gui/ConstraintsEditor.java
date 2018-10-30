package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.alchitry.labs.Util;
import com.alchitry.labs.widgets.CustomTabs;
import com.alchitry.labs.widgets.SVGTest;
import com.alchitry.labs.widgets.TabChild;

public class ConstraintsEditor extends Composite implements TabChild {
	private CustomTabs tabFolder;
	private SVGTest svg;

	public ConstraintsEditor(CustomTabs parent) {
			super(parent, SWT.NONE);
			setBackground(Theme.editorBackgroundColor);
			setForeground(Theme.editorForegroundColor);
			tabFolder = parent;
			tabFolder.addTab("Wave Capture", this);

			addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					ConstraintsEditor.this.controlResized(e);
				}
			});

			svg = new SVGTest(this);

			resize();
		}

	protected void controlResized(ControlEvent e) {
		resize();
	}

	private void resize() {
		svg.setSize(getSize());
		svg.setLocation(0,0);
	}

	public void grabFocus() {
		tabFolder.setSelection(this);
		setFocus();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void close() {
		tabFolder.close(this);
	}

	@Override
	public void switchFolder(CustomTabs folder) {
		if (folder != tabFolder) {
			if (!setParent(folder)) {
				Util.showError("Moving tabs between windows isn't supported on your platform!");
			}
			tabFolder.remove(this);
			tabFolder = folder;
			tabFolder.addTab("Constraints", this);
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int width = 300;
		int height = 300;

		if (wHint != SWT.DEFAULT)
			width = wHint;
		if (hHint != SWT.DEFAULT)
			height = hHint;
		return new Point(width, height);
	}

}