package com.alchitry.labs.widgets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.MainWindow;
import com.alchitry.labs.gui.Theme;

public class SVGImage extends Canvas {
	private Image image;
	private Timer timer;
	private TimerTask resizeTask;
	private Point size;
	private double aspectRatio = 1;

	public SVGImage(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				onDraw(e);
			}
		});

		timer = new Timer();
	}

	@Override
	public void dispose() {
		super.dispose();
		timer.cancel();
	}

	@Override
	public void setSize(Point size) {
		super.setSize(size);
		this.size = size;
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
	
	public double getAspectRatio() {
		return aspectRatio;
	}

	private void resizeImage() {
		ByteArrayOutputStream resultByteStream = new ByteArrayOutputStream();
		TranscoderInput input = new TranscoderInput(MainWindow.class.getResourceAsStream("/images/" + "mojo_v3.svg"));
		TranscoderOutput output = new TranscoderOutput(resultByteStream);

		PNGTranscoder png = new PNGTranscoder();

		if (size.x * aspectRatio > size.y)
			png.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) size.y);
		else
			png.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) size.x);

		try {
			png.transcode(input, output);
		} catch (TranscoderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			resultByteStream.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ByteArrayInputStream imgstream = new ByteArrayInputStream(resultByteStream.toByteArray());

		final Image newImage = new Image(Util.getDisplay(), imgstream);

		Rectangle bounds = newImage.getBounds();
		double newAspectRatio = (double) bounds.width / (double) bounds.height;
		double change = Math.abs(newAspectRatio - aspectRatio) / newAspectRatio;
		aspectRatio = newAspectRatio;
		if (change > 0.005) {
			resizeImage();
		} else {
			Util.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (image != null)
						image.dispose();
					image = newImage;
					redraw();
				}
			});
		}
	}

	private void onDraw(PaintEvent e) {
		e.gc.setBackground(Theme.editorBackgroundColor);
		e.gc.setForeground(Theme.editorBackgroundColor);
		Point canvasSize = getSize();
		e.gc.fillRectangle(0, 0, canvasSize.x, canvasSize.y);

		if (image != null) {
			Rectangle imgSize = image.getBounds();
			if (canvasSize.y * aspectRatio > canvasSize.x)
				canvasSize.y = (int) (canvasSize.x / aspectRatio);
			else
				canvasSize.x = (int) (canvasSize.y * aspectRatio);

			e.gc.drawImage(image, 0, 0, imgSize.width, imgSize.height, 0, 0, canvasSize.x, canvasSize.y);
		}
	}

}
