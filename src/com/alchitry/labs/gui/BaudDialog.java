package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BaudDialog extends Dialog {

	protected int result = -1;
	protected Shell shell;
	private Text text;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public BaudDialog(Shell parent, int style) {
		super(parent, style);
		setText("Set a Baud Rate");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public int open(int defaultBaud) {
		createContents(defaultBaud);
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
	private void createContents(int defaultBaud) {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(3, false));

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Baud Rate:");

		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		text.setText(Integer.toString(defaultBaud));

		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				Text text = (Text) e.getSource();
				final String oldS = text.getText();
				String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
				boolean isInt = true;
				if (!newS.isEmpty())
					try {
						Integer.parseUnsignedInt(newS);
					} catch (NumberFormatException ex) {
						isInt = false;
					}

				if (!isInt)
					e.doit = false;
			}
		});
		new Label(shell, SWT.NONE);

		Button baudButton = new Button(shell, SWT.NONE);
		baudButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = Integer.parseUnsignedInt(text.getText());
				shell.dispose();
			}
		});
		GridData gd_btnNewButton_1 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnNewButton_1.widthHint = 100;
		baudButton.setLayoutData(gd_btnNewButton_1);
		baudButton.setText("Set Baud");

		Button cancelButton = new Button(shell, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 100;
		cancelButton.setLayoutData(gd_btnNewButton);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = -1;
				shell.dispose();
			}
		});

		shell.pack();
		
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
		int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;
		shell.setLocation(new Point(locationX, locationY));

	}

}
