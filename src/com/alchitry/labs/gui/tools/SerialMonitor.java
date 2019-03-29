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

import com.alchitry.labs.Settings;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.widgets.CustomCombo;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortIOException;

public class SerialMonitor {

	protected Object result;
	protected Shell shell;
	protected StyledText text;
	protected CustomCombo combo;
	private int cursorPos;

	private SerialPort port;
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

		combo = new CustomCombo(shell, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.addListener(SWT.Arm, new Listener() {

			@Override
			public void handleEvent(Event event) {
				updatePorts();
			}
		});

		combo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String p = combo.getSelection();
				if (p != null) {
					reset();
					disconnect();
					connect();
				}
			}
		});

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
					port.writeBytes(t.getBytes(), t.getBytes().length);

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

		updatePorts();
		selectDefaultPort();
	}

	private void connect() {
		String p = combo.getSelection();
		if (p != null) {
			port = null;
			try {
				port = Util.connect(p, 1000000);
				port.addDataListener(new SerialPortDataListener() {

					@Override
					public void serialEvent(SerialPortEvent event) {
						if (port.bytesAvailable() > 0) {
							byte[] buff = new byte[port.bytesAvailable()];
							int ct = port.readBytes(buff, buff.length);
							final String rx = new String(Arrays.copyOfRange(buff, 0, ct));
							shell.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									addText(rx);
								}
							});

						}
					}

					@Override
					public int getListeningEvents() {
						return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
					}
				});
				reset();
			} catch (SerialPortIOException e) {
				e.printStackTrace();
				port = null;
				Util.showError("Connection Error", e.getMessage());
				shell.dispose();
			}
		}
	}

	public void close() {
		shell.dispose();
	}

	private void disconnect() {
		if (port != null) {
			port.closePort();
		}
		port = null;
	}

	private void selectDefaultPort() {
		String[] ports = combo.getItems();
		String defPort = Settings.pref.get(Settings.MOJO_PORT, null);
		if (defPort != null) {
			for (int i = 0; i < ports.length; i++) {
				if (ports[i].equals(defPort)) {
					combo.select(i);
					break;
				}
			}
		}
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

	private void updatePorts() {
		String[] ports = Util.getSerialPortNames();
		combo.setItems(ports);
	}

	public void setFocus() {
		shell.setFocus();
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}

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
