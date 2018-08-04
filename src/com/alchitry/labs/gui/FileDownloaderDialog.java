package com.alchitry.labs.gui;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.alchitry.labs.Download;
import com.alchitry.labs.Util;

public class FileDownloaderDialog extends Dialog {

	protected boolean result;
	protected Shell shell;
	private String name;
	private String destination;
	private URL url;
	private Download downloader;
	private ProgressBar progressBar;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public FileDownloaderDialog(Shell parent, URL url, String destination, String name) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("Downloading...");
		result = false;
		this.name = name;
		this.destination = destination;
		this.url = url;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public boolean open() {
		createContents();
		shell.open();
		shell.layout();
		final Display display = getParent().getDisplay();

		downloader = new Download(url, destination);

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				switch (downloader.getStatus()) {
				case Download.DOWNLOADING:
					display.timerExec(250, this); // run again in 1/4 second
					if (!progressBar.isDisposed())
						progressBar.setSelection((int) downloader.getProgress());
					break;
				case Download.COMPLETE:
					result = true;
					if (!shell.isDisposed())
						shell.close();
					break;
				case Download.CANCELLED:
					if (!shell.isDisposed())
						shell.close();
					break;
				case Download.PAUSED:
					break;
				case Download.ERROR:
					if (!shell.isDisposed())
						shell.close();
					Util.showError("Failed to download file " + url);
					break;
				}
			}
		});

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
		shell.setMinimumSize(450, 0);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		shell.addListener(SWT.Close, new Listener() {

			@Override
			public void handleEvent(Event event) {
				downloader.cancel();
			}
		});

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("Downloading " + name + "...");

		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setSelection(0);

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("Cancel");
		btnNewButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		shell.pack();
	}
}
