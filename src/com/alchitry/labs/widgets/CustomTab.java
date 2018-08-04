package com.alchitry.labs.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.MainWindow;
import com.alchitry.labs.gui.Theme;

public class CustomTab extends Canvas {
	private static final int NO_MOUSE = 0;
	private static final int MOUSE_HOVER_TAB = 1;
	private static final int MOUSE_CLICK_TAB = 2;
	private static final int MOUSE_HOVER_X = 3;
	private static final int MOUSE_CLICK_X = 4;

	private int mouse = NO_MOUSE;
	private boolean hit = false;
	private String text;
	private Color hoverColor, clickColor;
	private Integer index;
	private CustomTabs folder;

	public CustomTab(CustomTabs parent, int style, int index) {
		super(parent, style);
		folder = parent;

		this.index = index;

		if (Theme.set) {
			setBackground(Theme.windowBackgroundColor);
			setForeground(Theme.windowForgroundColor);
			hoverColor = Theme.mainAccentColor;
			clickColor = Theme.darkAccentColor;
		}

		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				CustomTab.this.paintControl(e);
			}
		});
		this.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				CustomTab.this.mouseMove(e);
			}
		});
		this.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent e) {
				CustomTab.this.mouseEnter(e);
			}

			public void mouseExit(MouseEvent e) {
				CustomTab.this.mouseExit(e);
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				CustomTab.this.mouseDown(e);
			}

			public void mouseUp(MouseEvent e) {
				CustomTab.this.mouseUp(e);
			}
		});
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				CustomTab.this.keyPressed(e);
			}
		});

		DragSource dnd = new DragSource(this, DND.DROP_MOVE | DND.DROP_COPY);
		Transfer[] types = new Transfer[] { TabTransfer.getInstance() };
		dnd.setTransfer(types);

		dnd.addDragListener(new DragSourceListener() {
			GC gc;
			Image image;

			@Override
			public void dragStart(DragSourceEvent event) {
				// getting control widget - Composite in this case
				Composite composite = (Composite) ((DragSource) event.getSource()).getControl();
				// Getting dimensions of this widget
				Point compositeSize = composite.getSize();
				// creating new GC
				gc = new GC(composite);
				// Creating new Image
				image = new Image(Display.getCurrent(), compositeSize.x, compositeSize.y);
				// Rendering widget to image
				gc.copyArea(image, 0, 0);
				// Setting widget to DnD image
				event.image = image;
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				if (TabTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = MainWindow.mainWindow.getIndex(folder.getTabChild(CustomTab.this.index));
				}
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				if (gc != null) {
					gc.dispose();
					gc = null;
				}

				if (image != null) {
					image.dispose();
					image = null;
				}
			}
		});
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	private void paintControl(PaintEvent e) {
		paint(e.gc);
	}

	private void paint(GC gc) {
		Point ts = gc.stringExtent(text);
		int iconHeight = (getSize().y - 13) / 2;

		if (Theme.set)
			gc.setForeground(Theme.tabNormalTextColor);

		switch (mouse) {
		case NO_MOUSE:
			gc.setForeground(getForeground());
			if (Theme.set)
				gc.setBackground(getBackground());
			gc.fillRectangle(getClientArea());
			break;
		case MOUSE_HOVER_TAB:
			if (Theme.set) {
				gc.setBackground(hoverColor);
				gc.setForeground(Theme.tabHoverTextColor);
			}
			gc.fillRectangle(getClientArea());
			gc.drawImage(Images.xIcon, ts.x + 12, iconHeight);
			break;
		case MOUSE_CLICK_TAB:
			if (Theme.set) {
				gc.setBackground(clickColor);
				gc.setForeground(Theme.tabHoverTextColor);
			}
			gc.fillRectangle(getClientArea());
			gc.drawImage(Images.xIcon, ts.x + 12, iconHeight);
			break;
		case MOUSE_HOVER_X:
			if (Theme.set) {
				gc.setBackground(hoverColor);
				gc.setForeground(Theme.tabHoverTextColor);
			}
			gc.fillRectangle(getClientArea());
			gc.drawImage(Images.xGreyIcon, ts.x + 12, iconHeight);
			break;
		case MOUSE_CLICK_X:
			if (Theme.set) {
				gc.setBackground(clickColor);
				gc.setForeground(Theme.tabHoverTextColor);
			}
			gc.fillRectangle(getClientArea());
			gc.drawImage(Images.xRedIcon, ts.x + 12, iconHeight);
			break;
		}

		if (text != null) {
			Point textSize = gc.stringExtent(text);
			gc.drawText(text, 6, (25 - textSize.y)/2);
		}
	}

	private void mouseMove(MouseEvent e) {
		Rectangle bounds = getBounds();
		if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
			mouse = NO_MOUSE;
		} else if (e.x < bounds.width - (9 + 13)) {
			mouse = hit ? MOUSE_CLICK_TAB : MOUSE_HOVER_TAB;
		} else {
			mouse = hit ? MOUSE_CLICK_X : MOUSE_HOVER_X;
		}
		redraw();
	}

	private void mouseEnter(MouseEvent e) {
		Rectangle bounds = getBounds();
		if (e.x < 0 || e.y < 0 || e.x > bounds.width || e.y > bounds.height) {
			mouse = NO_MOUSE;
		} else if (e.x < bounds.width - (9 + 13)) {
			mouse = MOUSE_HOVER_TAB;
		} else {
			mouse = MOUSE_HOVER_X;
		}
		redraw();
	}

	private void mouseExit(MouseEvent e) {
		mouse = NO_MOUSE;
		redraw();
	}

	private void mouseDown(MouseEvent e) {
		hit = true;
		if (mouse == MOUSE_HOVER_TAB)
			mouse = MOUSE_CLICK_TAB;
		else if (mouse == MOUSE_HOVER_X)
			mouse = MOUSE_CLICK_X;
		redraw();
	}

	private void mouseUp(MouseEvent e) {
		hit = false;

		if (e.x < 0 || e.y < 0 || e.x > getBounds().width || e.y > getBounds().height) {
			mouse = NO_MOUSE;
		} else if (mouse == MOUSE_CLICK_TAB)
			mouse = MOUSE_HOVER_TAB;
		else if (mouse == MOUSE_CLICK_X)
			mouse = MOUSE_HOVER_X;

		redraw();
		if (mouse == MOUSE_HOVER_TAB)
			notifyListeners(SWT.Selection, new Event());
		else if (mouse == MOUSE_HOVER_X)
			notifyListeners(SWT.Close, new Event());
	}

	private void keyPressed(KeyEvent e) {
		if (e.keyCode == '\r' || e.character == ' ') {
			Event event = new Event();
			notifyListeners(SWT.Selection, event);
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if (text != null) {
			GC gc = new GC(this);
			Point textSize = gc.stringExtent(text);
			gc.dispose();
			return new Point(textSize.x + 18 + 13, 25);
		}
		return new Point(25, 25);
	}
}
