package com.alchitry.labs.hardware;

import java.util.ArrayList;
import java.util.List;

import com.alchitry.labs.Util;

public class CopperLoader extends ProjectLoader {
	
	public CopperLoader() {
		
	}

	@Override
	protected void eraseFlash() {
		List<String> cmd = new ArrayList<>();
		cmd.add(Util.getIceProgCommand());
		cmd.add("-b");
		
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
		cmd.add(Util.getIceProgCommand());
		if (!flash)
			cmd.add("-S");
		cmd.add(binFile);
		
		try {
			Util.runCommand(cmd).waitFor();
		} catch (InterruptedException e) {
			Util.showError("Interrupted exception while eraseing flash!");
			Util.print(e);
		}
	}

}
