package com.alchitry.labs.gui.tools;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.usb4java.LibUsbException;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.hardware.usb.UsbSerial;
import com.alchitry.labs.hardware.usb.UsbUtil;

public class SerialMonitor {

	protected Object result;
	protected Shell shell;
	protected StyledText text;
	// protected CustomCombo combo;
	private int cursorPos;

	private UsbSerial port;
	private boolean ignoreText;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public SerialMonitor(Display display) {
		createContents(display);
		if (shell.isDisposed())
			return;
		shell.open();
		shell.layout();
		shell.setFocus();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(Display display) {
		shell = new Shell(display, SWT.CLOSE | SWT.RESIZE | SWT.MIN | SWT.TITLE | SWT.MAX);
		shell.setText("Serial Port Monitor");
		shell.setSize(450, 300);
		shell.setMinimumSize(450, 300);
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setBackground(Theme.editorBackgroundColor);
		shell.setForeground(Theme.comboBackgroundColor);
		shell.setImage(SWTResourceManager.getImage(MainWindow.class, "/images/icon.png"));

		cursorPos = 0;
		text = new StyledText(shell, SWT.V_SCROLL | SWT.H_SCROLL);
		text.setAlwaysShowScrollBars(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		text.setBackground(Theme.editorBackgroundColor);
		text.setForeground(Theme.editorForegroundColor);
		text.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				if (port != null && !ignoreText) {
					e.doit = false;
					String t = e.text;
					int len = e.end - e.start;
					if (t.isEmpty() && len > 0) {
						t = "\b";
					}
					port.writeData(t.getBytes());

				}
				{
					e.text = e.text.replace("\r", "");
					text.setTopIndex(text.getLineCount() - 1);

				}
			}
		});

		shell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event e) {
				disconnect();
			}
		});

		ignoreText = false;

		connect();
	}

	private void connect() {
		port = UsbUtil.openSerial();
		if (port == null) {
			shell.dispose();
			return;
		}
		port.setTimeouts(100, 2000);
		Thread t = new Thread() {
			public void run() {
				byte buffer[] = new byte[1];
				while (!shell.isDisposed()) {
					try {
						int len = port.readData(buffer);
						if (len > 0) {
							String rx = new String(Arrays.copyOfRange(buffer, 0, len));
							shell.getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									addText(rx);
								}
							});
						}
					} catch (LibUsbException e) {
						if (!e.getMessage().contains("timed out")) {
							shell.getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									if (!shell.isDisposed()) {
										Util.showError("Error reading the serial port!");
										shell.dispose();
									}
								}
							});
						}
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();

	}

	public void close() {
		shell.dispose();
	}

	private void disconnect() {
		if (port != null) {
			port.usbClose();
		}
		port = null;
	}

	public void enable(boolean e) {
		if (e) {
			connect();
			if (!shell.isDisposed())
				shell.setEnabled(true);
		} else {
			if (!shell.isDisposed())
				shell.setEnabled(false);
			disconnect();
		}
	}

	public void setFocus() {
		shell.setFocus();
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}

	@SuppressWarnings("unused")
	private void reset() {
		cursorPos = 0;
		ignoreText = true;
		text.replaceTextRange(0, text.getCharCount(), "");
		ignoreText = false;
	}

	private void addText(String t) {
		String[] st = t.split("\b", -1); // split on backspace
		boolean first = true;
		ignoreText = true;
		text.setCaretOffset(cursorPos);
		for (String s : st) {
			if (!first && cursorPos > 0) {
				text.replaceTextRange(cursorPos - 1, 1, "");
				text.setCaretOffset(--cursorPos);
			}
			first = false;

			text.insert(s);
			cursorPos += s.length();
			text.setCaretOffset(cursorPos);
			cursorPos = text.getCaretOffset();
		}
		ignoreText = false;
	}
}
