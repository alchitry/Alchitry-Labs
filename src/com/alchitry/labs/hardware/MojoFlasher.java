package com.alchitry.labs.hardware;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.boards.Board;
import com.alchitry.labs.gui.main.MainWindow;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class MojoFlasher {
	private Thread thread;

	public MojoFlasher() {
	}

	public boolean isLoading() {
		return thread != null && thread.isAlive();
	}

	public void flashMojo(final String portName, final Board board) {
		thread = new Thread() {
			public void run() {
				MainWindow.mainWindow.enableMonitor(false);
				Util.clearConsole();

				Util.println("Resetting into bootloader mode...");

				String port = portName;
				List<String> ports = Arrays.asList(SerialPortList.getPortNames());
				try {
					touchForCDCReset(port); // connect and close at 1200 baud
				} catch (Exception e) {
					Util.showError("Could not reset Mojo", e.getMessage());
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				List<String> newPorts = Arrays.asList(SerialPortList.getPortNames());
				if (!newPorts.contains(port)) { // if the same port wasn't used
					port = null;
					int ct = 0;
					while (port == null && ct++ < 5) { // limit wait time
						try {
							Thread.sleep(1000); // delay a sec
						} catch (InterruptedException e) {
						}

						for (String s : SerialPortList.getPortNames()) { // check current ports
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

				Util.println("Programming Mojo on " + port);

				BufferedWriter out = null;
				try {
					String avrDude = Locations.BIN + File.separator + "avrdude";
					String configPath = Locations.ETC + File.separator + "avrdude.conf";
					String hexFile = Locations.FIRMWARE + File.separator + board.getHexFile();

					if (Util.isWindows)
						avrDude += ".exe";

					if (!new File(avrDude).exists()) {
						Util.log.severe("Couldn't find avrdude");
						Util.println("The loading program avrdude couldn't be found!", true);
						return;
					}

					if (!new File(hexFile).exists()) {
						Util.log.severe("Couldn't find hex file");
						Util.println("Could not find the firmware for " + board.getName() + ". Looked at " + hexFile, true);
						return;
					}

					ProcessBuilder pb = new ProcessBuilder(avrDude, "-p", board.getAVRName(), "-P", port, "-c", "avr109", "-C", configPath, "-U", "flash:w:" + hexFile+":i");
					
					final Process p;
					try {
						p = pb.start();
					} catch (Exception e) {
						Util.log.severe("Couldn't start avrdude. Tried " + avrDude);
						Util.println("Could not start avrdude!", true);
						return;
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
					p.waitFor();

					Util.println("Finished flashing.");

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
			}
		};

		thread.start();
	}

	private static boolean touchForCDCReset(String iname) throws SerialException {
		SerialPort serialPort = new SerialPort(iname);
		try {
			serialPort.openPort();
			serialPort.setParams(1200, 8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setDTR(false);
			serialPort.closePort();
			return true;
		} catch (SerialPortException e) {
			Util.log.severe(e.getMessage());
		} finally {
			if (serialPort.isOpened()) {
				try {
					serialPort.closePort();
				} catch (SerialPortException e) {
					// noop
				}
			}
		}
		return false;
	}

}
