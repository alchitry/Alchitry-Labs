package com.alchitry.labs.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.alchitry.labs.gui.Theme;

public class CustomCombo extends Canvas {
	private String[] items;
	private int selection;
	private static final int[] TRIANGLE = new int[] { 0, 0, 16, 0, 8, 15 };
	private int[] pts = new int[TRIANGLE.length];
	private Menu menu;

	public CustomCombo(Composite parent, int style) {
		super(parent, style);

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				onDraw(e);
			}
		});

		addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				openMenu();
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		menu = new Menu(this);
		menu.setVisible(false);

		setBackground(Theme.comboBackgroundColor);
		setForeground(Theme.comboBackgroundColor);

		selection = -1;
	}

	public void select(int i) {
		if (items != null && i >= 0 && i < items.length)
			selection = i;
		else
			selection = -1;
		redraw();

		notifyListeners(SWT.Selection, null);
	}

	public String getSelection() {
		if (selection < 0)
			return null;
		return items[selection];
	}

	public void setItems(String[] items) {
		this.items = items;
		select(selection); // update selection
	}

	public String[] getItems() {
		return items;
	}

	private void onDraw(PaintEvent e) {
		e.gc.setBackground(Theme.comboBackgroundColor);
		e.gc.setForeground(Theme.editorForegroundColor);
		
		Point s = getSize();

		e.gc.setClipping(0, 0, s.x - 25, s.y);

		if (selection >= 0){
			Point ts = e.gc.stringExtent(items[selection]);
			
			e.gc.drawText(items[selection], 5, (s.y - ts.y)/2);
		}

		e.gc.setClipping(0, 0, s.x, s.y);

		int w = s.x - 20;
		int h = (s.y - 15)/2;

		for (int i = 0; i < TRIANGLE.length; i += 2) {
			pts[i] = TRIANGLE[i] + w;
			pts[i + 1] = TRIANGLE[i + 1] + h;
		}

		e.gc.setBackground(Theme.commentColor);
		e.gc.fillPolygon(pts);

		e.gc.setBackground(Theme.comboBackgroundColor);
		e.gc.setForeground(Theme.comboBackgroundColor);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(wHint, 25);
	}

	private void openMenu() {
		notifyListeners(SWT.Arm, null);

		menu.setLocation(toDisplay(0, 0));
		for (MenuItem i : menu.getItems())
			i.dispose();
		for (String item : items) {
			MenuItem mi = new MenuItem(menu, SWT.PUSH);
			mi.setText(item);
			mi.addListener(SWT.Selection, menuSelection);
		}
		menu.setVisible(true);
	}

	private Listener menuSelection = new Listener() {

		@Override
		public void handleEvent(Event event) {
			String t = ((MenuItem) event.widget).getText();
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(t)) {
					select(i);
					return;
				}
			}
		}
	};

}
