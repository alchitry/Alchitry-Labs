package com.alchitry.labs.gui;

import java.util.prefs.BackingStoreException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.alchitry.labs.Settings;
import com.alchitry.labs.Util;

public class ThemeSelectorDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	
	private Button btnDarkTheme;
	private Button btnLightTheme;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ThemeSelectorDialog(Shell parent) {
		super(parent);
		setText("Theme Selector");
	}

	/**
	 * Open the dialog.
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(3, false));
		
		Label lblSele = new Label(shell, SWT.NONE);
		lblSele.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		lblSele.setText("Select a Theme:");
		
		btnDarkTheme = new Button(shell, SWT.RADIO);
		GridData gd_btnDarkTheme = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_btnDarkTheme.horizontalIndent = 20;
		btnDarkTheme.setLayoutData(gd_btnDarkTheme);
		btnDarkTheme.setText("Dark Theme");
		
		btnLightTheme = new Button(shell, SWT.RADIO);
		GridData gd_btnLightTheme = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_btnLightTheme.horizontalIndent = 20;
		btnLightTheme.setLayoutData(gd_btnLightTheme);
		btnLightTheme.setText("Light Theme");
		new Label(shell, SWT.NONE);
		
		Button btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnNewButtocanceln = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnNewButtocanceln.widthHint = 100;
		btnCancel.setLayoutData(gd_btnNewButtocanceln);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				shell.close();
			}
		});
		
		Button btnSelect = new Button(shell, SWT.NONE);
		GridData gd_btnSelect = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelect.widthHint = 100;
		btnSelect.setLayoutData(gd_btnSelect);
		btnSelect.setText("Select");
		btnSelect.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				saveSelection();
				Util.showInfo("Theme Selection", "Note that your theme selection will take effect next time you launch the Mojo IDE!");
				shell.close();
			}
		});
		
		setDefault();
		
		shell.pack();
		
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width)/2+parentSize.x;
		int locationY = (parentSize.height - shellSize.height)/2+parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}
	
	private void saveSelection() {
		Settings.pref.putBoolean(Settings.THEME, btnLightTheme.getSelection());
		try {
			Settings.pref.flush();
		} catch (BackingStoreException e) {
			Util.showError("Failed to save your theme!");
		}
	}
	
	private void setDefault() {
		if (Settings.pref.getBoolean(Settings.THEME, false)){
			btnLightTheme.setSelection(true);
		} else {
			btnDarkTheme.setSelection(true);
		}
	}

}
