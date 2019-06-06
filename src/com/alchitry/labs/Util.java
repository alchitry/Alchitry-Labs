package com.alchitry.labs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.parsers.BigFunctions;
import com.alchitry.labs.widgets.CustomConsole;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortIOException;

public class Util {
	private static Display display;
	private static Shell shell;
	private static CustomConsole console;
	public static Path tmpDir;
	public static final boolean isWindows;
	public static final boolean isLinux;
	public static String osDir;
	public static Logger log;
	public static boolean isGUI;
	public static final String logFile = "alchitry-labs.log";

	public static final int UNKNOWN = -1;
	public static final int WIN32 = 0;
	public static final int WIN64 = 1;
	public static final int LIN32 = 2;
	public static final int LIN64 = 3;
	public static final int ECLIPSE = 4;
	private static int envType = UNKNOWN;

	public static final String[] sourceSuffixes = new String[] { ".v", ".luc" };
	public static final String[] errorProviderSuffixes = new String[] { ".v", ".luc", ".acf" };

	private static BufferedWriter consoleLogger;

	static {
		String os = System.getProperty("os.name");
		isWindows = os.startsWith("Windows");
		isLinux = os.startsWith("Linux");

		isGUI = false;

		try {
			tmpDir = Files.createTempDirectory("Mojo_IDE_");
			tmpDir.toFile().deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}

		log = Logger.getLogger("mojo");

		try {
			// This block configure the logger with handler and formatter
			FileHandler fh = new FileHandler(logFile);
			log.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getEnvType() {
		return envType;
	}

	public static void setEnvType(int env) {
		envType = env;
		switch (envType) {
		case UNKNOWN:
			osDir = "";
			break;
		case WIN32:
		case WIN64:
			osDir = "windows";
			break;
		case LIN32:
		case LIN64:
		case ECLIPSE:
			osDir = "linux";
			break;
		}
	}

	public static float getScalingFactor() {
		return display.getDPI().x;
	}

	public static void setDisplay(Display display) {
		Util.display = display;
	}

	public static void setShell(Shell shell) {
		Util.shell = shell;
	}

	public static void setConsole(CustomConsole console) {
		Util.console = console;
	}

	public static void setConsoleLogger(BufferedWriter stream) {
		consoleLogger = stream;
	}

	public static CustomConsole getConsole() {
		return console;
	}

	private static class QuestionRunnable implements Runnable {
		public boolean result;
		private String title;
		private String message;

		public QuestionRunnable(String title, String message) {
			this.title = title;
			this.message = message;
		}

		@Override
		public void run() {
			MessageBox confirm = new MessageBox(shell, SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
			confirm.setText(title);
			confirm.setMessage(message);
			result = confirm.open() == SWT.YES;
		}
	}

	public static boolean askQuestion(String message) {
		return askQuestion("Question?", message);
	}

	public static boolean askQuestion(String title, String message) {
		if (isGUI) {
			QuestionRunnable question = new QuestionRunnable(title, message);
			display.syncExec(question);
			return question.result;
		} else {
			System.out.println(title + ": " + message + " [y/n]");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				switch (br.readLine()) {
				case "y":
				case "yes":
				case "Yes":
				case "YES":
				case "Y":
					return true;
				default:
					return false;
				}
			} catch (IOException e) {
				System.err.println("Failed to read answer from STDIN!");
				System.exit(1);
			}
		}
		return false;
	}

	public static void showError(final String error) {
		showError("Error!", error);
	}

	public static void showError(final String error, final Shell shell) {
		showError("Error!", error, shell);
	}

	public static void showError(final String title, final String error) {
		showError(title, error, shell);
	}

	public static void showError(final String title, final String error, final Shell shell) {
		if (isGUI) {
			shell.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					MessageBox b = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR | SWT.APPLICATION_MODAL);
					b.setText(title);
					b.setMessage(error);
					b.open();
				}

			});
		} else {
			System.err.println(title + ": " + error);
		}
	}

	public static void showInfo(final String text) {
		showInfo("Info", text, shell);
	}

	public static void showInfo(final String text, final Shell shell) {
		showInfo("Info", text, shell);
	}

	public static void showInfo(final String title, final String text) {
		showInfo(title, text, shell);
	}

	public static void showInfo(final String title, final String text, final Shell shell) {
		if (isGUI) {
			shell.getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					MessageBox b = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION | SWT.APPLICATION_MODAL);
					b.setText(title);
					b.setMessage(text);
					b.open();
				}

			});
		} else {
			System.out.println(title + ": " + text);
		}
	}

	public static void clearConsole() {
		if (isGUI)
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					console.setText("");
					console.clearStyles();
				}
			});
	}

	public static void println(final String text) {
		println(text, false);
	}

	public static void println(final String text, final boolean red) {
		if (red)
			println(text, Theme.errorTextColor);
		else
			println(text, null);
	}

	public static void println(final String text, final Color color) {
		print(text + System.lineSeparator(), color);
	}

	public static void print(final String text, final boolean red) {
		if (red)
			print(text, Theme.errorTextColor);
		else
			print(text, null);
	}

	public static void print(final String text) {
		print(text, null);
	}

	public static void print(final Throwable e) {
		println(ExceptionUtils.getStackTrace(e), true);
	}

	public static void print(final String text, final Color color) {
		if (consoleLogger != null) {
			try {
				consoleLogger.write(text);
			} catch (Exception e) {
				e.printStackTrace();
				consoleLogger = null;
			}
		}
		if (isGUI) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					String message = text;
					if (message == null)
						message = "null";
					if (color != null) {
						int end = console.getCharCount() + message.length();
						StyleRange styleRange = new StyleRange();
						styleRange.start = end - message.length();
						styleRange.length = message.length();
						styleRange.foreground = color;
						console.addStyle(styleRange);
					}
					console.append(message);
				}
			});
		} else {
			System.out.print(text);
		}
	}

	public static void asyncExec(Runnable r) {
		display.asyncExec(r);
	}

	public static void syncExec(Runnable r) {
		display.syncExec(r);
	}

	public static Display getDisplay() {
		return display;
	}

	public static Shell getShell() {
		return shell;
	}

	public static int minWidthNum(BigInteger i) {
		if (i.compareTo(BigInteger.ZERO) != 0)
			return BigFunctions.ln(new BigDecimal(i), 10).divide(BigFunctions.LOG2, RoundingMode.HALF_UP).setScale(0, RoundingMode.FLOOR).intValue() + 1;
		else
			return 1;
	}

	public static int minWidthNum(long i) {
		if (i != 0)
			return (int) Math.floor(Math.log(i) / Math.log(2)) + 1;
		else
			return 1;
	}

	public static int minWidthNum(String str, int base) {
		long i = Long.parseLong(str, base);
		return minWidthNum(i);
	}

	public static int widthOfMult(long w1, long w2) {
		return (int) Math.floor((Math.log((Math.pow(2, w1) - 1) * (Math.pow(2, w2) - 1)) / Math.log(2))) + 1; // max value
	}

	public static String getWorkspace() {
		String workspace = Settings.pref.get(Settings.WORKSPACE, null);
		if (workspace == null) {
			JFileChooser fr = new JFileChooser();
			FileSystemView fw = fr.getFileSystemView();
			workspace = fw.getDefaultDirectory().getAbsolutePath() + File.separatorChar + "alchitry";
		}
		return workspace;
	}

	public static void setWorkspace(String workspace) {
		Settings.pref.put(Settings.WORKSPACE, workspace);
	}

	public static String exceptionToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static boolean isIseUpdated() {
		String path = getISELocation();
		File f = new File(path);
		try {
			float version = Float.parseFloat(f.getName());
			if (version == 14.7f)
				return true;
		} catch (NumberFormatException e) {

		}
		return false;
	}

	public static boolean isISESet() {
		return Settings.pref.get(Settings.XILINX_LOC, null) != null;
	}

	public static String getISELocation() {
		String xilinx = Settings.pref.get(Settings.XILINX_LOC, null);
		if (xilinx != null)
			return xilinx;

		File path = null;
		if (isWindows)
			path = new File("C:\\Xilinx");
		else
			path = new File("/opt/Xilinx");

		if (!path.isDirectory())
			return null;
		File[] subs = path.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		for (File f : subs) {
			try {
				Float.parseFloat(f.getName());
				path = f;
				break;
			} catch (NumberFormatException e) {

			}
		}
		return path.getAbsolutePath();
	}

	public static String getVivadoLocation() {
		String vivado = Settings.pref.get(Settings.VIVADO_LOC, null);
		if (vivado != null)
			return vivado;

		File path = null;
		if (isWindows)
			path = new File("C:\\Xilinx\\Vivado");
		else
			path = new File("/opt/Xilinx/Vivado");

		if (!path.isDirectory())
			return null;
		File[] subs = path.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		for (File f : subs) {
			try {
				Float.parseFloat(f.getName());
				path = f;
				break;
			} catch (NumberFormatException e) {

			}
		}
		return path.getAbsolutePath();
	}

	public static String getIceCubeFolder() {
		String iceCube = Settings.pref.get(Settings.ICECUBE_LOC, null);
		if (iceCube != null)
			return iceCube;

		File path = null;
		if (isWindows)
			path = new File("C:\\lscc\\iCEcube2.2017.08");
		else
			path = new File("~/lscc/iCEcube2");

		if (!path.isDirectory())
			return null;

		return path.getAbsolutePath();
	}

	public static String getIceCubeLicenseFile() {
		return Settings.pref.get(Settings.ICECUBE_LICENSE, null);
	}

	public static String getYosysCommand() {
		String yosys = Settings.pref.get(Settings.YOSYS_LOC, null);
		if (yosys != null)
			return yosys;
		if (isLinux)
			return "yosys";
		return null;
	}

	public static String getArachneCommand() {
		String arachne = Settings.pref.get(Settings.ARACHNE_LOC, null);
		if (arachne != null)
			return arachne;
		if (isLinux)
			return "arachne-pnr";
		return null;
	}

	public static String getIcePackCommand() {
		String icepack = Settings.pref.get(Settings.ICEPACK_LOC, null);
		if (icepack != null)
			return icepack;
		if (isLinux)
			return "icepack";
		return null;
	}

	public static String getAlchitryLoaderCommand() {
		if (isLinux)
			return "tools/linux/bin/loader";
		if (isWindows)
			return "tools\\windows\\bin\\loader.exe";
		return null;
	}

	public static <T extends Named> boolean removeByName(Collection<T> list, String name) {
		T t = getByName(list, name);

		if (t != null) {
			list.remove(t);
			return true;
		}

		return false;
	}

	public static int findByName(List<? extends Named> list, String name) {
		for (int i = 0; i < list.size(); i++)
			if (name.equals(list.get(i).getName()))
				return i;
		return -1;
	}

	public static boolean containsName(Collection<? extends Named> list, String name) {
		return getByName(list, name) != null;
	}

	public static <T extends Named> T getByName(Collection<T> list, String name) {
		if (list == null)
			return null;
		for (T t : list)
			if (Objects.equals(t.getName(), name))
				return t;
		return null;
	}

	public static String createTmpFont(String name) throws IOException {
		InputStream stream = MainWindow.class.getResourceAsStream("/fonts/" + name);
		File f = Files.createTempFile(name.split("\\.")[0], ".ttf").toFile();
		OutputStream out = new FileOutputStream(f);
		IOUtils.copy(stream, out);
		out.close();
		f.deleteOnExit();
		return f.getPath();
	}

	public static String readFile(File path) throws IOException {
		return readFile(path, Charset.defaultCharset());
	}

	public static String readFile(File path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path.getAbsolutePath()));
		return new String(encoded, encoding);
	}

	public static String getFileText(File file) {
		String t = MainWindow.mainWindow.getEditorText(file);
		if (t == null)
			try {
				t = readFile(file);
			} catch (IOException e) {
				Util.log.severe(ExceptionUtils.getStackTrace(e));
			}
		return t;
	}

	public static String[] getSerialPortNames() {
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] names = new String[ports.length];
		for (int i = 0; i < ports.length; i++)
			names[i] = ports[i].getSystemPortName();
		return names;
	}

	public static SerialPort connect(String portName, int baud) throws SerialPortIOException {
		if (portName.equals(""))
			throw new InvalidParameterException("A serial port must be selected!");
		SerialPort serialPort = SerialPort.getCommPort(portName);
		if (serialPort == null) {
			throw new SerialPortIOException("Port " + portName + " could not be found. Please select a different port.");

		}

		serialPort.openPort();
		serialPort.setBaudRate(baud);
		serialPort.setNumDataBits(8);
		serialPort.setNumStopBits(1);
		serialPort.setParity(0);
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 50000, 50000);

		return serialPort;
	}

	public static final VerifyListener integerVerifier = new VerifyListener() {

		@Override
		public void verifyText(VerifyEvent e) {
			Text text = (Text) e.getSource();

			final String old = text.getText();
			String edit = old.substring(0, e.start) + e.text + old.substring(e.end);

			if (edit.length() == 0) {
				return;
			}

			try {
				Integer.parseInt(edit);
			} catch (NumberFormatException ex) {
				e.doit = false;
			}
		}
	};

	public static void startStreamPrinter(final Process process, final InputStream s, final boolean red) {
		Thread printer = new Thread() {
			public void run() {
				StringBuffer stringBuffer = new StringBuffer(512);
				CharBuffer buffer = CharBuffer.allocate(512);
				InputStreamReader stream = new InputStreamReader(s);
				try {
					while (process.isAlive()) {
						while (stream.ready()) {
							int ct = stream.read(buffer);
							buffer.flip();
							if (ct == -1)
								return;
							String newText = buffer.toString();
							stringBuffer.append(newText);
							if (newText.contains("\n")) {
								String[] lines = stringBuffer.toString().split("\\r?\\n", -2);
								for (int i = 0; i < lines.length - 1; i++)
									Util.println(lines[i], red);
								stringBuffer.delete(0, stringBuffer.length() - lines[lines.length - 1].length());
							}
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}

					while (stream.ready()) {
						int ct = stream.read(buffer);
						buffer.flip();
						if (ct == -1)
							return;
						stringBuffer.append(buffer.toString());

					}
					Util.print(new String(stringBuffer.toString()), red);
				} catch (IOException e) {
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
					}
				}
			}
		};
		printer.setDaemon(true);
		printer.start();
	}

	public static Process runCommand(List<String> cmd) throws InterruptedException {
		return runCommand(cmd, true);
	}

	public static Process runCommand(List<String> cmd, boolean showRed) throws InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(cmd);

		Process process;

		try {
			process = pb.start();
		} catch (Exception e) {
			Util.log.severe("Couldn't start " + pb.command().get(0));
			Util.showError("Could not start " + pb.command().get(0) + "! Please check the location for " + pb.command().get(0) + " is correctly set in the settings menu.");
			return null;
		}

		startStreamPrinter(process, process.getInputStream(), false);
		startStreamPrinter(process, process.getErrorStream(), showRed);

		return process;
	}

	private static boolean endsWithSuffixList(String testString, String[] suffixList) {
		if (testString == null)
			return false;
		for (String suffix : suffixList) {
			if (testString.endsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean isConstraintFile(String fileName) {
		return isConstraintFile(fileName, null);
	}

	public static boolean isConstraintFile(String fileName, Board board) {
		String[] set;
		if (board != null)
			set = board.getSupportedConstraintExtensions();
		else
			set = Board.constraintExtensions.toArray(new String[Board.constraintExtensions.size()]);
		return endsWithSuffixList(fileName, set);
	}

	public static boolean isSourceFile(File file) {
		return isSourceFile(file.getName());
	}

	public static boolean isSourceFile(String fileName) {
		return endsWithSuffixList(fileName, sourceSuffixes);
	}

	public static boolean hasErrorProvider(File file) {
		return hasErrorProvider(file.getName());
	}

	public static boolean hasErrorProvider(String fileName) {
		return endsWithSuffixList(fileName, errorProviderSuffixes);
	}

	public static Image svgToImage(String svgFile, int width, int height) {
		ByteArrayOutputStream resultByteStream = new ByteArrayOutputStream();
		TranscoderInput input = new TranscoderInput(MainWindow.class.getResourceAsStream(svgFile));
		TranscoderOutput output = new TranscoderOutput(resultByteStream);

		PNGTranscoder png = new PNGTranscoder();

		if (height > 0)
			png.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) height);
		if (width > 0)
			png.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) width);

		try {
			png.transcode(input, output);
		} catch (TranscoderException e1) {
			System.err.println("Failed to transcode image " + svgFile + "!");
			return null;
		}

		try {
			resultByteStream.flush();
		} catch (IOException e1) {
			System.err.println("Failed to flush stream for image " + svgFile + "!");
			return null;
		}

		ByteArrayInputStream imgstream = new ByteArrayInputStream(resultByteStream.toByteArray());

		return new Image(getDisplay(), imgstream);
	}

	public static String assembleLinuxPath(File parent, String... pieces) {
		String out = parent.getAbsolutePath() + "/" + String.join("/", pieces);
		return out.replace("//", "/"); // remove any duplicate separators
	}

	public static String assembleLinuxPath(String... pieces) {
		String out = String.join("/", pieces);
		return out.replace("//", "/"); // remove any duplicate separators
	}

	public static String assemblePath(File parent, String... pieces) {
		String out = parent.getAbsolutePath() + File.separator + String.join(File.separator, pieces);
		return out.replace(File.separator + File.separator, File.separator); // remove any duplicate separators
	}

	public static String assemblePath(String... pieces) {
		String out = String.join(File.separator, pieces);
		return out.replace(File.separator + File.separator, File.separator); // remove any duplicate separators
	}

	public static File assembleFile(File parent, String... pieces) {
		return new File(assemblePath(parent, pieces));
	}

	public static File assembleFile(String... pieces) {
		return new File(assemblePath(pieces));
	}

	public static File changeExt(File file, String ext) {
		String name = file.getName();
		return Util.assembleFile(file.getParent(), name.substring(0, name.lastIndexOf(".")) + ext);
	}
}
