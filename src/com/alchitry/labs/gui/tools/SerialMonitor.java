package com.alchitry.labs.gui.tools;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.usb4java.LibUsbException;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.BaudDialog;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.hardware.usb.SerialDevice;
import com.alchitry.labs.hardware.usb.UsbUtil;

public class SerialMonitor {

	protected Object result;
	protected Shell shell;
	protected StyledText text;
	// protected CustomCombo combo;
	private int cursorPos;

	private SerialDevice port;
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

		ignoreText = false;

		connect();
	}

	private void connect() {
		port = UsbUtil.openSerial();
		if (port == null) {
			shell.dispose();
			return;
		}

		if (MainWindow.getOpenProject() == null) {
			Util.showError("Please open a project before opening the serial port monitor!");
			shell.dispose();
			return;
		}

		if (!MainWindow.getOpenProject().getBoard().isType(Board.MOJO)) {
			int baud = 1000000;
			int setBaud = -1;
			while (true) {
				BaudDialog bd = new BaudDialog(Util.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				baud = bd.open(baud);
				if (baud < 0) {
					shell.dispose();
					return;
				}
				try {
					setBaud = port.setBaudrate(baud);
				} catch (LibUsbException e) {
					Util.showError("Failed to set baudrate!", e.getMessage(), Util.getShell());
					continue;
				}
				if (setBaud == baud) {
					break;
				} else {
					if (Util.askQuestion("Baud Mismatch",
							"The requested baudrate could not be set. The actual baudrate is " + setBaud
									+ ". Is this acceptable?",
							Util.getShell()))
						break;
				}
			}
		}
		port.setTimeouts(100, 2000);
		Thread t = new Thread() {
			public void run() {
				byte buffer[] = new byte[1];
				while (!shell.isDisposed()) {
					try {
						int len = port.readDataWithTimeout(buffer);
						if (len > 0) {
							String rx = new String(Arrays.copyOfRange(buffer, 0, len));
							shell.getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									if (!shell.isDisposed())
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
				disconnect();
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
			port.close();
		}
		port = null;
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
