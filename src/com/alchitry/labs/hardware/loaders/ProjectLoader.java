package com.alchitry.labs.hardware.loaders;

import org.eclipse.swt.custom.StyledText;

import com.alchitry.labs.Util;

public abstract class ProjectLoader {
	protected abstract void eraseFlash();

	protected abstract void program(String binFile, boolean flash, boolean verify);

	public ProjectLoader() {
	}

	protected static void updateProgress(final int percent) {
		Util.asyncExec(new Runnable() {
			@Override
			public void run() {
				StyledText console = Util.getConsole();
				int lastLine = console.getLineCount() - 1;
				int lineOffset = console.getOffsetAtLine(lastLine);
				int lastOffset = console.getCharCount() - 1;
				if (lastOffset < lineOffset)
					lastOffset = lineOffset;

				StringBuilder bar = new StringBuilder("[");

				for (int i = 0; i < 50; i++) {
					if (i < (percent / 2)) {
						bar.append('=');
					} else if (i == (percent / 2)) {
						bar.append('>');
					} else {
						bar.append(' ');
					}
				}

				bar.append("]   " + percent + "%     ");

				console.replaceTextRange(lineOffset, lastOffset - lineOffset, bar.toString());
			}
		});
	}

	public void load(final String binFile, final boolean flash, final boolean verify) {
		Util.clearConsole();

		program(binFile, flash, verify);
	}

	public void erase() {
		Util.clearConsole();

		eraseFlash();
	}
}
