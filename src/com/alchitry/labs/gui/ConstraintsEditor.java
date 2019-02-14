package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.alchitry.labs.Util;
import com.alchitry.labs.boards.AlchitryAu;
import com.alchitry.labs.boards.Board;
import com.alchitry.labs.widgets.CustomTabs;
import com.alchitry.labs.widgets.IoRegion;
import com.alchitry.labs.widgets.SelectableSVG;
import com.alchitry.labs.widgets.SignalSelector;
import com.alchitry.labs.widgets.TabChild;

public class ConstraintsEditor extends Composite implements TabChild {
	private CustomTabs tabFolder;
	private SelectableSVG svg;
	private SignalSelector selector;
	private ScrolledComposite selectorSC;
	private Board board;

	public ConstraintsEditor(CustomTabs parent) {
		super(parent, SWT.NONE);
		setBackground(Theme.editorBackgroundColor);
		setForeground(Theme.editorForegroundColor);
		tabFolder = parent;
		tabFolder.addTab("Constraints Editor", this);

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				ConstraintsEditor.this.controlResized(e);
			}
		});

		board = new AlchitryAu();

		svg = new SelectableSVG(this, board.getSVGPath(), board.getIoRegions());

		selectorSC = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		selectorSC.setMinSize(400, 400);
		selectorSC.setAlwaysShowScrollBars(false);
		selectorSC.setExpandHorizontal(true);
		selectorSC.setExpandVertical(true);

		selector = new SignalSelector(selectorSC);

		selectorSC.setContent(selector);

		selector.addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				selectorSC.setMinSize(event.width, event.height);
			}
		});

		svg.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.button >= 0) {
					IoRegion region = board.getIoRegions()[event.button];
					selector.setIoRegion(region);
				} else {
					selector.setIoRegion(null);
				}
				selector.redraw();
			}
		});

		resize();

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				svg.dispose();
			}
		});
	}

	protected void controlResized(ControlEvent e) {
		resize();
	}

	private void resize() {
		Point size = getSize();
		double aspectRatio = svg.getAspectRatio();

		int width;
		int height;

		if (size.x * aspectRatio > size.y) {
			height = size.y;
			width = (int) (height * aspectRatio);
		} else {
			width = size.x;
			height = (int) (width / aspectRatio);
		}

		width = Math.min(width, size.x - 300);

		Point svgSize = new Point(width, height);

		svg.setSize(svgSize.x - 20, svgSize.y - 20);
		svg.setLocation(10, 10);

		selectorSC.setLocation(svgSize.x, 0);
		selectorSC.setSize(size.x - svgSize.x, size.y);
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