package com.alchitry.labs.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.alchitry.labs.gui.Theme;

public class CustomTable extends Canvas {
	private static final int PADDING_X = 8;
	private static final int PADDING_Y = 4;

	private static final int NO_MOUSE = 0;
	private static final int MOUSE_HOVER = 1;
	private static final int MOUSE_CLICK = 2;

	private int mouse = NO_MOUSE;
	private boolean hit = false;
	private List<String> items;
	private Color highlightColor;
	private int selection;
	private int rowHeight;

	public CustomTable(Composite parent, int style) {
		super(parent, style);
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				CustomTable.this.paintControl(e);
			}
		});
		this.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				CustomTable.this.mouseMove(e);
			}
		});
		this.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent e) {
				CustomTable.this.mouseEnter(e);
			}

			public void mouseExit(MouseEvent e) {
				CustomTable.this.mouseExit(e);
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				CustomTable.this.mouseDown(e);
			}

			public void mouseUp(MouseEvent e) {
				CustomTable.this.mouseUp(e);
			}
		});
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				CustomTable.this.keyPressed(e);
			}
		});

		if (Theme.set)
			highlightColor = Theme.autocompleteHighlightColor;

		selection = -1;
	}

	public void resetSelection() {
		selection = -1;
		redraw();
	}

	public int getItemCount() {
		return items.size();
	}

	public void setSelection(int s) {
		selection = s;
		redraw();
	}

	public void incrementSelection() {
		selection++;
		selection %= items.size();
		redraw();
	}

	public void decrementSelection() {
		selection--;
		if (selection < 0)
			selection = items.size() - 1;
		redraw();
	}

	public int getSelectionIndex() {
		return selection;
	}

	public void setItems(List<String> items) {
		this.items = items;
		redraw();
	}

	public List<String> getItems() {
		return items;
	}

	public String getItem(int i) {
		return items.get(i);
	}

	public String getSelectedItem() {
		if (selection == -1)
			return null;
		return items.get(selection);
	}

	private void paintControl(PaintEvent e) {
		paint(e.gc);
	}

	private void paint(GC gc) {
		gc.setForeground(getForeground());
		gc.setBackground(getBackground());

		int yOffset = 0;
		Rectangle bounds = getClientArea();
		gc.fillRectangle(bounds);

		for (int i = 0; i < items.size(); i++) {
			String line = items.get(i);
			Point se = gc.stringExtent(line);
			if (i == selection) {
				gc.setBackground(highlightColor);
				gc.fillRectangle(0, yOffset, bounds.width, se.y + 2 * PADDING_Y);
			} else {
				gc.setBackground(getBackground());
			}
			gc.drawText(line, PADDING_X, yOffset + PADDING_Y);
			yOffset += se.y + PADDING_Y * 2;
		}
	}

	private void mouseMove(MouseEvent e) {
		Rectangle bounds = getBounds();
		if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
			mouse = NO_MOUSE;
		} else {
			selection = e.y / rowHeight;
			mouse = hit ? MOUSE_CLICK : MOUSE_HOVER;
		}
		redraw();
	}

	private void mouseEnter(MouseEvent e) {
		Rectangle bounds = getBounds();
		if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
			mouse = NO_MOUSE;
		} else {
			selection = e.y / rowHeight;
			mouse = MOUSE_HOVER;
		}
		redraw();
	}

	private void mouseExit(MouseEvent e) {
		mouse = NO_MOUSE;
		redraw();
	}

	private void mouseDown(MouseEvent e) {
		hit = true;
		if (mouse == MOUSE_HOVER)
			mouse = MOUSE_CLICK;
		redraw();
	}

	private void mouseUp(MouseEvent e) {
		hit = false;

		if (e.x < 0 || e.y < 0 || e.x > getBounds().width || e.y > getBounds().height) {
			mouse = NO_MOUSE;
		} else if (mouse == MOUSE_CLICK) {
			selection = e.y / rowHeight;
			mouse = MOUSE_HOVER;
		}

		redraw();
		if (mouse == MOUSE_HOVER)
			notifyListeners(SWT.Selection, new Event());
	}

	public boolean keyPressed(int keyCode) {
		switch (keyCode) {
		case SWT.ARROW_DOWN:
			incrementSelection();
			return true;
		case SWT.ARROW_UP:
			decrementSelection();
			return true;
		case SWT.CR:
		case SWT.KEYPAD_CR:
			Event event = new Event();
			notifyListeners(SWT.Selection, event);
			return true;
		}
		return false;
	}

	public void keyPressed(KeyEvent e) {
		keyPressed(e.keyCode);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if (items.size() > 0) {
			GC gc = new GC(this);
			int width = 0;
			int height = 0;
			for (String s : items) {
				Point textSize = gc.stringExtent(s);
				width = Math.max(width, textSize.x);
				height += textSize.y + PADDING_Y * 2;
			}
			rowHeight = height / items.size();
			return new Point(width + 2 * PADDING_X, height);
		}
		return new Point(25, 25);
	}

	public void setHighlightColor(Color c) {
		highlightColor = c;
	}

}
