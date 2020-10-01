package com.alchitry.labs.hardware.boards;

import com.alchitry.labs.Settings;
import com.alchitry.labs.hardware.loaders.CuLoader;
import com.alchitry.labs.hardware.loaders.ProjectLoader;
import com.alchitry.labs.hardware.pinout.AlchitryCuPinConverter;
import com.alchitry.labs.hardware.pinout.PinConverter;
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor;
import com.alchitry.labs.project.builders.IceCubeBuilder;
import com.alchitry.labs.project.builders.IceStormBuilder;
import com.alchitry.labs.project.builders.ProjectBuilder;
import com.alchitry.labs.widgets.IoRegion;

public class AlchitryCu extends Board {
	private static IoRegion[] ioRegions = { new IoRegion("Bank A", 0, 0.289769230769231, 0.0274666666666666, 0.235846153846154, 0.145044444444444),
			new IoRegion("Bank B", 1, 0.682076923076923, 0.0274666666666666, 0.235846153846154, 0.145044444444444),
			new IoRegion("Bank C", 2, 0.351307692307692, 0.827466666666667, 0.235846153846154, 0.145044444444444),
			new IoRegion("Bank D", 3, 0.682076923076923, 0.827466666666667, 0.235846153846154, 0.145044444444444),
			new IoRegion("LEDs", 4, 0.930676923076923, 0.230355555555556, 0.0463538461538462, 0.339288888888889),
			new IoRegion("Reset", 5, 0.908246153846154, 0.634244444444444, 0.0757846153846154, 0.175933333333333),
			new IoRegion("Clock", 6, 0.583353846153846, 0.664444444444445, 0.0557076923076923, 0.115555555555556),
			new IoRegion("USB", 7, 0.00189230769230769, 0.107711111111111, 0.137446153846154, 0.221177777777778),
			new IoRegion("Flash", 8, 0.724230769230769, 0.575555555555555, 0.135384615384615, 0.111111111111111), };
	
	static {
		ioRegions[0].signals = new String[] { "A2", "A3" };
	}

	public AlchitryCu() {
		
	}

	@Override
	public String getFPGAName() {
		return "ICE40HX8K-CB132IC";
	}

	@Override
	public String getName() {
		return "Alchitry Cu";
	}

	@Override
	public String getExampleProjectDir() {
		return "alchitry-cu";
	}

	@Override
	public ProjectBuilder getBuilder() {
		if (Settings.INSTANCE.getUSE_ICESTORM().get())
			return new IceStormBuilder();
		else
			return new IceCubeBuilder();
	}

	@Override
	public ProjectLoader getLoader() {
		return new CuLoader();
	}

	@Override
	public IoRegion[] getIoRegions() {
		return ioRegions;
	}

	@Override
	public String getSVGPath() {
		return "/images/cu.svg";
	}

	@Override
	public String[] getSupportedConstraintExtensions() {
		return new String[] { ".acf", ".pcf", ".sdc" };
	}

	@Override
	public PinConverter getPinConverter() {
		return new AlchitryCuPinConverter();
	}
	
	private static UsbDescriptor usbDescription = new UsbDescriptor("Alchitry Cu", (short) 0x0403, (short) 0x6010, "Alchitry Cu");

	@Override
	public UsbDescriptor getUsbDesciptor() {
		return usbDescription;
	}
}
