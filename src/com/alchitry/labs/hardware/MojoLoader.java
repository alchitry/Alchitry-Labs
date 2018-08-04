package com.alchitry.labs.hardware;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.MainWindow;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class MojoLoader {
	private Display display;
	private SerialPort serialPort;
	private StyledText console;
	private Thread thread;

	public MojoLoader() {

	}

	public MojoLoader(Display display, StyledText console) {
		this.display = display;
		this.console = console;
	}

	public boolean isLoading() {
		return thread != null && thread.isAlive();
	}

	private void updateProgress(final int percent) {
		if (display != null) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					int lastLine = console.getLineCount() - 1;
					int lineOffset = console.getOffsetAtLine(lastLine);
					int lastOffset = console.getCharCount() - 1;
					if (lastOffset < lineOffset)
						lastOffset = lineOffset;

					StringBuilder bar = new StringBuilder("[");

					for (int i = 0; i < 50; i++) {
						if (i < (percent / 2)) {
							bar.append("=");
						} else if (i == (percent / 2)) {
							bar.append(">");
						} else {
							bar.append(" ");
						}
					}

					bar.append("]   " + percent + "%     ");

					console.replaceTextRange(lineOffset, lastOffset - lineOffset, bar.toString());
				}
			});
		} else {
			Util.print("\r" + Integer.toString(percent)+"%");
		}
	}

	private void restartMojo() throws InterruptedException, SerialPortException {
		serialPort.setDTR(false);
		Thread.sleep(5);
		for (int i = 0; i < 5; i++) {
			serialPort.setDTR(false);
			Thread.sleep(5);
			serialPort.setDTR(true);
			Thread.sleep(5);
		}
	}

	public void clearFlash(final String port) {
		thread = new Thread() {
			public void run() {
				MainWindow.mainWindow.enableMonitor(false);
				Util.clearConsole();
				Util.println("Connecting...");
				try {
					serialPort = Util.connect(port);
				} catch (Exception e) {
					onError("Could not connect to port " + port + "!");
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}

				try {
					restartMojo();
				} catch (InterruptedException | SerialPortException e) {
					onError(e.getMessage());
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}

				try {
					Util.println("Erasing...");

					serialPort.readBytes(); // flush the buffer

					serialPort.writeByte((byte) 'E'); // Erase flash

					if (serialPort.readBytes(1, 10000)[0] != 'D') {
						onError("Mojo did not acknowledge flash erase!");
						return;
					}

					Util.println("Done");

				} catch (SerialPortException | SerialPortTimeoutException e) {
					onError(e.getMessage());
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}

				try {
					serialPort.closePort();
				} catch (SerialPortException e) {
					onError(e.getMessage());
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}
				MainWindow.mainWindow.enableMonitor(true);
			}
		};
		thread.start();
	}

	public void sendBin(final String port, final String binFile, final boolean flash, final boolean verify) {
		if (Util.isGUI) {
			thread = new Thread() {
				public void run() {
					binSender(port, binFile, flash, verify);
				}
			};
			thread.start();
		} else {
			binSender(port, binFile, flash, verify);
		}
	}

	private void binSender(final String port, final String binFile, final boolean flash, final boolean verify) {
		MainWindow.mainWindow.enableMonitor(false);
		Util.clearConsole();
		Util.println("Connecting...");

		try {
			serialPort = Util.connect(port);
		} catch (Exception e) {
			onError("Could not connect to port " + port + "!");
			MainWindow.mainWindow.enableMonitor(true);
			return;
		}

		File file = new File(binFile);
		InputStream bin = null;
		try {
			bin = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			onError("The bin file could not be opened!");
			MainWindow.mainWindow.enableMonitor(true);
			return;
		}

		try {
			restartMojo();
		} catch (InterruptedException | SerialPortException e) {
			onError(e.getMessage());
			try {
				bin.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			MainWindow.mainWindow.enableMonitor(true);
			return;
		}

		try {
			serialPort.readBytes(); // flush the buffer

			if (flash)
				Util.println("Erasing flash...");
			else
				Util.println("Loading to RAM...");

			if (flash) {
				if (verify)
					serialPort.writeByte((byte) 'V'); // Write to flash
				else
					serialPort.writeByte((byte) 'F');
			} else {
				serialPort.writeByte((byte) 'R'); // Write to FPGA
			}

			if (serialPort.readBytes(1, 2000)[0] != 'R') {
				onError("Mojo did not respond! Make sure the port is correct.");
				bin.close();
				return;
			}

			int length = (int) file.length();

			byte[] buff = new byte[4];

			for (int i = 0; i < 4; i++) {
				buff[i] = (byte) (length >> (i * 8) & 0xff);
			}

			serialPort.writeBytes(buff);

			if (serialPort.readBytes(1, 10000)[0] != 'O') {
				onError("Mojo did not acknowledge transfer size!");
				bin.close();
				MainWindow.mainWindow.enableMonitor(true);
				return;
			}

			if (flash)
				Util.println("Loading to flash...");

			int num;
			int count = 0;
			int oldCount = 0;
			int percent = length / 100;
			byte[] data = new byte[percent];
			while (true) {
				int avail = bin.available();
				avail = avail > percent ? percent : avail;
				if (avail == 0)
					break;
				int read = bin.read(data, 0, avail);
				serialPort.writeBytes(Arrays.copyOf(data, read));
				count += read;

				if (count - oldCount > percent) {
					oldCount = count;
					float prog = (float) count / length;
					updateProgress(Math.round(prog * 100.0f));
				}
			}

			updateProgress(100);
			Util.println("");

			if (serialPort.readBytes(1, 2000)[0] != 'D') {
				onError("Mojo did not acknowledge the transfer!");
				bin.close();
				MainWindow.mainWindow.enableMonitor(true);
				return;
			}

			bin.close();

			if (flash && verify) {
				Util.println("Verifying...");
				bin = new BufferedInputStream(new FileInputStream(file));
				serialPort.writeByte((byte) 'S');

				int size = (int) (file.length() + 5);

				int tmp;
				if (((tmp = serialPort.readBytes(1, 2000)[0]) & 0xff) != 0xAA) {
					onError("Flash does not contain valid start byte! Got: " + tmp);
					bin.close();
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}

				int flashSize = 0;
				for (int i = 0; i < 4; i++) {
					flashSize |= (((int) serialPort.readBytes(1, 2000)[0]) & 0xff) << (i * 8);
				}

				if (flashSize != size) {
					onError("File size mismatch!\nExpected " + size + " and got " + flashSize);
					bin.close();
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}

				count = 0;
				oldCount = 0;
				while ((num = bin.read()) != -1) {
					int d = (((int) serialPort.readBytes(1, 2000)[0]) & 0xff);
					if (d != num) {
						onError("Verification failed at byte " + count + " out of " + length + "\nExpected " + num + " got " + d);
						bin.close();
						MainWindow.mainWindow.enableMonitor(true);
						return;
					}
					count++;
					if (count - oldCount > percent) {
						oldCount = count;
						float prog = (float) count / length;
						updateProgress(Math.round(prog * 100.0f));
					}
				}
				updateProgress(100);
				Util.println("");
			}

			if (flash) {
				serialPort.writeByte((byte) 'L');
				if ((((int) serialPort.readBytes(1, 5000)[0]) & 0xff) != 'D') {
					onError("Could not load from flash!");
					bin.close();
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}
			}

			bin.close();
		} catch (IOException | SerialPortException | SerialPortTimeoutException e) {
			onError(e.getMessage());
			return;
		}

		Util.println("Done");

		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			onError(e.getMessage());
			return;
		}

		MainWindow.mainWindow.enableMonitor(true);
	}

	private void onError(String e) {
		if (e == null)
			e = "";

		Util.println("Error: " + e, true);
		if (serialPort != null)
			try {
				serialPort.closePort();
			} catch (SerialPortException e1) {
			}
	}

}
