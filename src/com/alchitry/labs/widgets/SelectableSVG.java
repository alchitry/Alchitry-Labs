package com.alchitry.labs.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.alchitry.labs.gui.Theme;

public class SelectableSVG extends Canvas {
	private ResizingImage svgImage;
	private List<Region> selectableRegions;
	private Rectangle imageBounds;
	private Point mouseLocation;

	public SelectableSVG(Composite parent, String svg, Region[] regions) {
		super(parent, SWT.DOUBLE_BUFFERED);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				onDraw(e);
			}
		});

		svgImage = new ResizingImage(svg);
		svgImage.addImageChangedListener(new ImageChangedListener() {
			@Override
			public void onImageChanged() {
				redraw();
			}
		});

		imageBounds = new Rectangle(0, 0, 0, 0);
		mouseLocation = new Point(-1, -1);

		selectableRegions = new ArrayList<Region>();
		for (Region r : regions)
			selectableRegions.add(r);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				updateMouseLocation(e.x, e.y);
				for (Region region : selectableRegions) {
					if (region.active) {
						if (region.hover) {
							if (!region.selected)
								for (Region r : selectableRegions)
									r.selected = false;
							region.selected = !region.selected;
							Event event = new Event();
							event.button = region.selected ? region.id : -1;
							notifyListeners(SWT.Selection, event);
						}

						region.active = false;
						redraw();
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				updateMouseLocation(e.x, e.y);
				for (Region region : selectableRegions) {
					if (region.bounds.contains(mouseLocation)) {
						if (!region.active) {
							region.active = true;
							redraw();
						}
					} else {
						if (region.active) {
							region.active = false;
							redraw();
						}
					}
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent e) {
				updateMouseLocation(e.x, e.y);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				updateMouseLocation(-1, -1);
			}

			@Override
			public void mouseEnter(MouseEvent e) {
				updateMouseLocation(e.x, e.y);
			}
		});

		addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				updateMouseLocation(e.x, e.y);
			}
		});
	}

	public double getAspectRatio() {
		return svgImage.getAspectRatio();
	}

	private void updateMouseLocation(int x, int y) {
		mouseLocation.x = x;
		mouseLocation.y = y;
		for (Region region : selectableRegions) {
			if (region.bounds.contains(mouseLocation)) {
				if (!region.hover) {
					region.hover = true;
					redraw();
				}
			} else {
				if (region.hover) {
					region.hover = false;
					redraw();
				}
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		svgImage.dispose();
	}

	@Override
	public void setSize(Point size) {
		super.setSize(size);
		svgImage.setSize(size.x, size.y);
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		svgImage.setSize(x, y);
	}

	private void onDraw(PaintEvent e) {
		e.gc.setBackground(Theme.editorBackgroundColor);
		e.gc.setForeground(Theme.editorBackgroundColor);
		Point canvasSize = getSize();
		e.gc.fillRectangle(0, 0, canvasSize.x, canvasSize.y);

		Image image = svgImage.getImage();
		double aspectRatio = svgImage.getAspectRatio();

		if (image != null) {
			Rectangle imgSize = image.getBounds();
			if (canvasSize.y * aspectRatio > canvasSize.x)
				canvasSize.y = (int) (canvasSize.x / aspectRatio);
			else
				canvasSize.x = (int) (canvasSize.y * aspectRatio);

			e.gc.drawImage(image, 0, 0, imgSize.width, imgSize.height, 0, 0, canvasSize.x, canvasSize.y);
			imageBounds.x = 0;
			imageBounds.y = 0;
			imageBounds.width = canvasSize.x;
			imageBounds.height = canvasSize.y;
			for (Region r : selectableRegions)
				r.updateBounds(imageBounds);
		}

		e.gc.setBackground(Theme.editorForegroundColor);
		e.gc.setForeground(Theme.editorForegroundColor);
		e.gc.setLineWidth(3);
		for (Region region : selectableRegions) {
			if (region.selected) {
				e.gc.setForeground(Theme.mainAccentColor);
				e.gc.setBackground(Theme.mainAccentColor);
			} else if (region.hover) {
				e.gc.setBackground(Theme.editorForegroundColor);
				if (region.active)
					e.gc.setForeground(Theme.mainAccentColor);
				else
					e.gc.setForeground(Theme.editorForegroundColor);

			}
			if (region.selected || region.hover) {
				Rectangle rec = region.bounds;
				e.gc.setAlpha(150);
				e.gc.fillRectangle(rec);
				e.gc.setAlpha(255);
				e.gc.drawRectangle(rec);
			}
		}
	}

}
