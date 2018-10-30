package com.alchitry.labs.widgets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.alchitry.labs.gui.MainWindow;

public class SVGTest extends Canvas {

	public SVGTest(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				onDraw(e);
			}
		});
	}
	
	private void onDraw(PaintEvent e) {
		Point size = getSize();
		ByteArrayOutputStream resultByteStream = new ByteArrayOutputStream();
		TranscoderInput input = new TranscoderInput(MainWindow.class.getResourceAsStream("/images/" + "mojo_v3.svg"));
		TranscoderOutput output = new TranscoderOutput(resultByteStream);
		
		PNGTranscoder png = new PNGTranscoder();
		png.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float)size.y);
		png.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float)size.x);
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
		
		Image i = new Image(e.display, imgstream);
		
		e.gc.drawImage(i, 0, 0);
		
	}

}
