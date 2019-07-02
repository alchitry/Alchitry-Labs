package com.alchitry.labs.hardware.boards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.alchitry.labs.hardware.loaders.ProjectLoader;
import com.alchitry.labs.hardware.pinout.PinConverter;
import com.alchitry.labs.hardware.usb.UsbUtil.UsbDescriptor;
import com.alchitry.labs.project.builders.ProjectBuilder;
import com.alchitry.labs.widgets.IoRegion;

public abstract class Board {
	public static final int ANY = 0xFFFFFFFF;
	public static final int AU = 1 << 0;
	public static final int CU = 1 << 1;
	public static final int MOJO = 1 << 2;

	public static final Set<String> constraintExtensions;

	public final static ArrayList<Board> boards = new ArrayList<>();

	static {
		boards.add(new AlchitryCu());
		boards.add(new AlchitryAu());
		boards.add(new Mojo());
		constraintExtensions = new HashSet<>();
		for (Board b : boards)
			for (String ext : b.getSupportedConstraintExtensions())
				constraintExtensions.add(ext);
	}
	
	
	public abstract UsbDescriptor getUsbDesciptor();

	public abstract String getFPGAName();

	public abstract String getName();

	public abstract String getExampleProjectDir();

	public abstract ProjectBuilder getBuilder();

	public abstract ProjectLoader getLoader();

	public abstract IoRegion[] getIoRegions();

	public abstract String getSVGPath();

	public abstract String[] getSupportedConstraintExtensions();

	public abstract PinConverter getPinConverter();

	public static Board getFromName(String board) {
		for (Board b : boards) {
			if (b.getName().equals(board))
				return b;
		}
		return null;
	}

	public static Board getFromProjectName(String board) {
		for (Board b : boards) {
			if (b.getExampleProjectDir().equals(board))
				return b;
		}
		return null;
	}

	public int getType() {
		return getType(this);
	}

	public boolean isType(int type) {
		return isType(this, type);
	}

	public static int getType(Board board) {
		if (AlchitryAu.class.isInstance(board))
			return AU;
		if (AlchitryCu.class.isInstance(board))
			return CU;
		if (Mojo.class.isInstance(board))
			return MOJO;
		return 0;
	}

	public static boolean isType(Board board, int type) {
		if ((type & getType(board)) != 0)
			return true;
		return false;
	}
}
