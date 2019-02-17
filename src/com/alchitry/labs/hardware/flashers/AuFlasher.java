package com.alchitry.labs.hardware.flashers;

import java.util.ArrayList;
import java.util.List;

import com.alchitry.labs.Util;

public class AuFlasher extends Flasher {
	protected String firmwareFile;
	
	public AuFlasher() {
		firmwareFile = "au_ftdi.data";
	}

	@Override
	public void flash() {
		if (!Util.askQuestion("Warning", "Make sure that only one FTDI device is plugged in before proceeding!"))
			return;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Util.clearConsole();
				
				List<String> cmd = new ArrayList<>();
				cmd.add(Util.getAlchitryLoaderCommand());
				
				cmd.add("-b");
				cmd.add("0");

				cmd.add("-u");
				cmd.add(Util.assemblePath("tools", "etc", firmwareFile));
				
				try {
					Util.runCommand(cmd).waitFor();
					Util.showInfo("Unplug and replug the board now for the firmware update to take effect!");
				} catch (InterruptedException e) {
					Util.showError("Interrupted exception while eraseing flash!");
					Util.print(e);
				}
			}
		});
		thread.start();
	}

}
