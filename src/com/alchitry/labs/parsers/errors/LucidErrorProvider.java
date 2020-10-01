package com.alchitry.labs.parsers.errors;

import com.alchitry.labs.Util;
import com.alchitry.labs.dictionaries.LucidDictionary;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.tools.lucid.LucidExtractor;
import com.alchitry.labs.style.SyntaxError;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LucidErrorProvider extends ErrorProvider {
	LucidDictionary dict;

	public LucidErrorProvider() {
		super();
	}

	public LucidErrorProvider(StyledCodeEditor editor, LucidDictionary dict) {
		super(editor);
		this.dict = dict;
	}

	@Override
	public ArrayList<SyntaxError> getErrors(File file) {
		super.getErrors(file);
		try {
			MainWindow.getOpenProject().updateGlobals();
		} catch (Exception e) {
		}

		LucidExtractor lucid = new LucidExtractor(dict, null, this);

		try {
			if (MainWindow.getOpenProject() != null)
				lucid.setModuleList(MainWindow.getOpenProject().getModules(null));
			else
				lucid.setModuleList(new ArrayList<Module>());
		} catch (IOException e) {
			lucid.setModuleList(new ArrayList<Module>());
			Util.logger.severe("Could not parse project's modules!");
			e.printStackTrace();
		}

		lucid.parseAll(file);

		if (editor != null) {
			List<SyntaxError> gErrors = null;
			if (MainWindow.getOpenProject() != null && editor.getFile() != null)
				gErrors = MainWindow.getOpenProject().getGlobalErrors(editor.getFile());
			if (gErrors != null)
				errors.addAll(gErrors);
		}
		SyntaxError.spliceErrors(errors);
		return errors;
	}
}
