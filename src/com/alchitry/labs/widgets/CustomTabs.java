package com.alchitry.labs.widgets;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.MainWindow;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;

public class CustomTabs extends Composite implements Listener {
	private ArrayList<CustomTab> tabs;
	private ArrayList<CustomTab> hiddenTabs;
	private ArrayList<TabChild> contents;
	private Canvas canvas;
	private MainWindow mainWindow;
	public boolean opened = false;
	private Menu overflowMenu;
	private CustomButton overflowButton;

	private int selected;

	public CustomTabs(Composite parent, int style) {
		super(parent, style);
		mainWindow = MainWindow.mainWindow;

		tabs = new ArrayList<CustomTab>();
		hiddenTabs = new ArrayList<CustomTab>();
		contents = new ArrayList<>();
		selected = -1;

		setBackground(Theme.windowBackgroundColor);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				CustomTabs.this.widgetDisposed(e);
			}
		});

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				CustomTabs.this.controlResized(e);
			}
		});

		overflowButton = new CustomButton(this, SWT.POP_UP);
		overflowMenu = new Menu(this);
		overflowButton.setVisible(false);
		overflowMenu.setVisible(false);
		overflowButton.setBackground(Theme.tabBackgroundColor);
		overflowButton.setIcon(Images.plusIcon);
		overflowButton.setIconHover(Images.plusIconHover);

		overflowButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Point p = toDisplay(overflowButton.getLocation());
				p.y += 25;
				overflowMenu.setLocation(p);
				overflowMenu.setVisible(!overflowMenu.getVisible());
				for (MenuItem i : overflowMenu.getItems())
					i.dispose();

				for (CustomTab t : hiddenTabs) {
					MenuItem mi = new MenuItem(overflowMenu, SWT.PUSH);
					mi.setText(t.getText());
					mi.setData(t);
					mi.addListener(SWT.Selection, overflowSelectionListener);
				}
			}
		});

		canvas = new Canvas(this, SWT.NONE);

		DropTarget dnd = new DropTarget(this, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		final TabTransfer tabTransfer = TabTransfer.getInstance();
		Transfer[] types = new Transfer[] { tabTransfer };
		dnd.setTransfer(types);

		dnd.addDropListener(new DropTargetListener() {

			@Override
			public void dropAccept(DropTargetEvent event) {
			}

			@Override
			public void drop(final DropTargetEvent event) {
				// run async because windows in an unhappy child if you dispose
				// of the tab in the event handler >:(
				getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (tabTransfer.isSupportedType(event.currentDataType)) {
							Integer idx = (Integer) event.data;
							TabChild tab = mainWindow.getTabChild(idx);

							int tabIndex = contents.indexOf(tab);
							if (tabIndex >= 0) {
								moveTab(tabIndex, event.x, event.y);
							} else {
								tab.switchFolder(CustomTabs.this);
							}

							if (!opened && !contents.get(0).isModified())
								close(0);

							opened = true;
							canvas.setBounds(0, 0, 0, 0);
						}
					}
				});

			}

			@Override
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_NONE;
				Point p = CustomTabs.this.toControl(event.x, event.y);
				if (tabs.size() > 0 && p.y > 0 && p.y < 50 & p.x < getSize().x) {
					event.detail = DND.DROP_MOVE;
					int idx = getTab(p);
					Rectangle bounds;
					if (idx < tabs.size()) {
						CustomTab t = tabs.get(idx);
						bounds = t.getBounds();
						bounds.width = 3;
						if (idx > 0)
							bounds.x -= 2;
					} else {
						CustomTab t = tabs.get(tabs.size() - 1);
						bounds = t.getBounds();
						bounds.x += bounds.width - 1;
						bounds.width = 3;
					}

					canvas.setBounds(bounds);
					GC gc = new GC(canvas);
					gc.setForeground(Theme.editorForegroundColor);
					gc.drawRectangle(0, 0, 3, bounds.height);
					gc.dispose();
				} else {
					event.detail = DND.DROP_NONE;
					canvas.setBounds(0, 0, 0, 0);
				}
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
				event.detail = DND.DROP_NONE;
				canvas.setBounds(0, 0, 0, 0);
			}

			@Override
			public void dragEnter(DropTargetEvent event) {
				// will accept text but prefer to have files dropped
				for (int i = 0; i < event.dataTypes.length; i++) {
					if (tabTransfer.isSupportedType(event.dataTypes[i])) {
						event.currentDataType = event.dataTypes[i];
						break;
					}
				}
			}
		});

		final Listener focusListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				checkFocus(event);
			}
		};

		getDisplay().addFilter(SWT.FocusIn, focusListener);

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				getDisplay().removeFilter(SWT.FocusIn, focusListener);
				for (TabChild c : contents)
					c.dispose();
				for (CustomTab t : tabs)
					t.dispose();
			}
		});

		addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				e.doit = false;
			}
		});
	}

	private Listener overflowSelectionListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			for (int i = 0; i < tabs.size(); i++) {
				CustomTab tab = tabs.get(i);
				if (tab.getText().equals(((MenuItem) event.widget).getText())) {
					setSelection(i);
					moveTab(i, 0);
					tab.setBackground(Theme.tabSelectedBackgroundColor);
				} else {
					tab.setBackground(Theme.tabBackgroundColor);
				}
			}
		}
	};

	private void checkFocus(Event event) {
		if (!(event.widget instanceof Control)) {
			return;
		}

		boolean isOurChild = false;
		for (Control c = (Control) event.widget; c != null; c = c.getParent()) {
			if (c == this) {
				isOurChild = true;
				break;
			}
		}

		if (isOurChild) {
			MainWindow.mainWindow.setTabFolder(this);
		}
	}

	public void split(boolean vertical) {
		SashForm parent = (SashForm) getParent();
		int[] weights = parent.getWeights();
		SashForm sash = new SashForm(parent, vertical ? SWT.VERTICAL : SWT.NONE);
		sash.moveAbove(this);
		sash.setBackground(Theme.windowBackgroundColor);
		if (!setParent(sash)) {
			Util.showError("Vertical Splitting is unsupported on your platform!");
			sash.dispose();
			return;
		}

		mainWindow.setTabFolder(this);

		CustomTabs tabs = new CustomTabs(sash, SWT.NONE);
		final StyledCodeEditor codeEditor = new StyledCodeEditor(tabs, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL, null, true);
		mainWindow.addEditor(codeEditor);
		parent.setWeights(weights);
		sash.setWeights(new int[] { 1, 1 });
		parent.layout(true);
	}

	private <T> void move(AbstractList<T> c, int from, int to) {
		c.add(to, c.get(from));
		if (to < from)
			from++;
		c.remove(from);
	}

	public List<TabChild> getTabChildren() {
		return contents;
	}

	public TabChild getTabChild(int idx) {
		return contents.get(idx);
	}

	private int getTab(Point p) {
		int[] breaks = new int[tabs.size() + 1];
		breaks[0] = 0;
		for (int i = 0; i < breaks.length - 1; i++)
			breaks[i + 1] = breaks[i] + tabs.get(i).getSize().x + 1;

		int minDist = Integer.MAX_VALUE;
		int pos = -1;
		for (int i = 0; i < breaks.length; i++) {
			int d = Math.abs(breaks[i] - p.x);
			if (d < minDist) {
				minDist = d;
				pos = i;
			}
		}
		return pos;
	}

	private void moveTab(int fromIdx, int toIdx) {
		move(tabs, fromIdx, toIdx);
		move(contents, fromIdx, toIdx);
		if (toIdx > fromIdx)
			toIdx--;
		if (selected == fromIdx)
			selected = toIdx;
		else if (fromIdx < selected && toIdx >= selected)
			selected--;
		else if (fromIdx > selected && toIdx <= selected)
			selected++;
		resize();
		updateTabs();
	}

	private void moveTab(int tabIdx, int dropX, int dropY) {
		Point p = this.toControl(dropX, dropY);
		if (p.y < 50) {
			int pos = getTab(p);
			moveTab(tabIdx, pos);
		}
	}

	public void addTab(String text, TabChild e) {
		CustomTab tab = new CustomTab(this, 0, tabs.size());
		tab.setBackground(Theme.tabBackgroundColor);
		tab.setForeground(Theme.editorForegroundColor);
		tab.setText(text);
		tab.addListener(SWT.Selection, this);
		tab.addListener(SWT.Close, this);
		tabs.add(tab);
		contents.add(e);
		resize();
		if (selected == -1)
			setSelection(tabs.size() - 1);
		updateTabs();
	}

	protected void controlResized(ControlEvent e) {
		resize();
	}

	private void resize() {
		int x = 0;
		int h = 0;
		if (!isDisposed()) {
			Point size = getSize();
			boolean oversized = false;

			for (CustomTab tab : tabs) {
				Point tabExtent = tab.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
				tab.setBounds(x, 0, tabExtent.x, tabExtent.y);

				if (tabExtent.x + x >= size.x) {
					oversized = true;
					tab.setVisible(false);
				} else {
					tab.setVisible(true);
				}

				x += tabExtent.x + 1;
				h = Math.max(h, tabExtent.y);
			}

			if (oversized) {
				hiddenTabs.clear();
				for (CustomTab tab : tabs) {
					Rectangle bounds = tab.getBounds();

					if (bounds.x + bounds.width >= size.x - 26) {
						tab.setVisible(false);
						hiddenTabs.add(tab);
					} else {
						tab.setVisible(true);
					}
				}

				overflowButton.setVisible(true);
				overflowButton.setBounds(size.x - 25, 0, 25, 25);
			} else {
				overflowButton.setVisible(false);
			}

			for (int i = 0; i < tabs.size(); i++) {
				TabChild e = contents.get(i);
				Point p = getSize();
				e.setBounds(0, h, p.x, p.y - h);

				if (selected >= 0 && contents.get(selected) == e) {
					e.setVisible(true);
				} else {
					e.setVisible(false);
				}
			}
		}
	}

	protected void widgetDisposed(DisposeEvent e) {
		for (CustomTab tab : tabs) {
			tab.dispose();
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int width = 0;
		int height = 0;
		for (CustomTab tab : tabs) {
			Point tabExtenet = tab.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
			width += tabExtenet.x;
			height = Math.max(height, tabExtenet.y);
		}

		if (wHint != SWT.DEFAULT)
			width = wHint;
		if (hHint != SWT.DEFAULT)
			height = hHint;
		return new Point(width, height);
	}

	private void updateTabs() {
		TabChild sel = null;
		if (selected >= 0) {
			sel = contents.get(selected);
		}

		for (int i = 0; i < contents.size(); i++) {
			TabChild c = contents.get(i);
			CustomTab t = tabs.get(i);
			t.setIndex(i);
			if (c == sel) {
				c.setVisible(true);
				t.setBackground(Theme.tabSelectedBackgroundColor);
			} else {
				c.setVisible(false);
				t.setBackground(Theme.tabBackgroundColor);
			}
			t.redraw();
		}
		redraw();
	}

	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.Selection:
			for (int i = 0; i < tabs.size(); i++) {
				CustomTab tab = tabs.get(i);
				if (tab.equals(event.widget)) {
					setSelection(i);
					tab.setBackground(Theme.tabSelectedBackgroundColor);
				} else {
					tab.setBackground(Theme.tabBackgroundColor);
				}
			}
			break;

		case SWT.Close:
			for (int i = 0; i < tabs.size(); i++) {
				if (tabs.get(i) == event.widget) {
					TabChild control = contents.get(i);
					close(control);
					break;
				}
			}
			break;
		}
	}

	public TabChild getSelectedControl() {
		if (selected < 0)
			return null;
		return contents.get(selected);
	}

	public void setSelection(Control c) {
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).equals(c)) {
				setSelection(i);
				break;
			}
		}
	}

	public void nextTab() {
		selected = (selected + 1) % tabs.size();
		updateTabs();
		redraw();
		contents.get(selected).forceFocus();
	}

	public void prevTab() {
		selected--;
		if (selected < 0)
			selected = tabs.size() - 1;
		updateTabs();
		redraw();
		contents.get(selected).forceFocus();
	}

	public void setSelection(int i) {
		selected = i;
		updateTabs();
		redraw();
	}

	public void setText(Control c, String text) {
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i) == c) {
				tabs.get(i).setText(text);
				tabs.get(i).redraw();
				break;
			}
		}
		resize();
		// updateTabs();
	}

	public String getText(Control c) {
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).equals(c)) {
				return tabs.get(i).getText();
			}
		}
		return null;
	}

	public void remove(TabChild e) {
		int idx = contents.indexOf(e);
		if (idx >= 0)
			remove(idx);
	}

	public void remove(int i) {
		contents.remove(i);
		tabs.get(i).dispose();
		tabs.remove(i);

		if (selected == i) {
			if (selected > 0)
				setSelection(selected - 1);
			else if (contents.size() > 0)
				setSelection(0);
			else
				setSelection(-1);
		} else if (selected > i) {
			setSelection(selected - 1);
		}
		Composite p = getParent();
		Composite pp = p.getParent();
		if (contents.size() == 0 && !mainWindow.isSideSash(p)) {
			int[] weights = ((SashForm) pp).getWeights();
			dispose();
			for (Control c : p.getChildren())
				if (!(c instanceof Sash)) {
					c.setParent(pp);
					c.moveAbove(p);
					break;
				}
			p.dispose();

			((SashForm) pp).setWeights(weights);
			pp.layout(true);

			mainWindow.setDefaultFolder();
		} else {
			if (contents.size() == 0) {
				this.opened = false;
				final StyledCodeEditor codeEditor = new StyledCodeEditor(this, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL, null, true);
				mainWindow.addEditor(codeEditor);
			}
			resize();
			updateTabs();
		}
	}

	public void close() {
		if (selected < tabs.size()) {
			close(selected);
			contents.get(selected).forceFocus();
		}
	}

	public void close(int i) {
		TabChild e = contents.get(i);
		if (mainWindow.closeTab(e)) {
			e.dispose();
			remove(i);
		}
		resize();
	}

	public void close(TabChild c) {

		int idx = contents.indexOf(c);
		if (idx >= 0)
			close(idx);
	}

	public void setTabTextColor(Control c, Color tabTextColor) {
		int idx = contents.indexOf(c);
		if (idx < 0 || idx > tabs.size())
			return;
		tabs.get(idx).setForeground(tabTextColor);
		tabs.get(idx).redraw();
	}

}
