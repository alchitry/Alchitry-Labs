package com.alchitry.labs.boards;

import java.util.ArrayList;

import com.alchitry.labs.hardware.ProjectLoader;
import com.alchitry.labs.project.ProjectBuilder;
import com.alchitry.labs.widgets.IoRegion;

public abstract class Board {
	public static final int ANY = 0xFFFF;
	public static final int AU = 1 << 0;
	public static final int CU = 1 << 1;
	public static final int MOJO = 1 << 2;

	public final static ArrayList<Board> boards = new ArrayList<>();

	static {
		boards.add(new AlchitryCu());
		boards.add(new AlchitryAu());
		boards.add(new MojoV3());
		boards.add(new MojoV2());
	}

	public abstract String getFPGAName();

	public abstract String getAVRName();

	public abstract String getName();

	public abstract String getBaseProjectName();

	public abstract String getHexFile();

	public abstract ProjectBuilder getBuilder();

	public abstract ProjectBuilder getOpenBuilder();

	public abstract ProjectLoader getLoader();

	public abstract IoRegion[] getIoRegions();

	public abstract String getSVGPath();

	public abstract String[] getSupportedConstraintExtensions();

	public static Board getFromName(String board) {
		for (Board b : boards) {
			if (b.getName().equals(board))
				return b;
		}
		return null;
	}

	public static Board getFromProjectName(String board) {
		for (Board b : boards) {
			if (b.getBaseProjectName().equals(board))
				return b;
		}
		return null;
	}

	public static boolean isType(Board board, int type) {
		if ((type & AU) != 0 && AlchitryAu.class.isInstance(board))
			return true;
		if ((type & CU) != 0 && AlchitryCu.class.isInstance(board))
			return true;
		if ((type & MOJO) != 0 && (MojoV3.class.isInstance(board) || MojoV2.class.isInstance(board)))
			return true;
		return false;
	}
}
