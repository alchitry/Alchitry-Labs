package com.alchitry.labs.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;

import com.alchitry.labs.style.StyleUtil;
import com.alchitry.labs.style.StyleUtil.StyleMerger;

public abstract class CachedStyleListner implements LineStyleListener {
	protected ArrayList<StyleRange> styles;
	private StyleRange[] cachedStyles;
	private boolean newStyles = false;
	protected Semaphore lock = new Semaphore(1);
	private StyleMerger merger;

	public CachedStyleListner() {
		styles = new ArrayList<>();
		merger = getStyleMerger();
	}

	@Override 
	public void lineGetStyle(LineStyleEvent event) {
		lock.acquireUninterruptibly();
		if (newStyles || ((event.data instanceof Boolean) && (Boolean) event.data)) {
			newStyles = false;
			List<StyleRange> styleCopy = StyleUtil.mergeStyles(styles, event.styles, merger);
			lock.release();

			cachedStyles = styleCopy.toArray(new StyleRange[styleCopy.size()]);
			event.data = Boolean.TRUE;
		} else {
			lock.release();
			event.data = Boolean.FALSE;
		}

		event.styles = cachedStyles;
	}

	abstract protected StyleMerger getStyleMerger();
	
	protected void invalidateStyles() {
		newStyles = true;
	}
}
