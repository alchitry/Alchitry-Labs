package com.alchitry.labs.lucid.tools;

import java.util.List;

import com.alchitry.labs.style.SyntaxError;

public interface ErrorProvider {
	public List<SyntaxError> getErrors();
	public void reset();
}
