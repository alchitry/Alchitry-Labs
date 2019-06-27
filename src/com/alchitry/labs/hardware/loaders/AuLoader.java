package com.alchitry.labs.hardware.loaders;

import java.util.ArrayList;
import java.util.List;

import com.alchitry.labs.Util;

public class AuLoader extends ProjectLoader {

	@Override
	protected void eraseFlash() {
		List<String> cmd = new ArrayList<>();
		cmd.add(Util.getAlchitryLoaderCommand());
		cmd.add("-e");
		cmd.add("-p");
		cmd.add(Util.assemblePath("tools", "etc", "au_loader.bin"));
		cmd.add("-t");
		cmd.add("au");
		
		try {
			Util.runCommand(cmd).waitFor();
		} catch (InterruptedException e) {
			Util.logException(e,"Interrupted exception while eraseing flash!");
		}
	}

	@Override
	protected void program(String binFile, boolean flash, boolean verify) {
		List<String> cmd = new ArrayList<>();
		cmd.add(Util.getAlchitryLoaderCommand());
		cmd.add("-p");
		cmd.add(Util.assemblePath("tools", "etc", "au_loader.bin"));
		cmd.add("-t");
		cmd.add("au");

		if (flash) 
			cmd.add("-f");
		else
			cmd.add("-r");
		cmd.add(binFile);
		
		if (verify) {
			Util.println("Verify isn't currently supported on the Au!", true);
		}
		
		try {
			Util.runCommand(cmd).waitFor();
		} catch (InterruptedException e) {
			Util.logException(e,"Interrupted exception while eraseing flash!");
		}
	}

}
