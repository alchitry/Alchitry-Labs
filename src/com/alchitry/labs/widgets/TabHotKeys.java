package com.alchitry.labs.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class TabHotKeys implements KeyListener {
	private TabChild tab;

	public TabHotKeys(TabChild tabChild) {
		this.tab = tabChild;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
			if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT) { // ctrl + shift
				switch (e.keyCode) {
				case SWT.TAB:
					((CustomTabs) tab.getParent()).prevTab();
					e.doit = false;
					break;
				}
			} else { // ctrl only
				switch (e.keyCode) {
				case 'h':
					((CustomTabs) tab.getParent()).split(false);
					e.doit = false;
					break;
				case 'g':
					((CustomTabs) tab.getParent()).split(true);
					e.doit = false;
					break;
				case SWT.TAB:
				case SWT.PAGE_DOWN:
					((CustomTabs) tab.getParent()).nextTab();
					e.doit = false;
					break;
				case SWT.PAGE_UP:
					((CustomTabs) tab.getParent()).prevTab();
					e.doit = false;
					break;
				case 'w':
					((CustomTabs) tab.getParent()).close();
					e.doit = false;
					break;
				}
			}
		} else {
			if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT) { // shift only
				
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
