package com.alchitry.labs.parsers.styles.lucid;

import java.util.ArrayList;
import java.util.List;

import com.alchitry.labs.dictionaries.LucidDictionary;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.errors.ErrorProvider;
import com.alchitry.labs.parsers.tools.lucid.LucidErrorChecker;
import com.alchitry.labs.style.SyntaxError;

public class LucidErrorProvider extends ErrorProvider{
	private LucidErrorChecker errorChecker;
	
	public LucidErrorProvider() {
		super();
		errorChecker = new LucidErrorChecker(null);
	}

	public LucidErrorProvider(StyledCodeEditor editor, LucidDictionary dict) {
		super(editor);
		errorChecker = new LucidErrorChecker(dict, null);
	}
	
	@Override
	public ArrayList<SyntaxError> getErrors(String file) {
		try {
			MainWindow.getOpenProject().updateGlobals();
		} catch (Exception e) {
		}
		errors.clear();
		errors.addAll(errorChecker.getErrors(editor.getFilePath()));
		List<SyntaxError> gErrors = null;
		if (MainWindow.getOpenProject() != null)
			gErrors = MainWindow.getOpenProject().getGlobalErrors(editor.getFilePath());
		if (gErrors != null)
			errors.addAll(gErrors);
		SyntaxError.spliceErrors(errors);
		return errors;
	}
}
