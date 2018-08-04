package com.alchitry.labs.gui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.RegisterInterface;

import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class ImageCapture {
	private final int WIDTH = 1600;
	private final int HEIGHT = 1200;

	protected Shell shell;
	private Canvas canvas;
	private Image image;
	private ScrolledComposite scrolledComposite;
	private ProgressBar progressBar;
	private Text textWidth;
	private Text textHeight;
	private Text textAddr;
	private int width, height, address;

	public ImageCapture(Display display) {
		createContents(display);
		shell.open();
		shell.layout();
		shell.setFocus();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		shell = new Shell(display, SWT.CLOSE | SWT.RESIZE | SWT.MIN | SWT.TITLE | SWT.MAX);
		shell.setSize(450, 300);
		shell.setMinimumSize(250, 250);
		shell.setText("Image Capture");

		shell.setLayout(new GridLayout(4, false));

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Width:");

		textWidth = new Text(shell, SWT.BORDER);
		textWidth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(shell, SWT.NONE);
		textWidth.setText("1600");
		textWidth.addVerifyListener(Util.integerVerifier);

		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Height:");

		textHeight = new Text(shell, SWT.BORDER);
		textHeight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(shell, SWT.NONE);
		textHeight.setText("1200");
		textHeight.addVerifyListener(Util.integerVerifier);

		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("Start Address:");

		textAddr = new Text(shell, SWT.BORDER);
		textAddr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		textAddr.addVerifyListener(Util.integerVerifier);
		textAddr.setText("0");
		new Label(shell, SWT.NONE);

		scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		canvas = new Canvas(scrolledComposite, SWT.DOUBLE_BUFFERED);
		canvas.setSize(WIDTH, HEIGHT);
		scrolledComposite.setContent(canvas);
		scrolledComposite.setMinSize(WIDTH, HEIGHT);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (image != null)
					e.gc.drawImage(image, 0, 0);
			}
		});

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Thread thread = new Thread() {
					@Override
					public void run() {

						Util.syncExec(new Runnable() {
							@Override
							public void run() {
								try {
									width = Integer.parseInt(textWidth.getText());
								} catch (NumberFormatException e) {
									Util.showError("Width must be an integer!");
								}
								try {
									height = Integer.parseInt(textHeight.getText());
								} catch (NumberFormatException e) {
									Util.showError("Height must be an integer!");
								}
								try {
									address = Integer.parseInt(textAddr.getText());
								} catch (NumberFormatException e) {
									Util.showError("Address must be an integer!");
								}
								if (width == 0)
									Util.showError("Width cannot be 0");
								if (height == 0)
									Util.showError("Height cannot be 0");

								progressBar.setMaximum(height);
								canvas.setSize(width, height);
								scrolledComposite.setMinSize(width, height);
							}
						});

						RegisterInterface reg = new RegisterInterface();
						PaletteData palette = new PaletteData(0xFF0000, 0x00FF00, 0x0000FF);
						final ImageData image = new ImageData(width, height, 24, palette);
						if (reg.connect(null))
							try {
								reg.write(address, 0); // start capture
								try {
									Thread.sleep(250);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								for (int i = 0; i < height; i++) {
									if (shell.isDisposed())
										break;

									int[] data = new int[width / 2];

									reg.read(address + i * width / 2, true, data);

									drawRow(i, data, image);
									final int p = i;

									Util.asyncExec(new Runnable() {
										@Override
										public void run() {
											if (!shell.isDisposed()) {
												progressBar.setSelection(p);
												progressBar.redraw();
												Image partial = new Image(shell.getDisplay(), image);
												Image old = ImageCapture.this.image;
												ImageCapture.this.image = partial;
												ImageCapture.this.canvas.redraw();
												if (old != null)
													old.dispose();
											}
										}
									});
								}

							} catch (SerialPortException e) {
								Util.print(e);
								e.printStackTrace();
							} catch (SerialPortTimeoutException e) {
								e.printStackTrace();
								Util.print(e);
							} finally {
								try {
									reg.disconnect();
								} catch (SerialPortException e) {
									e.printStackTrace();
									Util.print(e);
								}
								Util.asyncExec(new Runnable() {
									@Override
									public void run() {
										if (!progressBar.isDisposed()) {
											progressBar.setSelection(progressBar.getMaximum());
											progressBar.redraw();
										}
									}
								});
							}
					}
				};

				thread.start();

			}
		});
		btnNewButton.setText("Capture");

		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progressBar.setMinimum(0);
		progressBar.setMaximum(HEIGHT);

		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (image != null) {
					FileDialog dialog = new FileDialog(shell, SWT.SAVE);
					dialog.setFilterNames(new String[] { ".png" });
					String file = dialog.open();
					if (file == null)
						return;
					ImageLoader loader = new ImageLoader();
					loader.data = new ImageData[] { image.getImageData() };
					loader.save(file, SWT.IMAGE_PNG);
				} else {
					Util.showError("There is no image to save!");
				}
			}
		});
		btnNewButton_1.setText("Save Image");
		new Label(shell, SWT.NONE);

	}

	private void drawRow(int row, int[] data, ImageData img) {
		for (int i = 0; i < data.length; i++) {
			int p = data[i] & 0x0000ffff;
			// System.out.println("data "+ Integer.toHexString(data[i]));

			int r, g, b, rgb;
			r = (p >> 11) & 0x1F;
			g = (p >> 5) & 0x3F;
			b = p & 0x1F;

			r = (r << 3) | (r >> 2);
			g = (g << 2) | (g >> 4);
			b = (b << 3) | (b >> 2);

			rgb = (r << 16) | (g << 8) | (b << 0);
			// System.out.println(Integer.toHexString(rgb));

			img.setPixel((i * 2), row, rgb);

			p = (data[i] >> 16) & 0x0000ffff;

			r = (p >> 11) & 0x1F;
			g = (p >> 5) & 0x3F;
			b = p & 0x1F;

			r = (r << 3) | (r >> 2);
			g = (g << 2) | (g >> 4);
			b = (b << 3) | (b >> 2);

			rgb = (r << 16) | (g << 8) | (b << 0);

			// System.out.println(Integer.toHexString(rgb));
			img.setPixel(i * 2 + 1, row, rgb);
		}
	}

	public void setFocus() {
		shell.setFocus();
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}
}
