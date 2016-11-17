/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.ui.wizardsPage;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Landi
 *
 */
public class Page05ViewDrifts extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public Page05ViewDrifts() {
		super("page05");
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
	}

}
