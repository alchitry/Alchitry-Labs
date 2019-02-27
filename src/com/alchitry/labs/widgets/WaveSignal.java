package com.alchitry.labs.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.alchitry.labs.parsers.ConstValue;

public class WaveSignal {
	private String name;
	private List<ConstValue> values;
	public int points[];

	public enum TriggerType {
		RISING, FALLING, HIGH, LOW
	};

	private List<EnumSet<TriggerType>> triggers;
	private Rectangle buttonBounds[];
	private boolean expanded;
	private int width;
	private Rectangle arrowBounds;
	private boolean signed;

	public WaveSignal(String name, int bits, boolean signed) {
		this.name = name;
		this.signed = signed;
		values = new ArrayList<>();
		triggers = new ArrayList<EnumSet<TriggerType>>(bits);

		buttonBounds = new Rectangle[bits];
		for (int i = 0; i < bits; i++) {
			triggers.add(EnumSet.noneOf(TriggerType.class));
			buttonBounds[i] = new Rectangle(0, 0, 0, 0);
		}
		arrowBounds = new Rectangle(0, 0, 0, 0);
		expanded = bits == 1;
		width = bits;
	}

	public int getWidth() {
		return width;
	}
	
	public boolean isSigned() {
		return signed;
	}

	public void expand(boolean expand) {
		expanded = expand;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void addValue(ConstValue v) {
		values.add(v);
	}

	public void addAllValues(Collection<ConstValue> values) {
		this.values.addAll(values);
	}

	public String getName() {
		return name;
	}

	public List<ConstValue> getValues() {
		return values;
	}

	public ConstValue getValue(int i) {
		return values.get(i);
	}

	public int getNumValues() {
		return values.size();
	}

	public void setTrigger(int bit, TriggerType trigger, boolean enable) {
		if (enable)
			triggers.get(bit).add(trigger);
		else
			triggers.get(bit).remove(trigger);
	}

	public boolean getTrigger(int bit, TriggerType trigger) {
		return triggers.get(bit).contains(trigger);
	}

	public EnumSet<TriggerType> getTriggers(int bit) {
		return triggers.get(bit);
	}

	public Rectangle getButtonBounds(int bit) {
		return buttonBounds[bit];
	}

	public void setButtonBounds(int bit, Rectangle bounds) {
		buttonBounds[bit].height = bounds.height;
		buttonBounds[bit].width = bounds.width;
		buttonBounds[bit].x = bounds.x;
		buttonBounds[bit].y = bounds.y;
	}

	public Rectangle getArrowBounds() {
		return arrowBounds;
	}

	public void setArrowBounds(int x, int y, int w, int h) {
		arrowBounds.height = h;
		arrowBounds.width = w;
		arrowBounds.x = x;
		arrowBounds.y = y;
	}

	public void setArrowBounds(Rectangle bounds) {
		arrowBounds.height = bounds.height;
		arrowBounds.width = bounds.width;
		arrowBounds.x = bounds.x;
		arrowBounds.y = bounds.y;
	}

	public void clearValues() {
		values.clear();
	}
}