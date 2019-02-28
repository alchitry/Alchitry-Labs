package com.alchitry.labs.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.alchitry.labs.Util;
import com.alchitry.labs.gui.main.MainWindow;
import com.alchitry.labs.parsers.InstModule;
import com.alchitry.labs.parsers.ProjectSignal;
import com.alchitry.labs.parsers.Sig;
import com.alchitry.labs.parsers.types.Dff;
import com.alchitry.labs.parsers.types.Fsm;
import com.alchitry.labs.project.DebugInfo;

public class SignalSelectionDialog extends Dialog {
	protected ArrayList<ProjectSignal> result;
	protected int samples;
	protected Shell shell;

	private InstModule top;
	private Tree tree;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public SignalSelectionDialog(Shell parent) {
		super(parent);
		setText("Debug Signal Selector");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public ArrayList<ProjectSignal> open(InstModule topModule) {
		top = topModule;
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	public int getSamples() {
		return samples;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
		shell.setMinimumSize(new Point(600, 500));
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(3, false));

		tree = new Tree(shell, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_tree.widthHint = 196;
		tree.setLayoutData(gd_tree);

		tree.addListener(SWT.Selection, checkListener);
		buildTree(tree);
		TreeItem ti = new TreeItem(tree, SWT.NONE);
		ti.setText(top.getName());
		ti.setData(top);
		addChildren(top, ti);
		ti.setExpanded(true);

		preSelect(ti);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Samples:");

		samplesText = new Text(shell, SWT.BORDER);
		samplesText.setText("256");
		samplesText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		samplesText.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {

				Text text = (Text) e.getSource();

				// get old text and create new text by using the VerifyEvent.text
				final String oldS = text.getText();
				String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

				boolean isInt = true;
				try {
					Integer.parseInt(newS);
				} catch (NumberFormatException ex) {
					isInt = false;
				}

				if (!isInt && !newS.isEmpty())
					e.doit = false;
			}
		});

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
		btnAdd.setText("Debug");
		btnAdd.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (samplesText.getText().isEmpty()) {
					Util.showError("Samples can not be empty!");
					return;
				}
				samples = Integer.parseInt(samplesText.getText());
				if (samples <= 0) {
					Util.showError("Samples must be a positve integer!");
					return;
				}
				getSelected();
				shell.close();
			}
		});

		composite.pack();
		shell.pack();
		
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width)/2+parentSize.x;
		int locationY = (parentSize.height - shellSize.height)/2+parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}

	private void getSelected() {
		result = new ArrayList<>();
		List<InstModule> path = new ArrayList<>();
		TreeItem top = tree.getItem(0);
		path.add((InstModule) top.getData());
		addChildren(top, path);

	}

	private void addChildren(TreeItem parent, List<InstModule> path) {
		for (TreeItem ti : parent.getItems()) {
			if (ti.getItemCount() == 0) {
				if (ti.getChecked() && !(ti.getData() instanceof InstModule)) {
					ProjectSignal ps = new ProjectSignal();
					ps.addAll(path);
					ps.set(ti.getData());
					result.add(ps);
				}
			} else {
				path.add((InstModule) ti.getData());
				addChildren(ti, path);
				path.remove(path.size() - 1);
			}
		}
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

	private void updateTreeItem(TreeItem item) {
		boolean checked = item.getChecked();
		checkItems(item, checked);
		checkPath(item.getParentItem(), checked, false);
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
				updateTreeItem(item);
			}
		}
	};
	private Text samplesText;

	private void addChildren(InstModule mod, TreeItem parent) {
		List<Sig> inputs = mod.getInputs();
		if (inputs != null)
			for (Sig s : inputs) {
				TreeItem ti = new TreeItem(parent, SWT.NONE);
				ti.setText(s.getName() + " [" + s.getWidth().getTotalWidth() + "]");
				ti.setData(s);
			}

		if (mod.getDrivenSigs() != null)
			for (Sig s : mod.getDrivenSigs()) {
				TreeItem ti = new TreeItem(parent, SWT.NONE);
				ti.setText(s.getName() + " [" + s.getWidth().getTotalWidth() + "]");
				ti.setData(s);
			}
		if (mod.getDffs() != null)
			for (Dff d : mod.getDffs()) {
				TreeItem ti = new TreeItem(parent, SWT.NONE);
				ti.setText(d.getName() + ".q [" + d.getWidth().getTotalWidth() + "]");
				ti.setData(d);
			}
		if (mod.getFsms() != null)
			for (Fsm f : mod.getFsms()) {
				TreeItem ti = new TreeItem(parent, SWT.NONE);
				ti.setText(f.getName() + ".q [" + f.getWidth().getTotalWidth() + "]");
				ti.setData(f);
			}

		for (InstModule im : mod.getChildren()) {
			List<Sig> outputs = im.getOutputs();
			if (outputs != null)
				for (Sig s : outputs) {
					TreeItem ti = new TreeItem(parent, SWT.NONE);
					ti.setText(im.getName() + "." + s.getName() + " [" + s.getWidth().getTotalWidth() + "]");
					ProjectSignal ps = new ProjectSignal();
					ps.setSig(s);
					ps.setParent(im);
					ti.setData(ps);
				}
		}

		for (InstModule im : mod.getChildren()) {
			if (im.isLucid() && !im.getType().getName().equals("reg_interface")) {
				TreeItem modItem = new TreeItem(parent, SWT.NONE);
				modItem.setText(im.getName());
				modItem.setData(im);
				addChildren(im, modItem);
			}
		}
	}

	private void preSelect(TreeItem root) {
		DebugInfo di = MainWindow.getOpenProject().getDebugInfo();
		if (di == null)
			return;

		for (ProjectSignal s : di.getSignals()) {
			TreeItem ti = root;
			for (int i = 1; i < s.getPath().size(); i++) {
				InstModule im = s.getPath().get(i);
				for (TreeItem child : ti.getItems()) {
					if (Objects.equals(child.getData(), im)) {
						ti = child;
						break;
					}
				}
			}
			for (TreeItem sigs : ti.getItems()) {
				if (Objects.equals(s.getDff(), sigs.getData()) || Objects.equals(s.getFsm(), sigs.getData()) || Objects.equals(s.getSig(), sigs.getData())
						|| s.equalIngorePath(sigs.getData())) {
					sigs.setChecked(true);
					updateTreeItem(sigs);
					break;
				}
			}

		}
	}

	private void buildTree(Tree tree) {
	}
}
