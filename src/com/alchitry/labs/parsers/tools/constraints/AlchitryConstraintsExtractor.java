package com.alchitry.labs.parsers.tools.constraints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.ConstValue;
import com.alchitry.labs.parsers.Module;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsBaseListener;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsParser.Alchitry_constraintsContext;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsParser.Array_indexContext;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsParser.ClockContext;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsParser.NameContext;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsParser.PinContext;
import com.alchitry.labs.parsers.constraints.AlchitryConstraintsParser.Port_nameContext;
import com.alchitry.labs.parsers.errors.DummyErrorListener;
import com.alchitry.labs.parsers.errors.ErrorListener;
import com.alchitry.labs.parsers.errors.ErrorStrings;
import com.alchitry.labs.parsers.lucid.SignalWidth;
import com.alchitry.labs.parsers.tools.lucid.ConstExprParser;
import com.alchitry.labs.parsers.types.ClockConstraint;
import com.alchitry.labs.parsers.types.PinConstraint;
import com.alchitry.labs.project.Project;
import com.alchitry.labs.tools.ParserCache;

public class AlchitryConstraintsExtractor extends AlchitryConstraintsBaseListener {
	private List<PinConstraint> pinConstraints;
	private List<ClockConstraint> clockConstraints;
	private ErrorListener errorListener;
	private List<Sig> portSignals;

	public AlchitryConstraintsExtractor() {
		this(null);
	}

	public AlchitryConstraintsExtractor(ErrorListener errorListener) {
		this.errorListener = errorListener;
		if (this.errorListener == null)
			this.errorListener = new DummyErrorListener();
	}

	public void parseAll(String file) {
		ParserCache.walk(file, this);
	}

	public List<PinConstraint> getPinConstraints() {
		return pinConstraints;
	}

	public List<ClockConstraint> getClockConstraints() {
		return clockConstraints;
	}

	@Override
	public void enterAlchitry_constraints(Alchitry_constraintsContext ctx) {
		pinConstraints = new ArrayList<>();
		clockConstraints = new ArrayList<>();
		portSignals = new ArrayList<>();
		Project p = MainWindow.getOpenProject();
		if (p != null) {
			try {
				Module top = p.getTopModule();
				portSignals.addAll(top.getOutputs());
				portSignals.addAll(top.getInputs());
				portSignals.addAll(top.getInouts());
			} catch (IOException e) {
				Util.println("Failed to get top module!", true);
			}
		}
	}

	private SignalWidth convertToFixed(SignalWidth sw) {
		if (sw.isFixed())
			return sw;

		SignalWidth fw = new SignalWidth(sw);
		for (SignalWidth ptr = fw; ptr != null; ptr = ptr.getNext()) {
			if (ptr.isText()) {
				ConstValue cv = ConstExprParser.parseExpr(ptr.getText(), null, ConstExprParser.globalConstProvider, null);
				if (cv == null)
					Util.log.severe("Could not parse width " + ptr.getText());
				else
					ptr.set(cv.getBigInt().intValue());
			}
		}

		fw.simplify(); // merge arrays

		return fw;
	}

	private int parseComplexWidth(Port_nameContext ctx, SignalWidth width) {
		int childOffset = ctx.children.indexOf(ctx.name(0)) + 1;

		int bitOffset = 0;
		SignalWidth current = new SignalWidth(width);

		current = convertToFixed(current);

		for (int i = childOffset; i < ctx.children.size(); i++) {
			ParseTree pt = ctx.children.get(i);
			if (pt instanceof Array_indexContext) {
				Array_indexContext aic = (Array_indexContext) pt;
				
				if (current == null) {
					errorListener.reportError(aic, ErrorStrings.ARRAY_INDEX_DIM_MISMATCH);
					return 0;
				}

				if (!current.isArray()) {
					errorListener.reportError(aic, ErrorStrings.BIT_SELECTOR_IN_NAME);
					return 0;
				}

				int bWidth = current.getWidths().remove(0);
				
				if (aic.INT() == null) {
					return 0;
				}

				int index = 0;
				try {
					index = Integer.parseInt(aic.INT().getText());
				} catch (NumberFormatException e) {
					errorListener.reportError(aic, String.format(ErrorStrings.NUMBER_PARSE_FAIL, aic.INT().getText()));
				}

				if (index >= bWidth || index < 0)
					errorListener.reportError(aic, ErrorStrings.ARRAY_INDEX_OUT_OF_BOUNDS);

				bitOffset += current.getTotalWidth() * index;

				if (current.getWidths().size() == 0)
					current = current.getNext();
			} else if (pt instanceof NameContext) {
				String name = pt.getText();
				if (!current.isStruct() && current.getWidths().size() == 1 && current.getWidths().get(0) == 1 && current.getNext() != null)
					current = current.getNext();

				int offset = current.getStruct().getOffsetOfMember(name);
				if (offset < 0) {
					errorListener.reportError((NameContext) pt, String.format(ErrorStrings.UNKNOWN_STRUCT_NAME, name, current.getStruct().getName()));
				} else {
					bitOffset += offset;
				}

				current = new SignalWidth(current.getStruct().getWidthOfMember(name));
			} else if (pt instanceof TerminalNode) {
				continue;
			} else {
				Util.log.severe("Uknown " + ctx.getText());
			}
		}
		
		if (current != null && current.getTotalWidth() != 1)
			errorListener.reportError(ctx, ErrorStrings.CONSTRAINT_MULTI_BIT);

		return bitOffset;
	}

	@Override
	public void exitPin(PinContext ctx) {
		String pin = ctx.pin_name().getText();
		String port = ctx.port_name().name(0).getText();
		boolean isMultiBit = ctx.port_name().array_index().size() > 0 || ctx.port_name().name().size() > 1;
		boolean pullUp = ctx.PULLUP() != null;
		boolean pullDown = ctx.PULLDOWN() != null;

		int bit = 0;

		int idx = portSignals.indexOf(new Sig(port));

		Sig portSig = null;
		if (idx >= 0)
			portSig = portSignals.get(idx);

		if (portSig == null) {
			errorListener.reportError(ctx.port_name(), String.format(ErrorStrings.CONSTRAINT_PORT_UNKNOWN, port));
		} else {
			bit = parseComplexWidth(ctx.port_name(), portSig.getWidth());
		}

		if (pullUp && pullDown)
			errorListener.reportError(ctx.PULLDOWN(), ErrorStrings.CONSTRAINT_UP_AND_DOWN);

		PinConstraint pc = new PinConstraint(pin, port, bit, isMultiBit, pullUp, pullDown);
		pinConstraints.add(pc);
	}

	@Override
	public void exitClock(ClockContext ctx) {
		String port = ctx.port_name().name(0).getText();
		boolean isMultiBit = ctx.port_name().array_index().size() > 0 || ctx.port_name().name().size() > 1;
		float fClk = 0;

		try {
			fClk = Float.parseFloat(ctx.frequency().number().getText());
		} catch (NumberFormatException e) {
			errorListener.reportError(ctx.frequency().number(), String.format(ErrorStrings.NUMBER_PARSE_FAIL, ctx.frequency().number().getText()));
		}

		String freqUnit = ctx.frequency().FREQ_UNIT().getText().toLowerCase();
		switch (freqUnit) {
		case "ghz":
			fClk *= 1000000000.0f;
			break;
		case "mhz":
			fClk *= 1000000.0f;
			break;
		case "khz":
			fClk *= 1000.0f;
			break;
		case "hz":
			break;
		default:
			errorListener.reportError(ctx.frequency().FREQ_UNIT(), String.format(ErrorStrings.CONSTRAINT_UNKNOWN_UNIT, ctx.frequency().FREQ_UNIT().getText()));
		}

		int bit = 0;

		Sig portSig = portSignals.get(portSignals.indexOf(new Sig(port)));
		if (portSig == null) {
			errorListener.reportError(ctx.port_name(), String.format(ErrorStrings.CONSTRAINT_PORT_UNKNOWN, port));
		} else {
			bit = parseComplexWidth(ctx.port_name(), portSig.getWidth());
		}

		ClockConstraint cc = new ClockConstraint(port, bit, isMultiBit, (int) fClk);
		clockConstraints.add(cc);
	}

}
