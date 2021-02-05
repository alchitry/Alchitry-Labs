package com.alchitry.labs.style;

import com.alchitry.labs.dictionaries.Dictionary;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.widgets.CustomTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

import java.util.List;

public class AutoComplete {
    private final Shell popupShell;
    private final CustomTable table;
    private final Display display;
    private final StyledCodeEditor editor;
    private final Dictionary dictionary;
    private final Point textLocation;
    private boolean skip = false;

    public AutoComplete(StyledCodeEditor editor, Dictionary dict) {
        this.editor = editor;
        dictionary = dict;
        display = editor.getDisplay();
        popupShell = new Shell(MainWindow.shell, SWT.ON_TOP | SWT.NO_TRIM | SWT.NO_FOCUS);
        popupShell.setLayout(new FillLayout());
        table = new CustomTable(popupShell, SWT.NONE);
        popupShell.setBackground(Theme.autocompleteBackgroundColor);
        popupShell.setForeground(Theme.autocompleteForegroundColor);
        table.setBackground(Theme.autocompleteBackgroundColor);
        table.setForeground(Theme.autocompleteForegroundColor);
        table.setFont(editor.getFont());

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

    public void updateFont() {
        table.setFont(editor.getFont());
    }

    public void dispose() {
        popupShell.dispose();
    }

    public boolean isActive() {
        if (popupShell.isDisposed())
            return false;
        return popupShell.isVisible();
    }

    public void skipNext() {
        skip = true;
    }

    private final Listener keyDownListener = new Listener() {
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
        while (start > 0 && (Character.isLetterOrDigit(text.charAt(start))
                || text.charAt(start) == '_'
                || text.charAt(start) == '$')
        )
            start--;
        start++;
        if (start > end)
            start = end;
        textLocation.x = start;
        textLocation.y = end;
        return text.substring(start, end);
    }

    private final Listener modifyListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            if (skip) {
                skip = false;
                return;
            }

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
                popupShell.setBounds(p.x - 8, (int) (p.y + 5 + editor.getFont().getFontData()[0].height), tb.x, tb.y);
                popupShell.setVisible(true);
            }

        }
    };

    private final Listener tableDefaultSelectionListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            selected();
        }
    };

    private final Listener tableKeyDownListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            if (event.keyCode == SWT.ESC) {
                popupShell.setVisible(false);
            }
        }
    };

    private final Listener focusOutListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            // async is needed to wait until focus reaches its new Control
            display.asyncExec(() -> {
                if (display.isDisposed() || popupShell.isDisposed())
                    return;
                Control control = display.getFocusControl();

                if (control == null || (control != editor && control != table && control != popupShell)) {
                    popupShell.setVisible(false);
                }
            });
        }
    };

    private final Listener moveListener = new Listener() {
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
