package com.alchitry.labs;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.alchitry.labs.gui.EmailMessage;

public class Reporter {
	public static void sendFeedback(EmailMessage message) {
		// dummy call for public source
	}

	public static void reportException(Exception e, EmailMessage message) {
		message.body += "\r\n\r\n" + Util.exceptionToString(e);
		sendFeedback(message);
	}
}
