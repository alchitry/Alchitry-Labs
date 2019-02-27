package com.alchitry.labs.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.hardware.LogicCapture;
import com.alchitry.labs.parsers.BitValue;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.ProjectSignal;
import com.alchitry.labs.project.DebugInfo;
import com.alchitry.labs.widgets.WaveSignal.TriggerType;

import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class Waves extends Canvas {
	private List<WaveSignal> signals;
	private int zoom;
	private int scrollX;
	private int scrollY;
	private double iX, iY;
	private int margin;
	private int toolbarSize;
	private boolean isSimulation;
	private int mouseScrollX, mouseScrollY;
	private int mouseX, mouseY;
	private WaveSignal hoverSignal;
	private TriggerType hoverTrigger;
	private int hoverBit;
	private int toolbarHover;
	private boolean hoverArrow;
	private AtomicBoolean armed = new AtomicBoolean(false);
	private boolean firstCapture;
	private int version;
	private DebugInfo debugInfo;
	private Rectangle waveArea;
	private int cursorY;
	private ConstValue cursorValue;

	private Font baseFont;
	private Font valueFont;

	private Thread captureThread;

	public Waves(Composite parent, boolean isSim) {
		super(parent, SWT.DOUBLE_BUFFERED);
		setBackground(Theme.editorBackgroundColor);
		setForeground(Theme.editorForegroundColor);
		signals = new ArrayList<>();
		isSimulation = isSim;
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				onDraw(e);
			}
		});

		mouseScrollX = -1;
		mouseScrollY = -1;
		mouseX = -1;
		mouseY = -1;
		iX = 0;
		iY = 0;
		margin = 0;
		toolbarSize = 0;
		toolbarHover = -1;
		version = -1;
		waveArea = new Rectangle(0, 0, 0, 0);

		if (!isSim)
			toolbarSize = 47;

		addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				mouseScrollX = -1;
				mouseScrollY = -1;
				updateInertia();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				boolean redraw = false;
				if (e.x > margin && e.y > toolbarSize) {
					mouseScrollX = e.x;
					mouseScrollY = e.y;
				}
				if (hoverSignal != null && hoverTrigger != null) {
					hoverSignal.setTrigger(hoverBit, hoverTrigger, !hoverSignal.getTrigger(hoverBit, hoverTrigger));
					redraw = true;
				}
				if (hoverSignal != null && hoverArrow) {
					hoverSignal.expand(!hoverSignal.isExpanded());
					redraw = true;
				}
				switch (toolbarHover) {
				case 0:
					connect();
					break;
				case 1:
					if (armed.get())
						stopCapture();
					else
						capture();
					break;
				}
				if (redraw)
					redraw();
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				boolean redraw = false;
				updateButtonHover(e.x, e.y);
				if (mouseScrollX >= 0 || mouseScrollY >= 0) {
					iX = e.x - mouseScrollX;
					scrollX += iX;
					mouseScrollX = e.x;
					iY = e.y - mouseScrollY;
					scrollY += iY;
					mouseScrollY = e.y;
					redraw = true;
				}

				if (waveArea.contains(mouseX, mouseY) || waveArea.contains(e.x, e.y))
					redraw = true;

				mouseX = e.x;
				mouseY = e.y;

				if (redraw)
					redraw();
			}
		});

		addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent e) {
			}

			@Override
			public void mouseExit(MouseEvent e) {
				mouseX = -1;
				mouseY = -1;
				redraw();
			}

			@Override
			public void mouseEnter(MouseEvent e) {
			}
		});

		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {
				if (e.count != 0) {
					double width = getWaveWidth();
					double pos = ((double) (-scrollX + e.x - margin)) / width;

					zoom += e.count > 0 ? 1 : -1;
					zoom = zoom > 1 ? zoom : 1;

					width = getWaveWidth();

					scrollX = (int) Math.round(-(pos * width - e.x + margin));
					redraw();
				}
			}
		});

		zoom = 50;

		baseFont = getFont();
		FontData[] fd = baseFont.getFontData();
		fd[0].setHeight(20);
		valueFont = new Font(getDisplay(), fd);
	}

	private boolean triggersActive() {
		for (WaveSignal sig : signals) {
			for (int bit = 0; bit < sig.getWidth(); bit++)
				if (sig.getTriggers(bit).size() != 0)
					return true;
		}
		return false;
	}

	private String getSignalAtBit(int b) {
		if (debugInfo != null) {
			int start = 0;
			for (int i = debugInfo.getSignals().size() - 1; i >= 0; i--) {
				ProjectSignal s = debugInfo.getSignals().get(i);
				int w = s.getTotalWidth();
				if (b >= start && b < start + w)
					return s.getName() + " [" + (b - start) + "]";
				start += w;
			}
		}
		return "Bit " + b;
	}

	private void connect() {
		if (captureThread == null || !captureThread.isAlive()) {
			captureThread = new Thread(new Runnable() {

				@Override
				public void run() {
					debugInfo = MainWindow.getOpenProject().getDebugInfo();
					LogicCapture lc = new LogicCapture();
					try {
						if (lc.connect(null)) {
							if (!lc.updateDeviceInfo()) {
								Util.showError("Failed to get wave capture information!");
								return;
							}

							if (debugInfo != null && lc.getNonce() != debugInfo.getNonce()) {
								Util.println("Project debug nonce doesn't match loaded project! Expected " + debugInfo.getNonce() + " got " + lc.getNonce(), true);
								debugInfo = null;
							}

							version = lc.getVersion();
							synchronized (signals) {
								signals.clear();
								if (debugInfo == null)
									for (int i = 0; i < lc.getWidth(); i++) {
										signals.add(new WaveSignal(getSignalAtBit(i), 1, false));
									}
								else
									for (int i = debugInfo.getSignals().size() - 1; i >= 0; i--) {
										ProjectSignal ps = debugInfo.getSignals().get(i);
										signals.add(new WaveSignal(ps.getName(), ps.getTotalWidth(), ps.isSigned()));
									}
							}

						} else {

							Util.showError("Failed to connect to Mojo!");
						}
					} catch (Exception e) {
						throw e;
					} finally {
						try {
							lc.disconnect();
						} catch (SerialPortException e1) {

						}
					}
				}
			});
			firstCapture = true;
			captureThread.start();
		} else {
			Util.showError("Can't connect when a capture is in progress!");
		}
	}

	private void stopCapture() {
		armed.set(false);
	}

	private int getNumBits(List<WaveSignal> signals) {
		int bits = 0;
		for (WaveSignal s : signals)
			bits += s.getWidth();
		return bits;
	}

	private ConstValue getValue(byte[][] data, int sample, int start, int width, boolean signed) {
		List<BitValue> bv = new ArrayList<>(width);
		for (int i = start; i < start + width; i++) {
			bv.add(data[i][sample] == 1 ? BitValue.B1 : BitValue.B0);
		}
		ConstValue cv = new ConstValue(bv);
		cv.setSigned(signed);
		return cv;
	}

	private void capture() {
		if (!armed.get()) {
			while (captureThread != null && captureThread.isAlive())
				try {
					synchronized (captureThread) {
						captureThread.wait();
					}
				} catch (InterruptedException e2) {
					Util.log.warning("Capture thread was interrupted!");
				}
			captureThread = new Thread(new Runnable() {
				@Override
				public void run() {
					LogicCapture lc = new LogicCapture();
					try {
						if (lc.connect(null)) {
							if (!lc.updateDeviceInfo()) {
								Util.showError("Failed to get wave capture information!");
								return;
							}
							if (lc.getWidth() != getNumBits(signals)) {
								Util.showError("The number of signals detected, " + lc.getWidth() + ", does not match the expected number, " + signals.size()
										+ ". Try connecting to the Mojo and try again.");
								return;
							}

							if (!lc.updateTriggers(signals)) {
								Util.showError("Failed to update triggers!");
								return;
							}
							byte[][] data = lc.capture(triggersActive(), armed);
							if (data != null) {
								synchronized (signals) {
									for (WaveSignal sig : signals)
										sig.clearValues();
									for (int i = 0; i < data[0].length; i++) {
										int bit = 0;

										for (WaveSignal sig : signals) {
											sig.addValue(getValue(data, i, bit, sig.getWidth(), sig.isSigned()));
											bit += sig.getWidth();
										}
									}
								}
								if (firstCapture) {
									zoom = 50;
									firstCapture = false;
								}
							}

						} else {
							Util.showError("Failed to connect to Mojo!");
						}
					} catch (SerialPortException e1) {
						Util.showError(e1.getMessage());
					} catch (SerialPortTimeoutException e) {
						Util.showError(e.getMessage());
					} finally {
						try {
							lc.disconnect();
						} catch (SerialPortException e1) {
							Util.println(e1.getMessage(), true);
						}
					}
					armed.set(false);
					getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							redraw();
						}
					});
				}
			});
			armed.set(true);
			captureThread.start();
			redraw();
		} else {
			Util.showError("Can't connect what a capture is in progress!");
		}
	}

	private void updateInertia() {
		boolean update = false;
		boolean redraw = false;
		if (mouseScrollX == -1) {
			scrollX += iX;
			iX /= 1.10;
			if (Math.abs(iX) > 1.0)
				update = true;
			else
				iX = 0;

			redraw = true;
		}
		if (mouseScrollY == -1) {
			scrollY += iY;
			iY /= 1.10;
			if (Math.abs(iY) > 1.0)
				update = true;
			else
				iY = 0;

			redraw = true;
		}

		if (update)
			getDisplay().timerExec(16, new Runnable() {
				@Override
				public void run() {
					updateInertia();
				}
			});

		if (redraw)
			redraw();
	}

	private int getNumberOfValues() {
		if (signals.size() > 0 && signals.get(0).getNumValues() > 0)
			return signals.get(0).getNumValues();
		return 1;
	}

	private int getWaveWidth() {
		return getNumberOfValues() * zoom;
	}

	private int getNumberOfShownWaves() {
		int waves = 0;
		for (WaveSignal sig : signals)
			waves += sig.isExpanded() ? sig.getWidth() : 1;
		return waves;
	}

	private int getWaveHeight() {
		return getNumberOfShownWaves() * 60 + 10;
	}

	private void updateZoom(int w) {
		int steps = getNumberOfValues();
		if (w > steps * zoom)
			zoom = w / steps;
	}

	private void updateScroll(int w, int h) {
		int waveWidth = getWaveWidth();

		if (waveWidth < w) {
			scrollX = 0;
		} else {
			if (scrollX < -(waveWidth - w))
				scrollX = -(waveWidth - w);
			else if (scrollX > 0)
				scrollX = 0;
		}

		int waveHeight = getWaveHeight();

		if (waveHeight < h) {
			scrollY = 0;
		} else {
			if (scrollY < -(waveHeight - h))
				scrollY = -(waveHeight - h);
			else if (scrollY > 0)
				scrollY = 0;
		}
	}

	public void clearWaves() {
		signals.clear();
	}

	public void addWaveData(WaveSignal sig) {
		signals.add(sig);
	}

	// private void generateTestData(int sigs, int length) {
	// for (int i = 0; i < sigs; i++) {
	// WaveSignal sig = new WaveSignal("Bit " + i);
	// signals.add(sig);
	// for (int j = 0; j < length; j++) {
	// sig.addValue(new ConstValue(Math.random() > 0.5 ? 1 : 0));
	// }
	// }
	// }

	private void drawToolbar(GC gc, Rectangle b) {
		gc.setForeground(Theme.windowBackgroundColor);
		gc.setLineWidth(3);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineCap(SWT.CAP_SQUARE);
		gc.drawLine(b.x, b.y + b.height, b.x + b.width, b.y + b.height);

		int x = b.x + 10;
		int y = b.y + 10;

		if (toolbarHover == 0) {
			gc.setBackground(Theme.waveButtonHoverColor);
			gc.fillRectangle(x, y, 25, 25);
		}
		gc.drawImage(Images.connectIcon, x, y);

		x += 35;

		if (toolbarHover == 1) {
			gc.setBackground(Theme.waveButtonHoverColor);
			gc.fillRectangle(x, y, 25, 25);
		}
		if (!armed.get())
			gc.drawImage(Images.captureIcon, x, y);
		else
			gc.drawImage(Images.cancelIcon, x, y);

		if (version >= 0) {
			gc.setForeground(getForeground());
			gc.setBackground(getBackground());
			Point textSize = gc.stringExtent("V" + version);
			int vx = b.x + Math.max(b.width - textSize.x - 10, 150);
			int vy = b.y + (b.height - textSize.y) / 2;
			gc.drawText("V" + version, vx, vy);
		}
	}

	private void setRotation(GC gc, int x, int y, Rectangle bounds, float angle) {
		Transform transform = new Transform(gc.getDevice());
		transform.translate(x + bounds.width / 2, y + bounds.height / 2);
		transform.rotate(angle);
		transform.translate(-x - bounds.width / 2, -y - bounds.height / 2);
		gc.setTransform(transform);
	}

	private void onDraw(PaintEvent e) {
		synchronized (signals) {
			Point size = getSize();
			int h = size.y;
			int w = size.x;
			Rectangle drawArea = new Rectangle(e.x, e.y, e.width, e.height);
			int buttonSpace = 0;
			if (!isSimulation) {
				buttonSpace = 25 * 4 + 40;
				Rectangle toolbarRec = new Rectangle(0, 0, w, toolbarSize);
				if (drawArea.intersects(toolbarRec))
					drawToolbar(e.gc, toolbarRec);
			}

			int nameSpace = 0;
			for (WaveSignal sig : signals)
				nameSpace = Math.max(e.gc.stringExtent(sig.getName()).x, nameSpace);
			int textHeight = e.gc.stringExtent("Bit").y;

			margin = nameSpace + 20 + buttonSpace + 35; // 35 for arrow

			updateZoom(w - margin);
			updateScroll(w - margin, h - toolbarSize);

			e.gc.setClipping(0, toolbarSize + 2, w, h - toolbarSize - 2);

			Rectangle b = new Rectangle(10, toolbarSize + 10 + scrollY, 25, 25);

			for (WaveSignal sig : signals) {
				if (sig.getWidth() > 1) {
					boolean hover = sig == hoverSignal && hoverArrow;
					if (hover) {
						e.gc.setBackground(Theme.waveButtonHoverColor);
						e.gc.fillRectangle(b.x, b.y + (50 - 25) / 2, 25, 25);
					}
					sig.setArrowBounds(b.x, b.y + (50 - 25) / 2, 25, 25);
					Image icon = Images.treeArrowIcon;
					int arrowOffsetY = (50 - icon.getBounds().height) / 2;
					int arrowOffsetX = (25 - icon.getBounds().width) / 2;
					if (sig.isExpanded()) {
						Transform oldTransform = new Transform(e.gc.getDevice());
						setRotation(e.gc, b.x + arrowOffsetX, b.y + arrowOffsetY, icon.getBounds(), 90);
						e.gc.drawImage(icon, b.x + arrowOffsetX, b.y + arrowOffsetY);
						e.gc.setTransform(oldTransform);
					} else {
						e.gc.drawImage(icon, b.x + arrowOffsetX, b.y + arrowOffsetY);
					}
				}
				b.y += 60 * (sig.isExpanded() ? sig.getWidth() : 1);
			}

			b.width = w - 45;
			b.height = 50;
			b.x = 45;
			b.y = toolbarSize + 10 + scrollY;

			e.gc.setForeground(Theme.editorForegroundColor);
			e.gc.setBackground(Theme.editorBackgroundColor);

			for (WaveSignal sig : signals) {
				if (drawArea.intersects(b))
					drawName(e.gc, sig, b, textHeight);
				b.y += 60 * (sig.isExpanded() ? sig.getWidth() : 1);
			}

			b.x += nameSpace + 10;
			b.y = toolbarSize + 10 + scrollY;

			for (WaveSignal sig : signals) {
				if (sig.isExpanded()) {
					for (int bit = 0; bit < sig.getWidth(); bit++) {
						if (drawArea.intersects(b))
							drawButtons(e.gc, sig, bit, b);
						b.y += 60;
					}
				} else {
					b.y += 60;
				}
			}

			b.y = toolbarSize + 2; // +2 for line thickness of toolbar seperator
			b.x = margin;
			b.width = w - margin;
			b.height = h - b.y;

			waveArea.x = b.x;
			waveArea.y = b.y;
			waveArea.width = b.width;
			waveArea.height = b.height;

			cursorY = -1;

			if (drawArea.intersects(waveArea)) {
				drawGrid(e.gc, waveArea);
				e.gc.setClipping(margin + 3, toolbarSize + 2, w, h - toolbarSize - 2);
				b.x += 3;
				b.y = toolbarSize + 10;
				b.height = 50;
				for (WaveSignal sig : signals) {
					drawSignal(e.gc, sig, b);
					b.y += 60 * (sig.isExpanded() ? sig.getWidth() : 1);
				}
				drawCursor(e.gc, waveArea);
			}
		}
	}

	private Image triggerTypeToImage(TriggerType trigger, boolean hover) {
		switch (trigger) {
		case FALLING:
			return hover ? Images.triggerFallIconHover : Images.triggerFallIcon;
		case RISING:
			return hover ? Images.triggerRiseIconHover : Images.triggerRiseIcon;
		case HIGH:
			return hover ? Images.triggerHighIconHover : Images.triggerHighIcon;
		case LOW:
			return hover ? Images.triggerLowIconHover : Images.triggerLowIcon;
		}
		return null;
	}

	private void updateButtonHover(int x, int y) {
		WaveSignal oldSig = hoverSignal;
		TriggerType oldTrigger = hoverTrigger;
		int oldToolbar = toolbarHover;
		int oldBit = hoverBit;
		boolean oldName = hoverArrow;
		hoverSignal = null;
		hoverTrigger = null;
		hoverArrow = false;
		toolbarHover = -1;
		if (y < toolbarSize) {
			Rectangle buttonBounds = new Rectangle(10, 10, 25, 25);
			for (int i = 0; i < 2; i++) {
				if (buttonBounds.contains(x, y)) {
					toolbarHover = i;
					break;
				}
				buttonBounds.x += 35;
			}
		} else {
			for (WaveSignal sig : signals) {
				if (sig.getArrowBounds().contains(x, y)) {
					hoverSignal = sig;
					hoverArrow = true;
					break;
				} else if (sig.isExpanded()) {
					boolean stop = false;
					for (int b = 0; b < sig.getWidth(); b++) {
						Rectangle buttonBounds = sig.getButtonBounds(b);
						if (buttonBounds.contains(x, y)) {
							hoverSignal = sig;
							hoverBit = b;
							int by = buttonBounds.y + buttonBounds.height / 2 - 25 / 2;
							if (y < by || y >= by + 25)
								break;
							int bx = buttonBounds.x;
							for (TriggerType type : TriggerType.values()) {
								if (x >= bx && x < bx + 25) {
									hoverTrigger = type;
									break;
								}
								bx += 35;
							}
							stop = true;
							break;
						}
					}
					if (stop)
						break;
				}
			}
		}
		updateTooltip();
		if (hoverSignal != oldSig || oldBit != hoverBit || oldTrigger != hoverTrigger || oldToolbar != toolbarHover || oldName != hoverArrow) {
			redraw();
		}
	}

	private void updateTooltip() {
		setToolTipText(null);
		switch (toolbarHover) {
		case 0:
			setToolTipText("Connect");
			return;
		case 1:
			setToolTipText("Capture");
			return;
		}

		if (hoverSignal != null && hoverTrigger != null) {
			switch (hoverTrigger) {
			case FALLING:
				setToolTipText("Falling Edge");
				return;
			case RISING:
				setToolTipText("Rising Edge");
				return;
			case HIGH:
				setToolTipText("High Level");
				return;
			case LOW:
				setToolTipText("Low Level");
				return;
			}
		}

	}

	private void drawButtons(GC gc, WaveSignal sig, int bit, Rectangle b) {

		int y = b.y + b.height / 2 - 25 / 2;
		int x = b.x;

		sig.setButtonBounds(bit, b);

		for (TriggerType type : TriggerType.values()) {
			if (sig.getTrigger(bit, type))
				gc.setBackground(Theme.waveButtonActiveColor);
			else if (hoverSignal == sig && hoverTrigger == type && hoverBit == bit)
				gc.setBackground(Theme.waveButtonHoverColor);
			else
				gc.setBackground(getBackground());
			gc.fillRectangle(x, y, 25, 25);
			gc.drawImage(triggerTypeToImage(type, sig.getTrigger(bit, type)), x, y);
			x += 35;
		}

		gc.setBackground(getBackground());

	}

	private void drawName(GC gc, WaveSignal sig, Rectangle b, int yOffset) {
		gc.drawText(sig.getName(), b.x, b.y + (b.height - yOffset) / 2);
	}

	// private int countTransistions(WaveSignal sig, int bit, int range, double max) {
	// if (sig.getValues().size() == 0)
	// return 0;
	// int transistions = 0;
	// if (!sig.isExpanded()) {
	// int old = (int) (sig.getValues().get(0).getBigInt().doubleValue() / max * range);
	// for (ConstValue cv : sig.getValues()) {
	// int cur = (int) (cv.getBigInt().doubleValue() / max * range);
	// if (cur != old)
	// transistions++;
	// old = cur;
	// }
	// } else {
	// BitValue old = sig.getValues().get(0).getValue().get(bit);
	// for (ConstValue cv : sig.getValues()) {
	// BitValue cur = cv.getValue().get(bit);
	// if (!cur.equals(old))
	// transistions++;
	// old = cur;
	// }
	// }
	// return transistions;
	// }

	private void drawSignal(GC gc, WaveSignal sig, Rectangle b) {
		if (sig.getValues().isEmpty())
			return;
		int offset = scrollX;

		gc.setForeground(Theme.mainAccentColor);
		gc.setLineWidth(3);
		gc.setLineJoin(SWT.JOIN_MITER);

		int startY = b.y;

		int waves = sig.isExpanded() ? sig.getWidth() : 1;

		for (int bit = 0; bit < waves; bit++) {
			int max = startY + 2 + scrollY;
			int min = startY + b.height - 2 + scrollY;
			int range = max - min;
			double sigMax = Math.pow(2, sig.getWidth()) - 1;

			// int points[] = sig.points;

			// int ptsLength = (countTransistions(sig, bit, range, sigMax) + 1) * 4;
			// if (points == null || points.length != ptsLength)
			// int points[] = new int[ptsLength];
			ArrayList<Integer> points = new ArrayList<>();

			// sig.points = points;

			points.add(offset + b.x);
			if (sig.isExpanded())
				points.add(sig.getValue(0).getValue().get(bit).equals(BitValue.B1) ? max : min);
			else if (sig.isSigned())
				points.add((int) (sig.getValue(0).getBigInt().doubleValue() / (sigMax / 2.0) * (range / 2.0) + min + range / 2));
			else
				points.add((int) (sig.getValue(0).getBigInt().doubleValue() / sigMax * range + min));

			if (mouseX >= offset + b.x && mouseX < offset + b.x + zoom && mouseY <= min && mouseY >= max) {
				cursorY = points.get(1);
				if (sig.isExpanded()) {
					cursorValue = new ConstValue(sig.getValue(0).getValue().get(bit));
				} else {
					cursorValue = sig.getValue(0);
				}
			}

			offset += zoom;

			for (int i = 1; i < sig.getNumValues(); i++) {
				ConstValue cv = sig.getValue(i);
				int nx = offset + b.x;
				int ny;
				if (sig.isExpanded())
					ny = cv.getValue().get(bit).equals(BitValue.B1) ? max : min;
				else if (sig.isSigned())
					ny = (int) (cv.getBigInt().doubleValue() / (sigMax / 2.0) * (range / 2.0) + min + range / 2);
				else
					ny = (int) (cv.getBigInt().doubleValue() / sigMax * range + min);

				if (mouseX >= nx && mouseX < nx + zoom && mouseY <= min && mouseY >= max) {
					cursorY = ny;
					if (sig.isExpanded()) {
						cursorValue = new ConstValue(cv.getValue().get(bit));
					} else {
						cursorValue = cv;
					}
				}

				if (ny != points.get(points.size() - 1)) {
					points.add(nx);
					points.add(points.get(points.size() - 2));
					points.add(nx);
					points.add(ny);
				}
				offset += zoom;
			}

			int nx = offset + b.x;

			if (points.get(points.size() - 2) != nx) {
				points.add(nx);
				points.add(points.get(points.size() - 2));
			}

			gc.drawPolyline(convertIntegers(points));

			offset = scrollX;
			startY += 60;
		}
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	private void drawCursor(GC gc, Rectangle b) {
		if (b.contains(mouseX, mouseY)) {
			gc.setLineWidth(1);
			gc.setForeground(Theme.waveCursorColor);
			gc.drawLine(mouseX, b.y, mouseX, b.y + b.height);

			if (cursorY >= 0) {
				gc.setBackground(Theme.waveCursorColor);
				gc.fillOval(mouseX - 3, cursorY - 3, 7, 7);

				gc.setFont(valueFont);

				String value = cursorValue.getBigInt().toString();
				Point textSize = gc.stringExtent(value);

				int textX = mouseX - textSize.x - 8;

				if (textX < b.x)
					textX = mouseX + 8;

				gc.drawText(value, textX, cursorY - textSize.y / 2, true);

				gc.setFont(baseFont);
			}
		}
	}

	private void drawGrid(GC gc, Rectangle b) {

		gc.setClipping(b);
		gc.setForeground(Theme.windowBackgroundColor);
		gc.setLineWidth(3);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawLine(b.x + 1, b.y, b.x + 1, b.y + b.height);

		gc.setForeground(Theme.waveGridColor);
		gc.setLineWidth(1);
		int ct = getNumberOfValues();
		for (int x = b.x + 3 + zoom + scrollX % zoom; x < b.width + b.x; x += zoom) {
			if (--ct == 0)
				break;
			gc.drawLine(x, b.y, x, b.y + b.height);
		}

	}

}
