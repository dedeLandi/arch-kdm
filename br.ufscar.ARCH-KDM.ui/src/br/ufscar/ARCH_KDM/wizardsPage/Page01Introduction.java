package br.ufscar.ARCH_KDM.wizardsPage;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Page01Introduction extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public Page01Introduction() {
		super("page01");
		setPageComplete(false);
		setMessage("");
		
		setTitle("Architectural Compilance Checking Wizard");
		setDescription("");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lDescritption = new Label(container, SWT.WRAP | SWT.HORIZONTAL);
		lDescritption.setBounds(10, 10, 554, 314);
		lDescritption.setText("Hello developer!\r\n\r\nThis wizard aims to facilitate your Architectural Compilance Checking based on the actual system (KDM instance) and the Architectural Description (DCL-KDM instance).\r\n\r\nLets go to start the wizard!");
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
}
