package com.alchitry.labs.dictionaries;

import java.io.IOException;

import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.project.Project;

public class AlchitryConstraintsDictionary extends Dictionary {
	private static final String[] KEYWORDS = { "pin", "clock", "pullup", "pulldown" };
	
	public AlchitryConstraintsDictionary() {
		updatePortNames();
	}

	@Override
	public void clear() {
		super.clear();
		addAll(KEYWORDS);
	}

	public void updatePortNames() {
		Project p = MainWindow.getOpenProject();
		if (p != null) {
			try {
				Module top = p.getTopModule();
				clear();
				for (Sig s : top.getInputs()) 
					dict.add(s.getName());
				for (Sig s : top.getOutputs())
					dict.add(s.getName());
				for (Sig s : top.getInouts())
					dict.add(s.getName());
			} catch (IOException e) {
				return;
			}
		}
	}
}
