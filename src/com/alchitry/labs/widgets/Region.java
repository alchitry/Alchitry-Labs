package com.alchitry.labs.widgets;

import org.eclipse.swt.graphics.Rectangle;

public class Region {
	public double x;
	public double y;
	public double width;
	public double height;
	public boolean hover;
	public boolean active;
	public boolean selected;
	public Rectangle bounds;
	public final int id;

	public interface ClickListener {
		public void onClick(int id);
	}

	public Region(int id, double x, double y, double width, double height) {
		set(x, y, width, height);
		this.id = id;
		hover = false;
		active = false;
		selected = false;
		bounds = new Rectangle(0, 0, 0, 0);
	}

	public void set(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void updateBounds(Rectangle imageBounds) {
		bounds.x = (int) (imageBounds.x + x * imageBounds.width);
		bounds.y = (int) (imageBounds.y + y * imageBounds.height);
		bounds.width = (int) (width * imageBounds.width);
		bounds.height = (int) (height * imageBounds.height);
	}
}
