package com.alchitry.labs.hardware.loaders;

import com.alchitry.labs.Util;
import org.eclipse.swt.custom.StyledText;

public abstract class ProjectLoader {
	protected abstract void eraseFlash();
	protected abstract void program(String binFile, boolean flash, boolean verify);

	public ProjectLoader() {
	}
	
	public interface ProgressCallback {
		public void update(int percent);
	}
	
	public void setProgressCallback(ProgressCallback callback) {
		updateCallback = callback;
	}
	
	protected ProgressCallback updateCallback = new ProgressCallback() {
		@Override
		public void update(int percent) {
			Util.asyncExec(new Runnable() {
				@Override
				public void run() {
					StyledText console = Util.console;
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

					bar.append("]   ").append(percent).append("%     ");

					console.replaceTextRange(lineOffset, lastOffset - lineOffset, bar.toString());
				}
			});
		}
	};

	protected void updateProgress(final int percent) {
		updateCallback.update(percent);
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
