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

public class ConfigureClusteringAlgoTOFROMDialog extends Dialog{

	private static String textSelection = "Select the configuration of the FROM TO Algo:";

	private Button btnGenerateUml;

	private Button btnGenerateUmlCode;

	private Button btnGenerateUmlStructure;

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

		btnGenerateUml = new Button(container, SWT.CHECK);
		btnGenerateUml.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		btnGenerateUml.setText("Generate UML");

		btnGenerateUmlCode = new Button(container, SWT.CHECK);
		btnGenerateUmlCode.setText("Generate UML Code");

		btnGenerateUmlStructure = new Button(container, SWT.CHECK);
		btnGenerateUmlStructure.setText("Generate UML Structure");

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
		return btnGenerateUml.getSelection();
	}

	public boolean getUMLCode() {
		return btnGenerateUmlCode.getSelection();
	}

	public boolean getUMLStructure() {
		return btnGenerateUmlStructure.getSelection();
	}
}
