package com.alchitry.labs.hardware.loaders;

import java.util.ArrayList;
import java.util.List;

import com.alchitry.labs.Util;

public class CuLoader extends ProjectLoader {

	@Override
	protected void eraseFlash() {
		List<String> cmd = new ArrayList<>();
		cmd.add(Util.getAlchitryLoaderCommand());
		cmd.add("-e");

		cmd.add("-t");
		cmd.add("cu");
		
		try {
			Util.runCommand(cmd).waitFor();
		} catch (InterruptedException e) {
			Util.showError("Interrupted exception while eraseing flash!");
			Util.print(e);
		}
	}

	@Override
	protected void program(String binFile, boolean flash, boolean verify) {
		if (!flash) {
			Util.println("Cu does not support writing to the FPGA directly!", true);
			return;
		}
		
		List<String> cmd = new ArrayList<>();
		cmd.add(Util.getAlchitryLoaderCommand());

		cmd.add("-t");
		cmd.add("cu");

		cmd.add("-f");
		cmd.add(binFile);
		
		if (verify) {
			Util.println("Verify isn't currently supported on the Cu!", true);
		}
		
		try {
			Util.runCommand(cmd).waitFor();
		} catch (InterruptedException e) {
			Util.showError("Interrupted exception while eraseing flash!");
			Util.print(e);
		}
	}

}
