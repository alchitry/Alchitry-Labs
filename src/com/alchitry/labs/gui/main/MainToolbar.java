package com.alchitry.labs.gui.main;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import com.alchitry.labs.Util;
import com.alchitry.labs.boards.Board;
import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.StyledCodeEditor;
import com.alchitry.labs.gui.Theme;
import com.alchitry.labs.project.Project;
import com.alchitry.labs.widgets.CustomButton;
import com.alchitry.labs.widgets.TabChild;

public class MainToolbar {
	MainWindow parent;
	Composite toolbar;
	CustomButton buildButton;

	ArrayList<CustomButton> buttons;

	public MainToolbar(MainWindow parent) {
		this.parent = parent;
		buttons = new ArrayList<>();

		toolbar = new Composite(parent.getShell(), SWT.NONE);
		toolbar.setBackground(Theme.windowBackgroundColor);
		toolbar.setForeground(Theme.windowForgroundColor);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		toolbar.setLayout(layout);
	}

	private CustomButton createButton(Image icon, Image hover, String toolTip, Listener selectionListener) {
		CustomButton button = new CustomButton(toolbar, SWT.NONE);
		buttons.add(button);
		button.setIcon(icon);
		button.setIconHover(hover);
		button.setToolTipText(toolTip);
		button.addListener(SWT.Selection, selectionListener);
		return button;
	}

	public void setBuilding(boolean building) {
		if (building) {
			buildButton.setIcon(Images.cancelIcon);
			buildButton.setIconHover(Images.cancelIconHover);
			buildButton.setToolTipText("Cancel Build");
		} else {
			buildButton.setIcon(Images.buildIcon);
			buildButton.setIconHover(Images.buildIconHover);
			buildButton.setToolTipText("Build Project");
		}
		buildButton.redraw();
	}

	public void build() {
		for (CustomButton b : buttons)
			b.dispose();
		buttons.clear();

		Board board = null;
		Project p = MainWindow.project;
		if (p != null)
			board = p.getBoard();

		createButton(Images.fileIcon, Images.fileIconHover, "New File", new Listener() {
			@Override
			public void handleEvent(Event event) {
				parent.addNewFile();
			}
		});

		createButton(Images.saveIcon, Images.saveIconHover, "Save File", new Listener() {
			@Override
			public void handleEvent(Event event) {
				TabChild tc = parent.tabFolder.getSelectedControl();
				if (tc instanceof StyledCodeEditor)
					parent.saveEditor((StyledCodeEditor) tc, false);
			}
		});

		createButton(Images.saveAllIcon, Images.saveAllIconHover, "Save All", new Listener() {
			@Override
			public void handleEvent(Event event) {
				parent.saveAll(false);
			}
		});

		createButton(Images.checkIcon, Images.checkIconHover, "Check Syntax", new Listener() {
			@Override
			public void handleEvent(Event event) {
				parent.checkProject();
			}
		});

		if (Board.isType(board, Board.ANY))
			buildButton = createButton(Images.buildIcon, Images.buildIconHover, "Build Project", new Listener() {
				@Override
				public void handleEvent(Event event) {
					parent.buildProject();
				}
			});

		if (Board.isType(board, Board.MOJO))
			createButton(Images.debugIcon, Images.debugIconHover, "Debug Project", new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (MainWindow.getOpenProject() != null) {
						Util.showError("A project must be opened before you can build it!");
						return;
					}
					if (MainWindow.project.isBusy()) {
						Util.showError("Can't debug while operation is in progress!");
						return;
					}
					if (!parent.saveAll(false)) {
						Util.showError("Could not save all open tabs before debugging!");
						MessageBox box = new MessageBox(parent.getShell(), SWT.YES | SWT.NO);

						box.setMessage("Continue anyway?");
						box.setText("All files not saved...");
						if (box.open() != SWT.YES) {
							return;
						}
					}
					MainWindow.project.build(true);
				}
			});

		if (Board.isType(board, Board.MOJO | Board.AU))
			createButton(Images.loadTempIcon, Images.loadTempIconHover, "Program (Temporary)", new Listener() {
				@Override
				public void handleEvent(Event event) {
					parent.programProject(false, false);
				}
			});

		if (Board.isType(board, Board.ANY))
			createButton(Images.loadIcon, Images.loadIconHover, "Program (Flash)", new Listener() {
				@Override
				public void handleEvent(Event event) {
					parent.programProject(true, event.button == 2);
				}
			});

		if (Board.isType(board, Board.ANY))
			createButton(Images.eraseIcon, Images.eraseIconHover, "Erase", new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (MainWindow.project.isBusy()) {
						Util.showError("Operation already in progress!");
						return;
					}
					MainWindow.project.erase();
				}
			});
		
		toolbar.requestLayout();
	}
}
