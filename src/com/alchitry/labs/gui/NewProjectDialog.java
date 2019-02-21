package com.alchitry.labs.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Settings;
import com.alchitry.labs.Strings;
import com.alchitry.labs.Util;
import com.alchitry.labs.hardware.boards.Board;
import com.alchitry.labs.project.Project;
import com.alchitry.labs.style.ParseException;

public class NewProjectDialog extends Dialog {
	private static final String PROJECT_XML = "projects.xml";
	private static final String PROJECT_TAG = "project";
	private static final String NAME_ATTR = "name";

	protected Project result;
	protected Shell shlNewProject;
	private Text projFolder;
	private Text projName;
	private Combo combo;
	private Button btnLucid;
	private Combo examples;
	private boolean clone;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public NewProjectDialog(Shell parent, int style, boolean clone) {
		super(parent, style);
		setText("SWT Dialog");
		this.clone = clone;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Project open() {
		createContents();
		if (shlNewProject.isDisposed())
			return null;
		shlNewProject.open();
		shlNewProject.layout();
		Display display = getParent().getDisplay();
		while (!shlNewProject.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlNewProject = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shlNewProject.setSize(459, 363);
		shlNewProject.setText("New Project");
		shlNewProject.setLayout(new GridLayout(4, false));

		Label lblProjectName = new Label(shlNewProject, SWT.NONE);
		// lblProjectName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProjectName.setText("Project Name:");

		projName = new Text(shlNewProject, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_text_1.widthHint = 341;
		projName.setLayoutData(gd_text_1);

		Label lblProjectFolder = new Label(shlNewProject, SWT.NONE);
		lblProjectFolder.setText("Workspace:");

		projFolder = new Text(shlNewProject, SWT.BORDER);
		projFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		projFolder.setText(Util.getWorkspace());

		Button btnNewButton = new Button(shlNewProject, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shlNewProject);
				// dialog.setFilterPath(string)
				String path = dialog.open();
				if (path != null) {
					projFolder.setText(path);
				}
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("Browse...");

		Label lblBoard = new Label(shlNewProject, SWT.NONE);
		lblBoard.setText("Board:");

		combo = new Combo(shlNewProject, SWT.READ_ONLY);

		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		String[] boards = new String[Board.boards.size()];
		for (int i = 0; i < boards.length; i++) {
			boards[i] = Board.boards.get(i).getName();
		}
		combo.setItems(boards);
		combo.select(0);

		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateExamples();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label lblNewLabel = new Label(shlNewProject, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblNewLabel.setText("Language:");

		btnLucid = new Button(shlNewProject, SWT.RADIO);
		btnLucid.setSelection(true);
		btnLucid.setText("Lucid");

		btnLucid.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateExamples();
			}
		});

		Button btnVerilog = new Button(shlNewProject, SWT.RADIO);
		btnVerilog.setText("Verilog");
		btnVerilog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateExamples();
			}
		});

		Label lblFromExample = new Label(shlNewProject, SWT.NONE);
		lblFromExample.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFromExample.setText("From Example:");

		examples = new Combo(shlNewProject, SWT.READ_ONLY);
		examples.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Button btnCancel = new Button(shlNewProject, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNewProject.close();
			}
		});
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		Button btnCreate = new Button(shlNewProject, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String projectName = projName.getText();
				String boardType = combo.getText();
				String language = btnLucid.getSelection() ? Strings.lucid : Strings.verilog;
				String workspace = projFolder.getText();
				if (clone) {
					result = new Project();
					result.setProjectFolder(workspace + File.separator + projectName);
					result.setProjectName(projectName);
				} else {
					result = createProject(projectName, workspace, boardType, language, examples.getText());
				}
				if (result != null)
					shlNewProject.close();
			}
		});
		GridData gd_btnCreate = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnCreate.widthHint = 80;
		btnCreate.setLayoutData(gd_btnCreate);
		btnCreate.setText("Create");

		if (clone) {
			examples.setEnabled(false);
			combo.setEnabled(false);
			btnLucid.setEnabled(false);
			btnVerilog.setEnabled(false);
		}

		shlNewProject.pack();
		
		Rectangle parentSize = getParent().getBounds();
		Rectangle shellSize = shlNewProject.getBounds();
		int locationX = (parentSize.width - shellSize.width)/2+parentSize.x;
		int locationY = (parentSize.height - shellSize.height)/2+parentSize.y;
		shlNewProject.setLocation(new Point(locationX, locationY));

		updateExamples(); // update list
	}

	public static Project createProject(String projectName, String workspace, String boardType, String language, String example) {

		String projectFolder = workspace + File.separator + projectName;

		Project project = new Project();

		if (projectName.equals("")) {
			Util.showError("Project name cannot be blank!");
			return null;
		}

		Board board = Board.getFromName(boardType);
		if (board == null) {
			Util.showError("Board type is invalid!");
			return null;
		}

		HashMap<String, String> nameToFile = getExampleProjects(boardType, language, null);

		File srcDir = new File(Locations.BASE + File.separator + board.getExampleProjectDir() + File.separator + language + File.separator + nameToFile.get(example));

		if (!srcDir.exists()) {
			Util.showError("Could not find starter code!");
			return null;
		}

		File destDir = new File(projectFolder);
		File parentDir = new File(workspace);

		if (!parentDir.exists()) {
			if (Util.askQuestion("Project parent directory does not exist. Create it?")) {
				if (!parentDir.mkdirs()) {
					Util.showError("Could not create parent directory!");
					return null;
				}
			} else {
				return null;
			}
		}

		if (destDir.exists()) {
			if (Util.askQuestion("Project directory already exists. Do you want to override it?")) {
				try {
					FileUtils.deleteDirectory(destDir);
				} catch (IOException ex) {
					Util.showError("Could not remove project folder.");
					return null;
				}
			}
		}

		boolean success = destDir.mkdir();
		if (!success) {
			Util.showError("Could not create project folder!");
			return null;
		}

		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e1) {
			Util.showError(e1.getMessage());
			return null;
		}

		String[] projFiles = destDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".alp");
			}
		});

		if (projFiles.length != 1) {
			Util.showError("Found " + projFiles.length + " project files. Was expecting only one.");
			return null;
		}

		File projFile = new File(destDir.getAbsolutePath() + File.separator + projFiles[0]);
		File newProjFile = new File(destDir.getAbsolutePath() + File.separator + projectName + ".alp");

		if (!projFile.renameTo(newProjFile)) {
			Util.showError("Could not rename project file!");
			return null;
		}

		try {
			project.openXML(newProjFile.getAbsolutePath());
		} catch (ParseException | IOException e2) {
			Util.showError("Could not open project file!");
			return null;
		}

		project.setProjectName(projectName);

		try {
			project.saveXML();

		} catch (IOException e1) {
			Util.showError(e1.getMessage());
			return null;
		}

		Settings.pref.put(Settings.WORKSPACE, workspace);

		return project;
	}

	private static HashMap<String, String> getExampleProjects(String boardType, String language, List<String> names) {
		Board board = Board.getFromName(boardType);
		HashMap<String, String> map = new HashMap<>();

		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(Locations.BASE + File.separator + board.getExampleProjectDir() + File.separator + language + File.separator + PROJECT_XML);

		Document document;
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException | IOException e) {
			Util.showError("Could not parse projects XML file!");
			return null;
		}
		Element library = document.getRootElement();

		List<Element> cat = library.getChildren(PROJECT_TAG);
		for (Element node : cat) {
			String name = node.getAttributeValue(NAME_ATTR);
			String file = node.getTextTrim();
			map.put(name, file);
			if (names != null)
				names.add(name);
		}

		return map;
	}

	private void updateExamples() {
		if (clone) {
			examples.setItems(new String[] { "Current Project" });
			examples.select(0);
		} else {
			String boardType = combo.getText();
			String language = btnLucid.getSelection() ? Strings.lucid : Strings.verilog;

			List<String> names = new ArrayList<>();

			getExampleProjects(boardType, language, names);

			examples.setItems(names.toArray(new String[names.size()]));
			examples.select(0);
		}
	}

}
