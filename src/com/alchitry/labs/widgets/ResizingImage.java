package com.alchitry.labs.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.alchitry.labs.Util;

public class ResizingImage {
	private Image image;
	private Timer timer;
	private TimerTask resizeTask;
	private Point size;
	private double aspectRatio = 1;
	private String svgFile;
	private List<ImageChangedListener> listeners;

	public ResizingImage(String svgFile) {
		this.svgFile = svgFile;
		timer = new Timer();
		listeners = new ArrayList<>();
		size = new Point(0, 0);
	}

	public void dispose() {
		timer.cancel();
		if (image != null)
			image.dispose();
	}

	public void addImageChangedListener(ImageChangedListener listener) {
		listeners.add(listener);
	}

	public void removeImageChangedListener(ImageChangedListener listener) {
		listeners.remove(listener);
	}

	public Image getImage() {
		return image;
	}
	
	public double getAspectRatio() {
		return aspectRatio;
	}

	public void setSize(int x, int y) {
		size.x = x;
		size.y = y;
		if (resizeTask != null)
			resizeTask.cancel();
		resizeTask = new TimerTask() {
			@Override
			public void run() {
				resizeImage();
			}
		};
		timer.schedule(resizeTask, 250);
	}

	private void resizeImage() {
		int width = 0;
		int height = 0;

		if (size.x * aspectRatio > size.y)
			height = size.y;
		else
			width = size.x;

		final Image newImage = Util.svgToImage(svgFile, width, height);

		Rectangle bounds = newImage.getBounds();
		double newAspectRatio = (double) bounds.width / (double) bounds.height;
		double change = Math.abs(newAspectRatio - aspectRatio) / newAspectRatio;
		aspectRatio = newAspectRatio;
		
		if (change > 0.005) {
			newImage.dispose();
			resizeImage();
		} else {
			Util.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (image != null)
						image.dispose();
					image = newImage;
					for (ImageChangedListener listener : listeners)
						listener.onImageChanged();
				}
			});
		}
	}
}
