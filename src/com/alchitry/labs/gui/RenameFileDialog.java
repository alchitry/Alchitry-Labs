package com.alchitry.labs.gui;

import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.project.SourceFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class RenameFileDialog extends Dialog {
	protected SourceFile result;
	protected Shell shell;
	private SourceFile oldFile;
	private Board board;

	public RenameFileDialog(Shell parent, SourceFile oldFile, Board board) {
		super(parent);
		setText("Rename");
		this.oldFile = oldFile;
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
		shell.setText(getText());
		shell.setLayout(new GridLayout());

		final Text txt_fileName;
		{
			Composite cmp_fileName = new Composite(shell, SWT.NONE);
			cmp_fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			cmp_fileName.setLayout(new GridLayout(2, false));

			Label lbl_fileName = new Label(cmp_fileName, SWT.NONE);
			lbl_fileName.setText("File name:");
			lbl_fileName.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

			txt_fileName = new Text(cmp_fileName, SWT.BORDER);
			txt_fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}

		final Button btn_cancel;
		final Button btn_rename;
		{
			Composite cmp_buttons = new Composite(shell, SWT.NONE);
			cmp_buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			cmp_buttons.setLayout(new GridLayout(2, false));

			btn_cancel = new Button(cmp_buttons, SWT.NONE);
			GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
			gd_btnCancel.widthHint = 100;
			btn_cancel.setLayoutData(gd_btnCancel);
			btn_cancel.setText("Cancel");

			btn_rename = new Button(cmp_buttons, SWT.NONE);
			GridData gd_btnCreateFile = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
			gd_btnCreateFile.widthHint = 100;
			btn_rename.setLayoutData(gd_btnCreateFile);
			btn_rename.setText("Create File");
		}

		{
			Runnable r_onRename = new Runnable() {
				@Override
				public void run() {
					result = new SourceFile();
					result.fileName = txt_fileName.getText();
					switch (oldFile.type) {
						case SourceFile.LUCID:
							if (!result.fileName.endsWith(".luc")) {
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("Invalid Options");
								box.setMessage("Lucid file names must end with \".luc\".");
								box.open();
								return;
							}
							break;
						case SourceFile.VERILOG:
							if (!result.fileName.endsWith(".v")) {
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("Invalid Options");
								box.setMessage("Verilog file names must end with \".v\".");
								box.open();
								return;
							}
							break;
						case SourceFile.CONSTRAINT:
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
							break;
					}
					shell.close();
				}
			};

			txt_fileName.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (event.detail == SWT.TRAVERSE_RETURN) {
						r_onRename.run();
					}
				}
			});

			btn_cancel.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					result = null;
					shell.close();
				}
			});

			btn_rename.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					r_onRename.run();
				}
			});
		}

		shell.pack();

		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
		int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}

}
