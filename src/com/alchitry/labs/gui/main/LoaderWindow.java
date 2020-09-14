package com.alchitry.labs.gui.main;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.boards.AlchitryAu;
import com.alchitry.labs.hardware.boards.AlchitryCu;
import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.hardware.boards.Mojo;
import com.alchitry.labs.hardware.loaders.ProjectLoader;
import com.alchitry.labs.hardware.loaders.ProjectLoader.ProgressCallback;

public class LoaderWindow {

	protected Shell shell;
	private Text binText;
	private Button btnAuButton;
	private Button btnCuButton;
	private Button btnEraseButton;
	private Button btnFlashCheckbox;
	private Button btnProgramButton;
	private Button btnOpenBinButton;
	private Label lblStatus;
	private Button btnMojoButton;

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		Util.setDisplay(display);
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void setEnabled(boolean enabled) {
		btnAuButton.setEnabled(enabled);
		btnCuButton.setEnabled(enabled);
		btnEraseButton.setEnabled(enabled);
		btnProgramButton.setEnabled(enabled);
		btnOpenBinButton.setEnabled(enabled);
		btnFlashCheckbox.setEnabled(enabled && !btnCuButton.getSelection());
		binText.setEnabled(enabled);
		shell.setEnabled(enabled);
	}

	public void setStatus(String status) {
		Util.syncExec(new Runnable() {
			@Override
			public void run() {
				lblStatus.setText(status);
			}
		});
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		Util.setShell(shell);
		shell.setText("Alchitry Loader V" + MainWindow.VERSION);
		shell.setLayout(new GridLayout(5, false));
		shell.setImage(SWTResourceManager.getImage(LoaderWindow.class, "/images/icon.png"));

		binText = new Text(shell, SWT.BORDER);
		binText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		btnOpenBinButton = new Button(shell, SWT.NONE);
		btnOpenBinButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnOpenBinButton.setText("Open Bin File");

		btnOpenBinButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				FileDialog fd = new FileDialog(shell);
				fd.setFilterExtensions(new String[] { "*.bin", "*.*" });
				fd.setFilterNames(new String[] { "Bin Files", "All Files" });
				String file = fd.open();
				if (file != null)
					binText.setText(file);
			}
		});

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("Board:");

		btnAuButton = new Button(shell, SWT.RADIO);
		btnAuButton.setSelection(true);
		btnAuButton.setText("Alchitry Au");

		btnAuButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				btnFlashCheckbox.setEnabled(true);
			}
		});

		btnCuButton = new Button(shell, SWT.RADIO);
		btnCuButton.setText("Alchitry Cu");

		btnCuButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				btnFlashCheckbox.setSelection(true);
				btnFlashCheckbox.setEnabled(false);
			}
		});

		btnMojoButton = new Button(shell, SWT.RADIO);
		btnMojoButton.setText("Mojo");
		new Label(shell, SWT.NONE);

		btnFlashCheckbox = new Button(shell, SWT.CHECK);
		btnFlashCheckbox.setSelection(true);
		btnFlashCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnFlashCheckbox.setText("Program Flash");
		new Label(shell, SWT.NONE);

		btnEraseButton = new Button(shell, SWT.NONE);
		btnEraseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnEraseButton.setText("Erase");

		btnEraseButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				setEnabled(false);
				setStatus("Starting...");
				erase(getBoard());
			}
		});

		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Status:");

		lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		new Label(shell, SWT.NONE);

		btnProgramButton = new Button(shell, SWT.NONE);
		btnProgramButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnProgramButton.setText("Program");

		btnProgramButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (!checkBinFileExists())
					return;
				setEnabled(false);
				setStatus("Starting...");
				program(binText.getText(), btnFlashCheckbox.getSelection(), getBoard());

			}
		});

		shell.pack();
		Point s = shell.getSize();
		shell.setMinimumSize(s);
		s.x = Math.max(s.x, 500);
		shell.setSize(s);
	}

	private Board getBoard() {
		if (btnAuButton.getSelection())
			return new AlchitryAu();
		else if (btnCuButton.getSelection())
			return new AlchitryCu();
		else
			return new Mojo();
	}

	private boolean checkBinFileExists() {
		File f = new File(binText.getText());
		if (!f.exists()) {
			Util.showError("Bin file could not be opened!");
			return false;
		}
		return true;
	}

	private void program(String binFile, boolean flash, Board board) {
		new Thread(() -> {
			ProjectLoader loader = board.getLoader();
			loader.setProgressCallback(new ProgressCallback() {
				@Override
				public void update(int percent) {
					setStatus(percent + "%");
				}
			});

			loader.load(binFile, flash, false);

			Util.syncExec(() -> {
				setEnabled(true);
			});
		}).start();
	}

	private void erase(Board board) {
		new Thread(() -> {
			board.getLoader().erase();
			Util.syncExec(() -> {
				setEnabled(true);
			});
		}).start();

	}
}
