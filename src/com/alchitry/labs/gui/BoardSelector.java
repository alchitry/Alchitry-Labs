package com.alchitry.labs.gui;

import java.util.ArrayList;

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

import com.alchitry.labs.boards.Board;

public class BoardSelector extends Dialog {

	protected Board result;
	protected Shell shell;

	private ArrayList<Button> selection;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public BoardSelector(Shell parent, int style) {
		super(parent, style | SWT.DIALOG_TRIM);
		setText("Board Selector");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Board open() {
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 69, 21);
		lblNewLabel.setText("Select Your Board:");
		new Label(shell, SWT.NONE);

		selection = new ArrayList<>();

		for (Board b : Board.boards) {
			Button btnRadioButton = new Button(shell, SWT.RADIO);
			btnRadioButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			btnRadioButton.setText(b.getName());
			selection.add(btnRadioButton);
		}

		Button btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.minimumWidth = 100;
		gd_btnCancel.widthHint = 100;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}

		});

		Button btnFlash = new Button(shell, SWT.NONE);
		GridData gd_btnFlash = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnFlash.widthHint = 100;
		gd_btnFlash.minimumWidth = 100;
		btnFlash.setLayoutData(gd_btnFlash);
		btnFlash.setText("Flash");
		btnFlash.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (Button b : selection) {
					if (b.getSelection()) {
						result = Board.getFromName(b.getText());
						shell.dispose();
						return;
					}
				}
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
