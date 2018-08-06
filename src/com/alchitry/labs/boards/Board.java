package com.alchitry.labs.boards;

import java.util.ArrayList;

import com.alchitry.labs.hardware.ProjectLoader;
import com.alchitry.labs.project.ProjectBuilder;

public abstract class Board {
	public final static ArrayList<Board> boards = new ArrayList<>();
	
	static {
		boards.add(new MojoV3());
		boards.add(new MojoV2());
		boards.add(new AlchitryAu());
		boards.add(new AlchitryCu());
	}
	
	public abstract String getFPGAName();
	public abstract String getAVRName();
	public abstract String getName();
	public abstract String getBaseProjectName();
	public abstract String getHexFile();
	public abstract ProjectBuilder getBuilder();
	public abstract ProjectBuilder getOpenBuilder();
	public abstract ProjectLoader getLoader();
	
	public static Board getFromName(String board) {
		for (Board b : boards){
			if (b.getName().equals(board))
				return b;
		}
		return null;
	}
	
	public static Board getFromProjectName(String board) {
		for (Board b : boards){
			if (b.getBaseProjectName().equals(board))
				return b;
		}
		return null;
	}
}
