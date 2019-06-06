package com.alchitry.labs.gui.tools;

import java.math.BigInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.RegisterInterface;
import com.fazecast.jSerialComm.SerialPortIOException;
import com.fazecast.jSerialComm.SerialPortTimeoutException;
import org.eclipse.swt.widgets.Combo;

public class RegInterface {
	protected Shell shell;
	private Text address;
	private Text value;
	private Button decAddr, hexAddr;
	private Button decVal, hexVal;
	private Combo combo;
	private Label lblNewLabel_2;
	private String port;

	public RegInterface(Display display) {
		createContents(display);
		shell.open();
		shell.layout();
		shell.setFocus();
	}

	private void updatePorts() {
		String[] ports = Util.getSerialPortNames();
		combo.setItems(ports);
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		shell = new Shell(display, SWT.CLOSE | SWT.RESIZE | SWT.MIN | SWT.TITLE | SWT.MAX);
		shell.setText("Register Interface");
		shell.setLayout(new GridLayout(4, false));

		lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("Port:");

		combo = new Combo(shell, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		combo.addListener(SWT.Arm, new Listener() {

			@Override
			public void handleEvent(Event event) {
				updatePorts();
			}
		});

		combo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				port = combo.getText();

			}
		});

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Address:");

		address = new Text(shell, SWT.BORDER);
		address.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

		decAddr = new Button(composite, SWT.RADIO);
		decAddr.setText("Decimal");
		decAddr.setSelection(true);

		hexAddr = new Button(composite, SWT.RADIO);
		hexAddr.setText("Hex");

		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Value:");

		value = new Text(shell, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_text_1.widthHint = 223;
		value.setLayoutData(gd_text_1);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		decVal = new Button(composite_1, SWT.RADIO);
		decVal.setText("Decimal");
		decVal.setSelection(true);

		hexVal = new Button(composite_1, SWT.RADIO);
		hexVal.setText("Hex");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Button writeButton = new Button(shell, SWT.NONE);
		writeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long addr = getNumber(address.getText(), decAddr.getSelection());
				long val = getNumber(value.getText(), decVal.getSelection());
				if (addr == -1) {
					Util.showError("Address is not a valid " + (decAddr.getSelection() ? "decimal" : "hex") + " number", shell);
					setFocus();
					return;
				}
				if (val == -1) {
					Util.showError("Value is not a valid " + (decVal.getSelection() ? "decimal" : "hex") + " number", shell);
					setFocus();
					return;
				}
				RegisterInterface regInt = new RegisterInterface();
				if (!regInt.connect(port)) {
					Util.showError("Failed to connect to serial port!", shell);
					setFocus();
					return;
				}
				regInt.write((int) addr, (int) val);

				regInt.disconnect();
			}
		});
		GridData gd_btnNewButton_1 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnNewButton_1.minimumWidth = 100;
		writeButton.setLayoutData(gd_btnNewButton_1);
		writeButton.setText("Write");

		Button readButton = new Button(shell, SWT.NONE);
		readButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long addr = getNumber(address.getText(), decAddr.getSelection());
				if (addr == -1) {
					Util.showError("Address is not a valid " + (decAddr.getSelection() ? "decimal" : "hex") + " number", shell);
					return;
				}
				RegisterInterface regInt = new RegisterInterface();
				if (!regInt.connect(port)) {
					Util.showError("Failed to connect to serial port!", shell);
					return;
				}
				try {
					int val = regInt.read((int) addr);
					value.setText(Integer.toString(val, decVal.getSelection() ? 10 : 16));
				} catch (SerialPortIOException | SerialPortTimeoutException e1) {
					Util.showError("Failed to read data!", shell);
				} finally {
					regInt.disconnect();
				}

			}
		});
		GridData gd_btnNewButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 100;
		gd_btnNewButton.minimumWidth = 100;
		readButton.setLayoutData(gd_btnNewButton);
		readButton.setText("Read");

		shell.pack();
		shell.setMinimumSize(shell.getSize());

		updatePorts();
		combo.select(0);
	}

	private long getNumber(String s, boolean dec) {
		try {
			BigInteger bigint = new BigInteger(s, dec ? 10 : 16);
			return bigint.longValue();
		} catch (NumberFormatException | ArithmeticException e) {
			return -1;
		}
	}

	public void setFocus() {
		shell.setFocus();
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}
}
