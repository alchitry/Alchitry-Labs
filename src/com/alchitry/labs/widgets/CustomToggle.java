package com.alchitry.labs.widgets;

import com.alchitry.labs.gui.Theme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class CustomToggle extends Canvas {
	private int mouse = 0;
	private boolean hit = false;
	private Image icon, iconHover;
	private boolean checked = false;

	public CustomToggle(Composite parent, int style) {
		super(parent, style);

		if (Theme.set) {
			setBackground(Theme.windowBackgroundColor);
			setForeground(Theme.windowForegroundColor);
		}

		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				CustomToggle.this.paintControl(e);
			}
		});
		this.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				CustomToggle.this.mouseMove(e);
			}
		});
		this.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent e) {
				CustomToggle.this.mouseEnter(e);
			}

			public void mouseExit(MouseEvent e) {
				CustomToggle.this.mouseExit(e);
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				CustomToggle.this.mouseDown(e);
			}

			public void mouseUp(MouseEvent e) {
				CustomToggle.this.mouseUp(e);
			}
		});
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				CustomToggle.this.keyPressed(e);
			}
		});

	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean state) {
		checked = state;
		redraw();
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public void setIconHover(Image icon) {
		this.iconHover = icon;
	}

	private void paintControl(PaintEvent e) {
		switch (mouse) {
		case 0:
			if (Theme.set)
				if (checked)
					e.gc.setBackground(Theme.toolBarHoverColor);
				else
					e.gc.setBackground(getBackground());
			e.gc.fillRectangle(getClientArea());
			if (icon != null)
				e.gc.drawImage(icon, 0, 0);
			break;
		case 1:
			if (Theme.set)
				e.gc.setBackground(Theme.toolBarHoverColor);
			e.gc.fillRectangle(getClientArea());
			if (iconHover != null)
				e.gc.drawImage(iconHover, 0, 0);
			break;
		case 2:
			if (Theme.set)
				e.gc.setBackground(Theme.toolBarClickColor);
			e.gc.fillRectangle(getClientArea());
			if (iconHover != null)
				e.gc.drawImage(iconHover, 0, 0);
			break;
		}

	}

	private void mouseMove(MouseEvent e) {
		if (!hit)
			return;
		mouse = 2;
		if (e.x < 0 || e.y < 0 || e.x > getBounds().width || e.y > getBounds().height) {
			mouse = 0;
		}
		redraw();
	}

	private void mouseEnter(MouseEvent e) {
		mouse = 1;
		redraw();
	}

	private void mouseExit(MouseEvent e) {
		mouse = 0;
		redraw();
	}

	private void mouseDown(MouseEvent e) {
		hit = true;
		mouse = 2;
		redraw();
	}

	private void mouseUp(MouseEvent e) {
		hit = false;
		mouse = 1;
		if (e.x < 0 || e.y < 0 || e.x > getBounds().width || e.y > getBounds().height) {
			mouse = 0;
		}
		redraw();
		if (mouse == 1) {
			checked = !checked;
			Event ev = new Event();
			ev.button = (e.stateMask & SWT.SHIFT) != 0 ? 2 : 1;
			notifyListeners(SWT.Selection, ev);
		}
	}

	private void keyPressed(KeyEvent e) {
		if (e.keyCode == '\r' || e.character == ' ') {
			checked = !checked;
			Event event = new Event();
			notifyListeners(SWT.Selection, event);
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(25, 25);
	}
}
