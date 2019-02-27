package com.alchitry.labs.style;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.alchitry.labs.dictionaries.Dictionary;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.widgets.CustomTable;

public class AutoComplete {
	private final Shell popupShell;
	private final CustomTable table;
	private final Display display;
	private StyledCodeEditor editor;
	private final Dictionary dictionary;
	private Point textLocation;

	public AutoComplete(StyledCodeEditor editor, Dictionary dict) {
		this.editor = editor;
		dictionary = dict;
		display = editor.getDisplay();
		popupShell = new Shell(display, SWT.ON_TOP | SWT.NO_TRIM);
		popupShell.setLayout(new FillLayout());
		table = new CustomTable(popupShell, SWT.NONE);
		popupShell.setBackground(Theme.autocompleteBackgroundColor);
		popupShell.setForeground(Theme.autocompleteForegroundColor);
		table.setBackground(Theme.autocompleteBackgroundColor);
		table.setForeground(Theme.autocompleteForegroundColor);
		table.setFont(new Font(display, "Ubuntu Mono", 12, SWT.NORMAL));

		editor.addListener(SWT.KeyDown, keyDownListener);
		editor.addListener(SWT.Modify, modifyListener);
		table.addListener(SWT.DefaultSelection, tableDefaultSelectionListener);
		table.addListener(SWT.KeyDown, tableKeyDownListener);
		table.addListener(SWT.FocusOut, focusOutListener);
		editor.addListener(SWT.FocusOut, focusOutListener);
		editor.getShell().addListener(SWT.Move, moveListener);

		table.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				selected();
			}
		});

		editor.addCaretListener(new CaretListener() {
			@Override
			public void caretMoved(CaretEvent event) {
				popupShell.setVisible(false);
			}
		});

		editor.getShell().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (!popupShell.isDisposed())
					popupShell.setVisible(false);
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		textLocation = new Point(0, 0);
	}

	public void dispose() {
		table.getFont().dispose();
		popupShell.dispose();
	}

	public boolean isActive() {
		if (popupShell.isDisposed())
			return false;
		return popupShell.isVisible();
	}

	private Listener keyDownListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if (event.keyCode == SWT.ESC && !popupShell.isDisposed())
				popupShell.setVisible(false);
		}
	};

	private String getWordAtCaret() {
		String text = editor.getText();
		int start, end;
		end = Math.min(editor.getCaretOffset(), text.length());
		start = Math.max(end - 1, 0);
		while (start > 0 && (Character.isLetterOrDigit(text.charAt(start)) || text.charAt(start) == '_'))
			start--;
		start++;
		if (start > end)
			start = end;
		textLocation.x = start;
		textLocation.y = end;
		return text.substring(start, end);
	}

	private Listener modifyListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			String string = getWordAtCaret();
			char preLetter = '\0';
			if (textLocation.x > 0)
				preLetter = editor.getText().charAt(textLocation.x - 1);

			if (string.length() == 0 && preLetter != '.') {
				popupShell.setVisible(false);
			} else {
				List<String> list = dictionary.findMatches(string);

				if (list == null || list.isEmpty()) {
					popupShell.setVisible(false);
					return;
				}

				table.setItems(list);
				table.setSelection(0);

				Point tb = table.computeSize(-1, -1, true);

				Point p = display.map(editor, null, editor.getLocationAtOffset(editor.getCaretOffset() - string.length()));
				popupShell.setBounds(p.x - 8, p.y + 17, tb.x, tb.y);
				popupShell.setVisible(true);
			}

		}
	};

	private Listener tableDefaultSelectionListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			selected();
		}
	};

	private Listener tableKeyDownListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if (event.keyCode == SWT.ESC) {
				popupShell.setVisible(false);
			}
		}
	};

	private Listener focusOutListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			/* async is needed to wait until focus reaches its new Control */
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (display.isDisposed() || popupShell.isDisposed())
						return;
					Control control = display.getFocusControl();
					if (control == null || (control != editor && control != table)) {
						popupShell.setVisible(false);
					}
				}
			});
		}
	};

	private Listener moveListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if (!popupShell.isDisposed())
				popupShell.setVisible(false);
		}
	};

	private void selected() {
		String text = table.getSelectedItem();
		int start = textLocation.x;
		int length = textLocation.y - start;
		editor.replaceTextRange(start, length, text);
		editor.setCaretOffset(start + text.length());
		popupShell.setVisible(false);
	}

	public boolean keyPressed(int keyCode) {
		if (keyCode == SWT.ESC) {
			popupShell.setVisible(false);
			return true;
		} else {
			return table.keyPressed(keyCode);
		}
	}

}
