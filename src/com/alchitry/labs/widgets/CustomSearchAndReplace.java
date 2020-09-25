package com.alchitry.labs.widgets;

import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.Theme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class CustomSearchAndReplace extends Composite {
	private static final int HEIGHT = 70;
	private int WIDTH = 300;

	private Composite parent;

	private StyledText searchText;
	private StyledText replaceText;
	private CustomButton nextBtn;
	private CustomButton prevBtn;
	private CustomButton replaceOneBtn;
	private CustomButton replaceAllBtn;
	private CustomToggle regexToggle;
	private CustomToggle caseSensitiveToggle;
	private Label searchLabel;
	private Label replaceLabel;
	Font font;

	public CustomSearchAndReplace(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		setBackground(Theme.windowBackgroundColor);
		setForeground(Theme.editorForegroundColor);

		nextBtn = new CustomButton(this, SWT.NONE);
		nextBtn.setIcon(Images.arrowDown);
		nextBtn.setIconHover(Images.arrowDownHover);
		nextBtn.setToolTipText("Next");

		prevBtn = new CustomButton(this, SWT.NONE);
		prevBtn.setIcon(Images.arrowUp);
		prevBtn.setIconHover(Images.arrowUpHover);
		prevBtn.setToolTipText("Previous");

		replaceOneBtn = new CustomButton(this, SWT.NONE);
		replaceOneBtn.setIcon(Images.replaceOneIcon);
		replaceOneBtn.setIconHover(Images.replaceOneHover);
		replaceOneBtn.setToolTipText("Replace once");

		replaceAllBtn = new CustomButton(this, SWT.NONE);
		replaceAllBtn.setIcon(Images.replaceAllIcon);
		replaceAllBtn.setIconHover(Images.replaceAllHover);
		replaceAllBtn.setToolTipText("Replace all");

		regexToggle = new CustomToggle(this, SWT.NONE);
		regexToggle.setIcon(Images.regexIcon);
		regexToggle.setIconHover(Images.regexHover);
		regexToggle.setToolTipText("Regular expression");

		caseSensitiveToggle = new CustomToggle(this, SWT.NONE);
		caseSensitiveToggle.setIcon(Images.caseSensitiveIcon);
		caseSensitiveToggle.setIconHover(Images.caseSensitiveHover);
		caseSensitiveToggle.setToolTipText("Case sensitive");

		regexToggle.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (regexToggle.isChecked())
					caseSensitiveToggle.setChecked(true);
			}
		});

		caseSensitiveToggle.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!caseSensitiveToggle.isChecked())
					regexToggle.setChecked(false);
			}
		});

		font = new Font(getDisplay(), "Ubuntu", 12, SWT.NORMAL);

		KeyAdapter selectAll = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
					((StyledText)e.widget).selectAll();
					e.doit = false;
				}
			}
		};

		searchText = new StyledText(this, SWT.SINGLE);
		searchText.setBackground(Theme.searchBackgroundColor);
		searchText.setForeground(Theme.searchForegroundColor);
		searchText.setSelectionBackground(Theme.editorTextSelectedColor);
		searchText.setFont(font);
		searchText.addKeyListener(selectAll);

		replaceText = new StyledText(this, SWT.SINGLE);
		replaceText.setBackground(Theme.searchBackgroundColor);
		replaceText.setForeground(Theme.searchForegroundColor);
		replaceText.setSelectionBackground(Theme.editorTextSelectedColor);
		replaceText.setFont(font);
		replaceText.addKeyListener(selectAll);

		searchLabel = new Label(this, SWT.NONE);
		searchLabel.setFont(font);
		searchLabel.setText("Search:");

		replaceLabel = new Label(this, SWT.NONE);
		replaceLabel.setFont(font);
		replaceLabel.setText("Replace:");

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				searchText.dispose();
				replaceText.dispose();
				font.dispose();
				nextBtn.dispose();
				prevBtn.dispose();
			}
		});

		parent.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				calcLayout();
			}
		});

		calcLayout();
	}

	private void calcLayout() {
		if (isDisposed())
			return;
		WIDTH = parent.getSize().x;
		setSize(WIDTH, HEIGHT);
		Point p = nextBtn.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int padding = (HEIGHT - p.y * 2) / 3;

		nextBtn.setBounds(WIDTH - p.x - padding, padding, p.x, p.y);
		prevBtn.setBounds(WIDTH - padding * 2 - p.x * 2, padding, p.x, p.y);
		replaceOneBtn.setBounds(WIDTH - padding * 2 - p.x * 2, padding * 2 + p.y, p.x, p.y);
		replaceAllBtn.setBounds(WIDTH - p.x - padding, padding * 2 + p.y, p.x, p.y);

		caseSensitiveToggle.setBounds(padding, padding, p.x, p.y);
		regexToggle.setBounds(padding, padding * 2 + p.y, p.x, p.y);

		Point replaceSize = replaceLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point searchSize = searchLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		int leftMargin = Math.max(replaceSize.x, searchSize.x) + 3 * padding + p.x;

		int textHeight = p.y;
		int margin = (textHeight - replaceSize.y) / 2;

		searchLabel.setBounds(leftMargin - padding - searchSize.x, padding + margin, searchSize.x, p.y);
		replaceLabel.setBounds(leftMargin - padding - replaceSize.x, padding * 2 + p.y + margin, replaceSize.x, p.y);

		int buttonMargin = padding * 3 + p.x * 2;

		int textBoxWidth = WIDTH - buttonMargin - leftMargin;

		searchText.setTopMargin(margin);
		searchText.setBottomMargin(margin);
		searchText.setLeftMargin(margin);
		searchText.setRightMargin(margin);
		searchText.setBounds(leftMargin, padding, textBoxWidth, textHeight);

		replaceText.setTopMargin(margin);
		replaceText.setBottomMargin(margin);
		replaceText.setLeftMargin(margin);
		replaceText.setRightMargin(margin);
		replaceText.setBounds(leftMargin, padding * 2 + textHeight, textBoxWidth, textHeight);

	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		WIDTH = parent.getSize().x;
		return new Point(WIDTH, HEIGHT);
	}

	public void addModifyListener(ModifyListener listener) {
		searchText.addModifyListener(listener);
	}

	public void setSearchText(String text) {
		this.searchText.setText(text);
	}

	public String getSearchText() {
		return searchText.getText();
	}

	public String getReplaceText() {
		return replaceText.getText();
	}

	public boolean isRegex() {
		return regexToggle.isChecked();
	}

	public boolean isCaseSensitive() {
		return caseSensitiveToggle.isChecked();
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		searchText.addKeyListener(listener);
		replaceText.addKeyListener(listener);
		nextBtn.addKeyListener(listener);
		prevBtn.addKeyListener(listener);
	}

	public void addNextListener(Listener listener) {
		nextBtn.addListener(SWT.Selection, listener);
	}

	public void addPrevListener(Listener listener) {
		prevBtn.addListener(SWT.Selection, listener);
	}

	public void addReplaceOnceListener(Listener listener) {
		replaceOneBtn.addListener(SWT.Selection, listener);
	}

	public void addReplaceAllListener(Listener listener) {
		replaceAllBtn.addListener(SWT.Selection, listener);
	}

	public void addCaseSensitiveListener(Listener listener) {
		caseSensitiveToggle.addListener(SWT.Selection, listener);
	}

	public void addRegexListener(Listener listener) {
		regexToggle.addListener(SWT.Selection, listener);
	}

	public void setSearchError(boolean error) {
		if (error) {
			searchText.setForeground(Theme.errorTextColor);
		} else {
			searchText.setForeground(Theme.searchForegroundColor);
		}
	}

	public void setReplaceError(boolean error) {
		if (error) {
			replaceText.setForeground(Theme.errorTextColor);
		} else {
			replaceText.setForeground(Theme.searchForegroundColor);
		}
	}

	@Override
	public boolean setFocus() {
		if (searchText.isDisposed())
			return false;
		return searchText.setFocus();
	}
}
