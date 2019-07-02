package com.alchitry.labs.hardware.boards;

import java.util.logging.Level;

import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.loaders.MojoLoader;
import com.alchitry.labs.hardware.loaders.ProjectLoader;
import com.alchitry.labs.hardware.pinout.PinConverter;
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor;
import com.alchitry.labs.project.builders.ISEBuilder;
import com.alchitry.labs.project.builders.ProjectBuilder;
import com.alchitry.labs.widgets.IoRegion;

public class Mojo extends Board {

	@Override
	public String getFPGAName() {
		return "xc6slx9-2tqg144";
	}

	@Override
	public String getName() {
		return "Mojo";
	}

	@Override
	public String getExampleProjectDir() {
		return "mojo";
	}

	@Override
	public ProjectBuilder getBuilder() {
		return new ISEBuilder();
	}
	
	@Override
	public ProjectLoader getLoader() {
		return new MojoLoader();
	}

	@Override
	public IoRegion[] getIoRegions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSVGPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String[] getSupportedConstraintExtensions() {
		return new String[] {".ucf"};
	}

	@Override
	public PinConverter getPinConverter() {
		Util.log.log(Level.SEVERE, "Error: Mojo doesn't have a pin converter!");
		return null;
	}
	
	private static UsbDescriptor usbDescription = new UsbDescriptor("Mojo", (short) 0x29DD, (short) 0x8001, null);

	@Override
	public UsbDescriptor getUsbDesciptor() {
		return usbDescription;
	}
	
}
