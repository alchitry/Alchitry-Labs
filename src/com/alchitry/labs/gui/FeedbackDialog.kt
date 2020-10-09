package com.alchitry.labs.gui;

import com.alchitry.labs.MainKt;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class FeedbackDialog {

    protected EmailMessage result = null;
    protected Shell shell;
    private Text nameText;
    private Text emailText;
    private Text commentsText;
    private Button submitButton;


    public FeedbackDialog() {
    }

    /**
     * Open the dialog.
     *
     * @return the result
     */
    public EmailMessage open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = Display.getDefault();
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
        shell = new Shell(Display.getDefault(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.CLOSE);
        shell.setSize(450, 300);
        shell.setText("FeedbackForm");
        shell.setLayout(new GridLayout(2, false));

        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText("Name:");

        nameText = new Text(shell, SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Email:");

		emailText = new Text(shell, SWT.BORDER);
		emailText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_2.setText("Comments:");

		commentsText = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		commentsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		new Label(shell, SWT.NONE);
		submitButton = new Button(shell, SWT.NONE);
		submitButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		submitButton.setText("Submit");

		submitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				result = new EmailMessage();
				result.email = emailText.getText();
				if (result.email.isEmpty())
					result.email = "noreply@alchitry.com";
				result.subject = "Feedback Report for " + MainKt.VERSION + " from " + nameText.getText();
				result.body = commentsText.getText();
				shell.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

}
