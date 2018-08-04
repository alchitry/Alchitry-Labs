package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.alchitry.labs.Util;
import com.alchitry.labs.widgets.CustomTabs;
import com.alchitry.labs.widgets.TabChild;
import com.alchitry.labs.widgets.TabHotKeys;
import com.alchitry.labs.widgets.Waves;

public class WaveForm extends Composite implements TabChild {
	private CustomTabs tabFolder;
	private SashForm sash;
	private Tree tree;
	private Waves waves;

	private int leftWidth;
	private int oldWeight;

	private boolean isSimulation;

	public WaveForm(CustomTabs parent, boolean isSim) {
		super(parent, SWT.NONE);
		setBackground(Theme.editorBackgroundColor);
		setForeground(Theme.editorForegroundColor);
		tabFolder = parent;
		tabFolder.addTab("Wave Capture", this);

		isSimulation = isSim;

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				WaveForm.this.controlResized(e);
			}
		});

		if (isSimulation)
			createSimContents();
		else
			createLiveContents();

		resize();
	}

	protected void controlResized(ControlEvent e) {
		resize();
	}

	private void resize() {
		if (isSimulation) {
			sash.setSize(getSize());
			sash.setLocation(0, 0);
		} else {
			waves.setSize(getSize());
			waves.setLocation(0,0);
		}
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
			tabFolder.addTab("Wave Capture", this);
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

	private void createSimContents() {
		leftWidth = 200;

		sash = new SashForm(this, SWT.HORIZONTAL);
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sash.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int width = sash.getClientArea().width;
				int[] weights = sash.getWeights();

				double perLeft = (double) leftWidth / (double) width;

				if (perLeft < 0.8) {
					weights[0] = (int) (perLeft * 1000.0);
					weights[1] = 1000 - weights[0];
				} else {
					weights[0] = 800;
					weights[1] = 200;
				}

				// oldWeights must be set before form.setWeights
				oldWeight = weights[0];
				sash.setWeights(weights);
			}
		});
		sash.setBackground(Theme.windowBackgroundColor);

		tree = new Tree(sash, SWT.NONE);
		tree.setBackground(Theme.editorBackgroundColor);
		tree.setForeground(Theme.editorForegroundColor);

		tree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int[] weights = sash.getWeights();
				if (oldWeight != weights[0]) {
					oldWeight = weights[0];
					leftWidth = (int) Math.round((double) sash.getClientArea().width * (double) weights[0] / (double) (weights[0] + weights[1]));
				}
			}
		});

		tree.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				if ((event.detail & SWT.SELECTED) != 0) {
					GC gc = event.gc;

					Rectangle rect = event.getBounds();
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					if (tree.isFocusControl())
						gc.setBackground(Theme.treeSelectedFocusedColor);
					else
						gc.setBackground(Theme.treeSelectedColor);
					gc.setForeground(Theme.editorForegroundColor);
					gc.fillRectangle(rect);
					// restore colors for subsequent drawing
					gc.setForeground(foreground);
					gc.setBackground(background);
					event.detail &= ~SWT.SELECTED;
				}
			}
		});

		new TreeItem(tree, SWT.NONE).setText("Item 1");
		new TreeItem(tree, SWT.NONE).setText("Item 2");
		new TreeItem(tree, SWT.NONE).setText("Item 3");
		new TreeItem(tree, SWT.NONE).setText("Item 4");

		waves = new Waves(sash, isSimulation);

		KeyListener hkl = new TabHotKeys(this);
		tree.addKeyListener(hkl);
		sash.addKeyListener(hkl);
		waves.addKeyListener(hkl);
	}

	private void createLiveContents() {
		waves = new Waves(this, isSimulation);

		KeyListener hkl = new TabHotKeys(this);
		waves.addKeyListener(hkl);
	}

}
