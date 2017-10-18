package br.ufscar.arch_kdm.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import br.ufscar.arch_kdm.ui.visualization.groupingTypes.GroupingAlgorithmTypes;

public class ConfigureClusteringAlgoTOFROMDialog extends Dialog{

	private static String textSelection = "Select the configuration of the FROM TO Algo:";

	private Button btnGenerateUml;

	private Button btnGenerateUmlCode;

	private Button btnGenerateUmlStructure;

	private boolean generateUml;

	private boolean generateUmlCode;

	private boolean generateUmlStructure;

	public ConfigureClusteringAlgoTOFROMDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);

		Label lblSelectOneModel = new Label(container, SWT.NONE);
		lblSelectOneModel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblSelectOneModel.setText(textSelection);
		
		String types[] = ((String) GroupingAlgorithmTypes.GROUPING_BY_TO_FROM.configAlgoDefault()).split(";");
		
		btnGenerateUml = new Button(container, SWT.CHECK);
		btnGenerateUml.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		btnGenerateUml.setText("Generate UML");
		btnGenerateUml.setSelection(Boolean.parseBoolean(types[0].split(":")[1]));

		btnGenerateUmlCode = new Button(container, SWT.CHECK);
		btnGenerateUmlCode.setText("Generate UML Code");
		btnGenerateUmlCode.setSelection(Boolean.parseBoolean(types[1].split(":")[1]));
		
		btnGenerateUmlStructure = new Button(container, SWT.CHECK);
		btnGenerateUmlStructure.setText("Generate UML Structure");
		btnGenerateUmlStructure.setSelection(Boolean.parseBoolean(types[2].split(":")[1]));

		return container;
	}

	// override method to use "Login" as label for the OK button
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnOk = createButton(parent, IDialogConstants.OK_ID, "Select", true);
		btnOk.setText("OK");
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		if(validateConfiguration()){
			generateUml = btnGenerateUml.getSelection();
			generateUmlCode = btnGenerateUmlCode.getSelection();
			generateUmlStructure = btnGenerateUmlStructure.getSelection();
			super.okPressed();
		}
	}

	private boolean validateConfiguration() {
		return true;
	}


	public static void setTextSelection(String textSelection) {
		ConfigureClusteringAlgoTOFROMDialog.textSelection = textSelection;
	}

	public boolean getUML() {
		return this.generateUml;
	}

	public boolean getUMLCode() {
		return this.generateUmlCode;
	}

	public boolean getUMLStructure() {
		return this.generateUmlStructure;
	}
}
