package com.alchitry.labs.hardware.loaders;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.hardware.usb.SerialDevice;
import com.alchitry.labs.hardware.usb.UsbUtil;

public class MojoLoader extends ProjectLoader {
	private SerialDevice mojo;

	public MojoLoader() {

	}

	private void restartMojo() {
		mojo.setDtrRts(false, true);
		Util.sleep(5);
		for (int i = 0; i < 5; i++) {
			mojo.setDtrRts(false, true);
			Util.sleep(5);
			mojo.setDtrRts(true, true);
			Util.sleep(5);
		}
	}

	private void onError(String e) {
		if (e == null)
			e = "";

		Util.println("Error: " + e, true);
		if (mojo != null)
			mojo.close();
	}

	private boolean connect() {
		Util.println("Connecting...");
		try {
			mojo = UsbUtil.openMojoSerial();
		} catch (Exception e) {
			onError("Error while opening Mojo! " + e.getMessage());
			return false;
		}

		if (mojo == null) {
			Util.showError("Could not detect a Mojo!");
			return false;
		}

		return true;
	}

	private boolean writeByte(byte b) {
		return mojo.writeData(new byte[] { b }) == 1;
	}

	private byte readByte() {
		byte[] bbuf = new byte[1];
		if (mojo.readDataWithTimeout(bbuf) != bbuf.length)
			throw new RuntimeException("Reading " + bbuf.length + " bytes took too long!");
		return bbuf[0];
	}

	@Override
	protected void eraseFlash() {
		try {
			if (!connect())
				return;

			restartMojo();

			Util.println("Erasing...");

			mojo.flushReadBuffer();

			writeByte((byte) 'E');

			Util.println("Wrote E");

			if (readByte() != 'D') {
				onError("Mojo did not acknowledge flash erase!");
				return;
			}

			Util.println("Done.", Theme.successTextColor);

		} catch (Exception e) {
			Util.logException(e);
		} finally {
			mojo.close();
		}
	}

	@Override
	protected void program(String binFile, boolean flash, boolean verify) {
		if (!connect())
			return;

		File file = new File(binFile);
		InputStream bin = null;
		try {
			bin = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			onError("The bin file could not be opened!");
			return;
		}

		Util.println("Restarting Mojo...");

		restartMojo();

		try {
			mojo.flushReadBuffer();

			if (flash)
				Util.println("Erasing flash...");
			else
				Util.println("Loading to RAM...");

			if (flash) {
				if (verify)
					writeByte((byte) 'V'); // Write to flash
				else
					writeByte((byte) 'F');
			} else {
				writeByte((byte) 'R'); // Write to FPGA
			}

			if (readByte() != 'R') {
				onError("Mojo did not respond! Make sure the port is correct.");
				bin.close();
				return;
			}

			int length = (int) file.length();

			byte[] buff = new byte[4];

			for (int i = 0; i < 4; i++) {
				buff[i] = (byte) (length >> (i * 8) & 0xff);
			}

			if (mojo.writeData(buff) != buff.length) {
				onError("Failed to write the transfer size!");
				bin.close();
				return;
			}

			if (readByte() != 'O') {
				onError("Mojo did not acknowledge transfer size!");
				bin.close();
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
				mojo.writeData(Arrays.copyOf(data, read));
				count += read;

				if (count - oldCount > percent) {
					oldCount = count;
					float prog = (float) count / length;
					updateProgress(Math.round(prog * 100.0f));
				}
			}

			updateProgress(100);
			Util.println("");

			if (readByte() != 'D') {
				onError("Mojo did not acknowledge the transfer!");
				bin.close();
				return;
			}

			bin.close();

			if (flash && verify) {
				Util.println("Verifying...");
				bin = new BufferedInputStream(new FileInputStream(file));
				writeByte((byte) 'S');

				int size = (int) (file.length() + 5);

				int tmp;
				if (((tmp = readByte()) & 0xff) != 0xAA) {
					onError("Flash does not contain valid start byte! Got: " + tmp);
					bin.close();
					return;
				}

				int flashSize = 0;
				for (int i = 0; i < 4; i++) {
					flashSize |= (((int) readByte()) & 0xff) << (i * 8);
				}

				if (flashSize != size) {
					onError("File size mismatch!\nExpected " + size + " and got " + flashSize);
					bin.close();
					return;
				}

				count = 0;
				oldCount = 0;
				while ((num = bin.read()) != -1) {
					int d = (((int) readByte()) & 0xff);
					if (d != num) {
						onError("Verification failed at byte " + count + " out of " + length + "\nExpected " + num + " got " + d);
						bin.close();
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
				writeByte((byte) 'L');
				if ((((int) readByte()) & 0xff) != 'D') {
					onError("Could not load from flash!");
					bin.close();
					return;
				}
			}

			bin.close();
		} catch (Exception e) {
			Util.logException(e);
			return;
		} finally {
			mojo.close();
		}

		Util.println("Done.", Theme.successTextColor);

	}

}
