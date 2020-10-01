package com.alchitry.labs.gui;

import com.alchitry.labs.gui.main.MainWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.List;

public class DeviceSelector extends Dialog {

	public static class DeviceSelectorRunnable implements Runnable {
		public String result;
		private List<String> entries;

		public DeviceSelectorRunnable(List<String> list) {
			entries = list;
		}

		@Override
		public void run() {
			DeviceSelector selector = new DeviceSelector(MainWindow.INSTANCE.getShell(), 0);
			result = selector.open(entries);
		}

	}

	protected String result;
	protected Shell shell;
	private List<String> devices;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public DeviceSelector(Shell parent, int style) {
		super(parent, style);
		setText("Device Selector");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public String open(List<String> devices) {
		this.devices = devices;
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent().getDisplay(), getStyle() | SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(3, false));

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Device:");

		Combo combo = new Combo(shell, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(shell, SWT.NONE);
		for (String d : devices)
			combo.add(d);
		combo.select(0);

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = null;
				shell.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Button btnSelect = new Button(shell, SWT.NONE);
		btnSelect.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnSelect.setText("Select");
		btnSelect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = combo.getItem(combo.getSelectionIndex());
				shell.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		shell.pack();
		
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width)/2+parentSize.x;
		int locationY = (parentSize.height - shellSize.height)/2+parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}
}
