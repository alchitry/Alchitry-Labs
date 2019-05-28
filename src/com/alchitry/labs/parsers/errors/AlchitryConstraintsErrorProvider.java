package com.alchitry.labs.parsers.errors;

import java.io.File;
import java.util.ArrayList;

import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.parsers.tools.constraints.AlchitryConstraintsExtractor;
import com.alchitry.labs.style.SyntaxError;

public class AlchitryConstraintsErrorProvider extends ErrorProvider {
	public AlchitryConstraintsErrorProvider() {
		super();
	}

	public AlchitryConstraintsErrorProvider(StyledCodeEditor editor) {
		super(editor);
	}

	@Override
	public ArrayList<SyntaxError> getErrors(File file) {
		super.getErrors(file);
		
		AlchitryConstraintsExtractor ace = new AlchitryConstraintsExtractor(this);
		ace.parseAll(file);
		
		SyntaxError.spliceErrors(errors);
		return errors;
	}
}
