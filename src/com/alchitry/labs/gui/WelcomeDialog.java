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
import org.eclipse.wb.swt.SWTResourceManager;

import com.alchitry.labs.gui.main.MainWindow;

public class WelcomeDialog extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public WelcomeDialog(Shell parent, int style) {
		super(parent, style | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("Welcome");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
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
		shell.setMinimumSize(new Point(700, 450));
		shell.setSize(700, 450);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));

		Label lblTitle = new Label(shell, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblTitle.setAlignment(SWT.CENTER);
		lblTitle.setFont(SWTResourceManager.getFont("Ubuntu", 24, SWT.BOLD));
		lblTitle.setText("Welcome to Alchtry Labs");

		Label lblVersion_1 = new Label(shell, SWT.NONE);
		lblVersion_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVersion_1.setText("Version " + MainWindow.VERSION);

		Label lblReleaseInfo = new Label(shell, SWT.WRAP);
		lblReleaseInfo.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, true, 1, 1));
		lblReleaseInfo
				.setText("Welcome to version "
						+ MainWindow.VERSION
						+ " of Alchitry Labs! We are actively working to improve the IDE so please send us your feedback"
						+ " at bugspray@alchitry.com\n\n"
						+ "This version fixed a bug when parsing .acf files."
						+ "\n\nAs always, we hope you enjoy this version!");

		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		GridData gd_btnOk = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnOk.widthHint = 100;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("Ok");
		
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width)/2+parentSize.x;
		int locationY = (parentSize.height - shellSize.height)/2+parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}

}
