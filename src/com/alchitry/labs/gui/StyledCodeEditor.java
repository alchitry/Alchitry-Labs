package com.alchitry.labs.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.alchitry.labs.Util;
import com.alchitry.labs.dictionaries.AlchitryConstraintsDictionary;
import com.alchitry.labs.dictionaries.LucidDictionary;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.errors.AlchitryConstraintsErrorProvider;
import com.alchitry.labs.parsers.errors.ErrorProvider;
import com.alchitry.labs.parsers.errors.VerilogErrorProvider;
import com.alchitry.labs.parsers.styles.AlchitryConstraintStyleProvider;
import com.alchitry.labs.parsers.styles.LucidStyleProvider;
import com.alchitry.labs.parsers.styles.VerilogStyleProvider;
import com.alchitry.labs.parsers.styles.lucid.LucidErrorProvider;
import com.alchitry.labs.parsers.styles.lucid.LucidNewLineIndenter;
import com.alchitry.labs.parsers.styles.verilog.VerilogIndentProvider;
import com.alchitry.labs.parsers.styles.verilog.VerilogNewLineIndenter;
import com.alchitry.labs.project.Project;
import com.alchitry.labs.style.AutoComplete;
import com.alchitry.labs.style.AutoFormatter;
import com.alchitry.labs.style.BracketUnderliner;
import com.alchitry.labs.style.IndentProvider;
import com.alchitry.labs.style.LineHighlighter;
import com.alchitry.labs.style.LineStyler;
import com.alchitry.labs.style.ToolTipListener;
import com.alchitry.labs.tools.ParserCache;
import com.alchitry.labs.widgets.CustomSearch;
import com.alchitry.labs.widgets.CustomTabs;
import com.alchitry.labs.widgets.TabChild;
import com.alchitry.labs.widgets.TabHotKeys;

public class StyledCodeEditor extends StyledText implements ModifyListener, TabChild {

	private String filePath;
	private boolean edited;
	private CustomTabs tabFolder;
	private boolean opened;
	private AutoFormatter formatter;
	private UndoRedo undoRedo;
	private String fileName;
	private AutoComplete autoComplete;
	private TextHighligher highlighter;
	private CustomSearch search;
	private DoubleClickHighlighter doubleClick;
	private List<LineStyleListener> lineStyleListeners;

	private ErrorProvider errorChecker;
	private boolean hasErrors;

	private boolean searchActive = false;

	private boolean isLucid = false;
	private boolean isVerilog = false;
	private boolean isConstraint = false;

	private boolean write;

	private Menu rightClickMenu;

	public StyledCodeEditor(CustomTabs parent, int style, String file, boolean write) {
		super(parent, style);
		this.tabFolder = parent;
		this.write = write;

		search = new CustomSearch(parent, SWT.NONE);

		if (Util.isLinux) // Windows has a bug where hidden scroll bars flash
			setAlwaysShowScrollBars(false);

		setBackground(Theme.editorBackgroundColor);
		setForeground(Theme.editorForegroundColor);

		LineHighlighter lineHighligher = new LineHighlighter(this);
		addCaretListener(lineHighligher);
		addExtendedModifyListener(lineHighligher);
		setSelectionBackground(Theme.editorTextSelectedColor);
		setSelectionForeground(null);

		IndentProvider indentProvider = null;
		VerifyListener newLineIndenter = null;
		ExtendedModifyListener unindentProvider = null;

		lineStyleListeners = new ArrayList<>();

		hasErrors = false;

		undoRedo = new UndoRedo(this);
		addExtendedModifyListener(undoRedo);

		if (file == null) {
			undoRedo.skipNext();
		} else if (file.endsWith(".luc")) {
			LucidDictionary dict = new LucidDictionary(this);
			LucidErrorProvider lErrorChecker = new LucidErrorProvider(this, dict);
			lineStyleListeners.add(lErrorChecker);
			addModifyListener(lErrorChecker);
			errorChecker = lErrorChecker;
			LucidStyleProvider lsp = new LucidStyleProvider(this);
			addModifyListener(lsp);
			lineStyleListeners.add(lsp);
			// indentProvider = new LucidIndentProvider();
			LucidNewLineIndenter nli = new LucidNewLineIndenter(this, undoRedo);
			newLineIndenter = nli;
			unindentProvider = nli;
			indentProvider = nli;
			isLucid = true;
			autoComplete = new AutoComplete(this, dict);
		} else if (file.endsWith(".v")) {
			VerilogErrorProvider vErrorChecker = new VerilogErrorProvider(this);
			lineStyleListeners.add(vErrorChecker);
			addModifyListener(vErrorChecker);
			errorChecker = vErrorChecker;
			VerilogStyleProvider vsp = new VerilogStyleProvider(this);
			lineStyleListeners.add(vsp);
			addModifyListener(vsp);
			indentProvider = new VerilogIndentProvider();
			newLineIndenter = new VerilogNewLineIndenter(this, undoRedo);
			isVerilog = true;
		} else if (file.endsWith(".ucf")) {
			isConstraint = true;
			// TODO : UCF checking
		} else if (file.endsWith(".acf")) {
			AlchitryConstraintsErrorProvider aErrorChecker = new AlchitryConstraintsErrorProvider(this);
			lineStyleListeners.add(aErrorChecker);
			addModifyListener(aErrorChecker);
			errorChecker = aErrorChecker;
			AlchitryConstraintStyleProvider asp = new AlchitryConstraintStyleProvider(this);
			lineStyleListeners.add(asp);
			addModifyListener(asp);

			AlchitryConstraintsDictionary dict = new AlchitryConstraintsDictionary();

			Project p = MainWindow.getOpenProject();
			if (p != null)
				p.addSaveListener(new Listener() {
					@Override
					public void handleEvent(Event arg0) {
						dict.updatePortNames();
					}
				});

			autoComplete = new AutoComplete(this, dict);
			isConstraint = true;
		} else {
			Util.log.severe("ERROR: UNSUPPORTED FILE TYPE. " + file);
		}

		ToolTipListener tooltips = new ToolTipListener(this, errorChecker);
		addMouseTrackListener(tooltips);
		addMouseMoveListener(tooltips);

		LineStyler styler = new LineStyler(this);
		addLineStyleListener(styler);
		addModifyListener(styler);
		setTabs(2);

		setFont(new Font(getDisplay(), "Ubuntu Mono", 12, SWT.NORMAL));

		if (file != null)
			fileName = file.substring(file.lastIndexOf(File.separatorChar) + 1);
		else
			fileName = "Untitled";

		tabFolder.addTab(fileName, this);

		if (indentProvider != null)
			formatter = new AutoFormatter(this, indentProvider);

		if (newLineIndenter != null)
			addVerifyListener(newLineIndenter);

		if (unindentProvider != null)
			addExtendedModifyListener(unindentProvider);

		opened = openFile(file);

		addModifyListener(this);
		addVerifyKeyListener(new HotKeys(this));
		addKeyListener(new TabHotKeys(this));

		highlighter = new TextHighligher(this);
		lineStyleListeners.add(highlighter);
		addModifyListener(highlighter);

		doubleClick = new DoubleClickHighlighter(this, isLucid, isVerilog);

		addListener(SWT.MouseDown, doubleClick);
		lineStyleListeners.add(doubleClick);

		BracketUnderliner bracketUnderliner = new BracketUnderliner(this);
		addCaretListener(bracketUnderliner);
		lineStyleListeners.add(bracketUnderliner);

		addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent event) {
				for (LineStyleListener l : lineStyleListeners)
					l.lineGetStyle(event);
			}
		});

		addVerifyKeyListener(new VerifyKeyListener() {
			@Override
			public void verifyKey(VerifyEvent event) {
				if (autoComplete != null && autoComplete.isActive()) {
					event.doit = event.doit && !autoComplete.keyPressed(event.keyCode);
				}
			}
		});

		new BlockIndenter(this); // converts tabs into spaces and multiline tabs to indents

		// attach search to parent so that it doesn't scroll with the text
		search = new CustomSearch(getParent(), SWT.NONE);
		search.addModifyListener(this);
		search.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC)
					setSearch(false);
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

		search.setVisible(false);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				search.dispose();
			}
		});

		rightClickMenu = new Menu(this);

		setupMenu();

		if (!write) {
			addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					if (isConstraint)
						Util.showInfo("Library constraint files are read only!");
					else
						Util.showInfo("Components are read only!");
					e.doit = false;
				}
			});
		}

		addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				e.doit = false;
			}
		});

		addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ParserCache.invalidate(getFilePath());
			}
		});
	}

	@Override
	public void dispose() {
		if (autoComplete != null)
			autoComplete.dispose();
		getFont().dispose();
		search.dispose();
		super.dispose();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		search.setVisible(searchActive);
	}

	public void switchFolder(CustomTabs folder) {
		if (folder != tabFolder) {
			if (!setParent(folder) || !search.setParent(folder)) {
				Util.showError("Moving tabs between windows isn't supported on your platform!");
			}
			tabFolder.remove(this);
			tabFolder = folder;
			tabFolder.addTab(fileName, this);
		}
	}

	public void setSearch(boolean s) {
		if (s == false) {
			if (searchActive == false)
				return;
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

	private void placeSearch() {
		Point p = getSize();
		Point fs = search.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		p.x -= fs.x + 20;
		p.y = 0;
		p = getDisplay().map(this, getParent(), p);
		search.setBounds(p.x, p.y, fs.x, fs.y);
		search.moveAbove(this);
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		placeSearch();
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isOpen() {
		return opened;
	}

	@Override
	public boolean isModified() {
		return edited;
	}

	public void formatText() {
		if (formatter == null)
			return;
		int carret = getCaretOffset();
		formatter.fixIndent();
		carret = Math.min(Math.max(0, carret), getCharCount());
		setCaretOffset(carret);
	}

	public String getFilePath() {
		return filePath;
	}

	public void grabFocus() {
		tabFolder.setSelection(this);
		setFocus();
	}

	private boolean openFile(String path) {
		String fileContents;
		if (path != null) {
			path = new File(path).getAbsolutePath();
			try {
				fileContents = Util.readFile(path);
			} catch (IOException e1) {
				Util.log.severe("Could not open file " + path);
				return false;
			}
		} else {
			fileContents = "";
		}

		filePath = path;
		edited = false;
		undoRedo.skipNext();
		setText(fileContents);
		tabFolder.setSelection(this);

		return true;
	}

	public boolean save() {
		if (!write) {
			return false;
		}

		if (filePath == null) {
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterExtensions(new String[] { "*.luc", "*.v", "*" });
			dialog.setText("Save File");
			String path = dialog.open();
			if (path == null) {
				return false;
			}

			filePath = path;
			tabFolder.setText(this, fileName);
			edited = false;
		}
		try {
			PrintWriter out = new PrintWriter(filePath);
			out.print(getText());
			out.close();
		} catch (FileNotFoundException e1) {
			return false;
		}

		if (edited == true) {
			tabFolder.setText(this, tabFolder.getText(this).substring(1));
		}
		edited = false;
		MainWindow.mainWindow.updateErrors();
		return true;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if (e.widget == this) {
			if (edited == false) {
				tabFolder.setText(this, "*" + fileName);
			}
			edited = true;

			// work around for selectAll -> delete bug
			if (getText().isEmpty())
				redraw();
		} else { // modify from search
			String text = ((Text) e.widget).getText();
			highlighter.setText(text);
		}
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
			placeSearch();
		}
	}

	public void close() {
		tabFolder.close(this);
	}

	public void updateTextColor() {
		hasErrors = false;
		if (errorChecker.hasErrors()) {
			hasErrors = true;
			tabFolder.setTabTextColor(this, Theme.tabErrorTextColor);
		} else if (errorChecker.hasWarnings()) {
			tabFolder.setTabTextColor(this, Theme.tabWarningTextColor);
		} else {
			tabFolder.setTabTextColor(this, Theme.tabNormalTextColor);
		}
	}

	public void undo() {
		undoRedo.undo();
	}

	public void redo() {
		undoRedo.redo();
	}

	public void updateErrors() {
		if (errorChecker != null)
			errorChecker.updateErrors();
	}

	public Color getLineColor(int line) {
		if (errorChecker != null)
			return errorChecker.getLineColor(line);
		return null;
	}

	public boolean isVerilog() {
		return isVerilog;
	}

	public boolean isLucid() {
		return isLucid;
	}

	private void setupMenu() {
		setMenu(rightClickMenu);

		MenuItem item;

		final MenuItem undo = new MenuItem(rightClickMenu, SWT.NONE);
		undo.setText("&Undo\tCtrl+Z");
		undo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				undo();
			}
		});

		final MenuItem redo = new MenuItem(rightClickMenu, SWT.NONE);
		redo.setText("&Redo\tCtrl+Shift+Z");
		redo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				redo();
			}
		});

		new MenuItem(rightClickMenu, SWT.SEPARATOR);

		item = new MenuItem(rightClickMenu, SWT.NONE);
		item.setText("Cut\tCtrl+X");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cut();
			}
		});

		item = new MenuItem(rightClickMenu, SWT.NONE);
		item.setText("&Copy\tCtrl+C");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				copy();
			}
		});

		final MenuItem paste = new MenuItem(rightClickMenu, SWT.NONE);
		paste.setText("&Paste\tCtrl+V");
		paste.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				paste();
			}
		});

		new MenuItem(rightClickMenu, SWT.SEPARATOR);

		item = new MenuItem(rightClickMenu, SWT.NONE);
		item.setText("&Save\tCtrl+S");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
			}
		});

		new MenuItem(rightClickMenu, SWT.SEPARATOR);

		item = new MenuItem(rightClickMenu, SWT.NONE);
		item.setText("Split &Horizontal\tCtrl+H");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tabFolder.split(false);
			}
		});

		item = new MenuItem(rightClickMenu, SWT.NONE);
		item.setText("Split &Vertical\tCtrl+G");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tabFolder.split(true);
			}
		});

		new MenuItem(rightClickMenu, SWT.SEPARATOR);

		item = new MenuItem(rightClickMenu, SWT.NONE);
		item.setText("&Search\tCtrl+F");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSearch(true);
			}
		});

		final MenuItem format = new MenuItem(rightClickMenu, SWT.NONE);
		format.setText("&Auto-format Code\tCtrl+Shift+F");
		format.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				formatText();
			}
		});

		rightClickMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				redo.setEnabled(undoRedo.canRedo());
				undo.setEnabled(undoRedo.canUndo());
				format.setEnabled(formatter != null);
			}
		});
	}
}
