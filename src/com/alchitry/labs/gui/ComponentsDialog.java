package com.alchitry.labs.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Util;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.project.Project;
import com.alchitry.labs.style.ParseException;

public class ComponentsDialog extends Dialog {
	public static final String LIBRARY_XML = "lib.xml";

	public static final String CATEGORY_TAG = "category";
	public static final String MODULE_TAG = "module";
	public static final String CONSTRAINT_TAG = "constraint";
	public static final String DEPENDS_TAG = "depends";
	public static final String DESCRIP_TAG = "description";
	public static final String INPUT_TAG = "input";
	public static final String OUTPUT_TAG = "output";
	public static final String PORTS_TAG = "ports";
	public static final String NAME_ATTR = "name";
	public static final String FILE_ATTR = "file";
	public static final String WIDTH_ATTR = "width";

	protected Shell shell;

	private HashMap<String, Component> libraryMap = new HashMap<>();
	private Tree tree;
	private Label description;
	private Group grpDescription;

	private class Component {
		public String name;
		public String file;
		public String description;
		public ArrayList<String> dependencies = new ArrayList<>();
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ComponentsDialog(Shell parent) {
		super(parent);
		setText("Component Selector");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public void open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
		shell.setMinimumSize(new Point(600, 500));
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(4, false));

		tree = new Tree(shell, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_tree.widthHint = 196;
		tree.setLayoutData(gd_tree);

		tree.addListener(SWT.Selection, checkListener);
		try {
			buildTree(tree);
		} catch (ParseException | IOException e) {
			Util.showError("Could not read components library file!");
			e.printStackTrace();
		}

		grpDescription = new Group(shell, SWT.NONE);
		grpDescription.setLayout(new GridLayout(1, false));
		GridData gd_grpDescription = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		gd_grpDescription.minimumWidth = 300;
		gd_grpDescription.widthHint = 300;
		grpDescription.setLayoutData(gd_grpDescription);
		grpDescription.setText("Description");

		description = new Label(grpDescription, SWT.WRAP);
		description.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));

		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new GridLayout(2, false));

		Button btnCancel = new Button(composite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 100;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setSize(100, 31);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				shell.close();
			}
		});

		Button btnAdd = new Button(composite, SWT.NONE);
		GridData gd_btnAdd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAdd.widthHint = 100;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setSize(100, 31);
		btnAdd.setText("Add");
		new Label(shell, SWT.NONE);
		btnAdd.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				addSelectedToProject();
			}
		});

		shell.pack();

		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
		int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}

	private void addDependants(HashSet<String> files, Component comp) {
		for (String dep : comp.dependencies) {
			Component depComp = libraryMap.get(dep);
			files.add(depComp.file);
			addDependants(files, depComp);
		}
	}

	private void addSelectedToProject() {
		HashSet<String> files = new HashSet<>();
		Project project = MainWindow.getOpenProject();

		if (project == null) {
			Util.showError("You need to open or create a project first!");
			return;
		}

		for (TreeItem category : tree.getItems()) {
			for (TreeItem module : category.getItems()) {
				if (module.getChecked()) {
					files.add((String) module.getData());
					for (TreeItem dep : module.getItems()) {
						Component depComp = libraryMap.get(dep.getText());
						files.add(depComp.file);
						addDependants(files, depComp);
					}
				}
			}
		}
		/*
		 * File folder = new File(project.getComponentFolder()); if (!folder.exists()) { if (!folder.mkdir()) { Util.showError("Could not create components directory at " +
		 * folder.getPath()); return; } }
		 * 
		 * 
		 * for (String file : files) { File srcFile = new File(LIBRARY_FOLDER + File.separator + file); File destFile = new File(project.getComponentFolder() + File.separator
		 * + file);
		 * 
		 * try { FileUtils.copyFile(srcFile, destFile); project.addExistingComponentFile(file); } catch (IOException e) { Util.showError("Error while copying component " +
		 * file + "!"); return; } }
		 */
		for (String file : files) {
			File srcFile = new File(Locations.COMPONENTS + File.separator + file);
			if (!srcFile.exists()) {
				Util.showError("The component " + file + " could not be found!");
				return;
			}
			if (Util.isConstraintFile(file))
				project.addExistingConstraintFile(file, true);
			else
				project.addExistingComponentFile(file);
		}

		project.updateTree();
		try {
			project.saveXML();
		} catch (IOException e) {
			Util.showError("Could not save the project!");
		}
		MainWindow.mainWindow.updateErrors();
		shell.close();
	}

	static void checkPath(TreeItem item, boolean checked, boolean grayed) {
		if (item == null)
			return;
		if (grayed) {
			checked = true;
		} else {
			int index = 0;
			TreeItem[] items = item.getItems();
			while (index < items.length) {
				TreeItem child = items[index];
				if (child.getGrayed() || checked != child.getChecked()) {
					checked = grayed = true;
					break;
				}
				index++;
			}
		}
		item.setChecked(checked);
		item.setGrayed(grayed);
		checkPath(item.getParentItem(), checked, grayed);
	}

	static void checkItems(TreeItem item, boolean checked) {
		item.setGrayed(false);
		item.setChecked(checked);
		TreeItem[] items = item.getItems();
		for (int i = 0; i < items.length; i++) {
			checkItems(items[i], checked);
		}
	}

	private Listener checkListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if (event.detail == SWT.CHECK) {
				TreeItem item = (TreeItem) event.item;
				if (item.getData() != null && item.getData().getClass() == Boolean.class) {
					item.setChecked(!item.getChecked());
					return;
				}

				boolean checked = item.getChecked();
				checkItems(item, checked);
				checkPath(item.getParentItem(), checked, false);
			} else { // focused
				TreeItem item = (TreeItem) event.item;
				Component c = libraryMap.get(item.getText());
				if (description != null && c != null && c.description != null) {
					description.setText(c.description);
				} else
					description.setText("");
				grpDescription.pack(true); // update the text box
				Point gd = grpDescription.getSize();
				gd.x = 300; // force the width to be 300
				grpDescription.setSize(gd);
				shell.layout(true); // update the layout so text wraps
			}
		}
	};

	private void buildTree(Tree tree) throws ParseException, IOException {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(Locations.COMPONENTS + File.separator + LIBRARY_XML);

		Document document;
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException e) {
			throw new ParseException(e.getMessage());
		}
		Element library = document.getRootElement();

		List<Element> cat = library.getChildren(CATEGORY_TAG);
		for (Element node : cat) {
			TreeItem category = new TreeItem(tree, SWT.NONE);
			category.setText(node.getAttributeValue(NAME_ATTR));
			List<Element> modules = node.getChildren();

			for (Element modNode : modules) {
				Component comp = new Component();
				comp.name = modNode.getAttributeValue(NAME_ATTR);
				comp.file = modNode.getAttributeValue(FILE_ATTR);
				libraryMap.put(comp.name, comp);

				TreeItem module = new TreeItem(category, SWT.NONE);
				module.setText(comp.name);
				module.setData(comp.file);

				Element desc = modNode.getChild(DESCRIP_TAG);
				if (desc != null)
					comp.description = desc.getTextNormalize();
				else
					Util.log.severe("No " + DESCRIP_TAG + " tag!");

				List<Element> deps = modNode.getChildren(DEPENDS_TAG);
				for (Element depsNode : deps) {
					String name = depsNode.getTextNormalize();
					comp.dependencies.add(name);

					TreeItem dep = new TreeItem(module, SWT.NONE);
					dep.setText(name);
					dep.setData(new Boolean(false));

				}
			}
		}
	}
}
