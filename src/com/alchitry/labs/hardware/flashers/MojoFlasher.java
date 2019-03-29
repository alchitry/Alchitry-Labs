package com.alchitry.labs.hardware.flashers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.SerialPortSelector;
import com.alchitry.labs.gui.main.MainWindow;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortIOException;

public class MojoFlasher extends Flasher {

	private int attemptFlash(boolean isV3, String port) {
		BufferedWriter out = null;
		int status = -1;
		try {
			String avrDude = Util.assemblePath(Locations.BIN, Util.isWindows ? "avrdude.exe" : "avrdude");
			String configPath = Util.assemblePath(Locations.ETC, "avrdude.conf");
			String hexFile = Util.assemblePath(Locations.FIRMWARE, (isV3 ? "mojo-v3-loader.hex" : "mojo-v2-loader.hex"));
			String avr = isV3 ? "atmega32u4" : "atmega16u4";
			String board = isV3 ? "Mojo V3" : "Mojo V2";

			if (!new File(avrDude).exists()) {
				Util.log.severe("Couldn't find avrdude");
				Util.println("The loading program avrdude couldn't be found!", true);
				return -1;
			}

			if (!new File(hexFile).exists()) {
				Util.log.severe("Couldn't find hex file");
				Util.println("Could not find the firmware for " + board + ". Looked at " + hexFile, true);
				return -1;
			}

			ArrayList<String> cmd = new ArrayList<>();
			cmd.add(avrDude);
			cmd.add("-p");
			cmd.add(avr);
			cmd.add("-P");
			cmd.add(port);
			cmd.add("-c");
			cmd.add("avr109");
			cmd.add("-C");
			cmd.add(configPath);
			cmd.add("-U");
			cmd.add("flash:w:" + hexFile + ":i");

			ProcessBuilder pb = new ProcessBuilder(cmd);

			final Process p;
			try {
				p = pb.start();
			} catch (Exception e) {
				Util.log.severe("Couldn't start avrdude. Tried " + avrDude);
				Util.println("Could not start avrdude!", true);
				return -1;
			}

			new Thread() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					try {
						while ((line = br.readLine()) != null) {
							Util.print(line + System.lineSeparator(), false);
						}
					} catch (IOException e) {
						Util.log.severe(ExceptionUtils.getStackTrace(e));
						Util.print(e.getMessage(), true);
					} finally {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			new Thread() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String line;
					try {
						while ((line = br.readLine()) != null) {
							Util.print(line + System.lineSeparator(), false);
						}
					} catch (IOException e) {
						Util.log.severe(ExceptionUtils.getStackTrace(e));
						Util.print(e.getMessage(), true);
					} finally {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			status = p.waitFor();

		} catch (Exception e) {
			Util.println("ERROR: " + e.getMessage(), true);
			Util.log.severe(ExceptionUtils.getStackTrace(e));
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			MainWindow.mainWindow.enableMonitor(true);
		}
		return status;
	}

	@Override
	public void flash() {
		SerialPortSelector dialog = new SerialPortSelector(MainWindow.mainWindow.getShell(), Util.getSerialPortNames());
		final String portName = dialog.open();
		if (portName == null)
			return;
		thread = new Thread() {
			public void run() {
				MainWindow.mainWindow.enableMonitor(false);
				Util.clearConsole();

				Util.println("Resetting into bootloader mode...");

				String port = portName;
				List<String> ports = Arrays.asList(Util.getSerialPortNames());
				try {
					touchForCDCReset(port); // connect and close at 1200 baud
				} catch (Exception e) {
					Util.showError("Could not reset Mojo", e.getMessage());
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				List<String> newPorts = Arrays.asList(Util.getSerialPortNames());
				if (!newPorts.contains(port)) { // if the same port wasn't used
					port = null;
					int ct = 0;
					while (port == null && ct++ < 5) { // limit wait time
						try {
							Thread.sleep(1000); // delay a sec
						} catch (InterruptedException e) {
						}

						for (String s : Util.getSerialPortNames()) { // check current ports
							if (!ports.contains(s) || s.equals(portName)) // if port is new or returned
								port = s;
						}
					}
				}

				if (port == null) {
					Util.println("Error: Could not find Mojo after reset", true);
					MainWindow.mainWindow.enableMonitor(true);
					return;
				}

				Util.println("Programming Mojo V3 on " + port);

				int status = attemptFlash(true, port);
				if (status != 0) {
					Util.println("Flashing failed. Trying V2 firmware instead!");
					Util.println("Programming Mojo V2 on " + port);
					status = attemptFlash(false, port);
				}

				if (status == 0)
					Util.println("Finished flashing.");
				else
					Util.println("Flashing failed.");

			}
		};

		thread.start();
	}

	private static boolean touchForCDCReset(String iname) throws SerialException {
		SerialPort serialPort = null;
		try {
			serialPort = Util.connect(iname, 1200);
			serialPort.clearDTR();
			serialPort.closePort();
			return true;
		} catch (SerialPortIOException e) {
			Util.log.severe(e.getMessage());
		} finally {
			if (serialPort != null && serialPort.isOpen()) {
				serialPort.closePort();
			}
		}
		return false;
	}

}
