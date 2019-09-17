package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

public class RenameDialog extends Dialog {

	protected String result;
	protected Shell shlRename;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RenameDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open(String original) {
		createContents();
		shlRename.open();
		text.setText(original);
		shlRename.layout();
		Display display = getParent().getDisplay();
		while (!shlRename.isDisposed()) {
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
		shlRename = new Shell(getParent(), getStyle());
		shlRename.setSize(450, 300);
		shlRename.setText("Rename");
		shlRename.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(shlRename, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("New Name:");
		
		text = new Text(shlRename, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.minimumWidth = 300;
		text.setLayoutData(gd_text);
		new Label(shlRename, SWT.NONE);
		
		Button btnNewButton = new Button(shlRename, SWT.NONE);
		shlRename.setDefaultButton(btnNewButton);
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("Rename");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = text.getText();
				shlRename.dispose();
			}
		});
		
		shlRename.pack();
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shlRename.getBounds();
		int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
		int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;
		shlRename.setLocation(new Point(locationX, locationY));
	}
}
