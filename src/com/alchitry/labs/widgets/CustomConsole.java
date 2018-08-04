package com.alchitry.labs.widgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.alchitry.labs.Settings;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.DoubleClickHighlighter;
import com.alchitry.labs.gui.TextHighligher;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.style.StyleUtil;

public class CustomConsole extends StyledText {
	private DoubleClickHighlighter doubleClick;
	private List<LineStyleListener> lineStyleListeners;
	private List<StyleRange> styles;
	private StyleRange[] cachedStyles;
	private boolean newStyles = false;
	private CustomSearch search;
	private TextHighligher highlighter;
	private boolean searchActive = false;

	public CustomConsole(Composite parent, int style) {
		super(parent, style);
		final DummyComposite dc = new DummyComposite(parent, SWT.NONE);
		setParent(dc);

		styles = new ArrayList<>();

		setBackground(Theme.consoleBackgroundColor);
		setForeground(Theme.consoleForegoundColor);
		setSelectionBackground(Theme.editorTextSelectedColor);
		setSelectionForeground(null);
		
		// Bug in windows makes scroll bars flash
		if (Util.isLinux)
			setAlwaysShowScrollBars(false);
		
		addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setTopIndex(getLineCount() - 1);
			}
		});

		setFont(new Font(parent.getDisplay(), "Ubuntu Mono", 12, SWT.NORMAL));

		final Menu consoleMenu = new Menu(this);
		final MenuItem toggleWordwrap = new MenuItem(consoleMenu, SWT.PUSH);
		if (Settings.pref.getBoolean(Settings.WORD_WRAP, false)) {
			setWordWrap(true);
			toggleWordwrap.setText("Disable word-wrap");
		} else {
			setWordWrap(false);
			toggleWordwrap.setText("Enable word-wrap");
		}

		toggleWordwrap.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (getWordWrap()) {
					setWordWrap(false);
					toggleWordwrap.setText("Enable word-wrap");
				} else {
					setWordWrap(true);
					toggleWordwrap.setText("Disable word-wrap");
				}
				Settings.pref.putBoolean(Settings.WORD_WRAP, getWordWrap());
			}
		});

		addMenuDetectListener(new MenuDetectListener() {

			@Override
			public void menuDetected(MenuDetectEvent e) {
				consoleMenu.setLocation(e.x, e.y);
				consoleMenu.setVisible(true);
			}
		});

		lineStyleListeners = new ArrayList<>();

		highlighter = new TextHighligher(this);
		lineStyleListeners.add(highlighter);
		addModifyListener(highlighter);

		doubleClick = new DoubleClickHighlighter(this, false, false);
		lineStyleListeners.add(doubleClick);
		addListener(SWT.MouseDown, doubleClick);

		addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent event) {
				event.data = Boolean.valueOf(newStyles);
				if (newStyles) {
					newStyles = false;
					cachedStyles = styles.toArray(new StyleRange[styles.size()]);
				}
				event.styles = cachedStyles;
				for (LineStyleListener l : lineStyleListeners)
					l.lineGetStyle(event);
			}
		});

		search = new CustomSearch(dc, SWT.NONE);
		search.setVisible(false);
		search.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				highlighter.setText(search.getText());
			}
		});
		search.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC)
					search.setVisible(false);
				else if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR)
					findWord(search.getText(), true);
			}
		});

		search.addNextListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				findWord(search.getText(), true);
			}
		});

		search.addPrevListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				findWord(search.getText(), false);
			}
		});

		addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
					switch (e.keyCode) {
					case 'f':
						setSearch(true);
						break;
					}
				} else {
					switch (e.keyCode) {
					case SWT.ESC:
						if (searchActive)
							setSearch(false);
						break;
					}
				}
			}
		});

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				search.dispose();
				dc.dispose();
			}
		});
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		placeSearch();
	}

	private void placeSearch() {
		Point p = getParent().getSize();
		Point fs = search.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		search.setBounds(p.x - fs.x - 20, 0, fs.x, fs.y);
		search.moveAbove(CustomConsole.this);
	}

	public void setSearch(boolean s) {
		if (s == false) {
			highlighter.setText(null);
			search.setText("");
		} else if (searchActive == false || doubleClick.getWord() != null) {
			String selection = doubleClick.getWord();
			highlighter.setText(selection);
			search.setText(selection == null ? "" : selection);
			if (selection != null && !selection.isEmpty())
				doubleClick.clearWord();
		}
		searchActive = s;

		if (searchActive) {
			search.setVisible(true);
			search.setFocus();
		} else {
			search.setVisible(false);
			setFocus();
		}
	}

	public void addStyle(StyleRange s) {
		styles.add(s);
		StyleUtil.sort(styles);
		newStyles = true;
	}

	public void clearStyles() {
		styles.clear();
		newStyles = true;
	}

	private void findWord(String word, boolean dir) {
		if (word == null || word.isEmpty())
			return;

		int start = getCaretOffset();
		String text = getText();
		int idx;
		if (dir)
			idx = StringUtils.indexOfIgnoreCase(text, word, start);
		else {
			idx = StringUtils.lastIndexOfIgnoreCase(text.substring(0, start > text.length() ? text.length() - 1 : start), word);
		}

		if (idx < 0) {
			if (dir)
				idx = StringUtils.indexOfIgnoreCase(text, word);
			else {
				idx = StringUtils.lastIndexOfIgnoreCase(text, word);
			}
		}

		if (idx >= 0) {
			setCaretOffset(idx);
			setSelection(idx, idx + word.length());
		}
	}

	private class DummyComposite extends Composite {

		public DummyComposite(Composite parent, int style) {
			super(parent, style);
			addControlListener(new ControlListener() {
				@Override
				public void controlResized(ControlEvent arg0) {
					Point p = getSize();
					CustomConsole.this.setBounds(0, 0, p.x, p.y);
				}

				@Override
				public void controlMoved(ControlEvent arg0) {

				}
			});
		}
	}

}
