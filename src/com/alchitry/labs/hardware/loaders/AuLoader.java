package com.alchitry.labs.hardware.loaders;

import java.util.ArrayList;
import java.util.List;

import com.alchitry.labs.Util;

public class AuLoader extends ProjectLoader {

	@Override
	protected void eraseFlash() {
		List<String> cmd = new ArrayList<>();
		cmd.add(Util.getAuLoaderCommand());
		cmd.add("-e");
		cmd.add("-l");
		cmd.add("tools/etc/au_loader.bin");
		
		try {
			Util.runCommand(cmd).waitFor();
		} catch (InterruptedException e) {
			Util.showError("Interrupted exception while eraseing flash!");
			Util.print(e);
		}
	}

	@Override
	protected void program(String binFile, boolean flash, boolean verify) {
		List<String> cmd = new ArrayList<>();
		cmd.add(Util.getAuLoaderCommand());
		cmd.add("-l");
		cmd.add("tools/etc/au_loader.bin");
		cmd.add("-b");
		cmd.add(binFile);
		if (flash) 
			cmd.add("-f");
		
		if (verify) {
			Util.println("Verify isn't currently supported on the Au!", true);
		}
		
		try {
			Util.runCommand(cmd).waitFor();
		} catch (InterruptedException e) {
			Util.showError("Interrupted exception while eraseing flash!");
			Util.print(e);
		}
	}

}
