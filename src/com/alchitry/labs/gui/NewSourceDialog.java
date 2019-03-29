package com.alchitry.labs.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.project.SourceFile;

import java.util.function.BiFunction;

public class NewSourceDialog extends Dialog {
	protected SourceFile result;
	protected Shell shell;
	private Board board;

	/**
	 * Create the dialog.
	 *
	 * @param parent
	 * @param board
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

		final Button btn_lucidSourceFile;
		final Button btn_verilogSourceFile;
		final Button btn_constraintsFile;
		{
			{
				Group grp_fileTypeSelect = new Group(shell, SWT.NONE);
				grp_fileTypeSelect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				grp_fileTypeSelect.setText("Select the type of file to add to your project");
				grp_fileTypeSelect.setLayout(new FillLayout());


				btn_lucidSourceFile = new Button(grp_fileTypeSelect, SWT.RADIO);
				btn_lucidSourceFile.setText("Lucid Source");

				btn_verilogSourceFile = new Button(grp_fileTypeSelect, SWT.RADIO);
				btn_verilogSourceFile.setText("Verilog Source");

				btn_constraintsFile = new Button(grp_fileTypeSelect, SWT.RADIO);
				btn_constraintsFile.setText("User Constraints");
			}

			BiFunction<String, String, String> bf_setFileNameExt = new BiFunction<String, String, String>() {
				@Override
				public String apply(String name, String ext) {
					int lastDot = name.lastIndexOf('.');
					if (lastDot >= 0) {
						return name.substring(0, lastDot) + ext;
					} else {
						return name + ext;
					}
				}
			};

			btn_lucidSourceFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					txt_fileName.setText(bf_setFileNameExt.apply(txt_fileName.getText(), ".luc"));
				}
			});

			btn_verilogSourceFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					txt_fileName.setText(bf_setFileNameExt.apply(txt_fileName.getText(), ".v"));
				}
			});

			btn_constraintsFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String[] constratintExtensions = board.getSupportedConstraintExtensions();
					if (constratintExtensions.length > 0) {
						txt_fileName.setText(bf_setFileNameExt.apply(txt_fileName.getText(), constratintExtensions[0]));
					}
				}
			});
		}

		final Button btn_cancel;
		final Button btn_createFile;
		{
			Composite cmp_buttons = new Composite(shell, SWT.NONE);
			cmp_buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			cmp_buttons.setLayout(new GridLayout(2, false));

			btn_cancel = new Button(cmp_buttons, SWT.NONE);
			GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
			gd_btnCancel.widthHint = 100;
			btn_cancel.setLayoutData(gd_btnCancel);
			btn_cancel.setText("Cancel");

			btn_createFile = new Button(cmp_buttons, SWT.NONE);
			GridData gd_btnCreateFile = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
			gd_btnCreateFile.widthHint = 100;
			btn_createFile.setLayoutData(gd_btnCreateFile);
			btn_createFile.setText("Create File");
		}

		{
			Runnable r_onCreateFile = new Runnable() {
				@Override
				public void run() {
					result = new SourceFile();
					result.fileName = txt_fileName.getText();
					if (btn_lucidSourceFile.getSelection()) {
						result.type = SourceFile.LUCID;
						if (!result.fileName.endsWith(".luc")) {
							MessageBox box = new MessageBox(shell, SWT.OK);
							box.setText("Invalid Options");
							box.setMessage("Lucid file names must end with \".luc\".");
							box.open();
							return;
						}
					} else if (btn_verilogSourceFile.getSelection()) {
						result.type = SourceFile.VERILOG;
						if (!result.fileName.endsWith(".v")) {
							MessageBox box = new MessageBox(shell, SWT.OK);
							box.setText("Invalid Options");
							box.setMessage("Verilog file names must end with \".v\".");
							box.open();
							return;
						}
					} else if (btn_constraintsFile.getSelection()) {
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
			};

			txt_fileName.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (event.detail == SWT.TRAVERSE_RETURN) {
						r_onCreateFile.run();
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

			btn_createFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					r_onCreateFile.run();
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
