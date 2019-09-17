package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PrintOptionsDialog extends Dialog {

	protected StyledTextPrintOptions result;
	protected Shell shlPrintOptions;

	private Button lineNumbers;
	private Button header;
	private Button textStyle;
	private Button printButton;
	private Button footer;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public PrintOptionsDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public StyledTextPrintOptions open() {
		createContents();
		shlPrintOptions.open();
		shlPrintOptions.layout();
		Display display = getParent().getDisplay();
		while (!shlPrintOptions.isDisposed()) {
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
		shlPrintOptions = new Shell(getParent(), getStyle());
		shlPrintOptions.setSize(450, 300);
		shlPrintOptions.setText("Print Options");
		shlPrintOptions.setLayout(new GridLayout(1, false));

		lineNumbers = new Button(shlPrintOptions, SWT.CHECK);
		lineNumbers.setText("Print Line Numbers");
		lineNumbers.setSelection(true);

		textStyle = new Button(shlPrintOptions, SWT.CHECK);
		textStyle.setText("Print Code Colors");
		textStyle.setSelection(true);

		header = new Button(shlPrintOptions, SWT.CHECK);
		header.setText("Print Filename");
		header.setSelection(true);

		footer = new Button(shlPrintOptions, SWT.CHECK);
		footer.setText("Print Page Numbers");
		footer.setSelection(true);

		printButton = new Button(shlPrintOptions, SWT.NONE);
		printButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		printButton.setText("Print");
		printButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = new StyledTextPrintOptions();
				result.header = header.getSelection() ? "FILE" : null;
				result.footer = footer.getSelection()
						? StyledTextPrintOptions.SEPARATOR + StyledTextPrintOptions.SEPARATOR + "Page "
								+ StyledTextPrintOptions.PAGE_TAG
						: null;
				result.printLineNumbers = lineNumbers.getSelection();
				result.printTextFontStyle = textStyle.getSelection();
				result.printTextForeground = textStyle.getSelection();
				shlPrintOptions.dispose();
			}

		});

		shlPrintOptions.setDefaultButton(printButton);

		shlPrintOptions.pack();
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shlPrintOptions.getBounds();
		int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
		int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;
		shlPrintOptions.setLocation(new Point(locationX, locationY));
	}
}
