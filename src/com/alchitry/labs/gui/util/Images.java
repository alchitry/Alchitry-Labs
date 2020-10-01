package com.alchitry.labs.gui.util;

import com.alchitry.labs.Settings;
import com.alchitry.labs.gui.main.MainWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class Images {
	public static Image loadIcon;
	public static Image loadTempIcon;
	public static Image fileIcon;
	public static Image buildIcon;
	public static Image eraseIcon;
	public static Image loadIconHover;
	public static Image loadTempIconHover;
	public static Image fileIconHover;
	public static Image buildIconHover;
	public static Image eraseIconHover;
	public static Image xIcon;
	public static Image xGreyIcon;
	public static Image xRedIcon;
	public static Image arrowUp;
	public static Image arrowDown;
	public static Image arrowUpHover;
	public static Image arrowDownHover;
	public static Image plusIcon;
	public static Image plusIconHover;
	public static Image cancelIcon;
	public static Image cancelIconHover;
	public static Image saveIcon;
	public static Image saveIconHover;
	public static Image saveAllIcon;
	public static Image saveAllIconHover;
	public static Image checkIcon;
	public static Image checkIconHover;
	public static Image treeArrowIcon;
	public static Image treeArrowIconSelected;
	public static Image triggerRiseIcon;
	public static Image triggerRiseIconHover;
	public static Image triggerFallIcon;
	public static Image triggerFallIconHover;
	public static Image triggerHighIcon;
	public static Image triggerHighIconHover;
	public static Image triggerLowIcon;
	public static Image triggerLowIconHover;
	public static Image connectIcon;
	public static Image connectIconHover;
	public static Image captureIcon;
	public static Image captureIconHover;
	public static Image syncIcon;
	public static Image syncIconHover;
	public static Image debugIcon;
	public static Image debugIconHover;
	public static Image replaceOneIcon;
	public static Image replaceOneHover;
	public static Image replaceAllIcon;
	public static Image replaceAllHover;
	public static Image regexIcon;
	public static Image regexHover;
	public static Image caseSensitiveIcon;
	public static Image caseSensitiveHover;

	private static Image getImage(Display display, String name) {
		return new Image(display, MainWindow.class.getResourceAsStream("/images/" + name));
	}

	public static void loadImages(Display display) {
		if (Settings.INSTANCE.getTHEME().get()) { // light theme
			loadIconHover = getImage(display, "load.png");
			loadIcon = colorIcon(loadIconHover, 0x000000);

			loadTempIconHover = getImage(display, "load-temp.png");
			loadTempIcon = colorIcon(loadTempIconHover, 0x000000);

			fileIconHover = getImage(display, "file.png");
			fileIcon = colorIcon(fileIconHover, 0x000000);

			buildIconHover = getImage(display, "build.png");
			buildIcon = colorIcon(buildIconHover, 0x000000);

			eraseIconHover = getImage(display, "erase.png");
			eraseIcon = colorIcon(eraseIconHover, 0x000000);

			xIcon = getImage(display, "x.png");
			xGreyIcon = colorIcon(xIcon, 0xC5C5C5);
			xRedIcon = colorIcon(xIcon, 0xFF7575);

			arrowUpHover = getImage(display, "arrow-up.png");
			arrowUp = colorIcon(arrowUpHover, 0x000000);

			arrowDownHover = getImage(display, "arrow-down.png");
			arrowDown = colorIcon(arrowDownHover, 0x000000);

			plusIconHover = getImage(display, "plus.png");
			plusIcon = colorIcon(plusIconHover, 0x000000);

			cancelIconHover = getImage(display, "cancel.png");
			cancelIcon = colorIcon(cancelIconHover, 0x000000);

			saveIconHover = getImage(display, "save.png");
			saveIcon = colorIcon(saveIconHover, 0x000000);

			saveAllIconHover = getImage(display, "saveall.png");
			saveAllIcon = colorIcon(saveAllIconHover, 0x000000);

			checkIconHover = getImage(display, "check.png");
			checkIcon = colorIcon(checkIconHover, 0x000000);

			treeArrowIconSelected = getImage(display, "tree-arrow.png");
			treeArrowIcon = colorIcon(treeArrowIconSelected, 0x000000);

			triggerRiseIconHover = getImage(display, "rising_edge.png");
			triggerRiseIcon = colorIcon(triggerRiseIconHover, 0x000000);

			triggerFallIconHover = getImage(display, "falling_edge.png");
			triggerFallIcon = colorIcon(triggerFallIconHover, 0x000000);

			triggerHighIconHover = getImage(display, "high_level.png");
			triggerHighIcon = colorIcon(triggerHighIconHover, 0x000000);

			triggerLowIconHover = getImage(display, "low_level.png");
			triggerLowIcon = colorIcon(triggerLowIconHover, 0x000000);

			connectIconHover = getImage(display, "connect.png");
			connectIcon = colorIcon(connectIconHover, 0x000000);

			captureIconHover = getImage(display, "capture.png");
			captureIcon = colorIcon(captureIconHover, 0x000000);

			syncIconHover = getImage(display, "sync.png");
			syncIcon = colorIcon(syncIconHover, 0x000000);
			
			debugIconHover = getImage(display, "debug.png");
			debugIcon = colorIcon(debugIconHover, 0x000000);
			
			replaceOneHover = getImage(display, "replace-one.png");
			replaceOneIcon = colorIcon(replaceOneHover, 0x000000);
			
			replaceAllHover = getImage(display, "replace-all.png");
			replaceAllIcon = colorIcon(replaceOneHover, 0x000000);
			
			regexHover = getImage(display, "regex.png");
			regexIcon = colorIcon(regexHover, 0x000000);
			
			caseSensitiveHover = getImage(display, "case-sensitive.png");
			caseSensitiveIcon = colorIcon(caseSensitiveHover, 0x000000);
		} else { // dark theme
			loadIcon = getImage(display, "load.png");
			loadTempIcon = getImage(display, "load-temp.png");
			fileIcon = getImage(display, "file.png");
			buildIcon = getImage(display, "build.png");
			eraseIcon = getImage(display, "erase.png");
			loadIconHover = loadIcon;
			loadTempIconHover = loadTempIcon;
			fileIconHover = fileIcon;
			buildIconHover = buildIcon;
			eraseIconHover = eraseIcon;

			xIcon = getImage(display, "x.png");
			xGreyIcon = colorIcon(xIcon, 0xC5C5C5);
			xRedIcon = colorIcon(xIcon, 0xFF7575);

			arrowUp = getImage(display, "arrow-up.png");
			arrowDown = getImage(display, "arrow-down.png");
			arrowUpHover = arrowUp;
			arrowDownHover = arrowDown;
			plusIcon = getImage(display, "plus.png");
			plusIconHover = plusIcon;

			cancelIcon = getImage(display, "cancel.png");
			cancelIconHover = cancelIcon;

			saveIcon = getImage(display, "save.png");
			saveIconHover = saveIcon;

			saveAllIcon = getImage(display, "saveall.png");
			saveAllIconHover = saveAllIcon;

			checkIcon = getImage(display, "check.png");
			checkIconHover = checkIcon;

			treeArrowIcon = getImage(display, "tree-arrow.png");
			treeArrowIconSelected = treeArrowIcon;

			triggerRiseIcon = getImage(display, "rising_edge.png");
			triggerRiseIconHover = triggerRiseIcon;

			triggerFallIcon = getImage(display, "falling_edge.png");
			triggerFallIconHover = triggerFallIcon;

			triggerHighIcon = getImage(display, "high_level.png");
			triggerHighIconHover = triggerHighIcon;

			triggerLowIcon = getImage(display, "low_level.png");
			triggerLowIconHover = triggerLowIcon;

			connectIcon = getImage(display, "connect.png");
			connectIconHover = connectIcon;

			captureIcon = getImage(display, "capture.png");
			captureIconHover = captureIcon;

			syncIcon = getImage(display, "sync.png");
			syncIconHover = syncIcon;
			
			debugIcon = getImage(display, "debug.png");
			debugIconHover = debugIcon;
			
			replaceOneIcon = getImage(display, "replace-one.png");
			replaceOneHover = replaceOneIcon;
			
			replaceAllIcon = getImage(display, "replace-all.png");
			replaceAllHover = replaceAllIcon;
			
			regexIcon = getImage(display, "regex.png");
			regexHover = regexIcon;
			
			caseSensitiveIcon = getImage(display, "case-sensitive.png");
			caseSensitiveHover = caseSensitiveIcon;
		}
	}

	private static Image colorIcon(Image original, int color) {
		ImageData id = (ImageData) original.getImageData().clone();
		for (int x = 0; x < id.width; x++)
			for (int y = 0; y < id.height; y++)
				id.setPixel(x, y, color);
		return new Image(original.getDevice(), id);
	}

	public static void dispose() {
		loadIcon.dispose();
		loadTempIcon.dispose();
		fileIcon.dispose();
		buildIcon.dispose();
		eraseIcon.dispose();
		loadIconHover.dispose();
		loadTempIconHover.dispose();
		fileIconHover.dispose();
		buildIconHover.dispose();
		eraseIconHover.dispose();
		xIcon.dispose();
		xGreyIcon.dispose();
		xRedIcon.dispose();
		arrowUp.dispose();
		arrowDown.dispose();
		arrowUpHover.dispose();
		arrowDownHover.dispose();
		plusIcon.dispose();
		plusIconHover.dispose();
		cancelIcon.dispose();
		cancelIconHover.dispose();
		saveIcon.dispose();
		saveIconHover.dispose();
		saveAllIcon.dispose();
		saveAllIconHover.dispose();
		checkIcon.dispose();
		checkIconHover.dispose();
		treeArrowIcon.dispose();
		treeArrowIconSelected.dispose();
		triggerRiseIcon.dispose();
		triggerRiseIconHover.dispose();
		triggerFallIcon.dispose();
		triggerFallIconHover.dispose();
		triggerHighIcon.dispose();
		triggerHighIconHover.dispose();
		triggerLowIcon.dispose();
		triggerLowIconHover.dispose();
		connectIcon.dispose();
		connectIconHover.dispose();
		captureIcon.dispose();
		captureIconHover.dispose();
		syncIcon.dispose();
		syncIconHover.dispose();
		debugIcon.dispose();
		debugIconHover.dispose();
		replaceOneIcon.dispose();
		replaceOneHover.dispose();
		replaceAllIcon.dispose();
		replaceAllHover.dispose();
		regexIcon.dispose();
		regexHover.dispose();
		caseSensitiveIcon.dispose();
		caseSensitiveHover.dispose();
	}
}
