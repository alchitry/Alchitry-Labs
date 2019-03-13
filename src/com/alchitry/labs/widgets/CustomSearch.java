package com.alchitry.labs.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.Theme;

public class CustomSearch extends Composite {
	private static final int HEIGHT = 35;
	private static final int WIDTH = 300;

	private StyledText text;
	CustomButton nextBtn;
	CustomButton prevBtn;
	Font font;

	public CustomSearch(Composite parent, int style) {
		super(parent, style);
		setSize(WIDTH, HEIGHT);
		setBackground(Theme.windowBackgroundColor);
		setForeground(Theme.editorForegroundColor);

		nextBtn = new CustomButton(this, SWT.NONE);
		nextBtn.setIcon(Images.arrowDown);
		nextBtn.setIconHover(Images.arrowDownHover);
		nextBtn.setToolTipText("Next");

		Point p = nextBtn.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		int padding = (HEIGHT - p.y) / 2;

		nextBtn.setBounds(WIDTH - p.x - padding, padding, p.x, p.y);

		prevBtn = new CustomButton(this, SWT.NONE);
		prevBtn.setIcon(Images.arrowUp);
		prevBtn.setIconHover(Images.arrowUpHover);
		prevBtn.setToolTipText("Previous");

		prevBtn.setBounds(WIDTH - padding * 2 - p.x * 2, padding, p.x, p.y);
		
		int textHeight = p.y;

		text = new StyledText(this, SWT.SINGLE);
		text.setBackground(Theme.searchBackgroundColor);
		text.setForeground(Theme.searchForegroundColor);
		text.setBounds(padding, padding, WIDTH - padding * 4 - p.x * 2, textHeight);
		font = new Font(getDisplay(), "Ubuntu", 12, SWT.NORMAL);
		text.setFont(font);
		GC gc = new GC(this);
		gc.setFont(font);
		Point size = gc.stringExtent("T");
		gc.dispose();
		int margin = (textHeight - size.y) / 2;
		text.setTopMargin(margin);
		text.setBottomMargin(margin);
		text.setLeftMargin(margin);
		text.setRightMargin(margin);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				text.dispose();
				font.dispose();
				nextBtn.dispose();
				prevBtn.dispose();
			}
		});
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		return new Point(WIDTH, HEIGHT);
	}

	public void addModifyListener(ModifyListener listener) {
		text.addModifyListener(listener);
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	public String getText() {
		return text.getText();
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		text.addKeyListener(listener);
		nextBtn.addKeyListener(listener);
		prevBtn.addKeyListener(listener);
	}

	public void addNextListener(Listener listener) {
		nextBtn.addListener(SWT.Selection, listener);
	}

	public void addPrevListener(Listener listener) {
		prevBtn.addListener(SWT.Selection, listener);
	}

	@Override
	public boolean setFocus() {
		return text.setFocus();
	}
}
