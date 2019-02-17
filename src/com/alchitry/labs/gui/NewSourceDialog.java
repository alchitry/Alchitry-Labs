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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.alchitry.labs.boards.Board;
import com.alchitry.labs.project.SourceFile;

public class NewSourceDialog extends Dialog {

	protected SourceFile result;
	protected Shell shell;
	private Text text;
	private Board board;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public NewSourceDialog(Shell parent, Board board) {
		super(parent);
		setText("New File...");
		this.board = board;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public SourceFile open() {
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
		shell.setSize(615, 472);
		shell.setText(getText());
		shell.setLayout(new GridLayout(6, false));

		Label lblFileName = new Label(shell, SWT.NONE);
		lblFileName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFileName.setText("File name:");

		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

		Label lblSelectAType = new Label(shell, SWT.NONE);
		lblSelectAType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		lblSelectAType.setText("Select the type of file to add to your project.");

		final Button btnLucidSourceFile = new Button(shell, SWT.RADIO);
		btnLucidSourceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = text.getText();
				String newName;
				int lastDot = name.lastIndexOf('.');
				if (lastDot >= 0)
					newName = name.substring(0, lastDot) + ".luc";
				else
					newName = name + ".luc";

				text.setText(newName);

			}
		});
		btnLucidSourceFile.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnLucidSourceFile.setText("Lucid Source");

		final Button btnVerilogSourceFile = new Button(shell, SWT.RADIO);
		btnVerilogSourceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = text.getText();
				String newName;
				int lastDot = name.lastIndexOf('.');
				if (lastDot >= 0)
					newName = name.substring(0, lastDot) + ".v";
				else
					newName = name + ".v";

				text.setText(newName);

			}
		});
		btnVerilogSourceFile.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnVerilogSourceFile.setText("Verilog Source");

		final Button btnConstraintsFile = new Button(shell, SWT.RADIO);
		btnConstraintsFile.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnConstraintsFile.setText("User Constraints");
		btnConstraintsFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] constratintExtensions = board.getSupportedConstraintExtensions();
				if (constratintExtensions.length > 0) {
					String ext = constratintExtensions[0];
					String name = text.getText();
					String newName;
					int lastDot = name.lastIndexOf('.');
					if (lastDot >= 0)
						newName = name.substring(0, lastDot) + ext;
					else
						newName = name + ext;

					text.setText(newName);
				}
			}
		});
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shell.close();
			}
		});
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 100;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		Button btnCreateFile = new Button(shell, SWT.NONE);
		btnCreateFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = new SourceFile();
				result.fileName = text.getText();
				if (btnLucidSourceFile.getSelection()) {
					result.type = SourceFile.LUCID;
					if (!result.fileName.endsWith(".luc")) {
						MessageBox box = new MessageBox(shell, SWT.OK);
						box.setText("Invalid Options");
						box.setMessage("Lucid file names must end with \".luc\".");
						box.open();
						return;
					}
				} else if (btnVerilogSourceFile.getSelection()) {
					result.type = SourceFile.VERILOG;
					if (!result.fileName.endsWith(".v")) {
						MessageBox box = new MessageBox(shell, SWT.OK);
						box.setText("Invalid Options");
						box.setMessage("Verilog file names must end with \".v\".");
						box.open();
						return;
					}
				} else if (btnConstraintsFile.getSelection()) {
					result.type = SourceFile.CONSTRAINT;
					boolean acceptable = false;
					String[] constraintTypes = board.getSupportedConstraintExtensions();
					for (String c : constraintTypes)
						if (result.fileName.endsWith(c)) {
							acceptable = true;
							break;
						}
					if (!acceptable) {
						MessageBox box = new MessageBox(shell, SWT.OK);
						box.setText("Invalid Options");
						box.setMessage("Constraint file names must end with \"" + String.join("\", \"", constraintTypes) + "\".");
						box.open();
						return;
					}
				} else {
					result = null;
					MessageBox box = new MessageBox(shell, SWT.OK);
					box.setText("Invalid Options");
					box.setMessage("You must select the file type.");
					box.open();
					return;
				}
				shell.close();
			}
		});
		GridData gd_btnCreateFile = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCreateFile.widthHint = 100;
		btnCreateFile.setLayoutData(gd_btnCreateFile);
		btnCreateFile.setText("Create File");

		shell.pack();
		
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width)/2+parentSize.x;
		int locationY = (parentSize.height - shellSize.height)/2+parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}

}
