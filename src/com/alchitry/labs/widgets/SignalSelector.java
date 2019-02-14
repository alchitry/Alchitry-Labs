package com.alchitry.labs.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.alchitry.labs.gui.Theme;

public class SignalSelector extends Canvas {
	private static final int PADDING = 10;

	private Font titleFont;
	private IoRegion region;
	private int signalColumnMinWidth;
	private int portColumnMinWidth;
	private int rowHeight;
	private int minWidth;
	private int minHeight;

	public SignalSelector(Composite parent) {
		super(parent, SWT.NONE);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				onDraw(e);
			}
		});

		FontData fd[] = Theme.defaultFont.getFontData();
		fd[0].setHeight(20);
		titleFont = new Font(getDisplay(), fd[0]);

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				titleFont.dispose();
			}
		});

		updateCache();
	}

	private void updateCache() {
		GC gc = new GC(this);
		gc.setFont(Theme.defaultFont);

		rowHeight = gc.stringExtent("H").y + PADDING;

		if (region != null && region.signals != null) {
			signalColumnMinWidth = maxStringWidth(region.signals, gc);
		}
		portColumnMinWidth = 100;

		int newMinWidth = portColumnMinWidth + signalColumnMinWidth + PADDING * 2;

		boolean notify = false;

		if (newMinWidth != minWidth) {
			minWidth = newMinWidth;
			notify = true;
		}

		int newMinHeight = 0;
		newMinHeight += PADDING;
		gc.setFont(titleFont);
		newMinHeight += gc.textExtent("H").y + PADDING;

		if (region != null) {
			gc.setFont(Theme.boldFont);
			newMinHeight += gc.textExtent("S").y + PADDING;
			if (region.signals != null) {
				newMinHeight += rowHeight * region.signals.length;
			}
			newMinHeight += PADDING;
		}

		if (newMinHeight != minHeight) {
			minHeight = newMinHeight;
			notify = true;
		}

		if (notify) {
			Event e = new Event();
			e.width = minWidth;
			e.height = minHeight;
			notifyListeners(SWT.Modify, e);
		}

	}

	public void setIoRegion(IoRegion region) {
		this.region = region;
		updateCache();
	}

	private int maxStringWidth(String[] list, GC gc) {
		int width = 0;
		for (String s : list)
			width = Math.max(gc.stringExtent(s).x, width);
		return width;
	}

	private void onDraw(PaintEvent e) {
		e.gc.setBackground(Theme.editorBackgroundColor);
		e.gc.setForeground(Theme.editorForegroundColor);

		Point size = getSize();
		drawBackground(e.gc, 0, 0, size.x, size.y);

		int px = PADDING;
		int py = PADDING;

		e.gc.setFont(titleFont);
		String title = region != null ? region.name : "Signal Selector";
		e.gc.drawText(title, px, py);

		Point ext = e.gc.stringExtent(title);
		py += ext.y;
		py += PADDING;

		if (region != null) {
			int sigColWidth = Math.max((getSize().x - PADDING * 2) / 2, signalColumnMinWidth);
			px += PADDING;
			int px2 = PADDING * 2 + sigColWidth;
			e.gc.drawLine(PADDING + sigColWidth, py, PADDING + sigColWidth, size.y - PADDING);
			e.gc.setFont(Theme.boldFont);
			e.gc.drawText("Signal", px, py);
			e.gc.drawText("Port", px2, py);
			py += e.gc.stringExtent("Signal").y + PADDING / 2;
			e.gc.drawLine(PADDING, py, size.x - PADDING, py);
			py += PADDING / 2;

			if (region.signals != null) {
				e.gc.setFont(Theme.defaultFont);
				for (String s : region.signals) {
					e.gc.drawText(s, px, py + PADDING / 2);
					py += rowHeight;
				}
			}
		}
	}

}
